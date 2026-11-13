# Mở bản xem trước bản đồ và năm địa điểm

## Phạm vi

Bản hiện tại dùng nền raster OSM tiêu chuẩn qua HTTPS và năm đa giác đề xuất; chỉ phần vùng đề xuất được bật bằng `VITE_MAP_PREVIEW=true` khi build. `VITE_MAP_BASEMAP=osm` mặc định, `offline` là lựa chọn kiểm riêng dùng bản trích giản lược có mốc snapshot. Mục đích là xem xét vị trí/vùng; chưa chứng minh ranh giới được duyệt hoặc luồng Khách có 20 cặp đã áp dụng.

[Hồ sơ lớp OSM](../06-kiem-thu/osm-map-20261111/README.md) ghi app `hueqr-osm-preview-21a39bd3`, ảnh `hueqr-local:20261111-osm-map`, giữ database/audio cũ. Ứng dụng đã hoạt động lại; kết nối trực tiếp tới máy chủ tile trên máy kiểm bị reset, vì vậy chưa xác nhận nền thật hiển thị. Khi chưa tải đủ nền, có thông báo và nút tải lại; không gọi lại API mở bài vì thử lại nền. Không dùng ảnh tile kiểm tự động làm ảnh bản đồ thật.

Môi trường mới có database `hueqr_preview`, kho audio và khóa runtime riêng. Seed năm địa điểm chuẩn, 20 ngăn ngôn ngữ, lưu năm bài vi qua API ở trạng thái nháp. Không tạo review-ref/cặp áp dụng, không bật điểm hoặc gọi TTS. Trang Khách gốc chưa có vùng hiển thị là đúng gate hiện hành; không nới gate để cho bản chưa duyệt xuất hiện.

## Địa chỉ và tài khoản

Mốc ghim/thu gọn mới: app `hueqr-osm-preview-f620552b`, cùng cổng và database/audio; [hồ sơ hiện tại](../06-kiem-thu/map-pin-20261111/README.md). Nhấn trong vùng để đặt ghim ở đúng tọa độ nhấn. Chỉ giữ một chú giải dưới map, đã bỏ hàng nút trùng; ghim không phải GPS và không tự phục hồi ở tâm vùng sau reload. Thông tin app tại hồ sơ OSM trước là mốc lịch sử.

- Bản xem trước bản đồ: `http://127.0.0.1:18082/map-preview.html`, không cần đăng nhập. Chạm từng vùng hoặc tên để đối chiếu số/vị trí; kéo/phóng trong khung.
- Admin xem năm nháp: `http://127.0.0.1:18082/admin.html`, tài khoản `preview-admin`. Mật khẩu giữ trong `runtime.json` ngoài repository, dưới thư mục LocalAppData `AudioQR-Preview-*` được giới hạn ACL. Không gửi mật khẩu lên chat/tài liệu/Git.
- Bản E2E cũ tại cổng 18081 không còn chạy; dữ liệu vẫn giữ trong volume, dump và kho audio cũ. Không dùng nó làm dữ liệu demo chuẩn.

Cập nhật sau dọn Docker 11/11: app preview 18082 và database của nó được giữ; các container thử cũ của E2E/restore/ảnh cũ đã được xóa nhưng volume, dump, audio và cấu hình vẫn còn. Xem [danh sách dọn và cách đối chiếu để dựng lại](../06-kiem-thu/docker-cleanup-20261111/README.md) trước dùng lệnh `docker start` theo tài liệu mốc cũ.

Container hiện tại và danh tính ảnh nằm trong hồ sơ kiểm, không suy tên từ README cũ. Khởi động lại database trước ứng dụng. Mốc OSM dùng network riêng `hueqr-preview-91b4feca`, datasource trỏ tên `database` thay vì IP bridge dễ đổi. Quy trình hiện là preview riêng, chưa thay kiểm Compose/persistence/update/rollback hoặc kênh phục vụ public.

## Chuẩn bị lại

Đọc [fixture chuẩn bị preview](../../backend/src/test/fixtures/demo/README.md). Các lệnh tạo dữ liệu mới yêu cầu runtime nguồn thử và thư mục đầu ra mới có ACL; không trỏ vào database đang dùng. Mẫu frontend `.env.example` giữ `VITE_MAP_PREVIEW=false` mặc định. Không sửa `.env` trong repository để chứa secret.

Kiểm nguồn tài sản có thể tái tạo bằng:

```powershell
node frontend/scripts/build-map-assets.mjs
```

Build ảnh xem trước với cờ rõ ràng, hoặc build bình thường để không tự bật chế độ xem vùng đề xuất. Cờ chỉ đổi trạng thái trình bày, không ghi phê duyệt và không thay polygon API của phiên khách.

## Điều kiện chuyển sang demo Khách

Nhóm xem xét/duyệt nền và từng vùng cùng nguồn/giới hạn. Sau đó lưu/xem trước/áp dụng vùng qua Admin, hoàn thiện đúng văn bản/audio và hồ sơ theo D09, áp dụng cặp vi rồi mới bật từng điểm. Tiếp tục đủ 20 cặp cho bản nghiệm thu. HTTPS/QR và thiết bị thật cần lượt cấu hình/kiểm riêng; bản loopback không cung cấp QR dùng từ điện thoại.

Đối chiếu các phần còn thiếu với [theo dõi issue](../01-tong-quan/issue-follow-up-2026-11-11.md); không tự tích checklist, đóng issue hoặc đổi Project từ việc mở được bản xem trước.
