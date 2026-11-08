> **Tổ chức lưu trữ 05/11/2026:** chuyển từ docs/04-giao-dien/ui-design-history.md; giữ nguyên nội dung bản cũ và trạng thái theo thời điểm. [Tài liệu hiện hành](../04-giao-dien/ui-design.md) mới là nguồn dùng triển khai. [Mục lục lưu trữ](README.md). Không duyệt lại mô hình/UI hoặc phục hồi phương án cũ.

# Lịch sử thiết kế giao diện khách

**Lưu ngày 31/10/2026:** nguyên bản ui-design.md 0.1 đọc trực tiếp trước xác nhận UI-01. Các nhãn dự thảo/chờ duyệt trong nguyên bản mô tả trạng thái tại lúc đó; hiện hành xem [ui-design 0.2](../04-giao-dien/ui-design.md) và [UI-01](../02-nghiep-vu/decisions.md#ui-guest-confirmed). Bố cục/cuộn, nhãn thường trực và UI-P03 cũ đã bị thay đúng phần; không áp bản lưu này như yêu cầu đang có hiệu lực. Nội dung dưới được giữ nguyên để bảo vệ chỉnh sửa trước lượt cập nhật, không suy đoán lần sửa đã hoàn tác ở chat khác.

**Ghi chú tổ chức 01/11/2026:** file lịch sử chuyển sang docs/04-giao-dien; chỉ điều chỉnh đích liên kết sang tài liệu hiện hành theo đường dẫn mới, giữ nội dung/phương án/trạng thái đã ghi ở bản gốc. Không dùng bản lưu này như yêu cầu song song. Mục lục tại [docs/README.md](../README.md).

---

# Phác thảo giao diện — Khách tham quan

**Phiên bản thiết kế:** 0.1 — 31/10/2026  
**Trạng thái:** **DỰ THẢO — CHỜ NGƯỜI DÙNG GÓP Ý**, chưa được duyệt theo PRD-01.  
**Cơ sở:** [PRD 0.4.1](../02-nghiep-vu/PRD.md), phạm vi yêu cầu đã rõ được duyệt theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval); [Business Flow khách](../02-nghiep-vu/business-flow.md#bf-3), [quyết định D/BS/P](../02-nghiep-vu/decisions.md#dec-4). Đã đọc trực tiếp bốn tài liệu hiện hành trước soạn.  
**Phạm vi lượt này:** màn hình, bố cục sơ bộ, luồng/trạng thái khách; chưa thiết kế Admin, ranh giới thật, bộ màu/đồ họa hoàn chỉnh hoặc giải pháp kỹ thuật. Không có nội dung thuyết minh thật, không lựa chọn công nghệ/TTS hoặc bằng chứng phần mềm.

**Cách đọc:** “Đã chốt” chỉ hành vi dẫn từ nguồn; toàn bộ cách bố trí/hiển thị ở tài liệu này vẫn là thiết kế đề xuất. **UI-P01–UI-P04** tại mục 6 là những chi tiết hành vi **chờ người dùng quyết định**, chưa được đưa vào BR/FR/UC/AC bắt buộc. Mã UI-K chỉ nhận diện phác thảo, không thay mã yêu cầu hay quyết định hiện hành.

**Mục lục:** [1. Màn hình](#ui-screens) · [2. Hành trình](#ui-flow) · [3. Bố cục](#ui-layout) · [4. Tương tác/audio](#ui-interactions) · [5. Trạng thái](#ui-states) · [6. Chờ quyết định](#ui-proposals) · [7. Truy vết/bước sau](#ui-trace).

<a id="ui-screens"></a>

## 1. Màn hình và mục đích

Khuyến nghị một trang bắt đầu và một trang tham quan gồm bản đồ cùng khu vực bài/audio. Các bảng chọn/thông báo dưới đây là trạng thái của trang; không yêu cầu mỗi trạng thái là một URL hoặc công nghệ cụ thể.

| Mã | Màn hình / lớp trạng thái đề xuất | Mục đích và nội dung chính | Hành vi nguồn đã chốt |
| --- | --- | --- | --- |
| UI-K01 | Bắt đầu tham quan | Tên bắt buộc; bộ chọn vi/en/fr/ru; nút “Bắt đầu tham quan”; phản hồi nhập liệu/bắt đầu. | FR-K01/K02, UC-K01; D02/BS, D03-BS, D04, P02 |
| UI-K02 | Bản đồ, chưa chọn địa điểm | Tên khách/lựa chọn ngôn ngữ; sơ đồ với các vùng hiện hành; hướng dẫn chạm trong vùng; “Kết thúc tham quan”. | FR-K03/K09; D01/BS, P07/P08 |
| UI-K03 | Bản đồ và bài đang chọn | Cùng trang UI-K02, thêm tên điểm/văn bản/audio/ngôn ngữ/trạng thái; vùng đang chọn được phân biệt. | FR-K03–K08; UC-K02–K04 |
| UI-K04 | Danh sách ứng viên khi vùng chồng lấn | Chọn trong đúng các vùng khớp lần chạm; chưa thay bài/audio trước khi khách chọn ứng viên. Có đề xuất “Hủy” tại UI-P01. | P08, AC-K09; cách hủy chưa chốt |
| UI-K05 | Phiên hết hạn | Thông báo, giữ bài đã tải để đọc; khóa nghe/tua/chọn vùng/ngôn ngữ; “Bắt đầu phiên mới”. | P01/P02, AC-K18/K19 |
| UI-K06 | Sau chủ động kết thúc | Xác nhận đã kết thúc và lối sang UI-K01; bố cục/chuyển trang cụ thể tại UI-P04. | P02, AC-K20 đã chốt về kết thúc/dừng/xóa thông tin tab; giao diện đích còn chờ quyết định |

QR chung và đường dẫn dự phòng được đặt trên bản in/điểm truy cập ngoài trang bắt đầu; phác thảo không thêm màn hình quét QR trong ứng dụng hoặc QR riêng từng địa điểm. Không tự tạo phiên khi quét/mở URL.

<a id="ui-flow"></a>

## 2. Hành trình từ QR đến kết thúc

1. **Quét QR hoặc mở URL in kèm** → UI-K01. Chọn sẵn tiếng Việt; khách nhập tên và có thể đổi ngôn ngữ.
2. **Bấm “Bắt đầu tham quan”** → kiểm tra theo BR-01; chỉ khi bắt đầu thành công mới tạo phiên của tab và mở UI-K02. Tên trùng không bị gộp; lỗi cho sửa/thử lại.
3. **Chạm bất kỳ vị trí bên trong một vùng** → UI-K03 của điểm tương ứng. Nếu chạm khớp nhiều vùng, qua UI-K04 chọn ứng viên trước. Không yêu cầu chạm chính xác nhãn/biểu tượng.
4. **Chọn vùng lần đầu của lần mở hoặc đổi điểm** → hiển thị văn bản đúng ngôn ngữ và yêu cầu phát audio từ đầu khi được phép/sẵn sàng; đổi điểm dừng bài cũ. Chỉ văn bản tải thành công của lần mở đủ điều kiện mới tính lượt.
5. **Nghe/đọc** → một Phát/Tạm dừng và thanh tua; tạm dừng giữ mốc, Phát tiếp tục. Bấm lại vùng đang chọn theo P06, không mặc định phát lại.
6. **Đổi ngôn ngữ tại điểm đang xem** → giữ điểm/phiên; dừng bài cũ, về đầu bài mới và **không tự phát**. Khách chủ động Phát; thiếu dịch/audio theo mục 5.
7. **Tiếp tục chọn vùng khác** hoặc tải lại tab → chọn vùng khác yêu cầu phát từ đầu; tải lại thì khôi phục cùng bài ở trạng thái tạm dừng, không cộng lượt. Hai hành động này khác nhau.
8. **Còn trên 0 đến 5 phút** → hiện cảnh báo nhẹ đúng câu đã chốt; không ngắt nghe, che nội dung hay buộc đóng hộp thoại.
9. **Chủ động kết thúc** → dừng audio, kết thúc phiên và xóa tên điền sẵn/trạng thái khôi phục tab; bản ghi Admin vẫn theo D08. Đề xuất giao diện UI-K06 chờ UI-P04.
10. **Hoặc đủ 8 tiếng** → UI-K05, không tự gia hạn/tạo phiên; giữ văn bản đã tải. Khách chọn “Bắt đầu phiên mới” để vào UI-K01: **điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới.**
11. **Không tìm thấy/không tiếp tục được phiên trước** → thông báo đúng câu P02, lối bắt đầu mới; không tìm bằng tên hoặc khẳng định đã hết hạn khi thiếu căn cứ. Phiên mới chỉ có khi bấm bắt đầu thành công.

Đếm phiên/lượt tuân BR-02/07/08, AC-K21/K22; phác thảo không thêm lượt cho cuộn, bảng ứng viên, audio, tải lại hoặc đổi ngôn ngữ sau lượt đã tính. Một phiên không phải một người duy nhất. Chỉ đóng trang không chứng minh thời điểm kết thúc chính xác.

<a id="ui-layout"></a>

## 3. Bố cục phác thảo trên điện thoại

### 3.1. Trang bắt đầu — UI-K01

```text
┌────────────────────────────────────┐
│ THUYẾT MINH KINH THÀNH HUẾ          │
│ Bắt đầu tham quan                  │
│                                    │
│ Tên hiển thị *                     │
│ [ Nhập tên của bạn               ] │
│ Tên dài 1–50 ký tự.                │
│ [Thông báo lỗi tại đây nếu có]     │
│                                    │
│ Ngôn ngữ                           │
│ [ Tiếng Việt (vi)              ▾ ] │
│                                    │
│ [       Bắt đầu tham quan        ] │
└────────────────────────────────────┘
```

- Nhãn ngôn ngữ đề xuất: Tiếng Việt (vi), English (en), Français (fr), Русский (ru). Chỉ bốn lựa chọn đã chốt.
- Chuẩn hóa/kiểm tra tên đúng BR-01: khoảng trắng, Unicode/số/dấu câu, 1–50 ký tự hiển thị; không bỏ qua xuống dòng/ký tự điều khiển để hợp thức hóa tên. Không thêm xác minh tên thật hoặc tài khoản khách.
- Đề xuất đặt lỗi ngay dưới ô tên, giữ dữ liệu khách nhập để sửa; khi đang bắt đầu thể hiện “Đang bắt đầu…”, khi lỗi không hiện đã có phiên thành công. Câu lỗi chưa có nguyên văn là thiết kế câu chữ, chưa được duyệt riêng.
- Khi bắt đầu lại do hết hạn dùng cùng bố cục với thông tin điền sẵn theo P02; khách sửa/xác nhận. Khi chủ động kết thúc, tên điền sẵn và trạng thái khôi phục phải đã xóa; không hồi sinh phiên cũ.
- Không có nút sửa tên ngay trong phiên; muốn đổi tên đi qua kết thúc rồi bắt đầu phiên mới.

### 3.2. Trang tham quan — UI-K02/UI-K03

```text
┌────────────────────────────────────┐
│ KINH THÀNH HUẾ                     │
│ Xin chào, [Tên khách]              │
│ Ngôn ngữ [Tiếng Việt (vi)       ▾] │
│                 Kết thúc tham quan │
├────────────────────────────────────┤
│ [Dải cảnh báo/lỗi khi cần]         │
├────────────────────────────────────┤
│ BẢN ĐỒ MINH HỌA                    │
│ Chạm vào một vùng để nghe.         │
│                                    │
│   ╱ Vùng A ╲      ┌ Vùng B ┐      │
│   ╲________╱      └────────┘      │
│        ╱──── Vùng C ────╲         │
│        ╲_______________╱         │
│   ┌ Vùng D ┐       ╱ Vùng E ╲     │
│   └────────┘       ╲________╱     │
│                                    │
│ Sơ đồ chọn địa điểm,               │
│ không dùng chỉ đường thực tế.      │
├────────────────────────────────────┤
│ [Tên địa điểm đang chọn]           │
│ Văn bản: Tiếng Việt (vi)           │
│ Audio: Tiếng Việt (vi)             │
│ Trạng thái: [Đang phát / …]        │
│                                    │
│ [Phát hoặc Tạm dừng]               │
│ 00:00 ──────●────────────  [Độ dài] │
│                                    │
│ [Văn bản được phép hiển thị…]      │
└────────────────────────────────────┘
```

**Đây chỉ là sơ đồ bố trí:** A = Cổng Ngọ Môn; B = Điện Thái Hòa; C = Tử Cấm Thành; D = Cửu Đỉnh; E = Cung Diên Thọ. Vị trí tương đối, hình dạng và kích thước ký tự ở đây **không biểu diễn địa lý thực hoặc ranh giới đề nghị áp dụng**; chưa là bản thiết kế năm vùng để duyệt.

- Đề xuất bản đồ toàn chiều ngang phần nội dung; bài/audio bên dưới trong luồng cuộn trang, không phủ lên vùng. Vùng có nhãn và đường viền, thêm dấu hiệu vùng đang chọn; màu sắc/đồ họa/thứ tự thực tế sẽ xem xét sau.
- Toàn bộ phần bên trong hình là vùng chạm. Không biến nhãn thành mục tiêu bắt buộc duy nhất. Tử Cấm Thành chọn một vùng/một bài tổng quan mỗi ngôn ngữ.
- Chỉ hiển thị các điểm/vùng được phép truy cập theo dữ liệu bản đồ hiện hành. Sơ đồ năm vùng minh họa bộ chuẩn, **không đặt trần năm vùng**; điểm Admin thêm chỉ hiện khi đủ A02.5. Không suy ra phải có quản lý thêm ngôn ngữ.
- Trước khi chọn: khu vực bài là hướng dẫn nhẹ. Sau chọn: tên điểm/ngôn ngữ/trạng thái phát ở gần điều khiển, văn bản bên dưới; không đưa bản nháp hoặc thông tin nội bộ duyệt ra khách.
- Giữ bản đồ trên cùng trang khi đang đọc để cuộn lên chọn vùng. Cuộn xem bản đồ không đóng lần mở, không tạo lượt; không tự thêm nút “Mở lại”/tác vụ riêng khi chỉ cuộn. Lối “Quay lại bản đồ” của nhánh thiếu bản dịch dùng đúng D03, khác với cuộn trong bài.
- Thời gian hiện tại/tổng độ dài chỉ thể hiện theo audio khi có thông tin; chưa gán thời lượng mẫu thành yêu cầu hay ngưỡng nghiệm thu.
- Bố cục rộng hơn có thể đặt bản đồ và bài cạnh nhau như một hướng thiết kế, chưa là yêu cầu hỗ trợ thêm thiết bị bắt buộc. Danh sách thiết bị/trình duyệt nghiệm thu vẫn O-03.

### 3.3. Danh sách vùng chồng lấn — UI-K04

```text
┌────────────────────────────────────┐
│ Chọn địa điểm                      │
│ Vị trí chạm khớp nhiều vùng:       │
│ [ Tên ứng viên 1                 ] │
│ [ Tên ứng viên 2                 ] │
│                                    │
│ [Hủy — đề xuất UI-P01]             │
└────────────────────────────────────┘
```

P08 đã chốt phải hỏi ứng viên trước khi thay đổi nội dung/audio, không chọn tùy ý. Đề xuất bảng chọn gọn; chỉ chứa vùng ứng viên thực tế của lần chạm. Khi chưa chọn ứng viên, không phát một bài mới hoặc cộng lượt chỉ vì bảng xuất hiện. Hủy/đóng và việc tiếp tục audio cũ trong lúc bảng mở là **UI-P01 chờ quyết định**, không phải hành vi đã được duyệt sẵn.

<a id="ui-interactions"></a>

## 4. Tương tác vùng và audio — giữ quy tắc đã duyệt

| Hành động | Hành vi bắt buộc từ nguồn | Cách thể hiện đề xuất |
| --- | --- | --- |
| Chọn vùng lần đầu của lần mở / vùng điểm khác | Đúng điểm/ngôn ngữ, yêu cầu phát từ đầu khi sẵn sàng; đổi điểm dừng bài cũ. | Tô dấu vùng mới, tên bài đổi và hiện trạng thái tải riêng cho văn bản/audio; không để bài cũ mang tên điểm mới. |
| Bấm lại vùng đang chọn trong cùng lần mở | Đang phát tiếp tục; tạm dừng tiếp tục mốc giữ; hết bài phát từ đầu; đang tải không lặp; không cộng lượt. | Không nháy lại khung bài hoặc đưa thanh tua về đầu khi chỉ tiếp tục. |
| Chạm ngoài vùng | Không đổi điểm/dừng/phát lại/audio/lượt hoặc chọn gần nhất. | Giữ trang; chỉ giữ hướng dẫn ban đầu nếu chưa chọn, không báo lỗi từng chạm. |
| Kéo/phóng/thu hoặc cuộn | Không vô tình chọn vùng/phát audio. | Tách thao tác di chuyển với chạm; chưa đặt ngưỡng hoặc chọn công nghệ. |
| Chọn liên tục A → B | Lựa chọn mới nhất thắng, tối đa một audio/tab; kết quả A về muộn không đổi nội dung hoặc phát chồng. | Đánh dấu B đang tải; không hiện kết quả A vào khung B. |
| Phát/Tạm dừng | Một nút đổi nhãn, pause giữ mốc, play tiếp tục; có thanh tua. | Đặt nút và thanh tua cạnh trạng thái bài/ngôn ngữ; không thêm nghe lại riêng, tốc độ hoặc nghe nền. |
| Tua trong bài | Chọn được mốc trong audio theo D07. | Chi tiết tua lúc tạm dừng tại UI-P02; hiển thị sau hết bài/nút Phát tại UI-P03 còn chờ quyết định. |
| Đổi ngôn ngữ | Giữ điểm/phiên; dừng bài cũ, về đầu bài mới, không tự phát, kể cả chọn ngôn ngữ thay thế khi thiếu dịch. | Khung văn bản/audio ghi rõ mã/tên ngôn ngữ; sau tải hiển thị “Phát”, không giả đang nghe. |
| Tải lại tab còn hiệu lực | Khôi phục điểm/ngôn ngữ/mốc gần nhất cùng bài, tạm dừng, không tự phát/cộng lượt/gia hạn. | Thông báo gọn nếu phải về đầu vì thiếu mốc/bài đổi. Không hứa chính xác từng giây. |
| Audio lỗi và khách thử lại | Thử lại từ đầu theo D03-BS; khác nhánh tải lại P03. | Nút “Thử lại audio” trong lỗi; không dùng mốc khôi phục cũ cho yêu cầu thử lại này. |

<a id="ui-states"></a>

## 5. Các trạng thái quan trọng

Bố cục/câu chữ chưa có nguyên văn được ghi là đề xuất; hành vi nguồn vẫn áp dụng. Thiếu bản dịch/audio không làm giảm bộ nội dung nghiệm thu 20 văn bản/20 audio.

| Trạng thái | Cách thể hiện đề xuất | Hành vi và giới hạn đã chốt / nguồn |
| --- | --- | --- |
| Đang tải bản đồ | Khung bản đồ ghi “Đang tải bản đồ…”, chưa vẽ vùng như thể đã dùng được; lỗi cho thử lại. | Chỉ chọn được vùng khi bản đồ đã tải; không GPS. UC-K02, P04. |
| Đang tải điểm/audio | Tên điểm lựa chọn mới nhất; khung văn bản và trạng thái audio thể hiện đang tải riêng. Không ghi “Đang phát” khi chưa phát. | Chọn liên tục ưu tiên mới nhất; bấm lại khi tải không tạo yêu cầu lặp. P06/P08; văn bản thành công mới đủ điều kiện lượt. |
| Điểm không còn khả dụng | Thông báo tại khu vực bài và lối chọn điểm khác trên bản đồ. | Không tự chọn gần nhất; thay đổi Admin có hiệu lực lần tải/chọn tiếp theo, không đẩy ngay. D01-BS/A07, AC-K09. |
| Thiếu bản dịch | Ghi tên điểm và ngôn ngữ đang thiếu; các nút/ngôn ngữ chỉ gồm bản văn được phép hiển thị, cùng “Quay lại bản đồ”. Không có ngôn ngữ nào thì lối về bản đồ. | Khách chủ động đổi, không tự chuyển; không hiển thị nháp hoặc phát bài cũ/sai ngôn ngữ. Chọn ngôn ngữ thay thế không tự phát. D03/BS, D07; thông báo thiếu chưa tính lượt. |
| Có văn bản, thiếu audio khớp được phép | Hiện nguyên câu **“Audio chưa sẵn sàng”**, nút Phát vô hiệu; thanh tua không thao tác khi không có bài để tua. | Văn bản vẫn đọc được; không dùng audio bản/ngôn ngữ khác. D03-BS, A04.3; khóa Phát là yêu cầu đã chốt, cách làm mờ/nhãn là phác thảo. |
| Audio tải/phát lỗi, kể cả bị trình duyệt cản phát | Dòng lỗi gần điều khiển và “Thử lại audio”; vẫn giữ văn bản/lựa chọn. | Báo lỗi và thử lại từ đầu, không hứa mọi lần chọn vùng luôn phát được trên mọi trình duyệt. D03-BS, P04, O-03; không chọn giải pháp kỹ thuật trong phác thảo. |
| Mất mạng/lỗi dịch vụ khi trang đang dùng | Dải thông báo trong trang, không phủ nội dung, cho thử lại phần lỗi; giữ dữ liệu đang có. | Không ép dừng phần audio đã tải còn phát được; khi nối lại kiểm tra phiên trước tác vụ, không ghi lặp/gia hạn/offline/gửi bù. P04; không hứa tải lại trang lúc mất mạng luôn khôi phục được giao diện. |
| Chọn vùng chồng lấn | UI-K04 trước khi đổi bài/audio. | P08 đã chốt hỏi ứng viên; hủy/tiếp tục bài cũ khi bảng đang mở là UI-P01. Nếu hết hạn khi bảng mở, không cho chọn vùng tạo tác vụ mới theo P01. |
| Còn trên 0 đến 5 phút | Một dải trong luồng trang: **“Phiên tham quan sẽ hết hạn trong vòng 5 phút”**; không cần nút đóng. | Không che nội dung/ngắt nghe/bắt đóng hộp thoại; một lần trong lần dùng hiện tại, không nhắc lặp; tải lại/quay tab vẫn nhận biết. Không đếm ngược bắt buộc hoặc nút gia hạn. D04-BS. |
| Đủ 8 tiếng, kể cả đang đọc/nghe | UI-K05: đề xuất câu “Phiên tham quan đã hết hạn”; nút **“Bắt đầu phiên mới”**, bài đã tải vẫn còn đọc. | Dừng audio, khóa Phát/Tạm dừng/tua/chọn vùng/đổi ngôn ngữ; không ghi lượt mới/tự tạo/gia hạn. Thông báo hết hạn thay cảnh báo 5 phút; mất mạng không cho vượt hạn. P01. |
| Quay lại phiên hết hạn | UI-K01 sau hành động bắt đầu mới; đề xuất giải thích cần xác nhận lại thông tin. | **Điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới.** P02. |
| Không tìm thấy/không tiếp tục được phiên trước | Hiện đúng **“Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”**, lối mở UI-K01. | Không khẳng định hết hạn thiếu căn cứ, không tìm theo tên, không tự tạo phiên. P02. |
| Chủ động kết thúc | Chỉ khi kết thúc thành công mới báo đã kết thúc; cách trình bày UI-K06 tại UI-P04. | Dừng audio, xóa tên điền sẵn/trạng thái khôi phục tab; không xóa ngay bản ghi Admin. Không coi đóng tab là biết chính xác mốc kết thúc. P02/D08. |

<a id="ui-proposals"></a>

## 6. Bốn chi tiết hành vi chờ người dùng quyết định

**Toàn bộ bảng dưới là đề xuất, chưa áp dụng vào yêu cầu hoặc tiêu chí bắt buộc.** Người dùng có thể duyệt chung hoặc sửa từng mã; không yêu cầu xác nhận lại các quy tắc D/P đã chốt.

| Mã / trạng thái | Phương án khuyến nghị cụ thể | Lý do / ví dụ | Phạm vi ảnh hưởng sau xác nhận |
| --- | --- | --- | --- |
| **UI-P01 — CHỜ QUYẾT ĐỊNH:** hủy bảng vùng chồng lấn | Có nút “Hủy”. Hủy, nút quay lại hoặc thao tác đóng bảng được giao diện hỗ trợ đều chỉ đóng bảng: không đổi điểm/văn bản hoặc tự thay mốc/trạng thái audio đang có tại lúc đóng; nếu chưa chọn thì về bản đồ chưa chọn. Audio đang phát có thể tiến mốc hoặc tự hết bài trong lúc bảng mở; hủy không khôi phục ảnh chụp trạng thái trước khi mở bảng. Trong lúc bảng mở, audio cũ đang phát được tiếp tục; chỉ chọn một ứng viên mới yêu cầu thay nội dung/audio theo quy tắc đã chốt. Không cộng lượt do mở/hủy bảng. Hết hạn vẫn ưu tiên P01, không khôi phục trạng thái phát cũ sau hết hạn. | Khách chạm nhầm có lối thoát, không làm mất bài đang nghe hoặc chọn tùy ý. Ví dụ đang nghe Ngọ Môn, mở bảng ứng viên rồi Hủy thì tiếp tục nghe Ngọ Môn. Không bắt buộc thêm cử chỉ đóng riêng. | O-02; FR-K03/K05; UC-K02; AC-K09. Chỉ bổ sung cách hủy/chờ chọn, không mở lại P08. |
| **UI-P02 — CHỜ QUYẾT ĐỊNH:** tua lúc tạm dừng | Tua chỉ đổi mốc; giữ tạm dừng, nút vẫn “Phát”. Chỉ phát tiếp khi bấm Phát hoặc bấm lại vùng theo P06. Nếu đang phát mà tua thành công thì tiếp tục phát từ mốc mới. Thiếu/lỗi/hết hạn vẫn theo D03/P01. | Khách có thể chọn mốc yên lặng trước khi nghe. Ví dụ pause tại 00:30, tua đến 01:10: vẫn yên lặng cho tới Phát. Các mốc ví dụ không là ngưỡng bắt buộc. | O-02; FR-K05; UC-K03; AC-K12. Không đổi quy tắc pause giữ mốc đã chốt. |
| **UI-P03 — CHỜ QUYẾT ĐỊNH:** hiển thị khi bài phát hết | Hiện “Đã phát hết”, thanh ở cuối, dùng chính nút “Phát”; bấm nút này phát từ đầu. Tua về một mốc trước cuối đưa bài về tạm dừng tại mốc đó, đợi Phát; không tự lặp. Bấm lại vùng vẫn phát từ đầu theo P06. Không thêm nút nghe lại riêng. | Trạng thái kết thúc dễ nhận biết, tránh nhấn Phát mà không có phản hồi. Phần bấm lại vùng đã chốt; chỉ cách hiển thị/nút/tua sau hết bài là đề xuất mới. | O-02; FR-K05; UC-K03; AC-K12/K13, giữ P06. Không cộng lượt vì nghe lại. |
| **UI-P04 — CHỜ QUYẾT ĐỊNH:** giao diện sau chủ động kết thúc | Sau kết thúc thành công, hiển thị UI-K06 gọn: “Đã kết thúc tham quan” và “Bắt đầu phiên mới”; không còn vùng/nút nghe hoặc bài của phiên cũ trên màn hình này. Nút mở UI-K01, tên trống, vi mặc định; khách chọn lại và bấm “Bắt đầu tham quan” mới tạo phiên. Không tự chuyển sang một phiên mới hoặc khôi phục tên/điểm/mốc cũ. Không thêm bước xác nhận kết thúc thứ hai. | Phân biệt đã kết thúc với hết hạn: hết hạn giữ bài đã tải theo P01, còn chủ động kết thúc có màn hoàn tất rõ. Ví dụ muốn đổi tên: Kết thúc → Bắt đầu phiên mới → nhập tên khác. Việc xóa tên/trạng thái tab là yêu cầu P02 đã chốt; UI đích là đề xuất. | FR-K09; UC-K05; AC-K20 và lối bắt đầu UC-K01. Chưa đổi PRD/Business Flow cho phần UI đề xuất này. |

Bốn đề xuất nhất quán: việc hủy/đóng bảng hoặc tua tạm dừng không trở thành chọn vùng mới; nút Phát sau hết bài khác bấm lại vùng nhưng đều có kết quả dự đoán được; hết hạn luôn khóa thao tác dù bảng/audio đang ở trạng thái nào. UI-P04 không thay nghĩa vụ giữ văn bản của **hết hạn**.

<a id="ui-trace"></a>

## 7. Truy vết và việc tiếp theo

| Nhóm phác thảo | PRD / tiêu chí liên quan | Nguồn chính |
| --- | --- | --- |
| UI-K01 và phản hồi tên/ngôn ngữ/bắt đầu | FR-K01/K02; UC-K01; AC-K01–K04 | D01-BS/D02-BS/D03-BS/D04, A05 |
| UI-K02/UI-K03, chọn vùng và đếm lượt | FR-K03/K04/K10; UC-K02; AC-K05–K11/K13/K21/K22; NFR-01/02 | D01/BS, D03/BS, D04/D05/D09, P05–P08, A02.5/A07 |
| Audio/đổi ngôn ngữ/khôi phục/mạng | FR-K05–K08; UC-K03/K04; AC-K12–K16/K21/K22; NFR-04/05/06 | D07, D03-BS, P03/P04/P06/P08, A07 |
| UI-K04 và UI-P01 | FR-K03; UC-K02; AC-K09; O-02 | P08 đã chốt; hủy/đóng chờ quyết định |
| Cảnh báo/UI-K05/UI-K06/bắt đầu lại | FR-K02/K09; UC-K05; AC-K17–K20 | D04-BS, P01/P02, D08; UI-P04 chờ quyết định |
| UI-P02/UI-P03 | FR-K05; UC-K03; AC-K12/K13; O-02 | D07/P06 đã chốt; hành vi bổ sung chờ quyết định |

**Để tiếp tục:** người dùng góp ý hướng bố cục (bản đồ và bài/audio cùng trang), phản hồi UI-P01–UI-P04. Tiếp đó chỉnh phác thảo khách và thiết kế/xem xét bản đồ-vùng cụ thể; phần Admin thực hiện sau khi thống nhất hướng khách. Việc đồng ý bố cục không mặc định duyệt ranh giới thật hoặc bộ đồ họa cuối.

Các phụ thuộc TTS/giọng/chi phí/khả thi bốn ngôn ngữ, môi trường/ngưỡng đánh giá, file/log/bản sao lưu và công việc duyệt nội dung tiếp tục theo [PRD 9](../02-nghiep-vu/PRD.md#prd-open-classification) và [prd-review 7.4](../02-nghiep-vu/prd-review.md#review-open-final). Chưa khảo sát dịch vụ, chưa soạn toàn bộ TDD/roadmap; bước đó chỉ bắt đầu khi được giao. Chưa thao tác code/cấu hình/database/kiểm thử/Git/triển khai.

## 8. Nhật ký thiết kế

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 31/10/2026 | Kiểm tra bốn tài liệu, repo chưa có thiết kế giao diện; tạo bản 0.1 phác thảo khách gồm màn hình/hành trình/khung bản đồ-bài-audio/trạng thái; đề xuất UI-P01–UI-P04 tách riêng. | Toàn bộ thiết kế dự thảo; hành vi nguồn theo PRD-01, phương án mới chưa được duyệt. Không có nội dung thuyết minh hoặc kết quả phần mềm được xác minh. |
