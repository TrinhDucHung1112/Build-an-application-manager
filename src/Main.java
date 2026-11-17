import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chuong trinh chinh: dang nhap -> menu theo vai tro (xem PhanQuyen, VaiTro, Quyen).
 * Moi thao tac nhay cam deu: (1) an menu khong duoc phep, (2) kiem tra lai quyen trong ham xu ly
 * (yeuCau), (3) ghi nhat ky (dsNK).
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
    private static final DanhSachNhatKy dsNK = new DanhSachNhatKy(500);

    //phien dang nhap hien tai (null khi chua dang nhap) - dung cho phan quyen
    private static PhienDangNhap phien = null;
    //canh bao hop dong lao dong sap het han trong bao nhieu ngay
    private static final int SO_NGAY_CANH_BAO_HOP_DONG = 30;

    //1 muc trong menu dong: ten hien thi + viec can lam (menu chi chua cac muc nguoi dung duoc phep)
    private static final class Muc {
        final String ten;
        final Runnable viec;

        Muc(String ten, Runnable viec) {
            this.ten = ten;
            this.viec = viec;
        }
    }

    public static void main(String[] args) {
        chay("KHOI DONG - NAP DU LIEU", Main::napDuLieu);
        boolean tiepTuc = true;
        while (tiepTuc) {
            phien = null;
            int chon = menu("QUAN LY NHAN SU - DANG NHAP", new String[]{
                "1. Dang nhap",
                "2. Dang ky tai khoan moi",
                "0. Thoat"}, 2);
            switch (chon) {
                case 1:
                    chay("DANG NHAP", Main::dangNhap);
                    if (phien != null) menuChinh();
                    break;
                case 2: chay("DANG KY TAI KHOAN MOI", Main::dangKy); break;
                case 0: chay("THOAT CHUONG TRINH", () -> GiaoDien.in("Da dong chuong trinh.")); tiepTuc = false; break;
            }
        }
    }

//----------------- TIEN ICH PHAN QUYEN -----------------
    private static NhanVien nguoi() {
        return phien.getNguoiDung();
    }

    private static boolean co(Quyen q) {
        return phien != null && PhanQuyen.coQuyen(nguoi(), q);
    }

    private static PhamVi phamVi(Quyen q) {
        return phien == null ? PhamVi.KHONG : PhanQuyen.phamVi(nguoi(), q);
    }

    //quyen nay ap dung rong hon "chi ban than" (phong ban hoac toan cong ty)
    private static boolean rongHon(Quyen q) {
        return phamVi(q).ordinal() > PhamVi.BAN_THAN.ordinal();
    }

    private static boolean choPhep(Quyen q, NhanVien doiTuong) {
        return phien != null && PhanQuyen.choPhep(nguoi(), q, doiTuong);
    }

    //thao tac khong co doi tuong cu the: khong du quyen thi nem ngoai le + ghi nhat ky TU CHOI
    private static void yeuCau(Quyen q) {
        try {
            PhanQuyen.yeuCau(nguoi(), q);
        } catch (KhongCoQuyenException e) {
            ghiNhatKy(q.getMoTa(), "-", NhatKyHeThong.TU_CHOI);
            throw e;
        }
    }

    //thao tac len 1 nhan vien cu the
    private static void yeuCau(Quyen q, NhanVien doiTuong) {
        try {
            PhanQuyen.yeuCau(nguoi(), q, doiTuong);
        } catch (KhongCoQuyenException e) {
            ghiNhatKy(q.getMoTa(), doiTuong == null ? "-" : doiTuong.getMaNV(), NhatKyHeThong.TU_CHOI);
            throw e;
        }
    }

    //thao tac len du lieu cua nhan vien co ma maNV (don nghi phep, cham cong...): nhan vien da xoa thi chi pham vi toan cong ty duoc phep
    private static void yeuCauTheoMaNV(Quyen q, String maNV) {
        NhanVien chu = ql.timTheoMa(maNV);
        if (chu != null) {
            yeuCau(q, chu);
        } else if (phamVi(q) != PhamVi.TAT_CA) {
            ghiNhatKy(q.getMoTa(), maNV, NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Du lieu nay khong thuoc pham vi cua ban.");
        }
    }

    private static void ghiNhatKy(String hanhDong, String doiTuong) {
        ghiNhatKy(hanhDong, doiTuong, NhatKyHeThong.THANH_CONG);
    }

    private static void ghiNhatKy(String hanhDong, String doiTuong, String ketQua) {
        dsNK.ghi(phien == null ? "-" : phien.getMaNV(), hanhDong, doiTuong, ketQua);
    }

    //danh sach nhan vien ma nguoi dang nhap duoc phep thuc hien quyen q
    private static NhanVien[] nvTrongQuyen(Quyen q) {
        return PhanQuyen.loc(nguoi(), q, ql.getDanhSachNV());
    }

    //nhan vien dich cua thao tac: neu quyen chi o pham vi BAN_THAN thi la chinh minh, nguoc lai hoi ma nhan vien
    private static NhanVien docNhanVienDich(Quyen q, String loiNhac) {
        if (phamVi(q) == PhamVi.BAN_THAN) return nguoi();
        String ma = docChuoi(loiNhac);
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) throw new IllegalArgumentException("Khong tim thay nhan vien " + ma);
        return nv;
    }

    //chi con 1 giam doc dang hoat dong thi khong duoc xoa / cho nghi / khoa (tranh he thong khong con ai quan tri)
    private static boolean laGiamDocCuoi(NhanVien nv) {
        if (PhanQuyen.xacDinhVaiTro(nv) != VaiTro.GIAM_DOC) return false;
        int dem = 0;
        for (NhanVien x : ql.getDanhSachNV()) {
            if (x == null || PhanQuyen.xacDinhVaiTro(x) != VaiTro.GIAM_DOC) continue;
            if (x.getTrangThai().toLowerCase().startsWith("nghi")) continue;
            TaiKhoan tk = dsTK.timTheoTen(x.getMaNV());
            if (tk != null && tk.dangHoatDong()) dem++;
        }
        return dem <= 1;
    }

//----------------- MENU THEO VAI TRO -----------------
    private static Muc muc(String ten, String tieuDe, Runnable viec) {
        return new Muc(ten, () -> chay(tieuDe, viec));
    }

    //hien menu dong. Chon 0: chay khiThoat (neu co) roi thoat menu
    private static void chayMenu(String tieuDe, List<Muc> cacMuc, String nhanThoat, Runnable khiThoat) {
        int chon;
        do {
            String[] nhan = new String[cacMuc.size() + 1];
            for (int i = 0; i < cacMuc.size(); i++) nhan[i] = (i + 1) + ". " + cacMuc.get(i).ten;
            nhan[cacMuc.size()] = "0. " + nhanThoat;
            chon = menu(tieuDe, nhan, cacMuc.size());
            if (chon == 0) {
                if (khiThoat != null) khiThoat.run();
            } else {
                cacMuc.get(chon - 1).viec.run();
            }
        } while (chon != 0);
    }

    //menu chinh sau khi dang nhap. Giam doc/truong phong/pho phong: menu quan ly (chi hien muc duoc phep);
    //nhan vien: menu ca nhan. Chon 0 = dang xuat
    private static void menuChinh() {
        if (!phien.getVaiTro().laQuanLy()) {
            menuCaNhan(true);
            return;
        }
        List<Muc> m = new ArrayList<>();
        m.add(new Muc("Thong tin ca nhan cua toi (ho so, cham cong, nghi phep, luong...)", () -> menuCaNhan(false)));
        if (co(Quyen.XEM_PHONG_BAN)) m.add(new Muc("Phong ban", Main::menuPhongBan));
        if (co(Quyen.XEM_NHAN_VIEN)) m.add(new Muc("Quan ly nhan vien", Main::menuNhanVien));
        if (co(Quyen.XEM_CHAM_CONG)) m.add(new Muc("Quan ly cham cong", Main::menuChamCong));
        if (rongHon(Quyen.XEM_BAO_HIEM) || co(Quyen.QUAN_LY_BAO_HIEM)) m.add(new Muc("Quan ly bao hiem", Main::menuBaoHiem));
        if (rongHon(Quyen.XEM_LUONG) || co(Quyen.LAP_BANG_LUONG)) m.add(new Muc("Bang luong", Main::menuBangLuong));
        if (co(Quyen.XEM_BAO_CAO_CONG_TY) || co(Quyen.XEM_BAO_CAO_PHONG)) m.add(new Muc("Bao cao", Main::menuBaoCao));
        if (rongHon(Quyen.XEM_THUONG_PHAT) || co(Quyen.THEM_THUONG_PHAT)) m.add(new Muc("Quan ly thuong phat", Main::menuThuongPhat));
        if (rongHon(Quyen.XEM_NGHI_PHEP)) m.add(new Muc("Quan ly nghi phep", Main::menuNghiPhep));
        m.add(new Muc("Quy dinh thuong phat", Main::menuQuyDinhThuongPhat));
        if (co(Quyen.QUAN_LY_TAI_KHOAN)) m.add(new Muc("Quan ly tai khoan", Main::menuTaiKhoan));
        if (co(Quyen.XEM_NHAT_KY)) m.add(new Muc("Nhat ky he thong", Main::menuNhatKy));
        m.add(muc("Doi mat khau", "DOI MAT KHAU", Main::doiMatKhau));
        m.add(muc("Luu toan bo du lieu", "LUU TOAN BO DU LIEU", Main::luuDuLieu));
        if (co(Quyen.DOC_DU_LIEU)) m.add(muc("Doc du lieu tu file", "DOC DU LIEU TU FILE", Main::docDuLieu));
        chayMenu("QUAN LY NHAN SU - " + phien.getVaiTro().ten().toUpperCase() + " (" + phien.getMaNV() + ")",
                m, "Dang xuat (ve man hinh dang nhap)", () -> chay("DANG XUAT", Main::dangXuat));
    }

    //menu ca nhan: moi nguoi chi thay du lieu cua chinh minh. laMenuGoc=true (nhan vien): chon 0 la dang xuat
    private static void menuCaNhan(boolean laMenuGoc) {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Ho so cua toi", "HO SO CUA TOI", Main::cnHoSo));
        if (co(Quyen.XEM_PHONG_BAN)) m.add(muc("Phong ban cua toi", "PHONG BAN CUA TOI", Main::pbXemDanhSach));
        m.add(muc("Cham cong cua toi", "CHAM CONG CUA TOI", Main::cnChamCong));
        if (co(Quyen.TU_CHAM_CONG)) m.add(muc("Cham cong hom nay (tu cham cong)", "TU CHAM CONG HOM NAY", Main::ccTuChamCong));
        m.add(muc("Thong ke gio lam cua toi theo thang", "THONG KE GIO LAM CUA TOI", () -> ccThongKeCho(nguoi())));
        m.add(new Muc("Nghi phep cua toi", Main::menuNghiPhepCuaToi));
        m.add(muc("Bang luong cua toi", "BANG LUONG CUA TOI", Main::cnBangLuong));
        m.add(muc("Bao hiem cua toi", "BAO HIEM CUA TOI", () -> bhTimCho(nguoi())));
        m.add(muc("Thuong/phat cua toi", "THUONG PHAT CUA TOI", () -> tpTheoNVCho(nguoi())));
        if (co(Quyen.XEM_HOP_DONG)) m.add(muc("Hop dong lao dong cua toi", "HOP DONG CUA TOI", Main::cnHopDong));
        m.add(muc("Quy dinh thuong phat", "QUY DINH THUONG PHAT", dsQD::xuatDanhSach));
        m.add(muc("Doi mat khau", "DOI MAT KHAU", Main::doiMatKhau));
        if (laMenuGoc) m.add(muc("Luu du lieu", "LUU DU LIEU", Main::luuDuLieu));
        String tieuDe = laMenuGoc
                ? "CONG THONG TIN NHAN VIEN - " + phien.getVaiTro().ten().toUpperCase() + " (" + phien.getMaNV() + ")"
                : "THONG TIN CA NHAN (" + phien.getMaNV() + ")";
        chayMenu(tieuDe, m, laMenuGoc ? "Dang xuat (ve man hinh dang nhap)" : "Quay lai",
                laMenuGoc ? () -> chay("DANG XUAT", Main::dangXuat) : null);
    }

//----------------- DANG NHAP / DANG KY -----------------
    //tai khoan = ma nhan vien, mat khau mac dinh = phan so + phan chu cua ma (VD: TP001 -> 001TP), phai doi o lan dang nhap dau
    private static void dangNhap() {
        for (int lan = 1; lan <= 3; lan++) {
            String taiKhoan = docChuoi("Tai khoan (ma nhan vien): ");
            String matKhau = docChuoi("Mat khau: ");
            DanhSachTaiKhoan.KetQuaDangNhap kq = dsTK.xacThuc(taiKhoan, matKhau);
            if (kq == DanhSachTaiKhoan.KetQuaDangNhap.SAI_THONG_TIN) {
                dsNK.ghi(taiKhoan.toUpperCase(), "Dang nhap", "-", NhatKyHeThong.THAT_BAI);
                GiaoDien.in("Sai tai khoan hoac mat khau (lan " + lan + "/3).");
                continue;
            }
            if (kq == DanhSachTaiKhoan.KetQuaDangNhap.BI_KHOA) {
                dsNK.ghi(taiKhoan.toUpperCase(), "Dang nhap vao tai khoan bi khoa", "-", NhatKyHeThong.TU_CHOI);
                dsTK.ghiFile();
                GiaoDien.in("Tai khoan dang bi KHOA (nhap sai mat khau qua " + TaiKhoan.SO_LAN_SAI_TOI_DA
                        + " lan hoac bi khoa). Vui long lien he giam doc de mo khoa.");
                return;
            }
            if (kq == DanhSachTaiKhoan.KetQuaDangNhap.CHO_DUYET) {
                GiaoDien.in("Tai khoan cua ban dang CHO GIAM DOC DUYET. Vui long quay lai sau.");
                return;
            }
            TaiKhoan tk = dsTK.timTheoTen(taiKhoan);
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            if (nv == null) {
                GiaoDien.in("Tai khoan nay khong con gan voi nhan vien nao.");
                return;
            }
            if (nv.getTrangThai().toLowerCase().startsWith("nghi")) {
                GiaoDien.in("Tai khoan da bi khoa vi nhan vien da nghi viec.");
                return;
            }
            if (tk.isPhaiDoiMatKhau()) {
                GiaoDien.in("Day la lan dang nhap dau (hoac mat khau vua duoc dat lai): ban phai doi mat khau.");
                if (!batBuocDoiMatKhau(tk.getTenDangNhap(), matKhau)) {
                    GiaoDien.in("Chua doi mat khau, dang nhap bi huy.");
                    return;
                }
            }
            phien = new PhienDangNhap(nv);
            ghiNhatKy("Dang nhap", nv.getMaNV());
            String tenCV = nv.getChucVu() == null ? "chua co chuc vu" : nv.getChucVu().getTenCV();
            GiaoDien.in("");
            GiaoDien.in("Dang nhap thanh cong. Xin chao " + nv.getHoTen() + " (" + tenCV + " - vai tro: "
                    + phien.getVaiTro().ten() + ").");
            canhBaoHopDongSapHet();
            return;
        }
        GiaoDien.in("Nhap sai qua 3 lan, quay lai man hinh dau.");
    }

    //bat buoc doi mat khau (toi da 3 lan thu). Thanh cong -> luu file tai khoan ngay
    private static boolean batBuocDoiMatKhau(String tenDangNhap, String matKhauHienTai) {
        for (int lan = 1; lan <= 3; lan++) {
            String moi = docChuoi("Mat khau moi (>= " + DanhSachTaiKhoan.DO_DAI_MAT_KHAU_TOI_THIEU + " ky tu): ");
            String lai = docChuoi("Nhap lai mat khau moi: ");
            if (!moi.equals(lai)) {
                GiaoDien.in("Hai lan nhap khong giong nhau.");
                continue;
            }
            String loi = dsTK.doiMatKhau(tenDangNhap, matKhauHienTai, moi);
            if (loi != null) {
                GiaoDien.in(loi);
                continue;
            }
            dsTK.ghiFile();
            dsNK.ghi(tenDangNhap, "Doi mat khau (bat buoc lan dau)", tenDangNhap, NhatKyHeThong.THANH_CONG);
            GiaoDien.in("Doi mat khau thanh cong.");
            return true;
        }
        return false;
    }

    //giam doc / truong phong: bao co bao nhieu hop dong lao dong sap het han trong pham vi quan ly
    private static void canhBaoHopDongSapHet() {
        if (!rongHon(Quyen.XEM_HOP_DONG)) return;
        int dem = 0;
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_HOP_DONG)) {
            long ngayCon = soNgayConHopDong(nv);
            if (ngayCon != Long.MIN_VALUE && ngayCon >= 0 && ngayCon <= SO_NGAY_CANH_BAO_HOP_DONG) dem++;
        }
        if (dem > 0) {
            GiaoDien.in("CANH BAO: co " + dem + " hop dong lao dong sap het han trong " + SO_NGAY_CANH_BAO_HOP_DONG
                    + " ngay (xem: Quan ly nhan vien > Xem hop dong lao dong).");
        }
    }

    //so ngay con lai cua hop dong (am neu da het han); Long.MIN_VALUE neu nhan vien khong co hop dong
    private static long soNgayConHopDong(NhanVien nv) {
        if (!(nv instanceof NhanVienHopDong)) return Long.MIN_VALUE;
        HopDongLaoDong hd = ((NhanVienHopDong) nv).gethopDong();
        if (hd == null) return Long.MIN_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), hd.getngayHetHan());
    }

    //nhan vien moi voi cac gia tri mac dinh (luong, hop dong...) - giam doc dieu chinh sau
    private static NhanVien taoNhanVienMacDinh(int loai, String ma, String ten, LocalDate ns, String gt,
                                               String sdt, String email) {
        LocalDate homNay = LocalDate.now();
        if (loai == 1) {
            return new NhanVienToanThoiGian(ma, ten, ns, gt, sdt, email, 10000000, homNay, "Dang lam", 0, 0);
        } else if (loai == 2) {
            return new NhanVienBanThoiGian(ma, ten, ns, gt, sdt, email, 6000000, homNay, "Dang lam", 0, 30000);
        }
        return new NhanVienHopDong(ma, ten, ns, gt, sdt, email, 8000000, homNay, "Dang lam",
                180, 12000000, homNay, homNay.plusDays(180), 180);
    }

    //nguoi moi tu dang ky: luon la NHAN VIEN thuong, ma duoc cap tu dong, CHUA co phong ban.
    //Tai khoan o trang thai CHO_DUYET: giam doc duyet (va xep phong ban) thi moi dang nhap duoc
    private static void dangKy() {
        GiaoDien.in("Tai khoan moi co chuc vu NHAN VIEN. Ma nhan vien duoc cap tu dong.");
        GiaoDien.in("Sau khi dang ky, tai khoan phai duoc GIAM DOC duyet (va xep phong ban) moi dang nhap duoc.");
        int loai = docInt("Loai NV (1: Toan thoi gian, 2: Ban thoi gian, 3: Hop dong): ", 1, 3);
        String ten = docChuoiKhongRong("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();

        ChucVu chucVu = ChucVu.macDinh(4);
        String ma = MaNhanVien.taoMaMoi(MaNhanVien.tienTo(loai, chucVu), ql.getDanhSachNV());
        NhanVien nv = taoNhanVienMacDinh(loai, ma, ten, ns, gt, sdt, email);
        nv.setChucVu(chucVu);

        if (!ql.themNhanVien(nv)) {
            GiaoDien.in("Dang ky that bai (danh sach nhan vien da day).");
            return;
        }
        String matKhau = MaNhanVien.matKhauMacDinh(ma);
        TaiKhoan tk = new TaiKhoan(ma, matKhau);
        tk.setTrangThai(TaiKhoan.CHO_DUYET);
        dsTK.them(tk);
        ql.ghiFile();
        dsTK.ghiFile();
        dsNK.ghi(ma, "Dang ky tai khoan moi", ma, NhatKyHeThong.THANH_CONG);
        GiaoDien.in("");
        GiaoDien.in("Dang ky thanh cong! Ma nhan vien: " + ma);
        GiaoDien.in("Tai khoan: " + ma + "  |  Mat khau tam: " + matKhau + "  (se phai doi o lan dang nhap dau)");
        GiaoDien.in("Tai khoan dang CHO GIAM DOC DUYET.");
    }

    //doi mat khau tu nguoi dang dang nhap
    private static void doiMatKhau() {
        String ten = phien.getMaNV();
        String cu = docChuoi("Mat khau hien tai: ");
        String moi = docChuoi("Mat khau moi (>= " + DanhSachTaiKhoan.DO_DAI_MAT_KHAU_TOI_THIEU + " ky tu): ");
        String lai = docChuoi("Nhap lai mat khau moi: ");
        if (!moi.equals(lai)) {
            GiaoDien.in("Hai lan nhap mat khau moi khong giong nhau.");
            return;
        }
        String loi = dsTK.doiMatKhau(ten, cu, moi);
        if (loi != null) {
            ghiNhatKy("Doi mat khau", ten, NhatKyHeThong.THAT_BAI);
            GiaoDien.in(loi);
            return;
        }
        dsTK.ghiFile();
        ghiNhatKy("Doi mat khau", ten);
        GiaoDien.in("Doi mat khau thanh cong.");
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
        dsNK.docFile();
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
        } catch (KhongCoQuyenException e) {
            GiaoDien.in("KHONG DU QUYEN: " + e.getMessage());
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
        List<Muc> m = new ArrayList<>();
        m.add(muc("Xem danh sach phong ban", "DANH SACH PHONG BAN", Main::pbXemDanhSach));
        m.add(muc("Tim phong ban theo ma", "TIM PHONG BAN", Main::pbTim));
        if (co(Quyen.QUAN_LY_PHONG_BAN)) {
            m.add(muc("Them phong ban", "THEM PHONG BAN", Main::pbThem));
            m.add(muc("Xoa phong ban", "XOA PHONG BAN", Main::pbXoa));
        }
        chayMenu("PHONG BAN", m, "Quay lai", null);
    }

    //giam doc xem tat ca phong ban; nguoi khac chi xem phong cua minh
    private static void pbXemDanhSach() {
        if (phamVi(Quyen.XEM_PHONG_BAN) == PhamVi.TAT_CA) {
            dsPB.xuatDanhSach();
            return;
        }
        PhongBan pb = nguoi().getPhongBan();
        if (pb == null) GiaoDien.in("Ban chua duoc xep vao phong ban nao.");
        else DanhSachPhongBan.inBang(new PhongBan[]{pb});
    }

    private static void pbTim() {
        PhongBan pb = dsPB.timTheoMa(docChuoi("Ma phong ban: "));
        if (pb == null) {
            GiaoDien.in("Khong tim thay.");
            return;
        }
        if (!PhanQuyen.choPhepPhongBan(nguoi(), Quyen.XEM_PHONG_BAN, pb)) {
            ghiNhatKy("Xem phong ban", pb.getMaPB(), NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Ban chi duoc xem phong ban cua minh.");
        }
        pb.hienThiThongTin();
    }

    private static void pbThem() {
        yeuCau(Quyen.QUAN_LY_PHONG_BAN);
        String ma = docChuoi("Ma phong ban: ");
        String ten = docChuoi("Ten phong ban: ");
        String mota = docChuoi("Mo ta: ");
        boolean ok = dsPB.themPhong(new PhongBan(ma, ten, mota));
        if (ok) ghiNhatKy("Them phong ban", ma.toUpperCase());
        GiaoDien.in(ok ? "Them thanh cong." : "Khong the them (trung ma/loi du lieu).");
    }

    private static void pbXoa() {
        yeuCau(Quyen.QUAN_LY_PHONG_BAN);
        String ma = docChuoi("Ma can xoa: ");
        boolean ok = dsPB.xoaTheoMa(ma);
        if (ok) ghiNhatKy("Xoa phong ban", ma.toUpperCase());
        GiaoDien.in(ok ? "Da xoa." : "Khong xoa duoc phong ban.");
    }

//----------------- NHAN VIEN -----------------
    private static void menuNhanVien() {
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.THEM_NHAN_VIEN)) {
            m.add(muc("Them 1 nhan vien", "THEM NHAN VIEN", Main::themNhanVien));
            m.add(muc("Nhap nhieu nhan vien", "NHAP NHIEU NHAN VIEN", Main::nvNhapNhieu));
        }
        m.add(muc("Xem danh sach nhan vien", "DANH SACH NHAN VIEN", Main::nvDanhSach));
        m.add(muc("Tim kiem (ma, ten, SDT, email)", "TIM KIEM NHAN VIEN", Main::nvTimKiem));
        m.add(muc("Xem chi tiet 1 nhan vien", "CHI TIET NHAN VIEN", Main::nvChiTiet));
        m.add(muc("Tim theo phong ban", "TIM THEO PHONG BAN", Main::nvTheoPhongBan));
        if (rongHon(Quyen.XEM_LUONG)) m.add(muc("Tim theo khoang luong", "TIM THEO KHOANG LUONG", Main::nvTheoLuong));
        if (co(Quyen.SUA_NHAN_VIEN) || co(Quyen.SUA_LIEN_HE)) {
            m.add(muc("Sua thong tin nhan vien", "SUA NHAN VIEN", Main::suaNhanVienTuMenu));
        }
        if (co(Quyen.CHO_NGHI_VIEC)) m.add(muc("Cho nhan vien nghi viec", "CHO NGHI VIEC", Main::nvChoNghiViec));
        if (co(Quyen.XOA_NHAN_VIEN)) m.add(muc("Xoa nhan vien khoi he thong", "XOA NHAN VIEN", Main::nvXoa));
        if (rongHon(Quyen.XEM_HOP_DONG)) m.add(muc("Xem hop dong lao dong", "HOP DONG LAO DONG", Main::nvHopDong));
        if (co(Quyen.XEM_BAO_CAO_CONG_TY)) {
            m.add(muc("Thong ke nhan vien theo loai", "THONG KE THEO LOAI", ql::thongKeTheoLoaiNv));
            m.add(muc("Thong ke luong nhan vien", "THONG KE LUONG", ql::thongKeLuong));
        }
        chayMenu("QUAN LY NHAN VIEN", m, "Quay lai", null);
    }

    //bang nhan vien: cot LUONG chi hien voi nguoi ma nguoi dang nhap duoc xem luong
    private static void inBangNhanVien(NhanVien[] ds) {
        List<String[]> dong = new ArrayList<>();
        for (NhanVien nv : ds) {
            String phong = nv.getPhongBan() == null ? "-" : nv.getPhongBan().getTenPB();
            String chucVu = nv.getChucVu() == null ? "-" : nv.getChucVu().getTenCV();
            String luong = choPhep(Quyen.XEM_LUONG, nv) ? GiaoDien.tien(nv.tinhLuong()) : "***";
            dong.add(new String[]{nv.getMaNV(), nv.getHoTen(), phong, chucVu,
                    PhanQuyen.xacDinhVaiTro(nv).ten(), luong, nv.getTrangThai()});
        }
        GiaoDien.inBang(
                new String[]{"MA NV", "HO TEN", "PHONG BAN", "CHUC VU", "LOAI/VAI TRO", "LUONG", "TRANG THAI"},
                new int[]{8, 22, 14, 14, 22, 12, 10},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI},
                dong);
    }

    //ho so 1 nhan vien: day du (co luong, hop dong) neu duoc xem luong cua nguoi do, nguoc lai chi thong tin lien he/to chuc
    private static void inHoSo(NhanVien nv) {
        if (choPhep(Quyen.XEM_LUONG, nv)) {
            nv.hienThiThongTin();
            if (!PhanQuyen.cungMa(nguoi(), nv)) ghiNhatKy("Xem ho so day du (co luong)", nv.getMaNV());
            return;
        }
        List<String[]> cap = new ArrayList<>();
        cap.add(new String[]{"Ma nhan vien", nv.getMaNV()});
        cap.add(new String[]{"Ho ten", nv.getHoTen()});
        cap.add(new String[]{"Gioi tinh", nv.getGioiTinh()});
        cap.add(new String[]{"So dien thoai", nv.getSdt()});
        cap.add(new String[]{"Email", nv.getEmail()});
        cap.add(new String[]{"Phong ban", nv.getPhongBan() == null ? "-" : nv.getPhongBan().getTenPB()});
        cap.add(new String[]{"Chuc vu", nv.getChucVu() == null ? "-" : nv.getChucVu().getTenCV()});
        cap.add(new String[]{"Loai nhan vien", PhanQuyen.xacDinhVaiTro(nv).ten()});
        cap.add(new String[]{"Trang thai", nv.getTrangThai()});
        GiaoDien.inThongTin(cap);
        GiaoDien.in("(Thong tin luong, hop dong cua nguoi nay khong thuoc quyen cua ban.)");
    }

    private static void nvDanhSach() {
        NhanVien[] ds = nvTrongQuyen(Quyen.XEM_NHAN_VIEN);
        if (ds.length == 0) {
            GiaoDien.in("Khong co nhan vien nao trong pham vi cua ban.");
            return;
        }
        inBangNhanVien(ds);
        GiaoDien.in("Tong so nhan vien: " + ds.length);
    }

    private static void inKetQuaTrongQuyen(NhanVien[] ketQua, Quyen q) {
        NhanVien[] hienThi = PhanQuyen.loc(nguoi(), q, ketQua);
        if (hienThi.length == 0) {
            GiaoDien.in("Khong tim thay nhan vien phu hop (trong pham vi cua ban).");
            return;
        }
        inBangNhanVien(hienThi);
        GiaoDien.in("Tim thay " + hienThi.length + " nhan vien.");
    }

    private static void nvNhapNhieu() {
        yeuCau(Quyen.THEM_NHAN_VIEN);
        int so = docInt("So nhan vien muon nhap: ", 1, 100);
        for (int i = 0; i < so; i++) {
            GiaoDien.in("");
            GiaoDien.in("[Nhap nhan vien " + (i + 1) + "/" + so + "]");
            themNhanVien();
        }
    }

    private static void nvTimKiem() {
        String tuKhoa = docChuoi("Nhap ma, ten, SDT hoac email (co the nhap mot phan): ");
        inKetQuaTrongQuyen(ql.timKiemUuTien(tuKhoa), Quyen.XEM_NHAN_VIEN);
    }

    private static void nvTheoPhongBan() {
        pbXemDanhSach();
        inKetQuaTrongQuyen(ql.timKiemTheoPhongBan(docChuoi("Ma phong ban: ")), Quyen.XEM_NHAN_VIEN);
    }

    private static void nvTheoLuong() {
        double tuLuong = docDouble("Luong tu: ", 0);
        double denLuong = docDouble("Luong den: ", 0);
        inKetQuaTrongQuyen(ql.timKiemTheoLuong(tuLuong, denLuong), Quyen.XEM_LUONG);
    }

    private static void nvChiTiet() {
        NhanVien nv = ql.timTheoMa(docChuoi("Ma nhan vien: "));
        if (nv == null) {
            GiaoDien.in("Khong tim thay nhan vien.");
            return;
        }
        yeuCau(Quyen.XEM_NHAN_VIEN, nv);
        inHoSo(nv);
    }

    //hop dong lao dong cua cac nhan vien hop dong trong pham vi duoc xem, co danh dau hop dong sap het han
    private static void nvHopDong() {
        List<String[]> dong = new ArrayList<>();
        int sapHet = 0;
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_HOP_DONG)) {
            long ngayCon = soNgayConHopDong(nv);
            if (ngayCon == Long.MIN_VALUE) continue;
            HopDongLaoDong hd = ((NhanVienHopDong) nv).gethopDong();
            String tinhTrang;
            if (ngayCon < 0) tinhTrang = "DA HET HAN";
            else if (ngayCon <= SO_NGAY_CANH_BAO_HOP_DONG) { tinhTrang = "SAP HET HAN (con " + ngayCon + " ngay)"; sapHet++; }
            else tinhTrang = "Con " + ngayCon + " ngay";
            dong.add(new String[]{nv.getMaNV(), nv.getHoTen(), hd.getmaHD(), String.valueOf(hd.getngayKy()),
                    String.valueOf(hd.getngayHetHan()), tinhTrang});
        }
        if (dong.isEmpty()) {
            GiaoDien.in("Khong co hop dong lao dong nao trong pham vi cua ban.");
            return;
        }
        GiaoDien.inBang(
                new String[]{"MA NV", "HO TEN", "MA HD", "NGAY KY", "HET HAN", "TINH TRANG"},
                new int[]{8, 22, 10, 10, 10, 26},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
        GiaoDien.in("Tong " + dong.size() + " hop dong, trong do " + sapHet + " hop dong sap het han.");
    }

    //them nhan vien: giam doc nhap day du; truong phong chi them nhan vien thuong vao phong cua minh (luong/hop dong de mac dinh)
    private static void themNhanVien() {
        yeuCau(Quyen.THEM_NHAN_VIEN);
        if (phamVi(Quyen.THEM_NHAN_VIEN) != PhamVi.TAT_CA) {
            themNhanVienTrongPhong();
            return;
        }
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
        luuNhanVienMoi(nv);
    }

    //truong phong them nhan vien thuong vao phong cua minh
    private static void themNhanVienTrongPhong() {
        PhongBan pb = nguoi().getPhongBan();
        if (pb == null) throw new KhongCoQuyenException("Ban chua thuoc phong ban nao nen khong the them nhan vien.");
        GiaoDien.in("Nhan vien moi se co chuc vu NHAN VIEN, thuoc phong " + pb.getTenPB() + ".");
        GiaoDien.in("Luong va hop dong de mac dinh, giam doc se dieu chinh sau.");
        int loai = docInt("Loai NV (1: Toan thoi gian, 2: Ban thoi gian, 3: Hop dong): ", 1, 3);
        ChucVu chucVu = ChucVu.macDinh(4);
        String ma = MaNhanVien.taoMaMoi(MaNhanVien.tienTo(loai, chucVu), ql.getDanhSachNV());
        GiaoDien.in("Ma NV duoc cap tu dong: " + ma);
        String ten = docChuoiKhongRong("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();
        NhanVien nv = taoNhanVienMacDinh(loai, ma, ten, ns, gt, sdt, email);
        nv.setChucVu(chucVu);
        nv.setPhongBan(pb);
        luuNhanVienMoi(nv);
    }

    private static void luuNhanVienMoi(NhanVien nv) {
        yeuCau(Quyen.THEM_NHAN_VIEN, nv);      //kiem tra lai tren nhan vien that su duoc tao (cung phong, cap thap hon)
        if (ql.themNhanVien(nv)) {
            String matKhau = MaNhanVien.matKhauMacDinh(nv.getMaNV());
            dsTK.them(new TaiKhoan(nv.getMaNV(), matKhau));
            ghiNhatKy("Them nhan vien", nv.getMaNV());
            GiaoDien.in("Them nhan vien thanh cong.");
            GiaoDien.in("Tai khoan: " + nv.getMaNV() + "  |  Mat khau tam: " + matKhau + "  (phai doi o lan dang nhap dau)");
        } else {
            GiaoDien.in("Them that bai (ma NV trung hoac danh sach loi).");
        }
    }

    //sua so dien thoai, email cua 1 nhan vien (nhan vien tu sua, pho phong sua cho nhan vien thuong...)
    private static void capNhatLienHe(NhanVien nv) {
        yeuCau(Quyen.SUA_LIEN_HE, nv);
        String sdt = docSoDienThoai();
        String email = docEmail();
        nv.setSdt(sdt);
        nv.setEmail(email);
        ghiNhatKy("Sua lien he (sdt, email)", nv.getMaNV());
        GiaoDien.in("Da cap nhat so dien thoai va email.");
    }

    private static void suaNhanVienTuMenu() {
        String ma = docChuoi("Ma nhan vien can sua: ");
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) {
            GiaoDien.in("Khong tim thay nhan vien.");
            return;
        }
        boolean suaDayDu = choPhep(Quyen.SUA_NHAN_VIEN, nv);
        if (!suaDayDu && !choPhep(Quyen.SUA_LIEN_HE, nv)) yeuCau(Quyen.SUA_NHAN_VIEN, nv);   //nem ngoai le + ghi nhat ky
        GiaoDien.in("Thong tin hien tai:");
        inHoSo(nv);
        GiaoDien.in("");
        if (!suaDayDu) {
            GiaoDien.in("Ban chi duoc sua so dien thoai va email cua nhan vien nay.");
            capNhatLienHe(nv);
            return;
        }
        //luong, ngay vao lam, trang thai, phong ban, chuc vu, trinh do: chi nguoi co quyen DOI_LUONG_CHUC_VU (giam doc, khong phai tren chinh minh)
        boolean doiDuocLuongChucVu = choPhep(Quyen.DOI_LUONG_CHUC_VU, nv);
        GiaoDien.in("Nhap thong tin moi:");
        String ten = docChuoi("Ho ten: ");
        LocalDate ns = docNgaySinh();
        String gt = docGioiTinh();
        String sdt = docSoDienThoai();
        String email = docEmail();
        double luong = nv.getLuongCoBan();
        LocalDate vaoLam = nv.getNgayVaoLam();
        String trangThai = nv.getTrangThai();
        PhongBan pb = nv.getPhongBan();
        if (doiDuocLuongChucVu) {
            luong = docDouble("Luong co ban: ", 0);
            vaoLam = docNgay("Ngay vao lam (yyyy-MM-dd): ");
            trangThai = docChuoi("Trang thai: ");
            if (trangThai.toLowerCase().startsWith("nghi") && laGiamDocCuoi(nv)) {
                GiaoDien.in("Day la giam doc cuoi cung dang hoat dong, khong the cho nghi viec.");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("Danh sach phong ban hien co:");
            dsPB.xuatDanhSach();
            String maPB = docChuoi("Ma phong ban moi (bo trong neu chua phan): ");
            pb = maPB.isBlank() ? null : dsPB.timTheoMa(maPB);
            if (!maPB.isBlank() && pb == null) {
                GiaoDien.in("Ma phong ban khong ton tai; huy cap nhat.");
                return;
            }
        } else {
            GiaoDien.in("(Luong, trang thai, phong ban, chuc vu cua nguoi nay khong thuoc quyen cua ban nen duoc giu nguyen.)");
        }
        try {
            boolean ok = ql.capNhatThongTinCoBan(ma, ten, ns, gt, sdt, email,
                    luong, vaoLam, trangThai, pb);
            if (ok) ghiNhatKy("Sua thong tin nhan vien", nv.getMaNV());
            GiaoDien.in(ok ? "Cap nhat thanh cong." : "Cap nhat that bai.");
        } catch (IllegalArgumentException e) {
            GiaoDien.in("Du lieu khong hop le: " + e.getMessage());
            return;
        }
        if (!doiDuocLuongChucVu) return;
        String doiCV = docChuoi("Co muon doi chuc vu? (y/n): ");
        if (doiCV.equalsIgnoreCase("y")) {
            ChucVu moi = docChucVu();
            boolean haCap = PhanQuyen.xacDinhVaiTro(nv) == VaiTro.GIAM_DOC && !"CV01".equalsIgnoreCase(moi.getMaCV());
            if (haCap && laGiamDocCuoi(nv)) {
                GiaoDien.in("Day la giam doc cuoi cung dang hoat dong, khong the ha chuc vu.");
            } else {
                nv.setChucVu(moi);
                ghiNhatKy("Doi chuc vu thanh " + moi.getTenCV(), nv.getMaNV());
                GiaoDien.in("Da doi chuc vu (ma NV van giu nguyen: " + nv.getMaNV() + ").");
            }
        }
        String doiTD = docChuoi("Co muon nhap lai trinh do chuyen mon? (y/n): ");
        if (doiTD.equalsIgnoreCase("y")) {
            nv.setTrinhDo(docDanhSachTrinhDo());
            ghiNhatKy("Cap nhat trinh do chuyen mon", nv.getMaNV());
            GiaoDien.in("Da cap nhat trinh do.");
        }
    }

    //cho nghi viec = doi trang thai "Nghi" (tai khoan bi chan dang nhap), du lieu van con; truong phong chi duoc cho cap duoi trong phong
    private static void nvChoNghiViec() {
        String ma = docChuoi("Ma nhan vien cho nghi viec: ");
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) {
            GiaoDien.in("Khong tim thay nhan vien.");
            return;
        }
        yeuCau(Quyen.CHO_NGHI_VIEC, nv);
        if (nv.getTrangThai().toLowerCase().startsWith("nghi")) {
            GiaoDien.in("Nhan vien nay da nghi viec tu truoc.");
            return;
        }
        if (laGiamDocCuoi(nv)) {
            GiaoDien.in("Day la giam doc cuoi cung dang hoat dong, khong the cho nghi viec.");
            return;
        }
        if (!docChuoi("Xac nhan cho " + nv.getHoTen() + " nghi viec? (y/n): ").equalsIgnoreCase("y")) {
            GiaoDien.in("Da huy.");
            return;
        }
        nv.setTrangThai("Nghi");
        ghiNhatKy("Cho nghi viec", nv.getMaNV());
        GiaoDien.in("Da chuyen trang thai nhan vien sang NGHI (khong the dang nhap nua).");
    }

    //xoa han khoi he thong: chi giam doc, khong tu xoa minh, khong xoa giam doc cuoi cung
    private static void nvXoa() {
        String ma = docChuoi("Ma nhan vien can xoa: ");
        if (phien.getMaNV().equalsIgnoreCase(ma.trim())) {
            ghiNhatKy("Xoa nhan vien", ma.trim().toUpperCase(), NhatKyHeThong.TU_CHOI);
            GiaoDien.in("Khong the xoa chinh tai khoan dang dang nhap.");
            return;
        }
        NhanVien nv = ql.timTheoMa(ma);
        if (nv == null) {
            GiaoDien.in("Khong tim thay.");
            return;
        }
        yeuCau(Quyen.XOA_NHAN_VIEN, nv);
        if (laGiamDocCuoi(nv)) {
            GiaoDien.in("Day la giam doc cuoi cung dang hoat dong, khong the xoa.");
            return;
        }
        if (ql.xoaNhanVien(ma)) {
            dsTK.xoaTheoTen(ma);
            ghiNhatKy("Xoa nhan vien", nv.getMaNV());
            GiaoDien.in("Da xoa nhan vien va tai khoan cua nhan vien.");
        } else {
            GiaoDien.in("Khong tim thay.");
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
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.THEM_CHAM_CONG)) m.add(muc("Them cham cong cho nhan vien", "CHAM CONG - THEM MOI", Main::ccThem));
        m.add(muc("Xem danh sach cham cong", "CHAM CONG - DANH SACH", Main::ccXem));
        if (co(Quyen.XOA_CHAM_CONG)) m.add(muc("Xoa cham cong", "CHAM CONG - XOA", Main::ccXoa));
        m.add(muc("Thong ke gio theo nhan vien/thang", "CHAM CONG - THONG KE THEO THANG", Main::ccThongKe));
        chayMenu("CHAM CONG", m, "Quay lai", null);
    }

    private static void ccThem() {
        yeuCau(Quyen.THEM_CHAM_CONG);
        String maCC = docChuoi("Ma cham cong: ");
        String maNV = docChuoi("Ma nhan vien: ");
        NhanVien nv = ql.timTheoMa(maNV);
        if (nv == null) {
            GiaoDien.in("Nhan vien khong ton tai.");
            return;
        }
        yeuCau(Quyen.THEM_CHAM_CONG, nv);
        LocalDate ngay = docNgay("Ngay (yyyy-MM-dd): ");
        LocalTime vao = docTime("Gio vao (HH:mm): ");
        LocalTime ra = docTime("Gio ra (HH:mm): ");
        if (!ra.isAfter(vao)) {
            GiaoDien.in("Gio ra phai sau gio vao.");
            return;
        }
        boolean ok = dsCC.them(new ChamCong(maCC, nv.getMaNV(), ngay, vao, ra));
        if (ok) ghiNhatKy("Them cham cong " + ngay, nv.getMaNV());
        GiaoDien.in(ok ? "Da them cham cong." : "Them that bai.");
    }

    //cham cong trong pham vi duoc xem: giam doc xem het, truong/pho phong xem phong minh, con lai chi cua minh
    private static void ccXem() {
        if (phamVi(Quyen.XEM_CHAM_CONG) == PhamVi.TAT_CA) {
            dsCC.xuatDanhSach();
            return;
        }
        List<ChamCong> gop = new ArrayList<>();
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_CHAM_CONG)) {
            for (ChamCong cc : dsCC.timTheoMaNV(nv.getMaNV())) gop.add(cc);
        }
        DanhSachChamCong.inBang(gop.toArray(new ChamCong[0]));
    }

    private static void ccXoa() {
        yeuCau(Quyen.XOA_CHAM_CONG);
        String maCC = docChuoi("Ma cham cong: ");
        ChamCong cc = dsCC.timTheoMa(maCC);
        if (cc == null) {
            GiaoDien.in("Khong tim thay.");
            return;
        }
        yeuCauTheoMaNV(Quyen.XOA_CHAM_CONG, cc.getMaNV());
        dsCC.xoaTheoMa(maCC);
        ghiNhatKy("Xoa cham cong " + cc.getMaChamCong() + " (" + cc.getNgay() + ")", cc.getMaNV());
        GiaoDien.in("Da xoa.");
    }

    //nhan vien tu cham cong hom nay cho chinh minh (moi ngay 1 lan)
    private static void ccTuChamCong() {
        yeuCau(Quyen.TU_CHAM_CONG, nguoi());
        LocalDate homNay = LocalDate.now();
        String maNV = nguoi().getMaNV();
        for (ChamCong cc : dsCC.timTheoMaNV(maNV)) {
            if (cc.getNgay().equals(homNay)) {
                GiaoDien.in("Hom nay (" + homNay + ") ban da cham cong roi (ma " + cc.getMaChamCong() + ").");
                return;
            }
        }
        GiaoDien.in("Cham cong cho ngay hom nay: " + homNay);
        LocalTime vao = docTime("Gio vao (HH:mm): ");
        LocalTime ra = docTime("Gio ra (HH:mm): ");
        if (!ra.isAfter(vao)) {
            GiaoDien.in("Gio ra phai sau gio vao.");
            return;
        }
        int so = dsCC.getSoLuong() + 1;
        String maCC = String.format("CC%03d", so);
        while (dsCC.timTheoMa(maCC) != null) maCC = String.format("CC%03d", ++so);
        boolean ok = dsCC.them(new ChamCong(maCC, maNV, homNay, vao, ra));
        if (ok) ghiNhatKy("Tu cham cong " + homNay, maNV);
        GiaoDien.in(ok ? "Da cham cong (ma " + maCC + ")." : "Cham cong that bai.");
    }

    private static void ccThongKe() {
        ccThongKeCho(docNhanVienDich(Quyen.XEM_CHAM_CONG, "Ma NV: "));
    }

    private static void ccThongKeCho(NhanVien nv) {
        yeuCau(Quyen.XEM_CHAM_CONG, nv);
        String id = nv.getMaNV();
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
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.QUAN_LY_BAO_HIEM)) {
            m.add(muc("Them BHXH", "BAO HIEM - THEM BHXH", Main::bhThemXH));
            m.add(muc("Them BHYT", "BAO HIEM - THEM BHYT", Main::bhThemYT));
        }
        m.add(muc("Xem danh sach", "BAO HIEM - DANH SACH", Main::bhXem));
        m.add(muc("Tim theo ma NV", "BAO HIEM - TIM THEO MA NV", Main::bhTim));
        if (co(Quyen.QUAN_LY_BAO_HIEM)) m.add(muc("Xoa theo ma BH", "BAO HIEM - XOA", Main::bhXoa));
        m.add(muc("Tinh tien dong", "BAO HIEM - TINH TIEN DONG", Main::bhTinhTien));
        chayMenu("BAO HIEM", m, "Quay lai", null);
    }

    private static void bhThemXH() {
        yeuCau(Quyen.QUAN_LY_BAO_HIEM);
        String maBH = docChuoi("Ma BH: ");
        String maNV = docChuoi("Ma NV: ");
        String soSo = docChuoi("So so BHXH: ");
        boolean ok = dsBH.them(new BaoHiemXaHoi(maBH, maNV, soSo, LocalDate.now()));
        if (ok) ghiNhatKy("Them BHXH " + maBH.toUpperCase(), maNV.toUpperCase());
        GiaoDien.in(ok ? "Da them." : "Them that bai.");
    }

    private static void bhThemYT() {
        yeuCau(Quyen.QUAN_LY_BAO_HIEM);
        String maBH = docChuoi("Ma BH: ");
        String maNV = docChuoi("Ma NV: ");
        String the = docChuoi("So the BHYT: ");
        String noi = docChuoi("Noi KCB: ");
        LocalDate hanThe = docNgay("Han the (yyyy-MM-dd): ");
        boolean ok = dsBH.them(new BaoHiemYTe(maBH, maNV, the, LocalDate.now(), noi, hanThe));
        if (ok) ghiNhatKy("Them BHYT " + maBH.toUpperCase(), maNV.toUpperCase());
        GiaoDien.in(ok ? "Da them." : "Them that bai.");
    }

    private static void bhXem() {
        if (phamVi(Quyen.XEM_BAO_HIEM) == PhamVi.TAT_CA) {
            dsBH.xuatDanhSach();
            return;
        }
        List<BaoHiem> gop = new ArrayList<>();
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_BAO_HIEM)) {
            for (BaoHiem bh : dsBH.timTheoMaNV(nv.getMaNV())) gop.add(bh);
        }
        if (gop.isEmpty()) GiaoDien.in("Khong co du lieu.");
        else DanhSachBaoHiem.inBang(gop.toArray(new BaoHiem[0]));
    }

    private static void bhTim() {
        bhTimCho(docNhanVienDich(Quyen.XEM_BAO_HIEM, "Ma NV: "));
    }

    private static void bhTimCho(NhanVien nv) {
        yeuCau(Quyen.XEM_BAO_HIEM, nv);
        BaoHiem[] kq = dsBH.timTheoMaNV(nv.getMaNV());
        if (kq.length == 0) GiaoDien.in("Khong co du lieu bao hiem cua nhan vien " + nv.getMaNV() + ".");
        else DanhSachBaoHiem.inBang(kq);
    }

    private static void bhXoa() {
        yeuCau(Quyen.QUAN_LY_BAO_HIEM);
        String ma = docChuoi("Ma BH: ");
        boolean ok = dsBH.xoaTheoMa(ma);
        if (ok) ghiNhatKy("Xoa bao hiem", ma.toUpperCase());
        GiaoDien.in(ok ? "Da xoa." : "Khong tim thay.");
    }

    private static void bhTinhTien() {
        NhanVien nv = docNhanVienDich(Quyen.XEM_BAO_HIEM, "Ma NV: ");
        yeuCau(Quyen.XEM_BAO_HIEM, nv);
        String maNV = nv.getMaNV();
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
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.LAP_BANG_LUONG)) m.add(muc("Lap bang luong cho tat ca nhan vien", "BANG LUONG - LAP CHO TAT CA", Main::blLap));
        m.add(muc("Xem bang luong theo ma NV", "BANG LUONG - THEO MA NHAN VIEN", Main::blTheoNV));
        m.add(muc("Xem bang luong theo thang", "BANG LUONG - THEO THANG", Main::blTheoThang));
        if (co(Quyen.LAP_BANG_LUONG)) m.add(muc("Thong ke quy luong", "BANG LUONG - THONG KE QUY LUONG", Main::blThongKe));
        chayMenu("BANG LUONG", m, "Quay lai", null);
    }

    private static void blLap() {
        yeuCau(Quyen.LAP_BANG_LUONG);
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        dsBL.lapBangLuongChoTatCa(ql.getDanhSachNV(), thang, nam, dsTP, dsBH);
        dsBL.ghiFile();
        ghiNhatKy("Lap bang luong thang " + thang + "/" + nam, "TAT CA");
        GiaoDien.in("Da lap bang luong.");
    }

    private static void blTheoNV() {
        blTheoNVCho(docNhanVienDich(Quyen.XEM_LUONG, "Ma NV: "));
    }

    private static void blTheoNVCho(NhanVien nv) {
        yeuCau(Quyen.XEM_LUONG, nv);
        if (!PhanQuyen.cungMa(nguoi(), nv)) ghiNhatKy("Xem bang luong", nv.getMaNV());
        dsBL.xuatBangLuongNhanVien(nv.getMaNV());
    }

    //bang luong 1 thang: giam doc xem ca cong ty, truong phong chi xem cap duoi + ban than trong phong
    private static void blTheoThang() {
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        if (phamVi(Quyen.XEM_LUONG) == PhamVi.TAT_CA) {
            ghiNhatKy("Xem bang luong cong ty thang " + thang + "/" + nam, "TAT CA");
            dsBL.xuatBangLuongCongTy(thang, nam);
            return;
        }
        List<BangLuong> gop = new ArrayList<>();
        double tong = 0;
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_LUONG)) {
            BangLuong bl = dsBL.timTheoMaNV(nv.getMaNV(), thang, nam);
            if (bl != null) {
                gop.add(bl);
                tong += bl.getLuongThucNhan();
            }
        }
        if (gop.isEmpty()) {
            GiaoDien.in("Chua co bang luong thang " + thang + "/" + nam + " trong pham vi cua ban.");
            return;
        }
        ghiNhatKy("Xem bang luong thang " + thang + "/" + nam + " (pham vi phong)", "PHONG");
        DanhSachBangLuong.inBang(gop.toArray(new BangLuong[0]));
        GiaoDien.in("Tong luong thuc nhan (" + gop.size() + " nhan vien): " + GiaoDien.tien(tong) + " VND");
    }

    private static void blThongKe() {
        yeuCau(Quyen.LAP_BANG_LUONG);
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
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.XEM_BAO_CAO_CONG_TY)) {
            m.add(muc("Bao cao toan cong ty", "BAO CAO - TOAN CONG TY", Main::bcCongTy));
            m.add(muc("Bao cao theo phong ban", "BAO CAO - THEO PHONG BAN", Main::bcPhongBan));
            m.add(muc("Thong ke nhan su", "BAO CAO - THONG KE NHAN SU", () -> baoCao.thongKeNhanSu(ql, dsBL)));
            m.add(muc("Thong ke bao hiem", "BAO CAO - THONG KE BAO HIEM", baoCao::thongKeBaoHiem));
        } else {
            m.add(muc("Bao cao phong ban cua toi", "BAO CAO - PHONG BAN CUA TOI", Main::bcPhongToi));
        }
        chayMenu("BAO CAO", m, "Quay lai", null);
    }

    private static void bcCongTy() {
        yeuCau(Quyen.XEM_BAO_CAO_CONG_TY);
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        GiaoDien.in("");
        baoCao.inBaoCaoToanCongTy(ql, dsBL, thang, nam);
    }

    private static void bcPhongBan() {
        yeuCau(Quyen.XEM_BAO_CAO_CONG_TY);
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban: ");
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        GiaoDien.in("");
        baoCao.inBaoCaoTheoPhong(maPB, ql, dsBL, thang, nam);
    }

    //bao cao phong cua truong phong / pho phong: nhan su + cham cong thang; chi them tong luong neu duoc xem luong
    private static void bcPhongToi() {
        yeuCau(Quyen.XEM_BAO_CAO_PHONG);
        PhongBan pb = nguoi().getPhongBan();
        if (pb == null) {
            GiaoDien.in("Ban chua thuoc phong ban nao.");
            return;
        }
        int thang = docInt("Thang: ", 1, 12);
        int nam = docInt("Nam: ", 2000, 3000);
        NhanVien[] nhanSu = nvTrongQuyen(Quyen.XEM_NHAN_VIEN);
        List<String[]> dong = new ArrayList<>();
        double tongLuong = 0;
        int coBangLuong = 0;
        boolean xemLuong = rongHon(Quyen.XEM_LUONG);
        for (NhanVien nv : nhanSu) {
            String ma = nv.getMaNV();
            int donCho = 0;
            for (NghiPhep np : dsNP.timTheoMaNV(ma)) if ("Cho duyet".equals(np.getTrangThai())) donCho++;
            dong.add(new String[]{ma, nv.getHoTen(), PhanQuyen.xacDinhVaiTro(nv).ten(),
                    String.valueOf(dsCC.thongKeSoNgayDiLam(ma, thang, nam)),
                    String.format("%.1f", dsCC.thongKeGioLam(ma, thang, nam)),
                    String.format("%.1f", dsCC.thongKeGioTangCa(ma, thang, nam)),
                    String.valueOf(donCho)});
            if (xemLuong && choPhep(Quyen.XEM_LUONG, nv)) {
                BangLuong bl = dsBL.timTheoMaNV(ma, thang, nam);
                if (bl != null) {
                    tongLuong += bl.getLuongThucNhan();
                    coBangLuong++;
                }
            }
        }
        GiaoDien.in("BAO CAO PHONG " + pb.getTenPB() + " - THANG " + thang + "/" + nam);
        GiaoDien.inBang(
                new String[]{"MA NV", "HO TEN", "LOAI/VAI TRO", "NGAY LAM", "GIO LAM", "TANG CA", "DON CHO DUYET"},
                new int[]{8, 22, 22, 8, 8, 8, 8},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI},
                dong);
        GiaoDien.in("So nhan su trong bao cao: " + nhanSu.length);
        if (xemLuong) {
            GiaoDien.in("Tong luong thuc nhan (" + coBangLuong + " bang luong thuoc quyen xem): " + GiaoDien.tien(tongLuong) + " VND");
        }
        ghiNhatKy("Xem bao cao phong " + pb.getMaPB() + " thang " + thang + "/" + nam, "PHONG");
    }

//----------------- THUONG PHAT -----------------
    private static void menuThuongPhat() {
        List<Muc> m = new ArrayList<>();
        if (co(Quyen.THEM_THUONG_PHAT)) m.add(muc("Them thuong/phat thu cong", "THUONG PHAT - THEM THU CONG", Main::tpThem));
        m.add(muc("Xem danh sach", "THUONG PHAT - DANH SACH", Main::tpXem));
        m.add(muc("Xem theo ma nhan vien", "THUONG PHAT - THEO MA NHAN VIEN", Main::tpTheoNV));
        if (co(Quyen.XOA_THUONG_PHAT)) m.add(muc("Xoa theo ma thuong/phat", "THUONG PHAT - XOA", Main::tpXoa));
        m.add(muc("Thong ke thuong/phat theo thang", "THUONG PHAT - THONG KE THEO THANG", Main::tpThongKe));
        if (co(Quyen.TU_DONG_XET_THUONG_PHAT)) {
            m.add(muc("Tu dong xet thuong/phat theo quy dinh", "THUONG PHAT - TU DONG XET", Main::tpTuDongXet));
        }
        chayMenu("QUAN LY THUONG PHAT", m, "Quay lai", null);
    }

    private static void tpThem() {
        yeuCau(Quyen.THEM_THUONG_PHAT);
        String maTP = docChuoi("Ma thuong/phat: ");
        String maNV = docChuoi("Ma nhan vien: ");
        NhanVien nv = ql.timTheoMa(maNV);
        if (nv == null) {
            GiaoDien.in("Nhan vien khong ton tai.");
            return;
        }
        yeuCau(Quyen.THEM_THUONG_PHAT, nv);     //khong tu thuong/phat cho minh, truong phong chi cho cap duoi trong phong
        String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
        if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
            GiaoDien.in("Loai chi nhan THUONG hoac PHAT.");
            return;
        }
        String lyDo = docChuoi("Ly do: ");
        double tien = docDouble("So tien: ", 0);
        ThuongPhat tp = new ThuongPhat(maTP, nv.getMaNV(), "", loai, lyDo, 1, tien, LocalDate.now());
        boolean ok = dsTP.them(tp);
        if (ok) ghiNhatKy("Them " + loai + " " + GiaoDien.tien(tien) + " VND", nv.getMaNV());
        GiaoDien.in(ok ? "Da them thuong/phat." : "Them that bai (ma trung).");
    }

    private static void tpXem() {
        if (phamVi(Quyen.XEM_THUONG_PHAT) == PhamVi.TAT_CA) {
            dsTP.xuatDanhSach();
            return;
        }
        List<ThuongPhat> gop = new ArrayList<>();
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_THUONG_PHAT)) {
            for (ThuongPhat tp : dsTP.timTheoMaNV(nv.getMaNV())) gop.add(tp);
        }
        if (gop.isEmpty()) GiaoDien.in("Khong co du lieu.");
        else DanhSachThuongPhat.inBang(gop.toArray(new ThuongPhat[0]));
    }

    private static void tpTheoNV() {
        tpTheoNVCho(docNhanVienDich(Quyen.XEM_THUONG_PHAT, "Ma nhan vien: "));
    }

    private static void tpTheoNVCho(NhanVien nv) {
        yeuCau(Quyen.XEM_THUONG_PHAT, nv);
        ThuongPhat[] ds = dsTP.timTheoMaNV(nv.getMaNV());
        if (ds.length == 0) GiaoDien.in("Khong co du lieu.");
        else DanhSachThuongPhat.inBang(ds);
    }

    private static void tpXoa() {
        yeuCau(Quyen.XOA_THUONG_PHAT);
        String ma = docChuoi("Ma thuong/phat: ");
        boolean ok = dsTP.xoaTheoMa(ma);
        if (ok) ghiNhatKy("Xoa thuong/phat", ma.toUpperCase());
        GiaoDien.in(ok ? "Da xoa." : "Khong tim thay.");
    }

    private static void tpThongKe() {
        NhanVien nv = docNhanVienDich(Quyen.XEM_THUONG_PHAT, "Ma nhan vien: ");
        yeuCau(Quyen.XEM_THUONG_PHAT, nv);
        String maNV = nv.getMaNV();
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
        yeuCau(Quyen.TU_DONG_XET_THUONG_PHAT);
        int thang = docInt("Thang can xet: ", 1, 12);
        int nam = docInt("Nam can xet: ", 2000, 3000);
        dsTP.tuDongXetThuongPhat(thang, nam, ql.getDanhSachNV(),
                dsQD.getDanhSach(), dsCC, dsNP.getDanhSach());
        dsTP.ghiFile();
        ghiNhatKy("Tu dong xet thuong/phat thang " + thang + "/" + nam, "TAT CA");
        GiaoDien.in("Da tu dong xet thuong/phat theo cac quy dinh hien co.");
    }

//----------------- NGHI PHEP -----------------
    private static void menuNghiPhep() {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Tao don nghi phep", "NGHI PHEP - TAO DON", Main::npTao));
        m.add(muc("Xem don nghi phep", "NGHI PHEP - DANH SACH", Main::npXem));
        m.add(muc("Xem don theo nhan vien", "NGHI PHEP - THEO NHAN VIEN", Main::npTheoNV));
        if (co(Quyen.DUYET_NGHI_PHEP)) m.add(muc("Duyet don", "NGHI PHEP - DUYET DON", Main::npDuyet));
        m.add(muc("Xoa don", "NGHI PHEP - XOA DON", Main::npXoa));
        chayMenu("QUAN LY NGHI PHEP", m, "Quay lai", null);
    }

    //menu nghi phep trong cong thong tin ca nhan: chi don cua chinh minh
    private static void menuNghiPhepCuaToi() {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Tao don nghi phep cua toi", "NGHI PHEP - TAO DON CUA TOI", () -> npTaoCho(nguoi())));
        m.add(muc("Xem don nghi phep cua toi", "NGHI PHEP - DON CUA TOI", () -> npTheoNVCho(nguoi())));
        m.add(muc("Xoa don dang cho duyet cua toi", "NGHI PHEP - XOA DON CUA TOI", Main::npXoa));
        chayMenu("NGHI PHEP CUA TOI", m, "Quay lai", null);
    }

    private static String taoMaNghiPhep() {
        int so = dsNP.getSoLuong() + 1;
        String ma = String.format("NP%03d", so);
        while (dsNP.timTheoMa(ma) != null) ma = String.format("NP%03d", ++so);
        return ma;
    }

    private static void npTao() {
        npTaoCho(docNhanVienDich(Quyen.TAO_DON_NGHI_PHEP, "Ma nhan vien: "));
    }

    private static void npTaoCho(NhanVien nv) {
        yeuCau(Quyen.TAO_DON_NGHI_PHEP, nv);
        String maNP = taoMaNghiPhep();
        GiaoDien.in("Ma don duoc cap tu dong: " + maNP + "  |  Nhan vien: " + nv.getMaNV());
        LocalDate tuNgay = docNgay("Tu ngay (yyyy-MM-dd): ");
        LocalDate denNgay = docNgay("Den ngay (yyyy-MM-dd): ");
        String lyDo = docChuoi("Ly do: ");
        NghiPhep np = new NghiPhep(maNP, nv.getMaNV(), tuNgay, denNgay, lyDo);
        String loai = docChuoi("Loai nghi (CO_PHEP/KHONG_PHEP): ");
        np.setLoaiNghi(loai.equalsIgnoreCase("KHONG_PHEP") ? "Khong phep" : "Co phep");
        boolean ok = dsNP.them(np);
        if (ok) ghiNhatKy("Tao don nghi phep " + maNP, nv.getMaNV());
        GiaoDien.in(ok ? "Da tao don nghi phep (cho duyet)." : "Tao don that bai (kiem tra ngay).");
    }

    private static void npXem() {
        if (phamVi(Quyen.XEM_NGHI_PHEP) == PhamVi.TAT_CA) {
            dsNP.xuatDanhSach();
            return;
        }
        List<NghiPhep> gop = new ArrayList<>();
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_NGHI_PHEP)) {
            for (NghiPhep np : dsNP.timTheoMaNV(nv.getMaNV())) gop.add(np);
        }
        if (gop.isEmpty()) GiaoDien.in("Khong co don.");
        else DanhSachNghiPhep.inBang(gop.toArray(new NghiPhep[0]));
    }

    private static void npTheoNV() {
        npTheoNVCho(docNhanVienDich(Quyen.XEM_NGHI_PHEP, "Ma nhan vien: "));
    }

    private static void npTheoNVCho(NhanVien nv) {
        yeuCau(Quyen.XEM_NGHI_PHEP, nv);
        NghiPhep[] ds = dsNP.timTheoMaNV(nv.getMaNV());
        if (ds.length == 0) GiaoDien.in("Khong co don.");
        else DanhSachNghiPhep.inBang(ds);
    }

    //duyet don: cap tren truc tiep duyet (pho phong duyet nhan vien, truong phong duyet pho phong + nhan vien, giam doc duyet tat ca), khong tu duyet don cua minh
    private static void npDuyet() {
        yeuCau(Quyen.DUYET_NGHI_PHEP);
        String ma = docChuoi("Ma don can duyet: ");
        NghiPhep np = dsNP.timTheoMa(ma);
        if (np == null) {
            GiaoDien.in("Khong tim thay don.");
            return;
        }
        yeuCauTheoMaNV(Quyen.DUYET_NGHI_PHEP, np.getMaNV());
        if ("Da duyet".equals(np.getTrangThai())) {
            GiaoDien.in("Don nay da duoc duyet truoc do.");
            return;
        }
        dsNP.duyetTheoMa(ma);
        ghiNhatKy("Duyet don nghi phep " + np.getMaNP(), np.getMaNV());
        GiaoDien.in("Da duyet.");
    }

    //giam doc xoa duoc moi don; nguoi khac chi xoa don CUA MINH va CHUA duyet
    private static void npXoa() {
        yeuCau(Quyen.XOA_NGHI_PHEP);
        String ma = docChuoi("Ma don can xoa: ");
        NghiPhep np = dsNP.timTheoMa(ma);
        if (np == null) {
            GiaoDien.in("Khong tim thay don.");
            return;
        }
        yeuCauTheoMaNV(Quyen.XOA_NGHI_PHEP, np.getMaNV());
        if (phamVi(Quyen.XOA_NGHI_PHEP) != PhamVi.TAT_CA && "Da duyet".equals(np.getTrangThai())) {
            ghiNhatKy("Xoa don nghi phep da duyet " + np.getMaNP(), np.getMaNV(), NhatKyHeThong.TU_CHOI);
            GiaoDien.in("Don da duoc duyet, ban khong the xoa. Hay lien he cap tren.");
            return;
        }
        dsNP.xoaTheoMa(ma);
        ghiNhatKy("Xoa don nghi phep " + np.getMaNP(), np.getMaNV());
        GiaoDien.in("Da xoa don.");
    }

//----------------- QUY DINH THUONG PHAT -----------------
    private static void menuQuyDinhThuongPhat() {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Xem danh sach quy dinh", "QUY DINH - DANH SACH", dsQD::xuatDanhSach));
        if (co(Quyen.QUAN_LY_QUY_DINH)) {
            m.add(muc("Them quy dinh", "QUY DINH - THEM MOI", Main::qdThem));
            m.add(muc("Xoa quy dinh", "QUY DINH - XOA", Main::qdXoa));
        }
        chayMenu("QUY DINH THUONG PHAT", m, "Quay lai", null);
    }

    private static void qdThem() {
        yeuCau(Quyen.QUAN_LY_QUY_DINH);
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
        boolean ok = dsQD.them(qd);
        if (ok) ghiNhatKy("Them quy dinh thuong phat", ma.toUpperCase());
        GiaoDien.in(ok ? "Da them quy dinh." : "Them that bai (ma trung).");
    }

    private static void qdXoa() {
        yeuCau(Quyen.QUAN_LY_QUY_DINH);
        String ma = docChuoi("Ma quy dinh: ");
        boolean ok = dsQD.xoaTheoMa(ma);
        if (ok) ghiNhatKy("Xoa quy dinh thuong phat", ma.toUpperCase());
        GiaoDien.in(ok ? "Da xoa." : "Khong tim thay.");
    }

//----------------- CONG THONG TIN CA NHAN -----------------
    private static void cnHoSo() {
        NhanVien nv = nguoi();
        inHoSo(nv);
        if (co(Quyen.SUA_LIEN_HE) && docChuoi("Cap nhat so dien thoai, email cua ban? (y/n): ").equalsIgnoreCase("y")) {
            capNhatLienHe(nv);
        }
    }

    private static void cnChamCong() {
        DanhSachChamCong.inBang(dsCC.timTheoMaNV(phien.getMaNV()));
    }

    private static void cnBangLuong() {
        blTheoNVCho(nguoi());
    }

    //hop dong lao dong cua chinh minh (nhan vien toan thoi gian / ban thoi gian khong co hop dong -> menu nay bi an)
    private static void cnHopDong() {
        yeuCau(Quyen.XEM_HOP_DONG, nguoi());
        long ngayCon = soNgayConHopDong(nguoi());
        if (ngayCon == Long.MIN_VALUE) {
            GiaoDien.in("Ban khong co hop dong lao dong.");
            return;
        }
        ((NhanVienHopDong) nguoi()).gethopDong().hienThiThongTin();
        if (ngayCon < 0) GiaoDien.in("Hop dong da het han " + (-ngayCon) + " ngay.");
        else GiaoDien.in("Hop dong con hieu luc " + ngayCon + " ngay."
                + (ngayCon <= SO_NGAY_CANH_BAO_HOP_DONG ? " (SAP HET HAN)" : ""));
    }

//----------------- QUAN LY TAI KHOAN (giam doc) -----------------
    private static void menuTaiKhoan() {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Danh sach tai khoan", "TAI KHOAN - DANH SACH", Main::tkDanhSach));
        m.add(muc("Duyet tai khoan dang ky moi (va xep phong ban)", "TAI KHOAN - DUYET", Main::tkDuyet));
        m.add(muc("Dat lai mat khau ve mac dinh", "TAI KHOAN - DAT LAI MAT KHAU", Main::tkDatLai));
        m.add(muc("Khoa tai khoan", "TAI KHOAN - KHOA", Main::tkKhoa));
        m.add(muc("Mo khoa tai khoan", "TAI KHOAN - MO KHOA", Main::tkMoKhoa));
        chayMenu("QUAN LY TAI KHOAN", m, "Quay lai", null);
    }

    private static void tkDanhSach() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        TaiKhoan[] ds = dsTK.getDanhSach();
        List<String[]> dong = new ArrayList<>();
        for (TaiKhoan tk : ds) {
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            dong.add(new String[]{tk.getTenDangNhap(), nv == null ? "(khong con nhan vien)" : nv.getHoTen(),
                    nv == null ? "-" : PhanQuyen.xacDinhVaiTro(nv).ten(), tk.getTrangThai(),
                    tk.isPhaiDoiMatKhau() ? "Co" : "Khong"});
        }
        GiaoDien.inBang(
                new String[]{"TAI KHOAN", "HO TEN", "VAI TRO", "TRANG THAI", "PHAI DOI MK"},
                new int[]{10, 22, 24, 12, 11},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.GIUA},
                dong);
        GiaoDien.in("Tong so tai khoan: " + ds.length);
    }

    private static void tkDuyet() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        List<String[]> dong = new ArrayList<>();
        for (TaiKhoan tk : dsTK.getDanhSach()) {
            if (!tk.choDuyet()) continue;
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            dong.add(new String[]{tk.getTenDangNhap(), nv == null ? "-" : nv.getHoTen(),
                    nv == null ? "-" : PhanQuyen.xacDinhVaiTro(nv).ten()});
        }
        if (dong.isEmpty()) {
            GiaoDien.in("Khong co tai khoan nao dang cho duyet.");
            return;
        }
        GiaoDien.inBang(new String[]{"TAI KHOAN", "HO TEN", "LOAI"}, new int[]{10, 24, 24},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI}, dong);
        String ma = docChuoi("Ma tai khoan can duyet: ");
        TaiKhoan tk = dsTK.timTheoTen(ma);
        NhanVien nv = ql.timTheoMa(ma);
        if (tk == null || nv == null || !tk.choDuyet()) {
            GiaoDien.in("Khong co tai khoan nao dang cho duyet voi ma nay.");
            return;
        }
        if (nv.getPhongBan() == null) {
            GiaoDien.in("Danh sach phong ban hien co:");
            dsPB.xuatDanhSach();
            String maPB = docChuoi("Ma phong ban de xep cho nhan vien (bo trong neu chua xep): ");
            if (!maPB.isBlank()) {
                PhongBan pb = dsPB.timTheoMa(maPB);
                if (pb == null) {
                    GiaoDien.in("Khong tim thay phong ban; nhan vien chua duoc xep phong.");
                } else {
                    ql.capNhatThongTinCoBan(nv.getMaNV(), nv.getHoTen(), nv.getNgaySinh(), nv.getGioiTinh(),
                            nv.getSdt(), nv.getEmail(), nv.getLuongCoBan(), nv.getNgayVaoLam(), nv.getTrangThai(), pb);
                }
            }
        }
        tk.setTrangThai(TaiKhoan.HOAT_DONG);
        dsTK.ghiFile();
        ghiNhatKy("Duyet tai khoan", tk.getTenDangNhap());
        GiaoDien.in("Da duyet tai khoan " + tk.getTenDangNhap() + ".");
    }

    private static void tkDatLai() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String ma = docChuoi("Ma tai khoan can dat lai mat khau: ");
        if (dsTK.timTheoTen(ma) == null) {
            GiaoDien.in("Khong tim thay tai khoan.");
            return;
        }
        String mk = dsTK.datLaiMatKhauMacDinh(ma);
        dsTK.ghiFile();
        ghiNhatKy("Dat lai mat khau ve mac dinh", ma.trim().toUpperCase());
        GiaoDien.in("Da dat lai. Mat khau tam: " + mk + " (nguoi dung phai doi o lan dang nhap ke tiep).");
    }

    private static void tkKhoa() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String ma = docChuoi("Ma tai khoan can khoa: ");
        TaiKhoan tk = dsTK.timTheoTen(ma);
        if (tk == null) {
            GiaoDien.in("Khong tim thay tai khoan.");
            return;
        }
        if (phien.getMaNV().equalsIgnoreCase(tk.getTenDangNhap())) {
            ghiNhatKy("Khoa tai khoan", tk.getTenDangNhap(), NhatKyHeThong.TU_CHOI);
            GiaoDien.in("Khong the tu khoa tai khoan cua chinh minh.");
            return;
        }
        NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
        if (nv != null && laGiamDocCuoi(nv)) {
            GiaoDien.in("Day la giam doc cuoi cung dang hoat dong, khong the khoa.");
            return;
        }
        tk.setTrangThai(TaiKhoan.KHOA);
        dsTK.ghiFile();
        ghiNhatKy("Khoa tai khoan", tk.getTenDangNhap());
        GiaoDien.in("Da khoa tai khoan " + tk.getTenDangNhap() + ".");
    }

    private static void tkMoKhoa() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String ma = docChuoi("Ma tai khoan can mo khoa: ");
        TaiKhoan tk = dsTK.timTheoTen(ma);
        if (tk == null) {
            GiaoDien.in("Khong tim thay tai khoan.");
            return;
        }
        if (!tk.daKhoa()) {
            GiaoDien.in("Tai khoan nay khong o trang thai khoa.");
            return;
        }
        tk.setTrangThai(TaiKhoan.HOAT_DONG);
        tk.datLaiSoLanSai();
        dsTK.ghiFile();
        ghiNhatKy("Mo khoa tai khoan", tk.getTenDangNhap());
        GiaoDien.in("Da mo khoa tai khoan " + tk.getTenDangNhap() + ".");
    }

//----------------- NHAT KY HE THONG (giam doc) -----------------
    private static void menuNhatKy() {
        List<Muc> m = new ArrayList<>();
        m.add(muc("Xem 30 hoat dong gan nhat", "NHAT KY - GAN DAY", () -> {
            yeuCau(Quyen.XEM_NHAT_KY);
            DanhSachNhatKy.inBang(dsNK.ganDay(30));
        }));
        m.add(muc("Xem theo ma nguoi thuc hien", "NHAT KY - THEO NGUOI", () -> {
            yeuCau(Quyen.XEM_NHAT_KY);
            DanhSachNhatKy.inBang(dsNK.timTheoNguoi(docChuoi("Ma nguoi thuc hien: ")));
        }));
        m.add(muc("Xem cac lan bi tu choi / that bai", "NHAT KY - BAT THUONG", () -> {
            yeuCau(Quyen.XEM_NHAT_KY);
            DanhSachNhatKy.inBang(dsNK.timBatThuong());
        }));
        chayMenu("NHAT KY HE THONG", m, "Quay lai", null);
    }

//----------------- LUU / DOC / THOAT -----------------
    private static void dangXuat() {
        ghiNhatKy("Dang xuat", phien.getMaNV());
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
        dsTP.ghiFile(); dsNP.ghiFile(); dsQD.ghiFile(); dsBL.ghiFile(); dsTK.ghiFile(); dsNK.ghiFile();
        ghiNhatKy("Luu toan bo du lieu", "-");
        GiaoDien.in("Da luu toan bo du lieu vao thu muc data.");
    }

    //chi giam doc: doc lai se GHI DE du lieu dang co trong bo nho
    private static void docDuLieu() {
        yeuCau(Quyen.DOC_DU_LIEU);
        dsPB.docFile(); ql.docFile(); dsCC.docFile(); dsBH.docFile();
        dsTP.docFile(); dsNP.docFile(); dsQD.docFile(); dsBL.docFile();
        napTaiKhoan();
        NhanVien moi = ql.timTheoMa(phien.getMaNV());           //lam moi phien theo du lieu vua doc
        if (moi != null) phien = phien.lamMoi(moi);
        ghiNhatKy("Doc lai du lieu tu file", "-");
        GiaoDien.in("Da doc xong du lieu tu file.");
    }
}
