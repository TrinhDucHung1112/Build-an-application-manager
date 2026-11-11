import java.io.Serializable;
import java.time.LocalDate;

public class ThuongPhat implements Serializable {
//----thuoc tinh----
    private String maTP;
    private String maNV;
    private String maQD;
    private String loai;           //THUONG hoac PHAT
    private String lyDo;
    private double soLuong;
    private double soTien;
    private LocalDate ngayApDung;

//----constructor----
    public ThuongPhat(){
        this.maTP = "";
        this.maNV = "";
        this.maQD = "";
        this.loai = QuyDinhThuongPhat.THUONG;
        this.lyDo = "";
        this.soLuong = 0;
        this.soTien = 0;
        this.ngayApDung = LocalDate.now();
    }

    public ThuongPhat(String maTP, String maNV, String maQD, String loai, String lyDo,
                      double soLuong, double soTien, LocalDate ngayApDung){
        this.maTP = maTP;
        this.maNV = maNV;
        this.maQD = maQD;
        this.loai = loai;
        this.lyDo = lyDo;
        this.soLuong = soLuong;
        this.soTien = soTien;
        this.ngayApDung = ngayApDung;
    }

    //tao tu quy dinh: loai, ly do, so tien lay tu quy dinh
    public ThuongPhat(String maTP, String maNV, QuyDinhThuongPhat qd, double soLuong, LocalDate ngayApDung){
        this.maTP = maTP;
        this.maNV = maNV;
        this.maQD = qd.getMaQD();
        this.loai = qd.getLoai();
        this.lyDo = qd.getTenQD();
        this.soLuong = soLuong;
        this.soTien = qd.tinhTien(soLuong);
        this.ngayApDung = ngayApDung;
    }

//----methods----
    //so tien luon duong, dua vao loai de cong hay tru
    public double tinhSoTien(){
        return soTien;
    }

//---hienthi---
    public void hienThiThongTin(){
        System.out.println(maTP + " | NV " + maNV + " | " + loai + " | " + lyDo
                + " | SL " + soLuong + " | " + String.format("%,.0f", soTien) + " | " + ngayApDung);
    }

//---setter, getter---
    public String getMaTP(){ return maTP; }
    public void setMaTP(String maTP) { this.maTP = maTP; }
    public String getMaNV(){ return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getMaQD(){ return maQD; }
    public void setMaQD(String maQD) { this.maQD = maQD; }
    public String getLoai(){ return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public String getLyDo(){ return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public double getSoLuong(){ return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }
    public double getSoTien(){ return soTien; }
    public void setSoTien(double soTien) { this.soTien = soTien; }
    public LocalDate getNgayApDung(){ return ngayApDung; }
    public void setNgayApDung(LocalDate ngayApDung){ this.ngayApDung = ngayApDung; }
}
