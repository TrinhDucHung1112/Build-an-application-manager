import java.io.*;
import java.util.Arrays;

public class DanhSachNghiPhep implements IQuanLyDuLieu {
    private NghiPhep[] danhSach;
    private int soLuong;
    private static final String FILE = DuongDanDuLieu.file("nghiphep.dat");

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

    @Override public void ghiFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeInt(soLuong);
            for (int i = 0; i < soLuong; i++) out.writeObject(danhSach[i]);
        } catch (IOException e) {
            System.out.println("Loi luu nghi phep: " + e.getMessage());
        }
    }

    @Override public void docFile() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            int n = in.readInt();
            danhSach = new NghiPhep[Math.max(10, n)];
            soLuong = 0;
            for (int i = 0; i < n; i++) them((NghiPhep) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Loi doc nghi phep: " + e.getMessage());
        }
    }
}
