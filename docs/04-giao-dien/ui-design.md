# Thiết kế giao diện Khách và Admin

## 1. Phạm vi và nguyên tắc trình bày

Giao diện gồm trang Khách `/` và Admin `/admin.html`. Thiết kế dùng tiếng Việt mặc định, hỗ trợ vi/en/fr/ru cho Khách, phông sans-serif hỗ trợ Unicode. Mẫu Khách dùng đầu/tiêu đề đỏ sẫm, nền sáng và thẻ trắng. Không thêm dashboard, quản trị tài khoản/ngôn ngữ mới, GPS, xóa hàng loạt hoặc tính năng ngoài PRD.

[PRD](../02-nghiep-vu/PRD.md), [Business Flow](../02-nghiep-vu/business-flow.md) và [quy tắc D/A/P/Q/UI](../02-nghiep-vu/decisions.md) là nguồn hành vi. Các mã UI được giữ để đối chiếu; không dùng hình minh họa hoặc màn hình kỹ thuật làm hồ sơ duyệt nội dung.

<a id="ui-screens"></a>
## 2. Màn hình Khách

| Mã | Màn hình | Nội dung và hành vi |
|---|---|---|
| UI-K01 | Bắt đầu | Tên, bốn ngôn ngữ mặc định vi, nút Bắt đầu; phản hồi kiểm đầu vào/tải/lỗi, khóa bấm lặp |
| UI-K02 | Tham quan | Bản đồ → tên điểm → điều khiển audio → box văn bản cuộn; tên Khách/ngôn ngữ/Kết thúc dễ tìm |
| UI-K03 | Bài đang mở | Một bài/MP3 trong tab; văn bản còn đọc khi audio lỗi; trạng thái tải/thiếu bản/thử lại rõ ràng |
| UI-K04 | Vùng chồng lấn | Box nhỏ đúng ứng viên và Hủy, nền khóa; danh sách cuộn không che Hủy; giữ bài cũ tới khi chọn hợp lệ |
| UI-K05 | Hết hạn | Box chỉ trong bản đồ, không đóng/Hủy; bản đồ mờ-khóa, ngôn ngữ khóa, bài vẫn cuộn đọc và audio theo ngoại lệ |
| UI-K06 | Đã kết thúc | Box chặn toàn trang sau kết thúc thành công; một nút Bắt đầu phiên mới, không đóng bằng Esc/ngoài |

### 2.1. Bố cục tham quan

Giữ thứ tự bản đồ, tên điểm, audio và văn bản. Box văn bản cuộn riêng; cuộn bài không dịch bản đồ hoặc phát sinh lần mở/ACK mới. Nút Kết thúc và Hủy chồng lấn vẫn tiếp cận được trên viewport nhỏ. Không bổ sung dòng/nút báo đã phát hết hoặc popup cuối bài ngoài quy định.

### 2.2. Bản đồ và vùng

Leaflet hiển thị nền raster OpenStreetMap với attribution. Vùng của bản Khách lấy từ API, không tự nạp các vùng đề xuất để bật điểm. Chạm bên trong toàn polygon để chọn; ngoài vùng không đổi bài; kéo/phóng không chọn; bấm lại cùng điểm theo P06; lựa chọn mới nhất thắng khi chọn liên tục.

Một ghim xanh đặt đúng tọa độ chạm hợp lệ, không tự đặt vào tâm vùng. Chọn mới dời ghim, ngoài vùng/kéo map giữ vị trí địa lý. Chồng lấn chỉ đổi ghim khi chọn ứng viên; Hủy giữ ghim/bài/mốc cũ. Ghim không cản thao tác hoặc thể hiện GPS. Vùng đổi/xóa không còn chứa ghim thì bỏ ghim.

Nền có trạng thái tải/lỗi và thử lại riêng; thử lại nền không mở lại bài hoặc cộng lượt. Preview phục vụ rà vùng là trang riêng, không tạo phiên và không có trong build thường. [Tài sản thiết kế](design-assets/README.md) giữ nguồn, attribution và sơ đồ năm vùng.

### 2.3. Audio, đổi tiếng và hết hạn

| Trạng thái | Điều khiển/hiển thị |
|---|---|
| Đang tải | Văn bản hợp lệ vẫn đọc; chưa phát bytes chưa đủ/Hash sai |
| Đang phát | Nút Tạm dừng; tua hợp lệ tiếp tục từ mốc mới khi sẵn sàng |
| Đang tạm dừng | Nút Phát; tua giữ pause |
| Hết bài khi còn hạn | Cùng nút thành Nghe lại; tua về trước cuối giữ pause/nút Phát; không cộng lượt |
| Audio lỗi | Thông báo và thử lại khi còn quyền; không mất văn bản hoặc phát bài khác |
| Đổi ngôn ngữ | Dừng bài cũ, yêu cầu đúng bản mới, không autoplay/fallback vi; thiếu bản/audio báo rõ |
| Tải lại khi còn hạn | Đối chiếu revision/Hash, phục hồi mốc hợp lệ rồi pause; bài đổi/mất mốc về đầu và thông báo |
| Còn tối đa 5 phút | Dải “Phiên tham quan sẽ hết hạn trong vòng 5 phút”, không chặn hoặc ngắt bài; không lặp vô ích sau quay tab |
| Hết hạn | Chỉ bài tải trọn và đang phát đúng mốc được pause/resume tới hết; khóa tua/nghe lại/mở bài mới |

Bài pause trước hạn/đang tải/chưa phát hoặc response đến muộn không được ngoại lệ. Reload/đóng tab không phục hồi ngoại lệ. Form mới dừng/giải phóng bài cũ. Sau kết thúc chủ động, form mới tên trống/vi; trường hợp quay lại sau hết hạn có thể điền sẵn thông tin còn hợp lệ nhưng phải xác nhận Bắt đầu mới.

Câu không thể tiếp tục: “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”. Không khẳng định hết hạn hoặc mất dữ liệu nếu chưa có căn cứ. Kết thúc chỉ chuyển UI-K06 khi có phản hồi thành công, không thêm bước xác nhận trước nút Kết thúc.

<a id="ui-admin-round1"></a>
## 3. Màn hình Admin

| Mã | Màn hình/thao tác | Thiết kế chính |
|---|---|---|
| UI-A01 | Đăng nhập | Tài khoản/mật khẩu, trạng thái chờ/lỗi, phản hồi chung không lộ tài khoản |
| UI-A02 | Khung/đăng xuất | Điều hướng chức năng, trạng thái phiên và bảo vệ bản soạn |
| UI-A03 | Danh sách điểm | ID/tên, ẩn-hiện, vùng áp dụng; tải/rỗng/lỗi và tải lại |
| UI-A04 | Thêm/sửa điểm | Tên theo Q1, phản hồi trường/ghi, ID độc lập tên, ETag/đối chiếu xung đột |
| UI-A05 | Điều kiện hiển thị | Liệt kê điều kiện còn thiếu, bật/ẩn rõ trạng thái; áp dụng không tự bật |
| UI-A06 | Xác nhận xóa | Nêu đúng điểm/ID và dữ liệu riêng; Hủy hoặc Xóa, khóa nền |
| UI-A07 | Vẽ/sửa vùng | Trang riêng, phân biệt vùng đang dùng với hình sửa; vẽ/đóng/sửa đỉnh/di chuyển |
| UI-A08 | Xem trước/lưu vùng | Xem hình hợp lệ, lưu revision riêng; hình chưa lưu không áp dụng |
| UI-A09 | Áp dụng vùng | Đúng savedRevision/Hash đã xem; hình không đổi sau lưu không bắt xem lại |
| UI-A10 | Hết phiên | Che dữ liệu và dừng nghe thử, yêu cầu đăng nhập lại |
| UI-A11 | Bản chưa lưu | Tiếp tục soạn hoặc bỏ rõ ràng khi chuyển/đăng xuất; không tự gửi |
| UI-A12 | Kết quả xóa/file | Xóa DB thành công khác file đang chờ/đã dọn; không dùng chờ như đã xóa |
| UI-A13 | Chọn nội dung | Địa điểm/ngôn ngữ, phân biệt nháp với cặp đang phục vụ |
| UI-A14 | Soạn/lưu văn bản | Revision đã lưu và bản đang soạn riêng, giữ nháp khi lỗi/version đổi |
| UI-A15 | Hồ sơ cặp | Tham chiếu/giới hạn văn bản và audio riêng, gắn đúng bản đã lưu |
| UI-A16 | Tạo/theo dõi TTS | Tạo từ saved text, phase và hướng Check/Retry theo căn cứ |
| UI-A17 | Nghe thử | Chọn đúng audio của revision, kiểm Hash/tải, dừng khi hết phiên/đổi đối tượng |
| UI-A18 | Áp dụng cặp | Một nút Áp dụng văn bản và audio, điều kiện thiếu rõ ràng, không nửa cặp |
| UI-A19 | Mức đầy đủ | Năm điểm × bốn tiếng, tách nháp/audio/ref/cặp, không suy có file là đã duyệt |
| UI-A20 | Danh sách phiên | Chỉ đọc, bộ lọc và paging theo hợp đồng, không kết thúc phiên thay Khách |
| UI-A21 | Chi tiết phiên | Dialog đúng ID, lịch sử trong hạn; đóng về danh sách giữ bộ lọc/vị trí hợp lệ |
| UI-A22 | Thống kê | Hai loại số đếm/hai bảng nhóm, filter ngày Asia/Saigon, nhãn kết quả cũ khi đổi filter |
| UI-A23 | QR chung | URL HTTPS readonly, tải PNG và bản in gọn/URL dự phòng; chưa cấu hình thì chưa sẵn sàng |

### 3.1. Quy tắc thao tác và bản soạn

Mọi thao tác có trạng thái xử lý/thành công/lỗi/chưa rõ; khóa bấm lặp. Kết quả chưa rõ giữ key/payload/version và có Kiểm tra kết quả trước gửi lại đúng yêu cầu. Thay version không âm thầm ghi đè: giữ phần soạn, cho đối chiếu hiện trạng và chủ động chọn cách tiếp tục.

Q2 giữ tạm trong tab, khác Lưu nháp vào hệ thống. Khi chuyển màn/đăng xuất có phần chưa lưu phải cho quay lại soạn hoặc bỏ rõ ràng. Khi phiên 30 phút hết hạn, khóa ghi, che công việc và dừng nghe thử; cùng tài khoản mới được phục hồi phần phù hợp, không tự lưu/gửi lại/phát lại. Đổi tài khoản loại công việc trước; đọc nền/polling không gia hạn Q3.

Vùng đang dùng và nháp sửa hiển thị khác nhau. Lưu chỉ tạo revision, Áp dụng thay con trỏ đang dùng. Preview, saved revision và geometryHash phải khớp; thay hình làm mất preview cũ. Không cần thao tác xem lại nếu hình vừa lưu giữ nguyên.

<a id="ui-admin-round2"></a>
### 3.2. Văn bản, hồ sơ, TTS và áp dụng

Ba chiều trạng thái được tách: nháp đã lưu/đang soạn, audio kỹ thuật và cặp đang áp dụng. Tạo xong không tự duyệt hoặc áp dụng; ghi ref không tự thẩm định. Hồ sơ có tham chiếu và giới hạn, không bắt nhập lại người/ngày nếu hồ sơ đã có. Audio được chọn phải đúng text/lang/hash; sửa text bỏ liên kết chọn audio cũ.

TTS accepted vẫn chạy độc lập phiên Admin. Chưa rõ kết quả có Check; Retry theo retryAction/phaseVersion, không tự gửi provider lần hai. Được nghe nháp đúng bản trước áp dụng; quản lý Blob/late result và dừng nghe khi lock/đổi bản. Hết phiên Admin không có ngoại lệ nghe tiếp như Khách.

Chỉ có một thao tác áp dụng nguyên cặp, kèm hai revision/Hash/ref version và slot ETag. Thiếu hồ sơ/file hoặc version đổi phải giữ cặp cũ và nêu điều kiện, không áp nửa cặp. Cặp đang phục vụ chỉ đọc; soạn nháp không tự làm đổi nội dung Khách.

<a id="ui-admin-round3"></a>
### 3.3. Thống kê, dialog và QR

Số phiên tính theo ngày bắt đầu phiên, lượt xem theo ngày xem; hai tổng và bảng nhóm không được cộng lẫn. Địa điểm nhận diện bằng ID/snapshot, không gộp vì trùng tên hoặc chia nhóm chỉ do đổi tên. UI-A21 chỉ đọc, giữ ngữ cảnh bộ lọc khi đóng. Các bản ghi quá hạn D08 không được thống kê như còn giữ.

QR mở trang bắt đầu, không tự tạo phiên. URL readonly do cấu hình, một QR chung cho toàn hệ thống. Tải PNG/in gọn kèm URL dự phòng; không in thông tin đăng nhập. Chưa URL thật phải báo chưa sẵn sàng, không dùng ảnh QR giả. Nút In tạo bản in, không được báo đã in giấy chỉ vì đã gọi hộp thoại.

## 4. Phạm vi xác nhận và giới hạn kiểm

Các hướng UI-01–UI-05 và UI-AP01, UI-AP02, UI-AP03, UI-AP04, UI-AP05, UI-AP06, UI-AP07, UI-AP08, UI-AP09, UI-AP10 được giữ theo nguồn quy tắc. Hành vi không nêu không được tự suy rộng, ví dụ cách đóng box chồng lấn bằng Esc/chạm ngoài hoặc chức năng bản đồ nâng cao.

Năm vùng đã có hồ sơ chọn chạm dùng demo, không phải ranh giới di tích được khảo sát. Bản phục vụ mới Ngọ Môn vi; bốn vùng khác áp dụng trên bản riêng. Android/iPhone có xác nhận cơ bản, chưa đủ OS/browser/mạng và ma trận lỗi/time/tab. Tile/API fixture chỉ chứng minh phần tương ứng, không thay phép kiểm nền thật hoặc 20 cặp. [Báo cáo kiểm](../06-kiem-thu/README.md) và [hiện trạng](../01-tong-quan/handoff.md) ghi kết quả thực tế.


<a id="ui-flow"></a>
<a id="ui-layout"></a>
<a id="ui-reference-red"></a>
<a id="ui-interactions"></a>
<a id="ui-states"></a>
<a id="ui-proposals"></a>
<a id="ui-map"></a>
<a id="ui-trace"></a>
<a id="ui-next-technical"></a>
<a id="ui-guest-source-result"></a>
<a id="ui-history"></a>
