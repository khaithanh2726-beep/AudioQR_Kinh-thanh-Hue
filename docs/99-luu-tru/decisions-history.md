# Lịch sử quyết định và căn cứ xác nhận

**Lưu trữ ngày:** 05/11/2026. **Mục đích:** Quyết định/phương án đã bị thay thế và nguồn xác nhận dài được chuyển nguyên khối từ mục 7–8. Các căn cứ vẫn dùng để truy vết quyết định hiện hành; phương án cũ không được áp lại.
**Nguồn hiện hành:** [mở tài liệu chính](../02-nghiep-vu/decisions.md); kiểm phiên bản/trạng thái trước dùng. Bản lưu không là nguồn triển khai song song hoặc phê duyệt mới. [Mục lục lưu trữ](README.md).

## Mục lục phần lưu

- [decisions-evidence](#decisions-evidence) — từ docs/02-nghiep-vu/decisions.md

<a id="decisions-evidence"></a>

## Phần lưu: decisions-evidence

**Chuyển từ:** docs/02-nghiep-vu/decisions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="dec-7"></a>

<a id="7-quyết-địnhphương-án-bị-thay-thế"></a>

## 7. Quyết định/phương án bị thay thế

**Chỉ tra cứu lịch sử.** Hai bảng dưới đây giữ dấu vết đã lưu từ hai tài liệu; không là hai vị trí quyết định hiện hành. Vị trí chính của mọi mã nằm ở mục 3–5. Số mục trong bản ghi gốc thuộc cấu trúc cũ, xem mục 8.4; không dùng một câu “chưa duyệt” theo thời điểm cũ để mở lại nội dung hiện đã chốt.

<a id="71-dấu-vết-từ-decisionsmd-trước-khi-sắp-xếp"></a>

### 7.1. Dấu vết từ decisions.md trước khi sắp xếp

**KHÔNG DÙNG bảng này như yêu cầu đang áp dụng.** Các đề xuất cũ ở bộ phương án trước được chuyển thành dấu vết dưới đây để tránh cùng tồn tại hai bộ yêu cầu.

| Chủ đề | Phương án/quy tắc trước | Trạng thái hiện tại |
| --- | --- | --- |
| A02 cũ | Chỉ sửa/ẩn đúng năm điểm, chưa thêm ngoài danh sách và không xóa cứng điểm có liên kết. | **Đã bị thay thế:** thêm/sửa/ẩn-hiện/xóa; năm điểm là bộ chuẩn nghiệm thu. Cơ chế giao dịch/khóa ngoại đã chốt trước; A02.1–A02.5 nay đã cụ thể hóa phụ thuộc, giữ lịch sử, dọn file, vẽ vùng và điều kiện hiện. Không yêu cầu duyệt lại cơ chế xóa. |
| A04 cũ | Chỉ tải file audio chuẩn bị sẵn, chưa yêu cầu TTS trong Admin. | **Đã bị thay thế:** chọn khả năng tạo audio từ văn bản mới nhập để nghe thử. Vận hành A04.1–A04.4 đã chốt; nhà cung cấp/giọng/chi phí/công nghệ và bằng chứng bốn ngôn ngữ còn chờ. Không tự giữ luồng tải file thành chức năng bổ sung bắt buộc. |
| A02.4 cũ | Chỉ nhận/gắn cấu hình vùng chuẩn bị ngoài Admin, chưa vẽ trực tiếp. | **Đã bị thay thế:** Admin vẽ trực tiếp, xem trước/lưu và sửa/vẽ lại; không mở thành công cụ bản đồ phức tạp. Bố cục/thao tác/ranh giới cụ thể phải thiết kế/xem xét riêng. |
| A02.1 | Từng được trình như một phương án cần xác nhận lại cơ chế xóa. | **Đã làm rõ:** chỉ cụ thể hóa quy trình ứng dụng điều phối đã chốt trước; không là cơ chế mới cần duyệt lại. |
| Trạng thái chín mục A02/A04 | Trước đây còn đề xuất giữ lịch sử/dọn file/hiện điểm/trạng thái tạo/tác vụ đang chạy. | **Đã chốt A02.1–A02.5/A04.1–A04.4** theo xác nhận đó; nay bổ sung A02.6 và duyệt tổng thể Admin/hai vai trò theo BF-A01/BF-ALL01. Giao diện/ranh giới hoặc TTS cụ thể không được duyệt theo. |
| Trạng thái A01/A03/A05/A06/A07 | Toàn bộ A từng được ghi là đề xuất chưa chốt, sau đó đã chốt từng phần và chờ duyệt tổng thể. | **Đã thay thế trạng thái:** quyết định tại mục 8.1 có hiệu lực; nay BF-A01 duyệt tổng thể Admin, BF-ALL01 duyệt nghiệp vụ hai vai trò. |
| Cách chọn địa điểm | Năm điểm bấm, cần chạm đúng điểm/biểu tượng/tên nếu diễn giải theo cách cũ. | **Đã bị thay thế:** năm vùng tương tác, chạm bất kỳ vị trí trong vùng; hình dạng/ranh giới chờ thiết kế. |
| Phát khi chọn địa điểm | Chọn điểm rồi bấm Phát riêng; D07 từng ghi cả đổi địa điểm và đổi ngôn ngữ đều không tự phát. | **Đã điều chỉnh theo xác nhận mới:** chọn vùng đồng thời yêu cầu phát; chọn vùng khác dừng bài cũ, phát bài mới từ đầu khi sẵn sàng. Chỉ phần đổi ngôn ngữ tiếp tục không tự phát. |
| P05 | Một điểm bấm đại diện/bài tổng quan Tử Cấm Thành còn là đề xuất. | **Đã chốt ngày 30/10/2026:** một vùng tương tác và một bài tổng quan, không tách địa điểm con hoặc đổi 20/20. |
| Điền sẵn khi kết thúc/hết hạn | P02 cũ đề xuất chung điền sẵn tên/ngôn ngữ cho mọi lý do quay lại và dùng câu “Không tìm thấy phiên hiện tại”. | **Đã bị thay thế bởi P02 đã chốt:** hết hạn điền sẵn nếu hợp lệ; chủ động kết thúc dừng audio/xóa tên điền sẵn và trạng thái khôi phục tab, giữ bản ghi Admin theo hạn 30 ngày; không tìm thấy dùng “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”. Không tự chốt thêm mặc định điền sẵn ở trường hợp khác. |
| Phiên giữa tab | Một phiên dùng chung giữa các tab cùng trình duyệt/hồ sơ. | **Đã bị thay thế:** mỗi tab một phiên; chưa yêu cầu đồng bộ tab, theo D04 mới. |
| Hết hạn | Sau 30 phút không hoạt động; hoạt động/audio được nhận biết có thể kéo dài. | **Đã bị thay thế:** đúng 8 tiếng kể từ bắt đầu dù có hoạt động; không gia hạn. Không còn yêu cầu theo dõi hoạt động để gia hạn. |
| Hạn lưu theo mốc cũ | Phiên hết hạn có mốc kết thúc suy từ hoạt động cuối + 30 phút. | **Đã bị thay thế:** phiên hết hạn dùng bắt đầu + 8 tiếng; 30 ngày tính từ kết thúc/hết hạn theo quy tắc mới. |
| Danh sách địa điểm | Ngọ Môn, Điện Thái Hòa, Thế Miếu, Hiển Lâm Các, Cửu Đỉnh. | **Đã bị thay thế:** Cổng Ngọ Môn, Điện Thái Hòa, Tử Cấm Thành, Cửu Đỉnh, Cung Diên Thọ. |
| Điều khiển audio | Có nút nghe lại riêng, đề xuất không cần tua. | **Đã bị thay thế:** một nút Phát/Tạm dừng và thanh tua; không cần nút nghe lại riêng. |
| Giao diện khách | Xem thông tin/trạng thái phiên đầy đủ như một nhu cầu giao diện. | **Đã được thu hẹp/làm rõ:** khách chỉ cần tên và lựa chọn đang dùng; chưa cần trang lịch sử phiên. |
| Chi tiết tải lại | Luôn về đầu bài; sau đó có phương án khôi phục mốc nghe còn chờ duyệt. | **P03 nay đã chốt:** khôi phục điểm/ngôn ngữ/mốc gần nhất cùng bài, audio tạm dừng, không cần đúng từng giây; mốc không khôi phục được/bài đổi mới về đầu và báo. Lỗi audio thử lại vẫn từ đầu theo P04/D03-BS. |
| Chi tiết hết hạn/quay lại | Trước đây P01/P02 còn là đề xuất; lần cảnh báo chỉ chốt thông báo. | **Nay đã chốt đúng nội dung P01/P02 mới:** hết hạn giữ văn bản/dừng nghe/khóa chuyển nội dung/có bắt đầu mới; chủ động kết thúc xóa khôi phục tab; xác nhận lại trước tạo mới. Không duyệt thêm các chi tiết ngoài tin nhắn xác nhận. |
| Bấm lại sau khi hết bài | P06 cũ chỉ nêu đang phát/tạm dừng/đang tải. | **Đã bổ sung vào P06 đã chốt:** bấm lại vùng sau khi đã phát hết thì phát từ đầu; không cộng lượt trong cùng lần mở. |
| Phân biệt thao tác bản đồ | P07 cũ chưa ghi rõ kéo/phóng không kích hoạt vùng. | **Đã bổ sung vào P07 đã chốt:** tách chạm chọn với kéo/phóng; thao tác di chuyển bản đồ không vô tình chọn vùng/phát audio. |
| Trạng thái duyệt luồng khách | Các bản trước còn ghi chờ duyệt tổng thể khách. | **BF-K01 đã thay trạng thái khách:** Luồng nghiệp vụ khách đã được duyệt tổng thể; BF-A01/BF-ALL01 nay bổ sung duyệt nghiệp vụ Admin/hai vai trò. Thiết kế/nội dung và phần mềm không được duyệt theo. Nhật ký cũ giữ nguyên để tra cứu. |
| Câu chữ P02 | Có nơi dùng “có thể điền sẵn” khi hết hạn. | **Đã chuẩn hóa, không mở lại quyết định:** “Điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới.” Chủ động kết thúc vẫn xóa khôi phục tab, không xóa ngay bản ghi Admin. |
| Gợi ý nội dung | Khoảng 100–150 từ vi/điểm và các trường hồ sơ duyệt chi tiết. | **Chưa được duyệt thành yêu cầu:** không áp lên năm bản nháp chưa được cung cấp; sẽ rà soát/đề xuất khi làm nội dung. |
| Các chi tiết thêm trong bộ đề xuất trước | Thông báo dữ liệu, bộ tên trạng thái tài liệu, chính sách tài khoản/IP, đồng bộ lỗi offline và các mục không được nêu rõ trong xác nhận. | **Không tự duyệt theo gói:** chỉ nội dung xác nhận hiện hành là đã chốt; P01–P08 đã chốt đúng nội dung ở mục 4; các chi tiết ngoài xác nhận như bố cục, xử lý hủy danh sách ứng viên hoặc mẫu hồ sơ cụ thể vẫn chờ thiết kế/nhiệm vụ phù hợp. |
| Trạng thái A02.6 và duyệt tổng thể Admin | Trước đây A02.6 là đề xuất, Admin/toàn bộ Business Flow chờ duyệt tổng thể. | **Đã thay thế:** A02.6 đã chốt lưu khác áp dụng; BF-A01/BF-ALL01 duyệt nghiệp vụ. Không áp dụng lại mô tả còn mở/chờ duyệt trong nhật ký lịch sử. |
| UI-P03/bố cục/ngôn ngữ trước UI-01, 31/10/2026 | Bố cục cuộn cả trang; nút Phát + dòng “Đã phát hết” theo đề xuất UI-P03 0.1; nhãn thường trực Văn bản/Audio/Trạng thái. | Đã bị thay đúng phần bởi UI-01.1–UI-01.3: box văn bản cuộn riêng, một nút ba trạng thái với biểu tượng Nghe lại, bộ chọn chung và phản hồi khi cần. Giữ nguyên bản thiết kế cũ tại [ui-design-history.md](ui-design-history.md); không mở lại quy tắc P06. |

Không còn hiệu lực lời mời “duyệt toàn bộ bộ phương án tại mục 6” của bản trước. Bộ đề xuất đó đã được đối chiếu từng phần theo xác nhận mới; không dùng số mục cũ để duyệt thêm các chi tiết chưa được nêu.

<a id="72-dấu-vết-chuyển-từ-business-flow"></a>

### 7.2. Dấu vết chuyển từ Business Flow

| Mô tả/quy tắc trước | Nội dung thay thế đã chốt |
| --- | --- |
| A02 cũ: không thêm ngoài năm điểm/không xóa cứng địa điểm có liên kết. | Đã bị thay thế: Admin thêm/sửa/ẩn-hiện/xóa, năm điểm là bộ nghiệm thu. Cơ chế xóa đã chốt trước; A02.1–A02.3 nay cụ thể hóa phụ thuộc/giữ lịch sử/dọn file, không cần duyệt lại. |
| A04 cũ: chỉ tải file chuẩn bị sẵn, chưa sinh TTS trong Admin. | Đã bị thay thế: tạo audio từ văn bản với vận hành A04.1–A04.4 đã chốt. Nhà cung cấp/giọng/chi phí/công nghệ và bằng chứng hỗ trợ bốn ngôn ngữ vẫn chưa có. |
| A02.4 cũ: chỉ nhận/gắn cấu hình vùng chuẩn bị bên ngoài, chưa vẽ trực tiếp trong Admin. | Đã bị thay thế: Admin vẽ/xem trước/lưu vùng, hỗ trợ sửa/vẽ lại trực tiếp; không mở thành bộ công cụ bản đồ phức tạp, ranh giới/thao tác cụ thể xem xét riêng. |
| A02.1 được mô tả như phương án cần chốt lại cơ chế xóa. | A02.1 chỉ cụ thể hóa quy trình xóa do ứng dụng điều phối đã được xác nhận trước; không yêu cầu duyệt lại cơ chế. |
| Năm điểm bấm; chọn địa điểm rồi cần bấm Phát riêng. | Năm vùng tương tác; chạm bất kỳ vị trí trong vùng đồng thời chọn và yêu cầu phát khi sẵn sàng. |
| D07 trước đây: chọn địa điểm khác và đổi ngôn ngữ đều không tự phát. | Phần chọn vùng địa điểm khác đã được điều chỉnh: phát bài mới từ đầu. Phần đổi ngôn ngữ vẫn không tự phát. |
| P05 còn đề xuất một điểm bấm đại diện Tử Cấm Thành. | P05 đã chốt một vùng tương tác mở một bài tổng quan, không tách địa điểm con. |
| Dùng chung phiên giữa các tab cùng trình duyệt/hồ sơ. | Mỗi tab một phiên; chưa yêu cầu đồng bộ giữa tab. |
| Hết hạn sau 30 phút không hoạt động; hoạt động kéo dài phiên. | Hết hạn đúng 8 tiếng kể từ bắt đầu dù có hoạt động; không gia hạn. |
| Năm điểm Ngọ Môn, Điện Thái Hòa, Thế Miếu, Hiển Lâm Các, Cửu Đỉnh. | Danh sách D06 mới; không còn Thế Miếu/Hiển Lâm Các trong năm điểm bản đầu. |
| Nút nghe lại riêng; không cần thanh tua. | Một nút Phát/Tạm dừng và thanh tua; không cần nút nghe lại riêng. |
| Tính hạn lưu từ kết thúc/hết hạn theo mốc hoạt động cuối + 30 phút. | Tính từ kết thúc chủ động hoặc hết hạn theo mốc bắt đầu + 8 tiếng. |
| Khách xem thông tin/trạng thái toàn phiên như một nhu cầu giao diện. | Khách chỉ cần thấy tên và lựa chọn đang dùng; chưa cần trang lịch sử phiên. |
| P01–P04/P06–P08 còn là đề xuất; P06 chưa nêu trường hợp đã phát hết. | Đã chốt theo xác nhận mới; bấm lại vùng sau khi bài hết phát từ đầu; không tự bổ sung các chi tiết cũ ngoài xác nhận. |
| P02 dùng câu “Không tìm thấy phiên hiện tại” và đề xuất mặc định tên trống/vi khi không tìm thấy. | Thông báo đã chốt: “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”; không tự chốt thêm quy tắc điền sẵn cho trường hợp này. |
| P03 cũ luôn về đầu bài hoặc ngầm đòi khôi phục chính xác. | Khôi phục mốc gần nhất đã lưu của cùng bài, không cần chính xác từng giây; lỗi mốc/bài đổi mới về đầu và thông báo. |

Chi tiết đề xuất cũ khác không được xác nhận không tự trở thành yêu cầu. Nguồn tham khảo từng dùng để đề xuất điểm chỉ là tư liệu, không chứng minh danh sách mới/nội dung mới đã được kiểm chứng.

<a id="dec-q-history"></a>

<a id="73-lịch-sử-đề-xuất-q1q3-từ-prd-03--đã-được-thay-bằng-xác-nhận"></a>

### 7.3. Lịch sử đề xuất Q1–Q3 từ PRD 0.3 — ĐÃ ĐƯỢC THAY BẰNG XÁC NHẬN

**Bản gốc ngày 31/10/2026, giữ nguyên để truy vết.** Tất cả câu “đề xuất/chưa duyệt/cần xác nhận” và lời mời phản hồi trong bản gốc sau đây là trạng thái lịch sử, không còn áp dụng cho Q1–Q3 hiện hành. Xác nhận đang áp dụng tại [mục 5.8](../02-nghiep-vu/decisions.md#dec-q); không dùng bản gốc này như bộ yêu cầu thứ hai.

<a id="94-bộ-phương-án-q1q3-để-duyệt-chung--chưa-xác-nhận"></a>

### 9.4. Bộ phương án Q1–Q3 để duyệt chung — CHƯA XÁC NHẬN

**Trạng thái:** Đây là đề xuất phân tích ngày 31/10/2026, không nằm trong nhóm quyết định đã chốt. Chưa áp vào FR/UC/AC hoặc dùng làm điều kiện nghiệm thu bắt buộc. Q1–Q3 chỉ là nhãn ba câu hỏi, không thay hoặc bổ sung mã quyết định D/P/A/G/BF.

<a id="proposal-q1"></a>

<a id="941-q1--thông-tin-địa-điểm-tối-thiểu-tách-khỏi-nội-dung-thuyết-minh"></a>

#### 9.4.1. Q1 — Thông tin địa điểm tối thiểu, tách khỏi nội dung thuyết minh

**Khuyến nghị chính:** Tên địa điểm là trường Admin bắt buộc nhập. Mã địa điểm do hệ thống cấp, độc lập với tên; trạng thái hiển thị và các quan hệ vùng/văn bản/audio được quản lý theo nghiệp vụ tương ứng. Chưa đề xuất thêm trường mô tả thuyết minh, ảnh hoặc tọa độ ở biểu mẫu thông tin cơ bản.

| Trường / quan hệ | Bắt buộc khi lưu điểm chưa hiển thị? | Quy tắc đề xuất — CHƯA DUYỆT |
| --- | --- | --- |
| Mã địa điểm | Có trong bản ghi khi tạo thành công; Admin không phải nhập. | Hệ thống cấp mã ổn định, không đổi do đổi tên, không dùng lại mã của điểm đã xóa cho điểm khác. Không tìm/gộp/ghi đè địa điểm chỉ vì trùng tên. |
| Tên hiển thị địa điểm | **Bắt buộc nhập.** | Đề xuất 1–100 ký tự hiển thị sau bỏ khoảng trắng đầu/cuối và gộp khoảng trắng liên tiếp giữa tên. Chấp nhận Unicode, số, dấu câu thông thường; không nhận toàn khoảng trắng, xuống dòng hoặc ký tự điều khiển vì tên dùng làm nhãn. Giữ nguyên dấu/chữ hoa của tên khi hiển thị. Đây là quy tắc tên địa điểm mới đề xuất, không đổi tên khách 1–50 đã chốt. |
| Trạng thái hiển thị | Hệ thống có trạng thái; điểm mới mặc định chưa hiển thị theo A02.5. | Lưu thông tin hợp lệ không tự bật hiện. “Bật hiển thị” là thao tác riêng, chỉ thành công khi đủ A02.5. |
| Vùng liên kết | Có thể chưa có khi lưu điểm ẩn; là quan hệ riêng, không phải ô nhập thuyết minh. | Vẽ/xem trước/lưu/sửa theo A02.4; bật hiện cần vùng đã xem xét. Sửa vùng điểm đang hiện giữ vùng cũ đến khi Áp dụng theo A02.6. |
| Văn bản/bản dịch/audio | Có thể chưa có khi lưu điểm ẩn; quản lý riêng theo điểm-ngôn ngữ/bản văn. | Không tạo ô “nội dung thuyết minh” thứ hai trong thông tin địa điểm. Bật hiện cần ít nhất văn bản vi được duyệt/áp dụng; thiếu audio hoặc ngôn ngữ khác xử lý D03/BS. |

**Trường tùy chọn:** Bộ thông tin cơ bản này chưa cần thêm trường nội dung tùy chọn. Vùng và nội dung có thể thiếu ở giai đoạn điểm ẩn nhưng vẫn là các quan hệ riêng cần hoàn thiện, không sao chép vào biểu mẫu tên. Không yêu cầu vị trí thực tế của khách, GPS hoặc quyền vị trí.

**Độ dài và trùng tên — cần người dùng chấp thuận trong Q1:**

- Mức 100 ký tự là khuyến nghị cho trường tên/nhãn địa điểm, cân đối khả năng đặt tên và cách thể hiện trên màn hình; không là giới hạn do giảng viên, database hoặc TTS đã quy định. Cần người dùng xác nhận; bố cục nhãn cụ thể vẫn được thiết kế riêng.
- Đề xuất **cho phép trùng tên**, kèm cảnh báo không chặn khi tên sau chuẩn hóa khoảng trắng trùng với tên điểm còn tồn tại, so sánh không phân biệt hoa/thường nhưng không bỏ dấu tiếng Việt. Danh sách Admin hiển thị mã cùng tên để phân biệt. Admin được tiếp tục lưu, không tự gộp bản ghi; không tự thêm hậu tố vào tên khách thấy. Cách này giữ đúng nguyên tắc định danh độc lập tên và không đặt ràng buộc duy nhất mới.
- Thêm điểm mới mang tên điểm đã xóa luôn là bản ghi mới; lịch sử cũ vẫn gắn nhận biết điểm cũ theo A02.2, không được chuyển sang điểm mới.

**Phân biệt lưu và hiển thị:**

1. Lưu địa điểm mới: kiểm tra tên; thành công tạo mã và bản ghi **chưa hiển thị**, dù chưa có vùng/vi/audio. Chưa có bản ghi thành công thì phần đang nhập chỉ là nội dung chưa lưu theo Q2.
2. Bật hiển thị: kiểm tra độc lập ba điều kiện A02.5 — thông tin hợp lệ, vùng đã xem xét, ít nhất vi đã duyệt/áp dụng. Thiếu điều kiện nào thì thông báo đúng điều kiện đó, giữ trạng thái chưa hiển thị và cho Admin tiếp tục bổ sung.
3. Lưu tên không tự áp dụng nháp văn bản/audio hoặc bản sửa vùng. Với điểm đang hiển thị, sửa tên sai/lưu lỗi không được làm mất thông tin đã lưu hoặc tự thay vùng hiện hành.

**Phản hồi đề xuất:** Tên trống báo “Vui lòng nhập tên địa điểm”; quá giới hạn báo rõ giới hạn đang được đề xuất; có xuống dòng/ký tự điều khiển thì báo để sửa, không âm thầm bỏ qua. Giữ dữ liệu đang nhập, không báo đã lưu hoặc tạo mã khi thao tác chưa thành công. Tên trùng chỉ cảnh báo kèm mã để Admin kiểm tra, không biến thành lỗi chặn.

**Ví dụ:** Admin nhập “  Khu   trưng bày A  ” → tên sau chuẩn hóa “Khu trưng bày A”; lưu được một điểm ẩn chưa có vùng/bài. Bấm bật hiện thì hệ thống chỉ rõ còn thiếu vùng đã xem xét và vi đã duyệt/áp dụng. Nếu có điểm khác trùng tên, hiển thị cảnh báo và hai mã khác nhau; không gộp chúng. Tên minh họa không bổ sung địa điểm vào bộ nội dung chuẩn D06.

**Lý do/ảnh hưởng:** Tránh trùng dữ liệu thuyết minh và cho phép chuẩn bị điểm theo từng bước mà vẫn giữ cổng bật hiện. Sau xác nhận sẽ cụ thể hóa FR-A02, BR-11/DR-06, UC-A02 và AC-A03/A04; đối chiếu FR-A03/A04, A02.2/A02.5/A02.6 để giữ ranh giới.

<a id="proposal-q2"></a>

<a id="942-q2--bảo-vệ-nội-dung-chưa-lưu-trong-lần-soạn-hiện-tại"></a>

#### 9.4.2. Q2 — Bảo vệ nội dung chưa lưu trong lần soạn hiện tại

**Khuyến nghị chính:** Bảo vệ cả ba loại nội dung trong cùng tab đang soạn, qua hết phiên/đăng nhập lại, lưu lỗi/mất mạng và tải lại bình thường của chính tab. Không cam kết khôi phục qua thiết bị, tab khác, tab đã đóng hoặc sự cố trình duyệt; không tạo kho bản nháp giữ tạm dài hạn. Đây là phạm vi bản giữ tạm, **không xác lập mô hình phiên Admin theo tab**.

| Nội dung được giữ tạm | Phần cần tiếp tục được sau khôi phục |
| --- | --- |
| Văn bản/bản dịch | Nội dung đang soạn, địa điểm/ngôn ngữ và bản văn đang sửa để quay lại đúng màn hình soạn; không tự gắn audio cũ vào bản mới. |
| Thông tin địa điểm | Các giá trị biểu mẫu đang sửa hoặc đang thêm mới và đối tượng đích nếu đã tồn tại; điểm mới chưa lưu không được coi là đã tạo bản ghi. |
| Vùng đang vẽ/sửa | Những đỉnh/hình dạng đã đặt và địa điểm đích, kể cả hình chưa hoàn chỉnh; trở lại trình sửa để hoàn thiện/xem trước. Không cần giữ chuyển động con trỏ tức thời hoặc khung nhìn chính xác từng điểm ảnh. Vùng đang áp dụng không đổi do giữ tạm. |

**Kết quả cần phân biệt:** Giữ tạm chỉ bảo vệ việc tiếp tục soạn. “Đã giữ tạm” phải khác “Đã lưu địa điểm”, “Đã lưu nháp văn bản” và “Đã lưu bản sửa vùng”; tất cả vẫn khác “Đã áp dụng”. Nếu việc giữ tạm không được xác nhận thành công, thông báo đúng giới hạn, không hứa đã bảo vệ qua tải lại. Khôi phục qua tải lại dựa vào bản giữ tạm gần nhất đã được ghi nhận; không tự đặt ngưỡng thời gian ghi tạm trong đề xuất nghiệp vụ này.

| Tình huống | Kết quả đề xuất — CHƯA DUYỆT | Thời điểm bỏ/xóa phần giữ tạm |
| --- | --- | --- |
| Phiên hết hạn rồi đăng nhập lại | Giữ nội dung đang soạn, thông báo hết phiên và chặn tác vụ cần quyền. Sau đăng nhập lại **cùng tài khoản**, trở về đúng biểu mẫu/trình vẽ với nội dung chưa lưu để tiếp tục; không tự lưu/áp dụng/gửi lại yêu cầu. Nếu tài khoản khác đăng nhập, không hiển thị bản giữ tạm của người trước. | Không xóa chỉ vì hết phiên. Xóa phần tương ứng khi lưu thành công, chủ động bỏ hoặc kết thúc phạm vi giữ tạm bên dưới. Khi đổi tài khoản thì loại bỏ bản giữ tạm của tài khoản trước. |
| Lưu thất bại hoặc mất mạng | Giữ giá trị đang nhập và bản tạm; báo “Chưa lưu được” hoặc “Chưa xác nhận đã lưu” nếu kết quả chưa rõ. Khi có kết nối, kiểm tra phiên/trạng thái trước thử lại chủ động; không tự biến dữ liệu tạm thành bản đã lưu. | Chưa xóa nếu chưa xác nhận lưu thành công. Lưu thành công thì chỉ xóa bản tạm của đúng công việc vừa lưu, không xóa các bản ghi nghiệp vụ đã lưu khác. |
| Tải lại bình thường trong cùng tab | Khôi phục bản giữ tạm gần nhất của đúng đối tượng/ngôn ngữ trên đúng màn hình soạn/trình vẽ; nếu cần thì đăng nhập lại cùng tài khoản trước khi cho xem/sửa. Hiển thị rõ nội dung chưa lưu; không tự Tạo audio, Lưu hoặc Áp dụng. Tải lại không tự gia hạn phiên Admin. | Giữ đến khi lưu thành công hoặc Admin chủ động bỏ; lỗi không khôi phục được phải báo rõ, không báo đã lưu/đã khôi phục giả. |
| Chủ động đăng xuất khi còn thay đổi | Đề xuất hỏi “Đăng xuất và bỏ thay đổi” hoặc “Quay lại soạn”. Xác nhận đăng xuất thì dừng sử dụng bản giữ tạm và xóa phần chưa lưu của tài khoản trong phạm vi này; bản đã lưu nháp/bản sửa vùng không bị xóa theo thao tác đó. | Xóa khi xác nhận đăng xuất và bỏ thay đổi; nếu quay lại soạn thì giữ. Tác vụ TTS đã được tiếp nhận vẫn xử lý theo A04.4. |
| Đóng tab | Đóng tab chấm dứt phạm vi phục hồi được cam kết; không cung cấp khôi phục từ kho giữ tạm ở lần mở mới. Có thể cảnh báo mất thay đổi nếu trình duyệt cho phép, nhưng không hứa mọi trình duyệt luôn cảnh báo hoặc nhận biết chính xác lúc đóng. | Bản giữ tạm hết vòng đời phục vụ lần soạn đó. TDD phải mô tả cách loại bỏ dữ liệu tạm khi hết phạm vi, kể cả trường hợp không nhận được tín hiệu đóng; không hứa đã xóa vật lý đúng một thời điểm không quan sát được. |
| Lưu thành công hoặc chủ động bỏ | Lưu thành công thì tiếp tục theo loại bản ghi đã lưu: điểm vẫn theo trạng thái của nó, nháp văn bản chưa áp dụng, bản sửa vùng chưa áp dụng. “Bỏ thay đổi” quay về dữ liệu đã lưu, cần xác nhận nếu đang có phần chưa lưu. | Xóa bản tạm tương ứng khi đã xác nhận lưu thành công hoặc xác nhận bỏ; bản lưu nghiệp vụ không bị xóa chỉ vì dọn tạm. |

**Điều hướng khi còn thay đổi:** Trước khi chuyển sang công việc khác làm mất nội dung đang soạn, cho Admin chọn tiếp tục soạn hoặc xác nhận bỏ; không tự xóa chỉ do bấm một chức năng khác. Không yêu cầu một danh sách khôi phục nhiều điểm/ngôn ngữ lâu dài.

**Khi dữ liệu đích thay đổi:** Bản tạm không được tự khôi phục điểm đã bị xóa hoặc ghi đè im lặng một bản mới. Kiểm tra đối tượng/bản hiện hành sau đăng nhập lại; nếu điểm đã xóa, báo không thể tiếp tục lưu vào điểm đó và cho bỏ phần tạm. Nếu có thay đổi liên quan cần đối chiếu, cho Admin nhận biết trước lưu/áp dụng; chưa bổ sung cộng tác thời gian thực hoặc hệ thống lịch sử phiên bản phức tạp.

**Không giữ lặp tác vụ:** Kết quả/trạng thái TTS đã được tiếp nhận là dữ liệu tác vụ theo A04.2/A04.4, không phải phần nhập liệu chưa lưu. Khôi phục biểu mẫu hoặc đăng nhập lại phải xem yêu cầu cũ trước khi tạo mới nếu chưa rõ kết quả; không gửi lại tự động. Dữ liệu bản tạm không phải dữ liệu phiên/lượt xem khách theo D08.

**Ví dụ:** Admin đang sửa bản fr, hết phiên trước khi lưu; sau đăng nhập lại cùng tài khoản trong tab đó, trở về đúng bản fr với nhãn chưa lưu. Ở một lần soạn khác, Admin đang vẽ vùng rồi tải lại: các đỉnh đã giữ tạm được khôi phục để tiếp tục. “Lưu bản sửa vùng” thành công xóa bản tạm tương ứng nhưng không Áp dụng vùng. Nếu một yêu cầu TTS đã tiếp nhận trước đó đang chạy, nó vẫn tiếp tục và không bị gửi lại do khôi phục trang. Không yêu cầu phải mở đồng thời nhiều biểu mẫu hoặc có kho nhiều bản tạm.

**Lý do/giới hạn:** Bảo vệ các lỗi thường gặp khi soạn đồ án và cả tải lại nhầm, đồng thời giới hạn vòng đời tạm vào lần làm việc trong tab. Chưa chọn nơi/cách lưu tạm. Khôi phục sau đóng tab/crash hoặc ở thiết bị khác không nằm trong cam kết; nội dung đã Lưu nháp thành công vẫn được quản lý theo A03, không bị coi là dữ liệu tạm cần xóa.

**Ảnh hưởng sau xác nhận:** FR-A01–A04, UC-A01–A03, AC-A02/A03/A05/A06/A07 và NFR-04/DR-06 cần cụ thể hóa. UC-A04/UC-A06, AC-A10/A12/A18 được đối chiếu để giữ nguyên quy tắc tác vụ đã nhận/kết quả muộn; không mở lại các quyết định đó.

<a id="proposal-q3"></a>

<a id="943-q3--hoạt-động-chủ-động-tính-vào-phiên-admin"></a>

#### 9.4.3. Q3 — Hoạt động chủ động tính vào phiên Admin

**Khuyến nghị chính:** Sau đăng nhập thành công, giữ nguyên khoảng **30 phút không hoạt động**. Trong phiên còn hiệu lực, mỗi hành động nghiệp vụ chủ động được hệ thống ghi nhận bắt đầu lại khoảng 30 phút từ hành động đó; không tạo phiên mới chỉ vì có hoạt động. Không chọn kỹ thuật bắt/gửi sự kiện hoặc quyết định mô hình phiên Admin giữa các tab.

| Hành động / trạng thái | Có tính hoạt động để bắt đầu lại khoảng 30 phút? | Giới hạn đề xuất — CHƯA DUYỆT |
| --- | --- | --- |
| Nhập/sửa/xóa nội dung văn bản/bản dịch hoặc thông tin địa điểm | Có. | Là thao tác chủ động trong biểu mẫu; báo lỗi hợp lệ không đồng nghĩa không có hoạt động, cũng không được báo đã lưu. |
| Vẽ/đặt hoặc di chuyển đỉnh/sửa/vẽ lại vùng | Có. | Tính thao tác vẽ/sửa chủ động; không tính chỉ đưa con trỏ qua sơ đồ. Không chọn ngưỡng thời gian/khoảng cách bắt sự kiện ở đây. |
| Chọn chức năng, địa điểm, ngôn ngữ, bộ lọc/tìm kiếm; mở/xem trước hoặc chủ động cuộn đọc nội dung/danh sách trong quản trị | Có. | Tương tác phải do Admin chủ động, không phải giao diện tự chuyển/tự cuộn. Điều hướng vẫn bảo vệ phần chưa lưu theo Q2, không tự bỏ thay đổi. |
| Bấm Lưu địa điểm/Lưu nháp/Lưu bản sửa vùng; Áp dụng/ẩn-hiện/Xóa hoặc thao tác xác nhận | Có. | Tính thao tác được ghi nhận khi phiên còn hiệu lực; thành công hay không vẫn phải báo đúng kết quả. Không dùng việc bấm sau khi đã hết phiên để hồi sinh phiên. |
| Bấm Tạo audio, chủ động thử lại hoặc kiểm tra yêu cầu cũ | Có. | Một hành động yêu cầu chủ động; không biến mỗi lần trạng thái tự tải thành hoạt động. Giữ chống yêu cầu trùng theo A04.2. |
| Bấm Phát/Tạm dừng hoặc tua audio nghe thử | Có. | Tính lúc Admin chủ động điều khiển, không phải mọi giây audio phát hoặc vị trí nghe tự tăng. |
| Tác vụ TTS tiếp tục chạy, kết quả hoàn tất đến muộn, dọn file/tác vụ hệ thống | Không. | A04.4 vẫn cho tác vụ đã nhận tiếp tục sau hết phiên/đóng trang; hoàn tất không kéo dài hay tạo lại phiên Admin. |
| Tự tải trạng thái, tự làm mới danh sách, giữ tạm tự động | Không. | Các tác vụ tự động không phải hoạt động chủ động, dù chúng phát sinh do một thao tác trước đó. |
| Audio tự tiếp tục phát, tải đệm hoặc phát hết | Không. | Có thể hết phiên trong lúc chỉ nghe thụ động; khi muốn thao tác cần quyền thì đăng nhập lại. |
| Chỉ mở trang, để tab đang hiện, đổi tiêu điểm tab hoặc di chuyển con trỏ mà không thao tác nghiệp vụ | Không. | Không coi trang đang mở là Admin đang hoạt động. |
| Tương tác sau khi phiên đã hết hạn hoặc chỉ đóng thông báo hết hạn | Không gia hạn phiên cũ. | Yêu cầu đăng nhập lại để có phiên hợp lệ; giữ bản đang soạn theo Q2. Đăng xuất chủ động kết thúc phiên, không phải hành động kéo dài phiên. |

**Mất mạng:** Dữ liệu đang nhập vẫn được bảo vệ theo Q2. Không mặc định thao tác tại máy khi hệ thống chưa xác nhận được là bằng chứng phiên còn hiệu lực; khi nối lại kiểm tra phiên trước tác vụ cần quyền. Không ghi bù hoạt động để làm sống lại phiên đã hết hạn. TDD phải thiết kế cách ghi nhận đáp ứng quy tắc được duyệt, không dùng đề xuất này để chọn công nghệ hoặc gửi yêu cầu sau từng phím bấm.

**Ví dụ:** Đăng nhập 09:00; nhập nội dung lúc 09:20 → nếu không có hoạt động tiếp thì hết hạn 09:50. Chủ động bấm Tạo audio lúc 09:25 → mốc không hoạt động mới là 09:55. Nếu sau đó chỉ có tác vụ tạo/tự tải trạng thái/audio tự phát, phiên vẫn hết hạn 09:55; tác vụ đã nhận tiếp tục, nội dung chưa lưu được xử lý theo Q2.

**Lý do/ảnh hưởng:** Tách việc Admin thực sự đang dùng trang khỏi các tác vụ có thể tự chạy mãi; giữ khả năng làm việc chủ động mà không thay thời hạn 30 phút. Sau xác nhận sẽ cụ thể hóa FR-A01/BR-14, UC-A01, AC-A02 và NFR-03/NFR-04; đối chiếu UC-A04/AC-A12 để tác vụ nền vẫn tiếp tục nhưng không gia hạn.

<a id="944-kiểm-tra-nhất-quán-và-cách-phản-hồi-một-lượt"></a>

#### 9.4.4. Kiểm tra nhất quán và cách phản hồi một lượt

| Liên quan | Kết quả đề xuất |
| --- | --- |
| Q1 lưu điểm ẩn và Q2 giữ biểu mẫu | Đang nhập/chưa tạo mã chỉ là dữ liệu tạm; lưu thành công tạo điểm ẩn, chưa tự hiện hoặc áp dụng nội dung/vùng. |
| Q2 khôi phục sau hết phiên và Q3 hoạt động | Đăng nhập lại khôi phục việc soạn, không hồi sinh phiên cũ; hoạt động sau đó mới tính trong phiên hợp lệ. Bản thân tải lại/khôi phục/giữ tạm tự động không tự gia hạn; thao tác nghiệp vụ chủ động tiếp theo trong phiên hợp lệ mới được tính. |
| Q2 đóng tab và A04.4 | Bản tạm nằm ngoài cam kết khôi phục sau đóng tab; tác vụ TTS đã tiếp nhận vẫn tiếp tục. Bản đã lưu và bản tạm là hai loại khác nhau. |
| Q1/Q2 và vùng đang áp dụng | Lưu thông tin điểm, giữ tạm hoặc Lưu bản sửa vùng không tự Áp dụng; giữ A02.6 và hiệu lực lần tải bản đồ tiếp theo. |
| Q1 tên trùng, Q2 dữ liệu đích bị xóa và A02.2 | Phân biệt theo mã, không khôi phục/gộp điểm chỉ vì trùng tên; lịch sử điểm cũ vẫn giữ đúng hạn D08. |
| Khách và nghiệm thu | Không đổi tên khách, phiên khách 8 tiếng, cách đếm, một QR chung, bốn ngôn ngữ hoặc bộ chuẩn 20 văn bản/20 audio; không biến đề xuất thành bằng chứng phần mềm. |

Người dùng có thể trả lời “Đồng ý Q1–Q3” hoặc ghi sửa theo Q1/Q2/Q3; việc lưu phương án không được coi là đã đồng ý. Các chi tiết kỹ thuật còn lại được thiết kế sau theo kết quả được xác nhận. Sau xác nhận mới cập nhật FR/UC/AC liên quan và ghi căn cứ quyết định phù hợp.

**Hết bản gốc PRD 0.3.** Tiếp theo là căn cứ/nhật ký; nội dung Q1–Q3 hiện hành chỉ tra mục 5.8.

<a id="ui-02-history"></a>

<a id="74-dấu-vết-trước-xác-nhận-ui-02--không-áp-dụng"></a>

### 7.4. Dấu vết trước xác nhận UI-02 — KHÔNG ÁP DỤNG

Ngày 01/11/2026 người dùng chủ đích thay một phần P01 và chốt UI-P01/UI-P02/UI-P04. Dưới đây giữ mô tả trước thay đổi để truy vết; nội dung hiện hành ở 4.10/4.12, không dùng như yêu cầu song song.

| Mã | Nội dung trước UI-02 | Trạng thái thay thế |
| --- | --- | --- |
| P01 | Thông báo hết hạn; dừng audio; vô hiệu điều khiển nghe/tua, chọn vùng và đổi ngôn ngữ. Giữ văn bản đã tải để đọc; có nút “Bắt đầu phiên mới”. Không tự tạo phiên, gia hạn hoặc ghi lượt xem mới vào phiên hết hạn. Giữ cảnh báo trong 5 phút cuối theo D04-BS. Hết hạn ưu tiên hơn việc tiếp tục phát phần audio đã tải. | Thay phần dừng/khóa toàn bộ audio bằng ngoại lệ bài đang phát tại mốc hết hạn; mốc 8 tiếng/30 ngày/lượt không đổi. |
| **UI-P04 — ƯU TIÊN, CHƯA DUYỆT** | Khuyến nghị sau kết thúc thành công hiện màn gọn “Đã kết thúc tham quan” + “Bắt đầu phiên mới”; không còn bài/audio/vùng cũ trên màn đó. Nút mở trang bắt đầu, tên trống/vi mặc định; khách xác nhận Bắt đầu mới tạo phiên. Không tự tạo/gia hạn, không hộp xác nhận kết thúc. **Màn đích này là đề xuất**, khác hết hạn bắt buộc giữ bài theo P01. | Thay màn kết thúc riêng bằng box chặn toàn trang sau thành công. |
| **UI-P01 — CHƯA DUYỆT** | Khuyến nghị có Hủy; hủy/đóng chỉ đóng danh sách ứng viên, không đổi điểm/bài hoặc tua/phát lại; audio cũ đang phát tiếp tục kể cả khi bảng mở, không chụp/khôi phục trạng thái cũ. Nếu chưa chọn thì về bản đồ chưa chọn; không cộng lượt. Hết hạn luôn P01. | Nay chốt box ứng viên/nền khóa/Hủy/giữ audio và đóng khi hết hạn. |
| **UI-P02 — CHƯA DUYỆT TOÀN BỘ** | Phần đã chốt chỉ nhánh **đã hết bài rồi tua về trước cuối** theo UI-P03. Phần còn lại: khuyến nghị tua lúc tạm dừng thông thường giữ tạm dừng tại mốc mới; đang phát thì nghe tiếp từ mốc mới. Không coi xác nhận UI-P03 là duyệt hai nhánh này. | Các nhánh còn mở nay chốt cho phiên còn hiệu lực. |


<a id="ui-03-history"></a>

<a id="75-dấu-vết-trước-xác-nhận-ui-03--không-áp-dụng-như-trạng-thái-hiện-hành"></a>

### 7.5. Dấu vết trước xác nhận UI-03 — KHÔNG ÁP DỤNG NHƯ TRẠNG THÁI HIỆN HÀNH

UI 0.4 ngày 01/11/2026, mục 10.13 từng đặt các hướng sau ở trạng thái **đề xuất/chờ duyệt**. Giữ nguyên bảng để truy vết; UI-03 ngày 02/11/2026 xác nhận đúng các hướng và làm rõ thứ tự vùng, không duyệt toàn bộ chi tiết UI.

| Mã đề xuất UI | Khuyến nghị chính | Lý do / ảnh hưởng cần người dùng xem xét |
| --- | --- | --- |
| UI-AP01 | Một đa giác kín đơn giản/địa điểm; đặt đỉnh rồi Khép vùng, sửa kéo đỉnh, Bỏ đỉnh vừa đặt/Vẽ lại; tách chế độ di chuyển và vẽ, không lỗ/nhiều mảnh trong phương án này. Hình đang dở vẫn giữ tạm; hình lưu cần khép và xem trước được. | Giữ công cụ gọn, thao tác máy tính và điện thoại rõ. Cần người dùng xem có đủ cho hình dạng thực tế trước khi ràng buộc thiết kế; chưa duyệt giới hạn hình học/ranh giới hoặc tự cấm mọi chồng lấn. |
| UI-AP02 | Xem trước bản sửa đã lưu rồi Áp dụng tại cùng trang, không box xác nhận áp dụng thứ hai; khi sửa tiếp phải lưu/xem lại đúng bản. Giữ nhãn vùng đang dùng/bản sửa rõ. | Tách Lưu/Áp dụng đã chốt, giảm nhầm bản; cần duyệt đường thao tác xem xét thực tế, không coi chỉ mở preview là tự được duyệt ranh giới. |
| UI-AP03 | Trang riêng cho form và trình vùng, danh sách là trang mở đầu; hết phiên dùng box đăng nhập lại che dữ liệu. Hủy cảnh báo chưa lưu quay lại soạn, không có Lưu và đi tiếp. | Dễ giữ đúng công việc và kiểm tra quyền/phục hồi; người dùng góp ý trước khi phác thảo đồ họa chi tiết. Không đổi nghiệp vụ 30 phút/Q2 hoặc cách kết thúc khách. |

**Câu trình tự cũ UI 0.4:** “Xem trước bản sửa đã lưu rồi Áp dụng tại cùng trang” có thể khiến hiểu luôn phải xem trước lại sau lưu. Hiện hành UI-03: Vẽ/sửa → Xem trước → Lưu bản sửa → Áp dụng đúng bản đã xem; hình lưu không đổi không phải xem lại chỉ vì lưu, sửa tiếp/bản khác thì lưu và xem lại đúng bản.

**Nhãn cũ UI 0.4:** “0–100 ký tự hiển thị” được thay bằng “Bắt buộc, tối đa 100 ký tự hiển thị”; bộ đếm 0 không cho phép tên rỗng. Không thay Q1, không làm giảm phạm vi giữ tạm hoặc thay ranh giới/tài sản bản đồ.


<a id="ui-04-history"></a>

<a id="76-dấu-vết-trước-ui-04--không-áp-dụng-như-yêu-cầu-hiện-hành"></a>

### 7.6. Dấu vết trước UI-04 — KHÔNG ÁP DỤNG NHƯ YÊU CẦU HIỆN HÀNH

Các đoạn dưới giữ nguyên để truy vết trạng thái trước xác nhận ngày 02/11/2026. UI-04 chủ đích thay hai nút riêng/khả năng áp dụng văn bản trước audio và điều kiện hiện chỉ cần văn bản vi. Đọc hiện hành tại [5.10](../02-nghiep-vu/decisions.md#ui-admin-confirmed-02), không dùng các đoạn cũ làm nguồn song song. Các anchor gốc A02.5/A04.3 vẫn trỏ tới quyết định hiện hành; bản trích lịch sử không lặp anchor.

**Bản trích lịch sử — A02.5:**

<a id="525-a025--điều-kiện-hiển-thị-điểm-mới-1"></a>

#### 5.2.5. A02.5 — Điều kiện hiển thị điểm mới

**Đã chốt:** Điểm mới mặc định chưa hiện. Chỉ bật hiện khi thông tin hợp lệ, có vùng đã xem xét và ít nhất văn bản vi đã được duyệt/áp dụng. Thiếu audio hoặc ngôn ngữ khác xử lý D03/D03-BS. Năm điểm × bốn ngôn ngữ là bộ nội dung người dùng chọn, không phải giới hạn số lượng do giảng viên quy định.

**Giới hạn / thiết kế sau:** Giữ 20 văn bản/20 audio cho bộ này; không tự tăng bài chuẩn cho mọi điểm thêm và không bổ sung quản lý thêm ngôn ngữ. Bốn ngôn ngữ vi/en/fr/ru giữ nguyên phạm vi.

**Bản trích lịch sử — A04.3:**

<a id="543-a043--audio-gắn-đúng-bản-văn-1"></a>

#### 5.4.3. A04.3 — Audio gắn đúng bản văn

**Đã chốt:** Audio chỉ thuộc bản văn lúc tạo; văn bản đổi không tự gắn kết quả cũ vào bản mới. Giữ bản hợp lệ đang phục vụ khi tạo mới/lỗi. Văn bản mới chưa có audio khớp thì báo thiếu, không phát audio cũ. Tạo thành công, được duyệt và đã áp dụng là ba khái niệm riêng.

**Giới hạn / thiết kế sau:** Chưa cần lịch sử phiên bản phức tạp. Giữ D03/D09 và A03/A07; không coi tạo thành công là nội dung đã được người dùng duyệt.

**Bản trích lịch sử — UI-AP06 / UI-A18 tại UI 0.5:**

<a id="118-ui-a18--chủ-động-áp-dụng-đúng-văn-bản-hoặc-audio"></a>

### 11.8. UI-A18 — chủ động áp dụng đúng văn bản hoặc audio

**UI-AP06 — đề xuất hai hành động riêng tại cùng trang**, có phần tóm tắt trước nút thay vì box xác nhận thứ hai. Đây là hướng UI mới chờ duyệt, không thay A03/A04.3/A07/D09. **Nguồn:** FR-A07/FR-A12, UC-A05, AC-A13/A14/A15/A25/A26.

```text
ÁP DỤNG CHO KHÁCH — <điểm, mã> / fr

Văn bản đang dùng: V1
Bản muốn dùng: V2 — đã lưu — Tham chiếu hồ sơ: <...>
Nếu chưa có audio khớp được phép: khách vẫn đọc V2,
audio chưa sẵn sàng; không phát audio V1.
[ Áp dụng văn bản V2 ]

Audio đang dùng: A1 của V1 / hoặc chưa có audio khớp V2
Audio muốn dùng: A2 của V2/fr — Tham chiếu audio: <...>
[ Áp dụng audio A2 ]

Thay đổi có hiệu lực ở lần tải/chọn tiếp theo, không đẩy ngay.
```

- Áp dụng văn bản chỉ nhắm bản đã lưu và được người dùng duyệt theo hồ sơ; còn sửa chưa lưu thì yêu cầu lưu/đối chiếu đúng bản, không âm thầm lưu hoặc mang hồ sơ bản cũ qua. Đề xuất nút thiếu điều kiện nêu rõ bước còn thiếu, không chỉ vô hiệu hóa mà không giải thích.
- Áp dụng văn bản mới **không tự áp dụng** audio vừa tạo dù có kết quả; nếu chưa có audio khớp được phép, trạng thái phần khách là Audio chưa sẵn sàng theo D03/BS. File/bản ghi audio cũ không vì thế tự bị xóa hoặc phát cho V2.
- Đề xuất áp dụng audio riêng chỉ khi đúng bản/ngôn ngữ của văn bản đang dùng và có căn cứ duyệt audio; nếu V2 chưa là văn bản đang dùng thì nhắc áp dụng V2 trước. Không tự đổi văn bản qua nút audio; không cho audio V1 phục vụ văn bản V2.
- Ghi tham chiếu/thử nghe/tạo thành công không tự kích hoạt một nút hoặc tác vụ áp dụng. Phần mềm không tự kết luận hồ sơ ngoài đúng chỉ vì trường tham chiếu có chữ; Admin phải đối chiếu thật theo UC-A05.
- Đang áp dụng → phản hồi/chặn lặp; thành công có căn cứ → ở lại, cập nhật phần Bản đang dùng và thông tin hiệu lực. Lỗi → giữ bản phục vụ hợp lệ, giải thích; chưa rõ kết quả → kiểm tra bản hiện hành trước thử lại, không báo đã áp dụng giả.
- Điểm ẩn vẫn ẩn sau áp dụng nội dung/audio. Bật hiện là bước riêng A02.5; đủ vi duyệt/áp dụng + thông tin/vùng hợp lệ có thể bật, không bắt đủ bốn ngôn ngữ/audio để bật một điểm mới. Đây không phải kết luận đủ bộ chuẩn nghiệm thu.
- Không ngắt audio hoặc ép hết phiên khách khi Admin áp dụng; giữ A07/P01/UI-02 về nội dung đã tải và nghe tiếp có quyền. Audio mới không dùng mốc nghe của bài cũ; file xử lý theo A02.3, không đồng nhất đổi bản với xóa file.
- Trên điện thoại hai khối áp dụng xếp dọc, nhắc rõ bản/ngôn ngữ ngay trước từng nút; không đặt hai nút chỉ ghi chung “Áp dụng” dễ bấm nhầm.

**Bản trích lịch sử — BR-11, FR-A07, AC-A04, AC-A15 tại PRD 0.6:**

```markdown
| BR-11 | Tên là trường Admin bắt buộc nhập: 1–100 ký tự hiển thị sau chuẩn hóa khoảng trắng; Unicode/số/dấu câu thông thường, không toàn khoảng trắng/xuống dòng/ký tự điều khiển. Mã hệ thống cấp ổn định, độc lập tên, không tái dùng mã đã xóa. Trùng tên cảnh báo không chặn, so sánh sau chuẩn hóa không phân biệt hoa/thường nhưng giữ dấu; không tự gộp hoặc thêm hậu tố. Lưu được điểm ẩn chưa có vùng/bài; chỉ bật khi thông tin hợp lệ, vùng đã xem xét và vi duyệt/áp dụng. Nội dung đa ngôn ngữ/audio quản lý riêng, chưa thêm trường nội dung tùy chọn. Năm điểm là bộ chuẩn, không là giới hạn số địa điểm; phạm vi ngôn ngữ vẫn vi/en/fr/ru, chưa bổ sung chức năng quản lý thêm ngôn ngữ. | A02/A02.2/A02.4/A02.5, D06, Q1 |
| FR-A07 | Admin đối chiếu/ghi tham chiếu hồ sơ duyệt rồi áp dụng đúng phiên bản; tạo thành công khác duyệt/áp dụng, không tự biết duyệt ngoài hệ thống. Văn bản mới thiếu audio khớp báo thiếu, không dùng bài cũ. Hiệu lực theo A07. | A03/A07, A04.3, D03/D09 | UC-A05 | AC-A14, AC-A15, AC-A26 |
| AC-A04 | **Cho trước** điểm chưa hiển thị; **khi** bật hiện; **thì** phải đủ thông tin hợp lệ theo Q1, vùng đã xem xét và vi duyệt/áp dụng theo A02.5. Thiếu điều kiện nào báo rõ điều kiện đó, giữ chưa hiển thị; không nhầm việc lưu thông tin với bật hiện/áp dụng vùng hoặc nội dung. Đủ ba điều kiện có thể chủ động bật; thiếu audio/ngôn ngữ khác xử lý D03/BS. Tạm ẩn chặn truy cập mới lần tải/chọn tiếp, không ép dừng nội dung hợp lệ đã tải trong hạn phiên. | FR-A02; UC-A02; Q1 |
| AC-A15 | **Cho trước** có bản hợp lệ đang áp dụng; **khi** tạo mới/lỗi hoặc chỉ lưu nháp; **thì** giữ bản phục vụ hợp lệ. Khi chủ động áp dụng văn bản mới chưa có audio khớp, khách báo thiếu/không nghe audio văn bản cũ; đổi nội dung có hiệu lực lần tải/chọn tiếp theo, không đẩy ngay. Audio mới không nhận mốc nghe của bài cũ. | FR-A06/A07; UC-A04, UC-A05 |
```

UI 0.5 cũng từng đặt UI-AP04/UI-AP05/UI-AP07 ở trạng thái đề xuất; nay chỉ các hướng nêu rõ tại UI-04 đã được xác nhận. Bản cũ “UI-AP07: nguồn chưa chốt dừng hay tiếp tục” không còn là câu hỏi mở; Admin hết phiên dừng nghe thử, TTS đã nhận vẫn tiếp tục. Nhật ký cũ giữ đúng thời điểm, không sửa thành xác nhận mới.


<a id="ui-05-history"></a>

<a id="77-trước-ui-05--đề-xuất-trang-riêng-ui-a21-đã-bị-thay-thế"></a>

### 7.7. Trước UI-05 — đề xuất trang riêng UI-A21 đã bị thay thế

Bản trích **UI 0.6 ngày 02/11/2026 trước xác nhận UI-05**, giữ để truy vết. Trang chi tiết riêng bị thay bằng box chỉ đọc trên danh sách; các hướng ngày/thống kê/QR từ đề xuất thành xác nhận đúng phạm vi UI-05. Không dùng các mô tả dưới như nguồn đang áp dụng; mã UI-A21/UI-AP08 vẫn giữ.

**Nguyên văn phần UI-A21 cũ:**

<a id="123-ui-a21--thông-tin-phiên-chỉ-đọc"></a>

### 12.3. UI-A21 — thông tin phiên chỉ đọc

**Đề xuất trang chi tiết riêng:** đủ chỗ các mốc và lượt xem, dùng ổn định trên máy tính/điện thoại; không box hẹp hoặc màn chỉnh sửa. **Nguồn:** DR-01/DR-02/DR-03, FR-A11, UC-A08, AC-A17/A24, A02.2/D08.

```text
[Quay lại danh sách]   Phiên <mã>
Tên hiển thị: <...>     Ngôn ngữ ban đầu: <...>
Bắt đầu: <thời điểm>    Hết hạn cố định: <bắt đầu + 8 tiếng>
Kết thúc chủ động: <thời điểm nếu đã ghi nhận / —>
Trạng thái: <...>       Múi giờ hiển thị: Asia/Saigon

LƯỢT XEM ĐÃ ĐƯỢC TÍNH
Thời điểm       Địa điểm / nhận biết lịch sử      Ngôn ngữ
<...>           <Tên / mã nhận biết>               <...>
<...>           <Tên cũ> — địa điểm đã xóa          <...>
Không phải lịch sử từng thao tác nghe hoặc số lần quét QR.
```

- Chỉ hiển thị dữ liệu nghiệp vụ đã chốt: mã/tên/ngôn ngữ ban đầu, mốc bắt đầu/kết thúc/hết hạn/trạng thái và địa điểm/ngôn ngữ/thời điểm các lượt xem được tính. Không thêm tọa độ, điện thoại/email, log từng lần tua/phát, người duy nhất hoặc thiết bị/IP.
- Không bịa thời điểm kết thúc từ đóng tab hoặc lần xem cuối. Chưa có kết thúc chủ động hiển thị —; hết hạn theo bắt đầu +8 tiếng, không lúc audio nghe hết. Ngôn ngữ ban đầu khác ngôn ngữ ghi ở từng lượt xem, không dùng giá trị đang xem để viết lại lịch sử.
- Lượt của điểm đã xóa giữ nhận biết lịch sử theo A02.2 đến hạn lưu; điểm mới trùng tên không gộp, không nối sang trang điểm mới chỉ vì cùng tên. Đề xuất ghi “địa điểm đã xóa” cạnh tên cũ.
- Đang tải/lỗi: phản hồi/Thử lại, không đưa trường trống như dữ liệu thật. Không tìm thấy/không còn dữ liệu: báo “Không còn dữ liệu để xem phiên này”, về danh sách; không khẳng định nguyên nhân cụ thể hoặc trạng thái hết hạn nếu thiếu căn cứ.
- Không có lượt xem được tính: hiển thị rõ rỗng, không suy ra khách chưa nghe/đọc hoặc chưa mở trang; không tạo sự kiện bù.
- Không có nút sửa/xóa/gia hạn hay mở phiên thay khách. Hết phiên Admin → UI-A10; đăng nhập lại chỉ tải thông tin còn được giữ nếu còn quyền.
- Điện thoại các trường xếp dọc, lượt xem thành thẻ thời điểm/địa điểm/ngôn ngữ. Không thêm bộ lọc cho danh sách lượt trong chi tiết ở lượt này.

**Bảng phương án trước xác nhận:**

| Mã đề xuất mới — CHƯA DUYỆT | Khuyến nghị | Điểm người dùng xem xét |
| --- | --- | --- |
| UI-AP08 | Danh sách phiên và trang chi tiết chỉ đọc riêng; mở bằng mã, quay lại giữ bộ lọc trong lần điều hướng. Máy tính bảng/điện thoại thẻ. | Cách chuyển trang và lượng thông tin phiên/lượt cần đọc có thuận tiện, không thêm thao tác quản lý phiên. |
| UI-AP09 | Hai bảng thống kê phiên/lượt tách rõ, nhóm theo ngày; lượt thêm chiều điểm/ngôn ngữ đã ghi. Bộ ngày Từ/Đến trọn ngày Asia/Saigon, mặc định hôm nay; không lọc điểm/ngôn ngữ mới. | Hướng trình bày/nhập ngày. Đề xuất Về hôm nay chỉ đặt lại hai ngày, giữ trạng thái/tên-mã đang chọn ở danh sách; không thay cách tính đã chốt. |
| UI-AP10 | Trang QR có URL chỉ đọc; khi URL thật sẵn sàng tải PNG hoặc in trang gọn một QR + URL dự phòng. | Định dạng tải/bố cục in, không duyệt URL giả, khả năng quét, thư viện hoặc hệ thống chuyển hướng. |

Hiện hành xem [UI-05](../02-nghiep-vu/decisions.md#ui-admin-confirmed-03) và ui-design mục 12. Không sửa ui-design-history.md hoặc tài sản bản đồ để trình bày lại quá khứ.

<a id="78-trạng-thái-ttsđầu-vào-trước-tech-02-và-tts-01"></a>

### 7.8. Trạng thái TTS/đầu vào trước TECH-02 và TTS-01

Tại TECH-01 ngày 02/11, Gemini mới ưu tiên khảo sát; nhà cung cấp/model/giọng chưa chọn, cấu hình laptop/mạng/môn học còn thiếu. Trạng thái đó **đã được bổ sung/thay đúng phần** ngày 03/11 bởi TECH-02/TTS-01. Model/giọng nay đã chốt; không sửa nhật ký cũ thành bằng chứng đã biết từ trước. Phương án URL/Quick Tunnel, log/backup và kế hoạch demo là bối cảnh cũ, chưa từng được tiếp nhận thành quyết định dự án chính và không được kế thừa.

<a id="79-dấu-vết-trước-tech-03data-01g03--không-dùng-như-trạng-thái-hiện-hành"></a>

### 7.9. Dấu vết trước TECH-03/DATA-01/G03 — không dùng như trạng thái hiện hành

Technical-inputs 0.1 từng đặt IN-P01–IN-P05/DATA-P01–P04 ở trạng thái đề xuất; H1 ưu tiên Tailscale Funnel, các mốc Android 13/Chrome 142/iOS-Safari 18.6/Windows 11 chỉ minh họa. TECH-03 nay chốt mốc phiên bản/audio, nền tảng kiểm thực và **chọn thử ngrok Free**, thay ưu tiên Funnel. DATA-01 chỉ chốt phần được nêu, không tự duyệt nơi/nhịp/phạm vi backup/RPO-RTO. Lưu nguyên phần khảo sát/phương án bị thay ở technical-inputs mục 9.1 để truy vết; không dùng cả hai làm hiện hành. Câu “chưa tạo TDD” ở nhật ký trước lượt này chỉ phản ánh thời điểm đó.

<a id="710-dấu-vết-trước-tech-04data-02"></a>

### 7.10. Dấu vết trước TECH-04/DATA-02

TDD 0.1 từng đặt đóng gói Vue chung Spring Boot ở mức đề xuất; TECH-04 đã xác nhận hướng này, không mọi giải pháp TDD. Phần nghe tiếp/file trước đó còn so sánh full-download với tham chiếu phục vụ tạm; hướng hiện hành chọn **tải trọn MP3 cho tab**, không kéo thời gian bảo vệ file. CD-L01 vẫn đề xuất. Lưu backup ngoài laptop từng được khuyến nghị ở technical-inputs 0.1 nhưng chưa duyệt thành bắt buộc; DATA-02 nay loại khỏi yêu cầu bản chấm, không được hỏi lại như chốt chặn.

<a id="711-trước-tech-05data-03--trạng-thái-cdbackup-đã-được-thay"></a>

### 7.11. Trước TECH-05/DATA-03 — trạng thái CD/backup đã được thay

CD-L01 từng đề xuất/chưa chọn ở TDD 0.1–0.2 và nguồn TECH-03/04; nay TECH-05 chốt Tailscale riêng + OpenSSH Windows/PowerShell, **không chuyển thành đã chạy**. Phạm vi/nhịp backup từng đề xuất ở DATA-02/TDD 15; DATA-03 nay xác nhận đúng phần. Câu ví dụ TDD 0.2 “Một tuần sau phục hồi B” được sửa thành phục hồi trong hạn B, dữ liệu đã quá hạn theo mốc gốc. Nhật ký cũ giữ đúng thời điểm; không dùng trạng thái cũ như hiện hành.

<a id="dec-8"></a>

<a id="712-trước-data-04--context10-phút-và-bkp-p01-online-đã-bị-thay-đúng-phần"></a>

### 7.12. Trước DATA-04 — context/10 phút và BKP-P01 online đã bị thay đúng phần

TDD 0.4/model 0.1/API 0.2/operations 0.1 từng đề xuất start context 10 phút, window/receipt 24 giờ, job cũ bảy ngày, thu hồi quyền sau restore và online dump + catalog ở MySQL tạm. Ngày 04/11 người dùng chỉ chốt **hướng đơn giản hóa LIFE-P01**, chưa duyệt context/10 phút; các phần LIFE-P02/P03/REC-P01 nay xác nhận riêng. BKP-P01 online không còn mặc định, thay BKP-P02 tạm ngừng phục vụ có kiểm soát; phạm vi/nhịp DATA-03 không đổi. Nội dung trước xác nhận giữ ở [lịch sử mô hình/operations](data-model-history.md), không biến trạng thái cũ thành hiện hành.

<a id="713-trước-data-05--start_generationgate-purge-bị-thay-thế"></a>

### 7.13. Trước DATA-05 — start_generation/gate purge bị thay thế

TDD 0.5/model 0.2/API 0.3/operations 0.2 từng đề xuất vé ký không TTL kèm start_generation và khóa chung với purge D08 để chặn key đã dọn; chưa được xác nhận cơ chế. DATA-05/LIFE-P01 nay chọn vé ký **24 giờ**, bỏ generation và gate purge; giữ service_epoch cho REC-P01. Bản nguyên văn protocol/model generation ở [data-model-history](data-model-history.md), không hai cơ chế hiện hành. Bảng start context/10 phút của 0.4 và BKP-P01 online vẫn lịch sử, DATA-04 các phần khác giữ nguyên.

<a id="8-nguồn-căn-cứ-và-nhật-ký-thay-đổi"></a>

## 8. Nguồn, căn cứ và nhật ký thay đổi

<a id="81-căn-cứ-xác-nhận-và-ràng-buộc-cộng-tác"></a>

### 8.1. Căn cứ xác nhận và ràng buộc cộng tác

**Mục 8.1.1–8.1.2 lưu theo thời điểm đã ghi, không phải trạng thái hiện hành.** Mục 3–5 là nơi tra quyết định đang áp dụng; ràng buộc cộng tác tại 8.1.3 vẫn tiếp tục áp dụng. Giữ căn cứ xác nhận để không mất lịch sử khi rút gọn Business Flow.

<a id="811-ghi-nhận-căn-cứ-từ-decisionsmd-trước-khi-sắp-xếp"></a>

#### 8.1.1. Ghi nhận căn cứ từ decisions.md trước khi sắp xếp

- Căn cứ D01–D03 ban đầu: tin nhắn ngày 29/10/2026 bắt đầu bằng “Tôi chốt ba quyết định như sau”.
- Căn cứ xác nhận ngày 29/10/2026: tin nhắn bắt đầu bằng “Tôi xác nhận các quyết định dưới đây, bao gồm các bổ sung D01-BS, D02-BS và D03-BS”. Chỉ nội dung được nêu rõ trong xác nhận đó được chuyển từ đề xuất sang đã chốt.
- Căn cứ bổ sung ngày 30/10/2026: tin nhắn bắt đầu bằng “Tôi bổ sung xác nhận và định hướng như sau”. Chỉ chốt định hướng phiên bản đi chấm và phần cảnh báo sắp hết hạn/hiển thị thông báo khi đã hết hạn; không duyệt toàn bộ P01–P05 hoặc Business Flow.
- Đã đọc lại [business-flow.md](../02-nghiep-vu/business-flow.md) và decisions.md hiện có trước khi cập nhật. Không phát hiện mâu thuẫn với D01–D03 đã chốt. Phương án dùng chung phiên, 30 phút không hoạt động, danh sách điểm cũ và nút nghe lại là đề xuất trước đây, không phải yêu cầu đã được duyệt; nay được thay thế rõ tại mục 5.
- Căn cứ điều chỉnh tiếp ngày 30/10/2026: người dùng xác nhận mỗi địa điểm một vùng tương tác, chạm trong vùng đồng thời chọn/hiển thị/yêu cầu phát audio, chọn vùng khác phát bài mới từ đầu; giữ đổi ngôn ngữ không tự phát. P05 được chốt một vùng Tử Cấm Thành mở một bài tổng quan. Ở lượt xác nhận vùng/phát đó, những khuyến nghị P01–P04 và các tình huống bấm lại/bấm ngoài/chồng lấn chưa được xác nhận theo gói; trạng thái được cập nhật bằng xác nhận tiếp dưới đây.
- Căn cứ xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi đồng ý duyệt P01–P04 và P06–P08 theo phương án dưới đây”: chốt đúng các hành vi P nêu trong tin nhắn, bao gồm bấm lại sau khi hết bài, không kích hoạt vùng khi kéo/phóng, mốc nghe không cần chính xác từng giây và thông báo P02 có nguyên văn. P05 giữ quyết định trước; không duyệt tổng thể Business Flow hoặc năm bài vi.
- Căn cứ duyệt tổng thể ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận duyệt tổng thể luồng nghiệp vụ khách trong bản tổng hợp 13 bước gần nhất”: chốt BF-K01 đúng phạm vi khách và cách đếm, chuẩn hóa P02 chứ không mở lại quyết định; cho phép chuyển sang đề xuất Admin. Đã đối chiếu hai bản lưu với tổng hợp 13 bước và các quyết định, chưa thấy khác biệt thực chất; E15 còn chữ “có thể” đã được chuẩn hóa theo xác nhận. Không suy rộng thành hoàn thành/duyệt Admin, bản đồ/giao diện, nội dung hoặc bằng chứng kỹ thuật/kiểm thử.
- Căn cứ phản hồi Admin tiếp ngày 30/10/2026: người dùng đồng ý A01/A03/A06/A07 cùng phần làm rõ, chốt một QR chung A05 cho cả điểm thêm sau; thay giới hạn A02 bằng thêm/sửa/ẩn-hiện/xóa và chọn cơ chế ứng dụng xóa phụ thuộc/giao dịch/khóa ngoại; chọn hướng A04 tạo audio từ văn bản. Chính sách từng dữ liệu/file/lượt xem/vùng/hiển thị mới và vận hành tạo chưa được duyệt. Lý do mở rộng CRUD: cần trình diễn thêm/xóa khi chấm; bộ dữ liệu nội dung chuẩn vẫn năm điểm/20 văn bản/20 audio.
- Căn cứ xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận các phương án Admin còn mở như dưới đây”: chốt các chi tiết A02.1–A02.5/A04.1–A04.4; A02.1 cụ thể hóa cơ chế xóa đã chốt, không phải cơ chế mới yêu cầu duyệt lại. A02.4 chọn Admin vẽ/xem trước/lưu/sửa-vẽ lại vùng, thay cấu hình ngoài; bố cục/thao tác/ranh giới xem xét riêng. Giữ bộ nội dung năm điểm/bốn ngôn ngữ do người dùng chọn, không phải trần do giảng viên; không thêm quản lý ngôn ngữ. Ở thời điểm xác nhận đó chưa duyệt tổng thể Admin/toàn bộ Business Flow; trạng thái hiện hành được thay bằng BF-A01/BF-ALL01 dưới đây.
- Căn cứ xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận A02.6 và duyệt tổng thể luồng Admin”: đã đọc/đối chiếu bản lưu với 12 nhóm Admin, không phát hiện khác biệt thực chất. Chốt A02.6 và phạm vi BF-A01/BF-ALL01, giữ BF-K01. Không suy rộng sang các phần thiết kế/nội dung/TTS hoặc phần mềm. PRD/Use Case/tiêu chí nghiệm thu là dự thảo mới, không được duyệt theo xác nhận Business Flow.
- D07 từng chốt không tự phát khi đổi địa điểm; phần này nay được thay thế theo xác nhận mới, không gọi nhầm là chỉ một đề xuất cũ. Không thay quy tắc đổi ngôn ngữ. Các mô tả trước cập nhật phải đọc cùng dấu vết thay thế ở mục 5.
- Mã BS là bổ sung cho quyết định cùng số, không hủy bỏ phần quyết định gốc; D04-BS ghi phần cảnh báo. G01 ghi định hướng phiên bản đi chấm. Mã P nhận diện tình huống đã từng đề xuất riêng; trạng thái phải đọc theo bản hiện hành. P01–P08 nay đã chốt đúng phạm vi xác nhận, không tiếp tục coi tất cả mã P là đề xuất. Gán mã chỉ phục vụ tra cứu, không tự thêm yêu cầu.
- Quyết định nghiệp vụ không chứng minh code đã triển khai/kiểm thử hoặc nội dung lịch sử, bản dịch, audio đã được kiểm chứng.
- Chưa khảo sát dự án tham khảo hoặc bằng chứng triển khai trong lần cập nhật này. Chỉ cập nhật tài liệu; không thao tác code, cấu hình, database, kiểm thử, Git hoặc triển khai.
- Các ghi nhận lịch sử và nguồn tham khảo được giữ tại mục 5–7; không dùng chúng như yêu cầu đang áp dụng nếu đã bị thay thế.

<a id="812-ghi-nhận-theo-lượt-chuyển-từ-business-flow"></a>

#### 8.1.2. Ghi nhận theo lượt chuyển từ Business Flow

Đã đọc lại business-flow.md và decisions.md hiện có trước lần cập nhật này. Căn cứ áp dụng là D01–D03 đã được xác nhận trước đó và tin nhắn người dùng ngày 29/10/2026 bắt đầu bằng “Tôi xác nhận các quyết định dưới đây, bao gồm các bổ sung D01-BS, D02-BS và D03-BS”. Không có mâu thuẫn với D01–D03 đã chốt; các khác biệt với phương án D04/D06/D07 cũ là thay thế đề xuất chưa được duyệt, được ghi tại mục 5.1.

Bổ sung căn cứ ngày 30/10/2026: người dùng xác nhận định hướng phiên bản đi chấm và cảnh báo sắp hết hạn. Ghi mã **G01** cho định hướng và **D04-BS** cho cảnh báo để dễ đối chiếu; các mã này không tự bổ sung chức năng. Xác nhận đó không duyệt toàn bộ P01–P05 hoặc Business Flow.

Cập nhật tiếp ngày 30/10/2026 theo xác nhận “Hãy đọc lại … cách chọn địa điểm bằng vùng trên bản đồ”: D01/D01-BS và D07 được điều chỉnh để chọn vùng đồng thời yêu cầu phát audio; đổi ngôn ngữ vẫn dừng/đặt về đầu và không tự phát. P05 được chốt là một vùng Tử Cấm Thành mở một bài tổng quan. Ở thời điểm xác nhận vùng/phát đó, P01–P04 và P06–P08 chưa được duyệt. Phần D07 từng chốt không tự phát khi đổi địa điểm nay được thay thế đúng phạm vi này, không gọi nhầm là chỉ một đề xuất cũ.

Xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi đồng ý duyệt P01–P04 và P06–P08 theo phương án dưới đây”: chuyển đúng các hành vi trong xác nhận sang **đã chốt**, giữ P05 đã chốt trước đó; cập nhật thông báo không thể tiếp tục phiên, trạng thái đã phát hết, phân biệt kéo/phóng bản đồ và mức chính xác mốc nghe. Ở thời điểm xác nhận P đó, toàn bộ Business Flow còn chờ duyệt tổng thể; năm bài vi là bản nháp chờ cung cấp/rà soát.

Xác nhận hiện hành ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận duyệt tổng thể luồng nghiệp vụ khách trong bản tổng hợp 13 bước gần nhất”: **Luồng nghiệp vụ khách đã được duyệt tổng thể**. Đã đối chiếu bản lưu với 13 bước và quy tắc đếm đã trình bày, chưa phát hiện khác biệt thực chất. Chuẩn hóa P02 theo câu người dùng cung cấp, không mở lại quyết định. Ghi mã BF-K01 để tra cứu phạm vi duyệt; không suy rộng sang Admin, bản đồ/giao diện, nội dung, TDD hoặc phần mềm.

Cập nhật phản hồi Admin ngày 30/10/2026: xác nhận A01/A03/A06/A07, một QR chung A05; A02 thêm/sửa/ẩn-hiện/xóa và cơ chế toàn vẹn/xóa giao dịch; A04 chọn tạo audio từ văn bản. Giới hạn cũ “không thêm/xóa điểm” và “chỉ tải file audio” đã bị thay thế. Luồng khách BF-K01 giữ hiệu lực; chỉ điều chỉnh số lượng điểm/vùng từ giới hạn cứng thành khả năng mở rộng. Năm điểm và 20 văn bản/20 audio vẫn là bộ nghiệm thu. Ở lượt phản hồi Admin đó, chính sách dữ liệu phụ thuộc/file/vùng mới/điều kiện hiện điểm và vận hành tạo audio còn là đề xuất; trạng thái được cập nhật bằng xác nhận tiếp dưới đây.

Xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận các phương án Admin còn mở như dưới đây”: chốt A02.1–A02.5 và A04.1–A04.4 đúng nội dung xác nhận, trong đó A02.1 chỉ cụ thể hóa quy trình xóa đã chốt. **A02.4 thay phương án cấu hình ngoài bằng Admin vẽ/xem trước/lưu và sửa/vẽ lại vùng trực tiếp.** Giữ lịch sử đến hạn D08, dọn file tách database với bảo vệ 8 tiếng, điều kiện hiện điểm mới và vận hành tạo đã chốt; không yêu cầu duyệt lại. Ở thời điểm xác nhận đó, Admin/toàn bộ Business Flow chưa được duyệt tổng thể và A02.6 còn mở; trạng thái hiện hành đã được thay bằng xác nhận tiếp dưới đây.

Xác nhận tiếp ngày 30/10/2026 bắt đầu bằng “Tôi xác nhận A02.6 và duyệt tổng thể luồng Admin”: đã đọc lại hai file, đối chiếu bản lưu với 12 nhóm Admin được duyệt, không phát hiện khác biệt thực chất. Chốt A02.6: giữ vùng hiện hành khi sửa; “Lưu bản sửa” khác “Áp dụng”; xem trước/xem xét rồi chủ động áp dụng, khách nhận vùng mới ở lần tải bản đồ tiếp theo, không đẩy tức thì/ngắt audio/ép kết thúc phiên. Ghi BF-A01 cho duyệt tổng thể Admin và BF-ALL01 cho Business Flow hai vai trò trong phạm vi nghiệp vụ; BF-K01 giữ nguyên. Không duyệt theo giao diện/ranh giới, nội dung, nhà cung cấp/giọng/chi phí/công nghệ TTS, thiết kế kỹ thuật hoặc kết quả kiểm thử. PRD/Use Case/tiêu chí nghiệm thu mới soạn là dự thảo chờ xem xét.

- **Đã chốt:** chỉ nội dung người dùng xác nhận trực tiếp, chi tiết tại [decisions.md](../02-nghiep-vu/decisions.md).
- **Đề xuất / còn mở:** chưa được xác nhận, không dùng như yêu cầu đã duyệt.
- **Đã bị thay thế:** chỉ giữ để tra cứu lịch sử, không còn là phương án đang áp dụng.
- Việc lưu tài liệu hoặc chốt quy trình không đồng nghĩa duyệt nội dung thuyết minh hay duyệt toàn bộ Business Flow.

Khi tạo bản đầu của tài liệu, repo chỉ có README.md nêu tên dự án và chưa có docs/. Lần này đã đối chiếu hai tài liệu hiện tại; chưa khảo sát dự án tham khảo, code hoặc bằng chứng kiểm thử. Không có cơ sở tuyên bố phần mềm, lịch sử, bản dịch hoặc audio đã được kiểm chứng.

Ghi nhận bối cảnh lúc khởi tạo: repo khi đó chỉ có README.md nêu tên dự án, chưa có docs/. Các lần ghi nhận cũ không khảo sát dự án tham khảo/code/bằng chứng kiểm thử và không chứng minh nội dung/phần mềm đã được kiểm chứng. Lần sắp xếp này đọc ba file hiện tại và README, không khảo sát code hoặc mở dự án tham khảo.

<a id="813-ràng-buộc-cộng-tác-tiếp-tục-áp-dụng"></a>

#### 8.1.3. Ràng buộc cộng tác tiếp tục áp dụng

- Trợ lý được tạo/cập nhật tài liệu phục vụ công việc được giao sau khi kiểm tra và bảo vệ nội dung hiện có.
- Người dùng tự thực hiện code, cấu hình chạy, database, kiểm thử, Git và triển khai; trợ lý chỉ thao tác khi có lệnh trực tiếp cho thao tác cụ thể.
- Không sửa, xóa, reset hoặc di chuyển dự án tham khảo; không mặc định sao chép code, dữ liệu, secret, database hay lịch sử Git cũ.
- Không đưa thông tin nhạy cảm vào tài liệu; không tuyên bố lịch sử, bản dịch, audio hoặc phần mềm đã được kiểm chứng khi chưa có bằng chứng.

<a id="814-xác-nhận-q1q3-ngày-31102026"></a>

#### 8.1.4. Xác nhận Q1–Q3 ngày 31/10/2026

Người dùng duyệt Q1–Q3 tại mục 9.4 PRD 0.3, bao gồm các chi tiết/giới hạn đã trình bày; yêu cầu cụ thể hóa BR/DR/FR/UC/AC/NFR, đồng bộ Business Flow và giữ R06 mở với phần phụ thuộc còn lại. Đã đối chiếu bản lưu; không phát hiện mâu thuẫn thực chất. Xác nhận chỉ áp dụng Q1–Q3, không duyệt tổng thể PRD hoặc chọn công nghệ/TTS/ngưỡng kỹ thuật. Nội dung hiện hành tại mục 5.8, lịch sử nguyên bản tại mục 7.3.

<a id="815-xác-nhận-phạm-vi-prd-041-ngày-31102026"></a>

#### 8.1.5. Xác nhận phạm vi PRD 0.4.1 ngày 31/10/2026

Người dùng xác nhận các yêu cầu đã mô tả rõ trong PRD 0.4.1, gồm Q1–Q3 và BR-11 đã sửa; loại trừ mục còn mở/đề xuất/chưa kiểm chứng và thiết kế/nội dung/TTS/kỹ thuật/kết quả kiểm thử. Đã đối chiếu đúng phiên bản trước ghi nhận, không phát hiện khác biệt ảnh hưởng. Nội dung hiện hành tại [PRD-01 mục 3.3](../02-nghiep-vu/decisions.md#prd-approval). Cùng lượt được giao bắt đầu phác thảo khách dự thảo tại ui-design.md; không là phê duyệt các phương án mới.

<a id="816-xác-nhận-hướng-giao-diện-khách-ngày-31102026"></a>

#### 8.1.6. Xác nhận hướng giao diện khách ngày 31/10/2026

Căn cứ tin nhắn “Tôi đã thống nhất với chat đánh giá về hướng giao diện khách tham quan dưới đây”; bản thực tế đọc là ui-design 0.1 và PRD 0.4.1. Ghi UI-01 tại 4.11, không suy rộng UI-P01/UI-P02/UI-P04 hoặc bản đồ/bản dịch/kỹ thuật. Dấu vết thay thế tại mục 7.1; bản phác thảo/lịch sử tại ui-design và ui-design-history.

<a id="817-xác-nhận-ui-02-ngày-01112026"></a>

#### 8.1.7. Xác nhận UI-02 ngày 01/11/2026

Đọc docs/README và bản lưu Business Flow/decisions/PRD 0.5/báo cáo/UI 0.2 trước ghi. Căn cứ tin nhắn “Tôi xác nhận các phương án UI dưới đây sau khi trao đổi với chat đánh giá”: chốt đúng UI-P04 box toàn trang, box hết hạn chỉ trong bản đồ, ngoại lệ nghe hết bài đang phát, UI-P01 ứng viên và UI-P02 tua còn hiệu lực. Người dùng nêu rõ đây là thay đổi có chủ đích P01; nguồn hiện hành 4.12/P01, dấu vết thay thế 7.4. Giữ mã cũ, mốc 8 tiếng/lượt/30 ngày, giới hạn file và phần chưa duyệt khác. Không sửa ui-design-history.md hoặc tài sản bản đồ.

<a id="818-xác-nhận-ui-03-ngày-02112026"></a>

#### 8.1.8. Xác nhận UI-03 ngày 02/11/2026

Người dùng gửi yêu cầu trực tiếp “Tôi đã xem đánh giá về UI Admin lượt 1…” (cùng nội dung văn bản đính kèm đã đọc), xác nhận UI-AP01–UI-AP03 tại UI 0.4 mục 10, chỉnh nhãn tên và làm rõ quy tắc xem trước/lưu/áp dụng, đồng thời giao thiết kế lượt 2 **dự thảo**. Đã đọc docs/README, Business Flow/decisions/PRD 0.6/UI 0.4; không có khác biệt thực chất cần mở lại quyết định. Xác nhận hiện hành tại 5.9, dấu vết trước xác nhận tại 7.5; không lấy toàn bộ bản UI hoặc các câu hỏi lượt 2 làm nội dung đã duyệt.

<a id="819-xác-nhận-ui-04-ngày-02112026"></a>

#### 8.1.9. Xác nhận UI-04 ngày 02/11/2026

Người dùng xác nhận “Tôi xác nhận các hướng UI Admin lượt 2 dưới đây…” và chủ đích chọn chỉ áp dụng cả cặp, cập nhật gate bật hiện; chốt UI-AP04/UI-AP05/UI-AP07 và thay UI-AP06. Đối chiếu trực tiếp PRD 0.6/UI 0.5 cùng nguồn nghiệp vụ, không giả định toàn bộ đề xuất được duyệt. Hiện hành tại 5.10; dấu vết trước thay đổi tại 7.6. Đồng bộ PRD 0.7/UI 0.6/Business Flow/báo cáo và mục lục; UI Admin lượt 3 vẫn dự thảo.

<a id="8110-xác-nhận-ui-05-ngày-02112026"></a>

#### 8.1.10. Xác nhận UI-05 ngày 02/11/2026

Căn cứ yêu cầu “Tôi xác nhận các phương án UI Admin lượt 3 dưới đây…”; đọc trực tiếp docs/README, Business Flow/decisions/PRD 0.7/prd-review/UI 0.6 trước cập nhật. Người dùng duyệt UI-AP08–UI-AP10 đúng phần, đổi UI-A21 từ trang riêng sang box, giao rà soát nhất quán/tổng hợp công việc trước TDD. Nguồn chính 5.11, dấu vết đề xuất trước đó 7.7. Bản sau PRD 0.8/UI 0.7; không xác nhận mọi chi tiết còn mở hoặc phần mềm đã đáp ứng.

<a id="g02-source"></a>

<a id="8111-xác-nhận-g02-ngày-02112026"></a>

#### 8.1.11. Xác nhận G02 ngày 02/11/2026

Căn cứ yêu cầu “Hãy chỉnh lại bản dự thảo 11 issue trong roadmap theo hướng ngắn gọn và ghi nhận phân công nhóm dưới đây.” Người dùng xác nhận Thành phụ trách Backend khách/Admin và TTS; Nghĩa phụ trách Frontend khách/Admin; Hòa phụ trách Database, kiểm thử tích hợp và Git/CI/CD/triển khai; T01/T08/T09/T11 phối hợp theo [G02](../02-nghiep-vu/decisions.md#g02). Yêu cầu chỉ giữ Description và một Checklist cho mỗi issue, bảo toàn tiến độ, chưa duyệt nội dung issue hoặc thực hiện GitHub/kỹ thuật. Trước ghi đã đọc docs/README, roadmap 0.1 và phần quyết định liên quan; biên tập thành roadmap 0.2, giữ lịch sử 0.1. Không gán công việc đã làm cho người mới được phân công.

<a id="tech-01-source"></a>

<a id="8112-xác-nhận-tech-01-ngày-02112026"></a>

#### 8.1.12. Xác nhận TECH-01 ngày 02/11/2026

Người dùng xác nhận trực tiếp bộ công nghệ dự án chính và kiến trúc tại [3.5](../02-nghiep-vu/decisions.md#tech-01), nêu rõ đây là quyết định đã duyệt, không chỉ đề xuất demo. Đồng thời xác nhận chưa quen Vue/Vite/npm/Leaflet/Geoman, môi trường Windows/VS Code/PowerShell; yêu cầu hướng dẫn từng phần, chỉ hỏi tối đa ba nhóm đầu vào còn ảnh hưởng lớn. Gemini TTS chỉ là ưu tiên khảo sát; FFmpeg/ffprobe, phiên bản/tương thích, hosting/ngân sách/môi trường và chính sách còn mở. Chỉ cập nhật tài liệu, không Git/GitHub hoặc triển khai. Đã đọc bản thực tế trước sửa; dữ kiện và lựa chọn ở đây do người dùng cung cấp, không phải kết quả khảo sát dịch vụ hoặc thử tương thích.

**Căn cứ quản lý backlog trước TECH-01:** người dùng đã nói “Tôi duyệt backlog. Giờ bạn hãy tạo vào GitHub Project này” với Project #20. Lượt đó đã tạo T01–T11 thành #49–#59 trong repo chính, đọc lại nội dung issue và quan sát Project Todo 8/In Progress 3 (T01/T08/T10)/Done 0; bước tải lại cuối bị công cụ dừng. Lượt TECH-01 chỉ đồng bộ hồ sơ local theo bằng chứng đã có, không truy cập hoặc sửa GitHub để khẳng định trạng thái hiện tại.

<a id="tech-02-source"></a>

<a id="8113-xác-nhận-tech-02-và-tts-01-ngày-03112026"></a>

#### 8.1.13. Xác nhận TECH-02 và TTS-01 ngày 03/11/2026

Căn cứ hai tin nhắn “Tôi bổ sung đầu vào cho ba nhóm câu hỏi trước…” và “Bổ sung cho yêu cầu đang thực hiện”. Tin đầu xác nhận phương pháp hướng dẫn/môn học/thiết bị/4G/mốc mục tiêu/Free tier và giao khảo sát demo chỉ đọc, nguồn chính thức, chính sách/kế hoạch mới. Tin bổ sung chốt gemini-3.8-flash-tts/Charon từ sáu MP3 người dùng đã nghe; yêu cầu không kế thừa phương án URL/Quick Tunnel, log/backup hoặc lịch cũ. Các phần khác giữ nguyên.

Đã đọc tài liệu hiện hành, phần demo được giao và nguồn chính thức; kết quả tại [technical-inputs 0.1](../01-tong-quan/technical-inputs.md). Xác nhận đầu vào/TTS tách khỏi đề xuất IN-P/DATA-P và lịch chưa duyệt. Không có quyền gọi API, dùng ngân sách, đăng ký/cài dịch vụ hoặc code/test/Git/triển khai; không sao chép demo. Giữ TECH-01/G02/nghiệp vụ.

<a id="tech-03-source"></a>

<a id="8114-xác-nhận-tech-03data-01g03-ngày-03112026"></a>

#### 8.1.14. Xác nhận TECH-03/DATA-01/G03 ngày 03/11/2026

Tin nhắn “Tôi đã trao đổi và quyết định các phương án còn mở…” xác nhận IN-P01–IN-P03, chọn THỬ ngrok IN-P04 và các điều kiện/hạn IN-P05; giữ model/Charon, nghiệp vụ và mốc 8 giờ/30 ngày. Người dùng chấp nhận backup gần 37 ngày, chưa chọn nơi/RPO-RTO; yêu cầu lập lịch theo giả định không hỏi lại quỹ giờ tổng quát, nhắc đúng checklist GitHub và bắt đầu TDD dự thảo tổng thể. Không đồng nghĩa duyệt giải pháp TDD/CD hoặc phần mềm.

Trước ghi đọc tài liệu hiện hành theo docs/README; TDD chưa tồn tại. Kiểm nguồn ngrok chính thức 03/11, đọc đúng 11 issue #49–#59 bằng công cụ GitHub chỉ đọc, không sửa issue/Project hoặc Git. Kết quả/các khác biệt tại roadmap 6; không dùng lịch sử demo hoặc phương án cũ để suy ngược xác nhận.

<a id="tech-04-source"></a>

<a id="8115-xác-nhận-tech-04data-02-ngày-03112026"></a>

#### 8.1.15. Xác nhận TECH-04/DATA-02 ngày 03/11/2026

Người dùng “Tôi đã rà soát TDD 0.1 cùng chat đánh giá…” xác nhận đúng đóng gói/chia lớp, làm rõ độc lập tab/backend kiểm quyền, chọn tải trọn MP3 và điều kiện nghe tiếp/giải phóng; thu gọn backup cục bộ, giữ hạn/restore. CD-L01 chưa duyệt. Giao tiếp TDD dữ liệu/ERD và cơ chế cần thiết, không toàn API/test, không thực thi phần mềm/dịch vụ/Git/GitHub hoặc demo. Đã đọc file thực tế trước ghi, đối chiếu checkpoint GitHub chỉ đọc theo G03; không suy lời xác nhận thành đã kiểm tương thích hoặc triển khai.

<a id="tech-05-source"></a>

<a id="8116-xác-nhận-tech-05data-03-và-giao-thiết-kế-apigiao-dịch"></a>

#### 8.1.16. Xác nhận TECH-05/DATA-03 và giao thiết kế API/giao dịch

Ngày 03/11/2026 người dùng “Tôi đã rà soát TDD 0.2 và xác nhận các quyết định sau…” chốt backup và CD-L01, yêu cầu sửa ví dụ đúng hạn bản sao, tiếp tục API/giao dịch theo mô hình dự thảo và chỉ hỏi khi thay phạm vi/hành vi. Không duyệt toàn TDD/ERD hoặc kết quả tương thích/CD. Đã đọc file thực tế theo docs/README và đối chiếu checklist liên quan chỉ đọc; lượt này chỉ tài liệu, không code/config/database/test/Git hoặc thay GitHub/dịch vụ/triển khai.

<a id="data-04-source"></a>

<a id="8117-xác-nhận-vòng-đờirestorebackup-và-yêu-cầu-đơn-giản-hóa--04112026"></a>

#### 8.1.17. Xác nhận vòng đời/restore/backup và yêu cầu đơn giản hóa — 04/11/2026

Người dùng “Tôi xác nhận các quyết định sau sau khi trao đổi với chat đánh giá…” chốt LIFE-P02/P03/REC-P01 và chọn backup tạm ngừng phục vụ ngoài demo; LIFE-P01 chỉ duyệt hướng tìm cơ chế đơn giản hơn, chưa start context/10 phút. Yêu cầu đồng bộ nguồn/thiết kế, giữ dấu vết, sửa dấu # thừa trước nhật ký, không code/config/database/test/Git/GitHub/triển khai. Căn cứ này không duyệt toàn TDD/API hoặc kiểm thực; các mô tả ticket/generation/drain là đề xuất kỹ thuật.

<a id="data-05-source"></a>

<a id="8118-xác-nhận-life-p01-vé-ký-24-giờ--04112026"></a>

#### 8.1.18. Xác nhận LIFE-P01 vé ký 24 giờ — 04/11/2026

Người dùng “Tôi xác nhận điều chỉnh LIFE-P01 theo hướng sau…” chọn vé ký 24 giờ gắn request/quyền tab, bỏ context/10 phút/start_generation; giữ epoch/REC-P01/Admin cookie. Xác nhận lookup kết quả đã tạo phải kiểm đủ quyền/epoch/yêu cầu và không tạo/gia hạn lại bằng vé hết hạn; chỉ khi chưa có session mới kiểm hạn vé để tạo lần đầu. D08 giữ chống lặp cùng session; UI không tự tạo vé/session khác khi pending, không thêm thao tác/đồng hồ. Giao đồng bộ/ma trận chưa chạy và chuẩn bị thực hành người dùng tự làm, không code/config/DB/test/Git/GitHub/triển khai, không duyệt toàn TDD.

<a id="dec-sources"></a>

<a id="82-nguồn-tham-khảo-trước-đây-và-giới-hạn"></a>

### 8.2. Nguồn tham khảo trước đây và giới hạn

Các nguồn dưới đây đã được ghi trong bản tài liệu trước, chỉ phục vụ đề xuất tên/phạm vi điểm khi đó. Lần cập nhật hiện tại dùng danh sách do người dùng chọn; không khảo sát lịch sử mới hoặc khẳng định đã kiểm chứng toàn bộ năm điểm/bài mới.

- [Cục Di sản văn hóa — Quần thể Di tích cố đô Huế](https://dsvh.gov.vn/quan-the-di-tich-co-do-hue-475): nguồn đã dùng trong bước trước để đối chiếu phạm vi/tên công trình. Chưa phải căn cứ duyệt các bản thuyết minh.
- [Cửu đỉnh Huế: Bộ sưu tập độc đáo hình ảnh đất nước](https://scov.gov.vn/dat-nuoc-con-nguoi/dat-nuoc-viet-nam/cuu-dinh-hue-bo-suu-tap-doc-dao-hinh-anh-dat-nuoc.html): bài trên cổng thông tin Ủy ban nhà nước về người Việt Nam ở nước ngoài, ghi tác giả Ngô Minh Thuyên (VOV), ngày 21/02/2012; được dùng ở bước trước cho quan hệ địa điểm trong danh sách đề xuất cũ. Nguồn đăng lại, không thay thế rà soát chuyên ngành cho bài thuyết minh.
- Ngày truy cập ghi nhận ở bản trước: 29/10/2026. Trang Phú Bài khi đó không trả nội dung bài, trang điểm đến của Cục Du lịch gặp lỗi; không dùng hai trang làm căn cứ xác nhận.
- Chưa kiểm chứng nội dung năm bản nháp vi, bản dịch, audio, sơ đồ thực tế, điều kiện mở cửa hay lộ trình tham quan. Các câu mô tả nội dung dự kiến không được tự biến thành khẳng định lịch sử.
- Khi người dùng cung cấp bản nháp, cần đối chiếu nguồn phù hợp cho các dữ kiện được sử dụng và ghi giới hạn rà soát theo D09.

<a id="dec-history"></a>

<a id="83-nhật-ký-thay-đổi"></a>

### 8.3. Nhật ký thay đổi

Các dòng gốc được giữ nguyên, kể cả trạng thái đúng ở thời điểm đó. Số mục cũ được giải nghĩa tại mục 8.4; lần này không sửa lại quá khứ.

<a id="831-nhật-ký-decisionsmd"></a>

#### 8.3.1. Nhật ký decisions.md

| Ngày | Thay đổi | Căn cứ |
| --- | --- | --- |
| 29/10/2026 | Tạo mới, ghi nhận D01–D03 và chỉ mục điểm còn mở; liên kết Business Flow đã cập nhật. | Xác nhận trực tiếp của người dùng về ba quyết định. |
| 29/10/2026 | Bổ sung mục 6: bộ phương án cụ thể D04–D09 và D01-BS/D02-BS/D03-BS, quy tắc phiên/thống kê, ma trận nội dung, dữ liệu và duyệt nội dung; ghi nguồn/giới hạn kiểm tra địa điểm. Giữ nguyên các mục D01–D03 đã chốt. | Người dùng yêu cầu phân tích để xem xét; toàn bộ đề xuất mới chưa được xác nhận. D05 chỉ nhắc lại D01. |
| 29/10/2026 | Đọc/đối chiếu hai tài liệu và ghi xác nhận D01–D09, D01-BS/D02-BS/D03-BS. Thay dùng chung phiên bằng mỗi tab một phiên, 30 phút không hoạt động bằng 8 tiếng cố định, danh sách điểm và điều khiển audio cũ. Cập nhật ma trận 20/20, tự xóa 30 ngày, quy trình duyệt và trạng thái năm bản nháp vi. Chuyển đề xuất cũ sang dấu vết không áp dụng; tách P01–P05 chưa duyệt. | Xác nhận trực tiếp của người dùng bắt đầu “Tôi xác nhận các quyết định dưới đây…”; không duyệt tổng thể Business Flow hoặc nội dung thuyết minh. |
| 30/10/2026 | Bổ sung G01 về bản hoàn chỉnh đi chấm và D04-BS về cảnh báo khi còn trên 0 đến 5 phút, không gián đoạn, không lặp liên tục, tải lại/quay lại vẫn nhận biết; hết hạn ưu tiên thông báo hết hạn. Giữ D01–D09/BS đã chốt; rà lại P01–P05 và đổi khuyến nghị P03 sang giữ mốc nghe hợp lệ khi tải lại. Ghi rõ các phần còn lại chưa được duyệt. | Xác nhận trực tiếp của người dùng “Tôi bổ sung xác nhận và định hướng như sau”; không duyệt tổng thể Business Flow hay toàn bộ P01–P05. |
| 30/10/2026 | Đọc/đối chiếu hai file; cập nhật D01/D01-BS/D07 theo chọn năm vùng đồng thời yêu cầu phát audio, đổi vùng phát bài mới từ đầu, giữ đổi ngôn ngữ không tự phát. Chốt P05 vùng/bài tổng quan Tử Cấm Thành; phân biệt vùng tương tác với định vị GPS và ghi lịch sử thay thế. Hoàn thiện đề xuất P01–P04, tách P02 theo trạng thái, thêm P06–P08. | Xác nhận trực tiếp về vùng/phát và P05; không duyệt P01–P04/P06–P08, hình dạng ranh giới hoặc toàn bộ Business Flow. |
| 30/10/2026 | Đọc/đối chiếu bản hiện có; chuyển P01–P04/P06–P08 sang đã chốt đúng xác nhận mới, giữ P05. Cập nhật bảng quyết định/tham chiếu D và chi tiết xử lý; ghi rõ bấm lại sau hết bài, phân biệt kéo/phóng, mốc nghe không cần đúng từng giây, thông báo P02 và tải lại khác thử lại lỗi audio. Giữ lịch sử thay thế, ranh giới/giao diện chờ thiết kế và trạng thái năm bản nháp vi. | Tin nhắn người dùng “Tôi đồng ý duyệt P01–P04 và P06–P08 theo phương án dưới đây”; không duyệt tổng thể Business Flow hoặc nội dung thuyết minh. |
| 30/10/2026 | Đọc/đối chiếu bản lưu với tổng hợp 13 bước/cách đếm; ghi BF-K01 duyệt tổng thể riêng luồng khách và chuẩn hóa P02. Giữ phạm vi chưa duyệt ranh giới/giao diện/nội dung/TDD; ghi A01–A07 là đề xuất Admin, không mở lại quyết định khách. | Xác nhận của người dùng “Tôi xác nhận duyệt tổng thể luồng nghiệp vụ khách…”; Admin vẫn dự thảo, Business Flow tổng thể chưa hoàn thành. |
| 30/10/2026 | Đọc/đối chiếu phản hồi Admin; ghi A01/A03/A05/A06/A07 đã chốt, A02 CRUD và cơ chế xóa đã chốt, A04 chọn tạo audio từ văn bản. Thay giới hạn cứng năm điểm/chỉ tải audio; giữ bộ chuẩn 5 điểm/20 văn bản/20 audio, chỉ điều chỉnh số lượng trong luồng khách. Tách A02.1–A02.5/A04.1–A04.4 là đề xuất, không tự xóa lượt xem/file hoặc chọn dịch vụ. | Người dùng xác nhận theo từng phần; chưa duyệt tổng thể Admin hoặc chính sách vận hành còn mở. Chưa thao tác database/code/kiểm thử. |
| 30/10/2026 | Đọc/đối chiếu bản hiện tại; chốt A02.1–A02.5/A04.1–A04.4 đúng xác nhận. A02.1 cụ thể hóa cơ chế xóa đã chốt; thay A02.4 nhận cấu hình ngoài bằng vẽ/sửa vùng trực tiếp. Đồng bộ dữ liệu lịch sử độc lập, file tách giao dịch với bảo vệ 8 tiếng, điều kiện hiện điểm, trạng thái/phiên bản/tác vụ audio; giữ dấu vết phương án cũ. Nêu riêng A02.6 hiệu lực sửa vùng đang hiển thị chưa được xác nhận. | Người dùng yêu cầu chuyển sang rà soát tổng thể Admin, chưa duyệt tổng thể; chưa chọn/kiểm chứng TTS hay giao diện/ranh giới. |
| 30/10/2026 | Đọc/đối chiếu bản lưu với 12 nhóm Admin, không có khác biệt thực chất. Chốt A02.6 giữ vùng hiện hành, tách Lưu bản sửa/Áp dụng, xem trước/xem xét rồi áp dụng, hiệu lực lần tải bản đồ tiếp theo. Ghi BF-A01/BF-ALL01, giữ BF-K01, dấu vết thay thế và các nhật ký trước. Tạo PRD/Use Case/tiêu chí nghiệm thu có truy vết. | Xác nhận người dùng “Tôi xác nhận A02.6 và duyệt tổng thể luồng Admin”; chỉ duyệt phạm vi nghiệp vụ, PRD/Use Case/tiêu chí nghiệm thu còn dự thảo. |
| 30/10/2026 | Sắp xếp quyết định hiện hành trước lịch sử, tạo chỉ mục/vị trí chính và neo liên kết; gom phần trùng bằng dẫn chiếu, chuyển căn cứ/nhật ký Business Flow về đây, sửa câu trạng thái cũ theo quyết định đã chốt. Rà soát trực tiếp PRD; chỉ sửa PRD các dẫn chiếu bị ảnh hưởng, báo cáo riêng phát hiện tại [prd-review.md](../02-nghiep-vu/prd-review.md). | Chỉ thay cấu trúc và dẫn chiếu; không phê duyệt nghiệp vụ mới, không đổi mã/thời hạn/điều kiện/phạm vi hoặc trạng thái duyệt. PRD vẫn dự thảo. |
| 31/10/2026 | Bổ sung dẫn chiếu ở A02.1 tới quy trình xóa hiện hành tại Business Flow mục 4.2/A02 sau khi phát hiện đoạn lịch sử bị đặt nhầm trong lần sắp xếp trước; ghi lưu ý nguồn S01 ở prd-review.md. | Chỉ bổ sung dẫn chiếu/nhật ký; không thay nội dung quyết định nghiệp vụ. Phần chuẩn hóa đoạn lịch sử chưa thực hiện trong phạm vi lượt này. |
| 31/10/2026 | Khôi phục nội dung hiện hành A02.1 từ Business Flow 4.2/căn cứ xác nhận còn lưu; đưa câu kể lịch sử ra khỏi nhãn Đã chốt, giữ câu đó tại lịch sử mục 7.1. Đồng bộ S01 trong prd-review.md. | Sửa trình bày và dẫn chiếu, không đổi hoặc yêu cầu duyệt lại cơ chế xóa; không thao tác database/file thực tế. |
| 31/10/2026 | Đối chiếu và ghi xác nhận Q1–Q3; liên kết A01/A02/A03/A04, giữ toàn bộ giới hạn và chuyển nguyên bản đề xuất PRD 0.3 sang mục 7.3. Đồng bộ PRD/Business Flow/prd-review, giữ các quyết định khác và nhật ký cũ. | Chỉ Q1–Q3 được xác nhận; toàn bộ PRD vẫn dự thảo chờ duyệt tổng thể; chưa triển khai hoặc kiểm thử phần mềm. |
| 31/10/2026 | Ghi PRD-01 và căn cứ 8.1.5: duyệt phạm vi yêu cầu đã rõ của đúng PRD 0.4.1; đồng bộ trạng thái hiện hành và liên kết ui-design.md dự thảo. Giữ Q1–Q3, lịch sử đề xuất, mã yêu cầu và các điểm mở. | Xác nhận trực tiếp của người dùng; không duyệt ngầm thiết kế/nội dung/TTS/kỹ thuật/ngưỡng hoặc kết quả phần mềm. |
| 31/10/2026 | Đọc bản lưu sau thông tin hoàn tác; ghi UI-01/UI-P03 đúng phần xác nhận: khung tham quan/box cuộn, nút ba trạng thái, ngôn ngữ chung, xử lý/chống lặp Bắt đầu-Kết thúc và giới hạn kế thừa. Đồng bộ PRD 0.5/Business Flow/ui-design 0.2; lưu lịch sử 0.1 và nền/vùng đề xuất có nguồn. | Chỉ nội dung tin nhắn hiện tại được chốt; UI-P01, phần còn lại UI-P02 và UI-P04 chưa duyệt; nền/vùng/bản dịch/kỹ thuật/TTS/kết quả phần mềm không được duyệt ngầm. |
| 01/11/2026 | Sửa cách diễn đạt E15 theo P02; nối dòng lịch sử UI-P03 vào đúng bảng; làm rõ Cần Chánh way/285990343 là tham chiếu ngoài snapshot, không thay hình học. Chuyển bốn tài liệu nghiệp vụ vào docs/02-nghiep-vu, UI/lịch sử/design-assets vào docs/04-giao-dien; tạo docs/README và cập nhật README gốc/liên kết/bàn giao. | Công việc tổ chức và sửa hồ sơ theo yêu cầu người dùng; giữ mã/nội dung/trạng thái đã chốt, lịch sử và điểm mở. Không tạo quyết định nghiệp vụ/kỹ thuật mới, không tải nguồn map mới hoặc thao tác Git/code/database/kiểm thử/triển khai. |
| 01/11/2026 | Ghi UI-02: xác nhận UI-P01/UI-P02/UI-P04 và chủ đích thay P01 phần dừng audio bắt buộc bằng nghe hết đúng bài đang phát lúc hết hạn; giữ dấu vết ở 7.4. Đồng bộ phiên/quyền audio/30 ngày, box và phụ thuộc TDD/file, không đổi A02.3. | Căn cứ trực tiếp tin nhắn người dùng xác nhận các phương án UI; không duyệt toàn bộ UI/Admin/nền/vùng/nội dung/TTS/kỹ thuật hoặc bằng chứng phần mềm. |
| 02/11/2026 | Ghi UI-03/UI-AP01–UI-AP03: chốt đúng hướng UI Admin lượt 1, nhãn tên bắt buộc/tối đa 100 và thứ tự vùng; giữ bảng đề xuất cũ tại 7.5. Cập nhật UI 0.5 với lượt 2 dự thảo và mục lục/bàn giao. | Xác nhận trực tiếp của người dùng; không duyệt toàn bộ đồ họa/câu chữ/UI Admin lượt 2, nội dung/ranh giới/TTS/kỹ thuật hoặc bằng chứng phần mềm. Không sửa nghiệp vụ khách. |
| 02/11/2026 | Ghi UI-04 xác nhận đúng hướng lượt 2; thay hai nút/áp dụng nửa cặp bằng một thao tác cả cặp, gate cặp vi; bổ sung hồ sơ cặp/giữ tạm và hết phiên Admin dừng nghe thử. Giữ dấu vết 7.6, đồng bộ PRD/Business Flow/UI/báo cáo và thiết kế lượt 3 dự thảo. | Thay đổi có chủ đích của người dùng; không đổi D03/P01/UI-02 runtime, bộ 20+20 hoặc chính sách dữ liệu/file. Không duyệt toàn bộ UI/nội dung/TTS/kỹ thuật hoặc kết quả phần mềm. |
| 02/11/2026 | Ghi UI-05/UI-AP08–UI-AP10: box UI-A21 giữ mã/giữ lọc, ngày trọn ngày/hai bảng-tổng, QR chung PNG/in đơn giản. Giữ đề xuất cũ ở 7.7, đồng bộ PRD/Business Flow/UI/báo cáo/mục lục và tổng hợp công việc trước/trong TDD. | Xác nhận đúng hướng, không duyệt toàn bộ đồ họa/ranh giới/nội dung/TTS/kỹ thuật hoặc bằng chứng phần mềm; R06 vẫn mở một phần, chưa tạo TDD/roadmap. |
| 02/11/2026 | Bổ sung G02/3.4 và căn cứ 8.1.11: ghi phân công nhóm đã xác nhận, phân công nhóm và tiến độ có bằng chứng. Roadmap 0.2 biên tập gọn tại chỗ; cập nhật mục lục/README. | Xác nhận phân công trực tiếp của người dùng; nội dung issue vẫn dự thảo, chưa chốt lịch/username hoặc thay đổi GitHub, nghiệp vụ hay kỹ thuật. |
| 02/11/2026 | Ghi TECH-01/3.5 và căn cứ 8.1.12: công nghệ/kiến trúc đã chọn, nền tảng người học và phần còn mở; đồng bộ roadmap 0.3, PRD 0.8.1/UI 0.7.1, nguồn và mục lục đúng phần. Đồng bộ duyệt/xuất bản backlog trước đó, không sửa nhật ký cũ. | Xác nhận trực tiếp của người dùng; chưa kiểm chứng tương thích/triển khai/kiểm thử, chưa khảo sát TTS hoặc chốt chính sách. Không thao tác Git/GitHub, không đóng T01/T07/R06. |
| 03/11/2026 | Ghi TECH-02/TTS-01 và dấu vết 7.8: đầu vào môn học/thiết bị/hướng dẫn/mốc mục tiêu và Gemini 3.8 Flash TTS/Charon; khảo sát chỉ đọc/nguồn chính thức, roadmap 0.4 có lịch có điều kiện. | Xác nhận trực tiếp; không kế thừa tunnel/log/backup/lịch demo. IN-P/DATA-P/lịch chưa duyệt; không API, TDD, thực thi hoặc Git/GitHub. |
| 03/11/2026 | Ghi TECH-03/DATA-01/G03 và dấu vết 7.9; IN-P01/02/03 được chọn, IN-P04 thử ngrok, IN-P05 đúng phần chốt. Đồng bộ đầu vào 0.2/roadmap 0.5 và bắt đầu TDD 0.1 dự thảo. | Xác nhận trực tiếp; chưa tương thích/API/ngrok thực/test/CD. Giữ đề xuất còn mở và lịch sử, đọc checklist GitHub không thay đổi. |
| 03/11/2026 | Ghi TECH-04/DATA-02 và dấu vết 7.10; đóng gói đã chốt, tab độc lập/full MP3, backup cục bộ không yêu cầu ổ ngoài. Bổ sung TDD 0.2 ERD/quan hệ/cơ chế dự thảo và cập nhật nguồn liên quan. | CD-L01/ERD/chi tiết mới chưa duyệt hoặc chạy; giữ nghiệp vụ/mã/lịch sử, chỉ đọc checklist, không thực thi phần mềm/Git/dịch vụ/demo. |
| 03/11/2026 | Ghi TECH-05/DATA-03 và dấu vết 7.11: CD-L01 đã chọn/chưa kiểm, backup phạm vi/nhịp đã chốt và ví dụ còn hạn; TDD 0.3 dẫn phụ lục API/giao dịch dự thảo. | Không duyệt toàn TDD/API/ERD hoặc thực thi SSH/Docker/CD/restore; giữ nghiệp vụ/mã/lịch sử, chỉ đọc checklist. |
| 03/11/2026 | Giữ TECH-05/DATA-03, dẫn thiết kế TDD 0.4/model/API/operations và ghi rõ LIFE-P01–P03/REC-P01 ở phần mở; chưa xác nhận các phương án mới. | Không quyết định nghiệp vụ mới hoặc bằng chứng phần mềm; lịch sử xác nhận/nguồn giữ nguyên. |
| 04/11/2026 | DATA-04 ghi LIFE-P02/P03/REC-P01/hướng BKP-P02 đã xác nhận; LIFE-P01 chỉ hướng đơn giản, ticket/gate mới dự thảo; giữ dấu vết 7.12/nguồn 8.1.17 và sửa dấu # thừa trước dòng nhật ký 03/11. | Không duyệt toàn TDD/TTL context hoặc coi backup/SSH/phần mềm đã kiểm; không Git/GitHub/thực thi. |
| 04/11/2026 | DATA-05/LIFE-P01 chốt vé ký 24 giờ/ledger, bỏ generation/gate purge; giữ lịch sử 7.13/nguồn 8.1.18, đồng bộ TDD hiện hành và chuẩn bị thực hành 01. | Không toàn TDD hoặc phần mềm được duyệt/kiểm, không thực thi code/config/DB/test/Git/GitHub/triển khai. |

<a id="832-nhật-ký-business-flow-chuyển-nguyên-bản"></a>

### 8.3.2. Nhật ký Business Flow chuyển nguyên bản

| Ngày | Thay đổi | Trạng thái duyệt |
| --- | --- | --- |
| 29/10/2026 | Tạo mới Business Flow khách tham quan: căn cứ, mục tiêu, luồng chính, ngoại lệ và các quyết định còn mở. Không thay thế nội dung có sẵn. | Dự thảo, chờ người dùng xem xét. |
| 29/10/2026 | Cập nhật theo xác nhận D01–D03: bản đồ demo, tên bắt buộc và độc lập với nhận diện phiên, xử lý thiếu bản dịch/audio; đưa tọa độ ra khỏi luồng bản đầu; liên kết decisions.md. | Chỉ nội dung D01–D03 đã chốt; các phần khác giữ dự thảo/còn mở. |
| 29/10/2026 | Bổ sung bảng phương án chờ duyệt tại mục 5.1, liên kết chi tiết tại decisions.md mục 6 và cập nhật việc tiếp theo. Chưa đổi luồng chính thành yêu cầu đã duyệt. | Đề xuất mới là dự thảo; D01–D03 đã chốt giữ nguyên. |
| 29/10/2026 | Đọc và đối chiếu hai file; đồng bộ D01–D09 cùng D01-BS/D02-BS/D03-BS theo xác nhận mới; thay phương án phiên dùng chung/30 phút, danh sách cũ và nút nghe lại; cập nhật luồng/ngoại lệ/bảng quyết định. Bổ sung P01–P05 riêng; ghi năm bản nháp vi chờ cung cấp. | Chỉ nội dung người dùng xác nhận là đã chốt; P01–P05 và duyệt tổng thể còn mở. |
| 30/10/2026 | Bổ sung G01: phân tích phiên bản đi chấm, giữ tiêu chuẩn của bản đi chấm; bổ sung D04-BS: cảnh báo nhẹ khi còn trên 0 đến 5 phút, tải lại/quay lại vẫn nhận biết, hết hạn dùng thông báo hết hạn. Cập nhật luồng/ngoại lệ/bảng quyết định; rà lại P01–P05, đổi khuyến nghị P03 sang khôi phục mốc nghe trong tab. | Chỉ định hướng và cảnh báo được xác nhận; các hành vi P còn mở và toàn bộ Business Flow chưa được duyệt tổng thể. |
| 30/10/2026 | Đối chiếu hai file; cập nhật D01/D01-BS/D07 sang năm vùng tương tác, chọn vùng đồng thời yêu cầu phát audio và chọn vùng khác phát bài mới từ đầu; giữ đổi ngôn ngữ không tự phát. Chốt P05 một vùng/bài tổng quan Tử Cấm Thành; ghi rõ quy tắc cũ bị thay thế. Hoàn thiện P01–P04, bổ sung P06–P08 chưa duyệt. | Chỉ vùng/phát và P05 được xác nhận trong lượt này; P01–P04, P06–P08 và duyệt tổng thể còn mở. |
| 30/10/2026 | Đọc/đối chiếu bản hiện có; ghi nhận P01–P04/P06–P08 đã chốt đúng xác nhận mới, giữ P05. Đồng bộ luồng/ngoại lệ/bảng quyết định; làm rõ bấm lại sau khi hết bài, kéo/phóng không kích hoạt vùng, mốc nghe không cần đúng từng giây, thông báo P02 và khác biệt tải lại/thử lại lỗi audio. Giữ dấu vết thay thế và các phần chờ thiết kế. | Các quyết định P đã chốt; Business Flow vẫn chờ duyệt tổng thể; năm bài vi vẫn là bản nháp chờ cung cấp/rà soát. |
| 30/10/2026 | Đọc/đối chiếu bản lưu với tổng hợp 13 bước và cách đếm; ghi BF-K01 “Luồng nghiệp vụ khách đã được duyệt tổng thể”, giới hạn rõ phạm vi và chuẩn hóa P02. Bổ sung mục 8 đề xuất Admin, vẫn dự thảo; giữ các quyết định khách, lịch sử và trạng thái năm bài vi. | Xác nhận duyệt tổng thể riêng luồng khách; chưa duyệt Admin/bản đồ/giao diện/nội dung/TDD hoặc kiểm thử phần mềm. |
| 30/10/2026 | Đối chiếu phản hồi Admin mới; ghi phần xác nhận A01/A03/A05/A06/A07, A02 CRUD/cơ chế xóa và hướng A04 tạo audio. Bỏ giới hạn cứng năm điểm/chỉ tải file, giữ bộ nghiệm thu 5 điểm/20 văn bản/20 audio. Chỉ sửa phần khách chịu ảnh hưởng số lượng, giữ quy tắc khác; phân tích chính sách xóa/file/vùng mới và tác vụ tạo trong bảng đề xuất. | Admin vẫn dự thảo có quyết định đã chốt, chưa duyệt tổng thể; phần còn mở không tự chuyển sang đã duyệt. |
| 30/10/2026 | Đọc/đối chiếu bản hiện tại; ghi A02.1–A02.5/A04.1–A04.4 đã chốt. A02.1 cụ thể hóa cơ chế xóa cũ; thay A02.4 cấu hình ngoài bằng vẽ/xem trước/lưu/sửa vùng trong Admin. Đồng bộ giữ lịch sử/dọn file/vận hành tạo, điều kiện hiện và bộ nghiệm thu không phải trần giảng viên. Trình toàn luồng Admin theo nhóm; giữ trạng thái chưa duyệt tổng thể, nêu riêng hiệu lực sửa vùng đang hiển thị. | Xác nhận từng nội dung Admin, không duyệt sẵn toàn luồng, giao diện/ranh giới hoặc nhà cung cấp/giọng/chi phí/công nghệ TTS. |
| 30/10/2026 | Đọc/đối chiếu bản lưu với 12 nhóm Admin, không thấy khác biệt thực chất. Chốt A02.6: giữ vùng hiện hành, lưu khác áp dụng, xem trước/xem xét rồi áp dụng, hiệu lực lần tải bản đồ tiếp theo. Ghi BF-A01/BF-ALL01 duyệt tổng thể Admin và nghiệp vụ hai vai trò; giữ BF-K01, mọi nhật ký cũ và giới hạn phê duyệt. Tạo PRD/Use Case/tiêu chí nghiệm thu có mã truy vết. | Business Flow hai vai trò đã duyệt trong phạm vi nghiệp vụ; PRD/Use Case/tiêu chí nghiệm thu là dự thảo, không là bằng chứng phần mềm hoặc duyệt nội dung/giao diện/TTS. |

<a id="84-đối-chiếu-vị-trí-cũ--vị-trí-hiện-hành"></a>

### 8.4. Đối chiếu vị trí cũ → vị trí hiện hành

| File / vị trí trước lần sắp xếp | Vị trí hiện hành |
| --- | --- |
| Business Flow mục 1 (căn cứ theo lượt) | decisions.md mục 8.1.2; phần mục tiêu/trạng thái ở Business Flow mục 1 |
| Business Flow mục 1.1 / 1.2 / 1.3–1.4 | decisions.md mục 8.1.3 / Business Flow mục 1 / decisions.md mục 3 |
| Business Flow mục 2–3, 3.1–3.3, mục 4 | Business Flow mục 3.1 (điểm vào), 2.2 (đếm), 3.2 (audio), 3.3 (cảnh báo), 3.4 (ngoại lệ) |
| Business Flow mục 5 / 5.2 (bảng quyết định/P) | Chỉ mục decisions.md mục 2; vị trí chính decisions.md mục 3–5, P tại 4.10 |
| Business Flow mục 5.1 / 5.3 | decisions.md mục 7.2 / Business Flow mục 6 và decisions.md mục 6 |
| Business Flow mục 6 / 7 | Business Flow mục 7 / decisions.md mục 8.3.2; nhật ký ngắn tại Business Flow mục 8 |
| Business Flow mục 8.1 / 8.2–8.5 / 8.6–8.8 | decisions.md mục 5 / Business Flow mục 4.1–4.4 / Business Flow mục 1, 5–6 và decisions.md mục 3, 5 |
| decisions.md mục 1 / 2 | Mục 8.1.1 (căn cứ lịch sử), mục 1 (quy ước) / mục 2 (chỉ mục) và 3–5 (quyết định chính) |
| decisions.md mục 3 (G01), 3.1–3.9, 4, 4.1 | Mục 3.1 / 4.1–4.9 / 4.10 / 6 |
| decisions.md mục 5 / 6 / 7 | Mục 7.1 / 8.2 / 8.3.1 |
| decisions.md mục 8.1–8.4, 8.7 | Mục 5; A02.6 tại 5.2.6. Quy trình vận hành tương ứng tại Business Flow mục 4 |
| decisions.md mục 8.5–8.6 / 8.8 | Mục 3, 4.6, 6 / mục 3.2 |
| PRD mục 1.2 (dẫn vị trí hai tài liệu nguồn) | Cập nhật sang Business Flow mục 1–6, decisions.md mục 3–5 và lịch sử mục 7–8; nội dung FR/UC/NFR/AC không đổi |



06/11/2026 — biên tập thông tin tổ chức công việc theo phân công nhóm G02; giữ các mốc, bằng chứng kỹ thuật và phạm vi nghiệp vụ của bản lưu.
