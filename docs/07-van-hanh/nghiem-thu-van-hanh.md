# Điều kiện nghiệm thu và bàn giao môi trường

## 1. Nội dung phục vụ

Bản phục vụ V14 đã hiển thị năm điểm, đủ 20 cặp vi/en/fr/ru. Ngọ Môn vi giữ hồ sơ cũ; 19 cặp mới có [chấp thuận dùng học phần](../05-noi-dung/duyet-19-cap-v02-20261114.json), revision/Hash cụ thể và giới hạn chuyên gia/phát âm. Không dùng thành công TTS/ASR thay bằng chứng người đã nghe trọn. Thay văn bản/audio phải rà và duyệt lại phần bị ảnh hưởng theo D09. API Khách trên bản sao và bản phục vụ đều đạt 20/20 văn bản/MP3 khớp Hash.

## 2. Ứng dụng và thiết bị

CI đã kiểm build từ nguồn/lockfile, 530 backend, 71 logic frontend, 33 ca vận hành. Vue/API cùng origin, MySQL/Flyway và kho audio riêng. Chrome riêng đạt 11/11 ca tab/reload/mạng/lỗi/Q2/clock/crash với phạm vi fixture được ghi rõ. Android/iPhone từng dùng Ngọ Môn cơ bản; ma trận mẫu máy/OS/browser của bộ cập nhật được thực hiện ở lượt cuối, gồm QR/bản đồ/đọc-nghe/đổi tiếng/reload/hai tab. Không thay phép thử thiết bị bằng API.

## 3. Vận hành và phục hồi

[Quy trình bảo trì](bao-tri-va-sao-luu.md) yêu cầu đúng owner, quiesce/drain/freeze, snapshot database/audio cùng checkpoint, Hash và hạn nguồn; restore sang schema/volume mới, lọc retention theo anchor gốc và cấp epoch mới. Không tự xóa claim theo tuổi hoặc mở gate sau lỗi.

Trigger một lần tự chạy lúc 20:18:59 ngày 14/11 đạt LastTaskResult=0, backup đủ 40 tệp và mở lại HTTP. Khoảng đóng gate đo được 293.391 giây, không phải cam kết RTO. Trigger thử đã gỡ, lịch 23:00 giữ nguyên; chưa gọi đây là đã quan sát lượt 23:00. Chính backup này được restore sang project/schema/volume riêng: 40/40 Hash tại đích, 20 cặp đang áp dụng, năm điểm hiển thị, epoch mới, retention theo mốc gốc, gate FROZEN; hai container thử đã dừng.

Báo cáo chạy lịch giữ bảy ngày, chỉ dọn backup quá hạn đã đối chiếu sau khi có bản mới hợp lệ. Docker local 10 MB × 3 tệp/container đã kiểm xoay; đây là giới hạn dung lượng, không phải TTL theo tuổi của mọi log hạ tầng. Lịch không cam kết chạy khi laptop tắt/đăng xuất; tiếp tục theo dõi review và lease/claim bất thường.

## 4. CI/CD và nguồn chạy

[CD thật nguồn 134ac0d](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đã đạt qua Tailscale/OpenSSH/receiver, ảnh/volume/khóa/epoch được đối chiếu. Không có thay đổi Java/Vue/dependency so với ảnh đó trong mốc bổ sung công cụ/dữ liệu này. Push/PR chỉ kiểm, không tự chuyển bản phục vụ. Update/rollback giữ dữ liệu và phải tương thích schema; không hạ V14 về V13.

## 5. Hồ sơ bàn giao

Bộ chấm gồm yêu cầu/thiết kế, hồ sơ duyệt, báo cáo kiểm, tra chức năng và hướng dẫn chạy/sử dụng/backup/restore. Log/dump/audio/khóa giữ riêng đúng loại; Git không thay backup dữ liệu. Người nhận cần thực hiện lại hướng dẫn và ghi kết quả thực tế; không suy đã bàn giao độc lập từ kiểm của công cụ. Checklist giữ 62/89, chỉ đề xuất tích khi đủ từng ô nhiều vế.

[Báo cáo kiểm](../06-kiem-thu/README.md), [hiện trạng](../01-tong-quan/handoff.md) và [roadmap](../01-tong-quan/roadmap.md) là nguồn kết quả/giới hạn.
