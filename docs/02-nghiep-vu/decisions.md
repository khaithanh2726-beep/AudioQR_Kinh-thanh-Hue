# Quy tắc nghiệp vụ và quyết định thiết kế

Tập hợp các quy tắc có mã ổn định cho phạm vi, phân công, phiên tham quan, quản trị, nội dung và vòng đời dữ liệu. G02 phân định trách nhiệm thành viên; D/A/Q/UI/TECH/DATA là căn cứ cho đặc tả và thiết kế. Nguồn khảo sát và diễn biến xác nhận chi tiết được lưu trong hồ sơ phát triển; bộ hiện hành trình bày nội dung áp dụng.

<a id="dec-1"></a>

## 1. Cách đọc và quy ước trạng thái

Mục lục hiện hành tại [docs/README.md](../README.md). Tài liệu này giữ quy tắc áp dụng; lịch sử xác nhận nguyên bản được bảo toàn trong hồ sơ phát triển.

- **Đã chốt:** nội dung đã được nhóm xác nhận trong phạm vi ghi tại từng mã; không đồng nghĩa phần mềm đã nghiệm thu.
- **Đề xuất / còn mở / chờ thiết kế hoặc rà soát:** chưa được duyệt theo gói; không dùng như yêu cầu bắt buộc đã chốt.
- **Đã bị thay thế / lịch sử:** chỉ dùng để truy vết, không áp lại phương án cũ.
- Mã **BS** bổ sung quyết định cùng số, không tự hủy phần gốc. Mã P từng được dùng cho đề xuất nhưng **P01–P08 hiện đã chốt**; không suy ra trạng thái từ tên mã. Giữ nguyên mã D/P/A/G/BF và các mục con.
- Mỗi quyết định có một vị trí chính ở mục 3–5; mục 2 là chỉ mục, [Business Flow](business-flow.md) diễn giải vận hành và [PRD](PRD.md) đặc tả yêu cầu/nghiệm thu theo phạm vi PRD-01; không suy rộng duyệt sang mục còn mở.
- Duyệt nghiệp vụ không duyệt giao diện/ranh giới, nội dung lịch sử/bản dịch/audio, TTS, thiết kế kỹ thuật hoặc chứng minh phần mềm đã kiểm thử. Không đưa dữ liệu nhạy cảm vào tài liệu.

<a id="dec-2"></a>

## 2. Mục lục quyết định hiện hành

| Mã | Chủ đề / vị trí chính | Trạng thái |
| --- | --- | --- |
| G01 | [Định hướng đồ án học phần](#g01) | Đã chốt 30/10/2026 |
| BF-K01 / BF-A01 / BF-ALL01 | [Phạm vi duyệt tổng thể](#bf-approval) | Đã chốt 30/10/2026 |
| PRD-01 | [Phạm vi yêu cầu PRD 0.4.1 được duyệt](#prd-approval) | Đã chốt 31/10/2026; loại trừ phần còn mở/đề xuất/chưa kiểm chứng |
| G02 | [Phân công nhóm và người commit](#g02) | Đã chốt 02/11/2026; backlog 0.2 đã duyệt sau đó, nội dung hiện hành xem roadmap; tài khoản tại G02, lịch theo mục tiêu dự kiến |
| TECH-01 | [Công nghệ và kiến trúc dự án chính](#tech-01) | Đã chốt nền; phiên bản theo TECH-03, kiến trúc/CD theo TECH-04/05; tương thích và vận hành còn kiểm |
| TECH-02 | [Môn học, hướng dẫn, môi trường và mục tiêu](#tech-02) | Đã xác nhận đầu vào 03/11; nền tảng/trình duyệt theo TECH-03, ngrok chọn thử; giờ thực tế và ngân sách trả phí chưa chốt |
| TTS-01 | [Gemini TTS/Charon](#tts-01) | Giữ model/giọng; pipeline đã chạy, chưa duyệt đủ 20 cặp. |
| TECH-03 / IN-P01–IN-P04 | [Mốc phiên bản, nền tảng kiểm tra, audio và ngrok thử](#tech-03) | Đã chọn mốc/định dạng; ngrok chỉ chọn thử, chưa đạt tương thích/triển khai |
| DATA-01 / IN-P05 / DATA-P01–P04 | [Dọn file/log/backup](#data-01) | Đã chốt điều kiện/hạn/restore; DATA-03 bổ sung phạm vi/nhịp backup, đã bổ sung path/quyền/công cụ/checkpoint và lịch; RPO/RTO còn mở |
| G03 | [Ghi nhận checklist theo bằng chứng](#g03) | Đã chốt; đối chiếu đúng issue hiện hành, người vận hành trực tiếp cập nhật GitHub |
| TECH-04 | [Đóng gói, phiên theo tab và audio tải trọn](#tech-04) | Hướng đã chốt 03/11; CD-L01 cập nhật theo TECH-05; cơ chế/ERD mới và tương thích chưa duyệt/kiểm |
| DATA-02 | [Backup cục bộ cho phiên bản nghiệm thu](#data-02) | Đã bỏ yêu cầu ngoài laptop; giữ hạn/restore DATA-01, không mục tiêu cứu hỏng/mất toàn máy |
| TECH-05 / CD-L01 | [Kênh CD và kiểm chứng](#tech-05) | Actions → Tailscale riêng → OpenSSH Windows/PowerShell đã chạy thật; giữ giới hạn vận hành riêng |
| DATA-03 | [Phạm vi và nhịp backup](#data-03) | DB + WAV/MP3 đang tham chiếu + manifest, cuối ngày có thay đổi/trước deploy-schema; giữ cục bộ/7 ngày/restore mốc gốc |
| DATA-04 / LIFE-P01–P03 / REC-P01 / BKP-P02 | [Vòng đời, phục hồi và backup stop phục vụ](#data-04) | 04/11: P02/P03/REC/backup đã duyệt đúng phần; P01 tại DATA-04 chỉ hướng đơn giản, nay DATA-05 chốt vé 24 giờ riêng; chưa toàn thiết kế/kiểm thực |
| DATA-05 / LIFE-P01 | [Vé Bắt đầu ký 24 giờ và chống lặp](#data-05) | Đã duyệt đúng phần 04/11; không start_generation/context/10 phút, không toàn TDD được duyệt hoặc kiểm |
| D01 / D01-BS | [Vùng tương tác và truy cập dự phòng](#d01) | Đã chốt; gốc 29/10, điều chỉnh vùng/phát 30/10/2026 |
| D02 / D02-BS | [Tên khách](#d02) | Đã chốt 29/10/2026 |
| D03 / D03-BS | [Ngôn ngữ và thiếu nội dung](#d03) | Đã chốt 29/10/2026 |
| D04 | [Phiên và lượt xem](#d04) | Đã chốt 29/10/2026 |
| D04-BS | [Cảnh báo 5 phút cuối](#d04-bs) | Đã chốt 30/10/2026 |
| D05 | [Vị trí ngoài phạm vi](#d05) | Đã chốt 29/10/2026 |
| D06 | [Bộ nội dung nghiệm thu](#d06) | Đã chốt; xem cùng A02/A02.5 về khả năng thêm điểm |
| D07 | [Ngôn ngữ và audio](#d07) | Đã chốt; xem bổ sung UI-01.2/UI-01.3 |
| D08 | [Dữ liệu và hạn lưu](#d08) | Đã chốt 29/10/2026; xem cùng A02.2 |
| D09 | [Duyệt nội dung](#d09) | Đã chốt 29/10/2026; không duyệt sẵn nội dung |
| P01 / P02 / P03 / P04 | [Hết hạn / quay lại / tải lại / lỗi mạng](#dec-p) | Đã chốt 30/10/2026 |
| P05 / P06 / P07 / P08 | [Tử Cấm Thành / bấm lại / ngoài vùng / chồng lấn](#dec-p) | Đã chốt 30/10/2026 |
| UI-01 / UI-P03 | [Hướng giao diện khách](#ui-guest-confirmed) / [nút audio ba trạng thái](#ui-p03) | Đã chốt 31/10; quyền khi hết hạn theo P01/UI-02 |
| UI-02 / UI-P01 / UI-P02 / UI-P04 | [Xác nhận box và nghe tiếp](#ui-guest-confirmed-02) | Đã chốt đúng phạm vi 01/11/2026; thay P01 phần dừng audio bắt buộc |
| A01 | [Đăng nhập và phiên Admin](#a01) | Đã chốt 30/10/2026 |
| A02 | [Vòng đời địa điểm](#a02) | Đã chốt 30/10/2026 |
| A02.1 / A02.2 / A02.3 | [Xóa dữ liệu riêng](#a02-1) / [giữ lịch sử](#a02-2) / [dọn file](#a02-3) | Đã chốt 30/10/2026 |
| A02.4 / A02.5 / A02.6 | [Vẽ vùng](#a02-4) / [điều kiện hiện](#a02-5) / [hiệu lực sửa vùng](#a02-6) | Đã chốt 30/10/2026 |
| A03 | [Văn bản và bản dịch](#a03) | Đã chốt 30/10/2026 |
| A04 | [Tạo audio từ văn bản](#a04) | Giữ bản văn lúc tạo, Check/Retry theo trạng thái; tạo xong không là duyệt/áp dụng. |
| A04.1 / A04.2 | [Chủ động tạo](#a04-1) / [trạng thái và thử lại](#a04-2) | Đã chốt 30/10/2026 |
| A04.3 / A04.4 | [Đúng bản văn](#a04-3) / [tác vụ đang chạy](#a04-4) | Đã chốt 30/10/2026 |
| A05 / A06 / A07 | [QR chung](#a05) / [phiên-thống kê](#a06) / [hiệu lực nội dung](#a07) | Đã chốt 30/10/2026 |
| Q1 / Q2 / Q3 | [Thông tin địa điểm](#q1) / [giữ tạm nội dung Admin](#q2) / [hoạt động Admin](#q3); bổ sung A02/A01/A03/A04 | Đã chốt 31/10/2026; không duyệt tổng thể PRD |
| UI-03 / UI-AP01 / UI-AP02 / UI-AP03 | [Hướng UI Admin lượt 1 và làm rõ luồng vùng](#ui-admin-confirmed-01) | Đã chốt đúng phạm vi 02/11/2026; không duyệt toàn bộ chi tiết; phần nội dung/audio được xác nhận riêng ở UI-04 |
| UI-04 / UI-AP04–UI-AP07 | [Lượt 2, chỉ áp dụng cả cặp và gate bật hiện](#ui-admin-confirmed-02) | Đã chốt đúng phạm vi 02/11/2026; UI-AP06 thay phương án cũ, trạng thái lượt 3 theo UI-05 |
| UI-05 / UI-AP08–UI-AP10 | [Lượt 3: box phiên/hai bảng/QR chung](#ui-admin-confirmed-03) | Đã chốt đúng phạm vi 02/11/2026; thay trang riêng UI-A21, không duyệt toàn bộ đồ họa/kỹ thuật |

Phần còn mở tại [mục 6](#dec-6); lịch sử theo mốc tại [tóm lược](../99-luu-tru/README.md).

<a id="dec-3"></a>

## 3. Định hướng, phân công và các hướng kỹ thuật đã xác nhận

<a id="g01"></a>

### 3.1. G01 — Định hướng và tiêu chuẩn đồ án học phần

**Đã chốt ngày 30/10/2026.**

- Xây dựng hệ thống thuyết minh tự động bằng mã QR cho Kinh thành Huế với đầy đủ phạm vi và tiêu chí nghiệm thu đã thống nhất.
- Tổ chức phân tích, thiết kế, triển khai và kiểm chứng theo phân công của nhóm.
- Đánh giá giải pháp theo tính nhất quán nghiệp vụ, trải nghiệm khách, khả năng kiểm thử và khối lượng phù hợp với nhóm.
- Áp dụng thống nhất các yêu cầu cho luồng chính và ngoại lệ. Ghi nhận phạm vi ở các phiên bản cũ không làm giảm tiêu chuẩn xử lý hoặc nghiệm thu.
- Giữ phạm vi đã chốt: không bổ sung hoạt động offline, đồng bộ phiên giữa tab hoặc đổi tốc độ audio; thanh tua audio đã duyệt vẫn giữ nguyên.
- Mức yêu cầu đầy đủ không đồng nghĩa phần mềm đã hoàn thành, đã kiểm thử hoặc toàn bộ tài liệu đã được duyệt.

Phạm vi phê duyệt Business Flow được xác nhận riêng tại BF-K01/BF-A01/BF-ALL01.

<a id="bf-approval"></a>

### 3.2. BF-K01 / BF-A01 / BF-ALL01 — Phạm vi được duyệt


| Mã | Nội dung đã chốt | Ý nghĩa / ảnh hưởng | Giới hạn |
| --- | --- | --- | --- |
| <a id="bf-k01"></a>BF-K01 | Luồng nghiệp vụ khách đã được duyệt tổng thể: bản tổng hợp 13 bước, luồng chính/ngoại lệ, phiên, vùng tương tác, audio, cảnh báo và cách đếm thống nhất. P02 dùng câu bắt buộc có điều kiện, không mở lại quyết định. | Đã tạo cơ sở chuyển sang phân tích Admin không cần chờ bản đồ/giao diện hoặc nội dung; Admin nay đã được duyệt riêng theo BF-A01. | Riêng BF-K01 chỉ duyệt khách; BF-A01/BF-ALL01 nay bổ sung duyệt nghiệp vụ Admin/hai vai trò. Không duyệt ranh giới/giao diện, lịch sử/bản dịch/audio, TDD hoặc phần mềm đã kiểm thử. |
| <a id="bf-a01"></a>BF-A01 | Luồng nghiệp vụ Admin đã được duyệt tổng thể: 12 nhóm công việc đã tổng hợp, A01–A07, A02.1–A02.6, A04.1–A04.4. Đối chiếu bản lưu không có khác biệt thực chất. | Đủ cơ sở soạn PRD/Use Case/tiêu chí nghiệm thu; không yêu cầu duyệt lại nghiệp vụ Admin. | Không duyệt theo giao diện/thao tác vẽ/ranh giới, nội dung, lựa chọn TTS, thiết kế kỹ thuật hoặc kết quả kiểm thử. |
| <a id="bf-all01"></a>BF-ALL01 | Business Flow cho hai vai trò đã được duyệt trong phạm vi nghiệp vụ đã thống nhất; BF-K01/BF-A01 cùng có hiệu lực. | Phân biệt phê duyệt nghiệp vụ với thiết kế, nội dung, PRD và bằng chứng thực hiện. | Tại thời điểm BF-ALL01, PRD mới soạn là dự thảo; nay phạm vi yêu cầu đã rõ được duyệt riêng theo PRD-01. Trạng thái tài nguyên theo ma trận nội dung hiện hành. |

Ba xác nhận chỉ duyệt nghiệp vụ hai vai trò. Không duyệt theo bố cục giao diện, thao tác vẽ/ranh giới cụ thể; lịch sử/bản dịch/audio; nhà cung cấp TTS/giọng/chi phí/công nghệ; thiết kế kỹ thuật hoặc kết quả kiểm thử. Trạng thái văn bản/audio theo [ma trận hiện hành](../05-noi-dung/doi-chieu-van-ban-audio.md). Các nhánh AD trong [Business Flow mục 4.1](business-flow.md#bf-admin-main) chi tiết hóa 12 nhóm Admin đã tổng hợp, không bổ sung nhóm nghiệp vụ mới.

<a id="prd-approval"></a>

### 3.3. PRD-01 — Duyệt phạm vi yêu cầu đã rõ của PRD 0.4.1

**Đã chốt ngày 31/10/2026. Người xác nhận:** Người dùng/chủ đồ án.

**Phạm vi phê duyệt:** yêu cầu đã mô tả rõ trong PRD 0.4.1, gồm Q1–Q3 và cách diễn đạt BR-11; loại trừ mọi mục còn mở, đề xuất hoặc chưa kiểm chứng.


| Phạm vi | Trạng thái / giới hạn |
| --- | --- |
| Mục tiêu/vai trò, phạm vi trong-ngoài, bộ chuẩn năm điểm × vi/en/fr/ru; BR/DR, FR, UC, NFR và tiêu chí nghiệm thu đã mô tả rõ ở PRD 0.4.1 | **Đã được duyệt trong phạm vi đã rõ**, gồm Q1–Q3, giới hạn tương ứng và câu BR-11 hiện hành. Giữ mã/truy vết; không yêu cầu duyệt lại BF-K01/BF-A01/BF-ALL01 hoặc Q1–Q3. |
| Mục còn mở/đề xuất/chưa kiểm chứng | **Không thuộc xác nhận này**: PRD mục 9 và [bảng tổng hợp prd-review 7.3](prd-review.md#review-open-final) tiếp tục giữ trạng thái và mốc xử lý riêng. Có một phần đã rõ trong cùng FR/UC/AC không có nghĩa các chi tiết mở của mã đó đã được duyệt. |
| Thiết kế giao diện/thao tác/ranh giới; giải pháp kỹ thuật; TTS/giọng/chi phí/giới hạn chưa chốt; môi trường/ngưỡng đánh giá chưa chốt; chính sách log/bản sao lưu và file chưa từng áp dụng | Không được duyệt ngầm. Chỉ trình xác nhận phần thực sự mới khi tới công việc liên quan. |
| Nội dung lịch sử, bản dịch/audio; kết quả kiểm thử hoặc phần mềm đáp ứng | Không được duyệt hoặc xác minh qua PRD-01. D09 tiếp tục áp dụng; trạng thái văn bản/audio và hồ sơ theo [ma trận hiện hành](../05-noi-dung/doi-chieu-van-ban-audio.md). |


<a id="g02"></a>

### 3.4. G02 — Phân công nhóm và người commit

**Phân công được xác nhận ngày 02/11/2026; thông tin tài khoản cập nhật ngày 07/11/2026.** Nhóm gồm Thành, Hòa và Nghĩa, làm việc trên ba máy riêng và tích hợp qua repository chung. Bảng sau quy định trách nhiệm; kết quả thực hiện được ghi nhận bằng sản phẩm và bằng chứng kiểm chứng.

| Task | Người phụ trách | Người commit |
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

- T01: Thành tổng hợp; Nghĩa và Hòa đóng góp thiết kế liên quan phần việc mình.
- T08: cả nhóm phối hợp; phân chia địa điểm khi thống nhất cụ thể, chưa gán địa điểm riêng cho từng thành viên.
- T09: Hòa phụ trách tích hợp/nghiệm thu; mỗi thành viên kiểm tra và sửa lỗi thuộc phần mình.
- T11: Thành tổng hợp; Nghĩa và Hòa cung cấp hướng dẫn, kết quả và bằng chứng phần việc.
- T02 phụ trách cấu trúc/ràng buộc/migrations; T03/T05 phụ trách nghiệp vụ backend; T04/T06 phụ trách giao diện. T07 tạo/theo dõi audio; T05 quản trị, áp dụng cặp và xóa/dọn. T01 là phân tích/thiết kế; T11 là hướng dẫn/vận hành/bàn giao cuối.
- Mỗi nhóm commit/push dùng tài khoản GitHub tương ứng với cột người commit của G02. Trước commit xác định task, tài khoản, metadata Git, danh sách file, phụ thuộc và staged diff. Bảng phân công hoặc tài khoản dùng để push không tự chứng minh ai đã viết mã, thao tác hay hoàn thành phần việc.
- Theo dõi task và phụ thuộc tại [roadmap](../01-tong-quan/roadmap.md). Đối chiếu từng nhóm file và staged diff trước commit; không dùng tài khoản push để suy tác giả hoặc người đã thực hiện.

| Thành viên | Tài khoản GitHub |
| --- | --- |
| Thành | `khaithanh2726-beep` |
| Hòa | `gingpham` |
| Nghĩa | `lechiNghiaa` |

**Tài khoản theo task:** T01/T03/T05/T07/T11 dùng `khaithanh2726-beep`; T02/T09/T10 dùng `gingpham`; T04/T06 dùng `lechiNghiaa`. T08 dùng tài khoản tương ứng phần nội dung được giao, chưa tự gán địa điểm cho từng thành viên.

**Phân biệt khi kiểm Git:** người nhập lệnh, metadata author/committer và tài khoản xác thực GitHub là các thông tin khác nhau. `git config user.name/user.email` chỉ đặt metadata, không đăng nhập hoặc chuyển tài khoản push. Tài khoản xác thực phải theo task như trên; tên/email commit cần có căn cứ và email gắn với tài khoản tương ứng. Không tạo thông tin tác giả hoặc suy đã hoàn thành công việc từ việc dùng tài khoản. Không tự gán assignee, sửa issue/Project hoặc thao tác tài khoản khi chưa được giao.

[Roadmap mục 3](../01-tong-quan/roadmap.md#t01) là nguồn 11 task/89 ô; mốc đối chiếu hiện hành là 62/89. Issue và Project là hai trạng thái riêng; không suy toàn bộ task hoàn thành từ một sản phẩm hoặc một lượt kiểm.

Căn cứ phân công tại [hồ sơ G02](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#g02-source). G02 không thay đổi phạm vi G01/BF/PRD/UI.

<a id="tech-01"></a>

### 3.5. TECH-01 — Công nghệ và kiến trúc dự án chính

| Phạm vi | Lựa chọn và cách triển khai |
|---|---|
| Frontend | Vue 3, JavaScript, CSS; Vite và npm để phát triển/đóng gói. Node.js không là Backend nghiệp vụ. |
| Bản đồ | Leaflet, nền OpenStreetMap có attribution; mỗi điểm một đa giác kín đơn giản. Geoman Free là lựa chọn khảo sát ban đầu; mã hiện hành dùng trình sửa vùng riêng trên Leaflet, không có dependency Geoman. Không mở rộng lỗ/nhiều mảnh/GPS/Haversine. |
| Backend | Java 21, Spring Boot, Maven Wrapper; một ứng dụng, Controller → Service → Repository. |
| Dữ liệu | MySQL, Spring Data JPA, Flyway V1–V14; WAV/MP3 ở kho file riêng. |
| Admin | Spring Security, session/cookie, password hash và CSRF; giữ 30 phút/Q2/Q3. |
| API | DTO và hợp đồng API/giao dịch; springdoc/OpenAPI hỗ trợ tra cứu. |
| Kiểm thử | Spring Boot/Testcontainers MySQL, frontend logic và trình duyệt; kết quả theo báo cáo kiểm thử. |
| Đóng gói/CI | Docker Compose, Vue build vào static resources của Spring Boot; GitHub Actions test/build. CD riêng theo TECH-05. |

Mã nguồn tách `frontend/` và `backend/`, phục vụ web/API cùng origin HTTPS. Không bổ sung microservices, Redis, Kafka hoặc Kubernetes. Phiên bản chính xác và phạm vi đã kiểm ở [bảng công nghệ](../01-tong-quan/technical-inputs.md); giới hạn dữ liệu/vận hành ở [bàn giao](../01-tong-quan/handoff.md).

Giữ nghiệp vụ đã duyệt: áp dụng cả cặp, điều kiện cặp vi khi bật hiển thị, phiên khách/Admin, Q2/Q3, thống kê/QR và chính sách giữ dữ liệu. Lựa chọn công nghệ không thay việc duyệt ranh giới, nội dung hoặc nghiệm thu từng tiêu chí.

<a id="tech-01-context"></a>
<a id="tech-01-open"></a>

<a id="tech-02"></a>

### 3.6. TECH-02 — Đầu vào môn học và môi trường

- Chủ đề 2: Backend ba lớp có CI/CD, frontend đầy đủ; giữ Vue theo TECH-01. CI có bằng chứng thực; CD thực chạy đạt theo TECH-05; không đồng nghĩa mọi điều kiện vận hành đã hoàn tất.
- Môi trường phát triển/diễn tập: Windows, VS Code, PowerShell, Docker Desktop/Linux containers. Công cụ và lệnh kiểm tại [môi trường local](../07-van-hanh/moi-truong-local.md).
- Khách hướng tới điện thoại Android/Chrome và iPhone/Safari; Admin dùng Windows/Chrome với chuột/bàn phím. Cần ghi model, OS, browser và mạng trong ma trận nghiệm thu; báo cáo Android/iPhone chạy được chưa thay toàn bộ ma trận.
- Hướng trình diễn: laptop có Internet phục vụ qua HTTPS/ngrok, ưu tiên miễn phí. Ngân sách trả phí không tự được cấp từ việc chọn công nghệ.
- Mục tiêu nội bộ 17/11/2026 không là hạn nộp chính thức; không thay rubric môn học.
- Phân công theo G02. Mã, dữ liệu, khóa và lịch sử demo khác không tự trở thành đầu vào đã được duyệt cho dự án chính.



<a id="tts-01"></a>

### 3.7. TTS-01 — Gemini TTS và giọng Charon

Giữ Gemini API, model **gemini-3.8-flash-tts**, giọng **Charon**. Không tự đổi model/giọng hoặc thêm phong cách đọc chưa duyệt. Khóa ở cấu hình riêng ngoài repository; việc dùng API và ngân sách được quản lý riêng.

Pipeline thật đã tạo WAV/MP3; kết quả hiện có được tổng hợp ở [ma trận văn bản/audio](../05-noi-dung/doi-chieu-van-ban-audio.md). Chất lượng nghe mẫu hoặc kiểm codec/Hash/chép lời không thay duyệt từng cặp D09. Bộ 20 audio kỹ thuật đã đủ; 19 bài mới còn chờ nghe trọn/hồ sơ D09 và rà phát âm Thái Hòa fr trước áp dụng. Quota thực phải được kiểm trước lần tạo tiếp; không suy từ gói tài khoản hoặc một lần gọi thành công.



<a id="tech-03"></a>

### 3.8. TECH-03 — IN-P01–IN-P04: phiên bản và phương án chạy

| Mã | Nội dung áp dụng | Điều kiện nghiệm thu |
|---|---|---|
| IN-P01 | Giữ bộ phiên bản trong bảng công nghệ hiện hành. | Thay phiên bản phải có căn cứ và rà ảnh hưởng; không đổi âm thầm. |
| IN-P02 | Khách Android/Chrome, iPhone/Safari; Admin Windows/Chrome. | Ghi thiết bị/OS/browser/mạng thực, kiểm luồng chính và ngoại lệ; không cam kết mọi thiết bị. |
| IN-P03 | Giữ WAV nguồn còn tham chiếu; MP3 cho khách; FFmpeg/ffprobe trong Linux container. | Pipeline đã chạy không thay nghe trọn/D09; không tự áp ngưỡng chất lượng hoặc bitrate mới. |
| IN-P04 | Thử ngrok cho HTTPS công khai; laptop/app/agent phải hoạt động. Admin vẫn xác thực session/cookie. | QR phải trỏ đúng URL đang phục vụ. Kiểm trang trung gian và quota tài khoản trước buổi chấm; không coi ngrok là hosting độc lập hoặc CD. |

Không cố định thông số gói miễn phí vào tiêu chí nghiệp vụ. Hạn mức request/dữ liệu tính cả web, API và audio, cần đối chiếu tài khoản khi vận hành. Hướng đóng gói theo TECH-04; đường quản trị riêng theo TECH-05.



<a id="data-01"></a>

### 3.9. DATA-01 — IN-P05: chính sách file/log/backup được xác nhận

**Đã chốt đúng các nội dung sau ngày 03/11/2026.** Giữ DATA-P01–P04 là mã phương án nguồn trong technical-inputs, nay đọc theo hàng hiện hành dưới đây. Không coi mọi chi tiết của đề xuất cũ được duyệt theo gói.

| Nhóm / truy vết | Chính sách đã xác nhận | Giới hạn còn giữ |
| --- | --- | --- |
| File hoàn chỉnh chưa từng áp dụng / DATA-P01 | Chỉ tính dọn sau **24 giờ từ khi xác nhận không còn tham chiếu, không dùng chung và không tác vụ sử dụng**. Bản nháp còn tham chiếu không tự bị xóa vì chưa duyệt. | Kiểm lại điều kiện trước dọn; không gán mốc ngừng áp dụng giả cho file chưa áp dụng. |
| Workspace/file tạm / DATA-P02 | Dọn sau **1 giờ** khi tác vụ đã kết thúc, không worker dùng và đủ an toàn. Không coi đang chạy/chưa rõ kết quả là rác. | Tác vụ đã tiếp nhận theo A04.4; kết quả muộn không phục hồi địa điểm đã xóa. Cách xác định an toàn/mốc đủ điều kiện/worker/lịch thử lại để TDD. |
| Log kỹ thuật / DATA-P03 | Giữ **7 ngày**, không ghi secret hoặc thông tin khách/nội dung nhạy cảm. | Loại trường/mục đích/quyền cụ thể theo nhu cầu vận hành cần làm rõ; log ngrok/provider/hạ tầng không mặc định được app xóa. Không thêm trang log Admin. |
| Backup / DATA-P04 | Giữ mỗi bản **7 ngày từ lúc tạo**; bản sao phải được bảo vệ và dọn đúng hạn. Restore vào môi trường riêng, lọc phiên/lượt quá hạn theo mốc gốc **trước khi mở phục vụ**. | Người dùng chấp nhận bản sao có thể còn tới gần **37 ngày tính từ kết thúc/hết hạn gốc** (30 ngày vận hành cộng gần 7 ngày backup), không phải thêm 37 ngày sau xóa vận hành. Không làm sống lại dữ liệu D08 hoặc đổi mốc theo lúc restore/audio hết. |
| Quy tắc gốc | Giữ D08 **30 ngày** phiên/lượt và A02.3 **8 giờ** bảo vệ file từng áp dụng, đồng thời các điều kiện tham chiếu/dùng chung/DB thành công. | Không dùng chung TTL mọi lớp; không gộp xóa DB với file, không đổi quyền nghe tiếp khách. |

**Bổ sung DATA-02/DATA-03:** backup cục bộ tách dữ liệu chạy, gồm DB + WAV/MP3 tham chiếu + manifest, cuối ngày có thay đổi/trước deploy-schema. Công cụ backup/restore, ACL và lượt backup qua Task Scheduler trên bản phục vụ đã được kiểm; chưa quan sát trigger 23:00 tự chạy hoặc nghiệm thu RPO/RTO. Không tự kéo dài mốc giữ/xóa.

<a id="g03"></a>

### 3.10. G03 — Ghi nhận checklist GitHub theo bằng chứng

**Đã xác nhận ngày 03/11/2026.** Mỗi khi một mục thực sự hoàn thành:
- Đọc đúng checklist issue T01–T11 hiện hành trước khi nhắc; dùng nguyên văn mục và tên/link issue.
- Khi đề nghị ghi nhận hoàn thành, cung cấp mã/tên/link issue, nguyên văn ô checklist và căn cứ hoàn thành để người vận hành đối chiếu.
- Hoàn thành một phần: báo phần đã làm/còn thiếu, không nhắc tích toàn mục. Tài liệu xong không là build/test/CD hoặc phần mềm đã nghiệm thu.
- Người vận hành trực tiếp tích/sửa/đóng issue hoặc thay đổi Project. Khi local và issue khác nhau, cần đối chiếu nội dung và chuẩn bị phần cập nhật; công cụ hỗ trợ không tự thay đổi GitHub.
- Không suy tên người G02 thành GitHub username hoặc người đã thực hiện.

**Theo dõi tiến độ:** [Roadmap](../01-tong-quan/roadmap.md) giữ checklist và trạng thái có bằng chứng. Lịch mục tiêu không là xác nhận hoàn thành.

<a id="tech-04"></a>

### 3.11. TECH-04 — Hướng kiến trúc, phiên tab và audio tải trọn

**Đã xác nhận ngày 03/11/2026**, nguồn [8.1.15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#tech-04-source); bổ sung TECH-01–03/D04/P01/UI-02, không mở lại nghiệp vụ.

| Phần | Hướng đã xác nhận | Phạm vi chưa được duyệt/kiểm |
| --- | --- | --- |
| Đóng gói | Mã nguồn tách frontend/backend; Vue build rồi phục vụ cùng Spring Boot qua một cổng nội bộ, ngrok đưa ra HTTPS. Backend một ứng dụng chia module, Controller → Service → Repository. | Không duyệt toàn bộ TDD, tên mọi module/chi tiết DEV/API hoặc khẳng định tương thích đã chạy. |
| CD-L01 | TECH-05 đã chọn Tailscale riêng + OpenSSH Windows, Actions qua PowerShell; ngrok web không Funnel. | SSH/Docker và CD thực chạy đã có bằng chứng riêng; không coi mọi chi tiết vận hành được duyệt theo lựa chọn kênh. |
| Phiên theo tab | Hai người mở hai tab rồi bấm bắt đầu thành công có hai ID độc lập, tên có thể trùng. Mỗi tab giữ đúng phiên; đổi ngôn ngữ/kết thúc A không ảnh hưởng B. Mở/tải lại không tự tạo phiên. | TDD phải xử lý tải lại/nhân bản/khôi phục và backend kiểm quyền, không chỉ cookie chung hoặc tin ID client. Không yêu cầu đồng bộ nghiệp vụ giữa tab. |
| Tải audio | Khi chọn bài còn quyền, tải **trọn MP3 bài hiện tại** rồi giữ tạm cho tab và phát; Blob/object URL hoặc cơ chế tương đương. Không giả định media buffer/cache đã đủ. | Đây là hướng cần cụ thể hóa/kiểm chứng; không kho offline dài hạn, không thay mốc 8 giờ file/30 ngày/8 giờ phiên. |
| Ngoại lệ hết hạn | Chỉ đúng bài **đang phát tại mốc hết hạn** được giữ dữ liệu đã tải để pause/resume tới cuối; map mờ/khóa/box hết hạn, ngôn ngữ khóa, văn bản vẫn đọc-cuộn. Tua/nghe lại/bài mới/điểm mới khóa. | Đang tải/chưa phát/đã pause trước mốc không có ngoại lệ; phản hồi muộn không tự phát. Không khôi phục nghe tiếp sau reload/đóng tab. Không hứa browser giữ tab/bộ nhớ hoặc autoplay. |
| Bắt đầu phiên mới | Dừng A1, giải phóng dữ liệu cũ phù hợp, về UI-K01 theo nhánh hết hạn (điền sẵn tên/ngôn ngữ còn hợp lệ). Chỉ bấm Bắt đầu thành công mới tạo phiên; chọn lại điểm lấy cặp hiện hành A2. | Không tự phát A2 vì reload; vẫn giữ chọn vùng phát/đổi ngôn ngữ không tự phát/P06. Không âm thầm gia hạn hoặc thêm lượt. |

**Cần thiết kế/kiểm trong TDD:** vòng đời blob, hủy tải/loại phản hồi cũ khi đổi vùng/ngôn ngữ/chọn liên tục, không giải phóng sớm khi thao tác còn hợp lệ; đánh đổi thời gian chờ/bộ nhớ/mạng; Android/Chrome và iPhone/Safari, gồm tab nền/browser tải lại. Hướng đúng không phải kết quả đã chạy.

<a id="data-02"></a>

### 3.12. DATA-02 — Backup cục bộ, phạm vi phục hồi phiên bản nghiệm thu

**Đã xác nhận ngày 03/11/2026.** Bỏ yêu cầu phải chuẩn bị ổ ngoài hoặc nơi ngoài laptop cho phiên bản nghiệm thu. Cho phép thiết kế backup cục bộ tại vị trí tách dữ liệu đang chạy; không tự bỏ backup.

- Giữ DATA-01: mỗi bản bảy ngày từ tạo, bảo vệ/dọn đúng hạn, restore vào môi trường riêng, lọc phiên/lượt quá hạn theo mốc gốc trước phục vụ; chấp nhận phần lưu thêm gần 37 ngày đã chốt.
- Mục tiêu là phục hồi lỗi thao tác/triển khai khi còn bản sao; **không đặt mục tiêu khôi phục mất/hỏng toàn bộ laptop**. Tách thư mục/volume không trở thành bản sao ở máy khác.
- Trạng thái tại lần chốt DATA-02: nhịp/phạm vi từng để đề xuất trong TDD; **nay DATA-03 đã xác nhận riêng**. Công cụ/checkpoint đã được kiểm riêng; quyền/lịch vận hành và RPO/RTO trên môi trường phục vụ còn chờ nghiệm thu.
- Các phương án lưu mã hóa ở thiết bị khác của technical-inputs 0.1 chỉ còn là lựa chọn ngoài phạm vi bắt buộc, không điều kiện chặn TDD/nghiệm thu phiên bản nghiệm thu.
- Phạm vi DB + WAV/MP3 đang tham chiếu + manifest và nhịp cuối ngày có thay đổi/trước deploy-schema nay **đã được DATA-03 xác nhận riêng**; công cụ nhất quán/restore đã được kiểm riêng, lịch và quyền ở môi trường phục vụ còn chờ nghiệm thu.

<a id="tech-05"></a>

### 3.13. TECH-05 — CD-L01 đã chọn và có bằng chứng thực chạy

**Đã xác nhận ngày 03/11/2026**, căn cứ [8.1.16](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#tech-05-source).

- Chọn Tailscale **mạng riêng** kết hợp OpenSSH trên **Windows** để GitHub Actions triển khai qua PowerShell vào laptop.
- Ngrok tiếp tục cho khách/Admin truy cập website công khai; không dùng lại Funnel làm đường công khai. URL web không là quyền triển khai.
- **Cổng kiểm trước triển khai đầy đủ:** SSH qua mạng riêng bằng khóa/host key đã ghim và Docker context của đúng tài khoản đã kiểm; SFTP/receiver riêng đạt. [GitHub runner thật](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) đã build, truyền và triển khai đạt trên nguồn 134ac0d; source/image/runtime được đối chiếu sau chuyển.
- Giữ một Backend/ba lớp, source frontend/backend riêng và Vue build cùng Spring Boot theo TECH-04. Cấu hình runtime/khóa giữ ngoài source; workflow chủ động đúng SHA và chỉ deploy sau các job kiểm đạt.
- Kênh đã chọn không đồng nghĩa toàn TDD/API/ERD được duyệt, CI xanh không là bằng chứng CD; bảo toàn DB/audio và đối chiếu backup/restore/rollback cùng phạm vi đã kiểm.

<a id="data-03"></a>

### 3.14. DATA-03 — Phạm vi/nhịp backup và ví dụ phục hồi đúng hạn

**Đã xác nhận ngày 03/11/2026**, bổ sung DATA-01/DATA-02:

- Backup gồm **database, các file WAV/MP3 đang được tham chiếu và bản kê thông tin cần thiết để phục hồi**.
- Thực hiện **cuối ngày có thay đổi** và **trước triển khai hoặc thay đổi cấu trúc database**.
- Lưu **cục bộ, tách dữ liệu đang phục vụ**; không bổ sung yêu cầu ngoài laptop hoặc phục hồi mất/hỏng toàn laptop.
- Giữ từng bản **bảy ngày**; bảo vệ/dọn đúng hạn. Restore vào môi trường riêng và lọc phiên/lượt quá hạn theo timestamp gốc **trước đưa vào phục vụ**; giữ phần lưu thêm backup đã chốt DATA-01.
- Sửa ví dụ: phục hồi B **khi B vẫn còn trong hạn bảy ngày**, nhưng một phiên/lượt trong B đã quá hạn theo mốc gốc; không dùng câu “một tuần sau phục hồi B” dễ vượt hạn bản sao.
- Công cụ lấy DB–file nhất quán và restore đã được kiểm riêng; quyền/bảo vệ/lịch vận hành và RPO/RTO chưa nghiệm thu ở môi trường phục vụ. Không phê duyệt ngầm downtime hoặc thao tác restore ghi đè hệ thống đang chạy.

<a id="data-04"></a>

### 3.15. DATA-04 — Vòng đời metadata, phục hồi và backup tạm ngừng phục vụ

**Đã xác nhận ngày 04/11/2026**, căn cứ [8.1.17](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#data-04-source). Giữ mã LIFE-P01–P03/REC-P01 để truy vết; **BKP-P02** là mã cho hướng backup mới thay BKP-P01 online, không thêm phạm vi sao lưu.

- **LIFE-P01:** vé ký 24 giờ theo DATA-05, thay generation/context/10 phút; xem [3.16](#data-05).
<a id="life-p02"></a>

- **LIFE-P02 — đã duyệt:** cửa sổ tra lại/chống xử lý lặp lệnh Admin **24 giờ từ cấp**. Hết cửa sổ không tự thực hiện lại lệnh cũ; đối chiếu trạng thái thực trước thao tác tiếp. Không đổi thời hạn đăng nhập Admin, không tự gia hạn window khi login lại.
<a id="life-p03"></a>

- **LIFE-P03 — đã duyệt:** metadata job TTS cũ dọn sau **bảy ngày từ kết thúc rõ ràng**, khi không còn tham chiếu, không còn được sử dụng và không phải kết quả hiện tại cần giữ. Không áp job đang chạy/chưa rõ kết quả/liên kết cần thiết. Đây không là hạn xóa văn bản/audio; giữ chính sách file riêng.
<a id="rec-p01"></a>

- **REC-P01 — đã duyệt:** sau restore backup thật thu hồi quyền truy cập và window cũ trước mở phục vụ; khách xác nhận Bắt đầu phiên mới, Admin login/đối chiếu. Giữ lịch sử còn hạn và timestamp gốc, không endedAt giả/auto tạo phiên. Không áp vào deploy/restart thường, không tuyên bố xóa tức thì text/Blob đã tải. Người dùng hiểu dữ liệu đổi sau snapshot có thể mất.
<a id="bkp-p02"></a>

- **BKP-P02 — hướng đã chọn:** backup ngoài trình diễn, cuối ngày có thay đổi/trước deploy-schema theo DATA-03; chấp nhận tạm ngừng phục vụ, chưa thời lượng khi chưa đo. Thiết kế dừng app/ghi/dọn có kiểm soát, **MySQL còn chạy**, lấy DB/audio khớp; không tự gửi lại TTS, làm mất việc đã nhận hoặc đặt lại hạn phiên. Không MySQL tạm mỗi backup chỉ để catalog; restore thử vẫn môi trường riêng. Phạm vi/gói bảy ngày/lọc mốc gốc giữ nguyên. [Operations 3](../03-ky-thuat/operations-design.md#ops-backup) mô tả drain/job/checkpoint/mở lại; phạm vi đã kiểm riêng và phần còn thiếu ở báo cáo kiểm thử.
- Xác nhận này không duyệt toàn TDD/API/mô hình, không bằng chứng backup/restore/tab/audio/SSH/Docker đã hoạt động. R06 và các phần mở khác không đóng theo gói.

<a id="life-p01"></a>

<a id="data-05"></a>

### 3.16. DATA-05 / LIFE-P01 — Vé Bắt đầu có chữ ký, 24 giờ từ cấp

**Đã xác nhận ngày 04/11/2026**, căn cứ [8.1.18](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#data-05-source). Đây là vị trí chính LIFE-P01 hiện hành, thay đề xuất start_generation; giữ mã LIFE-P01 và mã API-K01/K11/TX-K01/TX-D01 để truy vết.

- Vé gắn requestId ngẫu nhiên và quyền của đúng tab, có chữ ký và hạn **24 giờ từ issuedAt**. Cấp/xử lý ở nền, không bước/màn hình/đồng hồ đếm ngược cho khách.
- Bỏ bảng start context, hạn 10 phút và start_generation phối hợp dọn phiên. **Giữ service_epoch cho REC-P01**, không đổi Admin session/cookie sang JWT.
- Bắt đầu thành công ghi requestId UNIQUE và thông tin đối chiếu cùng session trong một TX. Gửi lại đúng yêu cầu vì bấm lặp/mất phản hồi trả cùng ID/startedAt/expiresAt, không phiên mới; vé còn hạn không cho tạo nhiều phiên hoặc sửa tên/initialLanguage đã tạo.
- Có session tương ứng: kiểm signature/quyền/epoch và thông tin yêu cầu trước trả kết quả gốc; **vé hết hạn vẫn được tra kết quả đã tạo**, không tạo lại/gia hạn/hồi sinh session đã end/expired.
- Không session tương ứng: chỉ tạo lần đầu bằng vé còn hạn; vé hết hạn từ chối. Dữ liệu chống lặp giữ cùng D08; khi session bị dọn, vé cũ đã hết 24 giờ nên không tạo lại từ request đó.
- UI giữ đúng vé/requestId/quyền khi kết quả cũ chưa rõ, không tự cấp vé khác tạo phiên khác. Vé hết trước request đầu thì chuẩn bị vé mới nền, giữ tên/ngôn ngữ. Không xác định/tiếp tục được yêu cầu cũ: thông báo phù hợp, khách chủ động xác nhận lần mới; không khẳng định sai phiên đã hết hạn.
- Hạn vé không thay guest 8 giờ/D08 30 ngày/window lệnh Admin 24 giờ/login Admin 30 phút. LIFE-P02/P03/REC-P01/BKP-P02 giữ xác nhận DATA-04.
- Hướng đã chốt không đồng nghĩa toàn TDD/API/ERD được duyệt hoặc đã triển khai/kiểm. [Protocol hiện hành](../03-ky-thuat/operations-design.md#ops-start-simple), [ma trận và phạm vi kiểm chứng](../03-ky-thuat/operations-design.md#ops-start-checks), [chuẩn bị thực hành](../03-ky-thuat/TDD.md#tdd-layers).

<a id="dec-4"></a>

## 4. Quyết định khách — D, BS và P


<a id="d01"></a>

<a id="d01-bs"></a>

### 4.1. D01 / D01-BS — Vùng tương tác và truy cập dự phòng

**Điều chỉnh đã chốt ngày 30/10/2026:** quy tắc chọn/phát giữ chung cho các địa điểm; năm điểm D06 là bộ nghiệm thu. A02 mở rộng khả năng thêm điểm; vẽ/sửa vùng và điều kiện hiện điểm mới đã chốt tại A02.4/A02.5, hiệu lực sửa vùng tại A02.6 (mục 5.2).

- Sơ đồ minh họa có tên và các vùng tương tác; mỗi địa điểm có một vùng tương tác trên bản đồ. Khách chạm bất kỳ vị trí bên trong vùng thì chọn địa điểm, hiển thị văn bản và yêu cầu phát audio tương ứng ngôn ngữ đang chọn khi nội dung/audio sẵn sàng.
- Không yêu cầu chạm chính xác biểu tượng hoặc tên địa điểm; dùng vùng tương tác thay cho điểm bấm. Năm vùng là phần của bộ nghiệm thu, không phải giới hạn khả năng hệ thống.
- Chọn vùng địa điểm khác: dừng audio cũ, hiển thị nội dung mới, phát audio mới từ đầu theo ngôn ngữ đang chọn.
- Vùng không bắt buộc tròn; hình dạng/ranh giới cụ thể phải thiết kế và trình xem xét sau.
- Sơ đồ dùng được trên điện thoại, không chỉ đường thực tế. Không lấy vị trí thực của khách, không GPS/quyền vị trí; không suy ra phải dùng Google Maps hoặc công nghệ bản đồ cụ thể.
- Địa điểm không khả dụng: thông báo rõ và cho chọn vùng địa điểm khác. In đường dẫn trang bắt đầu kèm QR.
- Thiếu bản dịch, thiếu audio hoặc audio tải/phát lỗi áp dụng D03/D03-BS, không tự chuyển ngôn ngữ và không dùng bản chưa được duyệt theo D09.
- Bộ năm điểm chuẩn nghiệm thu áp dụng D06 do người dùng chọn, không phải giới hạn số điểm/vùng do giảng viên quy định. A02.4/A02.5 đã chốt vẽ vùng trong Admin và điều kiện hiện điểm mới. Bấm lại vùng, bấm ngoài, kéo/phóng và chồng lấn đã chốt theo P06–P08 tại mục 4.10; ranh giới/hình dạng vùng vẫn chờ thiết kế/xem xét.
- Chọn vùng là thao tác yêu cầu phát, khác với khôi phục trạng thái khi tải lại theo P03; không áp dụng quy tắc phát này cho đổi ngôn ngữ.

<a id="d02"></a>

<a id="d02-bs"></a>

### 4.2. D02 / D02-BS — Tên khách

- Tên bắt buộc trước khi bắt đầu, dài 1–50 ký tự hiển thị.
- Bỏ khoảng trắng đầu/cuối; gộp khoảng trắng liên tiếp giữa tên.
- Không nhận tên toàn khoảng trắng, xuống dòng hoặc ký tự điều khiển.
- Chấp nhận Unicode, số và dấu câu thông thường; không chỉ cho chữ cái.
- Tên chỉ để hiển thị và giúp Admin nhận biết phiên; cho phép trùng, không xác minh tên thật.
- Mỗi phiên được phân biệt độc lập với tên, không xác định người duy nhất.
- Muốn đổi tên thì kết thúc phiên hiện tại và bắt đầu phiên mới.
- Cách đo ký tự hiển thị và kiểm tra nhất quán để TDD cụ thể hóa; không phải lý do tự thay giới hạn hoặc siết thành tên chỉ có chữ.
- Chuẩn hóa khoảng trắng không được bỏ qua kiểm tra xuống dòng/ký tự điều khiển (giới hạn D02-BS).

<a id="d03"></a>

<a id="d03-bs"></a>

### 4.3. D03 / D03-BS — Ngôn ngữ và thiếu nội dung

- Màn hình bắt đầu chọn sẵn vi; khách có thể đổi trước khi bắt đầu.
- Thiếu bản dịch: thông báo rõ, chỉ liệt kê ngôn ngữ có văn bản được phép hiển thị để khách chủ động chọn; nếu không có thì cho quay lại bản đồ.
- Không tự chuyển ngôn ngữ; thể hiện đúng ngôn ngữ đang áp dụng qua bộ chọn chung theo UI-01.3. Không bắt buộc hai dòng nhãn thường trực cho văn bản/audio; xử lý nội dung cũ rõ khi chuyển.
- Có văn bản nhưng thiếu audio: vẫn cho đọc, hiển thị “Audio chưa sẵn sàng”, vô hiệu nút phát.
- Audio có nhưng tải/phát lỗi trong phiên còn hiệu lực: thông báo/thử lại từ đầu. Sau hết hạn không mở thêm quyền retry từ đầu; ngoại lệ nghe tiếp giới hạn theo P01/UI-02.
- Xử lý thiếu nội dung không làm giảm ma trận nội dung khi nghiệm thu D06.

**Bổ sung ranh giới D03 theo UI-04:** luồng thiếu/lỗi audio phía khách vẫn áp dụng khi cặp đã được áp dụng nhưng audio thực tế không sẵn sàng/tải-phát lỗi. Không dùng luồng này để cho phép Admin áp dụng cặp mới thiếu audio; giữ giới hạn phiên/thử lại P01/P04.

<a id="d04"></a>

### 4.4. D04 — Phiên, hết hạn và lượt xem

- **Đơn vị phiên:** mỗi tab một phiên; chưa yêu cầu đồng bộ phiên giữa các tab. Tab khác không mặc định dùng phiên đang có của tab trước. Tải lại chính tab tiếp tục phiên hiện tại nếu còn hiệu lực. Cách bảo đảm mô hình này khi nhân bản/khôi phục tab để TDD làm rõ.
- **Bắt đầu:** chỉ tạo khi thông tin hợp lệ và khách bấm “Bắt đầu tham quan” thành công.
- **Đổi lựa chọn:** đổi địa điểm/ngôn ngữ không tạo phiên mới.
- **Kết thúc chủ động:** có nút “Kết thúc tham quan”.
- **Hết hạn cố định:** thời điểm hết hạn = thời điểm bắt đầu + đúng 8 tiếng. Phiên chưa kết thúc chủ động sẽ hết hạn ở mốc này dù có hoạt động; tải lại, đổi địa điểm/ngôn ngữ không gia hạn. Không dùng thời điểm hoạt động cuối hoặc lúc audio phát hết để tính hết hạn. Nghe tiếp bài đang phát theo ngoại lệ [P01/UI-02](#p01-listen-tail) không làm phiên còn hiệu lực hoặc phát sinh lượt.
- **Đóng trang:** không coi là biết chính xác thời điểm kết thúc. Nếu không có kết thúc chủ động được ghi nhận thì mốc hết hạn vẫn tính từ lúc bắt đầu.
- **Phân biệt số phiên và số người:** không đồng nhất; tên trùng không gộp phiên và nhiều phiên không chứng minh có nhiều người.
- **Lượt xem:** chủ động mở địa điểm và tải thành công văn bản thì tính một lượt. Tải lại, đổi ngôn ngữ, nghe lại không cộng. Chuyển điểm khác rồi quay lại mở điểm cũ tính lượt mới.
- Theo quy tắc lượt xem đã đề xuất và được người dùng đồng ý: chỉ gặp thông báo thiếu bản dịch chưa là lượt xem; khi chọn ngôn ngữ có văn bản được phép hiển thị và tải thành công thì tính cho lần mở đó. Quay lại bản đồ rồi mở lại là lần mở mới; thử lại cùng lần mở không cộng lặp. Chống ghi nhận lặp để TDD xác định.
- **Ví dụ:** một phiên mở Cổng Ngọ Môn, đổi vi → en, tải lại rồi nghe lại vẫn là 1 phiên/1 lượt xem. Mở Điện Thái Hòa rồi quay lại Cổng Ngọ Môn thành 3 lượt xem. Tab khác có phiên riêng, không kết luận là một người khác.

**Trạng thái hiện hành:** D04 chốt mốc/trạng thái phiên; D04-BS chốt cảnh báo. P01–P03 nay đã chốt xử lý hết hạn, kết thúc/quay lại và khôi phục khi tải lại. Không còn yêu cầu xác nhận lại các hành vi này. Mốc thống kê và bộ lọc Admin nay đã chốt theo A06 tại mục 5.6; chỉ cách hiện thực sẽ được làm rõ ở TDD, không áp lại đề xuất chi tiết cũ ngoài xác nhận.

<a id="d04-bs"></a>

#### 4.4.1. D04-BS — Cảnh báo sắp hết hạn

- Giữ hạn cố định 8 tiếng từ lúc bắt đầu; không gia hạn theo hoạt động.
- Khi thời gian còn lại **trên 0 và bằng hoặc dưới 5 phút**, trong phiên còn hiệu lực, hiển thị: **“Phiên tham quan sẽ hết hạn trong vòng 5 phút”**.
- Cảnh báo nhẹ, không che nội dung, không ngắt audio, không buộc khách đóng hộp thoại để tiếp tục.
- Hiển thị một lần trong lần sử dụng trang hiện tại; không nhắc lặp liên tục. Khi tải lại hoặc quay lại tab trong khoảng này vẫn phải để khách nhận biết phiên sắp hết hạn; không coi việc đã từng hiển thị trước đó là lý do bỏ qua cảnh báo.
- Nếu đã hết hạn: box “Phiên tham quan đã hết hạn” trong khung bản đồ, không dùng cảnh báo sắp hết hạn; giữ đọc/cuộn và chỉ pause/resume bài đang phát lúc hết hạn theo P01/UI-02. Các quyền khác bị khóa; không dùng box này trước mốc 8 tiếng. Câu vi này đã được xác nhận ngày 01/11/2026, bản dịch vẫn phải rà soát.
- Cảnh báo không tự gia hạn hay tạo phiên mới; không tác động thanh tua hoặc các điều khiển đang dùng khi phiên còn hiệu lực.

| Trạng thái / thời gian còn lại | Hành vi đã chốt để đối chiếu khi nghiệm thu |
| --- | --- |
| Phiên còn hiệu lực, còn hơn 5 phút | Chưa hiển thị cảnh báo sắp hết hạn. |
| Phiên còn hiệu lực, còn đúng 5 phút hoặc ít hơn nhưng vẫn trên 0 | Hiển thị cảnh báo nhẹ với nội dung đã chốt; tiếp tục đọc/nghe bình thường. |
| Tải lại hoặc quay lại tab trong khoảng trên 0 đến 5 phút | Khách vẫn nhận biết được phiên sắp hết hạn; không nhắc lặp liên tục trong cùng lần sử dụng trang. |
| Đã hết hạn, kể cả khi lần trước chưa thấy cảnh báo | Hiển thị thông báo hết hạn; không hiển thị cảnh báo sắp hết hạn. |

**Khuyến nghị cách thể hiện, chưa chốt bố cục:** một dải thông báo nhỏ trong luồng trang, giữ nhìn thấy khi cần, không tự bật lại thành nhiều thông báo; khi quay lại tab chỉ bảo đảm dải hiện tại thể hiện đúng trạng thái. Không đề xuất thêm đếm ngược từng giây, hộp thoại bắt buộc hay nút gia hạn. Cách đo thời gian và xử lý tab ở nền sẽ cụ thể hóa ở TDD; bảng trên là quy tắc nghiệp vụ, chưa phải bằng chứng đã chạy kiểm thử.

<a id="d05"></a>

### 4.5. D05 — Vị trí ngoài phạm vi

Không lấy vị trí trực tiếp, không yêu cầu GPS hoặc quyền vị trí. Chỉ chọn vùng tương tác trên bản đồ minh họa; chưa thiết kế nhánh tọa độ thực. Vùng tương tác là phạm vi chạm trên sơ đồ, không phải vùng định vị địa lý của khách. Không cam kết sẽ bổ sung định vị ở phiên bản sau.

<a id="d06"></a>

### 4.6. D06 — Danh sách và ma trận nghiệm thu

**Bộ dữ liệu nghiệm thu và mức đầy đủ ĐÃ CHỐT; không phải giới hạn cứng số địa điểm hoặc xác nhận tài nguyên đã có/đã duyệt.** A02 cho phép thêm điểm qua Admin. Năm điểm bên dưới và 20 văn bản/20 audio vẫn là bộ chuẩn do người dùng chọn, không phải trần số lượng do giảng viên quy định; không tự tăng nội dung cho mọi điểm thêm hoặc thêm chức năng quản lý ngôn ngữ. Phạm vi ngôn ngữ vẫn vi/en/fr/ru.

| Địa điểm đã chọn | vi khi nghiệm thu | en khi nghiệm thu | fr khi nghiệm thu | ru khi nghiệm thu |
| --- | --- | --- | --- | --- |
| Cổng Ngọ Môn | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt |
| Điện Thái Hòa | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt |
| Tử Cấm Thành | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt |
| Cửu Đỉnh | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt |
| Cung Diên Thọ | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt | Văn bản + audio đã duyệt |

- Tổng mục tiêu: **20 bản văn bản và 20 audio**, vi/en/fr/ru tại mỗi điểm.
- Thứ tự thực hiện đã chốt: một điểm tiếng Việt → năm điểm tiếng Việt → bổ sung các ngôn ngữ còn lại. Không giảm phạm vi nghiệm thu.
- Đầu vào hiện hành: năm bài vi đã được tiếp nhận, 15 bản dịch nháp được lưu theo bundle v02. Bộ 20 cặp là mục tiêu nghiệm thu, không là mô tả dữ liệu đã duyệt.
- Hiện có 20 audio kỹ thuật được ghi nhận, gồm Ngọ Môn vi đã rà và 19 audio mới; kho 40 WAV/MP3 đã backup/restore riêng đạt. Rà nguồn, bản dịch, nghe trọn và hồ sơ D09 tiếp tục là điều kiện trước áp dụng; xem [ma trận nội dung](../05-noi-dung/doi-chieu-van-ban-audio.md).
- Không áp ngưỡng 100–150 từ như tiêu chí bắt buộc. P05: Tử Cấm Thành chỉ có một vùng và một bài tổng quan, không chia điểm con hoặc giảm phạm vi 20 cặp.

<a id="d07"></a>

### 4.7. D07 — Ngôn ngữ và điều khiển audio

- Cho đổi ngôn ngữ giữa phiên, giữ địa điểm và phiên.
- Một nút dùng chung Phát/Tạm dừng/Nghe lại theo [UI-01.2 / UI-P03](#ui-p03): biểu tượng/tên chức năng đúng trạng thái, không có nút Nghe lại thứ hai.
- Tạm dừng giữ vị trí; bấm Phát tiếp tục từ vị trí đó.
- Có thanh tua chọn vị trí trong bài; không cần nút nghe lại riêng.
- **Chọn vùng lần đầu hoặc chọn vùng địa điểm khác:** đồng thời yêu cầu phát audio khi sẵn sàng; khi đổi địa điểm dừng audio cũ, hiển thị nội dung mới và phát bài mới từ đầu theo ngôn ngữ đã chọn. Không yêu cầu bấm Phát riêng sau khi chọn vùng mới.
- **Đổi ngôn ngữ tại địa điểm đang xem:** giữ địa điểm/phiên, dừng bài cũ, đặt vị trí nghe về đầu và không tự phát; khách chủ động Phát. Chọn ngôn ngữ thay thế khi thiếu bản dịch cũng là hành động đổi ngôn ngữ, không tự trở thành chọn vùng.
- **Bấm lại vùng trong cùng lần mở:** xem đầy đủ quy tắc đã chốt tại [P06](#p06); cách đếm tại [D04](#d04).
- Chưa yêu cầu đổi tốc độ hoặc nghe nền.
- Thiếu/lỗi audio trong phiên còn hiệu lực theo D03-BS/P04, thử lại từ đầu; không dùng quy tắc đó để mở thêm quyền sau hết hạn. Tải lại tab còn hiệu lực theo P03: khôi phục mốc gần nhất cùng bài ở trạng thái tạm dừng, không yêu cầu đúng từng giây. Không đồng nhất hai hành động này.
- UI-P01/UI-P02/UI-P04 nay đã chốt đúng các nhánh tại [UI-02](#ui-guest-confirmed-02); tua/Nghe lại chỉ khi còn hiệu lực. Hết hạn chỉ pause/resume bài đủ điều kiện P01, không tua/Nghe lại hoặc khôi phục nghe sau tải lại.

<a id="d08"></a>

### 4.8. D08 — Dữ liệu, quyền xem và tự xóa

**Đã chốt:**

| Dữ liệu nghiệp vụ tối thiểu cần lưu | Mục đích trong phạm vi đã xác nhận |
| --- | --- |
| Mã phiên, tên hiển thị, ngôn ngữ ban đầu | Phân biệt phiên độc lập với tên; hiển thị/giúp Admin nhận biết phiên |
| Các mốc thời gian và trạng thái phiên | Xác định bắt đầu, kết thúc/hết hạn, phục vụ danh sách và thống kê; mốc hết hạn áp dụng D04 |
| Địa điểm, ngôn ngữ và thời điểm của lượt xem được tính, liên kết với phiên | Thống kê lượt xem và xóa dữ liệu liên quan theo phiên |
| Thông tin nhận biết địa điểm cũ độc lập với bản ghi địa điểm | A02.2 đã chốt để thống kê rõ “địa điểm đã xóa”, không gộp điểm mới trùng tên; vẫn theo hạn lưu D08, cách lưu trường/quan hệ thiết kế sau |

- Hệ thống hoạt động trực tuyến theo phạm vi đã chốt.
- Không đưa tọa độ khách, điện thoại, email hoặc lịch sử từng thao tác audio vào dữ liệu nghiệp vụ cần lưu.
- Admin đã đăng nhập được xem danh sách/thống kê. Khách chỉ cần thấy tên và lựa chọn đang dùng; chưa cần trang lịch sử phiên.
- Có chức năng **tự động xóa** dữ liệu phiên và lượt xem liên quan sau 30 ngày kể từ khi phiên kết thúc hoặc hết hạn.
- Phiên kết thúc chủ động: tính từ mốc kết thúc. Phiên hết hạn: tính từ mốc bắt đầu + 8 tiếng theo D04; không dùng hoạt động cuối + 30 phút, lúc tác vụ phát hiện phiên hoặc lúc audio phát hết. Nghe tiếp theo UI-02 không đổi mốc lưu/xóa 30 ngày.
- Thống kê từ dữ liệu này chỉ phản ánh phần còn giữ. Không suy ra báo cáo tổng khách mọi thời kỳ sau khi dữ liệu đã xóa.
- Chính sách chưa giải quyết log hạ tầng và bản sao lưu; cần làm rõ ở thiết kế kỹ thuật. Chưa có bằng chứng hệ thống đã hiện thực chính sách xóa.
- Cách ghi nhận/chống lặp lượt xem, lựa chọn mốc thời gian kỹ thuật chi tiết, nhận biết phiên theo tab, độ trễ tác vụ xóa và xử lý log/bản sao lưu để TDD xác định; không tự thêm trường bắt buộc như dấu vân tay hay lịch sử audio.
- Thông báo dữ liệu trước khi bắt đầu và các chính sách chi tiết về tài khoản khách/IP từ đề xuất cũ chưa được xác nhận thành yêu cầu mới; không cần giải quyết chúng để xác nhận các bước chọn điểm/đọc/nghe.

<a id="d09"></a>

### 4.9. D09 — Nội dung được phép đưa vào phần khách

- Theo dõi duyệt bằng tài liệu; chưa cần xây màn hình duyệt.
- Chỉ đưa vào phần khách phiên bản văn bản, bản dịch và audio được người dùng duyệt rõ ràng, có căn cứ nguồn và ghi nhận rà soát phù hợp.
- Khi sửa nội dung đã duyệt, rà soát lại phần bị ảnh hưởng trước khi dùng phiên bản mới.
- Audio phải được nghe trọn bài, khớp văn bản/ngôn ngữ đã duyệt và không trống/cắt cụt.
- Bản dịch chưa được người đủ năng lực rà soát phải ghi rõ giới hạn; chỉ dùng cho demo học phần khi người dùng duyệt rõ từng bản cùng giới hạn đó.
- AI hoặc dịch ngược không được coi là thẩm định chuyên gia.
- Chốt quy trình không duyệt sẵn nội dung. Không gọi lịch sử, bản dịch hoặc audio là đã được kiểm chứng khi chưa có bằng chứng tương ứng.

Để phân biệt trong tài liệu, **“bản nháp/chờ rà soát” không phải “đã được người dùng duyệt”**. UI-04/UI-AP05 đã chốt phần tham chiếu văn bản/audio trong Hồ sơ duyệt của cặp; mã phiên bản/mẫu hồ sơ bổ sung khác vẫn chuẩn bị riêng khi soạn hồ sơ nội dung; không tự chuyển toàn bộ chi tiết trong bảng trạng thái đề xuất cũ sang đã chốt.

<a id="dec-p"></a>

### 4.10. P01–P08 — Các tình huống đã chốt


| Mã | Tình huống | Nội dung ĐÃ CHỐT | Giới hạn / cách đối chiếu |
| --- | --- | --- | --- |
| <a id="p01"></a>P01 | Đủ 8 tiếng từ lúc bắt đầu. | Box “Phiên tham quan đã hết hạn”/“Bắt đầu phiên mới” chỉ trong khung bản đồ; toàn nền bản đồ làm mờ/khóa, không nút đóng/Hủy, không chặn toàn trang. Khóa vùng/ngôn ngữ/tua/Nghe lại và phát mới; bài đã tải vẫn đọc/cuộn. Chỉ bài đang phát lúc hết hạn được tiếp tục đến cuối, pause/resume chính bài đó; xong không Nghe lại. Mở form phiên mới dừng bài cũ, điền sẵn nếu hợp lệ/xác nhận lại P02. | UI-02/4.12.2 thay phần hết hạn luôn dừng/khóa toàn bộ audio. Phiên vẫn bắt đầu + 8 tiếng, không gia hạn/tạo mới/lượt mới; 30 ngày không tính từ hết bài. Không phục hồi ngoại lệ sau tải lại, không tự cấp tua/thử lại từ đầu hoặc quyền khác. Giữ cảnh báo 5 phút. |
| <a id="p02"></a>P02 | Quay lại phiên cũ hoặc chủ động kết thúc. | Hết hạn: Điền sẵn tên và ngôn ngữ nếu còn thông tin hợp lệ; khách được sửa và phải xác nhận trước khi tạo phiên mới. Chủ động kết thúc: dừng audio, xóa tên điền sẵn và trạng thái khôi phục trong tab; không xóa ngay bản ghi phục vụ Admin, vẫn lưu theo hạn 30 ngày. Không tìm thấy: thông báo “Không thể tiếp tục phiên trước. Vui lòng bắt đầu phiên mới”; không khẳng định đã hết hạn khi không có căn cứ. Phiên mới chỉ tạo khi bấm bắt đầu thành công, sau đó về bản đồ. Không tìm phiên bằng tên. | Phân biệt xóa thông tin khôi phục trong tab với tự xóa bản ghi nghiệp vụ theo D08. Không mặc định đã duyệt thêm việc điền sẵn hoặc xóa dữ liệu cho trường hợp không được nêu rõ. |
| <a id="p03"></a>P03 | Tải lại khi phiên còn hiệu lực. | Khôi phục địa điểm, ngôn ngữ và vị trí nghe gần nhất đã lưu của cùng bài; audio tạm dừng, không tự phát. Không yêu cầu chính xác từng giây. Không khôi phục được mốc hoặc audio đã thay đổi thì về đầu và thông báo phù hợp. Không cộng lượt xem; khôi phục lựa chọn không phải vừa chọn vùng. Chỉ giữ mốc cho phiên hiện tại. | Không thêm lịch sử nghe lâu dài hoặc đồng bộ giữa thiết bị/tab. Nếu địa điểm không khả dụng áp dụng D01-BS; nếu phiên không còn hiệu lực áp dụng P01/P02. |
| <a id="p04"></a>P04 | Mất mạng/lỗi dịch vụ. | Giữ nội dung/lựa chọn đang có; thông báo và cho thử lại. Không ép dừng audio nếu phần đã tải vẫn phát được. Audio tải/phát lỗi chỉ khi còn hiệu lực mới thử lại từ đầu theo D03-BS; sau hết hạn không mở quyền đó, chỉ ngoại lệ P01/UI-02 nếu còn phát được. Kết nối lại kiểm tra phiên trước tác vụ tiếp theo. Không cộng lặp lượt do thử lại, không gia hạn phiên. | Không thêm offline hoặc gửi bù lượt xem. Tải lại trang có thể khôi phục mốc theo P03; thử lại sau lỗi audio bắt đầu từ đầu. Phiên hết hạn vẫn áp dụng P01. |
| <a id="p05"></a>P05 | Tử Cấm Thành. | Giữ quyết định trước: một vùng tương tác mở một bài tổng quan; không tách thành địa điểm con hoặc thay đổi 20 văn bản/20 audio. | Chỉ duyệt phạm vi, không duyệt sẵn bài tổng quan hoặc ranh giới vùng. |
| <a id="p06"></a>P06 | Bấm lại vùng đang chọn trong cùng lần mở địa điểm. | Đang phát: tiếp tục, không quay về đầu. Đang tạm dừng: tiếp tục tại mốc giữ. Đã phát hết: phát lại từ đầu. Đang tải: không tạo yêu cầu tải/phát lặp. Không cộng lượt xem; thiếu/lỗi nội dung/audio theo D03/D03-BS. | Chuyển sang địa điểm khác rồi quay lại vẫn áp dụng quy tắc lượt xem D04; bấm lại để phát không phải một lượt xem mới trong cùng lần mở. |
| <a id="p07"></a>P07 | Bấm ngoài vùng hoặc thao tác di chuyển bản đồ. | Không đổi địa điểm, dừng hoặc phát lại audio; không cộng lượt, không tự chọn vùng gần nhất. Chưa chọn địa điểm thì giữ hướng dẫn nhẹ. Không hiện lỗi sau mỗi lần chạm ngoài. Phân biệt chạm chọn với kéo/phóng to/thu nhỏ; di chuyển bản đồ không được vô tình chọn vùng/phát audio. | Ghi đúng quy tắc phân biệt thao tác, không tự ấn định công nghệ bản đồ hoặc thêm điều hướng/GPS. |
| <a id="p08"></a>P08 | Chồng lấn vùng và chọn liên tục. | Vùng, nhãn, đường viền dễ phân biệt trên điện thoại. Chạm khớp nhiều vùng thì cho chọn trong danh sách ứng viên trước khi đổi nội dung/audio; không tự chọn tùy ý. Mỗi tab chỉ phát một audio. Chọn liên tục ưu tiên lựa chọn mới nhất; kết quả tải muộn của lựa chọn cũ không được thay nội dung hiện tại/phát chồng. | Hình dạng/ranh giới cụ thể các vùng chưa được duyệt; hành vi box ứng viên/nền khóa/Hủy/giữ audio/chuyển hết hạn đã bổ sung UI-P01/UI-02. Không đồng bộ tab hoặc tự duyệt cách đóng khác chưa nêu. |

<a id="ui-guest-confirmed"></a>

### 4.11. UI-01 — Xác nhận hướng giao diện khách

**Đã chốt đúng các phần dưới đây.** Phạm vi UI-01 được xác nhận sau PRD-01; [căn cứ nguyên văn](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/document-history.md#official-20261107-context-docs-02-nghiep-vu-decisions-md) được lưu để truy vết. Đối chiếu ui-design 0.1/PRD 0.4.1 và nguồn trước khi ghi nhận; không áp lại bản sửa đã hoàn tác. Xác nhận này không duyệt toàn bộ UI, UI-P01–UI-P04, nền/vị trí/ranh giới, bản dịch hoặc giải pháp kỹ thuật.

#### 4.11.1. UI-01.1 — Bố cục tham quan và phạm vi cuộn

- Trang bắt đầu riêng: tên/ngôn ngữ, chỉ mở trang tham quan khi bắt đầu thành công.
- Trang tham quan: đầu trang gọn gồm tiêu đề, tên khách, Kết thúc tách rõ bộ chọn “Ngôn ngữ”; vùng thông báo khi cần; khung bản đồ; tên điểm; điều khiển audio ngoài và phía trên box văn bản.
- Chỉ văn bản cuộn khi đọc trong box; bản đồ, tên điểm và audio vẫn nhìn thấy. Kéo/phóng bên trong bản đồ thay đổi bản đồ trong khung, không vô tình chọn vùng/phát audio theo P07.
- Điều chỉnh chiều cao theo điện thoại để còn đủ chỗ đọc, chưa đặt pixel bắt buộc. Chưa chọn điểm thì bài/audio hiện hướng dẫn, không nội dung giả.
- Thay hướng ui-design 0.1 cuộn cả trang/đưa bản đồ ra ngoài tầm nhìn khi đọc; không tự chốt bộ màu, kích thước, hình dạng vùng hoặc công nghệ.

<a id="ui-p03"></a>

#### 4.11.2. UI-01.2 / UI-P03 — Một nút Phát/Tạm dừng/Nghe lại

- **UI-P03 đã được xác nhận theo phương án mới này**, thay đề xuất cũ dùng nút Phát kèm dòng “Đã phát hết”.
- Chỉ một nút: đang phát là Tạm dừng; tạm dừng là Phát tiếp từ mốc giữ; kết thúc bình thường là biểu tượng Nghe lại dạng mũi tên vòng, thanh tua ở cuối.
- Bấm Nghe lại phát bài hiện tại từ đầu, nút thành Tạm dừng. Nếu đã hết bài rồi tua về trước cuối: tạm dừng ở mốc mới, nút thành Phát; Phát nghe từ đó.
- Không thêm nút Nghe lại thứ hai, dòng “Đã phát hết”, popup hoặc dải kết thúc bài. Nút biểu tượng có tên chức năng cho công cụ đọc màn hình; không bắt buộc chữ thường trực.
- P06 bấm lại vùng không đổi khi phiên còn hiệu lực; nghe lại/tua không cộng lượt. Hết hạn dùng P01 hiện hành/UI-02: chỉ pause/resume bài đang phát lúc mốc hết hạn, không tua/Nghe lại.
- UI-P01/UI-P02 áp dụng hành vi đã chốt tại UI-02, gồm Hủy chọn ứng viên và thanh tua khi còn quyền.

#### 4.11.3. UI-01.3 — Ngôn ngữ chung và phản hồi

- Một bộ chọn “Ngôn ngữ” áp dụng cho giao diện khách và nội dung/audio vi/en/fr/ru. Nhãn/tiêu đề/hướng dẫn/nút/thông báo chuyển theo ngôn ngữ; tên khách giữ nguyên. Không mở rộng sang Admin hoặc quản lý thêm ngôn ngữ.
- Bỏ hai dòng thường trực “Văn bản: …”, “Audio: …” và dòng “Trạng thái: Đang phát/…”. Bộ chọn thể hiện đúng ngôn ngữ đang áp dụng; khi chuyển, xử lý nội dung cũ rõ ràng để tránh nhầm, không để nhãn mới làm khách hiểu bài/audio cũ thuộc ngôn ngữ mới.
- Vẫn phản hồi tải/thiếu audio/lỗi và giữ thông báo bắt buộc. D03/D03-BS/D04-BS/P02 giữ câu tiếng Việt đã chốt làm căn cứ nghĩa; chuẩn bị bản dịch tương ứng, rà soát trước sử dụng. Không bắt giao diện en/fr/ru luôn hiện câu vi thay vì bản dịch được rà soát.
- Tên địa điểm/tiêu đề và thông báo dịch phải rà soát trước dùng; ví dụ “Cité impériale de Hué” chưa được duyệt. Xác nhận hướng đa ngôn ngữ không phê duyệt một bản dịch cụ thể hoặc chứng minh đã kiểm chứng.
- D03/D07 giữ không tự chuyển khi thiếu, chỉ ngôn ngữ có văn bản được phép để khách chọn thay thế; đổi ngôn ngữ giữ điểm/phiên, dừng/về đầu/không tự phát. Cách thể hiện chuyển tiếp cụ thể tại ui-design còn là thiết kế đề xuất.

#### 4.11.4. UI-01.4 — Bắt đầu/Kết thúc

- Có phản hồi đang xử lý và ngăn gửi lặp vì bấm nhiều lần cho cả hai thao tác.
- Không báo thành công khi chưa có căn cứ; phân biệt lỗi đã xác định với chưa biết kết quả. Lỗi bắt đầu giữ tên/ngôn ngữ để sửa/thử lại, không tự tạo thêm phiên.
- Kết thúc tách rõ khỏi bộ chọn ngôn ngữ. Giữ bắt đầu + 8 tiếng, cảnh báo 5 phút và các quy tắc phiên cũ.
- UI-P04 nay chốt box chặn toàn trang sau thành công theo UI-02, thay đề xuất màn riêng; không thêm hộp xác nhận trước kết thúc. Cơ chế xác định/chống lặp khi chưa rõ kết quả vẫn để TDD, không coi chưa rõ là thất bại để tạo phiên khác.

#### 4.11.5. UI-01.5 — Giới hạn kế thừa và bản đồ đề xuất

- Đầu trang gọn, tránh chia cột hẹp/xuống dòng nhiều. Không nút quét QR trong trang đã vào, không đặt form bắt đầu dưới bản đồ lớn; bỏ “Khoảng cách tới tâm”.
- Không đưa mã phiên/thông tin kỹ thuật/mô tả nội bộ dài khi không phục vụ thao tác. Không dùng cảnh báo “nội dung dự thảo” thay gate chỉ phục vụ bản được phép.
- Chỉ kế thừa ý tưởng/bài học, không mặc định sao chép code/dữ liệu/nội dung tham khảo.
- Được giao tạo phác thảo năm vùng có nền/dẫn nguồn/vị trí tương đối, vùng đa giác phù hợp, chạm toàn vùng; hạn chế chồng lấn, nếu còn thì danh sách P08, dấu chọn không chỉ màu. Ghim nếu dùng là điểm đang chọn, không phải vị trí khách.
- **Chưa duyệt nền/vị trí/ranh giới cụ thể** hoặc nhà cung cấp bản đồ qua việc duyệt bố cục. Bản phác thảo, ảnh và dữ liệu vùng trong [ui-design](../04-giao-dien/ui-design.md#ui-map) là đề xuất để xem xét; không cấu hình ứng dụng, không tọa độ/vị trí thực của khách. Năm điểm vẫn là bộ chuẩn, không phải trần hệ thống.

<a id="ui-guest-confirmed-02"></a>

### 4.12. UI-02 — Box khách và ngoại lệ nghe tiếp khi hết hạn — 01/11/2026

**Đã chốt đúng các hành vi dưới đây.** UI-02 xác nhận thay đổi có chủ đích so với P01 cũ; [căn cứ nguyên văn](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/document-history.md#official-20261107-context-docs-02-nghiep-vu-decisions-md) được lưu để truy vết. Giữ mã P01/UI-P01–UI-P04; UI-02 là mã bổ sung, không thay mã yêu cầu cũ. Phạm vi xác nhận không đồng nghĩa duyệt mọi chi tiết thiết kế, cách đóng chưa nêu, UI Admin, bản đồ, nội dung, TTS hoặc kỹ thuật.

<a id="ui-p04"></a>

#### 4.12.1. UI-P04 — Box sau chủ động kết thúc

- Chỉ sau khi có căn cứ kết thúc thành công: box nổi **chặn toàn trang ứng dụng**, “Đã kết thúc tham quan” và duy nhất nút “Bắt đầu phiên mới”.
- Không Hủy/nút đóng; chạm ngoài hoặc Esc không đóng; nền toàn trang làm mờ, không nhận thao tác. Audio đã dừng, tên điền sẵn và trạng thái khôi phục tab xóa theo P02.
- Nút về **UI-K01 — trang bắt đầu riêng**, tên trống/vi mặc định. Chỉ thông tin hợp lệ và Bắt đầu tham quan thành công mới tạo phiên; không tự tạo.
- Không hộp hỏi xác nhận trước kết thúc. Chặn chỉ trong ứng dụng, không hứa ngăn đóng tab/điều khiển trình duyệt. Tải lại/quay lại không hồi sinh phiên đã kết thúc.
- Thay đề xuất màn hình kết thúc riêng ở UI 0.2; khác cách trình bày tự hết hạn dưới đây.

<a id="p01-listen-tail"></a>

#### 4.12.2. P01 bổ sung — Box hết hạn trong bản đồ và nghe hết bài đang phát

- Đúng **bắt đầu + 8 tiếng**, phiên hết hạn dù còn nghe; box **“Phiên tham quan đã hết hạn”** và **“Bắt đầu phiên mới”** nằm trong khung bản đồ. Toàn nền bản đồ làm mờ/khóa, không chỉ phần bị box che; không nút đóng/Hủy.
- Không làm mờ/chặn toàn trang. Tên điểm, phần đọc và điều khiển audio vẫn nhìn thấy; văn bản đã tải vẫn cuộn đọc. Khóa chọn địa điểm/ngôn ngữ.
- **Ngoại lệ duy nhất:** bài đang phát tại thời điểm hết hạn được tiếp tục tới cuối; được tạm dừng/tiếp tục chính bài đó. Ngoài nhóm này không được bắt đầu bài chưa phát, tua, nghe lại hoặc chuyển/tự phát bài khác sau hết hạn. Bài đã tạm dừng trước mốc hết hạn không tự được cấp quyền nghe tiếp theo ngoại lệ này.
- Hết bài thì không cho Nghe lại trước phiên mới. P06/tua/Nghe lại của phiên còn hiệu lực không vượt quyền hết hạn; không được bấm lại vùng để mở quyền phát.
- Rời phần nghe để mở form **Bắt đầu phiên mới** phải dừng bài cũ trước chuyển form; không để audio cũ đi sang phiên mới. UI-K01 điền sẵn tên/ngôn ngữ nếu còn hợp lệ, được sửa và phải xác nhận Bắt đầu thành công.
- Không gia hạn/tạo phiên hoặc lượt xem mới bởi nghe tiếp/pause/resume. D08 vẫn xóa sau 30 ngày từ hết hạn cố định hoặc kết thúc chủ động, **không từ lúc audio phát hết**.
- Không mở rộng ngoại lệ thành khôi phục nghe sau tải lại, quyền thử lại từ đầu sau lỗi, hoặc các tình huống khác chưa xác nhận. P03 chỉ khôi phục nghe cho phiên còn hiệu lực; sau tải lại phiên hết hạn không được nhận lại ngoại lệ bằng mốc nghe đã lưu.
- Cảnh báo trên 0 đến 5 phút vẫn nhẹ theo D04-BS, không dùng box hết hạn thay cảnh báo trước mốc. Các câu vi làm căn cứ bản dịch theo UI-01.3; chưa duyệt bản dịch cụ thể.

<a id="ui-p01"></a>

#### 4.12.3. UI-P01 — Box chọn ứng viên chồng lấn

- Box nhỏ nổi trên trang, đúng danh sách ứng viên và nút **Hủy**; khi mở nền không nhận thao tác.
- Audio cũ đang phát tiếp tục, đang tạm dừng giữ tạm dừng. Hủy chỉ đóng box, không đổi điểm/bài, không tua/phát lại hoặc cộng lượt.
- Chọn ứng viên theo quy tắc chọn vùng/P06, kể cả ứng viên là địa điểm đang xem; không mặc định mọi lựa chọn đều phát từ đầu.
- Mở/hủy danh sách không cộng lượt. Hết hạn khi box mở: đóng danh sách, chuyển trạng thái 4.12.2; không nhận lựa chọn điểm mới, audio đang phát được xét đúng ngoại lệ P01 mới.
- Không tự suy rộng nút Hủy thành đã chốt tất cả cách đóng bằng Esc/nút quay lại/chạm ngoài; bố trí chi tiết còn thiết kế.

<a id="ui-p02"></a>

#### 4.12.4. UI-P02 — Tua khi phiên còn hiệu lực

- Đang phát: tua tới mốc mới thì phát tiếp từ đó khi audio sẵn sàng.
- Đang tạm dừng: tua tới mốc mới vẫn tạm dừng, khách bấm Phát mới nghe.
- Sau hết bài: tua về trước cuối thì tạm dừng ở mốc mới, nút thành Phát; Nghe lại từ đầu theo UI-P03 đã chốt.
- Các nhánh này chỉ cho phiên còn hiệu lực; hết hạn chỉ pause/resume đúng bài đủ điều kiện 4.12.2, không tua/Nghe lại.

#### 4.12.5. Phụ thuộc kỹ thuật — tải trọn MP3 theo TECH-04

TECH-04 chọn tải trọn MP3 trước phát và giữ cho tab, chỉ đúng bài đang phát tại mốc được pause/resume; đang tải/chưa phát/pause trước mốc hoặc phản hồi muộn không được grant. TDD hiện hành phải kiểm vòng đời Blob/hủy tải/reload/owner/tab nền, không tải thêm A1 từ server sau expiry. Giữ 8 giờ bảo vệ file, 8 giờ phiên/30 ngày/lượt gốc; không gia hạn file hoặc giữ vô hạn. Không phục hồi ngoại lệ sau reload/đóng tab, không thêm kho offline/nghe nền hoặc retry từ đầu sau expiry. Hướng được chọn không chứng minh browser thực đã đáp ứng, chưa full API/test hoặc TDD toàn bộ được duyệt.

<a id="dec-5"></a>

## 5. Quyết định Admin — A và các mục con

**Luồng Admin áp dụng BF-A01 và A01–A07; A02.1 cụ thể hóa cơ chế xóa do ứng dụng điều phối, không tạo cơ chế xóa khác.

<a id="a01"></a>

### 5.1. A01 — Đăng nhập và phiên Admin

Đã chốt: một vai trò Admin, tài khoản cấp trước, đăng nhập/đăng xuất; chưa đăng ký công khai/phân quyền nhiều cấp. Phiên Admin 30 phút không hoạt động, độc lập khách 8 tiếng; hết phiên thông báo, bảo vệ nội dung chưa lưu, không báo lưu thành công giả.

**Bổ sung hiện hành:** [Q2](#q2) đã chốt phạm vi giữ tạm/khôi phục; [Q3](#q3) đã chốt loại hoạt động và mốc 30 phút. [UI-03/UI-AP03](#ui-admin-confirmed-01) xác nhận hướng box đăng nhập lại che nội dung và bảo vệ công việc, không đổi Q2/Q3. [UI-04/UI-AP07](#ui-admin-confirmed-02) bổ sung hết phiên phải dừng nghe thử và che nội dung, đăng nhập lại không tự phát/gửi lại; TTS đã nhận vẫn tiếp tục. Cơ chế lưu tạm/ghi nhận sự kiện vẫn để thiết kế kỹ thuật, không chọn công nghệ qua xác nhận này.

<a id="a02"></a>

### 5.2. A02 — Vòng đời địa điểm

Đã chốt thêm/sửa/ẩn-hiện/xóa; ứng dụng điều phối phụ thuộc trước xóa điểm, Admin có xác nhận, database giữ FK chặn trực tiếp khi còn tham chiếu, không CASCADE ở quan hệ cần chặn; một giao dịch database thành công toàn bộ/hoàn tác. A02.1–A02.6 đã cụ thể hóa dữ liệu riêng/dùng chung, giữ lịch sử, dọn file, vẽ vùng trực tiếp, điều kiện hiện điểm mới và hiệu lực sửa vùng.

**Bổ sung hiện hành:** [Q1](#q1) cụ thể hóa thông tin địa điểm hợp lệ, mã/tên và cảnh báo trùng; [Q2](#q2) bảo vệ biểu mẫu/vùng chưa lưu. Không phải giới hạn cứng năm điểm/vùng; không tuyên bố ngăn mọi quyền database. Bố cục/thao tác vẽ/ranh giới cụ thể chưa duyệt; hiệu lực sửa vùng đã chốt tại A02.6.

<a id="a02-1"></a>

#### 5.2.1. A02.1 — Xử lý dữ liệu riêng và giao dịch xóa

**Trạng thái:** A02.1 là quy tắc đã chốt; việc xóa DB và dọn file là hai bước có điều kiện riêng.

**Nội dung hiện hành:**

1. Sau khi Admin chọn Xóa và xác nhận theo A02, ứng dụng xử lý dữ liệu phụ thuộc thuộc riêng địa điểm trước khi xóa địa điểm: văn bản/bản dịch, bản nháp, bản ghi audio và liên kết vùng.
2. Không xóa nhầm dữ liệu dùng chung. Xử lý các tham chiếu có kiểm soát để giữ toàn vẹn quan hệ; phiên/lượt xem và thông tin nhận biết địa điểm cũ vẫn được giữ theo [A02.2](#a02-2)/[D08](#d08), không xóa toàn bộ lịch sử hoặc gộp với điểm mới trùng tên.
3. Vô hiệu hóa liên kết với các yêu cầu tạo audio còn liên quan; kết quả đến muộn không được khôi phục địa điểm đã xóa. File phát sinh không còn dùng được xử lý theo [A02.3](#a02-3)/[A04.4](#a04-4); không hứa dịch vụ bên ngoài luôn hủy được yêu cầu.
4. Không tự xóa hồ sơ duyệt trong tài liệu.
5. Database giữ khóa ngoại/quy tắc chặn xóa địa điểm trực tiếp khi còn dữ liệu tham chiếu; không dùng `ON DELETE CASCADE` tại các quan hệ cần chặn.
6. Mọi thay đổi database trong quy trình nằm trong **một giao dịch: thành công toàn bộ hoặc hoàn tác**. Dọn file tách khỏi giao dịch database và chỉ thực hiện sau khi phần database thành công theo A02.3.
7. Mục đích là bảo vệ toàn vẹn quan hệ, không tuyên bố ngăn mọi người có quyền database xóa dữ liệu.


**Giới hạn / thiết kế sau:** Thứ tự xử lý theo quan hệ dữ liệu thực tế, cách lưu thông tin lịch sử, schema/lệnh và bằng chứng kiểm tra để bước kỹ thuật xác định; không thay đổi quy tắc đã duyệt hoặc tuyên bố đã thực hiện xóa/kiểm thử.

<a id="a02-2"></a>

#### 5.2.2. A02.2 — Giữ lịch sử địa điểm đã xóa

**Đã chốt:** Giữ phiên/lượt xem đến hết hạn D08. Lưu thông tin nhận biết điểm cũ độc lập với bản ghi địa điểm để thống kê rõ “địa điểm đã xóa”; không gộp với điểm mới chỉ vì trùng tên. Xử lý tham chiếu khi xóa theo quy trình có kiểm soát.

**Giới hạn / thiết kế sau:** Cách lưu trường/liên kết để TDD thiết kế; không tự giữ dữ liệu quá hạn 30 ngày hoặc gọi số phiên là số người.

<a id="a02-3"></a>

#### 5.2.3. A02.3 — Dọn file audio riêng với database

**Đã chốt:** Dọn file tách khỏi xóa bản ghi; chỉ sau database thành công và chỉ file không còn tham chiếu/dùng chung. Giữ khoảng bảo vệ 8 tiếng từ lúc ngừng áp dụng cuối cùng. Dọn lỗi có trạng thái chờ/thử lại; không báo file đã xóa khi mới xóa bản ghi.

**Giới hạn / thiết kế sau:** Không gia hạn phiên khách; giao dịch database không bao trùm thao tác file. Cách lên lịch/kiểm tra lại tham chiếu và xử lý file chưa từng áp dụng sẽ thiết kế theo nguyên tắc đã chốt, không tự tuyên bố có bằng chứng đáp ứng.

<a id="a02-4"></a>

#### 5.2.4. A02.4 — Vẽ và sửa vùng trực tiếp

**Đã chốt:** **Admin vẽ vùng trực tiếp trên bản đồ, xem trước và lưu cho điểm mới; hỗ trợ sửa/vẽ lại.** Thay phương án chỉ nhận/gắn cấu hình vùng chuẩn bị ngoài Admin. Hướng thao tác một đa giác kín đơn giản và thứ tự xem/lưu/áp dụng đã được xác nhận bổ sung theo [UI-03](#ui-admin-confirmed-01); ranh giới/công nghệ chưa duyệt.

**Giới hạn / thiết kế sau:** Không mở thành bộ công cụ bản đồ phức tạp. Hướng UI-AP01–UI-AP03 đã xác nhận tại UI-03; chi tiết bố cục/đồ họa và ranh giới cụ thể vẫn phải xem xét riêng. Hiệu lực bản sửa vùng đang hiển thị đã chốt riêng tại A02.6: Lưu bản sửa khác Áp dụng.

<a id="a02-5"></a>

#### 5.2.5. A02.5 — Điều kiện hiển thị điểm mới

**Hiện hành theo UI-04:** điểm mới mặc định chưa hiện. Chỉ bật khi thông tin hợp lệ, vùng hợp lệ đã được xem xét/áp dụng và ít nhất **cặp văn bản/audio tiếng Việt đã được duyệt và áp dụng**. Thiếu cặp vi thì chưa bật; các ngôn ngữ khác thiếu xử lý D03/D03-BS, không tự đổi. Vẫn lưu được điểm ẩn thiếu vùng/bài/audio. Năm điểm × bốn ngôn ngữ là bộ nội dung người dùng chọn, không trần số lượng. Gate cũ chỉ cần văn bản vi chuyển sang lịch sử 7.6.

**Giới hạn / thiết kế sau:** Giữ 20 văn bản/20 audio cho bộ này; không tự tăng bài chuẩn cho mọi điểm thêm và không bổ sung quản lý thêm ngôn ngữ. Bốn ngôn ngữ vi/en/fr/ru giữ nguyên phạm vi.

<a id="a02-6"></a>

#### 5.2.6. A02.6 — Hiệu lực sửa vùng


- Khi sửa vùng của địa điểm đang hiển thị, giữ vùng hiện hành cho khách.
- **“Lưu bản sửa” không đồng nghĩa “Áp dụng”.** Có bước xem trước, xem xét bản sửa rồi chủ động áp dụng; không cần duyệt nhiều cấp.
- Sau khi áp dụng, khách nhận vùng mới ở **lần tải bản đồ tiếp theo**.
- Không đẩy thay đổi tức thì, ngắt audio hoặc ép kết thúc phiên khách.
- [UI-03/UI-AP02](#ui-admin-confirmed-01) làm rõ: Vẽ/sửa → Xem trước → Lưu bản sửa → Áp dụng đúng bản đã xem. Lưu không đổi hình đã xem thì không bắt xem lại; sửa tiếp/bản lưu khác thì phải lưu và xem lại đúng bản, không âm thầm lưu khi áp dụng.

Cách lưu thông tin lịch sử/quan hệ, thứ tự chi tiết, lịch chạy dọn và xử lý file chưa từng áp dụng sẽ thiết kế theo nguyên tắc đã chốt; không tự gán mốc ngừng áp dụng chưa có căn cứ. Giao dịch database không bao trùm thao tác file. Chưa thao tác schema/database hoặc kiểm thử qua tài liệu.

<a id="a03"></a>

### 5.3. A03 — Văn bản và bản dịch

Đã chốt quản lý theo điểm/ngôn ngữ; nháp khác áp dụng; sửa bản đang dùng tạo nháp thay thế, giữ cặp hợp lệ cho khách. UI-04 yêu cầu **chỉ áp dụng cả cặp văn bản/audio khớp**, không áp dụng riêng. Admin đối chiếu hồ sơ duyệt của cặp: hai tham chiếu riêng, có thể cùng mục nếu chỉ rõ cả hai; ghi nhận không tự duyệt/áp dụng. UI-AP04/UI-AP05 chốt hướng soạn/đối chiếu và bảo vệ phần tham chiếu trong công việc theo giới hạn Q2. Có theo dõi mức đầy đủ, không coi nháp/file sinh ra là hoàn thành.

**Giới hạn:** Không coi phần mềm tự biết phê duyệt bên ngoài; chưa tự dịch, duyệt nhiều cấp/lịch sử phiên bản phức tạp.

<a id="a04"></a>

### 5.4. A04 — Tạo audio từ văn bản

Đã chốt tạo audio từ văn bản và A04.1–A04.4: lưu → chủ động tạo → trạng thái/kiểm tra yêu cầu cũ/thử lại → nghe thử → đối chiếu duyệt/áp dụng; kết quả gắn đúng bản/ngôn ngữ, bản cũ hợp lệ không bị hỏng. Tác vụ đã nhận tiếp tục qua hết phiên/đóng trang; kết quả muộn của điểm đã xóa không phục hồi điểm.

**Giới hạn hiện hành:** công nghệ nền TECH-01, model/giọng TTS-01; WAV/MP3/FFmpeg-Linux đã chốt IN-P03. Ngân sách trả phí/ngưỡng/tích hợp và chất lượng thực tế vi/en/fr/ru chưa chốt/kiểm chứng. Không tự thêm luồng tải file độc lập hoặc lịch sử phiên bản phức tạp.

<a id="a04-1"></a>

#### 5.4.1. A04.1 — Chủ động tạo và nghe thử

**Đã chốt:** Admin lưu văn bản rồi chủ động bấm “Tạo audio”, không tự tạo sau mọi lần lưu. Được tạo/nghe thử từ nháp trong Admin; yêu cầu gắn đúng bản văn và ngôn ngữ.

**Giới hạn / thiết kế sau:** Nghe thử nháp không đồng nghĩa phát cho khách. TECH-01/TTS-01 và IN-P03 giữ nền/model/giọng/WAV-MP3/Linux đã chọn; ngân sách trả phí/ngưỡng/cơ chế và tích hợp thực chưa chốt/kiểm chứng.

<a id="a04-2"></a>

#### 5.4.2. A04.2 — Trạng thái và thử lại

**Đã chốt:** Bốn trạng thái: chưa tạo, đang tạo, thành công, lỗi. Đang tạo không nhận yêu cầu bấm lặp; lỗi cho thử lại chủ động. Chưa rõ kết quả thì kiểm tra yêu cầu cũ trước khi tạo mới.

**Giới hạn / thiết kế sau:** Không tự tạo lại liên tục hoặc báo chắc đã lỗi/thành công khi chưa có căn cứ. Ngưỡng chờ và cách kiểm tra để thiết kế kỹ thuật.

**Diễn giải bốn trạng thái trong cùng quyết định A04.2:**

| Trạng thái tạo | Hành vi đã chốt | Không được suy ra |
| --- | --- | --- |
| Chưa tạo | Chỉ tạo sau khi Admin lưu văn bản rồi chủ động yêu cầu. Không tự tạo sau mọi lần lưu. | Nháp đã được phép dùng cho khách hoặc audio cũ khớp bản mới. |
| Đang tạo | Không nhận bấm lặp; nếu chưa rõ kết quả thì kiểm tra yêu cầu cũ trước khi tạo mới. | Mất kết nối/chờ lâu chứng minh đã lỗi, hoặc có thể tạo trùng không kiểm soát. |
| Thành công | Kết quả chỉ thuộc bản văn/ngôn ngữ lúc tạo; có thể nghe thử để rà soát. | Được người dùng duyệt, đã nghe trọn hoặc đã áp dụng. |
| Lỗi | Thử lại chủ động, giữ bản hợp lệ đang phục vụ khách. | Được thay bản đang phục vụ bằng kết quả lỗi hoặc audio sai phiên bản. |

<a id="a04-3"></a>

#### 5.4.3. A04.3 — Audio gắn đúng bản văn

**Hiện hành theo UI-04:** audio chỉ thuộc bản văn/ngôn ngữ lúc tạo; không gắn kết quả cũ sang bản sửa. Chỉ áp dụng cả cặp đã lưu/được duyệt, audio đã nghe trọn/khớp theo D09; chưa đủ audio thì chưa áp dụng cặp mới. Giữ cặp cũ khi tạo mới/lỗi hoặc áp dụng thất bại xác định; chưa rõ kiểm tra trạng thái thực tế trước thử lại. Không có cặp cũ thì chưa phục vụ nội dung mới trước thành công. Chỉ đổi audio dùng văn bản hiện tại không đổi cùng audio mới đã duyệt, không bắt duyệt lại văn bản. Cặp đã áp dụng nhưng audio thực tế thiếu/tải-phát lỗi vẫn đọc văn bản và xử lý D03/BS/P04 đúng quyền phiên; không phát bài cũ hoặc khác ngôn ngữ. Thành công tạo, được duyệt và đã áp dụng vẫn riêng.

**Giới hạn / thiết kế sau:** Chưa cần lịch sử phiên bản phức tạp. Giữ D03/D09 và A03/A07; không coi tạo thành công là nội dung đã được người dùng duyệt.

<a id="a04-4"></a>

#### 5.4.4. A04.4 — Tác vụ đã được tiếp nhận

**Đã chốt:** Yêu cầu đã được tiếp nhận tiếp tục xử lý khi Admin hết phiên/đóng trang; đăng nhập lại xem kết quả, không tự áp dụng. Điểm đã xóa thì bỏ kết quả muộn, không phục hồi điểm; file không dùng được đưa vào cơ chế dọn phù hợp.

**Giới hạn / thiết kế sau:** Không hứa dịch vụ ngoài luôn hủy được yêu cầu. Bảo vệ nội dung chưa lưu theo A01 vẫn giữ; không đồng nhất nháp chưa lưu với tác vụ đã tiếp nhận.

Gemini/model/Charon theo TTS-01, nền TECH-01; IN-P03 đã chọn WAV/MP3/FFmpeg-Linux. Ngân sách trả phí, bitrate/ngưỡng chờ và giới hạn kỹ thuật còn mở. Chưa kiểm chứng tích hợp/chất lượng đủ vi/en/fr/ru cho repo chính. Không tự gọi lịch sử/bản dịch/lời đọc/phát âm là đã kiểm chứng khi chưa có bằng chứng D09; không tự giảm bộ nội dung/ngôn ngữ chuẩn nếu dịch vụ không đáp ứng.

<a id="a05"></a>

### 5.5. A05 — Một QR chung

Đã chốt một QR chung vào trang bắt đầu cho mọi điểm kể cả thêm sau; giữ đường dẫn dự phòng; không QR riêng, thống kê lần quét hoặc dịch vụ chuyển hướng.

**Bổ sung UI-05/UI-AP10:** trang có một QR/URL chỉ đọc, tải PNG, bản in gọn tên hệ thống+QR+URL dự phòng. Chưa có URL thật thì chưa sẵn sàng/không QR giả; mở hộp in/yêu cầu tải không chứng minh in/lưu thành công. URL/thư viện và kiểm tra khả năng quét còn công việc sau; thêm điểm không tạo QR riêng.

<a id="a06"></a>

### 5.6. A06 — Phiên và thống kê

Đã chốt danh sách chỉ đọc, lọc ngày/trạng thái/tên-mã; mặc định hôm nay Asia/Saigon. Phiên theo lúc bắt đầu, lượt xem theo lúc xem/điểm/ngôn ngữ ghi nhận; chú thích cách tính/hạn lưu 30 ngày, không người duy nhất/lượt nghe, không sửa/gia hạn thay khách.

**Bổ sung UI-05/UI-AP08/UI-AP09:** xem chi tiết trong box UI-A21 trên danh sách, nền không thao tác, có Đóng/cuộn trong box; đóng giữ lọc, không trang riêng. Từ/Đến trọn cả hai ngày Asia/Saigon, mặc định hôm nay; hai bảng/tổng riêng, không cộng số phiên với lượt. Về hôm nay đổi hai ngày, danh sách giữ trạng thái/tên-mã. Không thêm lọc điểm/ngôn ngữ hoặc chức năng quản lý phiên.

**Giới hạn:** A02.2 giữ nhóm điểm đã xóa độc lập bản ghi gốc; trạng thái “còn hiệu lực” không chứng minh khách đang hiện diện. Thống kê chỉ dữ liệu còn giữ, không kho vô hạn; xem nguồn chi tiết UI-05.

<a id="a07"></a>

### 5.7. A07 — Hiệu lực thay đổi nội dung

Đã chốt đổi nội dung/tạm ẩn hiệu lực lần tải/chọn tiếp theo, không đẩy ngay/ép kết thúc; phần hợp lệ đã tải tiếp tục trong hạn phiên, thêm ngoại lệ nghe hết đúng bài đang phát lúc hết hạn theo P01/UI-02. Audio đổi không dùng mốc bài cũ. Tạm ẩn không thu hồi tức thì; ngoại lệ không cho tải/chọn bài khác hoặc phục hồi sau tải lại.

**Bổ sung UI-04:** áp dụng cả cặp nhất quán, hiệu lực lần tải/chọn tiếp theo; không phát hành một nửa. Cặp đã áp dụng nhưng audio thực tế lỗi vẫn giữ đọc theo D03/BS. Không thay quyền nghe tiếp khách P01/UI-02.

**Giới hạn:** Dọn file theo A02.3, không thay hạn 8 tiếng/lưu 30 ngày. Bản sửa vùng áp dụng theo A02.6, khách nhận ở lần tải bản đồ tiếp theo; lưu bản sửa không áp dụng ngay.

<a id="dec-q"></a>

### 5.8. Q1–Q3 — Xác nhận bổ sung hiện hành

**Đã xác nhận ngày 31/10/2026.** Q1–Q3 tại PRD 0.3 được phê duyệt trong phạm vi mô tả, có [căn cứ nguyên văn](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/document-history.md#official-20261107-context-docs-02-nghiep-vu-decisions-md). Nội dung hiện hành thay phần đề xuất Q1–Q3, không thay các quyết định D/P/A/G/BF khác.

Q1–Q3 giữ mã dùng khi phân tích để truy vết; là xác nhận bổ sung liên kết các quyết định cha, không tạo lại cơ chế xóa hoặc mô hình phiên Admin. **Tại thời điểm xác nhận Q1–Q3, chỉ ba nội dung này được duyệt; phạm vi PRD nay đã được xác nhận riêng theo PRD-01.** Bản đề xuất 0.3 được lưu nguyên tại mục 7.3 để tra lịch sử, không áp dụng như bộ yêu cầu song song.

<a id="q1"></a>

#### 5.8.1. Q1 — Thông tin địa điểm hợp lệ — ĐÃ CHỐT

**Liên kết:** A02, A02.2/A02.4/A02.5/A02.6; quản lý văn bản/audio theo A03/A04.

**Nội dung đã chốt:** Tên địa điểm là trường Admin bắt buộc nhập. Mã địa điểm do hệ thống cấp, độc lập với tên; trạng thái hiển thị và các quan hệ vùng/văn bản/audio được quản lý theo nghiệp vụ tương ứng. Chưa đề xuất thêm trường mô tả thuyết minh, ảnh hoặc tọa độ ở biểu mẫu thông tin cơ bản.

| Trường / quan hệ | Bắt buộc khi lưu điểm chưa hiển thị? | Quy tắc đã chốt |
| --- | --- | --- |
| Mã địa điểm | Có trong bản ghi khi tạo thành công; Admin không phải nhập. | Hệ thống cấp mã ổn định, không đổi do đổi tên, không dùng lại mã của điểm đã xóa cho điểm khác. Không tìm/gộp/ghi đè địa điểm chỉ vì trùng tên. |
| Tên hiển thị địa điểm | **Bắt buộc nhập.** | Dài 1–100 ký tự hiển thị sau bỏ khoảng trắng đầu/cuối và gộp khoảng trắng liên tiếp giữa tên. Chấp nhận Unicode, số, dấu câu thông thường; không nhận toàn khoảng trắng, xuống dòng hoặc ký tự điều khiển vì tên dùng làm nhãn. Giữ nguyên dấu/chữ hoa của tên khi hiển thị. Đây là quy tắc tên địa điểm Q1 đã chốt, không đổi tên khách 1–50. |
| Trạng thái hiển thị | Hệ thống có trạng thái; điểm mới mặc định chưa hiển thị theo A02.5. | Lưu thông tin hợp lệ không tự bật hiện. “Bật hiển thị” là thao tác riêng, chỉ thành công khi đủ A02.5. |
| Vùng liên kết | Có thể chưa có khi lưu điểm ẩn; là quan hệ riêng, không phải ô nhập thuyết minh. | Vẽ/xem trước/lưu/sửa theo A02.4; bật hiện cần vùng hợp lệ đã xem xét/áp dụng. Sửa vùng điểm đang hiện giữ vùng cũ đến khi Áp dụng theo A02.6. |
| Văn bản/bản dịch/audio | Có thể chưa có khi lưu điểm ẩn; quản lý riêng theo điểm-ngôn ngữ/bản văn. | Không tạo ô “nội dung thuyết minh” thứ hai trong thông tin địa điểm. Bật hiện cần ít nhất cặp vi đã duyệt/áp dụng theo UI-04; chưa có audio khớp thì chưa đạt gate. Các ngôn ngữ khác thiếu hoặc audio của cặp đã áp dụng thực tế lỗi xử lý D03/BS. |

**Trường tùy chọn:** Bộ thông tin cơ bản này chưa cần thêm trường nội dung tùy chọn. Vùng và nội dung có thể thiếu ở giai đoạn điểm ẩn nhưng vẫn là các quan hệ riêng cần hoàn thiện, không sao chép vào biểu mẫu tên. Không yêu cầu vị trí thực tế của khách, GPS hoặc quyền vị trí.

**Độ dài và trùng tên — ĐÃ CHỐT:**

- Mức 100 ký tự đã được người dùng xác nhận cho tên/nhãn địa điểm; không là giới hạn do giảng viên, database hoặc TTS đã quy định. Bố cục nhãn cụ thể vẫn được thiết kế riêng.
- **Cho phép trùng tên**, kèm cảnh báo không chặn khi tên sau chuẩn hóa khoảng trắng trùng với tên điểm còn tồn tại, so sánh không phân biệt hoa/thường nhưng không bỏ dấu tiếng Việt. Danh sách Admin hiển thị mã cùng tên để phân biệt. Admin được tiếp tục lưu, không tự gộp bản ghi; không tự thêm hậu tố vào tên khách thấy. Cách này giữ đúng nguyên tắc định danh độc lập tên và không đặt ràng buộc duy nhất mới.
- Thêm điểm mới mang tên điểm đã xóa luôn là bản ghi mới; lịch sử cũ vẫn gắn nhận biết điểm cũ theo A02.2, không được chuyển sang điểm mới.

**Phân biệt lưu và hiển thị:**

1. Lưu địa điểm mới: kiểm tra tên; thành công tạo mã và bản ghi **chưa hiển thị**, dù chưa có vùng/vi/audio. Chưa có bản ghi thành công thì phần đang nhập chỉ là nội dung chưa lưu theo Q2.
2. Bật hiển thị: kiểm tra độc lập ba điều kiện A02.5/UI-04 — thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng, ít nhất cặp vi đã duyệt/áp dụng. Thiếu điều kiện nào thì thông báo đúng điều kiện đó, giữ trạng thái chưa hiển thị và cho Admin tiếp tục bổ sung.
3. Lưu tên không tự áp dụng nháp văn bản/audio hoặc bản sửa vùng. Với điểm đang hiển thị, sửa tên sai/lưu lỗi không được làm mất thông tin đã lưu hoặc tự thay vùng hiện hành.

**Phản hồi đã chốt:** Tên trống báo “Vui lòng nhập tên địa điểm”; quá giới hạn báo rõ giới hạn 100 ký tự đã chốt; có xuống dòng/ký tự điều khiển thì báo để sửa, không âm thầm bỏ qua. Giữ dữ liệu đang nhập, không báo đã lưu hoặc tạo mã khi thao tác chưa thành công. Tên trùng chỉ cảnh báo kèm mã để Admin kiểm tra, không biến thành lỗi chặn.

**Ví dụ:** Admin nhập “  Khu   trưng bày A  ” → tên sau chuẩn hóa “Khu trưng bày A”; lưu được một điểm ẩn chưa có vùng/bài. Bấm bật hiện thì hệ thống chỉ rõ còn thiếu vùng hợp lệ đã xem xét/áp dụng và cặp vi đã duyệt/áp dụng. Nếu có điểm khác trùng tên, hiển thị cảnh báo và hai mã khác nhau; không gộp chúng. Tên minh họa không bổ sung địa điểm vào bộ nội dung chuẩn D06.

<a id="q2"></a>

#### 5.8.2. Q2 — Bảo vệ nội dung Admin chưa lưu — ĐÃ CHỐT

**Liên kết:** A01, A02/A02.6, A03, A04.2/A04.4 và Q3. Phạm vi theo tab là phạm vi bản giữ tạm, không xác lập mô hình phiên Admin theo tab.

**Nội dung đã chốt:** Bảo vệ cả ba loại nội dung trong cùng tab đang soạn, qua hết phiên/đăng nhập lại, lưu lỗi/mất mạng và tải lại bình thường của chính tab. Không cam kết khôi phục qua thiết bị, tab khác, tab đã đóng hoặc sự cố trình duyệt; không tạo kho bản nháp giữ tạm dài hạn. Đây là phạm vi bản giữ tạm, **không xác lập mô hình phiên Admin theo tab**.

| Nội dung được giữ tạm | Phần cần tiếp tục được sau khôi phục |
| --- | --- |
| Văn bản/bản dịch | Nội dung đang soạn, địa điểm/ngôn ngữ và bản văn đang sửa để quay lại đúng màn hình soạn; không tự gắn audio cũ vào bản mới. |
| Thông tin địa điểm | Các giá trị biểu mẫu đang sửa hoặc đang thêm mới và đối tượng đích nếu đã tồn tại; điểm mới chưa lưu không được coi là đã tạo bản ghi. |
| Vùng đang vẽ/sửa | Những đỉnh/hình dạng đã đặt và địa điểm đích, kể cả hình chưa hoàn chỉnh; trở lại trình sửa để hoàn thiện/xem trước. Không cần giữ chuyển động con trỏ tức thời hoặc khung nhìn chính xác từng điểm ảnh. Vùng đang áp dụng không đổi do giữ tạm. |

**Bổ sung theo UI-04/UI-AP05:** phần tham chiếu văn bản/audio đang nhập trong công việc soạn được bảo vệ cùng công việc, cảnh báo trước khi mất; không thêm kho dài hạn hoặc bảo vệ mọi biểu mẫu. Chỉ ghi cho đối tượng đã lưu, không âm thầm lưu văn bản đang sửa.

**Kết quả cần phân biệt:** Giữ tạm chỉ bảo vệ việc tiếp tục soạn. “Đã giữ tạm” phải khác “Đã lưu địa điểm”, “Đã lưu nháp văn bản” và “Đã lưu bản sửa vùng”; tất cả vẫn khác “Đã áp dụng”. Nếu việc giữ tạm không được xác nhận thành công, thông báo đúng giới hạn, không hứa đã bảo vệ qua tải lại. Khôi phục qua tải lại dựa vào bản giữ tạm gần nhất đã được ghi nhận; không tự đặt ngưỡng thời gian ghi tạm trong xác nhận nghiệp vụ này.

| Tình huống | Kết quả đã chốt | Thời điểm bỏ/xóa phần giữ tạm |
| --- | --- | --- |
| Phiên hết hạn rồi đăng nhập lại | Giữ nội dung đang soạn, thông báo hết phiên và chặn tác vụ cần quyền. Sau đăng nhập lại **cùng tài khoản**, trở về đúng biểu mẫu/trình vẽ với nội dung chưa lưu để tiếp tục; không tự lưu/áp dụng/gửi lại yêu cầu. Nếu tài khoản khác đăng nhập, không hiển thị bản giữ tạm của người trước. | Không xóa chỉ vì hết phiên. Xóa phần tương ứng khi lưu thành công, chủ động bỏ hoặc kết thúc phạm vi giữ tạm bên dưới. Khi đổi tài khoản thì loại bỏ bản giữ tạm của tài khoản trước. |
| Lưu thất bại hoặc mất mạng | Giữ giá trị đang nhập và bản tạm; báo “Chưa lưu được” hoặc “Chưa xác nhận đã lưu” nếu kết quả chưa rõ. Khi có kết nối, kiểm tra phiên/trạng thái trước thử lại chủ động; không tự biến dữ liệu tạm thành bản đã lưu. | Chưa xóa nếu chưa xác nhận lưu thành công. Lưu thành công thì chỉ xóa bản tạm của đúng công việc vừa lưu, không xóa các bản ghi nghiệp vụ đã lưu khác. |
| Tải lại bình thường trong cùng tab | Khôi phục bản giữ tạm gần nhất của đúng đối tượng/ngôn ngữ trên đúng màn hình soạn/trình vẽ; nếu cần thì đăng nhập lại cùng tài khoản trước khi cho xem/sửa. Hiển thị rõ nội dung chưa lưu; không tự Tạo audio, Lưu hoặc Áp dụng. Tải lại không tự gia hạn phiên Admin. | Giữ đến khi lưu thành công hoặc Admin chủ động bỏ; lỗi không khôi phục được phải báo rõ, không báo đã lưu/đã khôi phục giả. |
| Chủ động đăng xuất khi còn thay đổi | Cho lựa chọn “Đăng xuất và bỏ thay đổi” hoặc “Quay lại soạn”. Xác nhận đăng xuất thì dừng sử dụng bản giữ tạm và xóa phần chưa lưu của tài khoản trong phạm vi này; bản đã lưu nháp/bản sửa vùng không bị xóa theo thao tác đó. | Xóa khi xác nhận đăng xuất và bỏ thay đổi; nếu quay lại soạn thì giữ. Tác vụ TTS đã được tiếp nhận vẫn xử lý theo A04.4. |
| Đóng tab | Đóng tab chấm dứt phạm vi phục hồi được cam kết; không cung cấp khôi phục từ kho giữ tạm ở lần mở mới. Có thể cảnh báo mất thay đổi nếu trình duyệt cho phép, nhưng không hứa mọi trình duyệt luôn cảnh báo hoặc nhận biết chính xác lúc đóng. | Bản giữ tạm hết vòng đời phục vụ lần soạn đó. TDD phải mô tả cách loại bỏ dữ liệu tạm khi hết phạm vi, kể cả trường hợp không nhận được tín hiệu đóng; không hứa đã xóa vật lý đúng một thời điểm không quan sát được. |
| Lưu thành công hoặc chủ động bỏ | Lưu thành công thì tiếp tục theo loại bản ghi đã lưu: điểm vẫn theo trạng thái của nó, nháp văn bản chưa áp dụng, bản sửa vùng chưa áp dụng. “Bỏ thay đổi” quay về dữ liệu đã lưu, cần xác nhận nếu đang có phần chưa lưu. | Xóa bản tạm tương ứng khi đã xác nhận lưu thành công hoặc xác nhận bỏ; bản lưu nghiệp vụ không bị xóa chỉ vì dọn tạm. |

**Điều hướng khi còn thay đổi:** Trước khi chuyển sang công việc khác làm mất nội dung đang soạn, cho Admin chọn tiếp tục soạn hoặc xác nhận bỏ; không tự xóa chỉ do bấm một chức năng khác. Không yêu cầu một danh sách khôi phục nhiều điểm/ngôn ngữ lâu dài.

**Khi dữ liệu đích thay đổi:** Bản tạm không được tự khôi phục điểm đã bị xóa hoặc ghi đè im lặng một bản mới. Kiểm tra đối tượng/bản hiện hành sau đăng nhập lại; nếu điểm đã xóa, báo không thể tiếp tục lưu vào điểm đó và cho bỏ phần tạm. Nếu có thay đổi liên quan cần đối chiếu, cho Admin nhận biết trước lưu/áp dụng; chưa bổ sung cộng tác thời gian thực hoặc hệ thống lịch sử phiên bản phức tạp.

**Không giữ lặp tác vụ:** Kết quả/trạng thái TTS đã được tiếp nhận là dữ liệu tác vụ theo A04.2/A04.4, không phải phần nhập liệu chưa lưu. Khôi phục biểu mẫu hoặc đăng nhập lại phải xem yêu cầu cũ trước khi tạo mới nếu chưa rõ kết quả; không gửi lại tự động. Dữ liệu bản tạm không phải dữ liệu phiên/lượt xem khách theo D08.

**Ví dụ:** Admin đang sửa bản fr, hết phiên trước khi lưu; sau đăng nhập lại cùng tài khoản trong tab đó, trở về đúng bản fr với nhãn chưa lưu. Ở một lần soạn khác, Admin đang vẽ vùng rồi tải lại: các đỉnh đã giữ tạm được khôi phục để tiếp tục. “Lưu bản sửa vùng” thành công xóa bản tạm tương ứng nhưng không Áp dụng vùng. Nếu một yêu cầu TTS đã tiếp nhận trước đó đang chạy, nó vẫn tiếp tục và không bị gửi lại do khôi phục trang. Không yêu cầu phải mở đồng thời nhiều biểu mẫu hoặc có kho nhiều bản tạm.

**Lý do/giới hạn:** Bảo vệ các lỗi thường gặp khi soạn đồ án và cả tải lại nhầm, đồng thời giới hạn vòng đời tạm vào lần làm việc trong tab. Chưa chọn nơi/cách lưu tạm. Khôi phục sau đóng tab/crash hoặc ở thiết bị khác không nằm trong cam kết; nội dung đã Lưu nháp thành công vẫn được quản lý theo A03, không bị coi là dữ liệu tạm cần xóa.

<a id="q3"></a>

#### 5.8.3. Q3 — Hoạt động tính vào phiên Admin — ĐÃ CHỐT

**Liên kết:** A01, Q2, A04.4. Không thay phiên khách 8 tiếng hoặc chính sách lưu D08.

**Nội dung đã chốt:** Sau đăng nhập thành công, giữ nguyên khoảng **30 phút không hoạt động**. Trong phiên còn hiệu lực, mỗi hành động nghiệp vụ chủ động được hệ thống ghi nhận bắt đầu lại khoảng 30 phút từ hành động đó; không tạo phiên mới chỉ vì có hoạt động. Không chọn kỹ thuật bắt/gửi sự kiện hoặc quyết định mô hình phiên Admin giữa các tab.

| Hành động / trạng thái | Có tính hoạt động để bắt đầu lại khoảng 30 phút? | Giới hạn đã chốt |
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

**Mất mạng:** Dữ liệu đang nhập vẫn được bảo vệ theo Q2. Không mặc định thao tác tại máy khi hệ thống chưa xác nhận được là bằng chứng phiên còn hiệu lực; khi nối lại kiểm tra phiên trước tác vụ cần quyền. Không ghi bù hoạt động để làm sống lại phiên đã hết hạn. TDD phải thiết kế cách ghi nhận đáp ứng quy tắc được duyệt, không dùng xác nhận này để chọn công nghệ hoặc gửi yêu cầu sau từng phím bấm.

**Ví dụ:** Đăng nhập 09:00; nhập nội dung lúc 09:20 → nếu không có hoạt động tiếp thì hết hạn 09:50. Chủ động bấm Tạo audio lúc 09:25 → mốc không hoạt động mới là 09:55. Nếu sau đó chỉ có tác vụ tạo/tự tải trạng thái/audio tự phát, phiên vẫn hết hạn 09:55; tác vụ đã nhận tiếp tục, nội dung chưa lưu được xử lý theo Q2.


<a id="ui-admin-confirmed-01"></a>

### 5.9. UI-03 — Xác nhận hướng UI Admin lượt 1 — 02/11/2026


**Liên kết:** A01, A02.4/A02.5/A02.6, Q1/Q2/Q3; vị trí thiết kế hiện hành [ui-design mục 9](../04-giao-dien/ui-design.md#ui-admin-round1).

#### 5.9.1. UI-AP01 — Vẽ vùng — hướng đã chốt

- Mỗi địa điểm một đa giác kín đơn giản; bấm/chạm lần lượt đặt đỉnh rồi chủ động Khép vùng.
- Sửa bằng chọn/kéo đỉnh; có Bỏ đỉnh vừa đặt và Vẽ lại; tách Di chuyển bản đồ, Vẽ, Sửa đỉnh.
- Chưa gồm vùng có lỗ hoặc nhiều mảnh tách rời.
- Hình đang dở giữ tạm theo Q2; hình lưu phải khép và xem trước được.
- Không đồng nghĩa duyệt vị trí/ranh giới năm vùng, công nghệ hoặc cấm mọi chồng lấn; giữ P08 và phạm vi A02.4/A02.6.

#### 5.9.2. UI-AP02 — Xem trước, lưu và áp dụng — hướng đã chốt

- Xem trước và Áp dụng nằm trong cùng trang vùng; phân biệt hình đang sửa, bản sửa đã lưu và vùng khách đang dùng.
- **Vẽ/sửa → Xem trước → Lưu bản sửa → Áp dụng đúng bản đã xem.**
- Lưu không làm thay đổi hình đã xem thì không bắt Admin mở lại xem trước chỉ vì vừa lưu.
- Nếu sửa tiếp hoặc bản lưu khác hình đã xem thì phải lưu và xem lại đúng bản trước áp dụng.
- Chỉ áp dụng bản đã lưu, không âm thầm lưu phần đang sửa; Áp dụng là thao tác chủ động riêng, không thêm box xác nhận thứ hai.
- Giữ bước xem xét thực tế A02.5/A02.6; mở xem trước không tự chứng minh ranh giới được duyệt.
- Áp dụng không tự bật hiện địa điểm; khách nhận vùng ở lần tải bản đồ tiếp theo, không đẩy thay đổi vào phiên khách đang mở.

#### 5.9.3. UI-AP03 — Bố cục và bảo vệ công việc — hướng đã chốt

- Sau đăng nhập mở danh sách địa điểm, không thêm dashboard.
- Biểu mẫu thêm/sửa và trình vùng dùng trang riêng.
- Hết phiên dùng box đăng nhập lại che nội dung quản trị.
- Cảnh báo phần chưa lưu có Quay lại soạn hoặc xác nhận bỏ, không thêm Lưu và đi tiếp.
- Giữ Q2/Q3; không tự Lưu, Áp dụng hoặc gửi lại thao tác sau xác thực.

#### 5.9.4. Sửa câu chữ và phạm vi loại trừ

Nhãn form tên: **“Bắt buộc, tối đa 100 ký tự hiển thị.”** Bộ đếm có thể bắt đầu 0 nhưng tên rỗng không hợp lệ theo Q1; đây là sửa trình bày, không đổi khoảng 1–100.

Xác nhận chỉ bao gồm các hướng nêu trên và cách làm rõ luồng vùng. **Không duyệt toàn bộ đồ họa/câu chữ, các chi tiết chưa nêu của lượt 1, UI Admin lượt 2/lượt sau, vị trí/ranh giới, nội dung, bản dịch/audio, TTS hoặc kỹ thuật/kết quả kiểm thử.** Tại thời điểm UI-03, lượt 2 UI-A13–UI-A19/UI-AP04–UI-AP07 còn đề xuất; trạng thái hiện hành theo UI-04 ở mục 5.10. Không suy rộng UI-03 thành duyệt lượt 2. Không mở lại nghiệp vụ khách.


<a id="ui-admin-confirmed-02"></a>

### 5.10. UI-04 — Hướng UI Admin lượt 2 và áp dụng cả cặp — 02/11/2026

**Đã chốt đúng nội dung người dùng xác nhận.** Đã đọc docs/README, Business Flow, decisions, PRD 0.6, prd-review và UI 0.5 trước cập nhật. UI-04 là mã xác nhận tiếp sau UI-03; giữ UI-AP04–UI-AP07 và mã FR/UC/AC để truy vết. Đây là **thay đổi nghiệp vụ có chủ đích** ở UI-AP06/A03/A04.3/A02.5, không phải sửa nguồn để hợp thức hóa thiết kế hoặc code.

#### 5.10.1. UI-AP04 — Trang soạn và đối chiếu

Máy tính có Bản đang soạn và Bản khách đang dùng; bản đang dùng chỉ đọc, chỉnh sửa không thay trực tiếp nội dung phục vụ. Hồ sơ, tạo/nghe thử và áp dụng trong cùng công việc, rõ địa điểm/mã/ngôn ngữ. Điện thoại xếp dọc, đối chiếu mở/thu gọn; trang mức đầy đủ tách khỏi trang soạn.

#### 5.10.2. UI-AP05 — Hồ sơ duyệt của cặp nội dung

- Gom trong khu vực **Hồ sơ duyệt của cặp nội dung**. Văn bản/audio có tham chiếu riêng, đúng bản/ngôn ngữ.
- Tham chiếu là đường dẫn hoặc mã tài liệu kèm mục đủ đối chiếu; có ghi chú giới hạn khi cần. Được dẫn chung một mục nếu xác định rõ cả văn bản và audio đã được người dùng duyệt.
- Không bắt nhập lại ngày/người duyệt nếu hồ sơ đã chứa. Ghi tham chiếu không đồng nghĩa duyệt/áp dụng; phần mềm không tự thẩm định tài liệu ngoài.
- Chỉ ghi cho đối tượng đã lưu; không âm thầm lưu văn bản đang sửa.
- Phần tham chiếu đang nhập được bảo vệ cùng công việc soạn theo phạm vi đã đề xuất: cảnh báo trước khi mất, cùng tab/tài khoản và giới hạn Q2; không mở rộng sang mọi biểu mẫu/kho phục hồi dài hạn.

#### 5.10.3. UI-AP06 — Chỉ áp dụng cả cặp — THAY PHƯƠNG ÁN CŨ

**Đơn vị áp dụng:** đúng phiên bản văn bản và audio khớp của **một địa điểm, một ngôn ngữ**. Văn bản đã lưu/được người dùng duyệt theo hồ sơ; audio đã nghe trọn, được duyệt, khớp văn bản/ngôn ngữ theo D09.

- Trước một nút **“Áp dụng văn bản và audio”**, tóm tắt cặp đang dùng và cặp chuẩn bị thay. Thiếu điều kiện nào thì chưa áp dụng và nêu đúng phần thiếu.
- Không còn hành động áp dụng riêng văn bản hoặc audio; Lưu/Tạo audio/Ghi tham chiếu không tự áp dụng.
- Chuyển cả cặp nhất quán, không phục vụ nửa cặp mới. Lỗi xác định giữ cặp cũ; chưa rõ kết quả kiểm tra trạng thái thực tế trước thử lại, không báo thành công/thất bại thiếu căn cứ. Chưa có cặp cũ thì chưa phục vụ nội dung mới đến khi áp dụng thành công.
- Chỉ thay audio: dùng văn bản hiện tại không đổi cùng audio mới đã duyệt; không bắt sửa hoặc duyệt lại văn bản không đổi.
- Một cặp có thể áp dụng độc lập với ba ngôn ngữ còn lại. Hiệu lực lần tải/chọn tiếp theo theo A07/P01/UI-02, không ngắt bài đang nghe có quyền.
- Áp dụng không tự bật hiện địa điểm; không suy ra một cơ chế database/công nghệ cụ thể từ yêu cầu nhất quán này.

**Điều kiện bật hiện A02.5 hiện hành:** thông tin hợp lệ; **vùng hợp lệ đã được xem xét/áp dụng** theo quy tắc vùng; **ít nhất cặp tiếng Việt đã được duyệt và áp dụng**. Vẫn lưu được điểm ẩn khi thiếu vùng/bài/audio. Không bắt đủ bốn ngôn ngữ để bật một điểm; không tự chuyển ngôn ngữ khi thiếu. Bộ chuẩn vẫn năm điểm × bốn ngôn ngữ = 20 văn bản + 20 audio, không trần số điểm.

**Phân biệt với lỗi phía khách:** thiếu audio trước khi áp dụng cặp mới thì chặn áp dụng. Cặp đã áp dụng nhưng audio thực tế không sẵn sàng/tải-phát lỗi thì vẫn giữ văn bản đọc được, thông báo đúng D03/D03-BS/P04 và quyền phiên hiện hành. Không phát audio cũ/khác ngôn ngữ thay thế; không bỏ luồng thiếu/lỗi audio hoặc mở thêm quyền thử lại sau hết hạn.

#### 5.10.4. UI-AP07 — Hết phiên khi nghe thử Admin

Hết phiên Admin thì **dừng audio nghe thử, che nội dung, yêu cầu đăng nhập lại**. Đăng nhập lại không tự phát hoặc gửi lại thao tác. Giữ công việc soạn theo Q2 và phần tham chiếu thuộc UI-AP05; TTS đã tiếp nhận tiếp tục độc lập A04.4. Không áp ngoại lệ nghe tiếp của khách cho Admin, không đổi mốc 30 phút Q3.

#### 5.10.5. Phạm vi xác nhận và truy vết

Chỉ UI-AP04/UI-AP05/UI-AP07 và UI-AP06 thay thế nêu trên được chốt. Không duyệt toàn bộ đồ họa/câu chữ, UI Admin lượt 3, lịch sử/bản dịch/audio cụ thể, TTS/công nghệ/chi phí hoặc kết quả phần mềm. Nội dung chưa được duyệt không tự trở thành được duyệt do áp dụng quy trình.

Nguồn này thay phần A02.5/A03/A04.3/Q1 còn cho bật hiện chỉ cần văn bản vi hoặc áp dụng văn bản trước audio; dấu vết tại [7.6](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#ui-04-history). PRD hiện hành đồng bộ BR/DR/FR/UC/AC/NFR theo UI-04; tại thời điểm UI-04 lượt 3 còn đề xuất; hiện hành theo UI-05 ở mục 5.11, không được UI-04 duyệt ngầm.


<a id="ui-admin-confirmed-03"></a>

### 5.11. UI-05 — Xác nhận hướng UI Admin lượt 3 — 02/11/2026


#### 5.11.1. UI-AP08 — Danh sách phiên và box UI-A21 chỉ đọc

- Duyệt hướng danh sách; thay **trang chi tiết riêng** bằng **box chi tiết trên danh sách**. Bấm Xem/chọn đúng phiên mở box; nền không nhận thao tác.
- Hiển thị tên/mã, ngôn ngữ ban đầu, các mốc và trạng thái theo dữ liệu nghiệp vụ; các lượt xem đã được tính gồm thời điểm, địa điểm/nhận biết lịch sử và ngôn ngữ.
- Có nút Đóng, phần dài cuộn trong box; đóng về danh sách **giữ bộ lọc**. Điện thoại box vừa chiều rộng màn hình/vùng cuộn phù hợp, không bắt mở thêm trang.
- Giữ tải/lỗi/rỗng/dữ liệu không còn/hết phiên Admin. Không thêm sửa/xóa/gia hạn/kết thúc phiên thay khách. Mã UI-A21 giữ nguyên, chỉ đổi loại.

#### 5.11.2. UI-AP09 — Hai bảng và hai tổng, khoảng ngày trọn ngày

- Từ/Đến mặc định hôm nay theo **Asia/Saigon**, cả ngày đầu/cuối được tính trọn ngày. Nhiều ngày nhóm theo ngày.
- Hai tổng riêng: số phiên bắt đầu và số lượt xem được tính trong khoảng, **không cộng hai loại với nhau**.
- Bảng phiên: **Ngày — Số phiên bắt đầu**. Bảng lượt: **Ngày — Địa điểm/nhận biết lịch sử — Ngôn ngữ — Lượt xem**; có tổng tương ứng.
- Về hôm nay đặt lại hai ngày; tại danh sách giữ trạng thái/tên-mã đang lọc. Điện thoại hai nhóm dọc, dòng có thể thành thẻ nhưng đủ nhãn; chưa cần biểu đồ/dashboard.
- Phiên theo bắt đầu; lượt theo thời điểm xem/điểm/ngôn ngữ đã ghi. Không người duy nhất, lượt nghe/quét hoặc cộng gộp hai số đếm.
- Chỉ dữ liệu còn giữ D08; không kho thống kê vô hạn. Điểm đã xóa nhận biết riêng/không gộp điểm mới trùng tên. Không thêm lọc điểm/ngôn ngữ, xuất báo cáo/thống kê mới; ví dụ không là số liệu thực.

#### 5.11.3. UI-AP10 — Một QR chung đơn giản

- Trang QR có đúng một QR và URL trang bắt đầu; URL chỉ đọc.
- Tải ảnh **PNG**; bản in gọn có tên hệ thống, một QR và URL dự phòng đọc được. Không thêm trang trí/tùy biến hoặc chức năng ngoài nhu cầu.
- Chưa có URL thật thì chưa sẵn sàng, không sinh QR từ địa chỉ giả. Không tự chọn URL/thư viện, QR riêng theo điểm/ngôn ngữ, đếm quét hoặc chuyển hướng.
- Khả năng quét/URL đúng cần kiểm tra bản thực tế; mở hộp in/yêu cầu tải không chứng minh đã in/lưu thành công.

#### 5.11.4. Phạm vi và giới hạn

Chỉ các hướng trên được chốt; không duyệt toàn bộ đồ họa/câu chữ, nền/vị trí/ranh giới, nội dung/bản dịch/audio, TTS/công nghệ hoặc bằng chứng phần mềm. Không đóng toàn bộ R06; giữ UI-01–UI-04, phiên/đếm/thời hạn/cặp/gate và hồ sơ đã chốt. Nguồn liên quan A05/A06/D04/D08/A02.2, FR-A10/A11, UC-A07/A08, AC-A22–A24.

Bản chi tiết trang riêng và trạng thái đề xuất trước UI-05 được lưu tại [7.7](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md#ui-05-history), không áp dụng như nguồn hiện hành. Công việc chuẩn bị TDD xem [ui-design mục 12](../04-giao-dien/ui-design.md#ui-next-technical); đó là tổng hợp phụ thuộc, chưa là TDD/roadmap hoặc ủy quyền thao tác kỹ thuật.

<a id="dec-6"></a>

## 6. Phần còn mở và điều kiện nghiệm thu

Nghiệp vụ D/A/Q/UI/TECH/DATA ở trên giữ nguyên phạm vi. Mã đã triển khai hoặc kiểm tự động đạt không thay phê duyệt D09 và nghiệm thu môi trường phục vụ.

| Nhóm | Phần cần hoàn tất | Nguồn kiểm soát |
|---|---|---|
| Nội dung/audio | Đã tạo đủ năm ru; nghe trọn 19 audio mới, rà phát âm Thái Hòa fr, nguồn/bản dịch và hồ sơ từng cặp trước áp dụng. | D06/D09, ma trận nội dung |
| Vùng/giao diện | Vùng tương tác phục vụ UX, không là ranh giới pháp lý; kiểm dữ liệu áp dụng thực và đầy đủ luồng chồng lấn/chạm/kéo/phóng, Q2/Q3, thiếu nội dung. | P06–P08, UI, báo cáo kiểm |
| Thiết bị | Android/iPhone đã có báo cáo chạy được; còn ghi model/OS/browser/mạng và đủ ma trận ngoại lệ, tab nền, reload/hết hạn/audio. | IN-P02, T09 |
| Vận hành | V14/gate/backup/restore đã kiểm riêng và đã chuyển bản phục vụ có backup trước nâng; lịch backup/log đã đăng ký và chạy thử, còn theo dõi trigger, owner sau crash đủ căn cứ và RPO/RTO. | DATA-01–05, operations-design |
| CD/vận hành | CD GitHub và lượt backup qua task đã đạt; còn theo dõi trigger hằng ngày, giới hạn log hạ tầng, mọi ca crash và khả năng tái lập đầy đủ khi bàn giao. | TECH-05, T10 |
| Hồ sơ | Đối chiếu toàn bộ checklist và tài liệu với bằng chứng; không tích cả ô khi mới đạt một phần. | Roadmap, T11 |

Phân biệt ba nguồn: đặc tả quy định hành vi; mã/migration thể hiện triển khai; báo cáo ghi phép kiểm đã thực chạy. [Bàn giao](../01-tong-quan/handoff.md) và [báo cáo kiểm thử](../06-kiem-thu/README.md) tổng hợp trạng thái mới nhất; nhật ký nguyên bản không là trạng thái hiện hành.

<a id="dec-7"></a>
<a id="dec-8"></a>
<a id="dec-history"></a>
