import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Function;

/**
 * Bo chon NAM / THANG dung chung cho nhom chuc nang "Chung" (CLASS MOI, chi chua ham tien ich).
 *
 *  - chonNam   : in danh sach cac nam (kem trang thai) tu nam vao lam den nam hien tai, nhap SO THU TU hoac NGAY THANG de chon.
 *  - chonThang : in danh sach 12 thang cua 1 nam (kem trang thai), nhap SO THANG (1-12) hoac NGAY THANG de chon.
 *
 * Nhap ngay thang chi nhan tu ngay vao lam den ngay HOM NAY; nhap qua hom nay / truoc ngay vao lam
 * thi bao loi kem chu thich khoang hop le.
 * Nhap 0 (hoac de trong) de quay lai: ham tra ve null.
 */
public final class ChonKy {
    private ChonKy() {}

    /** Ket qua chon: nam, va thang (0 neu nguoi dung khong nhap thang). */
    public static final class KetQua {
        public final int nam;
        public final int thang;
        KetQua(int nam, int thang) {
            this.nam = nam;
            this.thang = thang;
        }
    }

    //----------------- chon nam -----------------
    /**
     * @param cacNam    cac nam duoc phep chon, theo thu tu tang dan
     * @param tieuDeCot tieu de cac cot (khong tinh STT); cot dau tien phai la NAM
     * @param dong      moi nam 1 dong, cung thu tu voi cacNam (cot dau tien = nam)
     * @param tuNgay    ngay som nhat duoc nhap (thuong la ngay vao lam)
     */
    public static KetQua chonNam(int[] cacNam, String[] tieuDeCot, int[] rong, String[] canh,
                                 String[][] dong, LocalDate tuNgay, Function<String, String> hoi) {
        if ((cacNam.length == 0)) {
            GiaoDien.in("Khong co nam nao de chon.");
            return null;
        }
        LocalDate homNay = LocalDate.now();
        GiaoDien.inBang(tieuDeCot, rong, canh, dong);
        GiaoDien.in("Nhap SO THU TU (1-" + cacNam.length + ") hoac NGAY THANG (yyyy-MM-dd hoac yyyy-MM) de chon, 0 de quay lai.");
        GiaoDien.in("(Chi nhap tu " + tuNgay + " den hom nay " + homNay + ")");
        int namMin = cacNam[0];
        int namMax = cacNam[cacNam.length - 1];
        while (true) {
            String nhap = hoi.apply("Chon: ").trim();
            if (nhap.isEmpty() || nhap.equals("0")) return null;

            if (nhap.matches("\\d{1,3}")) {                                     //so thu tu
                int stt = Integer.parseInt(nhap);
                if (stt >= 1 && stt <= cacNam.length) return new KetQua(cacNam[stt - 1], 0);
                GiaoDien.in("Sai: so thu tu chi tu 1 den " + cacNam.length + ".");
                continue;
            }
            if (nhap.matches("\\d{4}")) {                                       //nhap thang nam
                int nam = Integer.parseInt(nhap);
                if (Mang.chua(cacNam, nam)) return new KetQua(nam, 0);
                GiaoDien.in("Sai: nam " + nam + " ngoai khoang cho phep. Nhap nam tu " + namMin + " den " + namMax
                        + " (tu ngay vao lam " + tuNgay + " den hom nay " + homNay + ").");
                continue;
            }
            YearMonth ym;
            int thang = 0;
            try {
                if (nhap.matches("\\d{4}-\\d{1,2}")) {
                    ym = YearMonth.parse(nhap.length() == 6 ? nhap.substring(0, 5) + "0" + nhap.substring(5) : nhap);
                    thang = ym.getMonthValue();
                    if (ym.atDay(1).isAfter(homNay) || ym.atEndOfMonth().isBefore(tuNgay)) throw new IllegalStateException();
                } else {
                    LocalDate d = LocalDate.parse(nhap);
                    ym = YearMonth.from(d);
                    thang = d.getMonthValue();
                    if (d.isAfter(homNay) || d.isBefore(tuNgay)) throw new IllegalStateException();
                }
            } catch (java.time.format.DateTimeParseException e) {
                GiaoDien.in("Sai dinh dang. Nhap so thu tu, hoac ngay theo yyyy-MM-dd (VD " + homNay + "), hoac yyyy-MM.");
                continue;
            } catch (IllegalStateException e) {
                GiaoDien.in("Sai: chi nhap tu ngay vao lam " + tuNgay + " den ngay hom nay " + homNay + " (khong nhap ngay trong tuong lai).");
                continue;
            }
            if (!Mang.chua(cacNam, ym.getYear())) {
                GiaoDien.in("Sai: nam " + ym.getYear() + " ngoai khoang cho phep (" + namMin + " - " + namMax + ").");
                continue;
            }
            return new KetQua(ym.getYear(), thang);
        }
    }

    //----------------- chon thang -----------------
    /**
     * @param nam       nam dang chon
     * @param tieuDeCot tieu de cac cot (khong tinh STT); cot dau tien phai la THANG
     * @param dong      DUNG 12 dong, thang 1..12 (STT = so thang)
     * @param lyDoKhong lyDoKhong[t-1] = null neu thang t chon duoc, nguoc lai la ly do khong chon duoc
     */
    public static Integer chonThang(int nam, String[] tieuDeCot, int[] rong, String[] canh,
                                    String[][] dong, String[] lyDoKhong, LocalDate tuNgay,
                                    Function<String, String> hoi) {
        LocalDate homNay = LocalDate.now();
        GiaoDien.inBang(tieuDeCot, rong, canh, dong);
        GiaoDien.in("Nhap SO THANG (1-12) hoac NGAY THANG (yyyy-MM-dd hoac yyyy-MM) trong nam " + nam + ", 0 de quay lai.");
        GiaoDien.in("(Chi nhap tu " + tuNgay + " den hom nay " + homNay + ")");
        while (true) {
            String nhap = hoi.apply("Chon thang: ").trim();
            if (nhap.isEmpty() || nhap.equals("0")) return null;
            int thang;
            if (nhap.matches("\\d{1,2}")) {
                thang = Integer.parseInt(nhap);
                if (thang < 1 || thang > 12) {
                    GiaoDien.in("Sai: thang chi tu 1 den 12.");
                    continue;
                }
            } else {
                try {
                    if (nhap.matches("\\d{4}-\\d{1,2}")) {
                        String[] p = nhap.split("-");
                        YearMonth ym = YearMonth.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
                        if (ym.getYear() != nam) throw new IllegalStateException("nam");
                        thang = ym.getMonthValue();
                    } else {
                        LocalDate d = LocalDate.parse(nhap);
                        if (d.getYear() != nam) throw new IllegalStateException("nam");
                        if (d.isAfter(homNay) || d.isBefore(tuNgay)) throw new IllegalStateException("ngay");
                        thang = d.getMonthValue();
                    }
                } catch (java.time.DateTimeException e) {
                    GiaoDien.in("Sai dinh dang. Nhap so thang 1-12, hoac ngay theo yyyy-MM-dd (VD " + homNay + "), hoac yyyy-MM.");
                    continue;
                } catch (IllegalStateException e) {
                    if (e.getMessage().equals("nam")) GiaoDien.in("Sai: ban dang chon nam " + nam + ", hay nhap thang thuoc nam " + nam + ".");
                    else GiaoDien.in("Sai: chi nhap tu ngay vao lam " + tuNgay + " den ngay hom nay " + homNay + " (khong nhap ngay trong tuong lai).");
                    continue;
                }
            }
            if (lyDoKhong[thang - 1] != null) {
                GiaoDien.in("Khong chon duoc thang " + thang + "/" + nam + " (" + lyDoKhong[thang - 1].toLowerCase() + ").");
                continue;
            }
            return thang;
        }
    }

    //----------------- ham phu -----------------
    /** Cac nam tu nam cua tuNgay den nam cuoiNam (bao gom). */
    public static int[] cacNam(LocalDate tuNgay, int cuoiNam) {
        int[] kq = new int[0];
        for (int n = tuNgay.getYear(); n <= cuoiNam; n++) kq = Mang.them(kq, n);
        return kq;
    }

    /** Trang thai thang so voi khoang [tuNgay, hom nay]: null neu hop le, nguoc lai la ly do. */
    public static String lyDoThangKhongHopLe(int nam, int thang, LocalDate tuNgay) {
        YearMonth ym = YearMonth.of(nam, thang);
        if (ym.atEndOfMonth().isBefore(tuNgay)) return "Chua vao lam";
        if (ym.atDay(1).isAfter(LocalDate.now())) return "Chua den";
        return null;
    }

    /** "Hien tai" cho nam nay, "Da qua" cho cac nam truoc, "Sap toi" cho nam sau. */
    public static String trangThaiNam(int nam) {
        int nay = LocalDate.now().getYear();
        return nam == nay ? "Hien tai" : nam < nay ? "Da qua" : "Sap toi";
    }
}
