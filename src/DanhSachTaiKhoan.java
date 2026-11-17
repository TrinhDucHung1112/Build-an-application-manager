import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Danh sach tai khoan dang nhap, doc/ghi file data/taikhoan.txt
 * (moi dong: taiKhoan|matKhau|trangThai|phaiDoiMatKhau|soLanSai; file cu chi co 2 cot van doc duoc).
 */
public class DanhSachTaiKhoan implements IQuanLyDuLieu {
//----thuoc tinh----
    private static final String DUONG_DAN = DuongDanDuLieu.file("taikhoan.txt");
    public static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;

    /** Ket qua kiem tra dang nhap. */
    public enum KetQuaDangNhap { THANH_CONG, SAI_THONG_TIN, BI_KHOA, CHO_DUYET }

    private TaiKhoan[] danhSach;
    private int soLuong;

//----constructor----
    public DanhSachTaiKhoan(int sucChua) {
        this.danhSach = new TaiKhoan[sucChua];
        this.soLuong = 0;
    }

//----methods----
    public int getSoLuong() {
        return soLuong;
    }

    public TaiKhoan[] getDanhSach() {
        return Arrays.copyOf(danhSach, soLuong);
    }

    public int timViTri(String tenDangNhap) {
        if (tenDangNhap == null) return -1;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getTenDangNhap().equalsIgnoreCase(tenDangNhap.trim())) return i;
        }
        return -1;
    }

    public TaiKhoan timTheoTen(String tenDangNhap) {
        int vt = timViTri(tenDangNhap);
        return vt == -1 ? null : danhSach[vt];
    }

    public boolean them(TaiKhoan tk) {
        if (tk == null) return false;
        if (timViTri(tk.getTenDangNhap()) != -1) return false;     //trung tai khoan
        if (soLuong >= danhSach.length) danhSach = Arrays.copyOf(danhSach, Math.max(10, danhSach.length * 2));
        danhSach[soLuong++] = tk;
        return true;
    }

    public boolean xoaTheoTen(String tenDangNhap) {
        int vt = timViTri(tenDangNhap);
        if (vt == -1) return false;
        for (int i = vt; i < soLuong - 1; i++) danhSach[i] = danhSach[i + 1];
        danhSach[--soLuong] = null;
        return true;
    }

    /**
     * Kiem tra dang nhap. Sai mat khau SO_LAN_SAI_TOI_DA lan lien tiep thi tai khoan bi KHOA.
     * Dang nhap dung thi so lan sai ve 0.
     */
    public KetQuaDangNhap xacThuc(String tenDangNhap, String matKhau) {
        TaiKhoan tk = timTheoTen(tenDangNhap);
        if (tk == null) return KetQuaDangNhap.SAI_THONG_TIN;
        if (tk.daKhoa()) return KetQuaDangNhap.BI_KHOA;
        if (!tk.kiemTraMatKhau(matKhau)) {
            if (tk.tangSoLanSai() >= TaiKhoan.SO_LAN_SAI_TOI_DA) {
                tk.setTrangThai(TaiKhoan.KHOA);
                return KetQuaDangNhap.BI_KHOA;
            }
            return KetQuaDangNhap.SAI_THONG_TIN;
        }
        if (tk.choDuyet()) return KetQuaDangNhap.CHO_DUYET;
        tk.datLaiSoLanSai();
        return KetQuaDangNhap.THANH_CONG;
    }

    /** Tra ve tai khoan neu dang nhap thanh cong, nguoc lai tra ve null. */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        return xacThuc(tenDangNhap, matKhau) == KetQuaDangNhap.THANH_CONG ? timTheoTen(tenDangNhap) : null;
    }

    /**
     * Doi mat khau. Tra ve null neu thanh cong, nguoc lai tra ve thong bao loi.
     * Mat khau moi: it nhat 6 ky tu, khong chua khoang trang hay dau |, khac mat khau cu va mat khau mac dinh.
     */
    public String doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        TaiKhoan tk = timTheoTen(tenDangNhap);
        if (tk == null) return "Khong tim thay tai khoan.";
        if (!tk.kiemTraMatKhau(matKhauCu)) return "Mat khau hien tai khong dung.";
        if (matKhauMoi == null || matKhauMoi.isBlank()) return "Mat khau moi khong duoc de trong.";
        String moi = matKhauMoi.trim();
        if (moi.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            return "Mat khau moi phai co it nhat " + DO_DAI_MAT_KHAU_TOI_THIEU + " ky tu.";
        }
        if (moi.contains(" ") || moi.contains(TepVanBan.PHAN_CACH)) {
            return "Mat khau moi khong duoc chua khoang trang hoac dau " + TepVanBan.PHAN_CACH;
        }
        if (moi.equals(tk.getMatKhau())) return "Mat khau moi phai khac mat khau cu.";
        if (moi.equals(MaNhanVien.matKhauMacDinh(tk.getTenDangNhap()))) return "Khong duoc dung mat khau mac dinh.";
        tk.setMatKhau(moi);
        tk.setPhaiDoiMatKhau(false);
        return null;
    }

    /** Dat lai mat khau mac dinh (bat buoc doi o lan dang nhap sau, xoa so lan sai). Tra ve mat khau tam, hoac null. */
    public String datLaiMatKhauMacDinh(String tenDangNhap) {
        TaiKhoan tk = timTheoTen(tenDangNhap);
        if (tk == null) return null;
        String mk = MaNhanVien.matKhauMacDinh(tk.getTenDangNhap());
        tk.setMatKhau(mk);
        tk.setPhaiDoiMatKhau(true);
        tk.datLaiSoLanSai();
        return mk;
    }

    /** Nhan vien nao chua co tai khoan thi tao (mat khau mac dinh). Tra ve so tai khoan vua tao. */
    public int dongBoVoiNhanVien(NhanVien[] dsNV) {
        int them = 0;
        for (NhanVien nv : dsNV) {
            if (nv != null && timViTri(nv.getMaNV()) == -1) {
                if (them(new TaiKhoan(nv.getMaNV(), MaNhanVien.matKhauMacDinh(nv.getMaNV())))) them++;
            }
        }
        return them;
    }

//----doc/ghi file----
    @Override
    public void docFile() {
        File f = new File(DUONG_DAN);
        if (!f.exists()) {
            GiaoDien.in("Chua co file tai khoan (" + DUONG_DAN + ").");
            return;
        }
        try {
            List<String[]> cacDong = TepVanBan.docDong(DUONG_DAN);
            danhSach = new TaiKhoan[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 2) continue;
                try {
                    TaiKhoan tk = new TaiKhoan(p[0], p[1]);        //file cu 2 cot: dung mat khau mac dinh thi phai doi
                    if (p.length >= 3 && TaiKhoan.hopLe(p[2])) tk.setTrangThai(p[2]);
                    if (p.length >= 4) tk.setPhaiDoiMatKhau(p[3].trim().equalsIgnoreCase("true"));
                    if (p.length >= 5) {
                        try {
                            tk.setSoLanSai(Integer.parseInt(p[4].trim()));
                        } catch (NumberFormatException e) {
                            tk.setSoLanSai(0);
                        }
                    }
                    them(tk);
                } catch (IllegalArgumentException e) {
                    GiaoDien.in("Bo qua dong tai khoan loi: " + e.getMessage());
                }
            }
            GiaoDien.in("Da doc " + soLuong + " tai khoan tu file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi doc file tai khoan: " + e.getMessage());
        }
    }

    @Override
    public void ghiFile() {
        List<String> dong = new ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            TaiKhoan tk = danhSach[i];
            dong.add(TepVanBan.ghep(tk.getTenDangNhap(), tk.getMatKhau(), tk.getTrangThai(),
                    tk.isPhaiDoiMatKhau(), tk.getSoLanSai()));
        }
        try {
            TepVanBan.ghiDong(DUONG_DAN,
                    "taiKhoan(ma nhan vien)|matKhau|trangThai(HOAT_DONG/KHOA/CHO_DUYET)|phaiDoiMatKhau(true/false)|soLanSai",
                    dong);
            GiaoDien.in("Da ghi " + soLuong + " tai khoan vao file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi ghi file tai khoan: " + e.getMessage());
        }
    }
}
