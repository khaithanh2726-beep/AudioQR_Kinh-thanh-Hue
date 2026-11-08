# Báo cáo rà soát PRD

**Mục đích:** tra kết luận đối chiếu, phát hiện và phụ thuộc còn mở. Soạn 30/10/2026; biên tập 08/11/2026.
**Trạng thái:** R01–R05/R07/S01/S02 đã xử lý tài liệu; Q1–Q3 đã xác nhận; **R06 còn mở một phần**. PRD-01 và các bổ sung riêng giữ đúng phạm vi, không là bằng chứng phần mềm.
**Nguồn:** [PRD](PRD.md), [Business Flow](business-flow.md), [decisions hiện hành](decisions.md). Ưu tiên mã/anchor thay số dòng cũ. [Diễn biến theo từng lượt](../99-luu-tru/prd-review-history.md) đã tách khỏi kết luận hiện tại.

## 1. Phạm vi và kết luận hiện tại

Giữ 23 yêu cầu chức năng, 14 Use Case, 6 NFR, 50 tiêu chí nghiệm thu; hiện có 18 BR, 9 DR và O-01–O-06. Business Flow hai vai trò đã duyệt theo BF-K01/BF-A01/BF-ALL01; UI-04 ngày 02/11/2026 chủ đích thay áp dụng riêng/gate cũ, đồng bộ theo xác nhận người dùng, không sửa nguồn để hợp thức hóa PRD/code.

Ngày 31/10/2026 sửa các câu có đủ căn cứ ở BR-03/UC-K02/AC-K05, phần dẫn Use Case, AC-D01, O-06 và các câu điều kiện dọn file liên quan. Hoàn thiện truy vết hai chiều và thêm danh sách tiêu chí cho từng UC. Ở thời điểm rà soát ban đầu, các phương án tại PRD 9.2 còn là đề xuất. Q1–Q3 sau đó đã được xác nhận; chỉ các phần còn mở khác vẫn là đề xuất/chờ thiết kế, không thành ngưỡng hoặc nghĩa vụ đã duyệt.

R01–R05/R07/S01/S02 đã được xử lý ở mức tài liệu; Q1–Q3 được cụ thể hóa trong PRD và R06 còn mở một phần. Phạm vi duyệt PRD-01 được dẫn tại [mục 7.4](#review-approval-scope), diễn biến xử lý giữ trong [hồ sơ rà soát](../99-luu-tru/prd-review-history.md). Kết quả kiểm thử phần mềm có nguồn riêng, không suy từ việc đóng phát hiện tài liệu.

Phạm vi các lượt rà soát ban đầu không khảo sát code/database/dịch vụ hoặc nội dung. Lượt 03/11 bổ sung **chỉ đọc phần code/cấu hình demo được giao và nguồn chính thức**, không chạy code, đọc database/audio/key hoặc kiểm chứng phần mềm; hồ sơ và giới hạn ở 7.11. Không thực hiện code/cấu hình/database/kiểm thử/Git/triển khai.

## 2. Phân loại sử dụng trong báo cáo

- **Đã xử lý tài liệu:** câu chữ/dẫn chiếu/tiêu chí đã sửa và đọc lại theo nguồn đã chốt; không phải kết quả phần mềm.
- **Còn mở / cần xác nhận:** chưa đủ chi tiết yêu cầu hoặc phép đánh giá; chỉ đề xuất phương án và mốc cần chốt, không tự chọn.
- **Chờ thiết kế kỹ thuật:** hành vi/kết quả đã rõ; không buộc người dùng chọn cách thực hiện để duyệt lại hành vi.
- Giữ mô tả phát hiện ban đầu ở mục 3 để truy vết; các câu mô tả lỗi tại cột đó không còn là mô tả PRD hiện hành nếu trạng thái đã xử lý.
- Giữ lịch sử tổ chức tài liệu 30/10/2026 ở mục 6; trạng thái hiện hành đọc tại mục 1/3/7.

## 3. Trạng thái phát hiện và cách xử lý

| Mã / ưu tiên / loại | Vị trí hiện tại | Nguồn đối chiếu | Mô tả phát hiện ban đầu — giữ để truy vết | Trạng thái / cách xử lý |
| --- | --- | --- | --- | --- |
| R01 — Ưu tiên cao; câu khái quát thiếu giới hạn | [BR-03](PRD.md); [UC-K02](PRD.md#uc-k02); AC-K05; đối chiếu BR-04/AC-K13. | [D07](decisions.md#d07), [P06](decisions.md#p06), [Business Flow 3.2](business-flow.md#bf-guest-audio) | BR-03 nói chung chọn vùng yêu cầu phát “từ đầu”, trong khi bấm lại vùng đang phát/tạm dừng phải tiếp tục. BR-04, ngoại lệ UC-K02 và AC-K13 đã nêu đúng. Đây là nguy cơ đọc câu chính thành áp dụng cho mọi lần chạm, không kết luận tác giả đã đổi P06. | **ĐÃ XỬ LÝ TÀI LIỆU 31/10/2026.** BR-03, luồng chính UC-K02 và AC-K05 giới hạn phát từ đầu vào chọn vùng lần đầu của lần mở/chọn địa điểm khác. Nhánh bấm lại nêu P06/BR-04/AC-K13, không mặc định quay về đầu hoặc cộng lượt như lần mở mới; trạng thái đã hết bài vẫn phát lại từ đầu theo P06. Giữ các trạng thái tiếp tục/tạm dừng/hết bài/đang tải và thiếu-lỗi đã chốt. |
| R02 — Ưu tiên cao; câu ràng buộc quá rộng | [PRD.md](PRD.md), phần dẫn Use Case; [UC-K01](PRD.md#uc-k01), [UC-K05](PRD.md#uc-k05); AC-K18/K19. | [D04](decisions.md#d04), [P01](decisions.md#p01), [P02](decisions.md#p02), [Business Flow K10-B](business-flow.md#bf-guest-main) | “Mọi tác vụ khách bị ràng buộc hiệu lực phiên” có thể bị hiểu là hết hạn không được đọc văn bản đã tải hoặc không thể vào bước bắt đầu phiên mới. UC-K05/AC-K18 lại giữ văn bản để đọc, UC-K01 tạo phiên mới từ màn hình bắt đầu. | **ĐÃ XỬ LÝ TÀI LIỆU 31/10/2026.** Chỉ nghe/tua, chọn vùng, đổi ngôn ngữ và ghi lượt yêu cầu phiên còn hạn; nêu rõ quyền đọc văn bản đã tải sau hết hạn và chủ động mở/xác nhận phiên mới. Không tự tạo/gia hạn hoặc bỏ yêu cầu phiên Admin hợp lệ. |
| R03 — Ưu tiên vừa; truy vết chưa đối ứng | [Bảng FR](PRD.md) mục 4, bắt đầu; danh sách AC ở từng UC và cột FR/UC của AC tại mục 8, đã đối chiếu lại sau Q1–Q3. | [Các quyết định hiện hành](decisions.md#dec-2); chính cột FR của từng AC trong PRD | Sáu quan hệ AC→FR chưa có chiều FR→AC: FR-K03→AC-K13; FR-K02→AC-K15; FR-A05→AC-A13; FR-A06→AC-A15; FR-A07→AC-A26; FR-A11→AC-D02. Nội dung không bị mất, nhưng đọc từ bảng FR có thể bỏ qua tiêu chí. | **ĐÃ XỬ LÝ TÀI LIỆU 31/10/2026.** Bổ sung sáu liên kết đã báo, kiểm tra thêm cả hai chiều FR–UC, FR–AC và UC–AC. Các điều chỉnh cụ thể tại mục 3.1; chỉ sửa dẫn chiếu có ý nghĩa trực tiếp, không tạo chức năng để làm đầy bảng. Các mã/23 FR/14 UC/50 AC giữ nguyên. |
| R04 — Ưu tiên cao trước lập kế hoạch kiểm thử; tiêu chí kết quả chưa rõ | [AC-D01](PRD.md); FR-D01, DR-04, [UC-D01](PRD.md#uc-d01); phần thời gian còn mở tại mục 9.2. | [D08](decisions.md#d08), [A02.2](decisions.md#a02-2), [Business Flow quy tắc chung](business-flow.md#bf-2) | AC-D01 nói dữ liệu đến hạn “thuộc quy trình tự động xóa”, còn câu thống kê chỉ nói không tính phần “đã xóa”. Nếu chỉ đưa vào hàng chờ mà chưa xóa, câu này chưa đủ bác bỏ một kết quả không đạt. FR/UC đã yêu cầu tự xóa thực tế; chưa thấy quyết định cho phép giữ vô hạn. | **ĐÃ XỬ LÝ TÀI LIỆU 31/10/2026.** AC-D01 yêu cầu bản ghi phiên và toàn bộ lượt liên quan được xóa thực tế khỏi kho dữ liệu nghiệp vụ bởi cơ chế tự động; dữ liệu lưu/danh sách/thống kê không còn các bản ghi đó, phiên chưa đến hạn không bị xóa vì đóng trang/xóa điểm. Chỉ xếp hàng, ẩn hoặc gắn trạng thái khi vẫn giữ dữ liệu phải xóa chưa đạt. Không tự đặt lịch/độ trễ; phần thời gian vận hành còn mở trong R06/O-06. |
| R05 — Ưu tiên thấp; câu rút gọn dễ hiểu nhầm | [O-06](PRD.md); BR-16, [UC-A06](PRD.md#uc-a06), AC-A20. | [A02.3](decisions.md#a02-3), [Business Flow 4.2](business-flow.md#bf-admin-delete) | Cụm “chỉ không dùng/dùng chung” thiếu rõ điều kiện “không”; có thể đọc nhầm rằng file dùng chung cũng được dọn. Các yêu cầu/AC chính đều nêu đúng, không có căn cứ coi đây là chính sách xóa mới. | **ĐÃ XỬ LÝ TÀI LIỆU 31/10/2026.** Viết rõ chỉ file không còn được tham chiếu và không dùng chung; đồng bộ các câu điều kiện tương ứng trong BR-16/UC-A06/AC-A20. Giữ dọn sau database thành công, bảo vệ 8 tiếng từ ngừng áp dụng cuối cùng, trạng thái lỗi/thử lại và tách thao tác file khỏi giao dịch database. |
| R06 — Cần thiết kế/xem xét riêng; chưa phải lỗi nghiệp vụ | [PRD mục 9.2](PRD.md#prd-open-classification), [xác nhận Q1–Q3](PRD.md#prd-proposals), BR-11/BR-14/BR-18, DR-06/DR-09; AC-A02 mục 8.2.1 và AC-A03/A04 mục 8.2.2. | [Điểm còn mở](decisions.md#dec-6), [Business Flow mục 6](business-flow.md#bf-6), A01/A02.4/A02.5, P03, D08 | Các cụm “dễ phân biệt”, thông tin/vùng “hợp lệ/đã xem xét”, “bảo vệ nội dung chưa lưu”, hoạt động Admin, mốc nghe gần nhất và độ trễ tự xóa cần thiết kế/phép quan sát cụ thể. Chưa thể kết luận mọi AC đã sẵn sàng để chấm đạt/trượt chỉ từ câu chữ hiện tại. Các phụ thuộc này đã được PRD ghi mở, không phải ngưỡng bắt buộc bị bỏ quên. | **Q1–Q3 ĐÃ XÁC NHẬN/CỤ THỂ HÓA; R06 CÒN MỞ MỘT PHẦN.** Người dùng đã chốt đủ thông tin địa điểm, giữ tạm và hoạt động Admin ngày 31/10/2026; không còn ba câu hỏi nghiệp vụ đó. Nguồn hiện hành tại decisions 5.8, PRD 0.4 có yêu cầu/tiêu chí tương ứng, nguyên đề xuất 0.3 chỉ còn lịch sử. Chưa đóng R06: còn tích hợp/quota/ngân sách trả phí/chất lượng TTS (model/giọng đã chốt TTS-01), giao diện/ranh giới, môi trường/hiệu năng, cơ chế lưu tạm/ghi nhận, lịch/cơ chế xóa-dọn và chi tiết backup-log còn thiếu cùng các chi tiết khách; TECH-03/DATA-01 đã chốt mốc phiên bản/nền tảng/định dạng/điều kiện-hạn file/log/backup/restore đúng phần, ngrok chỉ chọn thử. TECH-04/DATA-02 nay chốt hướng full MP3/đóng gói và backup cục bộ, không bắt ngoài laptop. TECH-05/DATA-03 đã chọn CD-L01/phạm vi-nhịp backup; còn cơ chế/kiểm thực, xem [đối chiếu lịch sử 7.14](../99-luu-tru/prd-review-history.md#review-api-01), không coi các hướng/hạn đã chốt là câu hỏi mở. Không tự chọn công nghệ/ngưỡng hoặc coi phần mềm đã đáp ứng. |
| R07 — Câu chữ BR-11 nhập nhằng phạm vi ngôn ngữ | [BR-11](PRD.md),; FR-A02, phạm vi D06/Q1 | [D06](decisions.md#d06), [A02.5](decisions.md#a02-5), [Q1](decisions.md#q1); PRD mục 2.2/2.3 | Cuối BR-11 bản 0.4 ghi “Năm điểm là bộ chuẩn, không là giới hạn số điểm/ngôn ngữ mới”, dễ hiểu thành cho mở rộng/quản lý ngôn ngữ mới. Nguồn chỉ mở rộng số địa điểm, giữ vi/en/fr/ru. | **ĐÃ SỬA CÂU CHỮ, PRD 0.4.1.** Ghi rõ năm điểm không là giới hạn số địa điểm; phạm vi vẫn vi/en/fr/ru, chưa bổ sung chức năng quản lý thêm ngôn ngữ. Không đổi quyết định hoặc thêm yêu cầu; các FR/UC/AC/NFR và mã giữ nguyên. |

### 3.1. Chi tiết R03 — truy vết có ý nghĩa hai chiều

| Điều chỉnh | Căn cứ / giới hạn |
| --- | --- |
| Bổ sung FR-K03→AC-K13; FR-K02→AC-K15; FR-A05→AC-A13; FR-A06→AC-A15; FR-A07→AC-A26; FR-A11→AC-D02 | Sáu liên kết thiếu đã báo; các AC vốn đã ghi những FR này. Không thêm nội dung yêu cầu hoặc tiêu chí. |
| Bổ sung FR-K01→UC-A07 | UC-A07 vốn đã dẫn FR-K01 và mô tả truy cập qua QR/URL. |
| Bổ sung FR-A10 vào AC-K01; FR-K02 vào AC-K19 | AC-K01 kiểm tra QR/URL chung tới màn hình bắt đầu; AC-K19 kiểm tra tạo phiên mới chỉ khi bắt đầu thành công. Bảng FR đã dẫn các AC tương ứng, nay hai chiều nhất quán. |
| Bỏ FR-K02→AC-K17; bỏ FR-A02→AC-A17/AC-A18 | AC-K17 kiểm tra cảnh báo do FR-K09 chịu trách nhiệm; AC-A17 kiểm tra giữ lịch sử do FR-A08/A11, AC-A18 kiểm tra kết quả muộn/dùng chung do FR-A06/A08. FR-K02 vẫn có các AC phiên/hết hạn; FR-A02 vẫn có AC-A16 kiểm tra xóa. Không xóa AC hoặc bỏ yêu cầu nghiệp vụ. |
| Bổ sung danh sách AC ở từng UC | Đối ứng với cột UC trong 50 AC; không suy ra mọi FR và AC cùng xuất hiện trong một UC đều phải nối chéo với nhau. |
| Đọc quy ước mã và kiểm tra đối ứng | Ký hiệu rút gọn FR-K03/K05, AC-K17–K20 hoặc **FR:** K01 được đọc đúng ngữ cảnh; không báo là mã mới/không tồn tại. Không có tham chiếu tới FR/UC/AC chưa định nghĩa sau sửa. |

### 3.2. S01 — Nội dung hiện hành A02.1 — ĐÃ XỬ LÝ TÀI LIỆU

**Cập nhật 31/10/2026:** Đã khôi phục vị trí chính [A02.1](decisions.md#a02-1) từ [Business Flow mục 4.2](business-flow.md#bf-admin-delete), A02 và căn cứ xác nhận còn lưu. Phần hiện hành gồm dữ liệu riêng/dùng chung, tham chiếu/giữ lịch sử, vô hiệu liên kết tác vụ audio/kết quả muộn, hồ sơ duyệt, khóa ngoại/quy tắc chặn và một giao dịch database thành công toàn bộ/hoàn tác; dọn file vẫn là cơ chế riêng.

Câu lịch sử không còn nằm dưới nhãn “Đã chốt”; câu đó được giữ trong [phương án cũ của decisions](../99-luu-tru/decisions-history.md#dec-7). Đây là sửa cách trình bày/khôi phục nội dung đã xác nhận, không thay hoặc yêu cầu duyệt lại cơ chế xóa. Chưa thực hiện xóa dữ liệu hoặc chứng minh phần mềm đáp ứng.

**Dấu vết trước khi sửa:**

> Ở [decisions.md mục A02.1](decisions.md#a02-1), đoạn dưới nhãn “Đã chốt” hiện là câu kể lịch sử “Từng được trình như một phương án cần xác nhận lại cơ chế xóa”. Đó là lỗi chọn đoạn trong lần sắp xếp 30/10/2026; không dùng câu này thay quy trình xóa hiện hành.
> 
> Quy trình đầy đủ vẫn còn tại [Business Flow mục 4.2](business-flow.md#bf-admin-delete), cùng cơ chế tại [A02](decisions.md#a02). Lượt 31/10 chỉ **bổ sung dẫn chiếu** tới các vị trí này theo giới hạn được giao, giữ nguyên phần nội dung nguồn còn lại. Chuẩn hóa/chuyển câu lịch sử về đúng chỗ vẫn là việc tài liệu còn mở, không cần quyết định lại cơ chế xóa. PRD đã đối chiếu quy trình đầy đủ ở Business Flow, không rút yêu cầu xóa xuống thành câu lịch sử hoặc tự đổi nguồn.

### 3.3. S02 — Câu giới thiệu báo cáo trong Business Flow — ĐÃ XỬ LÝ TÀI LIỆU

Câu cũ trong [Business Flow mục 7](business-flow.md#bf-7) ghi “các đề xuất sửa thực chất chưa áp dụng”. Đã thay bằng: R01–R05 đã xử lý ở mức tài liệu, R06 còn các phụ thuộc cần xác nhận/thiết kế; không đồng nghĩa phần mềm đã đáp ứng. Dòng giới thiệu cũng dẫn đúng S01/S02 đã sửa và Q1–Q3 nay đã được xác nhận; R06 vẫn giữ các phụ thuộc khác. Luồng nghiệp vụ và trạng thái duyệt Business Flow giữ nguyên.

**Dấu vết cũ:** “Báo cáo đối chiếu trực tiếp PRD; các đề xuất sửa thực chất chưa áp dụng, không là quyết định mới.” Câu này chỉ đúng với lượt báo cáo ban đầu, không phải trạng thái hiện tại.

## 4. Đối chiếu bao phủ toàn bộ yêu cầu chức năng

| Mã FR | Nghiệp vụ đã đối chiếu / UC | Kết quả tài liệu |
| --- | --- | --- |
| FR-K01 | QR, URL, tên 1–50, bốn ngôn ngữ, bắt đầu hợp lệ — UC-K01 | Phù hợp D01-BS/D02/BS/D03-BS/A05; đã đối ứng UC-A07 trong bảng FR. Kiểm tra ngôn ngữ có ở bước 4 UC-K01; không kết luận bỏ sót chỉ vì chưa tách AC riêng cho đầu vào ngôn ngữ sai. |
| FR-K02 | Phiên theo tab, 8 tiếng cố định, tải lại — UC-K01/K04/K05 | Phù hợp D04/P02/P03; R02/R03 đã xử lý câu chung/truy vết. Không lẫn phiên với người. |
| FR-K03 | Vùng/điện thoại, trong-ngoài/bấm lại/kéo-phóng/chồng lấn — UC-K02 | Bao phủ D01/BS, P05–P08, A02.5; R01/R03 đã xử lý; thiết kế R06 còn mở. |
| FR-K04 | Văn bản được phép, thiếu dịch/audio — UC-K02 | Phù hợp D03/BS/D09; không tự chuyển ngôn ngữ hoặc đưa nháp ra khách. |
| FR-K05 | Audio chọn vùng, Phát/Tạm dừng/tua, lỗi — UC-K02/K03/K04 | Phù hợp D07/D03-BS/P04/P06/P08; R01 đã xử lý câu khái quát; không thiếu thanh tua. |
| FR-K06 | Đổi ngôn ngữ giữ phiên/điểm, không tự phát — UC-K03 | Phù hợp D07, kể cả chọn ngôn ngữ thay thế khi thiếu dịch; không đồng nhất chọn vùng. |
| FR-K07 | Khôi phục tab và đúng bài — UC-K04 | Phù hợp P03/A07; về đầu khi mốc không có/bài đổi, không đòi đúng từng giây; R03 đã xử lý; R06 còn phụ thuộc. |
| FR-K08 | Mạng/dịch vụ, thử lại audio từ đầu — UC-K04 | Phù hợp P04/D03-BS; không ép dừng audio còn phát được, không offline/gửi bù. P01 vẫn ưu tiên khi hết hạn. |
| FR-K09 | Cảnh báo, hết hạn/kết thúc/quay lại — UC-K05 | Phù hợp D04-BS/P01/P02; R02 đã sửa câu dẫn, giữ các nhánh đã mô tả đúng. |
| FR-K10 | Đếm lượt theo lần mở, không đếm lặp — UC-K02/K03/K04/A08 | Phù hợp D04/D08/A06; thiếu dịch chưa tính, lần đầu có văn bản tính theo ngôn ngữ đó; A→B→A là lượt mới. |
| FR-A01 | Admin/30 phút/hết phiên/nghe thử — UC-A01/A04 | UI-04 thêm dừng nghe thử/che nội dung, sau xác thực không tự phát/gửi lại; TTS đã nhận tiếp tục, Q2/UI-AP05 giữ công việc. AC-A13 và truy vết đã đồng bộ. |
| FR-A02 | CRUD/lưu ẩn/gate hiện — UC-A02/A06 | UI-04 thay gate văn bản vi bằng cặp vi đã duyệt/áp dụng cùng thông tin/vùng hợp lệ đã xem xét/áp dụng; không trần năm điểm hoặc chờ đủ bốn ngôn ngữ. |
| FR-A03 | Vẽ/xem trước/lưu/sửa/áp dụng vùng — UC-A02 | Phù hợp A02.4/A02.6; giữ vùng hiện hành đến áp dụng, lần tải bản đồ tiếp theo; ranh giới/thao tác cụ thể chưa duyệt; phạm vi giữ tạm/khôi phục vùng đã chốt Q2. |
| FR-A04 | Văn bản/bản dịch/đối chiếu/nháp — UC-A03 | UI-AP04/UI-AP05 giữ bản khách chỉ đọc và phần tham chiếu trong cùng công việc; không sửa trực tiếp cặp đang phục vụ hoặc tự áp dụng. |
| FR-A05 | Chủ động tạo, nghe thử, trạng thái/thử lại — UC-A04 | Phù hợp A04.1/A04.2; không tự tạo mỗi lần lưu; R03 đã bổ sung liên kết AC-A13. |
| FR-A06 | Đúng bản, qua hết phiên, kết quả muộn — UC-A04/A06 | Phù hợp A04.3/A04.4; giữ bản đang phục vụ, không hồi sinh điểm đã xóa; R03 đã xử lý. |
| FR-A07 | Hồ sơ cặp và một thao tác áp dụng — UC-A05 | UI-04 hiện hành: chỉ cả cặp đủ hồ sơ/khớp, không hai nút/áp dụng nửa cặp. Lỗi xác định giữ cặp cũ, chưa rõ kiểm tra thực tế; runtime audio lỗi vẫn giữ D03. |
| FR-A08 | Xóa điều phối/FK/giao dịch, giữ lịch sử — UC-A06 | Phù hợp A02.1/A02.2; không ON DELETE CASCADE tại quan hệ cần chặn, không xóa lượt hàng loạt/gộp điểm trùng tên. |
| FR-A09 | Dọn file sau database, bảo vệ/chờ-thử lại — UC-A06 | Phù hợp A02.3/A04.4; R05 đã xử lý câu O-06 và điều kiện liên quan; R06 về file chưa từng áp dụng/lịch dọn còn mở. |
| FR-A10 | Một QR/URL chỉ đọc/PNG/bản in — UC-A07 | UI-05 chốt đúng hướng đơn giản, chưa URL thì chưa sẵn sàng/không QR giả; không QR riêng/quét/chuyển hướng hoặc tự báo in/lưu thành công. Cần kiểm tra bản thật. |
| FR-A11 | Phiên/box chi tiết, lọc và hai bảng-tổng — UC-A08 | UI-05 thay trang riêng UI-A21 bằng box, nền khóa/Đóng/cuộn/giữ lọc. Ngày trọn ngày Asia/Saigon, đếm theo đúng mốc/ngày/điểm/ngôn ngữ, không cộng phiên+lượt hoặc thêm chức năng. |
| FR-A12 | Mức đầy đủ theo điểm-ngôn ngữ và 20+20 — UC-A08/A05 | Phù hợp A03/D06/D09; đếm file không thay duyệt đúng bản và không tự tăng bộ chuẩn theo mọi điểm thêm. |
| FR-D01 | Tự xóa phiên/lượt sau 30 ngày — UC-D01 | Phù hợp D08/A02.2/A06 về yêu cầu; R04 đã sửa phép kết luận AC-D01; vận hành R06 còn mở. |

## 5. Bao phủ Use Case, NFR và tiêu chí

### 5.1. Use Case

| Mã | Phần đã kiểm tra | Kết luận |
| --- | --- | --- |
| UC-K01 | Tác nhân, vào QR/URL, kiểm tra tên/ngôn ngữ, tạo phiên, lỗi bắt đầu | Phù hợp; R02 đã giới hạn câu dẫn chung. |
| UC-K02 | Chọn vùng/ứng viên, dừng cũ, văn bản/lượt/audio, thiếu/lỗi/chọn liên tục | Ngoại lệ đầy đủ; R01 đã giới hạn câu chính và dẫn P06. |
| UC-K03 | Phát/Tạm dừng/tua và đổi ngôn ngữ | Phù hợp, không tự phát khi đổi ngôn ngữ. |
| UC-K04 | Phân biệt tải lại với thử lại sau lỗi, khôi phục/mạng/phiên | Phù hợp P03/P04, không yêu cầu offline hoặc gửi bù. |
| UC-K05 | 5 phút cuối, hết 8 tiếng, chủ động kết thúc/quay lại/xác nhận mới | Phù hợp; R02 đã giữ rõ quyền đọc văn bản sau hết hạn. |
| UC-A01 | Tài khoản/30 phút/Q2/UI-AP05 và hết phiên khi nghe thử | UI-04 bổ sung dừng nghe thử/che nội dung, không tự phát/gửi lại; không đổi mốc Admin hoặc ngoại lệ khách. |
| UC-A02 | CRUD, vùng mới/sửa, Lưu khác Áp dụng, bật hiện/ẩn | Phù hợp A02.4–A02.6 và Q1/Q2; thông tin/lưu ẩn/cảnh báo trùng và khôi phục vùng đã cụ thể hóa; ranh giới vẫn chưa được duyệt theo luồng. |
| UC-A03 | Nháp, giữ bản đang dùng, sửa bản duyệt cần rà soát lại | Phù hợp A03/D09/Q2: giữ tạm khác nháp/áp dụng, khôi phục đúng công việc, không tự dịch. |
| UC-A04 | Bốn trạng thái, chống lặp, đúng bản/ngôn ngữ, tác vụ qua hết phiên/xóa điểm | Phù hợp A04.1–A04.4; không bảo đảm nhà cung cấp hủy được yêu cầu. |
| UC-A05 | Hồ sơ cặp, cặp cũ/cặp mới và một nút áp dụng | UI-04 thay mô tả áp dụng văn bản trước audio; đủ D09 mới áp dụng cặp, giữ D03 runtime và trường hợp chỉ đổi audio dùng văn bản không đổi. |
| UC-A06 | Xác nhận xóa, phụ thuộc/giao dịch, giữ lịch sử, dọn file riêng | Phù hợp; không đồng nhất bản ghi với file, không làm mất dữ liệu dùng chung/hồ sơ tài liệu. |
| UC-A07 | QR chung/URL chỉ đọc/tải PNG/bản in gọn | Phù hợp UI-05/A05/D01-BS; giữ chưa URL thật/không QR giả, mở hộp in hoặc yêu cầu tải không là bằng chứng thành công. |
| UC-A08 | Mức đầy đủ, danh sách/box phiên/hai bảng, tải-rỗng-lỗi/lịch sử điểm xóa | UI-05 đồng bộ đúng phạm vi A06/D04/D08/A02.2; box không thêm quản lý phiên, không lọc mới/xuất báo cáo/kho vô hạn. |
| UC-D01 | Đến hạn, xóa tự động thực tế và phần giữ lại | Phù hợp nguồn; R04 đã sửa tiêu chí kết quả xóa; vận hành R06 còn mở. |

### 5.2. NFR

| Mã | Đối chiếu và giới hạn |
| --- | --- |
| NFR-01 | D01-BS/P07/P08/D04-BS; không thêm kích thước vùng/màn hình bắt buộc. Cần môi trường/thiết kế để kết luận “dễ phân biệt” theo R06. |
| NFR-02 | UI-04 bổ sung không lộ nửa cặp khi áp dụng, lỗi xác định giữ cặp cũ/chưa rõ kiểm tra thực tế; vẫn giữ toàn vẹn/lượt/phiên/xóa theo nguồn. |
| NFR-03 | Quyền và dữ liệu Q2/Q3/UI-04: hết phiên dừng nghe thử/che nội dung, không tự phát/gửi lại hoặc ngoại lệ khách; không chọn công nghệ/chính sách bảo mật mới. |
| NFR-04 | P03/P04/A01/A04/A02.3; phục hồi và báo đúng trạng thái có căn cứ, phạm vi bảo vệ chưa lưu đã chốt Q2, chỉ cơ chế còn chờ thiết kế. |
| NFR-05 | D02-BS/D03/D09; Unicode, ngôn ngữ, bằng chứng duyệt/giới hạn thẩm định được giữ; không dùng AI làm chuyên gia. |
| NFR-06 | D08/P04; trực tuyến và phục hồi lỗi, không biến audio đệm còn phát được thành chế độ offline. |

Không tìm thấy ngưỡng hiệu năng/dung lượng/số người đồng thời hoặc công nghệ chưa chốt bị dùng làm điều kiện bắt buộc. Các con số 1–50 ký tự, 8 tiếng, 5 phút, 30 phút và 30 ngày có căn cứ trong quyết định nguồn; 20 văn bản/20 audio thuộc bộ nội dung người dùng chọn.

### 5.3. Bao phủ đủ 50 tiêu chí đã đọc

| Nhóm AC | Số tiêu chí | Phần đã đối chiếu / kết luận |
| --- | --- | --- |
| AC-K01–K04 | 4 | QR/tên/bắt đầu/phiên-tab: phù hợp nguồn. |
| AC-K05–K09 | 5 | Vùng/chuyển/bên ngoài/chọn nhanh/chồng lấn: phù hợp nhánh nguồn; R01 đã sửa diễn đạt; R06 còn phép đánh giá theo thiết kế. |
| AC-K10–K11 | 2 | Thiếu dịch/audio, không tự chuyển/dùng sai bản: phù hợp. |
| AC-K12–K14 | 3 | Điều khiển/tua/bấm lại/đổi ngôn ngữ: phù hợp; AC-K13 là căn cứ đối chiếu R01. |
| AC-K15–K16 | 2 | Khôi phục và lỗi audio/mạng được phân biệt đúng; R06 không được đổi thành đòi chính xác từng giây. |
| AC-K17–K20 | 4 | Cảnh báo/hết hạn/quay lại/kết thúc: phù hợp, kể cả câu cảnh báo/P02 nguyên văn; R02 đã sửa câu dẫn chung. |
| AC-K21–K22 | 2 | Lượt xem, thiếu dịch và không lặp: phù hợp D04 và các P liên quan. |
| AC-A01–A02 | 2 | Đăng nhập/quyền/hết phiên: phù hợp; Q2/Q3 đã chốt và phân rã AC-A02; R06 còn cơ chế kỹ thuật. |
| AC-A03–A06 | 4 | CRUD, điều kiện hiện, vẽ và hiệu lực sửa vùng: Q1/Q2 đã cụ thể hóa trường/tên/mã/trùng/lưu ẩn/giữ tạm, thiết kế cụ thể còn R06. |
| AC-A07–A13 | 7 | Nháp, chủ động tạo, trạng thái/chưa rõ/đúng bản/qua hết phiên/nghe thử: phù hợp; R03 đã làm đủ đối ứng dẫn chiếu. |
| AC-A14–A15 | 2 | Hồ sơ cặp/đúng bản, một thao tác cả cặp/giữ cặp cũ khi lỗi; phân rã phép quan sát ở PRD 8.2.3. D03 runtime không bị bỏ. |
| AC-A16–A18 | 3 | Xóa/giao dịch/FK/lịch sử/kết quả muộn: phù hợp, không hứa chặn mọi quyền database hoặc luôn hủy dịch vụ ngoài. |
| AC-A19–A21 | 3 | Dọn file riêng/sau thành công/8 tiếng/lỗi-thử lại: phù hợp; file chưa từng áp dụng chờ thiết kế, không tự thêm mốc. |
| AC-A22–A24 | 3 | QR chung và thống kê/đọc/lọc/rỗng-lỗi: phù hợp, không suy người/lượt nghe. |
| AC-A25–A26 | 2 | Mức đầy đủ/bộ 20+20/duyệt đúng bản: phù hợp; việc có bản nháp không được tính như duyệt. |
| AC-D01 | 1 | Tự xóa 30 ngày: R04 đã làm rõ kết quả; R06 còn phần vận hành. |
| AC-D02 | 1 | Dữ liệu tối thiểu/quyền/giới hạn log-bản sao lưu: phù hợp; dẫn chiếu R03 đã xử lý. |
| **Tổng** | **50** | Đã đối chiếu nội dung tài liệu, **chưa chạy kiểm thử**. |

## 6. Lịch sử rà soát

[Diễn biến tổ chức tài liệu và đề xuất Q1–Q3 cũ](../99-luu-tru/prd-review-history.md#review-organization) và [bảng tổng hợp đề xuất Q1–Q3 trước xác nhận](../99-luu-tru/prd-review-history.md#review-q-proposal-table) chỉ dùng truy vết. Trạng thái R/S hiện hành ở mục 3; phụ thuộc còn mở ở mục 7.3.

## 7. Phụ thuộc còn mở và mức sẵn sàng hiện tại

**Q1–Q3 đã được người dùng xác nhận toàn bộ ngày 31/10/2026**, gồm chi tiết/giới hạn mục 9.4 PRD 0.3. Đã đối chiếu bản lưu, không thấy khác biệt thực chất; nguồn hiện hành tại [decisions mục 5.8](decisions.md#dec-q). Không còn yêu cầu người dùng quyết định lại ba câu hỏi đó.

### 7.1. Xác nhận đã cụ thể hóa trong PRD 0.4, giữ nguyên ở 0.4.1

| Mã | Trạng thái hiện hành | Vị trí cụ thể hóa |
| --- | --- | --- |
| Q1 | Đã chốt: tên điểm 1–100, chuẩn hóa/Unicode/kiểm tra ký tự, mã ổn định/độc lập, trùng tên cảnh báo không chặn kèm mã; lưu ẩn và bật hiện tách theo A02.5, nội dung quản lý riêng. | BR-11, DR-06, FR-A02, UC-A02, AC-A03/A04 và mục 8.2.2, NFR-05 |
| Q2 | Đã chốt: giữ tạm ba loại công việc/cùng tab-tài khoản/các tình huống, mốc gần nhất và vòng đời/giới hạn; không tự tác vụ/ghi đè/hồi sinh điểm bị xóa. | BR-18/DR-09 mới tách riêng dữ liệu tạm Admin; FR-A01–A04, UC-A01–A03, AC-A02/A05/A06/A07 và mục 8.2.1, NFR-03/04 |
| Q3 | Đã chốt: 30 phút từ hoạt động chủ động được ghi nhận khi còn hiệu lực, danh sách tính/không tính, không ghi bù/hồi sinh, TTS nền độc lập. | BR-14, FR-A01, UC-A01, AC-A02/mục 8.2.1, NFR-03/04; đối chiếu UC-A04/AC-A12 giữ tác vụ đã nhận |

Giữ nguyên 23 FR, 14 UC và 50 mã AC. BR-18/DR-09 bổ sung để tách phạm vi giữ tạm Admin, không trộn với DR-05 khôi phục khách hoặc bản nháp nghiệp vụ DR-06. Các tình huống chi tiết nằm dưới mã AC hiện có; không tự thêm chức năng ngoài xác nhận.

### 7.2. Phần còn mở của R06 và bước duyệt tổng thể

Q1–Q3 không còn là điểm thiếu quyết định. R06 vẫn mở một phần; danh sách tổng hợp hiện hành và giới hạn duyệt nằm **duy nhất ở bảng mục 7.3** dưới đây, diễn giải từ PRD 9.1/9.2. Phạm vi đã được người dùng duyệt và phần loại trừ tại mục 7.4; không yêu cầu duyệt lại các quyết định không đổi.

<a id="review-open-final"></a>

### 7.3. Danh sách phần còn mở và giới hạn duyệt — bảng tổng hợp hiện hành

**Hiện hành DATA-05/DATA-04:** vé Bắt đầu ký 24 giờ, window Admin 24 giờ/job metadata bảy ngày/restore thu hồi quyền/backup tạm ngừng đã chốt đúng phạm vi; cơ chế và các cổng thực còn thiết kế/kiểm. Không đóng R06 hoặc coi mọi TDD/AC đã duyệt, xem [7.16](../99-luu-tru/prd-review-history.md#review-data-04).

Bảng này gom phần còn mở của R06 từ PRD 9.1/9.2, **không tạo yêu cầu hoặc phê duyệt phương án**. “Không chặn phạm vi đã rõ” không có nghĩa phần đang mở được duyệt ngầm. Những điều kiện nghiệp vụ đã chốt như tên địa điểm 1–100, phiên Admin 30 phút, phiên khách 8 tiếng và hạn lưu 30 ngày vẫn giữ nguyên.

| Nội dung chưa chốt / loại | FR/UC/AC/NFR bị ảnh hưởng | Có chặn duyệt phần PRD nào? | Mốc phải giải quyết | Người đề xuất → người xác nhận |
| --- | --- | --- | --- | --- |
| **Hành vi UI-P01/UI-P02/UI-P04 đã chốt đúng UI-02** | FR-K03/K05/K09; UC-K02/K03/K05; AC-K09/K12/K18/K20 | Không còn là câu hỏi cần duyệt lại; thiết kế chi tiết/cách đóng khác chưa nêu và ảnh chưa được duyệt ngầm. | Thiết kế cụ thể và kịch bản theo hành vi mới trước triển khai/nghiệm thu; phụ thuộc file tại PRD 9.5. | Người dùng đã xác nhận; nhóm thiết kế/cụ thể hóa cách chứng minh, không hỏi lại hành vi. |
| **Thiết kế giao diện/vùng:** hướng bố cục đã chốt UI-01.1; kích thước/đồ họa/thiết kế chi tiết, hướng Admin UI-03–UI-05 đã chốt đúng phần, chỉ chi tiết chưa nêu/đồ họa/nền/vị trí/ranh giới và kiểm tra bản thực tế còn công việc sau | FR-K03/FR-A03/FR-A10; AC-K05/K07–K09, AC-A05/A06/A22; NFR-01 | Không chặn yêu cầu khả năng vẽ/chọn vùng và Lưu khác Áp dụng; chưa duyệt thiết kế/ranh giới/bản in cụ thể. | UI-AP08–UI-AP10 đã chốt hướng; không hỏi lại. Hoàn thiện chi tiết ảnh hưởng triển khai/bản đồ và đối chiếu bản thật; đối chiếu lại bản thực tế trước nghiệm thu. | Nhóm đề xuất thiết kế → người dùng xem xét/xác nhận. |
| **TTS còn kiểm:** quota thực/chất lượng vi/en/fr/ru, ngân sách nếu trả phí, giới hạn/pipeline | FR-A04–A06/A12; UC-A03/A04; AC-A08–A13/A26; NFR-04/05 | Model/Charon TTS-01 và WAV-MP3/FFmpeg Linux IN-P03 đã chọn; không còn chờ chọn định dạng. Chưa kiểm pipeline hoặc duyệt 20 cặp. | Thiết kế chi tiết TTS trước code, thực hiện phép thử khi có quyền và D09 trước phục vụ. | Người triển khai kiểm/đề xuất giới hạn có căn cứ → người dùng duyệt nội dung và phần mới, không chọn lại IN-P03. |
| **Cách thực hiện kỹ thuật:** lưu/khôi phục/dọn vòng đời tạm, ghi nhận hoạt động, phiên/tab khách, ký tự hiển thị, mốc nghe, chống lặp, lịch xóa/dọn/xử lý lỗi và phục vụ đúng bài nghe tiếp sau hết hạn P01/UI-02 | Q2/Q3; BR-18/DR-09; FR-A01/A09/FR-D01, FR-K01/K02/K07/K10; AC-A02/A19–A21, AC-D01, AC-K02/K04/K15/K18/K21 | Không chặn hành vi đã rõ; công nghệ/phiên bản nền đã chốt theo TECH-01/TECH-03; thiết kế chi tiết, tương thích thực và lịch chạy còn cần cụ thể hóa/kiểm. Không tự dùng lịch để gia hạn chính sách lưu. | Làm rõ trong TDD trước triển khai phần tương ứng. Nếu đề xuất làm thay đổi hành vi/chính sách thì trình đúng quyết định mới. | Người triển khai đề xuất thiết kế → người dùng xem xét việc đáp ứng yêu cầu, không phải chọn công nghệ để duyệt lại hành vi. |
| **File chưa áp dụng/tạm:** DATA-01 chốt 24 giờ/1 giờ và điều kiện an toàn | FR-A06/A09; UC-A04/A06; AC-A18–A21; PRD 8.3.1 | Không còn thiếu quyết định về hạn/điều kiện đã nêu. Cơ chế kiểm lại/job unknown/lịch/retry còn thiết kế. | TDD trước code dọn; không gán last-unapply giả hoặc đổi 8 giờ A02.3. | Người triển khai thiết kế đúng nguồn; đổi chính sách nếu phát sinh phải trình riêng. |
| **Log/backup:** DATA-01 chốt log 7 ngày/không nhạy cảm, backup 7 ngày/restore riêng/lọc mốc gốc/đuôi gần 37 ngày | DR-04/DR-08; NFR-03; AC-D02/PRD 8.3.1 | DATA-02 cho cục bộ tách dữ liệu chạy, không bắt ổ ngoài/khôi phục toàn máy; DATA-03 chốt DB + WAV/MP3 còn tham chiếu + manifest, cuối ngày có thay đổi/trước deploy-schema; còn path/quyền/công cụ/checkpoint nhất quán/RPO-RTO và cơ chế; không coi tất cả đã chốt hoặc backup đã kiểm. | Làm rõ chính sách chi tiết trước cấu hình; kiểm backup-restore/dọn/log khi nghiệm thu. | Người triển khai đề xuất → người dùng xác nhận đúng phần còn mở, không hỏi lại hạn/restore. |
| **Môi trường/ngưỡng:** IN-P02 chốt Android/Chrome+iPhone/Safari, Admin Windows/Chrome; còn build thực/ngưỡng nếu dùng | NFR-01/04/05/06; AC-K05/K09/K16/K17, AC-A02/A05/A08–A12/A20/A21, AC-D01 | Ngrok chỉ chọn thử; chưa tương thích/QR khác mạng hoặc pipeline CD. Không dùng mốc phiên bản minh họa/giá trị tùy ý làm đạt-trượt. | Ghi model/OS/build khi kiểm; kênh CD riêng đã chọn theo TECH-05; thiết kế chi tiết và kiểm thực trước nghiệm thu. | Người triển khai ghi bằng chứng/thông số, người dùng xác nhận phần ngưỡng mới nếu cần. |
| **Công việc nội dung:** năm bản nháp vi đã được báo cáo là có, chờ xác minh bản lưu/rà soát nguồn, bản dịch, nghe trọn audio và duyệt từng phiên bản | D06/D09; FR-A07/A12; UC-A05/A08; AC-A14/A25/A26; NFR-05 | Không chặn duyệt yêu cầu chức năng/quy trình; chặn đưa từng bản chưa duyệt ra khách và nghiệm thu đủ bộ 20 văn bản/20 audio. | Rà soát/duyệt trước áp dụng từng phiên bản; đủ bộ chuẩn trước nghiệm thu. | Người dùng cung cấp bản nháp; người có năng lực phù hợp rà soát hoặc ghi giới hạn → người dùng duyệt từng bản theo D09. |

Nhóm triển khai gồm Thành, Hòa và Nghĩa, phân công theo [G02](decisions.md#g02). Người phụ trách đề xuất và kiểm chứng phần việc; người phê duyệt nội dung hoặc yêu cầu xác nhận đúng phạm vi còn mở. Quyền thao tác môi trường theo [handoff mục 7](../01-tong-quan/handoff.md#guidance-protocol), phụ thuộc giao diện theo [UI mục 12](../04-giao-dien/ui-design.md#ui-next-technical).


<a id="review-approval-scope"></a>

### 7.4. Phạm vi yêu cầu được duyệt

PRD-01 duyệt các yêu cầu đã mô tả rõ trong bản 0.4.1; Q1–Q3/BR-11 và các bổ sung UI/TECH/DATA được xác nhận riêng tiếp tục áp dụng theo decisions. Không duyệt ngầm phần mở, thiết kế chi tiết, nội dung hoặc kết quả phần mềm. [Căn cứ và diễn biến theo từng lượt](../99-luu-tru/prd-review-history.md#review-approval-scope).

<a id="review-data-05"></a>

### 7.5. DATA-05 — trạng thái thiết kế và kiểm chứng LIFE-P01

| Nhóm đối chiếu | Kết quả / phần chưa kiểm |
| --- | --- |
| Nguồn | DATA-05 mục 3.16/source 8.1.18 chốt vé ký 24 giờ gắn request/quyền tab/epoch, không context/10 phút/generation. LIFE-P02/P03/REC/BKP-P02 giữ nguyên; không toàn TDD/API được duyệt. |
| Nhánh tra đã tạo | Kiểm signature/credential/epoch/fingerprint/ticket metadata, có ledger trả ID/startedAt/expiresAt/state gốc kể cả vé hết hạn; không sửa tên/initialLanguage hoặc hồi sinh phiên. Có ca kiểm backend/MySQL trong bộ 64 test; không suy thành kiểm browser, runtime thật hoặc toàn LIFE-P01. |
| Nhánh tạo lần đầu | Chưa ledger chỉ insert bằng vé còn hạn. UNIQUE+TX lưu session/key/fingerprint/timestamp/digest cùng nhau; unique conflict rollback rồi đọc mới. Vé quá hạn/ledger mất không tạo. |
| Quan hệ D08 | startedAt không trước issuedAt, purge sớm nhất endedAt+30 ngày hoặc expiresAt+30 ngày nên sau ticketExpiresAt. Đủ căn cứ thiết kế để bỏ hook generation/start-purge gate; không kho key/PII vô hạn. Clock/retention/DB thật vẫn phải kiểm. |
| UI/ngoại lệ | Pending giữ đúng vé/request/quyền, không auto cấp mới/tạo S2; chưa gửi lần đầu vé hết thì refresh nền/giữ form. Không xác định/tiếp tục được báo phù hợp và khách xác nhận mới, không nói chắc phiên hết hạn. Không thao tác/màn hình/đồng hồ mới hoặc JWT Admin. |
| Truy vết | PRD 0.8.8 FR-K02/UC-K01/AC-K03/8.3.2 theo mã hiện có; model 0.3/API 0.4/TDD hiện hành/operations 0.3 đồng bộ, generation chỉ lịch sử. V-START01.01–10 giữ các nhóm yêu cầu; đã có bằng chứng một phần backend, trạng thái từng nhóm tại operations 5.1. |
| Bước thực hành | Các mốc phiên khách, K04, V1–V4 và phần Admin đã được hồi quy trong bộ 168 test với MySQL 8.4.13 tạm, xem [bằng chứng hiện hành](../03-ky-thuat/api-transactions.md#admin-place-test-result). Frontend/owner, runtime bền vững, D08 và restart/restore thật vẫn chưa đủ bằng chứng. |
| Phần còn mở | Chưa phát hiện trở ngại phải đổi hành vi LIFE-P01; còn cổng clock/khóa runtime/UNIQUE/concurrent/browser owner/restore/backup/SSH và các mục nội dung/vùng/ngưỡng cũ. Không đóng R06/T01/T02/T03 hoặc nhắc checkbox theo snapshot. |

## 8. Nhật ký báo cáo



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-02-nghiep-vu-prd-review-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
