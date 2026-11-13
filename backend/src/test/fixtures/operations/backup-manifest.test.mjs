import test from 'node:test'
import assert from 'node:assert/strict'
import { createHash } from 'node:crypto'
import { mkdtemp, mkdir, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import { join } from 'node:path'
import { verifySnapshot } from './backup-manifest.mjs'

const now = Date.parse('2026-11-11T03:00:00Z')
const storageKey = '00000000-0000-4000-8000-000000000001.mp3'
async function fixture() {
  const directory = await mkdtemp(join(tmpdir(), 'hueqr-manifest-test-'))
  await mkdir(join(directory, 'audio'))
  const sql = Buffer.from('SELECT 1;'), audio = Buffer.from('profile bytes for Hash validation only')
  await writeFile(join(directory, 'database.sql'), sql)
  await writeFile(join(directory, 'audio', storageKey), audio)
  const entry = bytes => ({ bytes: bytes.length, sha256: createHash('sha256').update(bytes).digest('hex') })
  return { directory, manifest: { kind: 'offline-test-rehearsal', status: 'SNAPSHOT_VERIFIED',
    createdAt: new Date(now).toISOString(), expiresAt: new Date(now + 7 * 86400000).toISOString(),
    database: { file: 'database.sql', ...entry(sql) }, files: [{ storageKey, ...entry(audio) }] } }
}
test('nhận bộ còn hạn với đủ database và audio khớp Hash', async () => {
  const f = await fixture()
  assert.equal((await verifySnapshot(f.directory, f.manifest, now)).files, 1)
})
test('từ chối đúng mốc hết hạn bảy ngày', async () => {
  const f = await fixture()
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now + 7 * 86400000))
})
test('từ chối audio bị đổi bytes cùng kích thước', async () => {
  const f = await fixture()
  await writeFile(join(f.directory, 'audio', storageKey), Buffer.alloc(f.manifest.files[0].bytes, 1))
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now), /Sai Hash/)
})
test('từ chối thiếu audio và storage key thoát thư mục', async () => {
  const f = await fixture()
  f.manifest.files[0].storageKey = '00000000-0000-4000-8000-000000000002.mp3'
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now), /ENOENT/)
  f.manifest.files[0].storageKey = '../database.sql'
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now))
})
test('từ chối dump sai kích thước hoặc kéo dài hạn giữ', async () => {
  const f = await fixture()
  f.manifest.database.bytes++
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now), /Sai kích thước/)
  f.manifest.expiresAt = new Date(now + 8 * 86400000).toISOString()
  await assert.rejects(verifySnapshot(f.directory, f.manifest, now), /bảy ngày/)
})
