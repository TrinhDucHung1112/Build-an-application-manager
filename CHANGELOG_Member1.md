# Thay đổi so với sơ đồ UML – Người 1 (Lõi nhân viên)

**Phạm vi:** `NhanVien` (abstract), `NhanVienToanThoiGian`, `NhanVienBanThoiGian`, `NhanVienHopDong`, `HopDongLaoDong`, `IThongTin`

**Quy ước:** ✅ giống UML · ✏️ khác UML (đã sửa) · ➕ UML không có, code thêm · ⚠️ chưa có / cần lưu ý

---

## 1. Tổng quan

| Class / Interface | Trạng thái | Ghi chú ngắn |
|---|---|---|
| `IThongTin` | ✅ | Giống UML |
| `NhanVien` | ✏️ | Đổi chữ ký 3 phương thức, thêm setter/getter + validate |
| `NhanVienToanThoiGian` | ✏️ | Constructor thêm tham số `thamNien` |
| `NhanVienBanThoiGian` | ✏️ | Constructor thêm tham số `soGioLam` |
| `NhanVienHopDong` | ✏️ | Constructor thêm 4 tham số, có thêm setter/getter |
| `HopDongLaoDong` | ✅ + ➕ | Khớp UML, thêm setter/getter + validate |

---

## 2. Thay đổi chung (áp dụng cho tất cả class)

- ➕ Thêm **setter/getter** cho mọi thuộc tính (UML không vẽ). Tên theo dạng `setmaNV`, `gethoTen`, … (không viết hoa chữ cái đầu sau `set/get`).
- ➕ Setter **kiểm tra dữ liệu** và ném `IllegalArgumentException` (chuỗi null/rỗng, số âm, ngày tương lai, …). Constructor có tham số gọi lại setter nên cũng được validate.
- ➕ Thông báo lỗi viết **không dấu** (ví dụ `"Ma nhan vien khong duoc de trong"`).

---

## 3. Chi tiết từng class

### 3.1 `IThongTin` ✅
- `void hienThiThongTin()` – giống UML.

### 3.2 `NhanVien` (abstract, implements `IThongTin`)

**Thuộc tính:** ✅ giống UML (12 thuộc tính + `static int soLuongNV`).

**Phương thức giống UML:** ✅ `NhanVien()`, `NhanVien(...)`, `tinhLuong()` (abstract), `hienThiThongTin()`, `getSoLuongNV()` (static), `nhap()`.

**Phương thức khác UML:**

| Phương thức | UML | Code hiện tại | Loại |
|---|---|---|---|
| `chamCong` | `chamCong(ngay, gioVao, gioRa) : ChamCong` | `double chamCong(int ngay, int gioVao, int gioRa)` – kiểm tra hợp lệ (ngày 1–31, giờ 0–23, giờ ra ≥ giờ vào) rồi trả về **số giờ làm** (`gioRa - gioVao`) | ✏️ Đổi kiểu trả về `ChamCong` → `double` |
| `tinhThuongPhat` | `tinhThuongPhat(thang, nam)` | `double tinhThuongPhat(boolean datKPI, int soNgayDiMuon, int soNamLamViec)` | ✏️ Đổi toàn bộ tham số |
| `tinhTienBaoHiem` | `tinhTienBaoHiem()` | `double tienBaoHiem()` | ✏️ Đổi tên |

**Công thức đang dùng:**
- `tinhThuongPhat` = `(datKPI ? 500.000 : 0)` − `soNgayDiMuon × 100.000` + `min(soNamLamViec, 5) × 200.000`. Hợp lệ khi `0 ≤ soNgayDiMuon ≤ 30` và `soNamLamViec ≥ 0`.
- `tienBaoHiem` = `luongCoBan × 0.105` (10,5%).

**Thêm mới (➕):**
- Setter cho `chucVu`, `phongBan`, `trinhDo` (`trinhDo` dùng `clone()` để tránh sửa mảng từ bên ngoài).
- Getter cho 9 thuộc tính cơ bản (`getmaNV` … `gettrangThai`).
- Constructor 9 tham số: `(maNV, hoTen, ngaySinh, gioiTinh, sdt, email, luongCoBan, ngayVaoLam, trangThai)`. UML chỉ ghi `(maNV, hoTen, ..., luongCoBan)`.

**Chưa có (⚠️):**
- Chưa có getter cho `chucVu`, `phongBan`, `trinhDo`.
- `hienThiThongTin()` chưa in `chucVu`, `phongBan`, `trinhDo`.
- Constructor 9 tham số chưa gán `chucVu`, `phongBan`, `trinhDo` (phải gọi setter riêng).

### 3.3 `NhanVienToanThoiGian` ✏️

| Mục | UML | Code |
|---|---|---|
| Thuộc tính | `phuCap: double`, `thamNien: int` | ✅ giống |
| Constructor | `(..., phuCap)` | `(..., double phuCap, int thamNien)` – ✏️ thêm `thamNien` |
| `tinhLuong()` | có | `luongCoBan + phuCap + thamNien × 200.000` |

### 3.4 `NhanVienBanThoiGian` ✏️

| Mục | UML | Code |
|---|---|---|
| Thuộc tính | `soGioLam: int`, `luongTheoGio: double` | ✅ giống |
| Constructor | `(..., luongTheoGio)` | `(..., int soGioLam, double luongTheoGio)` – ✏️ thêm `soGioLam` |
| `tinhLuong()` | có | `soGioLam × luongTheoGio` |

### 3.5 `NhanVienHopDong` ✏️

| Mục | UML | Code |
|---|---|---|
| Thuộc tính | `thoiHanHopDong`, `luongTheoHopDong`, `ngayBatDauThucTe`, `ngayKetThucTe`, `thoiGianThucTe` | ✅ giống |
| Constructor | `(..., thoiHanHopDong)` | `(..., thoiHanHopDong, luongTheoHopDong, ngayBatDauThucTe, ngayKetThucTe, thoiGianThucTe)` – ✏️ thêm 4 tham số |
| `tinhLuong()` | có | trả về `luongTheoHopDong` |
| `tinhThoiGianThucTe()` | `: int` | ✅ trả về **số ngày** giữa `ngayBatDauThucTe` và `ngayKetThucTe`; ném lỗi nếu null hoặc ngày kết thúc trước ngày bắt đầu |
| `kiemTraDungHan()` | `: boolean` | ✅ `tinhThoiGianThucTe() <= thoiHanHopDong` (nên `thoiHanHopDong` đang hiểu là **số ngày**) |

**Chưa có (⚠️):** UML có quan hệ `NhanVienHopDong ── signs ──> HopDongLaoDong (1..*)`, nhưng code **chưa có** thuộc tính nào tham chiếu tới `HopDongLaoDong`.

### 3.6 `HopDongLaoDong` ✅ + ➕

| Mục | UML | Code |
|---|---|---|
| Thuộc tính | `maHD`, `ngayKy`, `ngayHetHan`, `loaiHopDong` | ✅ giống |
| Constructor | `()` và `(maHD, ...)` | ✅ giống – bản đầy đủ nhận 4 tham số |
| `giaHan()` | `: void` | `void giaHan(LocalDate ngayHetHanMoi)` – ✅ thêm tham số; ném lỗi nếu null hoặc ngày mới không sau ngày cũ |
| `conHieuLuc()` | `: boolean` | ✅ `ngayHetHan != null && hôm nay chưa quá ngayHetHan` |
| Setter/getter | không có | ➕ thêm. `setngayKy` không cho ngày tương lai; `setngayHetHan` không cho trước `ngayKy` |

---

## 4. Ảnh hưởng tới các thành viên khác

| Với ai | Cần biết |
|---|---|
| Người 2 (`QuanLyNhanSu`, `PhongBan`, `ChucVu`, `TrinhDoChuyenMon`) | `NhanVien` dùng 3 class `ChucVu`, `PhongBan`, `TrinhDoChuyenMon` → cần có 3 class này mới biên dịch được. Các class `NhanVien*` chưa có getter cho `chucVu`, `phongBan`, `trinhDo`. |
| Người 3 (`ChamCong`, `NghiPhep`) | `NhanVien.chamCong(...)` trả về `double` (số giờ), **không** trả về đối tượng `ChamCong` như UML. |
| Người 4 (`BangLuong`, `ThuongPhat`) | `tinhThuongPhat` nhận `(boolean, int, int)` thay vì `(thang, nam)`. Lương lấy qua `tinhLuong()`, `getluongCoBan()`. |
| Người 5 (`BaoHiem`, `BaoCaoNhanSu`) | Tên phương thức bảo hiểm là `tienBaoHiem()` (không phải `tinhTienBaoHiem()`), cố định 10,5% lương cơ bản. |

---

## 5. Điểm cần lưu ý thêm (chưa sửa)

1. **Cộng thưởng thâm niên 2 nơi:** `NhanVienToanThoiGian.tinhLuong()` đã cộng `thamNien × 200.000`, còn `NhanVien.tinhThuongPhat()` cũng cộng `min(soNamLamViec, 5) × 200.000`. Nếu `BangLuong` cộng cả hai thì có thể bị tính trùng.
2. **`NhanVienHopDong.thoiGianThucTe` trùng lặp:** vừa là thuộc tính nhập vào, vừa có `tinhThoiGianThucTe()` tính từ 2 ngày → hai giá trị có thể không khớp nhau.
3. **Thông báo lỗi chưa chính xác:**
   - `setngayVaoLam`: ghi "khong duoc de trong" nhưng thực tế cũng từ chối ngày tương lai.
   - `setngayBatDauThucTe` / `setngayKetThucTe`: chữ "thuc the" nên là "thuc te".
   - `setthoiGianThucTe`: thông báo ghi "Thoi han hop dong khong hop le" (nhầm với `setthoiHanHopDong`).
4. **`nhap()`** tạo `new Scanner(System.in)` mỗi lần gọi; nên dùng chung 1 Scanner khi ghép vào `Main`.
5. **`chamCong()`** chỉ kiểm tra `ngay` trong khoảng 1–31, chưa kiểm tra theo số ngày thật của từng tháng.
6. **`HopDongLaoDong`** chưa được `NhanVienHopDong` sử dụng (xem mục 3.5).
