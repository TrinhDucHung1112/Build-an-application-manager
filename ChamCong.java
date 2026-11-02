import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ChamCong {
    private String maChamCong;
    private String maNV;
    private LocalDate ngay;
    private LocalTime gioVao;
    private LocalTime gioRa;
    private double soGioLam;
    private double gioTangCa;
    private String trangThai;

    public ChamCong() {}

    public ChamCong(String maChamCong, String maNV, LocalDate ngay, LocalTime gioVao, LocalTime gioRa) {
        this.maChamCong = maChamCong;
        this.maNV = maNV;
        this.ngay = ngay;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
    }

    public void ghiNhan() {
        this.trangThai = "Da ghi nhan";
    }

    public double tinhGioLam() {
        if (gioVao != null && gioRa != null) {
            long soPhut = ChronoUnit.MINUTES.between(gioVao, gioRa);
            this.soGioLam = soPhut / 60.0;
        }
        return this.soGioLam;
    }

    public double tinhGioTangCa() {
        if (this.soGioLam > 8.0) {
            this.gioTangCa = this.soGioLam - 8.0;
            this.soGioLam = 8.0;
        } else {
            this.gioTangCa = 0;
        }
        return this.gioTangCa;
    }

    //Getters
    public String getMaChamCong() { return maChamCong; }
    public String getMaNV() { return maNV; }
    public LocalDate getNgay() { return ngay; }
}
