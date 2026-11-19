import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 1 hop dong lao dong cua 1 nhan vien (CLASS MOI): 1 nhan vien co the co nhieu hop dong qua cac nam
 * (thu viec -> co thoi han -> gia han -> khong thoi han).
 * ngayHetHan = null nghia la KHONG THOI HAN.
 */
public class LichSuHopDong implements Serializable {
    private String maHD;
    private String maNV;
    private String loai;
    private LocalDate ngayKy;
    private LocalDate ngayHetHan;
    private String ghiChu;

    public LichSuHopDong(String maHD, String maNV, String loai, LocalDate ngayKy, LocalDate ngayHetHan, String ghiChu) {
        if (maHD == null || maHD.isBlank()) throw new IllegalArgumentException("Ma hop dong khong duoc de trong");
        if (maNV == null || maNV.isBlank()) throw new IllegalArgumentException("Ma nhan vien khong duoc de trong");
        if (loai == null || loai.isBlank()) throw new IllegalArgumentException("Loai hop dong khong duoc de trong");
        if (ngayKy == null) throw new IllegalArgumentException("Ngay ky khong duoc de trong");
        if (ngayHetHan != null && !ngayHetHan.isAfter(ngayKy))
            throw new IllegalArgumentException("Ngay het han phai sau ngay ky");
        this.maHD = maHD.trim().toUpperCase();
        this.maNV = maNV.trim().toUpperCase();
        this.loai = loai.trim();
        this.ngayKy = ngayKy;
        this.ngayHetHan = ngayHetHan;
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public String getMaHD() { return maHD; }
    public String getMaNV() { return maNV; }
    public String getLoai() { return loai; }
    public LocalDate getNgayKy() { return ngayKy; }
    public LocalDate getNgayHetHan() { return ngayHetHan; }
    public String getGhiChu() { return ghiChu; }
    public boolean khongThoiHan() { return ngayHetHan == null; }

    //hop dong co hieu luc trong (it nhat 1 ngay cua) nam nay khong
    public boolean hieuLucTrongNam(int nam) {
        if (ngayKy.getYear() > nam) return false;
        return ngayHetHan == null || ngayHetHan.getYear() >= nam;
    }

    //2 hop dong trung khoang thoi gian khong
    public boolean trungThoiGian(LocalDate ky, LocalDate het) {
        LocalDate cuoiNay = ngayHetHan == null ? LocalDate.MAX : ngayHetHan;
        LocalDate cuoiKia = het == null ? LocalDate.MAX : het;
        return !ky.isAfter(cuoiNay) && !ngayKy.isAfter(cuoiKia);
    }

    public String tinhTrang() {
        if (ngayHetHan == null) return ngayKy.isAfter(LocalDate.now()) ? "Chua hieu luc" : "Khong thoi han";
        LocalDate homNay = LocalDate.now();
        if (ngayKy.isAfter(homNay)) return "Chua hieu luc";
        long con = ChronoUnit.DAYS.between(homNay, ngayHetHan);
        if (con < 0) return "Da het han";
        return "Con hieu luc (" + con + " ngay)";
    }

    //sua cac truong (kiem tra lai quy tac ngay)
    public void sua(String loaiMoi, LocalDate kyMoi, LocalDate hetMoi, String ghiChuMoi) {
        LichSuHopDong thu = new LichSuHopDong(maHD, maNV, loaiMoi, kyMoi, hetMoi, ghiChuMoi);   //nem ngoai le neu sai
        this.loai = thu.loai;
        this.ngayKy = thu.ngayKy;
        this.ngayHetHan = thu.ngayHetHan;
        this.ghiChu = thu.ghiChu;
    }
}
