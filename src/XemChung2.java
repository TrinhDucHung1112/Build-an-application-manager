import java.time.LocalDate;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * Khung xem danh sach "Chung2" (CLASS MOI, chi chua ham tien ich).
 *
 *   Chung1 : chon NAM (list cac nam + trang thai + so ban ghi, nhap so thu tu hoac ngay thang)
 *   Chung2 : Chung1 + 3 lua chon:  1. Tim theo ma   2. Xem theo phong ban   3. Xem het
 *
 * Ket qua luon duoc chia TRANG (15 dong / trang) de de nhin; chon STT de xem chi tiet / xu ly 1 dong.
 * Man hinh nao khong co du lieu theo nam (VD tai khoan, quy dinh) thi bo buoc chon nam;
 * khong co phong ban thi muc 2 bao "khong ap dung".
 *
 * Cach dung:  XemChung2.chay(new XemChung2.Cau("TIEU DE")....layDong(nam -> {...}), Main::docChuoi);
 */
public final class XemChung2 {
    public static final int DONG_MOI_TRANG = 15;
    private static final int NGUONG_LOC_THANG = 60;      //tu bay nhieu dong tro len thi hoi loc theo thang

    private XemChung2() {}

    /** 1 dong du lieu cua danh sach. */
    public static final class Dong {
        public final String ma;          //ma ban ghi (tim theo ma)
        public final String maNV;        //nhan vien lien quan, "" neu khong co (cung duoc tim theo ma)
        public final String maPB;        //phong ban (theo nam dang xem), "" neu chua phan
        public final LocalDate ngay;     //ngay cua ban ghi (de loc theo thang), co the null
        public final String[] cot;       //cac cot hien thi
        public final Object goc;         //doi tuong goc (cho hanh dong khi chon STT)

        public Dong(String ma, String maNV, String maPB, LocalDate ngay, String[] cot, Object goc) {
            this.ma = ma == null ? "" : ma;
            this.maNV = maNV == null ? "" : maNV;
            this.maPB = maPB == null ? "" : maPB;
            this.ngay = ngay;
            this.cot = cot;
            this.goc = goc;
        }
    }

    /** Cau hinh 1 man hinh danh sach. */
    public static final class Cau {
        private final String tieuDe;
        private String[] td = new String[0];
        private int[] rong = new int[0];
        private String[] canh = new String[0];
        private LocalDate tuNgay;
        private int namCuoi;
        private String[] tdNam = new String[0];
        private int[] rongNam = new int[0];
        private String[] canhNam = new String[0];
        private IntFunction<String[]> cotNam = n -> new String[0];
        private IntFunction<Dong[]> layDong = n -> new Dong[0];
        private Function<String, String> tenPB = s -> s;
        private boolean theoPhongBan = true;
        private boolean hienCotPB = true;
        private Consumer<Dong> khiChon;
        private String tenKhiChon = "xem chi tiet";
        private boolean thoatSauKhiChon;                //sau khi xu ly 1 dong thi quay ve menu 1/2/3 (de nap lai danh sach)
        private BiConsumer<Integer, String> xemTheoPB;      //(nam, maPB): thay cach xem theo phong ban mac dinh
        private String goiYMa = "ma nhan vien hoac ma ban ghi";
        private String nhan1 = "Tim theo ma", nhan2 = "Xem theo phong ban", nhan3 = "Xem het";
        private IntConsumer muc2;                           //thay cach xu ly lua chon 2 (nhan vao nam)

        public Cau(String tieuDe) { this.tieuDe = tieuDe; }

        /** Cac cot cua bang du lieu (khong tinh STT va cot PB). */
        public Cau cot(String[] td, int[] rong, String[] canh) { this.td = td; this.rong = rong; this.canh = canh; return this; }

        /** Co buoc chon nam: tu ngay nao, den nam nao, cac cot them o bang chon nam (ngoai NAM / TRANG THAI / SO BAN GHI). */
        public Cau nam(LocalDate tuNgay, int namCuoi, String[] tdNam, int[] rongNam, String[] canhNam, IntFunction<String[]> cotNam) {
            this.tuNgay = tuNgay; this.namCuoi = namCuoi;
            this.tdNam = tdNam; this.rongNam = rongNam; this.canhNam = canhNam; this.cotNam = cotNam;
            return this;
        }

        public Cau nam(LocalDate tuNgay, int namCuoi) {
            return nam(tuNgay, namCuoi, new String[0], new int[0], new String[0], n -> new String[0]);
        }

        /** Lay tat ca dong (da loc theo quyen) cua 1 nam (nam = 0 neu man hinh khong theo nam). */
        public Cau layDong(IntFunction<Dong[]> layDong) { this.layDong = layDong; return this; }

        public Cau tenPhongBan(Function<String, String> tenPB) { this.tenPB = tenPB; return this; }
        public Cau khongPhongBan() { this.theoPhongBan = false; this.hienCotPB = false; return this; }
        public Cau khongCotPB() { this.hienCotPB = false; return this; }
        public Cau khiChon(String ten, Consumer<Dong> hanhDong) { this.tenKhiChon = ten; this.khiChon = hanhDong; return this; }
        /** Sau khi xu ly 1 dong (VD duyet don) quay lai menu de danh sach duoc nap lai. */
        public Cau thoatSauKhiChon() { this.thoatSauKhiChon = true; return this; }
        public Cau xemTheoPhongBan(BiConsumer<Integer, String> xem) { this.xemTheoPB = xem; return this; }
        /** Doi ten 3 lua chon (VD "Xem cac loai phong ban quan li"). */
        public Cau nhanMenu(String l1, String l2, String l3) { this.nhan1 = l1; this.nhan2 = l2; this.nhan3 = l3; return this; }
        /** Tu xu ly lua chon 2 (thay cho "xem theo phong ban" mac dinh). */
        public Cau muc2(IntConsumer xuLy) { this.muc2 = xuLy; return this; }
        public Cau goiYMa(String goiY) { this.goiYMa = goiY; return this; }
    }

    //----------------------------------------------------------------------------------------------------
    /** Chay man hinh: (chon nam) -> menu 1 / 2 / 3. */
    public static void chay(Cau c, Function<String, String> hoi) {
        while (true) {
            int nam = 0;
            if (c.tuNgay != null) {
                Integer n = chonNam(c, hoi);
                if (n == null) return;
                nam = n;
            }
            boolean tiep = true;
            while (tiep) {
                Dong[] tatCa = c.layDong.apply(nam);
                GiaoDien.in("");
                GiaoDien.in("== " + c.tieuDe + (nam > 0 ? " - NAM " + nam : "") + ": " + tatCa.length + " ban ghi ==");
                GiaoDien.in("1. " + c.nhan1);
                GiaoDien.in(c.theoPhongBan ? "2. " + c.nhan2 : "2. " + c.nhan2 + " (khong ap dung cho muc nay)");
                GiaoDien.in("3. " + c.nhan3);
                GiaoDien.in("0. " + (c.tuNgay != null ? "Chon nam khac / quay lai" : "Quay lai"));
                String ch = hoi.apply("Chon: ").trim();
                switch (ch) {
                    case "1": timTheoMa(c, nam, tatCa, hoi); break;
                    case "2":
                        if (!c.theoPhongBan) GiaoDien.in("Muc nay khong co phong ban de loc.");
                        else if (c.muc2 != null) c.muc2.accept(nam);
                        else xemTheoPhongBan(c, nam, tatCa, hoi);
                        break;
                    case "3": xemHet(c, tatCa, hoi); break;
                    case "0": case "": tiep = false; break;
                    default: GiaoDien.in("Chi nhap 1, 2, 3 hoac 0.");
                }
            }
            if (c.tuNgay == null) return;           //khong co buoc chon nam: 0 = thoat
        }
    }

    //---- chon nam (Chung1): NAM | TRANG THAI | SO BAN GHI | cot them ----
    private static Integer chonNam(Cau c, Function<String, String> hoi) {
        int[] cacNam = ChonKy.cacNam(c.tuNgay, c.namCuoi);
        int n = 3 + c.tdNam.length;
        String[] td = new String[n];
        int[] rong = new int[n];
        String[] canh = new String[n];
        td[0] = "NAM"; rong[0] = 6; canh[0] = GiaoDien.GIUA;
        td[1] = "TRANG THAI"; rong[1] = 12; canh[1] = GiaoDien.TRAI;
        td[2] = "SO BAN GHI"; rong[2] = 11; canh[2] = GiaoDien.PHAI;
        for (int i = 0; i < c.tdNam.length; i++) { td[3 + i] = c.tdNam[i]; rong[3 + i] = c.rongNam[i]; canh[3 + i] = c.canhNam[i]; }
        String[][] dong = new String[0][];
        for (int nam : cacNam) {
            String[] them = c.cotNam.apply(nam);
            String[] r = new String[n];
            r[0] = String.valueOf(nam);
            r[1] = ChonKy.trangThaiNam(nam);
            r[2] = String.valueOf(c.layDong.apply(nam).length);
            for (int i = 0; i < them.length && 3 + i < n; i++) r[3 + i] = them[i];
            for (int i = 0; i < n; i++) if (r[i] == null) r[i] = "";
            dong = Mang.them(dong, r);
        }
        GiaoDien.in("");
        GiaoDien.in(c.tieuDe + " - chon nam");
        ChonKy.KetQua k = ChonKy.chonNam(cacNam, td, rong, canh, dong, c.tuNgay, hoi);
        return k == null ? null : k.nam;
    }

    //---- 1. tim theo ma ----
    private static void timTheoMa(Cau c, int nam, Dong[] tatCa, Function<String, String> hoi) {
        String tuKhoa = hoi.apply("Nhap " + c.goiYMa + " (co the nhap 1 phan): ").trim();
        if (tuKhoa.isEmpty()) return;
        String k = tuKhoa.toLowerCase();
        Dong[] kq = new Dong[0];
        for (Dong d : tatCa) if (d.ma.toLowerCase().contains(k) || d.maNV.toLowerCase().contains(k)) kq = Mang.them(kq, d);
        if ((kq.length == 0)) {
            GiaoDien.in("Khong tim thay \"" + tuKhoa + "\"" + (nam > 0 ? " trong nam " + nam : "") + ".");
            return;
        }
        hienPhanTrang(c, "Ket qua tim \"" + tuKhoa + "\"", kq, hoi);
    }

    //---- 2. xem theo phong ban: chon 1 phong ban roi xem cac dong cua phong ban do ----
    private static void xemTheoPhongBan(Cau c, int nam, Dong[] tatCa, Function<String, String> hoi) {
        String[] cacMa = new String[0];      //2 mang song song: cacMa[i] co soBanGhi[i] ban ghi
        int[] soBanGhi = new int[0];
        for (Dong d : tatCa) {
            int vt = Mang.viTri(cacMa, d.maPB);
            if (vt < 0) {
                cacMa = Mang.them(cacMa, d.maPB);
                soBanGhi = Mang.them(soBanGhi, 1);
            } else {
                soBanGhi[vt]++;
            }
        }
        if (cacMa.length == 0) {
            GiaoDien.in("Khong co du lieu de chia theo phong ban.");
            return;
        }
        //sap xep ma phong ban tang dan (doi cho dong thoi so ban ghi tuong ung)
        for (int i = 1; i < cacMa.length; i++) {
            String khoaMa = cacMa[i];
            int khoaSo = soBanGhi[i];
            int j = i - 1;
            while (j >= 0 && cacMa[j].compareTo(khoaMa) > 0) {
                cacMa[j + 1] = cacMa[j];
                soBanGhi[j + 1] = soBanGhi[j];
                j--;
            }
            cacMa[j + 1] = khoaMa;
            soBanGhi[j + 1] = khoaSo;
        }
        String[][] dong = new String[0][];
        for (int i = 0; i < cacMa.length; i++) {
            String ma = cacMa[i];
            dong = Mang.them(dong, new String[]{String.valueOf(dong.length + 1), ma.isEmpty() ? "-" : ma,
                    ma.isEmpty() ? "(chua phan phong ban)" : c.tenPB.apply(ma), String.valueOf(soBanGhi[i])});
        }
        GiaoDien.in("");
        GiaoDien.inBangKhongStt(new String[]{"STT", "MA PB", "TEN PHONG BAN", "SO BAN GHI"}, new int[]{5, 8, 26, 11},
                new String[]{GiaoDien.GIUA, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.PHAI}, dong);
        String in = hoi.apply("Chon phong ban (STT hoac ma PB, 0 = quay lai): ").trim();
        if (in.isEmpty() || in.equals("0")) return;
        String maChon = null;
        if (in.matches("\\d{1,3}") && Integer.parseInt(in) >= 1 && Integer.parseInt(in) <= cacMa.length) maChon = cacMa[Integer.parseInt(in) - 1];
        else for (String ma : cacMa) if (!ma.isEmpty() && ma.equalsIgnoreCase(in)) maChon = ma;
        if (maChon == null) {
            GiaoDien.in("Khong co phong ban \"" + in + "\" trong danh sach tren.");
            return;
        }
        if (c.xemTheoPB != null) {
            c.xemTheoPB.accept(nam, maChon);
            return;
        }
        Dong[] kq = new Dong[0];
        for (Dong d : tatCa) if (d.maPB.equals(maChon)) kq = Mang.them(kq, d);
        hienPhanTrang(c, "Phong ban " + (maChon.isEmpty() ? "(chua phan)" : c.tenPB.apply(maChon) + " (" + maChon + ")"), kq, hoi);
    }

    //---- 3. xem het (neu nhieu dong va co ngay thi hoi loc theo thang) ----
    private static void xemHet(Cau c, Dong[] tatCa, Function<String, String> hoi) {
        Dong[] ds = tatCa;
        String tieu = "Tat ca";
        boolean coNgay = false;
        for (Dong d : tatCa) if (d.ngay != null) { coNgay = true; break; }
        if (coNgay && tatCa.length >= NGUONG_LOC_THANG) {
            GiaoDien.in("Co " + tatCa.length + " ban ghi, de xem de hon ban co the loc theo thang.");
            while (true) {
                String t = hoi.apply("Nhap thang (1-12) de loc, Enter = xem het: ").trim();
                if (t.isEmpty()) break;
                if (t.matches("\\d{1,2}") && Integer.parseInt(t) >= 1 && Integer.parseInt(t) <= 12) {
                    int thang = Integer.parseInt(t);
                    ds = new Dong[0];
                    for (Dong d : tatCa) if (d.ngay != null && d.ngay.getMonthValue() == thang) ds = Mang.them(ds, d);
                    tieu = "Thang " + thang;
                    break;
                }
                GiaoDien.in("Thang chi tu 1 den 12.");
            }
        }
        hienPhanTrang(c, tieu, ds, hoi);
    }

    //----------------------------------------------------------------------------------------------------
    /** In danh sach chia trang; chon STT de goi hanh dong (xem chi tiet / xu ly). */
    public static void hienPhanTrang(Cau c, String tieu, Dong[] ds, Function<String, String> hoi) {
        int tong = ds.length;
        if (tong == 0) {
            GiaoDien.in("(Khong co ban ghi nao)");
            return;
        }
        int soTrang = (tong + DONG_MOI_TRANG - 1) / DONG_MOI_TRANG;
        int trang = 1;
        boolean coPB = c.hienCotPB;
        int soCot = c.td.length + 1 + (coPB ? 1 : 0);
        String[] td = new String[soCot];
        int[] rong = new int[soCot];
        String[] canh = new String[soCot];
        int vt = 0;
        td[vt] = "STT"; rong[vt] = 5; canh[vt++] = GiaoDien.GIUA;
        if (coPB) { td[vt] = "PB"; rong[vt] = 6; canh[vt++] = GiaoDien.TRAI; }
        for (int i = 0; i < c.td.length; i++) { td[vt] = c.td[i]; rong[vt] = c.rong[i]; canh[vt++] = c.canh[i]; }
        while (true) {
            int tu = (trang - 1) * DONG_MOI_TRANG;
            int den = Math.min(tong, tu + DONG_MOI_TRANG);
            String[][] dong = new String[0][];
            for (int i = tu; i < den; i++) {
                Dong d = ds[i];
                String[] r = new String[soCot];
                int k = 0;
                r[k++] = String.valueOf(i + 1);
                if (coPB) r[k++] = d.maPB.isEmpty() ? "-" : d.maPB;
                for (int j = 0; j < c.td.length; j++) r[k++] = j < d.cot.length && d.cot[j] != null ? d.cot[j] : "";
                dong = Mang.them(dong, r);
            }
            GiaoDien.in("");
            GiaoDien.in(tieu + ": " + tong + " ban ghi - trang " + trang + "/" + soTrang + " (dong " + (tu + 1) + "-" + den + ")");
            GiaoDien.inBangKhongStt(td, rong, canh, dong);
            if (soTrang == 1 && c.khiChon == null) return;
            String[] lenh = new String[0];
            if (trang < soTrang) lenh = Mang.them(lenh, "Enter = trang sau");
            if (trang > 1) lenh = Mang.them(lenh, "p = trang truoc");
            if (soTrang > 2) lenh = Mang.them(lenh, "t<so> = den trang");
            if (c.khiChon != null) lenh = Mang.them(lenh, "STT = " + c.tenKhiChon);
            lenh = Mang.them(lenh, "0 = quay lai");
            String in = hoi.apply(String.join(" | ", lenh) + ": ").trim();
            if (in.equals("0")) return;
            if (in.isEmpty() || in.equalsIgnoreCase("n")) {
                if (trang < soTrang) trang++;
                else if (c.khiChon == null) return;
                else GiaoDien.in("Da o trang cuoi.");
            } else if (in.equalsIgnoreCase("p")) {
                if (trang > 1) trang--;
            } else if (in.toLowerCase().matches("t\\d{1,4}")) {
                int t = Integer.parseInt(in.substring(1));
                if (t >= 1 && t <= soTrang) trang = t;
                else GiaoDien.in("Trang chi tu 1 den " + soTrang + ".");
            } else if (c.khiChon != null && in.matches("\\d{1,6}")) {
                int stt = Integer.parseInt(in);
                if (stt >= 1 && stt <= tong) {
                    GiaoDien.in("");
                    c.khiChon.accept(ds[stt - 1]);
                    if (c.thoatSauKhiChon) return;
                    trang = (stt - 1) / DONG_MOI_TRANG + 1;       //quay lai dung trang vua xem
                } else GiaoDien.in("STT chi tu 1 den " + tong + ".");
            } else {
                GiaoDien.in("Lenh khong hop le.");
            }
        }
    }
}
