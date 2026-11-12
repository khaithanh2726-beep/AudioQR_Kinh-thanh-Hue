import test from 'node:test'
import assert from 'node:assert/strict'
import { createWorkspace } from '../src/admin/workspace.js'
import { AdminApiError } from '../src/admin/api.js'
import { decimal, prepareRegion } from '../src/admin/geometry.js'

function fixture() {
  const memory = new Map(), calls = []
  let now = 0, version = '"1"', contentVersion = '"slot:1"', account = 'a'
  const content = { draftText: { textRevisionId: 't1', body: 'saved', contentHash: 'hash', reviewReference: { version: 1, etag: '"review:1"', locator: 'text-proof' } }, appliedPair: null }
  const audio = { audioRevisionId: 'a1', textRevisionId: 't1', language: 'vi', version: 1, etag: '"audio:1"', locator: 'audio-proof', mp3Checksum: 'mp3' }
  let override
  const storage = { getItem: k => memory.get(k), setItem: (k,v) => memory.set(k,v), removeItem: k => memory.delete(k) }
  const api = { send: async (path, options = {}) => {
    calls.push({ path, options })
    if (override) { const result = await override(path, options); if (result !== undefined) return result }
    if (path === '/admin/session' || path === '/auth/login') return { data: { accountId: account, serverNow: '2026-11-10T00:00:00Z', expiresAt: '2026-11-10T00:30:00Z' } }
    if (path === '/admin/places') return { data: [] }
    if (path === '/admin/content-completeness') return { data: [{placeId:'p1',language:'vi',draftAudios:[audio],draftMissingConditions:[]}] }
    if (path === '/admin/places/p1') return { data: { placeId: 'p1', displayName: 'Place', version: 1 }, etag: version }
    if (path.endsWith('/content/vi') && options.method === undefined) return { data: content, etag: contentVersion }
    if (path.includes('/audio-revisions/')) return { data: audio, etag: audio.etag }
    if (path === '/admin/command-windows') return { data: { windowId: 'w1' } }
    throw new Error('Unexpected ' + path)
  } }
  const make = () => createWorkspace({ api, storage, uuid: () => 'k1', clock: () => now })
  return { make, calls, content, audio, setVersion: v => { contentVersion = v; version = v }, setAccount: v => account = v, advance: v => now += v, override: fn => override = fn }
}

test('reload preserves partial polygon and requires session before exposing it', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('region','p1')
  w.editDraft({ vertices: [[107,16],[108,17]], closed: false })
  const reload = f.make(); assert.equal(reload.state.locked,true); await reload.bootstrap()
  assert.equal(reload.state.editor.draft.vertices.length,2); assert.equal(reload.state.editor.draft.closed,false)
  assert.equal(f.calls.some(c => c.options.method === 'POST'),false)
})
test('reload preserves text and both reference drafts without sending them', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1')
  w.editDraft({ body: 'unsaved', textLocator: 'new proof', audioLocator: 'new audio proof' })
  const reload = f.make(); await reload.bootstrap()
  assert.equal(reload.state.editor.draft.body,'unsaved'); assert.equal(reload.state.editor.draft.audioLocator,'new audio proof'); assert.equal(reload.dirty(),true)
})
test('account switch clears content editor as well as place form', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); w.editDraft({ body:'secret' }); w.lock(); f.setAccount('b'); await w.login('b','password')
  assert.equal(w.state.editor,null)
})
test('new server version blocks command until explicit review acceptance', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); w.editDraft({body:'mine'}); f.setVersion('"2"'); await w.refreshEditor()
  assert.equal(w.state.editor.conflict,true); assert.equal(w.state.editor.draft.body,'mine'); assert.equal(await w.saveText(),false)
  w.acceptEditorVersion(); assert.equal(w.state.editor.contentEtag,'"2"')
})
test('late editor response after lock does not expose data', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap()
  let release; const delayed = new Promise(r => release = r)
  f.override(async path => { if (path.endsWith('/content/vi')) { await delayed; return { data:f.content,etag:'"slot:1"' } } })
  const opening = w.openEditor('content','p1'); await Promise.resolve(); await Promise.resolve(); w.lock(); release(); await opening
  assert.equal(w.state.locked,true); assert.equal(w.state.editor.content,undefined)
})
test('unknown text save keeps key payload and version across restore', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); w.editDraft({body:'new'})
  f.override(async (path,options) => { if (path.endsWith('/text-revisions') && options.method === 'POST') throw new Error('lost') })
  assert.equal(await w.saveText(),false); const original = JSON.stringify(w.state.pending)
  const reload = f.make(); await reload.bootstrap(); assert.equal(JSON.stringify(reload.state.pending),original)
})
test('expiry blocks editor write before obtaining a command window', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); f.advance(1800000)
  const count = f.calls.length; assert.equal(await w.saveText(),false); assert.equal(f.calls.length,count)
})
test('audio language mismatch cannot become selected pair', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); f.audio.language='fr'; assert.equal(await w.chooseAudio('a1'),false); assert.equal(w.state.editor.audioReview,undefined)
})
test('unsaved body prevents pair apply even with two valid references', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); await w.chooseAudio('a1'); w.editDraft({body:'changed'}); const count = f.calls.length
  assert.equal(await w.applyPair(),false); assert.equal(f.calls.length,count)
})
test('pair command includes both revisions hashes review versions and slot ETag', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); await w.chooseAudio('a1')
  f.override(async (path,options) => { if (path.endsWith('/active-pair')) { assert.equal(options.etag,'"slot:1"'); assert.equal(options.body.textRevisionId,'t1'); assert.equal(options.body.audioRevisionId,'a1'); assert.equal(options.body.expectedMp3Checksum,'mp3'); assert.equal(options.body.textReviewVersion,1); return {data:{result:{placeId:'p1'}}} } })
  assert.equal(await w.applyPair(),true); assert.equal(w.state.pending,null)
})
test('review write targets saved revision while preserving unsaved body', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1'); w.editDraft({body:'mine',textLocator:'proof'})
  f.override(async (path,options) => { if (options.method === 'PUT') { assert.equal(path,'/admin/text-revisions/t1/review-reference'); assert.equal(options.etag,'"review:1"'); assert.equal(options.body.body,undefined); return {data:{result:{placeId:'p1'}}} } })
  assert.equal(await w.saveReview('text'),true); assert.equal(w.state.editor.draft.body,'mine'); assert.equal(w.dirty(),true)
})
test('failed refresh after committed editor operation retains success evidence', async () => {
  const f = fixture(), w = f.make(); await w.bootstrap(); await w.openEditor('content','p1')
  let committed=false; f.override(async (path,options) => { if (options.method === 'POST' && path.endsWith('/text-revisions')) { committed=true; return {data:{result:{placeId:'p1'}}} } if(committed && path === '/admin/places/p1') throw new Error('offline') })
  await w.saveText(); assert.equal(w.state.pending,null); assert.match(w.state.message,/commit/); assert.equal(w.state.editor.reviewRequired,true)
})
test('region hash uses backend plain decimal canonicalization', async () => {
  assert.equal(decimal(1e-7),'0.0000001'); assert.equal(decimal(-0),'0')
  let bytes
  const result = await prepareRegion({vertices:[[107,16],[108,16],[107,17]],closed:true}, async input => {bytes = new TextDecoder().decode(input); return new Uint8Array(32).buffer})
  assert.equal(bytes,'WGS84_LON_LAT\n{"type":"Polygon","coordinates":[[[107,16],[108,16],[107,17],[107,16]]]}'); assert.equal(result.geometryHash,'0'.repeat(64))
})
test('incomplete region cannot be previewed as saved geometry', async () => {
  await assert.rejects(prepareRegion({vertices:[[107,16],[108,16]],closed:false}))
})

test('choosing another audio cannot overwrite an unsaved reference', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); await w.openEditor('content','p1'); await w.chooseAudio('a1'); w.editDraft({audioLocator:'unsaved proof'})
  assert.equal(await w.chooseAudio('a2'),false); assert.equal(w.state.editor.draft.audioLocator,'unsaved proof')
})
test('confirmed receipt survives same-account reload without replay', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); await w.openEditor('content','p1')
  f.override(async (path,options) => { if (path.endsWith('/text-revisions') && options.method === 'POST') return {data:{result:{placeId:'p1'},resultCode:'TEXT_SAVED'}} })
  await w.saveText(); const reload=f.make(); await reload.bootstrap(); assert.equal(reload.state.lastOperation.resultCode,'TEXT_SAVED'); assert.equal(reload.state.pending,null)
})
test('lock during command window allocation prevents dispatch', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); await w.openEditor('content','p1')
  let release; const delayed=new Promise(r=>release=r)
  f.override(async path => { if (path === '/admin/command-windows') { await delayed; return {data:{windowId:'late'}} } })
  const saving=w.saveText(); await Promise.resolve(); w.lock(); release(); await saving
  assert.equal(w.state.pending,null); assert.equal(f.calls.some(c=>c.path.endsWith('/text-revisions')),false)
})

test('audio candidates are read from completeness for the exact place language and draft', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); await w.openEditor('content','p1')
  assert.equal(w.state.editor.candidates[0].audioRevisionId,'a1'); assert.equal(f.calls.some(c=>c.path === '/admin/content-completeness'),true)
})

test('committed text keeps the submitted body when another edit becomes current before refresh', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); await w.openEditor('content','p1'); w.editDraft({body:'my submitted text'})
  f.override(async (path,options) => { if(path.endsWith('/text-revisions') && options.method === 'POST') { f.content.draftText={...f.content.draftText,textRevisionId:'other',body:'other author'}; return {data:{result:{placeId:'p1',textRevisionId:'my-revision'}}} } })
  await w.saveText(); assert.equal(w.state.editor.draft.body,'my submitted text'); assert.equal(w.state.editor.draft.baseTextId,'my-revision'); assert.equal(w.state.editor.conflict,true)
})
test('failed first editor load can initialize normally on explicit refresh', async () => {
  const f=fixture(), w=f.make(); await w.bootstrap(); let once=true
  f.override(async path=>{if(path.endsWith('/content/vi') && once){once=false;throw new Error('offline')}})
  assert.equal(await w.openEditor('content','p1'),false); await w.refreshEditor(); assert.equal(w.state.editor.draft.body,'saved'); assert.equal(w.state.editor.reviewRequired,false)
})

test('saving a new draft requires settling unsaved references and clears old-version references', async () => {
  const f = fixture(), w = f.make()
  await w.bootstrap()
  await w.openEditor('content', 'p1')
  w.editDraft({ body: 'new draft', textLocator: 'unsaved reference' })
  const count = f.calls.length
  assert.equal(await w.saveText(), false)
  assert.equal(f.calls.length, count)
  assert.equal(w.state.editor.draft.textLocator, 'unsaved reference')

  w.editDraft({ textLocator: 'text-proof' })
  await w.chooseAudio('a1')
  f.override(async (path, options) => {
    if (path.endsWith('/text-revisions') && options.method === 'POST') {
      f.content.draftText = { textRevisionId: 't2', body: 'new draft' }
      return { data: { result: { textRevisionId: 't2' } } }
    }
  })
  assert.equal(await w.saveText(), true)
  assert.equal(w.state.editor.draft.textLocator, '')
  assert.equal(w.state.editor.draft.audioLocator, '')
  assert.equal(w.state.editor.audioReview, null)
  assert.equal(w.dirty(), false)
})

test('late Admin preview is discarded when the workspace locks', async () => {
  const f = fixture(), w = f.make()
  await w.bootstrap()
  await w.openEditor('content', 'p1')
  await w.chooseAudio('a1')
  let release
  const bytes = new Promise(resolve => { release = resolve })
  f.override(async path => {
    if (path.endsWith('/preview')) return { data: await bytes }
  })
  let shown = false
  const request = w.previewAudio(() => { shown = true })
  w.lock()
  release(new ArrayBuffer(1))
  await request
  assert.equal(shown, false)
})

test('TTS creation keeps the expected saved hash and the receipt key without a slot If-Match', async () => {
  const f = fixture(), w = f.make()
  await w.bootstrap()
  await w.openEditor('content', 'p1')
  f.override(async (path, options) => {
    if (path.endsWith('/tts-jobs') && options.method === 'POST') {
      assert.equal(path, '/admin/text-revisions/t1/tts-jobs')
      assert.equal(options.body.expectedTextHash, 'hash')
      assert.equal(options.etag, undefined)
      assert.equal(options.key, 'w1:k1')
      return { data: { result: { jobId: 'job1' } } }
    }
    if (path === '/admin/tts-jobs/job1') return { data: { jobId: 'job1', phase: 'QUEUED' } }
  })
  assert.equal(await w.createTts(), true)
  assert.equal(w.state.editor.ttsJob.jobId, 'job1')
  assert.equal(w.state.pending, null)
})

test('a freshly recorded review at version zero can be applied as a pair', async () => {
  const f = fixture(), w = f.make()
  f.content.draftText.reviewReference.version = 0
  f.audio.version = 0
  await w.bootstrap()
  await w.openEditor('content', 'p1')
  await w.chooseAudio('a1')
  f.override(async (path, options) => {
    if (path.endsWith('/active-pair')) {
      assert.equal(options.body.textReviewVersion, 0)
      assert.equal(options.body.audioReviewVersion, 0)
      return { data: { result: { placeId: 'p1' } } }
    }
  })
  assert.equal(await w.applyPair(), true)
})

test('TTS retry is limited by retryAction and uses the observed phase version', async () => {
  const f = fixture(), w = f.make()
  await w.bootstrap()
  await w.openEditor('content', 'p1')
  w.state.editor.ttsJob = { jobId: 'job1', retryAction: 'INSPECT_ONLY', phaseVersion: 3 }
  assert.equal(await w.retryTts(), false)
  w.state.editor.ttsJob.retryAction = 'REPROCESS_LOCAL'
  f.override(async (path, options) => {
    if (path.endsWith('/retry')) {
      assert.equal(options.body.expectedPhaseVersion, 3)
      return { data: { result: { jobId: 'job1' } } }
    }
    if (path === '/admin/tts-jobs/job1') return { data: { jobId: 'job1', phase: 'RESULT_STAGED' } }
  })
  assert.equal(await w.retryTts(), true)
  assert.equal(w.state.editor.ttsJob.phase, 'RESULT_STAGED')
})
