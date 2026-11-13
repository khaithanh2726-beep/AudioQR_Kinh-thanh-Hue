# Bộ Ngọ Môn tiếng Việt dùng tập dượt

Ngày ghi nhận: 12/11/2026. Trạng thái: chấp thuận sử dụng bộ Ngọ Môn tiếng Việt cho bản tập dượt sau xác nhận rà văn bản, audio và vùng. Phạm vi này không thay nghiệm thu lịch sử/ngôn ngữ toàn bộ đồ án hoặc phê duyệt các cặp còn lại.

## Danh tính bản được áp dụng

| Thành phần | Bản / Hash SHA-256 |
|---|---|
| Địa điểm | Cổng Ngọ Môn — `039d5ff2-da6b-5283-b7ed-5196dd4dcb2e` |
| Văn bản | [Bản vi đã tiếp nhận](thuyet-minh-vi-v01.md#ngo-mon), revision `c424a807-923a-4dce-8072-1ae2820a25dc` |
| Hash văn bản | `d78e71e427aebe1552575ff4f5e6e62c3b27de904ce6d1450c802bf11dd66177` |
| Audio | Revision `b81bb03b-cd89-40cd-8180-e973a201d10c`, gắn đúng revision vi trên |
| MP3 | `b236c64424078f960a7dc164d17294b804fd1ff5ffbb800b1f2f988165011354`, 93.740 byte, 23,424 giây |
| WAV nguồn | `b32706ff255be3f18f5bb26e931855d54a079b45912f58bd57a85694d9fb2afa` |
| Vùng | Vùng Ngọ Môn của [bộ đề xuất](../04-giao-dien/design-assets/hue-touch-regions-proposal.geojson), không gian `WGS84_LON_LAT`, 12 đỉnh |
| Hash vùng theo hợp đồng API | `c27488be06174231be29ab2f158f9125d4ad215fbbcb6edbfa134f33ca8570a0` |

Audio có nguồn từ job Gemini thật `42a9ed2b-9017-4b85-ad86-b36aaadd6a26`, model `gemini-3.8-flash-tts`, voice `Charon`; [bằng chứng kỹ thuật](../06-kiem-thu/release-readiness-20261112/README.md) và [bảo quản sang bản tập dượt](../06-kiem-thu/rehearsal-20261112/README.md) ghi Hash, probe/giải mã và thử phát. Tệp thật giữ trong kho riêng ngoài repository.

## Ghi nhận trên ứng dụng

Tham chiếu hồ sơ cho văn bản/audio: `docs/05-noi-dung/duyet-ngo-mon-tap-duot-20261112.md`. Giới hạn cần lưu: chỉ sử dụng bản Ngọ Môn vi và vùng này cho tập dượt; chưa nghiệm thu toàn bộ 20 cặp hoặc công nhận đây là ranh giới di tích chính thức.

Chỉ áp dụng qua API Admin hiện hành với CSRF, phiên bản, command-window và receipt. Không thay nội dung/hash hoặc gắn audio sang revision mới để vượt gate. Sau khi lưu hai tham chiếu, áp dụng đúng cặp và vùng rồi bật đúng một địa điểm. Khi sửa văn bản, audio hoặc vùng, cần đối chiếu/duyệt đúng bản mới; hồ sơ này tiếp tục chỉ nhận diện bộ trong bảng.

Sáu tệp nháp T08 hiện có giữ nguyên và chưa commit. Hồ sơ này ghi nhận phạm vi đã chốt cho bản tập dượt, không tự tích checklist hoặc thay trạng thái GitHub Project.
