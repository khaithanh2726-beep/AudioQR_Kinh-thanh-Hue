# Chuẩn bị môi trường xem trước năm địa điểm

## Làm mới preview đang có

`refresh-preview.mjs` cập nhật riêng bộ preview `hueqr_preview` đã chuẩn bị, không dùng cho database phục vụ. Yêu cầu ảnh `hueqr-local:20261111-osm-map` đã build thành công. Script đọc tên app từ manifest hiện tại (hoặc `HUEQR_PREVIOUS_PREVIEW_APP`), xác minh đúng schema/ảnh MySQL/bộ cờ worker tắt, giữ Env trong bộ nhớ và giữ nguyên mount audio/database. Không in/lưu credential vào báo cáo.

Đặt `HUEQR_MAP_PREVIEW_RUNTIME` trỏ thư mục runtime riêng ngoài repo rồi chạy `node backend/src/test/fixtures/demo/refresh-preview.mjs`. Network riêng có alias `database` để tránh IP bridge đổi sau restart. App chỉ publish 127.0.0.1:18082; script chờ MySQL và API CSRF, ghi `osm-refresh.json` không chứa secret, cập nhật manifest. Giữ container trước để đối chiếu; nếu thay app đang chạy rồi lỗi, thử khởi động lại app trước và ghi kết quả. Không coi nhánh xử lý lỗi này là diễn tập rollback đã đạt.

Lượt thực chạy đầu đã khôi phục đúng bộ preview và kiểm giữ 5 điểm/20 slot/5 văn bản. Lượt cập nhật ghim sau đó đã dùng tên app từ manifest và thay app thành công, giữ database/audio. Chưa diễn tập nhánh lỗi/khởi động lại app cũ. Chi tiết tại [hồ sơ OSM](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/osm-map-20261111/README.md) và [hồ sơ ghim](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/map-pin-20261111/README.md).

## Khởi tạo bộ mới

`prepare-preview.mjs` chỉ tạo môi trường xem trước riêng. Không chạy trên database đang vận hành, không nhận tên/schema từ tham số tự do và không tự duyệt vùng, văn bản hoặc audio.

## Điều kiện

Node 24.22.2, Java/backend đã đóng gói trong ảnh Docker, MySQL 8.4.13 đã có trên máy và Chrome cài sẵn. Build ảnh bằng `deploy/Dockerfile`, đặt `VITE_MAP_PREVIEW=true`, tag `hueqr-local:20261111-map-preview`. Không đổi dependency hoặc cài công cụ hệ thống từ script.

Runtime nguồn là thư mục thử restore `hueqr-restore-*` có `private-runtime.json` và `browser-runtime.json`. Chỉ đọc tài khoản thử `smoke-admin` để cấp tài khoản riêng `preview-admin`; không sao chép địa điểm, phiên/lượt, audio hoặc hồ sơ của E2E. Mật khẩu vẫn giữ ngoài repository. Đây là cách chuẩn bị preview từ môi trường thử đã có, chưa thay quy trình cấp Admin cho môi trường chính thức từ checkout mới.

Container MySQL nguồn cần đang chạy ở bước đọc tài khoản; ứng dụng E2E nguồn không cần chạy. Khi bộ xem trước đã chuẩn bị xong, có thể dừng cả hai container E2E để giảm RAM. Thư mục runtime xem trước mới giữ riêng dưới LocalAppData hoặc vị trí được phép, không phụ thuộc đường dẫn Temp cho dữ liệu đang rà.

Thư mục đầu ra phải mới, ngoài repository và nằm trong thư mục cha có ACL chỉ cho tài khoản thực hiện. Nó chứa mật khẩu/khóa trong `runtime.json`, không đưa vào Git hoặc hồ sơ chia sẻ. Cổng 18082 cần rảnh; ứng dụng chỉ bind loopback, database không publish cổng.

```powershell
node frontend/scripts/build-map-assets.mjs
docker build --build-arg VITE_MAP_PREVIEW=true -f deploy/Dockerfile -t hueqr-local:20261111-map-preview .
node backend/src/test/fixtures/demo/prepare-preview.mjs '<runtime-restore-thu>' '<thu-muc-dau-ra-moi>'
```

Script tạo container `hueqr-preview-mysql-*`, schema/user `hueqr_preview`, kho audio mới và ứng dụng `hueqr-map-preview-*`. Bật migration trên database mới; kiểm bảng điểm trống trước seed. Năm bài vi được lưu qua API Admin với CSRF/ETag/command-window và đối chiếu Hash, không chèn revision bằng SQL. Không tạo tham chiếu duyệt, vùng áp dụng, cặp áp dụng hoặc bật điểm. Provider/local worker/cleaner/retention đều tắt.

Kết quả kỳ vọng: 5 điểm, 20 slot, 5 bản văn nháp, 0 điểm hiển thị, 0 cặp áp dụng, 0 hồ sơ duyệt. Vì vậy trang Khách thật chưa hiển thị năm điểm; trang `/map-preview.html` hiển thị riêng năm vùng đề xuất để xem xét, không đi qua phiên/grant/ACK và không phải minh chứng chức năng Khách hoàn tất.

`manifest.json` không chứa secret; `runtime.json` có secret. Nếu lỗi, giữ container và dữ liệu để đối chiếu, đọc tên từ runtime và kiểm log đúng container; không reset hoặc dọn toàn Docker. Sau khi rà có thể dừng đúng hai container; giữ gói/dữ liệu theo quyền và hạn vận hành đã chọn. Không tự mở tunnel hoặc đưa môi trường này ra ngoài máy.
