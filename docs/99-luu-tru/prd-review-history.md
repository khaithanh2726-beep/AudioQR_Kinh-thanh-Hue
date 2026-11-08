# Lịch sử rà soát PRD

**Lưu trữ ngày:** 05/11/2026. **Mục đích:** Giữ diễn biến rà soát, đề xuất Q1–Q3 cũ và các đối chiếu theo từng thời điểm. Trạng thái phát hiện, kết luận và R06 hiện hành ở báo cáo chính.
**Nguồn hiện hành:** [mở tài liệu chính](../02-nghiep-vu/prd-review.md); kiểm phiên bản/trạng thái trước dùng. Bản lưu không là nguồn triển khai song song hoặc phê duyệt mới. [Mục lục lưu trữ](README.md).

## Mục lục phần lưu

- [Bảng tổng hợp Q1–Q3 chuyển bổ sung](#review-q-proposal-table) — giữ nguyên trạng thái lịch sử, không áp dụng hiện hành.

- [review-organization](#review-organization) — từ docs/02-nghiep-vu/prd-review.md
- [review-q-final](#review-q-final) — từ docs/02-nghiep-vu/prd-review.md
- [review-timeline](#review-timeline) — từ docs/02-nghiep-vu/prd-review.md

<a id="review-organization"></a>

## Phần lưu: review-organization

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="6-lịch-sử-tổ-chức-tài-liệu-ngày-30102026"></a>

## 6. Lịch sử tổ chức tài liệu ngày 30/10/2026

**Ghi nhận theo thời điểm 30/10, giữ để tra cứu.** Những câu “chỉ đổi hai dòng” hoặc “chưa áp dụng” trong phần này mô tả lượt tổ chức trước; không thay trạng thái đã xử lý ngày 31/10 ở mục 3.

- Business Flow có 8 mục theo thứ tự đọc nghiệp vụ; luồng khách và Admin đều trước lịch sử. Giữ K/E/AD/AE, toàn bộ các bảng thao tác/ngoại lệ và giới hạn hiện hành.
- Bảng quyết định/P lặp toàn văn được thay bằng liên kết sang vị trí chính decisions.md. Dữ liệu mục tiêu/luồng vận hành vẫn xuất hiện ở Business Flow để người đọc dùng được độc lập ở mức nghiệp vụ.
- decisions.md có 8 mục: quy ước → chỉ mục → G/BF → D/BS/P → A → mở → thay thế → nguồn/căn cứ/nhật ký. Mã gốc và các bổ sung giữ nguyên; neo cố định hỗ trợ dẫn chiếu.
- Toàn bộ căn cứ/nhật ký cũ Business Flow chuyển sang decisions.md mục 8, hai bảng thay thế cũ giữ ở mục 7. Không xóa lịch sử để giảm độ dài; số mục cũ trong bản ghi nguyên gốc có bảng quy đổi mục 8.4.
- Sửa trạng thái cũ ở phần hiện hành: “Luồng Admin cụ thể sẽ được phân tích riêng”; “log/bản sao lưu và luồng Admin còn cần phân tích riêng”; “cách gắn vùng/điều kiện hiện điểm mới ... chưa được duyệt”. Giờ dẫn tới A02.4/A02.5/A02.6 đã chốt và luồng Admin đã duyệt; đây là sửa mô tả lỗi thời, không duyệt thêm.
- Giữ ràng buộc cộng tác và nguồn/giới hạn lịch sử; không khảo sát lại hoặc gọi nội dung/phần mềm là đã kiểm chứng.
- PRD chỉ đổi hai dòng dẫn tài liệu nguồn; R01–R06 không được áp vào yêu cầu/UC/AC trong lượt này.

<a id="61-lịch-sử-bản-tóm-tắt-q1q3-trước-xác-nhận--không-áp-dụng"></a>

### 6.1. Lịch sử bản tóm tắt Q1–Q3 trước xác nhận — KHÔNG ÁP DỤNG

Giữ bản tóm tắt cũ ngày 31/10/2026 dưới đây để tra lịch sử. Các câu “chưa xác nhận/chờ duyệt/sau xác nhận” trong bản gốc đã được thay bằng xác nhận hiện hành tại decisions 5.8 và PRD 0.4; nguyên bản chi tiết nằm ở decisions 7.3.

> #

<a id="review-q-final"></a>

## Phần lưu: review-q-final

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="review-final"></a>

<a id="73-rà-soát-cuối-sau-xác-nhận-q1q3--31102026"></a>

### 7.3. Rà soát cuối sau xác nhận Q1–Q3 — 31/10/2026

**Phiên bản đọc trực tiếp:** PRD 0.4 cùng Business Flow, decisions và báo cáo hiện tại. **Phiên bản sau sửa ở lượt rà soát đó:** PRD **0.4.1 — khi đó vẫn dự thảo**. Phạm vi yêu cầu đã rõ nay được xác nhận riêng theo PRD-01/mục 7.5, không đổi kết quả rà soát. Không dựa vào số dòng cũ để kết luận; đọc nội dung hiện hành tại decisions 5.8 và các phần liên quan, không áp đề xuất lịch sử ở decisions 7.3.

Phạm vi lượt này là thay đổi Q1–Q3, BR-18/DR-09, các tình huống AC bị ảnh hưởng và truy vết/dẫn chiếu; không làm lại toàn bộ cuộc rà soát cũ, không khảo sát code/dịch vụ hoặc chạy kiểm thử phần mềm.

| Nội dung | Chuỗi nguồn → đặc tả đã đối chiếu | Kết quả |
| --- | --- | --- |
| Q1 | Q1/A02.5 → BR-11, DR-06, FR-A02, UC-A02, AC-A03/A04 và 8.2.2, NFR-05; Business Flow 4.5 | Tên 1–100 sau chuẩn hóa, mã ổn định/độc lập, trùng cảnh báo không chặn, lưu điểm ẩn, cổng bật hiện và nội dung tách riêng khớp nguồn. Chỉ sửa câu cuối BR-11 theo R07, không đổi phạm vi ngôn ngữ. |
| Q2 | Q2/A01/A03/A04.4 → BR-18, DR-09, FR-A01–A04, UC-A01–A03, AC-A02/A05/A06/A07 và 8.2.1, NFR-03/04; Business Flow 4.5 | Đủ ba loại công việc; khôi phục mốc tạm gần nhất/cùng tab-tài khoản, hết phiên/lưu lỗi/mạng/tải lại, vòng đời/xóa tạm và giới hạn đóng tab/crash/thiết bị khác. Không nhầm tạm/nháp/áp dụng, không tự tác vụ/hồi sinh điểm/ghi đè im lặng. |
| Q3 | Q3/A01/A04.4 → BR-14, FR-A01, UC-A01, AC-A02 và 8.2.1, NFR-03/04; Business Flow 4.5 | Danh sách tính/không tính và mốc 30 phút từ hoạt động được ghi nhận khi còn hiệu lực khớp nguồn; ví dụ 09:20→09:50 đúng. Không hồi sinh/ghi bù, TTS nền tiếp tục độc lập nhưng không gia hạn. |
| BR-18/DR-09 và tình huống | Đối chiếu riêng giữ tạm Admin với Q2, DR-05 của khách, DR-06 bản nghiệp vụ và các giới hạn kỹ thuật chưa chốt | Không trộn dữ liệu/hạn lưu khách, không tự tạo mô hình phiên Admin theo tab hoặc hứa xóa vật lý đúng lúc đóng không quan sát được. Không thấy yêu cầu mới ngoài Q2 trong phần này. |
| Truy vết/dẫn chiếu | FR↔UC, FR↔AC, UC↔AC và liên kết cục bộ của các phần liên quan | Giữ 23 FR, 14 UC, 50 AC, 18 BR, 9 DR, 6 NFR. Ký hiệu rút gọn được đọc theo ngữ cảnh; không ép nối chéo mọi mã cùng một Use Case. Không thấy thiếu đối ứng hoặc liên kết gãy sau sửa. |

**Kết luận:** Có đủ cơ sở đề nghị duyệt phạm vi yêu cầu đã rõ, kèm loại trừ tường minh ở mục 7.4/7.5. Ngoài lỗi câu chữ R07 đã sửa, không phát hiện sai lệch mới về Q1–Q3, BR-18/DR-09 hoặc các tình huống liên quan. Không có vấn đề Q1–Q3 cần quyết định lại; không tạo thêm vòng rà soát nếu không phát sinh thay đổi có ảnh hưởng.

<a id="review-timeline"></a>

## Phần lưu: review-timeline

**Chuyển từ:** docs/02-nghiep-vu/prd-review.md; chỉ dẫn lịch sử, xem nguồn hiện hành tại đầu file.

<a id="review-approval-scope"></a>

<a id="75-phạm-vi-đã-được-người-dùng-duyệt-loại-trừ-và-bước-tiếp-theo"></a>

### 7.5. Phạm vi đã được người dùng duyệt, loại trừ và bước tiếp theo

**Người dùng đã duyệt phạm vi đã mô tả rõ của đúng PRD 0.4.1 ngày 31/10/2026 theo [PRD-01](../02-nghiep-vu/decisions.md#prd-approval):** mục tiêu/đối tượng, phạm vi trong-ngoài và bộ chuẩn năm điểm × vi/en/fr/ru; BR/DR, chức năng/Use Case khách và Admin, NFR đã rõ và kết quả nghiệm thu nghiệp vụ có nguồn đã chốt tại PRD mục 1–8, gồm Q1–Q3 và các giới hạn đã xác nhận. Các ví dụ/trường hợp trong 8.2.1/8.2.2 cụ thể hóa nguồn, không chứng minh đã chạy đạt.

**Loại trừ khỏi việc duyệt ngầm:** mọi hành vi còn mở, bố cục/thao tác/ranh giới vùng cụ thể, nhà cung cấp/giọng/giá/giới hạn TTS, lựa chọn công nghệ/TDD, chính sách dữ liệu bổ sung cho nhóm chưa định nghĩa, môi trường/ngưỡng đánh giá chưa chốt và nội dung thuyết minh/bản dịch/audio chưa được duyệt riêng. Các loại trừ nằm đúng hàng mục 7.4, giữ người chịu trách nhiệm và mốc xử lý. Không giảm 20 văn bản/20 audio hoặc coi tất cả AC đã sẵn sàng kết luận trên môi trường chưa chọn.

**Bước tiếp theo theo nhiệm vụ hiện tại và phụ thuộc sau đó:**

1. Đọc [ui-design hiện hành](../04-giao-dien/ui-design.md), kiểm tra phiên bản/trạng thái: UI-01–UI-05 chốt đúng phần, công việc còn lại ở [ui-design mục 13](../04-giao-dien/ui-design.md#ui-next-technical). Hình ảnh cũ/ranh giới chưa được duyệt, không coi sơ đồ là ranh giới được xác minh.
2. Khảo sát TTS có bằng chứng về vi/en/fr/ru, giọng, chất lượng nghe thử, chi phí và giới hạn; người dùng chọn phương án, không tự hạ phạm vi khi gặp giới hạn.
3. Dựa trên yêu cầu được duyệt và kết quả thiết kế/khảo sát, chuẩn bị TDD cùng roadmap có phụ thuộc, mốc xử lý điểm mở và cách chứng minh tiêu chí. Người dùng thực hiện code/cấu hình/database/kiểm thử/Git/triển khai khi tới bước được giao; trợ lý chưa được tự thao tác.

PRD **0.4.1 được duyệt phạm vi yêu cầu đã mô tả rõ**; mọi mục còn mở/đề xuất/chưa kiểm chứng tiếp tục được loại trừ. Đã đọc lại bốn bản lưu trước ghi nhận, đúng phiên bản 0.4.1 và không thấy khác biệt ảnh hưởng xác nhận; giữ nguyên mã/nội dung yêu cầu. R06 không đóng toàn bộ theo xác nhận này. Duyệt tài liệu không xác nhận phần mềm đáp ứng, tính chính xác lịch sử, bản dịch hoặc audio đã được kiểm chứng. Năm bản vi vẫn chờ cung cấp/rà soát.

<a id="76-bổ-sung-ui-01-sau-duyệt-prd--31102026"></a>

### 7.6. Bổ sung UI-01 sau duyệt PRD — 31/10/2026

**Ghi nhận theo thời điểm 31/10:** các câu UI-P01/UI-P02/UI-P04 chưa duyệt dưới đây là lịch sử của lượt đó; nay đã được thay đúng phần bằng UI-02/mục 7.7, không áp lại trạng thái cũ.

Đọc trực tiếp ui-design **0.1**, PRD **0.4.1**, Business Flow và decisions trước ghi nhận; không thấy một bản ui-design mới được duyệt trong file. Người dùng cho biết chat đánh giá đã hoàn tác lần sửa trước; chỉ lấy bản lưu thực tế và tin nhắn xác nhận hiện tại làm căn cứ.

- PRD **0.5** kế thừa PRD-01, cụ thể hóa **UI-01** đúng phần: layout/box cuộn, nút ba trạng thái/UI-P03, giao diện và nội dung dùng ngôn ngữ chung, Bắt đầu-Kết thúc xử lý/chống lặp và phân biệt lỗi/chưa rõ kết quả.
- Vị trí ảnh hưởng: BR-05; FR-K01/K03/K05/K06/K09; UC-K01/K03/K05; AC-K01/K03/K05/K12/K14/K20; NFR-01/NFR-05 và điểm mở O-02. Giữ mã/quan hệ FR–UC–AC và các yêu cầu khác.
- UI-P03 đã thay đề xuất “Phát + Đã phát hết” bằng chính nút Nghe lại; UI-P01, tua thông thường của UI-P02 và UI-P04 chưa được duyệt. Bảng 7.4 chỉ cập nhật đúng phần đã khép lại; **R06 còn mở**, không tạo phát hiện giả mới.
- [ui-design 0.2](../04-giao-dien/ui-design.md) ghi phần đã chốt/phần đề xuất; ảnh/bản đồ có nguồn và vùng đề xuất, chưa phê duyệt nền/vị trí/ranh giới. Nội dung/bản dịch chưa được duyệt qua xác nhận hướng.
- Đã đối chiếu câu vi trong AC với UI-01.3: giữ nguyên văn làm căn cứ nghĩa, chuẩn bị bản dịch được rà soát trước dùng; không buộc mọi giao diện luôn hiện câu vi hoặc mặc định bản dịch đã kiểm chứng.
- Chỉ sửa tài liệu và tạo tài sản minh họa thiết kế; không thực hiện ứng dụng/kiểm thử phần mềm hoặc chứng minh đã đáp ứng.

<a id="review-ui-02"></a>

<a id="77-đối-chiếu-ui-02-và-ngoại-lệ-p01--01112026"></a>

### 7.7. Đối chiếu UI-02 và ngoại lệ P01 — 01/11/2026

Đọc trực tiếp docs/README, Business Flow/decisions/PRD **0.5**/báo cáo/UI **0.2** trước sửa. Người dùng chủ đích thay P01 phần hết hạn phải dừng audio; không phải sửa nguồn để hợp thức hóa code. Bản sau: PRD **0.6**, UI **0.3**.

| Phạm vi đã đối chiếu | Nguồn → vị trí hiện hành | Kết quả tài liệu |
| --- | --- | --- |
| Chủ động kết thúc | UI-P04/UI-02 → FR-K09, UC-K05, AC-K20; UI-K06 | Box toàn trang chỉ sau thành công, nền mờ-khóa/không Hủy-đóng-ngoài-Esc, nút về UI-K01 tên trống/vi; không xác nhận trước/không hứa chặn trình duyệt. |
| Hết hạn/ngoại lệ bài đang phát | P01/D04/UI-02 → BR-02/06/13, FR-K05/K07/K09, UC-K03/K04/K05, AC-K15/K16/K18 và 8.1.1 | Box chỉ trong bản đồ, đọc vẫn cuộn; chỉ pause/resume đúng bài đang phát tại mốc tới cuối, không tua/Nghe lại/bài mới/khôi phục ngoại lệ sau reload. Rời nghe mở form dừng bài cũ. |
| Phiên/lượt/dữ liệu | D04/D08/UI-02 → BR-02/07, DR-04, AC-K18/K21/K22, AC-D01 | Hết hạn bắt đầu +8 tiếng, không phiên/lượt mới hoặc đổi mốc 30 ngày thành lúc hết bài. Không thêm lịch sử thao tác audio. |
| Ứng viên/tua còn hiệu lực | UI-P01/UI-P02 → FR-K03/K05, UC-K02/K03, AC-K09/K12/K13 | Box đúng danh sách/Hủy/nền khóa/giữ phát-pause; Hủy không đổi/lượt, chọn cùng điểm P06. Hết hạn đóng danh sách. Tua còn hiệu lực đúng hai nhánh và nhánh sau hết bài. |
| File/phục hồi/giới hạn | A02.3/A07/P01/UI-02 → BR-13/16, NFR, PRD 9.5 | Giữ chính sách file 8 tiếng từ ngừng áp dụng cuối cùng; TDD phải xử lý quyền đọc/tải đệm/đúng bài/dọn file, không chọn giải pháp hoặc hứa mạng/browser đáp ứng. Nếu cần đổi chính sách trình riêng. |
| Trạng thái/ảnh/lịch sử | decisions 7.4, UI 3.4/6/9, mục lục docs | UI-P01/UI-P02/UI-P04 chốt đúng phạm vi, R06 còn phụ thuộc khác. Ảnh 0.2 chưa đủ box mới, bổ sung sơ đồ văn bản; ui-design-history và tài sản bản đồ giữ nguyên. |

Giữ 23 FR, 14 UC và 50 AC, bổ sung ca dưới mã AC hiện có. Thêm nguồn UI-02 và liên kết trực tiếp FR-K05↔AC-K09/AC-K18, FR-K05↔UC-K05, UC-K03↔AC-K18 để phản ánh ngoại lệ; không ép nối chéo mọi mã. Đã sửa/rà tài liệu, **không có kết quả kiểm thử phần mềm**. R01–R05/R07 và S01/S02 không bị mở lại; R06 không đóng toàn bộ theo xác nhận này.


<a id="review-ui-04"></a>

<a id="78-đối-chiếu-ui-03ui-04-áp-dụng-cặp-và-lượt-3--02112026"></a>

### 7.8. Đối chiếu UI-03/UI-04, áp dụng cặp và lượt 3 — 02/11/2026

**Ghi nhận ở thời điểm UI-04:** trạng thái lượt 3 còn đề xuất dưới đây đã được UI-05 thay đúng phần; xem hiện hành mục 7.9. Giữ nội dung đối chiếu theo ngày, không áp lại trạng thái cũ.

Đã đọc trực tiếp docs/README, Business Flow/decisions/PRD **0.6**/báo cáo/UI **0.5** trước sửa. Bản sau: **PRD 0.7, UI 0.6**. UI-04 là thay đổi nghiệp vụ có chủ đích của người dùng, không kết luận PRD 0.6 sai khi mô tả quyết định cũ. Giữ dấu vết áp dụng riêng/gate cũ ở decisions 7.6, giữ mã R/S và nhật ký.

| Phạm vi | Nguồn → vị trí hiện hành | Kết quả đối chiếu tài liệu |
| --- | --- | --- |
| Một thao tác áp dụng cặp | UI-04/UI-AP06 → A03/A04.3, BR-09/13, DR-06, FR-A07, UC-A05, AC-A14/A15 và 8.2.3, NFR-02; UI-A18 | Đúng một điểm/ngôn ngữ, đủ văn bản/audio duyệt/khớp; tóm tắt cặp cũ/mới, một nút, không riêng/tự áp dụng. Lỗi xác định giữ cũ/chưa rõ kiểm tra thực tế. Chỉ đổi audio không bắt duyệt lại văn bản không đổi. |
| Điều kiện hiện | UI-04 → A02.5/Q1, BR-11, FR-A02, UC-A02, AC-A04/8.2.2; UI-A05 | Cần thông tin hợp lệ, vùng hợp lệ xem xét/áp dụng, cặp vi duyệt/áp dụng. Lưu điểm ẩn vẫn được, không chờ bốn ngôn ngữ/tự bật khi áp dụng, không đổi bộ 20+20. |
| Hồ sơ cặp/giữ tạm | UI-AP04/UI-AP05 → BR-09/18, DR-06/09, FR-A04/A07, UC-A03/A05, AC-A02/A07/A14; UI-A14/A15 | Tham chiếu riêng đúng bản, chung mục nếu đủ rõ, không nhập lại ngày/người đã có; ghi không tự duyệt/áp dụng, không âm thầm lưu. Phần đang nhập trong công việc được bảo vệ đúng phạm vi, không mọi biểu mẫu/kho dài hạn. |
| Hết phiên nghe thử Admin | UI-AP07 → A01, BR-14, FR-A01, UC-A01/A04, AC-A13, NFR-03; UI-A10/A17 | Dừng audio thử/che nội dung, xác thực lại không tự phát/gửi lại; giữ soạn, TTS đã nhận tiếp tục. Không ngoại lệ nghe tiếp khách. |
| Thiếu/lỗi audio khách | D03/BS/P04 và UI-04 → FR-K04/K05, AC-K11/K16/K18, ca AC-A15, Business Flow 4.3/5, UI-A18 | Không dùng D03 để phát hành nửa cặp. Cặp đã áp dụng nhưng audio thực tế thiếu/lỗi vẫn đọc/hiện đúng thông báo và thử lại đúng quyền; không audio cũ hoặc mở quyền mới sau hết hạn. |
| Truy vết/phiên bản/lịch sử | Bảng FR/UC/AC, decisions 5.10/7.6, PRD đầu file/9/10/11, UI 11/12, docs/README | Giữ 23 FR/14 UC/50 AC/6 NFR, thêm ca trong mã hiện có. Bổ sung FR-A01↔UC-A04/AC-A13, UC-A01↔AC-A13 và FR-A07/UC-A05↔AC-A02. Dẫn UI hiện hành thay số cũ 0.3 ở phần đầu/bàn giao. |
| Lượt 3/R06 | UI-A20–UI-A23/UI-AP08–UI-AP10; PRD O-01–O-06 | Lượt 3 toàn bộ dự thảo, không thêm dashboard/lọc/xuất báo cáo/sửa phiên/quét QR. R06 **vẫn mở một phần**; UI-AP04–UI-AP07 không còn là câu hỏi phải duyệt lại. |

Đã tìm lại các câu hiện hành cho áp dụng riêng hoặc gate chỉ văn bản vi trong Business Flow/decisions/PRD/UI; chuyển đúng phần bị thay, không sửa nhật ký/nguồn lịch sử thành quyết định mới. TTS/giọng/chi phí/khả thi, giới hạn, môi trường, kỹ thuật nhất quán cặp/giữ tạm/xóa/dọn, file chưa từng áp dụng và log/bản sao lưu còn phải xử lý theo mục 7.4/PRD 9. Thiếu TTS/audio khớp hiện chặn áp dụng cặp mới, không tự hạ điều kiện để trình diễn.

**Chưa có bằng chứng phần mềm đáp ứng hoặc nội dung được duyệt/kiểm chứng.** R01–R05/R07/S01/S02 giữ trạng thái xử lý tài liệu, R06 không đóng theo gói. Sơ đồ lượt 3 chưa được duyệt, QR chỉ khung giữ chỗ khi chưa có URL thật; không sinh QR từ địa chỉ giả.


<a id="review-ui-05"></a>

<a id="79-ui-05-và-rà-soát-nhất-quán-ui-trước-tdd--02112026"></a>

### 7.9. UI-05 và rà soát nhất quán UI trước TDD — 02/11/2026

Đọc trực tiếp docs/README, Business Flow/decisions/PRD **0.7**/báo cáo/UI **0.6** trước sửa. Bản sau: PRD **0.8**, UI **0.7**. Chỉ đồng bộ UI-AP08–UI-AP10 và các trạng thái/dẫn chiếu bị ảnh hưởng, không biên tập lại hàng loạt hoặc sửa lịch sử thành hiện hành.

**Thay đổi đã xác nhận:** UI-A21 từ trang riêng sang box chỉ đọc trên danh sách (nền khóa/Đóng/cuộn/giữ lọc/điện thoại); hai bảng/tổng riêng, ngày đầu/cuối trọn ngày Asia/Saigon và Về hôm nay; QR chung/URL chỉ đọc/PNG/bản in gọn/chưa URL không QR giả. Nguồn [UI-05](../02-nghiep-vu/decisions.md#ui-admin-confirmed-03), lịch sử cũ [7.7](decisions-history.md#ui-05-history). Truy vết: BR-08/17 → FR-A10/A11 → UC-A07/A08 → AC-A22–A24, ca [PRD 8.2.4](../02-nghiep-vu/PRD.md#ac-admin-session-qr); giữ 23 FR/14 UC/50 AC/6 NFR, không đổi mã hoặc tạo chức năng chỉ để nối bảng.

| Nhóm rà soát | Vị trí đã đối chiếu | Kết quả hiện hành ở mức tài liệu |
| --- | --- | --- |
| Box kết thúc khách và box hết hạn | UI 3.4/5; UI-P04/P01/UI-02; AC-K18/K20 | Khách chủ động kết thúc: box toàn trang chỉ sau thành công; hết hạn: box trong khung bản đồ, văn bản vẫn đọc. Không đổi hai phạm vi chặn. |
| Nghe tiếp khách khác Admin | UI 4.1/10.10/11.7; UI-04/UI-AP07; AC-A13 | Khách chỉ bài đủ điều kiện theo P01/UI-02; Admin hết phiên dừng nghe thử/che nội dung, xác thực không tự phát/gửi lại. Làm rõ câu trạng thái cũ cuối UI-A10 bằng dẫn chiếu UI-04, không tạo hành vi mới. |
| Lưu vùng khác Áp dụng | UI 10.8/10.9/10.12; UI-03/A02.6 | Giữ vùng hiện hành khi sửa/lưu; cùng hình đã xem không bắt xem lại chỉ vì lưu; sửa tiếp/bản khác thì lưu/xem lại đúng bản. |
| Chỉ áp dụng cặp | UI 11.8/11.11; UI-04; FR-A07/UC-A05/AC-A15 | Một nút cả cặp đúng điểm/ngôn ngữ; không hành động riêng hoặc tự áp dụng. Lỗi giữ cũ/chưa rõ kiểm tra thực tế; D03 runtime vẫn giữ. |
| Gate bật hiện | UI 10.6/11.8; A02.5/UI-04; AC-A04 | Cần thông tin/vùng hợp lệ đã xem xét-áp dụng và ít nhất cặp vi duyệt/áp dụng; lưu ẩn vẫn được, không đợi đủ bốn ngôn ngữ hoặc tự bật sau áp dụng. |
| Hồ sơ không tự thẩm định/áp dụng | UI 11.2/11.5; D09/UI-AP05; AC-A14 | Tham chiếu riêng/đúng bản, chung mục nếu đủ rõ; ghi không tự duyệt/áp dụng, không biến nháp/nội dung chưa rà soát thành được phép. |
| Phiên/thống kê/QR đúng phạm vi | UI 12.1–12.7; UI-05/A05/A06; AC-A22–A24 | UI-A21 hiện hành là box ở bảng màn hình/mô tả/sơ đồ; hai tổng không cộng, chỉ dữ liệu còn giữ/lịch sử điểm xóa riêng. Không sửa/xóa/gia hạn phiên, thêm lọc/xuất báo cáo/dashboard/quét/QR riêng/chuyển hướng. |

Sau cập nhật, chưa phát hiện mâu thuẫn thực chất giữa các hướng UI hiện hành và nguồn đã chốt trong bảy nhóm trên. Các mô tả đề xuất trang riêng UI-A21/trạng thái chưa duyệt còn trong lịch sử chỉ để truy vết. UI-05 không duyệt mọi đồ họa/câu chữ, nền/ranh giới, nội dung/bản dịch/audio, TTS/công nghệ hoặc kết quả phần mềm.

**R06 vẫn mở một phần** theo 7.4/PRD 9. [UI mục 13](../04-giao-dien/ui-design.md#ui-next-technical) tổng hợp hướng đã chốt, chi tiết còn hoàn thiện và phần trước/trong TDD: TTS/khả thi/chi phí/giới hạn, công nghệ/môi trường, nền/vùng, nội dung/hồ sơ, file chưa từng áp dụng/log/bản sao lưu và cơ chế quyền/file/nghe tiếp/cặp/giữ tạm. Không lấy đồ họa nhỏ hoặc đủ toàn bộ bài làm điều kiện chặn mọi công việc TDD; chính sách nghiệp vụ thiếu phải chốt trước thiết kế cuối phần tương ứng.

Kiểm tra bảng/liên kết/anchor/trạng thái/truy vết là **kiểm tra tài liệu**, không phần mềm đã đáp ứng. Chưa xác minh URL/quét/in/tải thật, dữ liệu thật, dịch vụ TTS hoặc chất lượng lịch sử/dịch/audio.


<a id="review-tech-01"></a>

<a id="710-tech-01--trạng-thái-lựa-chọn-và-đầu-vào-kỹ-thuật-ngày-02112026"></a>

### 7.10. TECH-01 — trạng thái lựa chọn và đầu vào kỹ thuật ngày 02/11/2026

Đã đọc bản hiện hành docs/README, roadmap 0.2, Business Flow/decisions, PRD 0.8/UI 0.7 và báo cáo trước cập nhật. Người dùng xác nhận bộ công nghệ/kiến trúc riêng cho repo chính; bản sau PRD 0.8.1/UI 0.7.1/roadmap 0.3. Đây là đồng bộ trạng thái và nguồn, không mở lại nghiệp vụ hoặc thực hiện khảo sát dịch vụ.

| Nhóm | Kết quả ghi nhận / giới hạn |
| --- | --- |
| Công nghệ nền | Đã chốt theo [TECH-01](../02-nghiep-vu/decisions.md#tech-01). Không phải đã chọn phiên bản cụ thể toàn bộ thư viện, kiểm chứng tương thích, cài đặt, triển khai hoặc kiểm thử. |
| TTS/FFmpeg | Ưu tiên khảo sát Gemini TTS, chưa chọn nhà cung cấp/model/giọng/ngân sách; vi/en/fr/ru/cách đọc tên di tích và giới hạn/chi phí/lỗi chưa kiểm chứng. FFmpeg/ffprobe chưa quyết định. |
| Đầu vào bước A | Đã có thông tin Windows/VS Code/PowerShell; người dùng chưa quen Vue/Vite/npm/Leaflet/Geoman. Năng lực khác/của nhóm, ràng buộc/hạn chấm và demo/hosting/ngân sách còn thiếu. Chưa khảo sát B hoặc trình chính sách C. |
| Truy vết và nghiệp vụ | Giữ mã/phạm vi FR/UC/AC, phiên, cặp/gate, Q2/Q3, 20+20, dữ liệu/file và thống kê/QR. Không sửa yêu cầu để hợp thức hóa công nghệ. |
| Tiến độ | Backlog 0.2 đã duyệt ở chat trước và 11 issue đã tạo; hồ sơ local nay đồng bộ theo bằng chứng cũ, không truy cập GitHub lượt này. Diễn giải checklist T01/T07 mới là dự thảo local chưa đồng bộ issue. T01/T07 chưa hoàn thành. |
| R06 | **Còn mở một phần:** phiên bản/tương thích, TTS/khả thi/chi phí, môi trường/ngưỡng, nền/vùng, nội dung/hồ sơ, file chưa áp dụng/log/backup và cơ chế thực hiện. Chọn công nghệ không giải quyết các phần này. |

Giữ R01–R05/R07/S01/S02 đã xử lý tài liệu, không mở phát hiện mới chỉ để đủ số lượng. Kiểm tra tài liệu không là bằng chứng phần mềm đáp ứng. Không khảo sát TTS, chốt chính sách, tạo TDD/cấu hình/code, chạy kiểm thử hoặc thao tác Git/GitHub/triển khai trong lượt này.

<a id="review-tech-02"></a>

<a id="711-tech-02tts-01--khảo-sát-đầu-vào-ngày-03112026"></a>

### 7.11. TECH-02/TTS-01 — khảo sát đầu vào ngày 03/11/2026

| Nội dung | Trạng thái bằng chứng |
| --- | --- |
| Xác nhận | TECH-02: hướng dẫn từng bước/ôn tập, chủ đề 2 ba lớp và CI/CD, laptop/thiết bị/4G/miễn phí/mốc mục tiêu. TTS-01: gemini-3.8-flash-tts/Charon, người dùng nghe sáu MP3 hài lòng. Không hỏi lại các lựa chọn này. |
| Khảo sát đã làm | [technical-inputs 0.1](../01-tong-quan/technical-inputs.md) có file/hash demo, phân biệt source/thiết kế cũ/hồ sơ/chưa kiểm; nguồn chính thức API/quota/giá/định dạng/bộ phiên bản/demo và CI-CD. Đã điều chỉnh theo bổ sung không kế thừa tunnel/log/backup/lịch demo. |
| Chưa kiểm chứng | Không gọi API hoặc nghe lại/copy audio, không kiểm môi trường/build/test/hosting/CD; chưa xác minh quota tài khoản/20 cặp/Windows pipeline. Demo converter chặn ngoài Linux và script deploy có chặn chủ động; hồ sơ sáu MP3 không khắc phục thiếu bằng chứng đó. |
| Đề xuất mới | IN-P01–IN-P05/DATA-P01–P04 và lịch 17/11 là đề xuất chờ người dùng chọn; ngân sách trả phí chưa có, giờ/ngày chưa biết. Đặc biệt backup 7 ngày có ảnh hưởng bản sao quá hạn D08 phải xác nhận riêng, không coi là cấu hình thuần kỹ thuật. |
| PRD/truy vết | PRD 0.8.2 giữ mã/nghiệp vụ/FR–UC–AC/NFR, cập nhật O-01 và 9.6; UI 0.7.2 chỉ đồng bộ phụ thuộc/thiết bị. Không thêm ngưỡng/version/hosting đề xuất thành AC bắt buộc. |
| R06 | **Còn mở một phần.** Đã đóng đúng phần thiếu lựa chọn model/giọng/đầu vào đã xác nhận; còn tương thích/ma trận cụ thể, TTS thực, chính sách, nền/vùng, nội dung/hồ sơ/ngưỡng và thiết kế cơ chế. Không đóng R06 hoặc task T01/T07 theo gói. |

R01–R05/R07/S01/S02 giữ đã xử lý tài liệu; không mở lỗi mới hoặc sửa nguồn để hợp thức hóa thực thi. Mục 7.10 giữ trạng thái ngày 02/11 để truy vết, không là nguồn TTS hiện hành. Kiểm tra liên kết/bảng/anchor không là phần mềm đã đáp ứng.

<a id="review-tech-03"></a>

<a id="712-in-p01in-p05-tdd-kiến-trúc-và-checklist--03112026"></a>

### 7.12. IN-P01–IN-P05, TDD kiến trúc và checklist — 03/11/2026

| Nội dung | Kết quả / giới hạn |
| --- | --- |
| Trạng thái nguồn | TECH-03 chọn mốc phiên bản 0.1, nền tảng kiểm thực (nhóm đủ Android/iPhone), WAV-MP3/Linux và **thử ngrok**; DATA-01 chốt đúng phần retention/điều kiện/restore, không nơi/RPO-RTO. G03 quy định nhắc checklist thật. |
| Tài liệu đồng bộ | technical-inputs 0.2 giữ dấu vết H1/Funnel/matrix minh họa và DATA-P cũ; PRD 0.8.3 cập nhật DR/NFR/O và 8.3.1; BF/UI 0.7.3 giữ luồng/hướng UI. TDD 0.1 mới tổng thể/module/ba lớp/API-quyền/job-file/runtime/CD, chưa dữ liệu/API/test chi tiết. |
| Ngrok | Nguồn chính thức 03/11: dev domain tài khoản không đổi chỉ vì restart, Free 1 GB outbound/20.000 HTTP request mỗi tháng, có trang trung gian. Chưa tài khoản/URL/usage thực/QR/khác mạng hoặc triển khai. Ngrok web không là đường CD. |
| Khác GitHub/local | Đọc đúng 11 issue #49–#59 chỉ đọc; T01/T06/T07/T10 khác checklist local. Roadmap 6 chuẩn bị nội dung người dùng cập nhật và chỉ nhắc T07 mục khảo sát tài liệu đủ căn cứ; không tích TDD/roadmap/CD/nghe thử đủ bộ. |
| R06 | **Còn mở một phần:** tương thích/build thực, quota/chất lượng TTS/giới hạn, nền-vùng/nội dung-hồ sơ, Q2/Q3/tab/nghe tiếp-file/lịch dọn, path/quyền/công cụ/checkpoint backup-log/RPO-RTO và kiểm SSH/Docker/CD đã chọn. Không thiếu xác nhận các hạn/định dạng/nền tảng đã chốt. |

Không mở lại R01–R05/R07/S01/S02 hoặc tạo lỗi giả; không coi review/nguyên văn checklist là phần mềm đã đáp ứng. TDD kiến trúc và CD-L01 mới là đề xuất, chưa được duyệt qua TECH-03. Lịch có giả định 6 giờ/ngày không là cam kết của người dùng.

<a id="review-data-02"></a>

<a id="713-tech-04data-02-và-tdd-dữ-liệu--03112026"></a>

### 7.13. TECH-04/DATA-02 và TDD dữ liệu — 03/11/2026

| Nội dung | Kết quả / giới hạn |
| --- | --- |
| Xác nhận | Chỉ hướng Vue build cùng Spring Boot/một cổng/ngrok/ba lớp, tab độc lập, full MP3 trước phát/đúng nhánh expiry; backup cục bộ không phục hồi hỏng/mất laptop. Không duyệt mọi TDD hoặc CD-L01. |
| Mô hình mới | TDD 0.2 có ERD/place-region/slot-text-audio-pair/job/file/session-intent-view, khóa/quan hệ/pointer và giao dịch xóa giữ snapshot lịch sử. Chưa mapping MySQL/DDL/migrations hoặc kiểm FK thật. |
| Tab/audio | TDD 13–14 đề xuất credential-hash/sessionStorage/owner lock, full Blob/generation/revoke/latch; đã ghi copy storage/tab freeze/capability và autoplay chưa kiểm. Không tuyên bố Web Locks hoặc cache tự chứng minh độc lập/quyền. |
| Backup/CD | Backup cục bộ/7 ngày/restore lọc mốc gốc đã chốt; nhịp/phạm vi/công cụ là đề xuất TDD 15. CD-L01 giải thích SSH Windows/WSL/Docker/lỗi, chưa chọn và không chặn dữ liệu/API. |
| PRD/UI | PRD 0.8.4 giữ 23 FR/14 UC/50 AC/6 NFR, đồng bộ TECH-04/DATA-02 đúng phần; UI 0.7.4 chỉ phản hồi audio/expiry/phụ thuộc, giữ sơ đồ/tài sản/ranh giới. |
| G03 | Đọc lại #49/#50/#55/#58: T07 khảo sát đã [x]. T02 thiết kế bảng và T01 TDD mới có dự thảo/một phần nên không nhắc tích toàn mục; không sửa GitHub. |
| R06 | Còn mapping/khóa/giao dịch, full API/state/idempotency/ACK, owner/reload/freeze, browser thực/hiệu năng, job unknown/cleanup, backup công cụ/phạm vi/nhịp/quyền, CD và nội dung/nền-vùng. Hướng đã xác nhận không là câu hỏi cần duyệt lại. |

Kết quả trên là rà soát tài liệu/nguồn chính thức theo nội dung, không kết quả database/browser/code/test/CD. Không tự thêm chức năng/kho offline/đồng bộ tab hoặc sửa mốc file/phiên.

<a id="review-api-01"></a>

<a id="714-tech-05data-03-và-apigiao-dịch--03112026"></a>

### 7.14. TECH-05/DATA-03 và API/giao dịch — 03/11/2026

| Nhóm đối chiếu | Kết quả và giới hạn |
| --- | --- |
| Xác nhận nguồn | Đã đối chiếu bản thực tế TDD 0.2 và xác nhận người dùng; [TECH-05](../02-nghiep-vu/decisions.md#tech-05) chốt Tailscale riêng/OpenSSH Windows/PowerShell, ngrok vẫn phục vụ web. [DATA-03](../02-nghiep-vu/decisions.md#data-03) chốt DB + WAV/MP3 còn tham chiếu + manifest, cuối ngày có thay đổi/trước deploy-schema, cục bộ/tách dữ liệu phục vụ/7 ngày/restore lọc mốc gốc. Không duyệt toàn TDD hoặc kết quả chạy. |
| Câu ví dụ backup | TDD 15 dùng B mới hai ngày còn trong hạn bảy ngày nhưng S đã kết thúc 31 ngày; loại S/lượt trước phục vụ. Không dùng “một tuần sau” để ngầm giữ B quá hạn. Đây là ví dụ giả định, không dữ liệu/backup thực. |
| PRD/UI/Business Flow | PRD 0.8.5 cập nhật DR-08/NFR-03/O-05/9.2 và nguồn; giữ 23 FR/14 UC/50 AC/6 NFR, không thêm chức năng. UI 0.7.5 chỉ đồng bộ phụ thuộc, giữ sơ đồ/hướng/tài sản. Business Flow giữ luồng/ngoại lệ đã duyệt. |
| Thiết kế mới | [TDD 0.3](../03-ky-thuat/TDD.md) dẫn [API/giao dịch 0.1](../03-ky-thuat/api-transactions.md): quyền khách/Admin, precondition, khóa/phiên bản, thao tác lặp/unknown-result, TX áp dụng cặp/xóa/ghi lượt, job/file/backup tách ranh giới commit. Mã API/TX chỉ là mã thiết kế truy vết, không đổi mã yêu cầu. |
| Cụ thể hóa mô hình 0.2 | Opening giữ ngữ cảnh P06, quay lại bản đồ thực sự kết thúc lần mở theo D04 (khác kéo/phóng/chạm ngoài); grant ghim đúng bản khi tải/ACK muộn và giữ lịch sử xóa; receipt trả cùng kết quả cho cùng lệnh. Phần bổ sung còn dự thảo, phải ánh xạ schema/khóa/FK/vòng đời ở bước sau, không coi SQL/API đã hiện thực hoặc tự thêm lịch sử nghe lâu dài. |
| Kiểm tra tài liệu | Đối chiếu quyền khi hết hạn, ngoại lệ Blob chỉ bài đang phát, không tải thêm sau hạn; không cấp quyền Admin qua credential khách; áp dụng cả cặp/gate vi, vùng lưu khác áp dụng, giữ lịch sử xóa/file tách TX. Kiểm tra dẫn chiếu/mã/bảng khi lưu; không là kiểm thử phần mềm. |
| R06 | **Còn mở một phần:** tương thích/môi trường thật, bản đồ/nội dung/hồ sơ/quota TTS; mapping vật lý/DTO, lease/claim/receipt, Q2/Q3/owner/clock/browser, công cụ/checkpoint backup/log/độ trễ dọn, kiểm SSH/quyền Docker/CD/restore. Không đóng theo gói hoặc hỏi lại phần đã chọn. |
| G03 | Đã đọc checklist GitHub hiện hành liên quan T01/T02/T07/T10 chỉ đọc; T07 khảo sát đã tích. API/ERD là dự thảo, chưa đủ tích toàn mục TDD/mô hình-migrations/CD/restore. Không sửa issue/Project hoặc quy kết người phụ trách đã thực hiện. |

<a id="review-tdd-04"></a>

<a id="715-hoàn-thiện-mô-hìnhdtovòng-đờibackup--03112026"></a>

### 7.15. Hoàn thiện mô hình/DTO/vòng đời/backup — 03/11/2026

**Kết quả theo thời điểm 03/11, giữ để truy vết:** trạng thái LIFE/REC/chọn online tại bảng này đã được thay đúng phần bởi DATA-04/mục 7.16 ngày 04/11; không dùng làm hiện hành hoặc coi link tới file hiện hành là bản snapshot cũ.

| Nội dung | Kết quả và giới hạn |
| --- | --- |
| Mô hình hiện hành | [data-model 0.1](../03-ky-thuat/data-model.md) hợp nhất ERD TDD 0.3/API 0.1, có opening/grant/receipt và context/window/claim. TDD 11–12 dẫn một nơi; mô hình cũ chuyển nguyên nội dung sang lịch sử, không ghép hai schema. |
| Hợp đồng | [API 0.2 mục 10](../03-ky-thuat/api-transactions.md#api-dto) có field gửi/nhận và ví dụ thành công/lỗi/mất phản hồi cho start/resume/select-language-ACK/TTS/Apply-pair/delete. K11/A31 chỉ là cấp context/window kỹ thuật, không FR/màn hình hay đăng ký vai trò mới. |
| Vòng đời | [Operations 1–2](../03-ky-thuat/operations-design.md#ops-lifecycle) phân biệt vô hiệu quyền với dọn bytes/metadata; window cũ không thực hiện lại sau dọn receipt. LIFE-P01–P03 là thời hạn mới chờ xác nhận, không AC bắt buộc. Unknown/worker/nháp còn tham chiếu không bị TTL xóa. |
| Backup/restore | BKP-P01 online snapshot + gate cleaner + dựng catalog từ chính dump ở DB tạm; cần đo I/O/disk/thời gian, không tự chốt pause mọi thao tác. REC-P01 thu hồi quyền cũ sau restore là hành vi đề xuất, chưa thuộc DATA-03 đã duyệt. D08/8 giờ/7 ngày/restore lọc mốc gốc giữ nguyên. |
| Kiểm chứng | V-TAB/V-AUDIO/V-CD/V-TX/V-TTS/V-BKP/V-REC là cách kiểm để người dùng thực hiện sau; chưa chạy phần mềm, máy/thiết bị/Docker/SSH/provider. Các cổng không thể đóng bằng việc mô tả trên tài liệu. |
| Nguồn yêu cầu | PRD 0.8.6/UI 0.7.6/Business Flow chỉ đồng bộ dẫn chiếu, không đổi mã/BR/DR/23 FR/14 UC/50 AC/6 NFR hoặc hướng UI/ranh giới. TECH-05/DATA-03 và lịch sử xác nhận giữ nguyên. |
| Tiến độ/G03 | Roadmap 0.8 ghi thiết kế một phần, không GitHub/test/CD. Lượt này không đọc GitHub, không nhắc tích theo snapshot cũ hoặc đánh dấu toàn TDD/DB hoàn thành. |
| R06 | Vẫn mở một phần: LIFE/REC cần quyết định, ranh giới/nội dung/hồ sơ, quota/pipeline thật, mapping/FK/security/tab/audio/clock/claims/backup/SSH và ngưỡng đánh giá chưa chốt. Không hỏi lại phần đã xác nhận. |

<a id="review-data-04"></a>

<a id="716-data-04--xác-nhận-vòng-đờirestorebackup-life-p01-đơn-giản--04112026"></a>

### 7.16. DATA-04 — xác nhận vòng đời/restore/backup, LIFE-P01 đơn giản — 04/11/2026

**Theo thời điểm trước DATA-05:** LIFE-P01 ở bảng này chỉ chốt đơn giản hóa và ticket/generation mới đề xuất; nay 7.17/DATA-05 thay đúng phần bằng vé 24 giờ, không generation. Các xác nhận DATA-04 khác vẫn giữ, không sửa lịch sử thành lựa chọn mới.

| Nhóm | Kết quả đối chiếu / giới hạn |
| --- | --- |
| Nguồn xác nhận | Đã đọc bản thực tế trước sửa; DATA-04 giữ LIFE-P01–P03/REC-P01 và dùng BKP-P02 truy vết hướng backup mới. P02/P03/REC/backup đã chốt đúng phần; P01 chỉ duyệt hướng đơn giản, chưa context/10 phút hoặc toàn giải pháp. |
| LIFE-P01 | Phương án minimal đề xuất ticket ký/nonce gắn credential + một start_generation bền; ledger còn trả session gốc, purge tăng generation trước xóa, mã cũ/ledger mất không tạo session. Bỏ bảng context/TTL 10 phút; không bước/đồng hồ/tombstone PII mới. Phải kiểm start/concurrent/purge/rollback trước hiện thực. |
| LIFE-P02/P03 | 24 giờ từ cấp window, không login gia hạn/replay sau hạn; job cũ terminal rõ sau bảy ngày chỉ khi hết refs/use/current. Không TTL audio/text/draft/active/unknown. Model/API/operations đã đồng bộ, V-TX/V-TTS chưa chạy. |
| REC-P01 | Restore thật thu hồi quyền/window cũ trước public, giữ lịch sử/timestamp; khách xác nhận mới/Admin login/đối chiếu, không endedAt giả/auto session. Không restart/deploy thường hoặc xóa Blob tức thì; thay đổi sau B có thể mất, người dùng đã hiểu. |
| BKP-P02 | Chọn stop ngoài demo thay BKP-P01 online. Drain/đóng nhận/dispatch/cleaner, stop app/writer, MySQL còn chạy; DB/catalog/copy cùng checkpoint, không MySQL tạm mỗi backup. TTS chưa rõ không gửi lại hoặc làm mất việc nhận; không checkpoint sạch thì hoãn/FAILED. Restore thử vẫn riêng, downtime/RPO-RTO chưa đo. |
| PRD/Business Flow/UI | PRD 0.8.7 DR-08/NFR-03/8.3.2 truy vết đúng các xác nhận mới theo mã cũ. Business Flow bổ sung quy tắc chung, UI 0.7.7 chỉ trạng thái/phụ thuộc. Giữ 18 BR/9 DR/23 FR/14 UC/50 AC/6 NFR; không thêm màn hình hoặc thiết kế đồ họa. |
| Lịch sử/câu chữ | Context/10 phút/BKP-P01 và trạng thái trước duyệt giữ trong data-model-history/decisions 7.12; hiện hành không ghép hai phương án. Dấu # thừa trước dòng nhật ký decisions 03/11 đã bỏ, ý/ngày/lịch sử giữ. |
| R06/G03 | R06 còn cơ chế/kiểm thực/nội dung-vùng/TTS/đánh giá, không đóng theo gói. Không GitHub/checklist mới được xác minh; chưa toàn mục TDD/DB/CD hoặc kết quả phần mềm, không nhắc tích theo snapshot cũ. |

<a id="review-q-proposal-table"></a>

## Bổ sung lưu trữ ngày 05/11/2026 — bảng đề xuất Q1–Q3

Chuyển nguyên phần còn sót ở báo cáo hiện hành, gồm tiêu đề và toàn bộ bảng bên dưới. Nhãn “CHƯA XÁC NHẬN” chỉ là trạng thái lúc đề xuất; Q1–Q3 đã được xác nhận theo [decisions](../02-nghiep-vu/decisions.md#dec-q). Trạng thái hiện tại và phần thực sự còn mở xem [báo cáo hiện hành](../02-nghiep-vu/prd-review.md#review-open-final). Chỉ cập nhật đường dẫn liên kết cho vị trí lưu, không đánh số lại nội dung lịch sử.

## 7.1. Bộ phương án duyệt chung — CHƯA XÁC NHẬN
> 
> Bản chi tiết, các ví dụ và giới hạn nằm tại [PRD mục 9.4](../02-nghiep-vu/PRD.md#prd-proposals). Bảng dưới là bản tóm tắt; không có nội dung nào trong ba phương án được coi là quyết định đã chốt.
> 
> | Câu hỏi | Khuyến nghị chính | Ví dụ / lý do | Ảnh hưởng sau xác nhận |
> | --- | --- | --- | --- |
> | Q1 — Thông tin địa điểm | Admin chỉ bắt buộc nhập tên; mã hệ thống cấp và độc lập tên. Đề xuất tên 1–100 ký tự hiển thị, chuẩn hóa khoảng trắng, Unicode/số/dấu câu, không xuống dòng/điều khiển. Cho trùng tên kèm cảnh báo không chặn và phân biệt bằng mã. Lưu được điểm ẩn chưa có vùng/bài; bật hiện vẫn đủ A02.5; thuyết minh quản lý riêng. Giới hạn 100 và quy tắc trùng tên mới đều chờ duyệt. | Nhập “  Khu   trưng bày A  ” → “Khu trưng bày A”, lưu điểm ẩn; bật hiện thì báo rõ còn thiếu vùng/vi. Tối thiểu cho CRUD và không trùng dữ liệu thuyết minh. | FR-A02; UC-A02; AC-A03/A04; BR-11/DR-06. Giữ ranh giới A02.2/A02.5/A02.6, FR-A03/A04. |
> | Q2 — Nội dung chưa lưu | Giữ tạm văn bản/bản dịch, thông tin địa điểm và hình vùng đã vẽ trong tab đang làm, qua hết phiên/đăng nhập lại cùng tài khoản, lưu lỗi/mất mạng và tải lại bình thường. Khôi phục bản tạm gần nhất được ghi nhận, không tự lưu/áp dụng/gửi lại tác vụ. Đăng xuất cần xác nhận bỏ; đóng tab/crash/thiết bị khác ngoài cam kết phục hồi, vòng đời tạm phải kết thúc phù hợp. | Đang sửa fr bị hết phiên, đăng nhập lại để tiếp tục đúng bản chưa lưu. Lưu bản sửa vùng thành công không đồng nghĩa Áp dụng. Giảm mất dữ liệu thường gặp mà không tạo kho nháp tạm dài hạn. | FR-A01–A04; UC-A01–A03; AC-A02/A03/A05/A06/A07; NFR-04/DR-06. Đối chiếu UC-A04/UC-A06, AC-A10/A12/A18; không mở lại tác vụ đã nhận/kết quả muộn. |
> | Q3 — Hoạt động Admin | Tính nhập/sửa, vẽ vùng, chọn chức năng/đối tượng/bộ lọc, chủ động xem/cuộn, Lưu/Áp dụng/ẩn-hiện/Xóa, yêu cầu TTS/kiểm tra/thử lại và Phát/Tạm dừng/tua nghe thử. Không tính tác vụ nền, tải trạng thái/giữ tạm tự động, audio tự phát hoặc chỉ để trang mở. Hoạt động được ghi nhận trong phiên còn hiệu lực bắt đầu lại khoảng 30 phút; không hồi sinh phiên đã hết. | Đăng nhập 09:00, nhập 09:20 → 09:50; bấm Tạo audio 09:25 → 09:55. Sau đó chỉ có tác vụ tự chạy thì vẫn hết 09:55, TTS đã nhận tiếp tục. | FR-A01/BR-14; UC-A01; AC-A02; NFR-03/NFR-04. Đối chiếu UC-A04/AC-A12 để không gia hạn bằng tác vụ nền. |

06/11/2026 — biên tập nhãn mốc mục tiêu theo cách trình bày dự án nhóm; giữ nội dung yêu cầu và bằng chứng theo thời điểm.
