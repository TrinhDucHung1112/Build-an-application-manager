import java.time.LocalDate;

/**
 * Lich su cong tac theo nam cua nhan vien (CLASS MOI), luu o data/lichsu.txt.
 *
 * Quy tac xac dinh trang thai cua 1 nam (xacDinh):
 *  - Nam hien tai          : lay theo NhanVien dang co (nguon du lieu duy nhat cua hien tai).
 *  - Nam truoc co ban ghi  : lay ban ghi do.
 *  - Nam truoc khong co    : GIU NGUYEN nhu ban ghi gan nhat truoc do (VD 2011 giong 2010).
 *  - Nam som hon moi ban ghi: lay ban ghi som nhat.
 *  - Nhan vien chua co ban ghi nao: lay theo hien tai.
 */
public class DanhSachLichSuCongTac implements IQuanLyDuLieu {
    private static final String FILE_TEXT = DuongDanDuLieu.file("lichsu.txt");

    //ket qua xac dinh 1 nam: trang thai + nguon cua so lieu
    public static final class KetQua {
        public final LichSuCongTac ls;
        public final String nguon;
        KetQua(LichSuCongTac ls, String nguon) {
            this.ls = ls;
            this.nguon = nguon;
        }
    }

    private LichSuCongTac[] ds = new LichSuCongTac[0];

    public int getSoLuong() { return ds.length; }

    public LichSuCongTac timChinhXac(String maNV, int nam) {
        for (LichSuCongTac x : ds)
            if (x.getMaNV().equalsIgnoreCase(maNV) && x.getNam() == nam) return x;
        return null;
    }

    public LichSuCongTac[] timTheoMaNV(String maNV) {
        LichSuCongTac[] kq = new LichSuCongTac[0];
        for (LichSuCongTac x : ds) if (x.getMaNV().equalsIgnoreCase(maNV)) kq = Mang.them(kq, x);
        return kq;
    }

    //them moi hoac thay the ban ghi cung (maNV, nam)
    public void ghi(LichSuCongTac moi) {
        for (int i = 0; i < ds.length; i++) {
            LichSuCongTac x = ds[i];
            if (x.getMaNV().equalsIgnoreCase(moi.getMaNV()) && x.getNam() == moi.getNam()) {
                ds[i] = moi;
                return;
            }
        }
        ds = Mang.them(ds, moi);
    }

    public void xoaTheoMaNV(String maNV) {
        ds = Mang.loc(ds, x -> !x.getMaNV().equalsIgnoreCase(maNV));
    }

    //trang thai hien tai cua nhan vien duoi dang 1 dong lich su (nam = nam hien tai)
    public static LichSuCongTac tuNhanVien(NhanVien nv, int nam, String ghiChu) {
        ChucVu cv = nv.getChucVu();
        return new LichSuCongTac(nv.getMaNV(), nam,
                nv.getPhongBan() == null ? "" : nv.getPhongBan().getMaPB(),
                cv == null ? "" : cv.getMaCV(), cv == null ? "" : cv.getTenCV(),
                cv == null ? 0 : cv.getHeSoLuong(), nv.getLuongCoBan(), ghiChu);
    }

    public KetQua xacDinh(NhanVien nv, int nam) {
        int namNay = LocalDate.now().getYear();
        if (nam >= namNay) return new KetQua(tuNhanVien(nv, nam, ""), "Hien tai");
        LichSuCongTac tot = null;       //ban ghi gan nhat khong sau nam can xem
        LichSuCongTac somNhat = null;
        for (LichSuCongTac x : ds) {
            if (!x.getMaNV().equalsIgnoreCase(nv.getMaNV())) continue;
            if (x.getNam() <= nam && (tot == null || x.getNam() > tot.getNam())) tot = x;
            if (somNhat == null || x.getNam() < somNhat.getNam()) somNhat = x;
        }
        if (tot != null) {
            LichSuCongTac kq = tot.sao();
            kq.setNam(nam);
            return new KetQua(kq, tot.getNam() == nam ? "Ban ghi nam " + nam : "Giu nguyen tu nam " + tot.getNam());
        }
        if (somNhat != null) {
            LichSuCongTac kq = somNhat.sao();
            kq.setNam(nam);
            return new KetQua(kq, "Theo ban ghi nam " + somNhat.getNam());
        }
        return new KetQua(tuNhanVien(nv, nam, ""), "Theo hien tai");
    }

    //truoc khi DOI phong ban/chuc vu/luong cua nam hien tai: luu lai trang thai cu cho nam truoc,
    //de cac nam truoc khong bi doi theo
    public void ghiNhanTruocKhiDoi(NhanVien nv) {
        int namNay = LocalDate.now().getYear();
        int namTruoc = namNay - 1;
        if (nv.getNgayVaoLam() == null || nv.getNgayVaoLam().getYear() > namTruoc) return;
        if (timChinhXac(nv.getMaNV(), namTruoc) != null) return;
        KetQua cu = xacDinh(nv, namTruoc);          //namTruoc < namNay nen khong lay tu hien tai neu da co ban ghi
        LichSuCongTac luu = cu.ls.sao();
        if (cu.nguon.equals("Theo hien tai")) luu = tuNhanVien(nv, namTruoc, "");
        luu.setNam(namTruoc);
        ghi(luu);
    }

    //sua 1 NAM CU (nam < nam hien tai): ghi ban ghi moi cho nam do, dong thoi giu nguyen cac nam khac
    //(neu nam sau / cac nam dau chua co ban ghi rieng thi luu lai trang thai cu cua chung truoc khi ghi)
    public void ghiSuaNamCu(NhanVien nv, LichSuCongTac moi) {
        int nam = moi.getNam();
        int namNay = LocalDate.now().getYear();
        int namVao = nv.getNgayVaoLam().getYear();
        if (nam > namVao) {                                  //cac nam dau (chua co ban ghi truoc nam nay) phai giu nguyen
            boolean coTruoc = false;
            for (LichSuCongTac x : timTheoMaNV(nv.getMaNV())) if (x.getNam() < nam) coTruoc = true;
            if (!coTruoc) {
                LichSuCongTac dau = xacDinh(nv, namVao).ls.sao();
                dau.setNam(namVao);
                ghi(dau);
            }
        }
        int tiep = nam + 1;
        if (tiep < namNay && timChinhXac(nv.getMaNV(), tiep) == null) {     //nam sau phai giu nguyen, khong ke thua gia tri moi
            LichSuCongTac giu = xacDinh(nv, tiep).ls.sao();
            giu.setNam(tiep);
            ghi(giu);
        }
        ghi(moi);
    }

//---doc, ghi file---
    // Dinh dang moi dong: maNV|nam|maPB|maCV|tenCV|heSoCV|luongCoBan|ghiChu
    @Override
    public void ghiFile() {
        String[] dong = new String[0];
        for (LichSuCongTac x : ds) {
            dong = Mang.them(dong, TepVanBan.ghep(x.getMaNV(), x.getNam(), x.getMaPB(), x.getMaCV(), x.getTenCV(),
                    x.getHeSoCV(), x.getLuongCoBan(), x.getGhiChu()));
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "maNV|nam|maPB|maCV|tenCV|heSoCV|luongCoBan|ghiChu  (nam khong co dong = giu nguyen nhu nam truoc do)", dong);
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu lich su cong tac: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            String[][] cacDong = TepVanBan.docDong(FILE_TEXT);
            ds = new LichSuCongTac[0];
            for (String[] p : cacDong) {
                if (p.length < 7) continue;
                try {
                    ghi(new LichSuCongTac(p[0], Integer.parseInt(p[1]), p[2], p[3], p[4],
                            Double.parseDouble(p[5]), Double.parseDouble(p[6]), p.length > 7 ? p[7] : ""));
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong lich su cong tac loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc lich su cong tac: " + e.getMessage());
        }
    }
}
