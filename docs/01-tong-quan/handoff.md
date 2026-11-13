# Hiện trạng sản phẩm và điều kiện bàn giao

Phạm vi ghi nhận: 13/11/2026. Tài liệu tổng hợp trạng thái đang có, điều kiện chưa đạt và đầu việc tiếp theo; lịch sử phát triển chỉ giữ tại [bảng mốc](../99-luu-tru/README.md).

## 1. Sản phẩm đã có

- Vue Khách/Admin; Spring Boot ba lớp; MySQL/Flyway V1–V14.
- Phiên Khách theo tab, vùng/cặp áp dụng đúng ngôn ngữ, ACK ghi lượt, tải MP3 theo quyền và kiểm Hash/Blob.
- Admin quản lý địa điểm/vùng/văn bản/hồ sơ/cặp, TTS/Check/Retry/nghe thử, thống kê và QR chung.
- Worker WAV→MP3, vòng đời dữ liệu/file, gate bảo trì và công cụ snapshot/restore môi trường riêng.

Nguồn đã tích hợp qua [PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/15), main `601c09a`. Các thay đổi biên tập tài liệu sau mốc này được chuẩn bị trên T01; không tự coi là đã commit hoặc cập nhật bản đang phục vụ.

## 2. Phân biệt nguồn, bản tập dượt và dữ liệu

| Thành phần | Trạng thái |
|---|---|
| Mã nguồn | Có V14 và đủ module đã nêu, giữ kiến trúc Controller → Service → Repository |
| Bản tập dượt | Ngọ Môn vi; schema V13; loopback 18083; HTTPS/ngrok và QR theo cấu hình đã ghi nhận |
| Vùng | Ngọ Môn được chấp thuận dùng tập dượt; bốn vùng khác đã rà/lưu/đọc lại Hash/áp dụng trên bản riêng |
| Văn bản | Bundle v02 có 20 bài; các bản chưa duyệt cuối giữ giới hạn demo/nháp |
| Audio mới | 14/19 bài thật vi/en/fr; năm ru chưa có kết quả đạt do quota |
| Duyệt | 13 audio khớp qua AI, Thái Hòa fr cần nghe lại; chưa có nghe duyệt trọn của người duyệt cho bộ mới |
| Phục vụ Khách | Chỉ Ngọ Môn vi được áp dụng/hiển thị trong bản tập dượt; chưa áp dụng các cặp mới |

## 3. Kiểm chứng

Full backend 530/530, 71/71 logic frontend, build và CI có kết quả đạt. Chrome đã kiểm API/MP3 thật và nhiều trạng thái UI trong phạm vi từng lượt. Android và iPhone có báo cáo sử dụng Ngọ Môn cơ bản; thiếu ma trận OS/browser/ca lỗi đầy đủ. Backup/restore và scheduler V14 đã kiểm riêng, chưa suy thành toàn vận hành bản phục vụ.

Chi tiết tại [báo cáo kiểm thử](../06-kiem-thu/README.md). Checklist đã đối chiếu **62/89**, còn **27 ô** chưa đủ toàn điều kiện; [roadmap](roadmap.md) giữ nguyên các ô và phân công.

## 4. Điều kiện cần hoàn tất

1. Tạo năm audio ru khi quota khả dụng, đối chiếu đúng revision/ngôn ngữ và bảo quản file/Hash.
2. Nghe trọn và duyệt từng cặp theo D09; kiểm thuật ngữ Thái Hòa fr; giữ giới hạn bản dịch chưa thẩm định chuyên gia.
3. Áp dụng cặp đạt duyệt, kiểm Khách đủ năm điểm/bốn ngôn ngữ và các ca lỗi/time/tab/thiết bị còn thiếu.
4. Hoàn thiện ma trận truy vết, CD/SSH/Tailscale, các tình huống claim/lease sau crash và lịch log/backup.
5. Chuyển bản phục vụ theo quy trình có backup/restore và giữ khả năng khôi phục schema tương thích; nghiệm thu/bàn giao cuối.

## 5. Nguồn tài liệu chính

[PRD](../02-nghiep-vu/PRD.md) quy định yêu cầu; [Business Flow](../02-nghiep-vu/business-flow.md) quy định luồng; [quy tắc](../02-nghiep-vu/decisions.md) giữ mã G02/D/A/Q/UI/TECH/DATA; [TDD](../03-ky-thuat/TDD.md), [API](../03-ky-thuat/api-transactions.md), [dữ liệu](../03-ky-thuat/data-model.md) và [vận hành](../03-ky-thuat/operations-design.md) mô tả kỹ thuật. Hướng dẫn chạy/sử dụng tại nhóm 07-van-hanh.
