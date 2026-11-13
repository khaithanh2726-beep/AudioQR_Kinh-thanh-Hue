# Đối chiếu 20 văn bản với audio

Phạm vi theo dõi gồm năm địa điểm và bốn ngôn ngữ. Các ô trống chờ ghi mã bản văn, audio và bằng chứng tương ứng.

Ngày 11/11 có bốn audio tiếng Việt nháp (Ngọ Môn, Thái Hòa, Tử Cấm Thành, Cửu Đỉnh), chưa nghe duyệt. Danh mục [audio-vi-v01.json](audio-vi-v01.json) ghi Hash WAV/MP3 và Hash bản văn; tệp thật ở `%LOCALAPPDATA%\AudioQR-Media-Drafts\vi-v01`, ngoài repository. Bài Diên Thọ chưa tạo thành công do provider trả HTTP 503. Các bản en/fr/ru chờ duyệt trước khi tạo audio.

Ghi đường dẫn hoặc mã bản văn đã lưu vào cột Văn bản. Khi có audio, ghi tên tệp, mã Hash hoặc mã revision và bản văn mà audio đọc. Nghe trọn, đối chiếu lời với bản văn, rồi ghi người và ngày duyệt kèm tham chiếu hồ sơ. Cập nhật kết quả áp dụng sau khi Admin áp dụng thành công. Nếu sửa văn bản, theo dõi audio cũ dưới mã bản cũ và ghi nhận phần cần thu lại.

| STT | Địa điểm | Ngôn ngữ | Văn bản và phiên bản | Audio và phiên bản văn bản tương ứng | Nghe và duyệt | Áp dụng |
| --- | --- | --- | --- | --- | --- | --- |
| 01 | Cổng Ngọ Môn | vi | [Bản tiếp nhận 01](thuyet-minh-vi-v01.md#ngo-mon) | — | — | — |
| 02 | Cổng Ngọ Môn | en | [Nháp 01, chờ duyệt](thuyet-minh-en-v01.md#ngo-mon) | — | — | — |
| 03 | Cổng Ngọ Môn | fr | [Nháp 01, chờ duyệt](thuyet-minh-fr-v01.md#ngo-mon) | — | — | — |
| 04 | Cổng Ngọ Môn | ru | [Nháp 01, chờ duyệt](thuyet-minh-ru-v01.md#ngo-mon) | — | — | — |
| 05 | Điện Thái Hòa | vi | [Bản tiếp nhận 01](thuyet-minh-vi-v01.md#thai-hoa) | — | — | — |
| 06 | Điện Thái Hòa | en | [Nháp 01, chờ duyệt](thuyet-minh-en-v01.md#thai-hoa) | — | — | — |
| 07 | Điện Thái Hòa | fr | [Nháp 01, chờ duyệt](thuyet-minh-fr-v01.md#thai-hoa) | — | — | — |
| 08 | Điện Thái Hòa | ru | [Nháp 01, chờ duyệt](thuyet-minh-ru-v01.md#thai-hoa) | — | — | — |
| 09 | Tử Cấm Thành | vi | [Bản tiếp nhận 01](thuyet-minh-vi-v01.md#tu-cam-thanh) | — | — | — |
| 10 | Tử Cấm Thành | en | [Nháp 01, chờ duyệt](thuyet-minh-en-v01.md#tu-cam-thanh) | — | — | — |
| 11 | Tử Cấm Thành | fr | [Nháp 01, chờ duyệt](thuyet-minh-fr-v01.md#tu-cam-thanh) | — | — | — |
| 12 | Tử Cấm Thành | ru | [Nháp 01, chờ duyệt](thuyet-minh-ru-v01.md#tu-cam-thanh) | — | — | — |
| 13 | Cửu Đỉnh | vi | [Bản tiếp nhận 01](thuyet-minh-vi-v01.md#cuu-dinh) | — | — | — |
| 14 | Cửu Đỉnh | en | [Nháp 01, chờ duyệt](thuyet-minh-en-v01.md#cuu-dinh) | — | — | — |
| 15 | Cửu Đỉnh | fr | [Nháp 01, chờ duyệt](thuyet-minh-fr-v01.md#cuu-dinh) | — | — | — |
| 16 | Cửu Đỉnh | ru | [Nháp 01, chờ duyệt](thuyet-minh-ru-v01.md#cuu-dinh) | — | — | — |
| 17 | Cung Diên Thọ | vi | [Bản tiếp nhận 01](thuyet-minh-vi-v01.md#dien-tho) | — | — | — |
| 18 | Cung Diên Thọ | en | [Nháp 01, chờ duyệt](thuyet-minh-en-v01.md#dien-tho) | — | — | — |
| 19 | Cung Diên Thọ | fr | [Nháp 01, chờ duyệt](thuyet-minh-fr-v01.md#dien-tho) | — | — | — |
| 20 | Cung Diên Thọ | ru | [Nháp 01, chờ duyệt](thuyet-minh-ru-v01.md#dien-tho) | — | — | — |

Chọn một dòng tiếng Việt cho [lần chạy thử đầu tiên](../07-van-hanh/chay-thu/README.md). Dùng bằng chứng của chính dòng đó khi ghi hai hồ sơ duyệt trong Admin.
