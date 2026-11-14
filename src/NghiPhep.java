import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NghiPhep implements Serializable {
    private String maNP;
    private String maNV;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String lyDo;
    private String loaiNghi;
    private String trangThai;

    public NghiPhep(String maNP, String maNV, LocalDate ngayBatDau, LocalDate ngayKetThuc, String lyDo) {
        this.maNP = maNP;
        this.maNV = maNV;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.lyDo = lyDo;
        this.trangThai = "Cho duyet";
        this.loaiNghi = "Co phep";
    }

    public String getMaNP() { return maNP; }
    public String getMaNV() { return maNV; }
    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public String getLyDo() { return lyDo; }
    public String getLoaiNghi() { return loaiNghi; }
    public void setLoaiNghi(String loaiNghi) { this.loaiNghi = loaiNghi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public void duyetNghiPhep() {
        this.trangThai = "Da duyet";
    }

    public int tinhSoNgayNghi() {
        if (ngayBatDau != null && ngayKetThuc != null) {
            return (int) ChronoUnit.DAYS.between(ngayBatDau, ngayKetThuc) + 1;
        }
        return 0;
    }
}