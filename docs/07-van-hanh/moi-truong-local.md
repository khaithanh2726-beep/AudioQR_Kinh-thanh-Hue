# Môi trường phát triển và chạy thử

Môi trường chuẩn là Windows/PowerShell với Docker Desktop chạy Linux containers. Phiên bản phần mềm của dự án tại [bảng công nghệ](../01-tong-quan/technical-inputs.md); không tự nâng dependency khi chuẩn bị máy.

| Công cụ | Vai trò |
|---|---|
| Node.js 24.22.2 / npm 11.19.1 | Kiểm logic và build Vue; trên PowerShell dùng npm.cmd khi npm.ps1 bị policy chặn. |
| Java 21 / Maven Wrapper | Build và kiểm Spring Boot; wrapper nằm trong backend. |
| Docker Desktop / Compose | MySQL/Testcontainers và môi trường ứng dụng tách biệt. |
| FFmpeg/ffprobe 9.0.2 | Kiểm/mã hóa audio; ảnh Linux có công cụ riêng, không dùng kết quả Windows để thay kiểm container. |
| Ngrok | Cấp HTTPS diễn tập tới cổng loopback của ứng dụng đã rà. Không là CD hoặc hosting độc lập. |

## Kiểm điều kiện máy

Mở terminal mới sau khi thay PATH. Từ repository, các lệnh dưới đây chỉ kiểm phiên bản, chưa khởi động dịch vụ:

```powershell
node --version
npm.cmd --version
java -version
docker version
docker compose version
ffmpeg -version
ffprobe -version
ngrok version
```

Docker cần engine Linux hoạt động để chạy Testcontainers/Compose. Kiểm Java/Node theo cấu hình dự án; thiếu công cụ hoặc không đủ quyền phải ghi phép kiểm bị chặn. Không lấy một lần kiểm trên máy cũ làm bằng chứng máy mới đã sẵn sàng.

## Kiểm phần mềm và môi trường

Lệnh frontend tại [frontend README](../../frontend/README.md), đóng gói/CI tại [hướng dẫn đóng gói](dong-goi-va-ci.md), database và kho audio riêng tại [chạy thử](chay-thu/README.md). Khóa runtime, tài khoản và cấu hình provider phải ở ngoài repository. Không nối database đang phục vụ để chạy fixture.

Kết quả đã có: 530/530 backend, 71/71 logic frontend, build Vue/JAR/ảnh Linux và kiểm media thực. Đây là bằng chứng các lượt được ghi trong [báo cáo kiểm thử](../06-kiem-thu/README.md), không là kết quả vừa chạy lại ở phiên biên tập tài liệu.
