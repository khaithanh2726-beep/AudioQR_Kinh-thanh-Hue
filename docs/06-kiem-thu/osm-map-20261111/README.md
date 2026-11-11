# Nền OpenStreetMap và khôi phục preview — 11/11/2026

## Kết quả và phạm vi

Đã thay nền vẽ lại giản lược bằng lớp raster OpenStreetMap tiêu chuẩn trong nguồn Khách/Admin; [UI 3.2.1](../../04-giao-dien/ui-design.md#ui-reference-red). Ảnh Docker mới đã build và chạy tại `http://127.0.0.1:18082`; trang rà năm vùng tại `/map-preview.html`, Admin tại `/admin.html`. Chưa xác nhận hình ảnh nền OSM thật trên máy kiểm vì hai lần kiểm kết nối trực tiếp tới tile đều lỗi. Không kết luận toàn UI hoặc đồ án đã nghiệm thu.

Nền và vùng tách độc lập: nền dùng tile HTTPS; vùng Khách chỉ lấy từ API. Cờ `VITE_MAP_PREVIEW` không tự cấp vùng cho phiên Khách. Giữ polygon/ranh giới đề xuất ở trạng thái chờ duyệt, 5 điểm/20 slot/5 bài vi nháp, 0 cặp áp dụng. Không thêm GPS, khoảng cách, mã phiên hoặc quét QR trong trang.

## Mã mới/sửa

- T04/T06: thêm `frontend/src/map/config.js`, `basemap.js`, `components/BasemapStatus.vue`; sửa `local-basemap.js`, `GuestMap.vue`, `RegionEditor.vue`, `App.vue`, `style.css`, `guest/messages.js`; sửa ba HTML và cấu hình mẫu frontend. Lớp nền có ghi công, trạng thái tải/lỗi và thử lại riêng; nhãn lỗi có bốn ngôn ngữ. Số/chú giải phân biệt điểm thay tên dài đè nhau trên map; hình học thay đổi cùng ID vẫn được tính lại khung.
- Kiểm: thêm `tests/basemap-config.test.mjs`, `tests/e2e/osm-map.spec.mjs`, `tests/e2e/helpers/tile-fixture.mjs`; cập nhật hai spec UI/map trước. `frontend/.gitignore` loại các thư mục đầu ra `test-results-*` khỏi nguồn commit.
- T10: `deploy/Dockerfile`, `deploy/compose.yaml`, `deploy/.env.example` truyền lựa chọn nền và URL tile lúc build, giữ phiên bản công cụ/phụ thuộc.
- Fixture demo: thêm `backend/src/test/fixtures/demo/refresh-preview.mjs` và cập nhật README. Không đổi Java/schema/API. [Đối chiếu](backend-source-comparison.json): **229/229 file baseline backend khớp Hash**, không mất/sửa file baseline.
- Tài liệu: cập nhật frontend README, docs README, UI, hướng dẫn preview, bảng issue, roadmap và handoff. Không sửa 89 ô checklist hoặc GitHub.

## Kiểm đã thực chạy

| Phép kiểm | Kết quả / giới hạn |
|---|---|
| Logic frontend | **65/65**, 0 lỗi/skip; có thêm kiểm cấu hình chế độ/HTTPS/template và không đặt secret trong URL tile. |
| Frontend build | Build mặc định trên Windows và build preview trong Docker đạt. Không đổi Node/npm/Vue/Vite/Leaflet. |
| Docker | `docker build --pull=false --build-arg VITE_MAP_PREVIEW=true --tag hueqr-local:20261111-osm-map --file deploy/Dockerfile .` đạt, exit 0; Maven package đạt, bỏ chạy backend tests trong build này. |
| Chrome trên ảnh Docker mới | **11/11 trong 2,2 phút**, exit 0: start bốn ngôn ngữ; bốn viewport bố cục; chọn 5 vùng và kéo/phóng ở desktop/mobile; lỗi/thử lại tile; nhãn bốn tiếng; bắt đầu/kết thúc phiên qua Guest API thật; vùng Admin qua API thật, không lưu hình học. |
| Chrome offline riêng | **3/3 trong 1,9 phút**, exit 0: nền 651 path/5 vùng ở hai viewport; API Admin kiểm Mức đầy đủ lỗi/tải lại/kết quả cũ. Chạy với `VITE_MAP_BASEMAP=offline`, không chứng minh raster OSM thật. |
| Chrome bảo vệ Admin trên ảnh mới | **3/3 trong 1,3 phút**, exit 0: Q2 Hủy chuyển màn/tải lại giữ văn bản chưa lưu; 401 che dữ liệu và đăng nhập lại đúng tài khoản phục hồi bản soạn; thống kê API thật và nhãn bộ lọc thay đổi. Không gửi lệnh ghi nội dung/vùng/cặp. Ca 401 chặn phản hồi để kiểm UI, không chứng minh chờ đủ 30 phút thật. |
| Compose | `docker compose -f deploy/compose.yaml config --quiet` đạt bằng giá trị placeholder, không mở Compose runtime. |
| Fixture refresh | Lượt đầu đã chạy thành công trên database preview giữ nguyên. Sau đó bổ sung lấy tên app từ manifest và nhánh khởi động lại app cũ khi lỗi; bản cuối qua `node --check`, chưa diễn tập nhánh lỗi/rollback. |
| Kết nối tile thật | Node HEAD một URL tile lỗi `fetch failed`; curl HEAD cùng tile lỗi 35/connection reset. Không tải body, không lưu tile/kho offline. **Chưa đạt xác nhận nền thật hiển thị.** |

[verification.json](verification.json) giữ số liệu; [log Chrome Docker](browser-packaged-final.txt), [log offline](browser-offline-final.txt), [kết nối tile](tile-connectivity.json), [curl](tile-head-curl.txt). Các log lượt thất bại/ngắt trước giữ riêng, không gộp thành lượt đạt: một lượt có 8/10 ca đạt do trang reload khi sửa metadata và backend preview đang dừng; lượt offline ban đầu 2/3 do API không kết nối. Lượt build `--network=none` thất bại vì cache theo chế độ network không có; lượt build cuối dùng cache bình thường và đạt.

**Tile trong toàn bộ kiểm Chrome là ảnh kiểm có nhãn TEST TILE**, bị chặn bằng Playwright trước khi gửi ra ngoài. Chỉ chứng minh cơ chế tải, quyền vùng, thứ tự lớp và tương tác. Bốn ca bố cục dùng phản hồi Guest cố định/audio NOT_READY; ca Guest API thật dùng bộ điểm còn ẩn và kiểm không lộ nháp. Không ca nào của mốc này chứng minh nghe MP3 thật hoặc TTS provider. Kết quả backend full/MP3 ở các mốc trước giữ nguyên phạm vi/thời điểm.

Rà bổ sung bằng `frontend/tests/e2e/admin-protection.spec.mjs`; [log bảo vệ Admin](browser-admin-protection.txt). Ba lượt Chrome có phạm vi riêng (11 + 3 + 3), không gọi là một lượt full 17 ca. Các ca thêm sau build chạy trực tiếp trên ảnh đã đóng gói; không đổi mã runtime sau build.

## Runtime và chính sách nền

App `hueqr-osm-preview-21a39bd3`, ảnh `hueqr-local:20261111-osm-map`, ID `sha256:c36ae6fc13a894d19e2be4a1b468fcd65bcb0d4e53ffd838345b0698b4f854d1`; MySQL `hueqr-preview-mysql-91b4feca`. Network riêng `hueqr-preview-91b4feca`, datasource dùng alias `database` thay IP bridge. Giữ database/audio mount/khóa/epoch và tắt provider, local worker, cleanup, retention. App cũ giữ ở trạng thái dừng để đối chiếu, không prune/xóa volume. Chỉ bind 127.0.0.1:18082, không tunnel/public/CI/CD.

OSM dùng `https://tile.openstreetmap.org/{z}/{x}/{y}.png`; tải tile do viewport yêu cầu, không prefetch/archive, không tự chuyển provider. Có attribution nhìn thấy và Referrer theo origin; không gửi credential/nội dung qua URL. Cache theo trình duyệt/HTTP, không thêm cache-bypass. Nguồn chính sách: [OSMF Tile Usage Policy](https://operations.osmfoundation.org/policies/tiles/); API lớp bản đồ theo Leaflet 1.9.4 đang cài. Bản `offline` riêng lấy snapshot hình học đã lưu, không tải trước tile OSM để làm offline.

## Điểm tiếp tục

1. Xác nhận nền raster thật có đường/công trình/nhãn trên kết nối được phép; xử lý kết nối hoặc chốt nguồn OSM phù hợp nếu endpoint hiện tại không khả dụng. Không suy TEST TILE/HTTP ứng dụng 200 thành đạt nền thực.
2. Rà/duyệt riêng năm vùng và nội dung/audio; lưu/áp dụng đúng bản qua Admin rồi mới bật điểm. Chưa có phê duyệt thì không tạo hồ sơ giả để trình diễn.
3. Kiểm nghe thật sau đổi UI, toàn bộ trạng thái Admin/Q2/Q3, thiết bị, HTTPS/QR, 20 cặp, TTS xuyên suốt và vận hành theo [bảng issue](../../01-tong-quan/issue-follow-up-2026-11-11.md).

Không dùng computer use, không cài công cụ hệ thống, đổi dependency, gọi TTS, dùng database đang vận hành hoặc Git ghi. Hồ sơ/ảnh nguồn đầu ra được ghi Hash trong [source-hashes.json](source-hashes.json). Dừng để rà mục lớn bản đồ/runtime trước khi chuyển mốc.
