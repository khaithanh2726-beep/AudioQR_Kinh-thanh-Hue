import test from 'node:test'
import assert from 'node:assert/strict'
import { mkdtemp, mkdir, writeFile, rm, symlink } from 'node:fs/promises'
import { join } from 'node:path'
import { tmpdir } from 'node:os'
import { createHash, randomUUID } from 'node:crypto'
import { verifyOperationsBackup } from '../../../../../deploy/operations-backup.mjs'

async function fixture(run) {
  const directory = await mkdtemp(join(tmpdir(), 'hueqr-backup-validator-'))
  const bytes = Buffer.from('backup-integrity-fixture')
  const digest = createHash('sha256').update(bytes).digest('hex')
  const key = randomUUID() + '.mp3'
  await mkdir(join(directory, 'audio'))
  await writeFile(join(directory, 'database.sql'), bytes)
  await writeFile(join(directory, 'audio', key), bytes)
  const now = Date.now()
  const manifest = { kind: 'quiesced-backup', status: 'COMPLETE', checkpoint: randomUUID(),
    createdAt: new Date(now).toISOString(), expiresAt: new Date(now + 7 * 86400000).toISOString(),
    database: { file: 'database.sql', bytes: bytes.length, sha256: digest },
    files: [{ storageKey: key, format: 'MP3', bytes: bytes.length, sha256: digest }] }
  try { await run(directory, manifest, now) }
  finally { await rm(directory, { recursive: true }) }
}

test('accepts exact database and audio with seven-day lifetime', () => fixture((d, m, n) => verifyOperationsBackup(d, m, n)))
test('rejects incomplete backup before restore', () => fixture(async (d, m, n) => {
  m.status = 'FAILED'; await assert.rejects(verifyOperationsBackup(d, m, n))
}))
test('rejects backup at original expiry without extending retention', () => fixture(async (d, m, n) => {
  await assert.rejects(verifyOperationsBackup(d, m, n + 7 * 86400000))
}))
test('rejects damaged dump', () => fixture(async (d, m, n) => {
  await writeFile(join(d, 'database.sql'), 'broken'); await assert.rejects(verifyOperationsBackup(d, m, n))
}))
test('rejects missing audio', () => fixture(async (d, m, n) => {
  await rm(join(d, 'audio', m.files[0].storageKey)); await assert.rejects(verifyOperationsBackup(d, m, n))
}))
test('rejects duplicate storage keys', () => fixture(async (d, m, n) => {
  m.files.push({ ...m.files[0] }); await assert.rejects(verifyOperationsBackup(d, m, n))
}))
test('rejects path traversal and wrong format', () => fixture(async (d, m, n) => {
  const key = m.files[0].storageKey
  m.files[0].storageKey = '../database.sql'; await assert.rejects(verifyOperationsBackup(d, m, n))
  m.files[0].storageKey = key; m.files[0].format = 'WAV'; await assert.rejects(verifyOperationsBackup(d, m, n))
}))
test('rejects symbolic audio directory', () => fixture(async (d, m, n) => {
  await rm(join(d, 'audio'), { recursive: true })
  await mkdir(join(d, 'replacement'))
  await symlink(join(d, 'replacement'), join(d, 'audio'), 'junction')
  await assert.rejects(verifyOperationsBackup(d, m, n))
}))
