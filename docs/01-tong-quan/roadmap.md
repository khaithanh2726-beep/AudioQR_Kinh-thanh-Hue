# Phân công, checklist và kế hoạch hoàn thiện

## 1. Phạm vi quản lý công việc

Đồ án gồm 11 task và 89 ô checklist theo G02. Dấu tích chỉ ghi khi đủ toàn bộ điều kiện của ô; không biểu thị phần trăm hoàn thành toàn đồ án. Nội dung từng ô được giữ nguyên để đối chiếu GitHub; phần mô tả trạng thái lịch sử trong ô không thay kết quả hiện tại dưới đây.

## 2. Trạng thái đã đối chiếu

GitHub và bản local cùng **62/89 ô**, sau đợt rà 13/11: thêm 34 ô so với 28 ô GitHub trước đó, đồng bộ năm ô T03/T05 đã có. Cả 11 issue vẫn mở; chưa đổi assignee hoặc Project. Các phần còn thiếu được tổng hợp tại [hiện trạng bàn giao](handoff.md) và [báo cáo kiểm thử](../06-kiem-thu/README.md).

| Task | Phụ trách | Đủ bằng chứng / tổng |
|---|---|---:|
| T01 | Thành | 12/18 |
| T02 | Hòa | 4/5 |
| T03 | Thành | 6/6 |
| T04 | Nghĩa | 5/7 |
| T05 | Thành | 9/9 |
| T06 | Nghĩa | 7/8 |
| T07 | Thành | 7/9 |
| T08 | Cả nhóm | 2/7 |
| T09 | Hòa | 3/6 |
| T10 | Hòa | 5/9 |
| T11 | Thành | 2/5 |

## 3. Checklist công việc

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
- [x] Thiết lập ràng buộc và khóa ngoại chặn xóa trực tiếp khi còn tham chiếu ở quan hệ cần chặn; không dùng ON DELETE CASCADE tại đó.
- [x] Viết migrations và dữ liệu khởi tạo tái tạo được cho năm điểm/bốn ngôn ngữ; không phục vụ nội dung chưa duyệt hoặc đưa secret vào repo.
- [x] Kiểm tra migrations, khởi tạo và ràng buộc; lưu bằng chứng phù hợp TDD, bàn giao cho Backend và T09.

<a id="t03"></a>

### T03 - Backend khách: Xây dựng API phiên tham quan và nội dung (Thành)

#### Description

Xây API phiên tham quan, nội dung được phép phục vụ và ghi lượt xem. Thành phối hợp T02 về dữ liệu, T04 về giao diện và T05 về cặp nội dung/file; không tính API hoàn thành là giao diện đã chạy.

#### Checklist

- [x] Xây bắt đầu/kết thúc/kiểm tra phiên: tên hợp lệ, phiên theo tab, hạn cố định 8 tiếng, không tự gia hạn hoặc tạo trùng.
- [x] Cung cấp địa điểm/vùng và cặp đúng ngôn ngữ được phép hiển thị; không đưa nháp, nửa cặp hoặc audio sai bản ra khách.
- [x] Ghi lượt xem khi chủ động mở địa điểm và tải văn bản thành công; không cộng do tải lại, đổi ngôn ngữ, nghe/tua hoặc thử lại.
- [x] Giữ đúng quyền khi hết hạn: chỉ tiếp tục/tạm dừng bài đang phát; không mở bài mới, tua, nghe lại hoặc khôi phục ngoại lệ sau tải lại.
- [x] Xử lý thiếu nội dung, lỗi/chưa rõ kết quả và phục vụ file theo hợp đồng/TDD; không ghi lượt vào phiên hết hạn.
- [x] Kiểm tra API chính, lỗi, mốc thời gian và chống lặp; lưu bằng chứng cho T04/T09.

<a id="t04"></a>

### T04 - Frontend khách: Xây dựng giao diện bản đồ và nghe thuyết minh (Nghĩa)

#### Description

Xây giao diện khách theo hướng UI đã chốt, kết nối API T03 và dùng nội dung/nhãn được phép từ T08. Nghĩa phụ trách thao tác, trạng thái và thích ứng điện thoại; việc xem xét ranh giới thuộc T01.

#### Checklist

- [x] Xây trang bắt đầu riêng: tên, bốn ngôn ngữ, mặc định vi; bắt đầu/kết thúc có phản hồi và ngăn bấm lặp.
- [x] Xây bản đồ chọn toàn vùng; phân biệt kéo/phóng, bấm lại/ngoài/chồng lấn và chọn liên tục; chỉ một audio, ưu tiên lựa chọn mới nhất.
- [x] Xây bố cục bản đồ, tên điểm, điều khiển audio phía trên box văn bản cuộn; một nút Phát/Tạm dừng/Nghe lại và thanh tua; tải trọn MP3 hiện tại cho tab trước phát, quản lý Blob/late response đúng TECH-04.
- [x] Xử lý đổi ngôn ngữ, thiếu bản dịch/audio và audio lỗi đúng quyền; không tự chuyển ngôn ngữ hoặc phát nhầm bài.
- [x] Xây cảnh báo 5 phút, box kết thúc toàn trang và box hết hạn trong bản đồ; nghe tiếp đúng ngoại lệ, sang form mới thì dừng bài cũ.
- [ ] Khôi phục khi tải lại, xử lý mất mạng/thử lại và các trạng thái tải/rỗng/lỗi theo yêu cầu.
- [ ] Kiểm tra luồng khách với API trên điện thoại/môi trường đã thống nhất; lưu bằng chứng cho T09.

<a id="t05"></a>

### T05 - Backend Admin: Xây dựng API quản trị và xử lý dữ liệu (Thành)

#### Description

Xây API quản trị, áp dụng nội dung, thống kê và xử lý dữ liệu theo T01/T02. Thành phối hợp T07 qua hợp đồng tạo/theo dõi audio; T05 giữ trách nhiệm quyền quản trị, áp dụng cặp, xóa dữ liệu và dọn file.

#### Checklist

- [x] Xây đăng nhập/đăng xuất và phiên 30 phút theo Q3; hỗ trợ Q2, không hồi sinh phiên hoặc tự gửi lại sau xác thực.
- [x] Xây quản lý địa điểm theo Q1, vùng và bản nháp/hồ sơ; giữ bản hiện hành khi sửa, tách Lưu bản sửa với Áp dụng.
- [x] Chỉ áp dụng cả cặp văn bản/audio đã duyệt, đúng bản/ngôn ngữ; lỗi giữ cặp cũ, chưa rõ kết quả thì kiểm tra trước thử lại.
- [x] Chỉ bật hiện khi thông tin hợp lệ, vùng hợp lệ đã xem xét/áp dụng và ít nhất cặp vi đã duyệt/áp dụng; áp dụng không tự bật hiện.
- [x] Điều phối xóa dữ liệu riêng trong giao dịch, bảo vệ dữ liệu dùng chung/hồ sơ tài liệu/lịch sử; vô hiệu liên kết TTS để kết quả muộn không phục hồi địa điểm.
- [x] Tự xóa phiên và lượt xem sau 30 ngày từ kết thúc/hết hạn; thống kê chỉ dùng dữ liệu còn giữ.
- [x] Dọn file sau database thành công: không còn tham chiếu và không dùng chung, bảo vệ 8 tiếng từ ngừng áp dụng cuối cùng, có chờ/thử lại; xử lý file khác theo chính sách được chốt.
- [x] Cung cấp danh sách phiên chỉ đọc, thống kê đúng mốc/Asia/Saigon và đầu vào QR chung khi cần; không thêm chức năng ngoài phạm vi.
- [x] Kiểm tra API, quyền, cặp, hoàn tác, lịch sử và kết quả xóa/dọn thực tế; lưu bằng chứng cho T06/T09.

<a id="t06"></a>

### T06 - Frontend Admin: Xây dựng giao diện quản trị đầy đủ (Nghĩa)

#### Description

Xây giao diện Admin đầy đủ theo UI hiện hành, kết nối T05/T07. Nghĩa phụ trách trạng thái thao tác và bảo vệ phần đang soạn; giao diện không tự thẩm định hồ sơ hoặc báo tác vụ thành công khi thiếu căn cứ.

#### Checklist

- [x] Xây đăng nhập, khung quản trị, danh sách và form địa điểm; thêm/sửa/ẩn-hiện/xóa có phản hồi và xác nhận xóa.
- [x] Xây vẽ/sửa/xem trước/Lưu bản sửa/Áp dụng vùng; chỉ áp dụng đúng bản đã lưu và xem, không bắt xem lại sau lưu nếu hình không đổi.
- [x] Xây soạn/đối chiếu bản khách chỉ đọc, Hồ sơ duyệt của cặp và nút Áp dụng văn bản và audio; chỉ rõ điều kiện còn thiếu.
- [x] Xây tạo/theo dõi/kiểm tra/thử lại/nghe thử audio; tạo thành công khác được duyệt và đã áp dụng.
- [x] Bảo vệ phần chưa lưu và tham chiếu theo Q2/Q3; hết phiên che nội dung, dừng nghe thử, đăng nhập lại không tự phát hoặc gửi lại.
- [x] Xây danh sách phiên và box UI-A21 giữ bộ lọc; hai bảng thống kê/tổng và mốc ngày theo Asia/Saigon.
- [x] Xây trang một QR chung, URL chỉ đọc, tải PNG và bản in gọn; chưa URL thật thì chưa sẵn sàng, không sinh QR giả.
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
- [x] Tích hợp tạo chủ động từ văn bản đã lưu: chưa tạo/đang tạo/thành công/lỗi, chống yêu cầu lặp, chưa rõ kết quả thì kiểm tra trước thử lại.
- [x] Gắn kết quả đúng bản văn/ngôn ngữ lúc tạo; sửa văn bản không gắn audio cũ, tạo lỗi không hỏng cặp đang dùng và không tự áp dụng.
- [x] Giữ tác vụ đã nhận qua hết phiên/đóng trang; bỏ kết quả muộn của điểm đã xóa, bàn giao file không dùng cho cơ chế dọn T05.
- [x] Kiểm tra tích hợp, trạng thái, đúng bản và lỗi; lưu bằng chứng khả năng hỗ trợ thực tế cho T09 và đầu vào audio cho T08.

<a id="t08"></a>

### T08 - Nội dung: Hoàn thiện năm địa điểm và bộ thuyết minh bốn ngôn ngữ (Thành, Nghĩa, Hòa)

#### Description

Cả nhóm chuẩn bị và rà soát nội dung năm điểm × vi/en/fr/ru, đủ 20 văn bản và 20 audio khớp. Chia theo địa điểm khi thống nhất sau, chưa gán riêng. T07 hỗ trợ sinh audio; T08 chịu trách nhiệm nguồn, rà soát và hồ sơ duyệt từng bản.

#### Checklist

- [x] Tiếp nhận bản lưu trong repo và rà soát năm nháp tiếng Việt người dùng đã gửi trong chat trước; chưa xác nhận file chứa bản nháp hoặc kết quả rà soát/duyệt.
- [ ] Hoàn thiện bài có nguồn cho Cổng Ngọ Môn, Điện Thái Hòa, Tử Cấm Thành (bài tổng quan), Cửu Đỉnh và Cung Diên Thọ; ghi giới hạn chưa kiểm chứng.
- [x] Cung cấp căn cứ vị trí cho việc xem xét vùng ở T01; không coi đa giác đề xuất là ranh giới di tích đã xác minh.
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
- [x] Kiểm tra tích hợp khách/Admin/TTS/database/file từ sản phẩm T02–T07, ghi lỗi và người xử lý theo phạm vi.
- [ ] Kiểm tra phiên khách 8 tiếng/cảnh báo 5 phút/ngoại lệ nghe tiếp, Admin 30 phút/Q2/Q3, tải lại, mạng lỗi và kết quả chưa rõ/đến muộn.
- [x] Kiểm tra áp dụng cả cặp, gate cặp vi, xóa giao dịch/giữ lịch sử, dọn file tách database và xóa dữ liệu 30 ngày.
- [ ] Kiểm tra bộ 20+20, số đếm/hai bảng thống kê và QR/URL/PNG/bản in thực tế trên môi trường T10.
- [x] Lưu báo cáo/bằng chứng với kết luận đạt, chưa đạt hoặc chưa kiểm tra; kiểm tra lại sau sửa, không coi hàng chờ là đã xóa hoặc yêu cầu in là đã in.

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
- [x] Hoàn thiện ignore/cấu hình/secret theo công nghệ và TDD; không mặc định đưa audio hoặc dữ liệu chạy vào Git.
- [ ] Thiết lập CI/CD với các kiểm tra phù hợp và lưu bằng chứng pipeline thực thi.
- [ ] Thiết lập, kiểm tra dịch vụ/database/file/log và backup cục bộ tách dữ liệu chạy theo DATA-02; giữ hạn/restore lọc mốc gốc, không yêu cầu ổ ngoài/mất toàn laptop; cung cấp URL thật cho QR chung.
- [ ] Xác minh demo và khả năng chạy lại theo hướng dẫn; phối hợp T09 đánh giá, bàn giao kết quả vận hành cho T11.

<a id="t11"></a>

### T11 - Tài liệu: Hoàn thiện hướng dẫn và bàn giao đồ án (Thành)

#### Description

Thành tổng hợp hướng dẫn và bàn giao theo sản phẩm thực tế; Nghĩa và Hòa cung cấp hướng dẫn, kết quả và bằng chứng phần mình. Cập nhật xuyên suốt khi có sản phẩm, không đợi cuối kỳ và không tính lại bộ phân tích/thiết kế của T01.

#### Checklist

- [x] Soạn hướng dẫn chạy, cấu hình và khởi tạo theo sản phẩm/môi trường thực tế; ghi điều kiện, giới hạn, không đưa secret vào tài liệu.
- [ ] Hoàn thiện hướng dẫn khách, Admin và trình diễn theo chức năng đã chạy, gồm phiên, vùng, cặp nội dung, lỗi, thống kê và QR.
- [x] Tập hợp hồ sơ nguồn/duyệt T08, kết quả kiểm thử T09 và vận hành T10; phân biệt đã kiểm chứng với chưa kiểm chứng.
- [ ] Đồng bộ phiên bản, liên kết, trạng thái và giới hạn cuối; chuẩn bị bàn giao đúng đường dẫn và việc tiếp theo.
- [ ] Kiểm tra người nhận tìm đúng tài liệu và thực hiện được các hướng dẫn trên môi trường thống nhất; lưu bằng chứng phù hợp.


## 4. Thứ tự hoàn thiện

1. Dữ liệu: đủ 20 văn bản/audio đã được duyệt dùng học phần và áp dụng; API Khách kiểm Hash 20/20. Giữ giới hạn hồ sơ khi thay revision.
2. Tích hợp: kiểm năm điểm/bốn tiếng, lỗi/time/tab/thiết bị và ma trận FR/UC/AC/NFR.
3. Vận hành: CD/SSH/Tailscale, crash/claim, lịch log/backup và mốc chuyển phục vụ.
4. Bàn giao: đồng bộ hướng dẫn, hồ sơ và lượt người nhận thực hiện lại.

T01/T11 do Thành; T02/T09/T10 do Hòa; T03/T05/T07 do Thành; T04/T06 do Nghĩa; T08 cả nhóm. Người thao tác, tác giả commit và tài khoản push là các vai trò khác nhau. Lịch triển khai chi tiết cũ và phân nhóm commit đã được gom trong hồ sơ nội bộ, không dùng như kết quả nghiệm thu.

## 5. Đề xuất bổ sung theo bằng chứng ngày 14/11

Đã đọc lại 11 issue trên GitHub: 62/89 ô, cùng dấu tích với mục 3. Không sửa issue/Project hoặc tự đổi checklist local trong đợt này. Bản đối chiếu từng ô giữ trong hồ sơ nội bộ `van-hanh-tu-dong-20261114`; 62 ô đã tích giữ bằng chứng mốc trước, không coi là đã kiểm lại toàn bộ ngày 14/11.

| Issue / nguyên văn ô chưa tích | Đề xuất và căn cứ |
|---|---|
| [T01 #1](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/1): Hoàn thiện path/quyền/công cụ/checkpoint backup và chi tiết log/cách kiểm phục hồi; DATA-03 đã chốt phạm vi/nhịp, chưa coi mọi lớp đã kiểm. | Đề xuất tích sau rà: kho có ACL, root riêng/junction guard, gate/checkpoint, lịch 23:00 và lượt task thật/restore hai file đạt; log/retention/giới hạn hạ tầng được mô tả cụ thể. Không suy mọi lớp hoặc RPO/RTO đã đạt. |
| [T10 #10](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/10): Thiết lập CI/CD với các kiểm tra phù hợp và lưu bằng chứng pipeline thực thi. | Đề xuất tích sau rà: CI và workflow CD thật 43486032664 đạt; source/image/volume/khóa và audio được đối chiếu. Step retention bổ sung đạt 9/9 trên CI nguồn 3f66b72, tổng nhóm vận hành 29/29. Lượt push không tự deploy lại. |
| [T09 #9](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/9): Lập kịch bản truy vết FR/UC/AC/NFR và điều kiện đánh giá đã chốt; không tự đặt ngưỡng bắt buộc. | Đề xuất tích sau rà: ma trận đủ 23 FR/14 UC/50 AC/6 NFR, ca nguồn/bằng chứng/điều kiện chưa khép; không đặt RPO/RTO hoặc biến fixture thành thiết bị thật. |
| [T11 #11](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/issues/11): Hoàn thiện hướng dẫn khách, Admin và trình diễn theo chức năng đã chạy, gồm phiên, vùng, cặp nội dung, lỗi, thống kê và QR. | Đề xuất tích sau rà: hướng dẫn sử dụng/chạy/tắt/backup/restore và QR PNG hiện hành; có thêm tra cứu file/dòng và liên kết ma trận. Không đồng nghĩa người nhận đã tự chạy lại. |

Nếu bốn đề xuất được duyệt và cập nhật, số ô sẽ là 66/89; hiện vẫn 62/89. Các ô rộng hơn về môi trường/thiết bị, vận hành cuối, tái lập bởi người nhận và dữ liệu 20+20 được duyệt/áp dụng giữ trạng thái mở. Ở thời điểm lập đề xuất này có 20 audio kỹ thuật; mốc xuất bản sau đó tại mục 6 đã có hồ sơ chủ đồ án chấp thuận 19 cặp mới và đủ 20 cặp áp dụng. Giữ dấu tích thực tế riêng với trạng thái sản phẩm. TTL báo cáo lịch bảy ngày khác giới hạn dung lượng buffer Docker; chưa dùng phép kiểm này để khẳng định mọi log hạ tầng tự xóa theo tuổi hoặc mọi ca crash đã được giải quyết.


## 6. Đối chiếu mốc xuất bản đủ bộ

Ngày 14/11: hồ sơ chủ đồ án duyệt 19 cặp mới đã được ghi, bốn vùng/19 cặp áp dụng qua API và năm điểm hiển thị. Bộ API thật đạt 20/20 bài/MP3, giữ cặp Ngọ Môn vi và dữ liệu cũ. Lịch backup tự kích hoạt một lần đạt 40 tệp, mã task 0; trigger thử đã gỡ, lịch 23:00 giữ nguyên. CI mốc 1bab9a6 đạt 530 backend/71 frontend/33 vận hành/build. Các bằng chứng này bổ sung cho T08/T09/T10/T11, không tự đóng ô nhiều vế còn thiếu kiểm thiết bị hoặc người nhận. Ma trận thiết bị thực hiện ở lượt cuối; chưa sửa dấu tích mục 3 hoặc GitHub Project.
