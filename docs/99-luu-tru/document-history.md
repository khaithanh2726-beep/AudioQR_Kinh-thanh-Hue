# Lịch sử biên tập và nhật ký tài liệu

**Lưu trữ ngày:** 05/11/2026. **Mục đích:** Giữ các mở đầu cũ, nhật ký, ảnh chụp điều hướng cũ và hướng dẫn đã đi qua. Câu trạng thái/công việc kế tiếp bên trong là lịch sử tại thời điểm đó; không yêu cầu chạy lại. Các câu được thay chỉ nhằm diễn đạt đúng hiện trạng/dẫn chiếu, không đổi nghiệp vụ.
**Nguồn hiện hành:** [mở tài liệu chính](../README.md); kiểm phiên bản/trạng thái trước dùng. Bản lưu không là nguồn triển khai song song hoặc phê duyệt mới. [Mục lục lưu trữ](README.md).

## Mục lục phần lưu

- [journal-inputs](#journal-inputs) — từ docs/01-tong-quan/technical-inputs.md
- [journal-ui](#journal-ui) — từ docs/04-giao-dien/ui-design.md
- [journal-review](#journal-review) — từ docs/02-nghiep-vu/prd-review.md
- [journal-bf](#journal-bf) — từ docs/02-nghiep-vu/business-flow.md
- [journal-prd](#journal-prd) — từ docs/02-nghiep-vu/PRD.md
- [journal-roadmap](#journal-roadmap) — từ docs/01-tong-quan/roadmap.md
- [journal-dfd](#journal-dfd) — từ docs/03-ky-thuat/dfd.md
- [journal-tdd](#journal-tdd) — từ docs/03-ky-thuat/TDD.md
- [journal-api](#journal-api) — từ docs/03-ky-thuat/api-transactions.md
- [journal-model](#journal-model) — từ docs/03-ky-thuat/data-model.md
- [journal-operations](#journal-operations) — từ docs/03-ky-thuat/operations-design.md
- [journal-practice](#journal-practice) — từ docs/03-ky-thuat/practice-01-start-session.md
- [practice-skeleton-history](#practice-skeleton-history) — từ docs/03-ky-thuat/practice-01-start-session.md
- [roadmap-old-reminder](#roadmap-old-reminder) — từ docs/01-tong-quan/roadmap.md
- [root-readme-before](#root-readme-before) — từ README.md
- [docs-index-before](#docs-index-before) — từ docs/README.md
- [header-roadmap](#header-roadmap) — từ docs/01-tong-quan/roadmap.md
- [header-technical-inputs](#header-technical-inputs) — từ docs/01-tong-quan/technical-inputs.md
- [header-business-flow](#header-business-flow) — từ docs/02-nghiep-vu/business-flow.md
- [header-decisions](#header-decisions) — từ docs/02-nghiep-vu/decisions.md
- [header-PRD](#header-PRD) — từ docs/02-nghiep-vu/PRD.md
- [header-prd-review](#header-prd-review) — từ docs/02-nghiep-vu/prd-review.md
- [header-TDD](#header-TDD) — từ docs/03-ky-thuat/TDD.md
- [header-api-transactions](#header-api-transactions) — từ docs/03-ky-thuat/api-transactions.md
- [header-data-model](#header-data-model) — từ docs/03-ky-thuat/data-model.md
- [header-operations-design](#header-operations-design) — từ docs/03-ky-thuat/operations-design.md
- [header-dfd](#header-dfd) — từ docs/03-ky-thuat/dfd.md
- [header-practice-01-start-session](#header-practice-01-start-session) — từ docs/03-ky-thuat/practice-01-start-session.md
- [header-ui-design](#header-ui-design) — từ docs/04-giao-dien/ui-design.md
- [editorial-0](#editorial-0) — từ docs/01-tong-quan/roadmap.md
- [editorial-1](#editorial-1) — từ docs/01-tong-quan/roadmap.md
- [editorial-2](#editorial-2) — từ docs/01-tong-quan/roadmap.md
- [editorial-3](#editorial-3) — từ docs/01-tong-quan/roadmap.md
- [editorial-4](#editorial-4) — từ docs/01-tong-quan/roadmap.md
- [editorial-5](#editorial-5) — từ docs/01-tong-quan/roadmap.md
- [editorial-6](#editorial-6) — từ docs/01-tong-quan/technical-inputs.md
- [editorial-7](#editorial-7) — từ docs/01-tong-quan/technical-inputs.md
- [editorial-8](#editorial-8) — từ docs/01-tong-quan/technical-inputs.md
- [editorial-9](#editorial-9) — từ docs/01-tong-quan/technical-inputs.md
- [editorial-10](#editorial-10) — từ docs/02-nghiep-vu/business-flow.md
- [editorial-11](#editorial-11) — từ docs/02-nghiep-vu/business-flow.md
- [editorial-12](#editorial-12) — từ docs/02-nghiep-vu/decisions.md
- [editorial-13](#editorial-13) — từ docs/02-nghiep-vu/decisions.md
- [editorial-14](#editorial-14) — từ docs/02-nghiep-vu/prd-review.md
- [editorial-15](#editorial-15) — từ docs/02-nghiep-vu/prd-review.md
- [editorial-16](#editorial-16) — từ docs/02-nghiep-vu/prd-review.md
- [editorial-17](#editorial-17) — từ docs/02-nghiep-vu/prd-review.md
- [editorial-18](#editorial-18) — từ docs/02-nghiep-vu/PRD.md
- [editorial-19](#editorial-19) — từ docs/03-ky-thuat/TDD.md
- [editorial-20](#editorial-20) — từ docs/03-ky-thuat/dfd.md
- [editorial-21](#editorial-21) — từ docs/03-ky-thuat/practice-01-start-session.md
- [editorial-22](#editorial-22) — từ docs/03-ky-thuat/practice-01-start-session.md
- [editorial-23](#editorial-23) — từ docs/03-ky-thuat/practice-01-start-session.md
- [editorial-24](#editorial-24) — từ docs/03-ky-thuat/practice-01-start-session.md

<a id="journal-inputs"></a>

## Phần lưu: journal-inputs

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="92-nhật-ký-tiếp-nhận-xác-nhận"></a>

### 9.2. Nhật ký tiếp nhận xác nhận

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 03/11/2026 | Bản 0.2: TECH-03/DATA-01/G03 chốt đúng đầu vào; thay ưu tiên Funnel bằng chọn thử ngrok, thay matrix minh họa bằng thiết bị thực cần ghi, lưu dấu vết 0.1. Dẫn TDD 0.1/roadmap 0.5, CD-L01 riêng chỉ đề xuất. | Chưa kiểm tương thích/TTS/ngrok/CD hoặc phê duyệt kiến trúc; không API/cài/đăng ký/code/config/database/test/Git hoặc sửa GitHub. |
| 03/11/2026 | Bản 0.3 ghi TECH-04/DATA-02: đóng gói đã chốt, tab độc lập/backend kiểm quyền/full MP3 và backup cục bộ; TDD 0.2 dữ liệu/cơ chế mới, CD-L01 vẫn đề xuất. | Không đặt yêu cầu ổ ngoài hoặc phục hồi mất laptop; giữ lịch sử 0.1/0.2 và mốc file/phiên, chưa kiểm phần mềm/dịch vụ. |
| 03/11/2026 | Bản 0.4 ghi TECH-05/DATA-03: CD-L01 đã chọn/chưa kiểm, phạm vi-nhịp backup đã chốt; dẫn TDD 0.3/API-giao dịch 0.1 và ví dụ đúng hạn. | Giữ nguyên đoạn lịch sử 0.1 và nhật ký, không suy đã có SSH/Docker/API/DB/test/CD/restore. |
| 03/11/2026 | Bản 0.5 dẫn TDD 0.4/data-model/API 0.2/operations 0.1, phân biệt đầu vào chốt với LIFE/REC còn đề xuất và V-* chưa chạy; giữ hồ sơ khảo sát trước. | Không khảo sát/gọi TTS hoặc kiểm phần mềm/SSH/Docker/Git/GitHub; không chốt TTL mới. |
| 04/11/2026 | Bản 0.6 dẫn DATA-04 và thiết kế tối giản/backup stop phục vụ; giữ các khảo sát/lựa chọn nguồn trước, không dùng phương án cũ như hiện hành. | Chưa kiểm software/provider/SSH/backup/restore/Git/GitHub; không duyệt toàn TDD hoặc context/10 phút. |
| 04/11/2026 | Bản 0.7 dẫn DATA-05/thiết kế vé 24 giờ, ma trận chưa chạy và thực hành 01 dự thảo; bộ phiên bản/khảo sát trước giữ đúng phạm vi. | Không khảo sát/gọi TTS mới hoặc kiểm software/DB/SSH/Git/GitHub; không coi chuẩn bị là đã thực hành. |

<a id="journal-ui"></a>

## Phần lưu: journal-ui

**Chuyển từ:** docs/04-giao-dien/ui-design.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="ui-history"></a>

<a id="9-nhật-ký-và-dấu-vết-thay-thế"></a>

## 9. Nhật ký và dấu vết thay thế

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 31/10/2026 | Tạo 0.1 phác thảo khách, bốn phương án UI-P01–UI-P04 chưa được duyệt lúc đó. Nguyên bản trước lượt hiện tại lưu tại ui-design-history.md. | Lịch sử dự thảo, không áp lại phương án bị thay. |
| 31/10/2026 | Bản 0.2: ghi đúng UI-01/UI-P03 xác nhận mới, thay cuộn cả trang bằng box cuộn, nút ba trạng thái, bộ chọn ngôn ngữ chung, xử lý/chống lặp phiên; tạo ảnh trang và nền/vùng đề xuất có nguồn. | Chỉ phần được nêu rõ đã chốt; UI-P01/UI-P02 còn phần mở, UI-P04 chưa duyệt; không duyệt nền/ranh giới/bản dịch/kỹ thuật hoặc bằng chứng phần mềm. |
| 01/11/2026 | Bản 0.3: ghi UI-02/UI-P01/UI-P02/UI-P04 và P01 nghe hết bài đang phát; thay màn kết thúc riêng bằng box toàn trang, box hết hạn chỉ trong bản đồ và box ứng viên/nền khóa. Bổ sung sơ đồ văn bản, giữ ảnh/hình học/lịch sử cũ. | Chỉ hành vi xác nhận đã chốt; không duyệt toàn bộ UI/ảnh cụ thể/UI Admin/bản đồ/nội dung/TTS/kỹ thuật hoặc phần mềm. Mốc 8 tiếng/lượt/30 ngày không đổi. |
| 01/11/2026 | Bản 0.4: bổ sung UI-A01–UI-A12, khung quản trị/địa điểm/vẽ-xem trước-lưu-áp dụng vùng, hết phiên/khôi phục và UI-AP01–UI-AP03 chờ duyệt. Đối chiếu PRD 0.6, A01/A02/A07/Q1–Q3; cập nhật mục lục/bàn giao. | Chỉ đề xuất thiết kế Admin lượt 1; giữ nguyên phần khách 1–7, nhật ký cũ, tài sản bản đồ và nguồn nghiệp vụ. Không quyết định mới về kỹ thuật hoặc kết quả phần mềm. |
| 02/11/2026 | Bản 0.5: ghi UI-03 chốt đúng hướng UI-AP01–UI-AP03; sửa nhãn tên bắt buộc/tối đa 100, thống nhất luồng vùng và không bắt xem lại hình không đổi sau lưu. Bổ sung UI-A13–UI-A19/UI-AP04–UI-AP07 cho nội dung/bản dịch/audio/mức đầy đủ. | Lượt 2 toàn bộ dự thảo, không phê duyệt theo gói. Giữ phần khách 1–7, nhật ký cũ, lịch sử và tài sản bản đồ; không chọn TTS/kỹ thuật hoặc tuyên bố phần mềm đã đáp ứng. |
| 02/11/2026 | Bản 0.6: UI-04 xác nhận đúng hướng lượt 2; thay hai nút bằng một thao tác cả cặp, gate cặp vi; hồ sơ cặp/giữ tạm và hết phiên dừng nghe thử. Thêm UI-A20–UI-A23/UI-AP08–UI-AP10 cho lượt 3 dự thảo, đồng bộ PRD/Business Flow/báo cáo. | Giữ lịch sử 0.5 ở decisions 7.6, D03 runtime và P01/UI-02; không duyệt toàn bộ UI/lượt 3/nội dung/TTS/kỹ thuật hoặc phần mềm. |
| 02/11/2026 | Bản 0.7: UI-05 xác nhận UI-AP08–UI-AP10; UI-A21 đổi trang riêng thành box trên danh sách giữ lọc. Đồng bộ ngày trọn ngày/hai bảng-tổng/QR PNG, rà soát bảy nhóm nhất quán và thêm mục 13 phân loại công việc trước/trong TDD. | Chỉ hướng được nêu đã chốt; không biên tập lại hàng loạt, không duyệt đồ họa/ranh giới/nội dung/TTS/kỹ thuật hoặc kết quả phần mềm. Lịch sử ở decisions 7.7, tài sản/lịch sử UI giữ nguyên. |
| 02/11/2026 | Bản 0.7.1: dẫn TECH-01, cập nhật mục 13 về công nghệ đã chọn và phiên bản/tương thích/môi trường còn mở; Gemini TTS mới ưu tiên khảo sát. Đồng bộ trạng thái roadmap đã có. | Giữ nguyên hướng UI và mọi sơ đồ, lịch sử/tài sản bản đồ; không duyệt nền/ranh giới hoặc chứng minh tương thích/triển khai/kiểm thử. |
| 03/11/2026 | Bản 0.7.2 dẫn TECH-02/TTS-01 và khảo sát mới; cập nhật mục 13 về đầu vào đã biết, model/Charon đã chọn, đề xuất/tương thích và thiết bị. | Giữ mọi hướng UI/sơ đồ/ranh giới/lịch sử/tài sản; chưa duyệt IN-P/DATA-P hoặc kiểm chứng phần mềm. |
| 03/11/2026 | Bản 0.7.3: cập nhật TECH-03/DATA-01, ngrok thử, matrix thực và TDD 0.1; giữ hướng UI/sơ đồ/câu chữ chưa duyệt riêng, tài sản và lịch sử. | Không duyệt kiến trúc hoặc kiểm phần mềm, không biến mốc browser minh họa thành bắt buộc; phần mở khác giữ nguyên. |
| 03/11/2026 | Bản 0.7.4 đồng bộ TECH-04/DATA-02: phản hồi tải trọn MP3/late response/expiry/lifecycle, không cấp ngoại lệ cho pause trước mốc; TDD 0.2 mới dữ liệu. | Giữ mọi sơ đồ/ảnh/ranh giới/lịch sử, không duyệt đồ họa/TDD hoặc coi browser đã chạy. |

**Đã bị thay thế:** bố cục 0.1 cuộn cả trang đưa bản đồ khỏi tầm nhìn, nhãn thường trực Văn bản/Audio/Trạng thái, UI-P03 dùng nút Phát + dòng Đã phát hết. Giữ nguyên bản cũ để tra lịch sử, không coi chúng là yêu cầu song song.

**Cập nhật tổ chức 01/11/2026:** chuyển ui-design, lịch sử và design-assets vào docs/04-giao-dien; cập nhật liên kết nghiệp vụ và làm rõ tham chiếu Cần Chánh ngoài bản trích ở mục 7. Giữ bản 0.2/trạng thái và hình học/ảnh; không duyệt mới. Mục lục/bàn giao xem [docs/README.md](../README.md).

**Thay thế thêm theo UI-02 (01/11/2026):** đề xuất màn kết thúc riêng 0.2, trạng thái UI-P01/UI-P02/UI-P04 còn chờ và quy tắc P01 hết hạn luôn dừng/khóa toàn bộ audio đã được thay đúng phần. Dấu vết tại [decisions 7.4](decisions-history.md#ui-02-history); ui-design-history.md giữ nguyên là lịch sử, không nguồn yêu cầu song song.

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 03/11/2026 | Bản 0.7.5 đồng bộ TECH-05/DATA-03, PRD 0.8.5/TDD 0.3/API 0.1 và mục 13: CD/phạm vi-nhịp backup đã chọn, kiểm thực/chi tiết còn mở. | Giữ toàn bộ hướng/mã/màn hình và tài sản; không duyệt toàn TDD/API hoặc chứng minh phần mềm/CD/restore. |
| 03/11/2026 | Bản 0.7.6 cập nhật dẫn chiếu thiết kế hiện hành TDD/model/API/operations và phần mở LIFE/REC; không sửa màn hình/bố cục/hành vi UI đã chốt. | Chỉ dẫn chiếu, không duyệt hạn mới/hành vi restore/giải pháp hoặc bằng chứng phần mềm. |
| 04/11/2026 | Bản 0.7.7 chỉ đồng bộ DATA-04 và phụ thuộc TDD 0.5/model/API/operations; không sửa màn hình/hướng UI/tài sản. | Backup downtime không gia hạn phiên, restore thu hồi đúng phạm vi; LIFE-P01 không context/10 phút đã duyệt, chưa phần mềm được kiểm. |
| 04/11/2026 | Bản 0.7.8 đồng bộ DATA-05 vé ký 24 giờ/lookup/pending/refresh nền và dẫn TDD hiện hành/thực hành 01; không màn hình hoặc đồ họa mới. | Không start_generation/context hoặc toàn UI/TDD/software đã duyệt/kiểm; DATA-04 các phần khác giữ. |

<a id="journal-review"></a>

## Phần lưu: journal-review

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="8-nhật-ký-báo-cáo"></a>

## 8. Nhật ký báo cáo

| Ngày | Thay đổi | Phân biệt trạng thái |
| --- | --- | --- |
| 30/10/2026 | Tạo báo cáo rà soát trực tiếp 23 FR, 14 UC, 6 NFR, 50 AC; ghi R01–R06, giữ PRD chỉ sửa dẫn chiếu do sắp xếp nguồn. | Phát hiện/đề xuất khi đó chưa được áp dụng; không là kết quả phần mềm. |
| 31/10/2026 | Đọc bốn file hiện tại; sửa R01–R05 theo nguồn đã chốt, kiểm tra truy vết hai chiều và ngữ cảnh; phân loại R06 tại PRD 9.2. Giữ mã/mô tả ban đầu, cập nhật vị trí và kết quả xử lý. Ghi S01 và bổ sung dẫn chiếu nguồn A02.1. | Đã sửa/đối chiếu tài liệu; PRD vẫn dự thảo, R06 còn mở, chưa triển khai/kiểm thử hoặc chứng minh phần mềm đáp ứng. |
| 31/10/2026 | Lượt tiếp theo: khôi phục A02.1 và đưa câu lịch sử về đúng vị trí, đóng S01; sửa giới thiệu trạng thái báo cáo trong Business Flow, ghi/đóng S02. Soạn Q1–Q3 ở PRD 9.4 và tóm tắt tại mục 7.1, giữ các phương án mới chờ xác nhận. | S01/S02 đã sửa tài liệu; R01–R05 giữ trạng thái đã xử lý tài liệu; R06/Q1–Q3 còn chờ duyệt. Không đổi nghiệp vụ đã duyệt hoặc chứng minh phần mềm đã đáp ứng. |
| 31/10/2026 | Lượt xác nhận: đối chiếu PRD 0.3 và xác nhận người dùng Q1–Q3, ghi nguồn decisions 5.8, chuyển nguyên đề xuất sang decisions 7.3 và giữ tóm tắt cũ ở mục 6.1. Cụ thể hóa PRD 0.4/Business Flow, cập nhật vị trí và truy vết, ghi phạm vi đã chốt trong R06. | Chỉ ba chi tiết Q1–Q3 đã chốt; R06 còn mở các phụ thuộc khác; PRD vẫn dự thảo, chưa có bằng chứng phần mềm. |
| 31/10/2026 | Rà soát cuối tập trung thay đổi Q1–Q3 theo bản thực tế PRD 0.4, nguồn và các tình huống chi tiết. Ghi/sửa R07 ở BR-11 thành bản 0.4.1; kiểm tra truy vết/dẫn chiếu, gom phần mở tại 7.4 và nêu phạm vi đề nghị duyệt/loại trừ/bước tiếp theo tại 7.5. Giữ phát hiện/nhật ký cũ và nguồn nghiệp vụ. | Không phát hiện sai lệch Q1–Q3 khác cần quyết định lại; R06 vẫn mở một phần. Chưa duyệt PRD, thiết kế, khảo sát TTS hoặc thao tác phần mềm. |
| 31/10/2026 | Đọc lại đúng PRD 0.4.1 và ba tài liệu liên quan; ghi nhận PRD-01 duyệt phạm vi yêu cầu đã rõ, gồm Q1–Q3/BR-11. Đồng bộ trạng thái/loại trừ, giữ bảng 7.4/R06; liên kết phác thảo khách ui-design.md và các đề xuất chưa chốt. | Duyệt tài liệu theo phạm vi, không đóng điểm mở hoặc chứng minh phần mềm/nội dung đáp ứng; chưa chọn/khảo sát TTS, công nghệ hoặc thực hiện kỹ thuật. |
| 31/10/2026 | Ghi UI-01 sau PRD-01; đồng bộ phạm vi đã chốt/mở, bản PRD 0.5 và ui-design 0.2. Giữ phát hiện/nhật ký cũ, thêm đối chiếu 7.6, cập nhật 7.4 đúng phần UI-P03. | Không đóng toàn bộ R06 hoặc duyệt toàn bộ UI/vùng/bản dịch/kỹ thuật; không là kết quả phần mềm. |
| 01/11/2026 | Chuyển báo cáo vào 02-nghiep-vu, cập nhật liên kết UI theo cấu trúc mới; giữ R01–R07/S01/S02 và R06 còn mở một phần. Sửa E15/bảng lịch sử/hồ sơ nguồn được ghi trong nhật ký tài liệu tương ứng. | Báo cáo rà soát, không nguồn yêu cầu song song hoặc bằng chứng phần mềm; không đóng điểm mở theo việc sắp xếp. |
| 01/11/2026 | Đối chiếu UI-02/P01 có chủ đích, đồng bộ PRD 0.6/UI 0.3, trạng thái UI-P01/UI-P02/UI-P04, ca nghe tiếp/box/30 ngày và phụ thuộc file. Giữ phát hiện/lịch sử và R06 còn mở phần khác. | Kiểm tra tài liệu, không bằng chứng phần mềm; không thay ảnh/bản đồ/lịch sử UI hoặc chọn kỹ thuật. |
| 02/11/2026 | Đối chiếu UI-04 có chủ đích, đồng bộ PRD 0.7/UI 0.6/Business Flow/decisions: cặp/gate/hồ sơ/nghe thử hết phiên, giữ D03 runtime và P01/UI-02. Cập nhật truy vết/7.8 và phạm vi lượt 3 dự thảo. | Kiểm tra tài liệu, không kiểm thử phần mềm; R06 còn phụ thuộc khác, không duyệt toàn bộ UI/nội dung/TTS/kỹ thuật. |
| 02/11/2026 | Đối chiếu UI-05, đồng bộ PRD 0.8/UI 0.7 và box UI-A21, hai bảng-tổng/ngày trọn ngày/QR PNG. Rà soát bảy nhóm nhất quán tại 7.9, phân loại công việc trước/trong TDD tại UI 13. | Không viết lại hàng loạt/lịch sử, không đóng R06 hoặc chứng minh phần mềm/nội dung/TTS/URL đã đáp ứng; chưa tạo TDD/roadmap hoặc thao tác kỹ thuật. |
| 02/11/2026 | Ghi TECH-01 tại 7.10, cập nhật trạng thái công nghệ và R06/7.4; dẫn PRD 0.8.1/UI 0.7.1/roadmap 0.3. | Chỉ chọn công nghệ được chốt; chưa kiểm chứng tương thích/triển khai/kiểm thử; R06 còn mở, không khảo sát dịch vụ hoặc thay đổi GitHub. |
| 03/11/2026 | Ghi TECH-02/TTS-01/khảo sát tại 7.11, đồng bộ PRD 0.8.2/UI 0.7.2 và đúng phần R06/7.4; phân biệt nguồn công bố, demo chỉ đọc, sáu MP3 theo người dùng và tích hợp chưa kiểm. | IN-P/DATA-P/lịch chưa duyệt; R06 không đóng theo gói, chưa API hoặc thực thi phần mềm/Git/GitHub. |
| 03/11/2026 | Ghi 7.12/TECH-03/DATA-01/G03, đồng bộ PRD 0.8.3/UI 0.7.3 và R06 đúng phần đã xác nhận; TDD 0.1 kiến trúc dự thảo, đối chiếu checklist GitHub chỉ đọc. | Ngrok thử/tương thích và phần mềm chưa kiểm; phần mở không đóng theo gói, không sửa GitHub hoặc thực thi code/config/database/test/Git/CI-CD. |
| 03/11/2026 | Ghi 7.13 và R06 đúng phạm vi TECH-04/DATA-02/TDD 0.2; đối chiếu PRD 0.8.4/UI 0.7.4 và checklist thật liên quan. | ERD/cơ chế mới dự thảo, chưa DB/browser/software; không có mục mới đủ tích, không thay GitHub/Git hoặc dịch vụ/demo. |
| 03/11/2026 | Thêm 7.14, ghi TECH-05/DATA-03, cập nhật R06/PRD 0.8.5/UI 0.7.5 và dẫn TDD 0.3/API 0.1; giữ kết luận cũ theo thời điểm. | Kiểm tra tài liệu, không xác nhận phần mềm/API/CD/backup đã chạy; không đổi GitHub hoặc đóng R06 toàn bộ. |
| 03/11/2026 | Ghi 7.15 về mô hình hiện hành/DTO/lifecycle/backup/V-* và phần LIFE/REC còn mở; PRD/UI chỉ dẫn chiếu, nguồn nghiệp vụ giữ nguyên. | Chưa phần mềm/SSH/backup/CD được kiểm hoặc xác nhận TTL/restore mới, không đọc/thay GitHub. |
| 04/11/2026 | Thêm 7.16, đồng bộ DATA-04/PRD 0.8.7/UI 0.7.7 và TDD hiện hành; ghi sửa dấu #/lịch sử thay thế/context không duyệt. | Kiểm tài liệu, chưa phần mềm/drain/SSH/backup/restore hoặc duyệt toàn TDD; không Git/GitHub. |
| 04/11/2026 | Thêm 7.17 DATA-05, rà soát vé/ledger/expiry và bằng chứng lý luận D08, dẫn ma trận chưa chạy/thực hành 01; giữ 7.16/generation trong lịch sử. | Chưa phần mềm hoặc môi trường/test thực, không toàn TDD được duyệt hoặc đọc/sửa GitHub. |

<a id="journal-bf"></a>

## Phần lưu: journal-bf

**Chuyển từ:** docs/02-nghiep-vu/business-flow.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="bf-8"></a>

<a id="8-nhật-ký-thay-đổi-ngắn-gọn"></a>

## 8. Nhật ký thay đổi ngắn gọn

Toàn bộ căn cứ theo lượt và nhật ký cũ đã chuyển, giữ nguyên tại [decisions.md mục 8.1 và 8.3](../02-nghiep-vu/decisions.md#dec-history); phương án bị thay thế tại [mục 7](../02-nghiep-vu/decisions.md#dec-7). Số mục cũ có bảng quy đổi ở decisions.md mục 8.4.

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 30/10/2026 | Ghi nhận trạng thái duyệt hiện hành BF-K01/BF-A01/BF-ALL01, gồm A02.6 đã xác nhận trước lần sắp xếp này. | Duyệt nghiệp vụ, không duyệt giao diện/nội dung/TTS/kỹ thuật hoặc bằng chứng kiểm thử. |
| 30/10/2026 | Sắp xếp Business Flow theo mục tiêu → quy tắc chung → khách → Admin → liên quan hai vai trò → thiết kế/rà soát → tài liệu tiếp theo → nhật ký; gom bảng quyết định/lịch sử sang decisions.md, sửa câu trạng thái cũ và cập nhật dẫn chiếu. Đối chiếu trực tiếp PRD, chỉ sửa PRD dẫn chiếu bị ảnh hưởng. | **Thay đổi cấu trúc và dẫn chiếu, không phê duyệt nghiệp vụ mới.** Giữ mã/quy tắc/thời hạn/điều kiện/phạm vi/trạng thái đã chốt; PRD vẫn dự thảo. |
| 31/10/2026 | Cập nhật câu giới thiệu prd-review.md theo trạng thái R01–R05 đã xử lý tài liệu, R06 còn mở; đồng bộ S01/S02 đã sửa. Giữ luồng hai vai trò và dẫn người đọc tới bộ đề xuất Q1–Q3 trong PRD. | Sửa trạng thái báo cáo, không phê duyệt nghiệp vụ mới hoặc chứng minh phần mềm đáp ứng. |
| 31/10/2026 | Đối chiếu xác nhận toàn bộ Q1–Q3 của PRD 0.3; bổ sung thông tin điểm, giữ tạm và hoạt động Admin vào đúng nhánh AD/AE, mục 2/4.5/5; cập nhật phần còn mở và trạng thái báo cáo. Giữ luồng khách, cơ chế xóa và các quyết định khác. | Q1–Q3 đã chốt bổ sung; không yêu cầu duyệt lại Business Flow, không tự duyệt tổng thể PRD hoặc chứng minh phần mềm đáp ứng. |
| 31/10/2026 | Đồng bộ trạng thái PRD-01 duyệt phạm vi yêu cầu đã rõ của PRD 0.4.1 và liên kết ui-design.md dự thảo phần khách; giữ nguyên luồng/quy tắc khách-Admin. | Không mở lại nghiệp vụ hoặc duyệt ngầm giao diện/ranh giới, điểm mở, nội dung, TTS, kỹ thuật hay kết quả phần mềm. |
| 31/10/2026 | Đồng bộ bổ sung UI-01/UI-P03 vào nhánh bắt đầu, audio, ngôn ngữ và kết thúc; ghi hướng box cuộn/khung bản đồ và các giới hạn kế thừa. Giữ cơ chế phiên/lượt/ngoại lệ và luồng Admin. | Chỉ chốt phần được xác nhận, không duyệt toàn bộ UI hoặc ranh giới/bản dịch; UI-P01/UI-P02/UI-P04 còn phần mở. |
| 01/11/2026 | Làm rõ E15 theo ba nhánh P02; chuyển tài liệu vào 02-nghiep-vu, cập nhật liên kết/đường dẫn bàn giao và vị trí dự kiến TDD/roadmap. | Chỉ sửa diễn đạt/cấu trúc/dẫn chiếu; không đổi nghiệp vụ hoặc trạng thái duyệt. |
| 01/11/2026 | Ghi UI-02 và xác nhận UI-P01/UI-P02/UI-P04: box ứng viên, tua còn hiệu lực, box kết thúc toàn trang và box hết hạn trong bản đồ; chủ đích thay phần P01 luôn dừng audio bằng ngoại lệ nghe hết bài đang phát. Đồng bộ ngoại lệ/bảng quyền và phụ thuộc file. | Giữ mốc hết hạn 8 tiếng, đếm phiên/lượt, hạn lưu 30 ngày và A02.3; không duyệt kỹ thuật/ảnh cụ thể/bản đồ hoặc phần mềm đã đáp ứng. |
| 02/11/2026 | Đồng bộ UI-04: một thao tác áp dụng cả cặp, gate cặp vi, Hồ sơ duyệt của cặp và giữ tạm tham chiếu; Admin hết phiên dừng nghe thử. Cập nhật luồng/ngoại lệ/liên kết; giữ D03 runtime và P01/UI-02. | Thay đổi có chủ đích được người dùng xác nhận, dấu vết cũ ở decisions 7.6; không duyệt UI lượt 3/nội dung/TTS/kỹ thuật hoặc kết quả phần mềm. |
| 02/11/2026 | Đồng bộ UI-05: UI-A21 là box trên danh sách giữ lọc, ngày trọn ngày/hai bảng-tổng và QR chung PNG/in đơn giản. Cập nhật liên kết PRD/UI và công việc chuẩn bị TDD; giữ các luồng/ngoại lệ khác. | Chỉ hướng UI-AP08–UI-AP10 xác nhận, không duyệt toàn bộ đồ họa/ranh giới/nội dung/TTS/kỹ thuật hoặc phần mềm; lịch sử ở decisions 7.7. |
| 02/11/2026 | Ghi dẫn chiếu TECH-01 và trạng thái PRD 0.8.1/roadmap 0.3, sửa câu roadmap chưa tồn tại; không đổi luồng chính/ngoại lệ hoặc trạng thái duyệt nghiệp vụ/UI. | Lựa chọn công nghệ do người dùng xác nhận riêng; chưa kiểm chứng tương thích, tạo TDD, triển khai hoặc kiểm thử. TTS/môi trường/chính sách tiếp tục mở. |
| 03/11/2026 | Đồng bộ TECH-02/TTS-01, PRD 0.8.2/roadmap 0.4 và báo cáo đầu vào; sửa trạng thái model/giọng chỉ ưu tiên khảo sát thành đã chọn. | Không đổi luồng/quy tắc duyệt; sáu MP3 được nghe không duyệt bộ 20. IN-P/DATA-P/lịch mới chưa chốt; chưa thực thi phần mềm. |
| 03/11/2026 | Ghi TECH-03/DATA-01/G03 đúng phần, đồng bộ PRD 0.8.3/roadmap 0.5 và TDD 0.1 đã có; chính sách mới đặt ở quy tắc chung, giữ luồng/ngoại lệ. | Ngrok chỉ chọn thử; TDD chưa duyệt, chưa tương thích/TTS/ngrok/CI-CD thực; phần backup-log còn mở không bị chốt ngầm. |
| 03/11/2026 | Đồng bộ TECH-04/DATA-02: tab độc lập/full MP3/không grant pause trước expiry hoặc response muộn; backup cục bộ bỏ ngoài laptop bắt buộc. Dẫn PRD 0.8.4/TDD 0.2/roadmap 0.6. | Luồng đã duyệt giữ nguyên đúng phần làm rõ; ERD/cơ chế/CD-L01 chưa duyệt/kiểm, không code/database/test/Git/dịch vụ/demo. |
| 03/11/2026 | Ghi TECH-05/DATA-03 vào đầu vào/quy tắc dữ liệu và phần mở; dẫn PRD 0.8.5/TDD 0.3/API 0.1, giữ luồng chính/ngoại lệ. | Chốt đúng CD riêng/phạm vi-nhịp backup, không duyệt toàn TDD/ERD/API hoặc chứng minh SSH/Docker/CD/restore/phần mềm đã chạy. |
| 03/11/2026 | Chỉ đồng bộ dẫn chiếu bản kỹ thuật hiện hành và PRD 0.8.6; mô hình hợp nhất/API/operations vẫn dự thảo, LIFE/REC chưa duyệt. | Giữ nguyên luồng/quyết định đã chốt, không thêm yêu cầu hoặc dùng thiết kế làm bằng chứng phần mềm. |
| 04/11/2026 | Ghi DATA-04 đúng phần quy tắc dùng chung/đầu vào và dẫn PRD 0.8.7/TDD 0.5; giữ luồng nghiệp vụ khác. | Không context/10 phút đã duyệt, không thời lượng backup hoặc phần mềm đã kiểm; không Git/GitHub/thực thi. |
| 04/11/2026 | DATA-05 bổ sung quy tắc Bắt đầu/ledger/vé ký 24 giờ và dẫn PRD 0.8.8/TDD hiện hành; giữ DATA-04 và luồng khác. | Không toàn TDD hoặc ca kiểm đã đạt, không thực thi code/config/DB/test/Git/GitHub/triển khai. |

<a id="journal-prd"></a>

## Phần lưu: journal-prd

**Chuyển từ:** docs/02-nghiep-vu/PRD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="11-nhật-ký-prd"></a>

## 11. Nhật ký PRD

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 30/10/2026 | Kiểm tra repo và hai tài liệu nguồn, PRD chưa tồn tại; tạo bản 0.1 gồm phạm vi/quy tắc/dữ liệu, FR, UC, NFR, AC, điểm cần làm rõ và thứ tự rà soát. Nguồn đã đồng bộ A02.6 và duyệt nghiệp vụ hai vai trò. | Toàn bộ PRD/UC/AC/NFR là dự thảo; không tự chọn công nghệ/ngưỡng, không tuyên bố nội dung hoặc phần mềm đã kiểm chứng. |
| 31/10/2026 | Bản 0.2: đọc/đối chiếu bốn file hiện tại; xử lý R01/R02/R04/R05 về chọn vùng, điều kiện phiên, kết quả tự xóa và dọn file; sửa truy vết R03 hai chiều, bổ sung danh sách AC cho từng UC. Phân loại R06/O-01–O-06 theo nghiệp vụ/tiêu chí/kỹ thuật và mốc cần chốt, không tự chọn công nghệ hoặc ngưỡng. | PRD/UC/AC vẫn dự thảo; giữ mã và nghiệp vụ nguồn. Đã sửa tài liệu, chưa thực hiện/kiểm chứng phần mềm. |
| 31/10/2026 | Bản 0.3: bổ sung bộ phương án Q1–Q3 tại mục 9.4 về trường địa điểm, bảo vệ nội dung Admin chưa lưu và hoạt động tính vào phiên; đối chiếu chung và liệt kê FR/UC/AC cần cụ thể hóa sau xác nhận. Đồng bộ dẫn chiếu phần đề xuất mục 9.2/9.3. | Chỉ là đề xuất chờ người dùng duyệt; chưa đổi FR/UC/AC, chưa ghi phương án mới vào nhóm quyết định đã chốt; PRD vẫn dự thảo. |
| 31/10/2026 | Bản 0.4: đối chiếu và ghi nhận người dùng xác nhận toàn bộ Q1–Q3 của bản 0.3; cụ thể hóa thông tin điểm, giữ tạm và hoạt động Admin vào BR/DR/FR/UC/AC/NFR, giữ 23 FR/14 UC/50 AC. Thêm BR-18/DR-09 để tách dữ liệu tạm Admin, mở rộng tình huống dưới AC-A02/A03/A04 thay vì đổi mã. Chuyển nguyên đề xuất cũ sang decisions 7.3, thay PRD 9.4 bằng bản dẫn xác nhận hiện hành; cập nhật phụ thuộc/readiness. | Nguồn Q1–Q3 đã chốt; toàn bộ PRD/UC/AC vẫn dự thảo chờ duyệt tổng thể. R06 còn phụ thuộc khác; chưa triển khai/kiểm thử hoặc chọn TTS/công nghệ/ngưỡng. |
| 31/10/2026 | Bản 0.4.1: rà soát cuối tập trung thay đổi Q1–Q3; sửa câu cuối BR-11 đang nhập nhằng số địa điểm/ngôn ngữ thành khả năng thêm địa điểm, giữ phạm vi vi/en/fr/ru và chưa thêm quản lý ngôn ngữ theo D06/A02.5. Liên kết kết quả và danh mục loại trừ ở prd-review 7.3–7.5. Giữ mã và nội dung yêu cầu còn lại. | Chỉ sửa câu chữ/dẫn chiếu theo quyết định đã chốt; PRD vẫn dự thảo, không tự duyệt tổng thể hoặc xác nhận phần mềm/nội dung đã kiểm chứng. |
| 31/10/2026 | Ghi nhận PRD-01: người dùng duyệt các yêu cầu mô tả rõ của đúng bản 0.4.1, gồm Q1–Q3 và BR-11; cập nhật trạng thái/giới hạn và dẫn chiếu ui-design.md dự thảo. Giữ nguyên phiên bản, mã và nội dung BR/DR/FR/UC/AC/NFR; không đóng điểm mở. | Duyệt phạm vi đã rõ, loại trừ mục còn mở/đề xuất/chưa kiểm chứng và thiết kế/nội dung/TTS/kỹ thuật/kết quả kiểm thử; không là bằng chứng phần mềm đáp ứng. |
| 31/10/2026 | Bản 0.5: kế thừa phạm vi 0.4.1/PRD-01, bổ sung đúng xác nhận UI-01/UI-P03 về khung cuộn riêng, nút ba trạng thái, ngôn ngữ chung và Bắt đầu-Kết thúc xử lý/chống lặp. Giữ mã FR/UC/AC; đồng bộ nhánh mở, nguồn và trường hợp quan sát. | Chỉ phần người dùng xác nhận được chốt; không duyệt toàn bộ UI-P01/UI-P02/UI-P04, thiết kế cụ thể, nền/vùng/bản dịch/TTS/kỹ thuật hoặc kết quả phần mềm. |
| 01/11/2026 | Giữ phiên bản 0.5 và toàn bộ BR/DR/FR/UC/AC/NFR; chỉ chuyển vào 02-nghiep-vu, cập nhật liên kết chéo UI và đường dẫn bàn giao theo docs/README.md. | Không thay yêu cầu, phạm vi hoặc trạng thái phê duyệt; lịch sử giữ nguyên, không là kết quả kiểm thử phần mềm. |
| 01/11/2026 | Bản 0.6: ghi UI-02 xác nhận UI-P01/UI-P02/UI-P04, hai loại box và thay đổi có chủ đích P01 để đúng bài đang phát lúc hết hạn nghe hết/pause-resume. Đồng bộ BR/DR/FR/UC/AC/NFR, thêm tình huống dưới AC-K18/K20 và phụ thuộc TDD 9.5; giữ mã hiện có, bổ sung liên kết truy vết trực tiếp. | Mốc 8 tiếng/lượt/30 ngày và A02.3 không đổi; không khôi phục ngoại lệ sau reload hoặc mở quyền tua/Nghe lại. Không chọn kỹ thuật/duyệt toàn bộ UI/bản đồ/nội dung/TTS hoặc kết quả phần mềm. |
| 02/11/2026 | Bản 0.7: đồng bộ UI-04/UI-AP04–UI-AP07; một thao tác áp dụng cả cặp, gate cặp vi, hồ sơ cặp/giữ tạm tham chiếu và hết phiên Admin dừng nghe thử. Cập nhật BR/DR/FR/UC/AC/NFR và ca 8.2.3, liên kết truy vết theo mã hiện có; sửa tham chiếu UI cũ ở đầu/bàn giao thành đường dẫn hiện hành. | Thay đổi có chủ đích đã xác nhận; giữ D03 runtime/P01/UI-02/20+20. Không đổi mã 23 FR/14 UC/50 AC/6 NFR, không duyệt UI lượt 3/nội dung/TTS/kỹ thuật hoặc kết quả phần mềm. |
| 02/11/2026 | Bản 0.8: UI-05 xác nhận đúng UI-AP08–UI-AP10; FR-A10/A11, UC-A07/A08 và AC-A22–A24 đồng bộ box chi tiết, ngày trọn ngày/hai bảng-tổng, QR chung PNG/in. Bổ sung ca 8.2.4 theo mã hiện có và dẫn mục 13 UI về công việc trước/trong TDD. | Không đổi mã/23 FR/14 UC/50 AC/6 NFR hoặc hành vi khác; không duyệt toàn bộ UI/ranh giới/nội dung/TTS/kỹ thuật hay kết quả phần mềm, R06 còn mở một phần. |
| 02/11/2026 | Bản 0.8.1: ghi dẫn chiếu TECH-01 và trạng thái công nghệ nền đã chọn; cập nhật O-01/O-02 và ghi chú NFR-03 đúng phần. Không đổi FR/UC/AC hoặc hành vi nghiệp vụ. | Chưa kiểm chứng tương thích, triển khai hoặc kiểm thử; Gemini TTS mới ưu tiên khảo sát, môi trường/ngân sách/chính sách còn mở; R06 không đóng theo gói. |
| 03/11/2026 | Bản 0.8.2 dẫn TECH-02/TTS-01 và khảo sát 03/11; cập nhật O-01/trạng thái R06, thêm 9.6 về môn học ba lớp/CI-CD và phạm vi thiết bị đã xác nhận. Giữ mã/yêu cầu nghiệp vụ và các bảng truy vết. | Model/giọng và đầu vào đã chốt; các IN-P/DATA-P/lịch chưa duyệt, không là phần mềm đáp ứng. Không API/TDD/thực thi/Git. |
| 03/11/2026 | Bản 0.8.3: TECH-03/DATA-01 cập nhật DR-04/DR-08/NFR-03/O-01/O-03/O-05/O-06, mục 8.3.1/9.6 và dẫn TDD 0.1; giữ FR/UC/AC và nghiệp vụ khác. | Chỉ đúng xác nhận đầu vào/chính sách; ngrok chọn thử, TDD kiến trúc chưa duyệt; R06 còn mở một phần, chưa thực thi phần mềm. |
| 03/11/2026 | Bản 0.8.4: TECH-04/DATA-02 đồng bộ tab/backend quyền, full MP3/negative expiry/late response/giải phóng và backup cục bộ; BR/DR/FR-K02/K05/UC-K02/K03/AC-K04/K06/K18/NFR-06/9.5 giữ mã. | Không thay 8 giờ/30 ngày hoặc phạm vi phần mềm/nội dung đã duyệt; cơ chế/ERD dự thảo chưa kiểm, R06 còn mở. |
| 03/11/2026 | Bản 0.8.5: dẫn TECH-05/DATA-03, cụ thể hóa DR-08/NFR-03/O-05/9.2 về phạm vi-nhịp backup cục bộ và CD đã chọn; dẫn TDD 0.3/API 0.1 và sửa trạng thái build thiết bị IN-P02 theo nguồn đã xác nhận. | Giữ mã/FR/UC/AC và nghiệp vụ; API/ERD vẫn dự thảo, chưa SSH/Docker/CD/backup-restore hoặc phần mềm đáp ứng; R06 còn mở. |
| 03/11/2026 | Bản 0.8.6 chỉ đồng bộ đường đọc TDD/model/API/operations hiện hành và trạng thái LIFE/REC chưa duyệt. | Không đổi mã/yêu cầu/tiêu chí hoặc phạm vi đã chốt, không duyệt TDD/TTL mới hoặc kết quả phần mềm. |
| 04/11/2026 | Bản 0.8.7 ghi DATA-04 vào DR-08/NFR-03, mục 8.3.2 và dẫn TDD hiện hành: window/job/restore/backup stop đã chốt, LIFE-P01 chỉ đơn giản hóa. | Giữ mã/luồng/phạm vi, không context/10 phút hoặc toàn TDD được duyệt; chưa kiểm phần mềm/backup/SSH. |
| 04/11/2026 | Bản 0.8.8 đồng bộ DATA-05 vào FR-K02/UC-K01/AC-K03 và mục 8.3.2, vé ký 24 giờ/ledger/expiry hai nhánh; dẫn thực hành 01 và TDD hiện hành. | Giữ mã/số lượng yêu cầu, LIFE-P01 duyệt riêng không toàn TDD/kiểm phần mềm; không start_generation hoặc thay DATA-04. |

<a id="journal-roadmap"></a>

## Phần lưu: journal-roadmap

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="7-nhật-ký-tài-liệu"></a>

## 7. Nhật ký tài liệu

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 02/11/2026 | Kiểm kê cấu trúc ngoài .git và nguồn hiện hành, soạn bản 0.1 gồm 11 issue lớn/tiến độ/bằng chứng/phụ thuộc/điều kiện đóng; tạo đúng thư mục chứa roadmap và cập nhật README/mục lục. | Dự thảo chờ duyệt; không tạo issue/Project hoặc giả lịch sử, chưa làm kỹ thuật/TTS/kiểm thử/Git/triển khai. Công việc tài liệu trước issue thuộc T01 được ghi nhận riêng. |
| 02/11/2026 | Biên tập bản 0.2: mỗi issue chỉ có Description và một Checklist; giữ tiến độ/bằng chứng và điều kiện hoàn thành, gom thông tin chung; ghi phân công nhóm theo G02. Thay bản dài tại chỗ, không giữ hai backlog hiện hành. | Chỉ phân công được xác nhận; nội dung issue/trạng thái còn chờ duyệt, chưa chốt lịch hoặc thay đổi GitHub. Không thực hiện kỹ thuật/kiểm thử/Git/triển khai. |
| 02/11/2026 | Bản 0.3 ghi TECH-01 và kết quả phân tích bước A; đồng bộ xác nhận backlog 0.2/11 issue #49–#59 vào hồ sơ local theo bằng chứng lượt trước. Tách công nghệ đã chọn khỏi tương thích/môi trường; ưu tiên khảo sát Gemini ở T07. | Chỉ lựa chọn công nghệ được chốt; diễn giải issue mới là dự thảo local, chưa sửa GitHub. T01/T07 chưa hoàn thành; chưa kiểm tra tương thích, khảo sát TTS/chính sách hoặc thực hiện kỹ thuật. |
| 03/11/2026 | Bản 0.4 ghi TECH-02/TTS-01, khảo sát demo chỉ đọc/nguồn chính thức; cập nhật tiến độ T01/T07/T10, phạm vi thiết bị T06 và lịch mục tiêu mới có ước lượng/cổng. | Model/giọng và đầu vào đã chốt; IN-P/DATA-P/lịch chưa duyệt. Không gọi API, viết TDD, thử phần mềm hoặc thay Git/GitHub. Không kế thừa topology/log/backup/lịch demo. |
| 03/11/2026 | Bản 0.5: TECH-03/DATA-01/G03, checklist local tách đã chọn khỏi kiểm thực, TDD 0.1 kiến trúc mới; lịch khoảng ngày/G02/phụ thuộc/kết quả/dự phòng với đánh giá quỹ giờ dự kiến. Đọc 11 issue, ghi chênh lệch T01/T06/T07/T10 và nhắc đúng T07 khảo sát. | Không thay GitHub/Project, không coi giờ giả định/đồ án tương lai hoàn tất. Ngrok chỉ chọn thử, chính sách chi tiết/CD-L01/TDD còn mở, chưa phần mềm/CI-CD/test. |
| 03/11/2026 | Bản 0.6 ghi TECH-04/DATA-02/TDD 0.2: ERD/tab-audio/backup dự thảo, T02 chuyển đang thiết kế một phần; checklist local tách khỏi DB/UI đã thực hiện. Đọc lại #49/#50/#55/#58; T07 khảo sát đã [x]. | Chưa mục mới đủ căn cứ nhắc tích; không thay GitHub/Git hoặc thực thi DB/code/test/CD, giữ nguồn/phân công/lịch dự kiến. |
| 03/11/2026 | Bản 0.7: TECH-05/DATA-03, TDD 0.3/API-giao dịch 0.1 dự thảo; T10 tách ghi lựa chọn khỏi kiểm SSH/Docker/CD, T01 vẫn một phần. Đọc lại #49/#50/#55/#58, chưa mục mới đủ tích. | Không sửa GitHub/Project hoặc code/DB/test/CI-CD/restore; giữ 11 task/phân công/lịch dự kiến và bằng chứng đã có. |
| 03/11/2026 | Bản 0.8 ghi TDD 0.4, mô hình hiện hành/API 0.2/operations 0.1 và lịch sử mô hình 0.3; tiến độ DTO/lifecycle/backup/V-* là thiết kế dự thảo, không checkbox triển khai. | Không đọc/thay GitHub hoặc chạy phần mềm; hạn LIFE/REC còn chờ xác nhận, G02/lịch mục tiêu giữ nguyên. |
| 04/11/2026 | Bản 0.9 ghi DATA-04/TDD 0.5/model 0.2/API 0.3/operations 0.2; LIFE-P01 đề xuất tối giản và BKP-P02 stop phục vụ, không tăng checkbox hoàn tất. | Không đọc/thay GitHub/Project; xác nhận chính sách không bằng chứng DB/TTS/backup/SSH/CD chạy, lịch mục tiêu giữ là dự kiến. |
| 04/11/2026 | Bản 0.10 ghi DATA-05/TDD 0.6/model 0.3/API 0.4/operations 0.3, soạn thực hành 01 và ma trận vé/ledger 10 nhóm chưa chạy; giữ T01–T11/G02/mốc mục tiêu. | Không nhắc tích/đóng theo tài liệu, không đọc/thay GitHub hoặc thực hiện môi trường/code/DB/test/Git/triển khai. |

<a id="journal-dfd"></a>

## Phần lưu: journal-dfd

**Chuyển từ:** docs/03-ky-thuat/dfd.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="84-nhật-ký"></a>

### 8.4. Nhật ký

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 04/11/2026 | Tạo DFD 0.1 gồm ngữ cảnh mức 0, nhóm/kho mức 1 và ba phân rã mức 2 với từ điển dữ liệu, bảng cân bằng, truy vết. Dựa LIFE-P01 vé ký 24 giờ, giữ các quyết định khác và hướng dẫn thực hành. | Được giao bổ sung phạm vi DFD; từng sơ đồ/toàn TDD còn dự thảo, chưa phần mềm hoặc phép kiểm thực hành. |
| 04/11/2026 | Bản 0.2 gom luồng DB mức 1 thành gói, giữ D1–D5 trong nhóm trình bày; thêm từ điển 4.2/bao phủ 4.3 và đồng bộ bảng cân bằng mức 2. Giữ cấu trúc/dữ liệu mức 0–2, LIFE-P01 và các quyết định nguồn; rút gọn nhãn để hiển thị rõ, chi tiết vẫn ở từ điển/phần giải thích. | Hướng tinh gọn được người dùng xác nhận; từng hình/toàn TDD chưa duyệt, không code/config/DB/test/Git/GitHub/triển khai. |
| 04/11/2026 | Bản 0.3 sửa đầu mút mức 1 về đúng D1–D5/F1/F2/T1; không cạnh tới DBS/khung, dùng ký hiệu kho cùng ID ghi lặp theo cụm. Thay từ điển/bảng cân bằng về luồng trực tiếp, xuất lại SVG từ nguồn duy nhất. | Chỉ chỉnh DFD/tài liệu; LIFE-P01/nghiệp vụ/thực hành giữ nguyên; từng hình/toàn TDD vẫn dự thảo. |

**Dấu vết 0.2 — không áp dụng hiện hành:** gói S06 từng gom S01/S02; R23/R24 gom R01–R09/R22; R25/R26 gom R10–R21; T11/T12 gom T03–T08; A10/A11 gom A03–A09; M15/M16 gom M01–M10 với đầu mút ở DBS. Nay giữ các mã thành phần và nối trực tiếp từng kho đúng chiều; S03 vẫn là ghi phiên vào D1. Không giữ hình/SVG 0.2 như bản hiện hành.

<a id="journal-tdd"></a>

## Phần lưu: journal-tdd

**Chuyển từ:** docs/03-ky-thuat/TDD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 03/11/2026 | Tạo TDD 0.1 lượt 1: tổng thể/module/ba lớp/ví dụ/API-quyền/MySQL-job/audio-file/Windows-Docker-ngrok/CI-CD và truy vết. Đầu vào TECH-03/DATA-01/G03 đã xác nhận riêng. | Toàn bộ giải pháp kiến trúc mới là dự thảo để đọc/góp ý; chưa code/cấu hình/API/ngrok/test/CI-CD/Git hoặc thao tác demo. |
| 03/11/2026 | Bản 0.2 ghi TECH-04/DATA-02, mở rộng CD-L01 Windows chưa chọn; thêm ERD/định danh/khóa/vùng-nháp/text-audio-cặp/job/phiên-lượt/lịch sử xóa, tab/Blob và backup cục bộ cùng câu hỏi phân loại. | Hướng đã xác nhận khác ERD/cơ chế dự thảo; chưa SQL/code/migrations/API hoàn chỉnh/test/CD hoặc dịch vụ. Giữ nhật ký 0.1 và phần chưa kiểm. |
| 03/11/2026 | Bản 0.3 ghi TECH-05/DATA-03: CD-L01/phạm vi-nhịp backup đã chọn, sửa ví dụ B còn hạn. Tách API/giao dịch 0.1 thành phần TDD; cập nhật opening/grant/receipt để khép P06/ACK/unknown-result. | TDD/ERD/API vẫn dự thảo; chưa SSH/Docker/DB/API/test/CI-CD/restore hoặc duyệt toàn tài liệu. |
| 03/11/2026 | Bản 0.4 hợp nhất ERD tại data-model 0.1, giữ mô hình/đề xuất backup 0.3 trong lịch sử; API 0.2 có DTO/ví dụ, operations 0.1 có lifecycle/state/BKP-P01/REC-P01/ma trận V-*. | Hạn/hành vi mới chưa duyệt; chưa schema/API/test/backup/restore/SSH/Docker/CD hoặc toàn TDD được duyệt. |
| 04/11/2026 | Bản 0.5 đồng bộ DATA-04/mô hình 0.2/API 0.3/operations 0.2; LIFE-P01 bỏ context/10 phút khỏi mặc định và đề xuất ticket/generation, BKP-P02 stop phục vụ thay online. | Các xác nhận đúng phạm vi không duyệt toàn TDD; chưa code/config/DB/test/SSH/backup/restore/Git/GitHub/triển khai. |
| 04/11/2026 | Bản 0.6 theo DATA-05 chốt vé ký 24 giờ/ledger, bỏ start_generation/gate với purge; dẫn model 0.3/API 0.4/operations 0.3/ma trận kiểm và thực hành 01 dự thảo. | Không toàn TDD được duyệt; chưa code/config/DB/test/Git/GitHub/triển khai hoặc phép kiểm thực hành. |
| 04/11/2026 | Bản 0.7 dẫn DFD 0.1 tại một vị trí chính: ngữ cảnh/mức 1 và ba phân rã Bắt đầu/TTS/áp dụng cặp; thêm từ điển kho TAB–DB–FILE, cân bằng biên và truy vết. | Chỉ phạm vi bổ sung DFD được giao; từng sơ đồ/toàn TDD còn dự thảo, LIFE-P01/DATA-04 và hướng dẫn thực hành giữ nguyên, chưa thực hành/kiểm phần mềm/Git/GitHub. |
| 04/11/2026 | Giữ mốc kiến trúc TDD 0.7; dẫn DFD 0.2 gom gói mức 1/từ điển/cân bằng, SVG xuất từ cùng nguồn và kiểm hiển thị cục bộ. Đồng bộ nhãn TDD trong thực hành 01, giữ nguyên các bước. | Hướng tinh gọn đã xác nhận, không duyệt từng hình/toàn TDD hoặc chứng minh phần mềm; không code/config/DB/test/Git/GitHub. |
| 04/11/2026 | Dẫn DFD 0.3 sửa đầu mút về từng kho, dùng ký hiệu lặp cùng ID/các cụm; cập nhật từ điển/cân bằng và thay SVG cùng nguồn. TDD giữ mốc kiến trúc 0.7. | Chỉ trình bày tài liệu, không đổi nghiệp vụ/LIFE-P01 hoặc bước thực hành; kiểm hình không là kiểm thử phần mềm. |

<a id="journal-api"></a>

## Phần lưu: journal-api

**Chuyển từ:** docs/03-ky-thuat/api-transactions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 03/11/2026 | Tạo API/giao dịch 0.1: 10 hợp đồng khách (K10 cụ thể hóa quay bản đồ theo D04), 30 mã Admin, quyền/precondition/receipt/ACK-grant/TX/race/error/truy vết; ghi bổ sung kỹ thuật tối thiểu so với ERD 0.2. | Toàn bộ hợp đồng/path/DTO/giải pháp mới dự thảo, chưa OpenAPI thực thi/code/DB/test/CD hoặc duyệt toàn TDD. |
| 03/11/2026 | Bản 0.2 dẫn mô hình hiện hành và operations, thêm DTO/ví dụ K01/K03/K06–K08/A18–A24/A11; K11/A31 là ngữ cảnh kỹ thuật chống replay sau dọn, không FR/màn hình mới. | LIFE-P01–P03/REC-P01 chưa duyệt, chưa API/OpenAPI/schema/test/SSH/CD chạy. |
| 04/11/2026 | Bản 0.3 đồng bộ DATA-04: LIFE-P02/P03/REC-P01 đã duyệt, BKP-P02 stop thay online; K11 cấp ticket không bảng context/TTL mới, StartRequest/TX-D01 dùng generation đề xuất. | Giữ mã nghiệp vụ/API, path context chỉ lịch sử; chưa API/schema/test/SSH/backup/CD hoặc duyệt toàn TDD. |
| 04/11/2026 | Bản 0.4 theo DATA-05: K11 vé 24 giờ, K01 tra ledger sau kiểm signature/quyền/epoch rồi phân nhánh expiry, TX-D01 bỏ generation/gate. Giữ mã K01/K11/TX-K01/TX-D01, error mã cũ chỉ lịch sử. | Chưa API/OpenAPI/schema/test thực; không duyệt toàn TDD hoặc đổi Admin JWT/REC/BKP. |

<a id="journal-model"></a>

## Phần lưu: journal-model

**Chuyển từ:** docs/03-ky-thuat/data-model.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 03/11/2026 | Tạo mô hình hiện hành 0.1 cho TDD 0.4/API 0.2; hợp nhất nội dung/session-opening-grant-view/window-receipt-job-file, giữ bản cũ riêng để truy vết. | Toàn bộ mapping mới dự thảo; TTL/restore mới chờ xác nhận, chưa kiểm MySQL hoặc triển khai. |
| 04/11/2026 | Bản 0.2 cập nhật DATA-04; bỏ bảng context/TTL 10 phút khỏi mô hình hiện hành, đề xuất ticket/generation, giữ LIFE/REC đã chốt và BKP-P02 stop phục vụ. | Không schema/database/test thực; số thế hệ/khóa ký và stop/drain phải kiểm, không duyệt toàn TDD. |
| 04/11/2026 | Bản 0.3 đồng bộ DATA-05: bỏ cột/hook start_generation, thêm issued/expires/digest vé cùng ledger session; OPS_GUARD chỉ giữ bảo trì, service_epoch giữ REC-P01. | Không schema/test thực, không duyệt toàn ERD/TDD; bản generation giữ lịch sử. |

<a id="journal-operations"></a>

## Phần lưu: journal-operations

**Chuyển từ:** docs/03-ky-thuat/operations-design.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 03/11/2026 | Soạn vòng đời context/opening/grant/window/receipt/job/claim, BKP-P01/REC-P01 và ma trận V-* để thực hiện sau. | Hạn mới/hành vi restore chờ xác nhận, chưa phép kiểm nào được chạy hoặc kết luận đạt. |
| 04/11/2026 | DATA-04 ghi LIFE-P02/P03/REC-P01 đã duyệt, BKP-P02 downtime thay BKP-P01 online; LIFE-P01 hướng đơn giản, ticket/generation đề xuất bỏ context/10 phút, đồng bộ ma trận kiểm. | Không duyệt toàn TDD/API hoặc kiểm thực; giữ dấu vết ở lịch sử và không code/config/DB/test/Git/GitHub/triển khai. |
| 04/11/2026 | DATA-05 chốt LIFE-P01 vé ký 24 giờ/tra ledger trước nhánh expiry, bỏ start_generation/purge gate; thêm ma trận V-START01.01–10, giữ LIFE-P02/P03/REC/BKP-P02. | Chưa ca nào chạy/đạt hoặc toàn TDD được duyệt; lịch sử generation giữ riêng, không code/config/DB/test/Git/GitHub. |

<a id="journal-practice"></a>

## Phần lưu: journal-practice

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

| Ngày | Thay đổi | Trạng thái |
| --- | --- | --- |
| 04/11/2026 | Soạn chuẩn bị thực hành đầu tiên cho nền dữ liệu/Bắt đầu phiên theo DATA-05, gồm kiểm môi trường người dùng tự làm, các chặng và ôn tập/bằng chứng. | Dự thảo hướng dẫn, chưa command/tool môi trường hoặc code/config/DB/test/Git thực hiện; không phần mềm đạt. |
| 04/11/2026 | Đồng bộ nhãn Đọc trước từ TDD 0.6 sang bản hiện hành 0.7 sau tinh gọn DFD; hướng dẫn giữ mốc 0.1 và toàn bộ bước/câu ôn tập/checklist. | Chỉ dẫn chiếu, chưa thực hiện lệnh hoặc phần mềm. |
| 04/11/2026 | Ghi đầu ra java/javac/Docker CLI/Compose/WSL người dùng cung cấp; đối chiếu Compose v5 với nguồn chính thức và chuẩn bị duy nhất bước docker info kiểm Engine/Linux. | Ghi nhận bằng chứng do người dùng thực hiện, chưa tự chạy lệnh môi trường/ứng dụng hoặc cài/đổi cấu hình; P01-ENV còn một phần. |
| 04/11/2026 | Ghi Docker Engine 29.8.1/OSType=linux do người dùng tự kiểm; chuẩn bị đúng bước tạo/tải ZIP backend bằng Initializr sau kiểm nguồn Boot 4.1.2/metadata. | Engine có bằng chứng, skeleton/build/MySQL/test chưa thực hiện; không trợ lý chạy công cụ dự án, sinh code, cấu hình hay Git/GitHub thay người dùng. |
| 04/11/2026 | Đọc backend.zip người dùng cung cấp: Boot/Java/package và dependency; ghi khác biệt Maven 3.10.0 và mysql:latest so với mốc. Hướng dẫn một bước giải nén có chặn đích tồn tại. | Chỉ đọc archive/cập nhật tài liệu; chưa giải nén, chỉnh cấu hình, chạy Wrapper/build/test hoặc thay mốc công nghệ. |
| 04/11/2026 | Người dùng báo giải nén bị chặn; đọc backend hiện có, đối chiếu đủ 12 file khớp ZIP. Chuyển bước hướng dẫn sang người dùng chỉnh distributionUrl về Maven 3.9.12. | Không kết luận lần giải nén vừa báo đã thành công, không xóa/ghi đè, sửa code/config hoặc chạy Wrapper/build/test thay người dùng. |

<a id="practice-skeleton-history"></a>

## Phần lưu: practice-skeleton-history

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="practice-initializr"></a>

<a id="30-bước-người-dùng-thực-hiện-tiếp--tạo-và-tải-bộ-khung-backend"></a>

### 3.0. Bước người dùng thực hiện tiếp — tạo và tải bộ khung backend

Một công việc trong chặng P01-BASE: mở [Spring Initializr](https://start.spring.io/), điền cấu hình dưới đây và bấm Generate để tải ZIP. **Chưa giải nén vào repo, chưa chạy build hoặc ứng dụng**; sau khi có file sẽ hướng dẫn kiểm bộ khung và đặt đúng thư mục backend, tránh ghi đè/nesting thư mục. Trợ lý không gọi endpoint tạo dự án hoặc tải/sinh code thay người dùng.

| Mục trên trang | Giá trị hướng dẫn |
| --- | --- |
| Project / Language | Maven / Java |
| Spring Boot | 4.1.2 stable; không chọn SNAPSHOT/RC hoặc nhánh khác thay thế |
| Group / Artifact | vn.hueqr / backend |
| Name / Package name | backend / vn.hueqr.backend |
| Description | Hue QR audio guide backend |
| Packaging / Java | Jar / 21 |
| Dependencies | Spring Web; Validation; Spring Data JPA; MySQL Driver; Flyway Migration; Testcontainers |

Các tên Group/Artifact/package là lựa chọn đặt tên kỹ thuật cho skeleton mới của repo chính; không lấy package/code demo, không thay nghiệp vụ hoặc tạo thêm ứng dụng Backend. Group là tiền tố định danh Maven/Java, không tuyên bố sở hữu tên miền.

Vai trò sáu dependency: Web nhận/trả API; Validation kiểm dữ liệu đầu vào; JPA truy cập dữ liệu qua Repository; MySQL Driver kết nối MySQL; Flyway quản lý migration; Testcontainers phục vụ kiểm tích hợp database sau này. Lưu ZIP chưa là build/test thành công; chưa cấu hình datasource nên không hướng dẫn bấm chạy ứng dụng ngay. Security/session-cookie, OpenAPI và các phần còn lại giữ nguyên TECH-01, bổ sung/hướng dẫn ở chặng tương ứng, không bị loại khỏi phạm vi.

**Nguồn đọc ngày 04/11/2026:** [Spring công bố Boot 4.1.2](https://spring.io/blog/2026/10/23/spring-boot-4-1-2-available-now/); [metadata Spring Initializr](https://start.spring.io/metadata/client) có Boot 4.1.2, Maven, Java 21, Jar và các dependency nêu trên; [parent POM 4.1.2 tại Maven Central](https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/4.1.2/spring-boot-starter-parent-4.1.2.pom) truy cập được. Đây là kiểm khả dụng nguồn, không resolve/build tương thích thực tế.

Maven Wrapper vẫn phải đối chiếu mốc 3.9.12 sau khi có ZIP; không khẳng định Initializr tự sinh đúng mốc đó hoặc tự thay bằng Maven global. Nếu trang thực tế không cho chọn 4.1.2/dependency thì người dùng báo lại, không tự chọn phiên bản khác. Kết quả cần báo cho bước này: đã tải ZIP tên gì; chưa coi file đã có trước phản hồi người dùng.

<a id="301-zip-đã-nhận-và-kiểm-tra-chỉ-đọc--04112026"></a>

### 3.0.1. ZIP đã nhận và kiểm tra chỉ đọc — 04/11/2026

Người dùng cung cấp `C:\Users\khait\Downloads\backend.zip` (17.611 byte). Đã đọc danh sách entry, pom.xml, cấu hình Wrapper và test trực tiếp trong archive; không giải nén hoặc thực thi nội dung thay người dùng. Các mô tả 3.0 ở trên giữ làm hướng dẫn tạo ZIP; tại lượt nhận ZIP chưa có backend trong repo. **Cập nhật hiện trạng ở 3.0.2:** backend đã tồn tại.

| Nội dung kiểm | Kết quả |
| --- | --- |
| Cấu trúc | Có một thư mục gốc backend/; có mvnw/mvnw.cmd, .mvn/wrapper và src/main, src/test |
| Backend | Parent Boot 4.1.2, java.version=21, groupId=vn.hueqr, artifactId=backend, package vn.hueqr.backend |
| Thành phần | Có Web MVC, Validation, JPA, Flyway và flyway-mysql, MySQL Connector/J, Testcontainers/MySQL cùng thành phần test; chưa resolve hoặc build |
| Maven được Wrapper tải | **3.10.0**, khác mốc đã chọn **3.9.12**; wrapperVersion=3.3.4 là phiên bản công cụ Wrapper, không phải Maven. Cần hướng dẫn người dùng chỉnh distributionUrl trước lần chạy Wrapper đầu tiên; chưa sửa code/config hoặc đổi mốc đã chọn |
| Database của test | TestcontainersConfiguration dùng **mysql:latest**, cần thay bằng mốc **mysql:8.4.13** đã chọn và kiểm khả dụng thực tế ở bước tương ứng trước test; chưa kéo image/chạy test |
| Thông tin hiển thị POM | name/description đang trống, khác hướng dẫn điền form; không phải sai group/artifact/package hoặc lý do tải lại ZIP. Có thể bổ sung khi chỉnh cấu hình, chưa tự sửa |

**Hướng dẫn giải nén đã giao ở lượt trước — không chạy lại khi backend đã tồn tại:** giải nén ZIP vào repo để có backend/pom.xml, không thành backend/backend/pom.xml. Kiểm kê lúc giao lệnh chưa có backend; lệnh chặn nếu đích đã tồn tại, không dùng -Force:

```powershell
if (Test-Path -LiteralPath 'C:\Study\Code\VS Code\Thuyet-minh-tu-dong-QR-main\backend') {
    Write-Error 'Thư mục backend đã tồn tại; dừng và báo lại, không ghi đè.'
} else {
    Expand-Archive -LiteralPath 'C:\Users\khait\Downloads\backend.zip' -DestinationPath 'C:\Study\Code\VS Code\Thuyet-minh-tu-dong-QR-main'
}
```

Sau đó báo kết quả giải nén hoặc lỗi; chưa chạy Wrapper/build/test/ứng dụng. Trợ lý không thực hiện lệnh trên thay người dùng. Có ZIP không chứng minh đã build được, chạy MySQL hoặc hoàn thành toàn mục GitHub.

<a id="practice-wrapper-pin"></a>

<a id="302-backend-đã-tồn-tại--chỉnh-mốc-maven-trước-khi-chạy-wrapper"></a>

### 3.0.2. Backend đã tồn tại — chỉnh mốc Maven trước khi chạy Wrapper

Người dùng báo lệnh giải nén bị chặn vì đích đã tồn tại. Kiểm tra chỉ đọc xác nhận backend/pom.xml ở đúng cấp repo, đủ 12 file trong ZIP và nội dung SHA-256 từng file khớp archive. Không suy ra lần chạy vừa báo đã giải nén thành công hoặc thời điểm/ai đã tạo thư mục trước đó. Không xóa, ghi đè hay yêu cầu giải nén lại; tiếp tục từ bộ khung hiện có. Chưa có bằng chứng build/test.

**Một bước tiếp theo người dùng tự làm:** mở backend/.mvn/wrapper/maven-wrapper.properties, chỉ thay dòng distributionUrl bằng:

```properties
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.12/apache-maven-3.9.12-bin.zip
```

Giữ nguyên wrapperVersion=3.3.4 và distributionType=only-script. wrapperVersion là phiên bản công cụ Wrapper; distributionUrl quyết định Maven sẽ được tải/chạy. Lưu file rồi gửi dòng distributionUrl đã lưu; chưa chạy mvnw/build/test. Đây là đưa cấu hình sinh sẵn về mốc 3.9.12 đã chọn, không đổi quyết định công nghệ hoặc chứng minh Maven tải/chạy được. mysql:latest vẫn cần chỉnh ở bước tiếp theo trước test, chưa sửa thay người dùng.

<a id="roadmap-old-reminder"></a>

## Phần lưu: roadmap-old-reminder

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<details>
<summary>Lịch sử lời nhắc trước khi T07 đã được người dùng tích</summary>

<a id="62-mục-đủ-căn-cứ-để-nhắc-tích-nguyên-văn"></a>

### 6.2. Mục đủ căn cứ để nhắc tích nguyên văn

Bạn sang issue T07 — [TTS: Khảo sát và tích hợp tạo audio từ văn bản (Thành), #55](https://github.com/khaithanh2726-beep/Thuyet-minh-tu-dong-QR-main/issues/55), tích mục **“Khảo sát có nguồn/bằng chứng về vi/en/fr/ru, giọng, chi phí và giới hạn; trình phương án để người dùng chọn.”** Căn cứ hoàn thành: technical-inputs mục 3 có nguồn chính thức/ngày/giới hạn; TTS-01 chốt model/Charon, TECH-03 chọn định dạng/đầu vào. Đây là **khảo sát tài liệu và lựa chọn**, chưa chứng minh API/quota tài khoản hoặc chất lượng đủ 20 cặp.

Không nhắc tích toàn mục T01 “Chốt công nghệ, môi trường trình diễn và điều kiện/ngưỡng đánh giá cần thiết” hoặc T10 “Thống nhất môi trường demo và ràng buộc triển khai để T01 hoàn thiện thiết kế”: mới chốt đầu vào/chọn thử, chưa xong kiểm môi trường/kênh CD. T01 mục chính sách mới rõ phần hạn/điều kiện/restore, còn nơi/nhịp/phạm vi/quyền cụ thể; TDD mới 0.1/roadmap còn dự kiến, không tích toàn các mục này.

<a id="root-readme-before"></a>

## Phần lưu: root-readme-before

**Chuyển từ:** README.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="thuyet-minh-tu-dong-qr-main"></a>

# Thuyet-minh-tu-dong-QR-main

Đồ án học phần **Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế**: khách chọn vùng trên bản đồ để đọc/nghe; Admin quản lý địa điểm, nội dung/audio, QR và xem phiên/thống kê.

**Tài liệu hiện hành 04/11/2026:** [DATA-05/LIFE-P01](../02-nghiep-vu/decisions.md#data-05) chốt vé Bắt đầu có chữ ký 24 giờ và chống lặp cùng phiên; bỏ context/10 phút/start_generation, giữ service_epoch cùng DATA-04. [TDD 0.7](../03-ky-thuat/TDD.md)/[model 0.3](../03-ky-thuat/data-model.md)/[API 0.4](../03-ky-thuat/api-transactions.md)/[operations 0.3](../03-ky-thuat/operations-design.md) vẫn dự thảo. PRD 0.8.8/UI 0.7.8/đầu vào 0.7/roadmap 0.10 đồng bộ đúng phần. [Hướng dẫn thực hành đầu tiên 0.1](../03-ky-thuat/practice-01-start-session.md) mới chuẩn bị để người dùng tự thực hiện; chưa code/test/SSH/backup/restore/CD hoặc duyệt toàn TDD.

Bắt đầu đọc tại **[mục lục tài liệu](../README.md)**; kiểm tra phiên bản/trạng thái và đường dẫn mới khi bàn giao giữa hai chat.

<a id="khung-toàn-dự-án--định-hướng-không-phải-trạng-thái-triển-khai"></a>

## Khung toàn dự án — định hướng, không phải trạng thái triển khai

Khung giữ mốc tổ chức 01/11; ngày 02/11 bổ sung roadmap, cập nhật hồ sơ tiến độ và lựa chọn TECH-01; chưa tạo thành phần ứng dụng. **[MỚI 02/11]** là file/thư mục chứa backlog được tạo trong lượt kiểm kê.

**[CÓ]** đã tồn tại trước lượt này; **[TẠO]** tạo trong lượt 01/11; **[CHUYỂN]** nội dung được di chuyển trong lượt này; **[DỰ KIẾN]** chưa tạo. Với thư mục dự kiến, toàn bộ phần bên trong cũng dự kiến.

```text
Thuyet-minh-tu-dong-QR-main/
├── README.md                         [CÓ, cập nhật lượt này]
├── .gitignore                        [CÓ, giữ nguyên]
├── .env.example                      [DỰ KIẾN, chỉ khi cần cấu hình chung]
├── docs/                             [CÓ]
│   ├── README.md                     [TẠO, mục lục]
│   ├── 01-tong-quan/                  [MỚI 02/11]
│   │   ├── roadmap.md                [0.10: DATA-05/thiết kế chưa kiểm]
│   │   └── technical-inputs.md       [0.7: DATA-05/phần chưa kiểm]
│   ├── 02-nghiep-vu/                  [TẠO để chứa tài liệu CHUYỂN]
│   │   ├── business-flow.md          [CHUYỂN]
│   │   ├── PRD.md                    [CHUYỂN]
│   │   ├── decisions.md              [CHUYỂN]
│   │   └── prd-review.md             [CHUYỂN]
│   ├── 03-ky-thuat/                   [MỚI 03/11: tài liệu]
│   │   ├── TDD.md                    [0.7: đường đọc thiết kế]
│   │   ├── dfd.md                    [0.3: một nguồn DFD mức 0–2]
│   │   ├── dfd-level1.svg            [bản xuất cùng nguồn, không sửa riêng]
│   │   ├── data-model.md             [0.3: mô hình hiện hành dự thảo]
│   │   ├── api-transactions.md       [0.4: API/DTO/giao dịch dự thảo]
│   │   ├── operations-design.md      [0.3: lifecycle/backup stop/kiểm chứng]
│   │   ├── data-model-history.md     [lịch sử 0.3–0.5, không áp dụng]
│   │   ├── practice-01-start-session.md [MỚI 04/11: hướng dẫn, chưa thực hiện]
│   │   └── diagrams/                 [DỰ KIẾN, chưa tạo]
│   ├── 04-giao-dien/                  [TẠO để chứa tài liệu CHUYỂN]
│   │   ├── ui-design.md              [CHUYỂN]
│   │   ├── ui-design-history.md      [CHUYỂN]
│   │   └── design-assets/            [CHUYỂN, gồm ảnh và hồ sơ nguồn]
│   ├── 05-noi-dung/                   [DỰ KIẾN]
│   ├── 06-kiem-thu/                   [DỰ KIẾN]
│   └── 07-van-hanh/                   [DỰ KIẾN]
├── frontend/                         [DỰ KIẾN]
│   ├── README.md
│   ├── src/
│   ├── public/
│   ├── tests/
│   └── .env.example
├── backend/                          [DỰ KIẾN]
│   ├── README.md
│   ├── src/
│   ├── tests/
│   ├── database/
│   │   ├── migrations/
│   │   └── seeds/
│   └── .env.example
├── deploy/                           [DỰ KIẾN]
└── .github/                          [DỰ KIẾN]
    └── workflows/
```

Khung này chỉ tổ chức dự án; công nghệ được chọn riêng theo TECH-01. TDD 0.7/model/API/operations có ERD/DTO/lifecycle/backup stop/cách kiểm dự thảo; [DFD 0.3](../03-ky-thuat/dfd.md) giữ năm sơ đồ mức 0–2, nối mức 1 tới đúng kho và dùng ký hiệu kho lặp theo cụm; vẫn phân biệt TAB–DB–FILE; SVG xuất từ cùng nguồn phục vụ xem/báo cáo; DATA-05 chốt vé ký 24 giờ, tra lại cùng phiên và không start_generation. Hướng dẫn thực hành đã soạn, mọi cơ chế vẫn cần triển khai/kiểm chứng, chưa có ứng dụng. **Đợt 01/11 chỉ tạo docs/02-nghiep-vu, docs/04-giao-dien và docs/README.md**, chuyển tài liệu/tài sản đã có và cập nhật README gốc; không tạo các phần dự kiến.

- `05-noi-dung/`: bài thuyết minh, bản dịch, nguồn và hồ sơ duyệt.
- `06-kiem-thu/`: kết quả/bằng chứng kiểm thử; mã kiểm thử đặt cùng frontend/backend.
- `07-van-hanh/`: hướng dẫn chạy và triển khai; `deploy/` và `.github/workflows/` dành cho cấu hình thực thi khi được giao sau này.
- Vị trí audio sinh ra/file tải lên/dữ liệu chạy chờ TDD, không mặc định đưa vào Git. `.gitignore` gốc áp dụng toàn repo; chỉ bổ sung quy tắc riêng theo thư mục khi thật sự cần. Không sửa file này hoặc tạo file môi trường trong lượt tổ chức tài liệu.

<a id="docs-index-before"></a>

## Phần lưu: docs-index-before

**Chuyển từ:** docs/README.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="mục-lục-tài-liệu-dự-án"></a>

# Mục lục tài liệu dự án

**Cập nhật mục lục:** 04/11/2026 (đợt sắp xếp đường dẫn: 01/11/2026), repo chính C:\Study\Code\VS Code\Thuyet-minh-tu-dong-QR-main.  
**Phạm vi mục lục:** [DATA-05/LIFE-P01](../02-nghiep-vu/decisions.md#data-05) chốt vé Bắt đầu có chữ ký 24 giờ, chống lặp cùng phiên; bỏ start context/10 phút/start_generation. Giữ service_epoch và DATA-04/LIFE-P02/P03/REC-P01/BKP-P02. TDD 0.7/model 0.3/API 0.4/operations 0.3, DFD 0.3 và hướng dẫn thực hành vẫn dự thảo; chưa chạy phần mềm hoặc duyệt toàn TDD.
**Backlog hiện hành:** [roadmap 0.10](../01-tong-quan/roadmap.md) giữ 11 task/G02, thay đổi local chưa gửi GitHub. Lượt này không đọc/thay issue/Project/checkbox; mục tiêu 17/11 và lịch vẫn có giả định/rủi ro.

Khung tại [README gốc](../../README.md). Đọc [DATA-05](../02-nghiep-vu/decisions.md#data-05) → [TDD 0.7](../03-ky-thuat/TDD.md) → [model 0.3](../03-ky-thuat/data-model.md) → [API/DTO 0.4](../03-ky-thuat/api-transactions.md#api-dto) → [operations 0.3](../03-ky-thuat/operations-design.md). Chỉ bản hiện hành dùng hướng dẫn triển khai; cơ chế context/10 phút/start_generation và backup online nằm trong lịch sử.

<a id="1-tài-liệu-hiện-hành-vai-trò-và-trạng-thái"></a>

## 1. Tài liệu hiện hành, vai trò và trạng thái

| Tài liệu | Vai trò / cách dùng | Phiên bản và trạng thái kiểm tra thực tế |
| --- | --- | --- |
| [01-tong-quan/roadmap.md](../01-tong-quan/roadmap.md) | 11 task/tiến độ/G02/lịch | **0.10**, DATA-05; hướng dẫn thực hành đã soạn, chưa thực hiện hoặc hoàn thành mục triển khai; local chưa gửi GitHub. |
| [01-tong-quan/technical-inputs.md](../01-tong-quan/technical-inputs.md) | Đầu vào/nguồn/phần kiểm | **0.7**, đồng bộ DATA-05; giữ khảo sát cũ và phần chưa kiểm, không gọi TTS mới. |
| [02-nghiep-vu/business-flow.md](../02-nghiep-vu/business-flow.md) | Luồng khách/Admin và ngoại lệ | Đã duyệt nghiệp vụ BF-K01/BF-A01/BF-ALL01 và bổ sung UI-01–UI-05 đúng phần; giữ cặp/gate/hồ sơ và D03/P01/UI-02, đồng bộ box phiên/hai bảng/QR. |
| [02-nghiep-vu/decisions.md](../02-nghiep-vu/decisions.md) | Xác nhận/phần mở/lịch sử | **DATA-05 mục 3.16**, nguồn **8.1.18**, lịch sử **7.13**; LIFE-P01 vé 24 giờ đã chốt, DATA-04 phần khác giữ nguyên. |
| [02-nghiep-vu/PRD.md](../02-nghiep-vu/PRD.md) | Yêu cầu và truy vết | **0.8.8**, đồng bộ FR-K02/UC-K01/AC-K03 và phần 8.3.2 theo DATA-05; không duyệt toàn bộ thiết kế kỹ thuật. |
| [02-nghiep-vu/prd-review.md](../02-nghiep-vu/prd-review.md) | Rà soát/phụ thuộc | **7.17**, DATA-05 đúng phạm vi; R06 vẫn mở phần khác, chưa kiểm phần mềm. |
| [03-ky-thuat/TDD.md](../03-ky-thuat/TDD.md) | Kiến trúc và đường đọc | **0.7 DỰ THẢO**, dẫn DFD 0.3 nối đúng kho; LIFE-P01 đã chốt đúng phần, không duyệt từng sơ đồ/toàn TDD hoặc kiểm phần mềm. |
| [03-ky-thuat/dfd.md](../03-ky-thuat/dfd.md) | Vị trí chính DFD: ngữ cảnh 0, mức 1 và ba phân rã mức 2 | **0.3 DỰ THẢO**, mức 1 nối từng kho có ID/ký hiệu lặp, từ điển/cân bằng trực tiếp; [SVG cùng nguồn](../03-ky-thuat/dfd-level1.svg) xuất lại và xem trực quan theo cụm. Không cam kết nguyên hình vừa A4/A3 hoặc duyệt từng hình. |
| [03-ky-thuat/api-transactions.md](../03-ky-thuat/api-transactions.md) | API/DTO/quyền/lặp/giao dịch | **0.4 DỰ THẢO**, K11/K01 vé ký 24 giờ; tra kết quả đã tạo trước khi xét hạn tạo lần đầu, không start_generation; chưa API chạy. |
| [03-ky-thuat/data-model.md](../03-ky-thuat/data-model.md) | **Vị trí chính mô hình hiện hành** | **0.3 DỰ THẢO**, mã yêu cầu và thông tin đối chiếu đi cùng phiên; không bảng context/generation phối hợp dọn phiên. |
| [03-ky-thuat/operations-design.md](../03-ky-thuat/operations-design.md) | Vòng đời/job/backup/restore/kiểm V-* | **0.3 DỰ THẢO**, LIFE-P01 vé 24 giờ; [ma trận mười nhóm V-START01](../03-ky-thuat/operations-design.md#ops-start-checks) mới chuẩn bị, chưa chạy. DATA-04/backup không đổi. |
| [03-ky-thuat/practice-01-start-session.md](../03-ky-thuat/practice-01-start-session.md) | Hướng dẫn thực hành đầu tiên cho người dùng tự làm | **0.1 DỰ THẢO**, có Engine Linux và backend ở đúng cấp repo, 12 file khớp ZIP. Mục 3.0.2 hướng dẫn chỉnh Maven về mốc đã chọn; chưa chạy Wrapper/build/MySQL/test, MySQL test còn latest. |
| [03-ky-thuat/data-model-history.md](data-model-history.md) | Lịch sử mô hình/operations | Giữ phương án 0.3–0.5, context/10 phút/BKP-P01/start_generation; **không áp dụng hiện hành**. |
| [04-giao-dien/ui-design.md](../04-giao-dien/ui-design.md) | Hướng UI/phụ thuộc | **0.7.8**, DATA-05: vé xử lý nền, giữ yêu cầu chưa rõ kết quả; không thêm bước/đồng hồ hoặc thay đồ họa/ranh giới. |
| [04-giao-dien/ui-design-history.md](ui-design-history.md) | Lưu bản phác thảo 0.1 và dấu vết trước thay đổi | **Lịch sử**, không bản hiện hành/nguồn yêu cầu thứ hai. Giữ nội dung cũ; chỉ điều chỉnh đích liên kết khi chuyển. |
| [04-giao-dien/design-assets/README.md](../04-giao-dien/design-assets/README.md) | Nguồn/cách dựng/giới hạn ảnh, dữ liệu nền và vùng | Tài sản thiết kế 0.2 **đề xuất chưa duyệt nền/vị trí/ranh giới**; Cần Chánh way/285990343 là tham chiếu ngoài snapshot, không lấy mới/kiểm chứng lại ngày 01/11. |

**Phân biệt bắt buộc:** duyệt nghiệp vụ/yêu cầu không duyệt UI chi tiết/nền/ranh giới, TTS, nội dung hoặc kết quả phần mềm. Công nghệ nền chọn riêng theo TECH-01; việc chọn không chứng minh tương thích, triển khai hoặc kiểm thử. Năm bài vi vẫn bản nháp chờ người dùng cung cấp/rà soát, không có nội dung nào được duyệt sẵn qua việc sắp xếp.

<a id="2-thứ-tự-đọc-và-bàn-giao-giữa-hai-chat"></a>

## 2. Thứ tự đọc và bàn giao giữa hai chat

1. Đọc mục lục này và [Business Flow](../02-nghiep-vu/business-flow.md) để hiểu mục tiêu, phạm vi và luồng hai vai trò.
2. Đọc [chỉ mục quyết định](../02-nghiep-vu/decisions.md#dec-2) rồi quyết định hiện hành liên quan; lịch sử ở mục 7–8 chỉ dùng truy vết.
3. Đọc [PRD](../02-nghiep-vu/PRD.md): giữ mã/truy vết, phân biệt phần được duyệt và phần mở; đối chiếu [báo cáo rà soát](../02-nghiep-vu/prd-review.md#review-open-final).
4. Với giao diện, đọc [ui-design hiện hành](../04-giao-dien/ui-design.md), [phạm vi xác nhận và giới hạn UI](../04-giao-dien/ui-design.md#ui-proposals) và [hồ sơ nguồn bản đồ](../04-giao-dien/design-assets/README.md). Đọc [UI Admin lượt 1](../04-giao-dien/ui-design.md#ui-admin-round1) khi rà soát khung/địa điểm/vùng; phân biệt hướng UI-AP01–UI-AP03 đã xác nhận với chi tiết chưa duyệt. Đọc [lượt 2 hiện hành](../04-giao-dien/ui-design.md#ui-admin-round2)/UI-04, chỉ hướng được nêu rõ đã chốt. Đọc [lượt 3 hiện hành](../04-giao-dien/ui-design.md#ui-admin-round3)/UI-05, UI-AP08–UI-AP10 ở mục 12.6; chỉ hướng được nêu rõ đã chốt. UI-A21 là box, không trang riêng. Chỉ đọc lịch sử 0.1 khi cần đối chiếu thay thế.
5. Khi bàn giao, nêu **đường dẫn mới, phiên bản/trạng thái thực tế, quyết định đã chốt, phần còn mở và việc tiếp theo**. Chat nhận phải đọc lại file, không giả định cùng lịch sử hoặc cùng phiên bản; không coi đường dẫn cũ trong lời nhắn/nhật ký là bản hiện hành.

Các hướng đã chốt giữ theo TECH-01–05/DATA-01–05, không hỏi duyệt lại. Đã có kết quả phiên bản/WSL, [Engine Linux 29.8.1](../03-ky-thuat/practice-01-start-session.md#practice-docker-engine) và backend hiện có khớp ZIP. Lệnh giải nén người dùng vừa báo đã dừng để bảo vệ đích tồn tại, không cần chạy lại. **Bước tiếp theo:** người dùng tự [chỉnh distributionUrl về Maven 3.9.12](../03-ky-thuat/practice-01-start-session.md#practice-wrapper-pin), chưa chạy Wrapper/build/test. MySQL test còn cần chỉnh về mốc đã chọn trước khi chạy test.

<a id="21-đầu-vào-cho-bước-thiết-kế-kỹ-thuật"></a>

### 2.1. Đầu vào cho bước thiết kế kỹ thuật

Đọc TDD/model/API/operations theo mục lục và [DFD mức 0–2](../03-ky-thuat/dfd.md) để theo đường đi dữ liệu trước thực hành; [báo cáo 7.17](../02-nghiep-vu/prd-review.md#review-data-05) và [đầu vào hiện hành](../01-tong-quan/technical-inputs.md) phân biệt DATA-05 đã xác nhận với cơ chế chưa kiểm chứng. BKP-P01/context 10 phút/start_generation chỉ còn lịch sử. Kiểm phiên bản/trạng thái khi bàn giao, không giả hai chat cùng lịch sử hoặc tài liệu là phần mềm đã kiểm.

<a id="3-cấu-trúc-tài-liệu-kiểm-kê-03112026--giữ-mốc-di-chuyển-0111"></a>

## 3. Cấu trúc tài liệu kiểm kê 03/11/2026 — giữ mốc di chuyển 01/11

Cây dưới đây phản ánh docs/ sau kiểm kê; chú thích tạo/chuyển cũ thuộc đợt 01/11. Lượt backlog thêm roadmap; ngày 03/11 thêm technical-inputs và tạo đúng nhóm 03-ky-thuat để chứa TDD 0.1. Các nhóm ứng dụng/vận hành/nội dung/kiểm thử dự kiến khác chưa tạo:

```text
docs/
├── README.md                         # tạo 01/11, cập nhật mục lục
├── 01-tong-quan/                      # mới tạo 02/11 để chứa roadmap
│   ├── roadmap.md                     # 0.10: DATA-05/11 task local
│   └── technical-inputs.md            # 0.7: DATA-05/phụ thuộc chưa kiểm
├── 02-nghiep-vu/                      # mới tạo để chứa file di chuyển
│   ├── business-flow.md
│   ├── PRD.md
│   ├── decisions.md
│   └── prd-review.md
├── 03-ky-thuat/                       # mới 03/11, chỉ tài liệu
│   ├── TDD.md                         # 0.7 đường đọc hiện hành
│   ├── dfd.md                         # 0.3 dự thảo: một nguồn DFD
│   ├── dfd-level1.svg                 # mới: bản xuất cùng nguồn, không sửa riêng
│   ├── data-model.md                  # 0.3 mô hình hiện hành duy nhất
│   ├── api-transactions.md            # 0.4 API/DTO/giao dịch
│   ├── operations-design.md           # 0.3 lifecycle/backup stop phục vụ/kiểm chứng
│   ├── data-model-history.md          # lịch sử 0.3–0.5, không áp dụng
│   └── practice-01-start-session.md   # backend đã có; hướng dẫn chỉnh Maven về mốc
└── 04-giao-dien/                      # mới tạo để chứa file di chuyển
    ├── ui-design.md
    ├── ui-design-history.md
    └── design-assets/                # di chuyển nguyên thư mục
        ├── README.md
        ├── guest-visit-layout-v02.png
        ├── hue-five-regions-proposal.png
        ├── hue-five-regions-proposal.svg
        ├── hue-osm-source.json
        └── hue-touch-regions-proposal.geojson
```

Trước lượt này các file nằm trực tiếp dưới docs/ và docs/design-assets/. Có sẵn README gốc, .gitignore và .git/; giữ .gitignore/.git/ không chỉnh sửa. Không thấy AGENTS.md trong repo hoặc các thư mục cha đã kiểm tra; không phát hiện file tài liệu ngoài danh sách cần tự xử lý.

<a id="4-bảng-chuyển-đường-dẫn-thực-hiện"></a>

## 4. Bảng chuyển đường dẫn thực hiện

| Đường dẫn trước 01/11/2026 — chỉ còn là lịch sử | Đường dẫn hiện hành |
| --- | --- |
| docs/business-flow.md | [docs/02-nghiep-vu/business-flow.md](../02-nghiep-vu/business-flow.md) |
| docs/PRD.md | [docs/02-nghiep-vu/PRD.md](../02-nghiep-vu/PRD.md) |
| docs/decisions.md | [docs/02-nghiep-vu/decisions.md](../02-nghiep-vu/decisions.md) |
| docs/prd-review.md | [docs/02-nghiep-vu/prd-review.md](../02-nghiep-vu/prd-review.md) |
| docs/ui-design.md | [docs/04-giao-dien/ui-design.md](../04-giao-dien/ui-design.md) |
| docs/ui-design-history.md | [docs/04-giao-dien/ui-design-history.md](ui-design-history.md) |
| docs/design-assets/ | [docs/04-giao-dien/design-assets/](../04-giao-dien/design-assets/README.md) — đủ sáu tài sản/hồ sơ cũ |

Đường dẫn cũ được giữ khi mô tả căn cứ/lịch sử; các liên kết có thể bấm đã chuyển tới đích hiện hành. Không giữ bản sao hiện hành tại đường dẫn cũ hoặc tự tạo liên kết tượng trưng/redirect.

<a id="5-nhóm-dự-kiến--chưa-có-thư-mụcfile"></a>

## 5. Nhóm dự kiến — chưa có thư mục/file

| Vị trí định hướng | Mục đích khi được giao | Trạng thái lượt này |
| --- | --- | --- |
| 03-ky-thuat/diagrams/ | Tài sản sơ đồ tách file nếu cần | Chưa tạo; TDD.md/API đã có; Mermaid đặt trong Markdown, không tạo thư mục rỗng. |
| 05-noi-dung/ | Bài thuyết minh/bản dịch, nguồn và hồ sơ duyệt theo D09 | Chưa tạo, chưa nhận/rà soát năm bản vi. |
| 06-kiem-thu/ | Kết quả/bằng chứng kiểm thử | Chưa tạo; mã kiểm thử sẽ nằm cùng frontend/backend, không mặc định đã có kết quả. |
| 07-van-hanh/ | Hướng dẫn chạy/triển khai | Chưa tạo. |
| ../frontend/ | Mã nguồn, mã kiểm thử giao diện theo TECH-01 | Chưa tạo. |
| ../deploy/, ../.github/workflows/ | Cấu hình thực thi deploy/CI/CD khi được giao | Chưa tạo. |
| Các .env.example | Mẫu biến môi trường theo phạm vi cần thiết; không secret | Chưa tạo; gốc chỉ khi cần cấu hình chung. |

**Ngoài nhóm dự kiến:** ../backend/ hiện đã có bộ khung Initializr, đối chiếu 12 file khớp ZIP ngày 04/11/2026; chưa triển khai nghiệp vụ hoặc có bằng chứng build/test. Không còn coi backend là thư mục chưa tạo.

Vị trí lưu audio sinh ra/file tải lên/dữ liệu chạy thực tế còn chờ TDD, **không mặc định đưa vào Git**. .gitignore gốc có phạm vi toàn repo; có thể thêm quy tắc theo thư mục khi thực sự cần, chưa sửa trong lượt này. Khung toàn dự án được ghi nhận là định hướng tổ chức, không là phê duyệt công nghệ hoặc chức năng đã triển khai.

<a id="6-nhật-ký-tổ-chức-tài-liệu"></a>

## 6. Nhật ký tổ chức tài liệu

| Ngày | Công việc | Giới hạn |
| --- | --- | --- |
| 01/11/2026 | Đọc bản thực tế; sửa E15/bảng lịch sử UI-P03/hồ sơ nguồn Cần Chánh; kiểm tra nguồn/đích rồi chuyển đúng bảy mục được giao. Tạo mục lục này, cập nhật README gốc, liên kết/ảnh/bàn giao; chỉ tạo hai thư mục chứa tài liệu di chuyển. | Giữ mã, nội dung đã chốt, lịch sử và phần mở. Không tạo TDD/roadmap/thư mục rỗng hoặc thành phần thực thi; không cài Graphify. Kiểm tra tài liệu không là kiểm thử phần mềm/xác minh di tích. |
| 01/11/2026 | Đồng bộ PRD 0.6/UI 0.3 và UI-02: đóng đúng phần câu hỏi UI-P01/UI-P02/UI-P04 đã được người dùng xác nhận, P01 đổi sang nghe hết đúng bài đang phát. Cập nhật thứ tự thiết kế tiếp và phụ thuộc file/30 ngày; khung thư mục giữ nguyên. | R06/phần mở khác vẫn còn; không duyệt toàn bộ UI/ảnh/nền/bản dịch/TTS/kỹ thuật hoặc kết quả phần mềm. |
| 01/11/2026 | Cập nhật UI 0.4: bổ sung bản dự thảo UI Admin lượt 1 tại mục 10 và hướng đọc/bàn giao, giữ PRD 0.6 và nguồn nghiệp vụ. | UI mới chờ duyệt, không ghi decisions/PRD/Business Flow lại để khớp thiết kế; không thay phần khách, bản lịch sử hoặc tài sản bản đồ. |
| 02/11/2026 | Đồng bộ UI 0.5 và UI-03: ghi xác nhận đúng hướng lượt 1, sửa nhãn tên/trình tự vùng, thêm lượt 2 dự thảo nội dung/bản dịch/audio/mức đầy đủ và hướng đọc/bàn giao. | Chỉ hướng UI-AP01–UI-AP03 được chốt; UI-AP04–UI-AP07 còn đề xuất. Giữ PRD 0.6/Business Flow và các nghiệp vụ khác, phần khách, lịch sử/tài sản; không chọn TTS/kỹ thuật hoặc tuyên bố phần mềm đáp ứng. |
| 02/11/2026 | Đồng bộ PRD 0.7/UI 0.6/UI-04: một thao tác áp dụng cặp, điều kiện bật hiện cặp vi, hồ sơ cặp/bảo vệ tham chiếu và hết phiên Admin dừng nghe thử; cập nhật báo cáo 7.8, README gốc và đường đọc. Thêm UI lượt 3 dự thảo. | Lượt 3 chưa duyệt; giữ D03/P01/UI-02/20+20, lịch sử/tài sản. Không tạo TDD/roadmap hoặc thao tác ứng dụng/cấu hình/database/kiểm thử/Git/triển khai. |
| 02/11/2026 | Đồng bộ UI-05/PRD 0.8/UI 0.7 và box UI-A21, hai bảng/ngày trọn ngày/QR PNG. Dẫn báo cáo nhất quán 7.9 và tổng hợp trước/trong TDD tại UI 13; cập nhật bàn giao và README gốc. | Chốt đúng hướng UI-AP08–UI-AP10, không toàn bộ đồ họa/ranh giới/nội dung/TTS/kỹ thuật; R06 còn mở một phần, chưa tạo TDD/roadmap hoặc thao tác phần mềm. |
| 02/11/2026 | Kiểm kê repo/tài liệu hiện hành, soạn roadmap 0.1 với đúng 11 issue lớn, tiến độ/bằng chứng/ranh giới/phụ thuộc/điều kiện đóng; thêm mục lục và cập nhật README gốc. | Backlog dự thảo chờ duyệt, chưa tạo issue/Project hoặc phân công/chốt lịch. Không làm kỹ thuật/TTS/Git/kiểm thử/triển khai, không sửa nguồn nghiệp vụ/UI hoặc tính hoàn tất roadmap. |
| 02/11/2026 | Roadmap 0.2 rút mỗi issue còn Description và một Checklist, giữ tiến độ; ghi phân công G02 tại decisions 3.4 và cập nhật hướng đọc/README. | Phân công nhóm đã xác nhận; nội dung 11 issue/trạng thái vẫn chờ duyệt, chưa chốt lịch hoặc thay đổi GitHub, không thực hiện kỹ thuật. |
| 02/11/2026 | Đồng bộ TECH-01 tại decisions 3.5, PRD 0.8.1/UI 0.7.1/báo cáo 7.10 và roadmap 0.3; ghi backlog 0.2 đã duyệt/xuất bản theo bằng chứng lượt trước, cập nhật bước A và hướng bàn giao. | Chưa kiểm chứng tương thích/triển khai/kiểm thử hoặc chọn TTS/hosting/chính sách; diễn giải issue mới chưa gửi GitHub, không tạo code/TDD/cấu hình hoặc khảo sát dịch vụ. |
| 03/11/2026 | Thêm technical-inputs 0.1, ghi TECH-02/TTS-01 và đồng bộ roadmap 0.4/PRD 0.8.2/UI 0.7.2/báo cáo 7.11; dẫn khảo sát demo chỉ đọc/nguồn chính thức và phương án mới/lịch mục tiêu. | Model/Charon và đầu vào đã chốt; IN-P/DATA-P/lịch chưa duyệt. Không kế thừa tunnel/log/backup/lịch demo hoặc thực thi phần mềm/API/Git/GitHub; chưa tạo TDD. |
| 03/11/2026 | Ghi TECH-03/DATA-01/G03; đầu vào 0.2/roadmap 0.5/PRD 0.8.3/UI 0.7.3/review 7.12; tạo đúng docs/03-ky-thuat/TDD.md 0.1, dẫn đọc/bàn giao và checklist chỉ đọc. | TDD dự thảo, ngrok thử, không giả kết quả; giữ tài sản/history và phần mở, không cấu hình/code/test/Git/CI-CD hoặc sửa GitHub. |
| 03/11/2026 | TECH-04/DATA-02, đầu vào 0.3/roadmap 0.6/PRD 0.8.4/UI 0.7.4/review 7.13; TDD 0.2 dữ liệu/ERD/tab-audio/backup cục bộ, cập nhật đường đọc/trạng thái/G03. | Giữ lịch sử/tài sản, không tạo schema/code/test hoặc thao tác Git/dịch vụ/demo; không duyệt toàn TDD/CD. |
| 03/11/2026 | Ghi TECH-05/DATA-03, đầu vào 0.4/roadmap 0.7/PRD 0.8.5/UI 0.7.5/review 7.14; TDD 0.3 và api-transactions 0.1, cập nhật mục lục/cây/đường đọc và G03. | Chốt đúng CD/phạm vi-nhịp backup; chưa SSH/quyền Docker/CD/restore/API chạy hoặc duyệt toàn TDD, không thực thi code/config/DB/test/Git/GitHub/dịch vụ/demo. |
| 03/11/2026 | Dẫn TDD 0.4/data-model/API 0.2/operations 0.1, mô hình cũ chuyển lịch sử; đầu vào 0.5/roadmap 0.8/PRD 0.8.6/UI 0.7.6/review 7.15. | LIFE/REC chưa duyệt, mọi V-* chưa chạy; không code/config/DB/test/Git/GitHub/triển khai hoặc sửa tài sản UI/demo. |
| 04/11/2026 | DATA-04/TDD 0.5/model 0.2/API 0.3/operations 0.2, đầu vào 0.6/roadmap 0.9/PRD 0.8.7/UI 0.7.7/review 7.16; cập nhật đường đọc/lịch sử. | Xác nhận đúng phần, P01 chỉ hướng tối giản không context/10 phút; chưa software/SSH/Docker/backup/restore/Git/GitHub. |
| 04/11/2026 | DATA-05/LIFE-P01; TDD 0.6/model 0.3/API 0.4/operations 0.3, PRD 0.8.8/UI 0.7.8/inputs 0.7/roadmap 0.10/review 7.17; thêm hướng dẫn thực hành 0.1 và ma trận V-START01. | Vé ký 24 giờ đã chốt; bỏ context/10 phút/start_generation, giữ service_epoch/DATA-04. Chỉ tài liệu, chưa thực hành hoặc kiểm chứng phần mềm/GitHub. |
| 04/11/2026 | Bổ sung DFD 0.1, TDD 0.7 và đường đọc/mục lục; năm sơ đồ ở một vị trí chính, không thư mục rỗng hoặc bản DFD trùng. | Giữ model/API/operations/nguồn nghiệp vụ và thực hành 01; chỉ tài liệu, từng sơ đồ/toàn TDD chưa duyệt hoặc phần mềm đã kiểm; không Git/GitHub. |
| 04/11/2026 | DFD 0.2 gom mức 1, bảng thành phần/cân bằng và SVG cùng nguồn; TDD giữ mốc 0.7, sửa nhãn đọc trước trong thực hành 01. | Đã xem render cục bộ; chưa cam kết mọi trình đọc/khổ giấy, chưa duyệt từng hình/toàn TDD hoặc chạy phần mềm. Không thay nghiệp vụ/bước thực hành. |
| 04/11/2026 | Dẫn DFD 0.3, đầu mút trực tiếp tới từng kho; ký hiệu cùng ID được lặp theo cụm, SVG cũ được thay tại cùng đường dẫn. | Không cạnh tới khung DBS, không mô hình hiện hành song song; thực hành mục 1 độc lập, chưa chạy thay người dùng. |
| 04/11/2026 | Ghi tiến độ thực hành 01: người dùng đã cung cấp Java/Javac 21.0.12.1, Docker CLI 29.8.1, Compose 5.5.1 và WSL mặc định 2; dẫn bước docker info tiếp theo. | Không trợ lý chạy môi trường, cài/đổi cấu hình hoặc chạy dự án; chưa kiểm Engine/Linux/tương thích tổng thể hoặc đủ tích checklist toàn mục. |
| 04/11/2026 | Ghi Engine Linux 29.8.1 đã có kết quả người dùng; dẫn bước Initializr/Maven/Java 21/Boot 4.1.2 và sáu dependency cơ sở, chưa sinh code. | Chỉ nguồn và hướng dẫn; chưa có ZIP/skeleton/build/MySQL/test hoặc đầy đủ tương thích, không thay phiên bản/chạy dự án/Git/GitHub. |
| 04/11/2026 | Ghi đã đọc backend.zip, khác biệt Maven/MySQL test sinh sẵn và dẫn bước người dùng tự giải nén. | Chưa giải nén, sửa/chạy code/config/Wrapper/build/test hoặc thay bộ phiên bản; nhật ký trước giữ đúng thời điểm. |
| 04/11/2026 | Ghi backend đã tồn tại/12 file khớp ZIP, lệnh giải nén bị chặn bảo vệ đích; dẫn bước chỉnh Maven 3.9.12. | Chỉ kiểm file/cập nhật tài liệu, không ghi đè hoặc sửa/chạy cấu hình/code/Wrapper/test thay người dùng. |

<a id="header-roadmap"></a>

## Phần lưu: header-roadmap

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="roadmap--backlog-11-issue-và-tiến-độ-theo-bằng-chứng"></a>

# Roadmap — Backlog 11 issue và tiến độ theo bằng chứng

**Phiên bản:** 0.10 — 04/11/2026; DATA-05/LIFE-P01 vé ký 24 giờ, thiết kế và chuẩn bị thực hành 01.  
**Trạng thái:** backlog/11 task/G02 đã duyệt giữ nguyên; bản 0.10 là cập nhật local chưa gửi GitHub. DATA-05 chốt LIFE-P01 vé ký 24 giờ, bỏ generation/context; DATA-04 giữ P02/P03/REC/BKP-P02. Có hướng dẫn thực hành dự thảo, chưa code/DB/test hoặc mục toàn phần mới hoàn thành; không đọc/thay GitHub.
**Giới hạn lượt xác nhận 04/11:** chỉ tài liệu/nguồn; chưa code/config/DB/test/backup/restore/Git/GitHub/SSH/triển khai hoặc thao tác demo.

<a id="header-technical-inputs"></a>

## Phần lưu: header-technical-inputs

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="đầu-vào-thiết-kế-kỹ-thuật--đối-chiếu-demo-khảo-sát-và-kế-hoạch-mục-tiêu"></a>

# Đầu vào thiết kế kỹ thuật — đối chiếu demo, khảo sát và kế hoạch mục tiêu

**Phiên bản:** 0.7 — 04/11/2026; DATA-05/LIFE-P01 vé ký 24 giờ, dẫn thiết kế và thực hành đầu tiên dự thảo.  
**Trạng thái:** DATA-05 chốt LIFE-P01 vé ký 24 giờ/ledger, bỏ context/10 phút/generation; TECH-05/DATA-04 và lựa chọn trước giữ nguyên. TDD/model/API/operations và hướng dẫn thực hành 01 còn dự thảo, chưa kiểm môi trường/tương thích/khóa/DB/API/backup/SSH hoặc toàn TDD được duyệt.
**Nguồn nghiệp vụ:** [Business Flow](../02-nghiep-vu/business-flow.md), [PRD](../02-nghiep-vu/PRD.md), [UI](../04-giao-dien/ui-design.md), [roadmap T01–T11](../01-tong-quan/roadmap.md). Không lấy TDD/quyền triển khai của demo làm quyền hoặc yêu cầu dự án chính.  
**Giới hạn thực hiện:** chỉ đọc tài liệu/nguồn công khai và GitHub issues, cập nhật tài liệu repo chính. Không đọc key, chạy/sửa demo, copy audio/code/dữ liệu, gọi TTS, cài/đăng ký dịch vụ, code/cấu hình/database/workflow, kiểm thử phần mềm, Git hoặc thay đổi GitHub/triển khai.

**Cách đọc bằng chứng:** (a) mã/cấu hình nhìn thấy; (b) thiết kế/đề xuất cũ; (c) kết quả hồ sơ ghi nhận; (d) dự án chính chưa kiểm chứng. Đọc tài liệu hoặc thấy test trong source không phải chạy test. Nguồn web kiểm tra ngày **03/11/2026**, giá/quota/phát hành có thể đổi; không có số liệu tài khoản từ API.

<a id="header-business-flow"></a>

## Phần lưu: header-business-flow

**Chuyển từ:** docs/02-nghiep-vu/business-flow.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="business-flow--khách-tham-quan-và-admin"></a>

# Business Flow — Khách tham quan và Admin

**Dự án:** Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế  
**Ngày soạn ban đầu:** 29/10/2026  
**Cập nhật:** 04/11/2026 — DATA-05/LIFE-P01 vé ký 24 giờ và dẫn TDD hiện hành; DATA-04/LIFE-P02/P03/REC/BKP-P02 giữ nguyên.  
**Trạng thái:** **Luồng nghiệp vụ khách đã được duyệt tổng thể. Luồng nghiệp vụ Admin đã được duyệt tổng thể. Business Flow cho hai vai trò đã được duyệt trong phạm vi nghiệp vụ đã thống nhất** (BF-K01, BF-A01, BF-ALL01).  
**Giới hạn:** Không duyệt theo giao diện/thao tác vẽ/ranh giới, nội dung thuyết minh/bản dịch/audio, TTS, thiết kế kỹ thuật hoặc kết quả kiểm thử. [PRD.md](../02-nghiep-vu/PRD.md) 0.4.1 đã được duyệt **phạm vi yêu cầu đã rõ** theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval) ngày 31/10/2026; các mục còn mở/đề xuất/chưa kiểm chứng được loại trừ. [ui-design hiện hành](../04-giao-dien/ui-design.md) giữ UI-01–UI-05 đã xác nhận đúng phần; chi tiết chưa xác nhận vẫn dự thảo. PRD 0.8.8 giữ cơ sở PRD-01/UI-01–UI-05 và bổ sung dẫn chiếu TECH-01. Không duyệt toàn bộ UI hoặc nội dung theo gói.

**Đầu vào kỹ thuật riêng:** [DATA-05](../02-nghiep-vu/decisions.md#data-05) chốt LIFE-P01 vé ký 24 giờ/lookup ledger đã tạo dù vé hết hạn; bỏ start_generation/context/10 phút, giữ service_epoch/REC-P01. DATA-04/TECH-05 và nghiệp vụ khác giữ nguyên. [TDD hiện hành](../03-ky-thuat/TDD.md)/model/API/operations/thực hành 01 chưa toàn giải pháp được duyệt hoặc kiểm thực.

**Mục lục:** [1. Mục tiêu/phạm vi](../02-nghiep-vu/business-flow.md#bf-1) · [2. Vai trò/quy tắc chung](../02-nghiep-vu/business-flow.md#bf-2) · [3. Khách](../02-nghiep-vu/business-flow.md#bf-3) · [4. Admin](../02-nghiep-vu/business-flow.md#bf-4) · [5. Liên quan hai vai trò](../02-nghiep-vu/business-flow.md#bf-5) · [6. Chờ thiết kế/rà soát](../02-nghiep-vu/business-flow.md#bf-6) · [7. Tài liệu tiếp theo](../02-nghiep-vu/business-flow.md#bf-7) · [8. Nhật ký](../02-nghiep-vu/business-flow.md#bf-8).

<a id="header-decisions"></a>

## Phần lưu: header-decisions

**Chuyển từ:** docs/02-nghiep-vu/decisions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="nhật-ký-quyết-định-nghiệp-vụ"></a>

# Nhật ký quyết định nghiệp vụ

**Dự án:** Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế  
**Cập nhật gần nhất:** 04/11/2026 — DATA-05 chốt LIFE-P01 vé ký 24 giờ, bỏ start_generation/context/10 phút; DATA-04/LIFE-P02/P03/REC/BKP-P02 giữ nguyên. TDD/model/API/operations vẫn dự thảo/chưa kiểm.  
**Trạng thái:** Luồng khách **đã được duyệt tổng thể** (BF-K01); luồng Admin **đã được duyệt tổng thể** (BF-A01); Business Flow hai vai trò **đã được duyệt trong phạm vi nghiệp vụ đã thống nhất** (BF-ALL01). [PRD.md](../02-nghiep-vu/PRD.md) 0.4.1 được người dùng duyệt **phạm vi yêu cầu đã mô tả rõ** ngày 31/10/2026 theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval); phần còn mở/đề xuất/chưa kiểm chứng được loại trừ. [ui-design hiện hành](../04-giao-dien/ui-design.md) 0.7.8 giữ UI-01–UI-04 và bổ sung UI-05/UI-AP08–UI-AP10 đúng phạm vi; chi tiết chưa xác nhận vẫn dự thảo. PRD hiện hành 0.8.8 kế thừa PRD-01/UI-01–UI-05, bổ sung dẫn chiếu TECH-01; UI hiện hành 0.7.8 giữ nguyên hướng đã xác nhận. TECH-01 chỉ chốt lựa chọn nền, không chứng minh tương thích, nội dung hoặc phần mềm. Model/giọng hiện hành theo TTS-01; đầu vào mới TECH-02 không phê duyệt các phương án khảo sát. Xác nhận đầu vào bổ sung theo TECH-03/DATA-01/G03; TDD hiện hành vẫn dự thảo; kênh CD riêng nay chọn theo TECH-05. TECH-04/DATA-02 chỉ chốt hướng nêu rõ; không duyệt ERD/API/TDD toàn bộ hoặc kết quả chạy. TECH-05/DATA-03 không duyệt toàn TDD hoặc chứng minh kết nối/CD/restore hoạt động.

<a id="header-PRD"></a>

## Phần lưu: header-PRD

**Chuyển từ:** docs/02-nghiep-vu/PRD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="prd--hệ-thống-thuyết-minh-tự-động-bằng-mã-qr-cho-kinh-thành-huế"></a>

# PRD — Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế

**Phiên bản tài liệu:** 0.8.8 — 04/11/2026 (DATA-05/LIFE-P01 vé ký 24 giờ, giữ mã yêu cầu/Use Case/tiêu chí)  
**Trạng thái:** **ĐÃ ĐƯỢC NGƯỜI DÙNG DUYỆT PHẠM VI YÊU CẦU ĐÃ MÔ TẢ RÕ TRONG PRD 0.4.1** ngày 31/10/2026, theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval). Bao gồm Q1–Q3 và câu BR-11 đã sửa; không bao gồm các mục còn mở, đề xuất hoặc chưa kiểm chứng. Bản 0.8.8 kế thừa phạm vi đó/UI-01–UI-04; bổ sung [UI-04](../02-nghiep-vu/decisions.md#ui-admin-confirmed-02) ngày 02/11/2026: chỉ áp dụng cả cặp, gate cặp vi, hồ sơ cặp/giữ tạm tham chiếu và Admin hết phiên dừng nghe thử. Đây là thay đổi có chủ đích đã xác nhận, không duyệt toàn bộ PRD/UI hoặc mục còn mở. [UI-05](../02-nghiep-vu/decisions.md#ui-admin-confirmed-03) xác nhận đúng hướng lượt 3: box chi tiết, ngày trọn ngày/hai bảng-tổng, QR chung PNG/in; chi tiết chưa nêu vẫn chưa duyệt.  
**Nguồn nghiệp vụ:** [business-flow.md](../02-nghiep-vu/business-flow.md) và [decisions.md](../02-nghiep-vu/decisions.md), đã đọc và đối chiếu lại ngày 02/11/2026. Luồng khách BF-K01, luồng Admin BF-A01 và Business Flow hai vai trò BF-ALL01 đã được duyệt trong phạm vi nghiệp vụ; Q1–Q3 được xác nhận bổ sung ngày 31/10/2026. Xác nhận Q1–Q3 khi đó không tự duyệt PRD; xác nhận phạm vi PRD hiện hành được ghi riêng theo PRD-01 ngày 31/10/2026.  
**Giới hạn duyệt:** Yêu cầu/Use Case/tiêu chí và NFR đã rõ được duyệt để làm cơ sở thiết kế và đánh giá sau này; các phần chưa chốt ở mục 9 và [prd-review mục 7.4](../02-nghiep-vu/prd-review.md#review-open-final) tiếp tục còn mở. Không duyệt giao diện/ranh giới, giải pháp kỹ thuật, nhà cung cấp/giọng/chi phí TTS, nội dung lịch sử/bản dịch/audio hoặc kết quả kiểm thử. **Chưa có bằng chứng phần mềm đáp ứng**. Hướng giao diện/ứng xử được xác nhận riêng theo [UI-01](../02-nghiep-vu/decisions.md#ui-guest-confirmed) và [UI-02](../02-nghiep-vu/decisions.md#ui-guest-confirmed-02); [ui-design hiện hành](../04-giao-dien/ui-design.md) có các hướng UI-01–UI-05 đã chốt đúng phạm vi; xem số phiên bản/trạng thái ở đầu file, không ghi cứng số cũ. Chỉ hướng UI-AP08–UI-AP10 được chốt theo UI-05; chi tiết chưa xác nhận vẫn dự thảo, không duyệt toàn bộ UI/nền/vị trí/ranh giới.

**Đầu vào kỹ thuật đã xác nhận riêng:** [DATA-05](../02-nghiep-vu/decisions.md#data-05) chốt LIFE-P01 vé ký 24 giờ/gắn quyền tab/ledger; không start_generation/context/10 phút. DATA-04 và TECH-01–05 giữ nền/LIFE-P02/P03/REC/BKP-P02. [TDD hiện hành](../03-ky-thuat/TDD.md)/model/API/operations và thực hành 01 vẫn thiết kế/hướng dẫn chưa thực hiện; không duyệt toàn giải pháp/phần mềm.

<a id="header-prd-review"></a>

## Phần lưu: header-prd-review

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="báo-cáo-rà-soát-trực-tiếp-prd-với-business-flow-và-decisions"></a>

# Báo cáo rà soát trực tiếp PRD với Business Flow và decisions

**Ngày soạn:** 30/10/2026  
**Cập nhật:** 04/11/2026 — DATA-05/LIFE-P01 và TDD 0.6/model 0.3/API 0.4/operations 0.3; R06 còn mở phần khác.  
**Trạng thái:** **R01–R05/R07 đã xử lý ở mức tài liệu; S01/S02 đã sửa; Q1–Q3 đã xác nhận và qua đối chiếu cuối. R06 vẫn mở một phần theo bảng mục 7.4.** Phạm vi yêu cầu đã rõ của PRD 0.4.1 đã được người dùng duyệt theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval) ngày 31/10/2026; các mục còn mở/đề xuất/chưa kiểm chứng tiếp tục được loại trừ. Đã sửa tài liệu không đồng nghĩa phần mềm đã đáp ứng.  
**Nguồn đã đọc trực tiếp:** [business-flow.md](../02-nghiep-vu/business-flow.md), [decisions.md](../02-nghiep-vu/decisions.md), [PRD.md](../02-nghiep-vu/PRD.md), prd-review.md hiện tại trong repo chính trước khi sửa. Đối chiếu nội dung thay vì dựa vào số dòng cũ; các vị trí theo nội dung/mã trong PRD; hiện hành PRD 0.8.8/UI 0.7.8 kế thừa UI-01–UI-05/PRD-01 và dẫn TECH-01. Số dòng/bản nhận xét cũ giữ như lịch sử, ưu tiên neo/mã; thay đổi UI xem 7.9, cập nhật công nghệ/đầu vào xem 7.10. Các kết luận theo ngày ở 7.6–7.8 được đọc đúng thời điểm, không tự thay phạm vi hiện hành. Cập nhật 03/11 xem 7.11; các mục theo ngày trước là lịch sử, không ghi lại thành khảo sát hiện tại. Lượt chốt IN-P xem 7.12; đã đọc thêm 11 checklist GitHub chỉ đọc, không thay đổi. Lượt dữ liệu xem 7.13; không biến xác nhận hướng thành duyệt ERD hoặc kết quả chạy.

**Trạng thái hiện hành:** [DATA-05](../02-nghiep-vu/decisions.md#data-05) chốt LIFE-P01 vé ký 24 giờ/ledger; DATA-04/LIFE-P02/P03/REC/BKP-P02 giữ nguyên. Bỏ generation/context, không toàn TDD được duyệt/kiểm. [7.17](../02-nghiep-vu/prd-review.md#review-data-05) là cập nhật mới; 7.16 và các bảng theo thời điểm trước giữ lịch sử, không dùng trạng thái P01 chưa chốt/generation như hiện hành.

<a id="header-TDD"></a>

## Phần lưu: header-TDD

**Chuyển từ:** docs/03-ky-thuat/TDD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="tdd--thiết-kế-kỹ-thuật-theo-từng-phần"></a>

# TDD — Thiết kế kỹ thuật theo từng phần

**Phiên bản:** 0.7 — 04/11/2026; giữ mốc kiến trúc, đồng bộ DFD 0.3 nối đúng kho theo LIFE-P01 vé ký 24 giờ; model 0.3/API 0.4/operations 0.3 không thay đổi.  
**Trạng thái:** **DỰ THẢO**, chưa toàn TDD/phần mềm được duyệt hoặc kiểm. [DATA-05](../02-nghiep-vu/decisions.md#data-05) chốt LIFE-P01 vé ký 24 giờ, ledger có trả gốc dù vé hết hạn, ledger không có chỉ tạo vé còn hạn; bỏ generation/context. DATA-04 giữ P02/P03/REC/BKP-P02. Model 0.3/API 0.4/operations 0.3 và [thực hành 01](../03-ky-thuat/practice-01-start-session.md) là thiết kế/hướng dẫn chưa thực hiện.
**Nguồn:** [mục lục](../README.md), [PRD](../02-nghiep-vu/PRD.md), [Business Flow](../02-nghiep-vu/business-flow.md), [decisions](../02-nghiep-vu/decisions.md), [đầu vào hiện hành](../01-tong-quan/technical-inputs.md), [UI](../04-giao-dien/ui-design.md), [roadmap](../01-tong-quan/roadmap.md). Đã đối chiếu bản thực tế trước ghi; giữ mã yêu cầu và nghiệp vụ.   Căn cứ bổ sung TECH-04/DATA-02; không dùng bản nháp schema làm quyết định đã chốt. Căn cứ mới TECH-05/DATA-03, không coi việc chọn kênh là triển khai đã hoạt động.
**Giới hạn:** chỉ tài liệu và sơ đồ; có dùng công cụ sẵn có để render/xem DFD, không cài thêm. Không tạo/chạy code ứng dụng, cấu hình/database/workflow, gọi TTS, đăng ký ngrok, kiểm thử phần mềm dự án, Git/GitHub hoặc sửa/chạy demo. Các tên module/lớp/đường API dưới đây là đề xuất để giải thích, chưa là hợp đồng API đã chốt.

**Mục lục:** [1. Đầu vào](../03-ky-thuat/TDD.md#tdd-inputs) · [2. Tổng thể](../03-ky-thuat/TDD.md#tdd-overview) · [3. Module](../03-ky-thuat/TDD.md#tdd-modules) · [4. Ba lớp](../03-ky-thuat/TDD.md#tdd-layers) · [5. API/quyền](../03-ky-thuat/TDD.md#tdd-api-security) · [6. MySQL và tác vụ](../03-ky-thuat/TDD.md#tdd-data-jobs) · [7. Audio/file](../03-ky-thuat/TDD.md#tdd-audio) · [8. Windows/Docker/ngrok](../03-ky-thuat/TDD.md#tdd-runtime) · [9. CI/CD](../03-ky-thuat/TDD.md#tdd-cicd) · [10. Truy vết](../03-ky-thuat/TDD.md#tdd-trace) · [11. ERD nội dung](../03-ky-thuat/TDD.md#tdd-erd) · [12. Phiên/lượt/xóa](../03-ky-thuat/TDD.md#tdd-visits-data) · [13. Tab](../03-ky-thuat/TDD.md#tdd-tab-session) · [14. Audio tải trọn](../03-ky-thuat/TDD.md#tdd-full-audio) · [15. Backup cục bộ](../03-ky-thuat/TDD.md#tdd-local-backup) · [16. Câu hỏi còn mở](../03-ky-thuat/TDD.md#tdd-open) · [17. Cách học/nhật ký](../03-ky-thuat/TDD.md#tdd-guide). · [Mô hình hiện hành](../03-ky-thuat/data-model.md) · [API/DTO](../03-ky-thuat/api-transactions.md#api-dto) · [Vòng đời/backup/kiểm chứng](../03-ky-thuat/operations-design.md) · [DFD mức 0–2](../03-ky-thuat/dfd.md).

<a id="header-api-transactions"></a>

## Phần lưu: header-api-transactions

**Chuyển từ:** docs/03-ky-thuat/api-transactions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="tdd--api-và-giao-dịch"></a>

# TDD — API và giao dịch

**Phiên bản:** 0.4 — 04/11/2026, Asia/Saigon.  
**Trạng thái:** **DỰ THẢO THIẾT KẾ**, TDD 0.6/[model 0.3](../03-ky-thuat/data-model.md). DATA-05 chốt LIFE-P01 vé ký 24 giờ/lookup sau vé hết hạn; không start_generation/context. DATA-04 giữ LIFE-P02/P03/REC/BKP-P02; chưa API/schema/OpenAPI/test hoặc duyệt toàn TDD.
**Nguồn:** [PRD](../02-nghiep-vu/PRD.md), [Business Flow](../02-nghiep-vu/business-flow.md), [decisions](../02-nghiep-vu/decisions.md), TECH-01–05/DATA-01–05/G03 và các mã truy vết bên dưới. CD-L01/phạm vi-nhịp backup đã chọn riêng, không chứng minh SSH/Docker/CD/restore hoạt động.  
**Giới hạn lượt:** chỉ tài liệu/nguồn công khai/đối chiếu checklist chỉ đọc; không code/config/database/migrations/test/Git hoặc thay GitHub, không dịch vụ/TTS/demo/triển khai.

Mục lục: [1. Quy ước](../03-ky-thuat/api-transactions.md#api-conventions) · [2. Quyền](../03-ky-thuat/api-transactions.md#api-auth) · [3. Khách](../03-ky-thuat/api-transactions.md#api-guest) · [4. Admin](../03-ky-thuat/api-transactions.md#api-admin) · [5. Lặp/chưa rõ](../03-ky-thuat/api-transactions.md#api-idempotency) · [6. Giao dịch/cạnh tranh](../03-ky-thuat/api-transactions.md#api-transactions) · [7. Job/file/backup](../03-ky-thuat/api-transactions.md#api-jobs) · [8. Lỗi](../03-ky-thuat/api-transactions.md#api-errors) · [9. Truy vết/còn mở](../03-ky-thuat/api-transactions.md#api-trace) · [10. DTO/ví dụ](../03-ky-thuat/api-transactions.md#api-dto).

<a id="header-data-model"></a>

## Phần lưu: header-data-model

**Chuyển từ:** docs/03-ky-thuat/data-model.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="tdd--mô-hình-dữ-liệu-hiện-hành"></a>

# TDD — Mô hình dữ liệu hiện hành

**Phiên bản:** 0.3 — 04/11/2026. **DỰ THẢO**, DATA-05 chốt LIFE-P01 vé ký 24 giờ; chưa schema/Flyway/MySQL chạy hoặc duyệt toàn mô hình.  
**Vị trí chính:** mô hình hiện hành duy nhất của TDD 0.6/API 0.4; thay phần start_generation của 0.2 bằng timestamp/digest vé gắn session, không bảng start context. [TDD 11–12](../03-ky-thuat/TDD.md#tdd-erd) dẫn về đây; [lịch sử](data-model-history.md) giữ generation, không ghép lại vào schema.
**Nguồn:** [PRD](../02-nghiep-vu/PRD.md), [decisions](../02-nghiep-vu/decisions.md), [API](../03-ky-thuat/api-transactions.md), [operations](../03-ky-thuat/operations-design.md). [DATA-05](../02-nghiep-vu/decisions.md#data-05) chốt LIFE-P01, DATA-04 giữ LIFE-P02/P03/REC/BKP-P02; lựa chọn đúng phần không duyệt toàn TDD hoặc kiểm thực.

Mục lục: [1. Quy ước](../03-ky-thuat/data-model.md#model-conventions) · [2. Nội dung](../03-ky-thuat/data-model.md#model-content) · [3. Phiên/quyền](../03-ky-thuat/data-model.md#model-guest) · [4. Thao tác/job/file](../03-ky-thuat/data-model.md#model-operations) · [5. Ràng buộc/xóa](../03-ky-thuat/data-model.md#model-constraints) · [6. Truy vết](../03-ky-thuat/data-model.md#model-trace).

<a id="header-operations-design"></a>

## Phần lưu: header-operations-design

**Chuyển từ:** docs/03-ky-thuat/operations-design.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="tdd--vòng-đời-dữ-liệu-kỹ-thuật-backup-và-kế-hoạch-kiểm-chứng"></a>

# TDD — Vòng đời dữ liệu kỹ thuật, backup và kế hoạch kiểm chứng

**Phiên bản:** 0.3 — 04/11/2026. **DỰ THẢO THIẾT KẾ**; LIFE-P01/DATA-05 đã chốt đúng phần, chưa phần mềm/backup/restore/test thực.  
**Đọc cùng:** [model 0.3](../03-ky-thuat/data-model.md), [API 0.4](../03-ky-thuat/api-transactions.md), [TDD 0.6](../03-ky-thuat/TDD.md), [DATA-04](../02-nghiep-vu/decisions.md#data-04)/[DATA-05](../02-nghiep-vu/decisions.md#data-05). LIFE-P01 nay chốt vé ký 24 giờ từ cấp, bỏ context/10 phút/start_generation; LIFE-P02/P03/REC-P01/BKP-P02 giữ nguyên, không duyệt toàn TDD hoặc bằng chứng chạy.

Mục lục: [1. Vòng đời](../03-ky-thuat/operations-design.md#ops-lifecycle) · [2. Job/file](../03-ky-thuat/operations-design.md#ops-jobs) · [3. Backup](../03-ky-thuat/operations-design.md#ops-backup) · [4. Restore](../03-ky-thuat/operations-design.md#ops-restore) · [5. Kiểm chứng](../03-ky-thuat/operations-design.md#ops-verification) · [6. Điểm cần quyết định](../03-ky-thuat/operations-design.md#ops-open).

<a id="header-dfd"></a>

## Phần lưu: header-dfd

**Chuyển từ:** docs/03-ky-thuat/dfd.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="dfd--luồng-dữ-liệu-của-hệ-thống"></a>

# DFD — Luồng dữ liệu của hệ thống

**Phiên bản:** 0.3 — 04/11/2026. **DỰ THẢO**, vị trí chính duy nhất của bộ DFD; chưa được duyệt từng sơ đồ hoặc kiểm chứng bằng phần mềm.  
**Phạm vi được giao:** bổ sung DFD trước thực hành; không thay nghiệp vụ, ERD, sequence, state machine hoặc hợp đồng API. Hướng dẫn [thực hành 01](../03-ky-thuat/practice-01-start-session.md) được giữ nguyên, chưa thực hiện.  
**Căn cứ:** giữ nguồn nghiệp vụ và LIFE-P01/DATA-05 đã đối chiếu ở bản 0.1; lượt tinh gọn đọc lại DFD/TDD 0.7, mục lục và hướng dẫn thực hành hiện hành. PRD 0.8.8, data-model 0.3, API 0.4 và operations 0.3 không thay nội dung. TDD giữ mốc kiến trúc 0.7, đồng bộ dẫn DFD 0.3; sửa đầu mút/bố trí không thay LIFE-P01 hoặc duyệt từng hình/toàn TDD.

**Mục lục:** [1. Quy ước](../03-ky-thuat/dfd.md#dfd-conventions) · [2. Mức 0](../03-ky-thuat/dfd.md#dfd-context) · [3. Kho dữ liệu](../03-ky-thuat/dfd.md#dfd-stores) · [4. Mức 1](../03-ky-thuat/dfd.md#dfd-level1) · [5. Bắt đầu phiên](../03-ky-thuat/dfd.md#dfd-start) · [6. TTS](../03-ky-thuat/dfd.md#dfd-tts) · [7. Áp dụng cặp](../03-ky-thuat/dfd.md#dfd-pair) · [8. Truy vết và rà soát](../03-ky-thuat/dfd.md#dfd-trace).

<a id="header-practice-01-start-session"></a>

## Phần lưu: header-practice-01-start-session

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="thực-hành-01--chuẩn-bị-nền-dữ-liệu-và-bắt-đầu-phiên"></a>

# Thực hành 01 — Chuẩn bị nền dữ liệu và Bắt đầu phiên

**Phiên bản:** 0.1 — cập nhật bằng chứng môi trường 04/11/2026. **DỰ THẢO HƯỚNG DẪN**; người dùng đã cung cấp kết quả kiểm phiên bản/WSL và kết nối Docker Engine Linux ở mục 1. Trợ lý chỉ đọc tài liệu/nguồn và ghi nhận, không chạy các lệnh môi trường, code/config/database/migrations/test/Git hoặc dự án thay người dùng.  
**Mục tiêu:** giúp Thành tự đi qua lát cắt nhỏ đầu tiên, sau đó hướng dẫn nhóm theo G02; không coi Hòa/Nghĩa đã làm hoặc hoàn thành toàn T02/T03/T01.  
**Đọc trước:** [DATA-05/LIFE-P01](../02-nghiep-vu/decisions.md#data-05), [TDD 0.7](../03-ky-thuat/TDD.md), [model 0.3](../03-ky-thuat/data-model.md#model-guest), [API 0.4/DTO](../03-ky-thuat/api-transactions.md#api-dto), [protocol](../03-ky-thuat/operations-design.md#ops-start-simple) và [ma trận kiểm](../03-ky-thuat/operations-design.md#ops-start-checks).

Mục lục: [1. Kiểm đầu vào](../03-ky-thuat/practice-01-start-session.md#practice-env) · [2. Bản nhỏ cần xây](../03-ky-thuat/practice-01-start-session.md#practice-slice) · [3. Các chặng](../03-ky-thuat/practice-01-start-session.md#practice-stages) · [4. Cách kiểm/lỗi](../03-ky-thuat/practice-01-start-session.md#practice-evidence) · [5. Ôn tập/bàn giao](../03-ky-thuat/practice-01-start-session.md#practice-recap).

<a id="header-ui-design"></a>

## Phần lưu: header-ui-design

**Chuyển từ:** docs/04-giao-dien/ui-design.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="thiết-kế-giao-diện--khách-tham-quan-và-admin"></a>

# Thiết kế giao diện — Khách tham quan và Admin

**Phiên bản:** 0.7.8 — 04/11/2026 (DATA-05 cập nhật quy tắc nền vé Bắt đầu; giữ hướng/màn hình/đồ họa)  
**Trạng thái:** **UI-01/UI-02 và UI-P01–UI-P04 đã xác nhận đúng các hành vi nêu rõ; thiết kế cụ thể/phần mở khác vẫn dự thảo.** P01 đã đổi có chủ đích: đúng bài đang phát tại mốc hết hạn được nghe hết/pause-resume; không duyệt toàn bộ UI/Admin/bản đồ/nội dung/TTS/kỹ thuật.  
**Bổ sung hiện hành:** UI-03 chốt đúng hướng lượt 1; [UI-04](../02-nghiep-vu/decisions.md#ui-admin-confirmed-02) chốt UI-AP04/UI-AP05/UI-AP07 và thay UI-AP06 bằng **một nút áp dụng cả cặp**, cập nhật gate cặp vi. [UI-05](../02-nghiep-vu/decisions.md#ui-admin-confirmed-03) xác nhận đúng hướng lượt 3/UI-AP08–UI-AP10; UI-A21 là box chỉ đọc, không trang riêng. [Mục 13](../04-giao-dien/ui-design.md#ui-next-technical) tổng hợp công việc còn lại, không thay thế TDD/roadmap. Không duyệt toàn bộ đồ họa/câu chữ/UI hoặc nội dung/TTS/kỹ thuật.
**Cơ sở hiện hành:** đã đọc docs/README, Business Flow/decisions/PRD/UI hiện tại trước ghi. [PRD hiện hành](../02-nghiep-vu/PRD.md) giữ các xác nhận và bổ sung TECH-05/DATA-03; [Business Flow](../02-nghiep-vu/business-flow.md), [nguồn UI-05](../02-nghiep-vu/decisions.md#ui-admin-confirmed-03). Lượt này chỉ đồng bộ phụ thuộc kỹ thuật, giữ mã/anchor/lịch sử, màn hình và tài sản bản đồ.
**Giới hạn:** chưa duyệt nền/vị trí/ranh giới năm vùng, kích thước/đồ họa chi tiết, bản dịch/tên địa điểm/tiêu đề/thông báo cụ thể hoặc giải pháp kỹ thuật/TTS. Ảnh là minh họa tài liệu, không ứng dụng đã triển khai/kiểm thử. Năm bản vi vẫn nháp chờ cung cấp/rà soát.  
**Bảo vệ lịch sử:** nguyên bản ui-design 0.1 trước lượt này tại [ui-design-history.md](ui-design-history.md); không suy đoán nội dung lần sửa được chat đánh giá hoàn tác. Bản hiện hành chỉ ghi xác nhận tin nhắn mới.

**Đầu vào kỹ thuật xác nhận riêng:** [TECH-01](../02-nghiep-vu/decisions.md#tech-01) chốt Vue 3/JavaScript/CSS, Vite/npm và Leaflet/Geoman miễn phí cùng nền Backend/Database/CI. Lựa chọn này không phát sinh từ bản đồ/thiết kế UI, không duyệt nền/ranh giới hoặc kiểm chứng tương thích; thiết kế khách/Admin đã chốt giữ nguyên. Các câu “không chọn công nghệ qua UI” tiếp tục phân biệt phạm vi phê duyệt; nguồn công nghệ hiện hành là TECH-01. [TECH-02](../02-nghiep-vu/decisions.md#tech-02) bổ sung môi trường/môn học; [TTS-01](../02-nghiep-vu/decisions.md#tts-01) đã chọn model/Charon. Khách Android/iOS, Admin máy tính/laptop là phạm vi xác nhận; hướng thu gọn Admin mobile trong tài liệu không trở thành nghiệm thu bắt buộc. TECH-03/DATA-01 đã xác nhận mốc phiên bản/nền tảng thực/WAV-MP3-Linux/chính sách, chọn thử ngrok. TDD hiện hành và API/giao dịch còn dự thảo; không thêm nghiệm thu mọi thiết bị hoặc duyệt ranh giới. TECH-04 chốt đóng gói/tab/full MP3, DATA-02 backup cục bộ; TECH-05/DATA-03 nay chọn CD riêng/phạm vi-nhịp backup. Không duyệt toàn TDD/ERD/API/kỹ thuật hoặc chứng minh đã hoạt động.

**Mục lục:** [1. Màn hình](../04-giao-dien/ui-design.md#ui-screens) · [2. Hành trình](../04-giao-dien/ui-design.md#ui-flow) · [3. Bố cục](../04-giao-dien/ui-design.md#ui-layout) · [4. Audio/ngôn ngữ](../04-giao-dien/ui-design.md#ui-interactions) · [5. Trạng thái](../04-giao-dien/ui-design.md#ui-states) · [6. Còn mở](../04-giao-dien/ui-design.md#ui-proposals) · [7. Bản đồ năm vùng](../04-giao-dien/ui-design.md#ui-map) · [8. Truy vết](../04-giao-dien/ui-design.md#ui-trace) · [9. Nhật ký](../04-giao-dien/ui-design.md#ui-history) · [10. UI Admin lượt 1 — hướng đã xác nhận](../04-giao-dien/ui-design.md#ui-admin-round1) · [11. UI Admin lượt 2 — hướng đã xác nhận](../04-giao-dien/ui-design.md#ui-admin-round2) · [12. UI Admin lượt 3 — hướng đã xác nhận](../04-giao-dien/ui-design.md#ui-admin-round3) · [13. Chuẩn bị thiết kế kỹ thuật](../04-giao-dien/ui-design.md#ui-next-technical).

<a id="editorial-0"></a>

## Phần lưu: editorial-0

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Có .git/, .gitignore, README.md và docs/; chỉ xác minh hiện diện/đọc ignore. Mẫu ignore Java không chốt công nghệ Java.

**Cách đọc hiện tại:** Có .git/, .gitignore, README.md, docs/ và bộ khung backend; chỉ xác minh hiện diện/nội dung file. Công nghệ theo TECH-01.

<a id="editorial-1"></a>

## Phần lưu: editorial-1

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

chuẩn bị môi trường/nền dữ liệu/Bắt đầu, chưa thực hiện.

**Cách đọc hiện tại:** đã có kết quả môi trường và bộ khung backend/Wrapper cấu hình 3.9.12; tạm dừng thực hành mới, chưa build/chạy/test.

<a id="editorial-2"></a>

## Phần lưu: editorial-2

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Soạn thực hành 01 có các chặng/kiểm/ôn tập, chưa cài công cụ/code/DB/test;

**Cách đọc hiện tại:** Thực hành 01 đã có kết quả công cụ/Engine Linux và bộ khung backend, Wrapper cấu hình 3.9.12, test còn mysql:latest; chưa có bằng chứng build/chạy/test, hiện tạm dừng hướng dẫn mới;

<a id="editorial-3"></a>

## Phần lưu: editorial-3

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

thực hành 01 mới soạn, không toàn TDD hoặc phần mềm hoàn thành.

**Cách đọc hiện tại:** thực hành 01 đã có kết quả công cụ/bộ khung, chưa build/test; không toàn TDD hoặc phần mềm hoàn thành.

<a id="editorial-4"></a>

## Phần lưu: editorial-4

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Chưa code/TDD đầy đủ hoặc kiểm thử phần mềm.

**Cách đọc hiện tại:** Bộ khung backend đã có nhưng chưa API nghiệp vụ, TDD hoàn chỉnh hoặc bằng chứng kiểm thử phần mềm.

<a id="editorial-5"></a>

## Phần lưu: editorial-5

**Chuyển từ:** docs/01-tong-quan/roadmap.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Hướng dẫn 01 mới chuẩn bị, chưa toàn TDD/chức năng.

**Cách đọc hiện tại:** Hướng dẫn 01 đã có kết quả công cụ/bộ khung, chưa toàn TDD/chức năng hoặc build/test.

<a id="editorial-6"></a>

## Phần lưu: editorial-6

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Thông tin người dùng cung cấp, chưa chạy kiểm tra Windows build/WSL/ảo hóa/dung lượng/Docker.

**Cách đọc hiện tại:** Thông tin người dùng cung cấp; đã có đầu ra Java/Docker/Compose/WSL/Engine Linux ở hướng dẫn thực hành. Windows build/ảo hóa/dung lượng và tổ hợp ứng dụng chưa có bằng chứng kiểm.

<a id="editorial-7"></a>

## Phần lưu: editorial-7

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Giữ nguyên các số dưới đây, chưa kiểm khả dụng/tương thích/cài/build/test.

**Cách đọc hiện tại:** Giữ nguyên các mốc dưới đây. Đã đọc cấu hình Boot 4.1.2/Java 21/Wrapper Maven 3.9.12 trong bộ khung và nhận kết quả công cụ; chưa có bằng chứng resolve/build/test hoặc tương thích toàn tổ hợp.

<a id="editorial-8"></a>

## Phần lưu: editorial-8

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

chuẩn bị kiểm môi trường và làm nền dữ liệu/Bắt đầu theo từng chặng, chưa thực hiện; toàn bộ V-START01.01–10 chưa chạy.

**Cách đọc hiện tại:** đã ghi điểm dừng công cụ/bộ khung và các chặng nền dữ liệu/Bắt đầu còn lại; hiện tạm dừng hướng dẫn mới, toàn bộ V-START01.01–10 chưa chạy.

<a id="editorial-9"></a>

## Phần lưu: editorial-9

**Chuyển từ:** docs/01-tong-quan/technical-inputs.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Phiên bản lịch sử ở 9.1 còn trạng thái cũ; chỉ dùng truy vết, không yêu cầu hiện hành.

**Cách đọc hiện tại:** Phương án cũ ở lưu trữ được dẫn tại 9.1; chỉ dùng truy vết, không là yêu cầu hiện hành.

<a id="editorial-10"></a>

## Phần lưu: editorial-10

**Chuyển từ:** docs/02-nghiep-vu/business-flow.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Bản hiện hành 0.8.4 giữ PRD-01/UI-01–UI-05, dẫn TECH-03/DATA-01;

**Cách đọc hiện tại:** Bản hiện hành giữ PRD-01/UI-01–UI-05 và các xác nhận TECH/DATA;

<a id="editorial-11"></a>

## Phần lưu: editorial-11

**Chuyển từ:** docs/02-nghiep-vu/business-flow.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Roadmap 0.6 giữ backlog đã duyệt, có lịch dự kiến/G02/phụ thuộc/dự phòng và checklist GitHub đối chiếu chỉ đọc. TDD 0.2 đã tạo ở mức kiến trúc dự thảo; đầu vào 0.3 không là kết quả phần mềm.

**Cách đọc hiện tại:** Roadmap giữ 11 task/G02/lịch dự kiến và snapshot GitHub có ngày; TDD cùng mô hình/API/operations là thiết kế dự thảo. Bộ khung và kết quả công cụ đã có ở hướng dẫn thực hành, chưa bằng chứng build/chạy/test.

<a id="editorial-12"></a>

## Phần lưu: editorial-12

**Chuyển từ:** docs/02-nghiep-vu/decisions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Đã chốt nền 02/11/2026; phiên bản/tương thích và cách triển khai còn mở

**Cách đọc hiện tại:** Đã chốt nền; phiên bản theo TECH-03, kiến trúc/CD theo TECH-04/05; tương thích và vận hành còn kiểm

<a id="editorial-13"></a>

## Phần lưu: editorial-13

**Chuyển từ:** docs/02-nghiep-vu/decisions.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

**Công nghệ nền đã chốt theo [TECH-01](../02-nghiep-vu/decisions.md#tech-01), đầu vào TECH-02 và model/giọng TTS-01; phiên bản/tương thích và phương án demo vẫn mở.

**Cách đọc hiện tại:** **Công nghệ nền/đầu vào/model-giọng và mốc phiên bản đã chốt theo TECH-01–05/TTS-01; ngrok là phương án thử, tương thích và vận hành còn kiểm.

<a id="editorial-14"></a>

## Phần lưu: editorial-14

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

**Hiện hành DATA-04:** không hỏi lại window 24 giờ/job metadata bảy ngày/restore thu hồi quyền/backup stop ngoài demo; cơ chế LIFE-P01 đơn giản và các cổng thực còn thiết kế/kiểm.

**Cách đọc hiện tại:** **Hiện hành DATA-05/DATA-04:** vé Bắt đầu ký 24 giờ, window Admin 24 giờ/job metadata bảy ngày/restore thu hồi quyền/backup tạm ngừng đã chốt đúng phạm vi; cơ chế và các cổng thực còn thiết kế/kiểm.

<a id="editorial-15"></a>

## Phần lưu: editorial-15

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

công nghệ nền đã chốt TECH-01 nhưng thiết kế chi tiết, phiên bản/tương thích và lịch chạy chưa chốt.

**Cách đọc hiện tại:** công nghệ/phiên bản nền đã chốt theo TECH-01/TECH-03; thiết kế chi tiết, tương thích thực và lịch chạy còn cần cụ thể hóa/kiểm.

<a id="editorial-16"></a>

## Phần lưu: editorial-16

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

thiết kế kênh CD riêng và kiểm thực trước nghiệm thu.

**Cách đọc hiện tại:** kênh CD riêng đã chọn theo TECH-05; thiết kế chi tiết và kiểm thực trước nghiệm thu.

<a id="editorial-17"></a>

## Phần lưu: editorial-17

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

practice-01-start-session 0.1 chuẩn bị các lệnh chỉ đọc để người dùng tự kiểm môi trường sau và các chặng nền dữ liệu/Bắt đầu; chưa backend/DDL/config/test hoặc Git/GitHub được tạo/chạy.

**Cách đọc hiện tại:** practice-01-start-session ghi kết quả công cụ/Engine Linux và backend hiện có; Wrapper cấu hình 3.9.12, test còn mysql:latest. Chưa DDL/API nghiệp vụ hoặc bằng chứng build/chạy/test; hiện tạm dừng thực hành mới.

<a id="editorial-18"></a>

## Phần lưu: editorial-18

**Chuyển từ:** docs/02-nghiep-vu/PRD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Chuẩn bị [thực hành nền dữ liệu/Bắt đầu](../03-ky-thuat/practice-01-start-session.md) để người dùng tự làm;

**Cách đọc hiện tại:** [Thực hành nền dữ liệu/Bắt đầu](../03-ky-thuat/practice-01-start-session.md#practice-stop) đã có kết quả công cụ/bộ khung, hiện tạm dừng hướng dẫn mới để rà soát tài liệu;

<a id="editorial-19"></a>

## Phần lưu: editorial-19

**Chuyển từ:** docs/03-ky-thuat/TDD.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Chuẩn bị [thực hành đầu tiên](../03-ky-thuat/practice-01-start-session.md) để người dùng tự kiểm môi trường rồi làm từng chặng nền dữ liệu/Bắt đầu; chưa thực hiện ở lượt này.

**Cách đọc hiện tại:** [Thực hành đầu tiên](../03-ky-thuat/practice-01-start-session.md#practice-stop) đã có kết quả công cụ/bộ khung; các chặng nền dữ liệu/Bắt đầu chưa triển khai và hiện tạm dừng hướng dẫn mới để rà soát tài liệu.

<a id="editorial-20"></a>

## Phần lưu: editorial-20

**Chuyển từ:** docs/03-ky-thuat/dfd.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

**Giữ bước thực hành đầu tiên:** người dùng tự thực hiện [mục 1 — kiểm môi trường](../03-ky-thuat/practice-01-start-session.md#practice-env) rồi gửi phiên bản/lỗi thực tế. Lượt DFD không chạy các lệnh đó và không tạo nền ứng dụng.

**Cách đọc hiện tại:** **Điểm dừng thực hành:** [mục 3.0](../03-ky-thuat/practice-01-start-session.md#practice-stop) đã ghi kết quả môi trường và bộ khung backend. Lượt chuẩn hóa không hướng dẫn thực hành mới; DFD không chứng minh ứng dụng đã chạy.

<a id="editorial-21"></a>

## Phần lưu: editorial-21

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

backend và các script Wrapper hiện đã có trong repo theo mục 3.0.2, nhưng distributionUrl còn khác mốc đã chọn và chưa chạy.

**Cách đọc hiện tại:** backend và các script Wrapper đã có; distributionUrl hiện trỏ Maven 3.9.12 theo mục 3.0, chưa có kết quả chạy.

<a id="editorial-22"></a>

## Phần lưu: editorial-22

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Đã có backend ở đúng cấp repo, 12 file đối chiếu khớp ZIP, xem 3.0.2. Wrapper chỉ được đọc cấu hình, còn Maven 3.10.0 thay vì mốc 3.9.12; chưa chạy/build/test

**Cách đọc hiện tại:** Backend ở đúng cấp repo; đã đọc Wrapper trỏ Maven 3.9.12 theo mục 3.0. Trước khi người dùng chỉnh Wrapper từng đối chiếu 12 file khớp ZIP; hiện không tuyên bố toàn bộ file còn giống ZIP. Chưa chạy/build/test

<a id="editorial-23"></a>

## Phần lưu: editorial-23

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

còn Wrapper/build và các phần cần kiểm ở chặng tương ứng.

**Cách đọc hiện tại:** Wrapper đã trỏ 3.9.12 nhưng chưa chạy; còn build và các phần cần kiểm ở chặng tương ứng.

<a id="editorial-24"></a>

## Phần lưu: editorial-24

**Chuyển từ:** docs/03-ky-thuat/practice-01-start-session.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

**Câu trước chuẩn hóa — chỉ lưu dấu vết:**

Tạo/build skeleton và migration phần Bắt đầu theo hướng dẫn được giao sau.

**Cách đọc hiện tại:** Build skeleton và migration phần Bắt đầu theo hướng dẫn được giao sau — bộ khung đã có, chưa có bằng chứng build hoặc migration.



06/11/2026 — biên tập thông tin tổ chức công việc theo phân công nhóm G02; giữ các mốc, bằng chứng kỹ thuật và phạm vi nghiệp vụ của bản lưu.

<a id="official-editorial-20261107"></a>

## Hồ sơ biên tập tài liệu dự án ngày 07/11/2026

Phần này bảo toàn các nhật ký, hồ sơ kiểm chứng và trích đoạn đã chuyển khỏi tài liệu hiện hành. Nội dung trong các khối dưới đây được giữ nguyên từ bản trước biên tập, gồm cả cách diễn đạt và thông tin người thực hiện tại thời điểm ghi nhận; không là quy trình hiện hành hoặc xác nhận tiến độ mới. Các đường dẫn trong khối nguyên văn được hiểu theo file nguồn ghi ở đầu mục. Phân công hiện hành tại decisions G02; quy trình tại handoff mục 7.

Đợt biên tập chuẩn hóa góc nhìn dự án nhóm Thành–Hòa–Nghĩa, giữ yêu cầu, phạm vi, checklist và bằng chứng. Hai câu checklist T01/T08 còn cách gọi bàn giao cũ được giữ nguyên để không thay nội dung issue đã đối chiếu; các phần mô tả và quy trình xung quanh đã được biên tập. Không thực hiện Git/GitHub, sửa code, build/test, database, API ngoài hoặc triển khai trong đợt này.

<a id="official-20261107-readme-root-journal"></a>

### README.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## Lịch sử biên tập

[Lịch sử README trước chuẩn hóa](docs/99-luu-tru/document-history.md#root-readme-before). Ngày 05/11/2026: chuẩn hóa điều hướng và trạng thái, không đổi yêu cầu hoặc sửa backend. Tài liệu tiếp tục cập nhật theo triển khai.

05/11/2026 — đồng bộ trạng thái theo 64 test đã đạt và branch có upstream do người dùng xác nhận; chuẩn bị [phân nhóm commit](docs/01-tong-quan/commit-plan-2026-11-05.md), chưa có bằng chứng commit/push các file hoặc hoàn thành issue.

05/11/2026 — rà lại luồng đọc và dẫn trạng thái Git/quy trình về handoff; giữ bằng chứng 64 test, phạm vi dự thảo và khung dự án hiện có.

06/11/2026 — đồng bộ tên thư mục, repo/Project mới và checklist 89 ô/18 dấu tích đã được người dùng duyệt. Giữ nguyên bằng chứng phần mềm theo ngày chạy; số issue mới và trạng thái Git cần đối chiếu, không thực hiện Git trong lượt tài liệu.

06/11/2026 — theo yêu cầu trực tiếp, Codex đã tạo và đọc lại đủ issue #1–#11 tương ứng T01–T11, khớp tiêu đề/Description/Checklist/89 ô/18 dấu tích. Cập nhật mapping, dọn bản sao issue tạm sau đối chiếu; Project 21 chưa xác minh thêm issue hoặc trạng thái. Không thao tác Git, code/build/test hoặc triển khai.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-readme-journal"></a>

### docs/README.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 8. Nhật ký ngắn

05/11/2026: chuẩn hóa điều hướng/bố cục, chuyển hai file lịch sử và các khối lịch sử dài; cập nhật điểm dừng thực tế sau đọc file chỉ đọc. Giữ mã/phạm vi xác nhận, sơ đồ và tài sản. [Nhật ký tổ chức/đường dẫn các đợt trước](99-luu-tru/document-history.md#docs-index-before) được giữ đủ, không chỉnh lịch sử thành trạng thái hiện nay.

[Danh sách file đã sửa/chuyển/tạo và kiểm tra bảo toàn](99-luu-tru/README.md#normalization-record).

05/11/2026 — bổ sung sau rà soát: bảng đề xuất Q1–Q3 còn sót đã chuyển nguyên khối sang [lịch sử](99-luu-tru/prd-review-history.md#review-q-proposal-table); đánh số lại mục/mục con hiện hành theo thứ tự đọc và đồng bộ dẫn chiếu. Các anchor rõ nghĩa và [bản bàn giao](01-tong-quan/handoff.md) vẫn dùng được; điểm dừng backend không thay đổi, tiếp tục chờ người dùng rà soát.

05/11/2026 — tiếp tục thực hành: cập nhật tag test đã được người dùng sửa và kiểm chỉ đọc xác nhận; liên kết quy ước hướng dẫn ở handoff, đồng bộ trạng thái tiếp tục trong mục lục/thực hành/TDD. Chưa có bằng chứng Wrapper chạy/kéo image/chạy MySQL/build/test; không thay yêu cầu nghiệp vụ.

05/11/2026 — bổ sung bằng chứng Wrapper: nhận đầu ra Maven 3.9.12/Java 21.0.12.1 do người dùng chạy; cập nhật thực hành và handoff. Bước đang chờ là tải image MySQL đúng mốc, chưa chạy MySQL/build/test.

05/11/2026 — bổ sung bằng chứng pull MySQL: người dùng đã tải được mysql:8.4.13, digest lưu tại thực hành mục 1.4; cập nhật handoff và điểm dừng, giao chạy Maven test skeleton. Chưa có kết quả chạy MySQL/build/test, không sửa code/cấu hình hoặc chạy thay người dùng.

05/11/2026 — test skeleton đạt: nhận log BUILD SUCCESS và đối chiếu báo cáo Surefire chỉ đọc; MySQL thực chạy/kết nối, 1 test không lỗi, chưa migration/nghiệp vụ. Bằng chứng tại thực hành mục 1.5; cập nhật điểm dừng và giao xử lý open-in-view, chưa sửa cấu hình/chạy test thay người dùng.

05/11/2026 — đồng bộ chỉ mục, trạng thái backend/Git và đường đọc; bổ sung kế hoạch commit vào bản đồ/cây tài liệu, giữ danh mục sơ đồ và phạm vi chưa kiểm.

06/11/2026 — đồng bộ phân công/người commit theo G02 và trạng thái push tài liệu; yêu cầu tạo lại T01 được ghi trong kế hoạch, chưa thực hiện Git.

06/11/2026 — tiếp nhận kế hoạch repository/Project mới và chat Codex mới; trạng thái chuyển giao ở handoff, link đích/issue mới chờ xác nhận.

06/11/2026 — đồng bộ checklist 89 ô/18 dấu tích đã duyệt, tên thư mục/origin/repo/Project mới, mapping issue và điều kiện dọn bản sao tạm tại handoff. Các đường đọc/sơ đồ giữ nguyên; chưa Git/GitHub hoặc kiểm thử phần mềm.

06/11/2026 — theo yêu cầu trực tiếp, Codex đã tạo và đọc lại đủ issue #1–#11 tương ứng T01–T11, khớp tiêu đề/Description/Checklist/89 ô/18 dấu tích. Cập nhật mapping, dọn bản sao issue tạm sau đối chiếu; Project 21 chưa xác minh thêm issue hoặc trạng thái. Không thao tác Git, code/build/test hoặc triển khai.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-01-tong-quan-technical-inputs-journal"></a>

### docs/01-tong-quan/technical-inputs.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/01-tong-quan/technical-inputs.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### 9.2. Nhật ký

[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-inputs).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |

05/11/2026 — bỏ nhãn phần của prompt cũ khỏi tiêu đề, đồng bộ tiến độ backend và dẫn quy trình hiện hành; không khảo sát lại dịch vụ hoặc thay lựa chọn công nghệ.

06/11/2026 — đồng bộ cách tổ chức theo nhóm và người commit theo G02; giữ công nghệ, mốc mục tiêu và giới hạn kiểm chứng.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-01-tong-quan-roadmap-journal"></a>

### docs/01-tong-quan/roadmap.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/01-tong-quan/roadmap.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 7. Nhật ký và snapshot theo thời điểm

**Tiến độ tài liệu tới 04/11:** LIFE-P01 vé 24 giờ/ledger đã chốt, bỏ generation/gate purge; có protocol/DTO/ERD hiện hành và ma trận V-START01.01–10 chưa chạy. Thực hành 01 đã có kết quả công cụ/Engine Linux và bộ khung backend, Wrapper cấu hình 3.9.12, test còn mysql:latest; chưa có bằng chứng build/chạy/test, hiện tạm dừng hướng dẫn mới; tiến độ theo bằng chứng được cập nhật tại handoff.


[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-roadmap).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |

05/11/2026 — đồng bộ bảng tiến độ với 64 test và trạng thái Git do người dùng cung cấp; chuyển snapshot cũ xuống nhật ký, lồng T01–T11 đúng cấp trong phần backlog. Không đổi checklist/phân công hoặc tự cập nhật GitHub.

06/11/2026 — trình bày phân công/người commit thống nhất cho nhóm; bỏ giả định tổ chức công việc cá nhân khỏi lịch, giữ mốc mục tiêu, phụ thuộc, dự phòng và yêu cầu nghiệm thu.

06/11/2026 — người dùng duyệt checklist mới 89 ô/18 dấu tích. Đồng bộ repo/Project/thư mục, nguồn nháp T08 và cách kiểm CD/Admin; thay link issue cũ ở bảng hiện hành bằng task/mapping chờ URL thật. Trạng thái Project vẫn là đề xuất; chưa thao tác Git/GitHub hoặc chạy test.

06/11/2026 — theo yêu cầu trực tiếp, Codex đã tạo và đọc lại đủ issue #1–#11 tương ứng T01–T11, khớp tiêu đề/Description/Checklist/89 ô/18 dấu tích. Cập nhật mapping, dọn bản sao issue tạm sau đối chiếu; Project 21 chưa xác minh thêm issue hoặc trạng thái. Không thao tác Git, code/build/test hoặc triển khai.

06/11/2026 — người dùng xác nhận Project 21 đã có đủ 11 issue. Giữ mapping #1–#11 và checklist 89 ô/18 dấu tích; trạng thái từng issue chưa đối chiếu, không tạo/thêm lại.

06/11/2026 — người dùng xác nhận đã đặt Status theo hướng dẫn: 6 In Progress, 5 Todo, 0 Done. Cập nhật bảng 6.2 theo xác nhận người dùng; Codex chưa trực tiếp quan sát board. Checklist/mapping không đổi; chuyển sang chuẩn bị bàn giao Git theo trạng thái mới người dùng cung cấp.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-01-tong-quan-handoff-journal"></a>

### docs/01-tong-quan/handoff.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/01-tong-quan/handoff.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 8. Nhật ký theo thời điểm

Các ghi nhận dưới đây giữ nguyên trạng thái lúc ghi, không là chỉ dẫn thực hiện hiện tại. Số mục thực hành 1.n được ghi trong nhật ký cũ nay tương ứng phụ lục A.n, các anchor liên kết được giữ. Mốc hiện tại ở mục 1–2; bằng chứng chi tiết ở bài thực hành.


05/11/2026: tạo bàn giao từ tài liệu và kiểm file chỉ đọc; không thay quyết định hoặc thực hiện phần mềm.

05/11/2026 — tiếp tục thực hành theo yêu cầu người dùng: xác nhận chỉ đọc tag test `mysql:8.4.13` do người dùng sửa; ghi quy ước hướng dẫn tại mục 7, bỏ trạng thái chờ rà soát và giao kiểm phiên bản Wrapper. Đọc checklist T01/T10, chưa đủ ô mới để nhắc tích; không sửa GitHub hoặc chạy phần mềm thay người dùng.

05/11/2026 — nhận đầu ra Wrapper do người dùng chạy: Maven 3.9.12/Java 21.0.12.1 đúng mốc; chưa build/test. Cập nhật bằng chứng và giao kiểm khả năng tải image MySQL đúng tag, chưa có kết quả. Chưa đủ hoàn thành thêm ô checklist tổng hợp; không tự tích hoặc chạy lệnh phần mềm.

05/11/2026 — nhận kết quả pull MySQL 8.4.13 thành công, ghi digest tại thực hành mục 1.4. Kiểm chỉ đọc test skeleton và giao người dùng chạy Maven test; chưa có kết quả chạy MySQL/build/test, chưa thêm ô checklist hoàn thành.

05/11/2026 — nhận log Maven test skeleton BUILD SUCCESS, đối chiếu Surefire chỉ đọc; MySQL 8.4.13 đã chạy/kết nối, Flyway 0 migration. Ghi bằng chứng và giới hạn tại thực hành mục 1.5, đọc lại checklist T01/T10 chưa đủ thêm ô; giao tắt open-in-view và kiểm lại sau thay đổi. Không sửa cấu hình hoặc chạy lệnh thay người dùng.

05/11/2026 — xác nhận chỉ đọc open-in-view=false do người dùng sửa; nhận log kiểm lại BUILD SUCCESS lúc 01:32:54+07:00, Surefire 1/0/0/0 khớp, hết cảnh báo open-in-view. Ghi bằng chứng mục 1.6 và giao chạy thử HTTP qua TestBackendApplication; chưa có kết quả HTTP hoặc thêm ô checklist hoàn thành. Không sửa cấu hình/chạy thay người dùng.

05/11/2026 — nhận ảnh HTTP localhost:8080 trả Whitelabel 404 và DispatcherServlet xử lý; ghi nhận một phần tại thực hành mục 1.7. Chờ log khởi chạy/MySQL/Tomcat và dừng ứng dụng; chưa kết luận toàn nhiệm vụ test-run đạt hoặc nhắc tích checklist.

05/11/2026 — bổ sung log test-run khớp PID/ảnh, MySQL 8.4.13 kết nối và Tomcat 8080 khởi động 09:41:53+07:00. Sửa kỳ vọng tên log sang BackendApplication theo cách gọi từ test launcher; chờ dừng ứng dụng, không yêu cầu chạy lại hoặc mở trang lại.

05/11/2026 — nhận dấu nhắc PowerShell trở lại sau dừng, hoàn tất nhiệm vụ HTTP thử trong phạm vi đã giao; chưa kiểm container cleanup. Đọc T02 #50, chưa có ô mới đủ hoàn thành; ghi mẫu migration guest_session trong thực hành mục 3.2.1 và giao người dùng tự tạo/chạy, chưa có kết quả.

05/11/2026 — người dùng thay quy ước quyền: trợ lý trực tiếp tạo/sửa file trong nhiệm vụ sau đọc/giải thích, người dùng vẫn chạy/kiểm chứng và thao tác môi trường/Git/GitHub/triển khai. Cập nhật vị trí chính mục 7, đồng bộ các dẫn chiếu đang có hiệu lực; giữ nhật ký trước đó là lịch sử. Kiểm V1 chưa tồn tại và tạo SQL theo mẫu/thiết kế đã đối chiếu; chưa chạy migration/test hoặc thêm ô checklist hoàn thành.

05/11/2026 — nhận log V1 applied/schema v1 và test 1/0/0/0 BUILD SUCCESS lúc 10:26:20+07:00, đối chiếu Surefire chỉ đọc. Đọc lại checklist T02 chưa đủ ô mới. Tạo GuestSessionSchemaTests.java với 4 ca ràng buộc, giữ V1 nguyên; chưa chạy test mới, chờ người dùng kiểm.

05/11/2026 — nhận log tổng 5 test đạt BUILD SUCCESS lúc 10:51:11+07:00, đối chiếu hai báo cáo TXT và tên ca trong XML; ghi mục 1.9. Đọc lại T02 chưa đủ ô mới. Thêm Entity/Repository/2 test mapping, sửa cấu hình Hibernate validate/UTC; giữ V1, chưa biên dịch/chạy thay người dùng.

05/11/2026 — người dùng chọn package theo nghiệp vụ/trách nhiệm và cây test tương ứng. Di chuyển 5 file, cập nhật package/import của chúng và tham chiếu tại BackendApplicationTests/TestBackendApplication; TestcontainersConfiguration đổi public để dùng khác package. Cập nhật TDD 3.1, thực hành 3.2.4 và đường dẫn/bàn giao; không thêm lớp rỗng, không đổi test/mapping/V1, không chạy test. Chờ người dùng clean test, kỳ vọng 7 ca; hai test Repository vẫn chưa có bằng chứng chạy.

05/11/2026 — người dùng thay lựa chọn package sang chia theo lớp trước; thay lựa chọn theo nghiệp vụ/trách nhiệm ở mục nhật ký ngay trên. Di chuyển 4 file từ guestsession vào entity/repository (nguồn) và repository/schema (test), cập nhật package/import; cấu hình support public và hai file test/launcher gốc giữ nguyên. Cập nhật TDD 3.1, thực hành 3.2.4, README tài liệu và bàn giao; không thay mapping/nội dung test/V1 hoặc tạo lớp rỗng. Chưa chạy build/test, hai test Repository chưa có bằng chứng; chờ người dùng clean test, kỳ vọng tổng 7 ca.

05/11/2026 — nhận log người dùng tổng 7/0/0/0 BUILD SUCCESS lúc 13:35:16+07:00, Maven 01:19 phút. Đối chiếu chỉ đọc 3 báo cáo Surefire TXT/XML: context 1, Repository 2, Schema 4; scan 1 Repository, MySQL 8.4.13/Flyway v1. Ghi thực hành mục 1.10 và đồng bộ trạng thái TDD/README/bàn giao; Entity/Repository/validate/UTC và package theo lớp đã kiểm đạt trong bộ test. Đọc lại T02 #50 chưa đủ ô mới để nhắc tích; không chạy lệnh hoặc sửa code/GitHub. Chặng tiếp theo theo lộ trình là P01-TICKET, chưa triển khai.

05/11/2026 — tiếp tục P01-TICKET theo yêu cầu: đọc DATA-05/API 9.1–9.2/operations 1.1 và mã hiện có; thêm StartTicketTimePolicy nhận Clock cùng 8 unit test service. Chỉ phần thời gian: +24 giờ, issuedAt không tương lai, biên expiry riêng cho nhánh tạo đầu; chưa HMAC/credential/khóa ký/epoch runtime/Spring bean/API. Giữ nguyên toàn mã/config/migration/test cũ; cập nhật thực hành 3.2.5, TDD cây hiện có, README và bàn giao. Chưa biên dịch/chạy, chưa có ô checklist mới đủ căn cứ; chờ người dùng chạy riêng 8 ca mới.

05/11/2026 — nhận log unit StartTicketTimePolicyTests 8/0/0/0 BUILD SUCCESS lúc 13:57:40+07:00, Maven 16.769 giây; đối chiếu Surefire TXT/XML khớp 0.451 giây/đủ 8 tên ca, ghi thực hành 1.11. Tiếp tục bộ ký HmacSHA256 nhận khóa constructor, thêm StartTicketSigner và 9 unit test gồm RFC 4231 case6; chưa biên dịch/chạy mới, chưa định dạng vé/runtime key/epoch/API. Giữ mã/config/V1/test cũ, cập nhật thực hành 3.2.6/TDD/README/bàn giao; chờ người dùng chạy riêng 9 ca, chưa đủ ô checklist mới.

05/11/2026 — nhận log riêng StartTicketSignerTests 9/0/0/0 BUILD SUCCESS lúc 14:07:51+07:00, Maven 15.487 giây; đối chiếu TXT/XML khớp 0.505 giây/đủ 9 tên ca, ghi thực hành 1.12. Tiếp tục StartTicketCodec và 14 ca mới theo format v1/encoding cố định tại operations; chưa chạy mới, chưa binding/runtime key/epoch/Service/API. Giữ toàn file backend cũ, đồng bộ thực hành 3.2.7/TDD/README/operations/bàn giao; chờ người dùng kiểm riêng codec, chưa đủ ô checklist mới.

05/11/2026 — người dùng bổ sung comment tiếng Việt, chuẩn bị branch/bàn giao commit theo issue và điểm dừng sau mục checklist; cấp trợ lý chạy riêng StartTicketCodecTests. Cập nhật quy ước chính mục 7; rà 14 file Java, sửa comment/Javadoc trong 11 file, đối chiếu tĩnh giữ logic/assertion và V1/config/pom. Trợ lý chạy 14/0/0/0 BUILD SUCCESS, mã thoát 0, lúc 14:32:52+07:00; Surefire khớp, không lỗi cần sửa. Đọc T03 #51 chưa đủ ô mới để nhắc tích; T03 do Thành phụ trách và commit. Người dùng cung cấp trạng thái main và remote chưa có feat/t03-start-ticket; giao người dùng tạo/push branch giữ sửa đổi, chưa có bằng chứng đã làm. Đồng bộ thực hành 1.13/TDD/operations/README và dẫn roadmap; dừng chờ xem/xác nhận tiếp tục, không tự chạy Git hoặc thêm chức năng.

05/11/2026 — người dùng đã xem 14 ca codec đạt và làm rõ quyền kiểm thử thường xuyên trong nhiệm vụ, gồm full suite khi cần/Testcontainers/database tạm tách biệt/tải dependency và image từ nguồn đã cấu hình; giữ Git/GitHub/database đang dùng/TTS/triển khai cho người dùng. Điểm dừng mặc định chuyển sang sau toàn mục checklist có bằng chứng, không từng class/unit test. Cập nhật quy ước chính mục 7 và dẫn chiếu/điểm dừng hiện hành; chưa có kết quả branch thành công, backend/docs vẫn untracked theo bằng chứng đã gửi. Nhận branch thành công sẽ tự chọn mục khả thi và tiếp tục, không gom toàn thay đổi thành T03. Lượt này chỉ sửa tài liệu, không chạy lại test hoặc thao tác Git.

05/11/2026 — nhận xác nhận người dùng đã tạo/push/upstream feat/t03-start-ticket; giữ trạng thái README modified, backend/docs untracked/chưa commit. Chọn mục local API ba lớp/lookup hai nhánh; thêm Controller/Service/DTO/config và test, giữ V1/mapping/nền. Chạy test mới 18 ca đạt rồi clean test đủ 64/0/0/0 BUILD SUCCESS, mã thoát 0 lúc 16:15:34+07:00 trên MySQL 8.4.13 tạm. Ghi bằng chứng/giới hạn tại thực hành 1.14 và dừng sau toàn mục đã chọn. Đọc lại T03 #51 còn thiếu K04/phạm vi đầy đủ nên chưa nhắc tích; không Git/live DB/TTS/triển khai. Đề nghị trạng thái Git chi tiết và diff README để phân nhóm mốc commit.

05/11/2026 — nhận danh sách Git chi tiết từ người dùng: README modified, 37 backend/29 docs untracked. Diff README bị dừng ở pager, đọc đủ file và sửa các dòng trạng thái cũ theo bằng chứng 64 test; không sửa code/test. Lập kế hoạch commit bốn nhóm có đường dẫn chính xác và hướng dẫn nhóm tài liệu trước, không gộp toàn bộ thành T03; người commit theo phân công task. Kế hoạch thêm một file docs mới, nhóm tài liệu gồm 31 file cả README. Chưa stage/commit/push hoặc thêm ô checklist GitHub hoàn thành; không chạy Git/build/test trong lượt chỉ sửa tài liệu.

05/11/2026 — người dùng xác nhận tên nhánh T01–T11 và thông điệp Txx: Những việc đã thực hiện; T01 đã tạo/push/upstream, nhánh feat/t03-start-ticket đã xóa local/remote. Đã stage 31 file tài liệu nhưng chưa commit; giao rà soát/chỉnh tài liệu trước commit. Sắp lại handoff, bài thực hành và nguồn dẫn; cập nhật phong cách comment // theo xác nhận mới, không sửa code hoặc thực hiện Git/test trong lượt biên tập.

06/11/2026 — thống nhất tài liệu dự án nhóm đi chấm, chỉ ghi người phụ trách/người commit theo G02. Ghi nhận 55b0906 đã push và yêu cầu tạo lại T01 sau khi bảo toàn tài liệu; chưa thực hiện Git hoặc sửa code trong lượt này.

06/11/2026 — người dùng chọn repository mới trên tài khoản hiện tại, Project mới và chat Codex mới, giữ thư mục và nội dung issue. Dừng hướng tạo lại T01 ở repo cũ; cập nhật trạng thái detached HEAD theo lệnh đã gửi, chưa thực hiện chuyển hệ thống.

06/11/2026 — nhận URL repo AudioQR_Kinh-thanh-Hue và Project 21; kiểm repo public, size 0 và issue rỗng. Lưu nguyên bản 11 issue trong 99-luu-tru để chuẩn bị tạo lại; chưa tạo issue, đổi remote hoặc push, chưa kiểm được bảng Project.

06/11/2026 — đồng bộ thư mục AudioQR_Kinh-thanh-Hue, origin mới theo đầu ra người dùng và checklist 89 ô/18 dấu tích đã duyệt. Bỏ trạng thái detached/“chưa đổi remote” khỏi hướng dẫn hiện hành, giữ nhật ký cũ. Ghi điều kiện xóa snapshot sau đối chiếu đủ issue; chưa Git/GitHub, sửa backend hoặc chạy test.

**Kiểm tra đợt đồng bộ 06/11/2026:** 13 file Markdown được cập nhật; 26 file Markdown/1.148 liên kết nội bộ không có đích hoặc anchor thiếu. Checklist vẫn 89 ô/18 dấu tích; 11 tiêu đề/Description, 15 khối Mermaid hiện hành và 52 file backend/tài sản/lịch sử được đối chiếu giữ nguyên. Bản sao issue gốc vẫn 9/78. Chỉ kiểm tra tài liệu, không chạy build/test, Git/GitHub hoặc sửa cấu hình/code.

06/11/2026 — người dùng gửi đầu ra: chuyển `.git` cũ tới `C:\Study\Git-backups\AudioQR_Kinh-thanh-Hue-20261106\.git`, khởi tạo Git mới và origin đúng. Main chưa có commit; `.gitignore`, README.md, backend/, docs/ đều untracked, chưa stage/commit/push. Không chạy lại thao tác khởi tạo. Trợ lý chỉ cập nhật tài liệu, không thực hiện Git.

06/11/2026 — theo yêu cầu trực tiếp, Codex đã tạo và đọc lại đủ issue #1–#11 tương ứng T01–T11, khớp tiêu đề/Description/Checklist/89 ô/18 dấu tích. Cập nhật mapping, dọn bản sao issue tạm sau đối chiếu; Project 21 chưa xác minh thêm issue hoặc trạng thái. Không thao tác Git, code/build/test hoặc triển khai.

**Kiểm tra sau tạo issue:** mục 3 roadmap giữ nguyên; 25 Markdown (README gốc +24 trong docs) có 1.142 liên kết nội bộ được kiểm, không thiếu đích/anchor. Không còn dẫn chiếu tới bản sao issue đã xóa; nhóm tài liệu còn 31 file. Đây là kiểm tra tài liệu, không phải chạy kiểm thử phần mềm.

06/11/2026 — người dùng xác nhận “Project đã có 11 issue”. Ghi nhận Project 21 đủ 11 issue theo báo cáo người dùng; chưa trực tiếp quan sát board hoặc đối chiếu trạng thái từng issue. Không tạo/thêm lại issue, không thao tác Git hoặc code/test.

06/11/2026 — người dùng báo “Đã xog” sau hướng dẫn đặt Status. Ghi nhận In Progress T01/T02/T03/T07/T08/T10; Todo T04/T05/T06/T09/T11; Done 0 theo xác nhận người dùng, chưa quan sát board trực tiếp. Hoàn tất chuyển giao issue/Project; chuẩn bị nhận đầu ra Git mới trước hướng dẫn commit, không thao tác Git hoặc code/test.

07/11/2026 — người dùng xác nhận cách hướng dẫn Git theo trọn nhóm commit trong một câu trả lời, có ba khối và các mốc gửi kết quả, không mặc định mỗi lượt một lệnh. Đã nhận tác giả Git tên KhaiThanh; vẫn chưa xác định người trực tiếp làm T10/danh tính thuộc ai. Cập nhật quy trình/điểm dừng và chuẩn bị đủ hướng dẫn mốc khởi tạo main chỉ .gitignore; chưa stage/commit/push, không tự chạy Git, sửa code/test hoặc thay đổi GitHub.

07/11/2026 — người dùng xác nhận KhaiThanh là chính mình (Thành); Hòa dùng gingpham, tài khoản còn lại trong ảnh Collaborators là Nghĩa (Chí Nghĩa/lechiNghiaa). Ghi ánh xạ tại G02, không suy username thành author Git hoặc bằng chứng thực hiện công việc. Mốc T10 chờ Hòa trực tiếp thực hiện bằng danh tính của Hòa; chưa stage/commit/push hoặc thay đổi GitHub.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.

**Rà soát tài liệu theo tiêu chí tổ chức nhóm:** kiểm toàn bộ 25 Markdown của đồ án và HELP.md sinh sẵn; kiểm kê sáu tài sản hình/SVG/JSON/GeoJSON. G02/roadmap đã có đủ ba thành viên; cập nhật chín tài liệu ở phần tổ chức, hướng dẫn Git và điểm dừng để kế hoạch giữ ba người/ba máy, thực hành ghi đúng Thành thao tác. Đối chiếu hash cho thấy 59 file ngoài phạm vi sửa không đổi, gồm backend, tài sản và các bản lịch sử; HELP.md giữ nguyên. Kiểm 1.152 liên kết nội bộ không thiếu đích/anchor, khối mã đóng đủ; 11 issue/89 ô/18 dấu tích và nội dung issue ở roadmap mục 3 giữ nguyên. Không thay thiết kế nghiệp vụ, code, test, Git hoặc GitHub trong lượt này.
````

<a id="official-20261107-01-tong-quan-commit-plan-2026-11-05-journal"></a>

### docs/01-tong-quan/commit-plan-2026-11-05.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/01-tong-quan/commit-plan-2026-11-05.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 5. Dấu vết thay đổi kế hoạch

05/11/2026 — kế hoạch cũ dùng feat/t03-start-ticket cho các nhóm và thông điệp có số issue. Người dùng thay bằng nhánh T01–T11, thông điệp tiếng Việt Txx: Những việc đã thực hiện; xác nhận nhánh T01 và xóa tên cũ. Kế hoạch được cập nhật trước commit, không tự thực hiện Git hoặc sửa code.

**Kiểm tra biên tập ngày 05/11/2026:** rà 25 file Markdown, kiểm tĩnh 1.115 liên kết nội bộ không có đích/anchor thiếu; giữ nguyên 15 khối Mermaid, 114 dòng quy tắc/yêu cầu/tiêu chí và 14 nội dung Use Case trong PRD. Đối chiếu hash 52 file backend/lưu trữ/tài sản được bảo vệ không phát hiện thay đổi; nhóm staging vẫn đúng 31 file. Đây là kiểm tra tài liệu, không chạy lại phần mềm hoặc xác nhận commit/push.

06/11/2026 — chuẩn hóa phân công/người commit theo task và ghi yêu cầu tạo lại T01 từ nền main sau khi bảo toàn file. Commit 55b0906 là bằng chứng bản tài liệu trước đã push; chưa có bằng chứng xóa/tạo lại T01 hoặc commit bản mới.

06/11/2026 — cập nhật kế hoạch theo quyết định tạo repository/Project mới; bỏ hướng dẫn thực thi nhánh cũ khỏi phần hiện hành, chờ URL đích và phương án giữ issue/lịch sử Git. Chưa thực hiện Git.

06/11/2026 — đích repo/Project đã nhận; bổ sung snapshot issue vào nhóm tài liệu dự kiến. Lịch sử Git cục bộ/remote và issue mới chưa được thay đổi.

06/11/2026 — đồng bộ thư mục và origin mới theo đầu ra người dùng, checklist đã duyệt 89 ô/18 dấu tích và bước tạo issue trước staging. Dẫn task sang roadmap/mapping chờ URL thật, giữ số file theo thời điểm và điều kiện xóa snapshot; không chạy Git.

06/11/2026 — người dùng gửi đầu ra: chuyển `.git` cũ tới `C:\Study\Git-backups\AudioQR_Kinh-thanh-Hue-20261106\.git`, khởi tạo Git mới và origin đúng. Main chưa có commit; `.gitignore`, README.md, backend/, docs/ đều untracked, chưa stage/commit/push. Không chạy lại thao tác khởi tạo. Trợ lý chỉ cập nhật tài liệu, không thực hiện Git.

06/11/2026 — theo yêu cầu trực tiếp, Codex đã tạo và đọc lại đủ issue #1–#11 tương ứng T01–T11, khớp tiêu đề/Description/Checklist/89 ô/18 dấu tích. Cập nhật mapping, dọn bản sao issue tạm sau đối chiếu; Project 21 chưa xác minh thêm issue hoặc trạng thái. Không thao tác Git, code/build/test hoặc triển khai.

07/11/2026 — người dùng xác nhận cách hướng dẫn Git theo trọn nhóm commit trong một câu trả lời, có ba khối và các mốc gửi kết quả, không mặc định mỗi lượt một lệnh. Đã nhận tác giả Git tên KhaiThanh; vẫn chưa xác định người trực tiếp làm T10/danh tính thuộc ai. Cập nhật quy trình/điểm dừng và chuẩn bị đủ hướng dẫn mốc khởi tạo main chỉ .gitignore; chưa stage/commit/push, không tự chạy Git, sửa code/test hoặc thay đổi GitHub.

07/11/2026 — đã xác nhận KhaiThanh thuộc Thành, người đang thao tác. Hòa dùng GitHub gingpham, Nghĩa dùng lechiNghiaa theo G02; chưa có author/committer Git của hai người này. Mốc khởi tạo T10 chờ Hòa trực tiếp tiếp nhận và kiểm danh tính của mình; chưa stage/commit/push, không đổi người commit hoặc thao tác Git/GitHub thay người dùng.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-02-nghiep-vu-prd-journal"></a>

### docs/02-nghiep-vu/PRD.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/02-nghiep-vu/PRD.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 11. Nhật ký PRD

[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-prd).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |

05/11/2026 — rút gọn tiêu đề, phân biệt điểm mở với phạm vi đã xác nhận và đồng bộ tiến độ thực hành; giữ nguyên BR/DR/FR/UC/AC/NFR và mức duyệt.
````

<a id="official-20261107-02-nghiep-vu-prd-review-journal"></a>

### docs/02-nghiep-vu/prd-review.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/02-nghiep-vu/prd-review.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 8. Nhật ký báo cáo

[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-review).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |
| 05/11/2026 | Chuyển nguyên bảng đề xuất Q1–Q3 còn sót sang lịch sử; đánh số lại mục con và dẫn chiếu. | Q1–Q3 giữ trạng thái đã xác nhận; R06 còn mở đúng phần, không đổi nghiệp vụ. |

05/11/2026 — bỏ dẫn số dòng dễ lỗi, phân biệt kết luận rà soát yêu cầu với kết quả backend mới; không đóng R06 hoặc tự tích checklist.
````

<a id="official-20261107-02-nghiep-vu-decisions-journal"></a>

### docs/02-nghiep-vu/decisions.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/02-nghiep-vu/decisions.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
<a id="dec-history"></a>

| Ngày | Biên tập | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Tách lịch sử/căn cứ dài sang 99-luu-tru, rút gọn mở đầu và cập nhật dẫn chiếu. | Không thêm quyết định nghiệp vụ/kỹ thuật, không thay mã hoặc phạm vi duyệt. |

05/11/2026 — sắp chỉ mục theo thứ tự nhóm/nội dung, cập nhật dẫn chiếu quy trình tại handoff; giữ mã, nội dung quyết định, anchor và phạm vi đã xác nhận.

06/11/2026 — thống nhất tài liệu cho dự án nhóm đi chấm; cập nhật G02 và người commit theo task, giữ nguyên phạm vi nghiệp vụ và trạng thái kiểm chứng.
````

<a id="official-20261107-02-nghiep-vu-business-flow-journal"></a>

### docs/02-nghiep-vu/business-flow.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/02-nghiep-vu/business-flow.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 8. Nhật ký

[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-bf).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |
````

<a id="official-20261107-04-giao-dien-ui-design-journal"></a>

### docs/04-giao-dien/ui-design.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/04-giao-dien/ui-design.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## 13. Nhật ký và dấu vết thay thế

Nhật ký ở mục 13, sau toàn bộ UI khách/Admin và phần còn mở; các anchor rõ nghĩa được giữ để truy vết. [Nhật ký đầy đủ và dấu vết thay thế](../99-luu-tru/document-history.md#journal-ui); [bản phác thảo khách cũ](../99-luu-tru/ui-design-history.md).

| Ngày | Biên tập | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Đặt toàn bộ phần UI trước nhật ký, tách lịch sử và rút gọn mở đầu. | Giữ nguyên màn hình, hướng đã chốt, điểm mở, sơ đồ và tài sản. |
| 05/11/2026 | Đánh số theo thứ tự đọc 1–13 và đồng bộ mục lục/câu dẫn. | Giữ mã UI, hành vi, anchor rõ nghĩa và thiết kế hiện có. |

05/11/2026 — dùng tiêu đề theo nhóm chức năng khách/Admin thay nhãn lượt trao đổi, giữ mã UI, trạng thái duyệt và nội dung hành vi.
````

<a id="official-20261107-03-ky-thuat-tdd-journal"></a>

### docs/03-ky-thuat/TDD.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/TDD.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### Nhật ký biên tập

[Lịch sử các lượt trước](../99-luu-tru/document-history.md#journal-tdd).

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa mở đầu, lịch sử và liên kết; ghi đúng điểm dừng thực hành. | Không đổi giải pháp/nghiệp vụ hoặc coi đã chạy phần mềm. |

05/11/2026 — dẫn trạng thái Git về handoff, giữ nội dung thiết kế, package theo lớp và bằng chứng 64 test; không sửa backend hoặc coi toàn TDD đã được duyệt.
````

<a id="official-20261107-03-ky-thuat-practice-01-start-session-journal"></a>

### docs/03-ky-thuat/practice-01-start-session.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/practice-01-start-session.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
<a id="practice-history"></a>

## Nhật ký biên tập

[Lịch sử các lượt trước](../99-luu-tru/document-history.md#journal-practice).

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa mở đầu, lịch sử và liên kết; ghi đúng điểm dừng thực hành. | Không đổi giải pháp/nghiệp vụ hoặc coi đã chạy phần mềm. |

05/11/2026 — sắp thứ tự mục tiêu → môi trường → triển khai → cách kiểm → ôn tập → phụ lục bằng chứng; giữ các anchor và nội dung kết quả, không chạy lại phần mềm.

06/11/2026 — dùng hướng dẫn chung cho nhóm và người phụ trách/commit theo task, giữ nguyên các bước, lệnh và bằng chứng kỹ thuật.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
````

<a id="official-20261107-03-ky-thuat-operations-design-journal"></a>

### docs/03-ky-thuat/operations-design.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/operations-design.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### Nhật ký biên tập

[Lịch sử các lượt trước](../99-luu-tru/document-history.md#journal-operations).

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa mở đầu, lịch sử và liên kết; ghi đúng điểm dừng thực hành. | Không đổi giải pháp/nghiệp vụ hoặc coi đã chạy phần mềm. |

05/11/2026 — bổ sung trạng thái từng phần V-START01 theo bằng chứng backend đã có, giữ nguyên tình huống/kết quả cần kiểm và phần chưa thực hiện.
````

<a id="official-20261107-03-ky-thuat-dfd-journal"></a>

### docs/03-ky-thuat/dfd.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/dfd.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### 8.4. Nhật ký

[Lịch sử trước chuẩn hóa](../99-luu-tru/document-history.md#journal-dfd).

| Ngày | Thay đổi | Phạm vi |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa bố cục, lịch sử và dẫn chiếu. | Không đổi yêu cầu, mã hoặc phạm vi duyệt; không kiểm thử phần mềm. |

05/11/2026 — đồng bộ dẫn trạng thái V-START01 theo từng phần; không đổi nguồn Mermaid, SVG hoặc cân bằng luồng dữ liệu.
````

<a id="official-20261107-03-ky-thuat-data-model-journal"></a>

### docs/03-ky-thuat/data-model.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/data-model.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### Nhật ký biên tập

[Lịch sử các lượt trước](../99-luu-tru/document-history.md#journal-model).

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa mở đầu, lịch sử và liên kết; ghi đúng điểm dừng thực hành. | Không đổi giải pháp/nghiệp vụ hoặc coi đã chạy phần mềm. |

05/11/2026 — đồng bộ trạng thái V1/mapping/concurrent với bằng chứng backend, giữ nguyên ba ERD và ràng buộc thiết kế.
````

<a id="official-20261107-03-ky-thuat-api-transactions-journal"></a>

### docs/03-ky-thuat/api-transactions.md — Nhật ký và ghi nhận trước biên tập

Nguồn: `docs/03-ky-thuat/api-transactions.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### Nhật ký biên tập

[Lịch sử các lượt trước](../99-luu-tru/document-history.md#journal-api).

| Ngày | Thay đổi | Giới hạn |
| --- | --- | --- |
| 05/11/2026 | Chuẩn hóa mở đầu, lịch sử và liên kết; ghi đúng điểm dừng thực hành. | Không đổi giải pháp/nghiệp vụ hoặc coi đã chạy phần mềm. |
| 05/11/2026 | Đánh số DTO thành mục 9, truy vết thành mục 10 và cập nhật dẫn chiếu. | Không đổi mã API, hợp đồng, giao dịch hoặc sơ đồ. |
````

<a id="official-20261107-readme-md-context"></a>

### README.md — Phần dẫn nhập và quy trình trước biên tập

Nguồn: `README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
# Hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế

Đồ án học phần: khách truy cập qua một QR chung, chọn vùng trên bản đồ để đọc/nghe; Admin quản lý địa điểm, nội dung/audio, vùng, phiên và thống kê.

**Tổ chức đồ án:** thiết kế và phân công cho nhóm ba thành viên theo [G02](docs/02-nghiep-vu/decisions.md#g02), kế hoạch cộng tác trên ba máy. Trong lượt thực hành bản chấm, Thành trực tiếp thao tác; tác giả commit và nhật ký ghi đúng thực tế theo [quy ước thực hành](docs/01-tong-quan/handoff.md#execution-mode).

**Cập nhật tài liệu 07/11/2026; bằng chứng phần mềm ngày 05/11/2026:** nghiệp vụ và phạm vi yêu cầu đã rõ được duyệt; hướng UI/công nghệ được xác nhận đúng phần, tài liệu kỹ thuật còn dự thảo. Backend dùng Java 21/Maven Wrapper 3.9.12/MySQL 8.4.13 Testcontainers; đã có V1/mapping và API K11/K01/K02. [Bộ 64 test hồi quy đạt](docs/03-ky-thuat/practice-01-start-session.md#practice-start-api-result) lúc 16:15:34+07:00; chưa toàn T03, frontend hoặc kiểm triển khai/backup/restore thực. Trạng thái Git và việc tiếp theo xem [handoff](docs/01-tong-quan/handoff.md#next-step); bằng chứng trên thuộc cây làm việc được kiểm, không suy các file đã commit/push.

Repository hiện hành: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue). Bảng công việc: [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Thư mục hiện hành: `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`. Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. Đã tạo và đọc lại đủ 11 issue mới (#1–#11 tương ứng T01–T11), khớp tiêu đề/Description/Checklist; mapping tại roadmap mục 6. Người dùng xác nhận ngày 06/11/2026: Project 21 đã có đủ 11 issue và đã đặt Status theo hướng dẫn: 6 In Progress, 5 Todo, 0 Done; đây là xác nhận của người dùng, Codex chưa trực tiếp quan sát board. [Checklist đã duyệt](docs/01-tong-quan/roadmap.md#t01): 11 task, 89 ô/18 ô đã tích; trạng thái Project được ghi theo xác nhận người dùng tại roadmap mục 6.

Bắt đầu tại **[mục lục và tìm nhanh](docs/README.md)**. Chat tiếp nhận đọc [bàn giao và điểm dừng](docs/01-tong-quan/handoff.md), không giả định cùng lịch sử hoặc phiên bản.
````

<a id="official-20261107-docs-01-tong-quan-handoff-md-context"></a>

### docs/01-tong-quan/handoff.md — Phần dẫn nhập và quy trình trước biên tập

Nguồn: `docs/01-tong-quan/handoff.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
# Bàn giao — tiến độ và quy trình cộng tác

**Cập nhật:** 07/11/2026. Đây là trạng thái để tiếp tục hoặc đổi chat, không phải phê duyệt toàn TDD hay kết luận nghiệm thu. Quy trình cộng tác có một vị trí chính tại [mục 7](#guidance-protocol); nhật ký các mốc cũ ở cuối file.

## 1. Hiện đã làm được gì

| Phần | Bằng chứng hiện có | Giới hạn |
| --- | --- | --- |
| Môi trường và nền backend | Java 21.0.12.1, Maven Wrapper 3.9.12, Boot 4.1.2; MySQL 8.4.13 chạy trong Testcontainers. V1, Entity/Repository và cấu hình validate/UTC đã được kiểm. | Database kiểm thử tạm; chưa toàn schema hoặc môi trường phục vụ bền vững. |
| API Bắt đầu và trạng thái | K11 cấp vé ký 24 giờ; K01 tạo/replay phiên cố định 8 giờ; K02 kiểm trạng thái. Có Controller → Service → Repository, binding/ledger/expiry và UNIQUE concurrent. | Chưa K04 kết thúc, frontend/owner tab, runtime thật/restart với ledger bền, D08 hoặc backup/restore. |
| Kiểm thử mới nhất | `clean test`: **64 test, 0 lỗi, 0 bỏ qua, BUILD SUCCESS**, mã thoát 0 lúc **05/11/2026 16:15:34 +07:00**, đối chiếu 9 báo cáo Surefire. [Bằng chứng](../03-ky-thuat/practice-01-start-session.md#practice-start-api-result). | Kết quả của toàn cây làm việc tại lần chạy đó, không chứng minh mọi AC hoặc từng commit trung gian đã đạt. |
| Thư mục, Git và tài liệu | Theo đầu ra người dùng: main chưa có commit, bốn mục untracked; origin đúng repo mới, remote chưa có nhánh. Đã nhận tên Git KhaiThanh thuộc Thành; đầu ra mới sau đổi cấu hình cho thấy author/committer gingpham. Chưa có bằng chứng stage/commit/push. | Xác nhận mới: Thành trực tiếp thao tác mọi task trong lượt thực hành, G02 vẫn là phân công theo mô hình ba người. Trước T10, Thành khôi phục danh tính Git của mình và gửi mốc A; không chờ Hòa tiếp nhận. Lịch sử 55b0906/T01 thuộc bản sao Git cũ. |

<a id="next-step"></a>

## 2. Điểm dừng và việc tiếp theo

**Đích hiện hành:** thư mục `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`, repo [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue) và [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Tên thư mục đã đổi, nội dung dự án được giữ lại. Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. Đã tạo và đọc lại đủ issue mới #1–#11 tương ứng T01–T11 theo yêu cầu trực tiếp ngày 06/11; khớp tiêu đề/Description/Checklist. [Mapping](roadmap.md#github-checklist-review) đã cập nhật. Người dùng xác nhận ngày 06/11/2026: Project 21 đã có đủ 11 issue và đã đặt Status theo hướng dẫn: In Progress T01/T02/T03/T07/T08/T10; Todo T04/T05/T06/T09/T11; Done 0. Bằng chứng là xác nhận người dùng, Codex chưa trực tiếp quan sát board.

<a id="issue-recreation"></a>

### 2.1. Tạo lại issue theo checklist đã duyệt

- **Đã duyệt:** 11 task, **89 ô/18 dấu tích**, nguyên văn hiện hành tại [roadmap mục 3](roadmap.md#t01), thống kê/căn cứ và mapping tại [mục 6](roadmap.md#github-checklist-review). Mỗi issue chỉ có Description và một Checklist; chưa cần chia nhỏ thêm. 18/89 không là phần trăm hoàn thành.
- **Trạng thái Project theo người dùng xác nhận:** In Progress T01/T02/T03/T07/T08/T10; Todo T04/T05/T06/T09/T11; Done 0. Người dùng báo đã đặt xong ngày 06/11; Codex chưa trực tiếp quan sát board.
- **Đã thực hiện:** theo yêu cầu trực tiếp ngày 06/11, Codex tạo đủ T01–T11 thành #1–#11, đọc lại từng issue và đối chiếu đúng tiêu đề/Description/Checklist/89 ô/18 dấu tích. Không tạo lặp hoặc gán assignee.
- **Project:** người dùng xác nhận ngày 06/11/2026 đã có đủ 11 issue trên Project 21. Không cần tạo hoặc thêm lại. Người dùng tiếp tục báo đã đặt xong Status theo hướng dẫn. Chuyển giao issue/Project đã xong theo xác nhận này; Codex chưa trực tiếp quan sát board.
- **Dọn bản sao tạm:** đã đủ 11 issue và đối chiếu xong nội dung đã duyệt, nên đã xóa bản sao issue gốc, gỡ dẫn chiếu và kiểm kê nhóm tài liệu còn 31 file theo quyền đã cấp. Không stage/commit; #49–#59 chỉ là số truy vết repo cũ.
- Năm bản nháp tiếng Việt đã được người dùng gửi trong chat trước; chưa xác minh bản lưu trong repo/rà soát/duyệt. Chat mới không có lịch sử đó thì yêu cầu bản lưu khi đến T08; không tự tái tạo hoặc coi là nội dung được duyệt.

### 2.2. Sau bước chuyển giao

- K11/K01/K02 và lookup hai nhánh có bằng chứng; còn K04 và phần đầy đủ của mục T03. Chưa nhắc tích toàn ô bắt đầu/kết thúc/kiểm phiên hoặc kiểm API.
- [Kế hoạch commit](commit-plan-2026-11-05.md) chỉ giữ phân nhóm. Khi tới mốc Git, yêu cầu đầu ra mới từ người dùng rồi hướng dẫn; dừng hướng tạo lại T01 của repo cũ. Không tự Git/reset/đổi remote/xóa .git.
- Đã có main chưa commit, bốn mục untracked, remote chưa có nhánh; author/committer hiện cấu hình gingpham sau thao tác đổi danh tính. Theo xác nhận mới, Thành trực tiếp làm tất cả thao tác; G02 và kế hoạch ba máy vẫn là mô hình tổ chức. Bước hiện tại: Thành khôi phục cấu hình Git của chính mình, kiểm lại do cấu hình đã đổi, stage riêng `.gitignore`, gửi mốc A trước commit. Sau đối chiếu đạt, commit/push/kiểm mốc B theo nhóm; tiếp đến các nhóm T01/T02/T03 có thật. Chưa có bằng chứng stage/commit/push.
- Sau khi các nhóm sản phẩm hiện có được bàn giao Git, Codex tiếp tục triển khai theo nhiệm vụ đã giao và [nhịp thực hành bản chấm](#execution-mode): tự chọn mục khả thi, code/test tách biệt; dừng sau toàn mục checklist hoặc issue đủ bằng chứng để cùng kiểm thử nhẹ và commit.
- Nhánh `T01`–`T11`, commit `Txx: Những việc đã thực hiện`; người phụ trách/người commit theo G02. Comment theo `//` tiếng Việt tại mục 7.1; chưa chuyển toàn code sang phong cách mới.

## 3. Tài liệu cần đọc

| Khi cần | Tài liệu |
| --- | --- |
| Tìm nhanh nội dung và sơ đồ | [docs/README.md](../README.md) |
| Hiểu nghiệp vụ và quyết định | [Business Flow](../02-nghiep-vu/business-flow.md), [decisions](../02-nghiep-vu/decisions.md) |
| Xác định yêu cầu và nghiệm thu | [PRD](../02-nghiep-vu/PRD.md), [phần mở R06](../02-nghiep-vu/prd-review.md#review-open-final) |
| Triển khai giao diện | [UI khách/Admin](../04-giao-dien/ui-design.md) |
| Học thiết kế và triển khai backend | [TDD](../03-ky-thuat/TDD.md), [model](../03-ky-thuat/data-model.md), [API](../03-ky-thuat/api-transactions.md), [operations](../03-ky-thuat/operations-design.md), [DFD](../03-ky-thuat/dfd.md) |
| Kiểm tiến độ và lập kế hoạch | [roadmap](roadmap.md), [đầu vào kỹ thuật](technical-inputs.md) |
| Tiếp tục lát cắt Bắt đầu | [Thực hành 01](../03-ky-thuat/practice-01-start-session.md#practice-stop), [kế hoạch commit](commit-plan-2026-11-05.md) |

Đọc trạng thái và phạm vi ở từng file. Các hướng đã chốt không đồng nghĩa toàn bộ thiết kế, nội dung hoặc phần mềm đã được duyệt. Bản trong 99-luu-tru chỉ dùng truy vết.

## 4. Quyết định chính cần giữ

- Một repo frontend/backend; backend ba lớp Controller → Service → Repository. Vue build phục vụ cùng Spring Boot qua một cổng; Admin session/cookie, không đổi sang JWT.
- Khách độc lập theo tab, phiên cố định 8 giờ. LIFE-P01/DATA-05 dùng vé ký 24 giờ, ledger gắn phiên; không start context 10 phút hoặc start_generation. Giữ service_epoch/REC-P01.
- Admin hết phiên sau 30 phút không hoạt động theo Q3; bảo vệ phần chưa lưu theo Q2.
- Chỉ áp dụng cả cặp văn bản/audio khớp đủ hồ sơ. Bật hiện cần thông tin/vùng hợp lệ và ít nhất cặp vi đã duyệt/áp dụng. Bộ nghiệm thu là 5 × 4: 20 văn bản + 20 audio; không giới hạn tối đa năm địa điểm.
- Ngoại lệ hết hạn chỉ cho nghe hết đúng bài đang phát từ MP3 đã tải trọn trong tab; không tua/nghe lại/bài mới, không áp dụng cho Admin hoặc gia hạn phiên.
- D08 lưu phiên/lượt 30 ngày; file từng áp dụng được bảo vệ 8 giờ theo A02.3. Các hạn file/log/backup và LIFE-P02/P03 theo DATA hiện hành; backup tạm ngừng phục vụ, giữ bảy ngày, restore riêng/lọc mốc gốc/thu hồi quyền cũ.
- CD riêng Tailscale + OpenSSH Windows/PowerShell đã chọn nhưng chưa được kiểm thực. Ngrok Free là phương án thử. Gemini model/Charon đã chọn, chưa kiểm đủ bộ audio được duyệt.

## 5. Điểm còn mở và trách nhiệm

Nền/vị trí/ranh giới bản đồ; đồ họa/câu chữ UI; nội dung/nguồn/hồ sơ/bản dịch/nghe duyệt; quota/chất lượng/giới hạn TTS; phần dữ liệu/nghiệp vụ còn lại; browser/owner/Blob; runtime bền vững, D08/file/backup/restore; ngrok/mạng, SSH/Docker/CD và các ngưỡng chưa chốt vẫn cần xử lý theo [R06](../02-nghiep-vu/prd-review.md#review-open-final) và [TDD](../03-ky-thuat/TDD.md#tdd-open). Không hỏi lại hướng đã được xác nhận hoặc dùng 64 test để đóng các phụ thuộc này.

Người phụ trách và người commit **theo kế hoạch nhóm G02**: Thành T01/T03/T05/T07/T11; Nghĩa T04/T06; Hòa T02/T09/T10; T08 cả nhóm theo phần được giao. Người thao tác Git thực tế trong lượt thực hành này là Thành, theo mục 7.3. Checkbox GitHub phải đọc lại trước khi nhắc tích; không lấy bảng snapshot trong roadmap làm trạng thái mới nhất.

## 6. Đoạn bàn giao ngắn

> Tiếp nhận tại `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`; repo AudioQR_Kinh-thanh-Hue, Project 21 và issue #1–#11 tương ứng T01–T11 đã đối chiếu 89 ô/18 dấu tích. Project có 6 In Progress/5 Todo/0 Done theo người dùng xác nhận; bản sao issue tạm đã dọn. Local main chưa commit, bốn mục untracked, remote chưa có nhánh theo các đầu ra đã nhận; chưa có bằng chứng stage/commit/push. Người dùng là Thành/KhaiThanh và sẽ trực tiếp thao tác mọi task của bản chấm; G02/commit-plan vẫn thiết kế cho ba người trên ba máy. Đã nhận cấu hình author/committer gingpham sau đổi danh tính: Thành cần khôi phục danh tính của mình trước mốc T10. Không chờ Hòa/Nghĩa tiếp nhận thao tác thật, không ghi tác giả khác người thực hiện. Bàn giao các nhóm nền hiện có theo task/phụ thuộc, rồi Codex tự code/test tách biệt, dừng sau một mục checklist/issue đủ bằng chứng để cùng xem, kiểm thử nhẹ và commit. Quy trình chính ở mục 7, thực hành ở mục 7.3; Git, live DB, TTS/API ngoài và triển khai vẫn do người dùng làm. Backend có 64 test đạt ngày 05/11, chưa toàn T02/T03/đồ án.

<a id="guidance-protocol"></a>

## 7. Quy trình cộng tác và quyền thực hiện

**Người xác nhận:** người dùng; quyền triển khai cập nhật 05/11/2026, cách hướng dẫn Git/commit cập nhật 07/11/2026. Quyền hiện hành thay hướng dẫn cũ yêu cầu người dùng tự sửa code/chạy mọi test; các ghi nhận quyền ở nhật ký chỉ phản ánh thời điểm cũ. Handoff được theo dõi trong Git và cập nhật cùng mốc công việc liên quan, không cần commit riêng sau mỗi thao tác.

1. **Chọn việc:** Codex đọc roadmap, issue/checklist thật và tài liệu liên quan; chủ động chọn mục khả thi theo phụ thuộc, không ép làm theo số task. Báo ngắn mục đang làm, người phụ trách/người commit, mục tiêu và lý do chọn.
2. **Triển khai:** đọc file hiện có, bảo vệ chỉnh sửa người dùng; tự tạo/sửa code, SQL migration, cấu hình dự án, mã kiểm thử và tài liệu trong phạm vi nhiệm vụ. Giải thích làm gì/vì sao, không chờ duyệt từng file hoặc dòng.
3. **Kiểm thử:** được tự biên dịch, chạy test liên quan/full suite khi cần, xử lý lỗi; dùng Testcontainers/database tạm tách biệt, áp dụng migration lên đó và tải dependency/image từ nguồn đã cấu hình. Không làm yếu assertion hoặc đổi nghiệp vụ để đạt test; không lặp kiểm đã đạt khi chưa có lý do mới.
4. **Báo kết quả:** nêu phần đã làm, file chính, giải thích cần hiểu để trả lời thầy, kết quả test và giới hạn, cách người dùng tự xem nếu cần. Câu ôn tập ngắn là hỗ trợ học, không là điều kiện tiếp tục. Không kết luận từ code rằng phần mềm đã chạy đạt.
5. **Dừng và ghi tiến độ:** mặc định dừng sau toàn bộ một mục checklist có bằng chứng. Các bước nhỏ trong cùng mục được làm liên tiếp. Chỉ dừng sớm khi có trở ngại thực sự, cần thao tác thuộc quyền người dùng hoặc quyết định quan trọng chưa chốt; không tự thay ô GitHub bằng một ô thực hành nhỏ để tuyên bố hoàn thành.
6. **Checklist:** khi đủ toàn mục, đọc lại GitHub rồi nhắc người dùng tích đúng ô, kèm link/nguyên văn/căn cứ theo G03. Người dùng xem và xác nhận tiếp tục; Codex không tự tích/sửa/đóng issue. Mới làm một phần phải ghi đúng phần còn thiếu.

**Ngoại lệ theo yêu cầu trực tiếp ngày 06/11/2026:** người dùng giao Codex tạo các issue đã duyệt và thêm vào Project 21. Quyền này chỉ áp dụng cho nhiệm vụ chuyển giao issue; không tự mở rộng sang Git, thay checklist, đóng issue hoặc các thao tác khác.

**Quyền giữ lại cho người dùng:** Git/GitHub (kể cả chạy lệnh Git chỉ đọc), database đang dùng và migrations lên đó, TTS/API bên ngoài, CI/CD, khởi động môi trường phục vụ và triển khai. Codex hướng dẫn, không chạy thay nếu chưa có lệnh cụ thể. Đọc issue/checklist bằng công cụ chỉ đọc vẫn được phép. Quyền test tách biệt không mở rộng sang dữ liệu đang phục vụ.

Không tự cài công cụ hệ thống, đổi nguồn tải, bật billing hoặc thay đổi tài khoản/dịch vụ. Không sửa/xóa/reset/di chuyển demo, sao chép secret/database/audio/lịch sử cũ hoặc ghi dữ liệu nhạy cảm vào tài liệu.

<a id="vietnamese-comments"></a>

### 7.1. Comment và giải thích code bằng tiếng Việt

- Giữ tên file/package/class/hàm/thuộc tính, annotation, dữ liệu và logic. Dùng comment `//` tiếng Việt có dấu; không JavaDoc, không dấu chấm cuối note. Ghi ngắn, cụ thể; dùng từ **mã Hash**.
- Class/Entity quan trọng có một dòng cho biết đại diện điều gì. Thuộc tính cần giải thích đặt note cuối dòng. Với liên kết Entity, ưu tiên mô tả đối tượng như `Bản dịch liên kết`; khi giải thích phải phân biệt Java giữ Entity với database lưu giá trị khóa theo mapping, không tự coi thuộc tính Java là Id.
- Hàm thông thường: note ngay trên annotation hoặc khai báo, bắt đầu `// Hàm ...`. Không cần note getter/setter hay constructor đơn giản; không ép rút mọi hàm thành một dòng.
- Hàm test: nêu tình huống kiểm tra và kết quả mong đợi. Hàm hỗ trợ test ghi đúng việc tạo Entity, lưu dữ liệu hay kiểm kết quả; không gọi tạo Entity là đã lưu database, hoặc tạo lỗi giả lập là đã kiểm kết quả.
- Hàm xử lý lỗi có HTTP status cố định dùng `// HTTP <mã> - <nguyên nhân>`. Hàm nhiều loại lỗi không gán một status duy nhất; không gắn HTTP status cho hàm hỗ trợ/repository test chỉ vì kiểm lỗi database.
- Dùng đúng mã lỗi/tên trong code; ưu tiên `trả mã LOCATION_NOT_FOUND` khi đó là hành vi thực. Không mô tả kiểm vài chuỗi thành ẩn mọi thông tin nội bộ, gán `active = false` thành đã có chức năng khóa, hoặc `entityManager.clear()` thành xóa bản ghi. Giá trị mặc định phải nêu đúng giá trị và nguồn nếu có căn cứ.
- Tránh note chung chung, chú thích từng dòng hoặc thuật ngữ khó chưa giải thích; chỉ thêm ví dụ khi cần. Comment bên trong chỉ dành cho lý do/quy tắc/đoạn xử lý khó. Không dùng tên đối tượng quá chung khi code cho biết là địa điểm, bản dịch hoặc audio.
- Khi chỉ được giao chỉnh note, giữ nguyên code/logic, nêu riêng lỗi đáng nghi. Không sửa migration đã áp dụng chỉ để đổi comment, file sinh tự động hoặc thư viện ngoài. Có thể đọc demo để tham khảo cách diễn đạt, không kế thừa nghiệp vụ cũ.
- Khi người dùng yêu cầu **giải thích một file**, nêu mục đích rồi giải thích hàm theo thứ tự; bảng và toàn bộ code chỉ xuất khi yêu cầu cần tới. Khi triển khai bình thường, sửa trực tiếp trong quyền đã cấp, báo ngắn và dẫn file. Câu hỏi về một từ/đoạn trả lời đúng phạm vi, không xuất lại cả file.

Ví dụ phong cách, không là mã yêu cầu thêm vào ứng dụng:

```java
private Long id; // Id audio
private LocationTranslation translation; // Bản dịch liên kết
// Hàm tạo và lưu địa điểm cùng bản dịch tiếng Việt để kiểm thử audio
// Hàm kiểm tra lỗi 404 chung trả mã NOT_FOUND và không lộ chuỗi kiểm thử
// HTTP 409 - Lưu dữ liệu bằng phiên bản cũ
```

**Áp dụng:** quy tắc mới thay yêu cầu ưu tiên Javadoc trước đây. Lượt chuẩn hóa này chỉ sửa tài liệu; code hiện có chưa được chuyển toàn bộ theo phong cách mới. Ghi quy tắc tại đây, file khác dẫn chiếu thay vì chép một bản quy tắc song song.

<a id="issue-git-handoff"></a>

### 7.2. Nhánh, commit và bàn giao Git

- **Cách hướng dẫn đã xác nhận ngày 07/11/2026:** trình bày trọn quy trình cho một nhóm commit trong một câu trả lời; không mặc định mỗi lượt chỉ giao một lệnh. Hướng dẫn ngắn, có lệnh cụ thể, mục đích, kết quả mong đợi và điều kiện dừng, chia ba khối: kiểm tra điều kiện/chuẩn bị; stage đúng file/xem staged diff; commit/push/kiểm kết quả.
- Dùng bằng chứng đã nhận để xác định điểm dừng; không yêu cầu chạy lại lệnh đã có kết quả khi chưa có thay đổi hoặc lý do kỹ thuật mới. Nếu còn thông tin thực sự chặn thao tác, hỏi gộp một lần và vẫn chuẩn bị phần hướng dẫn không phụ thuộc câu trả lời.
- Đặt mốc gửi kết quả cần thiết cho cả nhóm thay vì sau từng lệnh: trước commit, Codex đối chiếu danh sách/nội dung staged và người commit; sau commit/push, nhận bằng chứng commit và SHA local/remote/trạng thái theo nhu cầu. Người dùng có thể làm liên tiếp giữa các mốc khi kết quả đúng kỳ vọng; gặp lỗi hoặc khác kỳ vọng thì dừng, gửi lỗi, không chạy bước phụ thuộc. Lệnh chưa đủ điều kiện vẫn có thể trình bày để thấy toàn quy trình nhưng phải ghi rõ điều kiện, không ghép thành khối chạy mù.
- G02 giữ người phụ trách/người commit theo mô hình nhóm ba người. Khi làm việc nhóm thực tế, mỗi người dùng danh tính của mình; trong lượt thực hành bản chấm này, Thành trực tiếp thao tác mọi task và giữ danh tính Git của Thành theo mục 7.3. Tên Git hoặc GitHub username không tự chứng minh người thực hiện; không dùng đổi danh tính/`--author`/biến môi trường để ghi người khác thực hiện.
- Nhánh theo mã task: `T01`, `T02`, …, `T11`. Chuẩn bị nhánh trước khi triển khai issue; nhánh đã có thì dùng đúng trạng thái local/remote, không tự tạo lại. Người dùng kiểm và chạy Git; không tự stash/reset/clean, chuyển nhánh có nguy cơ mất thay đổi hoặc đổi tác giả.
- Thông điệp: **`Txx: Những việc đã thực hiện`**, bằng tiếng Việt, không bắt buộc thêm loại commit hoặc số issue. Một issue có thể nhiều commit; mô tả đúng phần vừa làm, không ghi hoàn thành toàn issue nếu mới xong một phần. Link issue vẫn giữ trong tài liệu/PR để truy vết.
- Phân nhóm thay đổi theo nội dung và phụ thuộc, không gom tài liệu/nền dữ liệu/API vào T03 chỉ vì từng làm trên nhánh đó. Mốc PR/merge có thể là phần nền đã rà soát, không tự đóng cả issue. Sau merge, nhánh phụ thuộc lấy nền đã cập nhật theo hướng dẫn riêng.
- Mỗi task ghi người phụ trách và người commit **theo kế hoạch nhóm G02**; trước bước Git nêu task, vai trò theo kế hoạch, người thao tác thực tế và file thuộc nhóm. T08 phân theo phần nội dung được giao. Kiểm cấu hình/tài khoản khi cần bằng chứng mới; áp dụng ngoại lệ thực hành mục 7.3, không lưu thông tin đăng nhập.
- Trước commit: dựa trên trạng thái/diff người dùng cung cấp để liệt kê file stage, giải thích file ngoài phạm vi, kiểm staged diff và đưa thông điệp. Không `git add .` khi chưa kiểm, không đưa secret/file chạy tạm vào Git.
- Khi toàn checklist/điều kiện nghiệm thu đủ bằng chứng, báo issue hoàn thành trong phạm vi thực tế và nhắc commit phần còn lại. Commit theo mốc một phần vẫn được thực hiện khi đủ điều kiện, không cần đợi toàn issue; trình bày trọn quy trình nhóm và các mốc đối chiếu theo quy ước trên. PR/merge được hướng dẫn khi đủ căn cứ, không giả đã tạo hoặc merge. Tách rõ đã code/test, đã commit, đã push và đã merge/đóng issue; không suy trạng thái sau từ trạng thái trước. Nêu ngắn nhóm tiếp theo để thấy toàn cảnh, chỉ đưa lệnh khi đủ căn cứ.

<a id="execution-mode"></a>

### 7.3. Bản chấm điểm và cách thực hành theo mô hình nhóm

**Xác nhận ngày 07/11/2026:** đây là bản phục vụ chấm điểm học phần; giữ đầy đủ nghiệp vụ, yêu cầu, chất lượng, 20 văn bản/20 audio, CI/CD và tiêu chí đã chốt. G02, roadmap và kế hoạch commit tiếp tục thiết kế cho ba thành viên Thành/Hòa/Nghĩa, làm việc trên ba máy riêng, có nhánh task, bàn giao và tích hợp qua repo chung.

**Người thao tác thực tế:** Thành trực tiếp thực hiện mọi thao tác thủ công của lượt thực hành này, kể cả Git của các task do G02 giao Hòa/Nghĩa. Cột “người commit theo kế hoạch nhóm” mô tả tổ chức công việc; nhật ký và author/committer phải ghi đúng người thực hiện thực tế. Thành dùng danh tính Git của chính mình cho các commit do mình tạo; không đổi sang gingpham hoặc tài khoản Nghĩa để mô phỏng tác giả. Đã nhận author/committer đang cấu hình gingpham; chưa có bằng chứng commit, nên bước chuẩn bị T10 là Thành khôi phục cấu hình Git của mình, kiểm lại vì cấu hình đã thay đổi, rồi stage riêng .gitignore. Không cần chờ Hòa tiếp nhận hoặc hỏi lại ai đang thao tác. Tên tài khoản không là bằng chứng Hòa/Nghĩa đã thực hiện công việc.

**Thứ tự hiện tại:** ghi nhận và commit các sản phẩm đang có theo nhóm T10 khởi tạo main → T01 tài liệu → T01 khung backend → T02 dữ liệu → T03 vé/API, đúng phụ thuộc và trạng thái Git do người dùng cung cấp. Chỉ commit phần có thật, đã rà soát; một task có thể có nhiều commit và chưa hoàn thành issue. Kiểm tra mức phù hợp từng nhóm; sửa .gitignore/tài liệu không cần chạy lại toàn bộ backend chỉ để lặp bằng chứng. Bộ 64 test cũ thuộc toàn cây làm việc, chưa chứng minh từng commit trung gian hoặc toàn đồ án đạt.

**Tiếp tục triển khai đã được giao:** sau khi các nhóm nền được đối chiếu và bàn giao Git xong, Codex chủ động chọn mục checklist khả thi theo phụ thuộc và quyền mục 7, đọc file hiện có, code và test trong môi trường tách biệt. Dừng sau toàn một mục checklist có bằng chứng hoặc một issue khi đủ toàn phạm vi để Thành cùng xem, kiểm thử nhẹ phù hợp và thực hiện nhóm commit. Không dừng sau từng file/hàm/test; không nhắc tích cả ô khi mới hoàn thành một phần. Sau mốc dừng, tiếp tục khi người dùng xác nhận; trở ngại thực sự hoặc thao tác thuộc quyền người dùng vẫn là lý do dừng sớm.

**Cách hướng dẫn Git:** trình bày gọn danh sách bước cho trọn một nhóm, kèm lệnh cần thiết và điều kiện dừng; giải thích thêm khi người dùng cần. Mốc A gửi danh tính/danh sách và nội dung staged trước commit; mốc B gửi bằng chứng commit/push và kiểm SHA khi hoàn tất nhóm. Không bắt gửi sau từng lệnh thành công. Kế hoạch ba máy ở commit-plan là thiết kế cộng tác, không phải nhật ký cho rằng ba người đã thực hiện. Các quy tắc yêu cầu Hòa trực tiếp tiếp nhận ở các mốc cũ đã được thay bởi xác nhận này; giữ nguyên nhật ký lịch sử.
````

<a id="official-20261107-docs-01-tong-quan-commit-plan-2026-11-05-md-context"></a>

### docs/01-tong-quan/commit-plan-2026-11-05.md — Phần dẫn nhập và quy trình trước biên tập

Nguồn: `docs/01-tong-quan/commit-plan-2026-11-05.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
# Kế hoạch commit — nền tài liệu, dữ liệu và API

**Cập nhật:** 07/11/2026. Kế hoạch này dựa trên trạng thái người dùng cung cấp; không phải bằng chứng đã commit/push/merge. Mọi lệnh Git do người dùng trực tiếp chạy. Quy trình chính ở [handoff mục 7.2](handoff.md#issue-git-handoff): hướng dẫn trọn nhóm commit, gửi kết quả tại các mốc cần thiết hoặc khi có lỗi.

**Phạm vi mô hình:** kế hoạch này thiết kế cho ba người trên ba máy riêng theo G02; cột người commit là phân công dự kiến của nhóm. Trong lượt thực hành bản chấm, Thành trực tiếp chạy mọi thao tác, giữ tác giả Git thực tế là Thành và dùng các nhánh task tương ứng. [Handoff 7.3](handoff.md#execution-mode) quy định cách áp dụng; không coi kế hoạch là bằng chứng Hòa/Nghĩa đã tạo commit.

## 1. Trạng thái hiện tại và quy ước

- Thư mục hiện hành: `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`. Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. Không chạy lại init/đổi remote hoặc dùng trạng thái detached cũ.
- Commit tài liệu **55b0906**, 31 file, đã push ở repo cũ. Đây là bằng chứng lịch sử; không suy commit hoặc bản sửa mới đã lên repo hiện hành. Không tạo lại/xóa nhánh chỉ để khôi phục kế hoạch cũ.
- Trạng thái `No commits yet on main`; `.gitignore`, README.md, backend/ và docs/ đều untracked. Khi đến staging phải đối chiếu lại file và task; không gom toàn backend vào commit tài liệu.
- Đã nhận remote chưa có nhánh, local main chưa commit; cấu hình ban đầu KhaiThanh thuộc Thành, sau đó user.name/user.email đã đổi và cả author/committer trả tên gingpham. Thành xác nhận sẽ trực tiếp thao tác tất cả task trong lượt thực hành; chưa có bằng chứng stage/commit/push. Trước mốc T10, khôi phục danh tính Thành và kiểm lại do cấu hình đã thay đổi; không chờ Hòa tiếp nhận hoặc yêu cầu lặp status/remote khi chưa có lý do mới.
- Tên nhánh: **T01, T02, …, T11**. Thông điệp: **Txx: Những việc đã thực hiện**, bằng tiếng Việt, không cần số issue hoặc tiền tố khác.
- Theo kế hoạch nhóm G02: T01/T03 do Thành, T02 do Hòa; các task khác theo bảng phân công. Trước commit nêu vai trò theo kế hoạch, người thao tác thực tế và file thuộc task. Với lượt thực hành này, mọi commit do Thành tạo phải ghi danh tính Thành. Commit/PR theo mốc không tự đóng toàn issue.

**Kế hoạch hiện hành ngày 06/11:** chuyển sang repository mới trên cùng tài khoản và GitHub Project mới; giữ mọi file; thư mục đã đổi tên thành AudioQR_Kinh-thanh-Hue, giữ mã T01–T11 và dùng checklist mới đã duyệt. Đích đã nhận: [repository mới](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue) và [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Hướng tạo lại T01 trên repo cũ đã được thay; không tiếp tục các lệnh xóa/tạo/reset nhánh của hướng đó. Không tự xóa .git, đổi remote hoặc khởi tạo lại Git.

## 2. Phân nhóm theo task và phụ thuộc

| Mốc | Nhánh | Nội dung / số file theo kiểm kê | Người phụ trách theo G02 | Người commit theo kế hoạch nhóm | Thông điệp đề xuất |
| --- | --- | --- | --- | --- | --- |
| 0 | main — khởi tạo | Chỉ `.gitignore` gốc hiện có — 1 | Hòa (T10) | Hòa | `T10: Khởi tạo repository với cấu hình bỏ qua file Java` |
| 1 | T01 | Tài liệu, thiết kế, nguồn hình và bàn giao — 31 | Thành | Thành | `T01: Bổ sung tài liệu nghiệp vụ, thiết kế và hướng dẫn bàn giao` |
| 2 | T01 | Khung backend, Maven Wrapper và hỗ trợ test — 11 | Thành | Thành | `T01: Bổ sung khung backend và cấu hình hỗ trợ kiểm thử` |
| 3 | T02 | V1, Entity/Repository và test dữ liệu — 5 | Hòa | Hòa | `T02: Tạo bảng phiên khách và ánh xạ Entity, Repository` |
| 4 | T03 | Vé ký, input, runtime config, K11/K01/K02 và test — 21 | Thành | Thành | `T03: Xây dựng API cấp vé và bắt đầu, kiểm tra phiên khách` |

Nhóm tài liệu hiện kiểm kê 31 file sau khi đủ 11 issue mới và đọc lại khớp nội dung; bản sao issue tạm đã được dọn theo [handoff 2.1](handoff.md#issue-recreation). Kiểm lại danh sách/diff thực tế trước stage. Con số 31 của commit 55b0906 giữ nguyên theo bằng chứng cũ. Dẫn task hiện hành: [T01](roadmap.md#t01), [T02](roadmap.md#t02), [T03](roadmap.md#t03); [mapping issue mới](roadmap.md#github-checklist-review) đã xác minh #1–#11 tương ứng T01–T11, không dùng số repo cũ.

**Mốc khởi tạo main:** remote chưa có nhánh và local chưa có commit, nên chuẩn bị commit đầu tiên chỉ gồm `.gitignore` gốc hiện có. Trong kế hoạch ba máy, Hòa phụ trách T10 và tạo nền main trên máy Hòa; các nhánh task sau đó vẫn là T01–T11. Trong lượt thực hành, Thành trực tiếp làm mốc này bằng danh tính Thành theo handoff 7.3; cấu hình hiện đã đổi sang gingpham nên cần khôi phục đúng trước stage. Chưa có bằng chứng stage/commit/push. Ignore mới là mẫu Java, không hoàn tất toàn mục ignore/cấu hình/secret T10; README/backend/docs nằm ngoài mốc khởi tạo.

**Phụ thuộc dự kiến:** sau mốc khởi tạo main, Thành đưa tài liệu/khung T01 đã rà soát vào main bằng Pull Request; nhánh T02 dựa trên main có nền đó, T03 dựa trên nền đã nhận phần dữ liệu cần thiết. Khi đến mốc sau, hướng dẫn từ trạng thái Git thật; chưa yêu cầu chuyển nhánh khi còn các thay đổi chưa commit cần giữ. Không tự stash/reset/clean hoặc dùng nhánh T01 để commit toàn T02/T03.

Tạo Pull Request mới là đề nghị; main chỉ đổi sau merge. Cách gộp giữ các commit là Create a merge commit; chọn khi hướng dẫn mốc PR thực tế. Có thể gộp một phần nền đã kiểm mà chưa đóng toàn issue, tiếp tục các yêu cầu còn lại theo roadmap.

**File dùng chung:** application.properties gồm cấu hình nền và ánh xạ biến môi trường khóa/epoch; TestcontainersConfiguration gồm MySQL và fixture công khai. Nhóm khung giữ nguyên hai file, không sửa code chỉ nhằm dựng lại lịch sử cũ. Chưa tuyên bố mỗi commit trung gian build/test đạt: bộ **64/0/0/0, BUILD SUCCESS lúc 16:15:34+07:00** là của toàn cây làm việc khi chạy, [bằng chứng](../03-ky-thuat/practice-01-start-session.md#practice-start-api-result). Kiểm mốc trung gian nếu có lý do kỹ thuật, không yêu cầu lặp cả bộ chỉ để tạo lịch sử.

### 2.1. Thiết kế phối hợp ba người trên ba máy

Đây là kế hoạch tổ chức, chưa là bằng chứng ba máy đã thiết lập hoặc ba thành viên đã thực hiện. Mỗi người theo mô hình dùng clone/thư mục làm việc và danh tính Git của chính mình; tích hợp qua cùng origin, nhánh task và Pull Request.

| Máy theo mô hình | Người phụ trách | Nhánh/phần việc | Cách nhận và bàn giao |
| --- | --- | --- | --- |
| Máy Thành | Thành | T01 tài liệu/khung; T03/T05/T07/T11, phần T08 được giao | Nhận main đã có mốc khởi tạo; đưa tài liệu và khung đã rà soát lên T01, PR/merge làm nền chung; các task tiếp theo lấy main có đúng phụ thuộc |
| Máy Hòa | Hòa | T10 khởi tạo/CI-CD; T02 dữ liệu; T09 nghiệm thu, phần T08 được giao | Khởi tạo main chỉ .gitignore; sau khi nền T01 được merge, nhận main đó để làm T02 và bàn giao phần dữ liệu trước T03 |
| Máy Nghĩa | Nghĩa | T04/T06 giao diện, phần T08 được giao | Clone repo sau khi main có commit, nhận hợp đồng/nền T01 và API cần thiết; làm nhánh T04/T06 rồi PR/merge, không đưa nguồn T02/T03 của người khác vào commit UI |

**Quy tắc bàn giao:** người nhận chỉ clone/fetch/pull những commit đã có trên remote, không tự nhận được file untracked ở máy người khác. Những nhóm nguồn hiện đang nằm chung trên máy Thành phải được bàn giao đúng danh sách theo task nếu thực hiện mô hình ba máy thật; chưa có bằng chứng đã bàn giao. Không stage toàn repo để chuyển các phần ngoài task. Sau mỗi PR được rà soát/merge, máy làm task phụ thuộc cập nhật nền rồi mới tiếp tục; nếu nhánh đã có thay đổi riêng phải kiểm trạng thái/diff trước, không tự reset hoặc ghi đè.

Trong lượt thực hành một người, Thành lần lượt thực hiện các nhóm tương đương trên các nhánh task của mình, giữ nguyên phân công thiết kế trong bảng; không cần tạo ba máy giả hoặc đổi tác giả để mô phỏng cộng tác. Lệnh cho nhánh/PR tiếp theo chỉ đưa khi mốc trước đã có kết quả.

## 3. Việc tiếp theo — chuẩn bị repository/Project mới

1. Đích đã xác định: repo https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue.git và Project https://github.com/users/khaithanh2726-beep/projects/21/views/1. Origin đã đúng theo đầu ra người dùng; chưa có bằng chứng push hiện tại hoặc xác minh trạng thái board.
2. Nguồn issue là [roadmap mục 3](roadmap.md#t01), đã duyệt 89 ô/18 dấu tích. Theo yêu cầu trực tiếp, Codex đã tạo và đọc lại 11 issue #1–#11 khớp tiêu đề/Description/Checklist; mapping URL thật ở roadmap mục 6.
3. Đã cập nhật mapping và dọn bản sao issue tạm sau đối chiếu theo quyền đã cấp; nhóm tài liệu còn 31 file. Người dùng xác nhận Project 21 đủ 11 issue và đã đặt Status theo hướng dẫn: 6 In Progress/5 Todo/0 Done; Codex chưa trực tiếp quan sát board. Đã nhận các đầu ra Git nêu ở mục 1; không yêu cầu lặp lại khi chưa có lý do mới. Không tự chạy Git.
4. Chuẩn bị mốc 0 chỉ `.gitignore` gốc, thuộc T10/Hòa theo kế hoạch nhóm; Thành trực tiếp thực hành bằng danh tính của Thành. Đã nhận author/committer gingpham sau đổi cấu hình, nên khôi phục danh tính Thành trước stage; gửi mốc A đối chiếu index, sau đó mốc B sau commit/push. Không còn chờ Hòa trực tiếp thao tác; không lặp init/xóa/tạo/reset lịch sử cũ.
5. Trình bày đầy đủ quy trình nhóm hiện tại theo mục 3.1; người dùng gửi kết quả tại mốc A/B hoặc khi có lỗi. Các bước sau mốc A chỉ chạy khi đã đối chiếu đạt; chưa coi bất cứ mốc dự kiến nào là đã thực hiện. Nhóm tiếp theo là tài liệu T01 do Thành commit, rồi khung backend T01, dữ liệu T02/Hòa và API T03/Thành; chỉ đưa lệnh cho nhóm sau khi đủ căn cứ.

Tên nhánh T01–T11, thông điệp tiếng Việt Txx: Những việc đã thực hiện và phân công/người commit vẫn giữ. Bảng mục 2 và danh sách backend mục 4 là phân nhóm công việc, không phải lệnh chạy liên tiếp trong giai đoạn chuyển giao.

### 3.1. Quy trình mốc 0 — T10/Hòa trong kế hoạch ba máy

Đây là hướng dẫn có điều kiện, chưa phải kết quả thực hiện. Dừng khi một lệnh lỗi hoặc kết quả khác kỳ vọng; không chạy tiếp bước phụ thuộc và không tự force/reset/đổi tác giả.

**Kiểm tra điều kiện và chuẩn bị:** theo mô hình ba máy, Hòa phụ trách mốc khởi tạo trên máy Hòa và dùng danh tính Hòa. Khi áp dụng cho lượt thực hành bản chấm hiện tại, Thành trực tiếp chạy toàn bộ nhóm này bằng danh tính Thành theo handoff 7.3. Không chờ Hòa tiếp nhận. Các đầu ra đã có cho biết main chưa commit, bốn mục untracked và remote chưa có nhánh; không yêu cầu chạy lại khi chưa có thay đổi. Riêng danh tính đã đổi sang gingpham nên Thành khôi phục cấu hình local của mình trước, rồi kiểm lại author/committer bên dưới. Việc kiểm lại có lý do là cấu hình vừa đổi; đây không phải chỉ đổi tên để ghi người khác thực hiện.

```powershell
git var GIT_AUTHOR_IDENT
git var GIT_COMMITTER_IDENT
```

Kỳ vọng author/committer là người thực hiện thực tế: Hòa nếu triển khai kế hoạch ba máy, Thành/KhaiThanh trong lượt thực hành này. Nếu sai thì dừng trước stage; không dùng username để suy email. Không cần gửi riêng từng lệnh thành công; gộp vào mốc A.

**Stage đúng file, xem nội dung đã stage:** chỉ khi đủ điều kiện trên, chạy lần lượt; lỗi thì dừng.

```powershell
git add -- .gitignore
git --no-pager diff --cached --name-status
git --no-pager diff --cached -- .gitignore
git --no-pager diff --cached --check
```

Kỳ vọng chỉ có `A .gitignore`, nội dung là mẫu bỏ qua file Java đã đọc; lệnh check không báo lỗi. README.md/backend/docs vẫn ngoài index. Nếu có file hoặc nội dung ngoài phạm vi, dừng và gửi kết quả, không tự reset/clean. **Mốc A bắt buộc gửi trước commit:** xác nhận đúng người trực tiếp thực hiện và danh tính của chính người đó, đầu ra committer, danh sách/nội dung staged và kết quả check. Chờ Codex đối chiếu đạt; không cần báo sau từng lệnh thành công.

**Commit, push và kiểm tra kết quả:** chỉ chạy sau khi mốc A được đối chiếu đạt và index/danh tính không đổi.

```powershell
git commit -m "T10: Khởi tạo repository với cấu hình bỏ qua file Java"
git --no-pager show --format=fuller --stat HEAD
```

Kỳ vọng commit đầu tiên trên main chỉ có `.gitignore`, đúng author/committer của người thực hiện thực tế và đúng thông điệp; trong lượt thực hành này là Thành. Nếu commit lỗi hoặc thông tin khác kỳ vọng, dừng trước push. Khi đúng, có thể tiếp tục mà không cần gửi thêm một mốc trung gian:

```powershell
git push -u origin main
```

Chỉ khi push thành công mới chạy khối kiểm kết quả. Nếu lỗi xác thực/quyền hoặc remote có lịch sử ngoài dự kiến, dừng, gửi lỗi; người dùng tự xử lý đăng nhập, không chia sẻ secret và không force push.

```powershell
git rev-parse HEAD
git ls-remote --heads origin main
git status --short --branch
```

**Mốc B gửi sau cả nhóm:** đầu ra commit/show/push và ba lệnh kiểm cuối. Kỳ vọng SHA local bằng SHA refs/heads/main trên origin, main theo dõi origin/main; README.md/backend/docs còn untracked, `.gitignore` đã được commit. Những kiểm tra này diễn ra sau thay đổi mới, không phải yêu cầu lặp bằng chứng cũ. Chưa coi đã có các kết quả đó hoặc T10 hoàn thành; chưa tạo nhánh task/PR trong nhóm này.

**Nhịp sau các nhóm nền:** khi mốc 0–4 đã đối chiếu và bàn giao đúng task, Codex tiếp tục triển khai theo quyền đã giao; dừng sau toàn mục checklist hoặc issue có bằng chứng để Thành cùng xem, kiểm thử nhẹ và commit. Hướng dẫn Git từ đây ưu tiên danh sách bước ngắn, lệnh cần thiết và mốc kiểm; xem handoff 7.2–7.3. Không đợi toàn issue mới commit phần nền đã rà soát và không ghi một commit là đã hoàn thành issue.
````

<a id="official-20261107-docs-02-nghiep-vu-decisions-md-20"></a>

### docs/02-nghiep-vu/decisions.md — Phần quyết định trước biên tập

Nguồn: `docs/02-nghiep-vu/decisions.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### 3.1. G01 — Định hướng phiên bản đi chấm

**Đã chốt 30/10/2026.**

- Dự án được phân tích và triển khai như bản hoàn chỉnh để đi chấm đồ án, theo phân công của nhóm.
- Đánh giá phương án theo tính nhất quán nghiệp vụ, trải nghiệm khách, khả năng kiểm thử và khối lượng phù hợp với nhóm.
- Không dùng lý do “bản đầu” để bỏ qua tình huống quan trọng. Cụm “bản đầu” trong ghi nhận cũ chỉ mô tả lịch sử/phạm vi, không làm giảm tiêu chuẩn xử lý và nghiệm thu của phiên bản đi chấm.
- Giữ phạm vi đã chốt: chưa bổ sung hoạt động offline, đồng bộ phiên giữa tab hoặc đổi tốc độ audio; thanh tua audio đã duyệt vẫn giữ nguyên.
- Hoàn chỉnh để đi chấm là định hướng/mức yêu cầu, không phải tuyên bố phần mềm đã hoàn thành, đã kiểm thử hoặc tài liệu đã được duyệt tổng thể.

G01 tự nó không phê duyệt toàn bộ Business Flow; trạng thái duyệt hiện hành được xác nhận riêng dưới đây.
````

<a id="official-20261107-docs-02-nghiep-vu-decisions-md-21"></a>

### docs/02-nghiep-vu/decisions.md — Phần quyết định trước biên tập

Nguồn: `docs/02-nghiep-vu/decisions.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
### 3.4. G02 — Phân công nhóm và người commit

**Phân công đã chốt ngày 02/11/2026; bổ sung cách thực hành ngày 07/11/2026.** Bảng dưới là phân công trách nhiệm và người commit theo kế hoạch nhóm ba thành viên trên ba máy, giữ nguyên phạm vi từng task. Trong lượt thực hành bản chấm, Thành trực tiếp thao tác mọi task và commit bằng danh tính của chính Thành theo [handoff 7.3](../01-tong-quan/handoff.md#execution-mode). Kế hoạch nhóm không là xác nhận ba người đã thực hiện; trạng thái hoàn thành vẫn căn cứ sản phẩm/kết quả.

| Task | Người phụ trách theo G02 | Người commit theo kế hoạch nhóm |
| --- | --- | --- |
| T01 — Phân tích và thiết kế | Thành | Thành |
| T02 — Database | Hòa | Hòa |
| T03 — Backend khách | Thành | Thành |
| T04 — Frontend khách | Nghĩa | Nghĩa |
| T05 — Backend Admin | Thành | Thành |
| T06 — Frontend Admin | Nghĩa | Nghĩa |
| T07 — Khảo sát và tích hợp TTS | Thành | Thành |
| T08 — Nội dung | Thành, Nghĩa, Hòa | Thành, Nghĩa, Hòa theo phần phụ trách |
| T09 — Kiểm thử tích hợp và nghiệm thu | Hòa | Hòa |
| T10 — Git/CI/CD/triển khai | Hòa | Hòa |
| T11 — Tài liệu và bàn giao | Thành | Thành |

- T01: Thành tổng hợp; Nghĩa và Hòa đóng góp thiết kế liên quan công việc mình.
- T08: cả nhóm phối hợp, chia theo địa điểm khi thống nhất cụ thể; chưa gán địa điểm cho từng người.
- T09: Hòa phụ trách tích hợp/nghiệm thu; mỗi người vẫn kiểm tra phần mình và sửa lỗi thuộc phần đó.
- T11: Thành tổng hợp; Nghĩa và Hòa cung cấp hướng dẫn, kết quả và bằng chứng phần mình.
- Giữ ranh giới: T02 cấu trúc/ràng buộc/migrations; T03/T05 nghiệp vụ Backend; T04/T06 giao diện; T07 tạo/theo dõi audio, T05 quản trị/áp dụng cặp/xóa-dọn. T01 phân tích/thiết kế khác T11 hướng dẫn/vận hành/bàn giao cuối.
- Người phụ trách/người commit theo kế hoạch nhóm giữ bảng trên; T08 phân chia địa điểm cụ thể khi thống nhất. Khi cộng tác ba máy thật, mỗi thành viên commit phần mình bằng danh tính của mình. Trong lượt thực hành hiện tại, Thành thao tác mọi nhóm; trước commit xác định task, vai trò theo kế hoạch, người thực hiện thực tế và danh sách file. Không đổi tác giả để ghi người khác là người thực hiện.
- **Danh tính đã xác nhận ngày 07/11/2026:** người dùng là Thành; tên tác giả Git `KhaiThanh` thuộc Thành. Người dùng xác nhận Hòa dùng `gingpham`, Nghĩa là tài khoản còn lại trong ảnh Collaborators; ảnh hiển thị tên Chí Nghĩa và username `lechiNghiaa`. Tài khoản chủ repo `khaithanh2726-beep` là của Thành. Đây là ánh xạ thành viên/tài khoản theo người dùng và ảnh, không chứng minh tác giả Git của Hòa/Nghĩa hoặc người đã thực hiện task. Chưa có quyền mới để tự gán assignee hoặc sửa issue/Project; thời hạn chưa xác nhận.

| Thành viên G02 | Tài khoản GitHub đã xác nhận | Danh tính Git hiện biết |
| --- | --- | --- |
| Thành — người dùng hiện tại | `khaithanh2726-beep` | `KhaiThanh` thuộc Thành, theo đầu ra và xác nhận người dùng |
| Hòa | `gingpham` | Đã nhận môi trường hiện tại cấu hình tên gingpham; người thao tác vẫn là Thành, không coi là bằng chứng Hòa thực hiện commit |
| Nghĩa — ảnh hiển thị Chí Nghĩa | `lechiNghiaa` | Chưa có đầu ra author/committer của Nghĩa |

- [Roadmap hiện hành](../01-tong-quan/roadmap.md#t01) là nguồn duy nhất của 11 task, mỗi issue có Description và một Checklist. Backlog 0.2 và lần xuất bản #49–#59/Project 20 là lịch sử của repo cũ. **Ngày 06/11/2026, người dùng duyệt phương án checklist mới 89 ô/18 dấu tích**, giữ mã task, phân công và quy ước nhánh/commit; 18 dấu tích xác nhận đúng kết quả tài liệu/khảo sát được ghi, không phải toàn chức năng. Repo hiện hành AudioQR_Kinh-thanh-Hue và Project 21 được dẫn tại [handoff](../01-tong-quan/handoff.md#next-step); đã tạo và đối chiếu đủ 11 issue mới #1–#11 theo yêu cầu trực tiếp ngày 06/11, mapping URL thật tại roadmap mục 6; người dùng xác nhận Project 21 đủ 11 issue và đã đặt Status theo hướng dẫn: 6 In Progress/5 Todo/0 Done; bảng 6.2 ghi theo xác nhận người dùng, Codex chưa trực tiếp quan sát board. Xác nhận này không duyệt toàn TDD/roadmap triển khai hoặc kết quả phần mềm.

Căn cứ xác nhận tại [8.1.11](../99-luu-tru/decisions-history.md#g02-source); G02 không mở lại G01/BF/PRD/UI hoặc duyệt thay phần còn mở.
````

<a id="official-20261107-practice-evidence"></a>

### docs/03-ky-thuat/practice-01-start-session.md — Phụ lục A — hồ sơ kiểm chứng nguyên văn trước biên tập

Nguồn: `docs/03-ky-thuat/practice-01-start-session.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
## Phụ lục A. Bằng chứng theo thời điểm

Các mốc dưới đây giữ diễn biến và quyền tại thời điểm thực hiện; không yêu cầu chạy lại hoặc thay điểm dừng hiện hành. Tên nhánh/quyền cũ bên trong phản ánh lúc ghi; hiện dùng T01 và quy trình tại handoff. Trạng thái mới nhất ở [handoff](../01-tong-quan/handoff.md#next-step).

### A.1. Kết quả người dùng cung cấp — ghi nhận 04/11/2026

Nguồn là nội dung người dùng dán vào chat sau khi tự chạy ở PowerShell trong repo chính. Chưa có timestamp lúc chạy, Windows build hoặc bằng chứng build/test; không bịa các thông tin này. Không coi đây là kết quả trợ lý tự kiểm máy.

| Bằng chứng môi trường | Kết quả và kết luận giới hạn |
| --- | --- |
| Người thực hiện / thời gian / Windows build | Ghi nhận ngày 04/11/2026 theo phiên kiểm môi trường. Thời gian chạy cụ thể và Windows build chưa cung cấp |
| Java runtime/compiler | java 21.0.12.1, runtime build 21.0.12.1+1-LTS-4; javac 21.0.12.1. Cùng nhánh Java 21 đã chốt; chưa build Boot/Wrapper hoặc chứng minh toàn bộ tổ hợp |
| Docker CLI | Docker 29.8.1, build 4a63305. Lệnh được nhận; đây là phiên bản CLI, chưa phải bằng chứng Server/engine đang chạy hoặc phiên bản Docker Desktop |
| Docker Engine | Người dùng chạy docker info, nhận ServerVersion=29.8.1; OSType=linux. Xác nhận PowerShell kết nối được Engine Linux ở thời điểm kiểm; không đồng nghĩa đã chạy container/MySQL hoặc Testcontainers |
| Docker Compose | v5.5.1. Plugin được nhận; khác dòng Compose v2 tại mốc tài liệu, chưa đổi mốc hoặc hạ bản |
| WSL | Default Distribution: docker-desktop; Default Version: 2. Có distro Docker Desktop và mặc định WSL 2; chưa xác minh version/kernel WSL thực, engine hoặc OSType server từ đầu ra này |
| Backend source/Wrapper/app build | Backend ở đúng cấp repo; đã đọc Wrapper trỏ Maven 3.9.12 theo mục 3.1. Trước khi người dùng chỉnh Wrapper từng đối chiếu 12 file khớp ZIP; hiện không tuyên bố toàn bộ file còn giống ZIP. Chưa chạy/build/test |
| MySQL image/tag/digest/Flyway | Chưa có kết quả kéo/chạy/migration/tương thích thực |
| Kết luận phần đã có/còn thiếu | Đã có runtime/compiler Java 21, CLI Docker/Compose và thông tin WSL ban đầu. Đã có bằng chứng kết nối Engine Linux; còn đối chiếu file sinh ra, Wrapper/build và kiểm tổ hợp trong các chặng kế tiếp |

**Đối chiếu Compose — nguồn chính thức kiểm 04/11/2026:** [Docker mô tả Compose v2 và v5](https://docs.docker.com/compose/intro/history/) cùng dùng Compose Specification và v5 tương đương chức năng v2 ở CLI. Vì vậy không kết luận v5.5.1 sai chỉ do khác số major, chưa có căn cứ yêu cầu cài/hạ bản. Đây là căn cứ tiếp tục kiểm môi trường, không phê duyệt âm thầm một bộ phiên bản mới hoặc chứng minh Compose/MySQL/Testcontainers của dự án đã chạy. Giữ bản mốc TECH-03 để đối chiếu và ghi đúng phiên bản thực khi thực hành.

Mốc tham chiếu đã chọn: Java 21, Boot 4.1.2/Maven Wrapper 3.9.12, MySQL 8.4.13 và BOM theo technical-inputs. Không khả dụng/không tương thích phải báo bằng chứng và ảnh hưởng trước đề xuất đổi, không âm thầm dùng latest. Chặng này chưa cần Node/Vue/Leaflet/TTS/ngrok hoặc SSH để chứng minh Bắt đầu backend; chúng vẫn ở phạm vi dự án đầy đủ.

<a id="practice-docker-engine"></a>

### A.2. Docker Engine — đã có kết quả người dùng tự kiểm

Lệnh chỉ đọc người dùng đã tự thực hiện tại PowerShell:

```powershell
docker info --format 'ServerVersion={{.ServerVersion}}; OSType={{.OSType}}'
```

Mục đích: kiểm CLI có kết nối tới Docker Engine hiện tại và engine đang phục vụ hệ điều hành container nào; [docker info/format](https://docs.docker.com/reference/cli/docker/system/info/) có dữ liệu phía Server. ServerVersion là phiên bản engine, không đồng nhất Docker Desktop hay phiên bản CLI.

Kết quả cần đọc: có ServerVersion và OSType=linux là bằng chứng kết nối được engine Linux ở bước này. Nếu báo lỗi kết nối/quyền/template hoặc OSType khác linux, người dùng gửi nguyên văn kết quả để hướng dẫn tiếp; chưa tự đổi context, chế độ container, cấu hình hoặc cài lại. Bước này không tạo container, tải image hoặc chạy dự án; thành công vẫn chưa là MySQL/Compose/ứng dụng đã kiểm.

**Đầu ra người dùng cung cấp:**

```text
ServerVersion=29.8.1; OSType=linux
```

Đã kết nối được Engine 29.8.1 và ở chế độ Linux container, phù hợp hướng Docker/Linux đã chốt. Chưa có kết quả tạo/chạy container, MySQL/Flyway/Testcontainers hoặc build ứng dụng; chưa đủ tích mục kiểm môi trường/tương thích tổng hợp trên GitHub. Không yêu cầu chạy lại lệnh khi không có lỗi hoặc thay đổi cần kiểm.

### A.3. Kết quả Wrapper — người dùng cung cấp ngày 05/11/2026

Người dùng chạy `.\mvnw.cmd --version` trong PowerShell tại `backend` và gửi đầu ra cuối không kèm prompt lẫn vào lệnh:

- Apache Maven **3.9.12**, revision `848fbb4bf2d427b72bdb2471c22fced7ebd9a7a1`.
- Maven home nằm dưới `C:\Users\khait\.m2\wrapper\dists\apache-maven-3.9.12`.
- Java **21.0.12.1**, vendor Oracle Corporation, runtime `C:\Program Files\Java\jdk-21.0.12.1`.
- Default locale `en_US`, platform encoding `UTF-8`; OS name `windows 11`, version `10.0`, arch `amd64`. Chưa có Windows build cụ thể.

Đủ xác nhận Wrapper chạy đúng Maven và Java đã chọn; không cần chạy lại khi chưa có lý do mới. Không biết Maven vừa tải hay dùng bản đã lưu từ trước; chưa có bằng chứng resolve dependency dự án/build/test, kéo image/chạy MySQL. Mục A.1 giữ kết quả môi trường ở mốc ghi nhận trước.

<a id="practice-mysql-pull"></a>

### A.4. Tải image MySQL — người dùng cung cấp ngày 05/11/2026

Người dùng chạy `docker pull mysql:8.4.13` trong PowerShell tại `backend` và cung cấp kết quả:

```text
Digest: sha256:a81d043930e51745522bd5c430961cf8286f0a23f0a9a2a6e0108f7ea71b0902
Status: Downloaded newer image for mysql:8.4.13
docker.io/library/mysql:8.4.13
```

Xác nhận Docker đã tải được image đúng tag đã chọn tại lần chạy này; digest được chép từ đầu ra, chưa tự kiểm lại metadata/kiến trúc image. Chưa có bằng chứng tạo/chạy MySQL, kết nối Testcontainers, migration hoặc build/test. Không cần pull lại khi chưa có lý do mới.

<a id="practice-skeleton-test"></a>

### A.5. Test skeleton lần đầu — ĐẠT trong phạm vi nạp context

**Người thực hiện:** người dùng, lệnh `.\mvnw.cmd test` tại `backend`. **Bằng chứng:** log người dùng gửi trong `C:\Users\khait\.codex\attachments\2a8fabda-e2e5-41a3-b09f-a20de188de35\Pasted text.txt`, kết thúc `2026-11-05T01:23:00+07:00`; trợ lý đối chiếu chỉ đọc `backend/target/surefire-reports/vn.hueqr.backend.BackendApplicationTests.txt` thấy cùng kết quả 1/0/0/0, thời gian test 76.57 giây. Tổng thời gian Maven trong log: 02:11 phút. Không phải trợ lý tự chạy test; các file ở `target` có thể bị thay bởi lần chạy sau.

| Quan sát thực tế | Kết luận đúng phạm vi |
| --- | --- |
| Biên dịch 1 source main, 3 source test, release 21; Boot 4.1.2/Java 21.0.12.1 | Đã resolve đủ thư viện và biên dịch/chạy test skeleton tại lần này; chưa đóng gói JAR/CI |
| Testcontainers 2.0.5 kết nối Docker Engine 29.8.1, khởi động Ryuk và mysql:8.4.13 | MySQL thực đã khởi chạy trong môi trường test; chưa database DEV bền vững |
| MySQL sẵn sàng qua SELECT 1; Hikari thêm connection; Hibernate báo database version 8.4.13 và khởi tạo EntityManagerFactory | Có kết nối MySQL/JPA thực, không chỉ pull image |
| Flyway kết nối, tạo flyway_schema_history, validated 0 migrations; Found 0 JPA repository interfaces | Chưa migration/bảng/ràng buộc/Repository nghiệp vụ; không tính P01-DATA hoặc T02 đã đạt |
| Tests run: 1, Failures: 0, Errors: 0, Skipped: 0; BUILD SUCCESS | contextLoads() đạt; chưa API phiên, vé ký, UNIQUE/đồng thời hoặc nghiệm thu nghiệp vụ |

**Cảnh báo và bước xử lý:** `No migrations found` phù hợp với việc chưa có migration source, sẽ xử lý ở P01-DATA; không tắt Flyway để giấu cảnh báo. `spring.jpa.open-in-view is enabled by default` là cấu hình JPA web cần làm rõ theo [ba lớp](TDD.md#tdd-layers): nhiệm vụ kế tiếp là người dùng đặt `spring.jpa.open-in-view=false` trong `backend/src/main/resources/application.properties`, rồi chạy test kiểm lại. Cách tắt theo [Spring Boot — Open EntityManager in View](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.jpa-and-spring-data.open-entity-manager-in-view), kiểm ngày 05/11/2026; đây là lựa chọn cấu hình triển khai, chưa được sửa trong lượt đọc log. Cảnh báo Mockito/Byte Buddy nạp agent động còn ghi nhận, không làm lần test này thất bại; chưa sửa agent hoặc thêm cờ che cảnh báo.

<a id="practice-osiv-test"></a>

### A.6. Tắt open-in-view và kiểm lại — ĐẠT

Người dùng tự thêm `spring.jpa.open-in-view=false`; trợ lý đọc chỉ đọc `backend/src/main/resources/application.properties` xác nhận file có hai dòng `spring.application.name=backend` và cấu hình trên. Log người dùng gửi ở `C:\Users\khait\.codex\attachments\d34ec6bd-21b7-45fd-964d-a53617957deb\Pasted text.txt` ghi lệnh `.\mvnw.cmd test` và kết thúc **2026-11-05T01:32:54+07:00**:

- **Tests run: 1, Failures: 0, Errors: 0, Skipped: 0**, **BUILD SUCCESS**; tổng thời gian 01:32 phút, test 76.39 giây. Báo cáo Surefire hiện tại khớp số liệu, đã đối chiếu chỉ đọc; lần mới đã thay nội dung report của lần đầu, không mất bằng chứng log lần đầu ở mục A.5.
- MySQL **8.4.13** khởi động/kết nối qua Testcontainers; không còn cảnh báo `spring.jpa.open-in-view is enabled by default` trong log mới.
- Flyway vẫn **0 migration**, Spring vẫn **0 Repository**; cảnh báo Mockito/Byte Buddy agent còn. Chưa có schema/API nghiệp vụ hoặc HTTP DEV hoạt động được chứng minh.

Hoàn tất nhiệm vụ sửa cấu hình và kiểm lại; không cần lặp khi chưa có thay đổi/lỗi mới. [Nhiệm vụ chờ tiếp theo](../01-tong-quan/handoff.md#next-step): chạy thử HTTP với TestBackendApplication/MySQL tạm sẵn có, chưa thiết lập database DEV bền vững.

<a id="practice-http-partial"></a>

### A.7. Chạy thử HTTP — khởi động/phản hồi và kết thúc lệnh đã xác nhận

Người dùng gửi ảnh VS Code với URL `http://localhost:8080/`, trang Whitelabel Error Page ghi `type=Not Found, status=404`. Terminal trong ảnh có DispatcherServlet initializing/completed và timestamp `2026-11-05T09:42:14.077+07:00`. Xác nhận đã có phản hồi HTTP 404 và xử lý servlet; phù hợp source hiện tại chưa có route `/`, không cần sửa để có trang chủ ở bước này.

Người dùng đã bổ sung log đầy đủ của chính lần HTTP này: lệnh `.\mvnw.cmd spring-boot:test-run`, Boot 4.1.2, Java 21.0.12.1, PID **29424** khớp terminal trong ảnh. Testcontainers 2.0.5 khởi động/kết nối MySQL **8.4.13**; Hibernate xác nhận đúng bản. Tomcat started trên **8080** lúc **09:41:53.076+07:00 ngày 05/11/2026**, tiếp đó `Started BackendApplication` lúc 09:41:53.092; DispatcherServlet xử lý request lúc 09:42:14 khớp ảnh. Flyway vẫn 0 migration, Spring vẫn 0 Repository.

Đủ bằng chứng chạy HTTP với MySQL tạm và phản hồi 404; chưa API nghiệp vụ/DEV bền vững. `Started BackendApplication` là phù hợp: `TestBackendApplication` gọi `SpringApplication.from(BackendApplication::main)` rồi bổ sung cấu hình container; không yêu cầu log phải mang tên TestBackendApplication như hướng dẫn trước. Người dùng đã gửi dấu nhắc `PS ...\backend>` trở lại sau hướng dẫn dừng: xác nhận lệnh test-run kết thúc, không yêu cầu chạy lại hoặc mở lại trang. Chưa có log shutdown hay kiểm Docker sau dừng, nên không khẳng định mọi container đã được dọn.

<a id="practice-v1-result"></a>

### A.8. Migration V1 guest_session — áp dụng ĐẠT trên MySQL test

Người dùng chạy `.\mvnw.cmd test` tại backend; log trong `C:\Users\khait\.codex\attachments\916cb70b-56fe-4713-a0c8-39df07a5b5b0\Pasted text.txt` kết thúc **2026-11-05T10:26:20+07:00**. Trợ lý đối chiếu file V1 hiện có và báo cáo Surefire chỉ đọc:

- MySQL **8.4.13** khởi động/kết nối; Flyway **validated 1 migration**, **Successfully applied 1 migration**, schema ở **v1**. Không còn cảnh báo No migrations found trong log này.
- **1 test, 0 failures/errors/skipped, BUILD SUCCESS**, test 36.89 giây; tổng Maven 42.198 giây. Surefire khớp số liệu.
- V1 tạo được bảng khi nạp context; vẫn **0 Repository**, chưa Entity/API hoặc kiểm dữ liệu sai/đồng thời. Cảnh báo Mockito/Byte Buddy agent còn ghi nhận, không làm lần chạy này thất bại.

Đủ hoàn tất nhiệm vụ áp dụng V1, không có bằng chứng các test ràng buộc mới ở mục 3.2.2 đã chạy. Đã đọc lại T02 #50: các ô gộp toàn mô hình/ràng buộc/migrations/khởi tạo, chưa đủ phạm vi để nhắc tích; không tự tích.

<a id="practice-schema-test-result"></a>

### A.9. Kiểm ràng buộc guest_session — 4 ca ĐẠT

Người dùng chạy `.\mvnw.cmd test`; log tại `C:\Users\khait\.codex\attachments\a718df1a-1f19-4e84-aa33-5a3c57808a44\Pasted text.txt` kết thúc **2026-11-05T10:51:11+07:00**. Trợ lý đọc hai báo cáo Surefire và XML của GuestSessionSchemaTests, đối chiếu tên đủ bốn ca tại mục 3.2.2:

- BackendApplicationTests: **1/0/0/0**, 42.20 giây; GuestSessionSchemaTests: **4/0/0/0**, 0.337 giây. Tổng **5 test, 0 failures/errors/skipped, BUILD SUCCESS**; Maven 53.063 giây.
- V1 được áp dụng trên MySQL **8.4.13**, schema **v1**. Có bằng chứng hai phiên cùng tên được lưu/đọc Unicode, trùng start request key/credential hash bị đúng UK chặn, `de` bị đúng CHECK chặn.
- Đây là INSERT/SELECT tuần tự qua JDBC, chưa concurrent/Service/ticket/API, chưa kiểm mapping JPA. Kết quả này không bao gồm các file Entity/Repository và cấu hình validate/UTC được viết sau đó ở mục 3.2.3.

Đã đọc lại checklist T02 #50 hiện hành: mới kiểm một bảng và một phần ràng buộc, chưa đủ ô gộp toàn mô hình/khởi tạo/kiểm để nhắc tích.

<a id="practice-mapping-test-result"></a>

### A.10. Entity/Repository và package theo lớp — bộ 7 test ĐẠT

Người dùng gửi kết quả Maven **7 test, 0 failures/errors/skipped, BUILD SUCCESS**, kết thúc **2026-11-05T13:35:16+07:00**, tổng thời gian **01:19 phút**, sau nhiệm vụ clean test tại mục 3.2.4. Trợ lý đối chiếu chỉ đọc ba báo cáo TXT/XML trong `backend/target/surefire-reports`:

| Lớp theo package hiện hành | Tests / failures / errors / skipped | Thời gian test |
| --- | --- | --- |
| vn.hueqr.backend.BackendApplicationTests | 1/0/0/0 | 52.25 giây |
| vn.hueqr.backend.repository.GuestSessionRepositoryTests | 2/0/0/0 | 3.822 giây |
| vn.hueqr.backend.schema.GuestSessionSchemaTests | 4/0/0/0 | 0.802 giây |

XML ghi nhận scan **1 JPA Repository**, MySQL **8.4.13**, Flyway áp dụng **1 migration**, schema **v1**, context nạp thành công với cấu hình validate/UTC hiện có. Đủ tên bảy ca; hai ca Repository `savesAndReloadsStartSessionByRequestKeyWithUtcMicroseconds` và `returnsEmptyWhenStartRequestKeyDoesNotExist` đều đạt. Có bằng chứng lưu/đọc lại mapping, đối chiếu UTC trực tiếp và Optional rỗng khi không có request key. Các test chạy trong package mới; cấu hình support dùng chung hoạt động trong bộ test này.

Hoàn tất nhiệm vụ kiểm Entity/Repository và tổ chức package hiện có, không yêu cầu chạy lại khi chưa có thay đổi/lý do mới. Chưa kiểm Service/ticket/API, concurrent/replay hoặc database DEV bền vững; fixture 24 giờ/8 giờ không chứng minh đã cài luật nghiệp vụ. Chưa kiểm lại HTTP test-run sau đổi package hoặc đóng gói JAR. Trợ lý không chạy lệnh thay người dùng; báo cáo target có thể bị thay bởi lần chạy sau.

Đã đọc lại GitHub [T02 #50](https://github.com/khaithanh2726-beep/Thuyet-minh-tu-dong-QR-main/issues/50) sau kết quả này: các ô vẫn gồm toàn mô hình/khóa ngoại/migrations/dữ liệu khởi tạo/bàn giao; một bảng với bộ test này chưa đủ hoàn thành toàn nội dung ô mới. Không nhắc tích hoặc sửa issue. Chặng tiếp theo theo lộ trình là P01-TICKET; chưa triển khai trong lượt ghi nhận kết quả này.

<a id="practice-ticket-time-result"></a>

### A.11. P01-TICKET bước thời gian — 8 unit test ĐẠT

Người dùng chạy `.\mvnw.cmd "-Dtest=StartTicketTimePolicyTests" test` trong backend, gửi log kết thúc **2026-11-05T13:57:40+07:00**: **8 test, 0 failures/errors/skipped, BUILD SUCCESS**; Maven **16.769 giây**, lớp test **0.451 giây**. Log biên dịch **4 file main/6 file test**, chỉ chạy lớp StartTicketTimePolicyTests. Trợ lý đọc Surefire TXT/XML, khớp số liệu và đủ tám tên ca tại mục 3.2.5.

Đã kiểm các mốc trước/đúng/sau hạn, 24 giờ/issuedAt tương lai, microsecond/múi giờ và việc validateTimes không tự chặn vé cũ vì expiry. Đây là unit test thời gian, không có chữ ký hoặc replay ledger thật. Bộ 7 test MySQL tại mục A.10 không được chạy lại trong lệnh này; không ghi thành một lần chạy tổng 15 test. Báo cáo target có thể bị thay bởi lần chạy sau. Trợ lý không chạy lệnh thay người dùng.

Bước thời gian hoàn tất trong phạm vi này; tiếp tục bộ ký HMAC tại [mục 3.2.6](#practice-ticket-signer), chưa toàn P01-TICKET hoặc ô checklist tổng hợp hoàn tất.

<a id="practice-ticket-signer-result"></a>

### A.12. P01-TICKET bộ ký HMAC — 9 unit test ĐẠT

Người dùng chạy `.\mvnw.cmd "-Dtest=StartTicketSignerTests" test`, log kết thúc **2026-11-05T14:07:51+07:00**: **9 test, 0 failures/errors/skipped, BUILD SUCCESS**; Maven **15.487 giây**, lớp test **0.505 giây**. Log biên dịch **5 file main/7 file test**, chỉ chạy StartTicketSignerTests. Trợ lý đối chiếu chỉ đọc Surefire TXT/XML, khớp số liệu và đủ chín tên ca, gồm `matchesRfc4231TestCase6`.

Đã có bằng chứng vector HMAC chuẩn, phát hiện thay payload/tag/sai key, kiểm tag đầy đủ và bảo vệ key đầu vào trong phạm vi unit test. Chưa chứng minh vé hoàn chỉnh, credential/epoch binding, khóa runtime lưu bền hoặc restart/restore. Đây là lần chạy riêng, không chạy lại 8 ca thời gian hoặc 7 ca MySQL. Tại mốc 14:07:51 này, codec mới chưa được biên dịch/chạy; không dùng kết quả HMAC để đánh đạt codec hoặc toàn P01-TICKET. Kết quả codec về sau được ghi riêng ở [mục A.13](#practice-ticket-codec-result). Trợ lý không chạy lệnh thay người dùng trong lần HMAC; báo cáo target có thể bị thay ở lần chạy sau.

<a id="practice-ticket-codec-result"></a>

### A.13. P01-TICKET codec v1 — trợ lý chạy theo quyền được cấp, 14 test ĐẠT

Ngày 05/11/2026, người dùng bổ sung quy ước comment tiếng Việt/bàn giao Git và giao cụ thể trợ lý chạy StartTicketCodecTests, xử lý lỗi trong phạm vi; quyền tại [handoff mục 7](../01-tong-quan/handoff.md#guidance-protocol). Sau cập nhật quy ước và comment, trợ lý trực tiếp chạy tại backend:

```powershell
.\mvnw.cmd "-Dtest=StartTicketCodecTests" test
```

**Kết quả thực tế:** **14 test, 0 failures/errors/skipped, BUILD SUCCESS**, mã thoát **0**, kết thúc **2026-11-05T14:32:52+07:00**. Maven **14.011 giây**, lớp test **0.595 giây**, biên dịch **6 file main/8 file test**. Chỉ StartTicketCodecTests được chạy; không khởi động Docker/MySQL hoặc ứng dụng. Không lỗi cần sửa và không chạy lại test chỉ để lặp bằng chứng.

Surefire TXT/XML trong `backend/target/surefire-reports` khớp đủ 14 tên ca. Có bằng chứng format v1/round-trip/microsecond, HMAC và dữ liệu bị sửa, sai key, envelope/encoding/version/field không hợp lệ, chặn cách biểu diễn thay thế, bảo vệ hash và giữ mốc cũ cho bước lookup. Chưa kiểm credential thật/request-key/epoch hiện hành, lớp cấp/xác minh đầy đủ, ledger/TX/API, runtime key/epoch bền vững hoặc restart/restore. Không cộng các lần 7/8/9/14 ca riêng thành một lần full suite đã chạy.

**Rà comment:** kiểm 14 file Java, sửa comment/Javadoc trong 11 file: main gồm `entity/GuestSession.java`, `repository/GuestSessionRepository.java`, `service/StartTicketTimePolicy.java`, `service/StartTicketSigner.java`, `service/StartTicketCodec.java`; test gồm `repository/GuestSessionRepositoryTests.java`, `schema/GuestSessionSchemaTests.java`, `service/StartTicketTimePolicyTests.java`, `service/StartTicketSignerTests.java`, `service/StartTicketCodecTests.java`, `support/TestcontainersConfiguration.java`. So sánh trước/sau loại comment xác nhận logic/annotation/mapping/assertion giữ nguyên; pom.xml, application.properties và V1 giữ nguyên hash. Không chỉnh file sinh tự động hoặc migration chỉ để đổi comment; ba file Java gốc không có comment cần đổi được giữ nguyên.

**Issue/branch:** đã đọc T03 [#51](https://github.com/khaithanh2726-beep/Thuyet-minh-tu-dong-QR-main/issues/51) hiện hành: người phụ trách và commit T03 là Thành. 14 ca codec không đủ toàn ô bắt đầu/kết thúc/kiểm phiên hoặc kiểm API, chưa nhắc tích. Người dùng gửi trạng thái đang main, README.md modified, backend/ và docs/ untracked; chỉ có main local/origin/main. Lệnh ls-remote cho `refs/heads/feat/t03-start-ticket` không có đầu ra, nhánh đề xuất chưa có trên remote ở lần kiểm này. Chưa có bằng chứng đã tạo/chuyển/push branch, stage/commit/merge/đóng issue; trợ lý không chạy Git.

**Cập nhật sau mốc codec:** người dùng đã tạo/push `feat/t03-start-ticket`, thiết lập upstream; chuẩn bị branch hoàn thành. README.md còn modified, backend/ và docs/ còn untracked theo kết quả người dùng gửi; push branch không đưa các file chưa commit lên GitHub. Đã chuyển sang [mục API ba lớp](#practice-start-api) theo quyền test thường xuyên và điểm dừng sau toàn mục checklist tại [quy ước chính](../01-tong-quan/handoff.md#guidance-protocol). Bàn giao commit phải đối chiếu thay đổi thực tế và phân nhóm nền dự án/tài liệu/T02/T03 phù hợp, không gom tất cả vào T03 hoặc git add . theo bản tổng hợp untracked.

<a id="practice-start-api-result"></a>

### A.14. API ba lớp/lookup hai nhánh — 64 test hồi quy ĐẠT

Ngày 05/11/2026, sau khi người dùng xác nhận branch `feat/t03-start-ticket` có upstream, trợ lý triển khai và kiểm theo quyền thường xuyên tại handoff mục 7. Không chạy Git, database đang dùng, TTS hoặc triển khai. Phạm vi mục checklist tại [3.2.8](#practice-start-api).

1. Chạy `.\mvnw.cmd "-Dtest=GuestInputPolicyTests,GuestSessionApiTests" test` tại backend: **18/0/0/0, BUILD SUCCESS**, mã thoát **0**, kết thúc **16:09:41+07:00**, Maven **1 phút 50 giây**. Đây là lần đầu gồm 6 input/12 API, chưa hai ca API bổ sung và cấu hình.
2. Bổ sung kiểm K11 body rỗng, nhánh ledger đã bị dọn bằng fixture, cấu hình khóa/epoch bắt buộc và dựng lại context; đối chiếu hợp đồng lỗi (JSON/path sai cấu trúc 400, validation 422, đủ trường lỗi). Sau đó chạy `.\mvnw.cmd clean test`: **64 test, 0 failures/errors/skipped, BUILD SUCCESS**, mã thoát **0**, kết thúc **2026-11-05T16:15:34+07:00**, Maven **1 phút 59 giây**; biên dịch 17 file main/12 file test. Surefire TXT/XML khớp đủ 9 lớp:

| Lớp test | Số ca đạt |
| --- | ---: |
| BackendApplicationTests | 1 |
| GuestRuntimeConfigurationTests | 6 |
| GuestSessionApiTests | 14 |
| GuestSessionRepositoryTests | 2 |
| GuestSessionSchemaTests | 4 |
| GuestInputPolicyTests | 6 |
| StartTicketCodecTests | 14 |
| StartTicketSignerTests | 9 |
| StartTicketTimePolicyTests | 8 |

**Quan sát:** API tích hợp qua MockMvc/Service/JPA/MySQL **8.4.13** tạm; Flyway áp dụng đúng V1 và Hibernate validate. Đã kiểm K11 chưa ghi phiên, K01 mới/replay/mất phản hồi/khác payload, hai credential cùng tên, sai quyền/chữ ký/epoch/thời gian, đúng mốc hết hạn vé/phiên, trạng thái không gia hạn, vé expired có/không ledger. Ca concurrent dùng barrier ép hai TX cùng đọc chưa có; một insert thắng, TX lỗi UNIQUE rollback rồi đọc trong TX mới; cùng ID, một row. Cảnh báo duplicate key trong ca tranh chấp/ràng buộc là kết quả âm dự kiến, không dùng nó thay assertion.

**Bằng chứng cục bộ:** `backend/target/surefire-reports/TEST-*.xml`, `*.txt` và `backend/target/test-run.log`; target là file sinh, không đưa vào commit, có thể bị thay khi chạy sau. Bảng này lưu tóm tắt lâu dài, dùng dữ liệu/khóa giả công khai, không secret thật. Không cộng các lượt riêng thành 64; đây là một lần clean test đầy đủ thực tế.

**Kết luận đúng phạm vi:** hoàn thành ô thực hành “Có API/ticket/Service/Repository đúng ba lớp và lookup/expiry hai nhánh”; dừng để người dùng xem. Chưa tự tích ô. Đã đọc lại T03 [#51](https://github.com/khaithanh2726-beep/Thuyet-minh-tu-dong-QR-main/issues/51) sau kết quả: ô “Xây bắt đầu/kết thúc/kiểm tra phiên: tên hợp lệ, phiên theo tab, hạn cố định 8 tiếng, không tự gia hạn hoặc tạo trùng.” vẫn thiếu K04 kết thúc và bằng chứng theo phạm vi đầy đủ; ô kiểm API cũng còn các API khách khác. **Chưa nhắc tích ô GitHub hoặc hoàn thành T03.** Chưa frontend/Web Locks, DEV bền vững, restart ứng dụng thật với ledger, cleaner D08, backup/restore hoặc triển khai. Code/docs chưa commit/push theo bằng chứng Git cuối của người dùng, dù branch đã push.
````

<a id="official-20261107-context-docs-readme-md"></a>

### docs/README.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Biên tập:** 07/11/2026. Nghiệp vụ/phạm vi PRD và các hướng UI/công nghệ được xác nhận đúng phần; toàn TDD, ranh giới, nội dung và bằng chứng phần mềm chưa được duyệt theo gói. Đã tiếp tục thực hành theo yêu cầu người dùng; áp dụng [quy ước hướng dẫn tại handoff](01-tong-quan/handoff.md#guidance-protocol).
**Cách tổ chức:** phân công/thiết kế theo nhóm ba thành viên G02 và kế hoạch ba máy; Thành trực tiếp thực hành bản chấm. [Handoff 7.3](01-tong-quan/handoff.md#execution-mode) quy định người thao tác thực tế, tác giả commit và nhịp dừng sau mục checklist lớn; không coi phân công là bằng chứng ba người đã làm.
| Dừng thực hành ở đâu / bàn giao chat | [Điểm dừng backend](03-ky-thuat/practice-01-start-session.md#practice-stop), [bàn giao ngắn](01-tong-quan/handoff.md) |
| [01-tong-quan/roadmap.md](01-tong-quan/roadmap.md) | T01–T11, phân công G02, checkbox có căn cứ, phụ thuộc/lịch và snapshot issue | Lập kế hoạch/ghi tiến độ. 0.11; đã tạo và đối chiếu đủ 11 issue #1–#11 trên repo mới, khớp 89 ô/18 dấu tích; mapping tại mục 6, người dùng xác nhận Project đủ 11 issue và đã đặt Status: 6 In Progress, 5 Todo, 0 Done; Codex chưa trực tiếp quan sát board. |
| [01-tong-quan/technical-inputs.md](01-tong-quan/technical-inputs.md) | Đầu vào đã xác nhận, khảo sát TTS/ngrok có ngày, bộ phiên bản, chính sách và đầu vào TDD | Trước thiết kế/cấu hình phần tương ứng. 0.7; đã chọn khác đã chạy thử; khảo sát dịch vụ giữ mốc 03/11, chưa kiểm lại ở lượt biên tập. |
| [01-tong-quan/handoff.md](01-tong-quan/handoff.md) | Đường đọc, trạng thái, quyết định chính, bằng chứng và điểm dừng backend | Khi đổi chat hoặc tiếp tục công việc; file sống được cập nhật cùng mốc triển khai. |
Có **15 khối Mermaid hiện hành** trong tài liệu kỹ thuật/hướng dẫn; không tính sơ đồ nằm trong bản lưu trữ. Lượt này không vẽ mới, đổi nội dung hoặc xuất lại sơ đồ.
Không yêu cầu phải vẽ thêm mọi loại sơ đồ trước khi triển khai; chỉ lập danh mục phần chưa có. Không kiểm lại bố cục Mermaid/SVG trong lượt này; các kết quả xem hình trước đây giữ đúng mốc ở lịch sử DFD.
2. **Tra cứu khi thầy hỏi:** mục 1 ở trên → quyết định nguồn → BR/FR/UC/AC tương ứng → sơ đồ giải thích → bằng chứng thực tế (nếu đã có). Nói rõ cái đã chọn/đã mô tả và cái đã chạy; đã có bằng chứng backend nhưng chưa nghiệm thu toàn hệ thống.
- Thư mục hiện hành `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`, origin đã trỏ repo mới theo đầu ra người dùng. Checklist 11 task/89 ô/18 dấu tích đã duyệt; mapping issue mới #1–#11 đã đối chiếu, người dùng xác nhận Project đủ 11 issue và đã đặt Status theo hướng dẫn; chưa có quan sát board trực tiếp từ Codex. Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. Không dùng detached 55b0906 làm hiện trạng. [Handoff](01-tong-quan/handoff.md#next-step) và [kế hoạch commit](01-tong-quan/commit-plan-2026-11-05.md) ghi mapping issue và hướng dẫn bàn giao trước commit; không gom toàn backend/docs vào T03.
- Bộ 20 văn bản/20 audio vẫn mục tiêu nghiệm thu; năm nháp vi đã gửi trong chat, chờ xác minh bản lưu/rà soát/duyệt. TDD/R06, nền/vùng, nội dung và tương thích toàn bộ chưa đóng theo kết quả test backend. [Điểm mở](02-nghiep-vu/decisions.md#dec-6), [R06](02-nghiep-vu/prd-review.md#review-open-final), [TDD](03-ky-thuat/TDD.md#tdd-open).
Đọc [bàn giao hiện hành](01-tong-quan/handoff.md), nêu đường dẫn/phiên bản/trạng thái, quyết định chính, phần mở và bằng chứng; không giả hai chat cùng lịch sử. Người dùng đã yêu cầu tiếp tục thực hành; xem nhiệm vụ chờ kết quả và quy ước hướng dẫn trong handoff.
````

<a id="official-20261107-context-docs-01-tong-quan-technical-inputs-md"></a>

### docs/01-tong-quan/technical-inputs.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/01-tong-quan/technical-inputs.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Cách đọc bằng chứng:** cấu hình/mã nhìn thấy, đề xuất cũ, kết quả hồ sơ và kết quả dự án chính là các loại khác nhau. Nguồn dịch vụ giữ mốc khảo sát **03/11/2026**, chưa kiểm lại giá/quota/API trong lượt biên tập; không suy ra đã gọi TTS.
| Hướng dẫn | Theo gói công việc, nêu việc/mục đích/cách kiểm và điều kiện dừng; Git trình bày gọn các bước trọn nhóm, chỉ gửi kết quả ở mốc cần thiết. Phân công/thiết kế theo nhóm ba người G02; Thành thực hành mọi thao tác theo [handoff 7.3](handoff.md#execution-mode). | Không mặc định một lượt một lệnh hoặc coi phân công là bằng chứng ai đã làm; dừng sau toàn mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. |
| Môn học | Chủ đề 2: BE Dev theo hướng 3 lớp bao gồm CD/CI. Backend phải thể hiện Controller → Service → Repository và bằng chứng CI/CD hoạt động; Frontend đầy đủ. | Căn cứ trực tiếp là lời xác nhận người dùng; lượt này không có ảnh/rubric để kiểm tra độc lập. React Native/React.js của chủ đề 1 không thành bắt buộc chủ đề 2. Giữ Vue 3/TECH-01. |
| Gemini | Người dùng xác nhận key hoạt động/còn quota/Free tier; chọn gemini-3.8-flash-tts + Charon, đã nghe sáu MP3 thử và hài lòng. | Không thu thập key hoặc chi tiết tài khoản. Google AI Plus không là bằng chứng quyền API. Không suy ra 20 cặp đã duyệt, quota đủ 20 lần hoặc được gọi API lượt này. |
| 2 | Cung cấp một bài vi nháp và nguồn để rà soát; chuẩn bị ba bản en/fr/ru tương ứng hoặc mẫu đọc không khẳng định lịch sử. | Năm nháp đã gửi trong chat trước, chưa xác minh bản lưu trong repo; không tự tái tạo. Mẫu thử không thành bài được duyệt. |
**Người dùng đã chọn nguyên bộ phiên bản mục 4 của bản 0.1 làm mốc triển khai theo TECH-03/IN-P01.** Giữ nguyên các mốc dưới đây. Đã đọc cấu hình Boot 4.1.2/Java 21/Wrapper Maven 3.9.12 trong bộ khung và nhận kết quả công cụ; chưa có bằng chứng resolve/build/test hoặc tương thích toàn tổ hợp. Sự không khả dụng/không tương thích phải có bằng chứng, ảnh hưởng và đề xuất điều chỉnh; không đổi âm thầm. Không lấy “đã chọn” hoặc “cài được” làm “nghiệp vụ đã đạt”.
Ngrok docs mô tả refresh theo chu kỳ tháng; trang pricing/limits có câu đầu tháng, free-plan nói monthly billing cycle. Không dự đoán ngày reset tài khoản từ hai cách diễn đạt; ghi chu kỳ/usage thực khi người dùng kiểm dashboard. Không đăng ký, cài, mua domain/hosting/gói trả phí hoặc gọi API trong lượt này.
Lịch nhóm cần tính công việc có thể làm song song, phụ thuộc dữ liệu/API, thời gian rà soát nội dung, tích hợp và dự phòng. Đây là mô hình ba thành viên; lượt thực hành do Thành thao tác toàn bộ phải đối chiếu quỹ giờ thực tế của Thành và bằng chứng sản phẩm, không coi đã có ba người làm song song. Quỹ giờ của từng thành viên được xác nhận khi theo dõi tiến độ; chưa có căn cứ cam kết chắc hoàn thành toàn phạm vi vào 17/11. Ưu tiên kiểm sớm phần rủi ro và cập nhật theo sản phẩm thực tế.
| IN-P05 / DATA-P01–P04 | DATA-01 và DATA-02: backup cục bộ tách dữ liệu chạy cho bản chấm, không ổ ngoài bắt buộc; giữ hạn/restore | DATA-03 chốt phạm vi-nhịp; path/quyền/công cụ/RPO-RTO và kiểm restore chưa chọn/đo; không mục tiêu mất/hỏng laptop |
````

<a id="official-20261107-context-docs-01-tong-quan-roadmap-md"></a>

### docs/01-tong-quan/roadmap.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/01-tong-quan/roadmap.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Phiên bản nội dung:** 0.11; biên tập 06/11/2026. **Trạng thái:** người dùng đã duyệt phương án 11 issue với 89 ô checklist/18 ô đã tích, kế thừa phân công G02. Đã tạo và đọc lại đủ 11 issue #1–#11 trên repo mới theo yêu cầu trực tiếp của người dùng ngày 06/11/2026; khớp toàn bộ tiêu đề/Description/Checklist. Mapping tại mục 6; người dùng xác nhận Project 21 đủ 11 issue và đã đặt Status theo hướng dẫn: 6 In Progress/5 Todo/0 Done; Codex chưa trực tiếp quan sát board. Phân công không chứng minh thành viên đã thực hiện.
**Giới hạn:** checkbox/thiết kế không thay bằng chứng phần mềm. [Quy trình cộng tác hiện hành](handoff.md#guidance-protocol), gồm quyền kiểm thử thường xuyên, điểm dừng sau toàn mục checklist, comment tiếng Việt và branch/bàn giao commit theo thay đổi thực tế, nằm duy nhất tại handoff; những câu tạm dừng/chưa chạy trong mốc cũ không là quyền hoặc điểm dừng hiện tại. Không tự sửa issue/Project.
| [Mục lục và bàn giao hai chat](../README.md), [README gốc](../../README.md) | Tài liệu đã tổ chức, có hướng đọc và kiểm tra phiên bản/trạng thái; kết quả thuộc T01, không tính lại ở T11. |
| [D06 — năm nháp vi](../02-nghiep-vu/decisions.md#d06) | Người dùng đã gửi nguyên văn năm bản nháp tiếng Việt trong chat trước; chưa xác nhận bản lưu trong repo, chưa có bằng chứng rà soát/duyệt. Không tự dựng lại hoặc tích hoàn tất T08. |
Nguồn FR/UC/AC/NFR vẫn ở PRD. Bằng chứng checklist GitHub ở mục 6 thuộc các lượt trước, không giả đã đọc lại trong lượt này. Phân công và người commit theo G02; tiến độ xác định bằng sản phẩm và bằng chứng.
**Xuất bản đã ghi nhận ở lượt trước (02/11/2026):** T01–T11 tương ứng #49–#59; Project tự nhận 11 issue. Đã đọc lại tiêu đề/body và quan sát Todo 8, In Progress 3 (T01/T08/T10), Done 0; tải lại kiểm tra cuối bị công cụ dừng. Không diễn giải là đã xác minh lại trong lượt này; xem [căn cứ](../99-luu-tru/decisions-history.md#tech-01-source).
**Mô hình nhóm và thực hành:** bảng phân công/người commit, lịch và phối hợp vẫn theo ba thành viên G02. Với bản chấm đang thực hành, Thành trực tiếp thao tác mọi task và dùng danh tính của mình; nhật ký không gán công việc thực tế cho người khác. Xem [handoff 7.3](handoff.md#execution-mode). Lịch ba người là kế hoạch, không cộng giả quỹ giờ của ba người vào tiến độ do một người đang thực hiện.
| [T08](#t08) | Thành, Nghĩa, Hòa | Thành, Nghĩa, Hòa theo phần phụ trách | Đang thực hiện | Đã gửi năm nháp vi trong chat trước; chưa xác minh bản lưu trong repo hoặc rà soát/duyệt; chưa tích hoàn tất nội dung. |
Mỗi issue chỉ có **Description và một Checklist**. Người dùng đã duyệt phương án **89 ô, 18 dấu tích** sau rà soát chat Codex mới. Giữ nguyên phạm vi task và phân công; không tách tiếp thành từng file/class/hàm. Chín dấu tích bổ sung ghi nhận lựa chọn/khảo sát đã có căn cứ, không xác nhận tích hợp hoặc vận hành đã đạt; không dùng 18/89 làm phần trăm hoàn thành. Bộ 9/78 của repo cũ chỉ là snapshot. Thay đổi câu chữ lần này: T01 kiểm chứng kênh CD đã chọn, T06 ghi rõ Windows/Chrome, T08 phản ánh năm nháp đã gửi; T04 giữ TECH-04. Toàn TDD, môi trường, nội dung và nghiệm thu vẫn theo giới hạn riêng.
Nguồn để xuất tiêu đề, Description và Checklist là **mục 3 của file này**. Không dùng bản sao gốc làm backlog hiện hành. Người dùng duyệt phương án mới ngày 06/11/2026 qua tin nhắn “được cho tôi prompt để gửi cho chat Codex mới”, sau đề xuất chọn 89 ô/18 dấu tích; lượt đồng bộ này tiếp tục được giao trực tiếp.
| T08 | 7 | 0 | Có nháp trong chat, chưa xác minh bản lưu/rà soát/duyệt |
Repo đích: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue). Board đích: [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Bảng dưới là mapping đã đọc lại từ GitHub ngày 06/11/2026, khớp tiêu đề/Description/Checklist tại mục 3. Người dùng xác nhận ngày 06/11/2026: Project 21 đã có đủ 11 issue. Người dùng tiếp tục xác nhận đã đặt xong Status theo hướng dẫn: 6 In Progress/5 Todo/0 Done. Cột trạng thái ghi theo xác nhận này; Codex chưa trực tiếp quan sát board.
1. Theo yêu cầu trực tiếp ngày 06/11/2026, Codex đã kiểm repo chưa có issue rồi tạo đủ T01–T11 thành #1–#11, không gán assignee hoặc thay phân công G02.
4. Người dùng xác nhận ngày 06/11/2026: Project 21 đủ 11 issue và đã đặt Status theo hướng dẫn. Bảng 6.2 ghi theo xác nhận người dùng; Codex chưa trực tiếp quan sát board. Không cần tạo/thêm lại hoặc đặt lại Status.
5. Đã hoàn tất chuyển giao issue/Project theo xác nhận người dùng. Các đầu ra đã có: main chưa commit, bốn mục untracked, remote chưa có nhánh; cấu hình Git đã đổi từ KhaiThanh sang gingpham, chưa có bằng chứng stage/commit/push. Người dùng xác nhận Thành trực tiếp thực hành mọi task; G02 vẫn là kế hoạch ba người. Bước tiếp theo: khôi phục danh tính Git Thành, gửi mốc A trước commit T10 khởi tạo main chỉ `.gitignore`, rồi mốc B sau push. Sau các nhóm nền hiện có, Codex tiếp tục triển khai và dừng theo [handoff 7.3](handoff.md#execution-mode) để cùng xem/kiểm thử nhẹ/commit. Không coi commit hoặc 64 test cũ là hoàn thành toàn issue.
````

<a id="official-20261107-context-docs-02-nghiep-vu-prd-md"></a>

### docs/02-nghiep-vu/PRD.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/02-nghiep-vu/PRD.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
Theo G01, đây là phạm vi bản hoàn chỉnh để đi chấm: nghiệp vụ nhất quán, thao tác rõ, có thể kiểm thử và khối lượng phù hợp nhóm. Phạm vi và tiêu chuẩn nghiệm thu áp dụng thống nhất cho dự án; không tự thêm chức năng nâng cao.
Thứ tự chuẩn bị: một điểm tiếng Việt → năm điểm tiếng Việt → bổ sung en/fr/ru. Người dùng đã chuẩn bị năm bản nháp vi, **chờ cung cấp và rà soát**, chưa đưa nguyên văn vào tài liệu, chưa được duyệt/kiểm chứng. Tên địa điểm ở đây là danh sách phạm vi, không phải tài liệu thẩm định lịch sử.
| AC-A26 | **Cho trước** bộ nội dung chuẩn để nghiệm thu; **khi** đối chiếu ma trận và hồ sơ; **thì** năm điểm có đủ 20 văn bản và 20 audio đúng vi/en/fr/ru được duyệt/áp dụng theo D09; audio nghe trọn/khớp bản. Thiếu nội dung xử lý tốt vẫn chưa đủ bộ chuẩn. Năm bản vi đã gửi trong chat nhưng chưa xác minh bản lưu/rà soát/duyệt không được tính là nội dung đã duyệt; không tự tăng bộ này theo mọi điểm thêm. | FR-A12/A07; UC-A08, UC-A05; D06/D09 |
- Đọc mục lục **docs/README.md** và dùng các đường dẫn mới ở trên. Không giả định chat khác dùng cùng phiên bản: đối chiếu đường dẫn, ngày và nhật ký hiện hành trước khi sử dụng.
````

<a id="official-20261107-context-docs-02-nghiep-vu-prd-review-md"></a>

### docs/02-nghiep-vu/prd-review.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/02-nghiep-vu/prd-review.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Dấu vết trước khi sửa trong lượt này:**
| **Công việc nội dung:** năm nháp vi đã gửi trong chat, chờ xác minh bản lưu/rà soát nguồn, bản dịch, nghe trọn audio và duyệt từng phiên bản | D06/D09; FR-A07/A12; UC-A05/A08; AC-A14/A25/A26; NFR-05 | Không chặn duyệt yêu cầu chức năng/quy trình; chặn đưa từng bản chưa duyệt ra khách và nghiệm thu đủ bộ 20 văn bản/20 audio. | Rà soát/duyệt trước áp dụng từng phiên bản; đủ bộ chuẩn trước nghiệm thu. | Người dùng cung cấp bản nháp; người có năng lực phù hợp rà soát hoặc ghi giới hạn → người dùng duyệt từng bản theo D09. |
````

<a id="official-20261107-context-docs-02-nghiep-vu-decisions-md"></a>

### docs/02-nghiep-vu/decisions.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/02-nghiep-vu/decisions.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
| G01 | [Định hướng bản đi chấm](#g01) | Đã chốt 30/10/2026 |
| G02 | [Phân công nhóm và người commit](#g02) | Đã chốt 02/11/2026; backlog 0.2 đã duyệt sau đó, bản diễn giải mới xem roadmap; chưa chốt lịch/GitHub username |
| G03 | [Nhắc checklist theo bằng chứng](#g03) | Đã chốt; đọc đúng issue hiện hành, người dùng tự tích, không tự sửa GitHub |
| DATA-02 | [Backup cục bộ cho bản chấm](#data-02) | Đã bỏ yêu cầu ngoài laptop; giữ hạn/restore DATA-01, không mục tiêu cứu hỏng/mất toàn máy |
Ba xác nhận chỉ duyệt nghiệp vụ hai vai trò. Không duyệt theo bố cục giao diện, thao tác vẽ/ranh giới cụ thể; lịch sử/bản dịch/audio; nhà cung cấp TTS/giọng/chi phí/công nghệ; thiết kế kỹ thuật hoặc kết quả kiểm thử. Năm bản vi đã gửi trong chat, vẫn là nháp chờ xác minh bản lưu và rà soát/duyệt. Các nhánh AD trong [Business Flow mục 4.1](business-flow.md#bf-admin-main) chi tiết hóa 12 nhóm Admin đã tổng hợp, không bổ sung nhóm nghiệp vụ mới.
**Đối chiếu trước ghi nhận:** Đã đọc trực tiếp PRD.md, business-flow.md, decisions.md và prd-review.md trong repo chính. Bản lưu đúng **0.4.1**, gồm Q1–Q3 và BR-11 đã sửa theo R07; không thấy khác biệt ảnh hưởng phạm vi xác nhận. Lượt này giữ phiên bản và nội dung yêu cầu, chỉ cập nhật trạng thái/căn cứ/dẫn chiếu và tạo thiết kế dự thảo.
| Nội dung lịch sử, bản dịch/audio; kết quả kiểm thử hoặc phần mềm đáp ứng | Không được duyệt hoặc xác minh qua PRD-01. D09 tiếp tục áp dụng; năm bản vi đã gửi trong chat, vẫn là nháp chờ xác minh bản lưu và rà soát/duyệt. |
**Bước tiếp theo tại thời điểm PRD-01 (lịch sử):** soạn [docs/ui-design.md](../04-giao-dien/ui-design.md) phần khách — **dự thảo** danh sách màn hình/bố cục/trạng thái và đề xuất tương tác để người dùng góp ý. Chưa duyệt sẵn thiết kế, chưa thiết kế Admin trong lượt này, chưa chọn công nghệ hoặc khảo sát TTS, chưa soạn toàn bộ TDD/roadmap hay thao tác phần mềm.
**Căn cứ:** tin nhắn “Hãy xem đây là quyết định đã được tôi duyệt, không chỉ là đề xuất từ bản demo.” Đây là xác nhận cho repo chính; không suy ra hoặc sao chép từ dự án tham khảo.
- Một repo, tách `frontend/` và `backend/`; chưa tạo các thư mục/mã nguồn trong lượt này.
**Lý do ghi nhận:** người dùng đã chọn hướng công nghệ cụ thể cho dự án chính để làm đầu vào thiết kế và hướng dẫn từng phần; kiến trúc và phân công giữ theo phạm vi đã thống nhất. Không diễn giải lựa chọn thành kết luận nhóm đã thành thạo hoặc các thư viện đã đáp ứng toàn bộ yêu cầu.
| Gemini | Người dùng xác nhận key hoạt động/còn quota/project Free tier. Google AI Plus không chứng minh quyền API TTS được bao gồm. | Chưa kiểm tài khoản bằng API, không lưu key/dữ liệu nhạy cảm; chưa cho phép gọi API hoặc chi phí trong lượt này. |
- Bằng chứng chất lượng ở mức **người dùng báo cáo đã nghe/hài lòng sáu MP3**; trợ lý không nghe lại/kiểm file lượt này. Không đổi hồ sơ cũ thành xác nhận đã nghe ở ngày trước.
### 3.10. G03 — Nhắc checklist GitHub theo bằng chứng
- Nhắc theo mẫu: “Bạn sang issue Txx — [tên issue/link], tích mục ‘[nguyên văn checklist]’. Căn cứ hoàn thành: …”.
- Người dùng trực tiếp tích; trợ lý không tự sửa issue/Project hoặc đóng issue. Khác local thì báo và chuẩn bị nội dung để người dùng cập nhật.
### 3.12. DATA-02 — Backup cục bộ, phạm vi phục hồi bản chấm
**Đã xác nhận ngày 03/11/2026.** Bỏ yêu cầu phải chuẩn bị ổ ngoài hoặc nơi ngoài laptop cho bản chấm. Cho phép thiết kế backup cục bộ tại vị trí tách dữ liệu đang chạy; không tự bỏ backup.
- Các phương án lưu mã hóa ở thiết bị khác của technical-inputs 0.1 chỉ còn là lựa chọn ngoài phạm vi bắt buộc, không điều kiện chặn TDD/nghiệm thu bản chấm.
- Giữ một Backend/ba lớp, source frontend/backend riêng và Vue build cùng Spring Boot theo TECH-04. Chưa chọn credential/path/port/workflow cụ thể hoặc cho phép cài/cấu hình trong lượt này.
- **Cập nhật đầu vào 06/11/2026:** người dùng đã gửi nguyên văn năm đoạn tiếng Việt trong chat trước. Chưa xác nhận file lưu trong repo, chưa có bằng chứng đối chiếu nguồn/rà soát/duyệt. Chat mới không tự có lịch sử đó; tiếp nhận bản lưu khi làm T08, không tái tạo hoặc coi là nội dung đã duyệt.
- Phiên bản đi chấm hoạt động trực tuyến theo phạm vi đã chốt.
**Đã chốt đúng các phần dưới đây. Căn cứ:** tin nhắn người dùng bắt đầu “Tôi đã thống nhất với chat đánh giá về hướng giao diện khách tham quan dưới đây”. Đã đọc trực tiếp ui-design 0.1/PRD 0.4.1 và các nguồn trước ghi nhận; không áp lại một bản sửa được cho biết đã hoàn tác. Đây là xác nhận bổ sung sau PRD-01, không tự duyệt toàn bộ UI, UI-P01–UI-P04, nền/vị trí/ranh giới, bản dịch hoặc giải pháp kỹ thuật.
**ĐÃ CHỐT đúng các hành vi dưới đây. Căn cứ:** tin nhắn người dùng “Tôi xác nhận các phương án UI dưới đây sau khi trao đổi với chat đánh giá”, gồm thay đổi có chủ đích so với P01 cũ. Giữ mã P01/UI-P01–UI-P04; UI-02 là mã bổ sung để truy vết lượt xác nhận, không thay mã yêu cầu cũ. Phạm vi UI-P01/UI-P02/UI-P04 đã xác nhận không đồng nghĩa duyệt mọi thiết kế, cách đóng chưa nêu, UI Admin, bản đồ, nội dung, TTS hoặc kỹ thuật.
**Người xác nhận:** Người dùng. **Ngày:** 31/10/2026. **Căn cứ:** tin nhắn “Tôi đã hiểu các phương án và đồng ý duyệt Q1–Q3 tại mục 9.4 của PRD bản 0.3, bao gồm các chi tiết và giới hạn đã trình bày”. Đã đối chiếu trực tiếp nội dung, không có khác biệt thực chất; các phần dưới thay trạng thái đề xuất Q1–Q3, không thay các quyết định D/P/A/G/BF khác.
| Nội dung/hồ sơ | Năm bản vi đã gửi trong chat; chờ xác minh bản lưu, rà soát nguồn, bản dịch, nghe trọn audio và duyệt từng bản. Mã phiên bản/mẫu hồ sơ cụ thể còn chuẩn bị; UI-AP05 đã chốt tham chiếu riêng trong hồ sơ cặp, không bắt nhập lại người/ngày nếu hồ sơ đã có. | D09 và A03 yêu cầu đối chiếu hồ sơ và tham chiếu phù hợp; không cần phần mềm tự biết phê duyệt bên ngoài, không dùng AI/dịch ngược làm thẩm định chuyên gia. Không áp độ dài gợi ý cũ 100–150 từ. |
````

<a id="official-20261107-context-docs-02-nghiep-vu-business-flow-md"></a>

### docs/02-nghiep-vu/business-flow.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/02-nghiep-vu/business-flow.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
Theo **G01**, phân tích phiên bản hiện tại như bản hoàn chỉnh để đi chấm: nhất quán nghiệp vụ, trải nghiệm rõ, kiểm thử được, khối lượng phù hợp nhóm. Phạm vi và tiêu chuẩn nghiệm thu áp dụng thống nhất cho dự án; không tự mở rộng chức năng nâng cao.
| E05 | Nhánh lỗi cung cấp tọa độ của dự thảo cũ. | D01/D05: ngoài phạm vi đã chốt của phiên bản đi chấm. | Giữ mã để tra cứu, không phải yêu cầu cần triển khai. |
| Văn bản/bản dịch — AD04 | Chọn điểm/ngôn ngữ/bản đang sửa; Q2 bảo vệ/khôi phục bản tạm đúng công việc với nhãn chưa lưu. Chủ động Lưu nháp thành công xóa tạm tương ứng, vẫn tách bản đang áp dụng; khách tiếp tục thấy bản hợp lệ cũ. | Giữ tạm khác lưu nháp/Áp dụng; không tự dịch/gắn audio cũ vào bản mới hoặc kho lịch sử phiên bản phức tạp. Năm nháp vi đã gửi trong chat, chờ xác minh bản lưu và rà soát/duyệt. |
**Làm rõ hiện hành TECH-04/DATA-02:** mỗi tab Bắt đầu có ID/credential riêng được backend kiểm, trùng tên không gộp; đổi tiếng/kết thúc A không đổi B, mở/reload không tự tạo phiên. Khách tải trọn MP3 hiện tại cho tab rồi phát; chỉ bài đang phát tại expiry được pause/resume, không cấp cho pause trước mốc/đang tải/response muộn. Vào form mới dừng/nhả A1 phù hợp, xác nhận phiên mới rồi chọn vùng lấy cặp hiện hành A2; không auto-play vì reload. Backup bản chấm được lưu cục bộ tách dữ liệu chạy, không mục tiêu cứu mất/hỏng toàn laptop; vẫn bảy ngày/restore riêng/lọc mốc gốc. Cách làm cụ thể theo TDD dự thảo, không bằng chứng đã chạy.
UI-01–UI-04 đã được xác nhận đúng phần; ảnh khách 0.2 chỉ mô tả bố cục cơ sở, xem sơ đồ box hiện hành. Hướng Admin lượt 1–3 đã chốt đúng phạm vi UI-03–UI-05. Đọc [ui-design mục 12](../04-giao-dien/ui-design.md#ui-next-technical) về các việc còn lại trước/trong TDD; không hỏi duyệt lại hành vi đã rõ. Thiết kế cụ thể/nền/vùng chưa duyệt theo gói; năm bài vi vẫn chờ cung cấp/rà soát. Không tự chọn TTS, công nghệ hoặc tuyên bố đã kiểm chứng. Bàn giao nêu đường dẫn, trạng thái, quyết định đã chốt, điểm mở và bước tiếp theo; không giả định chat khác có cùng phiên bản file, không sửa yêu cầu để hợp thức hóa code.
````

<a id="official-20261107-context-docs-04-giao-dien-ui-design-md"></a>

### docs/04-giao-dien/ui-design.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/04-giao-dien/ui-design.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Ảnh phác thảo 0.2 phía trên chỉ mô tả bố cục cơ sở/cảnh báo 5 phút, chưa có ba box này hoặc đủ trạng thái nút hết hạn.** Không coi ảnh cũ đã mô tả đầy đủ; lượt này không đổi ảnh, nền hoặc hình học vùng. Sơ đồ văn bản dưới ghi đúng phạm vi chặn đã xác nhận, chưa duyệt pixel/đồ họa cụ thể.
- Tên điểm/mã/ngôn ngữ là ngữ cảnh, không nhập một bản thuyết minh thứ hai vào form địa điểm. Lượt này đề xuất ô văn bản đơn giản, không công cụ tự dịch hoặc trình biên tập nội dung nâng cao.
| Nội dung và hồ sơ | Năm bản vi đã gửi trong chat, vẫn là nháp chờ xác minh bản lưu/rà soát/duyệt. Khuyến nghị nhận một bài và nguồn làm mẫu trước khảo sát giọng/thiết kế hồ sơ; chưa cần đủ 20+20 để viết TDD. Không tự tái tạo hoặc duyệt nội dung. | Chuẩn bị nguồn/bản dịch/hồ sơ đúng bản, nêu giới hạn rà soát theo D09; đủ cặp được duyệt trước áp dụng, đủ bộ năm điểm × bốn ngôn ngữ trước nghiệm thu. | Người dùng cung cấp/duyệt; người có năng lực phù hợp rà soát hoặc ghi rõ giới hạn. |
````

<a id="official-20261107-context-docs-03-ky-thuat-tdd-md"></a>

### docs/03-ky-thuat/TDD.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/03-ky-thuat/TDD.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Bằng chứng hiện tại:** test skeleton/OSIV/HTTP và V1 có log người dùng; bộ 7 test sau sắp package tại phụ lục A.10 được giữ làm lịch sử. Lần trợ lý chạy `clean test` mới nhất đủ 64 ca, gồm 14 API/MySQL, 6 cấu hình và các ca nền/ticket/input; Surefire khớp. Cấu hình thực ngoài repo, toàn schema, DEV bền vững, browser, SSH/CD/backup/restore còn chưa kiểm; không lấy kết quả database tạm làm bằng chứng môi trường thật. Điểm dừng theo [bàn giao hiện hành](../01-tong-quan/handoff.md).
## 1. Đầu vào đã chốt và phạm vi của lượt này
**Kiểm chứng:** bộ 7 sau sắp xếp giữ tại [mục A.10](practice-01-start-session.md#practice-mapping-test-result); về sau các ca đó vẫn đạt trong [64 test clean test](practice-01-start-session.md#practice-start-api-result) do trợ lý chạy theo quyền thường xuyên. Test support public dùng được qua package; DynamicPropertyRegistrar chỉ cấp fixture cho context import nó. V1/mapping/test nền giữ nguyên. Chưa kiểm lại HTTP test-run/đóng gói JAR hoặc áp dụng cấu hình runtime thật. Lệnh hồi quy khi có thay đổi là `.\mvnw.cmd clean test` tại backend, không yêu cầu chạy lại chỉ để lặp bằng chứng.
Nguồn [Pricing/limits](https://ngrok.com/docs/pricing-limits) công bố Free 1 GB outbound và 20.000 HTTP request/tháng. Đây là trần tài khoản, không 20.000 khách; MP3/ảnh/assets/Range và API cùng tiêu hao. Cần đo file/lượt thật và xem quota còn lại trước buổi chấm, không hứa đủ theo phép tính minh họa. Không mua domain/hosting/gói trả phí hoặc tạo tài khoản trong lượt này.
Chưa phát hiện trở ngại làm đổi LIFE-P01 đã chốt: expiry vé 24 giờ ngắn hơn thời điểm purge ledger D08, nên không cần phối hợp purge generation. [Thực hành hiện tại](practice-01-start-session.md#practice-start-api-result) đã kiểm K11/K01/K02, binding, ledger/expiry hai nhánh và UNIQUE concurrent trên MySQL tạm trong bộ 64 ca. Cấu hình key/epoch bắt buộc ngoài repo đã có test, chưa áp dụng hoặc kiểm môi trường thật/restart/restore/D08/browser. Tiến độ và quy ước tại [handoff](../01-tong-quan/handoff.md#guidance-protocol), gồm comment tiếng Việt, phân quyền test/Git và điểm dừng theo mục checklist. T03 #51 còn K04 và các phần khác, chưa đủ toàn ô mới để nhắc tích. Nếu thực tế đổi hành vi/phạm vi/chi phí/ngưỡng sẽ trình bằng chứng; không tự sửa GitHub.
````

<a id="official-20261107-context-docs-03-ky-thuat-practice-01-start-session-md"></a>

### docs/03-ky-thuat/practice-01-start-session.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/03-ky-thuat/practice-01-start-session.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Thực hành bản chấm:** tài liệu/phân công giữ mô hình nhóm ba người; Thành trực tiếp thao tác mọi task theo [handoff 7.3](../01-tong-quan/handoff.md#execution-mode). Bàn giao Git các nhóm hiện có trước, rồi Codex tiếp tục code/test tách biệt và dừng sau toàn mục checklist/issue có bằng chứng để cùng xem, kiểm thử nhẹ và commit. Các mốc class/unit test dưới đây là chặng học, không mặc định là điểm dừng mới.
Bảng sau giữ các lệnh của lượt kiểm ban đầu để tra cứu, không yêu cầu người dùng chạy lại năm lệnh đã gửi. Trợ lý chưa chạy chúng thay người dùng. Nếu tới bước sau Windows mời cài/cập nhật thì ghi thông báo, chưa tự chấp nhận thay đổi.
Các bước đã có bằng chứng được giữ để hiểu/tái hiện khi cần, không là yêu cầu chạy lại. Codex tự kiểm theo quyền tại handoff; người dùng trực tiếp thực hiện Git/database đang dùng/TTS/triển khai. Điểm tiếp tục hiện tại ở mục 3.1.
- Java/Javac 21.0.12.1; Docker CLI/Engine 29.8.1; Compose 5.5.1; WSL mặc định 2; Engine OSType=linux: theo đầu ra người dùng đã cung cấp.
- Đã đọc backend/.mvn/wrapper/maven-wrapper.properties: distributionUrl trỏ **Maven 3.9.12**. Người dùng đã chạy Wrapper đúng mốc và Maven test skeleton đạt theo mục A.3/1.5.
ServiceEpoch/khóa ký giữ trong runtime được bảo vệ ngoài repo theo thiết kế; chưa tạo file môi trường hoặc key thật ở lượt này. Quy trình restore epoch của REC-P01 vẫn cần chặng kiểm riêng.
**Trạng thái:** trợ lý đã tạo V1 theo thiết kế/mẫu; người dùng đã chạy và có kết quả áp dụng version 1/test đạt tại [mục A.8](#practice-v1-result). Không sửa V1 trong lượt viết test ràng buộc tiếp theo. Phần lệnh/cách kiểm dưới đây giữ để tra cứu bước đã đạt, không yêu cầu chạy lại chỉ để lặp bằng chứng.
Bạn chạy `.\mvnw.cmd test` tại `backend`, Docker Engine vẫn hoạt động. Lệnh nạp context và áp dụng migration trên MySQL do Testcontainers quản lý; trợ lý chưa chạy. Mong đợi Flyway tìm/áp dụng migration version 1 (guest session), không còn No migrations found; test 1/0/0/0 và BUILD SUCCESS. Kết quả này chỉ chứng minh migration được áp dụng khi nạp context, chưa chứng minh UK/CHECK xử lý dữ liệu sai/concurrency.
**Trạng thái:** người dùng đã chạy [GuestSessionSchemaTests.java](../../backend/src/test/java/vn/hueqr/backend/schema/GuestSessionSchemaTests.java), đủ 4 ca đạt trước sắp xếp package theo [mục A.9](#practice-schema-test-result), tiếp tục đạt trong bộ 7 test sau sắp xếp tại [mục A.10](#practice-mapping-test-result). Dùng JdbcTemplate gửi INSERT/SELECT xuống MySQL 8.4.13; riêng lớp này chưa kiểm mapping Entity/Repository. Lệnh/cách kiểm bên dưới giữ cho tra cứu giai đoạn 5 test, không phải yêu cầu chạy lại.
**Trạng thái:** đã biên dịch/chạy sau sắp xếp package theo [mục 3.2.4](#practice-package-layout); hai test Repository đạt trong bộ 7 test theo [bằng chứng mục A.10](#practice-mapping-test-result). Trợ lý giữ mapping và migration nguyên trạng. File thực tế:
**Người dùng điều chỉnh lựa chọn:** ngày 05/11/2026, chia theo lớp trước thay cách chia theo nghiệp vụ rồi theo trách nhiệm của lượt trước. [Quy tắc, cây thực tế và phần dự kiến tại TDD mục 3.1](TDD.md#tdd-packages). Trợ lý đã đọc file hiện tại rồi di chuyển Entity vào entity, Repository vào repository, hai lớp test vào repository và schema trực tiếp dưới package gốc vn.hueqr.backend; cập nhật package/import. Không còn package guestsession bao ngoài. Không thay mapping, logic/assertion/fixture của test hoặc migration V1, không tạo thư mục/lớp rỗng.
Tổng **7 test, 0 failures/errors/skipped, BUILD SUCCESS**, theo bằng chứng mục A.10. Trợ lý chỉ đọc báo cáo, không chạy lệnh. Đã đối chiếu T02 #50, chưa đủ nội dung ô mới để nhắc tích; kết quả không xác nhận Service/ticket/API hoặc toàn issue đã hoàn tất.
**Trạng thái:** viết ngày 05/11/2026, người dùng đã biên dịch/chạy riêng lớp test: **8 ca đạt, BUILD SUCCESS lúc 13:57:40+07:00**, Surefire TXT/XML khớp tại [mục A.11](#practice-ticket-time-result). Bộ 7 test ở mục A.10 là lần chạy riêng trước bước này. Nhiệm vụ tách quy tắc thời gian để kiểm mốc chính xác, trước khi triển khai payload/chữ ký, credential binding, khóa ký/epoch bền vững và API.
**Trạng thái:** người dùng đã biên dịch/chạy riêng lớp test, **9 ca đạt, BUILD SUCCESS lúc 14:07:51+07:00**, Surefire TXT/XML khớp tại [mục A.12](#practice-ticket-signer-result). Căn cứ [operations A.1](operations-design.md#ops-start-simple): payload do server định nghĩa, HmacSHA256 cố định, khóa runtime được bảo vệ/giữ qua restart; không lấy thuật toán từ client hoặc đổi Admin sang JWT.
**Trạng thái:** trợ lý đã trực tiếp chạy theo quyền người dùng cấp, **14 ca đạt, BUILD SUCCESS lúc 14:32:52+07:00**, bằng chứng [mục A.13](#practice-ticket-codec-result). Đã rà comment tiếng Việt, giữ logic/assertion. Mục tiêu nhỏ: ghép các field đã chuẩn bị ở server thành chuỗi vé rồi đọc lại sau kiểm toàn vẹn. [Định dạng v1 và quy tắc biểu diễn tại operations](operations-design.md#ops-start-ticket-format) là vị trí chính, không sửa yêu cầu DATA-05/K11/K01.
**Lệnh trợ lý đã chạy theo quyền được giao tại backend, giữ để tra cứu, không giao người dùng chạy lại:**
Mốc codec đã đạt và người dùng đã xem. Branch sau đó đã hoàn tất, phần cấp/xác minh kết hợp và API được triển khai tại [mục 3.2.8](#practice-start-api). Kết quả riêng codec không chứng minh toàn P01-TICKET, restart/restore thực hoặc toàn ô T03 #51.
**Lựa chọn kỹ thuật và cấu hình:** credential là 32 byte ngẫu nhiên do client đúng tab tạo, truyền Base64URL chuẩn không padding (43 ký tự); backend lưu SHA-256 của bytes, không credential thô. Test dùng fixture công khai, chưa chứng minh Web Crypto/owner/reload/duplicate trên browser. Cấu hình ứng dụng đọc `HUEQR_GUEST_SIGNING_KEY_BASE64` (Base64 chuẩn có padding, khóa tối thiểu 32 byte) và `HUEQR_SERVICE_EPOCH` (UUID chuẩn chữ thường). Không có giá trị fallback hoặc tự sinh mới mỗi boot; thiếu/sai thì không khởi động. Cấp/lưu khóa thật, giữ cấu hình qua restart và xoay epoch khi restore thật thuộc thao tác môi trường của người dùng, chưa thực hiện trong lượt này. Khóa/epoch phải ở cấu hình bảo vệ ngoài repo và ngoài gói backup; xem [operations](operations-design.md#ops-start-runtime).
**Cách kiểm hồi quy** tại thư mục backend, khi có thay đổi cần kiểm; trợ lý được chạy theo quyền thường xuyên, không yêu cầu người dùng lặp lại bộ đã đạt:
Bàn giao đọc đúng phiên bản ở docs/README, model/API/operations hiện hành; giữ code T01–T11/G02, không giả chia sẻ lịch sử giữa hai chat. Sau mốc Git hiện tại, chọn mục checklist khả thi theo phụ thuộc và tiếp tục theo handoff, không yêu cầu duyệt lại LIFE-P01 hoặc dùng việc có tài liệu để tích “Hoàn thiện TDD”/đóng issue.
````

<a id="official-20261107-context-docs-03-ky-thuat-operations-design-md"></a>

### docs/03-ky-thuat/operations-design.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/03-ky-thuat/operations-design.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
Ngày 05/11/2026, triển khai StartTicketCodec theo phần payload/encoding do server định nghĩa ở trên; không thay LIFE-P01 hoặc hợp đồng API. Bộ ký HMAC riêng có 9 unit test đạt; **14 ca codec đạt BUILD SUCCESS lúc 14:32:52+07:00**, [bằng chứng thực hành A.13](practice-01-start-session.md#practice-ticket-codec-result), người dùng đã xem. Sau branch thành công, lớp xác minh kết hợp và K11/K01/K02 đã kiểm trong [64 test lúc 16:15:34+07:00](practice-01-start-session.md#practice-start-api-result); không dùng các test này để kết luận cấu hình môi trường thật/restart/restore đã đạt. Điểm dừng tại [handoff](../01-tong-quan/handoff.md#next-step).
- Người dùng cung cấp/lưu cấu hình môi trường thật tại nơi bảo vệ ngoài repo, artifact và gói backup. Restart/cập nhật thường dùng lại khóa/epoch; restore thật luân chuyển epoch theo REC-P01. Không đặt lại key/epoch chỉ để chạy qua lỗi. Chưa cấp secret hoặc áp dụng cấu hình thật trong lượt này; hướng dẫn thao tác môi trường được giao riêng khi tới chặng đó.
| V-CD02 — TECH-05 | Khi V-CD01 đạt, kiểm từ workflow trong mạng riêng bằng tác vụ chỉ đọc trước; sau đó mới thử deploy có backup trên dữ liệu test | Runner đúng account/context, Docker Desktop hoạt động khi tài khoản/session thực tế; DB/audio vẫn giữ, lỗi có đường quay lại phù hợp schema. Chưa workflow/CI/CD được tạo/chạy ở lượt này |
Phép kiểm chưa thể kết luận từ bảng: MySQL thật UNIQUE/rollback/visibility, Browser owner theo tab, khóa runtime còn sau restart, epoch sau restore và clock/expiry. Thiết kế/AC được duyệt không chứng minh phần mềm đáp ứng. Lượt biên tập này không chạy lại phép thử; bằng chứng backend từ lần clean test 64 ca được dẫn ở thực hành, không đánh đạt toàn bộ ma trận.
TDD/ERD/API còn dự thảo, chưa đủ toàn checklist T01/T02/T07/T10. Lượt này không đọc/sửa GitHub, không nhắc tích theo snapshot cũ.
````

<a id="official-20261107-context-docs-03-ky-thuat-dfd-md"></a>

### docs/03-ky-thuat/dfd.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/03-ky-thuat/dfd.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Nguồn:** [Business Flow](../02-nghiep-vu/business-flow.md), [PRD](../02-nghiep-vu/PRD.md), [decisions](../02-nghiep-vu/decisions.md), [model](data-model.md), [API](api-transactions.md). Mermaid và SVG giữ nguyên nội dung ở lượt biên tập; giới hạn hiển thị ở mục 8.3.
````

<a id="official-20261107-context-docs-04-giao-dien-design-assets-readme-md"></a>

### docs/04-giao-dien/design-assets/README.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/04-giao-dien/design-assets/README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
**Làm rõ hồ sơ ngày 01/11/2026:** kiểm tra bản trích đang lưu thấy 655 đối tượng nhưng **không có OSM way/285990343 (Cần Chánh)**. Giữ liên kết này như **nguồn tham chiếu bên ngoài bản trích**, không gọi là đối tượng đã lưu trong snapshot. Lượt này không lấy thêm dữ liệu hoặc kiểm chứng lại nội dung liên kết ngoài; không gán thời điểm lấy mới hay mặc định đối tượng có cùng thời điểm với bản trích cũ. Metadata MAP-Z03 đã ghi rõ tình trạng này; giữ nguyên source_ids, mốc và hình học/ảnh đề xuất. Đây chỉ là sửa hồ sơ nguồn, không xác minh hoặc duyệt vị trí/ranh giới di tích.
| MAP-Z03 Tử Cấm Thành | [OSM node 2899189013](https://www.openstreetmap.org/node/2899189013), [Cần Chánh way 285990343](https://www.openstreetmap.org/way/285990343) **— tham chiếu bên ngoài, không có trong bản trích đang lưu; chưa lấy/kiểm chứng lại lượt này**, [Kiến Trung way 285990348](https://www.openstreetmap.org/way/285990348) | 107.5777550, 16.4694914 — điểm mang tên trên OSM | Đa giác tổng quan do người soạn thiết kế quanh khu trung tâm theo các mốc; không sao chép một đường ranh lịch sử/pháp lý đã xác minh. |
````

<a id="official-20261107-context-docs-99-luu-tru-readme-md"></a>

### docs/99-luu-tru/README.md — Các dòng trước biên tập (giữ thứ tự nguồn; có thể không liên tiếp)

Nguồn: `docs/99-luu-tru/README.md`, bản lưu trước biên tập ngày 07/11/2026.

````markdown
| [document-history.md](document-history.md) | Mở đầu/nhật ký dài, điều hướng cũ và các bước hướng dẫn đã qua | [Bản đồ tài liệu](../README.md#document-map), [điểm dừng/bàn giao](../01-tong-quan/handoff.md) |
**Cách đọc sau xác nhận 07/11/2026:** nguồn hiện hành giữ phân công/thiết kế ba người; Thành thao tác thực tế cho bản chấm theo [handoff 7.3](../01-tong-quan/handoff.md#execution-mode). Các nhật ký/hướng dẫn trong bản lưu giữ đúng thời điểm, kể cả cách chia việc hoặc yêu cầu chờ người commit cũ; không áp dụng chúng thay quy ước mới và không sửa lịch sử để gán công việc đã làm cho ba người.
````


<a id="account-sync-20261107"></a>

## Đồng bộ tài liệu hiện hành và tài khoản GitHub ngày 07/11/2026

Phần hiện hành sử dụng một repository và mapping issue T01–T11/#1–#11. Các dẫn chiếu chuyển repository và cột mapping trước đó được chuyển khỏi phần hiện hành; các dòng gốc dưới đây được giữ nguyên để truy vết, không áp dụng như hướng dẫn mới.

Tài khoản theo G02: Thành khaithanh2726-beep, Hòa gingpham, Nghĩa lechiNghiaa (tên Nghĩa đã được xác nhận lại). Quy trình phân biệt người nhập lệnh, metadata author/committer và tài khoản xác thực GitHub. Mốc T10 dùng gingpham, chưa có bằng chứng xác thực HTTPS, stage, commit hoặc push. Việc đồng bộ không tạo thông tin tác giả hoặc khẳng định các thành viên đã thực hiện phần việc. Giữ nguyên 11 issue/89 ô/18 dấu tích và các yêu cầu đã thống nhất.

Đây là hồ sơ nội bộ. Các khối nguyên văn dùng đường dẫn tương đối theo file nguồn được ghi ở từng mục. Không thực hiện Git/GitHub, code/test hoặc thao tác môi trường trong đợt đồng bộ.

### docs/README.md — các dòng trước đồng bộ

````markdown
| [01-tong-quan/roadmap.md](01-tong-quan/roadmap.md) | T01–T11, phân công G02, checkbox có căn cứ, phụ thuộc/lịch và snapshot issue | Lập kế hoạch/ghi tiến độ. 0.11; đã tạo và đối chiếu đủ 11 issue #1–#11 trên repo mới, khớp 89 ô/18 dấu tích; mapping tại mục 6, người dùng xác nhận Project đủ 11 issue và đã đặt Status: 6 In Progress, 5 Todo, 0 Done; chưa có lần kiểm tra trực tiếp mới. |
| [01-tong-quan/commit-plan-2026-11-05.md](01-tong-quan/commit-plan-2026-11-05.md) | Phân nhóm 31 file tài liệu hiện có/37 file backend, quy ước nhánh Txx và các bước người dùng commit | Dùng trước staging/commit; thư mục/đích repo mới và origin đã ghi tại handoff; issue mới #1–#11 đã đối chiếu; theo đầu ra người dùng, main mới chưa commit, mọi file dự án untracked; push chờ bằng chứng. |
- Thư mục hiện hành `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`, origin đã trỏ repo mới theo đầu ra người dùng. Checklist 11 task/89 ô/18 dấu tích đã duyệt; mapping issue mới #1–#11 đã đối chiếu, người dùng xác nhận Project đủ 11 issue và đã đặt Status theo hướng dẫn; chưa có lần kiểm tra trực tiếp mới. Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. Không dùng detached 55b0906 làm hiện trạng. [Handoff](01-tong-quan/handoff.md#next-step) và [kế hoạch commit](01-tong-quan/commit-plan-2026-11-05.md) ghi mapping issue và hướng dẫn bàn giao trước commit; không gom toàn backend/docs vào T03.
````

### docs/01-tong-quan/roadmap.md — các dòng trước đồng bộ

````markdown
| Cây làm việc và Git | Theo đầu ra người dùng ngày 06/11: đã sao lưu `.git` cũ ra ngoài, khởi tạo Git mới trên main chưa có commit; `.gitignore`, README.md, backend/ và docs/ đều untracked, origin fetch/push đúng repo mới. Chưa stage/commit/push. 55b0906 thuộc bản sao lịch sử cũ. |
**Lịch sử xuất bản:** số issue #49–#59 và các trạng thái board của repository cũ chỉ dùng truy vết trong [hồ sơ lịch sử](../99-luu-tru/decisions-history.md#tech-01-source); không là mapping hoặc trạng thái hiện hành.
**Đích hiện hành ngày 06/11:** repo [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue), [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1), thư mục `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`. Checklist tại mục 3 đã duyệt và đối chiếu đủ với issue mới #1–#11; mapping URL thật tại mục 6. Số #49–#59 chỉ thuộc repo cũ. Bản sao issue gốc đã được dọn sau đối chiếu theo quyền tại handoff 2.1.
| [T10](#t10) | Hòa | Hòa | Đang thực hiện | Đã có quy ước Txx/commit tiếng Việt; Git mới main chưa commit, origin đúng theo đầu ra người dùng. Chưa push repo mới, CI/CD hoặc demo đạt. |
Nguồn của tiêu đề, Description và Checklist là **mục 3**. Backlog 89 ô/18 dấu tích được duyệt ngày 06/11/2026; snapshot repository cũ chỉ dùng truy vết.
Giữ 9 dấu tích cũ và thêm 9 dấu tích có căn cứ; tổng số ô tăng 11 do tách rõ phần đã chọn khỏi phần còn phải kiểm. Không nhắc người dùng tích lại các ô đã tích. Nội dung duyệt local không tự chứng minh issue/Project mới đã được tạo hoặc cập nhật.
### 6.2. Mapping issue mới và trạng thái Project theo xác nhận người dùng
| Task | Số issue cũ — chỉ truy vết | URL/số issue mới | Status — người dùng xác nhận |
| [T01](#t01) | #49 | [#1](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/1) | In Progress |
| [T02](#t02) | #50 | [#2](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/2) | In Progress |
| [T03](#t03) | #51 | [#3](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/3) | In Progress |
| [T04](#t04) | #52 | [#4](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/4) | Todo |
| [T05](#t05) | #53 | [#5](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/5) | Todo |
| [T06](#t06) | #54 | [#6](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/6) | Todo |
| [T07](#t07) | #55 | [#7](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/7) | In Progress |
| [T08](#t08) | #56 | [#8](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/8) | In Progress |
| [T09](#t09) | #57 | [#9](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/9) | Todo |
| [T10](#t10) | #58 | [#10](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/10) | In Progress |
| [T11](#t11) | #59 | [#11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/11) | Todo |
Người dùng xác nhận chưa có task nào Done. Ánh xạ thành viên/GitHub đã được người dùng xác nhận tại G02: Thành khaithanh2726-beep, Hòa gingpham, Nghĩa lechiNghiaa; chưa có chỉ thị mới để tự gán assignee. Trước khi nhắc tích ở các lượt sau, đọc issue thật và đối chiếu đủ toàn ô; nếu chưa có URL thì báo thiếu mapping, không dùng số cũ trên repo mới.
### 6.3. Kết quả tạo issue và việc còn lại
3. Sau khi đủ điều kiện tại handoff 2.1, đã xóa bản sao issue gốc, gỡ dẫn chiếu và kiểm kê lại nhóm tài liệu còn 31 file; không stage/commit.
5. Bước tiếp theo là mốc 0/T10: kiểm danh tính và staged diff trước commit khởi tạo main chỉ .gitignore; sau đó đối chiếu commit/push/SHA. Các nhóm nền tiếp theo được bàn giao theo [commit-plan](commit-plan-2026-11-05.md). Chưa có bằng chứng stage/commit/push; không coi commit hoặc 64 test cũ là hoàn thành toàn issue.
````

### docs/01-tong-quan/handoff.md — các dòng trước đồng bộ

````markdown
| Git | Đầu ra gần nhất: main chưa có commit; .gitignore, README.md, backend/ và docs/ untracked; origin đúng repo hiện hành; remote chưa có nhánh | Chưa có bằng chứng stage/commit/push. Cần đối chiếu danh tính author/committer với người thực hiện trước commit |
[Bằng chứng kiểm chứng](../03-ky-thuat/practice-01-start-session.md#practice-results) lưu thời điểm, kết quả và giới hạn. Cấu hình tác giả Git không chứng minh ai đã thực hiện phần việc. Các commit trên repository cũ không chứng minh nội dung đã được push sang repository hiện hành.
- Bản sao issue gốc đã được dọn sau khi đối chiếu đủ nội dung; danh sách tài liệu hiện có gồm 31 file tính cả README gốc và tài sản.
1. Chuẩn bị commit khởi tạo main chỉ gồm .gitignore gốc, thuộc T10. Đối chiếu danh tính Git, danh sách/nội dung staged tại mốc A trước commit; kiểm commit/push/SHA tại mốc B.
Chưa có bằng chứng các commit trên đã được tạo. Không lặp lại init/đổi remote/xóa nhánh theo nhật ký cũ. Mục T03 bao gồm bắt đầu/kết thúc/kiểm phiên vẫn còn K04 và các phạm vi khác, chưa đủ điều kiện tích cả ô.
Backlog và mapping issue đã đối chiếu; trạng thái Project được báo cáo ngày 06/11. Điểm dừng hiện tại là kiểm staged diff trước commit khởi tạo main, sau đó bàn giao các nhóm nền theo task. Backend có 64 test đạt ngày 05/11; chưa hoàn tất toàn T02/T03, frontend hoặc triển khai. Người nhận đối chiếu phiên bản tài liệu, trạng thái Git và bằng chứng còn hiệu lực trước khi tiếp tục.
- Thành, Hòa và Nghĩa làm việc trên ba máy riêng theo G02. Mỗi người dùng danh tính của chính mình; xác định task, file và phụ thuộc trước khi stage. Không tạo hoặc thay thông tin tác giả để ghi người khác là người thực hiện.
- **Mốc A:** đối chiếu người commit, danh tính Git và danh sách/nội dung staged trước commit. Không stage toàn repository khi chưa kiểm phạm vi.
- **Mốc B:** sau commit/push, đối chiếu commit, SHA local/remote và trạng thái nhánh. Không yêu cầu gửi sau từng lệnh thành công; lỗi hoặc kết quả khác kỳ vọng thì dừng trước bước phụ thuộc.
````

### docs/01-tong-quan/commit-plan-2026-11-05.md — các dòng trước đồng bộ

````markdown
- Danh tính author/committer cần đối chiếu với người thực hiện trước stage. Username, cấu hình tác giả hoặc bảng phân công không chứng minh ai đã thực hiện công việc.
- Commit 55b0906 thuộc repository cũ. Không dùng commit hoặc trạng thái detached cũ để suy trạng thái hiện hành hoặc yêu cầu khởi tạo lại Git.
- Không stage toàn repository, đổi tác giả, reset/clean hoặc ghi đè sửa đổi để dựng lại lịch sử. Bản ghi kiểm tra phải phân biệt đã code/test, đã commit, đã push và đã merge.
Mỗi máy có clone/thư mục làm việc và danh tính Git của thành viên tương ứng. Các máy trao đổi commit qua origin; người nhận chỉ nhận được nội dung đã commit/push. File untracked không được truyền qua clone/fetch/pull. Những nhóm nguồn đang có trong bộ bàn giao ban đầu cần được phân đúng danh sách task trước khi đưa vào từng máy; chưa có bằng chứng các bước bàn giao đó đã thực hiện.
Người commit mốc 0 theo G02 là Hòa. Xác nhận đúng thư mục/repository, main chưa có commit, origin đúng và index không chứa phần ngoài phạm vi. Sử dụng đầu ra đã có; chỉ kiểm lại phần đã thay đổi hoặc chưa có bằng chứng. Danh tính Git phải thuộc chính người thực hiện, không suy email hoặc tác giả từ username.
Nếu không khớp người thực hiện hoặc trạng thái khác đầu vào, dừng trước stage và đối chiếu nguyên nhân. Không thay tác giả để ghi người khác thực hiện.
**Mốc A:** gửi danh tính người commit, danh sách và nội dung staged cùng kết quả check để đối chiếu trước commit. Nếu có file/nội dung ngoài phạm vi, giữ trạng thái và báo để xử lý; không tự reset/clean. Không yêu cầu gửi sau từng lệnh thành công.
Kỳ vọng commit đầu tiên trên main chỉ có .gitignore, đúng author/committer của người thực hiện và đúng thông điệp. Nếu khác, dừng trước push. Khi đúng, tiếp tục:
**Mốc B:** đối chiếu kết quả commit/show/push và ba lệnh kiểm cuối. SHA local phải bằng SHA refs/heads/main trên origin; main theo dõi origin/main; README/backend/docs vẫn untracked. Nếu lỗi quyền/xác thực hoặc remote có lịch sử ngoài dự kiến, dừng và báo lỗi; người vận hành tự xử lý đăng nhập, không cung cấp secret.
````

### docs/03-ky-thuat/practice-01-start-session.md — các dòng trước đồng bộ

````markdown
**Mục được chọn:** “Có API/ticket/Service/Repository đúng ba lớp và lookup/expiry hai nhánh.” Đây là ô thực hành bên dưới, làm nền cho [ô đầu T03 hiện hành](../01-tong-quan/roadmap.md#t03); không đồng nhất hai ô. T03 do Thành phụ trách và commit. Khi thực hiện mốc API, branch tên cũ đã có upstream; đó là mốc lịch sử. Thư mục hiện hành đã đổi tên, origin đã trỏ repo mới theo [handoff](../01-tong-quan/handoff.md#next-step); branch/HEAD/index/push cần đầu ra mới, không coi mốc API đã commit trên repo mới.
**Ghi chú chuyển repo 06/11/2026:** số/link issue và trạng thái Git trong phụ lục này thuộc thời điểm kiểm ở repo cũ. Tra task hiện hành tại [roadmap](../01-tong-quan/roadmap.md#t01), mapping mới tại [mục 6](../01-tong-quan/roadmap.md#github-checklist-review); không áp dụng lại các bước Git hoặc yêu cầu kiểm thử cũ chỉ từ phụ lục.
````

### docs/99-luu-tru/README.md — các dòng trước đồng bộ

````markdown

````

### docs/02-nghiep-vu/decisions.md — các dòng trước đồng bộ

````markdown
- Mỗi thành viên commit phần việc được giao bằng danh tính của chính mình. Trước commit xác định task, danh sách file, phụ thuộc và staged diff. Bảng phân công không xác nhận ai đã tạo commit hoặc hoàn thành phần việc.
Tài khoản GitHub không tự xác định tên/email tác giả Git hoặc chứng minh người thực hiện. Không tạo thông tin tác giả, tự gán assignee hoặc thay đổi issue/Project khi chưa được giao.
````
