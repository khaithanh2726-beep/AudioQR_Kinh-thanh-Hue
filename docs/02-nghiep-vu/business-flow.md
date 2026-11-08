# Business Flow — Khách tham quan và Admin

**Mục đích:** mô tả cách vận hành của hai vai trò. Soạn 29/10/2026; biên tập 05/11/2026.
**Trạng thái:** luồng khách, Admin và Business Flow hai vai trò đã duyệt trong phạm vi nghiệp vụ theo BF-K01/BF-A01/BF-ALL01; các bổ sung UI/TECH/DATA áp dụng đúng phần tại [decisions](decisions.md).
**Giới hạn:** không phê duyệt toàn bộ giao diện/ranh giới, nội dung/bản dịch/audio, TDD hoặc kết quả phần mềm. Phần chờ thiết kế/rà soát ở mục 6; không mở lại quyết định đã chốt. [PRD](PRD.md) giữ yêu cầu và tiêu chí truy vết.

**Mục lục:** [1. Mục tiêu/phạm vi](#bf-1) · [2. Vai trò/quy tắc chung](#bf-2) · [3. Khách](#bf-3) · [4. Admin](#bf-4) · [5. Liên quan hai vai trò](#bf-5) · [6. Chờ thiết kế/rà soát](#bf-6) · [7. Tài liệu tiếp theo](#bf-7) · [8. Nhật ký](#bf-8).

<a id="bf-1"></a>

## 1. Mục tiêu, phạm vi và trạng thái duyệt

### 1.1. Mục tiêu và cách đọc

Khách nhập tên/chọn ngôn ngữ, bắt đầu phiên của tab, chọn vùng trên bản đồ minh họa để đọc và yêu cầu phát audio được phép sử dụng. Admin chuẩn bị địa điểm/vùng, văn bản/bản dịch/audio, quản lý QR chung và theo dõi phiên/thống kê; có cách xử lý rõ cho thiếu nội dung, lỗi và thay đổi dữ liệu.

Theo **G01**, đồ án phải bảo đảm nhất quán nghiệp vụ, trải nghiệm rõ, kiểm thử được, khối lượng phù hợp nhóm. Phạm vi và tiêu chuẩn nghiệm thu áp dụng thống nhất cho dự án; không tự mở rộng chức năng nâng cao.

Đây là mô tả **vận hành hiện hành**. Căn cứ chi tiết, vị trí chính từng quyết định và toàn bộ lịch sử xem [decisions.md](decisions.md#dec-2); phần lịch sử không còn chen giữa hai luồng. Mã K/E/AD/AE giữ nguyên để tra cứu.

### 1.2. Phạm vi hiện hành

- Khách vào qua QR chung hoặc đường dẫn trang bắt đầu in kèm QR.
- Chọn địa điểm bằng **các vùng tương tác** trên bản đồ minh họa dùng được trên điện thoại. Chạm bất kỳ vị trí bên trong vùng để chọn, hiển thị văn bản và yêu cầu phát audio đúng ngôn ngữ khi sẵn sàng; không phải chạm đúng biểu tượng/tên. Không chỉ đường thực tế, không lấy vị trí trực tiếp, GPS hay quyền vị trí; không quy định Google Maps hoặc công nghệ bản đồ cụ thể.
- Tên bắt buộc, 1–50 ký tự hiển thị, cho phép trùng và không xác minh tên thật; nhận diện phiên độc lập với tên.
- Bốn ngôn ngữ vi/en/fr/ru; chọn sẵn vi trước khi bắt đầu, khách có thể đổi.
- Mỗi tab một phiên; tải lại tab tiếp tục phiên. Phiên hết hạn đúng 8 tiếng kể từ bắt đầu, không gia hạn theo hoạt động. D04-BS: khi còn trên 0 đến 5 phút và phiên còn hiệu lực, có cảnh báo nhẹ theo mục 3.3.
- **Bộ dữ liệu nghiệm thu** gồm năm địa điểm, mỗi địa điểm một vùng: **Cổng Ngọ Môn; Điện Thái Hòa; Tử Cấm Thành; Cửu Đỉnh; Cung Diên Thọ**. Hệ thống có khả năng thêm địa điểm, không bị giới hạn luôn đúng năm điểm/vùng. Vùng không bắt buộc tròn. Admin được vẽ/xem trước/lưu, sửa/vẽ lại vùng theo A02.4; bố cục/thao tác vẽ/ranh giới cụ thể xem xét riêng. Điều kiện A02.5/UI-04 hiện hành: mặc định ẩn, chỉ bật khi thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng và ít nhất cặp văn bản/audio vi đã duyệt/áp dụng. Tử Cấm Thành vẫn là một bài tổng quan, không chia điểm con.
- Mục tiêu nghiệm thu: đủ 20 bản văn bản và 20 audio cho 5 điểm × 4 ngôn ngữ, đúng quy trình duyệt.
- Admin đã đăng nhập xem danh sách/thống kê; dữ liệu phiên và lượt xem liên quan tự động xóa sau 30 ngày kể từ kết thúc/hết hạn. Luồng Admin đã được duyệt tổng thể, xem mục 4.

### 1.3. Phạm vi được duyệt và cơ sở nghiệm thu

| Phần | Trạng thái |
| --- | --- |
| Khách: luồng chính/ngoại lệ, phiên, vùng/audio, cảnh báo và cách đếm | **Đã duyệt tổng thể** theo BF-K01 |
| Admin: 12 nhóm công việc đã tổng hợp và A02.6 | **Đã duyệt tổng thể** theo BF-A01; Q1–Q3 bổ sung đã chốt 31/10/2026, các nhánh AD/mục 4.5 đồng bộ đúng phần liên quan |
| Business Flow hai vai trò trong phạm vi thống nhất | **Đã duyệt** theo BF-ALL01 |
| Giao diện/thao tác vẽ/hình dạng/ranh giới vùng | Hướng UI-01–UI-05 đã chốt đúng phạm vi, gồm áp dụng cặp/gate cặp vi và dừng nghe thử Admin khi hết phiên; thiết kế cụ thể/kích thước/đồ họa, nền/vị trí/ranh giới và phần mở khác chưa duyệt |
| Nội dung lịch sử, bản dịch, audio | Không được duyệt hoặc chứng minh kiểm chứng qua duyệt luồng; năm bài vi chờ cung cấp/rà soát |
| TTS: nhà cung cấp/model/giọng và phần còn kiểm | Gemini/Charon TTS-01 và WAV-MP3/FFmpeg-Linux IN-P03 đã chọn; ngân sách trả phí/ngưỡng/tích hợp và chất lượng đủ vi/en/fr/ru chưa chốt/kiểm chứng |
| Thiết kế kỹ thuật, triển khai, kết quả kiểm thử | Công nghệ nền chọn riêng theo TECH-01; TDD/tương thích/triển khai/kiểm thử chưa hoàn tất hoặc có bằng chứng qua tài liệu |
| PRD, Use Case và tiêu chí nghiệm thu | **Phạm vi yêu cầu đã rõ của PRD 0.4.1 được duyệt** theo PRD-01; không duyệt ngầm mục còn mở/đề xuất/chưa kiểm chứng |

Năm địa điểm và bốn ngôn ngữ là **bộ nội dung người dùng chọn**, không phải trần số lượng do giảng viên quy định. Giữ 20 văn bản và 20 audio cho bộ này, theo D09; thêm điểm không tự tăng bài chuẩn hoặc thêm chức năng quản lý ngôn ngữ. Tử Cấm Thành là một vùng và một bài tổng quan mỗi ngôn ngữ, không chia điểm con (P05). Thứ tự chuẩn bị một điểm vi → năm điểm vi → ngôn ngữ còn lại không giảm phạm vi nghiệm thu (D06).

Cần có bằng chứng tương ứng về CRUD/vẽ-xem trước-lưu-sửa-áp dụng vùng, điều kiện hiện điểm, xóa/hoàn tác/giữ lịch sử/dọn file, tạo/nghe thử/áp dụng audio và các nhánh lỗi khi triển khai/kiểm thử sau. Đây là yêu cầu cần chứng minh, không phải kết quả đã đạt.

<a id="bf-2"></a>

## 2. Vai trò và quy tắc dùng chung

### 2.1. Vai trò, thời hạn và dữ liệu

| Vai trò / quy tắc | Nội dung hiện hành | Nguồn |
| --- | --- | --- |
| Khách | Dùng tên hiển thị, không xác minh tên thật; phiên độc lập với tên, không cần trang lịch sử phiên. | D02/BS, D08 |
| Admin | Một vai trò, tài khoản cấp trước, đăng nhập/đăng xuất; chưa đăng ký công khai/phân quyền nhiều cấp. | A01 |
| Người dùng/chủ đồ án | Duyệt nội dung rõ từng bản bằng tài liệu; Admin đối chiếu và ghi tham chiếu, phần mềm không tự biết phê duyệt bên ngoài. | D09, A03 |
| Phiên khách | Mỗi tab một phiên; **8 tiếng cố định từ bắt đầu**, không gia hạn bởi hoạt động hoặc nghe tiếp bài đang phát theo P01/UI-02. Đóng trang không cho biết chính xác thời điểm kết thúc. | D04, P01–P03 |
| Phiên Admin | **30 phút không hoạt động** từ hoạt động chủ động gần nhất được ghi nhận khi còn hiệu lực theo Q3; độc lập phiên khách. Giữ tạm/khôi phục theo Q2. | A01, Q2/Q3 |
| Dữ liệu phiên/lượt | Tự xóa sau **30 ngày từ kết thúc chủ động hoặc hết hạn**, thống kê chỉ phần còn giữ; không lấy mốc phát hiện tác vụ dọn, mốc hoạt động cuối hoặc lúc audio phát hết thay mốc hết hạn. | D08, A06 |
| File audio ngừng áp dụng | Bảo vệ **8 tiếng từ ngừng áp dụng cuối cùng**, tách thời hạn phiên và thời hạn dữ liệu; dọn riêng sau database thành công và khi không còn tham chiếu/dùng chung. | A02.3 |

Dữ liệu nghiệp vụ tối thiểu, quyền xem và phần không lưu được mô tả tại [D08](decisions.md#d08); nhận biết địa điểm đã xóa theo [A02.2](decisions.md#a02-2). Không đưa tọa độ khách, điện thoại, email hoặc lịch sử từng thao tác audio vào dữ liệu nghiệp vụ cần lưu. Log hạ tầng/bản sao lưu chưa được giải quyết bởi hạn lưu này.

### 2.2. Quy tắc lượt xem và số phiên

**Đã chốt D04:**

- Mỗi lần bắt đầu thành công tạo một phiên của tab, phân biệt độc lập với tên. Đổi điểm/ngôn ngữ, tải lại không tạo phiên mới.
- Không đồng nhất số phiên với số người duy nhất.
- Chủ động mở địa điểm và tải thành công văn bản thì tính một lượt. Chỉ gặp thông báo thiếu bản dịch chưa phải lượt xem văn bản thành công.
- Tải lại, đổi ngôn ngữ, nghe lại hoặc bấm lại vùng đang chọn trong cùng lần mở không cộng lượt (D04, P03, P06). Chuyển A → B → A thì lần trở lại A là lượt mới. Theo quy tắc lượt xem đã đề xuất và được người dùng đồng ý, quay lại bản đồ rồi chủ động mở lại cũng là lần mở mới; thử lại cùng lần mở không cộng lặp.
- Ví dụ: trong một phiên, mở Cổng Ngọ Môn, đổi vi sang en, tải lại và nghe lại vẫn là 1 lượt. Sau đó mở Điện Thái Hòa rồi quay lại Cổng Ngọ Môn thì tổng 3 lượt. Tab khác có phiên riêng; không suy ra có thêm một người.

Bộ lọc thống kê đã chốt tại A06; cách triển khai chống ghi nhận lặp sẽ để TDD xác định sau; không tự thêm chỉ tiêu người duy nhất hoặc lượt nghe.

<a id="bf-3"></a>

## 3. Luồng khách và ngoại lệ

<a id="bf-guest-main"></a>

### 3.1. Từ quét QR đến kết thúc

**Điểm vào:** QR chung hoặc URL dự phòng. **Kết thúc phiên:** khách chủ động kết thúc hoặc đủ 8 tiếng; nghe tiếp đúng bài theo P01/UI-02 không kéo dài phiên. QR không tự xác định địa điểm; sơ đồ dùng lựa chọn, không chỉ đường/GPS. Các bước và ngoại lệ dưới đây thuộc BF-K01, không duyệt theo giao diện/ranh giới hoặc nội dung.

| Bước | Hành động / trạng thái của khách | Phản hồi / quy tắc đã chốt |
| --- | --- | --- |
| K01 | Quét QR chung hoặc mở đường dẫn trang bắt đầu in kèm QR. | Truy cập hệ thống trực tuyến. Có đường dẫn dự phòng theo D01-BS; lỗi mạng áp dụng P04. |
| K02 | Nhập tên và chọn ngôn ngữ. | Tên bắt buộc 1–50 ký tự hiển thị; bỏ khoảng trắng đầu/cuối, gộp khoảng trắng liên tiếp giữa tên; không nhận toàn khoảng trắng, xuống dòng/ký tự điều khiển. Chấp nhận Unicode/số/dấu câu thông thường; cho trùng, không xác minh tên thật. Chọn sẵn vi, được đổi en/fr/ru theo D02/BS, D03-BS. |
| K03 | Bấm “Bắt đầu tham quan” với thông tin hợp lệ. | Phản hồi xử lý/ngăn gửi lặp vì bấm nhiều, lỗi giữ tên/ngôn ngữ; phân biệt lỗi xác định/chưa biết kết quả, không tự tạo thêm phiên hoặc báo thành công thiếu căn cứ (UI-01.4). Chỉ bắt đầu thành công mới tạo phiên và mở bản đồ. Mỗi tab một phiên, không đồng bộ tab; hạn cố định bắt đầu + 8 tiếng, không gia hạn theo tải lại/hoạt động. Không tìm/gộp phiên bằng tên (D04, P02). |
| K04-A | Chạm bên trong một vùng địa điểm. | Mỗi địa điểm một vùng, không cần chạm đúng biểu tượng/tên; chọn/hiển thị văn bản và yêu cầu phát audio đúng ngôn ngữ khi sẵn sàng. Khi chuyển vùng khác: dừng bài cũ, bài mới từ đầu. Không GPS/quyền vị trí hoặc công nghệ bản đồ bắt buộc (D01/BS, D07). |
| K04-C | Một chạm khớp nhiều vùng; hoặc khách chọn liên tục. | UI-P01: box nhỏ đúng ứng viên và Hủy, nền không nhận thao tác; audio cũ phát tiếp hoặc giữ pause. Hủy không đổi bài/tua/phát lại/cộng lượt; chọn ứng viên theo P06 nếu cùng điểm. Hết hạn đóng box/chuyển K10-B, không nhận chọn mới. Lựa chọn mới nhất thắng, tối đa một audio (P08/UI-02). |
| K05 | Xem văn bản theo ngôn ngữ đang chọn. | Chỉ nội dung được duyệt rõ theo D09. Thiếu bản dịch thì báo, chỉ liệt kê ngôn ngữ có văn bản được phép hiển thị để khách tự chọn hoặc quay lại bản đồ; không có thì quay lại. Không tự chuyển ngôn ngữ (D03/BS). |
| K06 | Nghe sau khi chọn vùng; xử lý thiếu/lỗi audio nếu có. | Chọn vùng đồng thời yêu cầu phát khi sẵn sàng; hiển thị rõ ngôn ngữ. Có văn bản nhưng thiếu audio: vẫn đọc, báo “Audio chưa sẵn sàng”, vô hiệu nút phát. Lỗi tải/phát: thông báo và cho thử lại từ đầu (D03/BS, D07, P04). |
| K07 | Dùng nút audio chung và thanh tua. | Phiên còn hiệu lực: một nút Phát/Tạm dừng/Nghe lại UI-P03, tua đang phát thì tiếp tục ở mốc mới khi sẵn sàng, tua khi tạm dừng giữ tạm dừng; sau hết bài tua về trước thì tạm dừng/nút Phát. Không nút thứ hai/thông báo kết thúc bài. Hết hạn chỉ pause/resume đúng bài đang phát tại mốc hết hạn theo P01/UI-02, không tua/Nghe lại; P06 không vượt quyền phiên. |
| K08-A | Chọn vùng địa điểm khác. | Giữ phiên/ngôn ngữ; dừng audio cũ, hiển thị nội dung mới và phát bài mới từ đầu khi sẵn sàng. Thiếu/lỗi áp dụng D03/BS; kết quả mới nhất ưu tiên (D07, P08). |
| K08-B | Đổi ngôn ngữ tại địa điểm đang xem. | Giữ điểm/phiên, dừng bài cũ, đặt về đầu bài ngôn ngữ mới và **không tự phát**. Khách bấm Phát; thiếu/lỗi theo D03/BS. Chọn ngôn ngữ thay thế khi thiếu bản dịch cũng theo quy tắc này (D07). |
| K08-C | Bấm lại vùng đang chọn trong cùng lần mở. | Đang phát giữ nguyên; tạm dừng tiếp tục từ mốc giữ; đã hết bài phát lại từ đầu; đang tải không lặp yêu cầu. Không cộng lượt xem; thiếu/lỗi áp dụng D03/BS (P06). |
| K08-D | Bấm ngoài vùng hoặc kéo/phóng bản đồ. | Chạm ngoài không đổi điểm/audio, không cộng lượt/chọn vùng gần nhất; chưa chọn gì thì giữ hướng dẫn nhẹ, không báo lỗi mỗi lần. Kéo/phóng không vô tình kích hoạt vùng/phát audio (P07). |
| K09 | Tải lại tab khi phiên còn hiệu lực. | Khôi phục điểm/ngôn ngữ/mốc nghe gần nhất đã lưu của cùng bài, không cần đúng từng giây; audio tạm dừng, không tự phát. Mốc không khôi phục được/bài đổi thì về đầu và báo. Không tạo phiên, gia hạn hay cộng lượt; khôi phục không phải chọn vùng mới (P03). |
| K09-CB | Còn trên 0 đến 5 phút hoặc tải lại/quay lại tab trong khoảng này. | Hiện “Phiên tham quan sẽ hết hạn trong vòng 5 phút”; nhẹ, không che nội dung/ngắt audio/bắt đóng hộp thoại; không nhắc lặp liên tục, vẫn nhận biết khi quay lại; không gia hạn/tạo phiên (D04-BS). |
| K09-M | Mất mạng/lỗi dịch vụ rồi kết nối lại. | Giữ nội dung/lựa chọn, báo và thử lại; audio đã tải còn phát được thì không ép dừng. Lỗi audio thử lại từ đầu. Có mạng lại kiểm tra phiên trước tác vụ; không cộng lặp/gia hạn; không offline/gửi bù (P04). |
| K10-A | Chủ động bấm “Kết thúc tham quan”, tách khỏi Ngôn ngữ. | Phản hồi xử lý/chống lặp, phân biệt lỗi/chưa rõ, chỉ sau thành công dừng audio/xóa tên điền sẵn-trạng thái tab và hiện box “Đã kết thúc tham quan” chặn toàn trang ứng dụng. Nền mờ/khóa, không Hủy/đóng/chạm ngoài/Esc để đóng; “Bắt đầu phiên mới” mở UI-K01 tên trống/vi mặc định, phải xác nhận bắt đầu thành công mới tạo phiên. Không hộp xác nhận trước kết thúc; không chặn đóng tab/trình duyệt hoặc hồi sinh phiên khi tải lại. Bản ghi Admin giữ theo D08 (UI-P04/UI-02). |
| K10-B | Đủ 8 tiếng, kể cả đang đọc/nghe. | Hết hạn đúng mốc: box “Phiên tham quan đã hết hạn”/“Bắt đầu phiên mới” trong khung bản đồ, toàn nền bản đồ mờ/khóa, không đóng/Hủy, không chặn toàn trang. Khóa điểm/ngôn ngữ/tua/Nghe lại/phát mới; giữ bài cuộn đọc. Chỉ bài đang phát lúc hết hạn tiếp tục tới cuối và pause/resume chính bài; không quyền khôi phục sau tải lại. Xong không Nghe lại. Mở form phiên mới dừng bài cũ; điền sẵn nếu hợp lệ/xác nhận lại P02. Không gia hạn/phiên/lượt mới, 30 ngày không tính từ hết bài. Cảnh báo 5 phút giữ nguyên (P01/UI-02). |
| K10-C | Quay lại phiên hết hạn hoặc không tìm thấy phiên. | Hết hạn: điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới. Không tìm thấy dùng câu “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”, không khẳng định sai đã hết hạn. Chỉ tạo mới khi bấm bắt đầu thành công, rồi về bản đồ; không tìm bằng tên (P02). |
| K11 | Muốn đổi tên. | Kết thúc phiên hiện tại rồi bắt đầu phiên mới với tên hợp lệ; áp dụng P02 về xóa thông tin điền sẵn của phiên đã chủ động kết thúc (D02-BS). |

Trình tự rút gọn:

> QR/đường dẫn → tên và ngôn ngữ → bắt đầu phiên của tab → chọn vùng để đọc/phát audio → nghe/tua, đổi vùng hoặc đổi ngôn ngữ theo quy tắc riêng → cảnh báo 5 phút cuối → kết thúc chủ động hoặc hết hạn đúng 8 tiếng → khách xác nhận nếu muốn tạo phiên mới.

K04-B (nhánh tọa độ thực của dự thảo cũ) chỉ giữ mã để tra lịch sử, ngoài phạm vi D01/D05; không có bước yêu cầu quyền vị trí. Các trường hợp lỗi/thiếu nội dung và tải lại không làm giảm phạm vi nghiệm thu.

<a id="bf-guest-audio"></a>

### 3.2. Phân biệt chọn vùng, đổi ngôn ngữ và khôi phục

Các quyền chọn vùng/ngôn ngữ/tua/Nghe lại/retry từ đầu ở đây dành cho phiên còn hiệu lực. Khi hết hạn chỉ ngoại lệ đúng bài đang phát tại mốc theo P01/UI-02 và bảng 3.5; tải lại không khôi phục quyền này.

Chạm bất kỳ vị trí trong vùng để chọn và yêu cầu phát; không cần chạm chính xác tên/biểu tượng. Vùng không bắt buộc tròn, hình dạng/ranh giới phải thiết kế/xem xét riêng. Quy tắc áp dụng chung cho các điểm hiện và thêm sau theo A02.4/A02.5; không mặc định công nghệ bản đồ.

| Hành động | Quy tắc đang áp dụng | Trạng thái |
| --- | --- | --- |
| Chọn vùng lần đầu hoặc chọn vùng địa điểm khác | Chọn/hiển thị đúng nội dung và yêu cầu phát từ đầu khi sẵn sàng; đổi vùng dừng bài cũ. | Đã chốt D01/D01-BS/D07. |
| Đổi ngôn ngữ tại địa điểm hiện tại, kể cả chọn ngôn ngữ thay thế khi thiếu bản dịch | Giữ địa điểm/phiên; dừng bài cũ, mốc nghe về đầu, không tự phát. | Giữ quyết định D07; không suy ra chọn ngôn ngữ là chọn vùng mới. |
| Bấm lại vùng đang chọn trong cùng lần mở | Đang phát giữ nguyên; tạm dừng tiếp tục; đã hết bài phát từ đầu; đang tải không lặp; không cộng lượt. | Đã chốt P06; thiếu/lỗi theo D03/BS. |
| Tải lại/khôi phục lựa chọn sau tải lại | Khôi phục điểm/ngôn ngữ/mốc gần nhất của cùng bài, audio tạm dừng; không coi là chọn vùng hoặc cộng lượt. | Đã chốt P03; không cần đúng từng giây, không khôi phục được/bài đổi thì về đầu và báo. |

Chi tiết tình huống bấm lại/ngoài vùng/chồng lấn và giới hạn áp dụng tra tại [P06–P08](decisions.md#dec-p). Một nút dùng chung Phát/Tạm dừng/Nghe lại và thanh tua theo [UI-01.2](decisions.md#ui-p03), không nút Nghe lại thứ hai hoặc thông báo kết thúc bài; chưa yêu cầu đổi tốc độ/nghe nền.

**Bổ sung giao diện đã chốt UI-01:** trang bắt đầu riêng; trang tham quan đầu gọn, khung bản đồ → tên điểm → audio ngoài/trên box văn bản. Cuộn box chỉ cuộn văn bản, bản đồ/tên/audio vẫn thấy; kéo/phóng trong khung không chọn/phát nhầm, chiều cao thích ứng chưa chốt pixel. Trước chọn chỉ hướng dẫn. Một bộ chọn “Ngôn ngữ” cho giao diện khách và nội dung/audio bốn ngôn ngữ, tên khách giữ nguyên; bỏ nhãn thường trực Văn bản/Audio/Trạng thái nhưng vẫn phản hồi tải/thiếu/lỗi. Bộ chọn đúng ngôn ngữ áp dụng, nội dung cũ không gây nhầm khi đổi; câu vi đã chốt là căn cứ nghĩa, bản dịch tên/tiêu đề/thông báo phải rà soát trước dùng, chưa duyệt sẵn. Chi tiết và phạm vi tại [decisions 4.11](decisions.md#ui-guest-confirmed).

<a id="bf-warning"></a>

### 3.3. Cảnh báo 5 phút cuối — D04-BS

UI-01.3: câu dưới là nguyên văn căn cứ tiếng Việt; en/fr/ru dùng bản dịch tương ứng được rà soát trước dùng, không tự đổi nghĩa hoặc duyệt sẵn bản dịch.

- Giữ hạn cố định 8 tiếng từ lúc bắt đầu; không gia hạn theo hoạt động.
- Khi thời gian còn lại **trên 0 và bằng hoặc dưới 5 phút**, trong phiên còn hiệu lực, hiển thị: **“Phiên tham quan sẽ hết hạn trong vòng 5 phút”**.
- Cảnh báo nhẹ, không che nội dung, không ngắt audio, không buộc khách đóng hộp thoại để tiếp tục.
- Hiển thị một lần trong lần sử dụng trang hiện tại; không nhắc lặp liên tục. Khi tải lại hoặc quay lại tab trong khoảng này vẫn phải để khách nhận biết phiên sắp hết hạn; không coi việc đã từng hiển thị trước đó là lý do bỏ qua cảnh báo.
- Đã hết hạn: box “Phiên tham quan đã hết hạn” trong khung bản đồ theo P01/UI-02; giữ đọc/cuộn và chỉ pause/resume bài đủ điều kiện đang phát ở mốc hết hạn. Không dùng box này trước mốc hoặc thay cảnh báo nhẹ 5 phút; câu vi đã chốt, bản dịch vẫn rà soát.
- Cảnh báo không tự gia hạn hay tạo phiên mới; không tác động thanh tua hoặc các điều khiển đang dùng khi phiên còn hiệu lực.

| Trạng thái / thời gian còn lại | Hành vi đã chốt để đối chiếu khi nghiệm thu |
| --- | --- |
| Phiên còn hiệu lực, còn hơn 5 phút | Chưa hiển thị cảnh báo sắp hết hạn. |
| Phiên còn hiệu lực, còn đúng 5 phút hoặc ít hơn nhưng vẫn trên 0 | Hiển thị cảnh báo nhẹ với nội dung đã chốt; tiếp tục đọc/nghe bình thường. |
| Tải lại hoặc quay lại tab trong khoảng trên 0 đến 5 phút | Khách vẫn nhận biết được phiên sắp hết hạn; không nhắc lặp liên tục trong cùng lần sử dụng trang. |
| Đã hết hạn, kể cả khi lần trước chưa thấy cảnh báo | Hiển thị thông báo hết hạn; không hiển thị cảnh báo sắp hết hạn. |

<a id="bf-guest-errors"></a>

### 3.4. Lỗi và ngoại lệ khách

| Mã | Tình huống | Nội dung đã chốt | Phần còn mở / đề xuất |
| --- | --- | --- | --- |
| E01 | QR không đọc được hoặc hỏng. | D01-BS: đường dẫn trang bắt đầu in kèm QR làm cách truy cập dự phòng. | Chưa chốt bố cục/tài nguyên in QR, không cản trở luồng nghiệp vụ. |
| E02 | Không mở được trang, mất mạng hoặc dịch vụ lỗi. | P04: giữ nội dung/lựa chọn đang có, thông báo và thử lại; không ép dừng audio phần đã tải còn phát được. Có mạng lại kiểm tra phiên; không cộng lặp/gia hạn; hạn 8 tiếng vẫn giữ. | Chưa thêm offline/gửi bù. Khi trang chưa tải được, khả năng thông báo phụ thuộc trang có hoạt động hay không; chi tiết kỹ thuật xác định sau. |
| E03 | Tên không đáp ứng quy tắc. | D02-BS: bắt buộc, 1–50 ký tự hiển thị; quy tắc khoảng trắng/ký tự tại K02; tên trùng hợp lệ. | Đề xuất báo lỗi tại ô tên và cho sửa, không tạo phiên trước khi hợp lệ. “Ký tự hiển thị” cần được hiện thực nhất quán trong TDD. |
| E04 | Ngôn ngữ lựa chọn không hợp lệ. | D03-BS/D06: chọn sẵn vi, có vi/en/fr/ru; không tự chuyển ngôn ngữ. | Đề xuất báo lỗi và cho chọn lại trong danh sách; không xem lựa chọn không hợp lệ là đã bắt đầu thành công. |
| E05 | Nhánh lỗi cung cấp tọa độ của dự thảo cũ. | D01/D05: ngoài phạm vi đã chốt của phiên bản nghiệm thu. | Giữ mã để tra cứu, không phải yêu cầu cần triển khai. |
| E06 | Nhánh lỗi xác định vùng địa lý theo tọa độ thực của khách trong dự thảo cũ. | D01/D05: ngoài phạm vi đã chốt; khác với vùng tương tác trên bản đồ ở mục 3.2. | Không thiết kế bán kính/vùng định vị GPS; vùng tương tác bản đồ đã chốt và cần thiết kế ranh giới sau. |
| E07 | Địa điểm tại vùng được chọn không khả dụng. | D01-BS: thông báo rõ và cho chọn vùng địa điểm khác; không tự chọn điểm khác để phát. | A02/A02.1–A02.5 đã chốt thêm/sửa/ẩn-hiện/xóa, giữ lịch sử, dọn file và điều kiện hiện điểm mới; A07 chốt tạm ẩn có hiệu lực lần tải/chọn tiếp, không thu hồi tức thì. Ranh giới/thao tác vẽ cụ thể vẫn thiết kế riêng. |
| E08 | Thiếu bản dịch. | D03/D03-BS: báo rõ, chỉ liệt kê ngôn ngữ có văn bản được phép hiển thị để khách chọn; không có thì quay lại bản đồ; không tự chuyển ngôn ngữ. | Không được dùng xử lý này để giảm ma trận nghiệm thu D06. |
| E09 | Có văn bản nhưng thiếu audio tương ứng. | D03-BS: vẫn đọc, thông báo “Audio chưa sẵn sàng”, vô hiệu nút phát. D09: chỉ dùng audio đúng phiên bản đã được duyệt. | A04.1–A04.4 đã chốt tạo từ văn bản trong Admin, trạng thái/thử lại và bảo vệ bản đang phục vụ; không sinh trực tiếp do khách chọn vùng. Quy tắc thiếu audio khách giữ nguyên; chưa xác minh dịch vụ bốn ngôn ngữ. |
| E10 | Audio đã có nhưng tải/phát lỗi. | Còn hiệu lực: D03-BS/P04 báo/thử lại từ đầu, giữ nội dung/lựa chọn. Hết hạn không cấp quyền retry từ đầu; chỉ giữ ngoại lệ P01/UI-02 nếu bài đủ điều kiện còn phát được. | Tải lại chỉ khôi phục mốc P03 khi còn hiệu lực, không khôi phục ngoại lệ sau hết hạn. Không hứa mạng/browser đáp ứng. |
| E11-A | Chọn vùng địa điểm khác khi đang nghe hoặc chọn liên tục. | D01/D07/P08: dừng bài cũ, hiển thị/phát bài mới từ đầu khi sẵn sàng. Chỉ lựa chọn mới nhất có hiệu lực; kết quả cũ trả muộn không thay nội dung hoặc phát chồng. | Mỗi tab một audio; không mở yêu cầu đồng bộ giữa tab. |
| E11-B | Đổi ngôn ngữ khi đang nghe. | D07: giữ địa điểm/phiên, dừng audio cũ, mốc về đầu bài mới, không tự phát; thiếu/lỗi theo D03/D03-BS. | Không áp dụng quy tắc phát khi chọn vùng cho hành động đổi ngôn ngữ. |
| E12 | Tải lại hoặc mở tab khác. | D04/P03: tải lại tab tiếp tục phiên còn hiệu lực, khôi phục điểm/ngôn ngữ/mốc gần nhất cùng bài; audio tạm dừng, không tự phát/cộng lượt/gia hạn. Không khôi phục được mốc/bài đổi thì về đầu và báo. Tab khác có phiên riêng. | Không cần mốc chính xác từng giây; nhận biết/nhân bản/khôi phục tab là chi tiết TDD, không tự gộp phiên giữa tab. |
| E13 | Đóng trang không bấm kết thúc. | D04: không suy ra chính xác thời điểm kết thúc; mốc hết hạn vẫn bắt đầu + 8 tiếng. Khi quay lại áp dụng P02 theo trạng thái xác định được. | Không quay về cơ chế hết hạn do không hoạt động hoặc tìm phiên bằng tên. |
| E14 | Đủ 8 tiếng khi đọc/nghe hoặc box ứng viên đang mở. | P01/UI-02: đóng ứng viên nếu mở; box hết hạn chỉ trong khung bản đồ với toàn nền bản đồ bị khóa/mờ, ngôn ngữ khóa, bài đã tải cuộn đọc. Chỉ bài đang phát lúc mốc hết hạn có quyền tiếp tục/pause/resume tới cuối; không tua/Nghe lại/phát bài khác hoặc hồi sinh sau tải lại. Mở form mới dừng bài cũ. | Không hứa phát nếu mạng/browser/file không đáp ứng; mốc hết hạn/lượt/30 ngày không đổi, không bổ sung quyền thử lại từ đầu sau hết hạn. |
| E15 | Quay lại phiên đã kết thúc/hết hạn hoặc không thể tiếp tục phiên trước. | **Hết hạn:** điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới. **Chủ động kết thúc:** dừng audio, xóa tên điền sẵn và trạng thái khôi phục trong tab; không điền lại thông tin đã xóa, giữ bản ghi Admin theo hạn 30 ngày D08. **Không tìm thấy phiên:** “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”; không tự kết luận đã hết hạn hoặc tìm phiên bằng tên. Mọi nhánh chỉ tạo phiên mới khi khách bấm bắt đầu thành công, rồi về bản đồ (P02). | Không áp quy tắc điền sẵn của hết hạn cho tất cả các trường hợp; phân biệt thông tin khôi phục tab với bản ghi Admin. |
| E16 | Sắp hết hạn, tải lại hoặc quay lại tab gần mốc hết hạn. | D04-BS: trên 0 đến 5 phút cảnh báo nhẹ, không gián đoạn/nhắc liên tục; tải lại/quay lại vẫn nhận biết. Hết hạn chuyển sang thông báo/xử lý P01. | Bố cục thông báo và cách kiểm tra thời gian ở tab nền thuộc thiết kế; không đổi hạn cố định 8 tiếng. |
| E17 | Phiên còn hiệu lực, bấm lại vùng đang chọn trong cùng lần mở. | P06: đang phát giữ nguyên; tạm dừng tiếp tục; đã hết phát lại từ đầu; đang tải không lặp; không cộng lượt; thiếu/lỗi áp dụng D03/BS. | Chuyển điểm khác rồi quay lại vẫn tính theo D04; không gộp hai lần mở thành một. |
| E18 | Bấm ngoài vùng, kéo hoặc phóng bản đồ. | P07: ngoài vùng không đổi điểm/audio, không cộng lượt/chọn gần nhất hoặc báo lỗi mỗi lần; chưa chọn điểm giữ hướng dẫn nhẹ. Kéo/phóng không vô tình chọn vùng/phát audio. | Không ấn định công nghệ bản đồ; ngưỡng phân biệt thao tác là thiết kế cần thể hiện đúng hành vi đã chốt. |
| E19 | Chạm khớp nhiều vùng hoặc vùng khó phân biệt trên điện thoại. | P08: vùng/nhãn/viền dễ phân biệt; có danh sách ứng viên trước khi đổi nội dung/audio, không tự chọn tùy ý. | Ranh giới/hình dạng cụ thể phải thiết kế và trình xem xét riêng; xử lý chồng lấn đã chốt không đồng nghĩa duyệt sẵn ranh giới. |
| E20 | Chọn vùng có yêu cầu phát nhưng audio không sẵn sàng hoặc tải/phát lỗi. | D03/D03-BS: thiếu bản dịch chỉ cho khách chọn ngôn ngữ có văn bản; thiếu audio báo “Audio chưa sẵn sàng”, vô hiệu phát, vẫn đọc; lỗi tải/phát thông báo và cho thử lại từ đầu. | Không hứa mọi yêu cầu phát đều thành công; không phát bản khác ngôn ngữ hoặc thay thế bản chưa được duyệt. |

Vị trí chính của P01–P08 tại [decisions.md mục 4.10](decisions.md#dec-p), không chép thêm một bảng P đầy đủ tại đây. E05/E06/K04-B chỉ giữ để tra nhánh tọa độ cũ đã ngoài phạm vi, không là chức năng phải làm.

<a id="bf-expiry-audio"></a>

### 3.5. Quyền thao tác theo trạng thái phiên — UI-02

| Trạng thái | Bản đồ/ngôn ngữ và đọc | Audio / bắt đầu lại |
| --- | --- | --- |
| Còn hiệu lực, không có box ứng viên | Chọn vùng/ngôn ngữ theo D/P; box văn bản cuộn. | Phát/pause/tua/Nghe lại theo UI-P02/UI-P03/P06. |
| Box ứng viên đang mở | Nền trang không thao tác; đúng danh sách + Hủy; mở/hủy không lượt. | Bài cũ đang phát tiếp tục, đang pause giữ pause; chọn lại cùng điểm áp P06. |
| Hết hạn, có đúng bài đang phát ở mốc | Đóng ứng viên; box hết hạn chỉ trong khung bản đồ, toàn nền bản đồ khóa/mờ, ngôn ngữ khóa; tên/audio còn thấy, văn bản cuộn. | Chỉ nghe tiếp/pause/resume chính bài tới cuối; không tua/Nghe lại/bài khác. Mở UI-K01 dừng bài cũ. |
| Hết hạn, không có bài đủ điều kiện hoặc đã hết bài | Giữ box trong bản đồ và quyền đọc đã tải. | Không bắt đầu/Nghe lại/tua; không khôi phục nghe sau tải lại; tên/ngôn ngữ điền sẵn nếu hợp lệ khi vào form mới. |
| Chủ động kết thúc thành công | Box “Đã kết thúc tham quan” chặn toàn trang, nền mờ/khóa, không đóng/Hủy/ngoài/Esc; không ngăn trình duyệt/đóng tab. | Audio dừng, xóa điền sẵn/trạng thái tab; nút mở UI-K01 tên trống/vi. Không xác nhận trước kết thúc/tự tạo phiên. |

Phiên vẫn hết hạn bắt đầu + 8 tiếng; pause/resume không gia hạn/lượt mới. Hạn lưu 30 ngày tính từ hết hạn cố định, không theo lúc phát hết. Không tự mở rộng ngoại lệ sang bài tạm dừng trước mốc, đang tải chưa phát, lỗi cần phát lại hoặc khôi phục sau tải lại. Mạng/browser không đáp ứng thì không hứa phát được. Cơ chế xác định/giữ đúng bài và phục vụ file chờ TDD theo [PRD 9.5](PRD.md#prd-expiry-delivery); A02.3 giữ nguyên.

<a id="bf-4"></a>

## 4. Luồng Admin và ngoại lệ

<a id="bf-admin-main"></a>

### 4.1. Các nhóm công việc và nhánh thao tác

Sau đăng nhập, Admin chọn nhóm cần làm; không phải đi qua mọi nhánh trong một lần sử dụng.

| Nhóm / bước | Luồng nghiệp vụ đã chốt | Ngoại lệ / giới hạn |
| --- | --- | --- |
| Đăng nhập — AD01 | Tài khoản cấp trước, một vai trò; đăng nhập hợp lệ, đăng xuất. Phiên 30 phút từ hoạt động chủ động được ghi nhận theo Q3, tách khách 8 tiếng. | Hết phiên chặn tác vụ cần quyền, bảo vệ bản đang soạn theo Q2; hoạt động/tác vụ nền không hồi sinh phiên cũ. Cơ chế ghi nhận/lưu tạm chưa chọn. |
| Theo dõi dữ liệu — AD02 | Xem danh sách địa điểm và mức đầy đủ văn bản/audio theo điểm/ngôn ngữ; phân biệt bộ năm điểm/20 văn bản/20 audio với địa điểm thêm. | Năm điểm là bộ nội dung người dùng chọn, không phải trần do giảng viên quy định; chưa thêm chức năng quản lý ngôn ngữ. |
| Địa điểm — AD03 | Thêm/sửa/ẩn-hiện/xóa. Theo Q1, chỉ tên Admin bắt buộc nhập: 1–100 ký tự hiển thị sau chuẩn hóa, Unicode/số/dấu câu, không rỗng/xuống dòng/điều khiển. Mã hệ thống cấp ổn định/độc lập tên; trùng tên cảnh báo không chặn kèm mã, không gộp/hậu tố. Lưu được điểm ẩn chưa có vùng/bài; vùng/văn bản/audio quản lý riêng. | Q2 bảo vệ biểu mẫu chưa lưu; bật hiện vẫn theo A02.5. Không tự tăng bộ bài chuẩn/ngôn ngữ/QR riêng, không tái dùng mã điểm đã xóa. |
| Vùng — AD03-V | Vẽ/sửa → Xem trước → Lưu bản sửa → Áp dụng đúng bản đã xem theo UI-03; một đa giác kín đơn giản, không lỗ/nhiều mảnh. Hình dở giữ tạm Q2; lưu cùng hình không bắt xem lại, sửa tiếp/bản lưu khác thì lưu/xem lại đúng bản. Vùng hiện hành giữ đến khi áp dụng; khách nhận lần tải bản đồ tiếp theo. | Không tự áp dụng/bật hiện/ngắt audio/ép hết phiên; ranh giới/công nghệ chưa duyệt. Giữ Q2 và A02.6. |
| Bật hiện — AD03-H | A02.5/UI-04: thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng, ít nhất cặp vi đã duyệt/áp dụng. Thiếu cặp vi hoặc điều kiện khác thì chưa bật, nêu rõ phần thiếu. | Lưu điểm ẩn được dù thiếu vùng/bài/audio; không đợi đủ bốn ngôn ngữ. Tạm ẩn chỉ ngừng truy cập mới lần tải/chọn tiếp theo, không thu hồi tức thì nội dung có quyền đã tải. |
| Văn bản/bản dịch — AD04 | Chọn điểm/ngôn ngữ/bản đang sửa; Q2 bảo vệ/khôi phục bản tạm đúng công việc với nhãn chưa lưu. Chủ động Lưu nháp thành công xóa tạm tương ứng, vẫn tách bản đang áp dụng; khách tiếp tục thấy bản hợp lệ cũ. | Giữ tạm khác lưu nháp/Áp dụng; không tự dịch/gắn audio cũ vào bản mới hoặc kho lịch sử phiên bản phức tạp. Đã báo cáo có năm bản nháp vi; cần xác minh bản lưu, rà soát nguồn và hồ sơ duyệt. |
| Tạo audio — AD05 | Lưu văn bản rồi chủ động bấm “Tạo audio”, yêu cầu gắn đúng bản/ngôn ngữ; được tạo/nghe thử từ nháp trong Admin. Theo dõi chưa tạo/đang tạo/thành công/lỗi; đang tạo không bấm lặp, lỗi thử lại chủ động, chưa rõ kết quả thì kiểm tra yêu cầu cũ. | Không tự tạo sau mọi lần lưu. Tạo thành công/nghe thử chưa là được duyệt hoặc đã áp dụng. TTS-01 đã chọn model/giọng; chưa kiểm chứng tích hợp/chất lượng đủ bốn ngôn ngữ. |
| Đổi văn bản/tác vụ — AD05-C | Văn bản đổi không tự gắn audio của bản cũ vào bản mới; giữ bản hợp lệ đang phục vụ khi tạo mới/lỗi. Tác vụ đã tiếp nhận tiếp tục khi Admin hết phiên/đóng trang; đăng nhập lại xem kết quả, không tự áp dụng. | Điểm bị xóa thì bỏ kết quả muộn, không phục hồi điểm; file không dùng vào cơ chế dọn phù hợp. Không hứa dịch vụ ngoài luôn hủy được yêu cầu. |
| Duyệt/áp dụng — AD06 | Hồ sơ duyệt của cặp: tham chiếu riêng đúng bản văn/audio/ngôn ngữ, có thể chung mục nếu rõ cả hai. Ghi tham chiếu không tự duyệt. Tóm tắt cặp hiện tại/cặp mới rồi một nút “Áp dụng văn bản và audio”; chỉ áp dụng cả cặp đủ D09/UI-04. | Không áp dụng riêng hoặc âm thầm sau lưu/tạo/ghi tham chiếu. Chưa đủ audio thì chưa áp dụng; chỉ thay audio dùng văn bản hiện tại không đổi, không bắt duyệt lại văn bản. |
| Bản mới cho khách — AD07/AD10 | Chuyển cả cặp nhất quán, hiệu lực lần tải/chọn tiếp theo; thất bại xác định giữ cặp cũ, chưa rõ kiểm tra thực tế trước thử lại. Chưa có cặp cũ thì chưa phục vụ nội dung mới trước thành công. Cặp đã áp dụng nhưng audio thực tế thiếu/tải-phát lỗi vẫn đọc văn bản và xử lý D03/BS/P04. | Không ngắt bài đang nghe có quyền theo A07/P01/UI-02, không dùng audio sai bản/ngôn ngữ hoặc mốc bài cũ. Áp dụng không tự bật hiện. |
| Xóa — AD03-X | Admin bấm Xóa/xem xác nhận. Ứng dụng xử lý dữ liệu thuộc riêng và tham chiếu có kiểm soát, giữ lịch sử nhận biết điểm cũ, vô hiệu liên kết tác vụ tạo; sau đó xóa điểm. Mọi thay đổi database cùng một giao dịch, thành công toàn bộ/hoàn tác; không xóa nhầm dữ liệu dùng chung hoặc hồ sơ duyệt tài liệu. | Khóa ngoại chặn xóa trực tiếp khi còn tham chiếu, không CASCADE ở quan hệ cần chặn. Không gộp lịch sử điểm mới chỉ vì trùng tên. Đây là cơ chế đã chốt trước, nay cụ thể hóa. |
| Dọn file sau xóa — AD03-F | Chỉ dọn sau database thành công; chỉ file không còn tham chiếu/dùng chung, bảo vệ 8 tiếng từ ngừng áp dụng cuối cùng. Lỗi có chờ/thử lại; không báo file đã xóa khi mới xóa bản ghi. | File tách khỏi giao dịch database; khoảng bảo vệ không gia hạn phiên. Cách chạy dọn/kiểm tra tham chiếu là thiết kế kỹ thuật. |
| QR — AD08 | UI-05: trang một QR chung/URL trang bắt đầu chỉ đọc, tải PNG, bản in gọn tên hệ thống+QR+URL dự phòng; dùng cả điểm thêm/ngôn ngữ. | Chưa URL thật thì chưa sẵn sàng/không QR giả; không chọn URL-thư viện, QR riêng/đếm quét/chuyển hướng/tùy biến. Mở hộp in/yêu cầu tải không chứng minh in/lưu, cần đối chiếu bản thật. |
| Phiên/thống kê — AD09 | Danh sách chỉ đọc lọc ngày/trạng thái/tên-mã. Xem đúng phiên mở box UI-A21 trên danh sách, có Đóng/cuộn trong box, nền không thao tác, điện thoại vừa chiều rộng; đóng giữ lọc. Ngày Từ/Đến trọn cả hai ngày Asia/Saigon, mặc định hôm nay. Hai bảng/tổng riêng theo ngày: phiên bắt đầu và lượt theo thời điểm/điểm/ngôn ngữ đã ghi. Về hôm nay đổi hai ngày, danh sách giữ trạng thái/tên-mã. | Giữ tải/lỗi/rỗng/không còn dữ liệu/hết phiên Admin; không sửa/xóa/gia hạn/kết thúc thay khách. Chỉ dữ liệu còn giữ D08, điểm xóa riêng/không gộp tên. Không cộng phiên+lượt/suy người-nghe-quét, lọc điểm/ngôn ngữ/xuất báo cáo/dashboard hoặc kho thống kê vô hạn. |
| Đăng xuất/hết phiên — AD11 | Hết 30 phút Q3: dừng nghe thử Admin, che nội dung/chặn tác vụ cần quyền; giữ soạn và tham chiếu trong công việc theo Q2/UI-AP05. Cùng tài khoản trong tab đăng nhập lại để tiếp tục, không tự phát/Lưu/Áp dụng/gửi lại thao tác. Điều hướng/đăng xuất mất phần chưa lưu phải cho quay lại hoặc xác nhận bỏ. | Không áp ngoại lệ nghe tiếp của khách cho Admin. TTS đã nhận tiếp tục A04.4; đóng tab/crash/thiết bị khác ngoài cam kết phục hồi. Bản đã lưu không bị xóa chỉ do dọn tạm. |

Căn cứ hiện hành: [A01–A07 và các mục con](decisions.md#dec-5). Không yêu cầu duyệt lại các quyết định này.

<a id="bf-admin-delete"></a>

### 4.2. Xóa địa điểm, giữ lịch sử và dọn file

1. Admin chọn Xóa, xem xác nhận rồi ứng dụng thực hiện.
2. Trong quy trình ứng dụng: xử lý văn bản/bản dịch, nháp, bản ghi audio và liên kết vùng thuộc riêng địa điểm trước khi xóa điểm; bảo vệ dữ liệu dùng chung. Vô hiệu liên kết yêu cầu tạo liên quan; không tự xóa hồ sơ duyệt tài liệu.
3. Giữ phiên/lượt xem đến hết hạn D08; lưu thông tin nhận biết điểm cũ độc lập bản ghi điểm để thống kê rõ “địa điểm đã xóa”, không gộp theo trùng tên. Xử lý tham chiếu có kiểm soát, không tạo quan hệ mồ côi.
4. Mọi thay đổi database trong quy trình cùng một giao dịch: thành công toàn bộ hoặc hoàn tác. Khóa ngoại chặn xóa trực tiếp khi còn tham chiếu; không ON DELETE CASCADE ở quan hệ cần chặn. Mục tiêu là toàn vẹn quan hệ, không ngăn mọi người có quyền database xóa dữ liệu.
5. Chỉ sau database thành công mới dọn file; chỉ file không còn tham chiếu/dùng chung, sau khoảng bảo vệ 8 tiếng từ ngừng áp dụng cuối cùng. Lỗi có trạng thái chờ/thử lại; báo riêng trạng thái dữ liệu và file, không báo file đã xóa khi mới xóa bản ghi. UI-02 thêm phụ thuộc phục vụ bài nghe tiếp: TDD phải đối chiếu quyền đọc/file/tải đệm và tranh chấp dọn file; không tự đổi khoảng bảo vệ hoặc hứa file luôn phát được.
6. Giao dịch database không bao trùm thao tác file; khoảng bảo vệ không gia hạn phiên khách.
7. Điểm đã xóa không được phục hồi do kết quả tạo audio đến muộn; file phát sinh không dùng vào cơ chế dọn phù hợp, không hứa dịch vụ ngoài luôn hủy được yêu cầu.

Cách lưu thông tin lịch sử, xử lý khóa ngoại/thứ tự chi tiết, lịch dọn và mốc cho file chưa từng áp dụng sẽ thiết kế theo nguyên tắc đã chốt; chưa có thao tác hoặc bằng chứng database/file thực tế ở bước này.

<a id="bf-admin-audio"></a>

### 4.3. Tạo audio và áp dụng cả cặp — UI-04 hiện hành

> Lưu bản văn/ngôn ngữ → chủ động Tạo audio → theo dõi đúng bản → nghe thử → người dùng duyệt văn bản/audio theo D09 → Admin đối chiếu Hồ sơ duyệt của cặp → một nút **Áp dụng văn bản và audio**.

| Trạng thái tạo | Hành vi đã chốt | Giới hạn |
| --- | --- | --- |
| Chưa tạo | Chỉ bắt đầu khi Admin chủ động sau lưu, kể cả nháp | Lưu không tự tạo/duyệt/áp dụng |
| Đang tạo | Chống bấm lặp; chưa rõ kết quả kiểm tra yêu cầu cũ | Không tự tạo trùng do mạng/chờ lâu |
| Thành công | Kết quả thuộc đúng bản văn/ngôn ngữ lúc tạo, có thể nghe thử | Chưa đồng nghĩa nghe trọn/được duyệt/đã áp dụng |
| Lỗi | Thử lại chủ động, giữ cặp đang phục vụ hợp lệ | Không làm hỏng cặp cũ/gắn sai bản |

**Áp dụng:** một cặp của một điểm/ngôn ngữ, văn bản đã lưu/được duyệt và audio đã nghe trọn/duyệt/khớp D09. Tóm tắt cặp cũ/cặp mới; thiếu điều kiện nào thì báo và chưa áp dụng. Không áp dụng riêng nửa cặp. Thất bại xác định giữ cặp cũ; chưa rõ kiểm tra trạng thái thực tế trước thử lại; chưa có cặp cũ thì nội dung mới chưa phục vụ trước thành công. Chỉ thay audio dùng văn bản hiện tại không đổi cùng audio mới đã duyệt, không bắt sửa/duyệt lại văn bản. Không đợi đủ bốn ngôn ngữ để áp dụng một cặp.

**Sau áp dụng:** thay đổi ở lần tải/chọn tiếp theo, không tự bật hiện địa điểm hoặc ngắt bài đang nghe có quyền theo A07/P01/UI-02. Văn bản mới không được phục vụ thiếu audio do áp dụng một nửa. Nếu cặp đã áp dụng nhưng audio thực tế không sẵn sàng/tải-phát lỗi, giữ đọc văn bản và xử lý D03/BS/P04 theo hiệu lực phiên; không dùng audio cũ/khác ngôn ngữ hoặc mở thêm quyền thử lại sau hết hạn.

**Hồ sơ:** gom theo cặp, tham chiếu văn bản/audio riêng đúng bản/ngôn ngữ, có thể chung mục đủ xác định cả hai; ghi chú giới hạn khi cần, không bắt nhập lại ngày/người nếu hồ sơ có. Chỉ ghi cho đối tượng đã lưu; không âm thầm lưu văn bản đang sửa. Phần tham chiếu đang nhập được bảo vệ cùng công việc theo UI-AP05/Q2, không kho phục hồi dài hạn. Phần mềm không tự thẩm định hồ sơ ngoài.

**Hết phiên Admin:** dừng nghe thử/che nội dung/yêu cầu đăng nhập lại, không tự phát/gửi lại sau xác thực. TTS đã nhận tiếp tục độc lập A04.4; đăng nhập lại xem kết quả, không tự áp dụng. Điểm xóa thì bỏ kết quả muộn, không phục hồi điểm.

Model/giọng theo TTS-01, nền TECH-01; ngân sách trả phí/định dạng/ngưỡng chưa chốt, chưa kiểm chứng tích hợp/đủ vi/en/fr/ru hoặc nội dung cụ thể. Xem [UI-04](decisions.md#ui-admin-confirmed-02), lịch sử điều kiện cũ ở decisions 7.6.

<a id="bf-admin-errors"></a>

### 4.4. Ngoại lệ Admin

| Mã | Tình huống | Cách xử lý hiện hành |
| --- | --- | --- |
| AE01 | Sai/chưa đăng nhập hoặc hết phiên Admin. | Thông báo/chặn tác vụ cần quyền; giữ/khôi phục theo Q2, không báo lưu/giữ tạm giả. Q3 xác định hoạt động, không ghi bù/hồi sinh phiên. Giới hạn thử sai/cách thực hiện vẫn thuộc thiết kế. |
| AE02 | Tên không hợp lệ hoặc thiếu điều kiện bật hiện. | Q1 báo lỗi/giữ biểu mẫu, tên trùng cảnh báo không chặn. Lưu mới vẫn ẩn; A02.5/UI-04 chỉ bật khi đủ thông tin, vùng hợp lệ đã xem xét/áp dụng và cặp vi đã duyệt/áp dụng. Báo đúng điều kiện thiếu; thiếu ngôn ngữ khác theo D03/BS, không tự đổi. |
| AE03 | Tạo lỗi, bấm lặp hoặc chưa rõ kết quả. | A04.2: không lặp khi đang tạo, kiểm tra yêu cầu cũ khi chưa rõ, lỗi cho thử lại chủ động; giữ bản đang phục vụ. |
| AE04 | Văn bản đổi, audio chưa khớp/chưa duyệt hoặc chưa đủ điều kiện áp dụng. | Giữ cặp đang dùng; chưa cho áp dụng cặp mới. Không gắn audio cũ vào bản mới. Cặp đã áp dụng nhưng audio thực tế lỗi vẫn đọc văn bản và xử lý D03/BS; ghi tham chiếu/tạo thành công không tự duyệt/áp dụng. |
| AE05 | Lưu/áp dụng thất bại hoặc dữ liệu thay đổi trong lúc soạn. | Giữ bản đang phục vụ và dữ liệu soạn theo Q2, báo đúng chưa lưu/chưa xác nhận đã lưu. Đăng nhập lại/khôi phục kiểm tra đối tượng/bản hiện hành, không phục hồi điểm đã xóa hoặc ghi đè im lặng; thông báo để đối chiếu/bỏ tạm. Không tự tác vụ, không cộng tác thời gian thực. |
| AE06 | Tạm ẩn/sửa nội dung khi khách đang nghe. | A07: có hiệu lực lần tải/chọn tiếp theo, không thu hồi tức thì hoặc ép kết thúc phần hợp lệ đã tải; audio đổi theo P03, không dùng mốc cũ. |
| AE07 | Xóa database lỗi hoặc xóa trực tiếp khi còn tham chiếu. | Hoàn tác giao dịch nếu quy trình thất bại; FK/quy tắc chặn trực tiếp, không CASCADE ở quan hệ cần chặn. Không khẳng định chặn mọi quyền database. |
| AE08 | Database đã xóa nhưng file chưa dọn/dọn lỗi. | Báo riêng file chờ/thử lại, kiểm tra điều kiện không tham chiếu/dùng chung và bảo vệ 8 tiếng; không báo file đã xóa. |
| AE09 | Điểm bị xóa khi tạo hoặc kết quả đến muộn. | Vô hiệu liên kết, bỏ kết quả muộn, không phục hồi điểm; file không dùng vào cơ chế dọn phù hợp; không hứa dịch vụ ngoài luôn hủy được. |
| AE10 | QR/trang bắt đầu đổi URL. | Vẫn một QR chung; URL/cách thay bản in sẽ thiết kế, không có dịch vụ chuyển hướng được duyệt. Thêm điểm không cần QR riêng. |
| AE11 | Không có dữ liệu thống kê hoặc dữ liệu hết hạn lưu. | Phân biệt kết quả rỗng với lỗi tải; chú thích 30 ngày/cách tính, chỉ dữ liệu còn giữ; giữ nhóm điểm đã xóa không phụ thuộc bản ghi điểm gốc. |
| AE12 | Admin hết phiên/đóng trang khi TTS đã tiếp nhận hoặc đang nghe thử. | TTS tiếp tục độc lập, xem kết quả sau đăng nhập, không tự gửi lại/áp dụng. Hết phiên dừng nghe thử/che nội dung, đăng nhập lại không tự phát; giữ soạn/tham chiếu đúng phạm vi Q2/UI-AP05. Không áp ngoại lệ khách. Đóng tab/crash/thiết bị khác ngoài cam kết phục hồi. |
| AE13 | Sửa/vẽ lại vùng của điểm đang hiển thị. | Giữ vùng hiện hành; “Lưu bản sửa” khác “Áp dụng”. Xem trước/xem xét rồi chủ động áp dụng; nhận vùng mới ở lần tải bản đồ tiếp theo, không đẩy tức thì/ngắt audio/ép kết thúc phiên (A02.6). |

<a id="bf-admin-q"></a>

### 4.5. Chi tiết Q1–Q3 đã xác nhận — luồng Admin bổ sung

Nguồn hiện hành tại [decisions mục 5.8](decisions.md#dec-q). Đây là bổ sung đã được người dùng xác nhận 31/10/2026, không yêu cầu duyệt lại hai luồng hoặc đánh dấu toàn bộ PRD đã duyệt.

| Tình huống / bước | Kết quả vận hành hiện hành |
| --- | --- |
| Chuẩn bị điểm | Admin nhập tên theo Q1, mã hệ thống cấp khi tạo thành công. Lưu điểm ẩn được khi chưa có vùng/bài; bật hiện là bước riêng giữ đủ A02.5. Giữ nguyên dấu/chữ hoa hiển thị; trùng tên sau chuẩn hóa không phân biệt hoa/thường nhưng giữ dấu thì cảnh báo không chặn, phân biệt bằng mã. |
| Soạn chưa lưu | Q2 bảo vệ ba loại công việc trong tab; UI-AP05 thêm phần tham chiếu đang nhập thuộc công việc soạn, không mọi biểu mẫu hoặc kho tạm dài hạn. Giữ tạm khác lưu/áp dụng; chỉ báo Đã giữ tạm khi có căn cứ. |
| Hết phiên/đăng nhập lại | Chặn tác vụ cần quyền, không xóa tạm chỉ vì hết phiên. Đăng nhập lại cùng tài khoản khôi phục mốc tạm gần nhất đúng công việc, rõ chưa lưu; tài khoản khác không thấy tạm người trước, bản tạm cũ bị loại bỏ. Không tự Lưu/Áp dụng/Tạo audio hoặc hồi sinh phiên cũ. |
| Lưu lỗi/mất mạng/tải lại bình thường | Giữ dữ liệu đang soạn/bản tạm, báo đúng chưa lưu/chưa xác nhận đã lưu; tải lại cùng tab khôi phục mốc đã ghi nhận, cần xác thực cùng tài khoản khi cần. Nối lại kiểm tra phiên/trạng thái rồi thử lại chủ động, không ghi bù hoạt động hoặc gửi lại tác vụ TTS tự động. |
| Điều hướng/đăng xuất/Bỏ thay đổi | Trước khi làm mất nội dung chưa lưu, cho quay lại soạn hoặc xác nhận bỏ. Lưu thành công hoặc xác nhận bỏ thì xóa tạm của công việc tương ứng; đăng xuất xác nhận xóa tạm trong phạm vi Q2. Không xóa bản nghiệp vụ đã lưu vì dọn tạm. |
| Đóng tab hoặc sự cố | Kết thúc phạm vi phục hồi được cam kết; không cung cấp kho tạm dài hạn cho lần mở mới, không cam kết qua tab đã đóng/crash/thiết bị khác. Có thể cảnh báo khi trình duyệt cho phép, không hứa biết chính xác lúc đóng/xóa vật lý. TDD thiết kế việc kết thúc vòng đời; TTS đã nhận vẫn tiếp tục A04.4. |
| Dữ liệu đích thay đổi/xóa | Kiểm tra khi khôi phục, không phục hồi điểm bị xóa/gộp theo tên/ghi đè im lặng. Thông báo để Admin đối chiếu trước lưu/áp dụng hoặc bỏ phần tạm không còn điểm đích. |
| Hoạt động Admin chủ động | Nhập/sửa, vẽ, chọn chức năng/đối tượng/ngôn ngữ/bộ lọc/tìm kiếm, mở/xem trước/chủ động cuộn, lưu/áp dụng/ẩn-hiện/xóa/xác nhận, yêu cầu tạo/kiểm tra/thử lại và Phát/Tạm dừng/tua nghe thử được ghi nhận trong phiên còn hạn thì bắt đầu lại 30 phút. |
| Hoạt động tự động/thụ động | TTS/tác vụ hệ thống, tự tải trạng thái/làm mới/giữ tạm, audio tự tiếp tục/tải đệm/phát hết, chỉ mở trang/đổi tiêu điểm/con trỏ không tính. Hành động sau hết hạn không gia hạn, phải đăng nhập lại; mất mạng không ghi bù để hồi sinh phiên. |

Ví dụ: hoạt động cuối được ghi nhận 09:20 → hết hạn 09:50 nếu không có thao tác tiếp. TTS chạy nền không đổi mốc; nếu Admin chủ động hành động lúc 09:25 trước hết hạn thì mốc mới 09:55. Giữ tạm theo tab là phạm vi bản soạn, không tự đặt mô hình phiên Admin theo tab.

<a id="bf-5"></a>

## 5. Quy tắc liên quan giữa khách và Admin

| Thay đổi / tình huống | Hiệu lực đối với khách / dữ liệu | Nguồn |
| --- | --- | --- |
| Lưu/sửa văn bản nháp hoặc tạo/nghe thử audio | Giữ bản hợp lệ đang áp dụng; lưu nháp, tạo thành công, được duyệt và đã áp dụng là các trạng thái khác nhau. | A03, A04.1–A04.3, D09 |
| Áp dụng nội dung đã được duyệt hoặc tạm ẩn điểm | Hiệu lực lần tải/chọn tiếp theo, không cập nhật tức thì/ép kết thúc; phần hợp lệ đã tải tiếp tục trong hạn phiên, cộng ngoại lệ đúng bài đang phát khi hết hạn theo P01/UI-02. Ẩn không phải thu hồi ngay; không cấp quyền mở bài khác sau hết hạn. | A07 |
| Áp dụng cặp mới / audio thực tế lỗi sau áp dụng | Chưa đủ audio khớp/duyệt thì chưa áp dụng; không phát hành nửa cặp. Cặp đã áp dụng nhưng audio thực tế thiếu/lỗi vẫn giữ văn bản đọc và thông báo/thử lại theo D03/BS/P04 đúng quyền; không dùng audio cũ/sai ngôn ngữ. | UI-04, A04.3/A07, P03/P04 |
| Sửa vùng của điểm đang hiện | Giữ vùng hiện hành khi sửa/lưu; xem trước/xem xét rồi chủ động Áp dụng. Khách nhận vùng mới lần tải bản đồ tiếp theo; không ngắt audio/ép kết thúc. | A02.6 |
| Bật điểm mới | Mặc định ẩn; cần thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng và ít nhất cặp vi đã duyệt/áp dụng. Không chờ đủ bốn ngôn ngữ; cặp áp dụng không tự bật hiện. | A02.5, UI-04 |
| Xóa điểm | Xử lý phụ thuộc có kiểm soát, không xóa lịch sử còn hạn; nhận biết điểm cũ và không gộp điểm mới trùng tên. File có quy trình dọn riêng. Điểm không khả dụng báo/chọn khác. | A02.1–A02.3, D01-BS |
| Hết phiên khách lúc có bài đang phát | P01/UI-02: phiên vẫn hết hạn nhưng đúng bài đó có thể nghe đến cuối/pause/resume; TECH-04 tải trọn MP3 cho tab trước phát; không tua/Nghe lại/chọn điểm/ngôn ngữ hoặc khôi phục sau tải lại. Đang tải/chưa phát/pause trước mốc không đủ điều kiện, phản hồi muộn không phát. Mở form mới dừng bài cũ. Không phiên/lượt mới hoặc đổi mốc 30 ngày; bảo vệ file không gia hạn phiên. | D04/D08, P01/UI-02, A02.3 |
| Hết phiên Admin khi đã tiếp nhận tạo audio | Tác vụ tiếp tục, đăng nhập lại xem kết quả; không tự áp dụng cho khách. Điểm bị xóa không phục hồi do kết quả đến muộn. | A04.4 |
| Thêm địa điểm hoặc xóa dữ liệu đến hạn | Không thêm QR riêng/tự tăng bộ bài chuẩn; thống kê chỉ dữ liệu còn giữ, không đồng nhất phiên với người hoặc lượt xem với nghe. | A05/A06, D06/D08 |
| Giữ tạm/khôi phục công việc Admin | Theo Q2 giữ bản đang soạn, không tự Lưu/Áp dụng hoặc gửi lại TTS; giữ bản hợp lệ của khách, không hồi sinh điểm bị xóa. Tác vụ TTS đã nhận tiếp tục nhưng không gia hạn Admin theo Q3. | Q2/Q3, A02.6/A03/A04.4 |

**Chính sách bổ sung theo DATA-01, không đổi các mốc gốc:** file hoàn chỉnh chưa áp dụng chỉ tính dọn sau 24 giờ từ khi xác nhận không tham chiếu/không dùng chung/không tác vụ dùng; nháp còn tham chiếu giữ. Workspace/tạm sau 1 giờ khi job đã kết thúc/không worker/đủ an toàn; đang chạy/chưa rõ kết quả không là rác. Log kỹ thuật 7 ngày, không secret/thông tin khách/nội dung nhạy cảm. Backup mỗi bản 7 ngày từ tạo, bảo vệ/dọn đúng hạn; restore riêng lọc phiên/lượt quá hạn theo mốc gốc trước phục vụ, chấp nhận bản sao có thể còn gần 37 ngày. Giữ 30 ngày D08/8 giờ A02.3 và nghe tiếp; DATA-03 bổ sung backup DB + WAV/MP3 đang tham chiếu + manifest phục hồi, cuối ngày có thay đổi và trước triển khai/thay cấu trúc DB. Lưu cục bộ tách dữ liệu phục vụ, không yêu cầu ngoài laptop; path/quyền/công cụ/checkpoint nhất quán, chi tiết log và RPO-RTO còn thiết kế/kiểm.

**Bổ sung LIFE-P01/DATA-05 đã xác nhận:** vé ký 24 giờ từ cấp gắn requestId/quyền tab/epoch; tạo thành công lưu key UNIQUE + thông tin đối chiếu cùng session một TX. Gửi lại đúng yêu cầu trả ID/mốc gốc sau kiểm quyền/yêu cầu kể cả vé đã hết hạn; chưa session thì chỉ tạo vé còn hạn. D08 giữ ledger cùng phiên, khi dọn vé đã hết hạn nên không tạo lại. Pending không tự cấp vé khác/tạo phiên khác; chưa gửi vé hết thì chuẩn bị vé mới nền/giữ form. Không xác định/tiếp tục được thì báo phù hợp và khách chủ động xác nhận lần mới, không khẳng định sai phiên đã hết hạn. Không generation/context/đồng hồ mới, 24 giờ vé không đổi hạn phiên/window Admin.

**Bổ sung DATA-04 đã xác nhận 04/11:** window lệnh Admin 24 giờ từ cấp không gia hạn qua login/không thực hiện lại sau hạn; job metadata cũ bảy ngày từ terminal rõ chỉ dọn nếu hết ref/use/current cần giữ, khác file/text/audio. Backup lúc không demo tạm ngừng phục vụ, dừng app/ghi/dọn có kiểm soát giữ MySQL để lấy DB/audio khớp; TTS đã nhận không mất/gửi lại mù, mốc phiên giữ. Restore thật mới thu hồi quyền/window cũ trước mở, khách xác nhận phiên mới/Admin login/đối chiếu; giữ lịch sử/mốc gốc, không endedAt giả hoặc xóa Blob tức thì. Dữ liệu sau B có thể mất. Quy trình/drain/kiểm thực tại operations, chưa thời lượng đo đạt.

**Làm rõ hiện hành TECH-04/DATA-02:** mỗi tab Bắt đầu có ID/credential riêng được backend kiểm, trùng tên không gộp; đổi tiếng/kết thúc A không đổi B, mở/reload không tự tạo phiên. Khách tải trọn MP3 hiện tại cho tab rồi phát; chỉ bài đang phát tại expiry được pause/resume, không cấp cho pause trước mốc/đang tải/response muộn. Vào form mới dừng/nhả A1 phù hợp, xác nhận phiên mới rồi chọn vùng lấy cặp hiện hành A2; không auto-play vì reload. Backup phiên bản nghiệm thu được lưu cục bộ tách dữ liệu chạy, không mục tiêu cứu mất/hỏng toàn laptop; vẫn bảy ngày/restore riêng/lọc mốc gốc. Cách làm cụ thể theo TDD dự thảo, không bằng chứng đã chạy.

<a id="bf-6"></a>

## 6. Phần còn chờ thiết kế hoặc rà soát

- Hình dạng/ranh giới các vùng, gồm năm vùng của bộ nghiệm thu, phải được thiết kế và người dùng xem xét riêng. Khả năng vẽ/sửa vùng trực tiếp và điều kiện hiện điểm mới đã chốt theo A02.4/A02.5; hiệu lực bản sửa vùng đang hiển thị đã chốt riêng tại A02.6: lưu bản sửa khác áp dụng, khách nhận vùng mới ở lần tải bản đồ tiếp theo. Quy tắc phân biệt chạm với kéo/phóng, chồng lấn và ưu tiên lựa chọn mới nhất đã chốt; không mặc định bản đồ/ranh giới hiện có đã đáp ứng.
- Hướng bố cục/box cuộn, nút chung ba trạng thái, ngôn ngữ chung và xử lý Bắt đầu-Kết thúc đã chốt theo UI-01. Thiết kế chi tiết/kích thước/đồ họa, câu chữ và bản dịch chưa được duyệt riêng. Giữ câu vi D04-BS/P02 làm căn cứ nghĩa; en/fr/ru dùng bản dịch tương ứng được rà soát trước dùng.
- UI-P01/UI-P02/UI-P04 nay đã chốt đúng hành vi UI-02, không còn là câu hỏi cần duyệt lại. Thiết kế chi tiết/ảnh, cách đóng khác chưa nêu và các phần mở khác tiếp tục xem xét riêng.
- Nhận biết phiên theo tab, mốc khôi phục audio, chống ghi lượt lặp, ký tự hiển thị, xử lý tab nền và tự xóa là phần TDD; log/bản sao lưu còn cần làm rõ ở thiết kế kỹ thuật. Luồng Admin đã duyệt về nghiệp vụ tại mục 4. Không dùng các mục này để thay đổi các quyết định nghiệp vụ đã chốt.
- Năm bài vi vẫn là bản nháp chờ người dùng cung cấp và rà soát. Chốt luồng không chứng minh bản thuyết minh/bản dịch/audio đã được duyệt hoặc kiểm chứng.

- TECH-03 đã chọn mốc phiên bản, nền tảng thiết bị thực, WAV/MP3/FFmpeg-Linux; ngrok chỉ chọn thử. TECH-05 đã chọn CD riêng, DATA-03 đã chọn phạm vi/nhịp backup; còn tương thích/quota/chất lượng TTS/bản build/URL, kiểm SSH/quyền Docker và path/quyền/công cụ/checkpoint backup-log/RPO-RTO/cơ chế. Không dùng nguồn demo thành phê duyệt.
- Phạm vi giữ tạm/hoạt động Admin đã chốt Q2/Q3, UI-04 bổ sung phần tham chiếu trong công việc và dừng nghe thử khi hết phiên; chỉ cơ chế lưu/khôi phục/dọn vòng đời tạm và ghi nhận sự kiện để thiết kế. Lịch dọn/kiểm tra tham chiếu/file chưa từng áp dụng và cơ chế giữ trọn MP3/Blob trong tab theo TECH-04 cần kiểm browser, không còn chọn giữa full-download với tải tiếp sau expiry. File tồn tại không tự chứng minh khách phát liên tục được (A02.3/A07).
- Trường/quy tắc thông tin địa điểm hợp lệ đã chốt Q1; bố cục biểu mẫu/nhãn, cách kiểm tra kỹ thuật, URL QR thực tế/thư viện và kiểm tra bản in còn công việc sau; hướng QR đơn giản/PNG/bản in đã chốt UI-05. Không thêm dịch vụ chuyển hướng.
- Chi tiết điểm còn mở tập trung tại [decisions.md mục 6](decisions.md#dec-6); [PRD.md](PRD.md) mục 9 giữ O-01–O-06 ở trạng thái cần làm rõ, không biến thành điều kiện bắt buộc chưa được duyệt.

**Đề xuất bố cục cảnh báo, chưa được duyệt:** một dải thông báo nhỏ trong luồng trang, giữ nhìn thấy khi cần, không tự bật lại thành nhiều thông báo; khi quay lại tab chỉ bảo đảm dải hiện tại thể hiện đúng trạng thái. Không đề xuất thêm đếm ngược từng giây, hộp thoại bắt buộc hay nút gia hạn. Cách đo thời gian và xử lý tab ở nền sẽ cụ thể hóa ở TDD; bảng trên là quy tắc nghiệp vụ, chưa phải bằng chứng đã chạy kiểm thử.

<a id="bf-7"></a>

## 7. Liên kết tài liệu tiếp theo

Mục lục và hướng dẫn bàn giao theo đường dẫn mới: [docs/README.md](../README.md). Đường dẫn cũ trong nhật ký chỉ là dấu vết lịch sử.

| Tài liệu | Trạng thái / cách dùng |
| --- | --- |
| [decisions.md mục 2](decisions.md#dec-2) | Chỉ mục quyết định hiện hành; nội dung chính tại mục 3–5, điểm mở mục 6, lịch sử mục 7–8. |
| [PRD.md](PRD.md) | Bản hiện hành giữ PRD-01/UI-01–UI-05 và các xác nhận TECH/DATA; giữ mã FR/UC/AC, chỉ phần đã xác nhận được chốt. Mục 9 còn mở, không là bằng chứng phần mềm. |
| [ui-design.md](../04-giao-dien/ui-design.md) | Đọc phiên bản/trạng thái tại đầu file: hướng lượt 1–3 đã xác nhận đúng UI-03–UI-05; UI-A21 là box, công việc còn lại trước/trong TDD tại mục 12. Ảnh cũ/bản đồ không được duyệt ngầm; lịch sử UI giữ nguyên. |
| [prd-review.md](prd-review.md) | R01–R05/R07 và S01/S02 đã xử lý tài liệu; Q1–Q3 đã xác nhận. R06 còn phụ thuộc cần xác nhận/thiết kế, không đóng theo PRD-01. Báo cáo ghi nhận phạm vi duyệt PRD 0.4.1 và các loại trừ; không đồng nghĩa phần mềm đã đáp ứng. |
| [roadmap.md](../01-tong-quan/roadmap.md) / [TDD.md](../03-ky-thuat/TDD.md) | Roadmap giữ 11 task/G02/lịch dự kiến và snapshot GitHub có ngày; TDD cùng mô hình/API/operations là thiết kế dự thảo. Bằng chứng test skeleton/HTTP và điểm dừng triển khai cập nhật tại [handoff](../01-tong-quan/handoff.md), không suy ra nghiệp vụ đã kiểm đạt. |

UI-01–UI-04 đã được xác nhận đúng phần; ảnh khách 0.2 chỉ mô tả bố cục cơ sở, xem sơ đồ box hiện hành. Hướng Admin lượt 1–3 đã chốt đúng phạm vi UI-03–UI-05. Đọc [ui-design mục 12](../04-giao-dien/ui-design.md#ui-next-technical) về các việc còn lại trước/trong TDD; không hỏi duyệt lại hành vi đã rõ. Thiết kế cụ thể/nền/vùng chưa duyệt theo gói; năm bài vi vẫn chờ cung cấp/rà soát. Không tự chọn TTS, công nghệ hoặc tuyên bố đã kiểm chứng. Bàn giao nêu đường dẫn, trạng thái, quyết định đã chốt, điểm mở và bước tiếp theo; không giả định đợt bàn giao khác có cùng phiên bản file, không sửa yêu cầu để hợp thức hóa code.

Quyền thực hiện và quy trình hướng dẫn hiện hành có một vị trí chính tại [handoff mục 7](../01-tong-quan/handoff.md#guidance-protocol), đã được người dùng cập nhật ngày 05/11/2026. Dẫn chiếu này thay đoạn quyền cũ; không sửa yêu cầu nghiệp vụ trong Business Flow.

<a id="bf-8"></a>

## 8. Nhật ký



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-02-nghiep-vu-business-flow-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
