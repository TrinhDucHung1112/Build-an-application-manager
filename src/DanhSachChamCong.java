import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Scanner;

public class DanhSachChamCong implements IQuanLyDuLieu {
    private static final String TEN_FILE = DuongDanDuLieu.file("chamcong.dat");
    private ChamCong[] danhSach;
    private int soLuong;

    public DanhSachChamCong(int sucChua) {
        danhSach = new ChamCong[Math.max(10, sucChua)];
        soLuong = 0;
    }

    private void moRong() { danhSach = Arrays.copyOf(danhSach, danhSach.length * 2); }

    public void nhapDanhSach(int n) {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < n; i++) {
            try {
                System.out.println("Cham cong thu " + (i + 1));
                System.out.print("Ma cham cong: "); String ma = sc.nextLine().trim();
                System.out.print("Ma nhan vien: "); String maNV = sc.nextLine().trim();
                System.out.print("Ngay (yyyy-MM-dd): "); LocalDate ngay = LocalDate.parse(sc.nextLine().trim());
                System.out.print("Gio vao (HH:mm): "); LocalTime vao = LocalTime.parse(sc.nextLine().trim());
                System.out.print("Gio ra (HH:mm): "); LocalTime ra = LocalTime.parse(sc.nextLine().trim());
                if (!them(new ChamCong(ma, maNV, ngay, vao, ra)))
                    System.out.println("Ma cham cong bi trung hoac du lieu khong hop le.");
            } catch (Exception e) { System.out.println("Du lieu khong hop le: " + e.getMessage()); i--; }
        }
    }

    public void xuatDanhSach() {
        if (soLuong == 0) { System.out.println("Danh sach cham cong rong."); return; }
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            System.out.printf("%s | NV: %s | Ngay: %s | Gio lam: %.2f | Tang ca: %.2f%n",
                    cc.getMaChamCong(), cc.getMaNV(), cc.getNgay(), cc.tinhGioLam(), cc.tinhGioTangCa());
        }
    }

    public boolean them(ChamCong cc) {
        if (cc == null || cc.getMaChamCong() == null || cc.getMaNV() == null || cc.getNgay() == null) return false;
        for (int i = 0; i < soLuong; i++)
            if (danhSach[i].getMaChamCong().equalsIgnoreCase(cc.getMaChamCong())) return false;
        if (soLuong == danhSach.length) moRong();
        danhSach[soLuong++] = cc;
        return true;
    }

    public boolean xoaTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) if (danhSach[i].getMaChamCong().equalsIgnoreCase(ma)) {
            System.arraycopy(danhSach, i + 1, danhSach, i, soLuong - i - 1);
            danhSach[--soLuong] = null; return true;
        }
        return false;
    }

    public double thongKeGioLam(String maNV, int thang) {
        return thongKeGioLam(maNV, thang, -1);
    }

    public double thongKeGioLam(String maNV, int thang, int nam) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            if (cc.getMaNV().equalsIgnoreCase(maNV)
                    && cc.getNgay().getMonthValue() == thang
                    && (nam < 0 || cc.getNgay().getYear() == nam))
                tong += Math.min(cc.tinhGioLam(), 8.0);
        }
        return tong;
    }

    public double thongKeGioTangCa(String maNV, int thang) {
        return thongKeGioTangCa(maNV, thang, -1);
    }

    public double thongKeGioTangCa(String maNV, int thang, int nam) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            if (cc.getMaNV().equalsIgnoreCase(maNV)
                    && cc.getNgay().getMonthValue() == thang
                    && (nam < 0 || cc.getNgay().getYear() == nam))
                tong += cc.tinhGioTangCa();
        }
        return tong;
    }

    public int thongKeSoNgayDiLam(String maNV, int thang) {
        return thongKeSoNgayDiLam(maNV, thang, -1);
    }

    public int thongKeSoNgayDiLam(String maNV, int thang, int nam) {
        java.util.Set<LocalDate> ngayDiLam = new java.util.HashSet<>();
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            if (cc.getMaNV().equalsIgnoreCase(maNV)
                    && cc.getNgay().getMonthValue() == thang
                    && (nam < 0 || cc.getNgay().getYear() == nam))
                ngayDiLam.add(cc.getNgay());
        }
        return ngayDiLam.size();
    }

    @Override public void ghiFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TEN_FILE))) {
            oos.writeInt(soLuong);
            for (int i = 0; i < soLuong; i++) oos.writeObject(danhSach[i]);
            System.out.println("Da luu cham cong.");
        } catch (IOException e) { System.out.println("Loi luu cham cong: " + e.getMessage()); }
    }

    @Override public void docFile() {
        File f = new File(TEN_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            int n = ois.readInt(); danhSach = new ChamCong[Math.max(10, n)]; soLuong = 0;
            for (int i = 0; i < n; i++) them((ChamCong) ois.readObject());
        } catch (IOException | ClassNotFoundException e) { System.out.println("Loi doc cham cong: " + e.getMessage()); }
    }
}
