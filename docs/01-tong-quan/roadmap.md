# Roadmap — Backlog 11 issue và tiến độ theo bằng chứng

**Phiên bản nội dung:** 0.11, rà soát bàn giao 08/11/2026. **Trạng thái:** backlog 11 issue/89 ô/18 dấu tích đã được duyệt theo G02. Issue #1–#11 được tạo và đọc lại ngày 08/11. Project có đủ 11 issue, 6 In Progress/5 Todo/0 Done theo xác nhận vận hành ngày 08/11, chưa có lần kiểm trực tiếp mới. Phân công không chứng minh thành viên đã thực hiện hoặc hoàn thành task.
**Checklist local ngày 09/11:** 89 ô/20 dấu tích, giữ nguyên bản lưu đầu lượt; mốc GitHub đã đối chiếu là 18 dấu tích, chưa kiểm lại. Chi tiết tại [mục 6.5](#local-checklist-20261109).
**Mục đích:** tra phạm vi, phân công, phụ thuộc và lịch dự kiến. Bảng tổng quan cập nhật theo bằng chứng backend và xác nhận Project ngày 08/11/2026, các snapshot cũ giữ theo thời điểm. Điểm dừng và bằng chứng mới nhất ở [handoff](handoff.md#next-step).
**Giới hạn:** checklist và thiết kế không thay bằng chứng phần mềm. Quy trình phối hợp, quyền thao tác và mốc bàn giao tại [handoff mục 7](handoff.md#guidance-protocol). Trạng thái Git và sản phẩm được cập nhật theo kết quả thực tế, không theo kế hoạch dự kiến.

## 1. Căn cứ tiến độ và tài liệu nguồn

Dự án đã có nhiều kết quả phân tích và tài liệu trước khi tạo issue. Checkbox hoàn tất chỉ ghi đúng kết quả có căn cứ; không đồng nghĩa phần mềm đã triển khai hoặc mọi tiêu chí đã đạt. Việc mới soạn roadmap không đủ để tích hoàn thiện roadmap.

| Căn cứ hiện hành | Kết quả / giới hạn bằng chứng |
| --- | --- |
| [Business Flow](../02-nghiep-vu/business-flow.md), [BF-K01/BF-A01/BF-ALL01](../02-nghiep-vu/decisions.md#bf-approval) | Mục tiêu, phạm vi và hai luồng đã được duyệt trong phạm vi nghiệp vụ. |
| [PRD hiện hành](../02-nghiep-vu/PRD.md), [PRD-01](../02-nghiep-vu/decisions.md#prd-approval) | Có BR/DR, 23 FR, 14 UC, 50 AC, 6 NFR và truy vết; chỉ phạm vi đã rõ được duyệt theo nguồn, phần mở giữ riêng. |
| [decisions](../02-nghiep-vu/decisions.md#dec-2), [prd-review 7.9](../99-luu-tru/prd-review-history.md#review-ui-05) | Có căn cứ, lịch sử và điểm mở; R01–R05/R07/S01/S02 đã xử lý tài liệu, R06 còn mở một phần. |
| [UI hiện hành](../04-giao-dien/ui-design.md), [hồ sơ tài sản](../04-giao-dien/design-assets/README.md) | Hướng khách/Admin được xác nhận đúng phần, có phác thảo và PNG/SVG/JSON/GeoJSON. Chưa duyệt toàn bộ đồ họa/nền/ranh giới hoặc kiểm chứng lại nguồn ngoài trong lượt kiểm kê. UI-A21 hiện là box. |
| [Mục lục và hồ sơ bàn giao](../README.md), [README gốc](../../README.md) | Tài liệu đã tổ chức, có hướng đọc và kiểm tra phiên bản/trạng thái; kết quả thuộc T01, không tính lại ở T11. |
| Cây làm việc và Git | Ba commit/push T10/T01 tại repository hiện có đã được đối chiếu qua đầu ra Terminal; nguồn và giới hạn tại [handoff mục 2.2](handoff.md#bootstrap-result). Comment và hình Use Case đã được bổ sung ngày 08/11; đích chính thức đã nhận, trạng thái Git/issue cần được đối chiếu trước hướng dẫn tiếp theo. |
| Sản phẩm kỹ thuật và hướng dẫn vận hành | TDD/model/API/operations theo DATA-05, V1–V4, K11/K01/K02/K04, xác thực Admin, A31/A06/tạo điểm ẩn A08 và phần đọc/sửa tên A07/A09 đã có kiểm chứng trong bộ 168 test. [Bằng chứng ngày 08/11](../03-ky-thuat/api-transactions.md#admin-place-test-result). Chưa toàn TDD/schema/T03/T05 hoặc môi trường phục vụ |
| [D06 — năm nháp vi](../02-nghiep-vu/decisions.md#d06) | Đã có báo cáo về năm bản nháp tiếng Việt; chưa xác nhận bản lưu trong repo, chưa có bằng chứng rà soát/duyệt. Không tự dựng lại hoặc tích hoàn tất T08. |
| TTS, URL demo và bằng chứng phần mềm | Đã khảo sát chính thức, chọn model/Charon/WAV-MP3/Linux; chọn thử ngrok, chưa URL thật/usage/quota tài khoản hoặc pipeline/provider thật. Worker/WAV/Check/Retry có bằng chứng backend riêng; chưa provider/quota thật hoặc FFmpeg/MP3/audio hoàn chỉnh. Không dùng sáu MP3 demo làm bằng chứng hệ thống chính. |

Nguồn FR/UC/AC/NFR vẫn ở PRD. Bằng chứng checklist GitHub ở mục 6 được ghi theo ngày đối chiếu, không mặc định đã được kiểm lại. Phân công và người commit theo G02; tiến độ xác định bằng sản phẩm và bằng chứng.

**Repository và bảng công việc:** [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue), [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Thư mục đã ghi nhận: `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`. Nội dung issue #1–#11 đã đối chiếu với mục 3; mapping tại mục 6.

**Tổ chức công việc:** Thành, Hòa và Nghĩa phụ trách theo G02, làm việc trên ba máy riêng và bàn giao qua nhánh task/Pull Request. Tiến độ xác định bằng sản phẩm và quỹ giờ thực tế từng thành viên; lịch dự kiến không chứng minh đã có người thực hiện hoặc kết quả hoàn thành.

## 2. Tổng quan và phối hợp

| Mã task | Người phụ trách theo G02 | Người commit theo kế hoạch nhóm | Tiến độ công việc theo bằng chứng | Căn cứ ngắn |
| --- | --- | --- | --- | --- |
| [T01](#t01) | Thành | Thành | Đang thực hiện | Nghiệp vụ/PRD/hướng UI và công nghệ nền đã chốt; tương thích/môi trường/TDD/roadmap triển khai chưa hoàn tất. |
| [T02](#t02) | Hòa | Hòa | Đang thực hiện | V1–V10 gồm phiên, nội dung/audio/cặp, opening/grant/READ claim; seed riêng 5 điểm/20 ngăn trống đã kiểm. Còn dọn vòng đời/nội dung được duyệt, xem [handoff 2.24](handoff.md#guest-content-frontend-result) |
| [T03](#t03) | Thành | Thành | Đang thực hiện | K03/K05–K10, grant/ACK/tải MP3 và thu hồi quyền đã kiểm cùng nền phiên. Còn browser/Blob/owner, dọn metadata/D08 và tích hợp theo [bằng chứng](../03-ky-thuat/api-transactions.md#guest-content-test-result) |
| [T04](#t04) | Nghĩa | Nghĩa | Đang triển khai nguồn, chưa build/kiểm browser | Có [nguồn Vue khách](../../frontend/README.md); 13 ca Playback V8 đạt, chưa Node/npm/lockfile/build hoặc giao diện hoạt động được kiểm chứng. |
| [T05](#t05) | Thành | Thành | Đang thực hiện | Đã kiểm xác thực/CSRF/Q3, window/receipt, địa điểm/vùng/nháp/hồ sơ, áp dụng cặp/bật-ẩn/mức đầy đủ và báo cáo. Còn Q2/frontend, MP3/preview/nội dung thật, xóa/dọn và nghiệp vụ quản trị khác; [bằng chứng](../03-ky-thuat/api-transactions.md#admin-pair-visibility-test-result) |
| [T06](#t06) | Nghĩa | Nghĩa | Chưa bắt đầu | Hướng thiết kế đã chốt, chưa có UI Admin hoạt động. |
| [T07](#t07) | Thành | Thành | Đang thực hiện | Đã chọn model/giọng; worker/WAV/Check/Retry được kiểm theo [bằng chứng 09/11](../03-ky-thuat/api-transactions.md#worker-report-test-result). Provider/MP3/pipeline/quota và bốn ngôn ngữ thật chưa kiểm chứng. |
| [T08](#t08) | Thành, Nghĩa, Hòa | Thành, Nghĩa, Hòa theo phần phụ trách | Đang thực hiện | Đã có báo cáo về năm bản nháp vi; chưa xác minh bản lưu trong repo hoặc rà soát/duyệt; chưa tích hoàn tất nội dung. |
| [T09](#t09) | Hòa | Hòa | Chưa bắt đầu nghiệm thu toàn hệ thống | Có bằng chứng kiểm thử nền/backend; chưa phải bộ nghiệm thu FR/UC/AC toàn hệ thống của T09. |
| [T10](#t10) | Hòa | Hòa | Đang thực hiện | Đã có quy ước Txx/commit tiếng Việt và commit/push khởi tạo main theo handoff mục 2.2. Còn chờ xác nhận liên kết tài khoản trên GitHub; CI/CD và demo chưa có bằng chứng đạt. |
| [T11](#t11) | Thành | Thành | Chưa bắt đầu | Chưa có hướng dẫn vận hành/bàn giao cuối; tài liệu nền tính ở T01. |

- **Phân công G02 và checklist 89 ô/18 dấu tích đã duyệt ngày 06/11, đã tạo và đọc lại đủ issue mới #1–#11.** Project đủ 11 issue và đã đặt Status theo xác nhận vận hành ngày 08/11 tại mục 6.4. Người phụ trách và người commit được ghi theo task, kết quả theo dõi riêng bằng checklist và bằng chứng. Tên người không phải GitHub username, không tự gán assignee/thời hạn.
- T01: Thành tổng hợp, Nghĩa/Hòa đóng góp thiết kế phần mình. T08: cả nhóm phối hợp, chưa chia địa điểm. T09: Hòa kiểm thử tích hợp/nghiệm thu, mỗi người vẫn kiểm tra và sửa phần mình. T11: Thành tổng hợp, Nghĩa/Hòa cung cấp hướng dẫn/kết quả/bằng chứng.
- T02 phụ trách cấu trúc/ràng buộc/migrations; T03/T05 phụ trách nghiệp vụ Backend; T04/T06 phụ trách giao diện. T07 tạo/theo dõi audio; T05 quản trị/áp dụng cặp/xóa-dọn. T08 duyệt nội dung thực tế; tạo file ở T07 không thay duyệt.
- **T07 khảo sát sớm → đầu vào T01; T01/TDD và hợp đồng T02/T05 → T07 tích hợp sau.** Không tạo vòng chờ đóng cả hai issue. T10/T11 làm xuyên suốt, T09 có thể chuẩn bị kế hoạch sớm; số T không phải thứ tự tuần tự.
- “Chưa bắt đầu” căn cứ phạm vi sản phẩm được kiểm kê, không khẳng định công việc ngoài repo chưa từng diễn ra. T08 dựa thông tin nháp được báo cáo; T10 có bằng chứng khởi tạo/commit/push theo handoff, còn các phần vận hành chưa kiểm. T11 chưa bắt đầu phần hướng dẫn/bàn giao cuối, không phủ nhận tài liệu T01.

## 3. Nội dung 11 issue — kế thừa bản đã duyệt, thay đổi local ghi rõ

Mỗi issue gồm **Description và một Checklist**. Giữ nguyên 11 task/89 ô và phân công đã duyệt; mốc đối chiếu GitHub có 18 dấu tích, bản local hiện có 20 theo mục 6.5. Không tách thành từng file/class/hàm hoặc tự sửa các dấu tích. Các dấu tích chỉ ghi nhận kết quả trong phạm vi có căn cứ, không xác nhận tích hợp/vận hành toàn hệ thống và không biểu thị phần trăm hoàn thành đồ án. Phần checklist giữ nguyên văn đã đối chiếu với issue; hai cách gọi bàn giao cũ trong T01/T08 được giữ để không thay nội dung issue trong đợt biên tập này.

<a id="t01"></a>

### T01 - Phân tích và thiết kế: Hoàn thiện nghiệp vụ, yêu cầu, giao diện và thiết kế kỹ thuật (Thành)

#### Description

Phân tích nghiệp vụ, yêu cầu và giao diện; hoàn thiện thiết kế kỹ thuật và kế hoạch triển khai. Thành tổng hợp, Nghĩa và Hòa đóng góp thiết kế phần mình. Nhận khảo sát sớm từ T07 và ràng buộc môi trường từ T10; T11 dùng kết quả để viết hướng dẫn và bàn giao.

#### Checklist

- [x] Phân tích mục tiêu, vai trò và phạm vi.
- [x] Hoàn thiện Business Flow khách và Admin trong phạm vi nghiệp vụ đã duyệt.
- [x] Soạn PRD, Use Case, tiêu chí nghiệm thu và truy vết; giữ riêng phần còn mở.
- [x] Ghi nhận quyết định, căn cứ xác nhận, lịch sử thay thế và điểm còn mở.
- [x] Phác thảo và xác nhận các hướng UI khách/Admin; chưa bao gồm mọi chi tiết thiết kế.
- [x] Rà soát tài liệu, xử lý các phát hiện đã đủ căn cứ; giữ R06 mở một phần.
- [x] Tổ chức tài liệu, mục lục và hướng dẫn bàn giao giữa hai chat.
- [ ] Hoàn thiện thiết kế cụ thể cần triển khai và xem xét nền/ranh giới vùng.
- [x] Ghi nhận bộ công nghệ/kiến trúc người dùng đã chọn theo TECH-01; chưa kiểm chứng tương thích hoặc triển khai.
- [x] Ghi đầu vào TECH-02: cách hướng dẫn, môn học 3 lớp/CI-CD, máy/thiết bị/4G/mốc mục tiêu; không suy ra mức thành thạo.
- [x] Đối chiếu phần demo được giao chỉ đọc và khảo sát nguồn chính thức về phiên bản/TTS/demo; hồ sơ tại technical-inputs 0.1.
- [x] Ghi IN-P01–IN-P03: bộ phiên bản mốc, nền tảng Android/Chrome-iPhone/Safari-Admin Windows/Chrome và WAV/MP3/FFmpeg Linux theo TECH-03; chưa kiểm tương thích.
- [ ] Ghi ma trận model/OS/browser build thực và kiểm khả dụng/tương thích môi trường; không âm thầm đổi mốc đã chọn.
- [ ] Kiểm phương án thử ngrok/URL/usage/QR khác mạng và kiểm chứng kênh CD-L01 đã chọn theo TECH-05; ngrok Free chưa là kết quả demo đạt.
- [x] Ghi đúng chính sách DATA-01: file chưa áp dụng 24 giờ/tạm 1 giờ/log-backup 7 ngày/restore lọc hạn gốc, giữ 8 giờ/30 ngày.
- [ ] Hoàn thiện path/quyền/công cụ/checkpoint backup và chi tiết log/cách kiểm phục hồi; DATA-03 đã chốt phạm vi/nhịp, chưa coi mọi lớp đã kiểm.
- [ ] Hoàn thiện và rà soát TDD/truy vết/cách kiểm chứng; DATA-05 đã chốt LIFE-P01 vé ký 24 giờ, TDD hiện hành/model/API/operations còn mapping/kiểm thực; thực hành 01 có V1/mapping/K11/K01/K02 và 64 test đạt; chưa toàn TDD hoặc nghiệm thu phần mềm.
- [ ] Hoàn thiện, trình duyệt roadmap triển khai; duyệt backlog 0.2 chưa hoàn tất phần kế hoạch kỹ thuật.

<a id="t02"></a>

### T02 - Database: Thiết kế bảng, quan hệ và dữ liệu khởi tạo (Hòa)

#### Description

Thiết kế và xây dựng cấu trúc dữ liệu theo T01/TDD, cung cấp đầu vào cho T03/T05. Hòa phụ trách bảng, quan hệ, ràng buộc và migrations; nghiệp vụ API, điều phối giao dịch và tác vụ xóa do Backend thực hiện.

#### Checklist

- [ ] Thiết kế bảng/quan hệ cho địa điểm, vùng, văn bản, audio/cặp, hồ sơ, yêu cầu tạo, phiên và lượt xem; đã có ERD logic dự thảo tại data-model hiện hành, chờ rà soát/mapping.
- [x] Giữ định danh độc lập tên và thông tin nhận biết lịch sử địa điểm đã xóa, không gộp điểm trùng tên.
- [ ] Thiết lập ràng buộc và khóa ngoại chặn xóa trực tiếp khi còn tham chiếu ở quan hệ cần chặn; không dùng ON DELETE CASCADE tại đó.
- [ ] Viết migrations và dữ liệu khởi tạo tái tạo được cho năm điểm/bốn ngôn ngữ; không phục vụ nội dung chưa duyệt hoặc đưa secret vào repo.
- [ ] Kiểm tra migrations, khởi tạo và ràng buộc; lưu bằng chứng phù hợp TDD, bàn giao cho Backend và T09.

<a id="t03"></a>

### T03 - Backend khách: Xây dựng API phiên tham quan và nội dung (Thành)

#### Description

Xây API phiên tham quan, nội dung được phép phục vụ và ghi lượt xem. Thành phối hợp T02 về dữ liệu, T04 về giao diện và T05 về cặp nội dung/file; không tính API hoàn thành là giao diện đã chạy.

#### Checklist

- [ ] Xây bắt đầu/kết thúc/kiểm tra phiên: tên hợp lệ, phiên theo tab, hạn cố định 8 tiếng, không tự gia hạn hoặc tạo trùng.
- [ ] Cung cấp địa điểm/vùng và cặp đúng ngôn ngữ được phép hiển thị; không đưa nháp, nửa cặp hoặc audio sai bản ra khách.
- [ ] Ghi lượt xem khi chủ động mở địa điểm và tải văn bản thành công; không cộng do tải lại, đổi ngôn ngữ, nghe/tua hoặc thử lại.
- [ ] Giữ đúng quyền khi hết hạn: chỉ tiếp tục/tạm dừng bài đang phát; không mở bài mới, tua, nghe lại hoặc khôi phục ngoại lệ sau tải lại.
- [ ] Xử lý thiếu nội dung, lỗi/chưa rõ kết quả và phục vụ file theo hợp đồng/TDD; không ghi lượt vào phiên hết hạn.
- [ ] Kiểm tra API chính, lỗi, mốc thời gian và chống lặp; lưu bằng chứng cho T04/T09.

<a id="t04"></a>

### T04 - Frontend khách: Xây dựng giao diện bản đồ và nghe thuyết minh (Nghĩa)

#### Description

Xây giao diện khách theo hướng UI đã chốt, kết nối API T03 và dùng nội dung/nhãn được phép từ T08. Nghĩa phụ trách thao tác, trạng thái và thích ứng điện thoại; việc xem xét ranh giới thuộc T01.

#### Checklist

- [ ] Xây trang bắt đầu riêng: tên, bốn ngôn ngữ, mặc định vi; bắt đầu/kết thúc có phản hồi và ngăn bấm lặp.
- [ ] Xây bản đồ chọn toàn vùng; phân biệt kéo/phóng, bấm lại/ngoài/chồng lấn và chọn liên tục; chỉ một audio, ưu tiên lựa chọn mới nhất.
- [ ] Xây bố cục bản đồ, tên điểm, điều khiển audio phía trên box văn bản cuộn; một nút Phát/Tạm dừng/Nghe lại và thanh tua; tải trọn MP3 hiện tại cho tab trước phát, quản lý Blob/late response đúng TECH-04.
- [ ] Xử lý đổi ngôn ngữ, thiếu bản dịch/audio và audio lỗi đúng quyền; không tự chuyển ngôn ngữ hoặc phát nhầm bài.
- [ ] Xây cảnh báo 5 phút, box kết thúc toàn trang và box hết hạn trong bản đồ; nghe tiếp đúng ngoại lệ, sang form mới thì dừng bài cũ.
- [ ] Khôi phục khi tải lại, xử lý mất mạng/thử lại và các trạng thái tải/rỗng/lỗi theo yêu cầu.
- [ ] Kiểm tra luồng khách với API trên điện thoại/môi trường đã thống nhất; lưu bằng chứng cho T09.

<a id="t05"></a>

### T05 - Backend Admin: Xây dựng API quản trị và xử lý dữ liệu (Thành)

#### Description

Xây API quản trị, áp dụng nội dung, thống kê và xử lý dữ liệu theo T01/T02. Thành phối hợp T07 qua hợp đồng tạo/theo dõi audio; T05 giữ trách nhiệm quyền quản trị, áp dụng cặp, xóa dữ liệu và dọn file.

#### Checklist

- [ ] Xây đăng nhập/đăng xuất và phiên 30 phút theo Q3; hỗ trợ Q2, không hồi sinh phiên hoặc tự gửi lại sau xác thực.
- [ ] Xây quản lý địa điểm theo Q1, vùng và bản nháp/hồ sơ; giữ bản hiện hành khi sửa, tách Lưu bản sửa với Áp dụng.
- [ ] Chỉ áp dụng cả cặp văn bản/audio đã duyệt, đúng bản/ngôn ngữ; lỗi giữ cặp cũ, chưa rõ kết quả thì kiểm tra trước thử lại.
- [ ] Chỉ bật hiện khi thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng và ít nhất cặp vi đã duyệt/áp dụng; áp dụng không tự bật hiện.
- [ ] Điều phối xóa dữ liệu riêng trong giao dịch, bảo vệ dữ liệu dùng chung/hồ sơ tài liệu/lịch sử; vô hiệu liên kết TTS để kết quả muộn không phục hồi địa điểm.
- [ ] Tự xóa phiên và lượt xem sau 30 ngày từ kết thúc/hết hạn; thống kê chỉ dùng dữ liệu còn giữ.
- [ ] Dọn file sau database thành công: không còn tham chiếu và không dùng chung, bảo vệ 8 tiếng từ ngừng áp dụng cuối cùng, có chờ/thử lại; xử lý file khác theo chính sách được chốt.
- [ ] Cung cấp danh sách phiên chỉ đọc, thống kê đúng mốc/Asia/Saigon và đầu vào QR chung khi cần; không thêm chức năng ngoài phạm vi.
- [ ] Kiểm tra API, quyền, cặp, hoàn tác, lịch sử và kết quả xóa/dọn thực tế; lưu bằng chứng cho T06/T09.

<a id="t06"></a>

### T06 - Frontend Admin: Xây dựng giao diện quản trị đầy đủ (Nghĩa)

#### Description

Xây giao diện Admin đầy đủ theo UI hiện hành, kết nối T05/T07. Nghĩa phụ trách trạng thái thao tác và bảo vệ phần đang soạn; giao diện không tự thẩm định hồ sơ hoặc báo tác vụ thành công khi thiếu căn cứ.

#### Checklist

- [ ] Xây đăng nhập, khung quản trị, danh sách và form địa điểm; thêm/sửa/ẩn-hiện/xóa có phản hồi và xác nhận xóa.
- [ ] Xây vẽ/sửa/xem trước/Lưu bản sửa/Áp dụng vùng; chỉ áp dụng đúng bản đã lưu và xem, không bắt xem lại sau lưu nếu hình không đổi.
- [ ] Xây soạn/đối chiếu bản khách chỉ đọc, Hồ sơ duyệt của cặp và nút Áp dụng văn bản và audio; chỉ rõ điều kiện còn thiếu.
- [ ] Xây tạo/theo dõi/kiểm tra/thử lại/nghe thử audio; tạo thành công khác được duyệt và đã áp dụng.
- [ ] Bảo vệ phần chưa lưu và tham chiếu theo Q2/Q3; hết phiên che nội dung, dừng nghe thử, đăng nhập lại không tự phát hoặc gửi lại.
- [ ] Xây danh sách phiên và box UI-A21 giữ bộ lọc; hai bảng thống kê/tổng và mốc ngày theo Asia/Saigon.
- [ ] Xây trang một QR chung, URL chỉ đọc, tải PNG và bản in gọn; chưa URL thật thì chưa sẵn sàng, không sinh QR giả.
- [ ] Kiểm tra trạng thái tải/rỗng/lỗi/chưa rõ kết quả trên Windows/Chrome máy tính/laptop; phân biệt xóa dữ liệu với dọn file, lưu bằng chứng cho T09. Admin mobile chưa là nghiệm thu bắt buộc theo TECH-02.

<a id="t07"></a>

### T07 - TTS: Khảo sát và tích hợp tạo audio từ văn bản (Thành)

#### Description

Khảo sát TTS sớm để cung cấp đầu vào T01, rồi tích hợp sau khi có TDD và hợp đồng với T02/T05; không chờ đóng toàn bộ T01 mới khảo sát. Thành phụ trách tạo/theo dõi audio; duyệt nội dung thực tế thuộc T08, áp dụng cặp và dọn file thuộc T05.

#### Checklist

- [x] Ghi lựa chọn TTS-01: gemini-3.8-flash-tts/Charon; người dùng đã nghe sáu MP3 hài lòng, chưa duyệt bộ 20 cặp.
- [x] Khảo sát nguồn chính thức ngày 03/11 về API, vi/en/fr/ru, Free tier/quota/định dạng/lỗi và bài học demo; xem technical-inputs mục 3.
- [x] Ghi IN-P03 đã chọn WAV nguồn/MP3 khách và FFmpeg/ffprobe Linux container; chưa kiểm pipeline.
- [ ] Xác nhận quota thực tế/đầu vào giới hạn cần thiết và kiểm pipeline tích hợp theo TDD; tài liệu công bố chưa là thử phần mềm.
- [ ] Thực hiện kế hoạch nghe thử tên di tích/số liệu và các ngôn ngữ còn thiếu khi được giao; ghi bằng chứng/giới hạn, không thay duyệt từng cặp ở T08.
- [ ] Tích hợp tạo chủ động từ văn bản đã lưu: chưa tạo/đang tạo/thành công/lỗi, chống yêu cầu lặp, chưa rõ kết quả thì kiểm tra trước thử lại.
- [ ] Gắn kết quả đúng bản văn/ngôn ngữ lúc tạo; sửa văn bản không gắn audio cũ, tạo lỗi không hỏng cặp đang dùng và không tự áp dụng.
- [ ] Giữ tác vụ đã nhận qua hết phiên/đóng trang; bỏ kết quả muộn của điểm đã xóa, bàn giao file không dùng cho cơ chế dọn T05.
- [ ] Kiểm tra tích hợp, trạng thái, đúng bản và lỗi; lưu bằng chứng khả năng hỗ trợ thực tế cho T09 và đầu vào audio cho T08.

<a id="t08"></a>

### T08 - Nội dung: Hoàn thiện năm địa điểm và bộ thuyết minh bốn ngôn ngữ (Thành, Nghĩa, Hòa)

#### Description

Cả nhóm chuẩn bị và rà soát nội dung năm điểm × vi/en/fr/ru, đủ 20 văn bản và 20 audio khớp. Chia theo địa điểm khi thống nhất sau, chưa gán riêng. T07 hỗ trợ sinh audio; T08 chịu trách nhiệm nguồn, rà soát và hồ sơ duyệt từng bản.

#### Checklist

- [ ] Tiếp nhận bản lưu trong repo và rà soát năm nháp tiếng Việt người dùng đã gửi trong chat trước; chưa xác nhận file chứa bản nháp hoặc kết quả rà soát/duyệt.
- [ ] Hoàn thiện bài có nguồn cho Cổng Ngọ Môn, Điện Thái Hòa, Tử Cấm Thành (bài tổng quan), Cửu Đỉnh và Cung Diên Thọ; ghi giới hạn chưa kiểm chứng.
- [ ] Cung cấp căn cứ vị trí cho việc xem xét vùng ở T01; không coi đa giác đề xuất là ranh giới di tích đã xác minh.
- [ ] Chuẩn bị en/fr/ru, được rà soát phù hợp hoặc duyệt rõ từng bản cho demo kèm giới hạn theo D09; AI/dịch ngược không thay thẩm định chuyên gia.
- [ ] Chuẩn bị và rà soát tên/nhãn/thông báo giao diện; không tính các chuỗi UI thay cho bài thuyết minh.
- [ ] Phối hợp T07 tạo audio, nghe trọn từng bài, kiểm tra khớp văn bản/ngôn ngữ và không trống/cắt cụt; ghi hồ sơ duyệt văn bản/audio.
- [ ] Đối chiếu ma trận đủ 20 văn bản + 20 audio được duyệt, đủ căn cứ áp dụng cặp; làm một điểm vi → năm điểm vi → ngôn ngữ còn lại.

<a id="t09"></a>

### T09 - Kiểm thử: Đánh giá chức năng và nghiệm thu toàn hệ thống (Hòa)

#### Description

Hòa phụ trách kiểm thử tích hợp/nghiệm thu theo PRD và môi trường đã thống nhất. Mỗi người vẫn kiểm tra, sửa lỗi phần mình; T09 tổng hợp kết quả thực thi, không dùng rà soát tài liệu thay bằng chứng phần mềm.

#### Checklist

- [ ] Lập kịch bản truy vết FR/UC/AC/NFR và điều kiện đánh giá đã chốt; không tự đặt ngưỡng bắt buộc.
- [ ] Kiểm tra tích hợp khách/Admin/TTS/database/file từ sản phẩm T02–T07, ghi lỗi và người xử lý theo phạm vi.
- [ ] Kiểm tra phiên khách 8 tiếng/cảnh báo 5 phút/ngoại lệ nghe tiếp, Admin 30 phút/Q2/Q3, tải lại, mạng lỗi và kết quả chưa rõ/đến muộn.
- [ ] Kiểm tra áp dụng cả cặp, gate cặp vi, xóa giao dịch/giữ lịch sử, dọn file tách database và xóa dữ liệu 30 ngày.
- [ ] Kiểm tra bộ 20+20, số đếm/hai bảng thống kê và QR/URL/PNG/bản in thực tế trên môi trường T10.
- [ ] Lưu báo cáo/bằng chứng với kết luận đạt, chưa đạt hoặc chưa kiểm tra; kiểm tra lại sau sửa, không coi hàng chờ là đã xóa hoặc yêu cầu in là đã in.

<a id="t10"></a>

### T10 - Git và triển khai: Thiết lập quy trình làm việc, CI/CD và môi trường demo (Hòa)

#### Description

Hòa phụ trách quy trình cộng tác, CI/CD và môi trường demo, cung cấp ràng buộc sớm cho T01. Nền repo đã hiện hữu không chứng minh Hòa đã thực hiện hoặc quy trình Git đã được kiểm tra; kết quả vận hành chuyển cho T09/T11.

#### Checklist

- [x] Ghi nhận nền repo cục bộ có .git và .gitignore gốc; chỉ xác minh sự hiện diện.
- [x] Kiểm tra hiện trạng Git/remote/nhánh và thống nhất quy trình cộng tác khi được giao; chưa suy ra từ tên người hoặc file hiện có.
- [x] Ghi phương án thử ngrok Free/TECH-03 và nguồn domain-quota-trang trung gian tại technical-inputs mục 5; không đăng ký/cài/triển khai.
- [x] Ghi TECH-05/DATA-03: CD-L01 Tailscale riêng + OpenSSH Windows/PowerShell và phạm vi-nhịp backup đã chọn, chưa kiểm.
- [ ] Kiểm SSH và quyền/context Docker Desktop của tài khoản triển khai trước triển khai đầy đủ; kiểm ngrok/URL/HTTPS/QR khác mạng/quota/persistence.
- [ ] Hoàn thiện ignore/cấu hình/secret theo công nghệ và TDD; không mặc định đưa audio hoặc dữ liệu chạy vào Git.
- [ ] Thiết lập CI/CD với các kiểm tra phù hợp và lưu bằng chứng pipeline thực thi.
- [ ] Thiết lập, kiểm tra dịch vụ/database/file/log và backup cục bộ tách dữ liệu chạy theo DATA-02; giữ hạn/restore lọc mốc gốc, không yêu cầu ổ ngoài/mất toàn laptop; cung cấp URL thật cho QR chung.
- [ ] Xác minh demo và khả năng chạy lại theo hướng dẫn; phối hợp T09 đánh giá, bàn giao kết quả vận hành cho T11.

<a id="t11"></a>

### T11 - Tài liệu: Hoàn thiện hướng dẫn và bàn giao đồ án (Thành)

#### Description

Thành tổng hợp hướng dẫn và bàn giao theo sản phẩm thực tế; Nghĩa và Hòa cung cấp hướng dẫn, kết quả và bằng chứng phần mình. Cập nhật xuyên suốt khi có sản phẩm, không đợi cuối kỳ và không tính lại bộ phân tích/thiết kế của T01.

#### Checklist

- [ ] Soạn hướng dẫn chạy, cấu hình và khởi tạo theo sản phẩm/môi trường thực tế; ghi điều kiện, giới hạn, không đưa secret vào tài liệu.
- [ ] Hoàn thiện hướng dẫn khách, Admin và trình diễn theo chức năng đã chạy, gồm phiên, vùng, cặp nội dung, lỗi, thống kê và QR.
- [ ] Tập hợp hồ sơ nguồn/duyệt T08, kết quả kiểm thử T09 và vận hành T10; phân biệt đã kiểm chứng với chưa kiểm chứng.
- [ ] Đồng bộ phiên bản, liên kết, trạng thái và giới hạn cuối; chuẩn bị bàn giao đúng đường dẫn và việc tiếp theo.
- [ ] Kiểm tra người nhận tìm đúng tài liệu và thực hiện được các hướng dẫn trên môi trường thống nhất; lưu bằng chứng phù hợp.

## 4. Đầu vào còn mở và bàn giao

Giữ backlog/G02/nghiệp vụ/UI/TECH-01–05/TTS-01/DATA-01–03/G03; không hỏi lại phần đã chốt. Đã build/test phần backend với bộ phiên bản đã ghi; frontend và tương thích toàn bộ chưa kiểm, ngrok vẫn thử. CD-L01/phạm vi-nhịp backup đã chọn, còn kiểm SSH/quyền Docker, path/quyền/công cụ/checkpoint/log/RPO-RTO, giới hạn thực và cơ chế file/nghe tiếp. Không đóng R06 hoặc T01/T07 theo gói.

Đọc docs/README → nguồn quyết định → technical-inputs hiện hành → TDD hiện hành → data-model/API/operations/thực hành 01 → PRD/UI hiện hành; đối chiếu phiên bản/trạng thái trước dùng. Backend đã có API K11/K01/K02 và 64 test đạt; chưa toàn TDD hoặc nghiệm thu toàn hệ thống. T01–T11 giữ ranh giới, không kế thừa kế hoạch demo T01–T24. Hợp đồng API/giao dịch ở file riêng được dẫn từ TDD; trạng thái đã/chưa triển khai được ghi theo từng phần, không phủ nhận code hiện có.

<a id="target-1711"></a>

## 5. Lịch mục tiêu 17/11/2026 — dự kiến, không phải cam kết

Mốc mục tiêu dự án là 17/11/2026, không phải hạn nộp chính thức của môn học. Lịch dưới phân công theo G02, phụ thuộc và sản phẩm cần đạt; có thể triển khai song song khi hợp đồng dữ liệu/API đủ rõ. Ước lượng ban đầu 162–246 giờ người là căn cứ tham khảo, chưa phải thời gian thực tế hoặc cam kết quỹ giờ của thành viên. Theo dõi sản phẩm và giờ thực tế để cập nhật lịch; không hứa chắc hoàn tất hoặc tự giảm phạm vi đã chốt.

Thành phụ trách T01/T03/T05/T07/T11; Nghĩa phụ trách T04/T06; Hòa phụ trách T02/T09/T10; T08 cả nhóm. Người commit theo task tại G02. T08/T10/T11 được làm xuyên suốt, không chờ tuần tự theo số task.

| Khoảng ngày | Công việc cụ thể | Người phụ trách theo G02 | Phụ thuộc | Kết quả quan sát / cổng |
| --- | --- | --- | --- | --- |
| 03/11 | Ghi đầu vào đã chốt; TDD 0.4/model/API/operations dự thảo, DTO/lifecycle/backup và V-*; căn cứ checklist thuộc lượt trước | Thành T01/T07, Hòa T10 | Nguồn đã chốt/repo/official docs | Đã có tài liệu dự thảo, chưa toàn TDD được duyệt hoặc code/test/CD; không đọc/thay GitHub trong lượt hoàn thiện này |
| 04/11 | Rà ERD/dữ liệu đã dự thảo, rà soát hợp đồng API đã có dự thảo/quyền/idempotency/job; chuẩn bị kế hoạch test; bắt đầu kiểm môi trường theo hướng dẫn khi được giao | Thành T01, Hòa T02/T09 | Kiến trúc/TECH-03; người dùng trực tiếp thực hành | TDD phần dữ liệu/API, ma trận môi trường thật và kết quả khả dụng; chưa ghi sẵn đạt |
| 05–07/11 | Lát cắt một điểm vi: DB/migrations → Admin vùng/văn → TTS Linux → nghe/hồ sơ/cặp → khách bản đồ/audio; CI và một CD thực sớm/ngrok khác mạng | Hòa T02/T10, Thành T03/T05/T07, Nghĩa T04/T06; cả nhóm T08 | TDD dữ liệu/API, kênh CD chọn, bài vi/nguồn/duyệt và quyền gọi TTS riêng, quota | Một hành trình chạy + run/SHA CI-CD + QR/HTTPS thực; hết 07/11 chưa có thì báo nguy cơ trễ ngay |
| 08–10/11 | Hoàn thiện địa điểm/vùng/cặp/phiên/Q2-Q3/thống kê/QR, lỗi/chưa rõ-kết quả muộn; năm cặp vi; persistence/backup-restore | Thành T03/T05/T07, Nghĩa T04/T06, Hòa T02/T10, cả nhóm T08 | Lát cắt đã chạy, chính sách log/backup chi tiết, dữ liệu có nguồn | Luồng đầy đủ khách/Admin, năm cặp vi được duyệt, backup/restore có bằng chứng; tới 10/11 thiếu thì đánh giá lại lịch/nguồn lực |
| 11–13/11 | Bổ sung en/fr/ru, nghe trọn và hồ sơ đủ 20+20; hoàn thiện trạng thái/các lỗi; CD đúng bản/rollback | Cả nhóm T08, Thành T07/T05, Nghĩa T04/T06, Hòa T10 | Nội dung/rà soát/quota thực, hệ thống tích hợp | Ma trận 20+20 đủ hồ sơ, bản đúng SHA và rollback đã kiểm; không coi tạo thành công là duyệt |
| 14–15/11 | Kiểm tích hợp theo FR/UC/AC/NFR, Android/Chrome+iPhone/Safari+Admin Windows/Chrome, QR khác mạng/trang ngrok; expiry/giữ tạm/cặp/xóa-file/restore | Hòa T09, chủ phần sửa lỗi, Thành T11 tổng hợp | Phần mềm đã chạy, nội dung đủ, matrix build thực | Báo cáo đạt/chưa đạt/chưa kiểm tra và kiểm lại lỗi; mô phỏng clock/dữ liệu test, không chờ 30 ngày |
| 16/11 | **Dự phòng 1 ngày**: lỗi chặn/demo, quota/mạng, kiểm lại sau sửa; diễn tập và chốt bằng chứng/hướng dẫn | Hòa T09/T10, Thành T11, Nghĩa hỗ trợ UI | Kết quả 14–15/11 | Bản ổn định + hướng dẫn/evidence thực; không dùng dự phòng mở chức năng |
| 17/11 | **Dự phòng buổi đầu**, kiểm URL/quota/QR/bản demo; trình diễn sớm nhất bản đã kiểm và bàn giao | Theo G02, Thành tổng hợp | Bản và dữ liệu được duyệt, Internet | Demo đúng bản kiểm, nêu đúng phần chưa đạt; không tự đóng task vì tới ngày |

Cổng 07/11 hoặc 10/11 trễ: báo thiếu sản phẩm nào, phụ thuộc và tác động; người dùng quyết định tăng nguồn lực/thời gian hoặc điều chỉnh **mốc mục tiêu**, không tự bỏ 20+20/CI-CD/ngoại lệ. Lịch là kế hoạch phân công; mức khả thi phải đối chiếu quỹ giờ thực tế, phụ thuộc và bằng chứng sản phẩm. Cập nhật theo sản phẩm/giờ thật khi thực hành, không hỏi lại quỹ giờ chung để trì hoãn thiết kế.

<a id="github-checklist-review"></a>

## 6. Tạo lại issue và đối chiếu GitHub

### 6.1. Bản đã duyệt và dấu tích có căn cứ

Nguồn của tiêu đề, Description và Checklist là **mục 3**. Backlog 89 ô/18 dấu tích được duyệt ngày 06/11/2026; bảng dưới giữ số đếm tại mốc đó. Hai dấu tích local bổ sung ngày 09/11 được ghi riêng tại mục 6.5, chưa suy thành GitHub đã cập nhật.

| Task | Tổng ô | Đã tích | Phạm vi của dấu tích |
| --- | --- | --- | --- |
| T01 | 18 | 12 | Bảy kết quả phân tích/tài liệu cũ và năm mục ghi công nghệ, đầu vào, khảo sát, mốc phiên bản, chính sách đã chọn; chưa toàn TDD/môi trường/roadmap triển khai |
| T02 | 5 | 0 | V1–V9/audio/cặp và seed riêng đã kiểm, bằng chứng tại data-model 7.5; các dấu tích giữ nguyên bản đã đối chiếu, chưa toàn mô hình/quyền hoặc dữ liệu duyệt |
| T03 | 6 | 0 | K11/K01/K02 và phần kết thúc K04 đã kiểm backend, còn grant/nội dung/audio/lượt xem và điều kiện toàn ô |
| T04 | 7 | 0 | Chưa frontend khách được kiểm |
| T05 | 9 | 0 | Xác thực/Q3, window/receipt, tạo/đọc/sửa tên có bằng chứng; chưa đủ Q2 tích hợp hoặc toàn ô nghiệp vụ quản trị |
| T06 | 8 | 0 | Chưa frontend Admin được kiểm |
| T07 | 9 | 3 | Khảo sát nguồn cũ, model/giọng và định dạng đã chọn; chưa pipeline/duyệt bộ audio |
| T08 | 7 | 0 | Đã báo cáo có bản nháp, chưa xác minh bản lưu/rà soát/duyệt |
| T09 | 6 | 0 | Chưa nghiệm thu toàn hệ thống |
| T10 | 9 | 3 | Nền Git đã ghi nhận, phương án thử ngrok và kênh CD/phạm vi-nhịp backup đã chọn; chưa vận hành đạt |
| T11 | 5 | 0 | Chưa hướng dẫn/bàn giao cuối được kiểm |
| **Tổng** | **89** | **18** | Không quy đổi thành phần trăm hoàn thành đồ án |

Mỗi dấu tích chỉ ghi nhận đúng phần đã có căn cứ. Không tích lại ô đã hoàn thành và không suy trạng thái Project hoặc kết quả phần mềm từ việc duyệt backlog.

### 6.2. Mapping issue và bằng chứng Project ngày 06/11

Repository: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue). Project: [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Mapping được đối chiếu ngày 06/11/2026. Status ghi theo xác nhận vận hành cùng ngày: 6 In Progress/5 Todo/0 Done; chưa có lần kiểm trực tiếp mới.

Ngày 08/11 đã tạo và đọc lại issue #1–#11 trên repository chính thức có ID `1449659323`, sau khi xác nhận repository này chưa có issue. URL của từng task trùng bảng dưới và đã được đối chiếu lại với mục 3. Cột Status giữ bằng chứng ngày 06/11, xác nhận vận hành mới ngày 08/11 ở mục 6.4.

| Task | Issue — đã đọc lại ngày 08/11 | Status — xác nhận vận hành ngày 06/11 |
| --- | --- | --- |
| [T01](#t01) | [#1](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/1) | In Progress |
| [T02](#t02) | [#2](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/2) | In Progress |
| [T03](#t03) | [#3](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/3) | In Progress |
| [T04](#t04) | [#4](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/4) | Todo |
| [T05](#t05) | [#5](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/5) | Todo |
| [T06](#t06) | [#6](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/6) | Todo |
| [T07](#t07) | [#7](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/7) | In Progress |
| [T08](#t08) | [#8](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/8) | In Progress |
| [T09](#t09) | [#9](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/9) | Todo |
| [T10](#t10) | [#10](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/10) | In Progress |
| [T11](#t11) | [#11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/11) | Todo |

Theo xác nhận vận hành ngày 06/11, chưa có task nào Done. Tài khoản theo phân công được quy định tại [G02](../02-nghiep-vu/decisions.md#g02). Các issue ngày 08/11 được tạo với tiêu đề và nội dung đã duyệt, chưa tự gán assignee. Trước khi ghi nhận hoàn thành, đối chiếu đúng URL issue, nguyên văn ô và bằng chứng đủ toàn ô.

### 6.3. Trạng thái bàn giao

1. Ngày 08/11/2026 đã tạo và đọc lại đủ issue T01–T11 tương ứng #1–#11 trên repository chính thức. Tiêu đề và toàn body khớp mục 3, mỗi issue có một Description và một Checklist, tổng 89 ô/18 dấu tích. Toàn văn mục 3 được giữ nguyên.
2. Người vận hành xác nhận đã thêm đủ #1–#11 vào Project 21 và đặt Status theo mục 6.4 ngày 08/11. Kết quả ghi theo xác nhận vận hành, chưa có lần kiểm trực tiếp mới bằng công cụ.
3. Ba nhóm commit/push trước lần tạo lại repository giữ bằng chứng tại [handoff mục 2.2](handoff.md#bootstrap-result). Không suy các SHA này đã có trên repository ID `1449659323`. Nền T10 tại repository hiện tại đã commit/push d61a869, SHA local/remote khớp theo [handoff mục 2.11](handoff.md#current-t10-commit-result). Commit-plan đã khôi phục, nhóm tài liệu đủ 33 file và được rà trước T01 theo [mục 2.12](handoff.md#document-review-result).
4. Tiếp tục code và kiểm thử tách biệt theo [handoff mục 7.3](handoff.md#execution-mode), dừng ở mục triển khai có bằng chứng để rà soát. Việc tạo issue, có code hoặc commit không hoàn tất toàn task.
5. Điểm dừng triển khai và kết quả kiểm chứng gần nhất tại [handoff mục 2](handoff.md#next-step) và [thực hành phiên khách](../03-ky-thuat/practice-01-start-session.md#practice-stop).

<a id="new-project-ready"></a>

### 6.4. Bộ issue và trạng thái Project xác nhận ngày 08/11

Đã kiểm lại đủ 11 tiêu đề, mỗi issue có đúng một Description và một Checklist từ mục 3. Bảng 6.1 khớp số ô/dấu tích từng task. G02 giữ Thành dùng khaithanh2726-beep, Hòa dùng gingpham, Nghĩa dùng lechiNghiaa; T08 theo phần việc cả nhóm.

| Status đã được người vận hành xác nhận | Task | Số issue |
| --- | --- | --- |
| In Progress | T01, T02, T03, T07, T08, T10 | 6 |
| Todo | T04, T05, T06, T09, T11 | 5 |
| Done | Chưa có task đủ bằng chứng nghiệm thu toàn issue | 0 |

Phân loại này được đề xuất theo bằng chứng sản phẩm và đã được người vận hành xác nhận đặt xong ngày 08/11. Chưa có lần kiểm trực tiếp mới bằng công cụ, trạng thái Project 21 ngày 06/11 ở bảng 6.2 giữ nguồn và thời điểm riêng.

Sau khi nền xác thực Admin có kết quả tại [handoff mục 2.7](handoff.md#admin-auth-result), đề xuất chuyển riêng T05/#5 từ Todo sang In Progress. Nếu được cập nhật, Project sẽ có 7 In Progress và 4 Todo. Chưa có xác nhận thao tác này, bảng trên tiếp tục giữ trạng thái thực tế được báo cáo gần nhất.

Issue #1–#11 đã được tạo và đối chiếu theo hai URL chính thức, đã thêm vào Project theo xác nhận vận hành. Mọi thao tác sửa/tích/đóng issue tiếp tục do người vận hành thực hiện trừ hành động cụ thể được giao thêm. Nguồn phát hành tiếp tục là mục 3, giữ nguyên 89 ô/18 dấu tích.

<a id="local-checklist-20261109"></a>

### 6.5. Checklist local ngày 09/11/2026

Bản lưu được đọc đầu đợt V9 đã có 89 ô/20 dấu tích: T01 12, T02 1, T07 3, T10 4; các task khác 0. Hai ô bổ sung so với mốc đối chiếu GitHub là T02 ô 2 (định danh/lịch sử địa điểm) và T10 ô 2 (kiểm Git/remote/nhánh và quy trình cộng tác), đúng hai kết quả đã được rà trước đó. Giữ nguyên toàn bộ tiêu đề, Description, Checklist và dấu tích của bản lưu; đợt V9 không tự tích thêm ô.

V9/audio/cặp và seed năm điểm/20 ngăn có bằng chứng tại [data-model 7.5](../03-ky-thuat/data-model.md#model-audio-pair-result); không tự coi toàn T02 hoàn tất. Bảng 6.1 và các kết quả 06–08/11 giữ mốc 18 dấu tích. Chưa đọc lại nội dung issue/Project trong lượt này, không khẳng định GitHub cũng đã có 20 dấu tích hoặc đổi Status. Không quy đổi số ô thành phần trăm hoàn thành đồ án.


<a id="guest-content-progress"></a>

### 6.6. Mốc backend nội dung khách và nguồn frontend ngày 09/11

[Handoff 2.24](handoff.md#guest-content-frontend-result) và [bằng chứng API](../03-ky-thuat/api-transactions.md#guest-content-test-result) ghi V10/K03/K05–K10, kiểm backend và nguồn Vue khách chưa build. Giữ nguyên toàn mục 3 và 89 ô/20 dấu tích; chưa coi các ca backend hoặc nguồn frontend là đủ nghiệm thu một ô T03/T04. Không cập nhật GitHub trong đợt này.

Theo sản phẩm hiện có, đề xuất thêm T04/#4 vào In Progress cùng đề xuất T05/#5 trước đó. Nếu cả hai được đặt thì có 8 In Progress/3 Todo/0 Done. Đây là đề xuất; trạng thái được xác nhận gần nhất ở mục 6.4 vẫn 6 In Progress/5 Todo/0 Done, không ghi kế hoạch thành thao tác đã làm.

<a id="guest-retention-progress"></a>

### 6.7. Dọn metadata và phiên/lượt D08 ngày 10/11

[Handoff 2.26](handoff.md#guest-retention-result) dẫn tới nguồn/test retention mới: hết phiên dọn context, tới anchor gốc +30 ngày xóa phiên/lượt trong TX, giữ an toàn READ claim. 24 ca mới đạt và hồi quy toàn cây có [bằng chứng](../03-ky-thuat/operations-design.md#ops-guest-retention-result). Chưa bật scheduler vận hành, xử lý claim rò hoặc đủ toàn ô tự xóa/nghiệm thu T05; chưa hoàn tất cơ chế file/window/receipt/job/backup/restore. Giữ nguyên mục 3, 89 ô/20 dấu tích; không tự sửa issue/Project.

## 7. Nhật ký và snapshot theo thời điểm



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-roadmap-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
