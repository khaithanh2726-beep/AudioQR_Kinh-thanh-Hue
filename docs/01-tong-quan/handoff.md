# Hiện trạng sản phẩm và điều kiện bàn giao

Phạm vi ghi nhận: 14/11/2026. Tài liệu ghi trạng thái sản phẩm và điều kiện nghiệm thu; lịch sử chỉ giữ tại [bảng mốc](../99-luu-tru/README.md).

## 1. Sản phẩm và nguồn phục vụ

- Vue Khách/Admin; Spring Boot Controller → Service → Repository; MySQL/Flyway V1–V14.
- Khách có phiên theo tab, vùng chọn trên bản đồ, ghim, bốn ngôn ngữ, văn bản/MP3 đúng cặp, kiểm Hash/Blob và ACK ghi lượt.
- Admin quản lý địa điểm/vùng/văn bản/hồ sơ/cặp; TTS/Check/Retry/nghe thử, thống kê và QR chung. Có Q2/Q3, ETag, command window và receipt khi chưa rõ kết quả.
- Vận hành có gate quiesce/drain/freeze, backup/restore, CD qua Tailscale/OpenSSH, lịch backup và giới hạn log.

Ảnh đang phục vụ được build từ `134ac0d`, có [CD thật đạt](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664). Không có thay đổi Java/Vue/dependency so với ảnh này trong mốc bổ sung; phần mới là công cụ vận hành, kiểm thử, hồ sơ và dữ liệu đã duyệt. Mã nguồn tổng hợp trên T11, lịch sử tác giả được giữ theo G02; trạng thái tích hợp được tra trên [main](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/tree/main), không suy từ tên nhánh hoặc image tag.

## 2. Dữ liệu đang phục vụ

| Thành phần | Trạng thái |
|---|---|
| Truy cập | Loopback 18083, HTTPS/QR chung; bản đồ Khách và Admin cùng origin |
| Địa điểm/vùng | Năm địa điểm hiển thị, năm vùng đang áp dụng; bốn vùng mới được lưu/đọc lại Hash/áp dụng qua API |
| Văn bản/audio | Đủ 20 cặp vi/en/fr/ru; 40 WAV/MP3, mỗi cặp khóa đúng revision/Hash |
| Duyệt | Ngọ Môn vi giữ hồ sơ cũ; 19 cặp mới được chủ đồ án duyệt dùng học phần theo [hồ sơ](../05-noi-dung/duyet-19-cap-v02-20261114.json) |
| Giới hạn nội dung | Không thay chứng nhận chuyên gia/người bản ngữ; giữ ghi chú thuật ngữ Thái Hòa fr. Không ghi tự động thành bằng chứng người đã nghe trọn |
| Bảo vệ dữ liệu | Nhập bổ sung từ kho nội dung riêng, không thay database; Hash dữ liệu cũ và cấu hình runtime giữ nguyên, cặp Ngọ Môn vi không đổi |
| Kiểm Khách | Bản sao và bản phục vụ đều đạt 20/20 văn bản/MP3 khớp Hash; replay cùng key giữ opening; bốn phiên kỹ thuật đã kết thúc, không gửi view-ack |

[Ma trận nội dung](../05-noi-dung/doi-chieu-van-ban-audio.md) là nguồn trạng thái. Bundle v02 giữ nguyên Hash để nhận diện revision/job; metadata lúc chuẩn bị không thay hồ sơ duyệt sau đó.

## 3. Kiểm chứng và vận hành

[CI mốc 1bab9a6](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43598895644) đạt backend/frontend; bộ CI gồm 530 backend, 71 logic frontend, 33 ca vận hành và Vue build. Bốn ca receipt mới kiểm mất phản hồi/xung đột và chặn tự gửi lại lệnh ghi. Push/PR không tự deploy. Các lượt riêng không cộng thành một full lớn hơn.

Chrome 157.0.8103.78 / Windows 10.0.26300 đạt 11/11 ca trên bản phục hồi riêng: tab/reload/duplicate, mất response sau commit, mạng/audio lỗi, cảnh báo/ngoại lệ nghe tiếp, Q2/401, thống kê và crash gate. Clock/timestamp/401 là fixture, không phải chờ thật 8 giờ/30 phút. Ca crash chứng minh giữ gate và không tự xóa lease chưa rõ owner; không chứng minh mọi reconcile sau crash đã đạt.

Lịch Windows 23:00 hằng ngày dùng Interactive/Limited, không chạy bù, không lưu mật khẩu; kho backup/log có ACL riêng. Backup giữ bảy ngày, chỉ dọn bản quá hạn đã đối chiếu sau khi có bản mới hợp lệ. Báo cáo chạy lịch có TTL bảy ngày. App/MySQL dùng Docker local 10 MB × 3 tệp; đã kiểm xoay thật trong container riêng. Giới hạn dung lượng Docker không đồng nghĩa TTL theo tuổi của mọi log hạ tầng.

Trigger một lần tự chạy lúc 20:18:59 ngày 14/11 đạt LastTaskResult=0, backup đủ 40 tệp và mở lại HTTP. Khoảng đóng gate đo được 293.391 giây, không phải cam kết RTO. Trigger thử đã gỡ, lịch 23:00 giữ nguyên; chưa gọi đây là đã quan sát lượt 23:00. Chính backup này được restore sang project/schema/volume riêng: 40/40 Hash tại đích, 20 cặp đang áp dụng, năm điểm hiển thị, epoch mới, retention theo mốc gốc, gate FROZEN; hai container thử đã dừng.

Local/HTTPS Khách, Admin và API trả 200 sau xuất bản. Không dùng API thay bằng chứng nghe trên điện thoại. Chi tiết tại [báo cáo kiểm](../06-kiem-thu/README.md), [bảo trì/restore](../07-van-hanh/bao-tri-va-sao-luu.md) và [điều kiện nghiệm thu](../07-van-hanh/nghiem-thu-van-hanh.md).

## 4. Lượt nghiệm thu cuối

1. Kiểm thiết bị được thực hiện sau mốc nguồn/dữ liệu này: mẫu máy, OS/browser, mạng, QR/bản đồ/đọc-nghe/đổi tiếng/reload/hai tab. Android/iPhone từng nghe Ngọ Môn cơ bản; không suy thành đã kiểm toàn bộ bộ mới.
2. Người nhận thực hiện lại hướng dẫn trên môi trường đã thống nhất, xác nhận tài liệu/khởi động/tắt/backup và bàn giao tài khoản/khóa qua kênh riêng.
3. Theo dõi lịch 23:00 trong sử dụng thực, các mục backup/log cần review, hạn nguồn và lease/claim bất thường; máy tắt hoặc đăng xuất không có cam kết backup tự chạy. Không hạ V14 về V13.

Checklist hiện **62/89**, còn **27 ô chưa tích**. [Roadmap](roadmap.md) phân biệt dấu tích thực tế và đề xuất dựa trên bằng chứng; chưa tự sửa issue/Project. Ma trận có 23 FR/14 UC/50 AC/6 NFR; các điều kiện nhiều vế chỉ đóng khi đủ cả ô.

## 5. Nguồn tài liệu

[PRD](../02-nghiep-vu/PRD.md), [Business Flow](../02-nghiep-vu/business-flow.md), [quy tắc G02/D/A/Q/UI/TECH/DATA](../02-nghiep-vu/decisions.md), [TDD](../03-ky-thuat/TDD.md), [API](../03-ky-thuat/api-transactions.md) và [dữ liệu](../03-ky-thuat/data-model.md) giữ hợp đồng đã chốt. [Tra chức năng](../07-van-hanh/tra-cuu-chuc-nang.md), [ma trận nghiệm thu](../06-kiem-thu/ma-tran-truy-vet.md) và nhóm 07-van-hanh hỗ trợ chấm/chạy. Log/ảnh/dump/audio/khóa giữ tại kho riêng đúng loại, không đưa vào hồ sơ Git chính.
