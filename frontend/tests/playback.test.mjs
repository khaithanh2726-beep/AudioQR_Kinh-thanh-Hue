import test from 'node:test'
import assert from 'node:assert/strict'
import { Playback } from '../src/guest/playback.js'

export function registerPlaybackTests(testCase, assertThat) {
  function fixture() {
    let now = 0
    const revoked = []
    const audio = {
      src: '', paused: true, ended: false, currentTime: 0, duration: 10,
      pause() { this.paused = true; this.onpause?.() },
      play() { this.paused = false; this.onplaying?.(); return Promise.resolve() },
      removeAttribute() { this.src = '' },
      load() { if (this.src) this.onloadedmetadata?.() },
    }
    const player = new Playback({ audio, createUrl: () => 'blob:test', revokeUrl: url => revoked.push(url),
      makeBlob: bytes => bytes, checksum: async () => 'hash', now: () => now,
    })
    player.session(new Date(1000).toISOString())
    const track = { revisionId: 'revision-a', checksum: 'hash', byteSize: 3, availability: 'READY' }
    return { player, audio, track, revoked, setNow: value => { now = value }, load: options => player.load(track, async () => new ArrayBuffer(3), options) }
  }

  testCase('chỉ phát sau khi nhận đủ bytes và mã Hash khớp', async () => {
    const f = fixture()
    await f.load({ autoplay: true })
    assertThat.equal(f.player.phase, 'PLAYING')
    assertThat.equal(f.audio.src, 'blob:test')
  })

  testCase('sai kích thước không gắn nguồn và không phát', async () => {
    const f = fixture()
    await f.player.load(f.track, async () => new ArrayBuffer(1), { autoplay: true })
    assertThat.equal(f.player.phase, 'ERROR')
    assertThat.equal(f.audio.src, '')
  })

  testCase('sai mã Hash không phát dù đủ kích thước', async () => {
    const f = fixture()
    f.player.checksum = async () => 'other-hash'
    await f.load({ autoplay: true })
    assertThat.equal(f.player.phase, 'ERROR')
    assertThat.equal(f.audio.src, '')
  })

  testCase('response cũ đến sau lựa chọn mới không thay bài', async () => {
    const f = fixture()
    let resolveOld
    const pending = f.player.load(f.track, () => new Promise(resolve => { resolveOld = resolve }), { autoplay: true })
    await f.player.load({ ...f.track, revisionId: 'revision-b' }, async () => new ArrayBuffer(3))
    resolveOld(new ArrayBuffer(3))
    await pending
    assertThat.equal(f.player.track.revisionId, 'revision-b')
    assertThat.equal(f.player.phase, 'PAUSED')
  })

  testCase('đang tải khi chạm hạn thì loại bytes muộn và không cấp ngoại lệ', async () => {
    const f = fixture()
    let resolveBytes
    const pending = f.player.load(f.track, () => new Promise(resolve => { resolveBytes = resolve }), { autoplay: true })
    f.setNow(1000)
    f.player.tick()
    resolveBytes(new ArrayBuffer(3))
    await pending
    assertThat.equal(f.audio.src, '')
    assertThat.equal(f.player.continueCurrent, false)
  })

  testCase('bài đang phát ở hạn giữ quyền pause-resume nhưng không tua hoặc nghe lại', async () => {
    const f = fixture()
    await f.load({ autoplay: true })
    f.audio.currentTime = 4
    f.setNow(1000)
    f.player.tick()
    assertThat.equal(f.player.continueCurrent, true)
    f.player.seek(2)
    assertThat.equal(f.audio.currentTime, 4)
    await f.player.toggle()
    assertThat.equal(f.player.phase, 'PAUSED')
    await f.player.toggle()
    assertThat.equal(f.player.phase, 'PLAYING')
    f.audio.ended = true
    f.audio.onended()
    await f.player.toggle()
    assertThat.equal(f.player.phase, 'ENDED')
    assertThat.equal(f.player.continueCurrent, false)
    assertThat.equal(f.audio.src, '')
  })

  testCase('pause trước hạn không được nghe tiếp sau hạn', async () => {
    const f = fixture()
    await f.load()
    f.setNow(1000)
    await f.player.toggle()
    assertThat.equal(f.player.continueCurrent, false)
    assertThat.equal(f.audio.src, '')
  })

  testCase('khôi phục đúng bản giữ mốc và tạm dừng', async () => {
    const f = fixture()
    await f.load({ memento: { revisionId: 'revision-a', checksum: 'hash', position: 4 } })
    assertThat.equal(f.audio.currentTime, 4)
    assertThat.equal(f.player.phase, 'PAUSED')
  })

  testCase('mốc của bản khác không áp sang bài mới', async () => {
    const f = fixture()
    await f.load({ memento: { revisionId: 'revision-old', checksum: 'hash', position: 4 } })
    assertThat.equal(f.audio.currentTime, 0)
    assertThat.equal(f.player.phase, 'PAUSED')
  })

  testCase('đổi bài nhả URL cũ và không tự phát bài đổi tiếng', async () => {
    const f = fixture()
    await f.load({ autoplay: true })
    await f.load({ autoplay: false })
    assertThat.deepEqual(f.revoked, ['blob:test'])
    assertThat.equal(f.player.phase, 'PAUSED')
  })

  testCase('tua sau hết bài còn hạn giữ trạng thái tạm dừng', async () => {
    const f = fixture()
    await f.load({ autoplay: true })
    f.audio.ended = true
    f.audio.onended()
    f.player.seek(3)
    assertThat.equal(f.audio.currentTime, 3)
    assertThat.equal(f.player.phase, 'PAUSED')
  })

  testCase('mở phiên khác không khôi phục ngoại lệ của bài cũ', async () => {
    const f = fixture()
    await f.load({ autoplay: true })
    f.setNow(1000)
    f.player.tick()
    f.player.session(new Date(2000).toISOString())
    assertThat.equal(f.player.continueCurrent, false)
    assertThat.equal(f.player.url, null)
    assertThat.equal(f.player.memento(), null)
  })

  testCase('play bị trình duyệt từ chối không báo đang phát', async () => {
    const f = fixture()
    f.audio.play = () => Promise.reject(new Error('NotAllowedError'))
    await f.load({ autoplay: true })
    assertThat.equal(f.player.phase, 'PAUSED')
    assertThat.equal(f.player.continueCurrent, false)
  })
}

registerPlaybackTests(test, assert)
