import java.io.Serializable;
import java.util.Scanner;

public class TrinhDoChuyenMon implements Serializable{
//----thuoc tinh----
    private String maTD;
    private String tenTD;
    private String chuyenNganh;
    private String truongDaoTao;
    private int namTotNghiep;
    private double heSoTD;

//----constructor----
    public TrinhDoChuyenMon(){
        this.maTD = "";
        this.tenTD = "";
        this.chuyenNganh = "";
        this.truongDaoTao = "";
        this.namTotNghiep = 0;
        this.heSoTD = 0;
    }

    public TrinhDoChuyenMon(String maTD, String tenTD, String chuyenNganh, String truongDaoTao, int namTotNghiep, double heSoTD){
        this.maTD = maTD;
        this.tenTD = tenTD;
        this.chuyenNganh = chuyenNganh;
        this.truongDaoTao = truongDaoTao;
        this.namTotNghiep = namTotNghiep;
        this.heSoTD = heSoTD;
    }

//----methods----
//---getter, setter---
    public String getMaTD(){
        return maTD;
    }

    public void setMaTD(String maTD){
        this.maTD = maTD;
    }

    public String getTenTD(){
        return tenTD;
    }

    public void setTenTD(String tenTD){
        this.tenTD = tenTD;
    }

    public String getChuyenNganh(){
        return chuyenNganh;
    }

    public void setChuyenNganh(String chuyenNganh){
        this.chuyenNganh = chuyenNganh;
    }

    public String getTruongDaoTao(){
        return truongDaoTao;
    }
    
    public void setTruongDaoTao(String truongDaoTao){
        this.truongDaoTao = truongDaoTao;
    }

    public int getNamTotNghiep(){
        return namTotNghiep;
    }

    public void setNamTotNghiep(int namTotNghiep){
        this.namTotNghiep = namTotNghiep;
    }

    public double getHeSoTD(){
        return heSoTD;
    }

    public void setHeSoTD(double heSoTD){
        this.heSoTD = heSoTD;
    }

//---tinhPhuCapTrinhDo---
    public double tinhPhuCapTrinhDo(double luongCoBan){
        return heSoTD * luongCoBan;
    }

//---hienThiThongTin---
    public void hienThiThongTin(){
        System.out.println("maTD cua nhan vien: " + maTD);
        System.out.println("tenTD cua nhan vien: " + tenTD);
        System.out.println("chuyenNganh cua nhan vien: " + chuyenNganh);
        System.out.println("truongDaoTao cua nhan vien:" + truongDaoTao);
        System.out.println("namTotNghiep cua nhan vien: " + namTotNghiep);
        System.out.println("heSoTrinhDo cau nhan vien: " + heSoTD);
    }

//---nhapThongTinTrinhDo---(bo sung them trong so do)
//---viec nhap trinh do cua cua class TrinhDo, viec them trinh do se la cua NhanVien(Trinh Do la thuoc tinh cua NhanVien)
    public void nhapThongTinTD(Scanner sc){
        System.out.println("nhap vao ma trinh do cua nhan vien:");
        setMaTD(sc.nextLine().toUpperCase());
        System.out.println("nhap vao ten trinh do cua nhan vien:");
        setTenTD(sc.nextLine().toUpperCase());
        System.out.println("nhap vao chuyen nganh cua nhan vien:");
        setChuyenNganh(sc.nextLine().toUpperCase());
        System.out.println("nhap vao truong dao tao cua nhan vien:");
        setTruongDaoTao(sc.nextLine().toUpperCase());
        System.out.println("nhap vao nam tot nghiep cua nhan vien:");
        setNamTotNghiep(Integer.parseInt(sc.nextLine()));
        System.out.println("nhap vao he so trinh do cua nhan vien:");
        setHeSoTD(Double.parseDouble(sc.nextLine()));
    }
}
