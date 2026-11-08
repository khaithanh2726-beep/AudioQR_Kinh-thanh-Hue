# Kế hoạch commit — nền tài liệu, dữ liệu và API

**Cập nhật:** 09/11/2026. Kế hoạch tổ chức bàn giao cho Thành, Hòa và Nghĩa trên ba máy riêng, tích hợp qua repository chung. Bảng phân công là trách nhiệm dự kiến; trạng thái hoàn thành phải căn cứ sản phẩm và bằng chứng. Quy trình Git chung tại [handoff 7.2](handoff.md#issue-git-handoff).

## 1. Điều kiện và trạng thái đầu vào

- Repository: [AudioQR_Kinh-thanh-Hue](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue); Project 21 và mapping #1–#11 tại [roadmap mục 6](roadmap.md#github-checklist-review).
- Ba commit/push T10 và T01 ngày 07–08/11 trước lần tạo lại repository đã được đối chiếu qua đầu ra Terminal. SHA, nhánh, metadata và giới hạn tại [handoff mục 2.2](handoff.md#bootstrap-result). Chưa có bằng chứng các SHA này trên repository vừa tạo lại hoặc PR/merge mới.
- Nền T10 tại repository hiện tại đã commit/push `d61a8698cae3652dec01185a282f88d536b2169c`, SHA local/remote khớp theo [handoff mục 2.11](handoff.md#current-t10-commit-result). Tài liệu T01 đã commit/push `65807af9884b6f959808368ab39974ccc1ffb23d`, SHA local/remote khớp theo [handoff mục 2.13](handoff.md#current-t01-docs-result). Nhóm tiếp theo là khung backend T01 và hai tài liệu cập nhật bàn giao.
- Nhóm tài liệu gồm 33 file tính cả README gốc, nguồn Draw.io và tài sản; commit-plan đã được khôi phục sau khi bị xóa nhầm. Các nhóm backend theo mục 2 và 4. Đích chính thức tại [handoff mục 2.4](handoff.md#new-target-pending), trạng thái Git mới nhất theo mục 2.11 và các mốc tiếp theo.
- Mỗi nhóm commit/push dùng tài khoản GitHub theo G02. Metadata author/committer, người nhập lệnh và tài khoản xác thực là các thông tin riêng; cấu hình user.name/user.email không chọn tài khoản đăng nhập GitHub. Metadata cần có căn cứ, không tự suy email từ username.
- Nhánh task là T01–T11; thông điệp tiếng Việt dạng `Txx: Những việc đã thực hiện`. Phân công duy nhất tại [G02](../02-nghiep-vu/decisions.md#g02).
- Không stage toàn repository, tạo thông tin tác giả không có căn cứ, reset/clean hoặc ghi đè sửa đổi để hợp thức hóa kết quả. Bản ghi kiểm tra phải phân biệt đã code/test, đã commit, đã push và đã merge.

<a id="commit-groups"></a>

## 2. Phân nhóm sản phẩm và phụ thuộc

Bảng dưới là phương án bàn giao sản phẩm hiện có trên đích chính thức đã xác nhận. Kết quả đã commit/push trước lần tạo lại repository được lưu riêng trong handoff mục 2.2. Lần tiếp tục này chọn bước dựa trên trạng thái Git mới, không mặc định khởi tạo lại mốc 0. Các nhóm T04–T11 ngoài phần nền T10 chỉ được lập danh sách commit khi có sản phẩm tương ứng; phân công và phụ thuộc task tiếp tục theo G02/roadmap.

| Mốc | Nhánh | Nội dung / số file theo kiểm kê | Người phụ trách | Người commit | Thông điệp đề xuất |
| --- | --- | --- | --- | --- | --- |
| 0 | main — khởi tạo | Chỉ `.gitignore` gốc hiện có — 1 | Hòa (T10) | Hòa | `T10: Khởi tạo repository với cấu hình bỏ qua file Java` |
| 1 | T01 | Tài liệu, thiết kế, nguồn hình và bàn giao — 33 | Thành | Thành | `T01: Bổ sung tài liệu nghiệp vụ, thiết kế và hướng dẫn bàn giao` |
| 2 | T01 | Khung backend, Maven Wrapper, chính sách tên dùng chung và hỗ trợ test — 12 | Thành | Thành | `T01: Bổ sung khung backend, chính sách tên và cấu hình hỗ trợ kiểm thử` |
| 3 | T02 | V1–V4, Entity/Repository và test dữ liệu — 21 | Hòa | Hòa | `T02: Tạo dữ liệu phiên khách, địa điểm và nền quản trị` |
| 4 | T03 | Vé ký, input, runtime config, K11/K01/K02/K04 và test — 23 | Thành | Thành | `T03: Xây dựng API cấp vé, bắt đầu, kiểm tra và kết thúc phiên khách` |
| 5 | T05 | API xác thực/Q3, cửa sổ/receipt, tạo/đọc/sửa tên điểm và test — 30 | Thành | Thành | `T05: Xây dựng phiên quản trị và API tạo, đọc, sửa tên địa điểm` |

Nhóm tài liệu hiện gồm 33 file tính cả README gốc, nguồn Draw.io và tài sản. Nhóm đã commit ở d00bb03 có 31 file tại thời điểm đó. Backend hiện có 86 file, gồm 12 file nền dùng chung T01, 21 file dữ liệu T02, 23 file vé/API khách T03 và 30 file quản trị T05. Schema/Entity/Repository tài khoản và window/receipt, cùng test schema thuần dữ liệu thuộc T02; các lớp điều phối nghiệp vụ/HTTP và test giao dịch API Admin thuộc T05. PlaceRepository bổ sung khóa hàng và so sánh trùng tên loại trừ pid, dùng khi T05 sửa tên với If-Match. Danh sách ở mục 4, kiểm lại nội dung và phụ thuộc tại mỗi mốc.

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

Mỗi máy có clone/thư mục làm việc và cấu hình tài khoản theo phần việc G02. Tài khoản dùng để xác thực push được chọn riêng với metadata commit. Các máy trao đổi commit qua origin; người nhận chỉ nhận được nội dung đã commit/push. File untracked không được truyền qua clone/fetch/pull. Bộ nguồn được phân theo danh sách task trước khi bàn giao cho từng máy. Trạng thái hiện tại theo handoff mục 2.11; các commit trước lần tạo lại repository giữ riêng ở mục 2.2.

Sau PR được rà soát và merge, thành viên nhận task phụ thuộc cập nhật nền main. Nếu nhánh có sửa đổi riêng, phải kiểm trạng thái/diff và chọn cách tích hợp phù hợp; không chạy khối lệnh cập nhật nền khi chưa rõ trạng thái hoặc tự reset để bỏ sửa đổi.

<a id="current-commit-workflow"></a>

## 3. Điểm dừng và quy trình tiếp tục commit

Đích repository và GitHub Project chính thức đã được xác nhận theo handoff mục 2.4. Đã tạo và đọc lại issue #1–#11, người vận hành xác nhận đã thêm đủ vào Project và đặt Status. Tài liệu T01 đã push, trạng thái cuối T01...origin/T01 và backend/ còn untracked theo mục 2.13. Tiếp theo commit phần hiện có của Thành: nền T01 → T03 → T05, rồi chuyển sang Hòa làm T02; thứ tự tích hợp vẫn theo mục 2. Hai tài liệu commit-plan/handoff cập nhật trạng thái và thứ tự bàn giao được stage cùng nhóm nền T01, dự kiến 12 file backend mới và hai tài liệu sửa. Chỉ chuyển tài khoản/nhánh khi tới nhóm tương ứng; không suy các bước dự kiến đã thực hiện.

### 3.1. Điều kiện và chuẩn bị

Sử dụng hai URL chính thức đã nhận, đối chiếu repository/Project và các issue đã có. Giữ mapping hiện có cho đến khi URL mới được đối chiếu. Khi được giao tiếp tục Git, sử dụng bằng chứng nhánh/HEAD/index/remote còn hiệu lực do người vận hành cung cấp để chọn bước phù hợp. Tài khoản và metadata theo G02; email cần có căn cứ cho từng thành viên.

### 3.2. Stage và xem nội dung

Hướng dẫn trọn một nhóm với danh sách file, mục đích, lệnh xem staged diff, kết quả mong đợi và điều kiện dừng. Mốc A đối chiếu task, tài khoản, metadata và phạm vi/nội dung staged trước commit. Nhóm tài liệu hiện có 33 file, các nhóm backend lần lượt 12/21/23/30 file. Comment được đưa cùng tệp thuộc nhóm tương ứng.

### 3.3. Commit, push, PR và tích hợp

Chỉ hướng dẫn bước phụ thuộc khi mốc trước đủ căn cứ, giữ thông điệp Txx tiếng Việt theo bảng nhóm. Mốc B đối chiếu commit, SHA local/remote, trạng thái nhánh và liên kết tài khoản trên GitHub. PR nhận đúng nền phụ thuộc, được rà soát trước tích hợp; cách merge dự kiến là Create a merge commit. Các thành viên nhận nền đã tích hợp qua repository chung trước task phụ thuộc.

Khi kết quả khác kỳ vọng hoặc có lỗi, dừng trước bước phụ thuộc và gửi nguyên kết quả. Người vận hành gửi đầu ra tại các mốc đối chiếu, giữ quyền trực tiếp thực hiện mọi lệnh Git và thao tác GitHub trừ hành động cụ thể được giao thêm.

**Đợt được giao ngày 09/11/2026:** công cụ hỗ trợ trực tiếp kiểm/stage/commit các nhóm hiện có theo thứ tự Thành trước, Hòa sau và tự đối chiếu mốc A/B. Phạm vi, cách bảo vệ nguồn bằng worktree tạm và kết quả được lưu tại [handoff mục 2.14](handoff.md#local-commit-batch-20261109). Chỉ commit cục bộ, chưa push/PR/merge; đây là quyền cho đợt hiện tại, không thay quyền Git/GitHub chung cho các đợt sau.

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

V3/V4 và mapping thuộc T02 được tích hợp trước phần quản trị T05. Thay đổi dependency/cookie trong file dùng chung được bàn giao cùng phạm vi T05 theo diff thực tế. Các nhóm có thể bàn giao nhiều commit/PR theo sản phẩm đã kiểm, không suy cả nhóm hoặc toàn issue đã hoàn thành. Danh sách là kế hoạch, chưa có kết quả commit mới từ yêu cầu tiếp tục tại handoff mục 2.10.

Backend/HELP.md đang được ignore; target, log chạy, dependency/image tải về, file IDE, secret thật/database/audio thực không thuộc nhóm commit này. PNG/SVG/JSON/GeoJSON trong nhóm tài liệu là tài sản có hồ sơ nguồn và giới hạn, không mặc định là ranh giới hoặc dữ liệu ứng dụng đã được duyệt.

## 5. Dấu vết thay đổi kế hoạch



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](../99-luu-tru/document-history.md#official-20261107-01-tong-quan-commit-plan-2026-11-05-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
