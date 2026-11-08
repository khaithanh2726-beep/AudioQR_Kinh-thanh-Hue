# Bàn giao — trạng thái và quy trình dự án

**Cập nhật:** 09/11/2026. Tài liệu ghi nhận sản phẩm hiện có, phạm vi kiểm chứng, công việc tiếp theo và quy trình phối hợp. Phê duyệt yêu cầu hoặc thiết kế không thay thế bằng chứng kiểm thử và nghiệm thu.

## 1. Sản phẩm và bằng chứng hiện có

| Hạng mục | Bằng chứng gần nhất | Phạm vi chưa được chứng minh |
| --- | --- | --- |
| Nền backend | Java 21.0.12.1, Maven Wrapper 3.9.12, Boot 4.1.2, Security 7.1.2, MySQL 8.4.13 Testcontainers. V1–V4 và mapping đã kiểm, nâng V1 có dữ liệu lên V2 vẫn đạt | Toàn schema/seeds, tài khoản thật và môi trường phục vụ bền vững |
| API phiên khách | K11 cấp vé ký 24 giờ, K01 tạo/replay phiên 8 giờ, K02 kiểm trạng thái, K04 ghi kết thúc một lần và replay. Đã kiểm ba lớp, binding/ledger/expiry và concurrent | Grant/nội dung/audio, frontend/owner tab, runtime thật/restart với ledger bền, D08 và backup/restore |
| Xác thực Admin | A01–A05, session/CSRF, Q3 30 phút, mã Hash mật khẩu, đổi session id và logout. Header cookie và vòng đời đã kiểm qua HTTP localhost thật | Q2/Q3 trên frontend, tài khoản cấp cho môi trường phục vụ và HTTPS/proxy/browser thực |
| Lệnh Admin và địa điểm | A31/A06/A08, cửa sổ 24 giờ, receipt cùng giao dịch, tạo điểm ẩn, đọc dữ liệu cơ bản A07 và sửa tên A09/If-Match | Điều kiện bật hiện/mức đầy đủ A07, vùng/cặp, ẩn/hiện/xóa, frontend Q2 và worker dọn metadata |
| Hồi quy backend | 168 test, 0 lỗi, 0 bỏ qua, BUILD SUCCESS, mã thoát 0 lúc 08/11/2026 20:16:49 +07, đối chiếu 19 báo cáo Surefire. Kết quả 64/88/104/122/147 test trước giữ theo thời điểm | Mỗi commit trung gian, toàn bộ tiêu chí nghiệm thu và CI/CD |
| Git | T10 nền và tài liệu T01 đã push theo mục 2.11/2.13; nền backend T01, T03/T05 và dữ liệu T02 đã commit cục bộ theo mục 2.14. Ba commit cũ giữ riêng ở mục 2.2 | Push các commit mới, PR/merge, kiểm chứng từng nhánh và liên kết tài khoản trên trang commit GitHub |
| Backlog | Issue #1–#11 đã đối chiếu với T01–T11: 89 ô/18 dấu tích | Commit hoặc tạo issue không đồng nghĩa hoàn thành task |

[Bằng chứng kiểm chứng](../03-ky-thuat/practice-01-start-session.md#practice-results) lưu thời điểm, kết quả và giới hạn. Metadata Git không chứng minh tài khoản dùng để xác thực GitHub hoặc ai đã thực hiện phần việc. Phân công và tài khoản theo G02 được đối chiếu riêng với bằng chứng.

<a id="next-step"></a>

## 2. Điểm dừng và công việc tiếp theo

Repository: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue). Bảng công việc: [Project 21](https://github.com/users/khaithanh2726-beep/projects/21/views/1). Thư mục làm việc đã ghi nhận: `C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue`.

**Đọc để tiếp tục:** [mục 2.14](#local-commit-batch-20261109) ghi kết quả commit cục bộ và điểm dừng trước push; [mục 2.13](#current-t01-docs-result) ghi kết quả push tài liệu T01; [mục 2.12](#document-review-result) ghi kết quả rà tài liệu; [mục 7](#guidance-protocol) quy định quyền và các mốc thao tác. Các mục trước giữ sản phẩm và diễn biến bàn giao theo thời điểm, không thay trạng thái mới nhất. [Roadmap](roadmap.md) là nguồn checklist; phần này ghi bằng chứng và điểm tiếp tục.

<a id="issue-recreation"></a>

### 2.1. Issue và Project hiện có

- Nguồn duy nhất của tiêu đề, Description và Checklist là [roadmap mục 3](roadmap.md#t01). Giữ 11 task/89 ô/18 dấu tích và phân công G02.
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

Đã lưu bằng chứng commit/push trước lần tạo lại repository ở mục 2.2 và kết quả chỉnh comment ở mục 2.3. Đích chính thức tại mục 2.4. Nguồn Draw.io cùng SVG Use Case đã kiểm ảnh theo mục 2.5, chờ nhóm rà soát trong VS Code. Issue #1–#11 giữ 89 ô/18 dấu tích. Project có 6 In Progress/5 Todo theo xác nhận vận hành gần nhất, đề xuất chuyển T05 tại roadmap mục 6.4 chưa được xác nhận. Backend dừng ở API đọc/sửa tên địa điểm tại mục 2.9, 168 test đạt. Nền T10 đã commit/push theo mục 2.11; commit-plan đã khôi phục, nhóm tài liệu đủ 33 file và đang rà trước T01 theo mục 2.12.

<a id="guidance-protocol"></a>

## 7. Quy trình phối hợp và quyền thực hiện

1. **Chọn công việc:** căn cứ roadmap, issue hiện hành, phân công G02 và phụ thuộc; xác định mục tiêu, đầu ra và cách kiểm chứng.
2. **Triển khai:** đọc nguồn hiện có, bảo vệ sửa đổi đang có; thực hiện trong phạm vi mục checklist đã chọn. Tránh tạo file/lớp trống hoặc thay nghiệp vụ để thuận tiện triển khai.
3. **Kiểm chứng:** chạy kiểm tra liên quan và full suite khi có lý do; dùng môi trường/database tạm tách biệt. Không làm yếu assertion hoặc dùng dữ liệu đang phục vụ thay cho fixture.
4. **Rà soát:** báo sản phẩm, thay đổi chính, kết quả và giới hạn. Hoàn thành một phần phải ghi rõ phần còn thiếu.
5. **Mốc bàn giao:** dừng sau toàn mục checklist hoặc issue có bằng chứng để rà soát, kiểm thử tích hợp phù hợp và chuẩn bị commit. Không coi từng file/hàm/test là một mục checklist mới.
6. **Checklist:** đối chiếu đúng issue và nguyên văn ô trước khi ghi nhận hoàn thành. Người vận hành trực tiếp tích/sửa/đóng issue; chưa đủ toàn ô thì không tích.

**Thao tác do người vận hành thực hiện:** mọi lệnh Git, kể cả chỉ đọc, thay đổi GitHub, database đang dùng và migration trên đó, TTS/API bên ngoài, CI/CD, khởi động môi trường phục vụ và triển khai. Hành động GitHub cụ thể được giao thêm, như tạo issue và thêm vào Project ngày 08/11, được thực hiện trong đúng phạm vi ủy quyền khi công cụ hỗ trợ. Công cụ hỗ trợ được đọc tệp, biên tập tài liệu và triển khai/kiểm thử tách biệt khi được giao. Việc đọc issue chỉ đọc được thực hiện trong phạm vi đã cho phép.

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

- Trạng thái tiếp tục/hoãn commit theo điểm bàn giao hiện hành. Đã có yêu cầu tiếp tục phần Thành/Hòa tại mục 2.10; giữ phân công G02 và các mốc kiểm bên dưới, chỉ hướng dẫn thao tác phụ thuộc sau khi đủ bằng chứng đầu vào.
- Nhánh task: T01–T11. Thông điệp tiếng Việt: `Txx: Những việc đã thực hiện`. Một issue có thể có nhiều commit theo sản phẩm; commit không tự hoàn thành hoặc đóng issue.
- Thành, Hòa và Nghĩa phối hợp trên ba máy riêng theo G02. Mỗi nhóm commit/push sử dụng tài khoản GitHub của thành viên phụ trách task; nguồn ánh xạ và quy tắc metadata ở G02. Phân công, người nhập lệnh, metadata author/committer và tài khoản xác thực là các thông tin cần đối chiếu riêng; không suy kết quả thực hiện từ việc chọn tài khoản.
- Trình bày gọn toàn bộ quy trình của một nhóm commit: điều kiện/chuẩn bị → stage và xem staged diff → commit/push/kiểm kết quả. Mỗi khối có lệnh, kết quả mong đợi và điều kiện dừng; bước phụ thuộc chưa đủ điều kiện phải được ghi rõ.
- Dùng các kết quả đã nhận, chỉ kiểm lại khi cấu hình/trạng thái thay đổi hoặc có lý do kỹ thuật mới. Thông tin chặn cần được xác định cùng một lần thay vì chia nhỏ theo từng lệnh.
- **Mốc A:** đối chiếu task/tài khoản theo G02, metadata Git, bằng chứng xác thực phù hợp và danh sách/nội dung staged trước commit. Không stage toàn repository khi chưa kiểm phạm vi.
- **Mốc B:** sau commit/push, đối chiếu commit, SHA local/remote, trạng thái nhánh và tài khoản hiển thị trên GitHub. Không yêu cầu gửi sau từng lệnh thành công; lỗi hoặc kết quả khác kỳ vọng thì dừng trước bước phụ thuộc.
- PR/merge được thực hiện sau rà soát. Nhánh phụ thuộc nhận nền đã tích hợp, có kiểm trạng thái trước cập nhật; không tự stash/reset/clean hoặc ghi đè sửa đổi.

<a id="execution-mode"></a>

### 7.3. Nhịp triển khai và kiểm tra tại mốc bàn giao

Triển khai từng mục checklist khả thi theo phụ thuộc, thực hiện liền mạch từ viết code đến kiểm thử trong môi trường tách biệt. Việc tạm hoãn commit không chặn công việc này. Kế hoạch bàn giao qua nhánh task, PR và tích hợp vẫn giữ trong commit-plan để thực hiện khi tiếp tục Git.

Khi hoàn thành mục triển khai đã chọn hoặc toàn mục checklist có bằng chứng, dừng để nhóm rà soát sản phẩm và kiểm thử nhẹ phù hợp. Trong thời gian hoãn commit, lưu kết quả và điểm tiếp tục trong tài liệu bàn giao. Kiểm thử nhẹ hỗ trợ kiểm tra tích hợp tại mốc bàn giao. Chỉ đánh dấu hoàn thành khi đủ toàn điều kiện của ô checklist, tiếp tục xử lý các thay đổi nhỏ thuộc cùng mục khi không có trở ngại.

Tạm dừng sớm khi thiếu quyết định quan trọng, gặp lỗi chặn hoặc cần thao tác thuộc quyền người vận hành. Việc tiếp tục phải dựa trên kết quả mốc kiểm, không dựa riêng vào việc đã có code hoặc commit.

## 8. Nhật ký theo thời điểm



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-handoff-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
