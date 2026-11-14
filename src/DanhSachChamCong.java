import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Scanner;

public class DanhSachChamCong implements IQuanLyDuLieu {
    private static final String TEN_FILE = DuongDanDuLieu.file("chamcong.txt");
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
        if (soLuong == 0) { GiaoDien.in("Danh sach cham cong rong."); return; }
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            dong.add(new String[]{cc.getMaChamCong(), cc.getMaNV(), String.valueOf(cc.getNgay()),
                    String.valueOf(cc.getGioVao()), String.valueOf(cc.getGioRa()),
                    String.format("%.2f", cc.tinhGioLam()), String.format("%.2f", cc.tinhGioTangCa())});
        }
        GiaoDien.inBang(
                new String[]{"MA CC", "MA NV", "NGAY", "GIO VAO", "GIO RA", "GIO LAM", "TANG CA"},
                new int[]{10, 8, 10, 8, 8, 8, 8},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.GIUA, GiaoDien.GIUA, GiaoDien.PHAI, GiaoDien.PHAI},
                dong);
        GiaoDien.in("Tong so ban ghi cham cong: " + soLuong);
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

    // Dinh dang moi dong: maChamCong|maNV|ngay|gioVao|gioRa
    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            ChamCong cc = danhSach[i];
            dong.add(TepVanBan.ghep(cc.getMaChamCong(), cc.getMaNV(), cc.getNgay(), cc.getGioVao(), cc.getGioRa()));
        }
        try {
            TepVanBan.ghiDong(TEN_FILE, "maChamCong|maNV|ngay(yyyy-MM-dd)|gioVao(HH:mm)|gioRa(HH:mm)", dong);
            GiaoDien.in("Da luu cham cong.");
        } catch (IOException e) {
            GiaoDien.in("Loi luu cham cong: " + e.getMessage());
        }
    }

    @Override
    public void docFile(){
        if (!new File(TEN_FILE).exists()) return;
        try {
            java.util.List<String[]> cacDong = TepVanBan.docDong(TEN_FILE);
            danhSach = new ChamCong[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 5) continue;
                try {
                    them(new ChamCong(p[0], p[1], LocalDate.parse(p[2]), LocalTime.parse(p[3]), LocalTime.parse(p[4])));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong cham cong loi: " + String.join("|", p));
                }
            }
        } catch (IOException e) {
            GiaoDien.in("Loi doc cham cong: " + e.getMessage());
        }
    }
}

