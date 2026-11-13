# Tài sản bản đồ dẫn xuất

`hue-basemap.json` và `hue-proposed-regions.json` được tạo bởi `frontend/scripts/build-map-assets.mjs` từ nguồn đang lưu ở `docs/04-giao-dien/design-assets`. Chạy lại công cụ khi thay nguồn; Hash nguồn, mốc snapshot và trạng thái đề xuất nằm trong metadata. Không tải dữ liệu mới hoặc tile bên ngoài trong quá trình tạo/phục vụ.

Nền có 651 đối tượng way của bản trích OSM 31/10/2026; chuyển thành đường/đa giác với các thuộc tính tối thiểu phục vụ vẽ. Giữ thông tin nguồn/ODbL 1.0 và ghi công OpenStreetMap trên bản đồ; không tuyên bố vị trí hiện tại hoặc ranh giới di tích được xác minh. Hồ sơ nguồn/giấy phép và giới hạn ở `docs/04-giao-dien/design-assets/README.md`.

Năm vùng đề xuất chỉ xuất các trường dùng xem trước, không thay bản GeoJSON/provenance gốc. Cần xem xét/duyệt trước khi áp dụng cho khách. Vùng Khách thật vẫn lấy từ API, không tự đọc các vùng đề xuất vào phiên. `VITE_MAP_PREVIEW=true` là cờ bật xem trước khi build, không là xác nhận phê duyệt.
