# Quy trình nghiệp vụ — Khách tham quan và Admin

## 1. Mục tiêu và vai trò

Khách truy cập từ một QR/URL chung, bắt đầu phiên, chọn vùng và đọc/nghe thuyết minh. Admin quản trị dữ liệu phục vụ và xem thống kê. Phạm vi gồm năm điểm chuẩn, bốn tiếng vi/en/fr/ru; có thể thêm địa điểm nhưng không bổ sung chức năng quản lý ngôn ngữ mới. Không có tài khoản Khách, định vị GPS hoặc lịch sử nghe cá nhân lâu dài.

Chi tiết yêu cầu và tiêu chí tại [PRD](PRD.md); các quy tắc D/A/P/Q và thời hạn tại [quy tắc nghiệp vụ](decisions.md). Tài liệu này trình bày luồng, không thay hồ sơ nghiệm thu phần mềm.

<a id="bf-2"></a>
## 2. Quy tắc dùng chung

| Chủ đề | Quy tắc |
|---|---|
| Khách | Phiên gắn tab, tối đa 8 giờ cố định từ bắt đầu; tên không phải định danh để tìm/khôi phục phiên |
| Admin | Phiên 30 phút từ hoạt động tương tác hợp lệ; polling/đọc nền không gia hạn |
| Nội dung | Chỉ phục vụ cặp văn bản/audio được phép, đúng revision và ngôn ngữ; nháp khác cặp đang dùng |
| Thống kê | Số phiên khác lượt xem; nghe/tua/đổi tiếng/reload không tự cộng lượt |
| Lưu trữ | Audio là file, metadata trong MySQL; xóa dữ liệu khác dọn file |
| Dữ liệu | Lịch sử phiên/lượt giữ 30 ngày theo mốc kết thúc hoặc hết hạn; không đổi anchor khi dọn/restore |

<a id="bf-3"></a>
## 3. Luồng Khách

### 3.1. Bắt đầu

1. Quét QR hoặc mở URL chung để vào form; chưa tạo phiên hoặc lượt xem.
2. Nhập tên 1–50 ký tự hiển thị hợp lệ, chọn một trong bốn tiếng, mặc định vi.
3. Bấm Bắt đầu. Hệ thống kiểm đầu vào, chống bấm/yêu cầu lặp, tạo một phiên và trả mốc bắt đầu/hết hạn của server.
4. Hiển thị bản đồ, ngôn ngữ và tên Khách. Chỉ các điểm/vùng đủ điều kiện hiển thị mới được cung cấp.

Mất phản hồi Bắt đầu phải đối chiếu/gửi lại đúng yêu cầu để nhận cùng phiên; không tạo phiên mới chỉ vì chưa nhận response. Reload/nhân bản tab phải tuân theo chủ tab, không dùng tên để lấy lại phiên hoặc tự gia hạn.

### 3.2. Chọn vùng, đọc và nghe

1. Chạm một điểm bên trong vùng hợp lệ; kéo/phóng không chọn địa điểm. Ngoài vùng không đổi bài, cùng địa điểm giữ hành vi P06.
2. Nếu chồng lấn, hiện đúng các ứng viên và Hủy. Hủy giữ bài/ghim/mốc cũ; chọn mới chỉ xử lý khi phiên còn quyền.
3. Tải cặp đang áp dụng của ngôn ngữ chọn. Văn bản tải thành công mới có ACK để ghi lượt theo lần mở; không chờ audio mới ghi lượt.
4. MP3 được tải trọn và kiểm kích thước/Hash trong tab trước phát. Chỉ một player; lựa chọn mới nhất thắng, phản hồi cũ không được thay bài.
5. Phát/Tạm dừng/Nghe lại và tua theo trạng thái còn hiệu lực. Nghe hoặc tua không tạo lượt mới.

Đi từ A sang B rồi về A là lần mở khác; bấm lại cùng điểm, đổi tiếng hoặc khôi phục cùng lần mở không tự tạo lượt mới. Cặp bị thay sau đó có hiệu lực ở lần tải/chọn phù hợp, không dùng phản hồi cũ tráo revision mới.

### 3.3. Đổi ngôn ngữ, thiếu nội dung và lỗi

Đổi tiếng yêu cầu bản tương ứng, dừng audio cũ và không tự phát nhầm bài. Thiếu bản dịch/audio được báo rõ, không tự fallback vi. Văn bản hợp lệ vẫn đọc được khi audio chưa sẵn sàng hoặc tải lỗi. Thử lại phải giữ quyền phiên và yêu cầu cần đối chiếu, không ghi trùng lượt.

Reload khi còn hiệu lực phục hồi trạng thái hợp lệ, đối chiếu revision/Hash với mốc trong tab rồi tạm dừng; bài đổi/mốc không hợp lệ về đầu và thông báo. Không có đồng bộ audio giữa thiết bị hoặc khôi phục ngoại lệ hết hạn qua reload.

### 3.4. Hết hạn và kết thúc

Cảnh báo trong 5 phút cuối không che/chặn trang hoặc ngắt audio. Hết 8 giờ, bản đồ bị khóa với box hết hạn; tên/audio/văn bản vẫn đọc được theo quyền. Chỉ bài đã tải trọn và đang phát đúng mốc được nghe hết, có thể tạm dừng/tiếp tục; không tua, nghe lại, tải bài cũ hoặc mở bài mới. Bài pause trước hạn/đang tải/chưa phát không có ngoại lệ. Mở form phiên mới dừng và giải phóng bài cũ.

Kết thúc chủ động chỉ đổi màn khi server xác nhận thành công; khóa thao tác lặp, dừng bài và xóa dữ liệu khôi phục của tab. Box kết thúc chặn toàn trang ứng dụng, chỉ có Bắt đầu phiên mới; không thêm hộp xác nhận trước đó. Mở form sau kết thúc dùng tên trống/vi. Trường hợp không thể tiếp tục phiên phải báo đúng căn cứ và yêu cầu người dùng chủ động bắt đầu mới.

<a id="bf-4"></a>
## 4. Luồng Admin

### 4.1. Đăng nhập và bảo vệ công việc

Admin đăng nhập bằng tài khoản được cấp. Nội dung đang soạn giữ tạm trong tab theo Q2, không tự lưu vào database. Chuyển màn/đăng xuất khi có phần chưa lưu phải cho tiếp tục soạn hoặc bỏ rõ ràng. Hết phiên che nội dung, dừng nghe thử và chặn ghi; cùng tài khoản đăng nhập lại có thể phục hồi theo Q3 nhưng không tự lưu/gửi lại/phát lại. Đổi tài khoản không lộ công việc của tài khoản cũ.

### 4.2. Địa điểm, vùng và hiển thị

Admin thêm/sửa thông tin bằng định danh độc lập tên; tên trùng không gộp địa điểm. Với vùng: vẽ/sửa → xem trước → lưu revision → chủ động áp dụng đúng bản đã xem/lưu. Nháp không làm đổi vùng đang áp dụng; hình không đổi sau lưu không bắt xem trước lại.

Chỉ bật hiện khi thông tin hợp lệ, có vùng hợp lệ đã xem xét/áp dụng và ít nhất cặp vi đủ hồ sơ/file. Áp dụng cặp/vùng không tự bật hiện; ẩn không xóa dữ liệu hoặc đặt lại hạn bảo vệ file như ngừng áp dụng.

### 4.3. Văn bản, TTS và cặp nội dung

1. Chọn địa điểm/ngôn ngữ, soạn và lưu revision văn bản. Sửa nháp không thay cặp đang phục vụ.
2. Tạo audio từ bản đã lưu; tác vụ giữ snapshot text/hash/ngôn ngữ, độc lập việc Admin đóng tab hoặc hết phiên.
3. Theo dõi trạng thái. Kết quả chưa rõ chỉ đối chiếu bằng Check; Retry phải có căn cứ lỗi và version đúng. Lỗi local có WAV hợp lệ dùng lại WAV, không tự gọi provider mới.
4. Nghe thử/nghe trọn và ghi tham chiếu/giới hạn của văn bản/audio theo D09. Ghi tham chiếu không phải phần mềm tự thẩm định.
5. Áp dụng cả cặp đúng revision/lang/hash/ref version. Lỗi giữ cặp cũ, không có trạng thái nửa cặp; thao tác chưa rõ phải tra receipt trước thử lại.

TTS thành công khác duyệt, khác áp dụng và khác hiển thị. Danh sách mức đầy đủ đối chiếu năm điểm/bốn tiếng; thiếu một bản không tự phục vụ bản khác. Bộ demo học phần dùng bản dịch chưa thẩm định phải được duyệt rõ từng bản cùng giới hạn; AI không thay chuyên gia.

### 4.4. Xóa, lịch sử và dọn file

Xóa địa điểm có xác nhận, thao tác trong giao dịch theo thứ tự tham chiếu, giữ dữ liệu dùng chung và snapshot lịch sử. Liên kết TTS bị vô hiệu để kết quả muộn không tạo lại địa điểm/nội dung đã xóa. Hồ sơ duyệt bên ngoài không bị xóa cùng dữ liệu riêng.

Sau giao dịch, file chỉ được dọn khi không còn tham chiếu/claim và đủ điều kiện. File từng áp dụng được bảo vệ 8 giờ từ lần ngừng áp dụng cuối; file hoàn chỉnh chưa áp dụng và file tạm chịu chính sách 24 giờ/1 giờ tương ứng. Lỗi unlink giữ trạng thái để kiểm/thử lại, không báo đã xóa khi mới xếp hàng. Lease/claim không được tự xóa chỉ vì lâu.

### 4.5. Phiên khách, thống kê và QR

Admin đọc danh sách/chi tiết phiên và lượt còn trong hạn lưu, không kết thúc phiên Khách thay họ. Thống kê tách số phiên theo ngày bắt đầu với lượt xem theo ngày xem; nhóm theo địa điểm/ngôn ngữ và dùng Asia/Saigon. Điểm đã xóa/đổi tên vẫn nhận diện bằng ID/snapshot, không gộp các điểm trùng tên.

QR chung dùng URL HTTPS cấu hình chỉ đọc, có tải PNG/bản in và URL dự phòng. Khi chưa có URL hợp lệ thì chưa sẵn sàng; không tạo QR giả hoặc QR riêng cho từng điểm.

## 5. Sao lưu và phục hồi

Tạm ngừng tiếp nhận việc và drain trước checkpoint DB/audio. Snapshot có Hash, nguồn ảnh/schema và hạn bảy ngày; restore sang bộ mới, lọc theo mốc gốc, cấp epoch mới và không kéo dài tuổi phiên/file. Không coi restart thường là restore hoặc giả endedAt cho lịch sử. Cơ chế kỹ thuật tại [thiết kế vận hành](../03-ky-thuat/operations-design.md).

## 6. Trạng thái nghiệm thu

Các quy trình trên là phạm vi yêu cầu. [Hiện trạng bàn giao](../01-tong-quan/handoff.md) và [báo cáo kiểm](../06-kiem-thu/README.md) ghi phần đã triển khai/kiểm, bộ dữ liệu còn thiếu và điều kiện vận hành chưa đạt.


<a id="bf-1"></a>
<a id="bf-guest-main"></a>
<a id="bf-guest-audio"></a>
<a id="bf-warning"></a>
<a id="bf-guest-errors"></a>
<a id="bf-expiry-audio"></a>
<a id="bf-admin-main"></a>
<a id="bf-admin-delete"></a>
<a id="bf-admin-audio"></a>
<a id="bf-admin-errors"></a>
<a id="bf-admin-q"></a>
<a id="bf-5"></a>
<a id="bf-6"></a>
<a id="bf-7"></a>
<a id="bf-8"></a>
