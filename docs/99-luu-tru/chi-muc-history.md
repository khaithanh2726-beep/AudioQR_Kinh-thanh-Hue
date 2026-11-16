# Chỉ mục lịch sử nguyên bản

Sáu tệp history đã được tách khỏi bộ tài liệu chấm trong đợt biên tập 13/11/2026. Bản rút gọn theo mốc nằm tại [README](README.md); nguồn triển khai hiện hành đọc từ [mục lục tài liệu](../README.md). Các tệp dưới đây giữ giá trị truy vết, không thay hợp đồng hoặc quyết định hiện hành.

## Vị trí lưu

Kho nằm cạnh repository chính, không nằm trong database/audio hoặc các volume Docker đã dọn:

```text
C:\Study\Code\VS Code\AudioQR_Kinh-thanh-Hue-ho-so-noi-bo\
  bien-tap-20261113-7a62106f\
    lich-su-nguyen-ban\          Sáu history và README cũ
    ban-goc\docs\99-luu-tru\   Bản sao trong toàn bộ nguyên bản
    bien-tap.json                 Danh mục file/bytes/SHA256 trước biên tập
```

Liên kết “máy hiện tại” chỉ dùng khi kho cạnh repository còn đúng vị trí; khi chỉ có repository, dùng bản Git cố định ở cột cuối. Không sửa đường dẫn bên trong nguyên bản để biến tài liệu lịch sử thành đặc tả mới.

| Tệp | Nội dung | Máy hiện tại | Bản Git trước biên tập |
|---|---|---|---|
| data-model-history.md | Lịch sử mô hình dữ liệu, vòng đời và backup | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/data-model-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/data-model-history.md) |
| decisions-history.md | Căn cứ quyết định và phương án đã thay thế | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/decisions-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/decisions-history.md) |
| document-history.md | Lịch sử biên tập, kiểm chứng và bàn giao | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/document-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/document-history.md) |
| prd-review-history.md | Các lượt rà PRD theo thời điểm | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/prd-review-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/prd-review-history.md) |
| technical-inputs-history.md | Khảo sát công nghệ và đối chiếu đầu vào | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/technical-inputs-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/technical-inputs-history.md) |
| ui-design-history.md | Các phiên bản UI trước đây | [Mở nguyên bản](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/lich-su-nguyen-ban/ui-design-history.md) | [Xem tại 601c09a](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/99-luu-tru/ui-design-history.md) |

## Đối chiếu bảo quản

Ngày 15/11/2026 đã đọc lại cả sáu tệp: bytes khớp bản trong ban-goc và SHA256 khớp inventory của [bien-tap.json](../../../AudioQR_Kinh-thanh-Hue-ho-so-noi-bo/bien-tap-20261113-7a62106f/bien-tap.json). Đợt dọn Docker/build ngày 15/11 không xóa kho này. Chỉ mục không sao chép các khối lịch sử dài trở lại bộ mô tả hiện hành.

| Tệp | Bytes | SHA256 |
|---|---:|---|
| data-model-history.md | 66415 | `98525b8d48221a0676e1c528978c8d0472f4b5c29cdc0043b925c0940d6bb53a` |
| decisions-history.md | 113825 | `15850b0f26b471cdd1805a023de3af0e73c4e81f21d83e032e14df767e08406a` |
| document-history.md | 329672 | `b65742fe7def5be78afc0760211ceac50be9b79fd9e8c51e047d3538cff3a152` |
| prd-review-history.md | 46227 | `5579d8be8a3478efce626d291fa8983ca0ae51b970d07ab9315419bf0c6e292f` |
| technical-inputs-history.md | 22866 | `07cd9b3e6cc7e81a47d3c4fb6ba8e973c33dced2a58813caef9beb82cc59abfd` |
| ui-design-history.md | 29659 | `355ddc7a2bb4cb7e6a4ddf4f998a4d299c38ae861de4b12b9af587d531fa7b5d` |
