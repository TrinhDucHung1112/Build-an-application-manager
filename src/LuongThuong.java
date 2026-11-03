//mot dong thuong/phat chi tiet trong BangLuong
public class LuongThuong {
//----thuoc tinh----
    private String maLT;
    private String loai;           //THUONG hoac PHAT
    private double soTien;
    private String lyDo;

//----constructor----
    public LuongThuong(String maLT, String loai, double soTien) {
        this.maLT = maLT;
        this.loai = loai;
        this.soTien = soTien;
        this.lyDo = "";
    }

    public LuongThuong(String maLT, String loai, double soTien, String lyDo) {
        this.maLT = maLT;
        this.loai = loai;
        this.soTien = soTien;
        this.lyDo = lyDo;
    }

//----methods----
    //la thuong thi tra ve so tien, nguoc lai tra ve 0
    public double tinhThuong() {
        return QuyDinhThuongPhat.THUONG.equalsIgnoreCase(loai) ? soTien : 0;
    }

    //la phat thi tra ve so tien, nguoc lai tra ve 0
    public double tinhPhat() {
        return QuyDinhThuongPhat.PHAT.equalsIgnoreCase(loai) ? soTien : 0;
    }

//---hienthi---
    public void hienThi() {
        System.out.println("   + " + loai + ": " + lyDo + " = " + String.format("%,.0f", soTien));
    }

//---setter, getter---
    public String getMaLT() { return maLT; }
    public String getLoai() { return loai; }
    public double getSoTien() { return soTien; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
