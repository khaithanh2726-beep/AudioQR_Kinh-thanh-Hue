# Ghim vị trí nhấn và thu gọn preview — 11/11/2026

## Thay đổi

Trang preview bỏ hàng năm nút trùng với chú giải; gộp hướng dẫn và giữ một dòng ghi rõ vùng đề xuất chờ duyệt. Bỏ mô tả lặp trong thẻ đang chọn và dòng nguồn/snapshot không phù hợp với nền raster hiện tại; giữ attribution trong map. Bản đồ là phần chính, thẻ phản hồi chỉ hiển thị tên điểm.

Khách và preview dùng chung một ghim xanh, đặt **đúng tọa độ nhấn trong polygon hợp lệ**, không tại tâm vùng. Lần nhấn hợp lệ tiếp theo dời ghim; nhấn ngoài vùng không thay đổi. Kéo/phóng giữ vị trí địa lý của ghim, không chọn điểm mới. Chồng lấn lưu tọa độ nhấn tới khi chọn ứng viên; Hủy giữ ghim/bài cũ. Ghim không cản thao tác, không GPS, không gửi tọa độ thêm vào API và không tự đặt lại ở tâm sau reload. Vùng bị loại bỏ hoặc sửa không còn chứa điểm nhấn thì bỏ ghim cũ. Tắt animation tự căn vùng lúc khởi tạo để không dịch bản đồ ngay khi bắt đầu nhấn.

Mã sửa: `frontend/src/components/GuestMap.vue`, `src/preview/MapPreview.vue`, `src/style.css`, `src/guest/messages.js`; thêm `frontend/tests/e2e/map-pin.spec.mjs`, cập nhật `map-preview.spec.mjs` và `osm-map.spec.mjs` theo việc bỏ nút trùng. Không thay Java/schema/hợp đồng API, không duyệt hoặc áp dụng vùng/cặp.

## Kiểm thực chạy

- Logic **65/65**, exit 0; build mặc định Windows và Docker cuối đạt. Docker package bỏ chạy backend tests, không gọi đó là lượt backend full mới.
- Kiểm ghim riêng **3/3, 31,3 giây**, exit 0: desktop 1280×900, mobile 390×844, chồng lấn/Hủy/chọn lại. Đối chiếu mũi ghim với tọa độ pixel nhấn trong sai số tối đa 1 pixel; kiểm dời một ghim, ngoài vùng, kéo map và số yêu cầu mở bài.
- Chrome trên **ảnh Docker cuối: 14/14, 1,8 phút**, exit 0. Gồm ba ca ghim ở trên, các ca bố cục/bốn ngôn ngữ, OSM cơ chế chọn/kéo/phóng/lỗi/thử lại, Guest bắt đầu/kết thúc qua API thật và vùng Admin đọc API thật. Không cộng ba ca dev trùng vào tổng 14.
- Lượt dev đầu có 2/3 đạt, ca còn lại ghi nhận lệch khi map đang tự căn. Đã tắt animation căn vùng; lượt ghim và lượt Docker cuối đạt, giữ log đầu để đối chiếu.

Log: [unit](unit.txt), [build cuối](build-default-final.txt), [Docker cuối](docker-build-final.txt), [ghim chạy lại](browser-pin-recheck.txt), [14 ca Docker](browser-packaged.txt). [verification.json](verification.json) và [source-hashes.json](source-hashes.json) ghi phạm vi/Hash.

**Giới hạn:** các request tile trong Chrome bị chặn bằng ảnh có nhãn TEST TILE, không phải ảnh nền OSM thật. Ca chồng lấn dùng phản hồi Guest cố định để kiểm tọa độ/Hủy, không chứng minh nội dung/audio thật. Chưa kiểm thiết bị thật, GPS không được yêu cầu; không kiểm lại kết nối tile thật vốn bị reset ở [mốc trước](../osm-map-20261111/README.md). Không chạy lại nhóm offline riêng hoặc toàn backend vì không đổi loader offline/Java. Đã quan sát ảnh test mobile để rà bố cục/ghim, không dùng computer use.

## Runtime và bàn giao

URL `http://127.0.0.1:18082/map-preview.html`, app `hueqr-osm-preview-f620552b`, ảnh `hueqr-local:20261111-osm-map` / `sha256:5d49ffaea21b1a0bd457b050e836f975af230a9cd03b4d7abe8574827610896b`. Giữ cùng MySQL/kho audio/network/khóa/epoch; provider/local worker/cleanup/retention đều tắt. Bản trước giữ dừng; dev server kiểm đã dừng. Đã thực chạy nhánh refresh đọc app từ manifest thành công; chưa diễn tập nhánh rollback khi lỗi.

Xem [runtime-public.json](runtime-public.json) về HTTP/trạng thái và số đếm. Không copy runtime chứa credential vào hồ sơ. Chưa commit/push hoặc sửa issue/Project/checklist; không cài hoặc đổi phụ thuộc. Năm vùng đề xuất và các bản nháp vẫn chờ duyệt, không dùng ghim để xác nhận ranh giới hay nội dung được phép phục vụ.
