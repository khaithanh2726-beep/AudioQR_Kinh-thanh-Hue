# Giao diện AudioQR Kinh thành Huế

Vue/Vite phục vụ trang khách `/` và quản trị `/admin.html`. T04/T06 do Nghĩa phụ trách theo [G02](../docs/02-nghiep-vu/decisions.md#g02). Nghiệp vụ và tiêu chí nghiệm thu tại [PRD](../docs/02-nghiep-vu/PRD.md), [UI](../docs/04-giao-dien/ui-design.md); giao diện dùng hợp đồng API hiện hành.

## Chức năng

Khách bắt đầu phiên trong tab, chọn vùng/ngôn ngữ, đọc văn bản, tải trọn và kiểm Hash MP3 trước khi phát. ACK không chờ audio. Owner document dùng Web Locks/BroadcastChannel; hết hạn, kết thúc và khôi phục giữ quy tắc phiên đã thống nhất. Không lưu audio offline.

Admin đăng nhập và quản lý địa điểm/vùng, bản văn và cặp nội dung, tham chiếu duyệt, hiển thị/xóa. Lệnh dùng CSRF, phiên bản, command-window và receipt; kết quả chưa rõ không tự gửi lại bằng khóa mới. Bản chưa lưu được bảo vệ theo Q2/Q3. Có trang TTS tạo/đọc trạng thái/Check/Retry, nghe thử, mức đầy đủ 20 cặp, phiên/thống kê và QR chung.

QR chỉ được tạo từ URL HTTPS cấu hình lúc build qua `VITE_PUBLIC_START_URL`; chưa có URL thì hiển thị chưa sẵn sàng. Bản đồ dùng nền raster OpenStreetMap tiêu chuẩn qua Leaflet, tải trực tiếp qua HTTPS; vùng chọn vẫn lấy từ API. Nền có ghi công, trạng thái tải/lỗi/thử lại riêng. Không tự duyệt vùng/bài hoặc bật điểm seed.

Ngrok free: bản tập dượt bật `VITE_NGROK_SKIP_BROWSER_WARNING=true` và URL HTTPS thật. API Khách thêm header công khai của ngrok chỉ khi origin hiện tại khớp URL cấu hình; giữ `credentials: omit`, header credential riêng và chặn redirect. Cờ mặc định tắt, không thêm header khi chạy localhost hoặc origin khác. HTTP 200 không có Content-Type JSON cũng bị từ chối thay vì coi trang cảnh báo là phản hồi nghiệp vụ.

## Bản xem trước bản đồ

Nhấn trong vùng hợp lệ đặt một ghim xanh đúng vị trí nhấn, áp dụng cho Khách và preview; không dùng GPS, không gửi tọa độ thêm vào API. Mỗi trang chỉ có một ghim; Hủy chọn chồng lấn giữ ghim cũ. Preview đã bỏ hàng nút trùng chú giải và các lời nhắc lặp; [hồ sơ](../docs/06-kiem-thu/map-pin-20261111/README.md).

Mẫu cần nền bản đồ địa lý đầy đủ như [ảnh tham khảo](../docs/04-giao-dien/ui-design.md#ui-reference-red), không chỉ màu sắc. Docker preview 18082 đã nhận UI đỏ sẫm và lớp OSM; [hồ sơ mới](../docs/06-kiem-thu/osm-map-20261111/README.md) ghi kết quả và giới hạn kết nối tile. [Hồ sơ màu/bố cục trước](../docs/06-kiem-thu/ui-reference-20261111/README.md) giữ đúng mốc, không là trạng thái runtime hiện tại.

`map-preview.html` là trang riêng để rà năm vùng đề xuất, không phải phiên khách. Từ mốc chuẩn bị tập dượt 12/11, chỉ đóng gói trang này khi build với `VITE_MAP_PREVIEW=true`; bản thường chỉ có Khách/Admin. Cờ này độc lập nền bản đồ: `VITE_MAP_BASEMAP=osm` mặc định dùng OSM ở cả Khách/Admin; `offline` chỉ dùng bản trích giản lược khi kiểm riêng. URL mặc định `https://tile.openstreetmap.org/{z}/{x}/{y}.png`; có thể cấu hình `VITE_OSM_TILE_URL` cho nguồn OSM HTTPS phù hợp chính sách. Không kèm khóa/credential/query trong URL. Không prefetch/kho tile offline hoặc tự chuyển nhà cung cấp khi lỗi.

Tạo lại tài sản offline từ nguồn lưu bằng `node scripts/build-map-assets.mjs` trong thư mục frontend; có ghi công/mốc nguồn và chú giải số/tên. Bản offline không đại diện độ chi tiết/nhãn của raster OSM trong ảnh tham khảo.

Trang Khách thật vẫn lấy vùng qua API, không tự áp dụng GeoJSON đề xuất. Bộ xem trước riêng có năm điểm/20 slot/năm bài vi nháp còn ẩn, không giả hồ sơ/cặp để vượt gate. [Hồ sơ mốc bản đồ](../docs/06-kiem-thu/map-preview-20261111/README.md) ghi 3/3 ca Chrome, lỗi Mức đầy đủ đã sửa và giới hạn; [cách mở](../docs/07-van-hanh/xem-truoc-ban-do.md). Bản E2E cũ ở 18081 đang dừng, preview riêng ở 18082.

## Chạy kiểm

`tests/` là mã kiểm thử cần giữ. Đầu ra Playwright tạm mặc định đặt ngoài repository dưới `%TEMP%/hueqr-playwright/audioqr-kinh-thanh-hue`; có thể đổi bằng `HUEQR_E2E_OUTPUT`. `dist/` là đầu ra build tái tạo được, `node_modules/` là phụ thuộc local; cả hai không commit. Không đưa các thư mục `test-results-*` vào nguồn hoặc Docker context.

Node 24.22.2, npm đi kèm; giữ phiên bản chính xác trong package.json và package-lock.json.

```powershell
npm ci
npm test
npm run build
npm run dev
```

Vite mặc định proxy `/api` tới `http://127.0.0.1:8080`; có thể đặt `HUEQR_BACKEND_URL` cho backend chạy thử riêng. Không dùng database đang vận hành. Điện thoại cần môi trường HTTPS cùng origin được cấu hình riêng.

## Kiểm chứng ngày 11/11/2026

Mốc sửa chữ/bố cục Khách: thay phông serif bị trộn fallback bằng bộ phông hỗ trợ tiếng Việt, xếp dọc bản đồ/bài/audio, đưa Kết thúc lên đầu và giữ Hủy ngoài danh sách cuộn. Chrome trên ảnh Docker mới đạt 7/7 ca (54,5 giây), có kiểm bốn viewport, vi/en/fr/ru, lỗi audio và giữ mốc khi Hủy; đã quan sát lại trong VS Code. [Hồ sơ UI](../docs/06-kiem-thu/guest-ui-20261111/README.md) ghi ảnh, Hash và giới hạn. Các lượt dưới đây giữ đúng mốc trước sửa.

**Bản đồ chưa đạt demo:** môi trường đang mở vẫn có nền lưới và các tam giác E2E trùng nhau. Không coi các lượt API/audio đạt là nghiệm thu bản đồ hoặc toàn UI. Mốc tiếp theo cần dữ liệu demo tách E2E và bản xem trước nền/vùng từ tài sản thiết kế để duyệt.

- `npm test`: 60/60 đạt; `npm run build`: đạt.
- Playwright với Chrome cài sẵn: hai kịch bản API/MySQL tách biệt đạt, gồm Admin áp dụng cặp, phát MP3 thật, thống kê, lỗi mạng audio/thử lại/reload và viewport 390×844. Tạo WAV dùng Gemini thật; đưa WAV vào hàng đợi bằng fixture kiểm thử rồi worker chuyển MP3 thật. Chưa chứng minh thao tác tạo TTS trên UI đã gọi xuyên suốt provider worker.
- Lượt browser cuối đạt 2/2 (34,6 giây, exit 0) với thiết lập autoplay mặc định, có kiểm modal native và lưu bản văn mới bỏ job/audio cũ trong khi khách vẫn nhận cặp đang áp dụng.

Hồ sơ tại [kiểm chạy local](../docs/06-kiem-thu/local-20261111/README.md). `verification-admin.json` và `integration-admin.json` là bằng chứng mốc tích hợp trước, không đại diện lượt npm/browser mới.

Còn kiểm Android/Chrome và iPhone/Safari thật, BFCache/nhân bản tab/background theo ma trận, QR từ HTTPS thật, nội dung/audio được duyệt và bản đồ thực. Chưa đủ căn cứ hoàn tất T04/T06 hoặc tích checklist. API liệt kê lịch sử region-revisions theo địa điểm vẫn là đề xuất hỗ trợ tìm lại bản vùng; hiện chỉ đọc revision đã biết mã.
