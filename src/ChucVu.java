
import java.io.Serializable;
import java.util.Scanner;

public class ChucVu implements Serializable{
    private String maCV;
    private String tenCV;
    private double heSoLuong;

    //constructor
    public ChucVu(){
    }
    public ChucVu(String maCV, String tenCV, double heSoLuong){
        setMaCV(maCV);
        setTenCV(tenCV);
        setHeSoLuong(heSoLuong);
    }

    // Setter
    public void setMaCV(String maCV){
        if (maCV == null || maCV.isBlank()){
            throw new IllegalArgumentException("Ma chuc vu khong duoc de trong");
        }
        this.maCV = maCV.trim().toUpperCase();
    }
    public void setTenCV(String tenCV){
        if (tenCV == null || tenCV.isBlank()){
            throw new IllegalArgumentException("Ten chuc vu khong duoc de trong");
        }
        this.tenCV = tenCV.trim().toUpperCase();
    }
    public void setHeSoLuong(double heSoLuong){
        if (!Double.isFinite(heSoLuong) || heSoLuong < 0){
            throw new IllegalArgumentException("He so luong khong hop le");
        }
        this.heSoLuong = heSoLuong;
    }

    // Getter
    public String getMaCV(){
        return this.maCV;
    }
    public String getTenCV(){
        return this.tenCV;
    }
    public double getHeSoLuong(){
        return this.heSoLuong;
    }

    public double tinhPhuCapChucVu(double luongCoBan) {
        if (!Double.isFinite(luongCoBan) || luongCoBan < 0)
            throw new IllegalArgumentException("Luong co ban khong hop le");
        return luongCoBan * heSoLuong;
    }

    // Nhap chuc vu
    public void nhap(Scanner sc){
        System.out.print("Nhap ma chuc vu: ");
        setMaCV(sc.nextLine());
        System.out.print("Nhap ten chuc vu: ");
        setTenCV(sc.nextLine());
        System.out.print("Nhap he so luong: ");
        setHeSoLuong(Double.parseDouble(sc.nextLine()));
    }

    // Cap nhat chuc vu
    public void capNhatChucVu(Scanner sc){
        System.out.print("Nhap ten chuc vu moi: ");
        setTenCV(sc.nextLine());

        System.out.print("Nhap he so luong moi: ");
        setHeSoLuong(Double.parseDouble(sc.nextLine()));
    }
    public void xuat(){
        System.out.println("Ma chuc vu: " + maCV);
        System.out.println("Ten chuc vu: " + tenCV);
        System.out.println("He so luong: " + heSoLuong);
    }
}
