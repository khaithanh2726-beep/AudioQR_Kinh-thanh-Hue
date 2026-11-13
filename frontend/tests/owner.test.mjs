import test from 'node:test'
import assert from 'node:assert/strict'
import { acquireOwner } from '../src/guest/owner.js'

test('initial document lock uses Chrome-compatible options and releases the lock', async () => {
  const originals = new Map(['navigator', 'isSecureContext', 'BroadcastChannel'].map(name => [name, Object.getOwnPropertyDescriptor(globalThis, name)]))
  let held = false
  class Channel {
    postMessage() {}
    close() {}
  }
  try {
    Object.defineProperty(globalThis, 'isSecureContext', { configurable: true, value: true })
    Object.defineProperty(globalThis, 'BroadcastChannel', { configurable: true, value: Channel })
    Object.defineProperty(globalThis, 'navigator', {
      configurable: true,
      value: { locks: { async request(name, options, callback) {
        if (options.ifAvailable && options.signal) throw new DOMException('Incompatible lock options', 'NotSupportedError')
        held = true
        await callback({ name })
        held = false
      } } },
    })
    const owner = await acquireOwner('test-document')
    assert.equal(owner.acquired, true)
    assert.equal(held, true)
    owner.release()
    await new Promise(resolve => setImmediate(resolve))
    assert.equal(held, false)
  } finally {
    for (const [name, descriptor] of originals) {
      if (descriptor) Object.defineProperty(globalThis, name, descriptor)
      else delete globalThis[name]
    }
  }
})
