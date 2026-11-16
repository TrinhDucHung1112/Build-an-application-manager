import java.io.Serializable;

/**
 * Tai khoan dang nhap cua 1 nhan vien.
 *  - tenDangNhap : chinh la ma nhan vien (VD: TP001)
 *  - matKhau     : mac dinh la phan so + phan chu cua ma, VD: TP001 -> 001TP (xem MaNhanVien.matKhauMacDinh)
 */
public class TaiKhoan implements Serializable {
    private String tenDangNhap;
    private String matKhau;

    public TaiKhoan() {
        this.tenDangNhap = "";
        this.matKhau = "";
    }

    public TaiKhoan(String tenDangNhap, String matKhau) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
    }

    // Setter
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            throw new IllegalArgumentException("Ten dang nhap khong duoc de trong");
        }
        this.tenDangNhap = tenDangNhap.trim().toUpperCase();
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.isBlank()) {
            throw new IllegalArgumentException("Mat khau khong duoc de trong");
        }
        this.matKhau = matKhau.trim();
    }

    // Getter
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    /** Mat khau phan biet chu hoa/thuong. */
    public boolean kiemTraMatKhau(String matKhauNhap) {
        return matKhauNhap != null && matKhau.equals(matKhauNhap.trim());
    }
}
