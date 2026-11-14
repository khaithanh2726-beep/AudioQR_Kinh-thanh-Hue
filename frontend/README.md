# Frontend AudioQR Kinh thành Huế

Vue/Vite cung cấp trang Khách `/` và Admin `/admin.html`. T04/T06 thuộc Nghĩa theo G02; [PRD](../docs/02-nghiep-vu/PRD.md) và [UI](../docs/04-giao-dien/ui-design.md) quy định hành vi.

## Chức năng và cấu hình

Khách bắt đầu phiên trong tab, chọn vùng/ngôn ngữ, đọc bài và tải trọn MP3 kiểm Hash trước phát. Audio giữ bằng Blob trong tab; hết hạn/kết thúc/khôi phục tuân theo quyền phiên. Admin quản lý địa điểm/vùng, nháp/hồ sơ/cặp, TTS/Check/Retry/nghe thử, thống kê và QR. CSRF, ETag, command-window và receipt bảo vệ thao tác; kết quả chưa rõ không tự gửi lại bằng khóa mới.

Leaflet hiển thị raster OSM; vùng lấy từ API. Ghim đặt đúng điểm chạm hợp lệ, không dùng GPS. `map-preview.html` chỉ đóng gói khi `VITE_MAP_PREVIEW=true`; build thường dành cho Khách/Admin. `VITE_MAP_BASEMAP=osm` là mặc định; bản offline giản lược chỉ dùng kiểm riêng. Nguồn và ghi công bản đồ giữ trong tài sản thiết kế.

QR chỉ có khi `VITE_PUBLIC_START_URL` là URL HTTPS đã cấu hình. Ngrok có thể bật `VITE_NGROK_SKIP_BROWSER_WARNING=true` cho API cùng origin phù hợp; Khách vẫn `credentials: omit`. `HUEQR_BACKEND_URL` cấu hình proxy dev, mặc định backend 127.0.0.1:8080.

## Chạy và kiểm

Dùng Node 24.22.2 và lockfile hiện hành; từ thư mục frontend:

```powershell
npm.cmd ci --no-audit --no-fund
npm.cmd test
npm.cmd run build
npm.cmd run dev
```

Build xuất `dist`; `node_modules`/`dist`/kết quả Playwright không commit. Mã unit và E2E giữ trong `tests`. Output E2E đặt ngoài repository bằng `HUEQR_E2E_OUTPUT`; [hướng dẫn E2E](tests/e2e/README.md) quy định runtime/schema riêng và điều kiện chạy. Không có runtime thì các ca phụ thuộc bị skip, không tính là pass.

Đã có 71/71 logic và build đạt; các lượt Chrome/API/MP3 và giới hạn thiết bị tại [báo cáo kiểm thử](../docs/06-kiem-thu/README.md). Mốc nguồn trong Git khác với dữ liệu phục vụ đã duyệt; Bản phục vụ đã áp dụng đủ năm điểm/20 cặp vi/en/fr/ru, API Hash kiểm 20/20; kiểm thiết bị của bộ cập nhật được thực hiện ở lượt cuối.
