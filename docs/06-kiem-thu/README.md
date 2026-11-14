# Báo cáo kiểm thử và giới hạn nghiệm thu

Trạng thái mới nhất của bộ 20 cặp nằm tại mục 8; các mốc trước giữ kết quả đúng thời điểm thực chạy.

## 1. Phạm vi báo cáo

Báo cáo tổng hợp các lượt đã thực thi cho nguồn main `601c09a`, CD T10 `134ac0d` và mốc bổ sung vận hành/nghiệm thu ngày 14/11 được phân nhóm commit theo G02. Biên tập tài liệu tự nó không tạo kết quả kiểm; các lượt thực chạy mới được ghi riêng theo mốc. Log, XML, ảnh, JSON và các lần thử lỗi/chạy lại giữ trong hồ sơ nội bộ cạnh dự án; bộ chấm giữ phần tổng hợp.

Mã kiểm nằm trong `backend/src/test` và `frontend/tests`. Các bằng chứng đã được commit trước đợt biên tập có thể tra tại [nguồn theo phiên bản](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/tree/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu).

## 2. Các kết quả chính

| Nhóm | Kết quả đã ghi nhận | Giới hạn |
|---|---|---|
| Backend | Một lượt full V14 530/530, 0 failure/error/skipped; 51 suite. CI của nguồn tổng hợp và PR #15 đạt | Không coi test dữ liệu kỹ thuật là duyệt nội dung T08 |
| Frontend | 71/71 logic; Vue build đạt; kiểm Chrome/API/MP3 riêng | Không thay ma trận thiết bị thật hoặc mọi ca lỗi |
| Bản đồ/UI | Kiểm chọn trong polygon, ghim đúng điểm nhấn, chồng lấn/Hủy, kéo/phóng, nhiều viewport và lỗi audio | Một số ca dùng tile/API fixture; không xác nhận toàn nền OSM/mạng thật từ fixture |
| Admin | Q2/reload/401, vùng/cặp, thống kê/filter, provider và nghe thử MP3 có các lượt kiểm thật | Chưa nghiệm thu mọi trạng thái trên toàn bộ màn Admin |
| TTS | Một audio Ngọ Môn vi trước đó cùng 19 audio mới vi/en/fr/ru; probe/giải mã/Hash đạt, đủ 20 kỹ thuật | Đã có hồ sơ chủ đồ án duyệt dùng học phần; giữ giới hạn phát âm/thẩm định chuyên gia |
| HTTPS/QR | Chrome 2/2 trên bộ Ngọ Môn; QR PNG 512×512, URL/read-only, print request/CSS | Không tính request in là đã in giấy hoặc BarcodeDetector là đã giải mã |
| Thiết bị | Người kiểm báo nghe Ngọ Môn trên Android và chạy ổn trên iPhone | Chưa ghi đủ mẫu máy/OS/browser/mạng và từng ca lỗi/time/tab |
| Vận hành V14 | Gate/drain, backup/restore, chuyển V14 và CD thật; lịch backup Windows đã chạy thử, restore đạt; log giới hạn/xoay đã kiểm riêng | Theo dõi trigger hằng ngày, review/claim sau crash và giới hạn TTL log hạ tầng |
| Manifest backup | 8/8 ca V14 đạt khi chạy riêng; workflow CI còn có bộ nền 5 ca | Không cộng hai bộ hoặc các lần chạy thành một lượt full |

## 3. Nguồn kết quả để đối chiếu

Lượt kiểm vận hành bổ sung sau mốc tài liệu `20cd3ec`: bộ kiểm tính toàn vẹn backup đạt 8/8; backup nội dung V14 và restore vào project mới đạt 30/30 tệp, Hash nguồn/đích khớp, giữ số lượng nghiệp vụ 5 điểm/20 slot/16 text revision/15 audio revision/0 cặp/30 file. 20 bài trong bundle không đồng nghĩa đã lưu 20 revision vào database. Gate khởi động đóng, epoch mới và worker TTS tắt theo công cụ restore.

Scheduler trên bản phục hồi giữ nguyên dữ liệu khi gate đóng qua 75 giây; khi mở đã dọn ba phiên fixture quá hạn và file fixture đủ hạn, giữ phiên fixture còn hạn cùng timestamp gốc, giữ file từng áp dụng mới ngừng bảy giờ và toàn bộ 30 Hash audio thật. Cấu hình worker được trả về tắt, container thử dừng sau kiểm. Lượt kiểm 30 tệp này không gọi provider hoặc thay bản phục vụ; bước snapshot/nâng V13 thực hiện riêng ở mốc bổ sung bên dưới. Bằng chứng bổ sung được giữ tại hồ sơ nội bộ `van-hanh-20261113-dcc5f86a`, không cộng vào lượt full 530 ca hoặc tự tích thêm checklist.

Lượt 14/11 bổ sung: snapshot V13 Ngọ Môn hai file đã kiểm và restore bằng đúng ảnh V13; diễn tập nâng V13→V14 trên bản restore giữ inventory/vùng/Hash, phiên cũ và khóa/epoch, đọc văn bản/tải MP3 93.740 byte khớp Hash, gate close/freeze/resume đạt. Bản phục vụ sau đó đã chuyển V14, backup/restore lại hai file, giữ volume và cặp; bật cleanup/retention, provider/converter tắt. Local/API và HTTPS csrf trả 200; chưa có lượt thiết bị thật mới sau cập nhật. Bằng chứng riêng tại hồ sơ nội bộ vận hành 14/11; không gộp các lượt thành full backend mới.

Kiểm cập nhật ảnh trên restore riêng đạt cả nhánh thành công và nhánh quay ảnh cũ: ảnh có tùy chọn Java sai không khởi động, công cụ trả FAILED nhưng khôi phục ảnh cũ/health/gate RUNNING khi schema không đổi, cấu hình và metadata file giữ nguyên. Tag gây lỗi được gỡ sau kiểm. Đây là fault injection có lỗi dự kiến; không là bằng chứng ảnh lỗi đã phát hành thành công. Bộ Node cục bộ mới đạt 20/20 (5 manifest, 12 integrity, 3 runtime-env), không chạy lại full backend cục bộ trong lượt này.

Đường CD đã kiểm SSH bằng khóa, host key ghim, đúng tài khoản Windows và Docker context desktop-linux; firewall chỉ dải Tailscale. Truyền ảnh/bộ công cụ bằng SFTP và chạy receiver trên restore riêng đạt kiểm Hash tar/JAR/công cụ, backup, đổi ảnh, gate/health và giữ volume/khóa/epoch; không thay bản phục vụ. Lượt này dùng ảnh ứng dụng nền 20cd3ec và bộ công cụ đang phát triển, không là một lượt GitHub runner. Cú pháp YAML, ba script Bash và hai script PowerShell được sinh từ workflow đã kiểm; kết quả CD thật được ghi ở đoạn kế tiếp.

[CD thật 43486032664](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43486032664) trên nguồn `134ac0d` đã hoàn tất thành công cả ba job: frontend (71 logic + 5 manifest + 12 integrity + 3 runtime-env và build), backend (530/530, 0 failure/error/skipped), deploy. Job deploy build ảnh gắn SHA, kiểm Hash tar/JAR/công cụ, truyền qua Tailscale/SSH và backup trước đổi ảnh. Sau run, đọc target xác nhận ảnh `sha256:83ca2bf36ef2801a2f68efddda0ecfe049ebdc023960f92b23489840002773e4`, schema 14, inventory/vùng/cặp, hai volume, khóa/epoch và hai file audio thật không đổi; local/HTTPS 200. Chưa kiểm lại phát âm/thời lượng nghe trên thiết bị thật sau chuyển. Log và receipt ở kho kiểm riêng `real-cd-20261114-43486032664`; không đưa private key, OAuth secret hoặc dump vào hồ sơ chấm.

- [CI nguồn tổng hợp: 530 ca backend và frontend/build](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43402780832).
- [CI của PR #15](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43406790912).
- [Full V14 và 51 suite](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/result.json), [bằng chứng vận hành](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/operations-guard-20261113/README.md).
- [Chrome/API/MP3 và reload/audio lỗi](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/local-20261111/README.md), [kiểm ghim/bản đồ](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/map-pin-20261111/README.md).
- [TTS thật/Compose](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/release-readiness-20261112/README.md), [HTTPS/QR/Android/iPhone](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/https-ngo-mon-20261112/README.md).
- [Đối chiếu 14 audio mới](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/blob/601c09acd34e45d3d0b17bf2b7dbe94daf6bdae1/docs/06-kiem-thu/content-review-20261113/result.json).

Lượt đầu CI `791c581` lỗi tải Maven trước compile; chạy lại job lỗi đạt, không đổi phiên bản. Các lượt bị hủy vì trùng SHA không được tính là kiểm đạt độc lập. Lượt 522 cũ có một fixture sửa/rerun và ledger riêng; không gọi là một full 522/522. Các kết quả này được phân biệt trong hồ sơ gốc.

## 4. Đối chiếu checklist

Đợt rà đủ 89 ô xác nhận **62 ô đủ toàn điều kiện**, còn **27 ô**. GitHub trước rà 28 ô, thêm 34 ô; local trước rà 23 ô và đã được đồng bộ. Chỉ cập nhật checkbox, không đổi trạng thái issue/assignee/Project. Các ô xây chức năng có chứng cứ được phân biệt với ô nghiệm thu toàn bộ thiết bị/dữ liệu/vận hành.

| Task | Đã tích / tổng |
|---|---:|
| T01 | 12/18 |
| T02 | 4/5 |
| T03 | 6/6 |
| T04 | 5/7 |
| T05 | 9/9 |
| T06 | 7/8 |
| T07 | 7/9 |
| T08 | 2/7 |
| T09 | 3/6 |
| T10 | 5/9 |
| T11 | 2/5 |

Chi tiết tiêu chí tại [roadmap](../01-tong-quan/roadmap.md). Bản đọc lại GitHub 11 issue cùng lý do từng ô đã được bảo quản trong hồ sơ kiểm riêng; việc di chuyển bằng chứng không làm thay đổi kết quả rà hoặc tạo lượt test mới.

## 5. Điều kiện chưa đạt

1. Đủ 20 cặp đã được duyệt dùng học phần và áp dụng; xem kết quả xuất bản tại mục 8, giữ giới hạn hồ sơ nội dung.
2. Ma trận FR/UC/AC/NFR→ca→bằng chứng, các ca mạng/time/tab/thiết bị và toàn trạng thái Admin.
3. Theo dõi trigger backup 23:00 và mục review, giới hạn log hạ tầng và xử lý crash/claim thiếu bằng chứng owner. CD-L01/Tailscale/OpenSSH/Docker đã có lượt GitHub thực thi đạt.
4. Chuyển phục vụ, kiểm tái lập môi trường cuối và người nhận thực hiện lại bộ hướng dẫn.

Không kết luận nghiệm thu chính thức toàn đồ án từ mốc lưu mã nguồn hoặc CI xanh. [Hiện trạng bàn giao](../01-tong-quan/handoff.md) ghi việc tiếp theo.

<a id="ops-scheduled-result"></a>

## 6. Backup định kỳ, log và smoke sau triển khai — 14/11

| Phép kiểm thực chạy | Kết quả |
|---|---|
| Node manifest/integrity/runtime-env/retention | 29/29: 5 + 12 + 3 + 9. Nhóm mới có boundary bảy ngày, Hash hỏng, bản dở, tệp lạ và junction; clock chỉ được truyền vào fixture, không đổi giờ hệ điều hành. |
| Backup/restore trên bản riêng | Chu trình backup→mở lại→restore đạt, hai Hash audio được kiểm ở volume đích; giữ catalog/nghiệp vụ, đổi khóa/epoch khi restore; container thử dừng. |
| Tranh chấp/lỗi lịch | Chặn lượt chạy chồng; không tiếp quản hoặc tự mở gate do owner khác giữ; không tự bật ứng dụng đã dừng. Không thử phá tiến trình trên bản phục vụ để mô phỏng crash. |
| Windows Task Scheduler | Task đăng ký 23:00 hằng ngày, Interactive/Limited, không chạy bù; kích hoạt bằng Start-ScheduledTask trả mã 0. Backup hai file, giữ ba bản trước còn hạn, HTTP mở lại; gate đóng đo 110.157 giây. Chưa quan sát trigger 23:00 tự chạy. |
| Restore backup của task thật | RESTORE_VERIFIED_CLOSED, schema/volume riêng, đúng ảnh nguồn, hai file đủ Hash tại đích; thu hồi quyền cũ, retention theo mốc gốc, không public; container dừng sau kiểm. |
| Áp cấu hình log lên bản phục vụ | Cả app/MySQL dùng local/10m/3, giữ cùng ID ảnh/volume/schema/khóa; có backup trước recreate, HTTP 200 sau resume. |
| Xoay log thật trong container riêng | Sinh 45.000 dòng; còn 25.694 dòng, 25.899.551 bytes; dòng đầu đã bị xoay, dòng cuối còn. Không mạng/volume; xóa đúng container thử sau kiểm. Các lượt lỗi trước do cú pháp chương trình sinh log được giữ riêng, không phải kết quả đạt. |
| API trên bản phục vụ | Chín nhóm đạt: Admin login/session; vùng/cặp; start/replay; map/text; MP3 trọn/Hash; ACK/replay; sai credential; Range; audio sau kết thúc. MP3 93.740 bytes; một phiên và một lượt xem kiểm được tạo, phiên đã kết thúc. |
| HTTPS | URL hiện hành trả 200 sau khi tunnel mở lại. Không thay xác nhận nghe thật trên thiết bị; lượt này chưa có kết quả điện thoại mới. |

Báo cáo chạy lịch chỉ chứa trường vận hành, giữ bảy ngày. Dọn chỉ nhận đúng tên/loại, file lạ/hỏng/link được giữ để rà; backup hết hạn chỉ được dọn sau khi bản mới còn hạn/đủ Hash. Buffer Docker có giới hạn dung lượng, không tự có TTL theo tuổi bảy ngày. Không chạy lại full backend/frontend hoặc gọi Gemini trong đợt này; số 530/71 là bằng chứng mốc trước.

Mã kiểm ở `backend/src/test/fixtures/operations`; bộ công cụ ở `deploy`. Bằng chứng tổng hợp, snapshot 11 issue và Hash nguồn được bảo quản trong hồ sơ nội bộ `van-hanh-tu-dong-20261114`; dump/khóa giữ ở kho runtime có ACL, không đưa vào bộ chấm.

<a id="acceptance-final-result"></a>

## 7. Mốc lưu nghiệm thu 14/11 — Chrome, nội dung và truy vết

[CI nguồn 3f66b72, run 43592287460](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43592287460) hoàn tất thành công: backend 530/530 (0 failure/error/skipped), frontend 71/71, vận hành 29/29 và Vue build. Đây là lượt full riêng trên Linux; 11 ca Chrome bên dưới chạy trên Windows riêng. Không cộng các lượt thành một full lớn hơn. Deploy bị bỏ qua đúng điều kiện của push, không phát sinh lượt chuyển bản phục vụ.

- Chrome 157.0.8103.78, Windows 10.0.26300, Node v24.22.2: **11/11 ca đạt** trên restore riêng, backend thật.
- Tab độc lập, Duplicate không chiếm phiên, reload cùng id/pause/không view mới; response start/selection bị ngắt sau commit vẫn cùng key và không tạo trùng.
- Lỗi tải audio giữ văn bản, retry không thêm view; lỗi API khi reload rồi hồi mạng giữ cùng phiên. Cảnh báo/hết hạn/ngoại lệ nghe tiếp dùng timestamp của chính phiên fixture và clock browser; không đổi giờ máy/hạn runtime hoặc gọi đây là chờ 8 giờ thật.
- Q2 văn bản qua reload/401 được giữ đúng tài khoản, không tự lưu/apply; 401 là fault injection, không gọi đây là chờ Admin đủ 30 phút. Q3 server có MutableClock trong AdminAuthApiTests ở lượt full trước. Thống kê đọc API thật và ghi nhãn khi filter đổi.
- Kill/restart đúng container thử khi gate QUIESCING: gate còn, lease fixture cũ chưa rõ owner không tự bị xóa, freeze bị từ chối; chỉ tháo lease do ca kiểm tạo rồi mở lại. Khóa/epoch giữ nguyên. Không suy mọi reconcile/claim sau crash đã đạt.
- Lượt thử trước khi bổ sung readiness chờ app có 10 ca lỗi kết nối khởi động; sau sửa bộ kiểm chạy lại đủ 11/11. Giữ cả kết quả lỗi và kết quả cuối riêng, không tính lượt lỗi là đạt. Các container kiểm đã dừng.
- Năm bài ru thật tạo thành công sau một Retry được đối chiếu; 19 bài mới đều probe/giải mã đầy đủ, transcript đủ ngôn ngữ/đầu-cuối. Đủ 20 audio kỹ thuật; 18 bài mới khớp ý/số liệu, Thái Hòa fr cần nghe thuật ngữ; model đối chiếu thứ hai trả 503. Chưa ghi nhận nghe duyệt người cho 19 bài mới.
- Backup nội dung mới có 40 WAV/MP3, restore riêng đạt 40/40 Hash ở đích, 20 text/20 audio revision/0 cặp mới áp dụng. Bản phục vụ vẫn Ngọ Môn vi, nội dung không bị thay ngầm; provider/converter kho nội dung đã tắt, khóa bỏ khỏi cấu hình thử.
- Ma trận truy vết đủ **23 FR/14 UC/50 AC/6 NFR**; đối chiếu source index 82 tệp với marker/dòng. [Ma trận chi tiết](ma-tran-truy-vet.md) tách bằng chứng kỹ thuật và điều kiện chưa khép.

Kiểm mẫu máy/OS/browser thật là lượt sau mốc lưu nguồn; thông tin xác nhận Android/iPhone trước đây không được thay bằng model/version suy đoán. Checklist thực tế giữ 62/89, các đề xuất bổ sung nằm trên roadmap; chưa sửa issue/Project. Log/JSON/ảnh/source Hash nằm trong hồ sơ nội bộ `nghiem-thu-20261114`, không đưa dump, khóa hoặc audio vào Git.


<a id="published-content-result"></a>

## 8. Xuất bản đủ 20 cặp và kiểm vận hành

- Chủ đồ án duyệt 19 cặp v02 mới cho học phần, gồm ghi chú Thái Hòa fr; [hồ sơ revision/Hash](../05-noi-dung/duyet-19-cap-v02-20261114.json) không ghi tự động thành bằng chứng đã nghe trọn hoặc chứng nhận bản ngữ.
- Thử nhập trên bản sao trước, rồi backup/freeze bản phục vụ và nhập thêm 19 text/19 audio revision, 38 WAV/MP3 cùng dấu vết job/instance đã dừng. Khóa ngoại luôn bật, transaction kiểm slot trống; Hash dữ liệu cũ và cấu hình runtime giữ nguyên. Các lần thử đầu dừng trước ghi dữ liệu được lưu riêng, không tính là đạt.
- Bốn vùng được lưu, đọc lại Hash và áp dụng; 19 review/cặp được ghi bằng API Admin/ETag/receipt, sau đó bật bốn điểm. Giữ nguyên cặp Ngọ Môn vi. Không đổi API, không thêm phản hồi TTS giả, không gọi provider trong lượt này.
- Bản sao và bản phục vụ đều đạt 20/20 văn bản/MP3 thật đúng Hash, năm điểm/bốn tiếng, replay cùng key giữ opening. Bộ kiểm tạo/kết thúc bốn phiên kỹ thuật ở mỗi môi trường, không gửi view-ack vì không hiển thị văn bản; không dùng kết quả này làm bằng chứng nghe trên thiết bị. Local/HTTPS Khách/Admin/API sau xuất bản đều 200.
- Trigger một lần tự chạy lúc 20:18:59 ngày 14/11 đạt LastTaskResult=0, backup đủ 40 tệp và mở lại HTTP. Khoảng đóng gate đo được 293.391 giây, không phải cam kết RTO. Trigger thử đã gỡ, lịch 23:00 giữ nguyên; chưa gọi đây là đã quan sát lượt 23:00. Chính backup này được restore sang project/schema/volume riêng: 40/40 Hash tại đích, 20 cặp đang áp dụng, năm điểm hiển thị, epoch mới, retention theo mốc gốc, gate FROZEN; hai container thử đã dừng.
- [CI 1bab9a6, run 43598895644](https://github.com/khaithanh2726-beep/AudioQR_Kinh-thanh-Hue/actions/runs/43598895644) đạt 530/530 backend (0 lỗi/bỏ qua), 71/71 frontend, 33/33 ca vận hành (5+12+3+9+4) và Vue build. Bốn ca mới kiểm receipt khi mất response/conflict/không cấp được command window; deploy skipped theo điều kiện push. Không cộng API/Chrome riêng vào lượt full này.

Bằng chứng chọn lọc giữ tại hồ sơ nội bộ phat-hanh-20-cap-20261114; SQL/ledger chi tiết và runtime có ACL nằm ngoài Git. Kiểm mẫu máy/OS/browser và lượt người nhận thực hiện hướng dẫn là bước cuối. Checklist giữ 62/89, chưa sửa issue/Project.
