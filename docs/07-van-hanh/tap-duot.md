# Hướng dẫn sử dụng và trình diễn

## 1. Chuẩn bị buổi trình diễn

Mở Docker Desktop và đúng môi trường đã cấu hình, dùng database/kho audio/tài khoản riêng. Bản tập dượt được ghi nhận ở cổng loopback 18083, chỉ bật Ngọ Môn vi. Không dùng preview hoặc dữ liệu E2E làm nội dung đã duyệt. Cấu hình, mật khẩu, epoch và khóa ký giữ ngoài Git.

```powershell
$rehearsalDirectory = '<thu-muc-runtime-tap-duot>'
$rehearsalState = Get-Content (Join-Path $rehearsalDirectory 'runtime.json') -Raw | ConvertFrom-Json
docker compose -p $rehearsalState.project --env-file $rehearsalState.configPath -f deploy/compose.yaml -f (Join-Path $rehearsalDirectory 'limits.yaml') up -d --no-build --pull never
ngrok http 18083 --inspect=false
```

Bộ tập dượt hiện ở V14, chỉ mở phục vụ khi gate RUNNING. Lệnh Compose không tự mở gate bảo trì hoặc thu hồi quyền cũ; không dùng nó thay lệnh resume có đúng owner. Không dùng script khởi động cũ chỉ chấp nhận V13. Với bộ bảo trì/restore, theo [quy trình gate](bao-tri-va-sao-luu.md). Giữ ngrok hoạt động và kiểm URL Forwarding trùng URL QR. Khi URL thay đổi cần cấu hình/build và xuất QR lại; không chỉ thay ảnh QR.

## 2. Khách tham quan

1. Quét QR chung bằng Camera, mở URL HTTPS; nếu ngrok hiện trang trung gian thì chọn Visit Site.
2. Nhập tên hợp lệ, chọn vi/en/fr/ru, bấm Bắt đầu tham quan. Quét QR/mở form chưa tự tạo phiên.
3. Chạm trong vùng trên bản đồ; kéo/phóng không chọn điểm. Nếu có nhiều ứng viên, chọn đúng điểm hoặc Hủy.
4. Đọc tên/văn bản; audio chỉ phát khi đã tải trọn và kiểm Hash. Dùng Phát/Tạm dừng/Nghe lại và thanh tua theo quyền phiên. Nếu autoplay bị chặn, chủ động bấm Phát.
5. Đổi ngôn ngữ để yêu cầu bản tương ứng; thiếu bản dịch/audio được báo rõ, không tự chuyển sang vi hoặc phát bài khác.
6. Bấm Kết thúc tham quan để kết thúc phiên. Phiên tối đa 8 giờ cố định, có cảnh báo 5 phút cuối; nghe không tự gia hạn.

Khi hết hạn, chỉ bài đã tải trọn và đang phát đúng mốc được phép nghe tiếp/tạm dừng-tiếp tục; không tua/nghe lại/mở bài mới. Reload/đóng tab không khôi phục ngoại lệ. Lỗi mạng/audio có nút thử lại theo quyền; giữ cùng thao tác khi kết quả chưa rõ, không bắt đầu lại để ghi trùng lượt.

## 3. Admin

Đăng nhập tại `/admin.html` bằng tài khoản được cấp riêng. Phiên Admin hết hạn sau 30 phút không có hoạt động tương tác hợp lệ; polling không gia hạn. Hết phiên che nội dung/dừng nghe thử, đăng nhập cùng tài khoản để phục hồi bản soạn trong tab, không tự lưu/gửi lại/phát lại.

### Địa điểm và vùng

Thêm/sửa tên địa điểm, đọc điều kiện hiển thị hoặc xác nhận xóa. Xóa dữ liệu riêng khác dọn file; lịch sử và tài nguyên dùng chung chịu chính sách riêng. Với vùng: vẽ/sửa → Xem trước → Lưu bản sửa → Áp dụng đúng revision đã xem. Hình đang sửa không tự thay vùng áp dụng.

### Nội dung, TTS và cặp

Chọn địa điểm/ngôn ngữ, soạn và Lưu nháp. Tạo audio chỉ từ revision đã lưu; theo dõi phase, dùng Check/Retry đúng hướng dẫn của kết quả. Chưa rõ kết quả thì kiểm tra, không tạo khóa/yêu cầu mới để gọi provider lặp. Lỗi local có WAV hợp lệ có thể dùng Retry xử lý local; không đồng nghĩa phải gọi Gemini lại.

Chọn đúng audio để nghe thử, nghe trọn và ghi tham chiếu/giới hạn của văn bản/audio theo hồ sơ thực. Áp dụng cả cặp sau khi đủ điều kiện; tạo TTS/ghi tham chiếu không tự duyệt, áp dụng hoặc bật hiển thị. Chỉ bật điểm khi có thông tin/vùng áp dụng hợp lệ và cặp vi đủ điều kiện. Mức đầy đủ giúp đối chiếu bộ năm điểm/bốn tiếng.

### Phiên, thống kê và QR

Danh sách/chi tiết phiên chỉ đọc; lọc theo trường được hỗ trợ. Thống kê phân biệt số phiên với lượt xem, nhóm theo ngày/địa điểm/ngôn ngữ theo Asia/Saigon. Không cộng lượt do phát/tua/đổi tiếng/reload. Khi đổi bộ lọc, kết quả cũ được ghi nhãn tới khi tải lại.

QR chung dùng URL HTTPS cấu hình chỉ đọc; tải PNG hoặc xem bản in gọn. Kiểm đúng URL và form mở được từ thiết bị ngoài laptop trước buổi trình diễn. Token ngrok và mật khẩu Admin không được in lên QR hoặc chia sẻ cho người xem.

## 4. Phản hồi và xử lý lỗi

- Bản chưa lưu: chọn tiếp tục soạn hoặc bỏ thay đổi rõ ràng trước chuyển màn/đăng xuất.
- Version thay đổi: giữ bản soạn, đối chiếu hiện trạng trước dùng version mới.
- Kết quả chưa rõ: giữ yêu cầu/key, kiểm receipt trước gửi lại đúng yêu cầu.
- Thiếu nội dung/file: không tạo hồ sơ giả; đối chiếu đúng revision/Hash và quyền.
- Ngrok tắt/URL đổi: khởi động đúng bộ hoặc cập nhật cấu hình/build/QR, không sửa credential để vượt lỗi.

## 5. Giới hạn bản trình diễn

Bản phục vụ hiện chỉ có Ngọ Môn vi đã được chấp thuận tập dượt. Android và iPhone đã có báo dùng cơ bản; chưa thay kiểm bộ 20 cặp, toàn ma trận thiết bị/lỗi hoặc vận hành chính thức. [Điều kiện nghiệm thu](nghiem-thu-van-hanh.md) ghi phần cần hoàn tất.
