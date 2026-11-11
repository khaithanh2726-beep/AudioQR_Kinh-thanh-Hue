# Kiểm đóng gói Linux và restore ngày 11/11/2026

Mốc tiếp nối [chạy local](../local-20261111/README.md). Chỉ dùng database, kho audio và container thử riêng; không triển khai public hoặc chạy provider TTS. Mã công cụ tại [fixture operations](../../../backend/src/test/fixtures/operations/README.md), ca Chrome tại `frontend/tests/e2e/packaged.spec.mjs`.

## Kết quả

| Phép kiểm | Kết quả |
|---|---|
| Docker build | Exit 0, tag `hueqr-local:20261111`, Linux amd64, 527.973.042 byte. Vue được build và chép vào static resources của Spring Boot. [Tổng kết](docker-build-summary.log), [danh tính ảnh](image.json). |
| Bộ công cụ trong ảnh | Node build 24.22.2, Maven 3.9.12/Java 21; runtime Temurin 21.0.13, FFmpeg/ffprobe 9.0.2. User runtime `hueqr`, uid/gid 10001. Java 21 là major đã chốt; công cụ Windows không bị đổi. |
| Media Linux | WAV Ngọ Môn thật → MP3 mono 24 kHz, 25,344 giây; giải mã toàn tệp exit 0, tên có dấu và khoảng trắng. Container media không có mạng. [Báo cáo](linux-media.json). |
| Snapshot offline | Dump và catalog từ MySQL thử đứng yên; 12 WAV/MP3 được tham chiếu, tổng 7.925.856 byte, kiểm Hash từng tệp trước/sau sao chép và trước import. [Manifest không chứa khóa](snapshot-manifest.json). |
| Restore riêng | Import vào container mới không publish cổng; số địa điểm/cặp/audio/phiên/lượt và Flyway khớp nguồn trước thêm fixture. [Báo cáo](restore-report.json). |
| Retention | Thêm bốn phiên và bốn lượt vào đích thử. Xóa ba phiên quá hạn và lượt phụ thuộc, giữ một phiên/lượt còn hạn cùng timestamp gốc; kiểm mốc đúng 30 ngày, fallback khi endedAt không hợp lệ. Thu hồi opening/grant/context cũ. |
| Bảo vệ gói restore | 5/5 ca Node đạt: gói đủ còn hạn, đúng mốc hết hạn bảy ngày, audio đổi bytes cùng kích thước, thiếu tệp/path traversal, sai kích thước dump/kéo dài hạn. [Log](manifest-test.log). Bytes giả trong unit chỉ dùng kiểm Hash, không dùng làm bằng chứng audio. |
| Browser sau restore | 1/1 Playwright đạt, exit 0, 10,6 giây; Chrome mặc định autoplay. Admin đăng nhập; khách bắt đầu, đọc đúng bài đã áp dụng, phát MP3, kết thúc phiên. Một phiên fixture còn hạn với credential khớp nhưng epoch cũ trả HTTP 401. [Log](packaged-browser.log), [assertion](packaged-result.json), [ảnh](packaged-guest.png). |

Ảnh được phục vụ tại `localhost:18081` qua Docker: frontend và API chung origin, không có Vite/proxy dev. Database restore không có host port. Provider và local worker/cleaner/retention đều tắt ở kiểm ứng dụng sau restore; không phát sinh TTS ngoài. Một fixture epoch được thêm sau bước restore để kiểm API, không thuộc số đếm trước đó.

Backend trong Docker build dùng `-DskipTests package`, **không phải** full regression Linux. Kết quả 520/520 trước đó là lượt Windows/Testcontainers tại hồ sơ local; không cộng các ca giữa hai môi trường. Frontend test/build được thực thi trong Docker. Tổng thời gian 27,506 giây trong report chỉ là diễn tập dữ liệu thử, không phải cam kết downtime/RPO/RTO hoặc tải 20 cặp thực.

## Giới hạn và bảo quản

OPS_GUARD/QUIESCING/FROZEN và drain live trong thiết kế chưa có mã thực thi. Diễn tập chỉ nhận nguồn thử đã dừng, job terminal và zero task/claim; kiểm này không fence writer mới. Chưa chứng minh backup khi đang phục vụ, phục hồi job QUEUED/DISPATCHING/UNCERTAIN/CONVERTING, claim rò, cleaner hoặc scheduler đang hoạt động. Không đổi phase hoặc xóa claim để vượt điều kiện.

Không có HTTPS/ngrok, quét QR ngoài mạng, thiết bị Android/iPhone thật, GitHub Actions hoặc CD trong mốc này. Compose mới kiểm cấu hình; lượt chạy ứng dụng dùng `docker run` tách biệt. Chưa kiểm start/update/rollback bằng Compose hoặc backup sản xuất. Bộ 20 cặp và bản đồ vẫn chờ duyệt.

Thư mục gói/dump/khóa nằm ngoài repository dưới thư mục cha có ACL riêng của tài khoản thực hiện. Hồ sơ này chỉ chép báo cáo, manifest Hash, ảnh và log đã chọn; không chép dump, `private-runtime.json`, `browser-runtime.json`, mật khẩu hoặc credential. Dừng đúng container thử sau kiểm, giữ dữ liệu/bằng chứng; chưa xóa bản sao hoặc tự bật lịch xóa. Hạn gói đúng bảy ngày từ createdAt, không đặt lại theo lần kiểm hoặc khởi động.
