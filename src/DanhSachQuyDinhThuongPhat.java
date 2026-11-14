import java.io.*;
import java.util.Arrays;

public class DanhSachQuyDinhThuongPhat implements IQuanLyDuLieu {
    private QuyDinhThuongPhat[] danhSach;
    private int soLuong;
    private static final String FILE = DuongDanDuLieu.file("quydinhthuongphat.txt");

    public DanhSachQuyDinhThuongPhat(int sucChua) {
        danhSach = new QuyDinhThuongPhat[Math.max(10, sucChua)];
    }

    public boolean them(QuyDinhThuongPhat qd) {
        if (qd == null || qd.getMaQD() == null || qd.getMaQD().isBlank()) return false;
        if (timTheoMa(qd.getMaQD()) != null) return false;
        if (soLuong == danhSach.length) danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
        danhSach[soLuong++] = qd;
        return true;
    }

    public QuyDinhThuongPhat timTheoMa(String ma) {
        if (ma == null) return null;
        for (int i = 0; i < soLuong; i++)
            if (danhSach[i].getMaQD().equalsIgnoreCase(ma)) return danhSach[i];
        return null;
    }

    public boolean xoaTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaQD().equalsIgnoreCase(ma)) {
                System.arraycopy(danhSach, i + 1, danhSach, i, soLuong - i - 1);
                danhSach[--soLuong] = null;
                return true;
            }
        }
        return false;
    }

    public QuyDinhThuongPhat[] getDanhSach() { return Arrays.copyOf(danhSach, soLuong); }
    public int getSoLuong() { return soLuong; }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            GiaoDien.in("Chua co quy dinh thuong/phat.");
            return;
        }
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            QuyDinhThuongPhat qd = danhSach[i];
            dong.add(new String[]{qd.getMaQD(), qd.getTenQD(), qd.getLoai(),
                    qd.getDieuKien() + " " + TepVanBan.soDep(qd.getNguong()),
                    String.format("%,.0f", qd.getMucTien()), qd.getDonVi()});
        }
        GiaoDien.inBang(
                new String[]{"MA QD", "TEN QUY DINH", "LOAI", "DIEU KIEN", "MUC TIEN (VND)", "DON VI"},
                new int[]{8, 28, 7, 10, 14, 22},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI},
                dong);
        GiaoDien.in("Tong so quy dinh: " + soLuong);
    }

    // Dinh dang moi dong: maQD|tenQD|loai|dieuKien|nguong|mucTien|donVi
    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            QuyDinhThuongPhat qd = danhSach[i];
            dong.add(TepVanBan.ghep(qd.getMaQD(), qd.getTenQD(), qd.getLoai(), qd.getDieuKien(),
                    qd.getNguong(), qd.getMucTien(), qd.getDonVi()));
        }
        try {
            TepVanBan.ghiDong(FILE, "maQD|tenQD|loai|dieuKien|nguong|mucTien|donVi", dong);
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu quy dinh: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE).exists()) return;
        try {
            java.util.List<String[]> cacDong = TepVanBan.docDong(FILE);
            danhSach = new QuyDinhThuongPhat[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 7) continue;
                try {
                    them(new QuyDinhThuongPhat(p[0], p[1], p[2], p[3], Double.parseDouble(p[4]),
                            Double.parseDouble(p[5]), p[6]));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong quy dinh loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc quy dinh: " + e.getMessage());
        }
    }

}
