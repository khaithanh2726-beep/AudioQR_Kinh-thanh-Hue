import assert from 'node:assert/strict'
import { createHash } from 'node:crypto'
import { lstat, readFile } from 'node:fs/promises'
import { join } from 'node:path'

export async function verifySnapshot(directory, manifest, now = Date.now()) {
  assert.equal(manifest.kind, 'offline-test-rehearsal')
  assert.equal(manifest.status, 'SNAPSHOT_VERIFIED')
  const created = Date.parse(manifest.createdAt), expires = Date.parse(manifest.expiresAt)
  assert(Number.isFinite(created) && created <= now, 'Mốc tạo không hợp lệ')
  assert.equal(expires - created, 7 * 86400000, 'Hạn backup phải đúng bảy ngày')
  assert(now < expires, 'Snapshot hết hạn')
  assert.equal(manifest.database.file, 'database.sql')
  assert(Array.isArray(manifest.files) && manifest.files.length > 0)
  const keys = new Set()
  for (const file of manifest.files) {
    assert.match(file.storageKey, /^[0-9a-f-]{36}\.(wav|mp3)$/)
    assert(!keys.has(file.storageKey), 'Trùng storage key')
    keys.add(file.storageKey)
  }
  const entries = [
    { path: join(directory, 'database.sql'), ...manifest.database },
    ...manifest.files.map(file => ({ path: join(directory, 'audio', file.storageKey), ...file })),
  ]
  for (const entry of entries) {
    assert.match(entry.sha256, /^[0-9a-f]{64}$/)
    assert(Number.isSafeInteger(entry.bytes) && entry.bytes > 0)
    const stat = await lstat(entry.path)
    assert(stat.isFile(), 'Không nhận symlink hoặc thư mục')
    const bytes = await readFile(entry.path)
    assert.equal(bytes.length, entry.bytes, 'Sai kích thước tệp backup')
    assert.equal(createHash('sha256').update(bytes).digest('hex'), entry.sha256, 'Sai Hash tệp backup')
  }
  return { files: manifest.files.length, databaseBytes: manifest.database.bytes }
}
