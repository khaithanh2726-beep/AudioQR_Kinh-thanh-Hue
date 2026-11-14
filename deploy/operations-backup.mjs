import assert from 'node:assert/strict'
import { createHash } from 'node:crypto'
import { lstat, readFile } from 'node:fs/promises'
import { join } from 'node:path'

export async function verifyOperationsBackup(directory, manifest, now = Date.now()) {
  assert.equal(manifest.kind, 'quiesced-backup')
  assert.equal(manifest.status, 'COMPLETE')
  assert.match(manifest.checkpoint, /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/)
  return verifyBackupFiles(directory, manifest, now)
}

export async function verifyOfflineV13Backup(directory, manifest, now = Date.now()) {
  assert.equal(manifest.kind, 'offline-v13-backup')
  assert.equal(manifest.status, 'COMPLETE')
  assert.equal(manifest.sourceStopped, true)
  assert.match(manifest.sourceImage, /^sha256:[0-9a-f]{64}$/)
  const migrations = manifest.schema.split('\n').map(line => line.trim().split('\t'))
  assert.deepEqual(migrations.map(row => row[0]), Array.from({ length: 13 }, (_, i) => String(i + 1)))
  assert(migrations.every(row => row.length === 3 && /^-?\d+$/.test(row[1]) && row[2] === '1'), 'Invalid V13 schema')
  return verifyBackupFiles(directory, manifest, now)
}

// Kiểm bytes/hạn dùng chung; loại snapshot và điều kiện fence do từng giao thức kiểm riêng.
export async function verifyBackupFiles(directory, manifest, now = Date.now()) {
  const created = Date.parse(manifest.createdAt), expires = Date.parse(manifest.expiresAt)
  assert(Number.isFinite(created) && created <= now)
  assert.equal(expires - created, 7 * 86400000)
  assert(now < expires, 'Backup expired')
  assert.equal(manifest.database.file, 'database.sql')
  assert(Array.isArray(manifest.files))
  assert((await lstat(directory)).isDirectory(), 'No symbolic backup root')
  assert((await lstat(join(directory, 'audio'))).isDirectory(), 'No symbolic audio root')
  const keys = new Set()
  for (const file of manifest.files) {
    assert.match(file.storageKey, /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\.(wav|mp3)$/)
    assert(!keys.has(file.storageKey), 'Duplicate storage key')
    keys.add(file.storageKey)
    assert.equal(file.format, file.storageKey.endsWith('.wav') ? 'WAV' : 'MP3')
  }
  for (const file of [{ ...manifest.database, path: join(directory, 'database.sql') },
    ...manifest.files.map(file => ({ ...file, path: join(directory, 'audio', file.storageKey) }))]) {
    assert.match(file.sha256, /^[0-9a-f]{64}$/)
    assert(Number.isSafeInteger(file.bytes) && file.bytes > 0)
    assert((await lstat(file.path)).isFile(), 'No symlink or directory as content')
    const bytes = await readFile(file.path)
    assert.equal(bytes.length, file.bytes)
    assert.equal(createHash('sha256').update(bytes).digest('hex'), file.sha256)
  }
}
