# CI, Compose và TTS thật — 12/11/2026

## Kết quả và phạm vi

Đã push chín nhánh task bằng đúng tài khoản G02, giữ T08 ở dạng nháp chưa commit và không đổi main. CI của nguồn `56814d3f3f7468ec891e25c15647516e56d2aed2` đã đạt cả frontend/backend trên [GitHub Actions](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43135057755). Bảng SHA và commit tại [commit-plan 4.21](../../01-tong-quan/commit-plan-2026-11-05.md#push-ci-20261112).

[result.json](result.json) tổng hợp kết quả API GitHub/log CI, đối chiếu push, diễn tập Compose và lượt provider. Credential, khóa Gemini, cấu hình runtime riêng, dump và audio không thuộc hồ sơ này. [Ảnh nghe thử](provider-preview.png) ghi màn Admin sau lượt TTS thật.

## Sửa CI

- `e37e472`: thêm LF cuối `maven-wrapper.properties` để vòng `read` của Wrapper trên Linux đọc được `distributionUrl`; giữ Maven 3.9.12. Gộp các lượt CI cùng workflow/SHA bằng concurrency; các lượt trùng bị hủy không phải lượt kiểm đạt.
- `56814d3`: truyền hostname thực của runner vào tiến trình Maven/Java. Lượt trước đã chạy 520 ca nhưng lỗi do `RUNTIME_NODE_UNKNOWN` tại cơ chế bảo vệ owner. Giữ cơ chế từ chối khi không xác định được node, không bỏ ca kiểm hoặc nới quyền xóa file.

Lượt CI đạt 520/520 backend, không lỗi hoặc bỏ qua; 65/65 logic frontend, 5/5 manifest backup và build Vue đạt. Đã xuất báo cáo Surefire, JAR và frontend dist. Đây là một lượt full Linux thực, tách với lượt full Windows ngày 11/11. Kết quả CI không bao gồm browser, provider hoặc database phục vụ thật; các lượt riêng bên dưới bổ sung đúng phạm vi đó.

## Diễn tập Compose

Chạy [công cụ Compose](../../../backend/src/test/fixtures/operations/compose-rehearsal.mjs) với hai ảnh local có ID khác nhau, cùng schema V1–V12. Project thử mới `hueqr-compose-check-*`, MySQL không publish cổng; ứng dụng chỉ bind loopback. Provider/converter/cleaner/retention tắt trong toàn lượt Compose.

Bốn chặng đạt: seed hai lần không nhân đôi 5 điểm/20 slot; restart hai dịch vụ; cập nhật ảnh ứng dụng; quay lại ảnh trước. Mọi chặng giữ volume MySQL/audio, Hash migration, khóa/epoch và tệp checkpoint. Các điểm vẫn ẩn, không có cặp áp dụng. Hai trang web và API CSRF phản hồi được sau từng chặng. Hai dịch vụ được dừng cuối lượt, giữ dữ liệu để rà.

Tệp checkpoint kiểm độ bền volume, không giả làm audio. Hai ảnh tương thích cùng schema nên lượt này chưa chứng minh rollback qua migration không tương thích, khôi phục sau cập nhật hỏng, HTTPS hoặc CD. Các ảnh là bản kiểm local đã có, chưa phải ảnh phát hành public từ CI.

Nguồn Compose mới cho phép chọn `HUEQR_IMAGE` rõ ràng và điều khiển `HUEQR_TTS_LOCAL_ENABLED`, mặc định tắt thay vì luôn bật. Tệp mẫu giữ mọi worker tắt; chỉ bật trong môi trường được giao kiểm.

## Một lượt TTS thật từ Admin

Sau khi kết thúc Compose, dùng chính database/volume thử riêng để chạy ảnh mới với provider và converter bật; cleaner/retention tiếp tục tắt. Tài khoản Admin thử có mật khẩu ngẫu nhiên lưu riêng; khóa chỉ được cấp qua môi trường container. Không dùng database chính hoặc bật billing.

[Ca browser provider](../../../frontend/tests/e2e/provider.spec.mjs) chạy đạt một lần trong Chrome cài sẵn, headless. Thao tác thực gồm đăng nhập, lưu bản Ngọ Môn vi, bấm Tạo, theo dõi job, chọn kết quả, tải nghe thử, đối chiếu SHA-256 và thử phát. Có đúng một POST tạo tác vụ và một job `SUCCEEDED`, không Retry. Các phase quan sát được: DISPATCHING → CONVERTING → SUCCEEDED; polling không nhất thiết quan sát mọi phase trung gian.

Model `gemini-3.8-flash-tts`, voice `Charon`. WAV PCM 16-bit mono 24 kHz dài 23,36 giây; MP3 mono 24 kHz dài 23,424 giây. Cả hai được probe và giải mã trọn bằng FFmpeg trong ảnh Linux; bản sao ngoài repository khớp kích thước/Hash registry. Hash MP3 tải qua Admin khớp backend. Kết thúc có 0 claim/0 task, 0 cặp/0 điểm hiển thị; hai container thử đã dừng. Browser dùng tile kiểm và không gọi OSM.

Audio mới là bản nháp kỹ thuật, chưa được con người nghe duyệt. Lượt này chưa kiểm áp dụng cặp/bật điểm/Khách trên chính kết quả provider, quota/429/503/timeout thực, Check/Retry thực, 19 bài còn lại hoặc thiết bị thật. Không dùng tham chiếu duyệt giả để mở rộng kết luận. Khóa `provider-attempt.lock` được giữ ngoài repository để chặn chạy lặp vô ý.

## Điều kiện còn mở trước phục vụ chính thức

Cần URL HTTPS/ngrok đúng origin để cấu hình build/QR; duyệt vùng, văn bản và đủ 20 audio/cặp; kiểm Android/Chrome và iPhone/Safari thật; hoàn thiện/kiểm OPS_GUARD, drain, scheduler, backup live, Tailscale/OpenSSH và CD. [Hướng dẫn nghiệm thu](../../07-van-hanh/nghiem-thu-van-hanh.md) giữ các bước bắt buộc. Chưa thay checklist 89 ô/20 dấu tích, issue hoặc GitHub Project. Nguồn kiểm mới được giữ để rà trước mốc commit tiếp theo.

Bản xem trước cũ đã được khởi động lại tại `http://127.0.0.1:18082/map-preview.html`, giữ database/kho audio preview, provider/converter/cleaner/retention tắt. Đây là hai container khác với project Compose/TTS thử đã dừng; chưa có endpoint public hoặc bộ dữ liệu phục vụ được duyệt.
