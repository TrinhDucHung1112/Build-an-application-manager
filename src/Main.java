import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Diem khoi chay cua chuong trinh quan ly nhan su.
 * Chay: javac *.java && java Main
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);

    private static final DanhSachPhongBan dsPB = new DanhSachPhongBan(100);
    private static final QuanLyNhanSu ql = new QuanLyNhanSu(100, dsPB);
    private static final DanhSachChamCong dsCC = new DanhSachChamCong(100);
    private static final DanhSachBaoHiem dsBH = new DanhSachBaoHiem(100);
    private static final DanhSachThuongPhat dsTP = new DanhSachThuongPhat(200);
    private static final DanhSachNghiPhep dsNP = new DanhSachNghiPhep(100);
    private static final DanhSachQuyDinhThuongPhat dsQD = new DanhSachQuyDinhThuongPhat(50);
    private static final DanhSachBangLuong dsBL = new DanhSachBangLuong(200);
    private static final BaoCaoNhanSu baoCao = new BaoCaoNhanSu(dsBH);

    public static void main(String[] args) {
        napDuLieu();
        int chon;
        do {
            System.out.println("\n========== QUAN LY NHAN SU ==========");
            System.out.println("1. Quan ly phong ban");
            System.out.println("2. Quan ly nhan vien");
            System.out.println("3. Quan ly cham cong");
            System.out.println("4. Quan ly bao hiem");
            System.out.println("5. Bang luong");
            System.out.println("6. Bao cao tong hop");
            System.out.println("7. Quan ly thuong phat");
            System.out.println("8. Quan ly nghi phep");
            System.out.println("9. Quan ly quy dinh thuong phat");
            System.out.println("10. Luu toan bo du lieu");
            System.out.println("11. Doc du lieu tu file");
            System.out.println("0. Thoat");
            chon = docInt("Lua chon: ", 0, 11);
            switch (chon) {
                case 1: menuPhongBan(); break;
                case 2: menuNhanVien(); break;
                case 3: menuChamCong(); break;
                case 4: menuBaoHiem(); break;
                case 5: menuBangLuong(); break;
                case 6: menuBaoCao(); break;
                case 7: menuThuongPhat(); break;
                case 8: menuNghiPhep(); break;
                case 9: menuQuyDinhThuongPhat(); break;
                case 10: luuDuLieu(); break;
                case 11: docDuLieu(); break;
                case 0: hoiLuuTruocKhiThoat(); System.out.println("Da dong chuong trinh."); break;
            }
        } while (chon != 0);
    }

    private static void napDuLieu() {
        dsPB.docFile();
        if (dsPB.getSoLuong() == 0) {
            dsPB.themPhong(new PhongBan("PB01", "HANH CHINH", "Quan ly hanh chinh nhan su"));
            dsPB.themPhong(new PhongBan("PB02", "KY THUAT", "Phat trien va van hanh he thong"));
        }
        ql.docFile();
        dsCC.docFile();
        dsBH.docFile();
        dsTP.docFile();
        dsNP.docFile();
        dsQD.docFile();
        dsBL.docFile();
    }

    private static String docChuoi(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int docInt(String prompt, int min, int max) {
        while (true) {
            try {
                int x = Integer.parseInt(docChuoi(prompt));
                if (x >= min && x <= max) return x;
            } catch (NumberFormatException ignored) {}
            System.out.println("Vui long nhap so nguyen tu " + min + " den " + max + ".");
        }
    }

    private static double docDouble(String prompt, double min) {
        while (true) {
            try {
                double x = Double.parseDouble(docChuoi(prompt));
                if (Double.isFinite(x) && x >= min) return x;
            } catch (NumberFormatException ignored) {}
            System.out.println("Gia tri khong hop le.");
        }
    }

    private static LocalDate docNgay(String prompt) {
        while (true) {
            try { return LocalDate.parse(docChuoi(prompt)); }
            catch (Exception e) { System.out.println("Nhap ngay theo yyyy-MM-dd."); }
        }
    }

    private static void menuPhongBan() {
        int c;
        do {
            System.out.println("\n--- PHONG BAN ---");
            System.out.println("1. Them phong ban\n2. Xem danh sach\n3. Tim phong ban\n4. Xoa phong ban\n0. Quay lai");
            c = docInt("Chon: ", 0, 4);
            switch (c) {
                case 1:
                    String ma = docChuoi("Ma phong ban: ");
                    String ten = docChuoi("Ten phong ban: ");
                    String mota = docChuoi("Mo ta: ");
                    System.out.println(dsPB.themPhong(new PhongBan(ma, ten, mota)) ? "Them thanh cong." : "Khong the them (trung ma/loi du lieu).");
                    break;
                case 2: dsPB.xuatDanhSach(); break;
                case 3:
                    PhongBan pb = dsPB.timTheoMa(docChuoi("Ma phong ban: "));
                    if (pb == null) System.out.println("Khong tim thay."); else pb.hienThiThongTin();
                    break;
                case 4: System.out.println(dsPB.xoaTheoMa(docChuoi("Ma can xoa: ")) ? "Da xoa." : "Khong xoa duoc phong ban."); break;
            }
        } while (c != 0);
    }

    private static void menuNhanVien() {
        int c;
        do {
            System.out.println("\n--- NHAN VIEN ---");
            System.out.println("1. Them 1 nhan vien\n2. Nhap nhieu nhan vien\n3. Xem danh sach");
            System.out.println("4. Tim kiem (chinh xac/gan dung)\n5. Sua thong tin nhan vien\n6. Xoa nhan vien");
            System.out.println("7. Thong ke theo loai\n8. Thong ke luong\n9. Tim theo phong ban\n10. Tim theo khoang luong\n0. Quay lai");
            c = docInt("Chon: ", 0, 10);
            switch (c) {
                case 1: themNhanVien(); break;
                case 2:
                    int so = docInt("So nhan vien muon nhap: ", 1, 100);
                    for (int i = 0; i < so; i++) {
                        System.out.println("\n--- Nhap nhan vien " + (i + 1) + "/" + so + " ---");
                        themNhanVien();
                    }
                    break;
                case 3: ql.xuatDanhSachNV(); break;
                case 4:
                    String tuKhoa = docChuoi("Nhap ma, ten, SDT hoac email (co the nhap mot phan): ");
                    NhanVien[] ketQua = ql.timKiemUuTien(tuKhoa);
                    if (ketQua.length == 0) System.out.println("Khong tim thay nhan vien phu hop.");
                    else ql.inKetQua(ketQua);
                    break;
                case 5: suaNhanVienTuMenu(); break;
                case 6: System.out.println(ql.xoaNhanVien(docChuoi("Ma nhan vien can xoa: ")) ? "Da xoa." : "Khong tim thay."); break;
                case 7: ql.thongKeTheoLoaiNv(); break;
                case 8: ql.thongKeLuong(); break;
                case 9:
                    dsPB.xuatDanhSach();
                    NhanVien[] theoPB = ql.timKiemTheoPhongBan(docChuoi("Ma phong ban: "));
                    if (theoPB.length == 0) System.out.println("Khong co nhan vien nao trong phong ban nay.");
                    else ql.inKetQua(theoPB);
                    break;
                case 10:
                    double tuLuong = docDouble("Luong tu: ", 0);
                    double denLuong = docDouble("Luong den: ", 0);
                    NhanVien[] theoLuong = ql.timKiemTheoLuong(tuLuong, denLuong);
                    if (theoLuong.length == 0) System.out.println("Khong co nhan vien nao trong khoang luong nay.");
                    else ql.inKetQua(theoLuong);
                    break;
            }
        } while (c != 0);
    }

    private static void themNhanVien() {
        int loai = docInt("Loai NV (1: Toan thoi gian, 2: Ban thoi gian, 3: Hop dong): ", 1, 3);
        String ma = docChuoi("Ma NV: ");
        String ten = docChuoi("Ho ten: ");
        LocalDate ns = docNgay("Ngay sinh (yyyy-MM-dd): ");
        String gt = docChuoi("Gioi tinh: ");
        String sdt = docChuoi("So dien thoai: ");
        String email = docChuoi("Email: ");
        double luong = docDouble("Luong co ban: ", 0);
        LocalDate vaoLam = docNgay("Ngay vao lam (yyyy-MM-dd): ");
        String trangThai = docChuoi("Trang thai (Dang lam/Nghi): ");
        NhanVien nv;
        if (loai == 1) {
            double pc = docDouble("Phu cap: ", 0);
            int tn = docInt("Tham nien (nam): ", 0, 100);
            nv = new NhanVienToanThoiGian(ma, ten, ns, gt, sdt, email, luong, vaoLam, trangThai, pc, tn);
        } else if (loai == 2) {
            int gio = docInt("So gio lam/thang: ", 0, 744);
            double gia = docDouble("Luong theo gio: ", 0);
            nv = new NhanVienBanThoiGian(ma, ten, ns, gt, sdt, email, luong, vaoLam, trangThai, gio, gia);
        } else {
            int han = docInt("Thoi han hop dong (ngay): ", 1, 3650);
            double luongHD = docDouble("Luong theo hop dong: ", 0);
            nv = new NhanVienHopDong(ma, ten, ns, gt, sdt, email, luong, vaoLam, trangThai,
                    han, luongHD, vaoLam, vaoLam.plusDays(han), han);
        }
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban (bo trong neu chua phan): ");
        if (!maPB.isBlank()) {
            PhongBan pb = dsPB.timTheoMa(maPB);
            if (pb == null) { System.out.println("Khong tim thay phong ban; nhan vien chua duoc gan phong."); }
            else nv.setPhongBan(pb);
        }
        String coCV = docChuoi("Co muon gan chuc vu? (y/n): ");
        if (coCV.equalsIgnoreCase("y")) {
            String maCV = docChuoi("Ma chuc vu: ");
            String tenCV = docChuoi("Ten chuc vu: ");
            double heSo = docDouble("He so phu cap chuc vu: ", 0);
            nv.setChucVu(new ChucVu(maCV, tenCV, heSo));
        }
        String coTD = docChuoi("Co muon them trinh do chuyen mon? (y/n): ");
        if (coTD.equalsIgnoreCase("y")) {
            nv.setTrinhDo(docDanhSachTrinhDo());
        }
        System.out.println(ql.themNhanVien(nv) ? "Them nhan vien thanh cong." : "Them that bai (ma NV trung hoac danh sach loi).");
    }

    private static void suaNhanVienTuMenu() {
        String ma = docChuoi("Ma nhan vien can sua: ");
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) {
            System.out.println("Khong tim thay nhan vien.");
            return;
        }
        System.out.println("Thong tin hien tai:");
        nv.hienThiThongTin();
        System.out.println("Nhap thong tin moi:");
        String ten = docChuoi("Ho ten: ");
        LocalDate ns = docNgay("Ngay sinh (yyyy-MM-dd): ");
        String gt = docChuoi("Gioi tinh: ");
        String sdt = docChuoi("So dien thoai: ");
        String email = docChuoi("Email: ");
        double luong = docDouble("Luong co ban: ", 0);
        LocalDate vaoLam = docNgay("Ngay vao lam (yyyy-MM-dd): ");
        String trangThai = docChuoi("Trang thai: ");
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban moi (bo trong neu chua phan): ");
        PhongBan pb = maPB.isBlank() ? null : dsPB.timTheoMa(maPB);
        if (!maPB.isBlank() && pb == null) {
            System.out.println("Ma phong ban khong ton tai; huy cap nhat.");
            return;
        }
        try {
            boolean ok = ql.capNhatThongTinCoBan(ma, ten, ns, gt, sdt, email,
                    luong, vaoLam, trangThai, pb);
            System.out.println(ok ? "Cap nhat thanh cong." : "Cap nhat that bai.");
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
            return;
        }
        String doiCV = docChuoi("Co muon doi chuc vu? (y/n): ");
        if (doiCV.equalsIgnoreCase("y")) {
            String maCV = docChuoi("Ma chuc vu: ");
            String tenCV = docChuoi("Ten chuc vu: ");
            double heSo = docDouble("He so phu cap chuc vu: ", 0);
            try {
                nv.setChucVu(new ChucVu(maCV, tenCV, heSo));
                System.out.println("Da doi chuc vu.");
            } catch (IllegalArgumentException e) {
                System.out.println("Du lieu khong hop le: " + e.getMessage());
            }
        }
        String doiTD = docChuoi("Co muon nhap lai trinh do chuyen mon? (y/n): ");
        if (doiTD.equalsIgnoreCase("y")) {
            nv.setTrinhDo(docDanhSachTrinhDo());
            System.out.println("Da cap nhat trinh do.");
        }
    }

    //nhap danh sach trinh do chuyen mon cua 1 nhan vien (nhap duoc nhieu trinh do)
    private static TrinhDoChuyenMon[] docDanhSachTrinhDo() {
        TrinhDoChuyenMon[] ds = new TrinhDoChuyenMon[0];
        do {
            try {
                String ma = docChuoi("Ma trinh do: ");
                String ten = docChuoi("Ten trinh do (Cao dang/Dai hoc/Thac si...): ");
                String nganh = docChuoi("Chuyen nganh: ");
                String truong = docChuoi("Truong dao tao: ");
                int nam = docInt("Nam tot nghiep: ", 1950, java.time.Year.now().getValue());
                double heSo = docDouble("He so trinh do: ", 0);
                TrinhDoChuyenMon td = new TrinhDoChuyenMon(ma, ten, nganh, truong, nam, heSo);
                ds = java.util.Arrays.copyOf(ds, ds.length + 1);
                ds[ds.length - 1] = td;
            } catch (IllegalArgumentException e) {
                System.out.println("Du lieu khong hop le: " + e.getMessage());
            }
        } while (docChuoi("Them trinh do khac? (y/n): ").equalsIgnoreCase("y"));
        return ds;
    }

    private static void menuNghiPhep() {
        int c;
        do {
            System.out.println("\n--- QUAN LY NGHI PHEP ---");
            System.out.println("1. Tao don nghi phep");
            System.out.println("2. Xem tat ca don");
            System.out.println("3. Xem don theo nhan vien");
            System.out.println("4. Duyet don");
            System.out.println("5. Xoa don");
            System.out.println("0. Quay lai");
            c = docInt("Chon: ", 0, 5);
            switch (c) {
                case 1: {
                    String maNP = docChuoi("Ma don: ");
                    String maNV = docChuoi("Ma nhan vien: ");
                    if (ql.timTheoMa(maNV) == null) {
                        System.out.println("Nhan vien khong ton tai.");
                        break;
                    }
                    LocalDate tuNgay = docNgay("Tu ngay (yyyy-MM-dd): ");
                    LocalDate denNgay = docNgay("Den ngay (yyyy-MM-dd): ");
                    String lyDo = docChuoi("Ly do: ");
                    NghiPhep np = new NghiPhep(maNP, maNV, tuNgay, denNgay, lyDo);
                    String loai = docChuoi("Loai nghi (CO_PHEP/KHONG_PHEP): ");
                    np.setLoaiNghi(loai.equalsIgnoreCase("KHONG_PHEP") ? "Khong phep" : "Co phep");
                    System.out.println(dsNP.them(np) ? "Da tao don nghi phep." : "Tao don that bai (kiem tra ma/ngay).");
                    break;
                }
                case 2: dsNP.xuatDanhSach(); break;
                case 3: {
                    NghiPhep[] ds = dsNP.timTheoMaNV(docChuoi("Ma nhan vien: "));
                    for (NghiPhep np : ds)
                        System.out.printf("%s | %s -> %s | %s | %s%n", np.getMaNP(),
                                np.getNgayBatDau(), np.getNgayKetThuc(), np.getLoaiNghi(), np.getTrangThai());
                    if (ds.length == 0) System.out.println("Khong co don.");
                    break;
                }
                case 4: System.out.println(dsNP.duyetTheoMa(docChuoi("Ma don can duyet: ")) ? "Da duyet." : "Khong tim thay don."); break;
                case 5: System.out.println(dsNP.xoaTheoMa(docChuoi("Ma don can xoa: ")) ? "Da xoa." : "Khong tim thay don."); break;
            }
        } while (c != 0);
    }

    private static void menuQuyDinhThuongPhat() {
        int c;
        do {
            System.out.println("\n--- QUY DINH THUONG PHAT ---");
            System.out.println("1. Them quy dinh");
            System.out.println("2. Xem danh sach quy dinh");
            System.out.println("3. Xoa quy dinh");
            System.out.println("0. Quay lai");
            c = docInt("Chon: ", 0, 3);
            switch (c) {
                case 1: {
                    String ma = docChuoi("Ma quy dinh: ");
                    String ten = docChuoi("Ten quy dinh: ");
                    String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
                    if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
                        System.out.println("Loai khong hop le.");
                        break;
                    }
                    String dieuKien = docChuoi("Dieu kien (>=, >, <=, <, ==): ");
                    if (!dieuKien.equals(">=") && !dieuKien.equals(">") && !dieuKien.equals("<=")
                            && !dieuKien.equals("<") && !dieuKien.equals("==")) {
                        System.out.println("Dieu kien khong hop le.");
                        break;
                    }
                    double nguong = docDouble("Nguong: ", 0);
                    double mucTien = docDouble("Muc tien tren moi don vi: ", 0);
                    System.out.println("Don vi: GIO_TANG_CA / NGAY_DI_LAM / NGAY_NGHI_KHONG_PHEP");
                    String donVi = docChuoi("Don vi: ").toUpperCase();
                    if (!donVi.equals(QuyDinhThuongPhat.GIO_TANG_CA)
                            && !donVi.equals(QuyDinhThuongPhat.NGAY_DI_LAM)
                            && !donVi.equals(QuyDinhThuongPhat.NGAY_NGHI_KHONG_PHEP)) {
                        System.out.println("Don vi khong hop le.");
                        break;
                    }
                    QuyDinhThuongPhat qd = new QuyDinhThuongPhat(ma, ten, loai, dieuKien, nguong, mucTien, donVi);
                    System.out.println(dsQD.them(qd) ? "Da them quy dinh." : "Them that bai (ma trung).");
                    break;
                }
                case 2: dsQD.xuatDanhSach(); break;
                case 3: System.out.println(dsQD.xoaTheoMa(docChuoi("Ma quy dinh: ")) ? "Da xoa." : "Khong tim thay."); break;
            }
        } while (c != 0);
    }

    private static void menuBaoCao() {
        int c;
        do {
            System.out.println("\n--- BAO CAO ---");
            System.out.println("1. Bao cao toan cong ty");
            System.out.println("2. Bao cao theo phong ban");
            System.out.println("3. Thong ke nhan su");
            System.out.println("4. Thong ke bao hiem");
            System.out.println("0. Quay lai");
            c = docInt("Chon: ", 0, 4);
            switch (c) {
                case 1: {
                    int thang = docInt("Thang: ", 1, 12);
                    int nam = docInt("Nam: ", 2000, 3000);
                    baoCao.inBaoCaoToanCongTy(ql, dsBL, thang, nam);
                    break;
                }
                case 2: {
                    dsPB.xuatDanhSach();
                    String maPB = docChuoi("Ma phong ban: ");
                    int thang = docInt("Thang: ", 1, 12);
                    int nam = docInt("Nam: ", 2000, 3000);
                    baoCao.inBaoCaoTheoPhong(maPB, ql, dsBL, thang, nam);
                    break;
                }
                case 3: baoCao.thongKeNhanSu(ql, dsBL); break;
                case 4: baoCao.thongKeBaoHiem(); break;
            }
        } while (c != 0);
    }

    private static void menuThuongPhat() {
        int c;
        do {
            System.out.println("\n--- QUAN LY THUONG PHAT ---");
            System.out.println("1. Them thuong/phat thu cong");
            System.out.println("2. Xem tat ca");
            System.out.println("3. Xem theo ma nhan vien");
            System.out.println("4. Xoa theo ma thuong/phat");
            System.out.println("5. Thong ke thuong/phat theo thang");
            System.out.println("6. Tu dong xet thuong/phat theo quy dinh");
            System.out.println("0. Quay lai");
            c = docInt("Chon: ", 0, 6);
            switch (c) {
                case 1: {
                    String maTP = docChuoi("Ma thuong/phat: ");
                    String maNV = docChuoi("Ma nhan vien: ");
                    if (ql.timTheoMa(maNV) == null) {
                        System.out.println("Nhan vien khong ton tai.");
                        break;
                    }
                    String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
                    if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
                        System.out.println("Loai chi nhan THUONG hoac PHAT.");
                        break;
                    }
                    String lyDo = docChuoi("Ly do: ");
                    double tien = docDouble("So tien: ", 0);
                    ThuongPhat tp = new ThuongPhat(maTP, maNV, "", loai, lyDo, 1, tien, LocalDate.now());
                    System.out.println(dsTP.them(tp) ? "Da them thuong/phat." : "Them that bai (ma trung).");
                    break;
                }
                case 2: dsTP.xuatDanhSach(); break;
                case 3: {
                    ThuongPhat[] ds = dsTP.timTheoMaNV(docChuoi("Ma nhan vien: "));
                    if (ds.length == 0) System.out.println("Khong co du lieu.");
                    for (ThuongPhat tp : ds) tp.hienThiThongTin();
                    break;
                }
                case 4:
                    System.out.println(dsTP.xoaTheoMa(docChuoi("Ma thuong/phat: ")) ? "Da xoa." : "Khong tim thay.");
                    break;
                case 5: {
                    String maNV = docChuoi("Ma nhan vien: ");
                    int thang = docInt("Thang: ", 1, 12);
                    int nam = docInt("Nam: ", 2000, 3000);
                    System.out.printf("Tong thuong: %,.0f VND%n", dsTP.tongThuong(maNV, thang, nam));
                    System.out.printf("Tong phat: %,.0f VND%n", dsTP.tongPhat(maNV, thang, nam));
                    break;
                }
                case 6: {
                    int thang = docInt("Thang can xet: ", 1, 12);
                    int nam = docInt("Nam can xet: ", 2000, 3000);
                    dsTP.tuDongXetThuongPhat(thang, nam, ql.getDanhSachNV(),
                            dsQD.getDanhSach(), dsCC, dsNP.getDanhSach());
                    dsTP.ghiFile();
                    System.out.println("Da tu dong xet thuong/phat theo cac quy dinh hien co.");
                    break;
                }
            }
        } while (c != 0);
    }

    private static void menuChamCong() {
        int c;
        do {
            System.out.println("\n--- CHAM CONG ---");
            System.out.println("1. Them cham cong\n2. Xem danh sach\n3. Xoa cham cong\n4. Thong ke gio theo nhan vien/thang\n0. Quay lai");
            c = docInt("Chon: ", 0, 4);
            switch (c) {
                case 1:
                    String maCC = docChuoi("Ma cham cong: ");
                    String maNV = docChuoi("Ma nhan vien: ");
                    LocalDate ngay = docNgay("Ngay (yyyy-MM-dd): ");
                    LocalTime vao = docTime("Gio vao (HH:mm): ");
                    LocalTime ra = docTime("Gio ra (HH:mm): ");
                    if (!ra.isAfter(vao)) System.out.println("Gio ra phai sau gio vao.");
                    else System.out.println(dsCC.them(new ChamCong(maCC, maNV, ngay, vao, ra)) ? "Da them cham cong." : "Them that bai.");
                    break;
                case 2: dsCC.xuatDanhSach(); break;
                case 3: System.out.println(dsCC.xoaTheoMa(docChuoi("Ma cham cong: ")) ? "Da xoa." : "Khong tim thay."); break;
                case 4:
                    String id = docChuoi("Ma NV: ");
                    int thang = docInt("Thang: ", 1, 12);
                    int nam = docInt("Nam: ", 2000, 3000);
                    System.out.printf("So ngay: %d | Gio thuong: %.2f | Gio tang ca: %.2f%n",
                            dsCC.thongKeSoNgayDiLam(id, thang, nam),
                            dsCC.thongKeGioLam(id, thang, nam),
                            dsCC.thongKeGioTangCa(id, thang, nam));
                    break;
            }
        } while (c != 0);
    }

    private static LocalTime docTime(String prompt) {
        while (true) {
            try { return LocalTime.parse(docChuoi(prompt)); }
            catch (Exception e) { System.out.println("Nhap gio theo HH:mm."); }
        }
    }

    private static void menuBaoHiem() {
        int c;
        do {
            System.out.println("\n--- BAO HIEM ---");
            System.out.println("1. Them BHXH\n2. Them BHYT\n3. Xem danh sach\n4. Tim theo ma NV\n5. Xoa theo ma BH\n6. Tinh tien dong\n0. Quay lai");
            c = docInt("Chon: ", 0, 6);
            switch (c) {
                case 1:
                    String maBH = docChuoi("Ma BH: "), maNV = docChuoi("Ma NV: "), soSo = docChuoi("So so BHXH: ");
                    System.out.println(dsBH.them(new BaoHiemXaHoi(maBH, maNV, soSo, LocalDate.now())) ? "Da them." : "Them that bai.");
                    break;
                case 2:
                    maBH = docChuoi("Ma BH: "); maNV = docChuoi("Ma NV: "); String the = docChuoi("So the BHYT: ");
                    String noi = docChuoi("Noi KCB: "); LocalDate hanThe = docNgay("Han the (yyyy-MM-dd): ");
                    System.out.println(dsBH.them(new BaoHiemYTe(maBH, maNV, the, LocalDate.now(), noi, hanThe)) ? "Da them." : "Them that bai.");
                    break;
                case 3: dsBH.xuatDanhSach(); break;
                case 4:
                    BaoHiem[] kq = dsBH.timTheoMaNV(docChuoi("Ma NV: "));
                    for (BaoHiem bh : kq) bh.hienThiThongTin();
                    if (kq.length == 0) System.out.println("Khong co du lieu.");
                    break;
                case 5: System.out.println(dsBH.xoaTheoMa(docChuoi("Ma BH: ")) ? "Da xoa." : "Khong tim thay."); break;
                case 6:
                    maNV = docChuoi("Ma NV: "); double mucLuong = docDouble("Luong dong bao hiem: ", 0);
                    System.out.printf("Nhan vien dong: %,.0f VND%nDoanh nghiep dong: %,.0f VND%n",
                            dsBH.tongTienNhanVienDong(maNV, mucLuong), dsBH.tongTienDoanhNghiepDong(maNV, mucLuong));
                    break;
            }
        } while (c != 0);
    }

    private static void menuBangLuong() {
        int c;
        do {
            System.out.println("\n--- BANG LUONG ---");
            System.out.println("1. Lap bang luong cho tat ca nhan vien\n2. Xem bang luong theo ma NV\n3. Xem bang luong cong ty theo thang\n4. Thong ke quy luong\n0. Quay lai");
            c = docInt("Chon: ", 0, 4);
            switch (c) {
                case 1:
                    int thang = docInt("Thang: ", 1, 12), nam = docInt("Nam: ", 2000, 3000);
                    dsBL.lapBangLuongChoTatCa(ql.getDanhSachNV(), thang, nam, dsTP, dsBH);
                    dsBL.ghiFile();
                    System.out.println("Da lap bang luong.");
                    break;
                case 2: dsBL.xuatBangLuongNhanVien(docChuoi("Ma NV: ")); break;
                case 3:
                    thang = docInt("Thang: ", 1, 12); nam = docInt("Nam: ", 2000, 3000);
                    dsBL.xuatBangLuongCongTy(thang, nam);
                    break;
                case 4:
                    thang = docInt("Thang: ", 1, 12); nam = docInt("Nam: ", 2000, 3000);
                    System.out.printf("Tong quy luong: %,.0f VND%nLuong trung binh: %,.0f VND%n",
                            dsBL.tongQuyLuong(thang, nam), dsBL.luongTrungBinh(thang, nam));
                    break;
            }
        } while (c != 0);
    }

    private static void hoiLuuTruocKhiThoat() {
        String tl = docChuoi("Luu toan bo du lieu truoc khi thoat? (y/n): ");
        if (tl.equalsIgnoreCase("y")) luuDuLieu();
        else System.out.println("Khong luu. Cac thay doi chua luu se bi mat.");
    }

    private static void luuDuLieu() {
        dsPB.ghiFile(); ql.ghiFile(); dsCC.ghiFile(); dsBH.ghiFile();
        dsTP.ghiFile(); dsNP.ghiFile(); dsQD.ghiFile(); dsBL.ghiFile();
        System.out.println("Da luu toan bo du lieu vao thu muc data.");
    }

    private static void docDuLieu() {
        dsPB.docFile(); ql.docFile(); dsCC.docFile(); dsBH.docFile();
        dsTP.docFile(); dsNP.docFile(); dsQD.docFile(); dsBL.docFile();
    }
}
