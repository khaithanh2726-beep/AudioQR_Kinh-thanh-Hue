# Bộ nội dung thuyết minh

Bộ chuẩn gồm năm địa điểm × vi/en/fr/ru = 20 văn bản và 20 audio khớp. Bản phục vụ đã áp dụng đủ 20 cặp tại năm điểm, bốn tiếng. Ngọ Môn vi giữ hồ sơ cũ; 19 cặp mới được chủ đồ án duyệt cho bản học phần ngày 14/11/2026.

## Tài liệu dùng để đọc và đối chiếu

- [Toàn bộ văn bản hiện hành v02](thuyet-minh-hien-hanh.md), trình bày theo ngôn ngữ và địa điểm.
- [Bundle dữ liệu v02](demo-content-v02.json): văn bản, Hash và vùng dùng đối chiếu; giữ nguyên để nhận diện các job đã tạo.
- [Ma trận văn bản/audio](doi-chieu-van-ban-audio.md): trạng thái 20 bản.
- [Hồ sơ duyệt 19 cặp](duyet-19-cap-v02-20261114.json): revision/Hash cụ thể và giới hạn phê duyệt.
- [Hồ sơ Ngọ Môn vi](duyet-ngo-mon-tap-duot-20261112.md), [hồ sơ bốn vùng demo](duyet-vung-demo-v02.json).
- [Nguồn và điều chỉnh v02](dieu-chinh-demo-v02.json): hiệu đính Cửu Đỉnh/Diên Thọ và nguồn tham khảo.

## Nguồn tiếp nhận và quy tắc phiên bản

Bốn tệp `thuyet-minh-*-v01.md` được giữ nguyên như nguồn tiếp nhận/đầu vào của công cụ chuẩn bị nội dung, không phải bản đã hiệu đính cuối. Văn bản sử dụng cho lượt tạo mới được đọc từ bundle v02; hai bài Cửu Đỉnh/Diên Thọ đã được điều chỉnh ở cả bốn tiếng. Không dùng bản v01 thay âm thầm cho revision đã có Hash. Các trường review trong bundle là snapshot lúc chuẩn bị, không phải trạng thái hiện tại; hồ sơ duyệt revision/Hash và ma trận là nguồn trạng thái sau đó.

Đã có 19 audio mới vi/en/fr/ru, đủ 20 khi tính Ngọ Môn vi cũ. Cả 19 bản mới qua kiểm Hash, probe và giải mã MP3 đầy đủ; 18 transcript khớp ý/số liệu, Thái Hòa fr cần nghe lại thuật ngữ kiến trúc. Chủ đồ án đã chấp thuận bộ hiện có, gồm ghi chú Thái Hòa fr; đây là duyệt dùng cho học phần có giới hạn, không phải chứng nhận chuyên gia hoặc bằng chứng tự động đã nghe trọn. Cặp mới đã áp dụng qua API Admin sau thử trên bản sao; API Khách kiểm 20/20 văn bản/MP3 khớp Hash. Kho 40 WAV/MP3 đã backup/restore riêng đạt. WAV/MP3 giữ ở kho riêng ngoài Git. Vùng chỉ là vùng chọn chạm dùng demo, không phải ranh giới pháp lý/khảo sát.

## Nguồn tham khảo

Các URL theo chủ đề và giới hạn được giữ trong tệp điều chỉnh v02. Bản Ngọ Môn có hồ sơ dùng tập dượt riêng. AI không được ghi thành thẩm định chuyên gia; khi sửa văn bản phải duyệt lại phần bị ảnh hưởng và tạo/đối chiếu audio đúng revision.
