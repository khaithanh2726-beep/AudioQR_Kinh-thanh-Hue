# Hướng dẫn sử dụng và trình diễn

## 1. Chuẩn bị buổi trình diễn

### Khởi động nhanh bộ đã lưu trên laptop

Mở Docker Desktop, chờ Linux Engine. Trong PowerShell tại thư mục repository:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\deploy\Khoi-dong.ps1 -MoTrang
```

Script đọc runtimeDirectory từ `%USERPROFILE%\AudioQR-CD\config.json`, kiểm đúng project/container/volume và chỉ khởi động tài nguyên đã tồn tại; không build/pull, cài công cụ, tạo database hoặc cấp khóa mới. Có thể truyền `-RuntimeDirectory '<runtime-chinh>'` khi cần chỉ định rõ. Tùy chọn `-MoTrang` mở Khách/Admin sau HTTP/CSRF sẵn sàng; không tự đăng nhập hoặc mở tunnel công khai.

Mở PowerShell khác để dùng điện thoại:

```powershell
ngrok http 18083 --inspect=false
```

Giữ cửa sổ ngrok và đối chiếu Forwarding với QR. Nếu chỉ dùng trên laptop, mở `http://localhost:18083` và `http://localhost:18083/admin.html`. Không cần chạy npm/Maven riêng cho image đã đóng gói.

Khi kết thúc, xử lý bản Admin chưa lưu, cho các phiên trình diễn kết thúc, dừng ngrok bằng Ctrl+C và chạy:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\deploy\Dung.ps1
```

Script dừng dùng quiesce → drain → freeze rồi dừng database; giữ nguyên image/volume/khóa. Receipt riêng trong runtime chỉ cho lần khởi động sau mở gate FROZEN do chính script đã đóng thành công. Gate của owner khác hoặc lượt dừng chưa rõ kết quả không được tự mở. Gặp lỗi, giữ trạng thái và đối chiếu `quick-run-state.json` với gate; không xóa receipt/claim hoặc chạy lệnh resume bằng token mới để ép qua kiểm. Thiếu container/volume sau dọn phải restore theo hướng dẫn, không tự tạo lại tài nguyên rỗng.

Script không tự tắt Windows, không bảo đảm chạy khi Docker chưa sẵn sàng hoặc máy mất điện. Phép thử dừng/khởi động container khác với tắt/bật máy thật. Sau thử ở ứng dụng, dừng bằng script, tắt Windows bình thường, bật lại, chạy script khởi động và kiểm QR/đọc/nghe trên thiết bị trước buổi chấm. Bật máy trước 20–30 phút là khoảng chuẩn bị đề nghị, không phải thời gian khởi động đã cam kết. Backup vẫn giữ hạn bảy ngày; mở máy lại sau thời gian dài cần kiểm và backup mới, không chỉnh hạn manifest.

### Lịch bảo trì và thao tác thủ công

Kiểm task backup trước buổi diễn theo [hướng dẫn bảo trì](bao-tri-va-sao-luu.md#backup-theo-lịch-và-quản-lý-log). Lịch Ngọ Môn là 23:00; nếu buổi diễn đi qua giờ này, Disable-ScheduledTask để chặn lượt mới rồi bật lại sau buổi diễn. Không dừng cưỡng bức lượt backup đang chạy; chờ hoàn tất và gate RUNNING. Nếu bỏ lỡ lịch, chạy bù ngoài thời gian sử dụng.

Mở Docker Desktop và đúng môi trường đã cấu hình, dùng database/kho audio/tài khoản riêng. Bản tập dượt được ghi nhận ở cổng loopback 18083, đã bật năm điểm và 20 cặp vi/en/fr/ru. Không dùng preview hoặc dữ liệu E2E làm nội dung đã duyệt. Cấu hình, mật khẩu, epoch và khóa ký giữ ngoài Git.

```powershell
$rehearsalDirectory = '<thu-muc-runtime-tap-duot>'
$rehearsalState = Get-Content (Join-Path $rehearsalDirectory 'runtime.json') -Raw | ConvertFrom-Json
docker compose -p $rehearsalState.project --env-file $rehearsalState.configPath -f deploy/compose.yaml -f (Join-Path $rehearsalDirectory 'limits.yaml') up -d --no-build --pull never
ngrok http 18083 --inspect=false
```

Bộ tập dượt hiện ở V14, chỉ mở phục vụ khi gate RUNNING. Lệnh Compose không tự mở gate bảo trì hoặc thu hồi quyền cũ; không dùng nó thay lệnh resume có đúng owner. Không dùng script khởi động cũ chỉ chấp nhận V13. Với bộ bảo trì/restore, theo [quy trình gate](bao-tri-va-sao-luu.md). Giữ ngrok hoạt động và kiểm URL Forwarding trùng URL QR. Khi URL thay đổi cần cấu hình/build và xuất QR lại; không chỉ thay ảnh QR.

Kết thúc buổi: ngừng tiếp nhận khách, xác nhận Admin đã xử lý bản chưa lưu và không có lượt vận hành/TTS đang chạy. Dừng ngrok bằng Ctrl+C tại cửa sổ đang giữ tunnel, rồi dùng cùng project/config/compose ở trên với lệnh `stop` thay cho `up -d --no-build --pull never`. Không dùng `down -v`, xóa volume hoặc tạo lại private.env. Lần khởi động sau giữ nguyên volume/khóa/epoch; nếu gate đang đóng thì xử lý đúng owner trước phục vụ. Nếu máy không hoạt động lúc 23:00, chạy bù backup ngoài buổi diễn.

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

Bản PNG tại mốc 14/11: [tải QR Kinh thành Huế](qr-kinh-thanh-hue.png), trỏ tới `https://kiwi-situated-outboard.ngrok-free.dev/`. Laptop, Docker và ngrok phải hoạt động. Nếu URL thay đổi phải cấu hình/build/xuất QR lại, không tiếp tục dùng bản PNG cũ.

## 4. Phản hồi và xử lý lỗi

- Bản chưa lưu: chọn tiếp tục soạn hoặc bỏ thay đổi rõ ràng trước chuyển màn/đăng xuất.
- Version thay đổi: giữ bản soạn, đối chiếu hiện trạng trước dùng version mới.
- Kết quả chưa rõ: giữ yêu cầu/key, kiểm receipt trước gửi lại đúng yêu cầu.
- Thiếu nội dung/file: không tạo hồ sơ giả; đối chiếu đúng revision/Hash và quyền.
- Ngrok tắt/URL đổi: khởi động đúng bộ hoặc cập nhật cấu hình/build/QR, không sửa credential để vượt lỗi.

## 5. Giới hạn bản trình diễn

Bản phục vụ hiện có năm điểm và 20 cặp được chấp thuận dùng học phần, API văn bản/MP3 khớp Hash 20/20. Android và iPhone đã có báo dùng Ngọ Môn cơ bản; ma trận mẫu máy/OS/browser của bộ cập nhật được thực hiện ở lượt kiểm cuối. Không dùng kết quả API thay kết quả nghe trên thiết bị. [Điều kiện nghiệm thu](nghiem-thu-van-hanh.md) ghi phần cần hoàn tất.
