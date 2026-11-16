import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Diem khoi chay cua chuong trinh quan ly nhan su.
 * Chay: javac *.java && java Main
 *
 * Moi lan nhap hoac xuat deu nam giua 2 dong -----------  (xem GiaoDien.batDau / GiaoDien.ketThuc)
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
    private static final DanhSachTaiKhoan dsTK = new DanhSachTaiKhoan(100);

    //nhan vien dang dang nhap (null khi chua dang nhap) - dung cho phan quyen
    private static NhanVien nguoiDangNhap = null;

    public static void main(String[] args) {
        chay("KHOI DONG - NAP DU LIEU", Main::napDuLieu);
        boolean tiepTuc = true;
        while (tiepTuc) {
            nguoiDangNhap = null;
            int chon = menu("QUAN LY NHAN SU - DANG NHAP", new String[]{
                "1. Dang nhap",
                "2. Dang ky tai khoan moi",
                "0. Thoat"}, 2);
            switch (chon) {
                case 1:
                    chay("DANG NHAP", Main::dangNhap);
                    if (nguoiDangNhap != null) menuChinh();
                    break;
                case 2: chay("DANG KY TAI KHOAN MOI", Main::dangKy); break;
                case 0: chay("THOAT CHUONG TRINH", () -> GiaoDien.in("Da dong chuong trinh.")); tiepTuc = false; break;
            }
        }
    }

    //menu chinh sau khi dang nhap. Chon 0 = dang xuat: hoi co luu du lieu khong roi quay lai man hinh dang nhap
    private static void menuChinh() {
        int chon;
        do {
            chon = menu("QUAN LY NHAN SU", new String[]{
                "1. Quan ly phong ban",
                "2. Quan ly nhan vien",
                "3. Quan ly cham cong",
                "4. Quan ly bao hiem",
                "5. Bang luong",
                "6. Bao cao tong hop",
                "7. Quan ly thuong phat",
                "8. Quan ly nghi phep",
                "9. Quan ly quy dinh thuong phat",
                "10. Luu toan bo du lieu",
                "11. Doc du lieu tu file",
                "0. Dang xuat (ve man hinh dang nhap)"}, 11);
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
                case 10: chay("LUU TOAN BO DU LIEU", Main::luuDuLieu); break;
                case 11: chay("DOC DU LIEU TU FILE", Main::docDuLieu); break;
                case 0: chay("DANG XUAT", Main::dangXuat); break;
            }
        } while (chon != 0);
    }

//----------------- DANG NHAP / DANG KY -----------------
    //tai khoan = ma nhan vien, mat khau = phan so + phan chu cua ma (VD: TP001 -> 001TP)
    private static void dangNhap() {
        for (int lan = 1; lan <= 3; lan++) {
            String taiKhoan = docChuoi("Tai khoan (ma nhan vien): ");
            String matKhau = docChuoi("Mat khau: ");
            TaiKhoan tk = dsTK.dangNhap(taiKhoan, matKhau);
            if (tk == null) {
                GiaoDien.in("Sai tai khoan hoac mat khau (lan " + lan + "/3).");
                continue;
            }
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            if (nv == null) {
                GiaoDien.in("Tai khoan nay khong con gan voi nhan vien nao.");
                return;
            }
            if (nv.getTrangThai().toLowerCase().startsWith("nghi")) {
                GiaoDien.in("Tai khoan da bi khoa vi nhan vien da nghi viec.");
                return;
            }
            nguoiDangNhap = nv;
            String tenCV = nv.getChucVu() == null ? "chua co chuc vu" : nv.getChucVu().getTenCV();
            GiaoDien.in("");
            GiaoDien.in("Dang nhap thanh cong. Xin chao " + nv.getHoTen() + " (" + tenCV + ").");
            return;
        }
        GiaoDien.in("Nhap sai qua 3 lan, quay lai man hinh dau.");
    }

    //nguoi moi tu dang ky: luon la NHAN VIEN thuong, ma duoc cap tu dong, luong/hop dong de mac dinh de quan ly cap nhat sau
    private static void dangKy() {
        GiaoDien.in("Tai khoan moi co chuc vu NHAN VIEN. Ma nhan vien duoc cap tu dong.");
        int loai = docInt("Loai NV (1: Toan thoi gian, 2: Ban thoi gian, 3: Hop dong): ", 1, 3);
        String ten = docChuoiKhongRong("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();

        ChucVu chucVu = ChucVu.macDinh(4);
        String ma = MaNhanVien.taoMaMoi(MaNhanVien.tienTo(loai, chucVu), ql.getDanhSachNV());
        LocalDate homNay = LocalDate.now();
        NhanVien nv;
        if (loai == 1) {
            nv = new NhanVienToanThoiGian(ma, ten, ns, gt, sdt, email, 10000000, homNay, "Dang lam", 0, 0);
        } else if (loai == 2) {
            nv = new NhanVienBanThoiGian(ma, ten, ns, gt, sdt, email, 6000000, homNay, "Dang lam", 0, 30000);
        } else {
            nv = new NhanVienHopDong(ma, ten, ns, gt, sdt, email, 8000000, homNay, "Dang lam",
                    180, 12000000, homNay, homNay.plusDays(180), 180);
        }
        nv.setChucVu(chucVu);

        GiaoDien.in("");
        GiaoDien.in("Danh sach phong ban hien co:");
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban (bo trong neu chua biet): ");
        if (!maPB.isBlank()) {
            PhongBan pb = dsPB.timTheoMa(maPB);
            if (pb == null) GiaoDien.in("Khong tim thay phong ban; ban chua duoc xep phong.");
            else nv.setPhongBan(pb);
        }

        if (!ql.themNhanVien(nv)) {
            GiaoDien.in("Dang ky that bai (danh sach nhan vien da day).");
            return;
        }
        String matKhau = MaNhanVien.matKhauMacDinh(ma);
        dsTK.them(new TaiKhoan(ma, matKhau));
        ql.ghiFile();
        dsTK.ghiFile();

        List<String[]> dong = new ArrayList<>();
        dong.add(new String[]{"Ho ten", ten});
        dong.add(new String[]{"Tai khoan (ma NV)", ma});
        dong.add(new String[]{"Mat khau", matKhau});
        GiaoDien.in("");
        GiaoDien.in("Dang ky thanh cong! Hay ghi nho tai khoan de dang nhap:");
        GiaoDien.inThongTin(dong);
    }

    //tuoi toi thieu de dang ky / them nhan vien
    private static final int TUOI_TOI_THIEU = 16;

    //ngay sinh: <= hom nay va du TUOI_TOI_THIEU tuoi. Moi loi bao rieng, ghi ro ngay hom nay
    private static LocalDate docNgaySinh() {
        LocalDate homNay = LocalDate.now();
        LocalDate hanCuoi = homNay.minusYears(TUOI_TOI_THIEU);
        GiaoDien.in("Hom nay la " + homNay + ". Ngay sinh phai tu hom nay tro ve truoc va du "
                + TUOI_TOI_THIEU + " tuoi tro len (sinh tu " + hanCuoi + " tro ve truoc).");
        while (true) {
            String nhap = docChuoi("Ngay sinh (yyyy-MM-dd, VD 2005-03-21): ");
            LocalDate ns;
            try {
                ns = LocalDate.parse(nhap);
            } catch (Exception e) {
                GiaoDien.in("Sai dinh dang. Nhap theo yyyy-MM-dd, nho co dau '-' (VD 2005-03-21).");
                continue;
            }
            if (ns.isAfter(homNay)) {
                GiaoDien.in("Sai: ngay sinh khong duoc sau hom nay (" + homNay + ").");
            } else if (ns.isAfter(hanCuoi)) {
                GiaoDien.in("Sai: phai du " + TUOI_TOI_THIEU + " tuoi tro len (ngay sinh phai la " + hanCuoi + " hoac som hon).");
            } else {
                return ns;
            }
        }
    }

    //gioi tinh: chon 1 trong 3
    private static String docGioiTinh() {
        GiaoDien.in("Gioi tinh: 1. Nam (male) | 2. Nu (female) | 3. Khac (other)");
        int c = docInt("Chon gioi tinh (1-3): ", 1, 3);
        return c == 1 ? "Nam" : c == 2 ? "Nu" : "Khac";
    }

    //so dien thoai: chi gom chu so, 8 hoac 10 so, khong toan so 0
    private static String docSoDienThoai() {
        while (true) {
            String sdt = docChuoi("So dien thoai (chi gom chu so, 8 hoac 10 so, VD 01234567): ");
            if (!sdt.matches("\\d+")) {
                GiaoDien.in("Sai: so dien thoai chi duoc gom chu so 0-9, khong co dau cach hay ky tu khac.");
            } else if (sdt.length() != 8 && sdt.length() != 10) {
                GiaoDien.in("Sai: so dien thoai phai co 8 hoac 10 chu so (ban nhap " + sdt.length() + " so).");
            } else if (sdt.matches("0+")) {
                GiaoDien.in("Sai: so dien thoai khong duoc toan so 0.");
            } else {
                return sdt;
            }
        }
    }

    //email: bat buoc ket thuc bang @gmail.com va phai co phan ten phia truoc
    private static String docEmail() {
        while (true) {
            String email = docChuoi("Email (phai ket thuc bang @gmail.com, VD ten@gmail.com): ").toLowerCase();
            if (email.matches("[a-z0-9._%+-]+@gmail\\.com")) return email;
            GiaoDien.in("Sai: email phai co dang ten@gmail.com (ten chi gom chu, so va cac ky tu . _ % + -).");
        }
    }

    private static String docChuoiKhongRong(String prompt) {
        while (true) {
            String s = docChuoi(prompt);
            if (!s.isBlank()) return s;
            GiaoDien.in("Khong duoc de trong.");
        }
    }

    //chon 1 trong 4 chuc vu co san
    private static ChucVu docChucVu() {
        GiaoDien.in("Chuc vu: 1. Giam doc | 2. Truong phong | 3. Pho phong | 4. Nhan vien");
        return ChucVu.macDinh(docInt("Chon chuc vu: ", 1, 4));
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
        napTaiKhoan();
        GiaoDien.in("Nap du lieu xong.");
    }

    //doc tai khoan tu file, nhan vien nao chua co tai khoan thi tu tao (mat khau mac dinh)
    private static void napTaiKhoan() {
        dsTK.docFile();
        int them = dsTK.dongBoVoiNhanVien(ql.getDanhSachNV());
        if (them > 0) GiaoDien.in("Da tao them " + them + " tai khoan con thieu (mat khau mac dinh).");
    }

//----------------- khung giao dien & ham nhap -----------------
    //chay 1 chuc nang: mo khung -----, thuc hien (nhap/xuat), dong khung -----
    private static void chay(String tieuDe, Runnable viec) {
        GiaoDien.batDau(tieuDe);
        try {
            viec.run();
        } catch (RuntimeException e) {
            GiaoDien.in("Co loi: " + e.getMessage());
        } finally {
            GiaoDien.ketThuc();
        }
    }

    //hien menu trong khung va doc lua chon
    private static int menu(String tieuDe, String[] muc, int toiDa) {
        GiaoDien.batDau(tieuDe);
        for (String m : muc) GiaoDien.in(m);
        int c = docInt("Chon: ", 0, toiDa);
        GiaoDien.ketThuc();
        return c;
    }

    private static String docChuoi(String prompt) {
        GiaoDien.hoi(prompt);
        return sc.nextLine().trim();
    }

    private static int docInt(String prompt, int min, int max) {
        while (true) {
            try {
                int x = Integer.parseInt(docChuoi(prompt));
                if (x >= min && x <= max) return x;
            } catch (NumberFormatException ignored) {}
            GiaoDien.in("Vui long nhap so nguyen tu " + min + " den " + max + ".");
        }
    }

    private static double docDouble(String prompt, double min) {
        while (true) {
            try {
                double x = Double.parseDouble(docChuoi(prompt));
                if (Double.isFinite(x) && x >= min) return x;
            } catch (NumberFormatException ignored) {}
            GiaoDien.in("Gia tri khong hop le.");
        }
    }

    private static LocalDate docNgay(String prompt) {
        while (true) {
            try { return LocalDate.parse(docChuoi(prompt)); }
            catch (Exception e) { GiaoDien.in("Nhap ngay theo yyyy-MM-dd."); }
        }
    }

    private static LocalTime docTime(String prompt) {
        while (true) {
            try { return LocalTime.parse(docChuoi(prompt)); }
            catch (Exception e) { GiaoDien.in("Nhap gio theo HH:mm."); }
        }
    }

//----------------- PHONG BAN -----------------
    private static void menuPhongBan() {
        int c;
        do {
            c = menu("PHONG BAN", new String[]{
                "1. Them phong ban",
                "2. Xem danh sach",
                "3. Tim phong ban",
                "4. Xoa phong ban",
                "0. Quay lai"}, 4);
            switch (c) {
                case 1: chay("PHONG BAN - THEM MOI", Main::pbThem); break;
                case 2: chay("PHONG BAN - DANH SACH", dsPB::xuatDanhSach); break;
                case 3: chay("PHONG BAN - TIM THEO MA", Main::pbTim); break;
                case 4: chay("PHONG BAN - XOA", Main::pbXoa); break;
            }
        } while (c != 0);
    }

    private static void pbThem() {
        String ma = docChuoi("Ma phong ban: ");
        String ten = docChuoi("Ten phong ban: ");
        String mota = docChuoi("Mo ta: ");
        GiaoDien.in(dsPB.themPhong(new PhongBan(ma, ten, mota)) ? "Them thanh cong." : "Khong the them (trung ma/loi du lieu).");
    }

    private static void pbTim() {
        PhongBan pb = dsPB.timTheoMa(docChuoi("Ma phong ban: "));
        if (pb == null) GiaoDien.in("Khong tim thay.");
        else pb.hienThiThongTin();
    }

    private static void pbXoa() {
        GiaoDien.in(dsPB.xoaTheoMa(docChuoi("Ma can xoa: ")) ? "Da xoa." : "Khong xoa duoc phong ban.");
    }

//----------------- NHAN VIEN -----------------
    private static void menuNhanVien() {
        int c;
        do {
            c = menu("NHAN VIEN", new String[]{
                "1. Them 1 nhan vien",
                "2. Nhap nhieu nhan vien",
                "3. Xem danh sach",
                "4. Tim kiem (chinh xac/gan dung)",
                "5. Sua thong tin nhan vien",
                "6. Xoa nhan vien",
                "7. Thong ke theo loai",
                "8. Thong ke luong",
                "9. Tim theo phong ban",
                "10. Tim theo khoang luong",
                "11. Xem chi tiet 1 nhan vien",
                "0. Quay lai"}, 11);
            switch (c) {
                case 1: chay("NHAN VIEN - THEM MOI", Main::themNhanVien); break;
                case 2: chay("NHAN VIEN - NHAP NHIEU", Main::nvNhapNhieu); break;
                case 3: chay("NHAN VIEN - DANH SACH", ql::xuatDanhSachNV); break;
                case 4: chay("NHAN VIEN - TIM KIEM", Main::nvTimKiem); break;
                case 5: chay("NHAN VIEN - SUA THONG TIN", Main::suaNhanVienTuMenu); break;
                case 6: chay("NHAN VIEN - XOA", Main::nvXoa); break;
                case 7: chay("NHAN VIEN - THONG KE THEO LOAI", ql::thongKeTheoLoaiNv); break;
                case 8: chay("NHAN VIEN - THONG KE LUONG", ql::thongKeLuong); break;
                case 9: chay("NHAN VIEN - TIM THEO PHONG BAN", Main::nvTheoPhongBan); break;
                case 10: chay("NHAN VIEN - TIM THEO KHOANG LUONG", Main::nvTheoLuong); break;
                case 11: chay("NHAN VIEN - XEM CHI TIET", Main::nvChiTiet); break;
            }
        } while (c != 0);
    }

    private static void nvNhapNhieu() {
        int so = docInt("So nhan vien muon nhap: ", 1, 100);
        for (int i = 0; i < so; i++) {
            GiaoDien.in("");
            GiaoDien.in("[Nhap nhan vien " + (i + 1) + "/" + so + "]");
            themNhanVien();
        }
    }

    private static void nvTimKiem() {
        String tuKhoa = docChuoi("Nhap ma, ten, SDT hoac email (co the nhap mot phan): ");
        NhanVien[] ketQua = ql.timKiemUuTien(tuKhoa);
        if (ketQua.length == 0) GiaoDien.in("Khong tim thay nhan vien phu hop.");
        else ql.inKetQua(ketQua);
    }

    private static void nvXoa() {
        String ma = docChuoi("Ma nhan vien can xoa: ");
        if (nguoiDangNhap != null && nguoiDangNhap.getMaNV().equalsIgnoreCase(ma)) {
            GiaoDien.in("Khong the xoa chinh tai khoan dang dang nhap.");
            return;
        }
        if (ql.xoaNhanVien(ma)) {
            dsTK.xoaTheoTen(ma);
            GiaoDien.in("Da xoa nhan vien va tai khoan cua nhan vien.");
        } else {
            GiaoDien.in("Khong tim thay.");
        }
    }

    private static void nvTheoPhongBan() {
        dsPB.xuatDanhSach();
        NhanVien[] theoPB = ql.timKiemTheoPhongBan(docChuoi("Ma phong ban: "));
        if (theoPB.length == 0) GiaoDien.in("Khong co nhan vien nao trong phong ban nay.");
        else ql.inKetQua(theoPB);
    }

    private static void nvTheoLuong() {
        double tuLuong = docDouble("Luong tu: ", 0);
        double denLuong = docDouble("Luong den: ", 0);
        NhanVien[] theoLuong = ql.timKiemTheoLuong(tuLuong, denLuong);
        if (theoLuong.length == 0) GiaoDien.in("Khong co nhan vien nao trong khoang luong nay.");
        else ql.inKetQua(theoLuong);
    }

    private static void nvChiTiet() {
        NhanVien nv = ql.timTheoMa(docChuoi("Ma nhan vien: "));
        if (nv == null) GiaoDien.in("Khong tim thay nhan vien.");
        else nv.hienThiThongTin();
    }

    private static void themNhanVien() {
        int loai = docInt("Loai NV (1: Toan thoi gian, 2: Ban thoi gian, 3: Hop dong): ", 1, 3);
        ChucVu chucVu = docChucVu();
        String ma = MaNhanVien.taoMaMoi(MaNhanVien.tienTo(loai, chucVu), ql.getDanhSachNV());
        GiaoDien.in("Ma NV duoc cap tu dong: " + ma);
        String ten = docChuoi("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();
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
        GiaoDien.in("");
        GiaoDien.in("Danh sach phong ban hien co:");
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban (bo trong neu chua phan): ");
        if (!maPB.isBlank()) {
            PhongBan pb = dsPB.timTheoMa(maPB);
            if (pb == null) { GiaoDien.in("Khong tim thay phong ban; nhan vien chua duoc gan phong."); }
            else nv.setPhongBan(pb);
        }
        nv.setChucVu(chucVu);
        String coTD = docChuoi("Co muon them trinh do chuyen mon? (y/n): ");
        if (coTD.equalsIgnoreCase("y")) {
            nv.setTrinhDo(docDanhSachTrinhDo());
        }
        if (ql.themNhanVien(nv)) {
            String matKhau = MaNhanVien.matKhauMacDinh(ma);
            dsTK.them(new TaiKhoan(ma, matKhau));
            GiaoDien.in("Them nhan vien thanh cong.");
            GiaoDien.in("Tai khoan: " + ma + "  |  Mat khau: " + matKhau);
        } else {
            GiaoDien.in("Them that bai (ma NV trung hoac danh sach loi).");
        }
    }

    private static void suaNhanVienTuMenu() {
        String ma = docChuoi("Ma nhan vien can sua: ");
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) {
            GiaoDien.in("Khong tim thay nhan vien.");
            return;
        }
        GiaoDien.in("Thong tin hien tai:");
        nv.hienThiThongTin();
        GiaoDien.in("");
        GiaoDien.in("Nhap thong tin moi:");
        String ten = docChuoi("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();
        double luong = docDouble("Luong co ban: ", 0);
        LocalDate vaoLam = docNgay("Ngay vao lam (yyyy-MM-dd): ");
        String trangThai = docChuoi("Trang thai: ");
        GiaoDien.in("");
        GiaoDien.in("Danh sach phong ban hien co:");
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban moi (bo trong neu chua phan): ");
        PhongBan pb = maPB.isBlank() ? null : dsPB.timTheoMa(maPB);
        if (!maPB.isBlank() && pb == null) {
            GiaoDien.in("Ma phong ban khong ton tai; huy cap nhat.");
            return;
        }
        try {
            boolean ok = ql.capNhatThongTinCoBan(ma, ten, ns, gt, sdt, email,
                    luong, vaoLam, trangThai, pb);
            GiaoDien.in(ok ? "Cap nhat thanh cong." : "Cap nhat that bai.");
        } catch (IllegalArgumentException e) {
            GiaoDien.in("Du lieu khong hop le: " + e.getMessage());
            return;
        }
        String doiCV = docChuoi("Co muon doi chuc vu? (y/n): ");
        if (doiCV.equalsIgnoreCase("y")) {
            nv.setChucVu(docChucVu());
            GiaoDien.in("Da doi chuc vu (ma NV van giu nguyen: " + nv.getMaNV() + ").");
        }
        String doiTD = docChuoi("Co muon nhap lai trinh do chuyen mon? (y/n): ");
        if (doiTD.equalsIgnoreCase("y")) {
            nv.setTrinhDo(docDanhSachTrinhDo());
            GiaoDien.in("Da cap nhat trinh do.");
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
                GiaoDien.in("Du lieu khong hop le: " + e.getMessage());
            }
        } while (docChuoi("Them trinh do khac? (y/n): ").equalsIgnoreCase("y"));
        return ds;
    }

//----------------- CHAM CONG -----------------
    private static void menuChamCong() {
        int c;
        do {
            c = menu("CHAM CONG", new String[]{
                "1. Them cham cong",
                "2. Xem danh sach",
                "3. Xoa cham cong",
                "4. Thong ke gio theo nhan vien/thang",
                "0. Quay lai"}, 4);
            switch (c) {
                case 1: chay("CHAM CONG - THEM MOI", Main::ccThem); break;
                case 2: chay("CHAM CONG - DANH SACH", dsCC::xuatDanhSach); break;
                case 3: chay("CHAM CONG - XOA", Main::ccXoa); break;
                case 4: chay("CHAM CONG - THONG KE THEO THANG", Main::ccThongKe); break;
            }
        } while (c != 0);
    }

    private static void ccThem() {
        String maCC = docChuoi("Ma cham cong: ");
        String maNV = docChuoi("Ma nhan vien: ");
        LocalDate ngay = docNgay("Ngay (yyyy-MM-dd): ");
        LocalTime vao = docTime("Gio vao (HH:mm): ");
        LocalTime ra = docTime("Gio ra (HH:mm): ");
        if (!ra.isAfter(vao)) GiaoDien.in("Gio ra phai sau gio vao.");
        else GiaoDien.in(dsCC.them(new ChamCong(maCC, maNV, ngay, vao, ra)) ? "Da them cham cong." : "Them that bai.");
    }

    private static void ccXoa() {
        GiaoDien.in(dsCC.xoaTheoMa(docChuoi("Ma cham cong: ")) ? "Da xoa." : "Khong tim thay.");
    }

    private static void ccThongKe() {
        String id = docChuoi("Ma NV: ");
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        List<String[]> dong = new ArrayList<>();
        dong.add(new String[]{"Nhan vien", id});
        dong.add(new String[]{"Thang / nam", thang + "/" + nam});
        dong.add(new String[]{"So ngay di lam", String.valueOf(dsCC.thongKeSoNgayDiLam(id, thang, nam))});
        dong.add(new String[]{"Gio lam thuong", String.format("%.2f", dsCC.thongKeGioLam(id, thang, nam))});
        dong.add(new String[]{"Gio tang ca", String.format("%.2f", dsCC.thongKeGioTangCa(id, thang, nam))});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- BAO HIEM -----------------
    private static void menuBaoHiem() {
        int c;
        do {
            c = menu("BAO HIEM", new String[]{
                "1. Them BHXH",
                "2. Them BHYT",
                "3. Xem danh sach",
                "4. Tim theo ma NV",
                "5. Xoa theo ma BH",
                "6. Tinh tien dong",
                "0. Quay lai"}, 6);
            switch (c) {
                case 1: chay("BAO HIEM - THEM BHXH", Main::bhThemXH); break;
                case 2: chay("BAO HIEM - THEM BHYT", Main::bhThemYT); break;
                case 3: chay("BAO HIEM - DANH SACH", dsBH::xuatDanhSach); break;
                case 4: chay("BAO HIEM - TIM THEO MA NV", Main::bhTim); break;
                case 5: chay("BAO HIEM - XOA", Main::bhXoa); break;
                case 6: chay("BAO HIEM - TINH TIEN DONG", Main::bhTinhTien); break;
            }
        } while (c != 0);
    }

    private static void bhThemXH() {
        String maBH = docChuoi("Ma BH: ");
        String maNV = docChuoi("Ma NV: ");
        String soSo = docChuoi("So so BHXH: ");
        GiaoDien.in(dsBH.them(new BaoHiemXaHoi(maBH, maNV, soSo, LocalDate.now())) ? "Da them." : "Them that bai.");
    }

    private static void bhThemYT() {
        String maBH = docChuoi("Ma BH: ");
        String maNV = docChuoi("Ma NV: ");
        String the = docChuoi("So the BHYT: ");
        String noi = docChuoi("Noi KCB: ");
        LocalDate hanThe = docNgay("Han the (yyyy-MM-dd): ");
        GiaoDien.in(dsBH.them(new BaoHiemYTe(maBH, maNV, the, LocalDate.now(), noi, hanThe)) ? "Da them." : "Them that bai.");
    }

    private static void bhTim() {
        BaoHiem[] kq = dsBH.timTheoMaNV(docChuoi("Ma NV: "));
        if (kq.length == 0) GiaoDien.in("Khong co du lieu.");
        else DanhSachBaoHiem.inBang(kq);
    }

    private static void bhXoa() {
        GiaoDien.in(dsBH.xoaTheoMa(docChuoi("Ma BH: ")) ? "Da xoa." : "Khong tim thay.");
    }

    private static void bhTinhTien() {
        String maNV = docChuoi("Ma NV: ");
        double mucLuong = docDouble("Luong dong bao hiem: ", 0);
        List<String[]> dong = new ArrayList<>();
        dong.add(new String[]{"Luong dong bao hiem", GiaoDien.tien(mucLuong) + " VND"});
        dong.add(new String[]{"Nhan vien dong", GiaoDien.tien(dsBH.tongTienNhanVienDong(maNV, mucLuong)) + " VND"});
        dong.add(new String[]{"Doanh nghiep dong", GiaoDien.tien(dsBH.tongTienDoanhNghiepDong(maNV, mucLuong)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- BANG LUONG -----------------
    private static void menuBangLuong() {
        int c;
        do {
            c = menu("BANG LUONG", new String[]{
                "1. Lap bang luong cho tat ca nhan vien",
                "2. Xem bang luong theo ma NV",
                "3. Xem bang luong cong ty theo thang",
                "4. Thong ke quy luong",
                "0. Quay lai"}, 4);
            switch (c) {
                case 1: chay("BANG LUONG - LAP CHO TAT CA", Main::blLap); break;
                case 2: chay("BANG LUONG - THEO MA NHAN VIEN", Main::blTheoNV); break;
                case 3: chay("BANG LUONG - CONG TY THEO THANG", Main::blCongTy); break;
                case 4: chay("BANG LUONG - THONG KE QUY LUONG", Main::blThongKe); break;
            }
        } while (c != 0);
    }

    private static void blLap() {
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        dsBL.lapBangLuongChoTatCa(ql.getDanhSachNV(), thang, nam, dsTP, dsBH);
        dsBL.ghiFile();
        GiaoDien.in("Da lap bang luong.");
    }

    private static void blTheoNV() {
        dsBL.xuatBangLuongNhanVien(docChuoi("Ma NV: "));
    }

    private static void blCongTy() {
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        dsBL.xuatBangLuongCongTy(thang, nam);
    }

    private static void blThongKe() {
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        List<String[]> dong = new ArrayList<>();
        dong.add(new String[]{"Thang / nam", thang + "/" + nam});
        dong.add(new String[]{"Tong quy luong", GiaoDien.tien(dsBL.tongQuyLuong(thang, nam)) + " VND"});
        dong.add(new String[]{"Luong trung binh", GiaoDien.tien(dsBL.luongTrungBinh(thang, nam)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- BAO CAO -----------------
    private static void menuBaoCao() {
        int c;
        do {
            c = menu("BAO CAO", new String[]{
                "1. Bao cao toan cong ty",
                "2. Bao cao theo phong ban",
                "3. Thong ke nhan su",
                "4. Thong ke bao hiem",
                "0. Quay lai"}, 4);
            switch (c) {
                case 1: chay("BAO CAO - TOAN CONG TY", Main::bcCongTy); break;
                case 2: chay("BAO CAO - THEO PHONG BAN", Main::bcPhongBan); break;
                case 3: chay("BAO CAO - THONG KE NHAN SU", () -> baoCao.thongKeNhanSu(ql, dsBL)); break;
                case 4: chay("BAO CAO - THONG KE BAO HIEM", baoCao::thongKeBaoHiem); break;
            }
        } while (c != 0);
    }

    private static void bcCongTy() {
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        GiaoDien.in("");
        baoCao.inBaoCaoToanCongTy(ql, dsBL, thang, nam);
    }

    private static void bcPhongBan() {
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban: ");
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        GiaoDien.in("");
        baoCao.inBaoCaoTheoPhong(maPB, ql, dsBL, thang, nam);
    }

//----------------- THUONG PHAT -----------------
    private static void menuThuongPhat() {
        int c;
        do {
            c = menu("QUAN LY THUONG PHAT", new String[]{
                "1. Them thuong/phat thu cong",
                "2. Xem tat ca",
                "3. Xem theo ma nhan vien",
                "4. Xoa theo ma thuong/phat",
                "5. Thong ke thuong/phat theo thang",
                "6. Tu dong xet thuong/phat theo quy dinh",
                "0. Quay lai"}, 6);
            switch (c) {
                case 1: chay("THUONG PHAT - THEM THU CONG", Main::tpThem); break;
                case 2: chay("THUONG PHAT - DANH SACH", dsTP::xuatDanhSach); break;
                case 3: chay("THUONG PHAT - THEO MA NHAN VIEN", Main::tpTheoNV); break;
                case 4: chay("THUONG PHAT - XOA", Main::tpXoa); break;
                case 5: chay("THUONG PHAT - THONG KE THEO THANG", Main::tpThongKe); break;
                case 6: chay("THUONG PHAT - TU DONG XET", Main::tpTuDongXet); break;
            }
        } while (c != 0);
    }

    private static void tpThem() {
        String maTP = docChuoi("Ma thuong/phat: ");
        String maNV = docChuoi("Ma nhan vien: ");
        if (ql.timTheoMa(maNV) == null) {
            GiaoDien.in("Nhan vien khong ton tai.");
            return;
        }
        String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
        if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
            GiaoDien.in("Loai chi nhan THUONG hoac PHAT.");
            return;
        }
        String lyDo = docChuoi("Ly do: ");
        double tien = docDouble("So tien: ", 0);
        ThuongPhat tp = new ThuongPhat(maTP, maNV, "", loai, lyDo, 1, tien, LocalDate.now());
        GiaoDien.in(dsTP.them(tp) ? "Da them thuong/phat." : "Them that bai (ma trung).");
    }

    private static void tpTheoNV() {
        ThuongPhat[] ds = dsTP.timTheoMaNV(docChuoi("Ma nhan vien: "));
        if (ds.length == 0) GiaoDien.in("Khong co du lieu.");
        else DanhSachThuongPhat.inBang(ds);
    }

    private static void tpXoa() {
        GiaoDien.in(dsTP.xoaTheoMa(docChuoi("Ma thuong/phat: ")) ? "Da xoa." : "Khong tim thay.");
    }

    private static void tpThongKe() {
        String maNV = docChuoi("Ma nhan vien: ");
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        List<String[]> dong = new ArrayList<>();
        dong.add(new String[]{"Nhan vien", maNV});
        dong.add(new String[]{"Thang / nam", thang + "/" + nam});
        dong.add(new String[]{"Tong thuong", GiaoDien.tien(dsTP.tongThuong(maNV, thang, nam)) + " VND"});
        dong.add(new String[]{"Tong phat", GiaoDien.tien(dsTP.tongPhat(maNV, thang, nam)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

    private static void tpTuDongXet() {
        int thang = docInt("Thang can xet: ", 1, 12);
        int nam = docInt("Nam can xet: ", 2000, 3000);
        dsTP.tuDongXetThuongPhat(thang, nam, ql.getDanhSachNV(),
                dsQD.getDanhSach(), dsCC, dsNP.getDanhSach());
        dsTP.ghiFile();
        GiaoDien.in("Da tu dong xet thuong/phat theo cac quy dinh hien co.");
    }

//----------------- NGHI PHEP -----------------
    private static void menuNghiPhep() {
        int c;
        do {
            c = menu("QUAN LY NGHI PHEP", new String[]{
                "1. Tao don nghi phep",
                "2. Xem tat ca don",
                "3. Xem don theo nhan vien",
                "4. Duyet don",
                "5. Xoa don",
                "0. Quay lai"}, 5);
            switch (c) {
                case 1: chay("NGHI PHEP - TAO DON", Main::npTao); break;
                case 2: chay("NGHI PHEP - DANH SACH", dsNP::xuatDanhSach); break;
                case 3: chay("NGHI PHEP - THEO NHAN VIEN", Main::npTheoNV); break;
                case 4: chay("NGHI PHEP - DUYET DON", Main::npDuyet); break;
                case 5: chay("NGHI PHEP - XOA DON", Main::npXoa); break;
            }
        } while (c != 0);
    }

    private static void npTao() {
        String maNP = docChuoi("Ma don: ");
        String maNV = docChuoi("Ma nhan vien: ");
        if (ql.timTheoMa(maNV) == null) {
            GiaoDien.in("Nhan vien khong ton tai.");
            return;
        }
        LocalDate tuNgay = docNgay("Tu ngay (yyyy-MM-dd): ");
        LocalDate denNgay = docNgay("Den ngay (yyyy-MM-dd): ");
        String lyDo = docChuoi("Ly do: ");
        NghiPhep np = new NghiPhep(maNP, maNV, tuNgay, denNgay, lyDo);
        String loai = docChuoi("Loai nghi (CO_PHEP/KHONG_PHEP): ");
        np.setLoaiNghi(loai.equalsIgnoreCase("KHONG_PHEP") ? "Khong phep" : "Co phep");
        GiaoDien.in(dsNP.them(np) ? "Da tao don nghi phep." : "Tao don that bai (kiem tra ma/ngay).");
    }

    private static void npTheoNV() {
        NghiPhep[] ds = dsNP.timTheoMaNV(docChuoi("Ma nhan vien: "));
        if (ds.length == 0) GiaoDien.in("Khong co don.");
        else DanhSachNghiPhep.inBang(ds);
    }

    private static void npDuyet() {
        GiaoDien.in(dsNP.duyetTheoMa(docChuoi("Ma don can duyet: ")) ? "Da duyet." : "Khong tim thay don.");
    }

    private static void npXoa() {
        GiaoDien.in(dsNP.xoaTheoMa(docChuoi("Ma don can xoa: ")) ? "Da xoa." : "Khong tim thay don.");
    }

//----------------- QUY DINH THUONG PHAT -----------------
    private static void menuQuyDinhThuongPhat() {
        int c;
        do {
            c = menu("QUY DINH THUONG PHAT", new String[]{
                "1. Them quy dinh",
                "2. Xem danh sach quy dinh",
                "3. Xoa quy dinh",
                "0. Quay lai"}, 3);
            switch (c) {
                case 1: chay("QUY DINH - THEM MOI", Main::qdThem); break;
                case 2: chay("QUY DINH - DANH SACH", dsQD::xuatDanhSach); break;
                case 3: chay("QUY DINH - XOA", Main::qdXoa); break;
            }
        } while (c != 0);
    }

    private static void qdThem() {
        String ma = docChuoi("Ma quy dinh: ");
        String ten = docChuoi("Ten quy dinh: ");
        String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
        if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
            GiaoDien.in("Loai khong hop le.");
            return;
        }
        String dieuKien = docChuoi("Dieu kien (>=, >, <=, <, ==): ");
        if (!dieuKien.equals(">=") && !dieuKien.equals(">") && !dieuKien.equals("<=")
                && !dieuKien.equals("<") && !dieuKien.equals("==")) {
            GiaoDien.in("Dieu kien khong hop le.");
            return;
        }
        double nguong = docDouble("Nguong: ", 0);
        double mucTien = docDouble("Muc tien tren moi don vi: ", 0);
        GiaoDien.in("Don vi: GIO_TANG_CA / NGAY_DI_LAM / NGAY_NGHI_KHONG_PHEP");
        String donVi = docChuoi("Don vi: ").toUpperCase();
        if (!donVi.equals(QuyDinhThuongPhat.GIO_TANG_CA)
                && !donVi.equals(QuyDinhThuongPhat.NGAY_DI_LAM)
                && !donVi.equals(QuyDinhThuongPhat.NGAY_NGHI_KHONG_PHEP)) {
            GiaoDien.in("Don vi khong hop le.");
            return;
        }
        QuyDinhThuongPhat qd = new QuyDinhThuongPhat(ma, ten, loai, dieuKien, nguong, mucTien, donVi);
        GiaoDien.in(dsQD.them(qd) ? "Da them quy dinh." : "Them that bai (ma trung).");
    }

    private static void qdXoa() {
        GiaoDien.in(dsQD.xoaTheoMa(docChuoi("Ma quy dinh: ")) ? "Da xoa." : "Khong tim thay.");
    }

//----------------- LUU / DOC / THOAT -----------------
    private static void dangXuat() {
        hoiLuuTruocKhiDangXuat();
        GiaoDien.in("Da dang xuat. Quay lai man hinh dang nhap.");
    }

    private static void hoiLuuTruocKhiDangXuat() {
        String tl = docChuoi("Luu toan bo du lieu truoc khi dang xuat? (y/n): ");
        if (tl.equalsIgnoreCase("y")) luuDuLieu();
        else GiaoDien.in("Khong luu. Cac thay doi chua luu se bi mat.");
    }

    private static void luuDuLieu() {
        dsPB.ghiFile(); ql.ghiFile(); dsCC.ghiFile(); dsBH.ghiFile();
        dsTP.ghiFile(); dsNP.ghiFile(); dsQD.ghiFile(); dsBL.ghiFile(); dsTK.ghiFile();
        GiaoDien.in("Da luu toan bo du lieu vao thu muc data.");
    }

    private static void docDuLieu() {
        dsPB.docFile(); ql.docFile(); dsCC.docFile(); dsBH.docFile();
        dsTP.docFile(); dsNP.docFile(); dsQD.docFile(); dsBL.docFile();
        napTaiKhoan();
        if (nguoiDangNhap != null) nguoiDangNhap = ql.timTheoMa(nguoiDangNhap.getMaNV());   //lam moi nguoi dang nhap theo du lieu vua doc
        GiaoDien.in("Da doc xong du lieu tu file.");
    }
}