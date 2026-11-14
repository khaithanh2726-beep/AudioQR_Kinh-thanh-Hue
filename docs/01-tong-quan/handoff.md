# Hiện trạng sản phẩm và điều kiện bàn giao

Phạm vi ghi nhận: 14/11/2026. Tài liệu tổng hợp trạng thái đang có, điều kiện chưa đạt và đầu việc tiếp theo; lịch sử phát triển chỉ giữ tại [bảng mốc](../99-luu-tru/README.md).

## 1. Sản phẩm đã có

- Vue Khách/Admin; Spring Boot ba lớp; MySQL/Flyway V1–V14.
- Phiên Khách theo tab, vùng/cặp áp dụng đúng ngôn ngữ, ACK ghi lượt, tải MP3 theo quyền và kiểm Hash/Blob.
- Admin quản lý địa điểm/vùng/văn bản/hồ sơ/cặp, TTS/Check/Retry/nghe thử, thống kê và QR chung.
- Worker WAV→MP3, vòng đời dữ liệu/file, gate bảo trì và công cụ snapshot/restore môi trường riêng.

Nguồn đã tích hợp qua [PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/15), main `601c09a`. CD đang phục vụ nguồn T10 `134ac0d`. Mốc bổ sung 14/11 gồm lịch backup/log, kiểm Chrome, bộ 20 audio kỹ thuật và hồ sơ tra cứu/truy vết đã commit/push lên T08/T09/T10/T11 theo G02; T11 giữ bản tổng hợp. Main vẫn 601c09a; mốc này chưa merge main và không đổi nguồn Vue/Java ứng dụng đang phục vụ.

## 2. Phân biệt nguồn, bản tập dượt và dữ liệu

| Thành phần | Trạng thái |
|---|---|
| Mã nguồn | Có V14 và đủ module đã nêu, giữ kiến trúc Controller → Service → Repository |
| Bản tập dượt | Ngọ Môn vi; schema V14; loopback 18083; HTTPS/API đã kiểm lại sau nâng, URL QR giữ nguyên |
| Vùng | Ngọ Môn được chấp thuận dùng tập dượt; bốn vùng khác đã rà/lưu/đọc lại Hash/áp dụng trên bản riêng |
| Văn bản | Bundle v02 có 20 bài; các bản chưa duyệt cuối giữ giới hạn demo/nháp |
| Audio mới | 19/19 bài thật vi/en/fr/ru; đủ 20 khi tính Ngọ Môn vi cũ, probe/giải mã đầy đủ |
| Duyệt | 18 transcript khớp ý/số liệu, Thái Hòa fr cần nghe thuật ngữ; 19 bài mới chưa có nghe duyệt trọn của người |
| Phục vụ Khách | Chỉ Ngọ Môn vi được áp dụng/hiển thị trong bản tập dượt; chưa áp dụng các cặp mới |

## 3. Kiểm chứng

[CI nguồn tổng hợp 3f66b72](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43592287460) đạt 530/530 backend, 71/71 logic frontend, 29/29 ca vận hành và Vue build. Deploy bị bỏ qua đúng điều kiện của lượt push; không gọi đây là CD mới. Kết quả Chrome riêng và bộ audio được ghi ở các lượt dưới, không cộng thành cùng một full run.

Full backend 530/530, 71/71 logic frontend, build và CI có kết quả đạt theo từng lượt. Đợt vận hành bổ sung đạt 20 ca Node cục bộ (5 manifest + 12 integrity + 3 runtime-env), snapshot/restore V13 và nâng V13→V14 riêng, backup/restore 30 tệp nội dung và hai tệp bản phục vụ. Ảnh V14 mới build frontend 71/71 và đóng gói JAR đạt, backend tests không chạy trong bước package. Hai scheduler dọn đã bật trên bản phục vụ sau kiểm riêng. Android/iPhone từng dùng Ngọ Môn cơ bản; chưa kiểm lại toàn ma trận sau cập nhật. CD toàn tuyến đã đạt; lịch backup/log đã bổ sung ở mốc dưới đây, còn theo dõi trigger và một số ca crash.

[CD thật ngày 14/11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đạt cả frontend/backend/deploy trên nguồn T10 `134ac0d`: backend 530/530, frontend 71/71 và 20 ca backup/cấu hình. Bản phục vụ dùng ảnh `hueqr-local:ci-134ac0d496c1646dd5d96a0b66bd59e3d6648c87`, có backup trước chuyển. Đã đọc lại runtime: schema V14, volume/vùng/cặp/khóa/epoch giữ nguyên, hai Hash audio thật khớp và local/HTTPS trả 200. Đây là phép kiểm kỹ thuật; chưa phải lượt nghe mới trên điện thoại.

Chi tiết tại [báo cáo kiểm thử](../06-kiem-thu/README.md). Checklist thực tế vẫn **62/89**, còn **27 ô chưa tích**; [roadmap](roadmap.md) giữ nguyên dấu tích/phân công và ghi riêng các đề xuất mới theo bằng chứng.

Vận hành ngày 14/11 bổ sung 29/29 ca Node (5 manifest + 12 integrity + 3 runtime-env + 9 retention). Task Windows `AudioQR-Operations-hueqr-rehearsal-15d2f5c0` đăng ký 23:00 mỗi ngày, Interactive/Limited, không chạy bù khi mở máy; lượt kích hoạt qua Task Scheduler trả LastTaskResult=0, tạo backup có hai audio và mở lại dịch vụ. Gate đóng 110.157 giây trong lượt đo này, không phải cam kết RTO. Đã restore chính backup do task tạo sang project mới, đối chiếu database/catalog/bytes hai audio, đổi khóa/epoch và giữ gate đóng; container thử dừng.

Cả hai container phục vụ đã dùng log local 10 MB × 3 tệp, cùng ảnh/volume/schema/khóa như trước. Container riêng sinh 45.000 dòng để kiểm xoay: dòng cũ bị bỏ, dòng cuối còn và payload giữ lại khoảng 25,9 MB; container thử đã xóa. Báo cáo chạy lịch có TTL bảy ngày; buffer Docker chỉ giới hạn dung lượng, không cam kết TTL theo tuổi. API Admin/Khách trên bản phục vụ đạt chín nhóm kiểm, gồm MP3 93.740 bytes đúng Hash và ba nhánh từ chối; tạo một phiên/lượt kiểm rồi kết thúc phiên. HTTPS được kiểm lại 200 sau mở tunnel. Chưa có lượt nghe mới trên điện thoại và chưa quan sát trigger 23:00 tự chạy.

Kiểm Chrome bổ sung: 11/11 ca đạt trên 157.0.8103.78 / Windows 10.0.26300. Bao gồm tab/reload/duplicate, mất response sau commit, lỗi mạng/audio, cảnh báo/ngoại lệ nghe tiếp, Q2/401, thống kê và crash gate thật trên container riêng. Clock/timestamp/401 là fixture được ghi riêng, không thay lượt chờ 8 giờ/30 phút hoặc thiết bị thật. [Tra cứu mã nguồn](../07-van-hanh/tra-cuu-chuc-nang.md) và [ma trận nghiệm thu](../06-kiem-thu/ma-tran-truy-vet.md) đã đối chiếu đủ mã. Bộ nội dung riêng đủ 20 text/20 audio, 40 WAV/MP3 được restore/Hash ở đích; worker tắt và khóa provider đã bỏ khỏi cấu hình thử.

## 4. Điều kiện cần hoàn tất

1. Bảo quản bộ 20 audio đã có và snapshot 40 tệp đúng hạn; quota không còn là điểm chặn của lượt này.
2. Nghe trọn và duyệt từng cặp theo D09; kiểm thuật ngữ Thái Hòa fr; giữ giới hạn bản dịch chưa thẩm định chuyên gia.
3. Áp dụng cặp đạt duyệt, kiểm Khách đủ năm điểm/bốn ngôn ngữ và các ca lỗi/time/tab/thiết bị còn thiếu.
4. Ma trận 23 FR/14 UC/50 AC/6 NFR đã có; hoàn tất các điều kiện còn mở trong từng hàng và các tình huống claim/lease sau crash. Ca crash riêng đã kiểm giữ gate/không tự xóa lease chưa rõ owner, không chứng minh mọi reconcile đã đạt.
5. Theo dõi trigger 23:00, LastTaskResult và mục review của backup/log; xử lý lượt bỏ lỡ ngoài trình diễn. Giữ giới hạn log hạ tầng, kiểm thiết bị sau cập nhật và nghiệm thu/bàn giao. Bản phục vụ đã V14; không tự hạ về V13 trên schema V14.

## 5. Nguồn tài liệu chính

[PRD](../02-nghiep-vu/PRD.md) quy định yêu cầu; [Business Flow](../02-nghiep-vu/business-flow.md) quy định luồng; [quy tắc](../02-nghiep-vu/decisions.md) giữ mã G02/D/A/Q/UI/TECH/DATA; [TDD](../03-ky-thuat/TDD.md), [API](../03-ky-thuat/api-transactions.md), [dữ liệu](../03-ky-thuat/data-model.md) và [vận hành](../03-ky-thuat/operations-design.md) mô tả kỹ thuật. Hướng dẫn chạy/sử dụng tại nhóm 07-van-hanh.
