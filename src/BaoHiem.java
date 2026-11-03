import java.time.LocalDate;

public abstract class BaoHiem {
    private String maBH;
    private String maNV;
    private String soSo;
    private LocalDate ngayThamGia;
    private String trangThai;

    public BaoHiem() {
        this.maBH = "";
        this.maNV = "";
        this.soSo = "";
        this.ngayThamGia = LocalDate.now();
        this.trangThai = "HieuLuc";
    }

    public BaoHiem(String maBH, String maNV, String soSo, LocalDate ngayThamGia) {
        this.maBH = maBH;
        this.maNV = maNV;
        this.soSo = soSo;
        this.ngayThamGia = ngayThamGia;
        this.trangThai = "HieuLuc";
    }

    public String getMaBH() { return maBH; }
    public void setMaBH(String maBH) { this.maBH = maBH; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getSoSo() { return soSo; }
    public void setSoSo(String soSo) { this.soSo = soSo; }

    public LocalDate getNgayThamGia() { return ngayThamGia; }
    public void setNgayThamGia(LocalDate ngayThamGia) { this.ngayThamGia = ngayThamGia; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    // Hai phương thức trừu tượng: mỗi loại bảo hiểm tự tính theo tỷ lệ riêng (đa hình)
    public abstract double tinhTienNhanVienDong(double luongDongBH);
    public abstract double tinhTienDoanhNghiepDong(double luongDongBH);

    // In 5 cột chung, lớp con in tiếp phần còn lại và xuống dòng
    public void hienThiThongTin() {
        System.out.printf("| %-8s | %-8s | %-12s | %-13s | %-10s ",
                maBH, maNV, soSo, ngayThamGia, trangThai);
    }
}
