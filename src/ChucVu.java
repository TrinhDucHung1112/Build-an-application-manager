import java.io.Serializable;
import java.util.Scanner;

public class ChucVu implements Serializable{        //implements interface co san cua java Serializable de co the ghi ca du lieu doi tuong PhongBan vao file
//----thuoc tinh----
    private String maCV;
    private String tenCV;
    private double heSoLuong;

//----constructor----
    public ChucVu(){
        this.maCV = "";
        this.tenCV = "";
        this.heSoLuong = 0;
    }

    public ChucVu(String maCV, String tenCV, double heSoLuong){
        this.maCV = maCV;
        this.tenCV = tenCV;
        this.heSoLuong = heSoLuong;
    }

//----methods----
//---getter, setter---
    public String getMaCV(){
        return maCV;
    }

    public void setMaCV(String maCV){
        this.maCV = maCV;
    }

    public String getTenCV(){
        return tenCV;
    }

    public void setTenCV(String tenCV){
        this.tenCV = tenCV;
    }

    public double getHeSoLuong(){
        return heSoLuong;
    }

    public void setHeSoLuong(double heSoLuong){
        this.heSoLuong = heSoLuong;
    }

//---capnhatchucvu---
    public void capNhatChucVu(Scanner sc){
        System.out.println("nhap vao ma chuc vu (muon cap nhat)");
        String ma = sc.nextLine().toUpperCase();
        setMaCV(ma);
        System.out.println("nhap vao ten chuc vu (muon cap nhat)");
        String ten = sc.nextLine().toUpperCase();
        setTenCV(ten);
        System.out.println("nhap vao he so luong (cua chuc vu sau cap nhat)");
        double heso = Double.parseDouble(sc.nextLine());
        setHeSoLuong(heso);
        return;
    }

//---tinhphucapchucvu---
    public double tinhPhuCapChucVu(double luongCoBan){      //truyen luong co ban cua doi tuong vao (2 doi tuong luong khac nhau)
        return heSoLuong * luongCoBan;
    }

//---nhapchucvu---
    public void nhap(Scanner sc){
        System.out.println("nhap vao ma chuc vu:");
        String ma = sc.nextLine().toUpperCase();
        setMaCV(ma);
        System.out.println("nhap vao ten chuc vu:");
        String ten = sc.nextLine().toUpperCase();
        setTenCV(ten);
        System.out.println("nhap vao he so luong:");
        double heso = Double.parseDouble(sc.nextLine());
        setHeSoLuong(heso);
        return;
    }
}
