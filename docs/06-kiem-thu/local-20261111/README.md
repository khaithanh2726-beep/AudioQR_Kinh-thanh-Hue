# Kiểm chạy local ngày 11/11/2026

## Môi trường và phạm vi

Nguồn trong thư mục dự án chính, nhánh T01, HEAD `3bdef85`; không commit/push hoặc cập nhật checklist/Project. MySQL 8.4.13 trong container thử riêng, schema/tài khoản `hueqr_smoke`, cổng loopback. Backend tại 18080, Vite tại 15173, kho audio và cấu hình/khóa thử ngoài repository. Không sử dụng database đang vận hành. Phiên bản công cụ tại [môi trường local](../../07-van-hanh/moi-truong-local.md).

## Kết quả đã chạy

| Phép kiểm | Kết quả và bằng chứng |
|---|---|
| Backend full | 520/520, 0 failures/errors/skipped, 49 suite, BUILD SUCCESS lúc 09:39:14 +07; [tổng kết Maven](backend-summary.log), [từng ca Surefire](backend-tests.json). Đây là một lượt full mới, không phải ledger ghép nhiều lượt. |
| Node test frontend | 60/60, không lỗi/bỏ qua; [log](frontend-test.log). Bao gồm hồi quy review phiên bản 0, Web Locks Chrome và luồng TTS/receipt. |
| Vite build | Exit 0; [log](frontend-build.log). |
| Chrome + API + MySQL riêng | Lượt cuối hai kịch bản đạt trong 34,6 giây, exit 0, không ghi đè thiết lập autoplay. [Log](e2e-final.log), [trạng thái runner](e2e-final-run.json), [mã kết quả](e2e-result.json); mã kiểm tại `frontend/tests/e2e/visit.spec.mjs`. |
| Gemini thật | WAV tiếng Việt Ngọ Môn trả HTTP 200, được kiểm profile PCM mono 24 kHz. WAV thật được đưa vào RESULT_STAGED bằng fixture kiểm thử, worker V12 chuyển MP3 và đăng ký audio thật. |
| Bốn audio vi nháp | FFmpeg 9.0.2 encode MP3 và giải mã đầy đủ exit 0; [Hash/bản văn tương ứng](../../05-noi-dung/audio-vi-v01.json). Cung Diên Thọ trả HTTP 503, chưa có tệp thành công. |
| Docker Compose | `config --quiet` đạt với giá trị thử riêng. Chưa build/chạy ảnh Linux. |

Lượt Chrome kiểm: địa điểm ẩn không thể bật trước khi đủ cặp; vẽ/lưu/áp dụng vùng; lưu bản văn; chọn và phát nghe thử audio; ghi tham chiếu **kiểm thử**; áp dụng cặp và bật; khách bắt đầu phiên, chọn vùng, hiển thị đúng văn bản và phát MP3; truy vấn thống kê; QR báo chưa cấu hình URL. Dialog chi tiết thực sự ở top layer modal và đóng được. Lưu bản văn mới xóa job/audio đã chọn cũ, vô hiệu nút đối chiếu audio khi chưa chọn, còn khách reload vẫn nhận bài của cặp đang áp dụng. Kịch bản viewport 390×844 chặn riêng tải audio, xác nhận văn bản vẫn đọc được, bỏ chặn/thử lại, giữ trạng thái tạm dừng qua reload và kết thúc phiên.

Ảnh lượt cuối: [Khách](guest-visit.png), [Thống kê](admin-statistics.png), [Viewport di động](mobile-visit.png). Vùng/địa điểm và tên khách trong ảnh là dữ liệu thử; nhiều điểm thử chồng lấn do giữ dữ liệu các lượt trước. Lượt đầu 25,8 giây dùng cờ cho phép autoplay ([trạng thái cũ](e2e-first-run.json)); lượt cuối đã bỏ cờ và kiểm thêm modal/bản nháp. Không dùng kết quả Chrome desktop để suy chính sách iPhone/Safari.

Lượt backend đầu cũng có 520 ca không lỗi trong XML nhưng thu log bị PowerShell dừng khi nhận cảnh báo Mockito; không ghi nhận BUILD SUCCESS cho lượt đó. Lượt 09:39 đã thu được tổng kết đầy đủ. Cảnh báo màu NO_COLOR/FORCE_COLOR trong log browser không làm lỗi ca kiểm, runner trả exit 0. Khởi động lại container thử đổi cổng động từ 4898 sang 1765; cấu hình ngoài repository đã đối chiếu và cập nhật trước lượt Chrome cuối.

## Giới hạn

- Chưa chứng minh luồng UI → provider worker → Gemini xuyên suốt. Lời gọi Gemini và worker WAV → MP3 đã kiểm thật theo hai bước riêng; fixture không chứng minh tính đúng của provider dispatch.
- Phiên bản review 0 là phiên bản hợp lệ; các hồ sơ thử không phải duyệt nội dung/audio của bộ 20 bản.
- Chưa kiểm Android/Chrome hoặc iPhone/Safari thật, BFCache/nhân bản tab/background đầy đủ, HTTPS/ngrok và QR quét từ thiết bị ngoài mạng.
- Nền bản đồ/ranh giới và nội dung chưa được duyệt. Bản dịch en/fr/ru là nháp; bốn audio vi chưa được nghe duyệt trọn bài. Không tạo audio ngoại ngữ từ bản dịch chưa duyệt.
- Docker build Linux, GitHub CI/CD, backup/restore, claim rò và scheduler vận hành chưa được nghiệm thu. Không suy kết quả các mục này từ npm hoặc Testcontainers.

Các cấu hình runtime, mật khẩu và khóa không đưa vào hồ sơ này. [Hash nguồn](source-hashes.json) đối chiếu baseline tích hợp frontend và 229 tệp backend; backend không khác baseline. Kiểm đường dẫn Markdown ở 11 tài liệu liên quan có 315 đích tệp tồn tại, không kiểm anchor hoặc website ngoài. Log thô và cấu hình thử giữ ngoài repository. Các tiến trình/container thử được dừng sau mốc kiểm; dữ liệu và bằng chứng được giữ để rà.
