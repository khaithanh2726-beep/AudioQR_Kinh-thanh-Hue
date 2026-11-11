// Không gửi request tới OSM trong kiểm tự động; ảnh này chỉ kiểm cơ chế tải/lỗi/tương tác.
export async function interceptTiles(page, fails = () => false) {
  const requests = []
  await page.route('https://tile.openstreetmap.org/**', async route => {
    requests.push(route.request().url())
    if (fails()) { await route.abort(); return }
    await route.fulfill({ contentType: 'image/svg+xml', body: '<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256"><rect width="256" height="256" fill="#e6e6e6"/><path d="M0 128H256M128 0V256" stroke="#ccc"/><text x="24" y="115" font-size="20" fill="#555">TEST TILE</text></svg>' })
  })
  return requests
}
