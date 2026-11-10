# Kế hoạch commit — nền tài liệu, dữ liệu và API

**Cập nhật:** 09/11/2026. Kế hoạch tổ chức bàn giao cho Thành, Hòa và Nghĩa trên ba máy riêng, tích hợp qua repository chung. Bảng phân công là trách nhiệm dự kiến; trạng thái hoàn thành phải căn cứ sản phẩm và bằng chứng. Quy trình Git chung tại [handoff 7.2](handoff.md#issue-git-handoff).

**Kết quả mới nhất:** phần nguồn đã kiểm của Thành/Hòa được commit theo T01/T02/T03/T05/T07; bảng kết quả tại [mục 4.12](#commit-push-result-20261109). Các nhãn “chưa commit” và “tạm hoãn” ở các mốc 4.5–4.11 phản ánh thời điểm kiểm kê trước lần bàn giao này, không thay kết quả mới. Phần Nghĩa dừng trước đăng nhập.

## 1. Điều kiện và trạng thái đầu vào

- Repository: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue); Project 21 và mapping #1–#11 tại [roadmap mục 6](roadmap.md#github-checklist-review).
- Ba commit/push T10 và T01 ngày 07–08/11 trước lần tạo lại repository đã được đối chiếu qua đầu ra Terminal. SHA, nhánh, metadata và giới hạn tại [handoff mục 2.2](handoff.md#bootstrap-result). Chưa có bằng chứng các SHA này trên repository vừa tạo lại hoặc PR/merge mới.
- Nền T10 và các nhóm T01/T03/T05/T02 trước phần vùng đã commit/push theo [handoff mục 2.11–2.15](handoff.md#pr12-review-result); PR #12/#13 tích hợp nền T01/T02 đã merge. Đã tiếp tục commit/push phần Thành và Hòa theo [mục 2.24](handoff.md#guest-content-frontend-result); dừng trước phần Nghĩa để đăng nhập. Phần vùng/văn bản/job và nội dung khách được phân nhóm ở mục 4; chỉ ghi nhận đã commit/push sau khi đối chiếu kết quả. PR/merge tiếp theo cần yêu cầu riêng và đủ phụ thuộc.
- Nhóm tài liệu gồm 33 file tính cả README gốc, nguồn Draw.io và tài sản; commit-plan đã được khôi phục sau khi bị xóa nhầm. Các nhóm backend theo mục 2 và 4. Đích chính thức tại [handoff mục 2.4](handoff.md#new-target-pending), trạng thái Git mới nhất theo mục 2.17; không suy main local đã cập nhật chỉ từ kết quả merge trên GitHub.
- Mỗi nhóm commit/push dùng tài khoản GitHub theo G02. Metadata author/committer, người nhập lệnh và tài khoản xác thực là các thông tin riêng; cấu hình user.name/user.email không chọn tài khoản đăng nhập GitHub. Metadata cần có căn cứ, không tự suy email từ username.
- Nhánh task là T01–T11; thông điệp tiếng Việt dạng `Txx: Những việc đã thực hiện`. Phân công duy nhất tại [G02](../02-nghiep-vu/decisions.md#g02).
- Không stage toàn repository, tạo thông tin tác giả không có căn cứ, reset/clean hoặc ghi đè sửa đổi để hợp thức hóa kết quả. Bản ghi kiểm tra phải phân biệt đã code/test, đã commit, đã push và đã merge.

<a id="commit-groups"></a>

## 2. Phân nhóm sản phẩm và phụ thuộc

Bảng dưới giữ phạm vi các mốc 0–5 đã commit/push tại repository hiện tại; phần vùng/văn bản/job/báo cáo/audio-cặp và nghiệp vụ áp dụng/bật-ẩn mới chưa commit được kiểm kê riêng ở mục 4.5–4.11. Kết quả trước lần tạo lại repository lưu riêng tại handoff mục 2.2. Khi nối lại Git, chọn bước theo trạng thái mới và bảo vệ sửa đổi hiện có, không mặc định khởi tạo lại mốc 0. Phân công và phụ thuộc task giữ theo G02/roadmap.

| Mốc | Nhánh | Nội dung / số file theo kiểm kê | Người phụ trách | Người commit | Thông điệp đề xuất |
| --- | --- | --- | --- | --- | --- |
| 0 | main — khởi tạo | Chỉ `.gitignore` gốc hiện có — 1 | Hòa (T10) | Hòa | `T10: Khởi tạo repository với cấu hình bỏ qua file Java` |
| 1 | T01 | Tài liệu, thiết kế, nguồn hình và bàn giao — 33 | Thành | Thành | `T01: Bổ sung tài liệu nghiệp vụ, thiết kế và hướng dẫn bàn giao` |
| 2 | T01 | Khung backend, Maven Wrapper, chính sách tên dùng chung và hỗ trợ test — 12 | Thành | Thành | `T01: Bổ sung khung backend, chính sách tên và cấu hình hỗ trợ kiểm thử` |
| 3 | T02 | V1–V4, Entity/Repository và test dữ liệu — 21 | Hòa | Hòa | `T02: Tạo dữ liệu phiên khách, địa điểm và nền quản trị` |
| 4 | T03 | Vé ký, input, runtime config, K11/K01/K02/K04 và test — 23 | Thành | Thành | `T03: Xây dựng API cấp vé, bắt đầu, kiểm tra và kết thúc phiên khách` |
| 5 | T05 | API xác thực/Q3, cửa sổ/receipt, tạo/đọc/sửa tên điểm và test — 30 | Thành | Thành | `T05: Xây dựng phiên quản trị và API tạo, đọc, sửa tên địa điểm` |

Nhóm tài liệu gồm 33 file tính cả README gốc và tài sản; d00bb03 có 31 file ở thời điểm cũ. Backend hiện có 194 file, gồm 12 nền T01, 56 dữ liệu T02, 36 vé/API khách T03, 70 quản trị T05 và 20 TTS T07. Nguồn Vue khách T04 có 14 file, chưa resolve/build hoặc kiểm browser; chi tiết tại mục 4.11. Mốc đã commit trước vùng có 86 file theo bảng trên; chín Java vùng ở mục 4.5 thuộc T05; 17 file văn bản ở mục 4.6 thuộc T02/T05, 10 file hàng đợi ở mục 4.7 thuộc T02/T07; 33 file worker/đọc dữ liệu ở mục 4.8 thuộc T02/T05/T07; 12 file audio/cặp/seed ở mục 4.9 thuộc T02; 11 Java nghiệp vụ cặp/bật-ẩn ở mục 4.10 thuộc T05. Các sửa đổi trên file cũ được ghi riêng, không cộng trùng. Schema/Entity/Repository thuộc T02; điều phối nghiệp vụ/HTTP và test API thuộc T03/T05, tạo/theo dõi audio thuộc T07. Tài liệu và các thay đổi nguồn sau mốc commit vẫn còn cục bộ, không suy từ kiểm kê thành đã commit/push.

Mốc 0 đã tạo commit nền main chỉ gồm .gitignore gốc để có chung gốc lịch sử và mở Pull Request cho các nhánh task. Hòa phụ trách mốc này trong T10. Các thay đổi T10 sau mốc khởi tạo được thực hiện trên nhánh T10. Ignore hiện mới là mẫu Java; commit nền không hoàn thành mục ignore/cấu hình/secret hoặc toàn T10.

Thứ tự tích hợp vào main là T01 → T02 → T03 → T05. Tài liệu và khung backend T01 được rà soát, push, PR và merge làm nền; T02 nhận nền này trước khi bàn giao dữ liệu. Một PR có thể chỉ bàn giao phần nền đã kiểm, chưa đóng cả issue. Cách merge dự kiến là Create a merge commit.

**Thứ tự tạo commit theo thành viên.** Phần sản phẩm hiện có của Thành được commit/push trước: hoàn tất nền T01, tạo T03 từ đầu nhánh T01 đã bàn giao, rồi tạo T05 từ đầu nhánh T03 đã bàn giao. T05 cần cấu hình và hỗ trợ test thuộc T03. Sau đó Hòa tạo T02 từ nền T01, không lấy T03/T05 làm nền dữ liệu. T03/T05 lúc chưa nhận T02 là các nhánh bàn giao còn thiếu phụ thuộc, chưa sẵn sàng build/test độc lập hoặc merge vào main; PR nếu mở chỉ ở trạng thái Draft. Khi T02 được kiểm và tích hợp, nhận T02 vào T03, kiểm chứng và tích hợp T03, rồi cập nhật T05 từ T03 đã đủ phụ thuộc trước khi kiểm chứng và tích hợp T05. Không chuyển file T02 sang commit của Thành để làm đầy nhánh.

**File dùng chung.** pom.xml thêm starter-security theo BOM Boot 4.1.2, application.properties có cấu hình cookie Admin và ánh xạ khóa/epoch khách. Hai cập nhật này phục vụ T05 trên các file khung T01, khi tiếp tục Git cần đối chiếu diff để bàn giao đúng phụ thuộc. DisplayNamePolicy được đặt trong nền T01 vì cả T03 và T05 dùng, tránh tạo phụ thuộc T03 → T05; giới hạn tên khách vẫn 50 và địa điểm vẫn 100. TestcontainersConfiguration giữ MySQL và fixture công khai. Các kết quả kiểm thử toàn cây tại [thực hành phiên khách](../03-ky-thuat/practice-01-start-session.md#practice-results), [địa điểm/vùng](../03-ky-thuat/data-model.md#place-region-test-result), [xác thực Admin](../03-ky-thuat/api-transactions.md#admin-auth-test-result) và [cửa sổ lệnh](../03-ky-thuat/api-transactions.md#admin-command-test-result) giữ theo thời điểm, chưa chứng minh từng commit trung gian build/test đạt.

### 2.1. Phối hợp trên ba máy riêng

| Máy làm việc | Thành viên | Phần việc theo G02 | Điểm nhận và bàn giao |
| --- | --- | --- | --- |
| Máy Thành | Thành | T01/T03/T05/T07/T11 và phần T08 | Nhận main có commit khởi tạo; làm T01 và PR làm nền chung. Các task sau nhận main có đúng phụ thuộc |
| Máy Hòa | Hòa | T02/T09/T10 và phần T08 | Khởi tạo main; nhận nền T01 đã merge để làm T02. Bàn giao dữ liệu trước T03; tổng hợp kiểm thử và vận hành theo sản phẩm thực tế |
| Máy Nghĩa | Nghĩa | T04/T06 và phần T08 | Nhận nền và hợp đồng API; làm nhánh giao diện, PR/merge theo chức năng; không gộp nguồn backend vào commit giao diện |

Mỗi máy có clone/thư mục làm việc và cấu hình tài khoản theo phần việc G02. Tài khoản dùng để xác thực push được chọn riêng với metadata commit. Các máy trao đổi commit qua origin; người nhận chỉ nhận được nội dung đã commit/push. File untracked không được truyền qua clone/fetch/pull. Bộ nguồn được phân theo danh sách task trước khi bàn giao cho từng máy. Trạng thái hiện tại theo handoff mục 2.16; các commit trước lần tạo lại repository giữ riêng ở mục 2.2.

Sau PR được rà soát và merge, thành viên nhận task phụ thuộc cập nhật nền main. Nếu nhánh có sửa đổi riêng, phải kiểm trạng thái/diff và chọn cách tích hợp phù hợp; không chạy khối lệnh cập nhật nền khi chưa rõ trạng thái hoặc tự reset để bỏ sửa đổi.

<a id="current-commit-workflow"></a>

## 3. Điểm dừng và quy trình tiếp tục commit

**Tiếp tục commit/push:** sau mỗi mục lớn đã kiểm chứng, công cụ được trực tiếp commit/push phần Thành và Hòa theo [handoff mục 7](handoff.md#guidance-protocol); dừng trước phần Nghĩa để đăng nhập. Điểm bàn giao nguồn tại [handoff 2.24](handoff.md#guest-content-frontend-result). Các hướng dẫn PR/merge bên dưới chỉ thực hiện khi có yêu cầu riêng; kết quả commit/push/merge cũ giữ nguyên.

Đích repository/Project và issue #1–#11 đã được đối chiếu theo handoff. Nền T01/T02 đã merge qua PR #12/#13; nguồn mới có kiểm chứng toàn cây, chưa suy thành từng nhánh riêng đã build đạt. Trước mỗi nhóm commit, đối chiếu nhánh/index và bảo vệ nguồn untracked trong thư mục IDE; dùng worktree riêng khi cần nhận nền phụ thuộc. Dừng trước phần Nghĩa; chỉ chuẩn bị PR/merge khi được giao riêng.

### 3.1. Điều kiện và chuẩn bị

Sử dụng hai URL chính thức đã nhận, đối chiếu repository/Project và các issue đã có. Giữ mapping hiện có cho đến khi URL mới được đối chiếu. Khi được giao tiếp tục Git, sử dụng bằng chứng nhánh/HEAD/index/remote còn hiệu lực do người vận hành cung cấp để chọn bước phù hợp. Tài khoản và metadata theo G02; email cần có căn cứ cho từng thành viên.

### 3.2. Stage và xem nội dung

Mỗi nhóm có danh sách file, mục đích, lệnh xem staged diff, kết quả mong đợi và điều kiện dừng. Mốc A đối chiếu task, tài khoản, metadata và phạm vi/nội dung staged trước commit. Nhóm tài liệu có 33 file; kiểm kê backend hiện tại 12/56/36/70/20 file theo T01/T02/T03/T05/T07. Phải đối chiếu thay đổi mới với các commit đã có, không stage lại toàn nhóm chỉ vì có trong danh sách. Comment đi cùng file thuộc task tương ứng.

### 3.3. Commit, push, PR và tích hợp

Chỉ hướng dẫn bước phụ thuộc khi mốc trước đủ căn cứ, giữ thông điệp Txx tiếng Việt theo bảng nhóm. Mốc B đối chiếu commit, SHA local/remote, trạng thái nhánh và liên kết tài khoản trên GitHub. PR nhận đúng nền phụ thuộc, được rà soát trước tích hợp; cách merge dự kiến là Create a merge commit. Các thành viên nhận nền đã tích hợp qua repository chung trước task phụ thuộc.

Khi kết quả khác kỳ vọng hoặc có lỗi, dừng trước bước phụ thuộc và báo nguyên kết quả. Quyền trực tiếp kiểm tra Git, commit/push phần Thành/Hòa và điểm dừng đăng nhập Nghĩa theo handoff mục 7; thao tác GitHub khác chỉ thực hiện khi được giao cụ thể.

## 4. Danh sách backend cho các mốc sau

Các danh sách dưới là phương án phân nhóm, chưa là lệnh yêu cầu stage ngay. Trước mỗi nhóm phải kiểm branch, các file dùng chung, phụ thuộc và diff thực tế. Không đổi code hoặc dựng lại phiên bản cũ chỉ để tách commit.

### 4.1. Nền backend — T01

```powershell
$backendBaseFiles = @(
    'backend/.gitattributes'
    'backend/.gitignore'
    'backend/.mvn/wrapper/maven-wrapper.properties'
    'backend/mvnw'
    'backend/mvnw.cmd'
    'backend/pom.xml'
    'backend/src/main/java/vn/hueqr/backend/BackendApplication.java'
    'backend/src/main/java/vn/hueqr/backend/service/DisplayNamePolicy.java'
    'backend/src/main/resources/application.properties'
    'backend/src/test/java/vn/hueqr/backend/BackendApplicationTests.java'
    'backend/src/test/java/vn/hueqr/backend/TestBackendApplication.java'
    'backend/src/test/java/vn/hueqr/backend/support/TestcontainersConfiguration.java'
)
```

### 4.2. Dữ liệu phiên, địa điểm và vùng — T02

```powershell
$guestDataFiles = @(
    'backend/src/main/resources/db/migration/V1__create_guest_session.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/GuestSession.java'
    'backend/src/main/java/vn/hueqr/backend/repository/GuestSessionRepository.java'
    'backend/src/test/java/vn/hueqr/backend/repository/GuestSessionRepositoryTests.java'
    'backend/src/test/java/vn/hueqr/backend/schema/GuestSessionSchemaTests.java'
)
```

Nền địa điểm và vùng được bàn giao cùng nhóm T02. Bộ kiểm và giới hạn tại [phần nền địa điểm/vùng của data-model](../03-ky-thuat/data-model.md#model-place-region-result).

```powershell
$placeRegionDataFiles = @(
    'backend/src/main/resources/db/migration/V2__create_place_and_region_revision.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/Place.java'
    'backend/src/main/java/vn/hueqr/backend/entity/RegionRevision.java'
    'backend/src/main/java/vn/hueqr/backend/repository/PlaceRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/RegionRevisionRepository.java'
    'backend/src/test/java/vn/hueqr/backend/repository/PlaceRegionRepositoryTests.java'
    'backend/src/test/java/vn/hueqr/backend/schema/PlaceRegionSchemaTests.java'
)
```

```powershell
$adminAccountDataFiles = @(
    'backend/src/main/resources/db/migration/V3__create_admin_account.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/AdminAccount.java'
    'backend/src/main/java/vn/hueqr/backend/repository/AdminAccountRepository.java'
)
```

```powershell
$adminCommandDataFiles = @(
    'backend/src/main/resources/db/migration/V4__create_admin_command_window_and_receipt.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/AdminCommandWindow.java'
    'backend/src/main/java/vn/hueqr/backend/entity/OperationReceipt.java'
    'backend/src/main/java/vn/hueqr/backend/repository/AdminCommandWindowRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/OperationReceiptRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/AdminCommandSchemaTests.java'
)
```

### 4.3. Vé và API khách — T03

```powershell
$guestApiFiles = @(
    'backend/src/main/java/vn/hueqr/backend/config/GuestRuntimeConfiguration.java'
    'backend/src/main/java/vn/hueqr/backend/controller/GuestSessionController.java'
    'backend/src/main/java/vn/hueqr/backend/controller/GuestSessionErrorHandler.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestErrorResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SessionResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/StartRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/StartTicketResponse.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestInputPolicy.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestSessionException.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestSessionService.java'
    'backend/src/main/java/vn/hueqr/backend/service/StartTicketCodec.java'
    'backend/src/main/java/vn/hueqr/backend/service/StartTicketService.java'
    'backend/src/main/java/vn/hueqr/backend/service/StartTicketSigner.java'
    'backend/src/main/java/vn/hueqr/backend/service/StartTicketTimePolicy.java'
    'backend/src/test/java/vn/hueqr/backend/config/GuestRuntimeConfigurationTests.java'
    'backend/src/test/java/vn/hueqr/backend/controller/GuestSessionApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/controller/GuestSessionEndApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/GuestInputPolicyTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/GuestSessionEndTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/StartTicketCodecTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/StartTicketSignerTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/StartTicketTimePolicyTests.java'
    'backend/src/test/java/vn/hueqr/backend/support/GuestSessionTestClock.java'
)
```

### 4.4. Phiên, cửa sổ lệnh và tạo/đọc/sửa tên địa điểm — T05

```powershell
$adminAuthFiles = @(
    'backend/src/main/java/vn/hueqr/backend/config/AdminSecurityConfiguration.java'
    'backend/src/main/java/vn/hueqr/backend/config/AdminSecurityErrorHandler.java'
    'backend/src/main/java/vn/hueqr/backend/config/AdminSessionFilter.java'
    'backend/src/main/java/vn/hueqr/backend/controller/AdminAuthController.java'
    'backend/src/main/java/vn/hueqr/backend/controller/AdminCommandController.java'
    'backend/src/main/java/vn/hueqr/backend/controller/AdminPlaceController.java'
    'backend/src/main/java/vn/hueqr/backend/controller/AdminErrorHandler.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminActivityRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminCommandWindowResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminOperationLookupResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminOperationResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminPlaceResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/UpdatePlaceRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/CreatePlaceRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminCsrfResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminErrorResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminLoginRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminSessionResponse.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminAccountDetailsService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminAuthService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminCommandException.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminCommandService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminPlaceService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminSessionException.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminSessionService.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminAuthApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminAuthHttpTests.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminCommandApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminPlaceApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/AdminCommandTransactionTests.java'
)
```

V3/V4 và mapping thuộc T02 được tích hợp trước phần quản trị T05. Các nhóm có thể bàn giao nhiều commit/PR theo sản phẩm đã kiểm, không suy cả nhóm hoặc toàn issue đã hoàn thành. Các file nền trong mục 4.1–4.4 đã có commit theo handoff mục 2.14; phần vùng/văn bản/job/báo cáo/audio-cặp/áp dụng-bật-ẩn và các sửa đổi sau mốc này lấy từ mục 4.5–4.10, hiện chưa commit/push.

Backend/HELP.md đang được ignore; target, log chạy, dependency/image tải về, file IDE, secret thật/database/audio thực không thuộc nhóm commit này. PNG/SVG/JSON/GeoJSON trong nhóm tài liệu là tài sản có hồ sơ nguồn và giới hạn, không mặc định là ranh giới hoặc dữ liệu ứng dụng đã được duyệt.

### 4.5. Backend vùng A12–A14 — nguồn mới, commit đang tạm dừng

Đã kiểm 204 test toàn cây tại [API mục 11.4](../03-ky-thuat/api-transactions.md#admin-region-test-result), chưa có commit mới từ phần vùng. Chín file Java mới thuộc T05 của Thành:

```powershell
$regionApiFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/AdminRegionController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminRegionRevisionResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/ApplyRegionRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SaveRegionRequest.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminPlacePreconditions.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminRegionService.java'
    'backend/src/main/java/vn/hueqr/backend/service/RegionGeometryPolicy.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminRegionApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/RegionGeometryPolicyTests.java'
)
```

Ngoài danh sách mới, T05 sửa AdminErrorHandler và AdminPlaceService trong nhóm 4.4; T02 của Hòa sửa RegionRevisionRepository trong nhóm 4.2 để đọc revision thuộc đúng địa điểm. Các file này đã được kiểm kê trong 86 file cũ, không cộng trùng vào tổng 95. Không thay Entity, pom.xml, cấu hình, Wrapper, migration hoặc test cũ. Các cập nhật tài liệu đi cùng bàn giao theo G02 khi có yêu cầu nối lại, không tự tạo commit trong giai đoạn tạm dừng.

### 4.6. Văn bản nháp và tham chiếu hồ sơ A15–A17 — nguồn mới, chưa commit

Nhóm nguồn được kiểm cùng toàn cây [240 test](../03-ky-thuat/api-transactions.md#admin-content-test-result), tách theo G02 và bàn giao T02 trước T05. Bảng mốc 0–5 giữ phạm vi đã commit; các danh sách dưới chỉ chuẩn bị nhóm mới, không phải lệnh cần chạy trong thời gian tạm dừng Git.

T02 của Hòa bổ sung tám file dữ liệu và test schema:

```powershell
$contentDataFiles = @(
    'backend/src/main/resources/db/migration/V5__create_content_slot_and_text_revision.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/ContentSlot.java'
    'backend/src/main/java/vn/hueqr/backend/entity/TextRevision.java'
    'backend/src/main/java/vn/hueqr/backend/entity/TextReviewReference.java'
    'backend/src/main/java/vn/hueqr/backend/repository/ContentSlotRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/TextRevisionRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/TextReviewReferenceRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/ContentSchemaTests.java'
)
```

T05 của Thành bổ sung chín file nghiệp vụ/HTTP/DTO/test:

```powershell
$contentApiFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/AdminContentController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminContentResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminTextReviewReferenceResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SaveTextRevisionRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SaveTextReviewReferenceRequest.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminContentService.java'
    'backend/src/main/java/vn/hueqr/backend/service/ContentTextPolicy.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminContentApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/ContentTextPolicyTests.java'
)
```

Ngoài file mới, T02 cập nhật assertion của PlaceRegionSchemaTests để đối chiếu chính xác danh sách migration V1–V5; T05 bổ sung Controller mới vào AdminErrorHandler. Hai file đã có trong nhóm cũ, không cộng trùng. V1–V4, cấu hình, pom.xml, Wrapper và các nguồn cũ khác giữ nguyên. Kiểm kê tại mốc văn bản là 112 file backend: 12 T01, 29 T02, 23 T03, 48 T05. Commit nội dung và schema mới chỉ được chuẩn bị khi có yêu cầu nối lại và nhận đúng phụ thuộc; chưa đủ toàn T02/T05.


### 4.7. Hàng đợi TTS và marker — nền A18/A19, chưa commit

Phần tạo/theo dõi audio thuộc T07, nhận nền dữ liệu T02 và session/window/receipt T05 đã có; T05 nối lại trạng thái job vào A15. Không yêu cầu đóng toàn issue để bàn giao phần nền. Bộ kiểm [267 test](../03-ky-thuat/api-transactions.md#admin-tts-queue-test-result) là bằng chứng toàn cây đủ các phụ thuộc, không chứng minh nhánh commit riêng đã build/test.

T02 của Hòa bổ sung bốn file:

```powershell
$ttsDataFiles = @(
    'backend/src/main/resources/db/migration/V6__create_tts_job_queue.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/TtsJob.java'
    'backend/src/main/java/vn/hueqr/backend/repository/TtsJobRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/TtsJobSchemaTests.java'
)
```

T07 của Thành bổ sung sáu file:

```powershell
$ttsQueueFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/AdminTtsController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/CreateTtsRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/TtsJobResponse.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsJobService.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsDispatchService.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminTtsApiTests.java'
)
```

Trên nguồn đã có, T02 sửa TextRevision/TextRevisionRepository và hai test schema PlaceRegionSchemaTests/ContentSchemaTests; T05 sửa AdminContentResponse, AdminContentService và AdminErrorHandler để nhận trạng thái/lỗi TTS. Không cộng trùng bảy file này vào nhóm mới. Kiểm kê tại mốc hàng đợi: 122 file, gồm 12 T01, 33 T02, 23 T03, 48 T05 và 6 T07. Nhận đủ các phần nền và nối HTTP/A15 trước khi kiểm API TTS trong nhánh tích hợp. Worker tự chạy/provider/file/audio/check-retry chưa có; nhóm này chưa hoàn thành T07 hoặc toàn ô checklist. Quy trình commit/PR tiếp tục tạm dừng.


### 4.8. Worker WAV và đọc phiên/thống kê — nguồn mới, chưa commit

Theo [bằng chứng 318 test](../03-ky-thuat/api-transactions.md#worker-report-test-result), nhóm này thêm 33 file và sửa 10 file đã có, không coi kiểm toàn cây là đã kiểm nhánh/commit riêng. Schema/mapping vẫn nhận T02 trước nghiệp vụ T05/T07. Không chuyển file giữa người commit chỉ để làm đầy một nhánh.

T02/Hòa bổ sung nền V7/V8:

```powershell
$workerReportDataFiles = @(
    'backend/src/main/resources/db/migration/V7__stage_tts_wav_result.sql'
    'backend/src/main/resources/db/migration/V8__create_place_view_history.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/FileAsset.java'
    'backend/src/main/java/vn/hueqr/backend/entity/PlaceView.java'
    'backend/src/main/java/vn/hueqr/backend/repository/FileAssetRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/PlaceViewRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/TtsWavSchemaTests.java'
    'backend/src/test/java/vn/hueqr/backend/schema/PlaceViewSchemaTests.java'
)
```

T07/Thành bổ sung worker/provider/WAV/recovery:

```powershell
$ttsWorkerFiles = @(
    'backend/src/main/java/vn/hueqr/backend/config/TtsWorkerConfiguration.java'
    'backend/src/main/java/vn/hueqr/backend/dto/RetryTtsRequest.java'
    'backend/src/main/java/vn/hueqr/backend/service/GeminiTtsProvider.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsProvider.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsQueueRunner.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsRecoveryService.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsResultService.java'
    'backend/src/main/java/vn/hueqr/backend/service/TtsWorker.java'
    'backend/src/main/java/vn/hueqr/backend/service/WavPolicy.java'
    'backend/src/main/java/vn/hueqr/backend/service/WavResultStore.java'
    'backend/src/test/java/vn/hueqr/backend/controller/TtsWorkerIntegrationTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/GeminiTtsProviderTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/WavResultStoreTests.java'
    'backend/src/test/java/vn/hueqr/backend/support/WavFixture.java'
)
```

T05/Thành bổ sung API đọc Admin:

```powershell
$adminReportFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/AdminGuestSessionController.java'
    'backend/src/main/java/vn/hueqr/backend/controller/AdminStatisticsController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminGuestSessionListResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminGuestSessionResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminPlaceViewResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminStatisticsResponse.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminDateRange.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminGuestSessionService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminStatisticsService.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminGuestSessionApiTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/AdminDateRangeTests.java'
)
```

Nguồn cũ sửa theo task: T02 gồm TextRevision, TtsJob, GuestSessionRepository, TtsJobRepository, PlaceRegionSchemaTests và TtsJobSchemaTests; T07 gồm AdminTtsController, TtsJobResponse và TtsJobService; T05 gồm AdminErrorHandler. Không cộng trùng các file này. Kiểm kê sau nhóm: 155 file backend, 12 T01/41 T02/23 T03/59 T05/20 T07. Tổng hợp này phân nhóm sản phẩm, chưa khẳng định commit/push/merge mới; phần Git vẫn tạm hoãn.


### 4.9. Nền dữ liệu audio/cặp và seed riêng — T02, chưa commit

Hòa phụ trách theo G02. [Bằng chứng 336 test](../03-ky-thuat/data-model.md#audio-pair-data-test-result) thuộc toàn cây có đủ phụ thuộc, không suy nhánh/commit riêng đã build đạt. Nhóm này thêm 12 file:

```powershell
$audioPairDataFiles = @(
    'backend/src/main/resources/db/migration/V9__create_audio_revision_and_content_pair.sql'
    'backend/src/main/resources/db/seed/five-places.sql'
    'backend/src/main/java/vn/hueqr/backend/entity/AudioRevision.java'
    'backend/src/main/java/vn/hueqr/backend/entity/AudioReviewReference.java'
    'backend/src/main/java/vn/hueqr/backend/entity/ContentPair.java'
    'backend/src/main/java/vn/hueqr/backend/repository/AudioRevisionRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/AudioReviewReferenceRepository.java'
    'backend/src/main/java/vn/hueqr/backend/repository/ContentPairRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/AudioPairSchemaTests.java'
    'backend/src/test/java/vn/hueqr/backend/schema/InitialPlaceDataTests.java'
    'backend/src/test/java/vn/hueqr/backend/repository/AudioPairRepositoryTests.java'
    'backend/src/test/java/vn/hueqr/backend/support/AudioDataFixture.java'
)
```

File cũ sửa cùng T02: ContentSlot, FileAsset, PlaceRegionSchemaTests và TtsWavSchemaTests. V1–V8 giữ nguyên; seed nằm ngoài đường migration tự động, không chạy chỉ vì commit hoặc khởi động ứng dụng. Tổng nguồn sau nhóm: 167 file, 12 T01/53 T02/23 T03/59 T05/20 T07. Chưa tạo audio MP3 thật, API áp dụng/bật hiện hoặc hoàn tất toàn T02. Nhận nền này trước nghiệp vụ cặp T05 và đăng ký kết quả T07; không tự stage/commit/push khi quy trình vẫn tạm dừng.


### 4.10. Hồ sơ audio, áp dụng cặp, bật-ẩn và mức đầy đủ — T05, chưa commit

Thành phụ trách HTTP/nghiệp vụ/DTO/test API theo G02. [Bằng chứng 366 test](../03-ky-thuat/api-transactions.md#admin-pair-visibility-test-result) thuộc toàn cây đủ phụ thuộc; không thay bằng chứng nhánh/commit riêng. Nhóm này thêm 11 Java:

```powershell
$adminPairFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/AdminPairController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminAppliedPairResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminAudioReviewReferenceResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/AdminContentCompletenessResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/ApplyPairRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SaveAudioReviewReferenceRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/SetPlaceVisibilityRequest.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminPairService.java'
    'backend/src/main/java/vn/hueqr/backend/service/AdminVisibilityService.java'
    'backend/src/main/java/vn/hueqr/backend/service/ContentPairReadiness.java'
    'backend/src/test/java/vn/hueqr/backend/controller/AdminPairApiTests.java'
)
```

17 nguồn đã có được sửa, không cộng trùng vào nhóm mới: T02 gồm Place, FileAsset, AudioRevisionRepository, ContentPairRepository, ContentSlotRepository, FileAssetRepository; T05 gồm AdminContentController/AdminPlaceController/AdminErrorHandler, AdminContentResponse/AdminPlaceResponse/AdminErrorResponse/AdminOperationResponse, AdminCommandException/AdminContentService/AdminPlaceService/ContentTextPolicy. File DTO lỗi/receipt dùng chung nhận thêm trường hoặc hỗ trợ previousPairId null, giữ nền hợp đồng Admin hiện có.

Tổng nguồn sau nhóm 178 file: 12 T01/53 T02/23 T03/70 T05/20 T07. V1–V9/seed/cấu hình/Wrapper/pom.xml giữ nguyên. T05 cần nền audio/cặp và Repository T02, file thực sẵn sàng từ T07 khi kiểm tích hợp thật; fixture không chứng minh codec hoặc nghe duyệt. Commit/PR tiếp tục tạm hoãn, không tự stage/commit/push hoặc chuyển phân công để làm đầy nhánh.


<a id="guest-content-commit-group"></a>

### 4.11. Nội dung khách, grant/ACK và nguồn Vue khách — chưa commit

Theo G02, T02/Hòa bàn giao nền schema/Repository cho T03/Thành, rồi T04/Nghĩa nối giao diện. T03 nhận nền cặp được phép từ T05; không bắt đóng cả issue phụ thuộc. Nhóm tích hợp qua PR vào repository chung trên ba máy riêng, giữ nhánh Txx và thông điệp tiếng Việt. Đã được giao tiếp tục commit/push phần Thành/Hòa sau mốc kiểm; dừng trước T04/T06 để đăng nhập. Danh sách dưới phân nhóm sản phẩm, không tự xác nhận thành viên đã thực hiện hoặc toàn issue đã hoàn thành.

Thêm 3 file T02:

```powershell
$guestContentDataFiles = @(
    'backend/src/main/resources/db/migration/V10__create_guest_opening_grant_and_file_claim.sql'
    'backend/src/main/java/vn/hueqr/backend/repository/GuestContentRepository.java'
    'backend/src/test/java/vn/hueqr/backend/schema/GuestContentSchemaTests.java'
)
```

Thêm 13 file T03:

```powershell
$guestContentApiFiles = @(
    'backend/src/main/java/vn/hueqr/backend/controller/GuestContentController.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestContentResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestLanguageRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestLeaveRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestMapResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestResumeRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestResumeResponse.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestSelectionRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestViewAckRequest.java'
    'backend/src/main/java/vn/hueqr/backend/dto/GuestViewAckResponse.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestAudioService.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestContentService.java'
    'backend/src/test/java/vn/hueqr/backend/controller/GuestContentApiTests.java'
)
```

Sáu nguồn đã có được cập nhật, giữ nhóm cũ và không cộng trùng: GuestSession, GuestSessionRepository, PlaceRepository, PlaceRegionSchemaTests thuộc T02; GuestSessionService và GuestSessionErrorHandler thuộc T03. Tổng backend 194 nguồn: 12 T01/56 T02/36 T03/70 T05/20 T07, mỗi đường dẫn duy nhất một nhóm. [Bằng chứng backend](../03-ky-thuat/api-transactions.md#guest-content-test-result) thuộc toàn cây đủ phụ thuộc, không suy build từng nhánh riêng.

Nguồn frontend T04/Nghĩa có 14 file, chưa dependency lockfile/build:

```powershell
$guestFrontendFiles = @(
    'frontend/.gitignore'
    'frontend/README.md'
    'frontend/package.json'
    'frontend/index.html'
    'frontend/vite.config.js'
    'frontend/src/main.js'
    'frontend/src/App.vue'
    'frontend/src/style.css'
    'frontend/src/components/GuestMap.vue'
    'frontend/src/guest/api.js'
    'frontend/src/guest/messages.js'
    'frontend/src/guest/owner.js'
    'frontend/src/guest/playback.js'
    'frontend/tests/playback.test.mjs'
)
```

Thông điệp dự kiến theo đúng sản phẩm: `T02: Bổ sung dữ liệu lần mở, quyền nội dung và claim đọc`; `T03: Cung cấp nội dung khách, ghi lượt và tải audio theo quyền phiên`; `T04: Bổ sung nguồn giao diện khách và quản lý audio trong tab`. Không mô tả build/browser/codec/nghiệm thu đã đạt trước khi kiểm. Khi nối lại Git cần đối chiếu trạng thái, tài khoản, staged diff và nền phụ thuộc; chưa đủ thông tin đăng nhập không tự đổi metadata để ghi nhận người khác.

<a id="commit-push-result-20261109"></a>

### 4.12. Kết quả commit/push ngày 09/11/2026

Phạm vi chốt: 194 file backend theo danh sách mục 4 và 11 tài liệu thay đổi; nguồn mới phát sinh sau mốc chốt để lượt sau. Commit sản phẩm có thay đổi thực tế: T01 11 tài liệu, T02 41 file, T05 47 file, T07 20 file, T03 15 file. Các file đã có và không đổi không tạo commit lặp. Metadata Thành dùng `KhaiThanh <khaithanh2726@gmail.com>`; Hòa dùng `gingpham <gingpham13052006@gmail.com>`, theo thông tin đã xác nhận. Tài khoản xác thực push được chọn riêng theo G02.

| Nhánh | Commit sản phẩm | Đầu nhánh đã push và đối chiếu | Tài khoản xác thực |
| --- | --- | --- | --- |
| T01 | `f8f1c9a` | `f8f1c9a494557a10c02d7c9252de1e3a6d02be81` | `khaithanh2726-beep` |
| T02 | `b6d39fc` | `b6d39fc407740fd85bec7cad32bf5c421b2b4e81` | `gingpham` |
| T05 | `462d94b` | `394f69db2ac08fc134c18d50409f99a3b3586ddd` | `khaithanh2726-beep` |
| T07 | `a676989` | `a67698944994859c0915c6c1363b772ec3fab831` | `khaithanh2726-beep` |
| T03 | `671c3b5` | `671c3b53a61cff24ee778af7b8ea603d44fb8c44` | `khaithanh2726-beep` |

Bảng ghi mốc đối chiếu, không mặc định là HEAD ở lần đọc sau; các commit tài liệu tiếp theo không đổi mã commit sản phẩm. T02 nhận `origin/main` `6042508` rồi T01; T03 nhận T02, T05 nhận nền T03, T07 nhận nền T05, T05 nhận T07 và T03 nhận T05 trước commit nội dung khách. Giữ lịch sử cũ bằng các commit nhận phụ thuộc, không force-push hoặc gán file khác task vào commit sản phẩm. Đã đối chiếu mã Git của cả 194 file backend ở T03 với nguồn đã kiểm: khớp toàn bộ, không có file backend ngoài danh sách. Từng nhánh trung gian chưa được xác nhận build độc lập. Khi mở PR phải xem diff với base thực tế và sắp thứ tự nhận phụ thuộc, không suy từ nhãn task rằng PR chỉ chứa commit riêng của task đó.

Các worktree riêng phục vụ Git, không chạy ứng dụng hoặc kiểm thử trên database đang dùng. Thư mục IDE giữ nhánh T01 và nguyên trạng nguồn của lượt triển khai tiếp theo; không chuyển nhánh để làm mất file đang sửa. 14 file frontend T04 chưa commit/push; T06 chưa thực hiện. Chưa merge PR mới vào main hoặc thay issue/Project. Điểm dừng và giới hạn kiểm chứng tại [handoff mục 2.25](handoff.md#commit-push-20261109).

<a id="guest-retention-commit-group"></a>

### 4.13. Dọn metadata và phiên/lượt D08 — T02/T05

[Bằng chứng và giới hạn](../03-ky-thuat/operations-design.md#ops-guest-retention-result) thuộc cây backend đầy đủ: 200 file/182 Java, sáu Java mới; nguồn cũ, cấu hình và V1–V10 giữ nguyên. Không thêm file vào task khác để làm đầy nhánh, không coi scheduler mặc định chưa bật là retention vận hành đã nghiệm thu.

T02/Hòa bổ sung Repository:

```powershell
$guestRetentionDataFiles = @(
    'backend/src/main/java/vn/hueqr/backend/repository/GuestRetentionRepository.java'
)
```

T05/Thành bổ sung quy tắc, giao dịch, runner và kiểm:

```powershell
$guestRetentionServiceFiles = @(
    'backend/src/main/java/vn/hueqr/backend/service/GuestRetentionPolicy.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestRetentionService.java'
    'backend/src/main/java/vn/hueqr/backend/service/GuestRetentionRunner.java'
    'backend/src/test/java/vn/hueqr/backend/service/GuestRetentionPolicyTests.java'
    'backend/src/test/java/vn/hueqr/backend/service/GuestRetentionTests.java'
)
```

T02 nhận nền dữ liệu V10 hiện có rồi bàn giao Repository; T05 nhận T02 và cây API khách/cặp/TTS đã kiểm trước khi nhận Service/test. Tổng kiểm kê mới theo nguồn chính: 12 T01/57 T02/36 T03/75 T05/20 T07 =200 file backend. 14 file frontend T04 không thuộc nhóm này; T06 có bản chuẩn bị riêng chưa nhận vào cây bàn giao. Các số ở mục 4.12 giữ đúng mốc trước.

Kết quả đã đối chiếu commit/push:

| Task | Commit sản phẩm | Số file thay đổi | Tài khoản xác thực |
| --- | --- | --- | --- |
| T02 | `695480b9d4556362b063b84808c2ccff2eb924d4` — Bổ sung truy vấn dọn metadata và dữ liệu phiên khách | 1 | `gingpham` |
| T05 | `23813ca0605b90f0dab0f4406113a8a532eede5d` — Dọn phiên và lượt xem theo hạn lưu, bảo vệ lượt tải đang xử lý | 5 | `khaithanh2726-beep` |

SHA local/remote khớp tại mốc push. T05 nhận cây đầy đủ từ T03 và Repository T02; đã đối chiếu 200 file backend với bản hồi quy 421 test. Các commit nhận tài liệu có thể làm HEAD đổi, bảng ghi commit sản phẩm. Tài liệu lưu theo T01 và bàn giao vào T05; chưa merge PR vào main hoặc đổi issue/Project. Nguồn cũ và phần Nghĩa giữ nguyên.

<a id="lifecycle-cleanup-commit-group"></a>

### 4.14. Xóa địa điểm và dọn vòng đời — T02/T05/T03/T07

Nguồn và kiểm chứng tại [operations 6.3](../03-ky-thuat/operations-design.md#ops-lifecycle-cleanup-result). Đây là **phân nhóm chuẩn bị, chưa stage/commit/push trong lượt vòng đời**; nhóm đang rà kết quả. Khi tiếp tục, phối hợp trên nhánh task theo G02, giữ lịch sử bằng merge phụ thuộc; PR tích hợp phải xem diff với base thực tế, không suy sản phẩm đã vào main từ việc push.

| Task / phụ trách | Nhóm file thay đổi |
| --- | --- |
| T02 / Hòa | V11, PlaceDeletionRepository, LifecycleRepository, GuestContentRepository, FileAsset và kiểm inventory schema PlaceRegionSchemaTests |
| T05 / Thành | A11 trong AdminPlaceController/AdminPlaceDeletionService; RuntimeOwnerService, MetadataCleanupService, CleanupFileStore, FileCleanupService, WorkspaceCleanupService, LifecycleCleanupRunner; AdminPlaceDeletionTests và LifecycleCleanupTests |
| T03 / Thành | GuestAudioService giữ scope tới khi stream thực đóng; GuestContentService bảo vệ metadata tham chiếu file |
| T07 / Thành | TtsWorkerConfiguration, TtsWorker, WavResultStore, TtsRecoveryService, TtsResultService và TtsWorkerIntegrationTests |
| T01 / Thành | Đồng bộ thiết kế, phạm vi kiểm, bằng chứng và điểm bàn giao trong tài liệu |

Thứ tự bàn giao dự kiến sau rà soát: T02 → T05 nền xóa/dọn → T03 luồng khách → T07 worker/Check/workspace → T05 bổ sung bộ kiểm tích hợp đầy đủ → T01 tài liệu. LifecycleCleanupTests kiểm xuyên các phần nên chỉ nhận sau T07; các nhánh trung gian không mặc định đã build độc lập. Cây cuối phải khớp 212 file/193 Java đã qua 459 test. Không đưa frontend T04/T06 vào nhóm này; giữ điểm dừng đăng nhập Nghĩa.

<a id="admin-preview-commit-group"></a>

### 4.15. A22 nghe thử Admin — nguồn đã kiểm, chờ rà trước commit

[Phạm vi và bằng chứng](../03-ky-thuat/api-transactions.md#admin-preview-prepared-result): 111 ca liên quan đạt trên bản sao/MySQL tạm, đã đối chiếu và nhận nguồn vào backend chính theo handoff 2.28. Chưa stage/commit/push; không coi kết quả truyền tải fixture là đã nghe duyệt MP3 thực.

| Task / phụ trách | File bàn giao |
| --- | --- |
| T02 / Hòa | FileReadClaimRepository.java — READ claim dùng cho bản audio nghe thử, cùng registry V11 |
| T05 / Thành | AdminAudioPreviewService.java, AdminPairController.java, AdminAudioPreviewTests.java, AdminAudioPreviewStreamTests.java |
| T01 / Thành | Hợp đồng, trạng thái kiểm và điểm bàn giao trong tài liệu |

Phụ thuộc nền V11/claim tại mục 4.14. Sau khi nhóm rà và tiếp tục Git: tích hợp nền T02 → Repository claim T02 → API/bộ kiểm T05 → tài liệu T01, kiểm nhánh nhận đủ nền trước mỗi bàn giao. Giữ G02 và đăng nhập tương ứng; phần này không thuộc T07 hoặc Nghĩa chỉ vì có audio. Các mục chờ công cụ/frontend/MP3 thực tiếp tục được theo dõi riêng, không đóng issue theo commit.

<a id="lifecycle-preview-local-commits"></a>

### 4.16. Bàn giao commit cục bộ vòng đời và A22 — 10/11/2026

Đã commit 29 file backend mới/sửa theo đúng nhóm mục 4.14–4.15, gồm 7 T02, 2 T03, 14 T05 và 6 T07. Các commit nhận phụ thuộc giữ lịch sử giữa các task, không tạo lại sản phẩm cũ. T02 dùng metadata `gingpham <gingpham13052006@gmail.com>`; T03/T05/T07 dùng `KhaiThanh <khaithanh2726@gmail.com>`. Metadata không chứng minh tài khoản xác thực GitHub hoặc ai đã viết mã.

| Task | Commit sản phẩm cục bộ | File thay đổi | Nội dung |
| --- | --- | --- | --- |
| T02 | `38f095be8b76d5a4909a172f832e600e167c698c` | 7 | Bổ sung dữ liệu vòng đời, owner và claim đọc audio |
| T05 | `a2ccc26c068b2175aacddae3914317cceaddc39f` | 9 | Xóa địa điểm an toàn và dọn metadata, claim, file theo hạn lưu |
| T03 | `52ddc6bae407b56b8983ef1f11877c3495de6bf9` | 2 | Giữ scope tải audio và bảo vệ tham chiếu nội dung khách |
| T07 | `8efdc2b0ae5a290d783fbddfbdec0f848d02e411` | 6 | Theo dõi owner của worker, WAV và phục hồi tác vụ |
| T05 | `0b99500787bb30c80568b881e4ae1e122faa31b4` | 5 | Bổ sung nghe thử audio Admin và kiểm thử tích hợp vòng đời |

T02 nhận nền T05 đã kiểm trước đó; sau đó bàn giao T02 → T05 nền xóa/dọn → T03 → T07 → T05 nghe thử và bộ kiểm vòng đời. Đầu nhánh backend đầy đủ ở T05 khớp cả 216 file/197 Java của bản kiểm A22, đối chiếu bằng Git blob sau chuẩn hóa line ending. 459 ca toàn cây là mốc trước A22; 111 ca liên quan là mốc A22, không cộng thành số ca độc lập hoặc coi đã chạy lại toàn bộ cây mới. Không sửa mã nguồn hoặc chạy lại build/test trong lượt commit.

Tám tài liệu thay đổi được commit theo T01 với thông điệp `T01: Ghi nhận thiết kế, kiểm chứng và bàn giao vòng đời, nghe thử audio`. Thư mục IDE giữ T01, nguồn backend/frontend được bảo vệ bằng mã Hash; các file hiện untracked tại T01 vẫn có thể đã được lưu trên nhánh task. Không stage lại toàn thư mục vì thấy U. T04/T06/Nghĩa giữ nguyên điểm dừng đăng nhập.

**Điểm dừng:** mới commit cục bộ, chưa push các commit trong bảng, chưa merge PR hoặc thay main/issue/Project. Khi tiếp tục push cần chọn tài khoản theo G02, đối chiếu SHA local/remote và diff với base thực tế. Checklist giữ nguyên 89 ô/20 dấu tích; việc commit không xác nhận hoàn tất issue. Bằng chứng thao tác, bản chốt và đối chiếu ở `C:\Users\khait\AppData\Local\Temp\hueqr-commit-lifecycle-4zc8n4oh`.

<a id="lifecycle-preview-push-result"></a>

### 4.17. Kết quả push vòng đời và A22 — 10/11/2026

Đã đối chiếu SHA local/remote sau push thường, không force-push:

| Nhánh | Đầu nhánh tại mốc push sản phẩm | Tài khoản được chọn khi push |
| --- | --- | --- |
| T01 | `a1d1743e15d9098a55a1267267b25eee5f1c00a3` | khaithanh2726-beep |
| T02 | `38f095be8b76d5a4909a172f832e600e167c698c` | gingpham |
| T03 | `52ddc6bae407b56b8983ef1f11877c3495de6bf9` | khaithanh2726-beep |
| T05 | `e1b9b341ffc66a1d9c9c989f03bdc068e3341ed8` | khaithanh2726-beep |
| T07 | `8efdc2b0ae5a290d783fbddfbdec0f848d02e411` | khaithanh2726-beep |

T05 có commit nhận tài liệu `e1b9b34`; các commit sản phẩm được giữ riêng ở mục 4.16. Không merge PR hoặc thay main. Commit T01 bổ sung kết quả push/phạm vi phối hợp có thể làm đầu nhánh đổi sau bảng này. Cây nguồn/giới hạn kiểm chứng không thay từ lượt commit; không phát sinh build/test mới do push.

[Handoff 2.30](handoff.md#parallel-backend-frontend) là nguồn chính về quyền sở hữu file và điểm bàn giao khi làm backend/frontend song song. Frontend có bản sao riêng, không commit/push phần Nghĩa hoặc ghép vào cây chính trước rà soát. Checklist và trạng thái issue/Project giữ nguyên.

## 5. Dấu vết thay đổi kế hoạch



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-commit-plan-2026-11-05-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
