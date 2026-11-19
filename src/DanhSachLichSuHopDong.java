import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Danh sach hop dong lao dong theo nhan vien (CLASS MOI), luu o data/hopdong.txt. */
public class DanhSachLichSuHopDong implements IQuanLyDuLieu {
    private static final String FILE_TEXT = DuongDanDuLieu.file("hopdong.txt");
    private final List<LichSuHopDong> ds = new ArrayList<>();

    public int getSoLuong() { return ds.size(); }

    public boolean them(LichSuHopDong hd) {
        if (hd == null || timTheoMa(hd.getMaHD()) != null) return false;
        ds.add(hd);
        return true;
    }

    public LichSuHopDong timTheoMa(String ma) {
        if (ma == null) return null;
        for (LichSuHopDong x : ds) if (x.getMaHD().equalsIgnoreCase(ma.trim())) return x;
        return null;
    }

    //cac hop dong cua 1 nhan vien, xep theo ngay ky
    public List<LichSuHopDong> timTheoMaNV(String maNV) {
        List<LichSuHopDong> kq = new ArrayList<>();
        for (LichSuHopDong x : ds) if (x.getMaNV().equalsIgnoreCase(maNV)) kq.add(x);
        kq.sort((a, b) -> a.getNgayKy().compareTo(b.getNgayKy()));
        return kq;
    }

    public List<LichSuHopDong> timTheoNam(String maNV, int nam) {
        List<LichSuHopDong> kq = new ArrayList<>();
        for (LichSuHopDong x : timTheoMaNV(maNV)) if (x.hieuLucTrongNam(nam)) kq.add(x);
        return kq;
    }

    public boolean xoaTheoMa(String ma) {
        LichSuHopDong x = timTheoMa(ma);
        return x != null && ds.remove(x);
    }

    public void xoaTheoMaNV(String maNV) {
        ds.removeIf(x -> x.getMaNV().equalsIgnoreCase(maNV));
    }

    //khoang [ky, het] co trung voi hop dong khac cua cung nhan vien khong (bo qua hop dong boQuaMa)
    public boolean trungVoiHopDongKhac(String maNV, LocalDate ky, LocalDate het, String boQuaMa) {
        for (LichSuHopDong x : timTheoMaNV(maNV)) {
            if (boQuaMa != null && x.getMaHD().equalsIgnoreCase(boQuaMa)) continue;
            if (x.trungThoiGian(ky, het)) return true;
        }
        return false;
    }

    public String taoMaMoi(String maNV) {
        int so = timTheoMaNV(maNV).size() + 1;
        String ma = "HD_" + maNV.toUpperCase() + "_" + so;
        while (timTheoMa(ma) != null) ma = "HD_" + maNV.toUpperCase() + "_" + (++so);
        return ma;
    }

    public static void inBang(List<LichSuHopDong> cac) {
        List<String[]> dong = new ArrayList<>();
        for (LichSuHopDong h : cac) {
            dong.add(new String[]{h.getMaHD(), h.getLoai(), String.valueOf(h.getNgayKy()),
                    h.khongThoiHan() ? "(khong thoi han)" : String.valueOf(h.getNgayHetHan()),
                    h.tinhTrang(), h.getGhiChu()});
        }
        GiaoDien.inBang(
                new String[]{"MA HD", "LOAI HOP DONG", "NGAY KY", "NGAY HET HAN", "TINH TRANG", "GHI CHU"},
                new int[]{12, 16, 10, 16, 26, 24},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
    }

//---doc, ghi file---
    // Dinh dang moi dong: maHD|maNV|loai|ngayKy|ngayHetHan|ghiChu     (ngayHetHan de trong = khong thoi han)
    @Override
    public void ghiFile() {
        List<String> dong = new ArrayList<>();
        for (LichSuHopDong x : ds) {
            dong.add(TepVanBan.ghep(x.getMaHD(), x.getMaNV(), x.getLoai(), x.getNgayKy(),
                    x.getNgayHetHan() == null ? "" : x.getNgayHetHan(), x.getGhiChu()));
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "maHD|maNV|loai|ngayKy(yyyy-MM-dd)|ngayHetHan(de trong = khong thoi han)|ghiChu", dong);
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu hop dong lao dong: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            List<String[]> cacDong = TepVanBan.docDong(FILE_TEXT);
            ds.clear();
            for (String[] p : cacDong) {
                if (p.length < 5) continue;
                try {
                    them(new LichSuHopDong(p[0], p[1], p[2], LocalDate.parse(p[3]),
                            p[4].isEmpty() ? null : LocalDate.parse(p[4]), p.length > 5 ? p[5] : ""));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong hop dong loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc hop dong lao dong: " + e.getMessage());
        }
    }
}
