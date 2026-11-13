// Chỉ trao đổi quyền document, không gửi credential hoặc nội dung qua kênh chung
export async function acquireOwner(ownerId, onWaiting = () => {}, signal = null) {
  if (!globalThis.isSecureContext || !navigator.locks || !globalThis.BroadcastChannel) {
    throw new Error('OWNER_UNSUPPORTED')
  }
  const channel = new BroadcastChannel('hueqr-document-owner')
  let releaseLock
  let owns = false
  const abort = new AbortController()
  const held = new Promise(resolve => { releaseLock = resolve })
  const probe = crypto.randomUUID()
  let occupied
  const response = new Promise(resolve => { occupied = resolve })
  channel.onmessage = ({ data }) => {
    if (data?.ownerId !== ownerId) return
    if (data.type === 'probe' && owns) channel.postMessage({ type: 'occupied', ownerId, probe: data.probe })
    if (data.type === 'occupied' && data.probe === probe) occupied(true)
  }
  const release = () => {
    owns = false
    abort.abort()
    releaseLock()
    channel.close()
  }
  const cancelled = new Promise((resolve, reject) => {
    const cancel = () => { release(); reject(new DOMException('Document left', 'AbortError')) }
    if (signal?.aborted) cancel()
    else signal?.addEventListener('abort', cancel, { once: true })
  })
  try {
    const acquired = await Promise.race([cancelled, new Promise((resolve, reject) => {
      navigator.locks.request(`hueqr-owner:${ownerId}`, { ifAvailable: true }, async lock => {
        if (!lock || abort.signal.aborted) { resolve(false); return }
        owns = true
        resolve(true)
        await held
      }).catch(reject)
    })])
    if (acquired) return { acquired: true, release }
    onWaiting()
    channel.postMessage({ type: 'probe', ownerId, probe })
    const queued = new Promise((resolve, reject) => {
      navigator.locks.request(`hueqr-owner:${ownerId}`, { signal: abort.signal }, async lock => {
        owns = true
        resolve(true)
        await held
      }).catch(error => { if (error.name !== 'AbortError') reject(error) })
    })
    // Chờ khóa thật hoặc phản hồi chủ đang sống, không suy quyền từ việc hết thời gian chờ
    const result = await Promise.race([cancelled, queued.then(() => 'acquired'), response.then(() => 'occupied')])
    if (result === 'occupied') {
      abort.abort()
      release()
      return { acquired: false, release: () => {} }
    }
    return { acquired: true, release }
  } catch (error) {
    release()
    throw error
  }
}
