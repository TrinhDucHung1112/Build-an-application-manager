public class QuyDinhThuongPhat implements java.io.Serializable {
//----hang so----
    public static final String THUONG = "THUONG";
    public static final String PHAT = "PHAT";
    //3 hang so duoi dung cho donVi
    public static final String GIO_TANG_CA = "GIO_TANG_CA";
    public static final String NGAY_DI_LAM = "NGAY_DI_LAM";
    public static final String NGAY_NGHI_KHONG_PHEP = "NGAY_NGHI_KHONG_PHEP";

//----thuoc tinh----
    private String maQD;
    private String tenQD;
    private String loai;           //THUONG hoac PHAT
    private String dieuKien;       //>=, >, <=, <, ==
    private double nguong;
    private double mucTien;        //tien tren moi don vi
    private String donVi;          //chi tieu can xet

//----constructor----
    public QuyDinhThuongPhat() {
        this.maQD = "";
        this.tenQD = "";
        this.loai = THUONG;
        this.dieuKien = ">=";
        this.nguong = 0;
        this.mucTien = 0;
        this.donVi = "";
    }

    public QuyDinhThuongPhat(String maQD, String tenQD, String loai, String dieuKien,
                             double nguong, double mucTien, String donVi) {
        this.maQD = maQD;
        this.tenQD = tenQD;
        this.loai = loai;
        this.dieuKien = dieuKien;
        this.nguong = nguong;
        this.mucTien = mucTien;
        this.donVi = donVi;
    }

//----methods----
//---kiem tra, tinh tien---
    //gia tri co thoa dieu kien khong
    public boolean kiemTraDieuKien(double giaTri) {
        switch (dieuKien) {
            case ">=": return giaTri >= nguong;
            case ">":  return giaTri > nguong;
            case "<=": return giaTri <= nguong;
            case "<":  return giaTri < nguong;
            case "==": return giaTri == nguong;
            default:   return false;
        }
    }

    //so tien = so luong * muc tien
    public double tinhTien(double soLuong) {
        if (soLuong <= 0) return 0;
        return soLuong * mucTien;
    }

    public boolean laThuong() {
        return THUONG.equalsIgnoreCase(loai);
    }

//---setter, getter---
    public String getMaQD() { return maQD; }
    public void setMaQD(String maQD) { this.maQD = maQD; }
    public String getTenQD() { return tenQD; }
    public void setTenQD(String tenQD) { this.tenQD = tenQD; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public String getDieuKien() { return dieuKien; }
    public void setDieuKien(String dieuKien) { this.dieuKien = dieuKien; }
    public double getNguong() { return nguong; }
    public void setNguong(double nguong) { this.nguong = nguong; }
    public double getMucTien() { return mucTien; }
    public void setMucTien(double mucTien) { this.mucTien = mucTien; }
    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }
}
