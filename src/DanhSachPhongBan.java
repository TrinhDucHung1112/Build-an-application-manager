import java.util.Scanner;

public class DanhSachPhongBan {
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
        return danhSach;
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
            System.out.println("danh sach da day khong the them");
            return false;
        }
        if(timViTri(pb.getMaPB()) >= 0){
            System.out.println("phong nay da co, trung ma phong ban");
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
    public void xuatDanhSach(){
        if(soLuong == 0){
            System.out.println("danh sach rong");
            return;
        }

        for(int i = 0; i < soLuong; i++){
            System.out.println("THONG TIN PHONG BAN: " + i);
            danhSach[i].hienThiThongTin();
        }
    }

//---xoadanhsachtheoma---
     public boolean xoaTheoMa(String maPB){
        if(timViTri(maPB) == -1){
            System.out.println("ma khong ton tai hoac khong tim thay vi tri ma");
            return false;
        }
        int idx = timViTri(maPB);
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
}
