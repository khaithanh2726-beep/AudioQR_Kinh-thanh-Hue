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

### Kết nối MySQL bản chính từ VS Code

Database chính là `hueqr`, user `hueqr`; kết nối dev cũ `hue_qr_dev`/`hue_qr_app` không còn là bản phục vụ. MySQL vẫn nằm trong mạng Compose, không sửa volume hoặc publish cổng trực tiếp trên container database. Cầu TCP riêng chỉ publish loopback Windows, dùng image Java hiện có và không giữ mật khẩu:

```powershell
node deploy/database-access.mjs start
```

Trong Database Client: MySQL, host `127.0.0.1`, port `3307`, username `hueqr`, database `hueqr`, mật khẩu database từ runtime riêng (khác mật khẩu Admin web). Không chép mật khẩu vào `.vscode/settings.json` hoặc repository. Bước tạo cầu lần đầu cần javac 21 đã cài; không tải image hoặc cài dependency. Cầu không tạo database mới, không đổi xác thực MySQL và không biến kết nối thành tài khoản chỉ đọc; dùng để tra cứu, không tùy ý sửa dữ liệu đang phục vụ.

Đóng kết nối Database Client trước backup/dừng; kết nối SQL còn mở có thể chặn freeze theo thiết kế. Khi không dùng:

```powershell
node deploy/database-access.mjs stop
```

Sau khởi động lại Docker, chạy `start` khi cần truy cập VS Code; web vẫn dùng kết nối nội bộ và không phụ thuộc cầu này. Cổng này chỉ cho laptop, không đưa qua ngrok. Không chia sẻ tệp runtime/config hoặc lưu password vào tài liệu.

Lệnh frontend tại [frontend README](../../frontend/README.md), đóng gói/CI tại [hướng dẫn đóng gói](dong-goi-va-ci.md), database và kho audio riêng tại [kịch bản thử riêng](../99-luu-tru/chay-thu-mot-diem.md). Khóa runtime, tài khoản và cấu hình provider phải ở ngoài repository. Không nối database đang phục vụ để chạy fixture.

Kết quả đã có: 530/530 backend, 71/71 logic frontend, build Vue/JAR/ảnh Linux và kiểm media thực. Đây là bằng chứng các lượt được ghi trong [báo cáo kiểm thử](../06-kiem-thu/README.md), không là kết quả vừa chạy lại ở phiên biên tập tài liệu.
