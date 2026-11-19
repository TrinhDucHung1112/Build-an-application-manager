import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Danh sach cac yeu cau da gui (CLASS MOI), luu o data/yeucau.txt. */
public class DanhSachYeuCau implements IQuanLyDuLieu {
    private static final String FILE_TEXT = DuongDanDuLieu.file("yeucau.txt");
    private final List<YeuCau> ds = new ArrayList<>();

    public int getSoLuong() { return ds.size(); }

    public boolean them(YeuCau yc) {
        if (yc == null || timTheoMa(yc.getMaYC()) != null) return false;
        ds.add(yc);
        return true;
    }

    public YeuCau timTheoMa(String ma) {
        if (ma == null) return null;
        for (YeuCau x : ds) if (x.getMaYC().equalsIgnoreCase(ma.trim())) return x;
        return null;
    }

    public String taoMaMoi() {
        int so = ds.size() + 1;
        String ma = String.format("YC%03d", so);
        while (timTheoMa(ma) != null) ma = String.format("YC%03d", ++so);
        return ma;
    }

    //cac yeu cau DA GUI boi 1 nguoi (moi nhat truoc)
    public List<YeuCau> timDaGui(String maNV) {
        List<YeuCau> kq = new ArrayList<>();
        for (YeuCau x : ds) if (x.getNguoiGui().equalsIgnoreCase(maNV)) kq.add(x);
        kq.sort((a, b) -> b.getNgayGui().compareTo(a.getNgayGui()));
        return kq;
    }

    //cac yeu cau DANG CHO nguoi nay duyet
    public List<YeuCau> timCanDuyet(String maNguoiNhan) {
        List<YeuCau> kq = new ArrayList<>();
        for (YeuCau x : ds) if (x.dangCho() && x.getNguoiNhan().equalsIgnoreCase(maNguoiNhan)) kq.add(x);
        kq.sort((a, b) -> a.getNgayGui().compareTo(b.getNgayGui()));
        return kq;
    }

    public int demChoDuyet(String maNguoiNhan) {
        return timCanDuyet(maNguoiNhan).size();
    }

    //da co yeu cau dang cho cung muc, cung nam cua nguoi nay chua
    public boolean daCoYeuCauCho(String nguoiGui, int nam, String truong) {
        for (YeuCau x : ds)
            if (x.dangCho() && x.getNguoiGui().equalsIgnoreCase(nguoiGui) && x.getNam() == nam && x.getTruong().equals(truong))
                return true;
        return false;
    }

    public void xoaTheoMaNV(String maNV) {
        ds.removeIf(x -> x.getNguoiGui().equalsIgnoreCase(maNV) || x.getNguoiNhan().equalsIgnoreCase(maNV));
    }

//---doc, ghi file---
    // Dinh dang moi dong: maYC|nguoiGui|nguoiNhan|loai|nam|truong|giaTriCu|giaTriMoi|ngayGui|trangThai|ngayXuLy|ghiChuXuLy
    @Override
    public void ghiFile() {
        List<String> dong = new ArrayList<>();
        for (YeuCau y : ds) {
            dong.add(TepVanBan.ghep(y.getMaYC(), y.getNguoiGui(), y.getNguoiNhan(), y.getLoai(), y.getNam(),
                    y.getTruong(), y.getGiaTriCu(), y.getGiaTriMoi(), y.getNgayGui().format(YeuCau.DINH_DANG),
                    y.getTrangThai(), y.getNgayXuLy() == null ? "" : y.getNgayXuLy().format(YeuCau.DINH_DANG),
                    y.getGhiChuXuLy()));
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "maYC|nguoiGui|nguoiNhan|loai|nam|truong|giaTriCu|giaTriMoi|ngayGui(yyyy-MM-dd HH:mm)|trangThai(CHO_DUYET/DA_DUYET/TU_CHOI/DA_HUY)|ngayXuLy|ghiChuXuLy", dong);
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu yeu cau: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            List<String[]> cacDong = TepVanBan.docDong(FILE_TEXT);
            ds.clear();
            for (String[] p : cacDong) {
                if (p.length < 10) continue;
                try {
                    them(new YeuCau(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), p[5], p[6], p[7],
                            LocalDateTime.parse(p[8], YeuCau.DINH_DANG), p[9],
                            p.length > 10 && !p[10].isEmpty() ? LocalDateTime.parse(p[10], YeuCau.DINH_DANG) : null,
                            p.length > 11 ? p[11] : ""));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong yeu cau loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc yeu cau: " + e.getMessage());
        }
    }
}
