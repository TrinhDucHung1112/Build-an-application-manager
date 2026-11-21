import java.time.LocalDate;

/** Danh sach hop dong lao dong theo nhan vien (CLASS MOI), luu o data/hopdong.txt. */
public class DanhSachLichSuHopDong implements IQuanLyDuLieu {
    private static final String FILE_TEXT = DuongDanDuLieu.file("hopdong.txt");
    private LichSuHopDong[] ds = new LichSuHopDong[0];

    public int getSoLuong() { return ds.length; }

    public boolean them(LichSuHopDong hd) {
        if (hd == null || timTheoMa(hd.getMaHD()) != null) return false;
        ds = Mang.them(ds, hd);
        return true;
    }

    public LichSuHopDong timTheoMa(String ma) {
        if (ma == null) return null;
        for (LichSuHopDong x : ds) if (x.getMaHD().equalsIgnoreCase(ma.trim())) return x;
        return null;
    }

    //cac hop dong cua 1 nhan vien, xep theo ngay ky
    public LichSuHopDong[] timTheoMaNV(String maNV) {
        LichSuHopDong[] kq = new LichSuHopDong[0];
        for (LichSuHopDong x : ds) if (x.getMaNV().equalsIgnoreCase(maNV)) kq = Mang.them(kq, x);
        Mang.sapXep(kq, (a, b) -> a.getNgayKy().compareTo(b.getNgayKy()));
        return kq;
    }

    public LichSuHopDong[] timTheoNam(String maNV, int nam) {
        LichSuHopDong[] kq = new LichSuHopDong[0];
        for (LichSuHopDong x : timTheoMaNV(maNV)) if (x.hieuLucTrongNam(nam)) kq = Mang.them(kq, x);
        return kq;
    }

    public boolean xoaTheoMa(String ma) {
        LichSuHopDong x = timTheoMa(ma);
        if (x == null) return false;
        ds = Mang.xoa(ds, x);
        return true;
    }

    public void xoaTheoMaNV(String maNV) {
        ds = Mang.loc(ds, x -> !x.getMaNV().equalsIgnoreCase(maNV));
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
        int so = timTheoMaNV(maNV).length + 1;
        String ma = "HD_" + maNV.toUpperCase() + "_" + so;
        while (timTheoMa(ma) != null) ma = "HD_" + maNV.toUpperCase() + "_" + (++so);
        return ma;
    }

    public static void inBang(LichSuHopDong[] cac) {
        String[][] dong = new String[0][];
        for (LichSuHopDong h : cac) {
            dong = Mang.them(dong, new String[]{h.getMaHD(), h.getLoai(), String.valueOf(h.getNgayKy()),
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
        String[] dong = new String[0];
        for (LichSuHopDong x : ds) {
            dong = Mang.them(dong, TepVanBan.ghep(x.getMaHD(), x.getMaNV(), x.getLoai(), x.getNgayKy(),
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
            String[][] cacDong = TepVanBan.docDong(FILE_TEXT);
            ds = new LichSuHopDong[0];
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
