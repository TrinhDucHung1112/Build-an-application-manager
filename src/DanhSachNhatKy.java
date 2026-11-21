import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Nhat ky he thong, doc/ghi file data/nhatky.txt (CLASS MOI - phan quyen).
 * Moi dong: thoiDiem|nguoiThucHien|hanhDong|doiTuong|ketQua
 * Khac cac danh sach khac: moi su kien duoc GHI NOI VAO FILE NGAY (khong phu thuoc nguoi dung co bam "Luu" hay khong),
 * vi nhat ky khong duoc phep that lac.
 */
public class DanhSachNhatKy implements IQuanLyDuLieu {
//----thuoc tinh----
    private static final String DUONG_DAN = DuongDanDuLieu.file("nhatky.txt");
    private static final String TIEU_DE = "thoiDiem(yyyy-MM-dd HH:mm:ss)|nguoiThucHien|hanhDong|doiTuong|ketQua";

    private NhatKyHeThong[] danhSach;
    private int soLuong;

//----constructor----
    public DanhSachNhatKy(int sucChua) {
        this.danhSach = new NhatKyHeThong[Math.max(10, sucChua)];
        this.soLuong = 0;
    }

//----methods----
    public int getSoLuong() {
        return soLuong;
    }

    public void ghi(String nguoiThucHien, String hanhDong, String doiTuong, String ketQua) {
        NhatKyHeThong nk = new NhatKyHeThong(LocalDateTime.now(), nguoiThucHien, hanhDong, doiTuong, ketQua);
        themVaoBoNho(nk);
        noiVaoFile(nk);
    }

    private void themVaoBoNho(NhatKyHeThong nk) {
        if (soLuong >= danhSach.length) danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
        danhSach[soLuong++] = nk;
    }

    private static String thanhDong(NhatKyHeThong nk) {
        String[] t = nk.thanhDong();
        return TepVanBan.ghep((Object[]) t);
    }

    private void noiVaoFile(NhatKyHeThong nk) {
        try {
            Path p = Paths.get(DUONG_DAN);
            if (!Files.exists(p)) TepVanBan.ghiDong(DUONG_DAN, TIEU_DE, new String[0]);
            Files.write(p, (thanhDong(nk) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            GiaoDien.in("Khong ghi duoc nhat ky: " + e.getMessage());
        }
    }

    /** n su kien gan nhat (moi nhat o cuoi). n <= 0: lay tat ca. */
    public NhatKyHeThong[] ganDay(int n) {
        int tu = n <= 0 ? 0 : Math.max(0, soLuong - n);
        return Arrays.copyOfRange(danhSach, tu, soLuong);
    }

    public NhatKyHeThong[] timTheoNguoi(String ma) {
        NhatKyHeThong[] kq = new NhatKyHeThong[0];
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getNguoiThucHien().equalsIgnoreCase(ma == null ? "" : ma.trim())) kq = Mang.them(kq, danhSach[i]);
        }
        return kq;
    }

    /** Cac su kien khong thanh cong (bi tu choi hoac that bai). */
    public NhatKyHeThong[] timBatThuong() {
        NhatKyHeThong[] kq = new NhatKyHeThong[0];
        for (int i = 0; i < soLuong; i++) {
            if (!danhSach[i].getKetQua().equals(NhatKyHeThong.THANH_CONG)) kq = Mang.them(kq, danhSach[i]);
        }
        return kq;
    }

    public static void inBang(NhatKyHeThong[] ds) {
        if (ds == null || ds.length == 0) {
            GiaoDien.in("Khong co dong nhat ky nao.");
            return;
        }
        String[][] dong = new String[0][];
        for (NhatKyHeThong nk : ds) dong = Mang.them(dong, nk.thanhDong());
        GiaoDien.inBang(
                new String[]{"THOI DIEM", "NGUOI LAM", "HANH DONG", "DOI TUONG", "KET QUA"},
                new int[]{19, 10, 40, 10, 10},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
        GiaoDien.in("Tong so dong nhat ky: " + ds.length);
    }

//----doc/ghi file----
    @Override
    public void docFile() {
        java.io.File f = new java.io.File(DUONG_DAN);
        if (!f.exists()) {
            GiaoDien.in("Chua co file nhat ky (" + DUONG_DAN + "), se tao khi co su kien dau tien.");
            return;
        }
        try {
            String[][] cacDong = TepVanBan.docDong(DUONG_DAN);
            danhSach = new NhatKyHeThong[Math.max(10, cacDong.length)];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 5) continue;
                try {
                    themVaoBoNho(new NhatKyHeThong(LocalDateTime.parse(p[0], NhatKyHeThong.DINH_DANG), p[1], p[2], p[3], p[4]));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong nhat ky loi: " + e.getMessage());
                }
            }
            GiaoDien.in("Da doc " + soLuong + " dong nhat ky tu file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi doc file nhat ky: " + e.getMessage());
        }
    }

    @Override
    public void ghiFile() {
        String[] dong = new String[0];
        for (int i = 0; i < soLuong; i++) dong = Mang.them(dong, thanhDong(danhSach[i]));
        try {
            TepVanBan.ghiDong(DUONG_DAN, TIEU_DE, dong);
            GiaoDien.in("Da ghi " + soLuong + " dong nhat ky vao file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi ghi file nhat ky: " + e.getMessage());
        }
    }
}
