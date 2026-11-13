# Đầu vào duyệt bộ phục vụ — 13/11/2026

**Mốc mới cùng ngày:** đã có [bundle v02](demo-content-v02.json), [hồ sơ bốn vùng](duyet-vung-demo-v02.json) và [bằng chứng TTS/đối chiếu](../06-kiem-thu/content-review-20261113/README.md). Đã tạo 14/19 audio mới, 13 bản khớp qua AI, một bản fr cần nghe lại, năm bản ru bị chặn quota. Bốn vùng đã áp dụng trong bản thử riêng. Chưa áp dụng cặp mới hoặc cập nhật bản Ngọ Môn đang phục vụ; nghe trọn theo D09 vẫn còn thiếu. Bảng bên dưới ghi đầu vào trước lượt v02, không thay thế kết quả mới.

## Trạng thái đã đối chiếu

Có năm bài trong mỗi tệp [vi](thuyet-minh-vi-v01.md), [en](thuyet-minh-en-v01.md), [fr](thuyet-minh-fr-v01.md), [ru](thuyet-minh-ru-v01.md), tổng 20 bài. Có bài viết không đồng nghĩa đã đủ nguồn hoặc được duyệt. Bốn nháp tiếng Việt trong [manifest audio](audio-vi-v01.json) còn đủ WAV/MP3 ngoài repository và khớp Hash bản văn cùng Hash bytes. [Báo cáo kỹ thuật](../06-kiem-thu/operations-guard-20261113/content-readiness.json) lưu kết quả đọc tệp, không thay nghe duyệt.

| Điểm | Tiếng Việt | en/fr/ru | Vùng |
|---|---|---|---|
| Cổng Ngọ Môn | Một cặp đã duyệt/áp dụng cho tập dượt theo [hồ sơ riêng](duyet-ngo-mon-tap-duot-20261112.md) | Ba bản dịch nháp chờ duyệt, chưa có audio tương ứng | Đã chấp thuận cho tập dượt theo hồ sơ riêng |
| Điện Thái Hòa | Bản văn đã tiếp nhận; WAV/MP3 vi-02 còn nguyên Hash, cần nghe trọn và ghi hồ sơ | Ba nháp chờ duyệt, chưa có audio | Đề xuất chờ rà |
| Tử Cấm Thành | Bản văn đã tiếp nhận; WAV/MP3 vi-03 còn nguyên Hash, cần nghe trọn và ghi hồ sơ | Ba nháp chờ duyệt, chưa có audio | Đề xuất chờ rà |
| Cửu Đỉnh | Bản văn đã tiếp nhận; WAV/MP3 vi-04 còn nguyên Hash, cần nghe trọn và ghi hồ sơ | Ba nháp chờ duyệt, chưa có audio | Đề xuất chờ rà |
| Cung Diên Thọ | Bản văn đã tiếp nhận; chưa có audio đạt từ lượt cũ | Ba nháp chờ duyệt, chưa có audio | Đề xuất chờ rà |

Nháp audio Ngọ Môn vi-01 là bản ghi khác với revision đã duyệt trong runtime, không thay thế bản được áp dụng. Giữ lịch sử và Hash riêng, không gộp hai bản ghi chỉ vì cùng văn bản.

## Trình tự hoàn thiện

1. Rà nội dung/nguồn/giới hạn của bốn bài vi còn lại và 15 bản dịch. Với bản dùng trình diễn, xác nhận rõ từng bản và giới hạn theo D09; không dùng dịch ngược hoặc kiểm Hash thay thẩm định ngôn ngữ/lịch sử.
2. Nghe trọn vi-02/03/04 tại `%LOCALAPPDATA%\AudioQR-Media-Drafts\vi-v01` để rà giọng/nội dung. API hiện hành gắn audio với job tạo từ bản văn đã lưu, chưa có đường nhập audio ngoài được nghiệm thu; không giả job hoặc sửa trực tiếp database để gắn file. Giữ các bản cũ làm tư liệu đối chiếu, không mặc định đã tái sử dụng được.
3. Theo hợp đồng hiện hành, kế hoạch là tạo bốn bài vi còn lại và 15 bản dịch (19 bài) sau khi chốt bản văn, dùng khóa/quota được phép; nghe trọn từng kết quả trước ghi hồ sơ. Chỉ giảm xuống 16 bài nếu về sau ba audio cũ có đường nhập được duyệt và kiểm riêng; đây chưa phải phương án đã triển khai hoặc lý do mở rộng API trong lượt này.
4. Rà bốn đa giác trong [bộ vùng đề xuất](../04-giao-dien/design-assets/hue-touch-regions-proposal.geojson), ghi căn cứ và phạm vi, rồi Lưu/Áp dụng trên Admin. Không dùng việc giao diện đã được chấp nhận để suy các ranh giới đã được xác minh.
5. Ghi hồ sơ đúng revision văn bản/audio; áp dụng cả cặp, kiểm mức đầy đủ và chỉ bật điểm đủ gate. Kiểm Khách từng ngôn ngữ để tránh nửa cặp, audio sai bản và tự chuyển tiếng.

Sáu tệp T08 nháp ban đầu được giữ nguyên trong lượt đối chiếu này. Bảng theo dõi cũ giữ trạng thái theo thời điểm; hồ sơ Ngọ Môn và báo cáo hiện hành bổ sung mốc mới, không tự tích checklist hoặc duyệt các dòng còn lại.
