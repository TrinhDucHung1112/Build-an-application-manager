import java.util.ArrayList;
import java.util.List;

/**
 * Tien ich giao dien dung chung cho toan chuong trinh (CLASS MOI).
 *
 *  - batDau(...) / ketThuc(): moi lan nhap hoac xuat deu nam giua 2 dong -------------
 *  - inBang(...): in bang co cot STT, tu dong xuong dong khi noi dung dai
 *  - inThongTin(...): in bang 2 cot (ten thong tin | gia tri) cho 1 doi tuong
 *
 * Neu terminal hep/rong hon, chi can sua DO_RONG ben duoi.
 */
public final class GiaoDien {
    public static final int DO_RONG = 110;      // chieu rong cua dong -----------
    public static final String LE = "  ";       // thut dau dong cho chu nam trong khung

    public static final String TRAI = "T";      // can trai (chu)
    public static final String PHAI = "P";      // can phai (so, tien)
    public static final String GIUA = "G";      // can giua

    private GiaoDien() {}

    //ham doc 1 dong tu ban phim (do Main gan) de chia trang cac bang dai
    private static java.util.function.Function<String, String> docDong;
    private static final int NGUONG_CHIA_TRANG = 30;     //bang co nhieu hon so dong nay thi tu chia trang
    private static final int DONG_MOI_TRANG = 20;

    public static void setDocDong(java.util.function.Function<String, String> f) {
        docDong = f;
    }

//----khung nhap/xuat----
    public static void batDau(String tieuDe) {
        String t = " " + tieuDe + " ";
        int con = Math.max(4, DO_RONG - t.length());
        int trai = con / 2;
        int phai = con - trai;
        System.out.println();
        System.out.println("-".repeat(trai) + t + "-".repeat(phai));
    }

    public static void ketThuc() {
        System.out.println("-".repeat(DO_RONG));
    }

//----in dong chu / hoi nhap----
    public static void in(String noiDung) {
        for (String d : noiDung.split("\n", -1)) System.out.println(d.isEmpty() ? "" : LE + d);
    }

    public static void hoi(String cauHoi) {
        System.out.print(LE + cauHoi);
    }

    public static void tieuMuc(String tieuDe) {
        System.out.println();
        System.out.println(LE + "[" + tieuDe + "]");
    }

    public static String tien(double x) {
        return String.format("%,.0f", x);
    }

//----in bang co STT----
    public static void inBang(String[] tieuDe, int[] rongToiDa, String[] canh, List<String[]> dong) {
        if (dong == null || dong.isEmpty()) {
            in("(Khong co du lieu)");
            return;
        }
        int n = tieuDe.length + 1;
        String[] td = new String[n];
        int[] max = new int[n];
        String[] ca = new String[n];
        td[0] = "STT";
        max[0] = 5;
        ca[0] = GIUA;
        for (int c = 1; c < n; c++) {
            td[c] = tieuDe[c - 1];
            max[c] = rongToiDa[c - 1];
            ca[c] = canh[c - 1];
        }
        List<String[]> tatCa = new ArrayList<>();
        for (int i = 0; i < dong.size(); i++) {
            String[] g = dong.get(i);
            String[] h = new String[n];
            h[0] = String.valueOf(i + 1);
            for (int c = 1; c < n; c++) {
                h[c] = (c - 1 < g.length && g[c - 1] != null) ? g[c - 1] : "";
            }
            tatCa.add(h);
        }
        ve(td, max, ca, tatCa, true);
    }

//----in bang khong co STT (dung cho thong tin chi tiet / thong ke)----
    public static void inBangKhongStt(String[] tieuDe, int[] rongToiDa, String[] canh, List<String[]> dong) {
        if (dong == null || dong.isEmpty()) {
            in("(Khong co du lieu)");
            return;
        }
        ve(tieuDe, rongToiDa, canh, dong, false);
    }

    //bang 2 cot: THONG TIN | GIA TRI
    public static void inThongTin(List<String[]> cap) {
        inBangKhongStt(new String[]{"THONG TIN", "GIA TRI"}, new int[]{26, 70},
                new String[]{TRAI, TRAI}, cap);
    }

    //bang 2 cot: CHI TIEU | GIA TRI (dung cho thong ke / bao cao)
    public static void inThongKe(List<String[]> cap) {
        inBangKhongStt(new String[]{"CHI TIEU", "GIA TRI"}, new int[]{34, 60},
                new String[]{TRAI, TRAI}, cap);
    }

    //bang 2 cot tuy chinh ten cot, cot 2 can phai (dung cho tien)
    public static void inThongTin(String cot1, String cot2, List<String[]> cap) {
        inBangKhongStt(new String[]{cot1, cot2}, new int[]{40, 30},
                new String[]{TRAI, PHAI}, cap);
    }

//----ve bang----
    private static void ve(String[] td, int[] max, String[] canh, List<String[]> dong, boolean choPhepChiaTrang) {
        int n = td.length;
        int[] rong = new int[n];
        for (int c = 0; c < n; c++) {
            int w = doDaiDongDai(td[c]);
            for (String[] d : dong) w = Math.max(w, doDaiDongDai(d[c]));
            rong[c] = Math.min(w, max[c]);
        }

        //do dai tu dai nhat cua moi cot: khong bop cot nho hon muc nay de khong gay ma/so giua chung
        int[] tuDai = new int[n];
        for (int c = 0; c < n; c++) {
            int m = tuDaiNhat(td[c]);
            for (String[] d : dong) m = Math.max(m, tuDaiNhat(d[c]));
            tuDai[c] = Math.min(m, max[c]);
        }

        //thu hep cac cot chu (khong dong vao cot so) neu bang rong hon khung
        while (tongRong(rong) > DO_RONG - LE.length()) {
            int viTri = -1;
            for (int c = 0; c < n; c++) {
                if (TRAI.equals(canh[c]) && rong[c] > Math.max(6, tuDai[c])
                        && (viTri == -1 || rong[c] > rong[viTri])) viTri = c;
            }
            if (viTri == -1) break;
            rong[viTri]--;
        }

        StringBuilder ngang = new StringBuilder("+");
        for (int c = 0; c < n; c++) ngang.append("-".repeat(rong[c] + 2)).append("+");

        //bang dai thi chia trang (cot giu nguyen do rong giua cac trang)
        int tong = dong.size();
        boolean chia = choPhepChiaTrang && docDong != null && tong > NGUONG_CHIA_TRANG;
        int moiTrang = chia ? DONG_MOI_TRANG : Math.max(1, tong);
        int soTrang = (tong + moiTrang - 1) / moiTrang;
        for (int tu = 0, trang = 1; tu < tong; tu += moiTrang, trang++) {
            int den = Math.min(tong, tu + moiTrang);
            System.out.println(LE + ngang);
            inHang(td, rong, canh, true);
            System.out.println(LE + ngang);
            for (int i = tu; i < den; i++) inHang(dong.get(i), rong, canh, false);
            System.out.println(LE + ngang);
            if (chia && den < tong) {
                String r = docDong.apply("Trang " + trang + "/" + soTrang + " (" + tong + " dong). Enter = trang tiep, q = dung xem: ");
                if (r.trim().equalsIgnoreCase("q")) break;
            }
        }
    }

    private static int tongRong(int[] rong) {
        int tong = 1;
        for (int w : rong) tong += w + 3;
        return tong;
    }

    //in 1 hang cua bang, o nao dai qua thi xuong dong trong chinh o do
    private static void inHang(String[] o, int[] rong, String[] canh, boolean laTieuDe) {
        List<List<String>> cot = new ArrayList<>();
        int cao = 1;
        for (int c = 0; c < rong.length; c++) {
            List<String> l = cat(o[c], rong[c]);
            cot.add(l);
            cao = Math.max(cao, l.size());
        }
        for (int i = 0; i < cao; i++) {
            StringBuilder sb = new StringBuilder(LE).append("|");
            for (int c = 0; c < rong.length; c++) {
                String s = i < cot.get(c).size() ? cot.get(c).get(i) : "";
                sb.append(" ").append(can(s, rong[c], laTieuDe ? GIUA : canh[c])).append(" |");
            }
            System.out.println(sb);
        }
    }

    private static String can(String s, int w, String kieu) {
        int thieu = Math.max(0, w - s.length());
        if (PHAI.equals(kieu)) return " ".repeat(thieu) + s;
        if (GIUA.equals(kieu)) {
            int t = thieu / 2;
            return " ".repeat(t) + s + " ".repeat(thieu - t);
        }
        return s + " ".repeat(thieu);
    }

    private static int doDaiDongDai(String s) {
        if (s == null) return 0;
        int max = 0;
        for (String d : s.split("\n", -1)) max = Math.max(max, d.length());
        return max;
    }

    private static int tuDaiNhat(String s) {
        if (s == null) return 0;
        int max = 0;
        for (String tu : s.split("\\s+")) max = Math.max(max, tu.length());
        return max;
    }

    //cat chuoi thanh nhieu dong rong toi da w, uu tien cat o dau cach
    private static List<String> cat(String s, int w) {
        List<String> kq = new ArrayList<>();
        if (s == null) s = "";
        s = s.replace('\t', ' ');
        for (String doan : s.split("\n", -1)) {
            String con = doan.stripTrailing();
            int thut = 0;                                   //giu khoang thut dau dong (vd "   + THUONG: ...")
            while (thut < con.length() && con.charAt(thut) == ' ') thut++;
            String dauDong = " ".repeat(Math.min(thut, Math.max(0, w / 2)));
            if (thut > 0) con = dauDong + con.substring(thut);
            while (con.length() > w) {
                int cut = con.lastIndexOf(' ', w);
                if (cut <= dauDong.length()) cut = w;       //khong co cho cat dep thi cat cung
                kq.add(con.substring(0, cut).stripTrailing());
                con = dauDong + con.substring(cut).trim();
            }
            kq.add(con);
        }
        return kq;
    }
}
