# Mục lục tài liệu dự án

**Chuẩn bị Git:** [dọn thư mục ngày 12/11](06-kiem-thu/workspace-cleanup-20261112/README.md) đã chuyển kết quả test/dist ra ngoài repo và gom bằng chứng trùng có giữ Hash/đường dẫn gốc. 456 mục Source Control trên T01 không phải 456 file mới; 208 file backend khớp các nhánh task local. Giữ mã ứng dụng/kiểm thử; chưa stage/commit/push.

**Preview mới nhất:** [ghim đúng vị trí nhấn và thu gọn trang](06-kiem-thu/map-pin-20261111/README.md) đã lên cổng 18082; 65 ca logic/build và 14 ca Chrome trên ảnh cuối đạt. Chỉ một ghim, không GPS; tile Chrome là ảnh kiểm, chưa xác nhận kết nối nền thật. Các mốc runtime bên dưới giữ đúng lịch sử.

**UI/runtime hiện tại:** [nền raster OSM theo mẫu](04-giao-dien/ui-design.md#ui-reference-red) và UI đỏ sẫm đã vào Docker 18082; [hồ sơ mới](06-kiem-thu/osm-map-20261111/README.md) ghi 65 ca logic, build, 11 ca Chrome trên ảnh mới và 3 ca offline riêng đạt. Tile Chrome là ảnh kiểm; kết nối tới tile thật bị reset nên chưa xác nhận nền thật hiển thị. [Bảng issue/lỗi](01-tong-quan/issue-follow-up-2026-11-11.md) giữ checklist còn lại T01–T11, chưa cập nhật GitHub. Các mốc bên dưới giữ đúng kết quả lịch sử.

**Biên tập:** 11/11/2026. Nghiệp vụ/phạm vi PRD và các hướng UI/công nghệ được xác nhận đúng phần; toàn TDD, ranh giới, nội dung và bằng chứng phần mềm chưa được duyệt theo gói. Áp dụng [quy trình phối hợp tại handoff](01-tong-quan/handoff.md#guidance-protocol).

**Hiện trạng cần ưu tiên:** [rà soát thiếu sót toàn hệ thống](06-kiem-thu/review-20261111/README.md) đối chiếu PRD/UI, nguồn, runtime và bằng chứng; ghi mười nhóm còn mở, gồm HTTPS/QR, bản đồ/bộ demo, nội dung/TTS, Admin/thiết bị và vận hành/nghiệm thu. Bản đang chạy là môi trường kiểm kỹ thuật, chưa đạt demo hoàn chỉnh hoặc toàn đồ án. Các kết quả bên dưới chỉ có hiệu lực trong phạm vi từng lượt.

**Mốc đang rà:** [bản đồ offline và bộ nháp riêng](06-kiem-thu/map-preview-20261111/README.md) đã có trang xem trước năm vùng, seed 5 điểm/20 slot/5 nháp vi còn ẩn và sửa lỗi trạng thái Mức đầy đủ; 60 ca logic, build và 3 ca Chrome trên ảnh cuối đạt. [Theo dõi issue T01–T11](01-tong-quan/issue-follow-up-2026-11-11.md) giữ phạm vi/G02, chưa sửa GitHub. Preview 18082 chờ rà; bản E2E 18081 đang dừng để giảm RAM. Không suy preview thành duyệt vùng hoặc demo Khách có QR/audio hoàn chỉnh.

**Dọn môi trường thử:** [kết quả Docker](06-kiem-thu/docker-cleanup-20261111/README.md) ghi đã xóa 6 container cũ và 3 image ứng dụng cũ, giữ toàn bộ volume/audio/bằng chứng và bản preview 18082. Container cũ đã xóa không thể dùng lại bằng `docker start`; cần đối chiếu volume/cấu hình khi dựng lại. Các hồ sơ theo mốc bên dưới giữ nguyên kết quả lịch sử.

**Mốc chạy local 11/11:** frontend khách/Admin đã resolve, 60 ca npm đạt và build thành công; có kiểm Chrome/API/MySQL riêng với MP3 thật. Phạm vi, kết quả và giới hạn tại [hồ sơ kiểm](06-kiem-thu/local-20261111/README.md). Bộ nội dung có [15 bản dịch nháp](05-noi-dung/doi-chieu-van-ban-audio.md) và [bốn audio vi nháp](05-noi-dung/audio-vi-v01.json), chưa duyệt. [Đóng gói/CI](07-van-hanh/dong-goi-va-ci.md) đã có cấu hình nguồn; HTTPS/CD và thiết bị thật chưa kiểm. Các mốc lịch sử bên dưới giữ nguyên kết quả tại thời điểm ghi.

**Mốc đóng gói/restore 11/11:** [ảnh Linux và diễn tập offline](06-kiem-thu/packaging-20261111/README.md) đã kiểm build, FFmpeg Unicode, 12 file audio có Hash, lọc retention, epoch mới và phát MP3 từ ứng dụng phục hồi chung origin. Chưa có OPS_GUARD/drain live, Compose update/rollback, HTTPS hoặc CI/CD thật. Không tự mở rộng kết quả offline thành hoàn tất vận hành.

**Mốc sửa UI Khách 11/11:** [chữ/bố cục và bằng chứng](06-kiem-thu/guest-ui-20261111/README.md) ghi sửa fallback phông, bố cục dọc, văn bản cuộn riêng và Hủy luôn thấy; 60 ca logic, build và 7 ca Chrome trên bản Docker đạt, đã quan sát trong VS Code. Bản đồ nền lưới/các tam giác E2E vẫn chưa đạt demo; cần chuẩn bị dữ liệu và bản xem trước nền/vùng để duyệt. Không suy kiểm API/audio trước đó thành UI được nghiệm thu.

**Checklist local ngày 09/11:** 89 ô/20 dấu tích theo [roadmap 6.5](01-tong-quan/roadmap.md#local-checklist-20261109). Mốc issue đã đối chiếu có 18 dấu tích; chưa kiểm lại GitHub. Các bản ghi theo ngày giữ nguyên phạm vi và số đếm cũ.

**Tổ chức dự án:** Thành, Hòa và Nghĩa phụ trách theo [G02](02-nghiep-vu/decisions.md#g02), làm việc trên ba máy riêng và bàn giao qua repository chung. Quy trình phối hợp và kiểm chứng tại [handoff mục 7](01-tong-quan/handoff.md#guidance-protocol).

**Bàn giao Git ngày 09/11:** bộ nguồn đã kiểm của Thành/Hòa được chốt và commit theo task; kết quả push, phụ thuộc và điểm dừng trước phần Nghĩa tại [handoff 2.25](01-tong-quan/handoff.md#commit-push-20261109) và [commit-plan 4.12](01-tong-quan/commit-plan-2026-11-05.md#commit-push-result-20261109). File phát sinh sau mốc chốt thuộc lượt tiếp theo; commit không tự hoàn thành issue.


**Mốc nội dung khách và frontend ngày 09/11:** V10/K03/K05–K10, grant/ACK/tải MP3/READ claim và kết quả kiểm tại [API 11.9](03-ky-thuat/api-transactions.md#guest-content-test-result). [Frontend khách](../frontend/README.md) có nguồn Vue, 13 ca logic Playback V8 đạt, chưa resolve/lockfile/build hoặc browser. Điểm tiếp tục tại [handoff 2.24](01-tong-quan/handoff.md#guest-content-frontend-result); không coi source có sẵn là hoàn tất T03/T04.

**Mốc D08 ngày 10/11:** đã bổ sung dọn context và phiên/lượt theo hạn gốc; [operations 6.2](03-ky-thuat/operations-design.md#ops-guest-retention-result) giữ kết quả và giới hạn scheduler/claim rò. Điểm bàn giao tại [handoff 2.26](01-tong-quan/handoff.md#guest-retention-result).

**Mốc vòng đời ngày 10/11:** A11 và cơ chế dọn window/receipt/job/claim/file có [bằng chứng và giới hạn](03-ky-thuat/operations-design.md#ops-lifecycle-cleanup-result); [handoff 2.27](01-tong-quan/handoff.md#lifecycle-cleanup-result) ghi điểm tiếp tục. Cleaner mặc định tắt, chưa áp V11 trên database đang dùng và chưa đủ nghiệm thu toàn issue.

**Mốc A22 ngày 10/11:** [handoff 2.28](01-tong-quan/handoff.md#admin-preview-prepared) ghi backend nghe thử Admin đã nhận vào dự án và dừng để nhóm rà. [API 11.12](03-ky-thuat/api-transactions.md#admin-preview-prepared-result) giữ kết quả 111 ca liên quan đạt, giới hạn MP3 thực/frontend và bằng chứng. Chưa stage/commit/push hoặc đánh dấu hoàn thành toàn issue.

**Bàn giao cục bộ ngày 10/11:** 29 file backend vòng đời/A22 đã commit theo task; 8 tài liệu thuộc nhóm T01. [Handoff 2.29](01-tong-quan/handoff.md#local-commits-lifecycle-preview) và [commit-plan 4.16](01-tong-quan/commit-plan-2026-11-05.md#lifecycle-preview-local-commits) là trạng thái Git mới nhất, thay điểm dừng trước commit ở các mốc kiểm bên dưới. Chưa push lượt này, chưa thay main/issue/Project hoặc checklist.

**Mốc local audio ngày 11/11:** [handoff 2.31](01-tong-quan/handoff.md#local-audio-result) ghi nguồn V12/chuyển đổi/đăng ký audio, Check/Retry và điểm dừng để rà. [API 11.13](03-ky-thuat/api-transactions.md#local-audio-implementation-result) ghi ledger 520 ca từ full và nhóm local/TTS chạy lại, cùng giới hạn encoder giả/FFmpeg/MP3 thực. Chưa stage/commit/push mốc mới hoặc thay checklist; frontend vẫn làm riêng theo 2.30.

**Thứ tự đọc từ tổng quan đến triển khai:** README gốc → Business Flow → PRD và các quyết định liên quan → UI → TDD → mô hình dữ liệu/API/vận hành. Sau đó dùng roadmap để chọn công việc, handoff để xác định trạng thái và quyền thao tác, commit-plan để bàn giao qua nhánh/PR. Mục 1 phục vụ tìm nhanh; mục 2 xác định nguồn chính; mục 3 kiểm kê sơ đồ. Lịch sử chỉ tra khi cần đối chiếu căn cứ, không thay nguồn hiện hành.

<a id="find"></a>

## 1. Muốn tìm gì → mở file/mục nào

| Muốn tìm | Mở trực tiếp |
| --- | --- |
| Hiểu đề tài, vai trò và phạm vi | [README gốc](../README.md), [Business Flow mục 1–2](02-nghiep-vu/business-flow.md#bf-1) |
| Toàn bộ luồng khách / Admin và ngoại lệ | [Khách](02-nghiep-vu/business-flow.md#bf-3) / [Admin](02-nghiep-vu/business-flow.md#bf-4) |
| Quyết định đã chốt / phần còn mở | [Chỉ mục quyết định](02-nghiep-vu/decisions.md#dec-2) / [điểm mở](02-nghiep-vu/decisions.md#dec-6) |
| Yêu cầu, quy tắc, dữ liệu, Use Case và nghiệm thu | [BR/DR](02-nghiep-vu/PRD.md#prd-rules), [FR](02-nghiep-vu/PRD.md#prd-functional), [Use Case mô tả](02-nghiep-vu/PRD.md#prd-use-cases), [NFR](02-nghiep-vu/PRD.md#prd-nfr), [AC](02-nghiep-vu/PRD.md#prd-acceptance) |
| ERD hiện hành | [Nội dung/vùng/cặp](03-ky-thuat/data-model.md#model-content), [phiên/lượt/quyền](03-ky-thuat/data-model.md#model-guest), [job/chống lặp/file](03-ky-thuat/data-model.md#model-operations) |
| DFD từng mức | [Mức 0](03-ky-thuat/dfd.md#dfd-context), [mức 1](03-ky-thuat/dfd.md#dfd-level1), [mức 2 Bắt đầu](03-ky-thuat/dfd.md#dfd-start), [TTS](03-ky-thuat/dfd.md#dfd-tts), [áp dụng cặp](03-ky-thuat/dfd.md#dfd-pair) |
| Kiến trúc và ví dụ Controller → Service → Repository | [Tổng thể](03-ky-thuat/TDD.md#tdd-overview), [ba lớp](03-ky-thuat/TDD.md#tdd-layers) |
| Sequence và trạng thái | [Áp dụng qua ba lớp](03-ky-thuat/TDD.md#tdd-layers), [mất phản hồi](03-ky-thuat/api-transactions.md#api-pair-sequence), [Bắt đầu phiên](03-ky-thuat/practice-01-start-session.md#practice-slice), [trạng thái TTS](03-ky-thuat/operations-design.md#ops-jobs) |
| API/DTO, quyền và giao dịch | [Khách](03-ky-thuat/api-transactions.md#api-guest), [Admin](03-ky-thuat/api-transactions.md#api-admin), [giao dịch](03-ky-thuat/api-transactions.md#api-transactions), [DTO](03-ky-thuat/api-transactions.md#api-dto) |
| UI khách / Admin | [Khách](04-giao-dien/ui-design.md#ui-screens), [địa điểm/vùng](04-giao-dien/ui-design.md#ui-admin-round1), [nội dung/audio](04-giao-dien/ui-design.md#ui-admin-round2), [phiên/thống kê/QR](04-giao-dien/ui-design.md#ui-admin-round3) |
| Công nghệ, TTS, phiên bản và thiết bị | [Đầu vào](01-tong-quan/technical-inputs.md#inputs-confirmed), [TTS](01-tong-quan/technical-inputs.md#tts-research), [bộ phiên bản](01-tong-quan/technical-inputs.md#version-proposal) |
| CI/CD, backup/restore và vòng đời | [CI/CD dự thảo](03-ky-thuat/TDD.md#tdd-cicd), [backup](03-ky-thuat/operations-design.md#ops-backup), [restore](03-ky-thuat/operations-design.md#ops-restore), [vòng đời](03-ky-thuat/operations-design.md#ops-lifecycle) |
| Chạy thử một địa điểm | [Hướng dẫn và cấu hình mẫu](07-van-hanh/chay-thu/README.md), [bằng chứng](06-kiem-thu/chay-thu-mot-dia-diem.json), [bảng đối chiếu 20 bản](05-noi-dung/doi-chieu-van-ban-audio.md) |
| Công cụ local đã cài | [Phiên bản, nguồn tải và phép kiểm Windows](07-van-hanh/moi-truong-local.md) |
| Tiến độ / checklist / lịch có giả định | [11 task](01-tong-quan/roadmap.md#t01), [lịch](01-tong-quan/roadmap.md#target-1711), [mapping hiện có](01-tong-quan/roadmap.md#github-checklist-review), [bộ issue cho đích mới](01-tong-quan/roadmap.md#new-project-ready) |
| Kết luận rà soát và R06 | [Trạng thái phát hiện](02-nghiep-vu/prd-review.md), [phụ thuộc còn mở](02-nghiep-vu/prd-review.md#review-open-final) |
| Dừng thực hành ở đâu / bàn giao công việc | [Điểm dừng backend](03-ky-thuat/practice-01-start-session.md#practice-stop), [điểm bàn giao hiện hành](01-tong-quan/handoff.md#new-target-pending), [kết quả chỉnh comment](01-tong-quan/handoff.md#comment-audit-20261108) |
| Phân nhóm commit và bàn giao | [Kế hoạch commit 05/11/2026](01-tong-quan/commit-plan-2026-11-05.md) — phân nhóm nguồn đã rà; đích chính thức đã được xác nhận, đối chiếu trạng thái trước nhóm Git tiếp theo |
| Căn cứ cũ, phương án bị thay thế | [Mục lục lưu trữ](99-luu-tru/README.md), [căn cứ xác nhận](99-luu-tru/decisions-history.md#dec-8) |

<a id="document-map"></a>

## 2. Bản đồ tài liệu — vai trò và thời điểm đọc

Phiên bản dưới đây là **mốc nội dung**, không coi đợt biên tập này là phê duyệt mới. Đọc trạng thái ở đầu file; phần kỹ thuật vẫn cập nhật theo triển khai.

| File hiện hành | Dùng để làm gì / chứa gì | Khi đọc / trạng thái |
| --- | --- | --- |
| [01-tong-quan/roadmap.md](01-tong-quan/roadmap.md) | T01–T11, phân công G02, checkbox có căn cứ, phụ thuộc/lịch và snapshot issue | Lập kế hoạch/ghi tiến độ. 0.11; đã tạo và đối chiếu đủ 11 issue #1–#11, khớp 89 ô/18 dấu tích; mapping tại mục 6, người dùng xác nhận Project đủ 11 issue và đã đặt Status: 6 In Progress, 5 Todo, 0 Done; chưa có lần kiểm tra trực tiếp mới. |
| [01-tong-quan/technical-inputs.md](01-tong-quan/technical-inputs.md) | Đầu vào đã xác nhận, khảo sát TTS/ngrok có ngày, bộ phiên bản, chính sách và đầu vào TDD | Trước thiết kế/cấu hình phần tương ứng. 0.7; đã chọn khác đã chạy thử; khảo sát dịch vụ giữ mốc 03/11, chưa có lần kiểm chứng nguồn mới. |
| [01-tong-quan/handoff.md](01-tong-quan/handoff.md) | Đường đọc, trạng thái, quyết định chính, bằng chứng và điểm dừng backend | Khi bàn giao hoặc tiếp tục công việc; file sống được cập nhật cùng mốc triển khai. |
| [01-tong-quan/commit-plan-2026-11-05.md](01-tong-quan/commit-plan-2026-11-05.md) | Phân nhóm sản phẩm theo task, nhánh Txx, tài khoản G02 và các mốc bàn giao | Đã merge nền T01/T02; tiếp tục commit/push phần Thành/Hòa theo quyền tại handoff mục 7, dừng trước phần Nghĩa. Trạng thái nguồn tại handoff mục 2.24; PR/merge cần được giao riêng. |
| [02-nghiep-vu/business-flow.md](02-nghiep-vu/business-flow.md) | Luồng khách/Admin, ngoại lệ và liên quan hai vai trò | Hiểu hệ thống; nghiệp vụ đã duyệt đúng phạm vi BF và bổ sung riêng. |
| [02-nghiep-vu/decisions.md](02-nghiep-vu/decisions.md) | Quyết định hiện hành G/BF/PRD/D/BS/P/A/Q/UI/TECH/DATA và phần mở | Xác định căn cứ/giải quyết khác biệt; lịch sử và nguồn dài dẫn sang lưu trữ. |
| [02-nghiep-vu/PRD.md](02-nghiep-vu/PRD.md) | BR/DR, 23 FR, 14 UC dạng mô tả và hình tổng quan, 6 NFR, 50 AC và truy vết | Triển khai/đánh giá một hành vi. 0.8.8; kế thừa PRD-01 và xác nhận riêng, phần mở loại trừ. |
| [02-nghiep-vu/prd-review.md](02-nghiep-vu/prd-review.md) | Phát hiện R/S, kết luận, bao phủ và phụ thuộc hiện hành | Rà soát yêu cầu, tra R06; R01–R05/R07/S01/S02 đã xử lý tài liệu, R06 còn mở một phần. |
| [03-ky-thuat/TDD.md](03-ky-thuat/TDD.md) | Kiến trúc, module/ba lớp, xác thực, tab/audio, môi trường/CI-CD, đường đọc chi tiết | Học nền và nối yêu cầu với cách làm. 0.7 dự thảo. |
| [03-ky-thuat/data-model.md](03-ky-thuat/data-model.md) | Ba ERD logic, định danh/quan hệ/khóa/xóa, bằng chứng V2 và dẫn tới V3–V9 | 0.4 còn dự thảo toàn mô hình; V9/audio/cặp/seed có bằng chứng riêng, chưa toàn schema/quyền hoặc nội dung được duyệt. |
| [03-ky-thuat/api-transactions.md](03-ky-thuat/api-transactions.md) | API/DTO/quyền/giao dịch, xác thực, địa điểm/vùng và bằng chứng | 0.4 hợp đồng tổng thể còn dự thảo; đã kiểm các API phiên khách/xác thực/cửa sổ/địa điểm nêu ở handoff cùng A12–A14 và phần nháp/hồ sơ A15–A17. Mục 9.8–9.14 giữ hợp đồng vùng/văn bản/worker/Admin/cặp và nội dung khách; 11.9 là nguồn kết quả mới nhất ngày 09/11, mốc 366 test ở 11.8 và các mốc trước giữ riêng. |
| [03-ky-thuat/operations-design.md](03-ky-thuat/operations-design.md) | Vòng đời, trạng thái TTS, file, backup/restore và V-* | 0.3 dự thảo; phần Bắt đầu/hàng đợi có bằng chứng riêng, chưa toàn vận hành. |
| [03-ky-thuat/dfd.md](03-ky-thuat/dfd.md) | Năm DFD và từ điển/cân bằng cha–con | Học dữ liệu vào/ra hoặc đưa báo cáo; 0.3 dự thảo. SVG mức 1 xuất cùng nguồn, giữ giới hạn khổ giấy. |
| [03-ky-thuat/practice-01-start-session.md](03-ky-thuat/practice-01-start-session.md) | Các chặng nhỏ, bằng chứng, lỗi/ôn tập và dẫn tới điểm dừng hiện hành | Giữ các mốc phiên khách/K04 và dẫn tới bằng chứng mới nhất ở API mục 11.9/handoff mục 2.24; chưa toàn T03/T05. |
| [04-giao-dien/ui-design.md](04-giao-dien/ui-design.md) | UI khách/Admin, sơ đồ chữ/trạng thái/truy vết và phần còn thiết kế | Triển khai giao diện/đối chiếu thao tác. 0.7.8, hướng đã chốt đúng phần; toàn bộ UI đứng trước nhật ký. |
| [04-giao-dien/design-assets/README.md](04-giao-dien/design-assets/README.md) | Nguồn, phương pháp và giới hạn PNG/SVG/OSM/GeoJSON | Trước dùng/đánh giá nền-vùng. Tài sản 0.2 đề xuất, chưa duyệt nền/vị trí/ranh giới. |
| [99-luu-tru/README.md](99-luu-tru/README.md) và các file được liệt kê | Bản UI/mô hình cũ, dấu vết quyết định/khảo sát/rà soát/nhật ký | Chỉ khi cần căn cứ hoặc lịch sử; không là nguồn triển khai song song. |

**Markdown ngoài phạm vi biên tập:** [backend/HELP.md](../backend/HELP.md) là tham khảo do Initializr sinh, không là yêu cầu hoặc hướng dẫn hiện hành của dự án; đã kiểm kê vai trò, giữ nguyên. README gốc giới thiệu repo, không chép lại toàn bộ đặc tả.

<a id="diagram-index"></a>

## 3. Danh mục sơ đồ — đã có và còn thiếu

Có **15 khối Mermaid hiện hành** trong tài liệu kỹ thuật/hướng dẫn và **một sơ đồ Use Case tổng quan** có nguồn Draw.io cùng bản xuất SVG. Các khối Mermaid giữ nguyên trong lượt bổ sung sơ đồ ngày 08/11/2026; trạng thái kiểm tra của từng sản phẩm được ghi riêng.

| Tên / mục đích | Nguồn chính | Bản xuất / trạng thái |
| --- | --- | --- |
| Kiến trúc tổng thể — frontend/backend, dữ liệu và dịch vụ | [TDD mục 2](03-ky-thuat/TDD.md#tdd-overview) | Mermaid flowchart đã có; dự thảo, chưa bản xuất riêng. |
| Ba lớp — sequence Áp dụng cặp qua Controller/Service/Repository | [TDD mục 4](03-ky-thuat/TDD.md#tdd-layers) | Mermaid sequence đã có; giải thích trách nhiệm, chưa bằng chứng chạy. |
| Pipeline WAV/MP3/file | [TDD mục 11](03-ky-thuat/TDD.md#tdd-audio) | Mermaid flowchart đã có; dự thảo. |
| CI/CD — đường build và CD vào laptop | [TDD mục 14](03-ky-thuat/TDD.md#tdd-cicd) | Mermaid flowchart đã có; chưa workflow/run/SSH/Docker/CD thành công. |
| ERD nội dung — điểm/vùng/văn bản/audio/cặp | [model mục 2](03-ky-thuat/data-model.md#model-content) | Mermaid ERD đã có; dự thảo logic, chưa schema chạy. |
| ERD phiên — tab/lần mở/quyền tải/lượt | [model mục 3](03-ky-thuat/data-model.md#model-guest) | Mermaid ERD đã có; mô hình hiện hành. |
| ERD thao tác — cửa sổ/chống lặp/job/file | [model mục 4](03-ky-thuat/data-model.md#model-operations) | Mermaid ERD đã có; dự thảo. |
| DFD mức 0 — hệ thống với Khách/Admin/Gemini | [DFD mục 2](03-ky-thuat/dfd.md#dfd-context) | Mermaid đã có; dự thảo, chưa bản xuất riêng. |
| DFD mức 1 — xử lý chính và kho | [DFD mục 4](03-ky-thuat/dfd.md#dfd-level1) | [SVG](03-ky-thuat/dfd-level1.svg) cùng nguồn đã có; giới hạn hiển thị/khổ giấy ở mục 8.3, chưa duyệt từng hình. |
| DFD mức 2 — Bắt đầu phiên/vé ký 24 giờ | [DFD mục 5](03-ky-thuat/dfd.md#dfd-start) | Mermaid đã có; từ điển và cân bằng kèm theo. |
| DFD mức 2 — tạo/xử lý TTS | [DFD mục 6](03-ky-thuat/dfd.md#dfd-tts) | Mermaid đã có; tạo thành công khác duyệt/áp dụng. |
| DFD mức 2 — áp dụng cả cặp | [DFD mục 7](03-ky-thuat/dfd.md#dfd-pair) | Mermaid đã có; không áp dụng riêng văn bản/audio. |
| Sequence mất phản hồi khi Áp dụng | [API mục 6.3](03-ky-thuat/api-transactions.md#api-pair-sequence) | Mermaid sequence đã có; dự thảo giao dịch/tra kết quả. |
| Trạng thái tác vụ TTS | [operations mục 2](03-ky-thuat/operations-design.md#ops-jobs) | Mermaid stateDiagram đã có; chưa tác vụ thực. |
| Sequence lát cắt Bắt đầu phiên | [thực hành mục 2](03-ky-thuat/practice-01-start-session.md#practice-slice) | K11/K01/K02 đã kiểm với MySQL tạm; chưa frontend/DEV bền vững. |
| UI khách/Admin — bố cục và chuyển tiếp | [UI khách](04-giao-dien/ui-design.md#ui-layout), [Admin 1](04-giao-dien/ui-design.md#ui-admin-round1), [2](04-giao-dien/ui-design.md#ui-admin-round2), [3](04-giao-dien/ui-design.md#ui-admin-round3) | Sơ đồ chữ đã có; [ảnh khách](04-giao-dien/design-assets/guest-visit-layout-v02.png) không chứa đầy đủ box mới; giữ giới hạn ảnh cũ. |
| Bản đồ/vùng chạm đề xuất | [UI mục 7](04-giao-dien/ui-design.md#ui-map), [nguồn](04-giao-dien/design-assets/README.md) | [PNG](04-giao-dien/design-assets/hue-five-regions-proposal.png)/[SVG](04-giao-dien/design-assets/hue-five-regions-proposal.svg)/JSON/GeoJSON đã có; chưa xác minh hoặc duyệt ranh giới. |
| Use Case — hình tổng quan tác nhân/chức năng | [PRD mục 5](02-nghiep-vu/PRD.md#use-case-overview), [Business Flow](02-nghiep-vu/business-flow.md#bf-2) | [Nguồn Draw.io](02-nghiep-vu/use-case-overview.drawio) và [SVG cùng nguồn](02-nghiep-vu/use-case-overview.svg) đã có; đủ 14 mã UC, một trang A3 ngang. Đã xem ảnh SVG qua trình duyệt ngày 08/11, kiểm chữ tiếng Việt/nhãn/đường nối/mép hình; chờ nhóm rà soát và kiểm bản in thực tế. |
| Trạng thái phiên khách/Admin — hình riêng | [Quy tắc phiên](02-nghiep-vu/business-flow.md#bf-expiry-audio), [API/quyền](03-ky-thuat/api-transactions.md#api-auth) | Hiện có bảng/mô tả và sequence liên quan; **chưa có state diagram riêng**. Không nhầm với hình trạng thái TTS. |

Nguồn .drawio là bản chỉnh sửa chính của hình Use Case, SVG được xuất lại từ cùng nguồn khi có thay đổi. Draw.io Integration (`hediet.vscode-drawio`) là công cụ tùy chọn trong VS Code. Hình tổng quan yêu cầu được dùng cùng mô tả UC và Business Flow; các sơ đồ Mermaid/DFD giữ kết quả kiểm theo thời điểm đã ghi.

<a id="reading-paths"></a>

## 4. Ba đường đọc

1. **Hiểu đồ án:** README gốc → Business Flow 1–5 → PRD phạm vi/UC → UI khách/Admin → TDD tổng thể/ba lớp → ERD/DFD. Xem phần mở sau khi đã hiểu luồng chính.
2. **Tra cứu yêu cầu và bằng chứng:** mục 1 ở trên → quyết định nguồn → BR/FR/UC/AC tương ứng → sơ đồ giải thích → bằng chứng thực tế (nếu đã có). Nói rõ cái đã chọn/đã mô tả và cái đã chạy; đã có bằng chứng backend nhưng chưa nghiệm thu toàn hệ thống.
3. **Triển khai một chức năng:** decisions/Business Flow → FR/UC/AC trong PRD → màn hình UI → dữ liệu/API/giao dịch → lifecycle/kiểm chứng → bài thực hành/roadmap. Ví dụ Bắt đầu: DATA-05/LIFE-P01 → UC-K01 → UI-K01 → model-guest/API-K11/K01/K02 → ops-start-simple/V-START01 → điểm dừng thực hành. Tiếp tục theo [quy ước hướng dẫn hiện hành](01-tong-quan/handoff.md#guidance-protocol).

<a id="actual-progress"></a>

## 5. Hiện trạng và phần chưa được chứng minh

Mốc hồi quy backend gần nhất: **421 test đạt, 0 failures/errors/skipped, BUILD SUCCESS, exit 0**, 2026-11-10T00:32:58+07:00, trên MySQL tạm. [Bằng chứng D08 và giới hạn](03-ky-thuat/operations-design.md#ops-guest-retention-result) là nguồn chính; mốc 397 test nội dung khách và các mốc trước giữ nguyên theo thời điểm tại từng hồ sơ.

| Sản phẩm hiện có | Phạm vi chưa hoàn tất |
| --- | --- |
| V1–V10, audio/cặp, opening/grant/READ claim, seed riêng 5 điểm/20 ngăn và ràng buộc đã kiểm | Nội dung được duyệt, vòng đời file/job và migration/seed trên môi trường vận hành |
| K03/K05–K10, K11/K01/K02/K04, grant/nội dung/audio/ACK, ledger/replay/expiry và đồng thời | Owner/browser, runtime bền vững, claim rò và restore |
| A01–A05, window/receipt, địa điểm/vùng/nháp/hồ sơ; hồ sơ audio/áp dụng cặp/bật-ẩn/mức đầy đủ A10/A15/A23–A25 | Q2/frontend, ranh giới/hồ sơ thật, MP3/preview, xóa/dọn và phần quản trị còn lại |
| Worker/adapter WAV, READY/manifest, Check local/Retry lỗi xác định và job của nháp | Provider/quota thật, FFmpeg/MP3/audio, crash thật/file claims/dọn và toàn T07 |
| Đọc phiên/lượt/thống kê A26–A28, dọn context và xóa vật lý D08 đã kiểm | Bật/đo scheduler vận hành, claim rò, frontend và toàn T05 |
| Use Case Draw.io/SVG, Mermaid và tài sản bản đồ theo danh mục | Rà bản in, UI ứng dụng, nền/vị trí/ranh giới thật chưa được duyệt |

Bộ nghiệm thu **20 văn bản/20 audio** vẫn giữ nguyên. Năm nháp vi đã được báo cáo là có, còn cần xác minh bản lưu, nguồn, bản dịch, nghe trọn và hồ sơ duyệt. Không dùng kết quả backend để đóng R06, toàn T01/T02/T03/T05 hoặc các mục UI/nội dung/CI-CD/vận hành.

Trạng thái Git, Project và điểm tiếp tục có nguồn chính tại [handoff](01-tong-quan/handoff.md#next-step); phân nhóm bàn giao tại [commit-plan](01-tong-quan/commit-plan-2026-11-05.md). Nền T01/T02 đã tích hợp qua PR #12/#13; các nhóm T01/T02/T03/T05/T07 đã push theo handoff 2.25. Quyền Git hiện hành tại mục 7 handoff; D08 là mốc mới được ghi riêng, không suy đã merge main. Phần còn mở theo [decisions](02-nghiep-vu/decisions.md#dec-6), [R06](02-nghiep-vu/prd-review.md#review-open-final) và [TDD](03-ky-thuat/TDD.md#tdd-open).

<a id="structure"></a>

## 6. Cấu trúc tài liệu sau chuẩn hóa

```text
docs/
├── README.md
├── 01-tong-quan/
│   ├── roadmap.md
│   ├── technical-inputs.md
│   ├── handoff.md
│   └── commit-plan-2026-11-05.md
├── 02-nghiep-vu/
│   ├── business-flow.md
│   ├── PRD.md
│   ├── use-case-overview.drawio
│   ├── use-case-overview.svg
│   ├── decisions.md
│   └── prd-review.md
├── 03-ky-thuat/
│   ├── TDD.md
│   ├── data-model.md
│   ├── api-transactions.md
│   ├── operations-design.md
│   ├── practice-01-start-session.md
│   ├── dfd.md
│   └── dfd-level1.svg
├── 04-giao-dien/
│   ├── ui-design.md
│   └── design-assets/                 # README + sáu tài sản/hồ sơ, giữ nguyên
├── 05-noi-dung/
│   ├── doi-chieu-van-ban-audio.md
│   └── thuyet-minh-vi-v01.md
├── 06-kiem-thu/
│   └── chay-thu-mot-dia-diem.json
├── 07-van-hanh/
│   └── chay-thu/                      # hướng dẫn và cấu hình mẫu
└── 99-luu-tru/
    ├── README.md
    ├── ui-design-history.md           # di chuyển từ 04-giao-dien
    ├── data-model-history.md          # di chuyển từ 03-ky-thuat
    ├── decisions-history.md           # phương án cũ và căn cứ dài
    ├── technical-inputs-history.md    # demo/source snapshot/phương án 0.1
    ├── prd-review-history.md          # diễn biến rà soát theo từng lượt
    └── document-history.md            # mở đầu cũ, nhật ký và hướng dẫn đã qua
```

Tài liệu hiện hành và tài sản giữ vị trí ngoài hai file lịch sử đã chuyển. Các nhóm 05-noi-dung, 06-kiem-thu và 07-van-hanh chứa sản phẩm của mốc chuẩn bị chạy thử. Khung toàn dự án ở [README gốc](../README.md).

<a id="handoff"></a>

## 7. Bàn giao và thay đổi đường dẫn

Đọc [bàn giao hiện hành](01-tong-quan/handoff.md), đối chiếu đường dẫn/phiên bản, sản phẩm, kết quả kiểm chứng và công việc còn mở trước khi tiếp nhận.

| Đường dẫn trước chuẩn hóa | Đường dẫn hiện tại |
| --- | --- |
| docs/04-giao-dien/ui-design-history.md | [docs/99-luu-tru/ui-design-history.md](99-luu-tru/ui-design-history.md) |
| docs/03-ky-thuat/data-model-history.md | [docs/99-luu-tru/data-model-history.md](99-luu-tru/data-model-history.md) |
| Lịch sử/căn cứ dài trong decisions, inputs, review và nhật ký | Các file tương ứng trong [99-luu-tru](99-luu-tru/README.md); phần hiện hành dẫn tới căn cứ, không sao chép thành hai nguồn áp dụng. |

## 8. Nhật ký ngắn



Nhật ký theo thời điểm được giữ nguyên trong [hồ sơ biên tập](99-luu-tru/document-history.md#official-20261107-readme-journal). Phần hiện hành không thay thế kết quả hoặc căn cứ lịch sử.
