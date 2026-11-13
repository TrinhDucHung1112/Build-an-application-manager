# HƯỚNG DẪN SỬ DỤNG ỨNG DỤNG QUẢN LÝ NHÂN SỰ

Ứng dụng console (dòng lệnh) viết bằng Java, quản lý **phòng ban, nhân viên, chấm công, nghỉ phép, bảo hiểm, thưởng/phạt, bảng lương và báo cáo**. Dữ liệu lưu bằng **file text** trong thư mục `data/`.

---

## 1. Chạy chương trình

### Cách 1: VS Code (khuyên dùng)

1. Mở thư mục `OOP(PRJ)` (hoặc `Build-an-application-manager`) bằng VS Code.
2. Mở file `src/Main.java`.
3. Bấm **F5** (hoặc nút ▶ **Run** ở góc trên bên phải).
4. Thao tác ngay trong cửa sổ **Terminal** bên dưới. Gõ số rồi nhấn **Enter**.

> Lần đầu chạy nếu VS Code hỏi chọn môi trường, chọn **Java**.

### Cách 2: Dòng lệnh

```
cd Build-an-application-manager/src
javac -encoding UTF-8 *.java
java Main
```

Chương trình tự tìm thư mục `data/` dù bạn chạy từ `src`, `Build-an-application-manager` hay `OOP(PRJ)`. Khi khởi động, nếu thấy dòng `da doc soLuongNv = 12` nghĩa là đã nạp xong dữ liệu mẫu.

---

## 2. Cấu trúc thư mục

```
Build-an-application-manager/
├── src/          ← mã nguồn (*.java)
└── data/         ← dữ liệu (file text), tự nạp khi mở chương trình
    ├── phongban.txt
    ├── nhanvien.txt
    ├── chamcong.txt
    ├── nghiphep.txt
    ├── baohiem.txt
    ├── quydinhthuongphat.txt
    ├── thuongphat.txt
    └── bangluong.txt
```

### Dữ liệu mẫu có sẵn

| Dữ liệu | Số lượng | Ghi chú |
|---|---|---|
| Phòng ban | 5 | PB01 Hành chính, PB02 Kỹ thuật, PB03 Kinh doanh, PB04 Kế toán, PB05 Marketing (PB05 chưa có nhân viên) |
| Nhân viên | 12 | NV001-NV006 toàn thời gian, NV007-NV009 bán thời gian, NV010-NV012 hợp đồng. NV012 đã nghỉ việc |
| Chấm công | 216 dòng | Tháng **10/2026**, cho NV001-NV011 |
| Nghỉ phép | 5 đơn | NP005 đang ở trạng thái **Chờ duyệt** để thử chức năng duyệt |
| Bảo hiểm | 20 hồ sơ | Có 1 thẻ BHYT đã hết hạn (NV012) |
| Quy định thưởng/phạt | 3 | QD01 chuyên cần, QD02 tăng ca, QD03 nghỉ không phép |
| Thưởng/phạt | 11 | Đã xét tự động cho tháng 10/2026 và 2 khoản nhập tay |
| Bảng lương | 11 | Tháng 10/2026 (không có NV012 vì đã nghỉ việc) |

---

## 3. Quy ước nhập liệu (đọc trước khi dùng)

| Loại dữ liệu | Cách nhập | Ví dụ |
|---|---|---|
| Chọn menu | Gõ **số** rồi Enter. Nhập sai sẽ được hỏi lại | `2` |
| Ngày | `yyyy-MM-dd` (năm-tháng-ngày, có số 0 đứng trước) | `2026-10-31` |
| Giờ | `HH:mm` (24 giờ) | `08:00`, `17:30` |
| Có/không | `y` hoặc `n` | `y` |
| Số tiền, lương | Gõ số liền, **không** dấu phẩy | `15000000` |
| Hệ số, phụ cấp (số thập phân) | Dùng dấu chấm | `0.3` |
| Mã (NV, PB, CC, BH...) | Không để trống, không trùng mã đã có | `NV013`, `CC217` |
| Tiếng Việt | Nên gõ **không dấu** cho thống nhất với dữ liệu mẫu | `Nguyen Van A` |

Tìm kiếm **không phân biệt hoa thường** và có thể nhập một phần.

---

## 4. Menu chính

```
========== QUAN LY NHAN SU ==========
1. Quan ly phong ban
2. Quan ly nhan vien
3. Quan ly cham cong
4. Quan ly bao hiem
5. Bang luong
6. Bao cao tong hop
7. Quan ly thuong phat
8. Quan ly nghi phep
9. Quan ly quy dinh thuong phat
10. Luu toan bo du lieu
11. Doc du lieu tu file
0. Thoat
```

Trong mọi menu con, chọn **0** để quay lại menu trước.

---

## 5. Hướng dẫn từng chức năng

### 5.1. Quản lý phòng ban (menu 1)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Thêm phòng ban | Nhập mã, tên, mô tả. Mã trùng sẽ báo "Khong the them" |
| 2 | Xem danh sách | In tất cả phòng ban kèm số nhân viên |
| 3 | Tìm phòng ban | Nhập mã phòng ban, hiện thông tin chi tiết |
| 4 | Xóa phòng ban | Nhập mã. **Phòng ban còn nhân viên sẽ không xóa được**, cần chuyển nhân viên sang phòng khác trước |

Ví dụ thử xóa: `PB05` xóa được (chưa có nhân viên), `PB02` bị từ chối.

### 5.2. Quản lý nhân viên (menu 2)

| Số | Chức năng |
|---|---|
| 1 | Thêm 1 nhân viên |
| 2 | Nhập nhiều nhân viên liên tiếp |
| 3 | Xem danh sách |
| 4 | Tìm kiếm (chính xác / gần đúng) |
| 5 | Sửa thông tin nhân viên |
| 6 | Xóa nhân viên |
| 7 | Thống kê theo loại nhân viên |
| 8 | Thống kê lương |
| 9 | Tìm theo phòng ban |
| 10 | Tìm theo khoảng lương |

#### Thêm nhân viên (mục 1)

Các bước chương trình hỏi lần lượt:

1. **Loại NV**: `1` Toàn thời gian, `2` Bán thời gian, `3` Hợp đồng.
2. Mã NV, họ tên, ngày sinh, giới tính, số điện thoại, email.
3. Lương cơ bản, ngày vào làm (không được sau ngày hôm nay), trạng thái (`Dang lam` hoặc `Nghi`).
4. Thông tin riêng theo loại:
   - **Toàn thời gian:** phụ cấp, thâm niên (năm).
   - **Bán thời gian:** số giờ làm trong tháng, lương theo giờ.
   - **Hợp đồng:** thời hạn hợp đồng (ngày), lương theo hợp đồng. Ngày kết thúc được tính tự động từ ngày vào làm.
5. Chương trình hiện danh sách phòng ban, bạn nhập **mã phòng ban** (để trống nếu chưa phân).
6. `Co muon gan chuc vu? (y/n)`: nếu `y` nhập mã chức vụ, tên chức vụ, hệ số phụ cấp chức vụ (ví dụ `0.3`).
   > ⚠️ **Không để trống** mã hoặc tên chức vụ, nếu không chương trình sẽ báo lỗi và dừng.
7. `Co muon them trinh do chuyen mon? (y/n)`: nếu `y` nhập mã trình độ, tên trình độ (Cao dang/Dai hoc/Thac si), chuyên ngành, trường, năm tốt nghiệp, hệ số trình độ. Có thể nhập **nhiều trình độ**, hỏi `Them trinh do khac? (y/n)` sau mỗi lần.

Thành công sẽ báo `Them nhan vien thanh cong.`. Trùng mã sẽ báo thất bại.

#### Tìm kiếm (mục 4)

Nhập **mã, tên, số điện thoại hoặc email** (được nhập một phần). Kết quả **khớp chính xác hiện trước**, khớp gần đúng hiện sau, kèm dòng `tim thay N nhan vien`.

Ví dụ: nhập `NV010` thì NV010 lên đầu; nhập `nguyen` ra mọi người có chữ "nguyen".

#### Sửa nhân viên (mục 5)

1. Nhập mã nhân viên, chương trình hiện thông tin hiện tại.
2. Nhập lại các thông tin cơ bản (họ tên, ngày sinh, giới tính, SĐT, email, lương, ngày vào làm, trạng thái) và mã phòng ban mới (để trống nếu không phân).
3. Sau đó hỏi tiếp: **đổi chức vụ?** (y/n) và **nhập lại trình độ chuyên môn?** (y/n).

> Phần thông tin riêng theo loại (phụ cấp, số giờ làm, hợp đồng) hiện chưa sửa được qua menu này.

#### Xóa nhân viên (mục 6)

Nhập mã nhân viên. Báo `Da xoa.` hoặc `Khong tim thay.`.

#### Thống kê (mục 7, 8)

- **7:** số lượng nhân viên theo từng loại (toàn thời gian, bán thời gian, hợp đồng).
- **8:** tổng lương, lương trung bình, nhân viên lương cao nhất và thấp nhất.

#### Tìm theo phòng ban, theo khoảng lương (mục 9, 10)

- **9:** hiện danh sách phòng ban, nhập mã phòng ban, in nhân viên thuộc phòng đó.
- **10:** nhập `Luong tu` và `Luong den` (ví dụ `20000000` đến `30000000`).

#### Thông tin hiển thị của mỗi nhân viên

Mỗi nhân viên được in kèm: phòng ban, chức vụ, trình độ, và phần **riêng theo loại** (phụ cấp/thâm niên; số giờ/lương giờ; thông tin hợp đồng và hạn hợp đồng).

### 5.3. Quản lý chấm công (menu 3)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Thêm chấm công | Nhập mã chấm công, mã NV, ngày, giờ vào, giờ ra. **Giờ ra phải sau giờ vào** |
| 2 | Xem danh sách | In toàn bộ chấm công |
| 3 | Xóa chấm công | Nhập mã chấm công |
| 4 | Thống kê giờ | Nhập mã NV, tháng, năm. Ra số ngày đi làm, giờ thường, giờ tăng ca |

Quy tắc: mỗi ngày làm tối đa **8 giờ thường**, phần vượt 8 giờ tính là **tăng ca**.

Ví dụ thử: mục 4 với `NV003`, tháng `10`, năm `2026`.

### 5.4. Quản lý bảo hiểm (menu 4)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Thêm BHXH | Nhập mã BH, mã NV, số sổ BHXH. Ngày tham gia lấy là hôm nay |
| 2 | Thêm BHYT | Nhập mã BH, mã NV, số thẻ, nơi khám chữa bệnh, hạn thẻ (`yyyy-MM-dd`) |
| 3 | Xem danh sách | In bảng bảo hiểm |
| 4 | Tìm theo mã NV | Hiện các bảo hiểm của 1 nhân viên |
| 5 | Xóa theo mã BH | Nhập mã bảo hiểm |
| 6 | Tính tiền đóng | Nhập mã NV và mức lương đóng bảo hiểm, ra tiền nhân viên đóng và doanh nghiệp đóng |

Tỷ lệ đóng:

| Loại | Nhân viên đóng | Doanh nghiệp đóng |
|---|---|---|
| BHXH | 8% | 17,5% |
| BHYT | 1,5% | 3% |

Chỉ tính các bảo hiểm có trạng thái **`HieuLuc`**. Hồ sơ `HetHieuLuc` (ví dụ thẻ BHYT của NV012) không được tính.

### 5.5. Bảng lương (menu 5)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Lập bảng lương cho tất cả | Nhập tháng, năm. Tự tính và **tự lưu** |
| 2 | Xem bảng lương theo mã NV | Nhập mã NV |
| 3 | Xem bảng lương công ty theo tháng | Nhập tháng, năm |
| 4 | Thống kê quỹ lương | Nhập tháng, năm. Ra tổng quỹ lương và lương trung bình |

**Công thức:**

```
Tổng lương   = Lương nhân viên + Phụ cấp + Tiền thưởng − Tiền phạt
Thực nhận    = Tổng lương − Tiền bảo hiểm nhân viên đóng
```

- **Lương nhân viên** tùy loại:
  - Toàn thời gian: lương cơ bản + phụ cấp + thâm niên × 200.000
  - Bán thời gian: số giờ làm × lương theo giờ
  - Hợp đồng: lương theo hợp đồng
- **Phụ cấp** = lương cơ bản × (hệ số chức vụ + hệ số các trình độ).
- **Tiền thưởng/phạt** lấy từ danh sách thưởng/phạt của đúng tháng đó.
- **Bảo hiểm** lấy từ các hồ sơ bảo hiểm có trạng thái `HieuLuc` của nhân viên.

> Nên **xét thưởng/phạt (menu 7, mục 6) trước** rồi mới lập bảng lương, để lương có tính thưởng/phạt của tháng.

### 5.6. Báo cáo tổng hợp (menu 6)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Báo cáo toàn công ty | Nhập tháng, năm |
| 2 | Báo cáo theo phòng ban | Chọn mã phòng ban, nhập tháng, năm |
| 3 | Thống kê nhân sự | Tổng nhân viên, số lượng theo loại, theo trạng thái, theo phòng ban, thống kê lương, số bảng lương đã lập |
| 4 | Thống kê bảo hiểm | Tổng số hồ sơ, số hồ sơ BHXH, số hồ sơ BHYT, số thẻ BHYT hết hạn |

> Báo cáo lương chỉ có số liệu khi tháng đó **đã lập bảng lương** (menu 5, mục 1).

### 5.7. Quản lý thưởng/phạt (menu 7)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Thêm thưởng/phạt thủ công | Nhập mã, mã NV, loại (`THUONG` hoặc `PHAT`), lý do, số tiền. Ngày áp dụng là hôm nay |
| 2 | Xem tất cả | In toàn bộ |
| 3 | Xem theo mã NV | Nhập mã NV |
| 4 | Xóa theo mã | Nhập mã thưởng/phạt |
| 5 | Thống kê theo tháng | Nhập mã NV, tháng, năm. Ra tổng thưởng và tổng phạt |
| 6 | **Tự động xét thưởng/phạt** | Nhập tháng, năm. Áp quy định (menu 9) vào dữ liệu chấm công và nghỉ phép, tự tạo khoản thưởng/phạt và tự lưu |

Chạy mục 6 nhiều lần cho cùng một tháng **không bị nhân đôi** khoản thưởng/phạt.

### 5.8. Quản lý nghỉ phép (menu 8)

| Số | Chức năng | Cách dùng |
|---|---|---|
| 1 | Tạo đơn nghỉ phép | Nhập mã đơn, mã NV (phải tồn tại), từ ngày, đến ngày, lý do, loại nghỉ |
| 2 | Xem tất cả đơn | In toàn bộ |
| 3 | Xem đơn theo nhân viên | Nhập mã NV |
| 4 | Duyệt đơn | Nhập mã đơn, trạng thái chuyển thành `Da duyet` |
| 5 | Xóa đơn | Nhập mã đơn |

Loại nghỉ gõ `CO_PHEP` hoặc `KHONG_PHEP`. Đơn mới tạo có trạng thái `Cho duyet`. Ngày kết thúc không được trước ngày bắt đầu.

Thử ngay: duyệt đơn `NP005` (đang chờ duyệt).

### 5.9. Quản lý quy định thưởng/phạt (menu 9)

| Số | Chức năng |
|---|---|
| 1 | Thêm quy định |
| 2 | Xem danh sách quy định |
| 3 | Xóa quy định |

Khi thêm quy định, nhập lần lượt:

| Mục | Giá trị hợp lệ |
|---|---|
| Loại | `THUONG` hoặc `PHAT` |
| Điều kiện | `>=`, `>`, `<=`, `<`, `==` |
| Ngưỡng | số (ví dụ `22`) |
| Mức tiền trên mỗi đơn vị | số tiền (ví dụ `25000`) |
| Đơn vị | `GIO_TANG_CA`, `NGAY_DI_LAM` hoặc `NGAY_NGHI_KHONG_PHEP` |

**Cách hoạt động:** khi xét tự động, chương trình lấy số liệu của nhân viên theo *đơn vị* (số ngày đi làm, số giờ tăng ca, số ngày nghỉ không phép trong tháng). Nếu thỏa *điều kiện so với ngưỡng* thì tạo khoản thưởng/phạt với **số tiền = số đơn vị × mức tiền**.

Ba quy định mẫu:

| Mã | Tên | Điều kiện | Mức tiền |
|---|---|---|---|
| QD01 | Thưởng chuyên cần | Ngày đi làm `>= 22` | 25.000 / ngày |
| QD02 | Thưởng tăng ca | Giờ tăng ca `>= 10` | 50.000 / giờ |
| QD03 | Phạt nghỉ không phép | Ngày nghỉ không phép `>= 1` | 200.000 / ngày |

### 5.10. Lưu và đọc dữ liệu (menu 10, 11, 0)

| Số | Chức năng | Ghi chú |
|---|---|---|
| 10 | **Lưu toàn bộ dữ liệu** | Ghi cả 8 danh sách vào thư mục `data/` |
| 11 | Đọc dữ liệu từ file | Nạp lại từ `data/`, **mọi thay đổi chưa lưu sẽ mất** |
| 0 | Thoát | Hỏi `Luu toan bo du lieu truoc khi thoat? (y/n)` |

> ⚠️ Thao tác thêm, sửa, xóa chỉ nằm trong bộ nhớ. **Phải chọn 10 hoặc trả lời `y` khi thoát** thì mới được ghi vào file. (Riêng *Lập bảng lương* và *Tự động xét thưởng/phạt* tự lưu.)

---

## 6. Quy trình mẫu: làm lương cho một tháng

Ví dụ làm lương tháng **11/2026**:

1. **Thêm chấm công** cho nhân viên (menu 3, mục 1), hoặc ghi sẵn vào `chamcong.txt`.
2. **Tạo đơn nghỉ phép** nếu có và **duyệt** (menu 8, mục 1 rồi 4).
3. Kiểm tra **quy định** thưởng/phạt (menu 9, mục 2). Thêm quy định nếu cần.
4. **Xét thưởng/phạt tự động** (menu 7, mục 6), nhập `11` và `2026`. Xem kết quả bằng mục 2.
5. Thêm **thưởng/phạt thủ công** nếu có (menu 7, mục 1).
6. **Lập bảng lương** (menu 5, mục 1), nhập `11` và `2026`.
7. **Xem bảng lương** (menu 5, mục 3) và **báo cáo** (menu 6, mục 1 hoặc 2).
8. **Lưu** (menu 10).

---

## 7. Kịch bản demo nhanh (gợi ý khi báo cáo)

1. Menu **2 → 3**: xem danh sách nhân viên, chỉ ra thông tin riêng của 3 loại (đa hình).
2. Menu **2 → 4**, nhập `NV010` rồi `nguyen`: so sánh tìm chính xác và gần đúng.
3. Menu **1 → 4**, nhập `PB02`: bị từ chối vì còn nhân viên. Nhập `PB05`: xóa được.
4. Menu **3 → 4**, nhập `NV003`, `10`, `2026`: xem giờ tăng ca.
5. Menu **8 → 4**, nhập `NP005`: duyệt đơn nghỉ phép.
6. Menu **7 → 6**, nhập `10`, `2026`: xét thưởng/phạt tự động.
7. Menu **5 → 1** rồi **5 → 3**, nhập `10`, `2026`: lập và xem bảng lương.
8. Menu **6 → 1**, nhập `10`, `2026`: báo cáo toàn công ty.
9. Menu **10**: lưu dữ liệu, rồi mở file trong `data/` để cho thấy dữ liệu là **file text**.

---

## 8. Định dạng file dữ liệu

Mỗi file là **một đối tượng một dòng**, các trường cách nhau bằng dấu `|`. Dòng bắt đầu bằng `#` là ghi chú, chương trình bỏ qua. Bạn có thể mở bằng Notepad hay VS Code để xem hoặc sửa tay.

Ví dụ `phongban.txt`:

```
# maPB|tenPB|moTa
PB01|HANH CHINH|Quan ly hanh chinh nhan su
```

Ví dụ `chamcong.txt`:

```
# maChamCong|maNV|ngay|gioVao|gioRa
CC001|NV001|2026-10-01|08:00|16:00
```

Ví dụ `nhanvien.txt` (nhân viên toàn thời gian):

```
TTG|NV001|Nguyen Van An|1985-03-12|Nam|0901234501|an.nguyen@congty.vn|20000000|2015-06-01|Dang lam|PB01|CV01;GIAM DOC;0.5|TD02;THAC SI;QUAN TRI KINH DOANH;DAI HOC KINH TE TPHCM;2012;0.2|3000000|11
```

Mã đầu dòng: `TTG` (toàn thời gian), `BTG` (bán thời gian), `HD` (hợp đồng). Chức vụ viết dạng `maCV;tenCV;heSo`; nhiều trình độ cách nhau bằng dấu `~`.

Nếu sửa tay: giữ đúng số cột, không gõ thêm dấu `|` trong nội dung. Dòng nào sai định dạng sẽ bị bỏ qua và chương trình in `Bo qua dong ... loi`.

> Mỗi lần **Lưu**, chương trình ghi đè các file và rút gọn dòng ghi chú `#` ở đầu file. Nên sao lưu thư mục `data/` trước buổi demo.

---

## 9. Lỗi thường gặp

| Hiện tượng | Nguyên nhân và cách xử lý |
|---|---|
| Khởi động báo `Chua co du lieu nhan vien ... bat dau moi` | Không tìm thấy thư mục `data/` có file. Kiểm tra thư mục `data` nằm cạnh `src` |
| Báo cáo hoặc bảng lương trống | Tháng đó chưa lập bảng lương. Vào menu 5, mục 1 |
| `Them that bai` | Mã bị trùng, hoặc danh sách nhân viên đã đầy (tối đa 100 người) |
| `Nhap ngay theo yyyy-MM-dd` | Sai định dạng ngày. Gõ đủ 4 số năm, 2 số tháng, 2 số ngày, ví dụ `2026-03-05` |
| `Nhap gio theo HH:mm` | Sai định dạng giờ, ví dụ `08:00` |
| `Nhan vien khong ton tai` | Mã NV chưa có. Xem lại ở menu 2, mục 3 |
| `Khong xoa duoc phong ban` | Phòng ban còn nhân viên. Chuyển nhân viên sang phòng khác rồi xóa |
| Chương trình dừng khi thêm nhân viên có chức vụ | Để trống mã hoặc tên chức vụ. Nhập đủ cả hai |
| Mất dữ liệu vừa nhập | Chưa lưu. Dùng menu 10, hoặc trả lời `y` khi thoát. Không dùng menu 11 khi chưa lưu |
| Chữ tiếng Việt bị lỗi font ở menu bảo hiểm | Chỉnh terminal sang UTF-8 hoặc chạy `java -Dstdout.encoding=UTF-8 Main` |
| Tìm kiếm không ra kết quả | Kiểm tra lại từ khóa. Có thể nhập một phần mã, tên, SĐT hoặc email |

---

## 10. Tóm tắt thông tin kỹ thuật (để trả lời khi vấn đáp)

- **Lớp trung tâm và kế thừa:** `NhanVien` (abstract) với 3 lớp con `NhanVienToanThoiGian`, `NhanVienBanThoiGian`, `NhanVienHopDong`. `BaoHiem` (abstract) với `BaoHiemXaHoi`, `BaoHiemYTe`.
- **Đa hình:** `tinhLuong()`, `hienThiThongTin()` (nhân viên); `tinhTienNhanVienDong()`, `tinhTienDoanhNghiepDong()` (bảo hiểm).
- **Lớp mảng đối tượng:** `QuanLyNhanSu` và các lớp `DanhSach...` (phòng ban, chấm công, nghỉ phép, bảo hiểm, thưởng/phạt, quy định, bảng lương).
- **Interface:** `IThongTin` (`hienThiThongTin()`), `IQuanLyDuLieu` (`docFile()`, `ghiFile()`).
- **Static:** `TepVanBan` (đọc/ghi text dùng chung), `DuongDanDuLieu.file()`, biến đếm `NhanVien.soLuongNV`.
- **Đọc/ghi file:** file text, mỗi dòng một đối tượng, tách trường bằng `|`.
