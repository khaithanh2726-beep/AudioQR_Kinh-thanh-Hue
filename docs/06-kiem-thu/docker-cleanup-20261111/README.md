# Dọn Docker thử nghiệm — 11/11/2026

Đã dọn theo danh sách định danh cụ thể sau khi kiểm trạng thái/mount/image. Không dùng `system prune`, `volume prune`, `container prune`, `--force` hoặc xóa volume. Không sửa mã ứng dụng, dữ liệu database, audio, Git/issue/Project hoặc thư mục nguồn chưa theo dõi.

## Kết quả

- Xóa 6 container thử đã dừng: một app map-preview cũ, hai app packaged cũ, hai database restore và một database E2E cũ. Danh sách tại [containers-removed.json](containers-removed.json), mount volume/định danh phục hồi tại [removal-plan.json](removal-plan.json).
- Xóa 3 image ứng dụng cũ không còn container tham chiếu. Không xóa image bản xem trước đang chạy hoặc image MySQL/testcontainers cần dùng; [kết quả image](images-removed.json).
- Còn 3 container: app `hueqr-map-preview-507da98b` và DB `hueqr-preview-mysql-91b4feca` đang chạy; `hue-qr-dev-mysql-1` của workspace trước đang dừng và được giữ nguyên cùng volume `hue-qr-dev-db`.
- Giữ cả 12 volume, đối chiếu tên trước/sau không khác. Các volume của database thử đã xóa container vẫn còn; không tự phân loại volume không rõ nguồn thành rác chỉ vì đang không có container dùng.
- Map preview và trang Admin ở cổng 18082 trả HTTP 200; query database vẫn có 5 điểm, 20 slot, 5 bản văn nháp. [Báo cáo kiểm sau dọn](result.json).

Image tags `hueqr-local:20261111-map-preview` và `hueqr-local:20261111-map-preview-final` cùng trỏ một image, không nhân đôi dung lượng. Build cache có lịch sử của cả workspace chính và workspace cũ nên không prune chung. [Dung lượng Docker sau dọn](disk-after.txt) không phải bằng chứng đã thu nhỏ tệp đĩa Docker Desktop trên Windows.

## Dữ liệu và khả năng dựng lại

Ảnh/log/Hash của các lượt trước trong `docs/06-kiem-thu` được giữ nguyên dưới đúng mốc lịch sử, dù container/image thử tương ứng đã được dọn. Dump/manifest, kho WAV/MP3 và cấu hình riêng ngoài repository không bị xóa. Bản kiểm kê chi tiết trước dọn ở LocalAppData `AudioQR-Docker-Cleanup/20261111-210223`; không chứa bản sao biến môi trường hoặc mật khẩu.

Các lệnh `docker start` dùng tên container cũ đã xóa sẽ không còn dùng được. Nếu cần dựng lại database thử, đối chiếu volume trong `removal-plan.json`, đảm bảo volume chưa gắn vào một MySQL khác, rồi tạo container MySQL **8.4.13** gắn đúng volume còn giữ. Không khởi tạo lại hoặc xóa dữ liệu volume. Dữ liệu tài khoản có trong volume, mật khẩu/runtime ở tệp riêng trước đó. Muốn chạy lại ứng dụng còn cần image phù hợp hoặc build lại và đối chiếu datasource/cổng/đường dẫn audio; không coi việc dựng lại container DB là restore ứng dụng đã kiểm.

Lượt này giữ volume để bảo toàn dữ liệu, không phải diễn tập backup/restore mới. Môi trường đang dùng để rà vẫn là bản xem trước tại `http://127.0.0.1:18082/map-preview.html`.
