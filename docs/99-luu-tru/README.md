# Các mốc phát triển chính

Lịch sử được rút gọn để trình bày tiến trình; trạng thái hiện tại đọc tại [bàn giao](../01-tong-quan/handoff.md), không ghép các kết quả của nhiều mốc thành một lượt nghiệm thu.

| Mốc | Kết quả chính |
|---|---|
| 30/10–03/11 | Xác lập phạm vi hai vai trò, yêu cầu/Use Case, hướng UI, công nghệ, Gemini TTS/Charon và chính sách phiên/dữ liệu |
| 05–08/11 | Chuẩn hóa bộ tài liệu, mô hình/DFD; khởi tạo nền backend/dữ liệu và tích hợp qua PR #12/#13 |
| 09–10/11 | Phát triển API Khách/Admin, cặp nội dung, TTS, grant/ACK, lịch sử và vòng đời/file |
| 11/11 | Chạy local đầy đủ 520 ca; build Vue; sửa UI/map/ghim; kiểm MP3 thật, đóng gói Linux và restore riêng |
| 12/11 | CI Linux/Compose/provider thật; V13 giữ tọa độ vùng; HTTPS/QR và bộ Ngọ Môn vi được áp dụng cho tập dượt |
| 13/11 — kỹ thuật | V14 gate/backup/restore/scheduler riêng, lượt full 530 ca đạt; tạo 14 audio mới, dừng phần ru vì quota |
| 13/11 — lưu nguồn | PR #15 tích hợp vào main tại 601c09a, giữ lịch sử/tác giả; CI của PR đạt |
| 13/11 — rà tiến độ | Đối chiếu 89 ô, cập nhật GitHub/local 62 ô, giữ 27 ô chưa đủ; Android/iPhone có báo dùng Ngọ Môn cơ bản |
| 14/11 — vận hành | Snapshot/restore V13, diễn tập nâng V14, chuyển bản Ngọ Môn V14 và kiểm backup/restore; bật scheduler dọn, CD GitHub thực chạy đạt, giữ dữ liệu và URL QR |

## Trạng thái kế thừa

Bản tập dượt Ngọ Môn không phải nghiệm thu đủ 20 cặp. Bản phục vụ đã chuyển V14 ở mốc 14/11, giữ snapshot V13 trước nâng và kiểm phục hồi riêng. Lượt full 530/530 là một lượt đầy đủ; các lượt trước có lỗi/chạy lại hoặc ledger giữ riêng trong bằng chứng, không cộng thành một lượt full.

## Bảo quản nguyên bản

Toàn bộ nguyên bản tài liệu, lịch sử dài, mốc commit/bàn giao, log/JSON/XML/ảnh kiểm được giữ trong hồ sơ nội bộ riêng cạnh dự án. Mỗi tệp có Hash đối chiếu trước biên tập; không xóa nguồn gốc hoặc viết lại lịch sử Git. Bộ tài liệu chấm chỉ giữ bản mô tả hiện hành, bảng mốc này và báo cáo kết quả tổng hợp.
