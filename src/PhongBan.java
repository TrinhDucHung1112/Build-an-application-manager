import java.io.Serializable;
import java.util.Scanner;

public class PhongBan implements Serializable {
    private String maPB;
    private String tenPB;
    private String moTa;
    private int soNhanVien;

    // Constructor
    public PhongBan(){
        this.maPB = "";
        this.tenPB = "";
        this.moTa = "";
        this.soNhanVien = 0;
    }
    public PhongBan(String maPB, String tenPB, String moTa){
        setMaPB(maPB);
        setTenPB(tenPB);
        setMoTa(moTa);
        this.soNhanVien = 0;
    }

    // Getter, setter
    public String getMaPB(){
        return maPB;
    }
    public void setMaPB(String maPB){
        if (maPB == null || maPB.isBlank()){
            throw new IllegalArgumentException("Ma phong ban khong duoc de trong");
        }
        this.maPB = maPB.trim().toUpperCase();
    }

    public String getTenPB(){
        return tenPB;
    }
    public void setTenPB(String tenPB){
        if (tenPB == null || tenPB.isBlank()){
            throw new IllegalArgumentException("Ten phong ban khong duoc de trong");
        }
        this.tenPB = tenPB.trim().toUpperCase();
    }

    public String getMoTa(){
        return moTa;
    }
    public void setMoTa(String moTa){
        this.moTa = (moTa == null) ? "" : moTa.trim();
    }

    // Hiển thị thông tin
    public void hienThiThongTin(){
        System.out.println("Ma phong ban: " + maPB);
        System.out.println("Ten phong ban: " + tenPB);
        System.out.println("Mo ta phong ban: " + moTa);
    }

    // Nhập thông tin phòng ban
    public void nhapPhongBan(Scanner sc){
        System.out.println("Nhap vao ma cua phong ban:");
        setMaPB(sc.nextLine());
        System.out.println("Nhap vao ten phong ban:");
        setTenPB(sc.nextLine());
        System.out.println("Nhap vao mo ta cua phong ban:");
        setMoTa(sc.nextLine());
    }

    // Đếm số nhân viên thuộc phòng ban
    public int demNhanVien(){
        return soNhanVien;
    }
    public void giamSoNhanVien(){
        if (soNhanVien > 0) {
            soNhanVien--;
        }
    }
    public void tangSoNhanVien(){
        soNhanVien++;
    }
}