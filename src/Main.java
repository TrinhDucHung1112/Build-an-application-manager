import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    private static final DanhSachLichSuCongTac dsLS = new DanhSachLichSuCongTac();      //lich su phong ban/chuc vu/luong theo nam
    private static final DanhSachLichSuHopDong dsHDLD = new DanhSachLichSuHopDong();    //hop dong lao dong theo nam
    private static final DanhSachYeuCau dsYC = new DanhSachYeuCau();                    //yeu cau gui len cap tren duyet

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
        GiaoDien.setDocDong(Main::docChuoi);       //cho phep bang dai tu chia trang
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
    private static void chayMenu(String tieuDe, Muc[] cacMuc, String nhanThoat, Runnable khiThoat) {
        int chon;
        do {
            String[] nhan = new String[cacMuc.length + 1];
            for (int i = 0; i < cacMuc.length; i++) nhan[i] = (i + 1) + ". " + cacMuc[i].ten;
            nhan[cacMuc.length] = "0. " + nhanThoat;
            chon = menu(tieuDe, nhan, cacMuc.length);
            if (chon == 0) {
                if (khiThoat != null) khiThoat.run();
            } else {
                cacMuc[chon - 1].viec.run();
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
        Muc[] m = new Muc[0];
        m = Mang.them(m, new Muc("Thong tin ca nhan cua toi (ho so, cham cong, nghi phep, luong...)", () -> menuCaNhan(false)));
        if (laGiamDoc() && co(Quyen.XEM_PHONG_BAN)) m = Mang.them(m, new Muc("Phong ban quan li", Main::menuPhongBan));
        if (co(Quyen.XEM_NHAN_VIEN)) m = Mang.them(m, new Muc("Quan ly nhan vien", Main::menuNhanVien));
        if (co(Quyen.XEM_CHAM_CONG)) m = Mang.them(m, new Muc("Quan ly cham cong", Main::menuChamCong));
        if (rongHon(Quyen.XEM_BAO_HIEM) || co(Quyen.QUAN_LY_BAO_HIEM)) m = Mang.them(m, new Muc("Quan ly bao hiem", Main::menuBaoHiem));
        if (rongHon(Quyen.XEM_LUONG) || co(Quyen.LAP_BANG_LUONG)) m = Mang.them(m, new Muc("Bang luong", Main::menuBangLuong));
        if (co(Quyen.XEM_BAO_CAO_CONG_TY) || co(Quyen.XEM_BAO_CAO_PHONG)) m = Mang.them(m, new Muc("Bao cao", Main::menuBaoCao));
        if (rongHon(Quyen.XEM_THUONG_PHAT) || co(Quyen.THEM_THUONG_PHAT)) m = Mang.them(m, new Muc("Quan ly thuong phat", Main::menuThuongPhat));
        if (rongHon(Quyen.XEM_NGHI_PHEP)) m = Mang.them(m, new Muc("Quan ly nghi phep", Main::menuNghiPhep));
        if (co(Quyen.DUYET_YEU_CAU) || co(Quyen.DUYET_NGHI_PHEP)) m = Mang.them(m, new Muc("Duyet yeu cau (sua ho so, nghi phep)", Main::menuDuyetYeuCau));
        m = Mang.them(m, new Muc("Quy dinh thuong phat", Main::menuQuyDinhThuongPhat));
        if (co(Quyen.QUAN_LY_TAI_KHOAN)) m = Mang.them(m, new Muc("Quan ly tai khoan", Main::menuTaiKhoan));
        if (co(Quyen.XEM_NHAT_KY)) m = Mang.them(m, new Muc("Nhat ky he thong", Main::menuNhatKy));
        m = Mang.them(m, muc("Doi mat khau", "DOI MAT KHAU", Main::doiMatKhau));
        m = Mang.them(m, muc("Luu toan bo du lieu", "LUU TOAN BO DU LIEU", Main::luuDuLieu));
        if (co(Quyen.DOC_DU_LIEU)) m = Mang.them(m, muc("Doc du lieu tu file", "DOC DU LIEU TU FILE", Main::docDuLieu));
        chayMenu("QUAN LY NHAN SU - " + phien.getVaiTro().ten().toUpperCase() + " (" + phien.getMaNV() + ")",
                m, "Dang xuat (ve man hinh dang nhap)", () -> chay("DANG XUAT", Main::dangXuat));
    }

    //menu ca nhan: moi nguoi chi thay du lieu cua chinh minh. laMenuGoc=true (nhan vien): chon 0 la dang xuat
    private static void menuCaNhan(boolean laMenuGoc) {
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Ho so cua toi (chon nam)", "HO SO CUA TOI", Main::cnHoSo));
        if (co(Quyen.XEM_PHONG_BAN)) m = Mang.them(m, muc("Phong ban cua toi (chon nam)", "PHONG BAN CUA TOI", Main::cnPhongBan));
        m = Mang.them(m, muc("Cham cong cua toi (chon nam)", "CHAM CONG CUA TOI", Main::cnChamCong));
        if (co(Quyen.TU_CHAM_CONG)) m = Mang.them(m, muc("Cham cong hom nay (tu cham cong)", "TU CHAM CONG HOM NAY", Main::ccTuChamCong));
        m = Mang.them(m, muc("Thong ke gio lam cua toi theo thang (chon nam, thang)", "THONG KE GIO LAM CUA TOI", () -> ccThongKeCho(nguoi())));
        m = Mang.them(m, new Muc("Nghi phep cua toi", Main::menuNghiPhepCuaToi));
        m = Mang.them(m, muc("Bang luong cua toi (chon nam, thang)", "BANG LUONG CUA TOI", Main::cnBangLuong));
        m = Mang.them(m, muc("Bao hiem cua toi (chon nam)", "BAO HIEM CUA TOI", Main::cnBaoHiem));
        m = Mang.them(m, muc("Thuong/phat cua toi (chon nam)", "THUONG PHAT CUA TOI", Main::cnThuongPhat));
        if (co(Quyen.XEM_HOP_DONG)) m = Mang.them(m, muc("Hop dong lao dong cua toi (chon nam)", "HOP DONG CUA TOI", Main::cnHopDong));
        if (!laGiamDoc()) m = Mang.them(m, muc("Yeu cau chinh sua ho so cua toi", "YEU CAU CHINH SUA HO SO CUA TOI", Main::yeuCauCuaToi));
        m = Mang.them(m, muc("Quy dinh thuong phat", "QUY DINH THUONG PHAT", Main::qdDanhSachChung2));
        m = Mang.them(m, muc("Doi mat khau", "DOI MAT KHAU", Main::doiMatKhau));
        if (laMenuGoc) m = Mang.them(m, muc("Luu du lieu", "LUU DU LIEU", Main::luuDuLieu));
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
            thongBaoYeuCau();
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
        dsLS.docFile();
        dsHDLD.docFile();
        dsYC.docFile();
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
            catch (java.util.NoSuchElementException e) { throw e; }
            catch (Exception e) { GiaoDien.in("Nhap ngay theo yyyy-MM-dd."); }
        }
    }

    private static LocalTime docTime(String prompt) {
        while (true) {
            try { return LocalTime.parse(docChuoi(prompt)); }
            catch (java.util.NoSuchElementException e) { throw e; }
            catch (Exception e) { GiaoDien.in("Nhap gio theo HH:mm."); }
        }
    }


//----------------- PHONG BAN -----------------
    private static void menuPhongBan() {
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Xem phong ban quan li (chon nam; tim ma / cac loai phong ban / xem het)", "DANH SACH PHONG BAN", Main::pbDanhSachChung2));
        if (co(Quyen.QUAN_LY_PHONG_BAN)) {
            m = Mang.them(m, muc("Them phong ban", "THEM PHONG BAN", Main::pbThem));
            m = Mang.them(m, muc("Xoa phong ban", "XOA PHONG BAN", Main::pbXoa));
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
        boolean ok = dsPB.themPhong(new PhongBan(ma, ten, mota, laGiamDoc() ? phien.getMaNV() : ""));
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
        Muc[] m = new Muc[0];
        if (co(Quyen.THEM_NHAN_VIEN)) {
            m = Mang.them(m, muc("Them 1 nhan vien", "THEM NHAN VIEN", Main::themNhanVien));
            m = Mang.them(m, muc("Nhap nhieu nhan vien", "NHAP NHIEU NHAN VIEN", Main::nvNhapNhieu));
        }
        m = Mang.them(m, muc("Xem danh sach nhan vien (chon nam, tim ma, theo phong ban)", "DANH SACH NHAN VIEN", Main::nvDanhSachChung2));
        m = Mang.them(m, muc("Tim kiem (ma, ten, SDT, email)", "TIM KIEM NHAN VIEN", Main::nvTimKiem));
        m = Mang.them(m, muc("Xem chi tiet 1 nhan vien", "CHI TIET NHAN VIEN", Main::nvChiTiet));
        if (rongHon(Quyen.XEM_LUONG)) m = Mang.them(m, muc("Tim theo khoang luong", "TIM THEO KHOANG LUONG", Main::nvTheoLuong));
        if (co(Quyen.SUA_NHAN_VIEN) || co(Quyen.SUA_LIEN_HE)) {
            m = Mang.them(m, muc("Sua thong tin nhan vien", "SUA NHAN VIEN", Main::suaNhanVienTuMenu));
        }
        if (co(Quyen.CHO_NGHI_VIEC)) m = Mang.them(m, muc("Cho nhan vien nghi viec", "CHO NGHI VIEC", Main::nvChoNghiViec));
        if (co(Quyen.XOA_NHAN_VIEN)) m = Mang.them(m, muc("Xoa nhan vien khoi he thong", "XOA NHAN VIEN", Main::nvXoa));
        if (rongHon(Quyen.XEM_HOP_DONG)) m = Mang.them(m, muc("Xem hop dong lao dong", "HOP DONG LAO DONG", Main::nvHopDong));
        if (co(Quyen.XEM_BAO_CAO_CONG_TY)) {
            m = Mang.them(m, muc("Thong ke nhan vien theo loai", "THONG KE THEO LOAI", ql::thongKeTheoLoaiNv));
            m = Mang.them(m, muc("Thong ke luong nhan vien", "THONG KE LUONG", ql::thongKeLuong));
        }
        chayMenu("QUAN LY NHAN VIEN", m, "Quay lai", null);
    }

    //bang nhan vien: cot LUONG chi hien voi nguoi ma nguoi dang nhap duoc xem luong
    private static void inBangNhanVien(NhanVien[] ds) {
        String[][] dong = new String[0][];
        for (NhanVien nv : ds) {
            String phong = nv.getPhongBan() == null ? "-" : nv.getPhongBan().getTenPB();
            String chucVu = nv.getChucVu() == null ? "-" : nv.getChucVu().getTenCV();
            String luong = choPhep(Quyen.XEM_LUONG, nv) ? GiaoDien.tien(nv.tinhLuong()) : "***";
            dong = Mang.them(dong, new String[]{nv.getMaNV(), nv.getHoTen(), phong, chucVu,
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
        String[][] cap = new String[0][];
        cap = Mang.them(cap, new String[]{"Ma nhan vien", nv.getMaNV()});
        cap = Mang.them(cap, new String[]{"Ho ten", nv.getHoTen()});
        cap = Mang.them(cap, new String[]{"Gioi tinh", nv.getGioiTinh()});
        cap = Mang.them(cap, new String[]{"So dien thoai", nv.getSdt()});
        cap = Mang.them(cap, new String[]{"Email", nv.getEmail()});
        cap = Mang.them(cap, new String[]{"Phong ban", nv.getPhongBan() == null ? "-" : nv.getPhongBan().getTenPB()});
        cap = Mang.them(cap, new String[]{"Chuc vu", nv.getChucVu() == null ? "-" : nv.getChucVu().getTenCV()});
        cap = Mang.them(cap, new String[]{"Loai nhan vien", PhanQuyen.xacDinhVaiTro(nv).ten()});
        cap = Mang.them(cap, new String[]{"Trang thai", nv.getTrangThai()});
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
        String[][] dong = new String[0][];
        int sapHet = 0;
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_HOP_DONG)) {
            long ngayCon = soNgayConHopDong(nv);
            if (ngayCon == Long.MIN_VALUE) continue;
            HopDongLaoDong hd = ((NhanVienHopDong) nv).gethopDong();
            String tinhTrang;
            if (ngayCon < 0) tinhTrang = "DA HET HAN";
            else if (ngayCon <= SO_NGAY_CANH_BAO_HOP_DONG) { tinhTrang = "SAP HET HAN (con " + ngayCon + " ngay)"; sapHet++; }
            else tinhTrang = "Con " + ngayCon + " ngay";
            dong = Mang.them(dong, new String[]{nv.getMaNV(), nv.getHoTen(), hd.getmaHD(), String.valueOf(hd.getngayKy()),
                    String.valueOf(hd.getngayHetHan()), tinhTrang});
        }
        if ((dong.length == 0)) {
            GiaoDien.in("Khong co hop dong lao dong nao trong pham vi cua ban.");
            return;
        }
        GiaoDien.inBang(
                new String[]{"MA NV", "HO TEN", "MA HD", "NGAY KY", "HET HAN", "TINH TRANG"},
                new int[]{8, 22, 10, 10, 10, 26},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
        GiaoDien.in("Tong " + dong.length + " hop dong, trong do " + sapHet + " hop dong sap het han.");
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
        if (doiDuocLuongChucVu) dsLS.ghiNhanTruocKhiDoi(nv);        //giu nguyen phong ban/chuc vu/luong cua cac nam truoc
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
            dsLS.xoaTheoMaNV(ma);
            dsHDLD.xoaTheoMaNV(ma);
            dsYC.xoaTheoMaNV(ma);
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
        Muc[] m = new Muc[0];
        if (co(Quyen.THEM_CHAM_CONG)) m = Mang.them(m, muc("Them cham cong cho nhan vien", "CHAM CONG - THEM MOI", Main::ccThem));
        m = Mang.them(m, muc("Xem danh sach cham cong (chon nam, tim ma, theo phong ban)", "CHAM CONG - DANH SACH", Main::ccDanhSachChung2));
        if (co(Quyen.XOA_CHAM_CONG)) m = Mang.them(m, muc("Xoa cham cong", "CHAM CONG - XOA", Main::ccXoa));
        m = Mang.them(m, muc("Thong ke gio theo nhan vien/thang", "CHAM CONG - THONG KE THEO THANG", Main::ccThongKe));
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
        ChamCong[] gop = new ChamCong[0];
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_CHAM_CONG)) {
            for (ChamCong cc : dsCC.timTheoMaNV(nv.getMaNV())) gop = Mang.them(gop, cc);
        }
        DanhSachChamCong.inBang(gop);
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


//----------------- BAO HIEM -----------------
    private static void menuBaoHiem() {
        Muc[] m = new Muc[0];
        if (co(Quyen.QUAN_LY_BAO_HIEM)) {
            m = Mang.them(m, muc("Them BHXH", "BAO HIEM - THEM BHXH", Main::bhThemXH));
            m = Mang.them(m, muc("Them BHYT", "BAO HIEM - THEM BHYT", Main::bhThemYT));
        }
        m = Mang.them(m, muc("Xem danh sach bao hiem (chon nam, tim ma, theo phong ban)", "BAO HIEM - DANH SACH", Main::bhDanhSachChung2));
        if (co(Quyen.QUAN_LY_BAO_HIEM)) m = Mang.them(m, muc("Xoa theo ma BH", "BAO HIEM - XOA", Main::bhXoa));
        m = Mang.them(m, muc("Tinh tien dong", "BAO HIEM - TINH TIEN DONG", Main::bhTinhTien));
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
        BaoHiem[] gop = new BaoHiem[0];
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_BAO_HIEM)) {
            for (BaoHiem bh : dsBH.timTheoMaNV(nv.getMaNV())) gop = Mang.them(gop, bh);
        }
        if ((gop.length == 0)) GiaoDien.in("Khong co du lieu.");
        else DanhSachBaoHiem.inBang(gop);
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
        String[][] dong = new String[0][];
        dong = Mang.them(dong, new String[]{"Luong dong bao hiem", GiaoDien.tien(mucLuong) + " VND"});
        dong = Mang.them(dong, new String[]{"Nhan vien dong", GiaoDien.tien(dsBH.tongTienNhanVienDong(maNV, mucLuong)) + " VND"});
        dong = Mang.them(dong, new String[]{"Doanh nghiep dong", GiaoDien.tien(dsBH.tongTienDoanhNghiepDong(maNV, mucLuong)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- BANG LUONG -----------------
    private static void menuBangLuong() {
        Muc[] m = new Muc[0];
        if (co(Quyen.LAP_BANG_LUONG)) m = Mang.them(m, muc("Lap bang luong cho tat ca nhan vien", "BANG LUONG - LAP CHO TAT CA", Main::blLap));
        m = Mang.them(m, muc("Xem bang luong (chon nam, tim ma, theo phong ban)", "BANG LUONG - DANH SACH", Main::blDanhSachChung2));
        if (co(Quyen.LAP_BANG_LUONG)) m = Mang.them(m, muc("Thong ke quy luong", "BANG LUONG - THONG KE QUY LUONG", Main::blThongKe));
        chayMenu("BANG LUONG", m, "Quay lai", null);
    }

    private static void blLap() {
        yeuCau(Quyen.LAP_BANG_LUONG);
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
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
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        if (phamVi(Quyen.XEM_LUONG) == PhamVi.TAT_CA) {
            ghiNhatKy("Xem bang luong cong ty thang " + thang + "/" + nam, "TAT CA");
            dsBL.xuatBangLuongCongTy(thang, nam);
            return;
        }
        BangLuong[] gop = new BangLuong[0];
        double tong = 0;
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_LUONG)) {
            BangLuong bl = dsBL.timTheoMaNV(nv.getMaNV(), thang, nam);
            if (bl != null) {
                gop = Mang.them(gop, bl);
                tong += bl.getLuongThucNhan();
            }
        }
        if ((gop.length == 0)) {
            GiaoDien.in("Chua co bang luong thang " + thang + "/" + nam + " trong pham vi cua ban.");
            return;
        }
        ghiNhatKy("Xem bang luong thang " + thang + "/" + nam + " (pham vi phong)", "PHONG");
        DanhSachBangLuong.inBang(gop);
        GiaoDien.in("Tong luong thuc nhan (" + gop.length + " nhan vien): " + GiaoDien.tien(tong) + " VND");
    }

    private static void blThongKe() {
        yeuCau(Quyen.LAP_BANG_LUONG);
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        String[][] dong = new String[0][];
        dong = Mang.them(dong, new String[]{"Thang / nam", thang + "/" + nam});
        dong = Mang.them(dong, new String[]{"Tong quy luong", GiaoDien.tien(dsBL.tongQuyLuong(thang, nam)) + " VND"});
        dong = Mang.them(dong, new String[]{"Luong trung binh", GiaoDien.tien(dsBL.luongTrungBinh(thang, nam)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- BAO CAO -----------------
    private static void menuBaoCao() {
        Muc[] m = new Muc[0];
        if (co(Quyen.XEM_BAO_CAO_CONG_TY)) {
            m = Mang.them(m, muc("Bao cao toan cong ty", "BAO CAO - TOAN CONG TY", Main::bcCongTy));
            m = Mang.them(m, muc("Bao cao theo phong ban (chon nam, tim ma, theo phong ban)", "BAO CAO - THEO PHONG BAN", Main::bcPhongBanChung2));
            m = Mang.them(m, muc("Thong ke nhan su", "BAO CAO - THONG KE NHAN SU", () -> baoCao.thongKeNhanSu(ql, dsBL)));
            m = Mang.them(m, muc("Thong ke bao hiem", "BAO CAO - THONG KE BAO HIEM", baoCao::thongKeBaoHiem));
        } else {
            m = Mang.them(m, muc("Bao cao phong ban cua toi", "BAO CAO - PHONG BAN CUA TOI", Main::bcPhongToi));
        }
        chayMenu("BAO CAO", m, "Quay lai", null);
    }

    private static void bcCongTy() {
        yeuCau(Quyen.XEM_BAO_CAO_CONG_TY);
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        GiaoDien.in("");
        baoCao.inBaoCaoToanCongTy(ql, dsBL, thang, nam);
    }

    private static void bcPhongBan() {
        yeuCau(Quyen.XEM_BAO_CAO_CONG_TY);
        dsPB.xuatDanhSach();
        String maPB = docChuoi("Ma phong ban: ");
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
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
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        NhanVien[] nhanSu = nvTrongQuyen(Quyen.XEM_NHAN_VIEN);
        String[][] dong = new String[0][];
        double tongLuong = 0;
        int coBangLuong = 0;
        boolean xemLuong = rongHon(Quyen.XEM_LUONG);
        for (NhanVien nv : nhanSu) {
            String ma = nv.getMaNV();
            int donCho = 0;
            for (NghiPhep np : dsNP.timTheoMaNV(ma)) if ("Cho duyet".equals(np.getTrangThai())) donCho++;
            dong = Mang.them(dong, new String[]{ma, nv.getHoTen(), PhanQuyen.xacDinhVaiTro(nv).ten(),
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
        Muc[] m = new Muc[0];
        if (co(Quyen.THEM_THUONG_PHAT)) m = Mang.them(m, muc("Them thuong/phat thu cong", "THUONG PHAT - THEM THU CONG", Main::tpThem));
        m = Mang.them(m, muc("Xem danh sach thuong/phat (chon nam, tim ma, theo phong ban)", "THUONG PHAT - DANH SACH", Main::tpDanhSachChung2));
        if (co(Quyen.XOA_THUONG_PHAT)) m = Mang.them(m, muc("Xoa theo ma thuong/phat", "THUONG PHAT - XOA", Main::tpXoa));
        m = Mang.them(m, muc("Thong ke thuong/phat theo thang", "THUONG PHAT - THONG KE THEO THANG", Main::tpThongKe));
        if (co(Quyen.TU_DONG_XET_THUONG_PHAT)) {
            m = Mang.them(m, muc("Tu dong xet thuong/phat theo quy dinh", "THUONG PHAT - TU DONG XET", Main::tpTuDongXet));
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
        ThuongPhat[] gop = new ThuongPhat[0];
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_THUONG_PHAT)) {
            for (ThuongPhat tp : dsTP.timTheoMaNV(nv.getMaNV())) gop = Mang.them(gop, tp);
        }
        if ((gop.length == 0)) GiaoDien.in("Khong co du lieu.");
        else DanhSachThuongPhat.inBang(gop);
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
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        String[][] dong = new String[0][];
        dong = Mang.them(dong, new String[]{"Nhan vien", maNV});
        dong = Mang.them(dong, new String[]{"Thang / nam", thang + "/" + nam});
        dong = Mang.them(dong, new String[]{"Tong thuong", GiaoDien.tien(dsTP.tongThuong(maNV, thang, nam)) + " VND"});
        dong = Mang.them(dong, new String[]{"Tong phat", GiaoDien.tien(dsTP.tongPhat(maNV, thang, nam)) + " VND"});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

    private static void tpTuDongXet() {
        yeuCau(Quyen.TU_DONG_XET_THUONG_PHAT);
        int[] thangNam = docThangNam();
        if (thangNam == null) return;
        int thang = thangNam[0];
        int nam = thangNam[1];
        dsTP.tuDongXetThuongPhat(thang, nam, ql.getDanhSachNV(),
                dsQD.getDanhSach(), dsCC, dsNP.getDanhSach());
        dsTP.ghiFile();
        ghiNhatKy("Tu dong xet thuong/phat thang " + thang + "/" + nam, "TAT CA");
        GiaoDien.in("Da tu dong xet thuong/phat theo cac quy dinh hien co.");
    }

//----------------- NGHI PHEP -----------------
    private static void menuNghiPhep() {
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Tao don nghi phep", "NGHI PHEP - TAO DON", Main::npTao));
        m = Mang.them(m, muc("Xem don nghi phep (chon nam, tim ma, theo phong ban)", "NGHI PHEP - DANH SACH", Main::npDanhSachChung2));
        if (co(Quyen.DUYET_NGHI_PHEP)) m = Mang.them(m, muc("Duyet don (danh sach don dang cho)", "NGHI PHEP - DUYET DON", Main::npDuyetCoDanhSach));
        m = Mang.them(m, muc("Xoa don", "NGHI PHEP - XOA DON", Main::npXoa));
        chayMenu("QUAN LY NGHI PHEP", m, "Quay lai", null);
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
        if (tuNgay.isBefore(nv.getNgayVaoLam())) {
            GiaoDien.in("Tu ngay khong duoc truoc ngay vao lam (" + nv.getNgayVaoLam() + ").");
            return;
        }
        boolean giamDoc = PhanQuyen.xacDinhVaiTro(nv) == VaiTro.GIAM_DOC;      //giam doc khong co cap tren nen don duoc duyet ngay
        if (giamDoc) np.duyetNghiPhep();
        boolean ok = dsNP.them(np);
        if (ok) ghiNhatKy("Tao don nghi phep " + maNP, nv.getMaNV());
        GiaoDien.in(ok ? (giamDoc ? "Da tao don nghi phep (giam doc: tu dong duyet)." : "Da tao don nghi phep (cho duyet).") : "Tao don that bai (kiem tra ngay).");
    }

    private static void npXem() {
        if (phamVi(Quyen.XEM_NGHI_PHEP) == PhamVi.TAT_CA) {
            dsNP.xuatDanhSach();
            return;
        }
        NghiPhep[] gop = new NghiPhep[0];
        for (NhanVien nv : nvTrongQuyen(Quyen.XEM_NGHI_PHEP)) {
            for (NghiPhep np : dsNP.timTheoMaNV(nv.getMaNV())) gop = Mang.them(gop, np);
        }
        if ((gop.length == 0)) GiaoDien.in("Khong co don.");
        else DanhSachNghiPhep.inBang(gop);
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
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Xem danh sach quy dinh (tim ma, xem het)", "QUY DINH - DANH SACH", Main::qdDanhSachChung2));
        if (co(Quyen.QUAN_LY_QUY_DINH)) {
            m = Mang.them(m, muc("Them quy dinh", "QUY DINH - THEM MOI", Main::qdThem));
            m = Mang.them(m, muc("Xoa quy dinh", "QUY DINH - XOA", Main::qdXoa));
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




//----------------- QUAN LY TAI KHOAN (giam doc) -----------------
    private static void menuTaiKhoan() {
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Danh sach tai khoan (tim ma, theo phong ban, xem het)", "TAI KHOAN - DANH SACH", Main::tkDanhSach));
        m = Mang.them(m, muc("Duyet tai khoan dang ky moi (va xep phong ban)", "TAI KHOAN - DUYET", Main::tkDuyet));
        m = Mang.them(m, muc("Dat lai mat khau ve mac dinh", "TAI KHOAN - DAT LAI MAT KHAU", Main::tkDatLai));
        m = Mang.them(m, muc("Khoa tai khoan", "TAI KHOAN - KHOA", Main::tkKhoa));
        m = Mang.them(m, muc("Mo khoa tai khoan", "TAI KHOAN - MO KHOA", Main::tkMoKhoa));
        chayMenu("QUAN LY TAI KHOAN", m, "Quay lai", null);
    }


    private static void tkDuyet() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String[][] dong = new String[0][];
        for (TaiKhoan tk : dsTK.getDanhSach()) {
            if (!tk.choDuyet()) continue;
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            dong = Mang.them(dong, new String[]{tk.getTenDangNhap(), nv == null ? "-" : nv.getHoTen(),
                    nv == null ? "-" : PhanQuyen.xacDinhVaiTro(nv).ten()});
        }
        if ((dong.length == 0)) {
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
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Xem 30 hoat dong gan nhat", "NHAT KY - GAN DAY", () -> {
            yeuCau(Quyen.XEM_NHAT_KY);
            DanhSachNhatKy.inBang(dsNK.ganDay(30));
        }));
        m = Mang.them(m, muc("Xem nhat ky (chon nam, tim ma, theo phong ban)", "NHAT KY - DANH SACH", Main::nkChung2));
        m = Mang.them(m, muc("Xem cac lan bi tu choi / that bai", "NHAT KY - BAT THUONG", () -> {
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
        dsLS.ghiFile(); dsHDLD.ghiFile(); dsYC.ghiFile();
        ghiNhatKy("Luu toan bo du lieu", "-");
        GiaoDien.in("Da luu toan bo du lieu vao thu muc data.");
    }

    //chi giam doc: doc lai se GHI DE du lieu dang co trong bo nho
    private static void docDuLieu() {
        yeuCau(Quyen.DOC_DU_LIEU);
        dsPB.docFile(); ql.docFile(); dsCC.docFile(); dsBH.docFile();
        dsTP.docFile(); dsNP.docFile(); dsQD.docFile(); dsBL.docFile();
        dsLS.docFile(); dsHDLD.docFile(); dsYC.docFile();
        napTaiKhoan();
        NhanVien moi = ql.timTheoMa(phien.getMaNV());           //lam moi phien theo du lieu vua doc
        if (moi != null) phien = phien.lamMoi(moi);
        ghiNhatKy("Doc lai du lieu tu file", "-");
        GiaoDien.in("Da doc xong du lieu tu file.");
    }

//=====================================================================================================
//----------------- "CHUNG": CHON NAM / THANG ROI MOI XEM (ho so, phong ban, cham cong, luong...) -----------------
//=====================================================================================================
    private static final String[] TRUONG_AN_TOAN = {"HO_TEN", "NGAY_SINH", "GIOI_TINH", "SDT", "EMAIL"};
    private static final String[] TRUONG_DAY_DU = {"HO_TEN", "NGAY_SINH", "GIOI_TINH", "SDT", "EMAIL",
            "NGAY_VAO_LAM", "PHONG_BAN", "CHUC_VU", "LUONG_CO_BAN"};

    private static int namNay() {
        return LocalDate.now().getYear();
    }

    private static boolean laGiamDoc() {
        return phien != null && phien.getVaiTro() == VaiTro.GIAM_DOC;
    }

    //chi giam doc moi duoc sua truc tiep ho so cua chinh minh (nguoi khac phai gui yeu cau)
    private static void yeuCauLaGiamDoc() {
        if (!laGiamDoc()) {
            ghiNhatKy("Sua truc tiep ho so", nguoi().getMaNV(), NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Chi giam doc moi duoc sua truc tiep; cac vai tro khac phai gui yeu cau.");
        }
    }

    private static String tenPB(String maPB) {
        if (maPB == null || maPB.isBlank()) return "(chua phan)";
        PhongBan pb = dsPB.timTheoMa(maPB);
        return pb == null ? maPB : pb.getTenPB();
    }

    //so voi nam truoc: doi phong ban / chuc vu / luong khong
    private static String thayDoi(NhanVien nv, int nam) {
        if (nam <= nv.getNgayVaoLam().getYear()) return "Vao lam";
        LichSuCongTac a = dsLS.xacDinh(nv, nam - 1).ls;
        LichSuCongTac b = dsLS.xacDinh(nv, nam).ls;
        String[] d = new String[0];
        if (!a.getMaPB().equalsIgnoreCase(b.getMaPB())) d = Mang.them(d, "Doi phong ban");
        if (!a.getMaCV().equalsIgnoreCase(b.getMaCV())) d = Mang.them(d, "Doi chuc vu");
        if (a.getLuongCoBan() != b.getLuongCoBan()) d = Mang.them(d, "Doi luong");
        return (d.length == 0) ? "Giu nguyen" : String.join(", ", d);
    }

    //---- chon nam: moi nam 1 dong (NAM | TRANG THAI | cac cot them) ----
    private static ChonKy.KetQua chonNam(NhanVien nv, int namCuoi, String[] cotThem, int[] rongThem, String[] canhThem,
                                         java.util.function.IntFunction<String[]> cot) {
        return chonTrongCacNam(ChonKy.cacNam(nv.getNgayVaoLam(), namCuoi), nv.getNgayVaoLam(), cotThem, rongThem, canhThem, cot);
    }

    private static ChonKy.KetQua chonTrongCacNam(int[] cacNam, LocalDate tuNgay, String[] cotThem, int[] rongThem,
                                                 String[] canhThem, java.util.function.IntFunction<String[]> cot) {
        int n = 2 + cotThem.length;
        String[] td = new String[n];
        int[] rong = new int[n];
        String[] canh = new String[n];
        td[0] = "NAM"; rong[0] = 6; canh[0] = GiaoDien.GIUA;
        td[1] = "TRANG THAI"; rong[1] = 12; canh[1] = GiaoDien.TRAI;
        for (int i = 0; i < cotThem.length; i++) {
            td[2 + i] = cotThem[i];
            rong[2 + i] = rongThem[i];
            canh[2 + i] = canhThem[i];
        }
        String[][] dong = new String[0][];
        for (int nam : cacNam) {
            String[] them = cot.apply(nam);
            String[] r = new String[n];
            r[0] = String.valueOf(nam);
            r[1] = ChonKy.trangThaiNam(nam);
            System.arraycopy(them, 0, r, 2, them.length);
            dong = Mang.them(dong, r);
        }
        return ChonKy.chonNam(cacNam, td, rong, canh, dong, tuNgay, Main::docChuoi);
    }

    //---- chon thang cua 1 nam: 12 dong (THANG | TRANG THAI | cac cot them), cot them dau tien la trang thai cua thang ----
    private static Integer chonThangCuaNam(LocalDate tuNgay, int nam, String[] cotThem, int[] rongThem, String[] canhThem,
                                           java.util.function.IntFunction<String[]> cot) {
        int n = 1 + cotThem.length;
        String[] td = new String[n];
        int[] rong = new int[n];
        String[] canh = new String[n];
        td[0] = "THANG"; rong[0] = 9; canh[0] = GiaoDien.GIUA;
        for (int i = 0; i < cotThem.length; i++) {
            td[1 + i] = cotThem[i];
            rong[1 + i] = rongThem[i];
            canh[1 + i] = canhThem[i];
        }
        String[][] dong = new String[0][];
        String[] lyDo = new String[12];
        for (int t = 1; t <= 12; t++) {
            lyDo[t - 1] = ChonKy.lyDoThangKhongHopLe(nam, t, tuNgay);
            String[] r = new String[n];
            for (int i = 0; i < n; i++) r[i] = "";
            r[0] = t + "/" + nam;
            if (lyDo[t - 1] != null) r[1] = lyDo[t - 1];
            else System.arraycopy(cot.apply(t), 0, r, 1, cotThem.length);
            dong = Mang.them(dong, r);
        }
        return ChonKy.chonThang(nam, td, rong, canh, dong, lyDo, tuNgay, Main::docChuoi);
    }

//----------------- HO SO CUA TOI -----------------
    private static void cnHoSo() {
        NhanVien nv = nguoi();
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"PHONG BAN", "CHUC VU", "LUONG CO BAN", "THAY DOI SO VOI NAM TRUOC"},
                new int[]{14, 14, 13, 30},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI},
                n -> {
                    LichSuCongTac ls = dsLS.xacDinh(nv, n).ls;
                    return new String[]{tenPB(ls.getMaPB()), ls.getTenCV(), GiaoDien.tien(ls.getLuongCoBan()), thayDoi(nv, n)};
                });
        if (k == null) return;
        int nam = k.nam;
        GiaoDien.in("");
        GiaoDien.in("HO SO NAM " + nam);
        hienHoSoNam(nv, nam);
        GiaoDien.in("");
        if (laGiamDoc()) {
            if (docChuoi("Ban co muon chinh sua ho so nam " + nam + "? (y/n): ").equalsIgnoreCase("y")) suaHoSoGiamDoc(nv, nam);
        } else if (docChuoi("Ban co muon GUI YEU CAU chinh sua ho so nam " + nam + "? (y/n): ").equalsIgnoreCase("y")) {
            guiYeuCauSuaHoSo(nv, nam);
        }
    }

    private static void hienHoSoNam(NhanVien nv, int nam) {
        if (nam == namNay()) {
            inHoSo(nv);
            return;
        }
        DanhSachLichSuCongTac.KetQua kq = dsLS.xacDinh(nv, nam);
        LichSuCongTac ls = kq.ls;
        String[][] cap = new String[0][];
        cap = Mang.them(cap, new String[]{"Ma nhan vien", nv.getMaNV()});
        cap = Mang.them(cap, new String[]{"Ho ten", nv.getHoTen()});
        cap = Mang.them(cap, new String[]{"Ngay sinh", String.valueOf(nv.getNgaySinh())});
        cap = Mang.them(cap, new String[]{"Gioi tinh", nv.getGioiTinh()});
        cap = Mang.them(cap, new String[]{"So dien thoai", nv.getSdt()});
        cap = Mang.them(cap, new String[]{"Email", nv.getEmail()});
        cap = Mang.them(cap, new String[]{"Ngay vao lam", String.valueOf(nv.getNgayVaoLam())});
        cap = Mang.them(cap, new String[]{"Phong ban nam " + nam, ls.getMaPB().isEmpty() ? "(chua phan)" : tenPB(ls.getMaPB()) + " (" + ls.getMaPB() + ")"});
        cap = Mang.them(cap, new String[]{"Chuc vu nam " + nam, ls.getTenCV().isEmpty() ? "(chua co)" : ls.getTenCV() + " (he so " + TepVanBan.soDep(ls.getHeSoCV()) + ")"});
        cap = Mang.them(cap, new String[]{"Luong co ban nam " + nam, GiaoDien.tien(ls.getLuongCoBan())});
        cap = Mang.them(cap, new String[]{"Nguon so lieu nam " + nam, kq.nguon + (ls.getGhiChu().isEmpty() ? "" : " - " + ls.getGhiChu())});
        GiaoDien.inThongTin(cap);
        GiaoDien.in("(Phong ban, chuc vu, luong lay theo nam " + nam + "; cac muc con lai la thong tin hien tai.)");
    }

    //---- kiem tra / ap dung 1 thay doi ho so (dung chung cho giam doc tu sua va khi duyet yeu cau) ----
    private static String giaTriHienTai(NhanVien nv, int nam, String truong) {
        LichSuCongTac ls = dsLS.xacDinh(nv, nam).ls;
        switch (truong) {
            case "HO_TEN": return nv.getHoTen();
            case "NGAY_SINH": return String.valueOf(nv.getNgaySinh());
            case "GIOI_TINH": return nv.getGioiTinh();
            case "SDT": return nv.getSdt();
            case "EMAIL": return nv.getEmail();
            case "NGAY_VAO_LAM": return String.valueOf(nv.getNgayVaoLam());
            case "PHONG_BAN": return ls.getMaPB();
            case "CHUC_VU": return ls.getMaCV();
            case "LUONG_CO_BAN": return TepVanBan.soDep(ls.getLuongCoBan());
            case "PB_TEN": return nv.getPhongBan() == null ? "" : nv.getPhongBan().getTenPB();
            case "PB_MO_TA": return nv.getPhongBan() == null ? "" : nv.getPhongBan().getMoTa();
            default: return "";
        }
    }

    private static String hienGiaTri(String truong, String v) {
        if (v == null || v.isEmpty()) return "(trong)";
        try {
            switch (truong) {
                case "PHONG_BAN": return v + " - " + tenPB(v);
                case "CHUC_VU": return chucVuTuMa(v).getTenCV();
                case "LUONG_CO_BAN": return GiaoDien.tien(Double.parseDouble(v));
                default: return v;
            }
        } catch (RuntimeException e) {
            return v;
        }
    }

    private static ChucVu chucVuTuMa(String ma) {
        if (ma == null || !ma.toUpperCase().matches("CV0[1-4]")) return null;
        return ChucVu.macDinh(Integer.parseInt(ma.substring(3)));
    }

    private static boolean truongNhayCam(String truong) {
        for (String t : TRUONG_AN_TOAN) if (t.equals(truong)) return false;
        return true;
    }

    //tra ve null neu gia tri moi hop le, nguoc lai la noi dung loi
    private static String kiemTraSuaHoSo(NhanVien nv, int nam, String truong, String moi) {
        if (moi == null || moi.isBlank()) return "Gia tri moi khong duoc de trong.";
        LocalDate homNay = LocalDate.now();
        if (nam < nv.getNgayVaoLam().getYear() || nam > namNay()) return "Nam " + nam + " khong hop le (tu nam vao lam den nam hien tai).";
        switch (truong) {
            case "HO_TEN":
                return null;
            case "NGAY_SINH": {
                LocalDate ns;
                try { ns = LocalDate.parse(moi); } catch (Exception e) { return "Ngay sinh phai theo dang yyyy-MM-dd."; }
                if (ns.isAfter(homNay)) return "Ngay sinh khong duoc sau hom nay (" + homNay + ").";
                if (ns.plusYears(TUOI_TOI_THIEU).isAfter(nv.getNgayVaoLam()))
                    return "Phai du " + TUOI_TOI_THIEU + " tuoi vao ngay vao lam (" + nv.getNgayVaoLam() + ").";
                return null;
            }
            case "GIOI_TINH":
                return (moi.equals("Nam") || moi.equals("Nu") || moi.equals("Khac")) ? null : "Gioi tinh chi nhan Nam / Nu / Khac.";
            case "SDT":
                if (!moi.matches("\\d+")) return "So dien thoai chi gom chu so.";
                if (moi.length() != 8 && moi.length() != 10) return "So dien thoai phai co 8 hoac 10 chu so.";
                return moi.matches("0+") ? "So dien thoai khong duoc toan so 0." : null;
            case "EMAIL":
                return moi.toLowerCase().matches("[a-z0-9._%+-]+@gmail\\.com") ? null : "Email phai co dang ten@gmail.com.";
            case "NGAY_VAO_LAM": {
                LocalDate d;
                try { d = LocalDate.parse(moi); } catch (Exception e) { return "Ngay vao lam phai theo dang yyyy-MM-dd."; }
                if (d.isAfter(homNay)) return "Ngay vao lam khong duoc sau hom nay (" + homNay + ").";
                if (nv.getNgaySinh().plusYears(TUOI_TOI_THIEU).isAfter(d)) return "Ngay vao lam phai khi da du " + TUOI_TOI_THIEU + " tuoi.";
                for (ChamCong cc : dsCC.timTheoMaNV(nv.getMaNV()))
                    if (cc.getNgay().isBefore(d)) return "Da co cham cong tu ngay " + cc.getNgay() + ", khong the doi ngay vao lam sau ngay do.";
                for (LichSuCongTac ls : dsLS.timTheoMaNV(nv.getMaNV()))
                    if (ls.getNam() < d.getYear()) return "Da co lich su cong tac tu nam " + ls.getNam() + ", khong the doi ngay vao lam sau nam do.";
                return null;
            }
            case "PHONG_BAN":
                return dsPB.timTheoMa(moi) == null ? "Khong co phong ban ma " + moi + "." : null;
            case "CHUC_VU": {
                ChucVu cv = chucVuTuMa(moi);
                if (cv == null) return "Chuc vu phai la CV01 (giam doc), CV02 (truong phong), CV03 (pho phong) hoac CV04 (nhan vien).";
                if (nam == namNay() && PhanQuyen.xacDinhVaiTro(nv) == VaiTro.GIAM_DOC && !"CV01".equals(cv.getMaCV())) {
                    if (PhanQuyen.cungMa(nv, nguoi())) return "Giam doc khong tu ha chuc cua chinh minh (hay nho giam doc khac thuc hien).";
                    if (laGiamDocCuoi(nv)) return "Day la giam doc cuoi cung dang hoat dong, khong the ha chuc.";
                }
                return null;
            }
            case "LUONG_CO_BAN": {
                double l;
                try { l = Double.parseDouble(moi); } catch (NumberFormatException e) { return "Luong phai la so."; }
                if (!Double.isFinite(l) || l < BangLuong.getLuongToiThieu())
                    return "Luong co ban phai tu " + GiaoDien.tien(BangLuong.getLuongToiThieu()) + " VND tro len.";
                return null;
            }
            default:
                return "Muc sua khong hop le.";
        }
    }

    //kiem tra roi ap dung. Tra ve null neu thanh cong, nguoc lai la noi dung loi
    private static String apDungSuaHoSo(NhanVien nv, int nam, String truong, String moi) {
        String loi = kiemTraSuaHoSo(nv, nam, truong, moi);
        if (loi != null) return loi;
        switch (truong) {
            case "HO_TEN": nv.setHoTen(moi); return null;
            case "NGAY_SINH": nv.setNgaySinh(LocalDate.parse(moi)); return null;
            case "GIOI_TINH": nv.setGioiTinh(moi); return null;
            case "SDT": nv.setSdt(moi); return null;
            case "EMAIL": nv.setEmail(moi.toLowerCase()); return null;
            case "NGAY_VAO_LAM": nv.setNgayVaoLam(LocalDate.parse(moi)); return null;
            default: break;
        }
        //phong ban / chuc vu / luong: theo nam
        if (nam == namNay()) {
            dsLS.ghiNhanTruocKhiDoi(nv);            //giu nguyen cac nam truoc
            if (truong.equals("PHONG_BAN")) {
                PhongBan cu = nv.getPhongBan();
                PhongBan pbMoi = dsPB.timTheoMa(moi);
                if (cu != pbMoi) {
                    if (cu != null) cu.giamSoNhanVien();
                    nv.setPhongBan(pbMoi);
                    pbMoi.tangSoNhanVien();
                }
            } else if (truong.equals("CHUC_VU")) {
                nv.setChucVu(chucVuTuMa(moi));
            } else {
                nv.setLuongCoBan(Double.parseDouble(moi));
            }
        } else {
            LichSuCongTac ls = dsLS.xacDinh(nv, nam).ls.sao();
            ls.setNam(nam);
            if (truong.equals("PHONG_BAN")) ls.setMaPB(moi.toUpperCase());
            else if (truong.equals("CHUC_VU")) ls.setChucVu(chucVuTuMa(moi));
            else ls.setLuongCoBan(Double.parseDouble(moi));
            ls.setGhiChu("Sua ngay " + LocalDate.now());
            dsLS.ghiSuaNamCu(nv, ls);
        }
        return null;
    }

    //doc gia tri moi cho 1 muc (dung lai cac ham nhap co kiem tra san)
    private static String docGiaTriMoi(String truong) {
        switch (truong) {
            case "HO_TEN": return docChuoiKhongRong("Ho ten moi: ");
            case "NGAY_SINH": return docNgaySinh().toString();
            case "GIOI_TINH": return docGioiTinh();
            case "SDT": return docSoDienThoai();
            case "EMAIL": return docEmail();
            case "NGAY_VAO_LAM":
                GiaoDien.in("Hom nay la " + LocalDate.now() + ". Ngay vao lam khong duoc sau hom nay.");
                return docNgay("Ngay vao lam moi (yyyy-MM-dd): ").toString();
            case "PHONG_BAN":
                GiaoDien.in("Danh sach phong ban hien co:");
                dsPB.xuatDanhSach();
                return docChuoi("Ma phong ban moi: ").toUpperCase();
            case "CHUC_VU": return docChucVu().getMaCV();
            case "LUONG_CO_BAN":
                return TepVanBan.soDep(docDouble("Luong co ban moi (VND, toi thieu " + GiaoDien.tien(BangLuong.getLuongToiThieu()) + "): ", 0));
            default: throw new IllegalArgumentException("Muc sua khong hop le");
        }
    }

    private static boolean laMucTheoNam(String truong) {
        return truong.equals("PHONG_BAN") || truong.equals("CHUC_VU") || truong.equals("LUONG_CO_BAN");
    }

    //---- giam doc tu sua ho so (nam dang chon) ----
    private static void suaHoSoGiamDoc(NhanVien nv, int nam) {
        yeuCauLaGiamDoc();
        while (true) {
            GiaoDien.in("");
            GiaoDien.in("Chon muc can sua (ho so nam " + nam + "):");
            for (int i = 0; i < TRUONG_DAY_DU.length; i++) {
                String t = TRUONG_DAY_DU[i];
                GiaoDien.in((i + 1) + ". " + YeuCau.tenTruong(t) + (laMucTheoNam(t) ? " (nam " + nam + ")" : "")
                        + ": " + hienGiaTri(t, giaTriHienTai(nv, nam, t)));
            }
            GiaoDien.in("10. Trinh do chuyen mon (nhap lai)");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 10);
            if (c == 0) return;
            try {
                if (c == 10) {
                    nv.setTrinhDo(docDanhSachTrinhDo());
                    ghiNhatKy("Cap nhat trinh do chuyen mon (ho so cua toi)", nv.getMaNV());
                    GiaoDien.in("Da cap nhat trinh do.");
                    continue;
                }
                String truong = TRUONG_DAY_DU[c - 1];
                String cu = giaTriHienTai(nv, nam, truong);
                String moi = docGiaTriMoi(truong);
                if (moi.equals(cu)) {
                    GiaoDien.in("Gia tri moi giong gia tri hien tai, khong thay doi.");
                    continue;
                }
                String loi = apDungSuaHoSo(nv, nam, truong, moi);
                if (loi != null) {
                    ghiNhatKy("Sua ho so cua toi nam " + nam + ": " + YeuCau.tenTruong(truong), nv.getMaNV(), NhatKyHeThong.THAT_BAI);
                    GiaoDien.in("Khong sua duoc: " + loi);
                } else {
                    ghiNhatKy("Sua ho so cua toi nam " + nam + ": " + YeuCau.tenTruong(truong) + " " + cu + " -> " + moi, nv.getMaNV());
                    GiaoDien.in("Da cap nhat " + YeuCau.tenTruong(truong).toLowerCase() + (laMucTheoNam(truong) ? " nam " + nam : "") + ".");
                    if (laMucTheoNam(truong) && nam != namNay())
                        GiaoDien.in("(Bang luong, thuong phat da lap truoc do khong tu doi theo.)");
                }
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong sua duoc: " + e.getMessage());
            }
        }
    }

//----------------- YEU CAU SUA HO SO (nhan vien / truong phong / pho phong gui len cap tren) -----------------
    //nhan vien gui cho TRUONG PHONG (tuy chon), truong phong / pho phong gui cho GIAM DOC (tuy chon)
    private static NhanVien[] cacNguoiNhanYeuCau(NhanVien gui) {
        VaiTro canTim = PhanQuyen.xacDinhVaiTro(gui).laQuanLy() ? VaiTro.GIAM_DOC : VaiTro.TRUONG_PHONG;
        NhanVien[] kq = new NhanVien[0];
        for (NhanVien x : ql.getDanhSachNV()) {
            if (x == null || PhanQuyen.cungMa(x, gui) || PhanQuyen.xacDinhVaiTro(x) != canTim) continue;
            if (x.getTrangThai().toLowerCase().startsWith("nghi")) continue;
            TaiKhoan tk = dsTK.timTheoTen(x.getMaNV());
            if (tk == null || !tk.dangHoatDong()) continue;
            kq = Mang.them(kq, x);
        }
        Mang.sapXep(kq, (a, b) -> Boolean.compare(PhanQuyen.cungPhong(b, gui), PhanQuyen.cungPhong(a, gui)));
        return kq;
    }

    private static void guiYeuCauSuaHoSo(NhanVien nv, int nam) {
        if (laGiamDoc()) throw new IllegalStateException("Giam doc tu sua ho so, khong can gui yeu cau.");
        NhanVien[] nhan = cacNguoiNhanYeuCau(nv);
        boolean denGiamDoc = PhanQuyen.xacDinhVaiTro(nv).laQuanLy();
        if ((nhan.length == 0)) {
            GiaoDien.in("Hien khong co " + (denGiamDoc ? "giam doc" : "truong phong") + " nao dang hoat dong de nhan yeu cau.");
            return;
        }
        GiaoDien.in("");
        GiaoDien.in("Chon " + (denGiamDoc ? "GIAM DOC" : "TRUONG PHONG") + " de gui yeu cau:");
        String[][] dong = new String[0][];
        for (NhanVien x : nhan) {
            dong = Mang.them(dong, new String[]{x.getMaNV(), x.getHoTen(), x.getPhongBan() == null ? "(chua phan)" : x.getPhongBan().getTenPB(),
                    PhanQuyen.cungPhong(x, nv) ? "Cung phong ban" : ""});
        }
        GiaoDien.inBang(new String[]{"MA NV", "HO TEN", "PHONG BAN", "GHI CHU"}, new int[]{8, 24, 14, 16},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI}, dong);
        int c = docInt("Chon nguoi nhan (0 = huy): ", 0, nhan.length);
        if (c == 0) return;
        NhanVien nguoiNhan = nhan[c - 1];

        //giam doc duoc xin sua moi muc; truong phong chi duoc xin sua cac muc an toan (khong phai luong/chuc vu/phong ban/ngay vao lam)
        String[] choPhep = denGiamDoc ? TRUONG_DAY_DU : TRUONG_AN_TOAN;
        GiaoDien.in("");
        GiaoDien.in("Chon muc muon sua (ho so nam " + nam + "):");
        for (int i = 0; i < choPhep.length; i++) {
            GiaoDien.in((i + 1) + ". " + YeuCau.tenTruong(choPhep[i]) + (laMucTheoNam(choPhep[i]) ? " (nam " + nam + ")" : "")
                    + ": " + hienGiaTri(choPhep[i], giaTriHienTai(nv, nam, choPhep[i])));
        }
        if (!denGiamDoc) GiaoDien.in("(Phong ban, chuc vu, luong, ngay vao lam chi giam doc moi duyet duoc.)");
        GiaoDien.in("0. Huy");
        int m = docInt("Chon: ", 0, choPhep.length);
        if (m == 0) return;
        String truong = choPhep[m - 1];
        String cu = giaTriHienTai(nv, nam, truong);
        String moi = docGiaTriMoi(truong);
        if (moi.equals(cu)) {
            GiaoDien.in("Gia tri moi giong gia tri hien tai, khong can gui yeu cau.");
            return;
        }
        String loi = kiemTraSuaHoSo(nv, nam, truong, moi);
        if (loi != null) {
            GiaoDien.in("Khong gui duoc: " + loi);
            return;
        }
        if (dsYC.daCoYeuCauCho(nv.getMaNV(), nam, truong)) {
            GiaoDien.in("Ban da co 1 yeu cau sua \"" + YeuCau.tenTruong(truong).toLowerCase() + "\" nam " + nam
                    + " dang cho duyet. Hay doi duoc xu ly hoac huy (Yeu cau chinh sua ho so cua toi).");
            return;
        }
        GiaoDien.in("");
        GiaoDien.in("Gui den " + nguoiNhan.getMaNV() + " - " + nguoiNhan.getHoTen() + ": sua "
                + YeuCau.tenTruong(truong).toLowerCase() + " nam " + nam + " tu \"" + hienGiaTri(truong, cu)
                + "\" thanh \"" + hienGiaTri(truong, moi) + "\".");
        if (!docChuoi("Xac nhan gui yeu cau? (y/n): ").equalsIgnoreCase("y")) {
            GiaoDien.in("Da huy, chua gui yeu cau.");
            return;
        }
        YeuCau yc = YeuCau.moi(dsYC.taoMaMoi(), nv.getMaNV(), nguoiNhan.getMaNV(), nam, truong, cu, moi);
        dsYC.them(yc);
        ghiNhatKy("Gui yeu cau " + yc.getMaYC() + " sua " + YeuCau.tenTruong(truong) + " nam " + nam, nguoiNhan.getMaNV());
        GiaoDien.in("Da gui yeu cau " + yc.getMaYC() + ". Chi khi " + nguoiNhan.getHoTen() + " dong y thi ho so moi duoc sua.");
    }

    private static void inBangYeuCau(YeuCau[] cac, boolean hienNguoiGui) {
        String[][] dong = new String[0][];
        for (YeuCau y : cac) {
            dong = Mang.them(dong, new String[]{y.getMaYC(), hienNguoiGui ? y.getNguoiGui() : y.getNguoiNhan(), String.valueOf(y.getNam()),
                    YeuCau.tenTruong(y.getTruong()), hienGiaTri(y.getTruong(), y.getGiaTriCu()),
                    hienGiaTri(y.getTruong(), y.getGiaTriMoi()), y.getNgayGui().format(YeuCau.DINH_DANG), y.tenTrangThai()});
        }
        GiaoDien.inBang(
                new String[]{"MA YC", hienNguoiGui ? "NGUOI GUI" : "GUI DEN", "NAM", "MUC SUA", "GIA TRI CU", "GIA TRI MOI", "NGAY GUI", "TRANG THAI"},
                new int[]{7, 9, 5, 12, 20, 20, 16, 11},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.GIUA, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
    }

    //cac yeu cau minh da gui + huy yeu cau dang cho
    private static void yeuCauCuaToi() {
        String ma = phien.getMaNV();
        YeuCau[] ds = dsYC.timDaGui(ma);
        if ((ds.length == 0)) {
            GiaoDien.in("Ban chua gui yeu cau nao.");
            return;
        }
        inBangYeuCau(ds, false);
        long cho = Mang.dem(ds, YeuCau::dangCho);
        GiaoDien.in("Tong " + ds.length + " yeu cau, con " + cho + " yeu cau dang cho duyet (yeu cau da duyet / tu choi se khong con o trang thai cho).");
        if (cho == 0) return;
        int c = docInt("Nhap STT yeu cau muon HUY (0 = khong huy): ", 0, ds.length);
        if (c == 0) return;
        YeuCau yc = ds[c - 1];
        if (!yc.dangCho()) {
            GiaoDien.in("Yeu cau nay da duoc xu ly (" + yc.tenTrangThai() + "), khong the huy.");
            return;
        }
        yc.huy();
        ghiNhatKy("Huy yeu cau " + yc.getMaYC(), yc.getNguoiNhan());
        GiaoDien.in("Da huy yeu cau " + yc.getMaYC() + ".");
    }

    //---- nguoi nhan duyet ----
    private static void menuDuyetYeuCau() {
        Muc[] m = new Muc[0];
        if (co(Quyen.DUYET_YEU_CAU)) m = Mang.them(m, muc("Duyet yeu cau chinh sua ho so", "DUYET YEU CAU SUA HO SO", Main::ycDuyet));
        if (co(Quyen.DUYET_NGHI_PHEP)) m = Mang.them(m, muc("Duyet don nghi phep", "DUYET DON NGHI PHEP", Main::npDuyetCoDanhSach));
        chayMenu("DUYET YEU CAU", m, "Quay lai", null);
    }

    private static void thongBaoYeuCau() {
        if (!co(Quyen.DUYET_YEU_CAU)) return;
        int dem = dsYC.demChoDuyet(phien.getMaNV());
        if (dem > 0) GiaoDien.in("CO " + dem + " YEU CAU CHINH SUA HO SO dang cho ban duyet (xem: Duyet yeu cau).");
    }


    private static void xuLyYeuCau(YeuCau yc) {
        NhanVien gui = ql.timTheoMa(yc.getNguoiGui());
        if (gui == null) {
            yc.tuChoi("Nguoi gui khong con trong he thong");
            GiaoDien.in("Nguoi gui " + yc.getNguoiGui() + " khong con trong he thong, yeu cau tu dong bi tu choi.");
            return;
        }
        if (PhanQuyen.capBac(gui) >= PhanQuyen.capBac(nguoi()) || PhanQuyen.cungMa(gui, nguoi())) {
            ghiNhatKy("Duyet yeu cau " + yc.getMaYC(), gui.getMaNV(), NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Ban chi duyet yeu cau cua nguoi co cap bac thap hon.");
        }
        String[][] cap = new String[0][];
        cap = Mang.them(cap, new String[]{"Ma yeu cau", yc.getMaYC()});
        cap = Mang.them(cap, new String[]{"Nguoi gui", gui.getMaNV() + " - " + gui.getHoTen() + " (" + PhanQuyen.xacDinhVaiTro(gui).ten() + ")"});
        cap = Mang.them(cap, new String[]{"Ngay gui", yc.getNgayGui().format(YeuCau.DINH_DANG)});
        cap = Mang.them(cap, new String[]{"Muc can sua", YeuCau.tenTruong(yc.getTruong()) + " (ho so nam " + yc.getNam() + ")"});
        cap = Mang.them(cap, new String[]{"Gia tri luc gui", hienGiaTri(yc.getTruong(), yc.getGiaTriCu())});
        cap = Mang.them(cap, new String[]{"Gia tri hien tai", hienGiaTri(yc.getTruong(), giaTriHienTai(gui, Math.max(yc.getNam(), gui.getNgayVaoLam().getYear()), yc.getTruong()))});
        cap = Mang.them(cap, new String[]{"Gia tri de nghi", hienGiaTri(yc.getTruong(), yc.getGiaTriMoi())});
        GiaoDien.in("");
        GiaoDien.inThongTin(cap);
        GiaoDien.in("1. Dong y (sua ho so ngay)  |  2. Tu choi  |  0. De sau");
        int c = docInt("Chon: ", 0, 2);
        if (c == 0) return;
        if (c == 2) {
            String lyDo = docChuoi("Ly do tu choi (co the bo trong): ");
            yc.tuChoi(lyDo);
            ghiNhatKy("Tu choi yeu cau " + yc.getMaYC(), gui.getMaNV());
            GiaoDien.in("Da tu choi yeu cau " + yc.getMaYC() + ".");
            return;
        }
        if (truongNhayCam(yc.getTruong()) && !laGiamDoc()) {
            ghiNhatKy("Duyet yeu cau " + yc.getMaYC(), gui.getMaNV(), NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Phong ban, chuc vu, luong, ngay vao lam chi giam doc moi duyet duoc.");
        }
        String loi = yc.getTruong().startsWith("PB_")
                ? apDungSuaPhongBan(gui, yc.getTruong(), yc.getGiaTriMoi())
                : apDungSuaHoSo(gui, yc.getNam(), yc.getTruong(), yc.getGiaTriMoi());
        if (loi != null) {
            GiaoDien.in("Khong ap dung duoc yeu cau: " + loi);
            GiaoDien.in("(Yeu cau van dang cho; ban co the tu choi no.)");
            return;
        }
        yc.duyet();
        ghiNhatKy("Duyet yeu cau " + yc.getMaYC() + " (" + YeuCau.tenTruong(yc.getTruong()) + " nam " + yc.getNam() + ")", gui.getMaNV());
        GiaoDien.in("Da dong y va cap nhat ho so cua " + gui.getMaNV() + ".");
    }


//----------------- PHONG BAN CUA TOI -----------------
    private static void cnPhongBan() {
        NhanVien nv = nguoi();
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"PHONG BAN", "CHUC VU", "THAY DOI SO VOI NAM TRUOC"},
                new int[]{16, 14, 30},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                n -> {
                    LichSuCongTac ls = dsLS.xacDinh(nv, n).ls;
                    return new String[]{tenPB(ls.getMaPB()), ls.getTenCV(), thayDoi(nv, n)};
                });
        if (k == null) return;
        int nam = k.nam;
        boolean hienTai = nam == namNay();
        while (true) {
            DanhSachLichSuCongTac.KetQua kq = dsLS.xacDinh(nv, nam);
            PhongBan pb = dsPB.timTheoMa(kq.ls.getMaPB());
            String[][] cap = new String[0][];
            cap = Mang.them(cap, new String[]{"Nam", nam + " (" + ChonKy.trangThaiNam(nam) + ")"});
            cap = Mang.them(cap, new String[]{"Phong ban", pb == null ? "(chua phan phong ban)" : pb.getTenPB() + " (" + pb.getMaPB() + ")"});
            if (pb != null) cap = Mang.them(cap, new String[]{"Mo ta", pb.getMoTa()});
            cap = Mang.them(cap, new String[]{"Chuc vu trong nam", kq.ls.getTenCV().isEmpty() ? "(chua co)" : kq.ls.getTenCV()});
            if (hienTai && pb != null) cap = Mang.them(cap, new String[]{"So nhan vien hien tai", String.valueOf(pb.demNhanVien())});
            cap = Mang.them(cap, new String[]{"Nguon so lieu", kq.nguon});
            GiaoDien.in("");
            GiaoDien.inThongTin(cap);
            VaiTro vt = PhanQuyen.xacDinhVaiTro(nv);
            boolean truongPho = vt == VaiTro.TRUONG_PHONG || vt == VaiTro.PHO_PHONG;
            if (hienTai && truongPho) {
                GiaoDien.in("");
                if (docChuoi("Gui yeu cau len giam doc de sua ten / mo ta phong ban? (y/n): ").equalsIgnoreCase("y")) guiYeuCauSuaPhongBan(nv);
                return;
            }
            if (!hienTai || !laGiamDoc()) {
                if (!hienTai) GiaoDien.in("(Nam da qua chi de xem. Muon sua ho so nam cu: Ho so cua toi.)");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("1. Sua ten / mo ta phong ban hien tai");
            GiaoDien.in("2. Chuyen sang phong ban khac");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 2);
            if (c == 0) return;
            try {
                if (c == 1) {
                    if (pb == null) {
                        GiaoDien.in("Ban chua thuoc phong ban nao, hay chon 2 de chuyen vao phong ban.");
                        continue;
                    }
                    String ten = docChuoi("Ten phong ban moi (Enter = giu nguyen): ");
                    String moTa = docChuoi("Mo ta moi (Enter = giu nguyen): ");
                    if (!ten.isBlank()) {
                        for (PhongBan x : dsPB.getDanhSach())
                            if (x != null && x != pb && x.getTenPB().equalsIgnoreCase(ten.trim())) throw new IllegalArgumentException("Da co phong ban ten " + ten.trim().toUpperCase());
                        pb.setTenPB(ten);
                    }
                    if (!moTa.isBlank()) pb.setMoTa(moTa);
                    ghiNhatKy("Sua phong ban " + pb.getMaPB() + " (phong ban cua toi)", nv.getMaNV());
                    GiaoDien.in("Da cap nhat phong ban.");
                } else {
                    String cu = kq.ls.getMaPB();
                    String moi = docGiaTriMoi("PHONG_BAN");
                    if (moi.equalsIgnoreCase(cu)) {
                        GiaoDien.in("Ban dang o phong ban nay roi.");
                        continue;
                    }
                    String loi = apDungSuaHoSo(nv, nam, "PHONG_BAN", moi);
                    if (loi != null) {
                        GiaoDien.in("Khong chuyen duoc: " + loi);
                    } else {
                        ghiNhatKy("Chuyen phong ban " + cu + " -> " + moi + " (phong ban cua toi)", nv.getMaNV());
                        GiaoDien.in("Da chuyen sang phong ban " + tenPB(moi) + ".");
                    }
                }
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong sua duoc: " + e.getMessage());
            }
        }
    }

//----------------- CHAM CONG CUA TOI -----------------
    private static double[] tongChamCongNam(String id, int nam) {
        double ngay = 0, gio = 0, tangCa = 0;
        for (int t = 1; t <= 12; t++) {
            ngay += dsCC.thongKeSoNgayDiLam(id, t, nam);
            gio += dsCC.thongKeGioLam(id, t, nam);
            tangCa += dsCC.thongKeGioTangCa(id, t, nam);
        }
        return new double[]{ngay, gio, tangCa};
    }

    private static final String[] CANH_SO = {GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI};

    private static void cnChamCong() {
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO NGAY LAM", "GIO LAM", "TANG CA", "CHUC VU"},
                new int[]{11, 10, 9, 14},
                new String[]{GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.TRAI},
                n -> {
                    double[] t = tongChamCongNam(id, n);
                    return new String[]{String.valueOf((int) t[0]), String.format("%.1f", t[1]), String.format("%.1f", t[2]),
                            dsLS.xacDinh(nv, n).ls.getTenCV()};
                });
        if (k == null) return;
        int nam = k.nam;
        Integer thang = k.thang > 0 ? Integer.valueOf(k.thang) : null;
        while (true) {
            if (thang == null) {
                GiaoDien.in("");
                GiaoDien.in("CHAM CONG NAM " + nam + " - chon thang de xem chi tiet");
                thang = chonThangCuaNam(nv.getNgayVaoLam(), nam,
                        new String[]{"TRANG THAI", "SO NGAY LAM", "GIO LAM", "TANG CA"},
                        new int[]{16, 11, 10, 9},
                        new String[]{GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI},
                        t -> {
                            int ngay = dsCC.thongKeSoNgayDiLam(id, t, nam);
                            return new String[]{ngay > 0 ? "Co cham cong" : "Khong co du lieu", String.valueOf(ngay),
                                    String.format("%.1f", dsCC.thongKeGioLam(id, t, nam)), String.format("%.1f", dsCC.thongKeGioTangCa(id, t, nam))};
                        });
                if (thang == null) break;
            }
            ChamCong[] thangNay = new ChamCong[0];
            for (ChamCong cc : dsCC.timTheoMaNV(id))
                if (cc.getNgay().getYear() == nam && cc.getNgay().getMonthValue() == thang) thangNay = Mang.them(thangNay, cc);
            Mang.sapXep(thangNay, (a, b) -> a.getNgay().compareTo(b.getNgay()));
            GiaoDien.in("");
            GiaoDien.in("CHI TIET CHAM CONG THANG " + thang + "/" + nam);
            DanhSachChamCong.inBang(thangNay);
            thang = null;
        }
        if (nam == namNay() && laGiamDoc()) ccGiamDocSua(nv);
        else if (nam != namNay()) GiaoDien.in("(Nam da qua chi de xem.)");
    }

    private static String taoMaChamCong() {
        int so = dsCC.getSoLuong() + 1;
        String ma = String.format("CC%03d", so);
        while (dsCC.timTheoMa(ma) != null) ma = String.format("CC%03d", ++so);
        return ma;
    }

    //giam doc sua cham cong cua chinh minh trong nam hien tai
    private static void ccGiamDocSua(NhanVien nv) {
        yeuCauLaGiamDoc();
        String id = nv.getMaNV();
        LocalDate tuNgay = LocalDate.of(namNay(), 1, 1);
        if (nv.getNgayVaoLam().isAfter(tuNgay)) tuNgay = nv.getNgayVaoLam();
        while (true) {
            GiaoDien.in("");
            GiaoDien.in("Sua cham cong nam " + namNay() + " (chi nhap tu " + tuNgay + " den hom nay " + LocalDate.now() + "):");
            GiaoDien.in("1. Them cham cong");
            GiaoDien.in("2. Sua gio vao / gio ra");
            GiaoDien.in("3. Xoa cham cong");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 3);
            if (c == 0) return;
            try {
                if (c == 1) {
                    LocalDate ngay = docNgay("Ngay (yyyy-MM-dd): ");
                    if (ngay.isBefore(tuNgay) || ngay.isAfter(LocalDate.now())) {
                        GiaoDien.in("Sai: chi them tu " + tuNgay + " den hom nay " + LocalDate.now() + ".");
                        continue;
                    }
                    ChamCong co = timChamCongTheoNgay(id, ngay);
                    if (co != null) {
                        GiaoDien.in("Ngay " + ngay + " da co cham cong (ma " + co.getMaChamCong() + "), hay chon 2 de sua.");
                        continue;
                    }
                    LocalTime vao = docTime("Gio vao (HH:mm): ");
                    LocalTime ra = docTime("Gio ra (HH:mm): ");
                    if (!ra.isAfter(vao)) {
                        GiaoDien.in("Gio ra phai sau gio vao.");
                        continue;
                    }
                    String ma = taoMaChamCong();
                    dsCC.them(new ChamCong(ma, id, ngay, vao, ra));
                    ghiNhatKy("Them cham cong " + ngay + " (cham cong cua toi)", id);
                    GiaoDien.in("Da them cham cong " + ma + ".");
                } else {
                    ChamCong cc = docChamCongCuaToi(id, tuNgay);
                    if (cc == null) continue;
                    if (c == 2) {
                        GiaoDien.in("Hien tai: " + cc.getGioVao() + " - " + cc.getGioRa());
                        LocalTime vao = docTime("Gio vao moi (HH:mm): ");
                        LocalTime ra = docTime("Gio ra moi (HH:mm): ");
                        if (!ra.isAfter(vao)) {
                            GiaoDien.in("Gio ra phai sau gio vao.");
                            continue;
                        }
                        dsCC.xoaTheoMa(cc.getMaChamCong());
                        dsCC.them(new ChamCong(cc.getMaChamCong(), id, cc.getNgay(), vao, ra));
                        ghiNhatKy("Sua cham cong " + cc.getNgay() + " (cham cong cua toi)", id);
                        GiaoDien.in("Da sua cham cong ngay " + cc.getNgay() + ".");
                    } else {
                        if (!docChuoi("Xoa cham cong ngay " + cc.getNgay() + " (" + cc.getGioVao() + " - " + cc.getGioRa() + ")? (y/n): ").equalsIgnoreCase("y")) continue;
                        dsCC.xoaTheoMa(cc.getMaChamCong());
                        ghiNhatKy("Xoa cham cong " + cc.getNgay() + " (cham cong cua toi)", id);
                        GiaoDien.in("Da xoa.");
                    }
                }
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong thuc hien duoc: " + e.getMessage());
            }
        }
    }

    private static ChamCong timChamCongTheoNgay(String id, LocalDate ngay) {
        for (ChamCong cc : dsCC.timTheoMaNV(id)) if (cc.getNgay().equals(ngay)) return cc;
        return null;
    }

    //nhap ma hoac ngay, tra ve cham cong cua chinh minh trong nam hien tai (null + thong bao neu khong hop le)
    private static ChamCong docChamCongCuaToi(String id, LocalDate tuNgay) {
        String nhap = docChuoi("Ma cham cong hoac ngay (yyyy-MM-dd): ");
        ChamCong cc;
        if (nhap.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try { cc = timChamCongTheoNgay(id, LocalDate.parse(nhap)); }
            catch (Exception e) { GiaoDien.in("Ngay khong hop le."); return null; }
        } else {
            cc = dsCC.timTheoMa(nhap);
            if (cc != null && !cc.getMaNV().equalsIgnoreCase(id)) cc = null;
        }
        if (cc == null) {
            GiaoDien.in("Khong tim thay cham cong cua ban voi \"" + nhap + "\".");
            return null;
        }
        if (cc.getNgay().isBefore(tuNgay) || cc.getNgay().getYear() != namNay()) {
            GiaoDien.in("Chi duoc sua cham cong trong nam " + namNay() + ".");
            return null;
        }
        return cc;
    }

//----------------- THONG KE GIO LAM THEO THANG -----------------
    private static void ccThongKeCho(NhanVien nv) {
        yeuCau(Quyen.XEM_CHAM_CONG, nv);
        String id = nv.getMaNV();
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO NGAY LAM", "GIO LAM", "TANG CA", "CHUC VU", "LUONG CO BAN"},
                new int[]{11, 10, 9, 14, 13},
                new String[]{GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.TRAI, GiaoDien.PHAI},
                n -> {
                    double[] t = tongChamCongNam(id, n);
                    LichSuCongTac ls = dsLS.xacDinh(nv, n).ls;
                    return new String[]{String.valueOf((int) t[0]), String.format("%.1f", t[1]), String.format("%.1f", t[2]),
                            ls.getTenCV(), GiaoDien.tien(ls.getLuongCoBan())};
                });
        if (k == null) return;
        int nam = k.nam;
        Integer thang = k.thang > 0 ? Integer.valueOf(k.thang) : null;
        if (thang == null) {
            GiaoDien.in("");
            GiaoDien.in("THONG KE GIO LAM NAM " + nam + " - chon thang");
            thang = chonThangCuaNam(nv.getNgayVaoLam(), nam,
                    new String[]{"TRANG THAI", "SO NGAY LAM", "GIO LAM", "TANG CA", "CHUC VU", "LUONG CO BAN"},
                    new int[]{16, 11, 10, 9, 14, 13},
                    new String[]{GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.TRAI, GiaoDien.PHAI},
                    t -> {
                        int ngay = dsCC.thongKeSoNgayDiLam(id, t, nam);
                        LichSuCongTac ls = dsLS.xacDinh(nv, nam).ls;
                        return new String[]{ngay > 0 ? "Co cham cong" : "Khong co du lieu", String.valueOf(ngay),
                                String.format("%.1f", dsCC.thongKeGioLam(id, t, nam)), String.format("%.1f", dsCC.thongKeGioTangCa(id, t, nam)),
                                ls.getTenCV(), GiaoDien.tien(ls.getLuongCoBan())};
                    });
            if (thang == null) return;
        }
        LichSuCongTac ls = dsLS.xacDinh(nv, nam).ls;
        String[][] dong = new String[0][];
        dong = Mang.them(dong, new String[]{"Nhan vien", id + " - " + nv.getHoTen()});
        dong = Mang.them(dong, new String[]{"Thang / nam", thang + "/" + nam});
        dong = Mang.them(dong, new String[]{"Chuc vu nam " + nam, ls.getTenCV().isEmpty() ? "(chua co)" : ls.getTenCV()});
        dong = Mang.them(dong, new String[]{"Phong ban nam " + nam, tenPB(ls.getMaPB())});
        dong = Mang.them(dong, new String[]{"Luong co ban nam " + nam, GiaoDien.tien(ls.getLuongCoBan()) + " VND"});
        dong = Mang.them(dong, new String[]{"So ngay di lam", String.valueOf(dsCC.thongKeSoNgayDiLam(id, thang, nam))});
        dong = Mang.them(dong, new String[]{"Gio lam thuong", String.format("%.2f", dsCC.thongKeGioLam(id, thang, nam))});
        dong = Mang.them(dong, new String[]{"Gio tang ca", String.format("%.2f", dsCC.thongKeGioTangCa(id, thang, nam))});
        GiaoDien.in("");
        GiaoDien.inThongKe(dong);
    }

//----------------- NGHI PHEP CUA TOI -----------------
    //menu nghi phep trong cong thong tin ca nhan: chi don cua chinh minh
    private static void menuNghiPhepCuaToi() {
        Muc[] m = new Muc[0];
        m = Mang.them(m, muc("Tao don nghi phep cua toi", "NGHI PHEP - TAO DON CUA TOI", () -> npTaoCho(nguoi())));
        m = Mang.them(m, muc("Xem don nghi phep cua toi (chon nam)", "NGHI PHEP - DON CUA TOI", Main::cnNghiPhep));
        //giam doc khong co don cho duyet nen khong co chuc nang nay
        if (!laGiamDoc()) m = Mang.them(m, muc("Xoa don dang cho duyet cua toi", "NGHI PHEP - XOA DON CHO DUYET", Main::npXoaChoDuyetCuaToi));
        chayMenu("NGHI PHEP CUA TOI", m, "Quay lai", null);
    }

    private static int namDon(NghiPhep np) {
        return np.getNgayBatDau().getYear();
    }

    private static void cnNghiPhep() {
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        int namCuoi = namNay();
        for (NghiPhep np : dsNP.timTheoMaNV(id)) namCuoi = Math.max(namCuoi, namDon(np));
        ChonKy.KetQua k = chonNam(nv, namCuoi,
                new String[]{"TONG DON", "DA DUYET", "CHO DUYET", "SO NGAY NGHI"},
                new int[]{9, 9, 10, 12},
                new String[]{GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI},
                n -> {
                    int tong = 0, da = 0, cho = 0, ngay = 0;
                    for (NghiPhep np : dsNP.timTheoMaNV(id)) {
                        if (namDon(np) != n) continue;
                        tong++;
                        ngay += np.tinhSoNgayNghi();
                        if ("Da duyet".equals(np.getTrangThai())) da++; else cho++;
                    }
                    return new String[]{String.valueOf(tong), String.valueOf(da), String.valueOf(cho), String.valueOf(ngay)};
                });
        if (k == null) return;
        int nam = k.nam;
        while (true) {
            NghiPhep[] ds = new NghiPhep[0];
            for (NghiPhep np : dsNP.timTheoMaNV(id)) if (namDon(np) == nam) ds = Mang.them(ds, np);
            Mang.sapXep(ds, (a, b) -> a.getNgayBatDau().compareTo(b.getNgayBatDau()));
            GiaoDien.in("");
            GiaoDien.in("DON NGHI PHEP NAM " + nam);
            if ((ds.length == 0)) GiaoDien.in("Khong co don nao trong nam " + nam + ".");
            else DanhSachNghiPhep.inBang(ds);
            long cho = Mang.dem(ds, d -> !"Da duyet".equals(d.getTrangThai()));
            if (cho > 0) GiaoDien.in("Con " + cho + " don chua duoc duyet.");
            if (nam != namNay() || !laGiamDoc()) {
                if (nam == namNay() && !laGiamDoc() && cho > 0) GiaoDien.in("(Muon xoa don dang cho duyet: Nghi phep cua toi > Xoa don dang cho duyet cua toi.)");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("1. Sua don nghi phep");
            GiaoDien.in("2. Xoa don nghi phep");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 2);
            if (c == 0 || (ds.length == 0)) return;
            try {
                NghiPhep np = ds[docInt("Chon STT don: ", 1, ds.length) - 1];
                if (c == 2) {
                    if (!docChuoi("Xoa don " + np.getMaNP() + "? (y/n): ").equalsIgnoreCase("y")) continue;
                    dsNP.xoaTheoMa(np.getMaNP());
                    ghiNhatKy("Xoa don nghi phep " + np.getMaNP() + " (nghi phep cua toi)", id);
                    GiaoDien.in("Da xoa don.");
                } else {
                    LocalDate tu = docNgay("Tu ngay (yyyy-MM-dd): ");
                    LocalDate den = docNgay("Den ngay (yyyy-MM-dd): ");
                    if (tu.isBefore(nv.getNgayVaoLam())) { GiaoDien.in("Tu ngay khong duoc truoc ngay vao lam (" + nv.getNgayVaoLam() + ")."); continue; }
                    if (den.isBefore(tu)) { GiaoDien.in("Den ngay phai tu ngay bat dau tro di."); continue; }
                    if (tu.getYear() != nam) { GiaoDien.in("Don nay thuoc nam " + nam + ", tu ngay phai nam trong nam " + nam + "."); continue; }
                    String lyDo = docChuoi("Ly do: ");
                    String loai = docChuoi("Loai nghi (CO_PHEP/KHONG_PHEP): ");
                    NghiPhep moi = new NghiPhep(np.getMaNP(), id, tu, den, lyDo);
                    moi.setLoaiNghi(loai.equalsIgnoreCase("KHONG_PHEP") ? "Khong phep" : "Co phep");
                    moi.duyetNghiPhep();
                    dsNP.xoaTheoMa(np.getMaNP());
                    if (!dsNP.them(moi)) dsNP.them(np);
                    ghiNhatKy("Sua don nghi phep " + np.getMaNP() + " (nghi phep cua toi)", id);
                    GiaoDien.in("Da sua don " + np.getMaNP() + ".");
                }
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong thuc hien duoc: " + e.getMessage());
            }
        }
    }

    //xoa don CHO DUYET cua chinh minh: liet ke theo nam kem so don chua duyet; don da duyet tu roi khoi danh sach nay
    private static void npXoaChoDuyetCuaToi() {
        yeuCau(Quyen.XOA_NGHI_PHEP);
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        int[] cacNam = new int[0];
        int tong = 0;
        for (NghiPhep np : dsNP.timTheoMaNV(id)) {
            if ("Da duyet".equals(np.getTrangThai())) continue;
            tong++;
            if (!Mang.chua(cacNam, namDon(np))) cacNam = Mang.them(cacNam, namDon(np));
        }
        if (tong == 0) {
            GiaoDien.in("Ban khong co don nao dang cho duyet. (Don da duoc duyet se tu roi khoi danh sach cho nay.)");
            return;
        }
        Mang.sapXep(cacNam);
        GiaoDien.in("Ban con " + tong + " don chua duoc duyet:");
        ChonKy.KetQua k = chonTrongCacNam(cacNam, nv.getNgayVaoLam(),
                new String[]{"DON CHUA DUYET"}, new int[]{14}, new String[]{GiaoDien.PHAI},
                n -> {
                    int dem = 0;
                    for (NghiPhep np : dsNP.timTheoMaNV(id)) if (namDon(np) == n && !"Da duyet".equals(np.getTrangThai())) dem++;
                    return new String[]{String.valueOf(dem)};
                });
        if (k == null) return;
        while (true) {
            NghiPhep[] cho = new NghiPhep[0];
            for (NghiPhep np : dsNP.timTheoMaNV(id)) if (namDon(np) == k.nam && !"Da duyet".equals(np.getTrangThai())) cho = Mang.them(cho, np);
            if ((cho.length == 0)) {
                GiaoDien.in("Nam " + k.nam + " khong con don nao cho duyet.");
                return;
            }
            DanhSachNghiPhep.inBang(cho);
            int c = docInt("Chon STT don can xoa (0 = quay lai): ", 0, cho.length);
            if (c == 0) return;
            NghiPhep np = cho[c - 1];
            yeuCauTheoMaNV(Quyen.XOA_NGHI_PHEP, np.getMaNV());
            if (!docChuoi("Xoa don " + np.getMaNP() + " (" + np.getNgayBatDau() + " - " + np.getNgayKetThuc() + ")? (y/n): ").equalsIgnoreCase("y")) continue;
            dsNP.xoaTheoMa(np.getMaNP());
            ghiNhatKy("Xoa don nghi phep " + np.getMaNP(), id);
            GiaoDien.in("Da xoa don " + np.getMaNP() + ".");
        }
    }

//----------------- BANG LUONG CUA TOI -----------------
    private static void cnBangLuong() {
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        yeuCau(Quyen.XEM_LUONG, nv);
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO BANG LUONG", "TONG THUC NHAN", "CHUC VU"},
                new int[]{13, 16, 14},
                new String[]{GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.TRAI},
                n -> {
                    int dem = 0;
                    double tong = 0;
                    for (int t = 1; t <= 12; t++) {
                        BangLuong bl = dsBL.timTheoMaNV(id, t, n);
                        if (bl != null) { dem++; tong += bl.getLuongThucNhan(); }
                    }
                    return new String[]{String.valueOf(dem), GiaoDien.tien(tong), dsLS.xacDinh(nv, n).ls.getTenCV()};
                });
        if (k == null) return;
        int nam = k.nam;
        Integer thang = k.thang > 0 ? Integer.valueOf(k.thang) : null;
        while (true) {
            if (thang == null) {
                GiaoDien.in("");
                GiaoDien.in("BANG LUONG NAM " + nam + " - chon thang");
                thang = chonThangCuaNam(nv.getNgayVaoLam(), nam,
                        new String[]{"TRANG THAI", "LUONG THUC NHAN", "CHUC VU"},
                        new int[]{12, 16, 14},
                        new String[]{GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI},
                        t -> {
                            BangLuong bl = dsBL.timTheoMaNV(id, t, nam);
                            return new String[]{bl == null ? "Chua lap" : "Da lap", bl == null ? "" : GiaoDien.tien(bl.getLuongThucNhan()),
                                    dsLS.xacDinh(nv, nam).ls.getTenCV()};
                        });
                if (thang == null) return;
            }
            BangLuong bl = dsBL.timTheoMaNV(id, thang, nam);
            GiaoDien.in("");
            if (bl == null) GiaoDien.in("Chua co bang luong thang " + thang + "/" + nam + ".");
            else bl.xuatBangLuong();
            thang = null;
        }
    }

//----------------- BAO HIEM CUA TOI -----------------
    private static boolean baoHiemTrongNam(BaoHiem bh, int nam) {
        int namTG = bh.getNgayThamGia().getYear();
        if (namTG > nam) return false;
        if (bh instanceof BaoHiemYTe) return ((BaoHiemYTe) bh).getHanTheDen().getYear() >= nam;      //the BHYT: theo han the
        return "HieuLuc".equalsIgnoreCase(bh.getTrangThai()) || namTG == nam;                       //BHXH da het hieu luc: chi nam tham gia
    }

    private static BaoHiem[] baoHiemCuaNam(String id, int nam) {
        BaoHiem[] kq = new BaoHiem[0];
        for (BaoHiem bh : dsBH.timTheoMaNV(id)) if (baoHiemTrongNam(bh, nam)) kq = Mang.them(kq, bh);
        Mang.sapXep(kq, (a, b) -> a.getNgayThamGia().compareTo(b.getNgayThamGia()));
        return kq;
    }

    private static void cnBaoHiem() {
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        yeuCau(Quyen.XEM_BAO_HIEM, nv);
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO HO SO", "BHXH", "BHYT"},
                new int[]{9, 12, 12},
                new String[]{GiaoDien.PHAI, GiaoDien.TRAI, GiaoDien.TRAI},
                n -> {
                    BaoHiem[] ds = baoHiemCuaNam(id, n);
                    boolean xh = false, yt = false;
                    for (BaoHiem b : ds) { if (b instanceof BaoHiemYTe) yt = true; else xh = true; }
                    return new String[]{String.valueOf(ds.length), xh ? "Co" : "Khong", yt ? "Co" : "Khong"};
                });
        if (k == null) return;
        int nam = k.nam;
        while (true) {
            BaoHiem[] ds = baoHiemCuaNam(id, nam);
            GiaoDien.in("");
            GiaoDien.in("BAO HIEM NAM " + nam);
            if ((ds.length == 0)) GiaoDien.in("Khong co ho so bao hiem nao hieu luc trong nam " + nam + ".");
            else DanhSachBaoHiem.inBang(ds);
            if (nam != namNay() || !laGiamDoc()) {
                if (nam != namNay()) GiaoDien.in("(Nam da qua chi de xem.)");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("1. Them BHXH");
            GiaoDien.in("2. Them BHYT");
            GiaoDien.in("3. Sua ho so bao hiem");
            GiaoDien.in("4. Xoa ho so bao hiem");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 4);
            if (c == 0) return;
            try {
                bhGiamDocSua(nv, ds, c);
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong thuc hien duoc: " + e.getMessage());
            }
        }
    }

    private static String taoMaBaoHiem() {
        int so = dsBH.getSoLuong() + 1;
        while (true) {
            String ma = String.format("BH%03d", so++);
            boolean trung = false;
            for (int i = 0; i < dsBH.getSoLuong(); i++)
                if (dsBH.getBaoHiem(i) != null && dsBH.getBaoHiem(i).getMaBH().equalsIgnoreCase(ma)) trung = true;
            if (!trung) return ma;
        }
    }

    private static void bhGiamDocSua(NhanVien nv, BaoHiem[] ds, int c) {
        yeuCauLaGiamDoc();
        String id = nv.getMaNV();
        LocalDate homNay = LocalDate.now();
        if (c == 1) {
            for (BaoHiem b : dsBH.timTheoMaNV(id))
                if (!(b instanceof BaoHiemYTe) && "HieuLuc".equalsIgnoreCase(b.getTrangThai())) {
                    GiaoDien.in("Ban da co BHXH dang hieu luc (" + b.getMaBH() + "), khong them BHXH thu hai.");
                    return;
                }
            String soSo = docChuoiKhongRong("So so BHXH: ");
            String ma = taoMaBaoHiem();
            dsBH.them(new BaoHiemXaHoi(ma, id, soSo, homNay));
            ghiNhatKy("Them BHXH " + ma + " (bao hiem cua toi)", id);
            GiaoDien.in("Da them BHXH " + ma + ".");
        } else if (c == 2) {
            for (BaoHiem b : dsBH.timTheoMaNV(id))
                if (b instanceof BaoHiemYTe && "HieuLuc".equalsIgnoreCase(b.getTrangThai()) && ((BaoHiemYTe) b).conHanThe()) {
                    GiaoDien.in("The BHYT " + b.getMaBH() + " con han den " + ((BaoHiemYTe) b).getHanTheDen() + ". Hay chon 3 de gia han / sua the nay.");
                    return;
                }
            String the = docChuoiKhongRong("So the BHYT: ");
            String noi = docChuoiKhongRong("Noi kham chua benh: ");
            GiaoDien.in("Han the phai sau hom nay (" + homNay + ").");
            LocalDate han = docNgay("Han the (yyyy-MM-dd): ");
            if (!han.isAfter(homNay)) { GiaoDien.in("Han the phai sau hom nay (" + homNay + ")."); return; }
            String ma = taoMaBaoHiem();
            dsBH.them(new BaoHiemYTe(ma, id, the, homNay, noi, han));
            ghiNhatKy("Them BHYT " + ma + " (bao hiem cua toi)", id);
            GiaoDien.in("Da them BHYT " + ma + ".");
        } else {
            if ((ds.length == 0)) { GiaoDien.in("Khong co ho so nao de chon."); return; }
            BaoHiem bh = ds[docInt("Chon STT ho so: ", 1, ds.length) - 1];
            if (c == 4) {
                if (!docChuoi("Xoa ho so " + bh.getMaBH() + "? (y/n): ").equalsIgnoreCase("y")) return;
                dsBH.xoaTheoMa(bh.getMaBH());
                ghiNhatKy("Xoa bao hiem " + bh.getMaBH() + " (bao hiem cua toi)", id);
                GiaoDien.in("Da xoa.");
                return;
            }
            bh.setSoSo(docChuoiKhongRong("So so/the moi: "));
            if (bh instanceof BaoHiemYTe) {
                BaoHiemYTe yt = (BaoHiemYTe) bh;
                yt.setNoiKhamChuaBenh(docChuoiKhongRong("Noi kham chua benh: "));
                LocalDate han = docNgay("Han the moi (yyyy-MM-dd, khong truoc ngay tham gia " + yt.getNgayThamGia() + "): ");
                if (han.isBefore(yt.getNgayThamGia())) throw new IllegalArgumentException("Han the khong duoc truoc ngay tham gia.");
                yt.setHanTheDen(han);
            }
            String tt = docChuoi("Trang thai (1 = HieuLuc, 2 = HetHieuLuc): ");
            bh.setTrangThai(tt.equals("2") ? "HetHieuLuc" : "HieuLuc");
            ghiNhatKy("Sua bao hiem " + bh.getMaBH() + " (bao hiem cua toi)", id);
            GiaoDien.in("Da cap nhat ho so " + bh.getMaBH() + ".");
        }
    }

//----------------- THUONG / PHAT CUA TOI -----------------
    private static ThuongPhat[] thuongPhatCuaNam(String id, int nam) {
        ThuongPhat[] kq = new ThuongPhat[0];
        for (ThuongPhat tp : dsTP.timTheoMaNV(id)) if (tp.getNgayApDung().getYear() == nam) kq = Mang.them(kq, tp);
        Mang.sapXep(kq, (a, b) -> a.getNgayApDung().compareTo(b.getNgayApDung()));
        return kq;
    }

    private static void cnThuongPhat() {
        NhanVien nv = nguoi();
        String id = nv.getMaNV();
        yeuCau(Quyen.XEM_THUONG_PHAT, nv);
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO KHOAN", "TONG THUONG", "TONG PHAT"},
                new int[]{9, 13, 13},
                new String[]{GiaoDien.PHAI, GiaoDien.PHAI, GiaoDien.PHAI},
                n -> {
                    double thuong = 0, phat = 0;
                    ThuongPhat[] ds = thuongPhatCuaNam(id, n);
                    for (ThuongPhat tp : ds) { if ("PHAT".equalsIgnoreCase(tp.getLoai())) phat += tp.getSoTien(); else thuong += tp.getSoTien(); }
                    return new String[]{String.valueOf(ds.length), GiaoDien.tien(thuong), GiaoDien.tien(phat)};
                });
        if (k == null) return;
        int nam = k.nam;
        while (true) {
            ThuongPhat[] ds = thuongPhatCuaNam(id, nam);
            GiaoDien.in("");
            GiaoDien.in("THUONG / PHAT NAM " + nam);
            if ((ds.length == 0)) GiaoDien.in("Khong co khoan thuong/phat nao trong nam " + nam + ".");
            else DanhSachThuongPhat.inBang(ds);
            if (nam != namNay() || !laGiamDoc()) {
                if (nam != namNay()) GiaoDien.in("(Nam da qua chi de xem.)");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("1. Them khoan thuong / phat");
            GiaoDien.in("2. Xoa khoan thuong / phat");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 2);
            if (c == 0) return;
            try {
                tpGiamDocSua(nv, ds, c);
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong thuc hien duoc: " + e.getMessage());
            }
        }
    }

    private static void tpGiamDocSua(NhanVien nv, ThuongPhat[] ds, int c) {
        yeuCauLaGiamDoc();
        String id = nv.getMaNV();
        if (c == 2) {
            if ((ds.length == 0)) { GiaoDien.in("Khong co khoan nao de xoa."); return; }
            ThuongPhat tp = ds[docInt("Chon STT khoan can xoa: ", 1, ds.length) - 1];
            if (!docChuoi("Xoa khoan " + tp.getMaTP() + "? (y/n): ").equalsIgnoreCase("y")) return;
            dsTP.xoaTheoMa(tp.getMaTP());
            ghiNhatKy("Xoa thuong/phat " + tp.getMaTP() + " (thuong phat cua toi)", id);
            GiaoDien.in("Da xoa.");
            return;
        }
        String loai = docChuoi("Loai (THUONG/PHAT): ").toUpperCase();
        if (!loai.equals("THUONG") && !loai.equals("PHAT")) {
            GiaoDien.in("Loai chi nhan THUONG hoac PHAT.");
            return;
        }
        String lyDo = docChuoiKhongRong("Ly do: ");
        double tien = docDouble("So tien (> 0): ", 0);
        if (tien <= 0) { GiaoDien.in("So tien phai lon hon 0."); return; }
        LocalDate tuNgay = LocalDate.of(namNay(), 1, 1);
        if (nv.getNgayVaoLam().isAfter(tuNgay)) tuNgay = nv.getNgayVaoLam();
        GiaoDien.in("Ngay ap dung chi nhap tu " + tuNgay + " den hom nay " + LocalDate.now() + ".");
        LocalDate ngay = docNgay("Ngay ap dung (yyyy-MM-dd): ");
        if (ngay.isBefore(tuNgay) || ngay.isAfter(LocalDate.now())) {
            GiaoDien.in("Sai: ngay ap dung phai tu " + tuNgay + " den hom nay " + LocalDate.now() + ".");
            return;
        }
        int so = 1;
        String ma = "TP_" + id + "_TC_" + ngay.toString().replace("-", "") + "_" + so;
        while (dsTP.timTheoMa(ma) != null) ma = "TP_" + id + "_TC_" + ngay.toString().replace("-", "") + "_" + (++so);
        dsTP.them(new ThuongPhat(ma, id, "", loai, lyDo, 1, tien, ngay));
        ghiNhatKy("Them " + loai + " " + GiaoDien.tien(tien) + " VND (thuong phat cua toi)", id);
        GiaoDien.in("Da them khoan " + loai.toLowerCase() + " " + ma + ". (Bang luong da lap truoc do khong tu doi.)");
    }

//----------------- HOP DONG LAO DONG CUA TOI -----------------
    //hop dong cua nhan vien: uu tien danh sach hop dong (data/hopdong.txt); khong co thi dung hop dong gan voi nhan vien hop dong
    private static LichSuHopDong[] hopDongCuaToi(NhanVien nv) {
        LichSuHopDong[] ds = dsHDLD.timTheoMaNV(nv.getMaNV());
        if (!(ds.length == 0) || !(nv instanceof NhanVienHopDong)) return ds;
        HopDongLaoDong hd = ((NhanVienHopDong) nv).gethopDong();
        if (hd == null) return ds;
        try {
            ds = Mang.them(ds, new LichSuHopDong(hd.getmaHD(), nv.getMaNV(), hd.getloaiHopDong(), hd.getngayKy(), hd.getngayHetHan(), ""));
        } catch (RuntimeException ignored) { }
        return ds;
    }

    private static void cnHopDong() {
        NhanVien nv = nguoi();
        yeuCau(Quyen.XEM_HOP_DONG, nv);
        String id = nv.getMaNV();
        LichSuHopDong[] tat = hopDongCuaToi(nv);
        if ((tat.length == 0)) {
            GiaoDien.in("Ban chua co hop dong lao dong nao.");
            if (!laGiamDoc()) return;
            if (docChuoi("Them hop dong lao dong cho ban? (y/n): ").equalsIgnoreCase("y")) hdGiamDocSua(nv, 3, new LichSuHopDong[0]);
            return;
        }
        ChonKy.KetQua k = chonNam(nv, namNay(),
                new String[]{"SO HOP DONG", "LOAI HOP DONG"},
                new int[]{11, 40},
                new String[]{GiaoDien.PHAI, GiaoDien.TRAI},
                n -> {
                    String[] loai = new String[0];
                    for (LichSuHopDong h : hopDongCuaToi(nv)) if (h.hieuLucTrongNam(n)) loai = Mang.them(loai, h.getLoai());
                    return new String[]{String.valueOf(loai.length), (loai.length == 0) ? "(khong co hop dong)" : String.join(", ", loai)};
                });
        if (k == null) return;
        int nam = k.nam;
        while (true) {
            LichSuHopDong[] ds = new LichSuHopDong[0];
            for (LichSuHopDong h : hopDongCuaToi(nv)) if (h.hieuLucTrongNam(nam)) ds = Mang.them(ds, h);
            GiaoDien.in("");
            GiaoDien.in("HOP DONG LAO DONG NAM " + nam);
            if ((ds.length == 0)) GiaoDien.in("Khong co hop dong nao hieu luc trong nam " + nam + ".");
            else DanhSachLichSuHopDong.inBang(ds);
            if (nam != namNay() || !laGiamDoc()) {
                if (nam != namNay()) GiaoDien.in("(Nam da qua chi de xem.)");
                return;
            }
            GiaoDien.in("");
            GiaoDien.in("1. Sua hop dong");
            GiaoDien.in("2. Gia han hop dong");
            GiaoDien.in("3. Them hop dong moi");
            GiaoDien.in("4. Xoa hop dong");
            GiaoDien.in("0. Xong");
            int c = docInt("Chon: ", 0, 4);
            if (c == 0) return;
            try {
                hdGiamDocSua(nv, c, ds);
            } catch (java.util.NoSuchElementException e) {
                throw e;                                        //het du lieu nhap (Ctrl+D): thoat, khong lap vo han
            } catch (RuntimeException e) {
                GiaoDien.in("Khong thuc hien duoc: " + e.getMessage());
            }
        }
    }

    //ngay het han: de trong = khong thoi han
    private static LocalDate docNgayHetHan(String prompt) {
        while (true) {
            String s = docChuoi(prompt);
            if (s.isBlank()) return null;
            try { return LocalDate.parse(s); }
            catch (java.util.NoSuchElementException e) { throw e; }
            catch (Exception e) { GiaoDien.in("Nhap ngay theo yyyy-MM-dd, hoac de trong neu khong thoi han."); }
        }
    }

    private static void hdGiamDocSua(NhanVien nv, int c, LichSuHopDong[] ds) {
        yeuCauLaGiamDoc();
        String id = nv.getMaNV();
        if (c == 3) {
            String ma = dsHDLD.taoMaMoi(id);
            String loai = docChuoiKhongRong("Loai hop dong (Thu viec / Co thoi han / Khong thoi han ...): ");
            LocalDate ky = docNgay("Ngay ky (yyyy-MM-dd): ");
            if (ky.isAfter(LocalDate.now())) throw new IllegalArgumentException("Ngay ky khong duoc sau hom nay (" + LocalDate.now() + ").");
            if (ky.isBefore(nv.getNgayVaoLam())) throw new IllegalArgumentException("Ngay ky khong duoc truoc ngay vao lam (" + nv.getNgayVaoLam() + ").");
            LocalDate het = docNgayHetHan("Ngay het han (yyyy-MM-dd, de trong = khong thoi han): ");
            String ghiChu = docChuoi("Ghi chu (co the bo trong): ");
            LichSuHopDong moi = new LichSuHopDong(ma, id, loai, ky, het, ghiChu);
            if (dsHDLD.trungVoiHopDongKhac(id, ky, het, null)) throw new IllegalArgumentException("Hop dong moi bi trung thoi gian voi hop dong khac.");
            dsHDLD.them(moi);
            ghiNhatKy("Them hop dong " + ma + " (hop dong cua toi)", id);
            GiaoDien.in("Da them hop dong " + ma + ".");
            return;
        }
        LichSuHopDong[] thatSu = new LichSuHopDong[0];       //chi sua duoc hop dong trong danh sach hop dong (khong sua hop dong gan voi nhan vien hop dong)
        for (LichSuHopDong h : ds) if (dsHDLD.timTheoMa(h.getMaHD()) != null) thatSu = Mang.them(thatSu, h);
        if ((thatSu.length == 0)) {
            GiaoDien.in("Khong co hop dong nao co the chon.");
            return;
        }
        LichSuHopDong hd = ds[docInt("Chon STT hop dong: ", 1, ds.length) - 1];
        if (dsHDLD.timTheoMa(hd.getMaHD()) == null) throw new IllegalArgumentException("Hop dong nay gan voi ho so nhan vien, khong sua o day.");
        if (c == 4) {
            if (!docChuoi("Xoa hop dong " + hd.getMaHD() + "? (y/n): ").equalsIgnoreCase("y")) return;
            dsHDLD.xoaTheoMa(hd.getMaHD());
            ghiNhatKy("Xoa hop dong " + hd.getMaHD() + " (hop dong cua toi)", id);
            GiaoDien.in("Da xoa.");
        } else if (c == 2) {
            GiaoDien.in("Ngay het han hien tai: " + (hd.khongThoiHan() ? "(khong thoi han)" : hd.getNgayHetHan()));
            LocalDate het = docNgayHetHan("Ngay het han moi (yyyy-MM-dd, de trong = chuyen thanh khong thoi han): ");
            if (het != null && !hd.khongThoiHan() && !het.isAfter(hd.getNgayHetHan()))
                throw new IllegalArgumentException("Ngay het han moi phai sau ngay het han cu (" + hd.getNgayHetHan() + ").");
            if (het == null && hd.khongThoiHan()) throw new IllegalArgumentException("Hop dong nay da la khong thoi han.");
            if (dsHDLD.trungVoiHopDongKhac(id, hd.getNgayKy(), het, hd.getMaHD())) throw new IllegalArgumentException("Gia han bi trung thoi gian voi hop dong khac.");
            hd.sua(hd.getLoai(), hd.getNgayKy(), het, hd.getGhiChu());
            ghiNhatKy("Gia han hop dong " + hd.getMaHD() + " (hop dong cua toi)", id);
            GiaoDien.in("Da gia han hop dong " + hd.getMaHD() + ".");
        } else {
            String loai = docChuoiKhongRong("Loai hop dong moi (hien tai: " + hd.getLoai() + "): ");
            LocalDate ky = docNgay("Ngay ky moi (yyyy-MM-dd): ");
            if (ky.isAfter(LocalDate.now())) throw new IllegalArgumentException("Ngay ky khong duoc sau hom nay (" + LocalDate.now() + ").");
            if (ky.isBefore(nv.getNgayVaoLam())) throw new IllegalArgumentException("Ngay ky khong duoc truoc ngay vao lam (" + nv.getNgayVaoLam() + ").");
            LocalDate het = docNgayHetHan("Ngay het han moi (yyyy-MM-dd, de trong = khong thoi han): ");
            String ghiChu = docChuoi("Ghi chu (co the bo trong): ");
            if (dsHDLD.trungVoiHopDongKhac(id, ky, het, hd.getMaHD())) throw new IllegalArgumentException("Thoi gian moi bi trung voi hop dong khac.");
            hd.sua(loai, ky, het, ghiChu);
            ghiNhatKy("Sua hop dong " + hd.getMaHD() + " (hop dong cua toi)", id);
            GiaoDien.in("Da sua hop dong " + hd.getMaHD() + ".");
        }
    }


//=====================================================================================================
//----------------- CHUNG2: CHON NAM + (1) TIM THEO MA  (2) XEM THEO PHONG BAN  (3) XEM HET, CHIA TRANG -----------------
//=====================================================================================================
    private static final String T = GiaoDien.TRAI;
    private static final String P = GiaoDien.PHAI;
    private static final String G = GiaoDien.GIUA;
    private static final java.time.format.DateTimeFormatter GIO_PHUT = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //nhan vien (ma) nam trong pham vi cua quyen q; du lieu cua nhan vien da xoa chi giam doc (toan cong ty) moi thay
    private static java.util.function.Predicate<String> trongQuyen(Quyen q) {
        String[] tam = new String[0];
        for (NhanVien nv : nvTrongQuyen(q)) tam = Mang.them(tam, nv.getMaNV().toUpperCase());
        final String[] ok = tam;      //final de dung duoc trong lambda ben duoi
        boolean tatCa = phamVi(q) == PhamVi.TAT_CA;
        return ma -> Mang.chua(ok, ma.toUpperCase()) || (tatCa && ql.timTheoMa(ma) == null);
    }

    //ngay vao lam som nhat trong pham vi (de lam moc cho danh sach nam)
    private static LocalDate tuNgayTrongQuyen(Quyen q) {
        LocalDate min = null;
        for (NhanVien nv : nvTrongQuyen(q))
            if (nv.getNgayVaoLam() != null && (min == null || nv.getNgayVaoLam().isBefore(min))) min = nv.getNgayVaoLam();
        return min == null ? LocalDate.now() : min;
    }

    private static LocalDate tuNgayCongTy() {
        LocalDate min = null;
        for (NhanVien nv : ql.getDanhSachNV())
            if (nv != null && nv.getNgayVaoLam() != null && (min == null || nv.getNgayVaoLam().isBefore(min))) min = nv.getNgayVaoLam();
        return min == null ? LocalDate.now() : min;
    }

    private static String hoTenNV(String maNV) {
        NhanVien nv = ql.timTheoMa(maNV);
        return nv == null ? "(da xoa)" : nv.getHoTen();
    }

    //phong ban cua nhan vien trong nam (nam hien tai: theo du lieu dang co)
    private static String maPBNam(NhanVien nv, int nam) {
        if (nv == null) return "";
        if (nam <= 0 || nam >= namNay()) return nv.getPhongBan() == null ? "" : nv.getPhongBan().getMaPB();
        return dsLS.xacDinh(nv, nam).ls.getMaPB();
    }

    private static String so1(double x) {
        return String.format("%.1f", x);
    }

    //cac nhan vien thuoc 1 phong ban trong 1 nam
    private static NhanVien[] thanhVienPB(String maPB, int nam) {
        NhanVien[] kq = new NhanVien[0];
        for (NhanVien nv : ql.getDanhSachNV()) {
            if (nv == null || nv.getNgayVaoLam().getYear() > nam) continue;
            if (nam >= namNay() && nv.getTrangThai().toLowerCase().startsWith("nghi")) continue;
            if (maPBNam(nv, nam).equalsIgnoreCase(maPB)) kq = Mang.them(kq, nv);
        }
        return kq;
    }

    private static void sapXepDong(XemChung2.Dong[] ds) {
        Mang.sapXep(ds, (a, b) -> {
            int c = a.maPB.compareToIgnoreCase(b.maPB);
            if (c != 0) return c;
            if (a.ngay != null && b.ngay != null && !a.ngay.equals(b.ngay)) return a.ngay.compareTo(b.ngay);
            return a.ma.compareToIgnoreCase(b.ma);
        });
    }

    //chon thang + nam cho cac thao tac theo thang (thay cho nhap "Thang: / Nam:"), tra ve {thang, nam} hoac null
    private static int[] docThangNam() {
        LocalDate tu = tuNgayCongTy();
        ChonKy.KetQua k = chonTrongCacNam(ChonKy.cacNam(tu, namNay()), tu, new String[0], new int[0], new String[0], n -> new String[0]);
        if (k == null) return null;
        int nam = k.nam;
        int thang = k.thang;
        if (thang == 0) {
            GiaoDien.in("");
            GiaoDien.in("Nam " + nam + " - chon thang");
            Integer t = chonThangCuaNam(tu, nam, new String[]{"BANG LUONG"}, new int[]{14}, new String[]{T},
                    th -> {
                        int dem = 0;
                        for (int i = 0; i < dsBL.getSoLuong(); i++) {
                            BangLuong b = dsBL.getBangLuong(i);
                            if (b != null && b.getThang() == th && b.getNam() == nam) dem++;
                        }
                        return new String[]{dem > 0 ? "Da lap (" + dem + ")" : "Chua lap"};
                    });
            if (t == null) return null;
            thang = t;
        }
        return new int[]{thang, nam};
    }

//----------------- 2. PHONG BAN -----------------

    //phong ban ma nguoi dang nhap quan ly, sap theo ten tang dan (tu dien):
    //giam doc: cac phong co ma giam doc trung minh + cac phong chua phan cong; nguoi khac: chi phong cua minh
    private static PhongBan[] phongBanQuanLy() {
        PhongBan[] kq = new PhongBan[0];
        if (laGiamDoc()) {
            String me = phien.getMaNV();
            for (PhongBan pb : dsPB.getDanhSach())
                if (pb != null && (pb.getMaGiamDoc().isEmpty() || pb.getMaGiamDoc().equalsIgnoreCase(me))) kq = Mang.them(kq, pb);
        } else if (nguoi().getPhongBan() != null) {
            kq = Mang.them(kq, nguoi().getPhongBan());
        }
        Mang.sapXep(kq, (a, b) -> {
            int c = a.getTenPB().compareToIgnoreCase(b.getTenPB());
            return c != 0 ? c : a.getMaPB().compareToIgnoreCase(b.getMaPB());
        });
        return kq;
    }

    private static void pbDanhSachChung2() {
        yeuCau(Quyen.XEM_PHONG_BAN);
        PhongBan[] trongQ = phongBanQuanLy();
        if ((trongQ.length == 0)) {
            GiaoDien.in("Ban chua quan ly phong ban nao.");
            return;
        }
        GiaoDien.in("Ban dang quan ly " + trongQ.length + " phong ban (xep theo ten A-Z).");
        XemChung2.Cau c = new XemChung2.Cau("PHONG BAN")
                .cot(new String[]{"MA PB", "TEN PHONG BAN", "MO TA", "SO NV", "TRUONG PHONG"}, new int[]{7, 20, 30, 6, 18}, new String[]{T, T, T, P, T})
                .khongCotPB()
                .nam(tuNgayTrongQuyen(Quyen.XEM_PHONG_BAN), namNay())
                .nhanMenu("Tim theo ma", "Xem cac loai phong ban quan li", "Xem het")
                .goiYMa("ma phong ban hoac ten")
                .tenPhongBan(Main::tenPB)
                .layDong(nam -> {
                    XemChung2.Dong[] kq = new XemChung2.Dong[0];
                    for (PhongBan pb : trongQ) {
                        NhanVien[] tv = thanhVienPB(pb.getMaPB(), nam);
                        String tp = "-";
                        for (NhanVien nv : tv) {
                            String maCV = nam >= namNay() ? (nv.getChucVu() == null ? "" : nv.getChucVu().getMaCV()) : dsLS.xacDinh(nv, nam).ls.getMaCV();
                            if ("CV02".equals(maCV)) { tp = nv.getHoTen(); break; }
                        }
                        kq = Mang.them(kq, new XemChung2.Dong(pb.getMaPB(), pb.getTenPB(), pb.getMaPB(), null,
                                new String[]{pb.getMaPB(), pb.getTenPB(), pb.getMoTa(), String.valueOf(tv.length), tp},
                                new Object[]{pb, nam}));
                    }
                    return kq;            //da theo thu tu ten tang dan
                })
                .khiChon("xem chi tiet va nhan vien", d -> pbChiTiet((PhongBan) ((Object[]) d.goc)[0], (Integer) ((Object[]) d.goc)[1]))
                .muc2(nam -> pbChonLoai(trongQ, nam));
        XemChung2.chay(c, Main::docChuoi);
    }

    //lua chon 2: moi phong ban quan ly 1 hang kem so nhan vien; chon theo STT hoac ma de vao phong do
    private static void pbChonLoai(PhongBan[] trongQ, int nam) {
        String[][] dong = new String[0][];
        for (PhongBan pb : trongQ)
            dong = Mang.them(dong, new String[]{String.valueOf(dong.length + 1), pb.getMaPB(), pb.getTenPB(), String.valueOf(thanhVienPB(pb.getMaPB(), nam).length)});
        GiaoDien.in("");
        GiaoDien.in("CAC PHONG BAN BAN QUAN LI - NAM " + nam + " (" + trongQ.length + " phong, xep theo ten A-Z)");
        GiaoDien.inBangKhongStt(new String[]{"STT", "MA PB", "TEN PHONG BAN", "SO NHAN VIEN"}, new int[]{5, 8, 26, 13},
                new String[]{G, T, T, P}, dong);
        String in = docChuoi("Chon phong ban (STT hoac ma PB, 0 = quay lai): ");
        if (in.isEmpty() || in.equals("0")) return;
        PhongBan chon = null;
        if (in.matches("\\d{1,3}") && Integer.parseInt(in) >= 1 && Integer.parseInt(in) <= trongQ.length) chon = trongQ[Integer.parseInt(in) - 1];
        else for (PhongBan pb : trongQ) if (pb.getMaPB().equalsIgnoreCase(in)) chon = pb;
        if (chon == null) {
            GiaoDien.in("Khong co phong ban \"" + in + "\" trong danh sach tren.");
            return;
        }
        pbChiTiet(chon, nam);
    }

    //chi tiet 1 phong ban + nhan vien cua phong (nam dang xem); giam doc duoc sua phong o nam hien tai
    private static void pbChiTiet(PhongBan pb, int nam) {
        pb.hienThiThongTin();
        GiaoDien.in("Giam doc quan ly: " + (pb.getMaGiamDoc().isEmpty() ? "(chua phan cong)" : pb.getMaGiamDoc() + " - " + hoTenNV(pb.getMaGiamDoc())));
        hienThanhVienPB(pb.getMaPB(), nam);
        if (laGiamDoc() && nam == namNay() && docChuoi("Sua ten / mo ta phong " + pb.getTenPB() + "? (y/n): ").equalsIgnoreCase("y")) {
            try {
                String ten = docChuoi("Ten phong ban moi (Enter = giu nguyen): ");
                String moTa = docChuoi("Mo ta moi (Enter = giu nguyen): ");
                if (!ten.isBlank()) {
                    for (PhongBan x : dsPB.getDanhSach())
                        if (x != null && x != pb && x.getTenPB().equalsIgnoreCase(ten.trim())) throw new IllegalArgumentException("Da co phong ban ten " + ten.trim().toUpperCase());
                    pb.setTenPB(ten);
                }
                if (!moTa.isBlank()) pb.setMoTa(moTa);
                ghiNhatKy("Sua phong ban " + pb.getMaPB(), pb.getMaPB());
                GiaoDien.in("Da cap nhat phong ban.");
            } catch (IllegalArgumentException e) {
                GiaoDien.in("Khong sua duoc: " + e.getMessage());
            }
        }
    }

    //truong phong / pho phong khong tu sua phong ban: gui yeu cau len giam doc
    private static void guiYeuCauSuaPhongBan(NhanVien nv) {
        PhongBan pb = nv.getPhongBan();
        if (pb == null) {
            GiaoDien.in("Ban chua thuoc phong ban nao.");
            return;
        }
        NhanVien[] nhan = cacNguoiNhanYeuCau(nv);
        if ((nhan.length == 0)) {
            GiaoDien.in("Hien khong co giam doc nao dang hoat dong de nhan yeu cau.");
            return;
        }
        Mang.sapXep(nhan, (a, b) -> Boolean.compare(pb.getMaGiamDoc().equalsIgnoreCase(b.getMaNV()), pb.getMaGiamDoc().equalsIgnoreCase(a.getMaNV())));
        GiaoDien.in("");
        GiaoDien.in("Chon GIAM DOC de gui yeu cau:");
        String[][] dong = new String[0][];
        for (NhanVien x : nhan)
            dong = Mang.them(dong, new String[]{x.getMaNV(), x.getHoTen(), pb.getMaGiamDoc().equalsIgnoreCase(x.getMaNV()) ? "Quan ly phong " + pb.getTenPB() : ""});
        GiaoDien.inBang(new String[]{"MA NV", "HO TEN", "GHI CHU"}, new int[]{8, 24, 30}, new String[]{T, T, T}, dong);
        int c = docInt("Chon nguoi nhan (0 = huy): ", 0, nhan.length);
        if (c == 0) return;
        NhanVien nguoiNhan = nhan[c - 1];
        GiaoDien.in("");
        GiaoDien.in("1. Sua ten phong ban (hien tai: " + pb.getTenPB() + ")");
        GiaoDien.in("2. Sua mo ta phong ban (hien tai: " + (pb.getMoTa().isEmpty() ? "(trong)" : pb.getMoTa()) + ")");
        GiaoDien.in("0. Huy");
        int m = docInt("Chon: ", 0, 2);
        if (m == 0) return;
        String truong = m == 1 ? "PB_TEN" : "PB_MO_TA";
        String cu = m == 1 ? pb.getTenPB() : pb.getMoTa();
        String moi = docChuoiKhongRong((m == 1 ? "Ten phong ban moi: " : "Mo ta moi: "));
        if (m == 1) {
            moi = moi.trim().toUpperCase();
            for (PhongBan x : dsPB.getDanhSach())
                if (x != null && x != pb && x.getTenPB().equalsIgnoreCase(moi)) {
                    GiaoDien.in("Khong gui duoc: da co phong ban ten " + moi + ".");
                    return;
                }
        }
        if (moi.equals(cu)) {
            GiaoDien.in("Gia tri moi giong gia tri hien tai, khong can gui yeu cau.");
            return;
        }
        if (dsYC.daCoYeuCauCho(nv.getMaNV(), namNay(), truong)) {
            GiaoDien.in("Ban da co 1 yeu cau cung muc dang cho duyet. Hay doi duoc xu ly hoac huy (Yeu cau chinh sua ho so cua toi).");
            return;
        }
        GiaoDien.in("Gui den " + nguoiNhan.getMaNV() + " - " + nguoiNhan.getHoTen() + ": sua " + YeuCau.tenTruong(truong).toLowerCase()
                + " tu \"" + cu + "\" thanh \"" + moi + "\".");
        if (!docChuoi("Xac nhan gui yeu cau? (y/n): ").equalsIgnoreCase("y")) {
            GiaoDien.in("Da huy, chua gui yeu cau.");
            return;
        }
        YeuCau yc = YeuCau.moi(dsYC.taoMaMoi(), nv.getMaNV(), nguoiNhan.getMaNV(), namNay(), truong, cu, moi);
        dsYC.them(yc);
        ghiNhatKy("Gui yeu cau " + yc.getMaYC() + " sua " + YeuCau.tenTruong(truong), nguoiNhan.getMaNV());
        GiaoDien.in("Da gui yeu cau " + yc.getMaYC() + ". Chi khi " + nguoiNhan.getHoTen() + " dong y thi phong ban moi duoc sua.");
    }

    //ap dung yeu cau sua phong ban da duoc duyet (phong ban hien tai cua nguoi gui)
    private static String apDungSuaPhongBan(NhanVien gui, String truong, String moi) {
        PhongBan pb = gui.getPhongBan();
        if (pb == null) return "Nguoi gui khong con thuoc phong ban nao.";
        if (moi == null || moi.isBlank()) return "Gia tri moi khong duoc de trong.";
        if (truong.equals("PB_TEN")) {
            for (PhongBan x : dsPB.getDanhSach())
                if (x != null && x != pb && x.getTenPB().equalsIgnoreCase(moi.trim())) return "Da co phong ban ten " + moi.trim().toUpperCase() + ".";
            pb.setTenPB(moi);
        } else {
            pb.setMoTa(moi);
        }
        return null;
    }

    //nhan vien cua 1 phong ban trong 1 nam (chia trang)
    private static void hienThanhVienPB(String maPB, int nam) {
        PhongBan pb = dsPB.timTheoMa(maPB);
        if (pb != null && !PhanQuyen.choPhepPhongBan(nguoi(), Quyen.XEM_PHONG_BAN, pb)) {
            ghiNhatKy("Xem phong ban", pb.getMaPB(), NhatKyHeThong.TU_CHOI);
            throw new KhongCoQuyenException("Ban chi duoc xem phong ban cua minh.");
        }
        XemChung2.Cau c = cauNhanVien(Quyen.XEM_NHAN_VIEN).khongCotPB();
        XemChung2.Dong[] ds = new XemChung2.Dong[0];
        for (XemChung2.Dong d : dongNhanVien(Quyen.XEM_NHAN_VIEN, nam)) if (d.maPB.equalsIgnoreCase(maPB)) ds = Mang.them(ds, d);
        XemChung2.hienPhanTrang(c, "Nhan vien phong " + tenPB(maPB) + " nam " + nam, ds, Main::docChuoi);
    }

//----------------- 3. NHAN VIEN -----------------
    //cau hinh danh sach nhan vien (dung cho menu Quan ly nhan vien va xem nhan vien theo phong ban)
    private static XemChung2.Cau cauNhanVien(Quyen q) {
        return new XemChung2.Cau("NHAN VIEN")
                .cot(new String[]{"MA NV", "HO TEN", "CHUC VU", "LOAI/VAI TRO", "LUONG CO BAN", "TRANG THAI"},
                        new int[]{7, 16, 12, 17, 12, 10}, new String[]{T, T, T, T, P, T})
                .goiYMa("ma / ten nhan vien")
                .tenPhongBan(Main::tenPB)
                .khiChon("xem ho so", d -> {
                    NhanVien nv = (NhanVien) ((Object[]) d.goc)[0];
                    int nam = (Integer) ((Object[]) d.goc)[1];
                    if (nam >= namNay() || !choPhep(Quyen.XEM_LUONG, nv)) inHoSo(nv);
                    else hienHoSoNam(nv, nam);
                })
                .nam(tuNgayTrongQuyen(q), namNay())
                .layDong(nam -> dongNhanVien(q, nam));
    }

    private static XemChung2.Dong[] dongNhanVien(Quyen q, int nam) {
        XemChung2.Dong[] kq = new XemChung2.Dong[0];
        for (NhanVien nv : nvTrongQuyen(q)) {
            if (nv.getNgayVaoLam().getYear() > nam) continue;
            LichSuCongTac ls = dsLS.xacDinh(nv, nam).ls;
            String luong = choPhep(Quyen.XEM_LUONG, nv) ? GiaoDien.tien(ls.getLuongCoBan()) : "***";
            kq = Mang.them(kq, new XemChung2.Dong(nv.getMaNV(), nv.getMaNV() + " " + nv.getHoTen(), ls.getMaPB(), null,
                    new String[]{nv.getMaNV(), nv.getHoTen(), ls.getTenCV().isEmpty() ? "-" : ls.getTenCV(),
                            PhanQuyen.xacDinhVaiTro(nv).ten().replace("Nhan vien ", "NV "), luong, nam >= namNay() ? nv.getTrangThai() : "-"},
                    new Object[]{nv, nam}));
        }
        sapXepDong(kq);
        return kq;
    }

    private static void nvDanhSachChung2() {
        yeuCau(Quyen.XEM_NHAN_VIEN);
        XemChung2.chay(cauNhanVien(Quyen.XEM_NHAN_VIEN), Main::docChuoi);
    }

//----------------- 4. CHAM CONG -----------------
    private static void ccDanhSachChung2() {
        yeuCau(Quyen.XEM_CHAM_CONG);
        java.util.function.Predicate<String> ok = trongQuyen(Quyen.XEM_CHAM_CONG);
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NhanVien nv : nvTrongQuyen(Quyen.XEM_CHAM_CONG)) {
                String pb = maPBNam(nv, nam);
                for (ChamCong cc : dsCC.timTheoMaNV(nv.getMaNV())) {
                    if (cc.getNgay().getYear() != nam) continue;
                    kq = Mang.them(kq, new XemChung2.Dong(cc.getMaChamCong(), nv.getMaNV() + " " + nv.getHoTen(), pb, cc.getNgay(),
                            new String[]{cc.getMaChamCong(), nv.getMaNV(), nv.getHoTen(), String.valueOf(cc.getNgay()),
                                    String.valueOf(cc.getGioVao()), String.valueOf(cc.getGioRa()),
                                    so1(Math.min(cc.tinhGioLam(), 8)), so1(cc.tinhGioTangCa())}, cc));
                }
            }
            sapXepDong(kq);
            return kq;
        };
        XemChung2.Cau c = new XemChung2.Cau("CHAM CONG")
                .cot(new String[]{"MA CC", "MA NV", "HO TEN", "NGAY", "VAO", "RA", "GIO", "TC"},
                        new int[]{8, 7, 18, 10, 5, 5, 5, 5}, new String[]{T, T, T, T, G, G, P, P})
                .goiYMa("ma nhan vien / ma cham cong / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tuNgayTrongQuyen(Quyen.XEM_CHAM_CONG), namNay(), new String[]{"GIO LAM", "TANG CA"}, new int[]{9, 8}, new String[]{P, P},
                        nam -> {
                            double gio = 0, tc = 0;
                            for (XemChung2.Dong d : lay.apply(nam)) {
                                ChamCong cc = (ChamCong) d.goc;
                                gio += Math.min(cc.tinhGioLam(), 8);
                                tc += cc.tinhGioTangCa();
                            }
                            return new String[]{so1(gio), so1(tc)};
                        })
                .layDong(lay)
                .khiChon("xem chi tiet", d -> {
                    ChamCong cc = (ChamCong) d.goc;
                    String[][] cap = new String[0][];
                    cap = Mang.them(cap, new String[]{"Ma cham cong", cc.getMaChamCong()});
                    cap = Mang.them(cap, new String[]{"Nhan vien", cc.getMaNV() + " - " + hoTenNV(cc.getMaNV())});
                    cap = Mang.them(cap, new String[]{"Ngay", String.valueOf(cc.getNgay())});
                    cap = Mang.them(cap, new String[]{"Gio vao - ra", cc.getGioVao() + " - " + cc.getGioRa()});
                    cap = Mang.them(cap, new String[]{"Gio lam thuong", so1(Math.min(cc.tinhGioLam(), 8))});
                    cap = Mang.them(cap, new String[]{"Gio tang ca", so1(cc.tinhGioTangCa())});
                    GiaoDien.inThongTin(cap);
                });
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 5. BAO HIEM -----------------
    private static void bhDanhSachChung2() {
        yeuCau(Quyen.XEM_BAO_HIEM);
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NhanVien nv : nvTrongQuyen(Quyen.XEM_BAO_HIEM)) {
                String pb = maPBNam(nv, nam);
                for (BaoHiem bh : dsBH.timTheoMaNV(nv.getMaNV())) {
                    if (!baoHiemTrongNam(bh, nam)) continue;
                    boolean yt = bh instanceof BaoHiemYTe;
                    kq = Mang.them(kq, new XemChung2.Dong(bh.getMaBH(), nv.getMaNV() + " " + nv.getHoTen(), pb, null,
                            new String[]{bh.getMaBH(), nv.getMaNV(), nv.getHoTen(), yt ? "BHYT" : "BHXH", bh.getSoSo(),
                                    String.valueOf(bh.getNgayThamGia()), bh.getTrangThai(),
                                    yt ? String.valueOf(((BaoHiemYTe) bh).getHanTheDen()) : "-"}, bh));
                }
            }
            sapXepDong(kq);
            return kq;
        };
        XemChung2.Cau c = new XemChung2.Cau("BAO HIEM")
                .cot(new String[]{"MA BH", "MA NV", "HO TEN", "LOAI", "SO SO / THE", "THAM GIA", "TRANG THAI", "HAN THE"},
                        new int[]{7, 7, 16, 5, 13, 10, 10, 10}, new String[]{T, T, T, T, T, T, T, T})
                .goiYMa("ma nhan vien / ma bao hiem / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tuNgayTrongQuyen(Quyen.XEM_BAO_HIEM), namNay(), new String[]{"BHXH", "BHYT"}, new int[]{6, 6}, new String[]{P, P},
                        nam -> {
                            int xh = 0, yt = 0;
                            for (XemChung2.Dong d : lay.apply(nam)) { if (d.goc instanceof BaoHiemYTe) yt++; else xh++; }
                            return new String[]{String.valueOf(xh), String.valueOf(yt)};
                        })
                .layDong(lay)
                .khiChon("xem chi tiet", d -> ((BaoHiem) d.goc).hienThiThongTin());
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 6. BANG LUONG -----------------
    private static void blDanhSachChung2() {
        yeuCau(Quyen.XEM_LUONG);
        ghiNhatKy("Xem danh sach bang luong", phamVi(Quyen.XEM_LUONG) == PhamVi.TAT_CA ? "TAT CA" : "PHONG");
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NhanVien nv : nvTrongQuyen(Quyen.XEM_LUONG)) {
                String pb = maPBNam(nv, nam);
                for (int t = 1; t <= 12; t++) {
                    BangLuong bl = dsBL.timTheoMaNV(nv.getMaNV(), t, nam);
                    if (bl == null) continue;
                    kq = Mang.them(kq, new XemChung2.Dong(bl.getMaBangLuong(), nv.getMaNV() + " " + nv.getHoTen(), pb, LocalDate.of(nam, t, 1),
                            new String[]{t + "/" + nam, nv.getMaNV(), nv.getHoTen(), GiaoDien.tien(bl.getLuongNhanVien()),
                                    GiaoDien.tien(bl.getTienThuong()), GiaoDien.tien(bl.getTienPhat()),
                                    GiaoDien.tien(bl.getLuongThucNhan())}, bl));
                }
            }
            Mang.sapXep(kq, (a, b) -> {
                int c = a.ngay.compareTo(b.ngay);
                if (c != 0) return c;
                c = a.maPB.compareToIgnoreCase(b.maPB);
                return c != 0 ? c : a.maNV.compareToIgnoreCase(b.maNV);
            });
            return kq;
        };
        XemChung2.Cau c = new XemChung2.Cau("BANG LUONG")
                .cot(new String[]{"THANG", "MA NV", "HO TEN", "LUONG NV", "THUONG", "PHAT", "THUC NHAN"},
                        new int[]{8, 7, 15, 11, 9, 8, 12}, new String[]{G, T, T, P, P, P, P})
                .goiYMa("ma nhan vien / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tuNgayTrongQuyen(Quyen.XEM_LUONG), namNay(), new String[]{"TONG THUC NHAN"}, new int[]{16}, new String[]{P},
                        nam -> {
                            double tong = 0;
                            for (XemChung2.Dong d : lay.apply(nam)) tong += ((BangLuong) d.goc).getLuongThucNhan();
                            return new String[]{GiaoDien.tien(tong)};
                        })
                .layDong(lay)
                .khiChon("xem phieu luong", d -> ((BangLuong) d.goc).xuatBangLuong());
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 7. BAO CAO THEO PHONG BAN -----------------
    private static void bcPhongBanChung2() {
        yeuCau(Quyen.XEM_BAO_CAO_CONG_TY);
        PhongBan[] tamPB = new PhongBan[0];
        for (PhongBan pb : dsPB.getDanhSach()) if (pb != null) tamPB = Mang.them(tamPB, pb);
        final PhongBan[] cacPB = tamPB;      //final de dung duoc trong lambda ben duoi
        if ((cacPB.length == 0)) {
            GiaoDien.in("Chua co phong ban nao.");
            return;
        }
        LocalDate tu = tuNgayCongTy();
        java.util.function.BiConsumer<Integer, String> chiTiet = (nam, maPB) -> {
            GiaoDien.in("Nam " + nam + " - chon thang cho bao cao phong " + tenPB(maPB));
            Integer t = chonThangCuaNam(tu, nam, new String[]{"BANG LUONG"}, new int[]{14}, new String[]{T},
                    th -> new String[]{daLapLuongPhong(maPB, th, nam) ? "Da lap" : "Chua lap"});
            if (t == null) return;
            GiaoDien.in("");
            baoCao.inBaoCaoTheoPhong(maPB, ql, dsBL, t, nam);
        };
        XemChung2.Cau c = new XemChung2.Cau("BAO CAO THEO PHONG BAN")
                .cot(new String[]{"MA PB", "TEN PHONG BAN", "SO NV", "THUC NHAN CA NAM", "THUONG", "PHAT"},
                        new int[]{7, 22, 6, 17, 13, 12}, new String[]{T, T, P, P, P, P})
                .khongCotPB()
                .goiYMa("ma phong ban")
                .tenPhongBan(Main::tenPB)
                .nam(tu, namNay())
                .layDong(nam -> {
                    XemChung2.Dong[] kq = new XemChung2.Dong[0];
                    for (PhongBan pb : cacPB) {
                        NhanVien[] tv = thanhVienPB(pb.getMaPB(), nam);
                        double thucNhan = 0, thuong = 0, phat = 0;
                        for (NhanVien nv : tv)
                            for (int t = 1; t <= 12; t++) {
                                BangLuong bl = dsBL.timTheoMaNV(nv.getMaNV(), t, nam);
                                if (bl == null) continue;
                                thucNhan += bl.getLuongThucNhan();
                                thuong += bl.getTienThuong();
                                phat += bl.getTienPhat();
                            }
                        kq = Mang.them(kq, new XemChung2.Dong(pb.getMaPB(), "", pb.getMaPB(), null,
                                new String[]{pb.getMaPB(), pb.getTenPB(), String.valueOf(tv.length), GiaoDien.tien(thucNhan),
                                        GiaoDien.tien(thuong), GiaoDien.tien(phat)}, new Object[]{pb, nam}));
                    }
                    sapXepDong(kq);
                    return kq;
                })
                .khiChon("xem bao cao theo thang", d -> chiTiet.accept((Integer) ((Object[]) d.goc)[1], d.ma))
                .xemTheoPhongBan(chiTiet);
        XemChung2.chay(c, Main::docChuoi);
    }

    private static boolean daLapLuongPhong(String maPB, int thang, int nam) {
        for (NhanVien nv : thanhVienPB(maPB, nam))
            if (dsBL.timTheoMaNV(nv.getMaNV(), thang, nam) != null) return true;
        return false;
    }

//----------------- 8. THUONG PHAT -----------------
    private static void tpDanhSachChung2() {
        yeuCau(Quyen.XEM_THUONG_PHAT);
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NhanVien nv : nvTrongQuyen(Quyen.XEM_THUONG_PHAT)) {
                String pb = maPBNam(nv, nam);
                for (ThuongPhat tp : dsTP.timTheoMaNV(nv.getMaNV())) {
                    if (tp.getNgayApDung().getYear() != nam) continue;
                    kq = Mang.them(kq, new XemChung2.Dong(tp.getMaTP(), nv.getMaNV() + " " + nv.getHoTen(), pb, tp.getNgayApDung(),
                            new String[]{String.valueOf(tp.getNgayApDung()), nv.getMaNV(), nv.getHoTen(), tp.getLoai(),
                                    tp.getLyDo(), GiaoDien.tien(tp.getSoTien())}, tp));
                }
            }
            sapXepDong(kq);
            return kq;
        };
        XemChung2.Cau c = new XemChung2.Cau("THUONG / PHAT")
                .cot(new String[]{"NGAY", "MA NV", "HO TEN", "LOAI", "LY DO", "SO TIEN"},
                        new int[]{10, 7, 16, 7, 26, 10}, new String[]{T, T, T, T, T, P})
                .goiYMa("ma nhan vien / ma thuong phat / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tuNgayTrongQuyen(Quyen.XEM_THUONG_PHAT), namNay(), new String[]{"TONG THUONG", "TONG PHAT"}, new int[]{12, 12}, new String[]{P, P},
                        nam -> {
                            double th = 0, ph = 0;
                            for (XemChung2.Dong d : lay.apply(nam)) {
                                ThuongPhat tp = (ThuongPhat) d.goc;
                                if ("PHAT".equalsIgnoreCase(tp.getLoai())) ph += tp.getSoTien(); else th += tp.getSoTien();
                            }
                            return new String[]{GiaoDien.tien(th), GiaoDien.tien(ph)};
                        })
                .layDong(lay)
                .khiChon("xem chi tiet", d -> ((ThuongPhat) d.goc).hienThiThongTin());
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 9. NGHI PHEP -----------------
    private static int namCuoiNghiPhep() {
        int cuoi = namNay();
        for (NghiPhep np : dsNP.getDanhSach()) cuoi = Math.max(cuoi, np.getNgayBatDau().getYear());
        return cuoi;
    }

    private static String khoangNgay(NghiPhep np) {
        return String.format("%02d/%02d - %02d/%02d", np.getNgayBatDau().getDayOfMonth(), np.getNgayBatDau().getMonthValue(),
                np.getNgayKetThuc().getDayOfMonth(), np.getNgayKetThuc().getMonthValue());
    }

    private static XemChung2.Dong dongNghiPhep(NghiPhep np, NhanVien nv, int nam) {
        return new XemChung2.Dong(np.getMaNP(), nv.getMaNV() + " " + nv.getHoTen(), maPBNam(nv, nam), np.getNgayBatDau(),
                new String[]{np.getMaNP(), nv.getMaNV(), nv.getHoTen(), khoangNgay(np),
                        String.valueOf(np.tinhSoNgayNghi()), np.getLoaiNghi(), np.getTrangThai()}, np);
    }

    private static final String[] TD_NGHI_PHEP = {"MA DON", "MA NV", "HO TEN", "TU - DEN", "NGAY", "LOAI", "TRANG THAI"};
    private static final int[] RONG_NGHI_PHEP = {6, 6, 16, 13, 4, 10, 10};
    private static final String[] CANH_NGHI_PHEP = {T, T, T, T, P, T, T};

    //chi tiet 1 don; neu duoc quyen duyet va don dang cho thi hoi duyet luon
    private static void npChiTietVaDuyet(NghiPhep np) {
        String[][] cap = new String[0][];
        cap = Mang.them(cap, new String[]{"Ma don", np.getMaNP()});
        cap = Mang.them(cap, new String[]{"Nhan vien", np.getMaNV() + " - " + hoTenNV(np.getMaNV())});
        cap = Mang.them(cap, new String[]{"Thoi gian", np.getNgayBatDau() + " den " + np.getNgayKetThuc() + " (" + np.tinhSoNgayNghi() + " ngay)"});
        cap = Mang.them(cap, new String[]{"Loai nghi", np.getLoaiNghi()});
        cap = Mang.them(cap, new String[]{"Ly do", np.getLyDo()});
        cap = Mang.them(cap, new String[]{"Trang thai", np.getTrangThai()});
        GiaoDien.inThongTin(cap);
        if ("Da duyet".equals(np.getTrangThai()) || !co(Quyen.DUYET_NGHI_PHEP)) return;
        NhanVien chu = ql.timTheoMa(np.getMaNV());
        if (chu != null && !choPhep(Quyen.DUYET_NGHI_PHEP, chu)) return;
        if (docChuoi("Duyet don nay? (y/n): ").equalsIgnoreCase("y")) npDuyetDon(np);
    }

    private static void npDanhSachChung2() {
        yeuCau(Quyen.XEM_NGHI_PHEP);
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NhanVien nv : nvTrongQuyen(Quyen.XEM_NGHI_PHEP))
                for (NghiPhep np : dsNP.timTheoMaNV(nv.getMaNV()))
                    if (np.getNgayBatDau().getYear() == nam) kq = Mang.them(kq, dongNghiPhep(np, nv, nam));
            sapXepDong(kq);
            return kq;
        };
        XemChung2.Cau c = new XemChung2.Cau("NGHI PHEP")
                .cot(TD_NGHI_PHEP, RONG_NGHI_PHEP, CANH_NGHI_PHEP)
                .goiYMa("ma nhan vien / ma don / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tuNgayTrongQuyen(Quyen.XEM_NGHI_PHEP), namCuoiNghiPhep(), new String[]{"CHO DUYET", "SO NGAY NGHI"}, new int[]{10, 12}, new String[]{P, P},
                        nam -> {
                            int cho = 0, ngay = 0;
                            for (XemChung2.Dong d : lay.apply(nam)) {
                                NghiPhep np = (NghiPhep) d.goc;
                                ngay += np.tinhSoNgayNghi();
                                if (!"Da duyet".equals(np.getTrangThai())) cho++;
                            }
                            return new String[]{String.valueOf(cho), String.valueOf(ngay)};
                        })
                .layDong(lay)
                .khiChon("xem chi tiet / duyet", d -> npChiTietVaDuyet((NghiPhep) d.goc));
        XemChung2.chay(c, Main::docChuoi);
    }

    //don dang cho duyet ma minh duoc duyet (dung cho menu "Duyet don" va "Duyet yeu cau")
    private static void npDuyetCoDanhSach() {
        yeuCau(Quyen.DUYET_NGHI_PHEP);
        java.util.function.IntFunction<XemChung2.Dong[]> lay = nam -> {
            XemChung2.Dong[] kq = new XemChung2.Dong[0];
            for (NghiPhep np : dsNP.getDanhSach()) {
                if ("Da duyet".equals(np.getTrangThai()) || np.getNgayBatDau().getYear() != nam) continue;
                NhanVien chu = ql.timTheoMa(np.getMaNV());
                if (chu == null ? phamVi(Quyen.DUYET_NGHI_PHEP) != PhamVi.TAT_CA : !choPhep(Quyen.DUYET_NGHI_PHEP, chu)) continue;
                kq = Mang.them(kq, chu == null ? new XemChung2.Dong(np.getMaNP(), np.getMaNV(), "", np.getNgayBatDau(),
                        new String[]{np.getMaNP(), np.getMaNV(), "(da xoa)", khoangNgay(np),
                                String.valueOf(np.tinhSoNgayNghi()), np.getLoaiNghi(), np.getTrangThai()}, np)
                        : dongNghiPhep(np, chu, nam));
            }
            sapXepDong(kq);
            return kq;
        };
        LocalDate tu = LocalDate.now();
        int cuoi = namNay();
        boolean coDon = false;
        for (NghiPhep np : dsNP.getDanhSach()) {
            if ("Da duyet".equals(np.getTrangThai())) continue;
            NhanVien chuDon = ql.timTheoMa(np.getMaNV());
            if (chuDon == null ? phamVi(Quyen.DUYET_NGHI_PHEP) != PhamVi.TAT_CA : !choPhep(Quyen.DUYET_NGHI_PHEP, chuDon)) continue;
            coDon = true;
            if (np.getNgayBatDau().isBefore(tu)) tu = np.getNgayBatDau();
            cuoi = Math.max(cuoi, np.getNgayBatDau().getYear());
        }
        if (!coDon) {
            GiaoDien.in("Khong co don nghi phep nao dang cho ban duyet.");
            return;
        }
        XemChung2.Cau c = new XemChung2.Cau("DON NGHI PHEP CHO DUYET")
                .cot(TD_NGHI_PHEP, RONG_NGHI_PHEP, CANH_NGHI_PHEP)
                .goiYMa("ma nhan vien / ma don / ten")
                .tenPhongBan(Main::tenPB)
                .nam(tu, cuoi)
                .layDong(lay)
                .khiChon("duyet don", d -> npChiTietVaDuyet((NghiPhep) d.goc))
                .thoatSauKhiChon();
        XemChung2.chay(c, Main::docChuoi);
    }

    //duyet 1 don cu the (kiem tra quyen theo chu don)
    private static void npDuyetDon(NghiPhep np) {
        yeuCauTheoMaNV(Quyen.DUYET_NGHI_PHEP, np.getMaNV());
        if ("Da duyet".equals(np.getTrangThai())) {
            GiaoDien.in("Don nay da duoc duyet truoc do.");
            return;
        }
        dsNP.duyetTheoMa(np.getMaNP());
        ghiNhatKy("Duyet don nghi phep " + np.getMaNP(), np.getMaNV());
        GiaoDien.in("Da duyet don " + np.getMaNP() + ".");
    }

//----------------- 10. DUYET YEU CAU SUA HO SO -----------------
    private static void ycDuyet() {
        yeuCau(Quyen.DUYET_YEU_CAU);
        String me = phien.getMaNV();
        if (dsYC.timCanDuyet(me).length == 0) {
            GiaoDien.in("Khong co yeu cau nao dang cho ban duyet.");
            return;
        }
        LocalDate tu = LocalDate.now();
        for (YeuCau y : dsYC.timCanDuyet(me)) if (y.getNgayGui().toLocalDate().isBefore(tu)) tu = y.getNgayGui().toLocalDate();
        XemChung2.Cau c = new XemChung2.Cau("YEU CAU SUA HO SO CHO DUYET")
                .cot(new String[]{"MA YC", "MA NV", "HO TEN", "MUC SUA", "GIA TRI CU", "GIA TRI MOI", "NGAY GUI"},
                        new int[]{5, 6, 12, 12, 20, 20, 10}, new String[]{T, T, T, T, T, T, T})
                .khongCotPB()
                .goiYMa("ma nguoi gui / ma yeu cau")
                .tenPhongBan(Main::tenPB)
                .nam(tu, namNay())
                .layDong(nam -> {
                    XemChung2.Dong[] kq = new XemChung2.Dong[0];
                    for (YeuCau y : dsYC.timCanDuyet(me)) {
                        if (y.getNgayGui().getYear() != nam) continue;
                        NhanVien gui = ql.timTheoMa(y.getNguoiGui());
                        kq = Mang.them(kq, new XemChung2.Dong(y.getMaYC(), y.getNguoiGui(), maPBNam(gui, nam), y.getNgayGui().toLocalDate(),
                                new String[]{y.getMaYC(), y.getNguoiGui(), hoTenNV(y.getNguoiGui()),
                                        YeuCau.tenTruong(y.getTruong()), hienGiaTri(y.getTruong(), y.getGiaTriCu()),
                                        hienGiaTri(y.getTruong(), y.getGiaTriMoi()), String.valueOf(y.getNgayGui().toLocalDate())}, y));
                    }
                    sapXepDong(kq);
                    return kq;
                })
                .khiChon("xu ly (dong y / tu choi)", d -> xuLyYeuCau((YeuCau) d.goc))
                .thoatSauKhiChon();
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 11. QUY DINH THUONG PHAT -----------------
    private static void qdDanhSachChung2() {
        XemChung2.Dong[] ds = new XemChung2.Dong[0];
        for (QuyDinhThuongPhat qd : dsQD.getDanhSach()) {
            if (qd == null) continue;
            ds = Mang.them(ds, new XemChung2.Dong(qd.getMaQD(), "", "", null,
                    new String[]{qd.getMaQD(), qd.getTenQD(), qd.getLoai(), qd.getDieuKien() + " " + TepVanBan.soDep(qd.getNguong()),
                            GiaoDien.tien(qd.getMucTien()), qd.getDonVi()}, qd));
        }
        final XemChung2.Dong[] dsCuoi = ds;      //final de dung duoc trong lambda
        XemChung2.Cau c = new XemChung2.Cau("QUY DINH THUONG PHAT")
                .cot(new String[]{"MA QD", "TEN QUY DINH", "LOAI", "DIEU KIEN", "MUC TIEN", "DON VI"},
                        new int[]{7, 30, 7, 10, 12, 22}, new String[]{T, T, T, T, P, T})
                .khongPhongBan()
                .goiYMa("ma quy dinh")
                .layDong(nam -> dsCuoi);
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 12. TAI KHOAN -----------------
    private static void tkDanhSach() {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        XemChung2.Dong[] ds = new XemChung2.Dong[0];
        for (TaiKhoan tk : dsTK.getDanhSach()) {
            NhanVien nv = ql.timTheoMa(tk.getTenDangNhap());
            ds = Mang.them(ds, new XemChung2.Dong(tk.getTenDangNhap(), tk.getTenDangNhap(), maPBNam(nv, 0), null,
                    new String[]{tk.getTenDangNhap(), nv == null ? "(khong con nhan vien)" : nv.getHoTen(),
                            nv == null ? "-" : PhanQuyen.xacDinhVaiTro(nv).ten().replace("Nhan vien ", "NV "), tk.getTrangThai(),
                            tk.isPhaiDoiMatKhau() ? "Co" : "Khong"}, tk));
        }
        sapXepDong(ds);
        final XemChung2.Dong[] dsCuoi = ds;      //final de dung duoc trong lambda
        XemChung2.Cau c = new XemChung2.Cau("TAI KHOAN")
                .cot(new String[]{"TAI KHOAN", "HO TEN", "VAI TRO", "TRANG THAI", "DOI MK"},
                        new int[]{10, 22, 22, 11, 7}, new String[]{T, T, T, T, G})
                .goiYMa("ten dang nhap / ma nhan vien")
                .tenPhongBan(Main::tenPB)
                .layDong(nam -> dsCuoi);
        XemChung2.chay(c, Main::docChuoi);
    }

//----------------- 13. NHAT KY -----------------
    private static void nkChung2() {
        yeuCau(Quyen.XEM_NHAT_KY);
        NhatKyHeThong[] tatCa = dsNK.ganDay(Math.max(1, dsNK.getSoLuong()));
        LocalDate tu = LocalDate.now();
        for (NhatKyHeThong n : tatCa) if (n.getThoiDiem().toLocalDate().isBefore(tu)) tu = n.getThoiDiem().toLocalDate();
        XemChung2.Cau c = new XemChung2.Cau("NHAT KY HE THONG")
                .cot(new String[]{"THOI DIEM", "NGUOI LAM", "HO TEN", "HANH DONG", "DOI TUONG", "KET QUA"},
                        new int[]{16, 9, 14, 36, 9, 9}, new String[]{T, T, T, T, T, T})
                .goiYMa("ma nguoi lam / doi tuong")
                .tenPhongBan(Main::tenPB)
                .nam(tu, namNay(), new String[]{"BAT THUONG"}, new int[]{11}, new String[]{P},
                        nam -> {
                            int dem = 0;
                            for (NhatKyHeThong n : tatCa)
                                if (n.getThoiDiem().getYear() == nam && !NhatKyHeThong.THANH_CONG.equals(n.getKetQua())) dem++;
                            return new String[]{String.valueOf(dem)};
                        })
                .layDong(nam -> {
                    XemChung2.Dong[] kq = new XemChung2.Dong[0];
                    for (NhatKyHeThong n : tatCa) {
                        if (n.getThoiDiem().getYear() != nam) continue;
                        NhanVien nv = ql.timTheoMa(n.getNguoiThucHien());
                        kq = Mang.them(kq, new XemChung2.Dong(n.getDoiTuong(), n.getNguoiThucHien(), maPBNam(nv, nam), n.getThoiDiem().toLocalDate(),
                                new String[]{n.getThoiDiem().format(GIO_PHUT), n.getNguoiThucHien(), nv == null ? "-" : nv.getHoTen(),
                                        n.getHanhDong(), n.getDoiTuong(), n.getKetQua()}, n));
                    }
                    Mang.sapXep(kq, (a, b) -> ((NhatKyHeThong) b.goc).getThoiDiem().compareTo(((NhatKyHeThong) a.goc).getThoiDiem()));     //moi nhat truoc
                    return kq;
                });
        XemChung2.chay(c, Main::docChuoi);
    }

}
