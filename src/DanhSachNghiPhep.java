import java.io.*;
import java.util.Arrays;

public class DanhSachNghiPhep implements IQuanLyDuLieu {
    private NghiPhep[] danhSach;
    private int soLuong;
    private static final String FILE = DuongDanDuLieu.file("nghiphep.txt");

    public DanhSachNghiPhep(int sucChua) {
        danhSach = new NghiPhep[Math.max(10, sucChua)];
    }

    public boolean them(NghiPhep np) {
        if (np == null || np.getMaNP() == null || np.getMaNP().isBlank()
                || np.getMaNV() == null || np.getMaNV().isBlank()
                || np.getNgayBatDau() == null || np.getNgayKetThuc() == null
                || np.getNgayKetThuc().isBefore(np.getNgayBatDau())) return false;
        if (timTheoMa(np.getMaNP()) != null) return false;
        if (soLuong == danhSach.length) danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
        danhSach[soLuong++] = np;
        return true;
    }

    public NghiPhep timTheoMa(String ma) {
        if (ma == null) return null;
        for (int i = 0; i < soLuong; i++)
            if (danhSach[i].getMaNP().equalsIgnoreCase(ma)) return danhSach[i];
        return null;
    }

    public NghiPhep[] timTheoMaNV(String maNV) {
        NghiPhep[] tam = new NghiPhep[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++)
            if (danhSach[i].getMaNV().equalsIgnoreCase(maNV)) tam[dem++] = danhSach[i];
        return Arrays.copyOf(tam, dem);
    }

    public boolean xoaTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNP().equalsIgnoreCase(ma)) {
                System.arraycopy(danhSach, i + 1, danhSach, i, soLuong - i - 1);
                danhSach[--soLuong] = null;
                return true;
            }
        }
        return false;
    }

    public boolean duyetTheoMa(String ma) {
        NghiPhep np = timTheoMa(ma);
        if (np == null) return false;
        np.duyetNghiPhep();
        return true;
    }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Danh sach nghi phep rong.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            NghiPhep np = danhSach[i];
            System.out.printf("%s | NV: %s | %s -> %s | %s | %s | %s ngay%n",
                    np.getMaNP(), np.getMaNV(), np.getNgayBatDau(), np.getNgayKetThuc(),
                    np.getLoaiNghi(), np.getTrangThai(), np.tinhSoNgayNghi());
        }
    }

    public NghiPhep[] getDanhSach() { return Arrays.copyOf(danhSach, soLuong); }
    public int getSoLuong() { return soLuong; }

    // Dinh dang moi dong: maNP|maNV|ngayBatDau|ngayKetThuc|lyDo|loaiNghi|trangThai
    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            NghiPhep np = danhSach[i];
            dong.add(TepVanBan.ghep(np.getMaNP(), np.getMaNV(), np.getNgayBatDau(), np.getNgayKetThuc(),
                    np.getLyDo(), np.getLoaiNghi(), np.getTrangThai()));
        }
        try {
            TepVanBan.ghiDong(FILE, "maNP|maNV|ngayBatDau|ngayKetThuc|lyDo|loaiNghi|trangThai", dong);
        } catch (java.io.IOException e) {
            System.out.println("Loi luu nghi phep: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE).exists()) return;
        try {
            java.util.List<String[]> cacDong = TepVanBan.docDong(FILE);
            danhSach = new NghiPhep[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 7) continue;
                try {
                    NghiPhep np = new NghiPhep(p[0], p[1], java.time.LocalDate.parse(p[2]),
                            java.time.LocalDate.parse(p[3]), p[4]);
                    np.setLoaiNghi(p[5]);
                    np.setTrangThai(p[6]);
                    them(np);
                } catch (RuntimeException e) {
                    System.out.println("Bo qua dong nghi phep loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Loi doc nghi phep: " + e.getMessage());
        }
    }

}
