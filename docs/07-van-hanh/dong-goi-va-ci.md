# Đóng gói và kiểm tra tự động

Ứng dụng dùng Vue và Spring Boot chung origin. Docker build đóng gói hai trang khách/Admin vào static resources của backend. MySQL và kho audio nằm trên hai volume riêng.

Đã kiểm build ảnh Linux, FFmpeg/ffprobe, đường dẫn Unicode, ứng dụng chung origin và Compose restart/update/rollback trong môi trường tách biệt. HTTPS/QR Ngọ Môn vi đã có lượt chạy thử; không đồng nghĩa nâng bản V14, đủ 20 cặp hoặc nghiệm thu toàn bộ vận hành. Phạm vi và bằng chứng tại [báo cáo kiểm thử](../06-kiem-thu/README.md).

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

Job Node kiểm thêm manifest backup bằng năm ca độc lập. [CI PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43406790912) đạt 530/530 backend, 71/71 logic frontend, 5/5 manifest và build. Bộ kiểm manifest V14 mở rộng đạt 8/8 cục bộ là lượt khác. Artifact gồm Surefire, JAR và frontend dist; workflow chưa có CD hoặc build/publish ảnh release. Không gọi provider hay dùng database phục vụ trong CI.

Chạy workflow trên GitHub cần các file nguồn tương ứng đã được đưa lên nhánh. Lượt chạy cục bộ và lượt chạy GitHub được ghi nhận riêng trong hồ sơ kiểm thử.

## HTTPS/ngrok khi diễn tập

Ngrok đã được liên kết trên máy diễn tập. Token giữ trong cấu hình riêng của tài khoản, không ghi vào hồ sơ. Khi đổi máy, cấu hình lại công cụ theo tài liệu nhà cung cấp trước khi mở đường phục vụ.

Chỉ mở agent tới đúng cổng ứng dụng đã rà theo [hướng dẫn tập dượt](tap-duot.md). URL HTTPS phải khớp HUEQR_PUBLIC_START_URL và QR; giữ agent/laptop hoạt động, kiểm lại từ điện thoại sau mỗi lần khởi động. Ngrok không phục vụ database hoặc môi trường restore chưa nghiệm thu.



## Bàn giao CD

Đường CD đã chọn là GitHub Actions qua Tailscale riêng đến OpenSSH trên Windows, chạy PowerShell với context Docker Desktop của tài khoản triển khai. Cần cấu hình danh tính Tailscale, máy đích, SSH host key, khóa SSH, tài khoản và thư mục triển khai trước khi tạo bước phát hành thực thi.

Các phép kiểm bắt buộc gồm SSH vào đúng máy, chạy được Docker/Compose dưới đúng tài khoản, giữ volume qua cập nhật ảnh, đổi phiên bản có thể quay lại và URL HTTPS truy cập được từ thiết bị ngoài mạng. Kết quả các phép kiểm được bổ sung khi môi trường đích được cấp.
