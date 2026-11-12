export class Playback {
  constructor({ audio, createUrl, revokeUrl, makeBlob, checksum, now, changed = () => {} }) {
    Object.assign(this, { audio, createUrl, revokeUrl, makeBlob, checksum, now, changed })
    this.generation = 0
    this.phase = 'EMPTY'
    this.expiresAt = 0
    this.continueCurrent = false
    this.expired = false
    this.track = null
    this.url = null
    audio.onplaying = () => {
      if (this.tick() || this.continueCurrent) this.phase = 'PLAYING'
      else audio.pause()
      this.changed()
    }
    audio.onended = () => {
      this.tick()
      this.phase = 'ENDED'
      this.continueCurrent = false
      if (this.expired) this.detach()
      this.changed()
    }
    audio.onpause = () => {
      this.tick()
      if (this.phase === 'PLAYING') this.phase = 'PAUSED'
      this.changed()
    }
    audio.onerror = () => {
      this.tick()
      this.continueCurrent = false
      this.phase = 'ERROR'
      this.changed()
    }
  }

  session(expiresAt) {
    this.reset()
    this.expiresAt = Date.parse(expiresAt)
    this.expired = false
  }

  tick() {
    if (!this.expired && this.expiresAt && this.now() >= this.expiresAt) {
      this.expired = true
      this.continueCurrent = this.phase === 'PLAYING' && this.url !== null && !this.audio.ended
      if (!this.continueCurrent) {
        this.detach()
        this.phase = 'LOCKED'
      }
      this.changed()
    }
    return !this.expired && this.expiresAt > this.now()
  }

  detach() {
    this.audio.onloadedmetadata = null
    this.audio.pause()
    this.audio.removeAttribute('src')
    this.audio.load()
    if (this.url) this.revokeUrl(this.url)
    this.url = null
  }

  reset() {
    this.generation++
    this.phase = 'EMPTY'
    this.continueCurrent = false
    this.track = null
    this.detach()
    this.changed()
  }

  async load(track, fetchBytes, { autoplay = false, memento = null } = {}) {
    this.reset()
    if (!this.tick() || track?.availability !== 'READY') return false
    const generation = this.generation
    this.phase = 'LOADING'
    this.track = track
    this.changed()
    try {
      const bytes = await fetchBytes()
      if (generation !== this.generation || !this.tick()) return false
      if (bytes.byteLength !== track.byteSize || await this.checksum(bytes) !== track.checksum) throw new Error('AUDIO_INTEGRITY')
      if (generation !== this.generation || !this.tick()) return false
      this.url = this.createUrl(this.makeBlob(bytes))
      this.audio.onloadedmetadata = () => {
        if (generation !== this.generation || !this.tick()) return
        if (memento?.revisionId === track.revisionId && memento?.checksum === track.checksum && Number.isFinite(memento.position)) {
          this.audio.currentTime = Math.max(0, Math.min(memento.position, this.audio.duration || 0))
        }
      }
      this.audio.src = this.url
      this.phase = 'PAUSED'
      this.audio.load()
      this.changed()
      if (autoplay) await this.toggle()
      return true
    } catch (error) {
      if (generation === this.generation) {
        this.phase = this.expired ? 'LOCKED' : 'ERROR'
        this.changed()
      }
      return false
    }
  }

  async toggle() {
    this.tick()
    if ((!this.expired && this.url) || (this.expired && this.continueCurrent)) {
      if (this.phase === 'PLAYING') {
        this.audio.pause()
        this.phase = 'PAUSED'
      } else if (this.phase === 'PAUSED' || (!this.expired && this.phase === 'ENDED')) {
        if (this.phase === 'ENDED') this.audio.currentTime = 0
        const generation = this.generation
        try {
          await this.audio.play()
          if (generation === this.generation && this.url && (!this.expired || this.continueCurrent) && !this.audio.paused) {
            this.phase = 'PLAYING'
            this.tick()
          }
        } catch (error) {
          if (generation === this.generation) this.phase = 'PAUSED'
        }
      }
      this.changed()
    }
  }

  seek(position) {
    if (!this.tick() || !this.url || !Number.isFinite(position)) return
    this.audio.currentTime = Math.max(0, Math.min(position, this.audio.duration || 0))
    if (this.phase === 'ENDED') this.phase = 'PAUSED'
    this.changed()
  }

  memento() {
    if (!this.track || !this.url || this.expired) return null
    return { revisionId: this.track.revisionId, checksum: this.track.checksum, position: this.audio.currentTime }
  }
}
