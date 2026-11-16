import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Danh sach tai khoan dang nhap, doc/ghi file data/taikhoan.txt (moi dong: taiKhoan|matKhau).
 */
public class DanhSachTaiKhoan implements IQuanLyDuLieu {
//----thuoc tinh----
    private static final String DUONG_DAN = DuongDanDuLieu.file("taikhoan.txt");

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
        if (soLuong >= danhSach.length) danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
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

    /** Tra ve tai khoan neu dung ten + mat khau, nguoc lai tra ve null. */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = timTheoTen(tenDangNhap);
        if (tk != null && tk.kiemTraMatKhau(matKhau)) return tk;
        return null;
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
                    them(new TaiKhoan(p[0], p[1]));
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
            dong.add(TepVanBan.ghep(danhSach[i].getTenDangNhap(), danhSach[i].getMatKhau()));
        }
        try {
            TepVanBan.ghiDong(DUONG_DAN, "taiKhoan(ma nhan vien)|matKhau(so thu tu + tien to ma)", dong);
            GiaoDien.in("Da ghi " + soLuong + " tai khoan vao file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi ghi file tai khoan: " + e.getMessage());
        }
    }
}
