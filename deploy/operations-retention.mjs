import assert from 'node:assert/strict'
import { lstat, realpath, readdir, readFile, unlink, rmdir } from 'node:fs/promises'
import { resolve, dirname, join, relative, isAbsolute, sep } from 'node:path'
import { verifyOperationsBackup } from './operations-backup.mjs'

export const RETENTION_MS = 7 * 86400000
export const backupName = /^backup-(?:[0-9a-f-]{36}|\d{4}-\d\d-\d\dT[0-9TZ-]+-[0-9a-f]{8})$/
const logName = /^operations-[0-9a-f-]{36}\.json$/

// Từ chối cả junction ở thư mục cha; không đưa đường dẫn chưa đối chiếu vào lệnh xóa.
export async function plainDirectory(path) {
  const absolute = resolve(path)
  for (let current = absolute; ; current = dirname(current)) {
    assert((await lstat(current)).isDirectory(), 'Directory or ancestor is a link/non-directory')
    if (dirname(current) === current) break
  }
  assert.equal((await realpath(absolute)).toLowerCase(), absolute.toLowerCase())
  return absolute
}

export function disjoint(first, second) {
  const outside = (a, b) => {
    const delta = relative(resolve(a), resolve(b))
    return delta.startsWith('..' + sep) || isAbsolute(delta)
  }
  assert(outside(first, second) && outside(second, first), 'Protected directories must not overlap')
}

async function jsonFile(path) {
  assert((await lstat(path)).isFile(), 'Refuse symbolic metadata')
  return JSON.parse((await readFile(path, 'utf8')).replace(/^\uFEFF/, ''))
}

async function exactBackup(directory, manifest, now) {
  await plainDirectory(directory)
  await verifyOperationsBackup(directory, manifest, now)
  assert.deepEqual((await readdir(directory)).sort(), ['audio', 'database.sql', 'manifest.json'])
  assert.deepEqual((await readdir(join(directory, 'audio'))).sort(), manifest.files.map(f => f.storageKey).sort())
}

// Chỉ backup COMPLETE, còn bytes đúng và hết hạn mới được dọn; bản lạ/dở giữ để rà.
export async function pruneBackups(rootArg, newestArg, now = Date.now()) {
  const root = await plainDirectory(rootArg), newest = resolve(newestArg)
  assert.equal(dirname(newest), root)
  assert(backupName.test(newest.slice(root.length + 1)))
  const fresh = await jsonFile(join(newest, 'manifest.json'))
  await exactBackup(newest, fresh, now)
  const result = { removed: [], retained: [], review: [] }
  for (const name of await readdir(root)) {
    if (!backupName.test(name) || join(root, name) === newest) continue
    const directory = join(root, name)
    try {
      await plainDirectory(directory)
      const manifest = await jsonFile(join(directory, 'manifest.json'))
      assert.match(manifest.sourceImage, /^sha256:[0-9a-f]{64}$/)
      const expiry = Date.parse(manifest.expiresAt)
      assert(Number.isFinite(expiry))
      if (expiry > now) { result.retained.push(name); continue }
      // Đối chiếu Hash tại ngay trước hạn gốc, không gia hạn manifest để vượt kiểm.
      await exactBackup(directory, manifest, expiry - 1)
      assert(Date.parse(fresh.createdAt) > Date.parse(manifest.createdAt))
      // Không recursive delete: chỉ unlink đúng tập file vừa kiểm, rmdir phải rỗng.
      for (const file of manifest.files) await unlink(join(directory, 'audio', file.storageKey))
      await rmdir(join(directory, 'audio'))
      await unlink(join(directory, 'database.sql'))
      await unlink(join(directory, 'manifest.json'))
      await rmdir(directory)
      result.removed.push(name)
    } catch { result.review.push(name) }
  }
  return result
}

export async function pruneLogs(rootArg, now = Date.now()) {
  const root = await plainDirectory(rootArg)
  const result = { removed: 0, review: 0 }
  for (const name of await readdir(root)) {
    if (!logName.test(name)) continue
    try {
      const path = join(root, name), log = await jsonFile(path)
      assert.equal(log.kind, 'scheduled-operations')
      const finished = Date.parse(log.finishedAt)
      assert(Number.isFinite(finished) && finished <= now)
      if (finished + RETENTION_MS <= now) { await unlink(path); result.removed++ }
    } catch { result.review++ }
  }
  return result
}
