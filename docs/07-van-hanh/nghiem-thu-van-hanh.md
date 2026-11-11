# Điều kiện nghiệm thu môi trường demo

Tài liệu này hướng dẫn hoàn tất các phép kiểm còn mở. Quy tắc vòng đời, backup và restore giữ tại [thiết kế vận hành](../03-ky-thuat/operations-design.md); không thay hợp đồng bằng cấu hình chạy thử. Kết quả thực chạy lưu ở `docs/06-kiem-thu`, không đánh dấu đạt từ việc có cấu hình.

## 1. Dữ liệu phục vụ

Nhóm duyệt vùng/nền bản đồ và từng bản trong [ma trận 20 cặp](../05-noi-dung/doi-chieu-van-ban-audio.md). Mỗi audio phải đọc đúng revision, nghe trọn, ghi tham chiếu và giới hạn. Bản dịch nháp chỉ dùng để rà ngôn ngữ/lịch sử; tạo audio sau khi chốt bản văn nhằm tránh tiêu hao quota khi sửa. Audio nháp ngoài repository phải được bảo quản cùng manifest Hash.

Áp dụng từng cặp trong Admin, kiểm danh sách mức đầy đủ cho đúng năm điểm chuẩn, rồi bật hiển thị theo gate hiện hành. Không dùng các địa điểm/phiên và tham chiếu kiểm thử để chứng minh đã duyệt dữ liệu phục vụ.

## 2. Đóng gói và HTTPS

Chuẩn bị cấu hình riêng theo [đóng gói và CI](dong-goi-va-ci.md). Build ảnh Linux, kiểm FFmpeg/ffprobe trong ảnh và đường dẫn audio Unicode, khởi động trên database/volume riêng rồi kiểm cùng origin cho frontend/API. HTTPS phải giữ cookie Secure/HttpOnly/SameSite và CSRF; không sửa thuộc tính cookie để bỏ qua lỗi môi trường.

Khi có URL HTTPS thật, cấu hình `HUEQR_PUBLIC_START_URL`, build lại ảnh và đối chiếu URL hiển thị trên trang QR. Tải/in PNG rồi quét trên Android/Chrome và iPhone/Safari từ mạng ngoài laptop. Kiểm QR mở form, chưa tự tạo phiên; bắt đầu phiên, chọn vùng, đọc/nghe, đổi tiếng, lỗi mạng/thử lại, reload/nhân bản tab, BFCache/background và hết hạn theo ma trận UI. Viewport giả lập trên Chrome không thay hai lượt thiết bị thật.

## 3. Backup và diễn tập restore

Trước khi viết/chạy công cụ backup trên môi trường phục vụ, xác định tài khoản thực thi, thư mục ngoài repository, quyền truy cập, dung lượng và lịch giữ 7 ngày theo thiết kế. Mật khẩu/khóa runtime không được chèn vào command line hoặc hồ sơ kiểm công khai.

Một bộ backup phải ghi cùng mốc database và kho audio. Ngăn tiếp nhận thao tác mới, chờ các luồng ghi/worker/READ đang chạy kết thúc hoặc đối chiếu bằng chứng tiến trình theo thiết kế; không xóa claim theo timeout. Giữ hệ thống ở trạng thái không ghi trong suốt dump và sao chép audio. Ghi schema/Flyway, phiên bản nguồn/ảnh, thời điểm UTC, manifest kích thước/Hash và kết quả từng bước. Không coi dump database đơn lẻ là backup đầy đủ khi thiếu tệp audio tương ứng.

Diễn tập restore trên schema, volume và kho audio **mới**, không ghi đè môi trường đang dùng. Kiểm Hash trước nhập, nạp database và audio đúng bộ, dùng epoch mới và thực hiện lọc dữ liệu theo anchor gốc/retention trước mở phục vụ. Không kéo dài tuổi dữ liệu bằng thời điểm restore. Kiểm tham chiếu audio, cặp đang áp dụng, quản trị/khách, thống kê, quyền file và các tình huống thiếu/hỏng tệp. Chỉ xóa bộ backup hết hạn sau khi xác minh bộ mới đầy đủ; không xóa đệ quy theo đường dẫn chưa kiểm.

Đã có [diễn tập offline ngày 11/11](../06-kiem-thu/packaging-20261111/README.md): phục hồi DB/audio riêng, kiểm Hash, lọc retention và chạy ứng dụng với epoch mới. Chưa có OPS_GUARD/drain live hoặc reconcile job/claim đang xử lý; không coi lượt offline là backup môi trường đang phục vụ hoặc bật vận hành dựa riêng vào hướng dẫn này.

## 4. CI/CD và tài khoản

CI nguồn đã chuẩn bị; cần bàn giao các file đúng nhánh/tài khoản theo G02 và chạy GitHub Actions thật. CD cần Tailscale riêng, OpenSSH Windows, SSH host key/khóa đăng nhập, quyền Docker của tài khoản đích và thư mục triển khai. Chưa cấu hình các đầu vào này thì chưa chạy triển khai.

Sau khi cấu hình, kiểm triển khai lặp lại, giữ volume qua thay ảnh, khởi động lại, rollback phiên bản tương thích schema và truy cập HTTPS từ thiết bị ngoài mạng. Phân biệt kết quả CI, đóng gói local, CD và nghiệm thu dữ liệu/thiết bị. Commit/push, PR, checklist và Project chỉ cập nhật đúng quyền đã giao và sau mốc rà.
