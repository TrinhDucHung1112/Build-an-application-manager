import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NghiPhep {
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
    }

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