# HƯỚNG DẪN SỬ DỤNG ỨNG DỤNG QUẢN LÝ NHÂN SỰ

Ứng dụng console (dòng lệnh) viết bằng Java, quản lý **phòng ban, nhân viên, chấm công, nghỉ phép, bảo hiểm, thưởng/phạt, bảng lương, báo cáo** và có **đăng nhập, phân quyền theo chức vụ**. Dữ liệu lưu bằng **file text** trong thư mục `data/`.

Mọi thông tin hiển thị dưới dạng **bảng có cột STT**, và mỗi lần nhập hoặc xuất đều nằm trong một khung `-----` (xem mục 4).

## Mục lục

1. [Chạy chương trình](#1-chạy-chương-trình)
2. [Dữ liệu mẫu có sẵn](#2-dữ-liệu-mẫu-có-sẵn)
3. [Đăng nhập và đăng ký](#3-đăng-nhập-và-đăng-ký)
4. [Cách đọc giao diện](#4-cách-đọc-giao-diện)
5. [Quy ước nhập liệu](#5-quy-ước-nhập-liệu)
6. [Menu theo vai trò](#6-menu-theo-vai-trò)
7. [Hướng dẫn từng chức năng](#7-hướng-dẫn-từng-chức-năng)
8. [Các quy trình thường dùng](#8-các-quy-trình-thường-dùng)
9. [Lưu và đọc dữ liệu](#9-lưu-và-đọc-dữ-liệu)
10. [Câu hỏi thường gặp](#10-câu-hỏi-thường-gặp)

---

## 1. Chạy chương trình

Yêu cầu: **JDK 11 trở lên**.

### Cách 1: VS Code (khuyên dùng)

1. Mở thư mục `OOP(PRJ)` (hoặc `Build-an-application-manager`) bằng VS Code.
2. Mở file `src/Main.java`.
3. Bấm **F5** (hoặc nút ▶ **Run** ở góc trên bên phải).
4. Thao tác trong cửa sổ **Terminal** bên dưới: gõ số rồi nhấn **Enter**.

> Lần đầu chạy nếu VS Code hỏi chọn môi trường, chọn **Java**.

### Cách 2: Dòng lệnh

```
cd Build-an-application-manager/src
javac -encoding UTF-8 *.java
java Main
```

Chương trình tự tìm thư mục `data/` dù bạn chạy từ `src`, `Build-an-application-manager` hay `OOP(PRJ)`.

Khi khởi động sẽ thấy khung `KHOI DONG - NAP DU LIEU` báo số nhân viên, bảng lương, nhật ký, tài khoản đã đọc từ file, kết thúc bằng `Nap du lieu xong.` Sau đó hiện màn hình đăng nhập.

> **Cửa sổ Terminal nên rộng ít nhất khoảng 110 ký tự.** Nếu hẹp hơn, các dòng `-----` và bảng sẽ bị gãy dòng. Xem mục 10.

---

## 2. Dữ liệu mẫu có sẵn

| Dữ liệu | Số lượng | Ghi chú |
|---|---|---|
| Phòng ban | 11 | `PB01` Hành chính, `PB02` Kỹ thuật, `PB03` Kinh doanh, `PB04` Kế toán, `PB05` Marketing, `PB06` Nhân sự, `PB07` Pháp chế, `PB08` Cung ứng, `PB09` Chăm sóc khách hàng, `PB10` Nghiên cứu phát triển, `PB11` Đào tạo |
| Nhân viên | 45 | 3 giám đốc (`GD`), 11 trưởng phòng (`TP`), 6 phó phòng (`PP`), 12 toàn thời gian (`NVT`), 6 bán thời gian (`NVB`), 7 hợp đồng (`NVH`). `NVH003` đã nghỉ việc |
| Tài khoản | 45 | Mỗi nhân viên một tài khoản, tên đăng nhập là **mã nhân viên** |
| Chấm công | hàng nghìn dòng | Từ năm 2014 đến hiện tại |
| Nghỉ phép | 83 đơn | Có đơn đang **Chờ duyệt** để thử chức năng duyệt |
| Bảo hiểm | 88 hồ sơ | Gồm BHXH và BHYT |
| Quy định thưởng/phạt | 3 | `QD01` chuyên cần, `QD02` tăng ca, `QD03` nghỉ không phép |
| Thưởng/phạt, bảng lương | 255 và 356 dòng | Đã có sẵn nhiều tháng |
| Yêu cầu chỉnh sửa hồ sơ | 4 | 3 yêu cầu đang chờ duyệt, 1 đã bị từ chối |

---

## 3. Đăng nhập và đăng ký

Khi mở chương trình, bạn thấy:

```
---------------- QUAN LY NHAN SU - DANG NHAP ----------------
  1. Dang nhap
  2. Dang ky tai khoan moi
  0. Thoat
```

### 3.1. Tài khoản và mật khẩu

- **Tên đăng nhập = mã nhân viên** (ví dụ `TP002`, `NVT003`).
- **Mật khẩu mặc định** = phần số + phần chữ của mã. Ví dụ `TP002` → `002TP`, `NVT003` → `003NVT`, `GD001` → `001GD`.
- Lần đăng nhập đầu tiên (hoặc sau khi được đặt lại mật khẩu), chương trình **bắt buộc đổi mật khẩu**. Mật khẩu mới dài **ít nhất 6 ký tự**, nhập hai lần giống nhau.
- Nhập sai quá **3 lần** thì quay về màn hình đầu. Sai mật khẩu quá **5 lần** thì tài khoản bị **khóa**, cần giám đốc mở khóa.
- Nhân viên đã **nghỉ việc** không đăng nhập được.

Để thử nhanh, đăng nhập một tài khoản chưa đổi mật khẩu, ví dụ:

| Tài khoản | Mật khẩu mặc định | Vai trò |
|---|---|---|
| `TP002` | `002TP` | Trưởng phòng (Kinh doanh) |
| `PP002` | `002PP` | Phó phòng (Kinh doanh) |
| `NVT002` | `002NVT` | Nhân viên toàn thời gian |
| `NVB002` | `002NVB` | Nhân viên bán thời gian |
| `NVH002` | `002NVH` | Nhân viên hợp đồng |

Các tài khoản đã đổi mật khẩu (như giám đốc) dùng mật khẩu riêng của nhóm. Nếu quên, giám đốc khác vào **Quản lý tài khoản → Đặt lại mật khẩu về mặc định**.

Sau khi đăng nhập thành công, chương trình chào bạn kèm vai trò và có thể hiện thêm cảnh báo:
- Có hợp đồng lao động **sắp hết hạn trong 30 ngày**.
- Có **yêu cầu chỉnh sửa hồ sơ** đang chờ bạn duyệt.

### 3.2. Đăng ký tài khoản mới

Chọn **2. Dang ky tai khoan moi**, rồi nhập: loại nhân viên (1 toàn thời gian, 2 bán thời gian, 3 hợp đồng), họ tên, ngày sinh, giới tính, số điện thoại, email.

- Mã nhân viên được **cấp tự động**, chức vụ mặc định là Nhân viên.
- Chương trình in ra mã, mật khẩu tạm.
- Tài khoản ở trạng thái **CHO DUYET**: phải được **giám đốc duyệt** (và xếp phòng ban) mới đăng nhập được.

### 3.3. Đăng xuất và thoát

- Chọn `0` trong menu chính để **đăng xuất**; chương trình hỏi có lưu dữ liệu trước khi đăng xuất không (`y`/`n`).
- Ở màn hình đăng nhập chọn `0` để **thoát chương trình**.

---

## 4. Cách đọc giao diện

### 4.1. Khung `-----`

Mỗi lần chương trình **hỏi bạn nhập** hoặc **in kết quả**, nội dung nằm giữa hai dòng gạch. Dòng gạch phía trên có tên chức năng đang chạy, nên luôn biết mình đang ở đâu. Gặp dòng gạch dưới cùng nghĩa là chức năng đã xong và bạn quay về menu.

```
---------------------- PHONG BAN - TIM THEO MA ----------------------
  Ma phong ban: PB02                      ← bạn nhập ở đây
  +-----+-------+---------------+---------+
  | STT | MA PB | TEN PHONG BAN |  SO NV  |
  +-----+-------+---------------+---------+
  |  1  | PB02  | KY THUAT      |       5 |
  +-----+-------+---------------+---------+
---------------------------------------------------------------------
```

(Ví dụ đã rút gọn cho dễ đọc. Khung thật rộng 110 ký tự.)

### 4.2. Bảng

- Cột **STT** tự đánh số 1, 2, 3...
- **Chữ canh trái, số và tiền canh phải.**
- Ô nào có nội dung dài sẽ **tự xuống dòng ngay trong ô đó**.
- Danh sách dài được **chia trang 15 dòng**. Chọn số thứ tự (STT) để xem chi tiết hoặc xử lý dòng đó, nhập `0` để quay lại.
- Cột **LƯƠNG** chỉ hiện số tiền với người bạn **được phép xem lương**; còn lại hiện `***`.

### 4.3. Thông báo lỗi

Nhập sai hoặc vượt quyền thì chương trình in một dòng thông báo trong khung rồi **quay về menu**, không bị dừng, dữ liệu đang có không bị mất. Thao tác vượt quyền còn được ghi vào nhật ký hệ thống.

---

## 5. Quy ước nhập liệu

| Loại dữ liệu | Cách nhập | Ví dụ |
|---|---|---|
| Chọn menu | Gõ **số** rồi Enter. Nhập sai sẽ được hỏi lại | `2` |
| Ngày | `yyyy-MM-dd` (năm-tháng-ngày, có số 0 đứng trước) | `2026-10-31` |
| Giờ | `HH:mm` (24 giờ) | `08:00`, `17:30` |
| Có/không | `y` hoặc `n` | `y` |
| Số tiền, lương | Gõ số liền, **không** dấu phẩy | `15000000` |
| Số thập phân | Dùng dấu chấm | `0.3` |
| Loại nghỉ phép | `CO_PHEP` hoặc `KHONG_PHEP` | `CO_PHEP` |
| Quay lại, hủy | Nhập `0` (nhiều màn hình cho phép để trống) | `0` |
| Tiếng Việt | Nên gõ **không dấu** cho thống nhất với dữ liệu mẫu | `Nguyen Van A` |

Một số ràng buộc:
- Ngày sinh phải đủ **16 tuổi** trở lên và không ở tương lai.
- Ngày nghỉ phép không được trước ngày vào làm.
- Giờ ra phải sau giờ vào.
- Mã (nhân viên, phòng ban, chấm công, nghỉ phép...) không để trống và không trùng. Mã nhân viên, mã đơn nghỉ phép, mã chấm công được **cấp tự động**.

Tìm kiếm **không phân biệt hoa thường** và có thể nhập một phần.

### Chọn năm và tháng

Nhiều màn hình xem danh sách hỏi bạn chọn **năm** trước (kèm trạng thái và số bản ghi mỗi năm). Bạn nhập **số thứ tự** của năm hoặc một **ngày tháng** hợp lệ. Sau đó có 3 cách xem:

1. **Tìm theo mã**
2. **Xem theo phòng ban**
3. **Xem hết**

Từ khoảng 60 dòng trở lên, chương trình hỏi lọc thêm theo tháng.

---

## 6. Menu theo vai trò

Chương trình **chỉ hiện những mục bạn có quyền dùng**, nên mỗi vai trò thấy một menu khác nhau.

### 6.1. Giám đốc

```
 1. Thong tin ca nhan cua toi
 2. Phong ban quan li
 3. Quan ly nhan vien
 4. Quan ly cham cong
 5. Quan ly bao hiem
 6. Bang luong
 7. Bao cao
 8. Quan ly thuong phat
 9. Quan ly nghi phep
10. Duyet yeu cau (sua ho so, nghi phep)
11. Quy dinh thuong phat
12. Quan ly tai khoan
13. Nhat ky he thong
14. Doi mat khau
15. Luu toan bo du lieu
16. Doc du lieu tu file
 0. Dang xuat
```

Giám đốc có **mọi quyền trên toàn công ty**, sửa trực tiếp hồ sơ, lập bảng lương, quản lý tài khoản, xem nhật ký.

### 6.2. Trưởng phòng

Gồm: Thông tin cá nhân, Quản lý nhân viên, Quản lý chấm công, Quản lý bảo hiểm, Bảng lương, Báo cáo, Quản lý thưởng phạt, Quản lý nghỉ phép, Duyệt yêu cầu, Quy định thưởng phạt, Đổi mật khẩu, Lưu dữ liệu.

Phạm vi là **phòng ban của mình**, và chỉ tác động lên **cấp dưới** (phó phòng, nhân viên), không tự duyệt cho chính mình.

### 6.3. Phó phòng

Gồm: Thông tin cá nhân, Quản lý nhân viên, Quản lý chấm công, Báo cáo, Quản lý thưởng phạt, Quản lý nghỉ phép, Duyệt yêu cầu, Quy định thưởng phạt, Đổi mật khẩu, Lưu dữ liệu.

Xem trong phòng, chấm công và duyệt nghỉ phép cho nhân viên thường. **Chỉ xem lương, bảo hiểm của chính mình.**

### 6.4. Nhân viên (toàn thời gian, bán thời gian, hợp đồng)

Vào thẳng **Cổng thông tin nhân viên**:

```
 1. Ho so cua toi (chon nam)
 2. Phong ban cua toi (chon nam)
 3. Cham cong cua toi (chon nam)
 4. Cham cong hom nay (tu cham cong)
 5. Thong ke gio lam cua toi theo thang
 6. Nghi phep cua toi
 7. Bang luong cua toi (chon nam, thang)
 8. Bao hiem cua toi (chon nam)
 9. Thuong/phat cua toi (chon nam)
10. Yeu cau chinh sua ho so cua toi
11. Quy dinh thuong phat
12. Doi mat khau
13. Luu du lieu
 0. Dang xuat
```

Nhân viên **hợp đồng** có thêm mục **Hợp đồng lao động của tôi**.

> Số thứ tự trong menu có thể lệch một chút khi vai trò khác nhau, vì mục không có quyền sẽ bị ẩn. Luôn đọc tên mục trước khi chọn.

---

## 7. Hướng dẫn từng chức năng

### 7.1. Thông tin cá nhân (mọi vai trò)

- **Hồ sơ của tôi**: chọn năm để xem phòng ban, chức vụ, lương của năm đó. Giám đốc **sửa trực tiếp**; vai trò khác **gửi yêu cầu chỉnh sửa** (xem 7.10).
- **Chấm công hôm nay**: nhập giờ vào, giờ ra. Mỗi ngày chỉ chấm một lần.
- **Thống kê giờ làm theo tháng**: chọn năm, tháng để xem tổng giờ làm, giờ tăng ca, số ngày đi làm.
- **Nghỉ phép của tôi**: tạo đơn, xem đơn, xóa đơn đang chờ duyệt.
- **Bảng lương, bảo hiểm, thưởng/phạt của tôi**: chọn năm (và tháng) để xem.

### 7.2. Phòng ban (giám đốc)

- **Xem phòng ban quản lý**: chọn năm; tìm theo mã, xem theo loại phòng ban hoặc xem hết, có thể xem chi tiết thành viên.
- **Thêm phòng ban**, **Xóa phòng ban**: không cho xóa phòng còn nhân viên.

### 7.3. Quản lý nhân viên

| Mục | Mô tả | Ai dùng |
|---|---|---|
| Thêm 1 nhân viên | Chọn loại, chức vụ; mã cấp tự động; nhập thông tin; chọn phòng ban; có thể thêm trình độ | Giám đốc (toàn công ty), trưởng phòng (nhân viên thường trong phòng) |
| Nhập nhiều nhân viên | Nhập liên tiếp k nhân viên | Như trên |
| Xem danh sách | Chọn năm, tìm mã, theo phòng ban, xem hết | Theo quyền |
| Tìm kiếm | Theo mã, tên, SĐT, email; từ chính xác đến gần đúng | Theo quyền |
| Xem chi tiết 1 nhân viên | Bảng thông tin đầy đủ, có phần riêng theo loại nhân viên | Theo quyền |
| Tìm theo khoảng lương | Nhập lương từ, đến | Người được xem lương rộng |
| Sửa thông tin | Sửa hồ sơ, hoặc chỉ sửa số điện thoại/email | Theo quyền |
| Cho nghỉ việc | Chuyển trạng thái nghỉ việc, nhân viên không đăng nhập được nữa | Giám đốc, trưởng phòng (cấp dưới) |
| Xóa nhân viên | Xóa khỏi hệ thống | Giám đốc |
| Xem hợp đồng lao động | Xem và theo dõi hạn hợp đồng | Theo quyền |
| Thống kê theo loại, thống kê lương | Đếm theo loại nhân viên, lương cao/thấp/trung bình | Giám đốc |

**Mã nhân viên cấp tự động** theo chức vụ và loại: `GD` giám đốc, `TP` trưởng phòng, `PP` phó phòng, `NVT` toàn thời gian, `NVB` bán thời gian, `NVH` hợp đồng, kèm 3 chữ số.

**Ba loại nhân viên tính lương khác nhau:**

| Loại | Công thức lương |
|---|---|
| Toàn thời gian | Lương cơ bản + phụ cấp + thâm niên × 200.000 |
| Bán thời gian | Số giờ làm × lương theo giờ |
| Hợp đồng | Lương theo hợp đồng |

### 7.4. Chấm công

| Mục | Mô tả |
|---|---|
| Thêm chấm công cho nhân viên | Nhập mã NV, ngày, giờ vào, giờ ra (mã chấm công cấp tự động) |
| Xem danh sách chấm công | Chọn năm, tìm mã, theo phòng ban, xem hết |
| Xóa chấm công | Xóa theo mã |
| Thống kê giờ theo nhân viên/tháng | Tổng giờ làm, giờ tăng ca, số ngày đi làm |

Quy tắc: **8 giờ đầu mỗi ngày là giờ thường, phần vượt quá là giờ tăng ca.**

### 7.5. Bảo hiểm

| Mục | Mô tả |
|---|---|
| Thêm BHXH / Thêm BHYT | BHYT có thêm hạn thẻ và nơi khám chữa bệnh |
| Xem danh sách bảo hiểm | Chọn năm, tìm mã, theo phòng ban |
| Xóa theo mã BH | |
| Tính tiền đóng | Tiền nhân viên đóng và doanh nghiệp đóng |

Tỷ lệ đóng: **BHXH** nhân viên 8%, doanh nghiệp 17,5%. **BHYT** nhân viên 1,5%, doanh nghiệp 3%. Thẻ BHYT hết hạn không được tính vào bảng lương.

### 7.6. Bảng lương

| Mục | Mô tả |
|---|---|
| Lập bảng lương cho tất cả nhân viên | Chọn tháng, năm; chương trình tự lấy lương, thưởng/phạt, bảo hiểm |
| Xem bảng lương | Chọn năm, tìm mã, theo phòng ban |
| Thống kê quỹ lương | Tổng quỹ lương, lương cao nhất, thấp nhất, trung bình |

```
Tổng lương = Lương nhân viên + Phụ cấp + Tiền thưởng − Tiền phạt
Thực nhận  = Tổng lương − Tiền bảo hiểm nhân viên đóng   (không âm)
```

> Nên **tự động xét thưởng/phạt** (mục 7.8) trước khi lập bảng lương để tiền thưởng/phạt được tính vào.

### 7.7. Báo cáo

- **Giám đốc**: báo cáo toàn công ty, báo cáo theo phòng ban, thống kê nhân sự, thống kê bảo hiểm.
- **Trưởng phòng, phó phòng**: báo cáo phòng ban của mình.

### 7.8. Thưởng/phạt và quy định

| Mục | Mô tả |
|---|---|
| Thêm thưởng/phạt thủ công | Nhập mã NV, loại, lý do, số tiền |
| Xem danh sách thưởng/phạt | Chọn năm, tìm mã, theo phòng ban |
| Xóa theo mã | Giám đốc |
| Thống kê theo tháng | Tổng thưởng, tổng phạt |
| **Tự động xét thưởng/phạt** | Chọn tháng, năm; áp các quy định lên chấm công và nghỉ phép |

Ba quy định mẫu: thưởng chuyên cần (từ 22 ngày đi làm, 25.000/ngày), thưởng tăng ca (từ 10 giờ, 50.000/giờ), phạt nghỉ không phép (từ 1 ngày, 200.000/ngày). **Chạy tự động xét nhiều lần không bị nhân đôi khoản thưởng/phạt.** Mục **Quy định thưởng phạt** cho mọi người xem; giám đốc thêm hoặc xóa quy định.

### 7.9. Nghỉ phép

1. **Tạo đơn**: nhập từ ngày, đến ngày, lý do, loại nghỉ (`CO_PHEP` hoặc `KHONG_PHEP`). Đơn mới ở trạng thái **Chờ duyệt** (đơn của giám đốc được duyệt ngay vì không có cấp trên).
2. **Duyệt đơn** (trưởng phòng, phó phòng, giám đốc): chọn đơn trong danh sách đang chờ. Chỉ duyệt đơn của người có **cấp bậc thấp hơn**, không tự duyệt đơn của mình.
3. **Xóa đơn**: nhân viên chỉ xóa được đơn của mình khi còn **chờ duyệt**.

### 7.10. Yêu cầu chỉnh sửa hồ sơ

Chỉ **giám đốc** sửa trực tiếp hồ sơ. Người khác muốn sửa thì gửi yêu cầu:

1. Vào **Hồ sơ của tôi** (hoặc **Phòng ban của tôi**), chọn năm, chọn mục muốn sửa, nhập giá trị mới, xác nhận `y`.
2. Yêu cầu được gửi tới cấp trên: **nhân viên gửi trưởng phòng**, **trưởng phòng và phó phòng gửi giám đốc**.
3. Cấp trên vào **Duyệt yêu cầu**, chọn **1. Đồng ý** (hồ sơ được sửa ngay) hoặc **2. Từ chối** (có thể ghi lý do) hoặc **0. Để sau**.
4. Người gửi xem trạng thái (`CHO_DUYET`, `DA_DUYET`, `TU_CHOI`, `DA_HUY`) ở **Yêu cầu chỉnh sửa hồ sơ của tôi**, và có thể hủy yêu cầu đang chờ.

Các mục **nhạy cảm** (phòng ban, chức vụ, lương, ngày vào làm) **chỉ giám đốc mới duyệt được**. Mỗi mục trong một năm chỉ có một yêu cầu đang chờ.

### 7.11. Quản lý tài khoản (giám đốc)

| Mục | Mô tả |
|---|---|
| Danh sách tài khoản | Tìm mã, theo phòng ban, xem hết |
| Duyệt tài khoản đăng ký mới | Chọn mã đang chờ, xếp phòng ban nếu chưa có |
| Đặt lại mật khẩu về mặc định | Tài khoản phải đổi mật khẩu ở lần đăng nhập kế tiếp |
| Khóa tài khoản / Mở khóa | |

### 7.12. Nhật ký hệ thống (giám đốc)

- **Xem 30 hoạt động gần nhất.**
- **Xem nhật ký** (chọn năm, tìm mã, theo phòng ban).
- **Xem các lần bị từ chối / thất bại**: dùng để phát hiện đăng nhập sai, thao tác vượt quyền.

---

## 8. Các quy trình thường dùng

### 8.1. Thử nhanh toàn bộ chương trình (cho buổi demo)

1. Đăng nhập **giám đốc** → xem menu đầy đủ, vào **Quản lý nhân viên → Xem danh sách**.
2. **Thưởng/phạt → Tự động xét** tháng `10`, năm `2026`, rồi **Bảng lương → Lập** cùng tháng.
3. **Bảng lương → Xem bảng lương** để thấy kết quả; **Báo cáo → Báo cáo toàn công ty**.
4. **Duyệt yêu cầu**: xử lý các yêu cầu đang chờ.
5. Đăng xuất, đăng nhập `NVT002` / `002NVT` (bắt buộc đổi mật khẩu) → menu nhân viên chỉ có dữ liệu của chính mình, không có các mục quản lý.
6. Đăng nhập `TP002` / `002TP` → menu trưởng phòng chỉ trong phạm vi phòng Kinh doanh.
7. Quay lại giám đốc → **Nhật ký hệ thống** để xem các thao tác vừa thực hiện (và các lần bị từ chối, nếu có).

### 8.2. Tuyển nhân viên mới

1. Cách A (giám đốc): **Quản lý nhân viên → Thêm 1 nhân viên**, nhập thông tin, chọn phòng ban. Hệ thống tự tạo tài khoản với mật khẩu mặc định.
2. Cách B (tự đăng ký): nhân viên chọn **Đăng ký tài khoản mới** ở màn hình đầu; giám đốc vào **Quản lý tài khoản → Duyệt** và xếp phòng ban.

### 8.3. Chốt lương cuối tháng

1. Đảm bảo chấm công và nghỉ phép của tháng đã đầy đủ, đơn nghỉ phép đã được duyệt.
2. **Thưởng/phạt → Tự động xét** (tháng, năm).
3. **Bảng lương → Lập bảng lương cho tất cả nhân viên** (tháng, năm).
4. **Bảng lương → Thống kê quỹ lương**.
5. **Lưu toàn bộ dữ liệu**.

### 8.4. Nhân viên nghỉ việc

**Quản lý nhân viên → Cho nghỉ việc** → nhập mã. Tài khoản của nhân viên đó không đăng nhập được nữa, nhưng dữ liệu lịch sử vẫn còn.

---

## 9. Lưu và đọc dữ liệu

- Nhiều thao tác (đăng ký, duyệt tài khoản, đổi mật khẩu, lập bảng lương, tự động xét thưởng/phạt) **ghi file ngay**.
- Các thay đổi còn lại chỉ nằm trong bộ nhớ cho đến khi bạn chọn **Lưu toàn bộ dữ liệu** (hoặc trả lời `y` khi đăng xuất). **Nếu thoát mà không lưu, thay đổi sẽ mất.**
- **Đọc dữ liệu từ file** (giám đốc): nạp lại toàn bộ từ `data/`, dùng khi sửa tay file text hoặc muốn bỏ các thay đổi chưa lưu.
- Mỗi lần lưu **ghi đè** các file trong `data/`. **Hãy sao lưu thư mục `data/` trước khi demo.**

### Định dạng file

Mỗi dòng là một đối tượng, các trường cách nhau bằng dấu `|`. Dòng trống và dòng bắt đầu bằng `#` được bỏ qua.

| File | Nội dung | Cột chính |
|---|---|---|
| `phongban.txt` | Phòng ban | mã, tên, mô tả, giám đốc quản lý |
| `nhanvien.txt` | Nhân viên | loại (`TTG`/`BTG`/`HD`), mã, họ tên, ngày sinh, giới tính, SĐT, email, lương cơ bản, ngày vào làm, trạng thái, phòng ban, chức vụ, trình độ, phần riêng theo loại |
| `taikhoan.txt` | Tài khoản | mã, mật khẩu, trạng thái, phải đổi mật khẩu, số lần sai |
| `chamcong.txt` | Chấm công | mã, mã NV, ngày, giờ vào, giờ ra |
| `nghiphep.txt` | Nghỉ phép | mã, mã NV, từ ngày, đến ngày, lý do, loại, trạng thái |
| `baohiem.txt` | Bảo hiểm | mã, mã NV, số sổ/thẻ, ngày tham gia, loại |
| `quydinhthuongphat.txt` | Quy định | mã, tên, loại, điều kiện, ngưỡng, mức tiền, đơn vị |
| `thuongphat.txt` | Thưởng/phạt | mã, mã NV, loại, lý do, số lượng, ngày áp dụng |
| `bangluong.txt` | Bảng lương (dấu phẩy) | mã, mã NV, tháng, năm, phụ cấp, thưởng, phạt, bảo hiểm, lương NV |
| `lichsu.txt` | Lịch sử công tác theo năm | mã NV, năm, phòng ban, chức vụ, lương |
| `hopdong.txt` | Hợp đồng lao động | mã hợp đồng, mã NV, ngày ký, ngày hết hạn |
| `yeucau.txt` | Yêu cầu chỉnh sửa hồ sơ | mã, người gửi, người nhận, mục sửa, giá trị cũ/mới, trạng thái |
| `nhatky.txt` | Nhật ký hệ thống | thời gian, người thực hiện, hành động, đối tượng, kết quả |

> Nếu sửa tay file, giữ nguyên số cột và dấu `|`. Dòng sai định dạng hoặc trùng mã sẽ bị bỏ qua khi nạp (chương trình báo số dòng bị bỏ qua).

---

## 10. Câu hỏi thường gặp

**Khung `-----` và bảng bị gãy dòng?**
Terminal quá hẹp. Kéo rộng cửa sổ ra khoảng 110 ký tự, hoặc đổi hằng `DO_RONG` trong `GiaoDien.java` (mặc định 110) cho nhỏ hơn rồi biên dịch lại.

**Chữ tiếng Việt bị lỗi font?**
Nội dung chương trình in ra viết không dấu để tránh lỗi này. Khi biên dịch nhớ dùng `javac -encoding UTF-8 *.java`.

**Quên mật khẩu?**
Nhờ giám đốc vào **Quản lý tài khoản → Đặt lại mật khẩu về mặc định**, sau đó đăng nhập bằng mật khẩu mặc định (xem mục 3.1) và đổi lại.

**Tài khoản bị khóa?**
Do nhập sai quá 5 lần hoặc bị giám đốc khóa. Giám đốc vào **Quản lý tài khoản → Mở khóa**.

**Không thấy mục menu mình cần?**
Vai trò của bạn không có quyền đó. Xem bảng quyền ở mục 6 hoặc file `VaiTro.java`.

**Báo "Ban chi duyet yeu cau cua nguoi co cap bac thap hon"?**
Bạn không thể duyệt yêu cầu hoặc đơn của người cùng cấp, cấp trên, hoặc của chính mình.

**Khoản thưởng/phạt hoặc lương không như mong đợi?**
Kiểm tra: đã **tự động xét thưởng/phạt** trước khi **lập bảng lương** chưa; chấm công và nghỉ phép của tháng đã đủ chưa; thẻ BHYT còn hạn không.

**Muốn quay lại dữ liệu gốc?**
Chép đè lại thư mục `data/` từ bản sao lưu (hoặc `git checkout -- data/`), rồi chạy lại chương trình hoặc chọn **Đọc dữ liệu từ file**.
