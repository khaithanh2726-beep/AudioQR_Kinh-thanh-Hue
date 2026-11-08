# Mục lục lưu trữ

**Tổ chức:** 05/11/2026. Chỉ giữ lịch sử, căn cứ xác nhận và phương án cũ; không là nguồn triển khai song song. Bắt đầu nội dung hiện hành tại [docs/README.md](../README.md).

| File | Nguồn/mục đích | Tài liệu hiện hành |
| --- | --- | --- |
| [ui-design-history.md](ui-design-history.md) | Di chuyển bản UI khách 0.1 từ 04-giao-dien; giữ dấu vết thay thế | [UI khách/Admin](../04-giao-dien/ui-design.md) |
| [data-model-history.md](data-model-history.md) | Di chuyển lịch sử ERD/operations từ 03-ky-thuat; có context/generation/backup online đã bị thay đúng phần | [Mô hình](../03-ky-thuat/data-model.md), [operations](../03-ky-thuat/operations-design.md) |
| [decisions-history.md](decisions-history.md) | Mục 7–8 cũ: phương án bị thay, nguồn xác nhận, nhật ký và bảng đối chiếu đường dẫn | [Quyết định hiện hành](../02-nghiep-vu/decisions.md) |
| [technical-inputs-history.md](technical-inputs-history.md) | Đối chiếu demo, hash/file nguồn khảo sát và phương án 0.1 | [Đầu vào](../01-tong-quan/technical-inputs.md) |
| [prd-review-history.md](prd-review-history.md) | Diễn biến rà soát và kết luận theo thời điểm; không thay kết luận hiện tại | [R01–R07/S01/S02 và R06 còn mở](../02-nghiep-vu/prd-review.md) |
| [document-history.md](document-history.md) | Mở đầu/nhật ký, hồ sơ kiểm chứng và trích đoạn trước biên tập; có [hồ sơ ngày 07/11](document-history.md#official-editorial-20261107) | [Bản đồ tài liệu](../README.md#document-map), [điểm dừng/bàn giao](../01-tong-quan/handoff.md) |

Nội dung còn hiệu lực và điểm mở không bị chuyển khỏi nguồn chính chỉ vì có tuổi đời lâu: quyết định hiện hành vẫn ở decisions mục 3–6; báo cáo giữ phát hiện/kết luận và R06; toàn UI khách/Admin vẫn ở ui-design. Căn cứ duy nhất được lưu ở đây có liên kết từ nguồn chính và vẫn dùng để đối chiếu. Tên/số mục/ngày bên trong khối lịch sử giữ mốc gốc; đường dẫn có thể đã cập nhật để còn truy cập được.

<a id="normalization-record"></a>

**Phạm vi sử dụng:** đây là hồ sơ truy vết nội bộ, giữ nguyên nguồn gốc và kết quả theo thời điểm; không thuộc phần trình bày hiện hành về mục tiêu, yêu cầu, thiết kế và quy trình. Nội dung hiện hành được đọc từ docs/README.md và các nguồn chính được chỉ dẫn.

## Hồ sơ đợt chuẩn hóa 05/11/2026

Đây là ghi nhận biên tập và kiểm tra tài liệu, không là phê duyệt yêu cầu/thiết kế hoặc kiểm thử phần mềm.

### File đã sửa

| File | Thay đổi |
| --- | --- |
| README.md (gốc) | Giới thiệu gọn, cây thực tế tách khung dự kiến, trạng thái backend và đường đọc. |
| docs/README.md | Tìm nhanh, bản đồ toàn bộ tài liệu, danh mục sơ đồ, ba đường đọc, bàn giao và cấu trúc sau chuyển. |
| docs/01-tong-quan/roadmap.md | Rút mở đầu/nhật ký, cập nhật bằng chứng điểm dừng; không đổi trạng thái 89 checkbox. |
| docs/01-tong-quan/technical-inputs.md | Tách đối chiếu/nguồn demo và phương án cũ; giữ mốc phiên bản/chính sách hiện hành và khảo sát có ngày. |
| docs/02-nghiep-vu/business-flow.md | Rút trạng thái lặp, tách nhật ký, sửa nhãn dẫn chiếu cũ; giữ nghiệp vụ. |
| docs/02-nghiep-vu/decisions.md | Giữ vị trí chính mục 3–6; chuyển khối lịch sử/căn cứ dài, giữ ràng buộc cộng tác và dẫn chiếu. |
| docs/02-nghiep-vu/PRD.md | Gọn trạng thái, thêm neo tìm nhanh và dẫn chiếu, tách nhật ký, đồng bộ điểm dừng; không đổi nội dung BR/DR/FR/UC/NFR/AC. |
| docs/02-nghiep-vu/prd-review.md | Giữ kết luận/phát hiện/bao phủ và R06 dễ tra; chuyển diễn biến theo lượt, sửa trạng thái đầu vào đã lỗi thời. |
| docs/03-ky-thuat/TDD.md | Gọn mở đầu/nhật ký và nhãn phiên bản; dẫn điểm dừng hiện tại. |
| docs/03-ky-thuat/data-model.md | Gọn mở đầu/nhật ký, dẫn mô hình cũ tới lưu trữ; giữ ERD/ràng buộc. |
| docs/03-ky-thuat/api-transactions.md | Gọn mở đầu/nhật ký, đặt DTO trước phần truy vết/còn mở, thêm neo sequence; giữ mã/nội dung hợp đồng. |
| docs/03-ky-thuat/operations-design.md | Gọn mở đầu/nhật ký, liên kết lịch sử và điểm dừng; giữ vòng đời/backup/restore/ca kiểm. |
| docs/03-ky-thuat/dfd.md | Gọn mở đầu/nhật ký, thay lời nhắc thực hành đã qua bằng liên kết điểm dừng; giữ nguyên năm sơ đồ/từ điển/cân bằng/SVG. |
| docs/03-ky-thuat/practice-01-start-session.md | Tách các hướng dẫn ZIP/Wrapper đã qua; ghi Wrapper 3.9.12, mysql:latest và tạm dừng bước mới; giữ chặng/migration/sequence/ôn tập. |
| docs/04-giao-dien/ui-design.md | Toàn bộ UI khách/Admin đặt trước nhật ký, gọn mở đầu/dẫn chiếu; giữ màn hình/điểm mở/ảnh. |

### File di chuyển và tạo

- Di chuyển docs/04-giao-dien/ui-design-history.md → docs/99-luu-tru/ui-design-history.md.
- Di chuyển docs/03-ky-thuat/data-model-history.md → docs/99-luu-tru/data-model-history.md.
- Tạo docs/01-tong-quan/handoff.md và docs/99-luu-tru/README.md.
- Tạo docs/99-luu-tru/decisions-history.md, technical-inputs-history.md, prd-review-history.md và document-history.md từ các khối được lưu; không tạo thêm bản hiện hành.
- docs/04-giao-dien/design-assets/README.md đã đọc và giữ nguyên; các tài sản cùng thư mục và dfd-level1.svg giữ nguyên. backend/HELP.md chỉ kiểm kê, toàn bộ backend không bị sửa/di chuyển/chạy bởi trợ lý.

### Kết quả kiểm tra và giới hạn

- Đã đọc lại 24 Markdown gồm README gốc +23 file trong docs/, đối chiếu nguyên nội dung sau ghi với bản biên tập.
- Kiểm đường dẫn nội bộ, anchor, ảnh/SVG, số cột bảng và đóng khối mã: không còn lỗi trong phạm vi đã kiểm. Hai đường dẫn file lịch sử cũ không còn bản trùng.
- Giữ 15 khối Mermaid hiện hành nguyên nội dung; phạm vi yêu cầu PRD mục 3–8 không đổi sau khi loại khác biệt liên kết/anchor. Không mất mã FR/UC/AC/BR/DR/NFR; 89 checkbox roadmap giữ nguyên chuỗi trạng thái.
- Đối chiếu SHA-256 backend (kể cả HELP.md), toàn bộ design-assets và dfd-level1.svg với trước biên tập: không đổi.
- Không kiểm lại website/giá/quota dịch vụ hoặc mở lại 13 tham chiếu ngoài repo tới nguồn demo. Không render lại Mermaid/SVG/khổ giấy trong lượt này; giữ các kết quả và giới hạn xem hình trước đây theo đúng thời điểm.
- Không có kết quả build/chạy/test phần mềm mới; không truy cập hay sửa Git/GitHub, cấu hình, database, CI/CD hoặc triển khai. Chưa coi toàn tài liệu hoàn tất; chờ người dùng rà soát chuẩn hóa rồi tiếp tục từ điểm dừng.

**Cách sử dụng:** tài liệu hiện hành tại docs/01–04 quy định mục tiêu, yêu cầu, thiết kế và quy trình cộng tác. Các bản lưu chỉ dùng truy vết nội dung hoặc bằng chứng tại thời điểm ghi nhận; không áp lại hướng dẫn đã thay thế.

## Nhật ký

05/11/2026: chuyển hai file lịch sử sau kiểm nguồn/đích; tách các khối lịch sử dài, cập nhật liên kết. Không xóa lịch sử, thay quyết định, tạo sơ đồ mới hoặc thực thi phần mềm.

## Dọn bản sao issue tạm ngày 06/11/2026

Đã tạo và đọc lại đủ 11 issue mới #1–#11, khớp tiêu đề/Description/Checklist với [roadmap](../01-tong-quan/roadmap.md#t01): 89 ô/18 dấu tích. Đã xóa bản sao issue gốc và gỡ dẫn chiếu theo quyền tại [handoff 2.1](../01-tong-quan/handoff.md#issue-recreation); nhóm tài liệu còn 31 file. Người dùng xác nhận Project 21 đủ 11 issue và đã đặt Status theo hướng dẫn: 6 In Progress/5 Todo/0 Done; Codex chưa trực tiếp quan sát board. Không stage/commit.

07/11/2026 — cập nhật cách thực hành bản chấm: phân công/thiết kế giữ ba thành viên G02 và mô hình ba máy; Thành trực tiếp thao tác thực tế, giữ danh tính Git của Thành. Bàn giao các nhóm sản phẩm hiện có theo task, rồi Codex tiếp tục code/test tách biệt và dừng ở mục checklist/issue để cùng xem, kiểm thử nhẹ và commit. Đây là quy ước/kế hoạch, chưa là bằng chứng stage/commit/push hoặc nghiệm thu mới.
