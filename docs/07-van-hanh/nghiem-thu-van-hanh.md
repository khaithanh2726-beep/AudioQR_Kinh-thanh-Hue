# Điều kiện nghiệm thu và bàn giao môi trường

## 1. Dữ liệu phục vụ

Đủ năm điểm × bốn tiếng = 20 văn bản và 20 audio đúng revision/Hash; nghe trọn và duyệt từng cặp, ghi giới hạn bản dịch theo D09. Áp dụng qua API/Admin và bật theo gate, không dùng dữ liệu/ref fixture làm hồ sơ duyệt. Hiện đã đủ 20 audio kỹ thuật; còn nghe duyệt 19 bài mới và thuật ngữ Thái Hòa fr.

## 2. Ứng dụng và thiết bị

Build từ nguồn/lockfile, Vue/API cùng origin, MySQL/Flyway và kho audio riêng. Kiểm QR mở form, phiên/chọn vùng/đọc-nghe/đổi tiếng, lỗi mạng/reload/tab, mốc hết hạn và Q2/Q3. Android/Chrome, iPhone/Safari, Admin Windows/Chrome cần ma trận thiết bị/browser thực; xác nhận dùng Ngọ Môn cơ bản không đủ toàn ma trận.

## 3. Vận hành và khôi phục

[Quy trình bảo trì](bao-tri-va-sao-luu.md) yêu cầu đúng owner, đóng tiếp nhận việc, drain và đóng băng trước snapshot. Giữ MySQL/audio cùng checkpoint, Hash và hạn nguồn; restore sang schema/volume mới, lọc retention theo anchor gốc và cấp epoch mới. Không suy restart thường thành restore, không xóa claim theo tuổi hoặc mở lại gate sau lỗi.

Gate/backup/restore và scheduler V14 đã kiểm trên bộ riêng. Snapshot nội dung 30 tệp mới đã được backup/restore sang project mới, đối chiếu Hash ở đích và khởi động với gate đóng. Scheduler trên chính bản phục hồi đã giữ 30 tệp thật, chỉ dọn fixture quá hạn khi gate mở. Bản phục vụ đã chuyển V14 và bật hai scheduler dọn. Lịch backup Windows 23:00 đã đăng ký, kích hoạt thật trả mã 0 và restore hai file đạt ở đích riêng. Báo cáo chạy lịch có TTL bảy ngày; Docker local 10 MB × 3 tệp/container đã kiểm xoay bằng dữ liệu tổng hợp. Còn quan sát trigger tự chạy, các mục review, giới hạn TTL log hạ tầng và owner/claim thiếu bằng chứng sau crash.

## 4. CI/CD và khả năng tái lập

CI đã chạy thật trên GitHub và kiểm PR #15 trước merge. CD-L01 đã kiểm khóa/host key, Docker context qua SSH và SFTP/receiver trên restore riêng. [Job deploy thật](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đã đạt, source/image/runtime và Hash audio được đối chiếu sau chuyển. Kiểm restart/update/rollback phải giữ volume/khóa theo quy trình và tương thích schema; receiver riêng và workflow thật là hai lượt bằng chứng phân biệt. Backup/log theo lịch được ghi tại mục 3 và hướng dẫn bảo trì.

## 5. Hồ sơ bàn giao

Bộ chấm gồm đặc tả, thiết kế, dữ liệu/hồ sơ duyệt, báo cáo kiểm, hướng dẫn chạy/sử dụng và giới hạn còn mở. Bằng chứng thô, tài khoản/khóa và snapshot runtime giữ riêng đúng loại; mã nguồn Git không thay backup database/audio. Người nhận cần thực hiện lại hướng dẫn trên môi trường thống nhất và ghi kết quả thực tế. Checklist chỉ đóng khi đủ từng điều kiện, không tự kết luận Done từ commit/merge.

[Báo cáo kiểm](../06-kiem-thu/README.md) và [hiện trạng](../01-tong-quan/handoff.md) là nguồn ghi kết quả; [roadmap](../01-tong-quan/roadmap.md) giữ 62/89 ô hiện tại.
