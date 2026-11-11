import java.io.*;
import java.util.Arrays;

public class DanhSachQuyDinhThuongPhat implements IQuanLyDuLieu {
    private QuyDinhThuongPhat[] danhSach;
    private int soLuong;
    private static final String FILE = DuongDanDuLieu.file("quydinhthuongphat.dat");

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
            System.out.println("Chua co quy dinh thuong/phat.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            QuyDinhThuongPhat qd = danhSach[i];
            System.out.printf("%s | %s | %s | %s %.2f | %.0f VND/%s%n",
                    qd.getMaQD(), qd.getTenQD(), qd.getLoai(), qd.getDieuKien(),
                    qd.getNguong(), qd.getMucTien(), qd.getDonVi());
        }
    }

    @Override public void ghiFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeInt(soLuong);
            for (int i = 0; i < soLuong; i++) out.writeObject(danhSach[i]);
        } catch (IOException e) {
            System.out.println("Loi luu quy dinh: " + e.getMessage());
        }
    }

    @Override public void docFile() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            int n = in.readInt();
            danhSach = new QuyDinhThuongPhat[Math.max(10, n)];
            soLuong = 0;
            for (int i = 0; i < n; i++) them((QuyDinhThuongPhat) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Loi doc quy dinh: " + e.getMessage());
        }
    }
}
