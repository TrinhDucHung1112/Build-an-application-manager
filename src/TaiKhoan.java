import java.io.Serializable;

/**
 * Tai khoan dang nhap cua 1 nhan vien.
 *  - tenDangNhap     : chinh la ma nhan vien (VD: TP001)
 *  - matKhau         : mac dinh la phan so + phan chu cua ma, VD: TP001 -> 001TP (xem MaNhanVien.matKhauMacDinh)
 *  - trangThai       : HOAT_DONG | KHOA | CHO_DUYET (tai khoan tu dang ky phai cho giam doc duyet)
 *  - phaiDoiMatKhau  : true neu dang dung mat khau mac dinh -> bat buoc doi o lan dang nhap ke tiep
 *  - soLanSai        : so lan nhap sai mat khau lien tiep; sai SO_LAN_SAI_TOI_DA lan thi tai khoan bi KHOA
 */
public class TaiKhoan implements Serializable {
    public static final String HOAT_DONG = "HOAT_DONG";
    public static final String KHOA = "KHOA";
    public static final String CHO_DUYET = "CHO_DUYET";
    public static final int SO_LAN_SAI_TOI_DA = 5;

    private String tenDangNhap;
    private String matKhau;
    private String trangThai;
    private boolean phaiDoiMatKhau;
    private int soLanSai;

    public TaiKhoan() {
        this.tenDangNhap = "";
        this.matKhau = "";
        this.trangThai = HOAT_DONG;
        this.phaiDoiMatKhau = false;
        this.soLanSai = 0;
    }

    public TaiKhoan(String tenDangNhap, String matKhau) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        this.trangThai = HOAT_DONG;
        this.soLanSai = 0;
        //dang dung mat khau mac dinh thi phai doi o lan dang nhap dau
        this.phaiDoiMatKhau = this.matKhau.equals(MaNhanVien.matKhauMacDinh(this.tenDangNhap));
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String trangThai, boolean phaiDoiMatKhau, int soLanSai) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setTrangThai(trangThai);
        this.phaiDoiMatKhau = phaiDoiMatKhau;
        setSoLanSai(soLanSai);
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

    public void setTrangThai(String trangThai) {
        if (!hopLe(trangThai)) {
            throw new IllegalArgumentException("Trang thai tai khoan phai la HOAT_DONG, KHOA hoac CHO_DUYET");
        }
        this.trangThai = trangThai.trim().toUpperCase();
    }

    public void setPhaiDoiMatKhau(boolean phaiDoiMatKhau) {
        this.phaiDoiMatKhau = phaiDoiMatKhau;
    }

    public void setSoLanSai(int soLanSai) {
        if (soLanSai < 0) throw new IllegalArgumentException("So lan sai khong duoc am");
        this.soLanSai = soLanSai;
    }

    public static boolean hopLe(String trangThai) {
        if (trangThai == null) return false;
        String t = trangThai.trim().toUpperCase();
        return t.equals(HOAT_DONG) || t.equals(KHOA) || t.equals(CHO_DUYET);
    }

    // Getter
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public boolean isPhaiDoiMatKhau() {
        return phaiDoiMatKhau;
    }

    public int getSoLanSai() {
        return soLanSai;
    }

    public boolean dangHoatDong() {
        return HOAT_DONG.equals(trangThai);
    }

    public boolean daKhoa() {
        return KHOA.equals(trangThai);
    }

    public boolean choDuyet() {
        return CHO_DUYET.equals(trangThai);
    }

    /** Tang so lan nhap sai, tra ve so lan sai hien tai. */
    public int tangSoLanSai() {
        return ++soLanSai;
    }

    public void datLaiSoLanSai() {
        soLanSai = 0;
    }

    /** Mat khau phan biet chu hoa/thuong. */
    public boolean kiemTraMatKhau(String matKhauNhap) {
        return matKhauNhap != null && matKhau.equals(matKhauNhap.trim());
    }
}
