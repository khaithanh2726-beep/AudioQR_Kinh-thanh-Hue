# Môi trường chạy thử Windows

Các công cụ được cài ở cấp tài khoản trong `%LOCALAPPDATA%\Programs\AudioQR-Tools`. PATH của tài khoản chứa thư mục Node và thư mục bin của FFmpeg. Mở terminal mới sau khi cài để nhận PATH.

| Công cụ | Phiên bản | Kết quả kiểm |
| --- | --- | --- |
| Node.js | 24.22.2 LTS | `node --version` đạt |
| npm | 11.19.1, đi kèm Node | `npm.cmd --version` đạt |
| FFmpeg | 9.0.2 Essentials, Windows x64 | Mã hóa MP3 bằng libmp3lame đạt |
| ffprobe | 9.0.2 Essentials, Windows x64 | Đọc codec MP3, mono, 24 kHz, thời lượng 1 giây đạt |

Gói Node lấy từ nguồn phát hành Node.js. Gói FFmpeg lấy từ Gyan, nhà cung cấp bản Windows được trang FFmpeg dẫn tới. [Node.js 24.22.2](https://nodejs.org/en/download/archive/v24.22.2) · [FFmpeg](https://ffmpeg.org/download.html) · [Gyan release builds](https://www.gyan.dev/ffmpeg/builds/)

Đã đối chiếu SHA-256 của hai gói trước khi giải nén.

| Gói | SHA-256 |
| --- | --- |
| node-v24.22.2-win-x64.zip | ca1bff5d8ead13c9f2ea404a176db46bd430704d5b5113734049fa365e7799e2 |
| ffmpeg-9.0.2-essentials_build.7z | 4705843ccaaf54257c16ad90f3e952ece33c17df964ecf7bfdbb0f49c7171077 |

Lượt kiểm công cụ đã tạo WAV PCM 16-bit mono 24 kHz từ tín hiệu sine một giây, mã hóa thành MP3, đọc bằng ffprobe và giải mã toàn tệp thành công. Kết quả phục vụ xác nhận công cụ local. Chuyển đổi trong Linux container và nghe duyệt nội dung còn chờ kiểm riêng.

`npm.cmd test` trong frontend đã chạy bằng Node thật, đạt 54 ca, không có ca lỗi hoặc bỏ qua. Resolve dependency, build Vue và kiểm trình duyệt là các bước tiếp theo của [luồng chạy thử](chay-thu/README.md).
