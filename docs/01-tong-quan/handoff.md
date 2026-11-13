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
| Bản tập dượt | Ngọ Môn vi; schema V14; loopback 18083; HTTPS/API đã kiểm lại sau nâng, URL QR giữ nguyên |
| Vùng | Ngọ Môn được chấp thuận dùng tập dượt; bốn vùng khác đã rà/lưu/đọc lại Hash/áp dụng trên bản riêng |
| Văn bản | Bundle v02 có 20 bài; các bản chưa duyệt cuối giữ giới hạn demo/nháp |
| Audio mới | 14/19 bài thật vi/en/fr; năm ru chưa có kết quả đạt do quota |
| Duyệt | 13 audio khớp qua AI, Thái Hòa fr cần nghe lại; chưa có nghe duyệt trọn của người duyệt cho bộ mới |
| Phục vụ Khách | Chỉ Ngọ Môn vi được áp dụng/hiển thị trong bản tập dượt; chưa áp dụng các cặp mới |

## 3. Kiểm chứng

Full backend 530/530, 71/71 logic frontend, build và CI có kết quả đạt theo từng lượt. Đợt vận hành bổ sung đạt 20 ca Node cục bộ (5 manifest + 12 integrity + 3 runtime-env), snapshot/restore V13 và nâng V13→V14 riêng, backup/restore 30 tệp nội dung và hai tệp bản phục vụ. Ảnh V14 mới build frontend 71/71 và đóng gói JAR đạt, backend tests không chạy trong bước package. Hai scheduler dọn đã bật trên bản phục vụ sau kiểm riêng. Android/iPhone từng dùng Ngọ Môn cơ bản; chưa kiểm lại toàn ma trận sau cập nhật. CD toàn tuyến đã đạt; lịch backup/log và một số ca crash vẫn còn mở.

[CD thật ngày 14/11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đạt cả frontend/backend/deploy trên nguồn T10 `134ac0d`: backend 530/530, frontend 71/71 và 20 ca backup/cấu hình. Bản phục vụ dùng ảnh `hueqr-local:ci-134ac0d496c1646dd5d96a0b66bd59e3d6648c87`, có backup trước chuyển. Đã đọc lại runtime: schema V14, volume/vùng/cặp/khóa/epoch giữ nguyên, hai Hash audio thật khớp và local/HTTPS trả 200. Đây là phép kiểm kỹ thuật; chưa phải lượt nghe mới trên điện thoại.

Chi tiết tại [báo cáo kiểm thử](../06-kiem-thu/README.md). Checklist đã đối chiếu **62/89**, còn **27 ô** chưa đủ toàn điều kiện; [roadmap](roadmap.md) giữ nguyên các ô và phân công.

## 4. Điều kiện cần hoàn tất

1. Tạo năm audio ru khi quota khả dụng, đối chiếu đúng revision/ngôn ngữ và bảo quản file/Hash.
2. Nghe trọn và duyệt từng cặp theo D09; kiểm thuật ngữ Thái Hòa fr; giữ giới hạn bản dịch chưa thẩm định chuyên gia.
3. Áp dụng cặp đạt duyệt, kiểm Khách đủ năm điểm/bốn ngôn ngữ và các ca lỗi/time/tab/thiết bị còn thiếu.
4. Hoàn thiện ma trận truy vết, các tình huống claim/lease sau crash và lịch log/backup. CD thật từ GitHub runner đã đạt, xem bằng chứng mục 3.
5. Duy trì backup/restore đã kiểm, chốt lịch backup/log và hoàn tất nghiệm thu/bàn giao. Bản phục vụ đã chuyển V14; không tự hạ về V13 trên schema V14.

## 5. Nguồn tài liệu chính

[PRD](../02-nghiep-vu/PRD.md) quy định yêu cầu; [Business Flow](../02-nghiep-vu/business-flow.md) quy định luồng; [quy tắc](../02-nghiep-vu/decisions.md) giữ mã G02/D/A/Q/UI/TECH/DATA; [TDD](../03-ky-thuat/TDD.md), [API](../03-ky-thuat/api-transactions.md), [dữ liệu](../03-ky-thuat/data-model.md) và [vận hành](../03-ky-thuat/operations-design.md) mô tả kỹ thuật. Hướng dẫn chạy/sử dụng tại nhóm 07-van-hanh.
