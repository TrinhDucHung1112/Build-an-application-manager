import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Tien ich doc/ghi file van ban (text) dung chung cho moi lop danh sach.
 * Moi dong la 1 doi tuong, cac truong ngan cach boi dau '|'.
 * Dong trong va dong bat dau bang '#' duoc bo qua khi doc (dung de ghi chu).
 */
public final class TepVanBan {
    public static final String PHAN_CACH = "|";

    private TepVanBan() {}

    /** Doc file, tra ve moi dong du lieu thanh mang cac truong (da trim). */
    public static String[][] docDong(String duongDan) throws IOException {
        String[][] ketQua = new String[0][];
        try (BufferedReader br = Files.newBufferedReader(new File(duongDan).toPath(), StandardCharsets.UTF_8)) {
            String dong;
            while ((dong = br.readLine()) != null) {
                if (dong.startsWith("\uFEFF")) dong = dong.substring(1);   // bo BOM neu co
                if (dong.isBlank() || dong.trim().startsWith("#")) continue;
                String[] p = dong.split("\\|", -1);
                for (int i = 0; i < p.length; i++) p[i] = p[i].trim();
                ketQua = Mang.them(ketQua, p);
            }
        }
        return ketQua;
    }

    /** Ghi de toan bo file: 1 dong tieu de (ghi chu) roi cac dong du lieu. */
    public static void ghiDong(String duongDan, String tieuDe, String[] cacDong) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(new File(duongDan).toPath(), StandardCharsets.UTF_8)) {
            bw.write("# " + tieuDe);
            bw.newLine();
            for (String d : cacDong) {
                bw.write(d);
                bw.newLine();
            }
        }
    }

    /** Ghep cac truong thanh 1 dong, tu dong lam sach ky tu ngan cach. */
    public static String ghep(Object... cacTruong) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cacTruong.length; i++) {
            if (i > 0) sb.append(PHAN_CACH);
            Object o = cacTruong[i];
            if (o instanceof Double || o instanceof Float) sb.append(soDep(((Number) o).doubleValue()));
            else sb.append(lamSach(o == null ? "" : String.valueOf(o)));
        }
        return sb.toString();
    }

    /** In so thuc khong dung ky hieu khoa hoc: 2.0E7 -> 20000000, 0.5 -> 0.5 */
    public static String soDep(double x) {
        return new java.math.BigDecimal(Double.toString(x)).stripTrailingZeros().toPlainString();
    }

    /** Bo ky tu '|' va xuong dong de khong lam vo cau truc dong. */
    public static String lamSach(String s) {
        return s.replace('|', '/').replace('\r', ' ').replace('\n', ' ');
    }

    /** Dung cho truong con (chuc vu, trinh do) vi dung them ';' va '~' lam ngan cach. */
    public static String lamSachCon(String s) {
        return lamSach(s == null ? "" : s).replace(';', ',').replace('~', '-');
    }
}
