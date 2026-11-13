import assert from 'node:assert/strict'
import { randomUUID } from 'node:crypto'

export async function commandsFor(session, ledger, persist) {
  const issued = await session.request('/admin/command-windows', 'POST')
  assert.equal(issued.status, 201)
  ledger.operations ||= []
  return async (name, path, method, payload, etag) => {
    const previous = ledger.operations.find(step => step.name === name)
    if (previous) {
      assert.equal(previous.status, 'COMMITTED', 'Inspect pending receipt before reissuing an operation')
      return previous.response.result
    }
    const step = { name, path, method, payload, etag, key: issued.data.windowId + ':' + randomUUID(), status: 'PENDING' }
    ledger.operations.push(step); await persist()
    const response = await session.request(path, method, payload, etag, step.key)
    step.httpStatus = response.status; step.response = response.data
    step.status = response.status >= 200 && response.status < 300 ? 'COMMITTED' : 'REQUIRES_INSPECTION'
    await persist()
    assert.equal(step.status, 'COMMITTED', response.data?.code)
    return response.data.result
  }
}
