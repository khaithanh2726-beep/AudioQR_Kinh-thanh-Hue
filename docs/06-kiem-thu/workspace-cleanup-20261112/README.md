# Dọn thư mục trước bàn giao Git — 12/11/2026

## Kết quả

Đã chuyển tám thư mục `frontend/test-results*` và `frontend/dist` ra ngoài repository; tổng 29 file sinh tự động. Giữ `frontend/src`, `scripts`, `tests` và phụ thuộc local `node_modules`. `frontend/tests` có 15 file nguồn/tài liệu, không phải kết quả tạm. Không xóa mã kiểm thử để giảm số file; giữ cache/build backend đang có và không thao tác database/audio/Docker.

Đã gom **15 bản sao bằng chứng giống hệt bytes** về tám bản chung còn giữ trong hồ sơ gốc. [Bảng đối chiếu](artifact-aliases.json) ghi đường dẫn cũ, đường dẫn chung, kích thước và SHA-256. Liên kết Markdown được cập nhật; sổ Hash liên quan đổi vị trí lưu và giữ `originalPath` cùng SHA cũ của ảnh/JSON. Không coi thao tác đổi liên kết là chạy lại nghiệm thu cũ. Bản gốc metadata trước sửa và tất cả file chuyển đi được giữ ngoài repo, đối chiếu đủ 44 file bằng Hash.

Thư mục lưu trữ: `C:\Users\khait\AppData\Local\AudioQR-Workspace-Cleanup\20261112-927de8cd`. Có `plan.json`, `generated`, `duplicates`, `metadata-before` và `verification`; không chứa database hoặc cấu hình credential được lấy từ runtime. Không reset/clean/stash hoặc xóa đệ quy cây làm việc.

## Giải thích số file

| Chỉ số | Trước dọn | Sau dọn |
|---|---:|---:|
| File Git nhìn thấy, gồm tracked và untracked không bị ignore | 491 | 479 |
| Mục thay đổi trong Source Control trên T01 | 456 | 444 |
| File đã được HEAD T01 theo dõi | 46 | 46 |
| Thư mục kết quả test trong frontend | 8 | 0 |
| Thư mục build dist trong frontend | 1 | 0 |

456 là số mục thay đổi trên T01: 11 file sửa và 445 file chưa được nhánh này theo dõi; không phải 456 file được viết mới. Đã so Git blob với các nhánh task local T02/T03/T05/T07: **208/242 file backend hiện tại khớp hoàn toàn**. Trạng thái U trên T01 không chứng minh file chưa từng commit; đối chiếu này không kiểm lại remote hoặc chứng minh đã tích hợp main. Không dùng số file Source Control làm phần trăm tiến độ hoặc lý do stage toàn cây.

Phần tăng còn lại gồm nguồn frontend, backend mới, migrations/fixture/test và hồ sơ bằng chứng. Đợt này chỉ loại đầu ra tái tạo và bản sao bytes; không gộp các lớp Controller/Service/Repository hoặc xóa ca hồi quy để giảm số đếm.

## Ngăn phát sinh lại

- `frontend/playwright.config.js`: đầu ra tạm mặc định ở `%TEMP%/hueqr-playwright/audioqr-kinh-thanh-hue`; `HUEQR_E2E_OUTPUT` vẫn cho phép chọn thư mục riêng ngoài repo cho các lượt cần giữ/chạy song song.
- `.dockerignore`: loại cả `test-results-*`, hồ sơ `docs` và `.github` khỏi build context. Dockerfile vẫn dùng frontend/backend; logic frontend không đọc docs lúc chạy unit. Giảm dữ liệu thừa gửi builder và tránh đầu ra test làm mất cache COPY frontend.
- Cập nhật hai README frontend về ranh giới mã kiểm thử/kết quả tạm. Hồ sơ nghiệm thu được chọn lọc vào `docs/06-kiem-thu`; không tự đưa credential, dependency hoặc cache vào commit.

## Kiểm đã thực chạy

- **281/281 file nguồn/fixture/kiểm thử và package được bảo vệ khớp Hash**, không thay đổi sau di chuyển. Không gồm cấu hình output và tài liệu được sửa có chủ đích.
- `npm.cmd test`: **65/65**, exit 0.
- `npm.cmd run build -- --outDir <archive>/verification/dist`: đạt, exit 0; build bên ngoài repo để không tạo lại dist trong frontend.
- Playwright `test --list`: resolve được **29 ca trong 8 file E2E**, exit 0; **chỉ liệt kê, không phải 29 ca đã chạy**. Kiểm import config xác nhận output mặc định nằm ngoài repo.
- Kiểm Hash file chuyển đi/bản chung, liên kết của hồ sơ bị ảnh hưởng, staged diff và whitespace. [result.json](result.json) giữ kết quả, số đếm và Hash nguồn trước dọn.

Không chạy lại browser/API/backend full vì không đổi mã runtime hoặc Java; các kết quả ở [mốc ghim](../map-pin-20261111/README.md) giữ nguyên phạm vi. Không dùng computer use hoặc Git ghi; chưa stage/commit/push, sửa issue/Project hoặc tích checklist.
