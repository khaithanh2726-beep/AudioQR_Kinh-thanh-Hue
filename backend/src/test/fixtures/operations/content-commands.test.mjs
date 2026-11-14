import test from 'node:test'
import assert from 'node:assert/strict'
import {contentCommands} from '../../../../../deploy/content-admin-client.mjs'

function fixture(write) {
  const ledger = {operations: []}, persisted = [], calls = []
  const api = {request: async (...args) => {
    calls.push(args)
    if (args[0] === '/admin/command-windows') return {status: 201, data: {windowId: 'window'}}
    return write(...args)
  }}
  return {ledger, persisted, calls, command: () => contentCommands(api, ledger, async () => {
    persisted.push(structuredClone(ledger))
  })}
}

test('records pending key before dispatch and reuses a committed receipt without writing again', async () => {
  const f = fixture(async () => {
    assert.equal(f.persisted.at(-1).operations[0].status, 'PENDING')
    return {status: 200, data: {result: {version: 2}}}
  })
  const command = await f.command()
  assert.deepEqual(await command('apply', '/admin/sample', 'PUT', {value: 1}, '"v1"'), {version: 2})
  assert.deepEqual(await command('apply', '/admin/sample', 'PUT', {value: 1}, '"v1"'), {version: 2})
  assert.equal(f.calls.length, 2)
  assert.match(f.ledger.operations[0].key, /^window:[0-9a-f-]{36}$/)
})

test('lost response remains pending and blocks automatic redispatch', async () => {
  const f = fixture(async () => {throw new Error('response lost after possible commit')})
  const command = await f.command()
  await assert.rejects(command('apply', '/admin/sample', 'PUT', {}, '"v1"'))
  const key = f.ledger.operations[0].key
  await assert.rejects(command('apply', '/admin/sample', 'PUT', {}, '"v1"'), /Inspect the existing receipt/)
  assert.equal(f.calls.length, 2)
  assert.equal(f.ledger.operations[0].status, 'PENDING')
  assert.equal(f.ledger.operations[0].key, key)
})

test('conflict receipt is persisted and requires inspection before another write', async () => {
  const f = fixture(async () => ({status: 412, data: {code: 'STALE_VERSION'}}))
  const command = await f.command()
  await assert.rejects(command('apply', '/admin/sample', 'PUT', {}, '"v1"'))
  assert.equal(f.persisted.at(-1).operations[0].httpStatus, 412)
  await assert.rejects(command('apply', '/admin/sample', 'PUT', {}, '"v1"'), /Inspect the existing receipt/)
  assert.equal(f.calls.length, 2)
})

test('cannot dispatch a content command when issuing the command window fails', async () => {
  let calls = 0
  await assert.rejects(contentCommands({request: async () => {calls++; return {status: 401}}}, {operations: []}, async () => {}))
  assert.equal(calls, 1)
})
