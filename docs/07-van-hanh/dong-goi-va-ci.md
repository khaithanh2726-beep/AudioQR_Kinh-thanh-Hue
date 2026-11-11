# Đóng gói và kiểm tra tự động

Ứng dụng dùng Vue và Spring Boot chung origin. Docker build đóng gói hai trang khách/Admin vào static resources của backend. MySQL và kho audio nằm trên hai volume riêng.

Mốc kiểm ngày 11/11: đã build ảnh Linux, kiểm FFmpeg/ffprobe với WAV thật và đường dẫn Unicode, chạy ứng dụng chung origin trên database/kho audio phục hồi riêng. [Bằng chứng đóng gói/restore](../06-kiem-thu/packaging-20261111/README.md) ghi phạm vi và danh tính ảnh. Lượt ứng dụng dùng `docker run`; Compose chỉ mới kiểm cấu hình, chưa nghiệm thu update/rollback qua Compose. Đây là môi trường kiểm loopback, chưa triển khai public.

## Cấu hình

Sao chép mẫu `deploy/.env.example` ra tệp cấu hình riêng ngoài repository. Đặt mật khẩu database, khóa ký khách ít nhất 32 byte dưới dạng Base64 và UUID epoch. Giữ các giá trị ổn định trong cùng môi trường. Cấp khóa TTS cho môi trường được phép gọi provider.

`HUEQR_PUBLIC_START_URL` là URL HTTPS thật của trang bắt đầu và được nhận lúc build frontend. Trang Admin hiển thị URL chỉ đọc. Khi URL thay đổi, build lại ảnh ứng dụng và kiểm quét QR.

`HUEQR_TTS_ENABLED` bật lời gọi provider. `HUEQR_TTS_LOCAL_ENABLED` trong Compose bật phần chuyển WAV đã có thành MP3. Cleaner và retention bật bằng các biến cấu hình riêng sau khi kiểm điều kiện vận hành.

Từ thư mục repository, khi được giao chạy môi trường đóng gói, dùng lệnh sau với đường dẫn tệp cấu hình đã chuẩn bị.

```powershell
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml config --quiet
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml build
docker compose --env-file '<tệp cấu hình riêng>' -f deploy/compose.yaml up -d
```

MySQL chỉ được nối từ mạng Compose. Backend mặc định mở cổng 8080 trên loopback. HTTPS được cấp bởi đường phục vụ đã cấu hình, giữ cookie Secure/HttpOnly/SameSite và cùng origin cho trang web với API.

Ảnh Linux xây FFmpeg/ffprobe 9.0.2 từ nguồn, hỗ trợ WAV PCM 16-bit mono 24 kHz và MP3 bằng libmp3lame. Cần kiểm build ảnh, chuyển đổi, giải mã và đường dẫn Unicode trên Linux trước khi nghiệm thu môi trường đóng gói.

Lượt ngày 11/11 đã đạt các kiểm media trên Linux nêu trên; giữ yêu cầu kiểm lại khi thay công cụ, ảnh hoặc profile audio. Công cụ diễn tập và hướng dẫn chạy lại tại [fixture operations](../../backend/src/test/fixtures/operations/README.md).

## CI

Workflow `.github/workflows/ci.yml` chạy kiểm frontend và backend cho pull request, các nhánh task và khi kích hoạt thủ công. Frontend dùng lockfile, chạy test và build. Backend dùng Java 21, Maven Wrapper và Testcontainers MySQL, xuất báo cáo Surefire.

Job Node kiểm thêm manifest backup bằng năm ca độc lập; không chạy Docker/provider hoặc database thật của môi trường phục vụ từ bộ kiểm này. Chưa push hoặc kích hoạt workflow trên GitHub.

Chạy workflow trên GitHub cần các file nguồn tương ứng đã được đưa lên nhánh. Lượt chạy cục bộ và lượt chạy GitHub được ghi nhận riêng trong hồ sơ kiểm thử.

## Bàn giao CD

Đường CD đã chọn là GitHub Actions qua Tailscale riêng đến OpenSSH trên Windows, chạy PowerShell với context Docker Desktop của tài khoản triển khai. Cần cấu hình danh tính Tailscale, máy đích, SSH host key, khóa SSH, tài khoản và thư mục triển khai trước khi tạo bước phát hành thực thi.

Các phép kiểm bắt buộc gồm SSH vào đúng máy, chạy được Docker/Compose dưới đúng tài khoản, giữ volume qua cập nhật ảnh, đổi phiên bản có thể quay lại và URL HTTPS truy cập được từ thiết bị ngoài mạng. Kết quả các phép kiểm được bổ sung khi môi trường đích được cấp.
