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

    //in danh sach don nghi phep dang bang co STT
    public static void inBang(NghiPhep[] ds) {
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for (NghiPhep np : ds) {
            if (np == null) continue;
            dong.add(new String[]{np.getMaNP(), np.getMaNV(), String.valueOf(np.getNgayBatDau()),
                    String.valueOf(np.getNgayKetThuc()), String.valueOf(np.tinhSoNgayNghi()),
                    np.getLoaiNghi(), np.getTrangThai(), np.getLyDo()});
        }
        GiaoDien.inBang(
                new String[]{"MA DON", "MA NV", "TU NGAY", "DEN NGAY", "SO NGAY", "LOAI NGHI", "TRANG THAI", "LY DO"},
                new int[]{10, 8, 10, 10, 7, 10, 10, 28},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
    }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            GiaoDien.in("Danh sach nghi phep rong.");
            return;
        }
        inBang(getDanhSach());
        GiaoDien.in("Tong so don nghi phep: " + soLuong);
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
            GiaoDien.in("Loi luu nghi phep: " + e.getMessage());
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
                    GiaoDien.in("Bo qua dong nghi phep loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc nghi phep: " + e.getMessage());
        }
    }

}
