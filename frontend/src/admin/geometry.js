// Định dạng số thập phân trùng quy tắc toPlainString của backend
export function decimal(value) {
  if (!Number.isFinite(value)) throw new Error('Tọa độ phải là số hữu hạn')
  const [mantissa, exp] = String(value).toLowerCase().split('e')
  if (!exp) return value === 0 ? '0' : mantissa
  const negative = mantissa.startsWith('-')
  const digits = mantissa.replace('-', '').replace('.', '')
  const point = mantissa.replace('-', '').split('.')[0].length + Number(exp)
  const text = point <= 0 ? '0.' + '0'.repeat(-point) + digits : point >= digits.length ? digits + '0'.repeat(point - digits.length) : digits.slice(0, point) + '.' + digits.slice(point)
  return (negative ? '-' : '') + text
}
export async function prepareRegion(draft, digest = bytes => crypto.subtle.digest('SHA-256', bytes)) {
  const v = draft.vertices
  if (!draft.closed || !Array.isArray(v) || v.length < 3) throw new Error('Cần khép vùng có ít nhất ba đỉnh')
  if (v.some(p => p.length !== 2 || !Number.isFinite(p[0]) || !Number.isFinite(p[1]) || Math.abs(p[0]) > 180 || Math.abs(p[1]) > 90)) throw new Error('Tọa độ ngoài phạm vi WGS84')
  const ring = [...v, v[0]]
  const canonical = '{"type":"Polygon","coordinates":[[' + ring.map(p => '[' + decimal(p[0]) + ',' + decimal(p[1]) + ']').join(',') + ']]}'
  const bytes = await digest(new TextEncoder().encode('WGS84_LON_LAT\n' + canonical))
  const geometryHash = Array.from(new Uint8Array(bytes), x => x.toString(16).padStart(2, '0')).join('')
  return { polygon: JSON.parse(canonical), mapSpaceKey: 'WGS84_LON_LAT', geometryHash }
}
