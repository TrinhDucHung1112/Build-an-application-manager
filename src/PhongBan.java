import java.io.Serializable;
import java.util.Scanner;

public class PhongBan implements Serializable{
//----thuoc tinh----
    private String maPB;
    private String tenPB;
    private String moTa;
    private int soNhanVien;
    // private static int soPB = 0;        //thuoc tinh dung de dem so luong phong trong danh sach

//----constructor----
    public PhongBan(){
        this.maPB = "";
        this.tenPB = "";
        this.moTa = "";
        // soPB++;             //moi lan tao phong se tang so luong len 1
    }

    public PhongBan(String maPB, String tenPB, String moTa){
        this.maPB = maPB;
        this.tenPB = tenPB;
        this.moTa = moTa;
        // soPB++;
    }

//----methods----
//---setter, getter---
    public String getMaPB(){
        return maPB;
    }

    public void setMaPB(String maPB){
        this.maPB = maPB.toUpperCase();
    }

    public String getTenPB(){
        return tenPB;
    }

    public void setTenPB(String tenPB){
        this.tenPB = tenPB.toUpperCase();
    }

    public String getMoTa(){
        return moTa;
    }

    public void setMoTa(String moTa){
        this.moTa = moTa;
    }

    // public static int getSoPhong(){      //
    //     return soPB;
    // }

//---hienthi---
    public void hienThiThongTin(){
        System.out.println("Ma phong ban: " + maPB);
        System.out.println("Ten phong ban: " + tenPB);
        System.out.println("Mo ta phong ban: " + moTa);
        //System.out.println("so nhan vien trong phong: ");
    }

//----nhap---
//viec nhap phong ban se o PhongBan, them thi se thuc hien o DanhSachPhongBan
    public void nhapPhongBan(Scanner sc){
        System.out.println("nhap vao ma cua phong ban:");
        setMaPB(sc.nextLine().toUpperCase());
        System.out.println("nhap vao ten phong ban:");
        setTenPB(sc.nextLine().toUpperCase());
        System.out.println("nhap vao mo ta cua phong ban:");
        setMoTa(sc.nextLine().toUpperCase());
    }

//---soluongNVcuaPB---      can thay doi lai so do
    public int demNhanVien(){
        return soNhanVien;
    }

    public void giamSoNhanVien(){
        if(soNhanVien > 0) soNhanVien--;
    }

    public void tangSoNhanVien(){
        soNhanVien++;
    }
}
