# Bộ nội dung thuyết minh

Bộ chuẩn gồm năm địa điểm × vi/en/fr/ru = 20 văn bản và 20 audio khớp. Bản phục vụ mới có Ngọ Môn vi được chấp thuận dùng tập dượt; không suy bản nháp hay kết quả TTS thành đã duyệt.

## Tài liệu dùng để đọc và đối chiếu

- [Toàn bộ văn bản hiện hành v02](thuyet-minh-hien-hanh.md), trình bày theo ngôn ngữ và địa điểm.
- [Bundle dữ liệu v02](demo-content-v02.json): văn bản, Hash và vùng dùng đối chiếu; giữ nguyên để nhận diện các job đã tạo.
- [Ma trận văn bản/audio](doi-chieu-van-ban-audio.md): trạng thái 20 bản.
- [Hồ sơ Ngọ Môn vi](duyet-ngo-mon-tap-duot-20261112.md), [hồ sơ bốn vùng demo](duyet-vung-demo-v02.json).
- [Nguồn và điều chỉnh v02](dieu-chinh-demo-v02.json): hiệu đính Cửu Đỉnh/Diên Thọ và nguồn tham khảo.

## Nguồn tiếp nhận và quy tắc phiên bản

Bốn tệp `thuyet-minh-*-v01.md` được giữ nguyên như nguồn tiếp nhận/đầu vào của công cụ chuẩn bị nội dung, không phải bản đã hiệu đính cuối. Văn bản sử dụng cho lượt tạo mới được đọc từ bundle v02; hai bài Cửu Đỉnh/Diên Thọ đã được điều chỉnh ở cả bốn tiếng. Không dùng bản v01 thay âm thầm cho revision đã có Hash.

Có 14 audio mới vi/en/fr; năm ru còn thiếu vì quota. Trong 14 audio, 13 bản khớp qua ASR nhưng vẫn chờ nghe duyệt trọn; Thái Hòa fr cần nghe lại thuật ngữ kiến trúc. WAV/MP3 giữ ở kho riêng ngoài Git. Vùng chỉ là vùng chọn chạm dùng demo, không phải ranh giới pháp lý/khảo sát.

## Nguồn tham khảo

Các URL theo chủ đề và giới hạn được giữ trong tệp điều chỉnh v02. Bản Ngọ Môn có hồ sơ dùng tập dượt riêng. AI không được ghi thành thẩm định chuyên gia; khi sửa văn bản phải duyệt lại phần bị ảnh hưởng và tạo/đối chiếu audio đúng revision.
