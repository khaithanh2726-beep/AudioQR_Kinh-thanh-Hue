# Điều kiện nghiệm thu và bàn giao môi trường

## 1. Dữ liệu phục vụ

Đủ năm điểm × bốn tiếng = 20 văn bản và 20 audio đúng revision/Hash; nghe trọn và duyệt từng cặp, ghi giới hạn bản dịch theo D09. Áp dụng qua API/Admin và bật theo gate, không dùng dữ liệu/ref fixture làm hồ sơ duyệt. Hiện còn năm audio ru, nghe duyệt 14 bài mới và thuật ngữ Thái Hòa fr.

## 2. Ứng dụng và thiết bị

Build từ nguồn/lockfile, Vue/API cùng origin, MySQL/Flyway và kho audio riêng. Kiểm QR mở form, phiên/chọn vùng/đọc-nghe/đổi tiếng, lỗi mạng/reload/tab, mốc hết hạn và Q2/Q3. Android/Chrome, iPhone/Safari, Admin Windows/Chrome cần ma trận thiết bị/browser thực; xác nhận dùng Ngọ Môn cơ bản không đủ toàn ma trận.

## 3. Vận hành và khôi phục

[Quy trình bảo trì](bao-tri-va-sao-luu.md) yêu cầu đúng owner, đóng tiếp nhận việc, drain và đóng băng trước snapshot. Giữ MySQL/audio cùng checkpoint, Hash và hạn nguồn; restore sang schema/volume mới, lọc retention theo anchor gốc và cấp epoch mới. Không suy restart thường thành restore, không xóa claim theo tuổi hoặc mở lại gate sau lỗi.

Gate/backup/restore và scheduler V14 đã kiểm trên bộ riêng. Bản phục vụ vẫn V13; còn mốc chuyển phục vụ, lịch log/backup và xử lý owner/claim thiếu bằng chứng sau crash. Chưa restore lại chính snapshot nội dung 30 tệp mới; không dùng kết quả snapshot cũ thay lượt đó.

## 4. CI/CD và khả năng tái lập

CI đã chạy thật trên GitHub và kiểm PR #15 trước merge. CD-L01 qua Tailscale/OpenSSH Windows cần xác thực máy/tài khoản, host key, quyền Docker/context, cấu hình riêng và thư mục triển khai; chưa có lượt triển khai từ pipeline. Kiểm restart/update/rollback phải giữ volume/khóa theo quy trình và tương thích schema.

## 5. Hồ sơ bàn giao

Bộ chấm gồm đặc tả, thiết kế, dữ liệu/hồ sơ duyệt, báo cáo kiểm, hướng dẫn chạy/sử dụng và giới hạn còn mở. Bằng chứng thô, tài khoản/khóa và snapshot runtime giữ riêng đúng loại; mã nguồn Git không thay backup database/audio. Người nhận cần thực hiện lại hướng dẫn trên môi trường thống nhất và ghi kết quả thực tế. Checklist chỉ đóng khi đủ từng điều kiện, không tự kết luận Done từ commit/merge.

[Báo cáo kiểm](../06-kiem-thu/README.md) và [hiện trạng](../01-tong-quan/handoff.md) là nguồn ghi kết quả; [roadmap](../01-tong-quan/roadmap.md) giữ 62/89 ô hiện tại.
