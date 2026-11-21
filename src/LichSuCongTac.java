import java.io.Serializable;

/**
 * 1 dong lich su cong tac cua 1 nhan vien trong 1 NAM: phong ban, chuc vu, luong co ban (CLASS MOI).
 * Dung cho nhom chuc nang "Chung" (xem ho so / phong ban qua cac nam).
 * Nam hien tai luon lay theo du lieu dang co cua NhanVien, cac nam truoc lay tu day.
 */
public class LichSuCongTac implements Serializable {
    private String maNV;
    private int nam;
    private String maPB;            //rong neu chua phan phong ban
    private String maCV;
    private String tenCV;
    private double heSoCV;
    private double luongCoBan;
    private String ghiChu;

    public LichSuCongTac(String maNV, int nam, String maPB, String maCV, String tenCV,
                         double heSoCV, double luongCoBan, String ghiChu) {
        this.maNV = maNV;
        this.nam = nam;
        this.maPB = maPB == null ? "" : maPB;
        this.maCV = maCV == null ? "" : maCV;
        this.tenCV = tenCV == null ? "" : tenCV;
        this.heSoCV = heSoCV;
        this.luongCoBan = luongCoBan;
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public LichSuCongTac sao() {
        return new LichSuCongTac(maNV, nam, maPB, maCV, tenCV, heSoCV, luongCoBan, ghiChu);
    }

    public String getMaNV() { return maNV; }
    public int getNam() { return nam; }
    public String getMaPB() { return maPB; }
    public String getMaCV() { return maCV; }
    public String getTenCV() { return tenCV; }
    public double getHeSoCV() { return heSoCV; }
    public double getLuongCoBan() { return luongCoBan; }
    public String getGhiChu() { return ghiChu; }

    public void setNam(int nam) { this.nam = nam; }
    public void setMaPB(String maPB) { this.maPB = maPB == null ? "" : maPB; }
    public void setChucVu(ChucVu cv) {
        this.maCV = cv.getMaCV();
        this.tenCV = cv.getTenCV();
        this.heSoCV = cv.getHeSoLuong();
    }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu == null ? "" : ghiChu; }
}
