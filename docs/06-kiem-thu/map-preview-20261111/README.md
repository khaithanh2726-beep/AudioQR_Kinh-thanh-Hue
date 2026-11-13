# Bản đồ offline, bộ nháp riêng và lỗi Mức đầy đủ — 11/11/2026

## Sản phẩm và giới hạn

Trang `/map-preview.html` hiển thị nền OSM trích lưu trong dự án và năm vùng đề xuất đúng nguồn tại `docs/04-giao-dien/design-assets`. Có số, chú giải tên, dấu chọn, kéo/phóng, nguồn/mốc snapshot và thông báo chờ duyệt. Không tải tile bên ngoài, không định vị khách. Bản xem trước không tạo phiên/lượt, không phát audio và không thay dữ liệu qua thao tác chọn vùng.

Nền được tạo xác định từ 651 way của bản trích, tách lớp dưới vùng tương tác; nạp JSON cục bộ khi cần. GuestMap và trình vùng Admin cùng dùng nền khi bật `VITE_MAP_PREVIEW=true`. Khách thật vẫn chỉ lấy vùng qua API; dữ liệu đề xuất không tự nhập vào phiên hoặc database. Build mặc định giữ cờ false và trang xem trước báo chưa bật.

Môi trường riêng có 5 điểm chuẩn, 20 slot, 5 bài vi được lưu qua API thành nháp, **0 điểm hiển thị, 0 cặp áp dụng và 0 hồ sơ duyệt**. Không sao chép địa điểm/phiên/lượt/audio E2E, không dùng audio sai bản hoặc tạo hồ sơ giả để vượt gate. Manifest [dataset](dataset-manifest.json) ghi mã/Hash năm nháp, không chứa secret. Các bản dịch nháp vẫn ở tài liệu, chưa nhập vào bộ xem trước này.

Trang Admin Mức đầy đủ đã tách loading/error/result: tải lỗi kết thúc nhãn đang đọc, cho thử lại; khi đã có dữ liệu cũ, lỗi mới giữ bảng kèm nhãn lần tải trước. Sửa thêm tên/mã xuống dòng và checkbox nằm cùng nhãn để đọc rõ hơn.

## Kiểm thực chạy

| Kiểm | Kết quả |
|---|---|
| Npm logic | 60/60, exit 0; [log](unit-final.log). |
| Build mặc định | Exit 0; [log](build-default.log). |
| Docker preview | Build exit 0, Vue test/build và backend package đạt; Java test bị bỏ qua trong package. [Tổng kết](image-build-summary.log), [ảnh/runtime](preview-image.json). |
| Chrome trên ảnh cuối | **3/3 đạt, exit 0, 35,4 giây**; [log](browser-final.log), [runner](browser-run.json). Viewport desktop 1280×900 và giả lập điện thoại 390×844; không phải máy thật. |
| Phạm vi bản đồ | 651 path nền, 5 vùng riêng, chọn bằng chạm trong từng polygon và kiểm đúng tên/dấu chọn, đủ chú giải/ghi công; không tràn ngang. Theo dõi toàn yêu cầu: không có origin ngoài hoặc API phiên trên trang xem trước. |
| Mức đầy đủ | Danh sách Admin đủ 5 điểm chuẩn; chặn riêng API để kiểm lỗi, nhãn loading kết thúc; thử lại tải 5 hàng/20 slot, 5 nháp và 0/20 cặp; lỗi lần sau giữ bảng và nhãn dữ liệu cũ. [Assertion](assertions.json). |
| Chế độ mặc định | Kiểm browser riêng trên dist mặc định: không dựng map preview và hiện thông báo chưa bật; [kết quả](default-build-check.json). |

Ảnh: [desktop](map-preview-1280.png), [điện thoại giả lập](map-preview-390.png), [lỗi tải](completeness-failed.png), [thử lại](completeness-retried.png). Ảnh chứng minh bản xem trước, không là phê duyệt ranh giới/nội dung hoặc luồng QR→Khách hoàn chỉnh.

Lượt đầu trước chỉnh cách trình bày tên/mã/checkbox có 3/3 đạt trong 25,9 giây, giữ log ở thư mục thử ngoài repo. Lượt cuối ở bảng trên là căn cứ ảnh/mã hiện hành. Helper chuẩn bị dataset đã chạy thành công; sau đó bổ sung timeout hữu hạn cho bước chờ HTTP và kiểm cú pháp helper, chưa chạy tạo lại một database khác chỉ để kiểm riêng thay đổi timeout. Không cộng số ca giữa các lượt để tăng số lượng ca độc lập.

## Runtime và bàn giao

- Bản xem trước: `http://127.0.0.1:18082/map-preview.html`; Admin tại `/admin.html`, tài khoản `preview-admin`. Mật khẩu/khóa runtime ngoài repo trong LocalAppData `AudioQR-Preview-*` có ACL riêng.
- Container app cuối `hueqr-map-preview-507da98b`; database `hueqr-preview-mysql-91b4feca`, schema/user `hueqr_preview`, không mở host port MySQL. Provider/local audio/cleanup/retention đều tắt; kho audio mới trống.
- Ảnh `hueqr-local:20261111-map-preview`, SHA `978801878f79dfbbd0f9b9268de113153e94f66b0884ec3879932b9547fdd371`. Container preview trước và bản E2E 18081 đã dừng để giảm RAM, dữ liệu còn nguyên; không dùng reset/clean hoặc dọn toàn Docker.
- Quy trình tái tạo tại [fixture demo](../../../backend/src/test/fixtures/demo/README.md), cách mở/điều kiện áp dụng tại [hướng dẫn](../../07-van-hanh/xem-truoc-ban-do.md). Helper hiện đọc tài khoản từ runtime thử đã có; chưa thay quy trình cấp Admin phục vụ từ checkout mới.

Chưa áp dụng vùng, ghép audio, bật năm điểm hoặc mở HTTPS/QR. Cần nhóm xem xét/duyệt nền/vùng và nội dung, hoàn thiện đúng audio/hồ sơ trước chuyển thành demo Khách. Chưa kiểm thiết bị thật hoặc TTS xuyên provider ở mốc này. Không sửa hợp đồng API, Java main hoặc dữ liệu nguồn cũ. Giữ phân công G02 và các checkbox; bảng [theo dõi issue](../../01-tong-quan/issue-follow-up-2026-11-11.md) là tài liệu local, không phải cập nhật đã đăng lên GitHub.
