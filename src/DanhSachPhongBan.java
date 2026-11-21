import java.util.Scanner;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DanhSachPhongBan implements IQuanLyDuLieu {
//-----thuoc tinh-----
    private PhongBan[] danhSach;
    private int soLuong;

//----constructor----
    public DanhSachPhongBan(int succhua){
        this.danhSach = new PhongBan[succhua];
        this.soLuong = 0;
    }

//----methods----
//---getter---(o day khong can setter vi se khien du lieu danh sach khong nhat quan)
    public PhongBan[] getDanhSach(){
        PhongBan[] kq = new PhongBan[soLuong];
        System.arraycopy(danhSach, 0, kq, 0, soLuong);
        return kq;
    }

    public int getSoLuong(){
        return soLuong;
    }

//---nhapdanhsach---
    public void nhapDanhSach(){    //voi n la so luong phong muon nhap
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int phongTrong = danhSach.length - soLuong;

        while(n > phongTrong){
                System.out.println("so luong vuot qua so phong con trong, vui long giam so phong");
                n = Integer.parseInt(sc.nextLine());
        }

        int danhap = 0;
        while(danhap < n){
            System.out.println("phong ban: " + (danhap+1));
            PhongBan pb = new PhongBan();
            pb.nhapPhongBan(sc);
            if(themPhong(pb))
                danhap++;
            else
                System.out.println("vui long nhap lai");
        }
    }

    public boolean themPhong(PhongBan pb){
        if(pb == null) 
            return false;
        if(soLuong >= danhSach.length){
            danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
        }
        if(timViTri(pb.getMaPB()) >= 0){
            GiaoDien.in("Phong nay da co, trung ma phong ban.");
            return false;
        }
        danhSach[soLuong++] = pb;
        return true;
    }

    public int timViTri(String maPB){
        if(maPB == null) return -1;
        maPB = maPB.toUpperCase();
        for(int i = 0; i < soLuong; i++){
            if(danhSach[i].getMaPB().equalsIgnoreCase(maPB))
                return i;
        }
        return -1;
    }

    // public boolean themPhong(PhongBan pb){
    //     if(pb == null) 
    //         return false;
    //     if(soLuong >= danhSach.length){
    //         System.out.println("danh sach da day khong the them");
    //         return false;
    //     }
    //     for(int i = 0; i < soLuong; i++){
    //         if(danhSach[i].getMaPB().toUpperCase().contains(pb.getMaPB())){
    //             System.out.println("phong nay da ton tai, trung maPB");
    //             return false;
    //         }
    //     }
    //     danhSach[soLuong++] = pb;
    //     return true;
    // }

//---xuatdanhsach---
    //in danh sach phong ban dang bang co STT (dung chung cho xem danh sach, tim kiem...)
    public static void inBang(PhongBan[] ds){
        List<String[]> dong = new ArrayList<>();
        for(PhongBan pb : ds){
            if(pb == null) continue;
            dong.add(new String[]{pb.getMaPB(), pb.getTenPB(), pb.getMoTa(), String.valueOf(pb.demNhanVien())});
        }
        GiaoDien.inBang(
            new String[]{"MA PB", "TEN PHONG BAN", "MO TA", "SO NV"},
            new int[]{8, 22, 50, 6},
            new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI},
            dong);
    }

    public void xuatDanhSach(){
        if(soLuong == 0){
            GiaoDien.in("Danh sach phong ban rong.");
            return;
        }
        inBang(getDanhSach());
        GiaoDien.in("Tong so phong ban: " + soLuong);
    }

//---xoadanhsachtheoma---
     public boolean xoaTheoMa(String maPB){
        if(timViTri(maPB) == -1){
            GiaoDien.in("Ma phong ban khong ton tai.");
            return false;
        }
        int idx = timViTri(maPB);
        if(danhSach[idx].demNhanVien() > 0){
            GiaoDien.in("Phong ban con " + danhSach[idx].demNhanVien() + " nhan vien, hay chuyen nhan vien sang phong ban khac truoc khi xoa.");
            return false;
        }
        for(int i = idx; i < soLuong - 1; i++){
            danhSach[i] = danhSach[i+1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        return true;
     }

//---suadanhsachtheoma---
     public boolean suaTheoMa(String maPB){
        Scanner sc = new Scanner(System.in);
        if(timViTri(maPB) == -1){
            System.out.println("ma khong ton tai hoac khong tim thay vi tri ma");
            return false;
        }
        int idx = timViTri(maPB);
        System.out.println("nhap vao maPB (muon doi)");
        String ma = sc.nextLine().toUpperCase();
        System.out.println("nhap vao tenPB (muon doi)");
        String ten = sc.nextLine().toUpperCase();
        System.out.println("nhap vao mo ta (muon doi)");
        String mota = sc.nextLine().toUpperCase();
        danhSach[idx].setMaPB(ma);
        danhSach[idx].setTenPB(ten);
        danhSach[idx].setMoTa(mota);
        return true;
     }

//---timkiemPhongBan---
     public PhongBan[] timKiem(String tuKhoa){
        if(tuKhoa == null) return new PhongBan[0]; //tra ve danh sach rong(nen luc hien thi se khong in ra cai gi)
        tuKhoa = tuKhoa.toUpperCase();
        int count = 0;
        for(int i = 0; i < soLuong; i++){
            if(danhSach[i].getMaPB().contains(tuKhoa) || danhSach[i].getTenPB().contains(tuKhoa)){
                count++;
            }
        }
        if(count == 0){
            System.out.println("khong tim thay");
            return new PhongBan[0];
        }
        PhongBan[] ketQua = new PhongBan[count];
        int idx = 0;
        for(int i = 0; i < soLuong; i++){
            if(danhSach[i].getMaPB().contains(tuKhoa) || danhSach[i].getTenPB().contains(tuKhoa))
                ketQua[idx++] = danhSach[i];
        }
        return ketQua;
     }

//---timtheoma---
      public PhongBan timTheoMa(String maPB){
        if(maPB == null) return null;
        int vt = timViTri(maPB);
        if(vt == -1) return null;
        return danhSach[vt];
     }

    private static final String FILE_TEXT = DuongDanDuLieu.file("phongban.txt");

    // Dinh dang moi dong: maPB|tenPB|moTa|maGiamDocQuanLy (cot cuoi co the bo trong)
    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            PhongBan pb = danhSach[i];
            dong.add(TepVanBan.ghep(pb.getMaPB(), pb.getTenPB(), pb.getMoTa(), pb.getMaGiamDoc()));
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "maPB|tenPB|moTa|maGiamDocQuanLy (de trong = moi giam doc deu quan ly)", dong);
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu phong ban: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            java.util.List<String[]> cacDong = TepVanBan.docDong(FILE_TEXT);
            danhSach = new PhongBan[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 3) continue;
                try {
                    themPhong(new PhongBan(p[0], p[1], p[2], p.length > 3 ? p[3] : ""));
                } catch (IllegalArgumentException e) {
                    GiaoDien.in("Bo qua dong phong ban loi: " + e.getMessage());
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc phong ban: " + e.getMessage());
        }
    }

}
