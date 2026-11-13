# Kiểm mẫu thị giác đỏ sẫm — 11/11/2026

## Phạm vi

Áp dụng [UI 3.2.1](../../04-giao-dien/ui-design.md#ui-reference-red) cho nguồn Khách và trang xem trước: đầu đỏ sẫm, nền sáng, thẻ trắng, tiêu đề/vùng đỏ. Giữ phông sans-serif, ô văn bản cuộn riêng, thứ tự map/tên/audio/văn bản, quyền phiên và hợp đồng API. Admin chưa đổi phong cách trong mốc này.

Các file mã sửa: `frontend/src/style.css`, `frontend/src/components/GuestMap.vue`, `frontend/src/preview/MapPreview.vue`. Thêm `frontend/tests/e2e/guest-theme.spec.mjs`. Không đổi backend, database, phiên bản phụ thuộc hoặc cặp đang phục vụ. Ảnh mẫu được lưu tại `docs/04-giao-dien/design-assets/guest-reference-20261111.jpg` để truy vết; không gọi URL/tile trong ảnh.

## Kiểm đã thực chạy

- `npm.cmd test`: 60/60 đạt, 0 lỗi/skip; exit 0.
- `npm.cmd run build`: Vite 8.3.5, 68 module, build mặc định đạt; exit 0.
- Chrome cài sẵn/Playwright: `guest-theme.spec.mjs map-preview.spec.mjs`, **8/8 đạt trong 17,8 giây**, exit 0; [log](browser.log).
- Một ca trang bắt đầu kiểm bốn ngôn ngữ vi/en/fr/ru và màu nút, không tràn ngang ở 390×844.
- Bốn ca trang tham quan ở 1280×800, 960×600, 390×844, 320×568 kiểm đỏ đầu trang/tiêu đề, thứ tự map/tên/audio/text, chiều cao box còn đọc được, cuộn riêng và Kết thúc còn thấy. Các ca này chặn API bằng phản hồi cố định, audio NOT_READY; **chỉ chứng minh UI, không chứng minh API/audio thật**.
- Hai ca bản đồ preview 1280×900/390×844 kiểm 651 path nền/5 vùng, chạm chọn, chú giải, không request API phiên hoặc origin ngoài.
- Một ca Admin qua proxy tới backend preview thật kiểm 5 điểm/20 slot/5 nháp/0 cặp và lỗi/tải lại/dữ liệu cũ Mức đầy đủ. Không tạo cặp, duyệt nội dung hoặc bật hiển thị.

Node 24.22.2/npm 11.19.1 sẵn có; dùng `npm.cmd` vì PowerShell chặn `npm.ps1`, không đổi ExecutionPolicy. Dev server kiểm chỉ bind 127.0.0.1:15184, `VITE_MAP_PREVIEW=true`, proxy tới 127.0.0.1:18082; dừng sau kiểm. Docker 18082 giữ nguyên image trước, **chưa nhận kiểu UI mới**. Không build image mới hoặc mở môi trường công khai.

## Ảnh và giới hạn

- [Trang bắt đầu vi](start-vi.png), [en](start-en.png), [fr](start-fr.png), [ru](start-ru.png).
- [Khách fixture 390](visit-fixture-390.png), [320](visit-fixture-320.png), [960](visit-fixture-960.png), [1280](visit-fixture-1280.png). Nội dung lặp chỉ để ép cuộn, không là văn bản được duyệt hoặc dữ liệu demo.
- [Bản đồ 390](map-preview-390.png), [1280](map-preview-1280.png); [Mức đầy đủ lỗi](../map-preview-20261111/completeness-failed.png) và [tải lại](../map-preview-20261111/completeness-retried.png).

Đã quan sát ảnh Khách 390/320 và bản đồ 390. Bản đồ offline có mức chi tiết khác raster OSM trong ảnh mẫu; chưa tự dùng tile ngoài hoặc thêm định vị. Chưa kiểm Android/iPhone thật, tương tác audio thật sau đổi kiểu, Guest với vùng/cặp đã duyệt, build Docker mới, QR/HTTPS hoặc TTS provider. Preview còn 0 cặp, không dùng fixture để công bố demo hoàn tất.

[Bảng issue/lỗi](../../01-tong-quan/issue-follow-up-2026-11-11.md) là nội dung chuẩn bị cho GitHub; [verification.json](verification.json) và [source-hashes.json](source-hashes.json) giữ kết quả/định danh nguồn. Không thay 89 ô roadmap, GitHub Project, assignee hoặc Git ghi. Điểm dừng: rà mẫu UI trước mốc triển khai tiếp theo.
