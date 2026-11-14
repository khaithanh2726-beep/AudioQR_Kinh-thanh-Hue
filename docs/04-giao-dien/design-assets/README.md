# Nguồn và giới hạn bản phác thảo bản đồ Huế — 0.2

**Ngày lấy dữ liệu:** 31/10/2026. **Trạng thái:** đề xuất để người dùng xem xét, chưa duyệt nền/vị trí/ranh giới; không cấu hình ứng dụng hoặc dữ liệu định vị khách.

## Tài sản

- [Bản đồ PNG](hue-five-regions-proposal.png) và [SVG](hue-five-regions-proposal.svg): cùng bản thiết kế; viền nét đứt/màu là vùng chạm đề xuất, nét đậm + ✓ minh họa điểm đang chọn.
- [Trang tham quan](guest-visit-layout-v02.png): phác thảo bố cục đã xác nhận về hướng; pixel/màu/mốc nghe chỉ minh họa, thiết kế cụ thể chưa duyệt.
- [Nền trích OSM](hue-osm-source.json): bản trích đã lọc giữ hình học và thuộc tính bản đồ cần thiết, bỏ liên hệ/địa chỉ cá nhân hoặc thông tin cơ sở kinh doanh không cần thiết; không bản sao database dự án.
- [Năm đa giác đề xuất](hue-touch-regions-proposal.geojson): tọa độ vùng chạm cho tài liệu, có phương pháp/nguồn riêng từng vùng. Định dạng này không chốt schema hoặc công nghệ ứng dụng.

## Nguồn nền và vị trí

**Làm rõ hồ sơ ngày 01/11/2026:** kiểm tra bản trích đang lưu thấy 655 đối tượng nhưng **không có OSM way/285990343 (Cần Chánh)**. Giữ liên kết này như **nguồn tham chiếu bên ngoài bản trích**, không gọi là đối tượng đã lưu trong snapshot. Chưa có bằng chứng thu thập bổ sung hoặc kiểm chứng lại liên kết ngoài; không gán thời điểm lấy mới hay mặc định đối tượng có cùng thời điểm với bản trích cũ. Metadata MAP-Z03 đã ghi rõ tình trạng này; giữ nguyên source_ids, mốc và hình học/ảnh đề xuất. Đây chỉ là sửa hồ sơ nguồn, không xác minh hoặc duyệt vị trí/ranh giới di tích.

Nền được **vẽ lại từ hình học OpenStreetMap**, không ảnh vệ tinh/Google Maps: công trình, đường đi, nước và đường thành theo dữ liệu cộng đồng. Dùng Overpass API đọc hình học khu vực bbox nam/tây/bắc/đông **16.4645, 107.5720, 16.4735, 107.5825**; bản trích có `timestamp_osm_base=2026-10-31T15:46:50Z`. Khung ảnh cắt gọn khu vực năm điểm, hướng Bắc lên trên; phép chiếu cục bộ xấp xỉ quanh 16.469°B cho bản vẽ nhỏ, không dùng đo kiểm chính xác. Chỉ giữ 655 đối tượng cần cho hình nền/mốc tham chiếu; có lược bớt nhãn để đọc vùng.

**Ghi công:** © OpenStreetMap contributors, dữ liệu [ODbL 1.0](https://opendatacommons.org/licenses/odbl/1-0/); xem [Copyright and License](https://www.openstreetmap.org/copyright). Bản trích/đa giác xây từ dữ liệu này giữ dẫn nguồn và ODbL khi chia sẻ; ảnh có ghi công trực tiếp. Việc dùng nguồn trong tài liệu không chọn nhà cung cấp bản đồ chạy ứng dụng hoặc bảo đảm quyền sử dụng mọi dịch vụ tile/API về sau.

| Vùng | Đối tượng nguồn / tình trạng lưu trong bản trích | Mốc tham chiếu kinh độ, vĩ độ WGS84 | Cách đề xuất — chưa là ranh giới được duyệt |
| --- | --- | --- | --- |
| MAP-Z01 Ngọ Môn | [OSM way 172657562](https://www.openstreetmap.org/way/172657562) | 107.5791834, 16.46768245 — tâm hộp công trình | Bao lồi công trình, mở khoảng 12 m để thành vùng chạm. Không toàn bộ ranh giới di tích. |
| MAP-Z02 Thái Hòa | [OSM way 531031566](https://www.openstreetmap.org/way/531031566) | 107.5783867, 16.46874345 — tâm hộp công trình | Bao lồi công trình, mở khoảng 14 m; chỉ minh họa trạng thái đang chọn. |
| MAP-Z03 Tử Cấm Thành | [OSM node 2899189013](https://www.openstreetmap.org/node/2899189013), [Cần Chánh way 285990343](https://www.openstreetmap.org/way/285990343) **— tham chiếu bên ngoài, không có trong bản trích đang lưu; chưa có bằng chứng thu thập hoặc kiểm chứng lại**, [Kiến Trung way 285990348](https://www.openstreetmap.org/way/285990348) | 107.5777550, 16.4694914 — điểm mang tên trên OSM | Đa giác tổng quan do người soạn thiết kế quanh khu trung tâm theo các mốc; không sao chép một đường ranh lịch sử/pháp lý đã xác minh. |
| MAP-Z04 Cửu Đỉnh | [Cao đỉnh node 6533792064](https://www.openstreetmap.org/node/6533792064), [node 6533792057](https://www.openstreetmap.org/node/6533792057), [node 6533792058](https://www.openstreetmap.org/node/6533792058); [Hiển Lâm Các way 172657564](https://www.openstreetmap.org/way/172657564) | 107.5769794, 16.4668321 — mốc Cao đỉnh | Dải sân quanh mốc đỉnh; chưa có đo kiểm từng vị trí của cả chín đỉnh. |
| MAP-Z05 Diên Thọ | [OSM way 172763420](https://www.openstreetmap.org/way/172763420) | 107.5754008, 16.46889155 — tâm hộp công trình có tên | Bao lồi công trình chính, mở khoảng 22 m; chưa bao trọn khu cung, cần xem xét riêng nếu muốn mở rộng. |

Các khoảng 12/14/22 m chỉ mô tả cách tạo **bản đề xuất**, không độ chính xác của nguồn, bán kính GPS hay ngưỡng nghiệm thu đã chốt. Mã vùng MAP-Z dùng truy vết thiết kế, không mã địa điểm hệ thống.

## Đối chiếu bổ trợ và giới hạn

- [Sở Văn hóa, Thể thao và Du lịch Huế — Cửu Đỉnh trong kinh thành Huế](https://huetourism.gov.vn/Cuu-Dinh-trong-kinh-thanh-Hue.html/?pid=MTk4NTJ8Y3NkbGRs0): xác nhận mô tả vị trí tại sân Thế Miếu; dùng đối chiếu ngữ cảnh mốc OSM, không suy ra tọa độ/đa giác từ văn bản.
- [Cục Di sản văn hóa — Quần thể kiến trúc Cố đô Huế](https://dsvh.gov.vn/di-tich-lich-su-va-kien-truc-nghe-thuat-quan-the-kien-truc-co-do-hue-2944): nguồn bối cảnh phân khu/di tích, không chứng thực năm đa giác đề xuất. Không lấy bài này làm bản đo ranh giới.
- Chưa khảo sát thực địa, chưa có bản đo hoặc xác nhận chính thức về ranh giới năm đối tượng. OSM có thể thiếu/sai/chậm cập nhật; footprint mang tên Diên Thọ là mốc công trình, không toàn khu. Vùng Tử Cấm Thành là lựa chọn UX tổng quan, không toàn ranh lịch sử đã xác minh.
- Đã đọc ảnh sau dựng để kiểm tra nền/nhãn và đối chiếu hình học năm đa giác trong bản vẽ, chưa thấy chồng lấn nội bộ. Đây không là kiểm thử ứng dụng hoặc bảo đảm trên mọi kích thước/mọi bản sửa; P08 hỏi ứng viên vẫn áp dụng khi khớp nhiều vùng.
- Chạm toàn bộ bên trong vùng là hành vi ứng dụng; PNG/SVG ở đây là ảnh thiết kế tĩnh, không triển khai chạm/audio thật. Không ghim vị trí khách, không GPS/quyền vị trí/chỉ đường, không thêm điểm con Tử Cấm Thành.
- Chưa duyệt bản dịch, thuyết minh hay audio nào qua bản vẽ; năm điểm là bộ chuẩn không trần số điểm. Chỉ hình nền và nguồn vị trí được sử dụng, không sao chép code/dữ liệu/nội dung từ repo tham khảo.

## Phạm vi sử dụng

Bộ tài nguyên là thiết kế tĩnh từ bản trích OSM, phục vụ truy vết nguồn và ý tưởng bố cục. Giao diện chạy hiện hành dùng Leaflet/OSM theo [thiết kế UI](../ui-design.md); các ảnh này không thay dữ liệu vùng đã áp dụng hoặc bằng chứng kiểm ứng dụng.
