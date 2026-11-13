# Đóng gói và kiểm tra tự động

Ứng dụng dùng Vue và Spring Boot chung origin. Docker build đóng gói hai trang khách/Admin vào static resources của backend. MySQL và kho audio nằm trên hai volume riêng.

Mốc kiểm ngày 11/11: đã build ảnh Linux, kiểm FFmpeg/ffprobe với WAV thật và đường dẫn Unicode, chạy ứng dụng chung origin trên database/kho audio phục hồi riêng. [Bằng chứng đóng gói/restore](../06-kiem-thu/packaging-20261111/README.md) giữ phạm vi và danh tính ảnh. Mốc 12/11 đã kiểm [Compose restart/update/rollback và một lượt provider thật](../06-kiem-thu/release-readiness-20261112/README.md) trong môi trường mới chỉ mở loopback; chưa triển khai public hoặc nghiệm thu toàn bộ vận hành.

## Cấu hình

Sao chép mẫu `deploy/.env.example` ra tệp cấu hình riêng ngoài repository. Đặt mật khẩu database, khóa ký khách ít nhất 32 byte dưới dạng Base64 và UUID epoch. Giữ các giá trị ổn định trong cùng môi trường. Cấp khóa TTS cho môi trường được phép gọi provider.

`HUEQR_PUBLIC_START_URL` là URL HTTPS thật của trang bắt đầu và được nhận lúc build frontend. Trang Admin hiển thị URL chỉ đọc. Khi URL thay đổi, build lại ảnh ứng dụng và kiểm quét QR.

Riêng bản tập dượt ngrok free có `HUEQR_NGROK_SKIP_BROWSER_WARNING=true` lúc build để API Khách cùng origin nhận JSON/audio thay trang cảnh báo. Mặc định tắt; không đổi cơ chế credential hoặc gửi cookie cho Khách. Cách chạy và QR tại [hướng dẫn tập dượt](tap-duot.md).

`HUEQR_TTS_ENABLED` bật lời gọi provider. `HUEQR_TTS_LOCAL_ENABLED` trong Compose bật phần chuyển WAV đã có thành MP3. Cả hai mặc định tắt; cleaner và retention cũng chỉ bật bằng các biến cấu hình riêng sau khi kiểm điều kiện vận hành. Không dùng khóa hoặc database của dự án khác khi sao chép cấu hình.

`HUEQR_IMAGE` chọn tag ảnh, mặc định `hueqr:local`. Khi chuẩn bị phát hành, ghi tag/ID ảnh và SHA nguồn cụ thể vào hồ sơ. Chọn ảnh cũ bằng biến này chỉ phù hợp khi đã xác nhận tương thích schema; không hạ hoặc sửa migration đã áp dụng. [Công cụ Compose riêng](../../backend/src/test/fixtures/operations/README.md) kiểm độ bền volume qua bốn chặng, giữ cấu hình/credential ngoài repository.

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

Job Node kiểm thêm manifest backup bằng năm ca độc lập; không chạy Docker/provider hoặc database thật của môi trường phục vụ từ bộ kiểm này. [Lượt GitHub ngày 12/11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43135057755) trên `56814d3` đạt 520/520 backend, 65/65 logic frontend, 5/5 manifest và build. Artifact gồm Surefire, JAR và frontend dist; chưa có bước CD hoặc build/publish ảnh release trong workflow.

Chạy workflow trên GitHub cần các file nguồn tương ứng đã được đưa lên nhánh. Lượt chạy cục bộ và lượt chạy GitHub được ghi nhận riêng trong hồ sơ kiểm thử.

## Chuẩn bị ngrok trên Windows

Cài và liên kết tài khoản theo [hướng dẫn Windows chính thức](https://ngrok.com/download/windows?tab=download), đối chiếu ngày 12/11/2026:

```powershell
winget install ngrok -s msstore
ngrok version
ngrok config add-authtoken '<authtoken-rieng>'
```

Thao tác đăng nhập/cấu hình token do người vận hành thực hiện trên máy, giữ ngoài Git và hồ sơ chia sẻ. Sau khi cài, mở lại terminal nếu PATH chưa cập nhật. Chưa chạy tunnel tới preview hoặc database thử chỉ vì đã cài công cụ.

Khi đã xác nhận bản ứng dụng, dữ liệu và cổng phục vụ, mở một endpoint HTTP tới đúng cổng loopback theo [CLI quickstart](https://ngrok.com/docs/gateway/endpoints/agent-cli-quickstart), ví dụ `ngrok http 8080` nếu ứng dụng thực sự chạy tại 8080. Ghi URL HTTPS được cấp, đặt `HUEQR_PUBLIC_START_URL`, build lại và kiểm QR/cookie/CSRF qua URL đó. Ví dụ cổng không phải bằng chứng ứng dụng đang chạy hoặc URL đã được cấu hình. Giữ endpoint hoạt động trong lúc kiểm trên điện thoại; khi URL đổi phải cấu hình/build và phát QR lại.

## Bàn giao CD

Đường CD đã chọn là GitHub Actions qua Tailscale riêng đến OpenSSH trên Windows, chạy PowerShell với context Docker Desktop của tài khoản triển khai. Cần cấu hình danh tính Tailscale, máy đích, SSH host key, khóa SSH, tài khoản và thư mục triển khai trước khi tạo bước phát hành thực thi.

Các phép kiểm bắt buộc gồm SSH vào đúng máy, chạy được Docker/Compose dưới đúng tài khoản, giữ volume qua cập nhật ảnh, đổi phiên bản có thể quay lại và URL HTTPS truy cập được từ thiết bị ngoài mạng. Kết quả các phép kiểm được bổ sung khi môi trường đích được cấp.
