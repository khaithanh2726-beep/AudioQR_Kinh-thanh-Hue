# Công nghệ và môi trường triển khai

Các phiên bản dưới đây được đọc từ cấu hình nguồn đã tích hợp. Tài liệu mô tả bộ đang dùng; nguồn khảo sát/so sánh dịch vụ theo từng ngày được giữ trong hồ sơ phát triển, không lặp trong bộ chấm.

## 1. Bộ công nghệ

| Thành phần | Phiên bản/vai trò |
|---|---|
| Vue | 3.5.44, giao diện Khách/Admin |
| Vite | 8.3.5, dev/build frontend |
| Leaflet | 1.9.4, bản đồ; editor vùng tùy biến trên Leaflet |
| qrcode | 1.5.4, tạo PNG QR từ URL cấu hình |
| Node.js | 24.22.2; package-lock giữ phụ thuộc chính xác |
| Playwright | 1.65.0, mã kiểm browser; dùng Chrome đã cài |
| Java | Major 21; runtime Linux trong ảnh đã kiểm Temurin 21.0.13 |
| Spring Boot | 4.1.2, Web MVC/Security/Data JPA/Validation/Flyway |
| Maven Wrapper | 3.9.12 |
| MySQL | 8.4.13; JPA/JDBC và Flyway V1–V14 |
| FFmpeg/ffprobe | 9.0.2 trong ảnh Linux, chuyển và kiểm MP3 |
| Gemini TTS | gemini-3.8-flash-tts, giọng Charon |
| Đóng gói | Docker/Compose; Vue static cùng backend, kho audio/DB riêng |
| CI | GitHub Actions; kiểm frontend/backend và xuất artifact |

Geoman Free thuộc hướng khảo sát trước đó; package hiện hành sử dụng Leaflet và editor vùng đã triển khai, không khai báo Geoman như một dependency đã cài. Không cài thêm hoặc đổi phiên bản trong đợt biên tập tài liệu.

## 2. Môi trường

Máy triển khai mục tiêu là Windows/Docker Desktop với Linux container. Frontend và API dùng cùng origin; HTTPS tập dượt qua ngrok. Chạy local/backend riêng, khóa và tài khoản dùng tệp môi trường ngoài Git. File âm thanh lưu ở kho riêng, không nhúng vào database hoặc repository.

Khách mục tiêu Android/Chrome và iPhone/Safari; Admin mục tiêu Windows/Chrome. Báo cáo dùng cơ bản trên Android/iPhone đã có nhưng chưa đủ model máy/OS/browser version hoặc mọi ca kiểm thiết bị. Chi tiết công cụ Windows đã cài tại [môi trường local](../07-van-hanh/moi-truong-local.md).

## 3. Tích hợp TTS và vận hành

WAV nguồn PCM 16-bit mono 24 kHz được chuyển MP3 cho khách; thành công kỹ thuật khác với duyệt/áp dụng nội dung. Quota thật đang chặn năm bài ru; không suy ngưỡng quota ngày/phút từ mã lỗi chung hoặc âm thầm đổi model. Khóa/quota và billing không được đưa vào tài liệu hoặc bật ngoài phạm vi cho phép.

CD-L01 chọn Tailscale/OpenSSH nhưng chưa nghiệm thu đường triển khai thực. Gate/backup/restore riêng đã có; thời hạn file/phiên và quy tắc khôi phục giữ theo [quy tắc nghiệp vụ](../02-nghiep-vu/decisions.md) và [thiết kế vận hành](../03-ky-thuat/operations-design.md).
