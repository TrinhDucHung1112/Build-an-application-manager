import java.util.Scanner;

public class DanhSachChamCong {
    private ChamCong[] danhSach;
    private int soLuong;

    public DanhSachChamCong(int sucChua) {
        danhSach = new ChamCong[sucChua];
        soLuong = 0;
    }

    public void nhapDanhSach(int n) {
    }

    public void xuatDanhSach() {
    }

    public boolean them(ChamCong cc) {
        if (soLuong < danhSach.length) {
            danhSach[soLuong] = cc;
            soLuong++;
            return true;
        }
        return false; // Mảng đã đầy
    }

    public boolean xoaTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaChamCong().equals(ma)) {
                // Dồn mảng để xóa phần tử
                for (int j = i; j < soLuong - 1; j++) {
                    danhSach[j] = danhSach[j + 1];
                }
                danhSach[soLuong - 1] = null;
                soLuong--;
                return true;
            }
        }
        return false;
    }

    public double thongKeGioLam(String maNV, int thang) {
        double tongGio = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equals(maNV) && danhSach[i].getNgay().getMonthValue() == thang) {
                tongGio += danhSach[i].tinhGioLam();
            }
        }
        return tongGio;
    }

    public double thongKeGioTangCa(String maNV, int thang) {
        double tongTangCa = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equals(maNV) && danhSach[i].getNgay().getMonthValue() == thang) {
                tongTangCa += danhSach[i].tinhGioTangCa();
            }
        }
        return tongTangCa;
    }

    public int thongKeSoNgayDiLam(String maNV, int thang) {
        int soNgay = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equals(maNV) && danhSach[i].getNgay().getMonthValue() == thang) {
                soNgay++;
            }
        }
        return soNgay;
    }
}