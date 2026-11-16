
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

    /** 4 chuc vu co san cua cong ty: 1 Giam doc, 2 Truong phong, 3 Pho phong, 4 Nhan vien. */
    public static ChucVu macDinh(int muc){
        switch (muc){
            case 1: return new ChucVu("CV01", "GIAM DOC", 0.5);
            case 2: return new ChucVu("CV02", "TRUONG PHONG", 0.3);
            case 3: return new ChucVu("CV03", "PHO PHONG", 0.2);
            default: return new ChucVu("CV04", "NHAN VIEN", 0);
        }
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
