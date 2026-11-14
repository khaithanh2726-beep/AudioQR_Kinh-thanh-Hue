import { test, after } from 'node:test'
import assert from 'node:assert/strict'
import { mkdtemp, mkdir, writeFile, readFile, access, symlink, readdir, rm } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import { join, dirname, basename, resolve } from 'node:path'
import { randomUUID, createHash } from 'node:crypto'
import { pruneBackups, pruneLogs, plainDirectory, disjoint, RETENTION_MS } from '../../../../../deploy/operations-retention.mjs'

const now = Date.parse('2026-11-14T12:00:00Z')
const hash = data => createHash('sha256').update(data).digest('hex')
const scratch = []
async function temporary(prefix) { const path = await mkdtemp(join(tmpdir(), prefix)); scratch.push(path); return path }
after(async () => {
  for (const path of scratch) {
    assert.equal(dirname(resolve(path)), resolve(tmpdir()))
    assert.match(basename(path), /^hueqr-(retention|junction)-/)
    await rm(path, { recursive: true, force: true })
  }
})
async function fixture() {
  const root = await temporary('hueqr-retention-')
  async function backup(age = 0) {
    const directory = join(root, 'backup-' + randomUUID())
    await mkdir(join(directory, 'audio'), { recursive: true })
    const key = randomUUID() + '.mp3', dump = Buffer.from('private dump fixture'), audio = Buffer.from('mp3 fixture')
    const manifest = { kind: 'quiesced-backup', status: 'COMPLETE', checkpoint: randomUUID(),
      sourceImage: 'sha256:' + 'a'.repeat(64), createdAt: new Date(now - age).toISOString(),
      expiresAt: new Date(now - age + RETENTION_MS).toISOString(),
      database: { file: 'database.sql', bytes: dump.length, sha256: hash(dump) },
      files: [{ storageKey: key, format: 'MP3', bytes: audio.length, sha256: hash(audio) }] }
    await writeFile(join(directory, 'database.sql'), dump)
    await writeFile(join(directory, 'audio', key), audio)
    await writeFile(join(directory, 'manifest.json'), JSON.stringify(manifest))
    return { directory, manifest, key }
  }
  return { root, backup }
}

test('dọn tại đúng hạn bảy ngày, giữ bản còn hạn và dữ liệu ngoài root', async () => {
  const f = await fixture(), fresh = await f.backup(), expired = await f.backup(RETENTION_MS), valid = await f.backup(RETENTION_MS - 1)
  const sentinel = join(f.root, 'private.env'); await writeFile(sentinel, 'keep')
  const result = await pruneBackups(f.root, fresh.directory, now)
  assert.equal(result.removed.length, 1)
  await assert.rejects(access(expired.directory))
  await access(valid.directory); await access(fresh.directory)
  assert.equal(await readFile(sentinel, 'utf8'), 'keep')
})

test('bản mới sai Hash chặn toàn bộ dọn backup', async () => {
  const f = await fixture(), fresh = await f.backup(), expired = await f.backup(RETENTION_MS)
  await writeFile(join(fresh.directory, 'database.sql'), 'corrupt')
  await assert.rejects(pruneBackups(f.root, fresh.directory, now))
  await access(expired.directory)
})

test('backup cũ lỗi Hash hoặc có tệp lạ được giữ để rà', async () => {
  const f = await fixture(), fresh = await f.backup(), corrupt = await f.backup(RETENTION_MS), extra = await f.backup(RETENTION_MS)
  await writeFile(join(corrupt.directory, 'audio', corrupt.key), 'broken')
  await writeFile(join(extra.directory, 'notes.txt'), 'keep')
  const result = await pruneBackups(f.root, fresh.directory, now)
  assert.equal(result.review.length, 2); assert.equal(result.removed.length, 0)
  await access(join(extra.directory, 'database.sql'))
})

test('không dọn bản dở hoặc manifest kéo dài hạn', async () => {
  const f = await fixture(), fresh = await f.backup(), expired = await f.backup(RETENTION_MS * 2)
  expired.manifest.expiresAt = new Date(now - 1).toISOString()
  await writeFile(join(expired.directory, 'manifest.json'), JSON.stringify(expired.manifest))
  const failed = join(f.root, 'backup-' + randomUUID()); await mkdir(failed); await writeFile(join(failed, 'FAILED.json'), '{}')
  const result = await pruneBackups(f.root, fresh.directory, now)
  assert.equal(result.review.length, 2); await access(expired.directory)
})

test('từ chối junction ở root hoặc ancestor, không chạm đích liên kết', async () => {
  const f = await fixture(), fresh = await f.backup(), expired = await f.backup(RETENTION_MS)
  const outer = await temporary('hueqr-junction-'), link = join(outer, 'linked')
  await symlink(f.root, link, process.platform === 'win32' ? 'junction' : 'dir')
  await assert.rejects(plainDirectory(join(link, fresh.directory.slice(f.root.length + 1))))
  await assert.rejects(pruneBackups(link, fresh.directory, now))
  await access(expired.directory)
})

test('backup có audio symlink không được xóa', async () => {
  const f = await fixture(), fresh = await f.backup(), expired = await f.backup(RETENTION_MS)
  const { unlink } = await import('node:fs/promises')
  await unlink(join(expired.directory, 'audio', expired.key))
  // Junction thư mục luôn tạo được bằng quyền user trên Windows, không cần quyền symlink file.
  await symlink(join(fresh.directory, 'audio'), join(expired.directory, 'audio', expired.key), process.platform === 'win32' ? 'junction' : 'dir')
  const result = await pruneBackups(f.root, fresh.directory, now)
  assert.equal(result.review.length, 1); await access(fresh.directory)
})

test('log chỉ dọn đúng tên/loại và đúng hạn, giữ tệp lạ và cấu hình', async () => {
  const { root } = await fixture()
  const log = async (age, kind = 'scheduled-operations') => {
    const path = join(root, 'operations-' + randomUUID() + '.json')
    await writeFile(path, JSON.stringify({ kind, finishedAt: new Date(now - age).toISOString() })); return path
  }
  const expired = await log(RETENTION_MS), valid = await log(RETENTION_MS - 1), other = await log(RETENTION_MS, 'other')
  await writeFile(join(root, 'runtime.json'), '{}')
  assert.deepEqual(await pruneLogs(root, now), { removed: 1, review: 1 })
  await assert.rejects(access(expired)); await access(valid); await access(other); await access(join(root, 'runtime.json'))
})

test('log junction và log sai JSON không làm dọn nhầm đích', async () => {
  const f = await fixture(), target = join(f.root, 'protected'); await mkdir(target)
  await writeFile(join(target, 'sentinel'), 'keep')
  await symlink(target, join(f.root, 'operations-' + randomUUID() + '.json'), process.platform === 'win32' ? 'junction' : 'dir')
  await writeFile(join(f.root, 'operations-' + randomUUID() + '.json'), '{')
  assert.deepEqual(await pruneLogs(f.root, now), { removed: 0, review: 2 })
  assert.deepEqual(await readdir(target), ['sentinel'])
})

test('không cho backup/log lồng runtime, repository hoặc lồng nhau', () => {
  assert.throws(() => disjoint(join(tmpdir(), 'live'), join(tmpdir(), 'live', 'backup')))
  assert.throws(() => disjoint(tmpdir(), tmpdir()))
  disjoint(join(tmpdir(), 'live'), join(tmpdir(), 'backups'))
})
