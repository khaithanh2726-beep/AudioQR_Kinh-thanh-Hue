# Bàn giao — trạng thái và quy trình dự án

**Cập nhật:** 11/11/2026. Tài liệu ghi nhận sản phẩm hiện có, phạm vi kiểm chứng, công việc tiếp theo và quy trình phối hợp. Phê duyệt yêu cầu hoặc thiết kế không thay thế bằng chứng kiểm thử và nghiệm thu.

## 1. Sản phẩm và bằng chứng hiện có

| Hạng mục | Bằng chứng gần nhất | Phạm vi chưa được chứng minh |
| --- | --- | --- |
| Nền backend | V1–V12, nền phiên/nội dung/cặp, owner runtime và dọn vòng đời đã kiểm trên MySQL tạm theo [operations 6.3](../03-ky-thuat/operations-design.md#ops-lifecycle-cleanup-result) | Chưa áp V11/V12/bật cleaner trên môi trường phục vụ; nội dung được duyệt, tài khoản thật và vận hành bền vững còn kiểm |
| API phiên khách | K11/K01/K02/K04 cùng K03/K05–K10, grant/ACK/tải MP3 và D08; READ giữ scope tới khi stream đóng | Frontend/owner tab, Blob/expiry qua browser, restart/restore và vận hành thực |
| Xác thực Admin | A01–A05, session/CSRF, Q3 30 phút, mã Hash mật khẩu, đổi session id và logout. Header cookie và vòng đời đã kiểm qua HTTP localhost thật | Q2/Q3 trên frontend, tài khoản cấp cho môi trường phục vụ và HTTPS/proxy/browser thực |
| Lệnh Admin và địa điểm | Xác thực/receipt, địa điểm/vùng/nháp/hồ sơ/cặp/bật-ẩn; A11 đã có giao dịch xóa và dọn file riêng theo mục 2.27; A22 đã kiểm backend nghe thử tại mục 2.28 | Q2/frontend, ranh giới/hồ sơ thực, nghe thử MP3 trên browser và scheduler phục vụ |
| Job TTS | Worker WAV, local MP3/audio/Check/Retry đã kiểm bằng encoder giả theo mục 2.31; owner runtime và kết quả muộn | Provider/quota, FFmpeg/codec/MP3 thật, nghe duyệt, vận hành/restore và toàn T07 |
| Đọc dữ liệu Admin | A26–A28, ngày Asia/Saigon, snapshot/tổng thống kê; D08 vật lý đã kiểm trong MySQL tạm | Frontend, luồng ghi lượt trên browser thực và nghiệm thu toàn T05 |
| Hồi quy backend | [API 11.13](../03-ky-thuat/api-transactions.md#local-audio-implementation-result) giữ mốc toàn cây local audio mới; operations 6.3 và mục 2.28 giữ các lần kiểm trước; các mốc 397 tại 2.24 và 421 tại 2.26 giữ đúng thời điểm | Mỗi nhánh trung gian, toàn bộ tiêu chí nghiệm thu và CI/CD |
| Git | Vòng đời và A22 đã commit/push theo [mục 2.30](#parallel-backend-frontend), có cây backend đầy đủ tại T05 khớp nguồn đã kiểm | Chưa merge PR mới; frontend Nghĩa chưa commit/push, giữ điểm dừng đăng nhập |
| Backlog | Checklist hiện hành tại roadmap mục 3: 89 ô/20 dấu tích; các mốc 18 dấu tích trước đó giữ theo thời điểm | Đợt này không thay issue/Project hoặc coi test/commit là hoàn thành toàn task |

[Bằng chứng kiểm chứng](../03-ky-thuat/practice-01-start-session.md#practice-results) lưu thời điểm, kết quả và giới hạn. Metadata Git không chứng minh tài khoản dùng để xác thực GitHub hoặc ai đã thực hiện phần việc. Phân công và tài khoản theo G02 được đối chiếu riêng với bằng chứng.

<a id="next-step"></a>

## 2. Điểm dừng và công việc tiếp theo

Repository: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue). Bảng công việc: [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Thư mục làm việc đã ghi nhận: `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`.

**Đọc để tiếp tục:** [mục 2.31](#local-audio-result) ghi backend local audio và điểm dừng để rà; [mục 2.30](#parallel-backend-frontend) giữ kết quả push/phạm vi hai luồng. Các mốc trước giữ bằng chứng riêng; roadmap là nguồn checklist, mục 7 là nguồn quyền và nhịp bàn giao.

<a id="issue-recreation"></a>


### 2.1. Issue và Project hiện có

- Nguồn duy nhất của tiêu đề, Description và Checklist là [roadmap mục 3](roadmap.md#t01). Giữ 11 task/89 ô và phân công G02; bản local hiện có 20 dấu tích, mốc đối chiếu GitHub trước đó có 18. Không tự đổi dấu tích hoặc suy trạng thái GitHub từ bản local.
- Ngày 08/11 đã tạo và đọc lại đủ issue #1–#11 tại repository chính thức, đối chiếu khớp tiêu đề, Description và Checklist với mục 3 roadmap. [Mapping](roadmap.md#github-checklist-review) ghi URL thực.
- Người vận hành xác nhận ngày 08/11 đã thêm đủ 11 issue vào Project 21 và đặt Status theo roadmap mục 6.4, gồm 6 In Progress và 5 Todo, chưa có Done. Đây là xác nhận vận hành, chưa có lần kiểm trực tiếp mới bằng công cụ. Bằng chứng ngày 06/11 giữ riêng theo thời điểm.
- Nhóm tài liệu đã commit/push trong d00bb03 gồm 31 file tính cả README gốc và tài sản. Hiện có 33 file sau khi bổ sung nguồn Draw.io và SVG Use Case; các cập nhật sau commit được theo dõi riêng ở mục 2.3 và 2.5.
- Năm bản nháp tiếng Việt đã được báo cáo là có; chưa xác minh bản lưu trong repo, nguồn, kết quả rà soát hoặc hồ sơ duyệt. T08 cần tiếp nhận bản lưu, không tự tái tạo hoặc tính là nội dung đã duyệt.

<a id="bootstrap-result"></a>

### 2.2. Bằng chứng commit/push ngày 07–08/11/2026

Đầu ra Terminal được cung cấp và đối chiếu xác nhận ba nhóm sau tại repository AudioQR_Kinh-thanh-Hue trước lần tạo lại repository. Bảng giữ kết quả đã xảy ra trước đợt chỉnh comment ngày 08/11. Hai đích dùng cùng đường dẫn URL, vì vậy không suy các commit cũ đã có tại repository vừa tạo lại.

| Nhóm | Thời điểm commit +07 | Nhánh | SHA đầy đủ | Phạm vi và metadata |
| --- | --- | --- | --- | --- |
| Mốc 0/T10 | 07/11/2026 18:14:39 | main | `186fd4743e7075139c85e3cbd740763b7a78f092` | .gitignore gốc, 1 file/24 dòng thêm; author và committer gingpham <gingpham13052006@gmail.com> |
| Mốc 1/T01 | 07/11/2026 18:26:24 | T01 | `d00bb03b2ec4c472efd869028062417c782a3894` | 31 file tài liệu/tài sản, 12.099 dòng thêm; author và committer KhaiThanh <khaithanh2726@gmail.com> |
| Mốc 2/T01 | 08/11/2026 00:03:50 | T01 | `3a325fde360c057943421218de0b9d5e2c80bbf6` | 11 file khung backend, 707 dòng thêm; author và committer KhaiThanh <khaithanh2726@gmail.com> |

Mỗi lần push đều có thông báo thành công và SHA HEAD trùng SHA nhánh tương ứng trên origin. Kết quả cuối trước lượt chỉnh comment là T01 theo dõi origin/T01 ở 3a325fd; các nguồn thuộc T02/T03 còn untracked. Đây là snapshot đã được cung cấp, trạng thái index/HEAD sau đó cần căn cứ đầu ra mới khi tiếp tục Git.

GCM 2.9.1/helper manager đã được xác nhận. Trước push T10, GCM có gingpham và cấu hình tài khoản HTTPS riêng repository trả gingpham; trước các commit T01, cấu hình trả khaithanh2726-beep. Metadata commit và lựa chọn tài khoản xác thực được ghi riêng. Tên liên kết tài khoản trên trang commit GitHub còn chờ xác nhận. Đã có hướng dẫn chuẩn bị PR T01, còn thiếu URL PR hoặc bằng chứng merge.

Bộ 64 test đạt ngày 05/11 thuộc toàn cây backend khi chạy. Các commit trung gian có phạm vi kiểm chứng riêng theo [kế hoạch commit](commit-plan-2026-11-05.md). Checklist T01–T11 giữ nguyên 89 ô/18 dấu tích theo bằng chứng toàn ô.

<a id="comment-audit-20261108"></a>

### 2.3. Rà soát comment ngày 08/11/2026

Phạm vi đã kiểm kê gồm 29 tệp Java do nhóm quản lý, với 17 tệp ứng dụng và 12 tệp kiểm thử/hỗ trợ, cùng cấu hình có hỗ trợ comment. Đã chỉnh 29 tệp Java và application.properties, tổng 30 tệp nguồn/cấu hình. Comment Java dùng //, có dấu, mô tả lớp và hành vi hàm; hàm test nêu tình huống và kết quả được assertion kiểm. Hàm hỗ trợ phân biệt tạo dữ liệu với lưu database, thuộc tính cần giải thích có note cuối dòng. GuestSession hiện chưa có liên kết Entity.

Các tệp đã sửa được liệt kê đầy đủ dưới đây, đường dẫn tính từ thư mục dự án.

- `backend/src/main/java/vn/hueqr/backend/BackendApplication.java`
- `backend/src/main/java/vn/hueqr/backend/config/GuestRuntimeConfiguration.java`
- `backend/src/main/java/vn/hueqr/backend/controller/GuestSessionController.java`
- `backend/src/main/java/vn/hueqr/backend/controller/GuestSessionErrorHandler.java`
- `backend/src/main/java/vn/hueqr/backend/dto/GuestErrorResponse.java`
- `backend/src/main/java/vn/hueqr/backend/dto/SessionResponse.java`
- `backend/src/main/java/vn/hueqr/backend/dto/StartRequest.java`
- `backend/src/main/java/vn/hueqr/backend/dto/StartTicketResponse.java`
- `backend/src/main/java/vn/hueqr/backend/entity/GuestSession.java`
- `backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java`
- `backend/src/main/java/vn/hueqr/backend/service/GuestInputPolicy.java`
- `backend/src/main/java/vn/hueqr/backend/service/GuestSessionException.java`
- `backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java`
- `backend/src/main/java/vn/hueqr/backend/service/StartTicketCodec.java`
- `backend/src/main/java/vn/hueqr/backend/service/StartTicketService.java`
- `backend/src/main/java/vn/hueqr/backend/service/StartTicketSigner.java`
- `backend/src/main/java/vn/hueqr/backend/service/StartTicketTimePolicy.java`
- `backend/src/main/resources/application.properties`
- `backend/src/test/java/vn/hueqr/backend/BackendApplicationTests.java`
- `backend/src/test/java/vn/hueqr/backend/TestBackendApplication.java`
- `backend/src/test/java/vn/hueqr/backend/config/GuestRuntimeConfigurationTests.java`
- `backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java`
- `backend/src/test/java/vn/hueqr/backend/repository/GuestSessionRepositoryTests.java`
- `backend/src/test/java/vn/hueqr/backend/schema/GuestSessionSchemaTests.java`
- `backend/src/test/java/vn/hueqr/backend/service/GuestInputPolicyTests.java`
- `backend/src/test/java/vn/hueqr/backend/service/StartTicketCodecTests.java`
- `backend/src/test/java/vn/hueqr/backend/service/StartTicketSignerTests.java`
- `backend/src/test/java/vn/hueqr/backend/service/StartTicketTimePolicyTests.java`
- `backend/src/test/java/vn/hueqr/backend/support/GuestSessionTestClock.java`
- `backend/src/test/java/vn/hueqr/backend/support/TestcontainersConfiguration.java`

Đã rà .gitignore gốc, backend/.gitignore, backend/.gitattributes, pom.xml và cấu hình Wrapper. Các mẫu sinh ban đầu, tệp build, Wrapper, HELP sinh tự động, nội dung giấy phép/bản quyền và sáu tài sản dữ liệu được giữ nguyên. Migration V1 hiện chưa có comment cần biên tập, được giữ nguyên từng byte. Bằng chứng và bản chụp mã trong tài liệu lịch sử được bảo toàn.

Kiểm tra tĩnh chạy bằng Python chuẩn trong thư mục tạm tách biệt, trên bản sao trước/sau của 30 tệp. Bộ đối chiếu giữ nguyên token chuỗi thường, ký tự và text block khi tách comment. Kết quả đạt 29/29 tệp Java với 15.312 token mã thực thi và 346 token chuỗi/ký tự giữ nguyên; 64 khai báo @Test được giữ nguyên; cấu hình application.properties giữ nguyên các dòng dữ liệu. Kiểm tra comment cũng đã đối chiếu mô tả lớp/hàm, JavaDoc, dấu chấm cuối note, từ mã Hash và vị trí note.

Đợt này chỉ có kết quả kiểm tra tĩnh. Build, suite Java, ứng dụng, database và API bên ngoài chưa được chạy trong lượt rà soát. Kết quả 64 test ngày 05/11 tiếp tục giữ đúng thời điểm và phạm vi ban đầu.

<a id="new-target-pending"></a>

### 2.4. Đích chính thức và điểm bàn giao

Đích chính thức được xác nhận ngày 08/11/2026 là [repository AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue) và [GitHub Project 21](https://github.com/users/khaithanh2726-beep/projects/21). Repository được đọc qua công cụ GitHub có ID `1449659323`, chưa có issue trước lượt tạo mới. Đã tạo và đọc lại đủ #1–#11, khớp 89 ô/18 dấu tích với roadmap. Mapping ngày 06/11 và bằng chứng commit/push ở mục 2.2 giữ đúng thời điểm đã ghi.

Nguồn duy nhất của bộ 11 issue vẫn là [roadmap mục 3](roadmap.md#t01), đủ 89 ô/18 dấu tích. Việc tạo lại issue hoặc thay mapping dựa trên URL và kết quả đối chiếu thực tế. Mọi lệnh Git, kể cả chỉ đọc, và thao tác GitHub do người vận hành thực hiện trừ hành động cụ thể được giao thêm.

Người vận hành đã xác nhận Project đủ 11 issue và Status theo roadmap mục 6.4 ngày 08/11. Công cụ GitHub phiên này thiếu thao tác Project, vì vậy kết quả thêm issue và Status ghi theo xác nhận vận hành. Sau giai đoạn hoãn commit để triển khai theo mục 7.3, đã có yêu cầu tiếp tục commit phần Thành/Hòa tại mục 2.10.

Đã tiếp tục đến đọc thông tin cơ bản và sửa tên địa điểm theo mục 2.9, đạt 168 test toàn bộ với MySQL tạm và HTTP localhost. Hiện chuẩn bị nhóm commit theo mục 2.10 trước khi sang lưu/đọc/áp dụng bản vùng theo phụ thuộc. K04 giữ bằng chứng A.15, nền dữ liệu/xác thực/window giữ bằng chứng ở mục 2.6–2.8 theo thời điểm. Các ô checklist giữ nguyên theo điều kiện toàn ô.

<a id="use-case-diagram-result"></a>

### 2.5. Sơ đồ Use Case tổng quan

Đã kiểm kê trước khi tạo và chưa có hình Use Case tổng quan riêng trong tài liệu. [PRD mục 5](../02-nghiep-vu/PRD.md#use-case-overview) có một [nguồn .drawio](../02-nghiep-vu/use-case-overview.drawio) và [bản xuất SVG](../02-nghiep-vu/use-case-overview.svg) của cùng sơ đồ. Đủ 14 mã UC gồm 5 UC khách, 8 UC Admin và UC-D01 tự động; dịch vụ tạo giọng đọc là tác nhân hỗ trợ UC-A04 theo PRD.

Bố cục một trang A3 ngang. SVG được xuất từ model trong nguồn bằng mxGraph 32.6.10 đóng gói cùng Draw.io Integration 1.19.0 sẵn có, rồi mở qua Chrome headless để kiểm ảnh. Đã xem chữ tiếng Việt, nhãn, đường nối và mép hình; việc mở bằng trình biên tập VS Code và kiểm bản in thực tế còn chờ nhóm thực hiện.

Sơ đồ là hình tổng quan yêu cầu. Mã UC, mô tả nghiệp vụ và 15 khối Mermaid hiện hành được giữ nguyên. T01 tiếp tục theo checklist và bằng chứng toàn ô.

<a id="place-region-result"></a>

### 2.6. Nền dữ liệu địa điểm và vùng — T02

Đã thêm bảy file thuộc T02, gồm migration V2, Entity/Repository cho Place và RegionRevision, hai lớp test schema/mapping. Hòa phụ trách T02 theo G02, nghiệp vụ API quản trị thuộc T05. [Danh mục sản phẩm và lựa chọn mapping](../03-ky-thuat/data-model.md#model-place-region-result), [bằng chứng 104 test](../03-ky-thuat/data-model.md#place-region-test-result).

Đã kiểm định danh độc lập tên, lưu địa điểm ẩn thiếu vùng/nội dung, FK tổ hợp chặn vùng sai chủ, RESTRICT khi còn tham chiếu, tháo con trỏ trước xóa, mapping JSON/UTC và @Version chặn bản sửa cũ. Bản sao kiểm thử tách biệt có 46 file backend, mã nguồn và V1 cũ được bảo toàn. API/UI quản trị, quy tắc Q1, kiểm hình học/ranh giới thật, gate cặp vi, seeds và các bảng còn lại tiếp tục theo phụ thuộc.

Điểm bàn giao tại mốc này là rà nền dữ liệu đã kiểm, sau đó tiếp tục xác thực/phiên Admin theo mục 2.7. Commit tạm hoãn, chưa tích thêm ô hoặc đóng issue từ kết quả của mục này.

<a id="admin-auth-result"></a>

### 2.7. Nền xác thực và phiên Admin — T05

Đã triển khai A01–A05 theo [hợp đồng và nguồn mã](../03-ky-thuat/api-transactions.md#admin-auth-implementation), kiểm chứng [122 test toàn bộ](../03-ky-thuat/api-transactions.md#admin-auth-test-result). T05 do Thành phụ trách, ba file V3/Entity/Repository tài khoản thuộc T02 của Hòa. Backend ở mốc này có 65 file, nhóm commit được cập nhật theo file và phụ thuộc, commit tiếp tục tạm hoãn.

Lựa chọn kỹ thuật gồm tên đăng nhập phân biệt hoa thường/dấu, DelegatingPasswordEncoder mặc định bcrypt, cookie Secure/HttpOnly/SameSite=Strict, xoay session id/CSRF khi login. Q3 dùng giờ server và mốc hoạt động riêng, polling không gia hạn. Tài khoản được cấp trước, V3 khởi tạo bảng rỗng. Chưa cấp tài khoản thật hoặc áp migration lên database phục vụ.

Đã kiểm qua MockMvc/MySQL và Servlet HTTP localhost. Phép kiểm HTTP gửi cookie Secure thủ công để quan sát header và vòng đời. Browser, HTTPS/proxy/ngrok, UI che nội dung/dừng nghe/giữ tạm theo Q2, khôi phục đúng tài khoản và không tự gửi lại còn cần triển khai/kiểm chứng. Ô đầu T05 tiếp tục chưa tích vì các điều kiện Q2 tích hợp chưa đủ.

Điểm bàn giao tại mốc này là nhóm rà nền xác thực; cửa sổ lệnh Admin 24 giờ và receipt được triển khai tiếp ở mục 2.8. Việc cấp tài khoản thật và thao tác môi trường phục vụ do người vận hành thực hiện khi chuẩn bị tích hợp.

<a id="admin-command-result"></a>

### 2.8. Cửa sổ lệnh, receipt và tạo địa điểm ẩn — T05

Đã nối A31 cấp cửa sổ, A06 tra kết quả và phần tạo điểm ẩn A08. [Hợp đồng và nguồn mã](../03-ky-thuat/api-transactions.md#admin-command-implementation) là nguồn chính cho định dạng key, response, quy tắc giao dịch và giới hạn; [bằng chứng kiểm thử](../03-ky-thuat/api-transactions.md#admin-command-test-result) lưu kết quả toàn suite. Schema V4 cùng hai Entity/hai Repository và test ràng buộc dữ liệu thuộc T02 của Hòa, nghiệp vụ/API/test giao dịch thuộc T05 của Thành.

Đã kiểm cửa sổ cố định 24 giờ, chủ tài khoản/epoch/hạn trước replay, đăng nhập lại không gia hạn, cùng key khác đầu vào bị từ chối, hai request đồng thời chỉ tạo một điểm, rollback cả dữ liệu và receipt, FK/UK và trả receipt khi đích đã xóa. Tên điểm theo Q1, trùng tên chỉ cảnh báo, điểm mới ẩn và không bắt vùng/nội dung. Cấp cửa sổ/tra kết quả không gia hạn Q3. UI Q2, CRUD còn lại và worker dọn vật lý metadata chưa được triển khai trong mục này.

Backend ở mốc này có 83 file, gồm 72 file Java. DisplayNamePolicy dùng chung trong nền T01 để T03 không phụ thuộc T05, tên khách vẫn giới hạn 50 và địa điểm 100. Nhóm bàn giao ở mốc này là 12/21/23/27 file cho T01/T02/T03/T05, chưa là kết quả commit; danh sách hiện hành tại commit-plan. V1/V2/V3, cấu hình và hồ sơ lịch sử được giữ nguyên.

Điểm bàn giao tại mốc này là rà nhóm cửa sổ lệnh và tạo địa điểm ẩn. Phần danh sách/chi tiết/sửa tên địa điểm với If-Match được triển khai tiếp ở mục 2.9. Chưa chạy frontend hoặc áp V4 lên database phục vụ. Commit tiếp tục tạm hoãn, checklist giữ nguyên 89 ô/18 dấu tích, không tự đổi Project.

<a id="admin-place-result"></a>

### 2.9. Đọc thông tin cơ bản và sửa tên địa điểm — T05

Đã bổ sung phần đọc cơ bản A07 và sửa tên A09 qua AdminPlaceController → AdminPlaceService → PlaceRepository. [Hợp đồng và lựa chọn triển khai](../03-ky-thuat/api-transactions.md#admin-place-implementation) là nguồn chính cho DTO, ETag/If-Match, lỗi và giao dịch; [bằng chứng 168 test](../03-ky-thuat/api-transactions.md#admin-place-test-result) ghi thời điểm và phạm vi. T05 do Thành phụ trách, khóa/truy vấn Repository thuộc T02 của Hòa theo G02. Không thêm hoặc sửa migration, cấu hình hay Entity.

Đã kiểm danh sách rỗng/hơn năm điểm, định danh độc lập tên, GET không gia hạn Q3, tên theo Q1 và cảnh báo trùng loại trừ chính địa điểm đang sửa. PATCH giữ visible/vùng/createdAt, khóa WINDOW → PLACE, kiểm phiên bản và lưu receipt cùng giao dịch. Hai lệnh ở hai cửa sổ/session cùng version chỉ một được ghi; gửi lại đúng key không sửa lần hai. Lỗi ghi receipt hoàn tác cả tên, updatedAt và version. ETag replay là phiên bản của kết quả gốc, GET chi tiết cung cấp trạng thái hiện tại.

Backend hiện có 86 file, gồm 75 file Java. Thêm hai DTO và một lớp test, sửa Controller/Service/Repository địa điểm và bổ sung ca rollback trong AdminCommandTransactionTests. Nhóm bàn giao T01/T02/T03/T05 hiện là 12/21/23/30 file, đủ 86 file không trùng. Bản sao đã kiểm thử khớp SHA-256 toàn danh sách backend. Phần Q2 trên frontend, tổng hợp điều kiện bật hiện/mức đầy đủ A07, vùng/cặp, ẩn/hiện/xóa và dọn metadata chưa hoàn tất.

Điểm dừng là nhóm rà API đọc/sửa tên địa điểm. Mục tiếp theo dự kiến là lưu/đọc/áp dụng bản vùng A12–A14 với kiểm hình học, mã Hash và phiên bản; phải phân biệt fixture hình học với ranh giới thật chưa duyệt, không tự bật hiện địa điểm. Commit tiếp tục tạm hoãn. Chưa thao tác Git/GitHub hoặc database phục vụ, checklist vẫn 89 ô/18 dấu tích.

<a id="commit-resumption"></a>

### 2.10. Tiếp tục chuẩn bị commit phần Thành và Hòa

Đã có yêu cầu tiếp tục commit sau mốc kiểm chứng 168 test. Khả năng đăng nhập tài khoản Thành và Hòa đã được báo cáo; tài khoản xác thực GitHub/GCM và metadata Git cần đối chiếu tại mốc A, không suy từ đăng nhập trình duyệt hoặc chỉ từ user.name/user.email. Git và GitHub vẫn do người vận hành trực tiếp thực hiện.

Phạm vi sản phẩm theo [commit-plan](commit-plan-2026-11-05.md#commit-groups) gồm nền T10 của Hòa, tài liệu/khung dùng chung T01 của Thành, dữ liệu T02 của Hòa và API T03/T05 của Thành. Có 33 file tài liệu và 86 file backend, các nhóm backend 12/21/23/30 file. Phần T04/T06 của Nghĩa chưa có mã giao diện hoạt động, không chuyển phân công hoặc đưa vào commit của task khác.

Đầu ra Terminal tại mốc chuẩn bị xác nhận local ở main chưa có commit, không có nhánh đã sinh và index trống. .gitignore, README.md, backend/ và docs/ còn untracked. Origin fetch/push đúng repository chính thức, ls-remote --heads không trả nhánh. Author/committer khi đó là KhaiThanh <khaithanh2726@gmail.com>, GCM có gingpham. Kết quả commit/push T10 tiếp theo được ghi tại mục 2.11; các SHA tại mục 2.2 tiếp tục là bằng chứng trước lần tạo lại repository.

Nhóm được chọn tại mốc này là nền T10 của Hòa, chỉ stage .gitignore gốc 24 dòng trên main để tạo gốc lịch sử chung. Đối chiếu metadata/tài khoản gingpham và staged diff tại mốc A trước commit; mốc B kiểm SHA local/remote và trạng thái sau push. Đây là ngoại lệ khởi tạo nền đã có trong kế hoạch, phần T10 tiếp theo dùng nhánh T10. Không init lại, đổi remote hoặc stage toàn cây. Việc có commit này không hoàn thành toàn T10.

Khi đọc lại tài liệu cho mốc này, không truy cập được docs/01-tong-quan/commit-plan-2026-11-05.md trên đĩa và không tìm thấy tệp tên commit/plan trong docs. Không tự tạo lại hoặc ghi đè bản có thể đang mở trong IDE. Phân nhóm 33 file tài liệu và 86 file backend ở trên là mốc kiểm kê trước đó; phải kiểm kê lại nhóm tài liệu và xác định bản commit-plan trước khi stage T01. Tình trạng này không chặn nhóm .gitignore T10 độc lập.

Kết quả 168 test thuộc toàn cây backend đã kiểm, chưa chứng minh từng nhóm commit trung gian. Checklist giữ 89 ô/18 dấu tích, trạng thái issue/Project không đổi từ yêu cầu tiếp tục Git. Tiếp tục tính năng vùng A12–A14 sau mốc bàn giao commit phù hợp.

<a id="current-t10-commit-result"></a>

### 2.11. Commit/push nền T10 tại repository hiện tại

Đầu ra Terminal do người vận hành cung cấp xác nhận commit `d61a8698cae3652dec01185a282f88d536b2169c` trên main lúc 08/11/2026 23:43:37 +07, thông điệp `T10: Khởi tạo repository với cấu hình bỏ qua file Java`. Chỉ có .gitignore gốc, một file/24 dòng thêm. Author và committer đều là gingpham <gingpham13052006@gmail.com>. Cấu hình tài khoản HTTPS trước commit trả gingpham, staged diff đúng một file và diff --check không báo lỗi.

Push `main -> main` thành công tới repository chính thức; HEAD và refs/heads/main trên origin cùng SHA đầy đủ ở trên, main theo dõi origin/main. Trạng thái cuối là `main...origin/main`, README.md, backend/ và docs/ vẫn untracked. Chưa có PR/merge mới, chưa có bằng chứng commit T01/T02/T03/T05 tại repository này. Không thay ba SHA lịch sử ở mục 2.2 bằng SHA mới.

Nhóm tiếp theo là tài liệu T01 của Thành, rồi khung backend T01 trước các phụ thuộc T02/T03/T05. Khi đọc lại ngay sau T10, commit-plan thiếu trên đĩa và nhóm tài liệu có 32 file thay vì mốc 33. Tình trạng này đã được xử lý theo mục 2.12, không tự tạo lại hoặc ghi đè bản chưa lưu. Danh tính Git và lựa chọn HTTPS cuối cùng đã xác nhận là gingpham; chỉ chuyển sang Thành khi chuẩn bị nhóm T01.

Điểm bàn giao tại mốc này là xác định bản commit-plan và chuẩn bị nhóm tài liệu T01, chưa stage thêm. Nền T10 không hoàn thành toàn issue; checklist vẫn 89 ô/18 dấu tích, không tự đổi issue/Project. Kiểm chứng backend giữ mốc 168 test, không cần chạy lại build/test cho việc ghi nhận commit nền này.

<a id="document-review-result"></a>

### 2.12. Rà bộ tài liệu trước commit T01

Commit-plan đã được khôi phục sau xóa nhầm theo xác nhận của người vận hành; đã đọc bản trên đĩa trước biên tập. Nhóm tài liệu trở lại đủ 33 file gồm README gốc và docs/: 25 Markdown và tám tệp nguồn sơ đồ/tài sản. Tài liệu hiện hành ở docs/01–04 được rà về thứ tự đọc, góc nhìn dự án nhóm, nguồn chính, dẫn chiếu và trạng thái; bảy Markdown trong 99-luu-tru giữ nguyên nội dung lịch sử.

Đã làm rõ đường đọc tổng quan → nghiệp vụ → yêu cầu/quyết định → UI → kiến trúc/dữ liệu/API/vận hành → kế hoạch/bàn giao. TDD nhóm lại dữ liệu, phiên/tab/audio trước vận hành và truy vết; phần triển khai/kiểm chứng của API và data-model đặt sau thiết kế. Anchor cũ được giữ, số mục và nhãn dẫn chiếu cập nhật theo bố cục mới. Không đổi kiến trúc Vue + Spring Boot ba lớp hoặc thêm thành phần hệ thống.

Đã sửa trạng thái lỗi thời về backend/K04/Admin, phân biệt mốc lịch sử với trạng thái hiện hành và đồng bộ mã lỗi cửa sổ lệnh với code đã kiểm. Hướng dẫn phối hợp giữ G02, ba máy riêng, nhánh task, PR và hai mốc A/B. Commit-plan ghi đúng T10 đã push, T01 còn chuẩn bị. Bằng chứng 168 test giữ đúng thời điểm; đợt biên tập này không chạy thêm build/test/app, Git/GitHub hoặc môi trường phục vụ.

Checklist mục 3 roadmap và dấu tích được giữ nguyên. Hai câu trong checklist còn nhắc bàn giao/nội dung qua chat là nguyên văn nguồn issue đã duyệt, không tự biên tập trong đợt này. Các tài liệu yêu cầu/thiết kế hiện hành trình bày theo vai trò của nhóm, không đưa ngoại lệ thao tác cá nhân vào quy trình dùng chung. Bằng chứng lịch sử, mã Use Case, ma trận nghiệm thu và các sơ đồ/tài sản giữ nguyên phạm vi.

Kiểm tra tĩnh toàn bộ 33 file ghi nhận 15 Markdown được biên tập, 1.222 dẫn chiếu nội bộ hợp lệ, bảng/khối mã hợp lệ. Đối chiếu bản trước/sau giữ nguyên toàn bộ bảy file lưu trữ, tám tài sản, các khối mã và 15 Mermaid; giữ bảng G02, quy ước 7.1, nguyên mục 3 roadmap và nội dung yêu cầu/AC. FR-K05 chỉ đổi số dẫn chiếu TDD từ 14 sang 10 theo bố cục mới. Danh sách commit backend đủ 86 file không trùng; 87 file nguồn/cấu hình gồm backend và .gitignore gốc khớp mã Hash của đợt kiểm kê comment trước đó. Không kiểm lại nguồn ngoài hoặc bản in sơ đồ trong đợt biên tập này.

Điểm dừng là nhóm rà kết quả biên tập trước mốc A của commit tài liệu T01. Khi tiếp tục Git, chuyển metadata và tài khoản HTTPS sang Thành theo G02 rồi chọn nhánh T01 và stage đúng nhóm tài liệu; không chạy lại bootstrap T10. Chưa có bằng chứng stage/commit/push T01 mới từ đợt rà tài liệu này.

<a id="current-t01-docs-result"></a>

### 2.13. Commit/push tài liệu T01 và thứ tự bàn giao theo thành viên

Đầu ra Terminal xác nhận commit `65807af9884b6f959808368ab39974ccc1ffb23d` trên T01 lúc 09/11/2026 00:13:53 +07, thông điệp `T01: Bổ sung tài liệu nghiệp vụ, thiết kế và hướng dẫn bàn giao`. Phạm vi 33 file tài liệu/tài sản, 12.703 dòng thêm; author và committer đều là KhaiThanh <khaithanh2726@gmail.com>. GCM liệt kê khaithanh2726-beep và gingpham; đầu ra này không kèm giá trị cấu hình chọn username HTTPS. Các cảnh báo khoảng trắng tương ứng định dạng Markdown và hồ sơ lịch sử đã được rà, không sửa lại các bản lưu chỉ để xóa cảnh báo.

Push T01 thành công tới repository chính thức; HEAD và refs/heads/T01 trên origin cùng SHA trên. Trạng thái cuối T01...origin/T01, backend/ còn untracked. Chưa có URL PR/bằng chứng merge hoặc xác nhận liên kết tài khoản trên trang commit GitHub; không suy đăng nhập dùng để push chỉ từ metadata commit hay danh sách tài khoản GCM.

Thứ tự tạo commit tiếp theo theo thành viên là phần hiện có của Thành trên T01 → T03 → T05, sau đó Hòa bàn giao T02. [Commit-plan mục 2](commit-plan-2026-11-05.md#commit-groups) là nguồn chính cho cách tạo nhánh và nhận phụ thuộc; thứ tự tích hợp main vẫn T01 → T02 → T03 → T05. Không đưa dữ liệu T02 vào commit Thành, không coi nhánh thiếu phụ thuộc là đã build/test đạt. T07/T11 chưa có nhóm nguồn độc lập đủ căn cứ; không tạo commit rỗng để đủ các task được phân công.

Điểm tiếp tục là 12 file nền backend T01 và hai tài liệu commit-plan/handoff vừa cập nhật. Mốc A kiểm metadata, username HTTPS và staged đúng 14 file; mốc B kiểm commit/push/SHA và trạng thái. Các nhóm T03/T05 chưa stage/commit, T02 còn chờ bàn giao. Checklist giữ 89 ô/18 dấu tích; kết quả 168 test chỉ thuộc toàn cây ở thời điểm đã ghi, không thay cho kiểm chứng các nhánh trung gian. Mọi lệnh Git và thao tác GitHub tiếp tục do người vận hành thực hiện.

<a id="local-commit-batch-20261109"></a>

### 2.14. Đợt commit cục bộ ngày 09/11/2026

Đã giao công cụ hỗ trợ trực tiếp kiểm Git, stage và commit toàn bộ sản phẩm hiện có theo các nhóm T01/T03/T05 của Thành rồi T02 của Hòa. Quyền này áp dụng cho đợt commit hiện tại, gồm kiểm nhánh/index/diff, tạo nhánh task và worktree tạm, kiểm metadata và kết quả commit. Các mốc A/B được công cụ hỗ trợ tự đối chiếu; không cần xác nhận sau từng nhóm đạt kiểm tra. Push, PR/merge và cập nhật issue/Project không thuộc phạm vi đợt này; quyền chung ở mục 7 tiếp tục áp dụng ngoài phạm vi được giao.

Kiểm đầu vào xác nhận T01 ở 65807af, theo dõi origin/T01, index trống, hai tài liệu commit-plan/handoff có sửa đổi và backend/ còn untracked. Author/committer hiện là KhaiThanh <khaithanh2726@gmail.com>, cấu hình username HTTPS là khaithanh2726-beep. Kiểm kê đủ 86 file backend theo các nhóm 12/23/30/21, không trùng hoặc sót; đã lưu bản sao 120 file được Git theo dõi hoặc chưa bị ignore trước thao tác.

Đã commit nền T01 trong thư mục chính, dùng worktree tạm để tạo T03 từ nền T01 ở 2f8707a, T05 từ T03 và T02 từ nền T01 ở 2f8707a. Thứ tự tạo commit theo phần Thành trước rồi Hòa; không đổi thứ tự tích hợp main T01 → T02 → T03 → T05. Hai tài liệu bàn giao được cập nhật trên T01 sau các commit nguồn.

| Nhánh | SHA commit nguồn | Phạm vi | Metadata author và committer |
| --- | --- | --- | --- |
| T01 | `2f8707ad47ce3d06d59e7ef373a02b3211daff85` | 12 file nền backend và hai tài liệu sửa, 767 dòng thêm/7 dòng xóa | KhaiThanh <khaithanh2726@gmail.com> |
| T03 | `3dc4521030582a0daa7927f28b95966957a115ab` | 23 file phiên khách | KhaiThanh <khaithanh2726@gmail.com> |
| T05 | `d6097e19b306f0e455bb8d6964914d81860ade5c` | 30 file quản trị, 2.400 dòng thêm | KhaiThanh <khaithanh2726@gmail.com> |
| T02 | `d6e27cc70305ccb301654b5d90b366265ee4d1a2` | 21 file dữ liệu, 1.401 dòng thêm | gingpham <gingpham13052006@gmail.com> |

Mỗi nhóm được kiểm đúng danh sách staged, metadata, cha của commit và nội dung blob sau commit. Đủ 86 file backend khớp nội dung Git chuẩn hóa của các file gốc trên nhánh được phân công; 118 file ngoài hai tài liệu được phép biên tập giữ nguyên từng byte so với bản sao trước thao tác. Index trống và không có sửa đổi tracked sau bốn commit nguồn; worktree tạm đã được gỡ sau khi kiểm sạch. Nhánh/thư mục IDE vẫn ở T01, các file T02/T03/T05 còn hiện untracked tại đây nhưng đã được lưu trong commit của nhánh tương ứng. Không xóa hoặc add lại các file này vào T01 chỉ để làm sạch status.

Kiểm khoảng trắng T01/T05/T02 đạt; T03 có 14 cảnh báo dòng trống cuối file đã tồn tại nguyên trạng trong bản sao trước thao tác. Đã ghi nhận đúng 14 ngoại lệ này, kiểm các lỗi khoảng trắng còn lại đạt và không sửa nguồn để xóa cảnh báo. Không chạy build/test/app hoặc thay cấu hình, migration và dữ liệu. Kết quả 168 test trước đó tiếp tục thuộc toàn cây ở đúng thời điểm đã ghi, không phải kết quả test các nhánh mới.

Main vẫn ở d61a869; các ref origin theo dõi tại máy chưa đổi, origin/T01 vẫn ở 65807af. Không push, PR/merge hoặc sửa issue/Project; checklist giữ 89 ô/18 dấu tích. Cấu hình danh tính/username HTTPS trong repository chính vẫn của Thành; metadata Hòa được đặt riêng cho lệnh commit T02, không chọn tài khoản xác thực push. Điểm tiếp tục là push từng nhánh bằng tài khoản G02, sau đó nhận đủ phụ thuộc, kiểm chứng và tích hợp theo kế hoạch; chưa đóng issue từ việc đã commit.

<a id="pr12-review-result"></a>

### 2.15. Push các nhánh và rà PR #12 ngày 09/11/2026

Đầu ra Terminal do người vận hành cung cấp xác nhận push T01/T03/T05 với cấu hình chọn username khaithanh2726-beep và T02 với cấu hình chọn username gingpham. SHA local và refs/heads tương ứng trên origin khớp đầy đủ: T01 ở `0063ce0802bd5bfa85b68c5d79535c5e88e0468f`, T03/T05/T02 ở các SHA ghi tại mục 2.14. Bốn nhánh đã thiết lập theo dõi origin; thư mục IDE vẫn ở T01. Các file task khác còn untracked trên T01 được giữ nguyên theo cách bàn giao ở mục 2.14.

Đã đọc [PR #12 — T01: Bổ sung tài liệu thiết kế và nền backend dùng chung](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/12) qua trang GitHub công khai và API chỉ đọc do connector không khả dụng. PR do khaithanh2726-beep mở, từ T01 ở 0063ce0 vào main ở d61a869; đúng ba commit 65807af/2f8707a/0063ce0 và 45 file gồm 33 tài liệu/tài sản cùng 12 file nền backend. Description liệt kê từng commit và giải thích vai trò nền cho T02/T03/T05 theo quy ước mục 7.2. Tại thời điểm kiểm, PR đang mở, không phải Draft, chưa merge; API trả mergeable=true, mergeable_state=clean. Trang Checks chưa có CI check.

Đã tải bản nguồn đúng SHA đầy đủ 0063ce0 vào thư mục tạm tách biệt và chạy `mvnw.cmd --offline test` bằng Java 21.0.12.1, Docker Linux Engine và MySQL 8.4.13 Testcontainers. Kết quả lúc 09/11/2026 00:59:58 +07: một test BackendApplicationTests.contextLoads đạt, không lỗi/không bỏ qua, BUILD SUCCESS và mã thoát 0; đã đối chiếu một báo cáo Surefire XML. Đủ 46 file nguồn trong bản tải giữ nguyên sau kiểm thử. Đây là kiểm khởi động nền T01 riêng, không thay thế bộ 168 test toàn cây hoặc chứng minh nghiệp vụ T02/T03/T05 đã chạy trên nhánh riêng.

Bản nguồn, t01-test.log, báo cáo Surefire, pr-metadata.json và verification.json được lưu tại `C:\Users\khait\AppData\Local\Temp\hueqr-pr12-review-szstfmmp`. Không chạy Git, thay đổi GitHub, database phục vụ hoặc nguồn ứng dụng trong lượt rà PR. Các cập nhật quy ước Description và kết quả bàn giao trong handoff/commit-plan là sửa đổi tài liệu cục bộ, chưa nằm trong head 0063ce0 của PR.

Điểm tiếp tục là người vận hành tích hợp PR #12 bằng Create a merge commit nếu head/base và trạng thái kiểm vẫn khớp, sau đó gửi URL/kết quả merge để đối chiếu trước PR T02. Chưa có bằng chứng merge và không đóng issue #1 hoặc tích thêm checklist từ kết quả này. Giữ nhánh T01 để tiếp tục phần việc còn lại; thứ tự tích hợp tiếp theo T02 → T03 → T05, có nhận đủ phụ thuộc và kiểm chứng tương ứng.

<a id="pr12-merged-result"></a>

### 2.16. Xác nhận merge PR #12 vào main

Đã kiểm trang PR và API GitHub chỉ đọc sau khi người vận hành báo merge. [PR #12](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/12) có state=closed, merged=true, do khaithanh2726-beep merge lúc 09/11/2026 01:11:51 +07. [Commit merge](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/commit/056ed236bee9f28ac9745a5a7f8fe6492532861d) là `056ed236bee9f28ac9745a5a7f8fe6492532861d`, tiêu đề `T01: Tích hợp tài liệu thiết kế và nền backend dùng chung`; phần mô tả gồm năm gạch đầu dòng các việc chính đã thống nhất.

Commit có đúng hai parent: main cũ `d61a8698cae3652dec01185a282f88d536b2169c` và đầu T01 `0063ce0802bd5bfa85b68c5d79535c5e88e0468f`, xác nhận dùng Create a merge commit. Ref main trên GitHub ở đúng SHA merge khi kiểm. Tree của commit merge và đầu T01 cùng `420774f204722c6bcac80f67d303991ca8f28b40`, nên nội dung tích hợp trùng nguồn đã đạt một test khởi động ở mục 2.15. Không chạy lại test chỉ vì tạo commit merge có cùng nội dung.

Kết quả API lưu tại merge-verification.json trong thư mục tạm đã ghi ở mục 2.15. Chỉ đọc GitHub và sửa hai tài liệu handoff/commit-plan; không chạy Git, đổi nhánh/thư mục IDE, sửa code hoặc thay đổi GitHub. Chưa có bằng chứng mới rằng main local đã nhận commit merge. Giữ các file untracked và các cập nhật tài liệu cục bộ; không pull/switch/reset để ép làm sạch trạng thái. Những cập nhật này chưa commit/push và chưa nằm trong main trên GitHub.

Điểm tiếp tục là chuẩn bị PR dữ liệu T02 của Hòa vào main, kiểm đúng phạm vi/phụ thuộc và kiểm chứng trước merge, sau đó T03 rồi T05. PR #12 chỉ tích hợp tài liệu và nền dùng chung; không đóng issue #1 hoặc đổi 89 ô/18 dấu tích từ việc merge. Các mốc Git/GitHub tiếp theo do người vận hành thực hiện theo quyền hiện hành.

<a id="pr13-merged-result"></a>

### 2.17. Xác nhận merge T02 và chuẩn bị PR T03

API GitHub chỉ đọc xác nhận [PR #13 T02 → main](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/pull/13) đã đóng và có merged_at lúc 09/11/2026 01:30:31 +07. Commit merge là `6042508629f480c6d78eb80b2dd04b9395c6e83e`, head T02 là d6e27cc; ref main trên GitHub ở đúng SHA merge này khi kiểm. Đây là kết quả tích hợp đã có, không suy toàn issue T02 đã hoàn thành hoặc nhánh riêng đã chạy test đạt.

So sánh main...T03 hiện có một commit 3dc4521 và 23 file của API khách, không có PR T03 trong kết quả đọc tại thời điểm này. T03 có một commit riêng và thiếu bốn commit so với main; cần nhận/kiểm chứng nền đã có T02 trước khi tích hợp, không coi các file untracked trong thư mục IDE là phụ thuộc đã được lưu trên nhánh. Điểm tiếp tục là Thành tạo PR T03 → main theo Description tại mục 7.2, gửi URL để rà phạm vi và chạy kiểm tra tách biệt trước merge.

Lượt đối chiếu này chỉ đọc API/tệp và cập nhật tài liệu, không chạy Git, build/test hoặc thay đổi GitHub. Chưa có kết quả test riêng main sau T02 hoặc phần tích hợp T03; bộ 168 test và test khởi động nền T01 giữ đúng phạm vi cũ. Tài liệu cục bộ và nguồn chưa theo dõi trên T01 được bảo toàn, không tự checkout/pull/clean. Checklist giữ 89 ô/18 dấu tích; T05 tiếp tục sau T03 theo phụ thuộc.

<a id="region-api-result"></a>

### 2.18. Tạm dừng commit/PR và tiếp tục backend vùng — T05

Đã có yêu cầu tạm ngừng quy trình commit để tiếp tục hoàn thiện sản phẩm. Giữ nguyên các commit, nhánh, remote và kết quả merge đã ghi ở mục 2.14–2.17; chưa có PR/merge T03 mới được xác nhận. Không tiếp tục stage/commit/push/PR/merge khi chưa có yêu cầu nối lại. Quyền trực tiếp commit ở mục 2.14 chỉ dành cho đợt đã kết thúc, không áp dụng sang đợt triển khai mới này.

Phạm vi tiếp tục là backend A12–A14: lưu, đọc và áp dụng bản vùng theo UI-AP01/UI-AP02, giữ tách Lưu bản sửa với Áp dụng. Một đa giác kín đơn giản, chưa có lỗ hoặc nhiều mảnh; kiểm mã Hash, không gian tọa độ, phiên bản và giao dịch/receipt. Không tự bật hiện địa điểm, không cung cấp ranh giới thật hoặc tự coi hình thử đã được duyệt. Triển khai và kiểm thử trong bản sao backend tách biệt, đối chiếu nguồn trước khi bàn giao về thư mục chính.

Đã hoàn tất mục backend A12–A14 theo [hợp đồng và sản phẩm vùng](../03-ky-thuat/api-transactions.md#admin-region-implementation). Thêm chín Java gồm Controller/Service, policy hình học, helper UUID/version, ba DTO và hai lớp test; sửa AdminErrorHandler, AdminPlaceService và RegionRevisionRepository. Hiện có 95 file backend/84 Java; SHA-256 đủ 95 file trong thư mục chính khớp bản sao đã kiểm. Entity, cấu hình, pom.xml, Wrapper, V1–V4 và các test cũ giữ nguyên. Truy vấn Repository thuộc T02 của Hòa, nghiệp vụ/DTO/test thuộc T05 của Thành theo G02; đây là phân công, không suy tác giả từ task.

Kiểm thử trên bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-region-api-cw9bw7u1\backend` bằng `mvnw.cmd --offline test`: **204 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0 lúc 09/11/2026 01:59:57 +07**. Đã đối chiếu 21 XML Surefire, gồm 168 ca cũ, 14 unit hình học và 22 API/MySQL mới. [Bằng chứng chính](../03-ky-thuat/api-transactions.md#admin-region-test-result) ghi các ca hình học, key/replay, version, concurrent, rollback và Q3; log và manifest lưu trong thư mục tạm. Không chạy Git/GitHub, không áp migration lên database phục vụ, không gọi TTS/API ngoài hoặc thay công cụ trong đợt này.

Điểm dừng là nhóm rà backend vùng vừa hoàn tất; chưa có frontend vẽ/xem trước/giữ tạm Q2, bản đồ/ranh giới thật, nội dung/cặp hoặc gate bật hiện. Do đó chưa đủ toàn ô quản lý địa điểm/vùng của T05, giữ 89 ô/18 dấu tích và không đổi issue/Project. Commit/PR vẫn tạm dừng. Mục kế tiếp dự kiến là nền văn bản theo địa điểm/ngôn ngữ và revision nháp A15–A17 sau khi rà mốc vùng, tiếp tục theo nguồn yêu cầu đã chốt.

<a id="content-api-result"></a>

### 2.19. Nền văn bản nháp và tham chiếu hồ sơ ngày 09/11/2026

Đã triển khai phần nháp A15, lưu revision A16 và ghi/đọc tham chiếu hồ sơ A17 theo [hợp đồng 9.9](../03-ky-thuat/api-transactions.md#api-text-contract). Đọc không tự tạo ngăn hoặc bản dịch; mỗi lệnh lưu mới tạo văn bản bất biến đúng địa điểm/ngôn ngữ, giữ T1 và hồ sơ khi sinh T2. ETag có loại đối tượng bảo vệ lần tạo ngăn đầu tiên và phiên bản hồ sơ độc lập; replay cùng key giữ kết quả cũ. Tham chiếu chỉ ghi vị trí/giới hạn hồ sơ, không tự thẩm định/phê duyệt hoặc áp dụng.

Thêm 17 file (16 Java, một V5), sửa AdminErrorHandler và assertion danh sách migration của PlaceRegionSchemaTests để kiểm chính xác V1–V5; các assertion khác giữ nguyên. Backend có 112 file/100 Java, SHA-256 đủ 112 file khớp bản đã kiểm. Giữ nguyên V1–V4, cấu hình, pom.xml, Wrapper và các nguồn cũ khác. Schema/Entity/Repository/test schema thuộc T02 của Hòa; nghiệp vụ/DTO/HTTP/test API và policy thuộc T05 của Thành theo G02. Không suy người thực hiện từ phân công.

Bản sao kiểm thử: `C:\Users\khait\AppData\Local\Temp\hueqr-content-api-sfviid2y\backend`; `mvnw.cmd --offline test` kết thúc **09/11/2026 10:48:18 +07**, **240 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0**, đã đối chiếu 24 XML Surefire. [Bằng chứng chính](../03-ky-thuat/api-transactions.md#admin-content-test-result) gồm 204 ca trước và 36 ca mới: 7 policy, 23 API/MySQL, 6 schema/nâng V4. Log và manifest ở thư mục tạm cha, không đưa vào nguồn.

Điểm dừng là nhóm rà nền văn bản và hồ sơ vừa hoàn tất. A15 chưa có phần cặp đang phục vụ/job; chưa frontend soạn/Q2, nội dung thật được duyệt, TTS/audio/cặp/gate bật hiện hoặc nội dung khách. Mốc sau tiếp tục nền job/audio theo thiết kế; lời gọi dịch vụ TTS thật vẫn cần quyền và đầu vào/quota đã xác định. Chưa đủ toàn ô T02/T05, giữ 89 ô/18 dấu tích và Project. Commit/PR vẫn tạm dừng; không Git/GitHub, database phục vụ, TTS/API ngoài hoặc cài công cụ trong đợt này.


<a id="tts-queue-result"></a>

### 2.20. Nền tiếp nhận và theo dõi job TTS ngày 09/11/2026

Đã triển khai nền A18/A19: tạo job bền vững với snapshot đúng văn bản/ngôn ngữ/model/Charon, receipt 202 cùng giao dịch; key mới reuse job đang QUEUED/DISPATCHING/UNCERTAIN và cùng key replay receipt. A15 bổ sung job gần nhất của đúng bản nháp. Marker nội bộ giành quyền một lần, commit DISPATCHING trước khi trả lease; token/version đúng mới ghi UNCERTAIN, không tự gửi lại. [Hợp đồng và giới hạn](../03-ky-thuat/api-transactions.md#api-tts-queue-contract) là nguồn chính.

Thêm 10 file/9 Java/một V6, sửa bảy nguồn hiện có; backend có 122 file/109 Java, đã đối chiếu mã Hash khớp bản đã kiểm. V1–V5, cấu hình, pom.xml và Wrapper giữ nguyên. Theo G02: T02/Hòa phụ trách schema/Entity/Repository/test schema; T05/Thành giữ nền quyền Admin, nối lỗi và A15; T07/Thành phụ trách Controller tạo/theo dõi TTS, nghiệp vụ/marker, DTO và test API TTS. Phân công không thay bằng chứng người thực hiện.

Bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-tts-queue-648ygbvo\backend`, `mvnw.cmd --offline test` kết thúc **09/11/2026 11:15:57 +07**, **267 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0**; đã đối chiếu 26 XML Surefire. Gồm 240 ca cũ và 27 ca mới: 21 API/marker, 6 schema/nâng V5. [Bằng chứng chính](../03-ky-thuat/api-transactions.md#admin-tts-queue-test-result) ghi phạm vi concurrency/rollback/session/target; log và manifest ở thư mục tạm cha.

Điểm dừng: rà nền hàng đợi vừa hoàn tất. **Chưa nối scheduler/provider**, job nhận từ API nằm QUEUED cho tới khi có worker được triển khai và phép gọi dịch vụ phù hợp. Các ca chuyển marker/UNCERTAIN gọi Service nội bộ trong kiểm thử, không phải đã gửi Gemini hoặc phục hồi crash thật. Chưa WAV/file/audio, chặng kết quả/terminal, A20/A21, nghe duyệt, cặp áp dụng/khách hoặc frontend. Tiếp theo hoàn thiện adapter/worker và nền WAV/file theo thiết kế; gọi TTS thật/quota/công cụ môi trường vẫn cần đầu vào và quyền riêng. Giữ 89 ô/18 dấu tích, Project, phân công và trạng thái tạm dừng commit/PR; không Git/GitHub, database phục vụ, API ngoài hoặc cài công cụ trong đợt này.


<a id="worker-report-result"></a>

### 2.21. Worker WAV, kiểm tra/thử lại và đọc dữ liệu Admin ngày 09/11/2026

Đã mở rộng nền TTS: adapter REST unary theo nguồn chính thức, WAV/manifest bất biến, file_asset READY và con trỏ WAV trong job, lỗi có căn cứ/unknown, Check phục hồi bằng chứng local và Retry chủ động có version. V7 chỉ tới RESULT_STAGED/FAILED/DISCARDED, chưa MP3/audio success. Worker/scheduler mặc định tắt, không cấp khóa hoặc gọi provider thật trong đợt này. Các test dùng HTTP localhost/provider giả lập, WAV tổng hợp và MySQL/file tạm. [Hợp đồng](../03-ky-thuat/api-transactions.md#api-tts-worker-contract) phân biệt rõ các trạng thái.

Đã bổ sung V8/place_view cùng A26–A28: đọc phiên/lượt đã tính/thống kê, lọc ngày Asia/Saigon và loại dữ liệu quá hạn D08 khỏi kết quả. Kho lượt giữ snapshot đúng Id địa điểm; chưa nối ACK/grant của khách hoặc tự dọn vật lý D08. [Hợp đồng đọc](../03-ky-thuat/api-transactions.md#api-admin-report-contract) ghi DTO và giới hạn.

Backend có 155 file/140 Java; thêm 33 file, sửa 10 nguồn từ mốc 2.20. Giữ V1–V6, pom.xml, Wrapper và cấu hình đang dùng. Nguồn chính khớp SHA-256 bản sao đã kiểm. Schema/Entity/Repository/test schema thuộc T02 của Hòa; HTTP/Service/DTO/test đọc phiên và thống kê thuộc T05 của Thành; tạo/theo dõi/worker/adapter/WAV/retry thuộc T07 của Thành theo G02. Không suy tác giả từ phân công.

Bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-tts-worker-kdjaz1ll\backend`, `mvnw.cmd --offline test`: **318 test đạt, 0 lỗi/bỏ qua, BUILD SUCCESS, exit 0**, kết thúc **09/11/2026 12:08:13 +07**, đối chiếu 33 XML Surefire. [Bằng chứng chính](../03-ky-thuat/api-transactions.md#worker-report-test-result) ghi các ca mới/hồi quy và giới hạn. Log/manifest ở thư mục tạm cha, không đưa vào nguồn.

**Nhịp triển khai hiện hành:** tiếp tục các phần độc lập không cần cài công cụ, không dừng chỉ vì vừa xong một mốc nhỏ. Lưu bằng chứng theo mốc và dừng khi cần quyết định/quyền/thao tác môi trường hoặc cần nhóm cùng kiểm chứng phần phụ thuộc. Commit/PR vẫn tạm hoãn. Giữ 89 ô/18 dấu tích và trạng thái Project; chưa đủ toàn T02/T05/T07 hoặc đồ án.

**Phụ thuộc tạm gác:** FFmpeg/ffprobe chưa tìm thấy trong PATH; thiết kế chọn runtime Linux. node/npm chưa tìm thấy qua Get-Command nên frontend chưa build/test. Chưa cài hoặc nâng công cụ trong lượt này. Cần nhóm cùng chuẩn bị môi trường rồi tiếp tục MP3/preview/hồ sơ/cặp/guest audio; provider/quota/nội dung thật/URL/HTTPS/CI-CD/backup vẫn cần quyền và đầu vào riêng. Không tự bật `hueqr.tts.enabled`, lấy khóa từ kho khác hoặc gọi Gemini để kiểm. Worker crash/restart thật, file claims/dọn, D08 vật lý, grant/ACK và các tiêu chí nghiệm thu còn mở tiếp tục theo tài liệu nguồn.


<a id="audio-pair-data-result"></a>

### 2.22. Nền dữ liệu audio/cặp và dữ liệu khởi tạo ngày 09/11/2026

Đã bổ sung V9, ba Entity/Repository AudioRevision/AudioReviewReference/ContentPair, active_pair_id trong ContentSlot và metadata FileAsset cho WAV/MP3. FK tổ hợp giữ đúng text/ngăn/job/WAV và định dạng file, UNIQUE chống trùng; các quan hệ cần chặn dùng RESTRICT. Lưu nháp và cặp áp dụng tách con trỏ; có dữ liệu hoặc tham chiếu hồ sơ không tự duyệt/áp dụng/bật hiện. [Mô hình và bằng chứng chính](../03-ky-thuat/data-model.md#model-audio-pair-result) ghi phạm vi và ràng buộc vật lý.

Script five-places.sql chạy riêng, không tự chạy khi ứng dụng khởi động: năm điểm D06 ẩn và 20 ngăn ngôn ngữ trống, không vùng/bài/audio/tài khoản/secret/hồ sơ. Chạy lại không gộp theo tên hoặc đặt lại dữ liệu đã sửa. Đã kiểm trong MySQL tạm; chưa chạy migration hoặc seed trên database phục vụ.

Bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-audio-data-ft4lchr0\backend`, `mvnw.cmd --offline test`: **336 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0**, kết thúc **09/11/2026 13:27:28 +07**, đối chiếu 36 XML. Backend 167 file/150 Java, thêm 12 file, sửa 4 nguồn; giữ V1–V8/cấu hình/pom/Wrapper, bảo vệ sửa đổi trước bằng SHA-256 và đối chiếu nguồn chính với bản đã kiểm. Schema/Entity/Repository/test dữ liệu thuộc T02/Hòa theo G02; chưa có commit mới hoặc suy tác giả từ phân công.

**Điểm tiếp tục:** bước 2 là nghiệp vụ T05: hồ sơ audio, kiểm điều kiện và áp dụng cả cặp trong giao dịch, giữ cặp cũ khi lỗi, sau đó gate bật hiện. Nền này chưa thêm endpoint áp dụng, chưa chuyển MP3/TTS thật hoặc có bộ nội dung đã duyệt. Opening/grant/ACK, tự xóa D08 và dọn file vẫn cần triển khai. Các phụ thuộc FFmpeg/Node/provider và nhịp làm phần độc lập ở mục 2.21/7.3 vẫn giữ. Commit/PR tạm dừng, không tự sửa checklist/Project. Bản local đầu lượt đã có 89 ô/20 dấu tích, gồm T02 ô 2 và T10 ô 2 được tích thêm; giữ nguyên các dấu tích. Mốc GitHub đã đối chiếu trước đó có 18 dấu tích, chưa được kiểm lại. [Roadmap 6.5](roadmap.md#local-checklist-20261109) phân biệt hai nguồn.


<a id="pair-visibility-result"></a>

### 2.23. Hồ sơ audio, áp dụng cặp và bật/ẩn ngày 09/11/2026

Đã bổ sung A23 đọc/ghi hồ sơ audio, A24 áp dụng đúng cả cặp cùng receipt/version, A10 bật/ẩn với gate vùng/cặp vi; A15 trả cặp hiện hành riêng nháp, A07 trả missingConditions và A25 trả ma trận bốn tiếng/điểm. Không tự duyệt, lưu nháp, gọi TTS, bật hiện từ Apply hoặc xóa file trong giao dịch. File dùng chung chỉ ghi last_unapplied_at khi bỏ tham chiếu áp dụng cuối cùng; ẩn giữ cặp và mốc. [Hợp đồng và bằng chứng chính](../03-ky-thuat/api-transactions.md#admin-pair-visibility-implementation) ghi chi tiết và giới hạn.

Backend 178 file/161 Java; thêm 11 file và sửa 17 nguồn từ mốc 2.22. Giữ V1–V9/seed/cấu hình/pom.xml/Wrapper. T05/Thành phụ trách HTTP/nghiệp vụ/DTO/test API; T02/Hòa giữ Entity/Repository theo [phân nhóm commit](commit-plan-2026-11-05.md), chưa có commit mới. Nguồn chính khớp mã Hash bản kiểm, bảo vệ mọi sửa đổi có trước.

Bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-apply-pair-sa9jc86n\backend`, `mvnw.cmd --offline test`: **366 test đạt, không lỗi/bỏ qua, BUILD SUCCESS, exit 0**, kết thúc **09/11/2026 14:54:36 +07**, 37 XML. Ca mới dùng database/file tạm, MP3 fixture chỉ kiểm stat/metadata, không codec hoặc nội dung được duyệt. Không áp schema/seed lên database phục vụ, gọi provider thật hoặc cài công cụ.

**Điểm tiếp tục:** nối T03 nội dung được phép phục vụ, opening/grant/ACK và quyền hết hạn theo hợp đồng; song song tiếp tục phần xóa/dọn độc lập theo phụ thuộc. MP3/preview thực, frontend và nghiệm thu qua browser chờ môi trường/đầu vào. Giữ quyền và nhịp chủ động tại mục 7/7.3; commit/PR vẫn tạm dừng. Checklist local 89 ô/20 dấu tích và các nguồn GitHub/Project theo roadmap 6.5 giữ nguyên, không tự đóng issue hoặc đánh dấu hoàn thành cả T05.



<a id="guest-content-frontend-result"></a>

### 2.24. Nội dung khách, grant/ACK và giao diện khách ngày 09/11/2026

Đã có V10, K03/K05–K10, currentLanguage và thu hồi grant khi kết thúc; kiểm nonce/sequence/key/version, cặp áp dụng, snapshot lượt, tải trọn MP3/READ claim. [Nguồn bằng chứng chính](../03-ky-thuat/api-transactions.md#guest-content-test-result) ghi cả lần kiểm thất bại do danh sách migration cũ và lần hồi quy sau sửa. Backend 194 file/176 Java, V1–V9/seed/cấu hình/Wrapper giữ nguyên; nguồn chính được đối chiếu mã Hash trước/sau bàn giao. 397 test đạt, 0 failures/errors/skipped, BUILD SUCCESS, exit 0, 09/11/2026 22:26:24 +07.

Đã tạo 14 file frontend Vue khách theo T04: form/phiên, vùng API/chồng lấn, đọc/audio/Blob, owner document và box hết hạn/kết thúc; [frontend/README](../../frontend/README.md) ghi cách chuẩn bị và giới hạn. 13 ca Playback đạt trong V8 và 8 script qua kiểm cú pháp, chưa chạy Node/npm/build Vue hoặc browser. Get-Command chưa tìm thấy node/npm/ffmpeg/ffprobe trong PATH; không tự cài hoặc đổi phiên bản, không khẳng định công cụ không tồn tại ở mọi vị trí trên máy.

**Điểm tiếp tục:** commit/push phần backend và tài liệu theo T01/T02/T03/T05/T07; nhóm đã giao công cụ trực tiếp thực hiện sau mỗi mục lớn có kiểm chứng. Dừng trước T04/T06 để thành viên phụ trách đăng nhập. Khi môi trường frontend sẵn sàng, resolve đúng bộ đã chọn, chạy Node test/build rồi kiểm cùng API tạm. Tiếp tục xóa/dọn metadata/claims/D08 và frontend Admin theo phụ thuộc; FFmpeg/provider/nội dung/vùng thật chờ công cụ/đầu vào. Chưa kiểm quyền owner/audio/clock/background trên điện thoại hoặc hoàn thành toàn issue. Checklist 89 ô/20 dấu tích, mapping/Project và phân công G02 giữ nguyên; chưa tự merge PR hoặc cập nhật issue.

Đã dọn 7 container MySQL Testcontainers đã dừng từ lượt kiểm bị ngắt, cùng session kiểm thử `68822daa-5774-4cf1-bf3f-a91a04049f16`. Giữ container `hue-qr-dev-mysql-1` đang dừng, không kết nối database đó, xóa image hoặc volume. `container-cleanup.json` trong thư mục kiểm giữ Id/trạng thái và kiểm kê sau dọn.

<a id="commit-push-20261109"></a>

### 2.25. Bàn giao commit/push phần Thành và Hòa ngày 09/11/2026

Bộ nguồn bàn giao được chốt riêng trước khi commit: 194 file backend khớp bản hồi quy 397 test đạt tại mục 2.24 và 11 tài liệu thay đổi. Các file mới hoặc chỉnh sửa phát sinh sau mốc chốt thuộc lượt triển khai tiếp theo, không tự nhập vào nhóm này. Mỗi task giữ phân công G02, metadata và tài khoản xác thực tương ứng; kết quả commit/push không xác nhận hoàn thành toàn issue.

| Task / tài khoản | Commit sản phẩm | Nội dung |
| --- | --- | --- |
| T01 / `khaithanh2726-beep` | `f8f1c9a` | Thiết kế, bằng chứng kiểm thử và phân nhóm bàn giao |
| T02 / `gingpham` | `b6d39fc` | V5–V10, Entity/Repository, seed riêng và kiểm dữ liệu |
| T05 / `khaithanh2726-beep` | `462d94b` | Quản trị vùng, văn bản, hồ sơ/cặp áp dụng, bật-ẩn và thống kê |
| T07 / `khaithanh2726-beep` | `a676989` | Hàng đợi TTS, worker WAV, provider và phục hồi tác vụ |
| T03 / `khaithanh2726-beep` | `671c3b5` | K03/K05–K10, snapshot/grant/ACK, tải audio và thu hồi khi kết thúc |

T02 nhận nền `origin/main` đã merge T01/T02 và thiết kế T01 mới; T05 nhận nền dữ liệu/API phiên và T07; T03 nhận cặp áp dụng/TTS trước khi bổ sung nội dung khách. Đây là các lần nhận phụ thuộc trên nhánh task, không phải merge PR vào main. Từng commit trung gian chưa được build độc lập; cây backend đầy đủ tại T03 được đối chiếu với nguồn đã kiểm, không suy từ tên nhánh thành phạm vi diff của một PR.

Kết quả push và các đầu nhánh đã đối chiếu được ghi tại [commit-plan mục 4.12](commit-plan-2026-11-05.md#commit-push-result-20261109). Bảng trên ghi commit sản phẩm, không cố định HEAD sau các commit nhận phụ thuộc hoặc cập nhật tài liệu. Thư mục IDE giữ nhánh T01 để bảo vệ công việc triển khai song song; backend hiện thành untracked tại T01 vẫn có thể đã được lưu trên T02/T03/T05/T07. Không stage lại toàn thư mục chỉ dựa vào trạng thái untracked trên T01.

**Điểm dừng:** chưa commit/push 14 file frontend T04 và chưa thao tác T06; chờ đăng nhập tài khoản Nghĩa theo yêu cầu. Chưa merge PR mới, đổi main/Project hoặc tích issue. Trước lượt Git tiếp theo, kiểm lại nhánh/index, phần việc mới và phụ thuộc; không chạy lại kế hoạch khởi tạo repository.

<a id="guest-retention-result"></a>

### 2.26. Dọn metadata và phiên/lượt D08 ngày 10/11/2026

Đã bổ sung GuestRetentionRepository (T02/Hòa) cùng policy/Service/runner/hai lớp test (T05/Thành); sáu Java mới, backend 200 file/182 Java, không sửa nguồn cũ/config/migration. [Operations mục 6.2](../03-ky-thuat/operations-design.md#ops-guest-retention-result) là nguồn chính về thuật toán, UTC/DATETIME(6), claim, điều kiện scheduler, kết quả và giới hạn. 24 ca mới đạt; hồi quy 421 test đạt, 0 failures/errors/skipped, BUILD SUCCESS/exit 0, 2026-11-10T00:32:58+07:00, trên MySQL tạm.

Đến end/expiry dọn context, giữ ledger/lượt; tới anchor gốc +30 ngày xóa vật lý phiên/lượt trong một TX, chặn khi còn READ claim. Không unlink file hoặc nhả claim theo TTL. Mặc định runner chưa bật; chưa thao tác database đang dùng. Chưa dọn window/receipt/job/file hoặc xử lý claim rò/crash, backup/restore thực; không tự tích ô T05.

Phân nhóm sáu file ở [commit-plan 4.13](commit-plan-2026-11-05.md#guest-retention-commit-group). Repository đã commit/push T02 `695480b` bằng tài khoản Hòa; policy/Service/runner/test đã commit/push T05 `23813ca` bằng tài khoản Thành, SHA local/remote khớp. Cây backend đầy đủ 200 file ở T05 khớp nguồn đã kiểm 421 test; các commit nhận phụ thuộc/tài liệu có thể làm HEAD khác commit sản phẩm. Chưa merge vào main. Giữ thư mục IDE ở T01, bảo vệ triển khai song song, không gom các file untracked từ nhánh khác hoặc sản phẩm mới chưa kiểm vào commit này. Phần Nghĩa tiếp tục dừng trước đăng nhập.

**Điểm tiếp tục:** bật/đo retention khi môi trường phục vụ được chuẩn bị đúng quyền; xử lý claim rò có bằng chứng owner dừng, rồi hoàn thiện xóa địa điểm/dọn file và backup/restore. Nguồn Admin đang được chuẩn bị riêng theo hợp đồng API; chỉ nhận sau đối chiếu phần bàn giao và kiểm frontend khi có Node. Giữ 89 ô/20 dấu tích và trạng thái issue/Project theo nguồn hiện có.

<a id="lifecycle-cleanup-result"></a>

### 2.27. Xóa địa điểm, thu hồi claim có bằng chứng và dọn vòng đời — 10/11/2026

Đã triển khai A11, dọn window/receipt 24 giờ, job terminal đủ điều kiện 7 ngày và file theo các mốc 8 giờ/24 giờ/1 giờ. READ/TTS/Check có scope tiến trình; chỉ thu hồi claim khi xác nhận owner gốc đã dừng, không dùng TTL. Chi tiết, kết quả và giới hạn tại [operations 6.3](../03-ky-thuat/operations-design.md#ops-lifecycle-cleanup-result); nhóm file/phụ thuộc tại [commit-plan 4.14](commit-plan-2026-11-05.md#lifecycle-cleanup-commit-group).

Hồi quy 459 test đạt, nguồn backend 212 file/193 Java khớp bản kiểm tách biệt và đã đưa về thư mục làm việc để rà soát. Giữ V1–V10, bổ sung V11; chưa áp migration hoặc bật cleaner trên database đang dùng. Không thay nguồn frontend đang chuẩn bị riêng, không tự merge PR/tích issue/đổi Project. Giữ nguyên 89 ô/20 dấu tích. Lượt vòng đời này **chưa stage/commit/push**; dừng để nhóm kiểm tra kết quả trước khi tiếp tục hoặc bàn giao Git. Các mốc đã push ở 2.25–2.26 giữ nguyên.

**Điểm tiếp tục:** người vận hành chuẩn bị công cụ và môi trường để kiểm provider/quota, WAV → MP3 thật, frontend build/browser, scheduler và backup/restore. Nội dung/bản dịch/nghe duyệt, bản đồ và nghiệm thu tổng thể còn mở theo roadmap. Phần T04/T06 dừng trước đăng nhập Nghĩa; các sản phẩm backend đã có không đủ hoàn thành toàn issue.

<a id="admin-preview-prepared"></a>

### 2.28. A22 nghe thử Admin — kết quả backend ngày 10/11/2026

Đã bổ sung API nghe thử MP3 của đúng audio/text/ngôn ngữ đã lưu, kể cả nội dung nháp chưa duyệt/áp dụng hoặc địa điểm đang ẩn. Phạm vi T05; Repository READ claim thuộc T02. Năm file đã kiểm gồm FileReadClaimRepository, AdminAudioPreviewService, AdminPairController và hai bộ kiểm, đã nhận vào backend dự án để rà trong IDE.

**Bằng chứng:** 111 ca liên quan, 0 failure/error/skipped, BUILD SUCCESS/exit 0, kết thúc 10/11/2026 22:50:05 +07 trong bản sao và MySQL tạm. Gồm 16 ca API A22, 8 unit stream, 30 ca cặp Admin, 26 ca nội dung khách và 31 ca vòng đời. Lần kiểm trước bị chặn ở khởi tạo context do Docker chưa kết nối; log/XML thất bại được giữ riêng, không tính thành kết quả đạt. Mốc toàn cây 459 ca ở mục 2.27 thuộc nguồn trước A22; 111 ca mới là bộ kiểm liên quan, chưa phải chạy lại toàn bộ cây mới.

Cây backend nhận có 216 file/197 Java, khớp mã Hash bản đã kiểm tại `C:\Users\khait\AppData\Local\Temp\hueqr-admin-preview-h6qryy92/tested-source-hashes.json`. Giữ nguyên cấu hình, V1–V11 và nguồn ngoài năm file đã ghi. [API 11.12](../03-ky-thuat/api-transactions.md#admin-preview-prepared-result) giữ hành vi, giới hạn và bằng chứng; [commit-plan 4.15](commit-plan-2026-11-05.md#admin-preview-commit-group) giữ phân nhóm bàn giao.

**Điểm dừng:** dừng để nhóm rà kết quả backend và nguồn trong IDE trước mục tiếp theo hoặc commit/push. Chưa stage/commit/push, thay issue/Project hay tích thêm checklist. Chưa kiểm MP3 thực/codec/FFmpeg, frontend nghe thử, dừng player/Blob khi hết phiên hoặc browser/HTTPS; không đánh dấu hoàn thành toàn T05 hay tiêu chí nghe thử từ các fixture truyền tải.

<a id="local-commits-lifecycle-preview"></a>

### 2.29. Commit cục bộ vòng đời và A22 — 10/11/2026

Đã nhận phụ thuộc và commit 29 file backend mới/sửa theo T02/T03/T05/T07. [Commit-plan 4.16](commit-plan-2026-11-05.md#lifecycle-preview-local-commits) giữ SHA, số file và quan hệ bàn giao; tám tài liệu thiết kế/bằng chứng thuộc nhóm commit T01. T02 dùng metadata Hòa đã xác nhận, các task Thành dùng metadata Thành; không suy tài khoản xác thực từ metadata.

Cây backend đầy đủ tại T05 khớp 216 file/197 Java của bản kiểm ở mục 2.28 bằng Git blob sau chuẩn hóa line ending. Giữ nguồn IDE và frontend bằng mã Hash, giữ T01 đang mở và không gom các file untracked thuộc nhánh khác vào commit. Chỉ kiểm phạm vi/staged diff/whitespace và đối chiếu nguồn trong lượt Git; không chạy lại hoặc bổ sung kết quả build/test.

**Điểm dừng hiện hành:** mới commit cục bộ, chưa push lượt vòng đời/A22, chưa merge PR, đổi main/issue/Project hay tích thêm checklist. Các câu chưa commit ở mốc 2.27–2.28 phản ánh thời điểm kiểm thử trước bàn giao này. Khi tiếp tục push cần chọn tài khoản theo task, đối chiếu SHA local/remote; phần Nghĩa giữ điểm dừng trước đăng nhập. Checklist hiện vẫn 89 ô/20 dấu tích. Giới hạn frontend/công cụ/audio thực và môi trường phục vụ ở 2.27–2.28 vẫn còn.

<a id="parallel-backend-frontend"></a>

### 2.30. Push vòng đời/A22 và phối hợp backend–frontend — 10/11/2026

Đã push T02 bằng lựa chọn tài khoản gingpham, T01/T03/T05/T07 bằng khaithanh2726-beep; SHA local/remote khớp ở mốc [commit-plan 4.17](commit-plan-2026-11-05.md#lifecycle-preview-push-result). Không force-push, thay main, merge PR hoặc sửa issue/Project. Bảng giữ mốc sản phẩm; commit T01 bổ sung tài liệu phối hợp được theo dõi tiếp, không cố định HEAD bằng SHA cũ. Checklist vẫn 89 ô/20 dấu tích.

Chia công việc song song theo phạm vi file, giữ G02:

| Luồng | Phạm vi được sửa | Mục tiêu trước mắt |
| --- | --- | --- |
| Backend và tích hợp | backend/**, cấu hình ngoài frontend và tài liệu chung | T07 hoàn thiện chuyển đổi cục bộ/đăng ký MP3, trạng thái success/failure/retry và kiểm độc lập FFmpeg/provider; T02/T05 phối hợp nếu cần mapping/nghiệp vụ, T09 chuẩn bị kiểm, T01/T11 lưu hướng dẫn theo sản phẩm |
| Frontend khách/Admin | Chỉ frontend/** trong bản sao được bàn giao | T04/T06 hoàn thiện nguồn, nối API hiện có; mốc đầu ưu tiên Admin quản lý địa điểm/vùng và nội dung/cặp, Q2/Q3 dựa trên bản login/danh sách/đổi tên đã chuẩn bị |

Bản sao frontend tại `C:\Users\khait\AppData\Local\Temp\hueqr-frontend-parallel-2bdh_cq8`, có HANDOFF.md và baseline.json. Backend/tài liệu trong đó chỉ đọc. Đọc thêm bản Admin cũ `C:\Users\khait\AppData\Local\Temp\hueqr-codex2-admin-zf49j1_e\HANDOFF.md`, đối chiếu và kế thừa phần phù hợp, không tạo lại hoặc ghi đè tùy ý. Frontend vẫn thuộc Nghĩa theo G02; dừng trước commit/push để đăng nhập.

Luồng frontend không thao tác Git/GitHub, sửa backend/tài liệu chung hoặc chép về dự án nhận. Chỉ ghi file thay đổi, bằng chứng, mã Hash đầu ra và yêu cầu API còn thiếu trong HANDOFF.md của bản sao. Luồng tích hợp đối chiếu baseline với nguồn đích rồi mới ghép từng file sau rà soát. Không đổi hợp đồng API khi bên còn lại đang triển khai; đề xuất thay đổi phải nêu endpoint/DTO/lý do và được đối chiếu trước khi áp dụng. Việc chia luồng không thay phân công hoặc xác nhận ai đã hoàn thành task.

Không tự cài Node/npm/FFmpeg hoặc gọi provider/API ngoài. Thiếu công cụ thì tiếp tục code/kiểm độc lập và ghi giới hạn; không coi V8/mock/biên dịch là npm build/browser/MP3 thực đạt. Không dùng database chính hoặc mở môi trường phục vụ. Sau mỗi mục lớn vẫn dừng để nhóm rà theo 7.3; không tự tích checklist hoặc mở rộng quyền GitHub từ việc được push.

<a id="local-audio-result"></a>

### 2.31. Local WAV → MP3 và đăng ký audio — 11/11/2026

Đã nhận phần backend từ bản sao `C:\Users\khait\AppData\Local\Temp\hueqr-local-audio-46vou1oi` sau kiểm nguồn: thêm V12, chuyển đổi local, đăng ký audio đúng text/ngôn ngữ, Check kết quả và Retry dùng lại WAV. Không sửa endpoint hoặc các trường DTO đã có; bổ sung result đã mô tả ở API 9.4. Nguồn chính về hành vi/giới hạn tại [API 11.13](../03-ky-thuat/api-transactions.md#local-audio-implementation-result).

**Bằng chứng:** bộ full 519 ca có 518 ca đạt và một lỗi fixture Mockito; sau sửa fixture, ca đó chạy lại đạt. Sau đó siết biểu thức retryAction để DISCARDED không gợi ý xử lý từ lỗi local cũ và thêm một ca hồi quy. Nhóm local/TTS liên quan 60 ca chạy lại đạt (BUILD SUCCESS/exit 0 lúc 2026-11-11T00:44:53+07:00). Ledger đối chiếu 520 ca độc lập: 460 ca ngoài nhóm liên quan từ full và 60 ca từ lượt cuối; không cộng ca chạy trùng hoặc gọi full exit 1 là pass. Giữ 49 XML full và 3 XML nhóm cuối. Cây cuối 229 file/209 Java, 21 đường dẫn mới/sửa thuộc T02/T07/T05 theo [commit-plan 4.18](commit-plan-2026-11-05.md#local-audio-commit-group). Bộ riêng 30 ca trước khi nối Check giữ bằng chứng riêng.

**Giới hạn:** chưa cài/chạy FFmpeg/ffprobe thật, chưa codec/MP3 hoặc provider/quota thật, nghe duyệt/20 cặp, frontend/browser hoặc vận hành scheduler/backup/restore. Bộ tích hợp dùng encoder giả và bytes tổng hợp để kiểm SQL/quyền sở hữu/vòng đời; phép kiểm tiến trình dùng chương trình Java con. Đầu ra MP3 chưa có metadata/manifest đủ căn cứ được giữ để đối chiếu, không tự xóa như temp. Không đổi hạn chính sách hoặc coi mọi đầu ra mồ côi đã được xử lý xong.

**Điểm dừng:** dừng để nhóm rà mục lớn local audio trước commit/push hoặc công việc tiếp theo; chưa stage/commit/push, sửa main/GitHub hay tích checklist trong lượt triển khai này. V12 chỉ chạy trên database tạm. Backend/tài liệu là phạm vi luồng hiện tại; frontend T04/T06 vẫn ở bản sao riêng theo 2.30, chưa chép về hoặc commit phần Nghĩa. Các mốc push ở 2.30 giữ nguyên.

### 2.32. Chuẩn bị chạy thử một địa điểm

Đã đối chiếu luồng Admin và Khách với Controller/DTO hiện hành. Admin tải MP3 nghe thử theo đúng bản văn, ngôn ngữ và mã Hash. Khi lưu bản văn mới, giao diện yêu cầu xử lý tham chiếu đang soạn và bỏ liên kết tham chiếu/audio của bản cũ khỏi nháp mới. Khách chỉ nhận thông báo lỗi từ yêu cầu tải audio còn hiện hành.

Bộ chạy thử gồm cấu hình MySQL và kho audio riêng, tài khoản thử, fixture WAV để V12 chuyển thành MP3 và năm ca kiểm. Bảng nội dung có 20 dòng theo địa điểm và ngôn ngữ, chờ ghi bản văn và audio tương ứng. [Hướng dẫn chạy thử](../07-van-hanh/chay-thu/README.md) · [Bảng nội dung](../05-noi-dung/doi-chieu-van-ban-audio.md)

54/54 ca logic frontend đạt bằng V8, gồm ba ca hồi quy bổ sung. Đã kiểm cú pháp script và biên dịch công cụ sinh mã Hash mật khẩu thử. Build Vue, MySQL, FFmpeg, phát MP3 và trình duyệt còn chờ chạy thực tế. Mốc rà V12 ở mục 2.31 tiếp tục áp dụng cho bước bàn giao Git. [Bằng chứng và mã Hash nguồn](../06-kiem-thu/chay-thu-mot-dia-diem.json)

<a id="local-e2e-20261111"></a>

### 2.33. Frontend Admin và chạy thử local — 11/11/2026

Đã đối chiếu nguồn chính và `frontend/integration-admin.json`: Admin T04/T06 đã được ghép vào thư mục chính. Trạng thái frontend còn ở bản sao trong mục 2.31 là mốc lịch sử, không đại diện nguồn hiện tại. Bộ 229 tệp backend khớp manifest nguồn local-audio đã bàn giao; không đổi Controller/Service/Repository hoặc hợp đồng API trong mốc frontend này.

Đã bổ sung TTS tạo/đọc/Check/Retry và chọn kết quả nghe thử, bảng mức đầy đủ năm điểm/bốn ngôn ngữ, phiên/thống kê theo Asia/Saigon và QR từ URL HTTPS cấu hình. Giữ command-window/receipt, phiên bản, Q2/Q3 và không tự gửi lại lệnh chưa rõ. Sửa review phiên bản 0 bị coi là chưa có, tham số Web Locks không tương thích Chrome, hoạt động nghe thử Q3 và đọc lại phiên khi trở về tab. Bản nháp mới bỏ liên kết audio/job cũ; cặp đang phục vụ giữ độc lập.

Frontend có lockfile thực, `npm test` đạt 60/60 và build Vue thành công. Full backend mới đạt 520/520, không failures/errors/skipped, BUILD SUCCESS lúc 09:39:14 +07. Chrome lượt cuối đạt 2/2, exit 0 với autoplay mặc định và API/MySQL/kho audio riêng: áp dụng cặp, phát MP3 thật, thống kê, modal chi tiết, bản nháp mới và tình huống lỗi tải/thử lại/reload. Phạm vi từng lượt, kiểm hồi quy cuối, backend full và Hash nguồn tại [hồ sơ kiểm](../06-kiem-thu/local-20261111/README.md). Không dùng viewport giả lập hoặc cờ autoplay của lượt đầu làm bằng chứng thiết bị/chính sách trình duyệt mặc định.

Có 15 bài dịch en/fr/ru nháp chờ duyệt. Bốn WAV vi được tạo bằng Gemini thật, chuyển MP3 và giải mã bằng FFmpeg; lưu ngoài repository cùng [manifest Hash](../05-noi-dung/audio-vi-v01.json). Diên Thọ chưa tạo thành công do HTTP 503. WAV trong kiểm E2E được đưa vào bằng fixture rồi worker V12 xử lý thật; chưa chứng minh UI → provider worker xuyên suốt hoặc duyệt bộ 20 cặp.

Nguồn mới/sửa tập trung tại `frontend/src/admin`, `frontend/src/guest/owner.js`, bộ kiểm frontend, package/lockfile và Vite; danh mục chi tiết trong `source-hashes.json` của hồ sơ kiểm. Có Dockerfile/Compose, mẫu cấu hình và workflow CI nguồn tại `deploy`/`.github/workflows`, cùng [hướng dẫn đóng gói](../07-van-hanh/dong-goi-va-ci.md). Chưa build ảnh Linux hoặc chạy GitHub Actions/CD. Nội dung, bằng chứng và vận hành lần lượt ở `docs/05-noi-dung`, `06-kiem-thu`, `07-van-hanh`; không thêm thư mục chạy thử vào `03-ky-thuat`.

Điểm rà: kiểm kết quả local trước chuyển sang mốc HTTPS/thiết bị và vận hành. Chưa có URL HTTPS/ngrok, Tailscale/OpenSSH hoặc bằng chứng backup/restore. Các điều kiện và phép kiểm tiếp theo tại [nghiệm thu vận hành](../07-van-hanh/nghiem-thu-van-hanh.md). API liệt kê lịch sử region-revisions theo địa điểm vẫn là đề xuất không chặn; chưa tự thêm endpoint hoặc thay DTO. Không stage/commit/push, tích checklist, sửa issue/Project hoặc tự ghi nhận hoàn thành task.

<a id="packaging-restore-20261111"></a>

### 2.34. Ảnh Linux và diễn tập restore offline — 11/11/2026

Đã build `deploy/Dockerfile` thành ảnh `hueqr-local:20261111`, Linux amd64, 527.973.042 byte; danh tính SHA tại [hồ sơ đóng gói](../06-kiem-thu/packaging-20261111/README.md). Frontend trong ảnh chạy 60 ca logic và build, backend package với skipTests; không ghi lượt package thành full regression Linux. Java runtime 21.0.13, major 21 giữ nguyên; không đổi công cụ Windows hoặc phiên bản Vue/Spring/Maven đã chốt.

FFmpeg/ffprobe 9.0.2 trong ảnh đã chuyển WAV Ngọ Môn thật sang MP3 mono 24 kHz, probe và giải mã toàn tệp ở đường dẫn Unicode, chạy uid/gid 10001 và không có mạng. Ảnh phục vụ frontend tĩnh cùng API, không dùng Vite dev.

Diễn tập dùng nguồn E2E đã dừng backend/worker, zero task/claim, job terminal và không file DELETING. Chụp dump/các mốc Flyway cùng catalog tham chiếu, sao chép 12 WAV/MP3 tổng 7.925.856 byte có kiểm Hash; restore sang container mới, đối chiếu dữ liệu trước thêm fixture. Ba trong bốn phiên fixture bị lọc đúng retention 30 ngày; phiên/lượt còn hạn giữ timestamp gốc. Epoch mới/khóa và dump ở thư mục ngoài repository có ACL riêng. Năm ca kiểm gói hỏng/hết hạn/path traversal đạt. Đây là công cụ kiểm thử ở `backend/src/test/fixtures/operations`, không thêm migration/API hoặc thay hợp đồng.

Chrome trên ứng dụng Docker sau restore đạt 1/1, exit 0: Admin đăng nhập, khách bắt đầu/đọc/phát MP3/kết thúc; phiên fixture còn hạn nhưng epoch cũ bị từ chối HTTP 401 với credential đúng. Provider/worker/cleaner/retention đều tắt ở lượt ứng dụng sau restore. Có báo cáo, ảnh, Hash và hướng dẫn chạy lại; chưa đưa dump hoặc secret vào repository.

OPS_GUARD/QUIESCING/FROZEN/drain live trong thiết kế chưa có mã thực thi. Script kiểm nguồn dừng không thể fence writer mới, chỉ nhận job terminal; chưa nghiệm thu backup lúc phục vụ, job/claim đang xử lý, lịch dọn bảy ngày hoặc Compose update/rollback. HTTPS, thiết bị, bộ 20 cặp, provider worker xuyên suốt và CI/CD thật vẫn mở. CI nguồn bổ sung kiểm manifest bằng Node, chưa push/chạy GitHub.

Đã dừng đúng container ứng dụng/restore/nguồn thử, giữ dữ liệu và gói còn hạn để rà. Không sửa mã Java main, stage/commit/push, issue/Project hoặc checklist. Dừng mốc đóng gói/restore để nhóm rà; mục lớn khả thi tiếp theo là cơ chế bảo trì/drain có bằng chứng và kiểm backup với job đang xử lý, giữ thiết kế đã duyệt. Không dùng kết quả offline để tích hoàn tất T09/T10 hoặc toàn đồ án.

<a id="guest-ui-fix-20261111"></a>

### 2.35. Sửa chữ và bố cục Khách — 11/11/2026

Đã xác nhận trực tiếp trong khung trình duyệt VS Code: bản Khách cũ có chữ tách dấu/mất nét, bố cục desktop chia hai cột và Kết thúc nằm cuối bài; hộp ứng viên cuộn cả Hủy. DevTools trên Chrome ghi nhận trộn Georgia/Times New Roman trong cùng nút/đoạn tiếng Việt. Đã đổi bộ phông sang Segoe UI/Arial/sans-serif, kiểm platform font thực vi/en/fr/ru và ảnh hiển thị; không thay bản văn hoặc tự kết luận lỗi UTF-8.

Sửa `frontend/src/App.vue`, `frontend/src/style.css`, `frontend/src/components/GuestMap.vue`; thêm `frontend/tests/e2e/guest-layout.spec.mjs`. Form bắt đầu đặt tên/ngôn ngữ/nút trong cùng công việc; trang tham quan xếp dọc bản đồ/tên/audio/ô văn bản, Kết thúc ở đầu tách ngôn ngữ. Chỉ văn bản cuộn khi đọc, Hủy luôn thấy ngoài danh sách cuộn. Giữ API, owner/phiên, P01/P06, grant/ACK và cách phát/khôi phục; không thêm dependency hoặc sửa Java/backend.

`npm test` 60/60, build Vue và Docker exit 0. Chrome với ảnh Docker mới đạt 7/7, exit 0 (54,5 giây): vi/en/fr/ru, bốn viewport 1280×800/960×600/390×844/320×568, thứ tự/vị trí/không tràn trang, cuộn/Hủy không mở hoặc ACK lại, lỗi audio/thử lại và ca Admin/Khách trên dữ liệu phục hồi. Đã kiểm lại bằng Computer Use trong VS Code: chữ rõ, bố cục dọc, reload giữ mốc 0:05 tạm dừng, mở/Hủy trong khung nhỏ giữ bài/mốc. [Ảnh, log, baseline/Hash và giới hạn](../06-kiem-thu/guest-ui-20261111/README.md).

Ảnh `hueqr-local:20261111-guest-ui`, SHA `61722bfc150a04e38013ef7f31fb271f60d2ca21cda155b9359b213079065bf1`; container ứng dụng hiện `hueqr-packaged-ui-426f3b91` ở `localhost:18081`, thay container cũ đang dừng. Giữ database/kho audio, epoch/khóa và flags worker/provider/cleaner/retention tắt. Cấu hình riêng và mật khẩu không chép vào repository; HttpSession Admin có thể yêu cầu đăng nhập lại. Bản thử tiếp tục chạy để rà, không phải môi trường phục vụ public.

**Điểm chưa đạt demo:** map hiện vẫn là lưới và năm tam giác/bản ghi E2E trùng nhau; nhãn đè, không có nền và bộ năm vùng thực. Kiểm API/audio đã đạt không chứng minh bản đồ hoặc toàn UI đạt yêu cầu. Chưa sửa/gộp/xóa các bản ghi cũ để che hiện trạng; cần dữ liệu demo tách E2E và bản xem trước nền/vùng từ tài sản hiện có để duyệt. Bản đề xuất trong design-assets không tự thành ranh giới đã duyệt. Chưa rà xong toàn Admin hoặc thiết bị thật; không tích checklist hoặc thay issue/Project.

Điểm dừng: rà mốc chữ/bố cục trước chuyển mục lớn bản đồ/dữ liệu demo. Không stage/commit/push, không đổi phân công G02; frontend T04/T06 vẫn thuộc Nghĩa. Các sửa/tài liệu trước được giữ, baseline riêng ghi Hash trước mốc này.

<a id="overall-review-20261111"></a>

### 2.36. Đối chiếu thiếu sót toàn hệ thống — 11/11/2026

[Báo cáo rà soát](../06-kiem-thu/review-20261111/README.md) gom mười nhóm còn mở theo PRD/UI/roadmap và bằng chứng thực thi. Bản đang mở chưa đạt demo hoàn chỉnh hoặc nghiệm thu toàn đồ án; 520 ca backend, 60 ca frontend, build/restore/media và bảy ca browser giữ đúng phạm vi từng lượt, không tự chứng minh QR/bản đồ/nội dung/thiết bị/vận hành đã hoàn tất.

Đối chiếu chỉ đọc runtime thử: 6 địa điểm, 5 hiển thị, 0/5 mã điểm chuẩn, 5 slot áp dụng đều vi, chỉ 1 geometry hash trong các vùng đang hiển thị. Script seed năm điểm chuẩn có nguồn và test nhưng chưa áp cho runtime này. Không sửa/xóa/gộp dữ liệu E2E; cần bộ demo riêng. Browser context kiểm riêng xác nhận QR chưa có URL và tái hiện trang Mức đầy đủ giữ “Đang đọc…” sau lỗi API dù busy đã kết thúc. Flags provider/local audio/cleanup/retention trong bản thử đều tắt.

Phần đã sửa chữ/bố cục Khách tại 2.35 giữ kết quả; bản đồ/nền/nhãn, QR/HTTPS, bộ 20 cặp và TTS xuyên suốt, rà Admin/thiết bị, OPS_GUARD/drain, CI/CD và hồ sơ nghiệm thu vẫn mở. Thứ tự xử lý và đầu vào cần nhóm duyệt/thao tác ghi tại báo cáo; không tự đổi nghiệp vụ, DTO, phân công, checklist hoặc trạng thái GitHub. Lượt rà này chỉ bổ sung hồ sơ, không sửa mã ứng dụng, database, Git ghi hoặc bật dịch vụ ngoài.

<a id="map-preview-dataset-20261111"></a>

### 2.37. Bản đồ xem trước và dữ liệu nháp riêng — 11/11/2026

Đã tạo nền offline từ bản trích OSM hiện có: 651 way, nguồn/Hash/mốc snapshot/ODbL giữ trong metadata, có ghi công trên bản đồ. `VITE_MAP_PREVIEW=true` bật riêng nền và trang `/map-preview.html` với năm vùng đề xuất, số/chú giải/tên và dấu chọn; bản dựng mặc định không bật preview. GuestMap và trình vùng Admin có thể dùng cùng nền nhưng vùng phiên Khách vẫn từ API, không tự áp dữ liệu đề xuất. Năm vùng và nền còn chờ nhóm xem xét/duyệt, không là ranh giới được xác nhận.

Môi trường mới `hueqr_preview` có 5 mã điểm chuẩn, 20 ngăn ngôn ngữ và 5 bản vi nháp lưu qua API đúng Hash. Tất cả điểm còn ẩn, 0 review-ref/cặp áp dụng; không chép dữ liệu E2E hoặc dùng WAV sai bài để làm đủ bộ. Khóa/mật khẩu/kho audio mới ở LocalAppData `AudioQR-Preview-*` có ACL riêng; chỉ đọc tài khoản thử nguồn để cấp tài khoản `preview-admin`. Helper này chưa thay quy trình cấp Admin phục vụ từ checkout mới.

Đã sửa lỗi Mức đầy đủ còn báo đang đọc khi API thất bại; tách loading/error/result, thử lại và nhãn dữ liệu cũ. Tên/mã xuống dòng, checkbox cùng nhãn. `npm test` 60/60, build thường và Docker preview exit 0. Chrome trên ảnh cuối 3/3 đạt, exit 0 (35,4 giây): nền/vùng/chọn đủ năm điểm/không yêu cầu mạng ngoài ở 1280×900 và 390×844, Admin đủ bộ nháp, tải lỗi/thử lại/dữ liệu cũ. Browser kiểm dist mặc định xác nhận không dựng preview khi cờ false. [Ảnh/log/baseline/Hash và giới hạn](../06-kiem-thu/map-preview-20261111/README.md).

Ảnh `hueqr-local:20261111-map-preview`, SHA `978801878f79dfbbd0f9b9268de113153e94f66b0884ec3879932b9547fdd371`, app `hueqr-map-preview-507da98b`, database `hueqr-preview-mysql-91b4feca`. [Cách mở](../07-van-hanh/xem-truoc-ban-do.md): `http://127.0.0.1:18082/map-preview.html`, Admin `/admin.html`. Bản E2E cũ cổng 18081 đã dừng để giảm RAM, container/database và ảnh cũ còn nguyên. Provider/local worker/cleanup/retention đều tắt, không gọi TTS hoặc mở tunnel. App preview tiếp tục chạy để rà, database không publish cổng.

[Bảng tiếp tục theo issue](issue-follow-up-2026-11-11.md) đối chiếu danh sách công khai 11 issue mở, đọc riêng T04/T06/T09/T10 và liên kết checklist local cho T01–T11. Bản đồ/QR/thiết bị/môi trường đã nằm trong phạm vi hiện có; chưa tạo issue trùng, chưa sửa body/assignee/checklist/Project, chưa commit/push. Phân công G02 không đổi theo người thao tác.

Điểm dừng để rà nền và năm vùng đề xuất. Trang xem trước không là phiên Khách đầy đủ; chưa áp vùng/bật điểm/ghép audio, chưa nhập 15 bản dịch vào bộ nháp mới, chưa QR/HTTPS hoặc thiết bị thật. Sau duyệt đúng phần, tiếp tục Admin lưu/xem/áp vùng, nội dung/audio và hồ sơ/cặp trước bật. Giữ mục tiêu 20 cặp và các phần vận hành còn mở, không dùng preview thay nghiệm thu.

<a id="docker-cleanup-20261111"></a>

### 2.38. Dọn Docker thử nghiệm — 11/11/2026

Đã xóa theo định danh 6 container thử cũ ở trạng thái exited và 3 image ứng dụng cũ không còn container tham chiếu; [báo cáo và mapping volume](../06-kiem-thu/docker-cleanup-20261111/README.md). Giữ app/database preview 18082 đang chạy, MySQL `hue-qr-dev-mysql-1` của workspace trước, cả 12 volume, audio/dump/cấu hình và bằng chứng. Không prune chung/cache, không xóa nguồn/untracked, không Git ghi hoặc thay checklist. Sau dọn, preview/Admin HTTP 200 và database vẫn 5 điểm/20 slot/5 nháp.

Tên container cũ đã xóa không dùng được bằng `docker start`; khi cần dựng lại phải đối chiếu volume/phiên bản MySQL/cấu hình từ hồ sơ dọn. Các kết quả chạy trước giữ đúng mốc, không chứng minh container/image lịch sử còn tồn tại. Bản xem trước tại 2.37 và các đầu việc còn mở không đổi.

<a id="ui-reference-20261111"></a>

### 2.39. Mẫu UI đỏ sẫm và bảng tiếp tục issue — 11/11/2026

Đã áp mẫu thị giác tại [UI 3.2.1](../04-giao-dien/ui-design.md#ui-reference-red) vào nguồn Khách và bản đồ preview: đầu đỏ sẫm, thẻ trắng, tiêu đề/vùng đỏ, giữ phông và box cuộn riêng. Không thêm GPS/khoảng cách/mã phiên/quét QR trong trang hoặc đổi polygon/hợp đồng. [Hồ sơ kiểm](../06-kiem-thu/ui-reference-20261111/README.md) ghi 60/60 logic, build mặc định đạt và Chrome 8/8 trong 17,8 giây. Bốn ca bố cục dùng API fixture/audio NOT_READY, không phải bằng chứng nghe thật; bản đồ/Completeness dùng preview thật theo phạm vi riêng. Dev server kiểm đã dừng; Docker 18082 chưa được dựng lại với UI mới.

[Bảng tiếp tục issue](issue-follow-up-2026-11-11.md) bổ sung checklist còn lại T01–T11, trạng thái đề xuất In Progress/0 Done và sổ lỗi phân biệt đã sửa, đang chờ rà hoặc thiếu cấu hình/bằng chứng. Đọc lại danh sách công khai có 11 issue mở; chưa truy cập được bảng Project, không sửa issue/Project/89 ô hoặc Git ghi. G02 giữ nguyên. Dừng để rà mẫu UI và ưu tiên trước khi chuyển mốc.

<a id="osm-map-20261111"></a>

### 2.40. Nền raster OSM và khôi phục preview — 11/11/2026

Đã nối nền OpenStreetMap tiêu chuẩn như kiểu bản đồ trong mẫu UI, không chỉ đổi màu. Khách/Admin có nền HTTPS và trạng thái tải/lỗi/thử lại; vùng vẫn theo API, không tự áp đề xuất hoặc đổi hợp đồng. [Hồ sơ](../06-kiem-thu/osm-map-20261111/README.md) ghi 65/65 logic, build Windows/Docker đạt; **11/11 Chrome trên ảnh mới (2,2 phút)** và **3/3 offline riêng (1,9 phút)** đạt, cùng kiểm Compose và Hash 229/229 baseline backend không đổi. Tile browser bị chặn bằng ảnh kiểm; hai lần HEAD tile thật lỗi/reset, chưa kết luận nền thật hiển thị hoặc toàn đồ án đạt.

App mới `hueqr-osm-preview-21a39bd3`, ảnh `hueqr-local:20261111-osm-map` / `sha256:c36ae6fc13a894d19e2be4a1b468fcd65bcb0d4e53ffd838345b0698b4f854d1`, URL `http://127.0.0.1:18082/map-preview.html`. Database/audio/khóa/epoch được giữ; chuyển sang network riêng có alias `database` để tránh IP đổi sau restart. Provider/local worker/cleaner/retention vẫn tắt, 5 điểm/20 slot/5 vi nháp còn ẩn và 0 cặp áp dụng. Container app trước giữ dừng. Fixture refresh nằm trong `backend/src/test/fixtures/demo`, không đổi Java/schema. Nhánh lấy manifest/khởi động lại app cũ khi lỗi bổ sung sau lượt thành công chỉ kiểm cú pháp, chưa diễn tập rollback.

Không dùng computer use, Git ghi, thay issue/Project/checklist, cài công cụ hoặc gọi TTS. Các tiến trình dev kiểm được dừng sau hoàn tất; Docker preview tiếp tục để rà. Điểm tiếp tục: kiểm kết nối/hiển thị nền thực, duyệt vùng/nội dung/audio và hoàn tất hành trình thật/HTTPS/QR/thiết bị/vận hành. Dừng để rà mục lớn này trước chuyển mốc hoặc commit.

Rà bổ sung Admin trên cùng ảnh đạt **3/3 Chrome (1,3 phút)**: Q2 Hủy/tải lại giữ phần chưa lưu, 401 che dữ liệu rồi đăng nhập lại cùng tài khoản phục hồi, thống kê API thật/nhãn bộ lọc cũ. Ca 401 dùng phản hồi chặn, không chứng minh hết hạn 30 phút thật; không ghi nội dung/vùng/cặp. Bằng chứng nằm cùng hồ sơ 2.40, không gộp ba lượt Chrome thành một lượt full. Kiểm môi trường không thấy `HUEQR_TTS_API_KEY` hoặc `HUEQR_PUBLIC_START_URL` ở tiến trình/tài khoản; chưa đủ điều kiện kiểm provider thật hoặc QR HTTPS.

<a id="map-pin-20261111"></a>

### 2.41. Ghim đúng vị trí nhấn và thu gọn preview — 11/11/2026

Đã bỏ hàng nút trùng chú giải, gộp hướng dẫn/chờ duyệt và giữ tên điểm ở thẻ phản hồi. Khách/preview có một ghim xanh đúng tọa độ nhấn trong vùng; ngoài vùng/kéo/phóng không tạo chọn mới, chồng lấn chỉ dời sau chọn ứng viên, Hủy giữ ghim/bài cũ. Không GPS, không tọa độ mới trong API hoặc tự đặt ghim ở tâm sau reload. [Hồ sơ](../06-kiem-thu/map-pin-20261111/README.md) ghi 65/65 logic, build Windows/Docker, 3/3 ca ghim riêng và **14/14 Chrome trên ảnh cuối (1,8 phút)**. Tile kiểm có nhãn, chưa chứng minh nền OSM thật hoặc nghe MP3 sau thay đổi này.

Preview cùng URL 18082, app mới `hueqr-osm-preview-f620552b`, ảnh `hueqr-local:20261111-osm-map` / `sha256:5d49ffaea21b1a0bd457b050e836f975af230a9cd03b4d7abe8574827610896b`; giữ MySQL/audio/network/khóa/epoch và các worker tắt. Nhánh refresh lấy tên app từ manifest đã chạy thành công. Không đổi Java/schema/phụ thuộc, Git ghi hoặc checklist. Dev server kiểm đã dừng. Điểm dừng: rà thao tác ghim/bố cục; các phụ thuộc nền thật, vùng/nội dung/audio, QR/HTTPS và nghiệm thu ở mốc trước vẫn còn.

<a id="workspace-cleanup-20261112"></a>

### 2.42. Dọn thư mục trước bàn giao Git — 12/11/2026

Đã chuyển tám thư mục kết quả test frontend và dist ra LocalAppData, giữ nguồn/test/dependency; gom 15 bản sao bằng chứng giống bytes về bản chung, cập nhật liên kết và originalPath trong ledger, lưu nguyên bản metadata ngoài repo. [Hồ sơ](../06-kiem-thu/workspace-cleanup-20261112/README.md) ghi 44 file chuyển đi khớp Hash và 281/281 file nguồn được bảo vệ không đổi. Playwright mặc định xuất vào Temp; Docker context loại kết quả test và tài liệu không cần cho build.

65/65 logic và build frontend ngoài repo đạt; Playwright resolve 29 ca/8 file, chỉ là list chứ không thực chạy 29 ca. Trước dọn Source Control có 456 mục (11 sửa/445 U) trên T01; 208/242 file backend khớp blob trên nhánh task local, không suy trạng thái remote/main. Sau gom bản sao và thêm hồ sơ còn 444 mục. Không Git ghi, đổi Java/runtime, database/audio, issue/Project hoặc checklist. Khi nối commit phải phân loại từng file/nền phụ thuộc theo G02, không stage lại backend chỉ vì U tại T01.

<a id="local-commits-20261112"></a>

### 2.43. Bàn giao commit cục bộ — 12/11/2026

Đã commit các nhóm mới T02 `5724a98`, T07 `422c43f`, T05 `97490df`, T09 `acf7452`, T10 `95f1f1a`, T11 `cf9ed80`; bảng SHA đầy đủ/scope tại [commit-plan 4.19](commit-plan-2026-11-05.md#local-commits-20261112). Nhóm T01 giữ thiết kế và bản ghi bàn giao này. T02/T05/T07 đã nhận nền và khớp 229/229 file backend đã kiểm; không commit lại backend chỉ vì hiện U trên nhánh IDE.

Giữ nhánh IDE T01 và nguồn đang sửa bằng Hash; dùng worktree cùng repository để chia task. Metadata Hòa `gingpham <gingpham13052006@gmail.com>`, Thành `KhaiThanh <khaithanh2726@gmail.com>` theo G02 và lịch sử đã đối chiếu. Chưa push/xác thực tài khoản cho lượt push hoặc thay main/PR/issue/Project/checklist. Không phát sinh kết quả build/browser mới trong lượt commit.

Điểm tiếp tục: T04/T06 chờ đăng nhập/metadata đúng Nghĩa, chưa stage/commit 46 file frontend; T08 chờ chốt người ghi nhận sáu file nháp chung. Sau khi có nguồn frontend, nhận đủ nền vào các nhánh E2E/đóng gói/hướng dẫn, kiểm cây checkout trước push. Không dùng việc commit hồ sơ làm bằng chứng QR/HTTPS, nền tile thật, đủ 20 cặp được duyệt hoặc toàn đồ án hoàn tất.

## 3. Tài liệu bàn giao

| Nội dung | Nguồn chính |
| --- | --- |
| Tra cứu tài liệu | [docs/README.md](../README.md) |
| Nghiệp vụ và quyết định | [Business Flow](../02-nghiep-vu/business-flow.md), [decisions](../02-nghiep-vu/decisions.md) |
| Yêu cầu và nghiệm thu | [PRD](../02-nghiep-vu/PRD.md), [R06](../02-nghiep-vu/prd-review.md#review-open-final) |
| Giao diện | [UI khách/Admin](../04-giao-dien/ui-design.md) |
| Thiết kế kỹ thuật | [TDD](../03-ky-thuat/TDD.md), [dữ liệu](../03-ky-thuat/data-model.md), [API](../03-ky-thuat/api-transactions.md), [vận hành](../03-ky-thuat/operations-design.md), [DFD](../03-ky-thuat/dfd.md) |
| Tiến độ và cộng tác Git | [roadmap](roadmap.md), [kế hoạch commit](commit-plan-2026-11-05.md) |
| Kiểm chứng backend | [Nền dữ liệu và phiên khách](../03-ky-thuat/practice-01-start-session.md) |

## 4. Quyết định cần duy trì

- Một repository tách frontend/backend; backend theo Controller → Service → Repository. Vue build được phục vụ cùng Spring Boot; Admin dùng session/cookie.
- Phiên khách độc lập theo tab, hạn cố định 8 giờ; vé Bắt đầu ký có hạn 24 giờ và ledger theo DATA-05. Giữ service_epoch và REC-P01.
- Admin hết phiên sau 30 phút không hoạt động; bảo vệ nội dung chưa lưu theo Q2/Q3.
- Chỉ áp dụng cả cặp văn bản/audio đúng hồ sơ. Bật hiện cần thông tin/vùng hợp lệ và ít nhất cặp vi đã duyệt/áp dụng. Bộ nghiệm thu gồm 20 văn bản và 20 audio; hệ thống không bị giới hạn tối đa năm địa điểm.
- Ngoại lệ hết hạn chỉ áp dụng cho đúng bài đang phát từ MP3 đã tải trọn trong tab; không tua/nghe lại/bài mới hoặc gia hạn phiên.
- D08 giữ phiên/lượt 30 ngày; file từng áp dụng được bảo vệ 8 giờ theo A02.3. Các hạn file/log/backup và quy trình restore tuân thủ DATA hiện hành.
- Kênh CD Tailscale + OpenSSH Windows/PowerShell đã chọn nhưng chưa kiểm thực tế; ngrok Free là phương án cần thử. Model/giọng TTS đã chọn, chưa kiểm đủ bộ nội dung được duyệt.

Nguồn quyết định chi tiết và phân công duy nhất ở [decisions](../02-nghiep-vu/decisions.md); phần này chỉ tóm tắt để bàn giao.

## 5. Công việc còn mở

Cần tiếp tục xử lý nền/vị trí/ranh giới bản đồ, nội dung/nguồn/bản dịch/nghe duyệt, quota và tích hợp TTS, dữ liệu/nghiệp vụ còn lại, browser/owner/Blob, runtime bền vững, D08/file/backup/restore, ngrok/mạng, SSH/Docker/CD và các ngưỡng chưa chốt. Theo dõi [R06](../02-nghiep-vu/prd-review.md#review-open-final) và [TDD](../03-ky-thuat/TDD.md#tdd-open). Các bộ kiểm backend chỉ chứng minh phạm vi và thời điểm đã ghi trong bằng chứng.

Trách nhiệm và người commit theo [G02](../02-nghiep-vu/decisions.md#g02). Mỗi cập nhật tiến độ phải gắn với sản phẩm, kết quả kiểm chứng và issue hiện hành.

## 6. Thông tin chuyển giao

Đã lưu bằng chứng commit/push trước lần tạo lại repository ở mục 2.2 và kết quả chỉnh comment ở mục 2.3. Đích chính thức tại mục 2.4. Nguồn Draw.io cùng SVG Use Case đã kiểm ảnh theo mục 2.5, chờ nhóm rà soát trong VS Code. Mốc issue #1–#11 đã đối chiếu có 89 ô/18 dấu tích; bản local có 20 theo roadmap 6.5. Project có 6 In Progress/5 Todo theo xác nhận vận hành gần nhất, đề xuất chuyển T05 tại roadmap mục 6.4 chưa được xác nhận. Backend có nền V10, nội dung khách/grant/ACK/tải MP3 và READ claim cùng nền quản trị/worker WAV; bằng chứng hiện tại ở mục 2.24. Frontend khách đã có nguồn nhưng chưa build/browser; chưa provider/quota thật, FFmpeg/MP3 thực hoặc frontend Admin đã build/browser. Dọn vòng đời đã kiểm backend theo mục 2.27, chưa bật trên môi trường phục vụ. Các nhánh T01/T02/T03/T05/T07 đã được đối chiếu push ở mục 2.25; PR #12 và #13 giữ kết quả merge nền T01/T02. Mốc D08 tại mục 2.26, vòng đời tại mục 2.27 và A22 tại mục 2.28; không suy kiểm thử thành đã merge main. Trạng thái push ở mục 2.30, local audio mới ở 2.31 chờ rà trước commit/push. Quyền Git hiện hành tại mục 7; phần Nghĩa dừng trước đăng nhập.

<a id="guidance-protocol"></a>

## 7. Quy trình phối hợp và quyền thực hiện

1. **Chọn công việc:** căn cứ roadmap, issue hiện hành, phân công G02 và phụ thuộc; xác định mục tiêu, đầu ra và cách kiểm chứng.
2. **Triển khai:** đọc nguồn hiện có, bảo vệ sửa đổi đang có; thực hiện trong phạm vi mục checklist đã chọn. Tránh tạo file/lớp trống hoặc thay nghiệp vụ để thuận tiện triển khai.
3. **Kiểm chứng:** chạy kiểm tra liên quan và full suite khi có lý do; dùng môi trường/database tạm tách biệt. Không làm yếu assertion hoặc dùng dữ liệu đang phục vụ thay cho fixture.
4. **Rà soát:** báo sản phẩm, thay đổi chính, kết quả và giới hạn. Hoàn thành một phần phải ghi rõ phần còn thiếu.
5. **Mốc bàn giao:** lưu sản phẩm và bằng chứng sau mỗi mục lớn, dừng để nhóm kiểm tra trước khi chuyển sang mục tiếp theo hoặc commit/push theo nhịp tại mục 7.3. Dừng khi cần quyết định, thao tác môi trường hoặc kiểm chứng cùng nhóm; chuẩn bị commit khi có yêu cầu nối lại. Không coi từng file/hàm/test là một mục checklist mới.
6. **Checklist:** đối chiếu đúng issue và nguyên văn ô trước khi ghi nhận hoàn thành. Người vận hành trực tiếp tích/sửa/đóng issue; chưa đủ toàn ô thì không tích.

**Quyền Git hiện hành:** công cụ được trực tiếp kiểm tra Git, stage/commit/push theo từng task sau mỗi mục lớn đã kiểm chứng; hiện thực hiện phần Thành và Hòa, dừng trước phần Nghĩa để đăng nhập. Giữ tài khoản/metadata có căn cứ theo G02, không tự force-push, viết lại lịch sử hoặc ghi đè sửa đổi. Quyền này không tự bao gồm merge PR, tích/đóng issue hoặc đổi Project.

**Thao tác do người vận hành thực hiện:** database đang dùng và migration trên đó, TTS/API bên ngoài, CI/CD, khởi động môi trường phục vụ và triển khai. Thay đổi GitHub khác chỉ thực hiện khi được giao cụ thể; việc tạo issue/thêm Project ngày 08/11 và dọn container kiểm thử ngày 09/11 giữ đúng phạm vi đã giao. Công cụ hỗ trợ được đọc tệp, biên tập tài liệu, triển khai/kiểm thử tách biệt và đọc issue trong quyền đã xác nhận.

Không tự cài công cụ hệ thống, đổi nguồn tải, bật billing hoặc thay tài khoản/dịch vụ. Không sửa/xóa/di chuyển các dự án tham khảo, sao chép secret/database/audio/lịch sử Git hoặc đưa dữ liệu nhạy cảm vào tài liệu.

<a id="vietnamese-comments"></a>

### 7.1. Bố cục Java và comment tiếng Việt

- Giữ tên file/package/class/hàm/thuộc tính, annotation, dữ liệu và logic. Dùng comment `//` tiếng Việt có dấu; không JavaDoc, không dấu chấm cuối note. Ghi ngắn, cụ thể; dùng từ **mã Hash**.
- Bố cục Java đặt mỗi câu lệnh trên một dòng. Dùng một dòng trống giữa các hàm hoặc các nhóm xử lý khác nhau, đặt các câu lệnh cùng một việc liền nhau. Bỏ dòng trống ngay sau dấu mở lớp và ngay trước dấu đóng lớp.
- Mỗi Entity có một note ngắn về dữ liệu lớp đại diện. Mỗi trường lưu dữ liệu có một note tiếng Việt cuối dòng nêu ý nghĩa của trường. Đặt annotation sát trường và cách hai cụm trường một dòng trống. Trong GuestSession, note của `id` là `Id phiên tham quan`, note của `startRequestKey` là `Khóa chống tạo trùng phiên`. Khi giải thích liên kết Entity, phân biệt đối tượng Java liên kết với giá trị khóa lưu trong database theo mapping.
- Ngoài các note quy định cho Entity, chỉ viết comment khi bổ sung thông tin khó thấy từ tên hoặc thân code. Ưu tiên quy tắc nghiệp vụ, lý do kỹ thuật, trường hợp biên và mục đích của ca test phức tạp.
- Với Service và Controller, đặt note trên hàm hoặc sát đoạn xử lý khi cần làm rõ mục đích, quy tắc hoặc lý do. Ví dụ, trong StartTicketTimePolicy, phân biệt kiểm các mốc của vé để tra lại phiên cũ với xét hạn vé khi tạo phiên lần đầu.
- Với DTO, getter/setter, constructor, hàm phụ và dữ liệu test đơn giản, để tên và code diễn đạt hành vi đã rõ. Chỉ thêm note khi có thông tin quan trọng khó suy ra, như cách dựng ca kiểm thử đồng thời.
- Comment test chỉ bổ sung điều kiện hoặc kết quả quan trọng mà tên test và assertion chưa diễn đạt đủ. Khi cần chú thích hàm hỗ trợ test, mô tả chính xác việc tạo Entity, lưu database hoặc kiểm kết quả.
- Khi cần giải thích hàm xử lý lỗi có HTTP status cố định, có thể dùng `// HTTP <mã> - <nguyên nhân>`. Comment cho hàm xử lý nhiều status cần phản ánh đúng phạm vi của hàm.
- Dùng đúng mã lỗi/tên trong code; ưu tiên `trả mã LOCATION_NOT_FOUND` khi đó là hành vi thực. Không mô tả kiểm vài chuỗi thành ẩn mọi thông tin nội bộ, gán `active = false` thành đã có chức năng khóa, hoặc `entityManager.clear()` thành xóa bản ghi. Giá trị mặc định phải nêu đúng giá trị và nguồn nếu có căn cứ.
- Đặt comment sát đúng đoạn nó giải thích. Nếu giải thích cả hàm, đặt ngay trên annotation hoặc khai báo. Cách mở đầu theo nội dung cần làm rõ, ưu tiên thông tin bổ sung thay cho việc nhắc lại tên hoặc từng dòng code.
- Khi chỉ được giao chỉnh note, giữ nguyên code/logic, nêu riêng lỗi đáng nghi. Không sửa migration đã áp dụng chỉ để đổi comment, file sinh tự động hoặc thư viện ngoài. Có thể đọc demo để tham khảo cách diễn đạt, không kế thừa nghiệp vụ cũ.
- Tài liệu giải thích mã nguồn nêu mục đích trước, sau đó mô tả các hàm theo thứ tự. Chỉ đưa bảng hoặc toàn bộ mã khi cần cho việc rà soát; dẫn đến file nguồn thay vì duy trì bản sao song song.

Ví dụ comment bổ sung lý do kỹ thuật và điều kiện quan trọng

```java
// Cắt thời gian đến micro giây để khớp DATETIME(6) khi lưu phiên
// Đọc lại phiên trong giao dịch mới vì giao dịch tranh chấp khóa duy nhất đã hoàn tác
```

Quy tắc áp dụng cho mã nguồn do nhóm quản lý và cấu hình có hỗ trợ comment, dùng đúng cú pháp của từng định dạng. Giữ giấy phép/bản quyền, mã thư viện, Wrapper, tệp build/sinh tự động và tài sản dữ liệu. Bản ghi rà soát tại [mục 2.3](#comment-audit-20261108) phản ánh quy ước tại thời điểm thực hiện. Việc áp dụng quy ước vào code và chuẩn bị commit được thực hiện khi có yêu cầu riêng.

<a id="issue-git-handoff"></a>

### 7.2. Nhánh, commit và bàn giao Git

- Sau mỗi mục lớn đã kiểm chứng, dừng để nhóm rà kết quả. Khi tiếp tục Git, được thực hiện commit/push trực tiếp trong phạm vi đã giao tại mục 7 và 2.24. Hiện làm phần Thành/Hòa; dừng trước T04/T06 để đăng nhập tài khoản Nghĩa. Giữ phân công G02, các mốc kiểm và bằng chứng; PR/merge/issue không tự mở rộng từ quyền commit/push.
- Nhánh task: T01–T11. Thông điệp tiếng Việt: `Txx: Những việc đã thực hiện`. Một issue có thể có nhiều commit theo sản phẩm; commit không tự hoàn thành hoặc đóng issue.
- Thành, Hòa và Nghĩa phối hợp trên ba máy riêng theo G02. Mỗi nhóm commit/push sử dụng tài khoản GitHub của thành viên phụ trách task; nguồn ánh xạ và quy tắc metadata ở G02. Phân công, người nhập lệnh, metadata author/committer và tài khoản xác thực là các thông tin cần đối chiếu riêng; không suy kết quả thực hiện từ việc chọn tài khoản.
- Trình bày gọn toàn bộ quy trình của một nhóm commit: điều kiện/chuẩn bị → stage và xem staged diff → commit/push/kiểm kết quả. Mỗi khối có lệnh, kết quả mong đợi và điều kiện dừng; bước phụ thuộc chưa đủ điều kiện phải được ghi rõ.
- Dùng các kết quả đã nhận, chỉ kiểm lại khi cấu hình/trạng thái thay đổi hoặc có lý do kỹ thuật mới. Thông tin chặn cần được xác định cùng một lần thay vì chia nhỏ theo từng lệnh.
- **Mốc A:** đối chiếu task/tài khoản theo G02, metadata Git, bằng chứng xác thực phù hợp và danh sách/nội dung staged trước commit. Không stage toàn repository khi chưa kiểm phạm vi.
- **Mốc B:** sau commit/push, đối chiếu commit, SHA local/remote, trạng thái nhánh và tài khoản hiển thị trên GitHub. Không yêu cầu gửi sau từng lệnh thành công; lỗi hoặc kết quả khác kỳ vọng thì dừng trước bước phụ thuộc.
- PR/merge được thực hiện sau rà soát. Nhánh phụ thuộc nhận nền đã tích hợp, có kiểm trạng thái trước cập nhật; không tự stash/reset/clean hoặc ghi đè sửa đổi.
- **Description của PR:** liệt kê từng commit bằng mã SHA ngắn và tiêu đề, kèm các gạch đầu dòng giải thích sản phẩm thay đổi và mục đích. Viết theo góc nhìn dự án nhóm, dùng nội dung cụ thể có căn cứ; không biến kế hoạch thành kết quả đã thực hiện. Câu cuối dẫn issue liên quan và nêu rõ vai trò của sản phẩm trong PR đối với chức năng hoặc task tiếp theo, thay cho câu kết chung chung. Kết quả kiểm chứng và giới hạn ảnh hưởng đến việc rà soát/tích hợp vẫn phải được nêu rõ khi có.

<a id="execution-mode"></a>

### 7.3. Nhịp triển khai và kiểm tra tại mốc bàn giao

Triển khai từng mục checklist khả thi theo phụ thuộc, thực hiện liền mạch từ viết code đến kiểm thử trong môi trường tách biệt. Việc tạm hoãn commit không chặn công việc này. Kế hoạch bàn giao qua nhánh task, PR và tích hợp vẫn giữ trong commit-plan để thực hiện khi tiếp tục Git.

Trong mỗi mục lớn, triển khai và kiểm thử liền mạch, lưu sản phẩm và bằng chứng rồi dừng để nhóm rà trước khi tiếp tục hoặc commit/push. Không tách từng file, hàm hoặc ca test thành một mốc phải chờ riêng. Phần thiếu công cụ chưa cài tạm gác đúng phụ thuộc, triển khai và kiểm thử các phần độc lập trước để nhóm cùng rà. Ghi rõ mục nào chưa được kiểm và công cụ/đầu vào cần bổ sung; nhóm cùng chuẩn bị môi trường rồi hoàn thiện phần còn thiếu. Chỉ đánh dấu hoàn thành khi đủ toàn điều kiện của ô checklist; việc hoãn commit hoặc một công cụ chưa có không tự chặn toàn dự án.

Tạm dừng sớm khi thiếu quyết định quan trọng, gặp lỗi chặn hoặc cần thao tác thuộc quyền người vận hành. Việc tiếp tục phải dựa trên kết quả mốc kiểm, không dựa riêng vào việc đã có code hoặc commit.

## 8. Nhật ký theo thời điểm



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-handoff-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
