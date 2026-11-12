import test from 'node:test'
import assert from 'node:assert/strict'
import { createAdminClient, AdminApiError } from '../src/admin/api.js'
import { createWorkspace } from '../src/admin/workspace.js'

const accountA = 'account-a'
const accountB = 'account-b'
const session = (accountId = accountA) => ({ accountId, loginName: accountId, role: 'ADMIN',
  serverNow: '2026-11-09T14:00:00Z', expiresAt: '2026-11-09T14:30:00Z' })
const operation = { result: { placeId: 'place-1', duplicateNameWarning: false }, resultCode: 'PLACE_CREATED' }

test('visible-page bootstrap waits for an in-flight read and restores the unlocked session', async () => {
  const f = fixture()
  let release, listStarted
  const delayed = new Promise(resolve => { release = resolve })
  const started = new Promise(resolve => { listStarted = resolve })
  let lists = 0
  f.handler(async path => {
    if (path === '/admin/session') return { data: session() }
    if (path === '/admin/places') {
      if (++lists === 1) { listStarted(); await delayed }
      return { data: [] }
    }
    throw new Error(path)
  })
  const initial = f.work.bootstrap()
  await started
  f.work.lock()
  await f.work.bootstrap()
  release()
  await initial
  assert.equal(f.work.state.locked, false)
  assert.equal(f.work.state.busy, false)
  assert.equal(lists, 2)
})

test('a session response arriving after page lock cannot reveal Admin content', async () => {
  const f = fixture()
  let release
  const delayed = new Promise(resolve => { release = resolve })
  f.handler(async () => { await delayed; return { data: session() } })
  const initial = f.work.bootstrap()
  f.work.lock()
  release()
  await initial
  assert.equal(f.work.state.locked, true)
})

test('Admin audio preview verifies saved revision headers and the downloaded checksum', async () => {
  const bytes = new Uint8Array([1, 2, 3])
  const preview = {
    textRevisionId: 'text-1',
    language: 'vi',
    mp3Checksum: '039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81',
  }
  let revision = preview.textRevisionId
  const client = createAdminClient(async (path, options) => {
    assert.equal(path, '/api/v1/admin/audio-revisions/audio-1/preview')
    assert.equal(options.headers.Accept, 'audio/mpeg')
    return {
      ok: true,
      status: 200,
      headers: new Map([
        ['Content-Type', 'audio/mpeg'],
        ['X-Text-Revision-Id', revision],
        ['Content-Language', 'vi'],
        ['X-Audio-Checksum', preview.mp3Checksum],
      ]),
      arrayBuffer: async () => bytes.buffer,
    }
  })
  const path = '/admin/audio-revisions/audio-1/preview'
  assert.equal((await client.send(path, { preview })).data, bytes.buffer)
  bytes[0] = 4
  await assert.rejects(client.send(path, { preview }), { code: 'INVALID_AUDIO_PREVIEW' })
  revision = 'text-2'
  await assert.rejects(client.send(path, { preview }), { code: 'INVALID_AUDIO_PREVIEW' })
})

function fixture() {
  const memory = new Map()
  const calls = []
  let now = 0
  let send = async path => {
    if (path === '/admin/session') return { data: session() }
    if (path === '/admin/places') return { data: [] }
    throw new Error(`Unexpected ${path}`)
  }
  const storage = { getItem: key => memory.get(key) || null, setItem: (key, value) => memory.set(key, value), removeItem: key => memory.delete(key) }
  const api = { send: async (path, options) => { calls.push({ path, options }); return send(path, options) } }
  const work = createWorkspace({ api, storage, uuid: () => 'operation-1', clock: () => now })
  return { work, calls, memory, storage, api, handler: fn => { send = fn }, advance: ms => { now += ms } }
}

test('CSRF uses current session cookie and header, no guest credential or automatic replay', async () => {
  const calls = []
  const client = createAdminClient(async (path, options) => {
    calls.push({ path, options })
    if (path.endsWith('/csrf')) return { ok: true, status: 200, json: async () => ({ headerName: 'X-CSRF-TOKEN', token: 'fresh' }), headers: { get: () => null } }
    return { ok: false, status: 401, json: async () => ({ code: 'ADMIN_SESSION_EXPIRED' }), headers: { get: () => null } }
  })
  await assert.rejects(client.send('/admin/places', { method: 'POST', body: { displayName: 'Huế' } }), { status: 401 })
  assert.equal(calls.length, 2)
  assert.equal(calls[1].options.credentials, 'same-origin')
  assert.equal(calls[1].options.headers['X-CSRF-TOKEN'], 'fresh')
  assert.equal(calls[1].options.headers['X-Guest-Credential'], undefined)
})

test('bootstrap reads session and places without reporting interactive activity', async () => {
  const f = fixture()
  assert.equal(await f.work.bootstrap(), true)
  assert.equal(f.work.state.locked, false)
  assert.deepEqual(f.calls.map(x => x.path), ['/admin/session', '/admin/places'])
})

test('expiry masks data and blocks writes without a network request', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Điện Thái Hòa')
  f.advance(1800000)
  assert.equal(f.work.checkExpiry(), false)
  const count = f.calls.length
  assert.equal(await f.work.save(), false)
  assert.equal(f.calls.length, count)
  assert.equal(f.work.state.form.displayName, 'Điện Thái Hòa')
  assert.deepEqual(f.work.state.places, [])
})

test('same-account reload restores draft only after session check, without saving', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Cửu Đỉnh')
  const reopened = createWorkspace({ api: f.api, storage: f.storage, clock: () => 0 })
  assert.equal(reopened.state.form, null)
  const count = f.calls.length
  await reopened.bootstrap()
  assert.equal(reopened.state.form.displayName, 'Cửu Đỉnh')
  assert.deepEqual(f.calls.slice(count).map(x => x.path), ['/admin/session'])
})

test('different-account login removes previous draft and pending work', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Bản của A')
  f.work.lock()
  f.handler(async path => path === '/auth/login' ? { data: session(accountB) } : { data: [] })
  await f.work.login('b', 'not-persisted')
  assert.equal(f.work.state.form, null)
  assert.equal(f.work.state.pending, null)
  assert.equal(f.memory.size, 0)
})

test('unknown create keeps exact key and body across reload and explicit replay', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Ngọ Môn')
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    throw new TypeError('Network lost after commit')
  })
  assert.equal(await f.work.save(), false)
  const pending = JSON.stringify(f.work.state.pending.options)
  f.work.edit('Không được đổi payload')
  assert.equal(f.work.state.form.displayName, 'Ngọ Môn')
  f.handler(async path => {
    if (path === '/admin/session') return { data: session() }
    if (path === '/admin/places') return { data: operation }
    return { data: { placeId: 'place-1', displayName: 'Ngọ Môn', version: 0 }, etag: '"0"' }
  })
  const reopened = createWorkspace({ api: f.api, storage: f.storage, clock: () => 0 })
  const count = f.calls.length
  await reopened.bootstrap()
  assert.equal(f.calls.length, count + 1)
  assert.equal(JSON.stringify(reopened.state.pending.options), pending)
  await reopened.replay()
  const replay = f.calls.findLast(x => x.options?.key)
  assert.equal(JSON.stringify(replay.options), pending)
  assert.equal(reopened.state.pending, null)
})

test('NOT_OBSERVED keeps pending and never creates a second command', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Cung Diên Thọ')
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    if (path.includes('/operations/')) return { data: { state: 'NOT_OBSERVED' } }
    throw new Error('Connection failed')
  })
  await f.work.save()
  await f.work.inspect()
  assert.equal(f.work.state.pending.key, 'window-1:operation-1')
  assert.equal(f.calls.filter(x => x.path.endsWith('/command-windows')).length, 1)
})

test('expired-window replay does not discard an unresolved original operation', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Cung Diên Thọ')
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    throw new Error('Connection failed')
  })
  await f.work.save()
  f.handler(async () => { throw new AdminApiError(409, { code: 'COMMAND_WINDOW_EXPIRED' }) })
  await f.work.replay()
  assert.equal(f.work.state.pending.key, 'window-1:operation-1')
})

test('committed receipt followed by failed refresh stays successful and clears pending', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Ngọ Môn')
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    if (path === '/admin/places') return { data: operation }
    throw new Error('Refresh failed')
  })
  await f.work.save()
  assert.equal(f.work.state.pending, null)
  assert.equal(f.work.state.form, null)
  assert.ok(f.work.state.message.includes('Đã lưu'))
})

test('first determined validation error allows editing without an unknown operation', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('')
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    throw new AdminApiError(422, { code: 'VALIDATION_FAILED', fieldErrors: { displayName: 'Tên cần từ 1 đến 100 ký tự hiển thị.' } })
  })
  await f.work.save()
  assert.equal(f.work.state.pending, null)
  assert.ok(f.work.state.fieldErrors.displayName)
  f.work.edit('Tên mới')
  assert.equal(f.work.state.form.displayName, 'Tên mới')
})

test('late list response after lock cannot expose data', async () => {
  const f = fixture()
  await f.work.bootstrap()
  let release
  f.handler(() => new Promise(resolve => { release = resolve }))
  const request = f.work.list()
  f.work.lock()
  release({ data: [{ placeId: 'secret' }] })
  await request
  assert.deepEqual(f.work.state.places, [])
  assert.equal(f.work.state.locked, true)
})

test('blocked sessionStorage reports inability to protect work across reload', async () => {
  const f = fixture()
  const blocked = { getItem() { throw new Error() }, setItem() { throw new Error() }, removeItem() { throw new Error() } }
  const work = createWorkspace({ api: f.api, storage: blocked, clock: () => 0 })
  await work.bootstrap()
  work.newPlace()
  work.edit('Bản còn trong bộ nhớ')
  assert.equal(work.state.storageError, true)
  assert.equal(work.state.form.displayName, 'Bản còn trong bộ nhớ')
})

test('unknown logout masks data without claiming session termination', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.handler(async () => { throw new Error('Network unavailable') })
  await f.work.logout()
  assert.equal(f.work.state.locked, true)
  assert.ok(f.work.state.session)
  assert.ok(f.work.state.message.includes('Chưa xác nhận'))
})

test('restored edit compares new version and keeps draft instead of silently replacing it', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.handler(async path => path === '/admin/session' ? { data: session() }
    : { data: { placeId: 'place-1', displayName: 'Tên cũ', version: 1 }, etag: '"1"' })
  await f.work.detail('place-1')
  f.work.edit('Tên đang soạn')
  f.handler(async path => path === '/admin/session' ? { data: session() }
    : { data: { placeId: 'place-1', displayName: 'Tên người khác đã lưu', version: 2 }, etag: '"2"' })
  const reopened = createWorkspace({ api: f.api, storage: f.storage, clock: () => 0 })
  await reopened.bootstrap()
  assert.equal(reopened.state.form.displayName, 'Tên đang soạn')
  assert.equal(reopened.state.form.conflict, true)
  const count = f.calls.length
  assert.equal(await reopened.save(), false)
  assert.equal(f.calls.length, count)
  reopened.acceptCurrentVersion()
  assert.equal(reopened.state.form.etag, '"2"')
  assert.equal(reopened.state.form.displayName, 'Tên đang soạn')
})

test('deleted target on reload remains a draft and cannot recreate the old place', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.handler(async () => ({ data: { placeId: 'place-1', displayName: 'Tên cũ', version: 1 }, etag: '"1"' }))
  await f.work.detail('place-1')
  f.work.edit('Phần chưa lưu')
  f.handler(async path => {
    if (path === '/admin/session') return { data: session() }
    throw new AdminApiError(404, { code: 'LOCATION_NOT_FOUND' })
  })
  const reopened = createWorkspace({ api: f.api, storage: f.storage, clock: () => 0 })
  await reopened.bootstrap()
  assert.equal(reopened.state.form.deleted, true)
  const count = f.calls.length
  assert.equal(await reopened.save(), false)
  assert.equal(f.calls.length, count)
})

test('successful save reads server session to track Q3 extension without a fake activity', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Ngọ Môn')
  f.advance(1500000)
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return { data: { windowId: 'window-1' } }
    if (path === '/admin/places') return { data: operation }
    if (path === '/admin/session') return { data: { ...session(), serverNow: '2026-11-09T14:25:00Z', expiresAt: '2026-11-09T14:55:00Z' } }
    return { data: { placeId: 'place-1', displayName: 'Ngọ Môn', version: 0 }, etag: '"0"' }
  })
  await f.work.save()
  f.advance(400000)
  assert.equal(f.work.checkExpiry(), true)
  assert.equal(f.calls.filter(x => x.path === '/admin/session/activity').length, 0)
})

test('network delay reading session does not extend the remaining idle deadline', async () => {
  const f = fixture()
  f.handler(async path => {
    if (path === '/admin/session') {
      f.advance(800)
      return { data: { ...session(), expiresAt: '2026-11-09T14:00:01Z' } }
    }
    return { data: [] }
  })
  await f.work.bootstrap()
  assert.equal(f.work.state.locked, false)
  f.advance(200)
  assert.equal(f.work.checkExpiry(), false)
})

test('double save click does not issue two windows while the first is in flight', async () => {
  const f = fixture()
  await f.work.bootstrap()
  f.work.newPlace()
  f.work.edit('Ngọ Môn')
  let release
  f.handler(async path => {
    if (path.endsWith('/command-windows')) return new Promise(resolve => { release = resolve })
    if (path === '/admin/places') return { data: operation }
    if (path === '/admin/session') return { data: session() }
    return { data: { placeId: 'place-1', displayName: 'Ngọ Môn' }, etag: '"0"' }
  })
  const first = f.work.save()
  assert.equal(await f.work.save(), false)
  release({ data: { windowId: 'window-1' } })
  await first
  assert.equal(f.calls.filter(x => x.path.endsWith('/command-windows')).length, 1)
})
