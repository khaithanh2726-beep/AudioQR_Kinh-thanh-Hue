import assert from 'node:assert/strict'

// Đọc cấu hình runtime đã ghi giá trị cụ thể; không thực thi lệnh hoặc nội suy secret.
export function parseRuntimeEnv(text) {
  const result = {}
  for (const raw of text.replace(/^\uFEFF/, '').split(/\r?\n/)) {
    const line = raw.trim()
    if (!line || line.startsWith('#')) continue
    const separator = line.indexOf('=')
    assert(separator > 0, 'Invalid runtime configuration line')
    const key = line.slice(0, separator).trim()
    assert.match(key, /^[A-Z][A-Z0-9_]*$/, 'Invalid runtime configuration key')
    let value = line.slice(separator + 1).trim()
    const quote = value[0]
    if (quote === '"' || quote === "'") {
      assert(value.length > 1 && value.at(-1) === quote, 'Unclosed runtime configuration quote')
      value = value.slice(1, -1)
    }
    assert(!value.includes('\0') && !value.includes('\r') && !value.includes('\n'), 'Invalid runtime configuration value')
    assert(quote === "'" || !value.includes('$'), 'Resolve interpolation before runtime operation')
    result[key] = value
  }
  return result
}
