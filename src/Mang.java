import java.util.Comparator;

/**
 * Tien ich thao tac tren MANG THUAN (array) dung chung cho ca du an.
 * Thay cho ArrayList / List: moi ham tao ra 1 MANG MOI co kich thuoc dung bang so phan tu,
 * nen khong dung bat ky cau truc du lieu dong nao cua java.util (ArrayList, Vector, LinkedList, HashMap...).
 * Cac ham deu static.
 */
public final class Mang {
    private Mang() {}

    /** Them 1 phan tu vao cuoi mang, tra ve mang moi. */
    public static <T> T[] them(T[] a, T x) {
        T[] b = java.util.Arrays.copyOf(a, a.length + 1);
        b[a.length] = x;
        return b;
    }

    public static int[] them(int[] a, int x) {
        int[] b = new int[a.length + 1];
        System.arraycopy(a, 0, b, 0, a.length);
        b[a.length] = x;
        return b;
    }

    /** Them toan bo mang b vao cuoi mang a, tra ve mang moi. */
    public static <T> T[] themTatCa(T[] a, T[] b) {
        T[] kq = java.util.Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, kq, a.length, b.length);
        return kq;
    }

    /** Xoa phan tu o vi tri i, tra ve mang moi (neu i sai thi tra ve mang cu). */
    public static <T> T[] xoaTai(T[] a, int i) {
        if (i < 0 || i >= a.length) return a;
        T[] b = java.util.Arrays.copyOf(a, a.length - 1);
        System.arraycopy(a, i + 1, b, i, a.length - i - 1);
        return b;
    }

    /** Vi tri cua phan tu (so sanh bang equals), -1 neu khong co. */
    public static <T> int viTri(T[] a, T x) {
        for (int i = 0; i < a.length; i++) if (a[i] == null ? x == null : a[i].equals(x)) return i;
        return -1;
    }

    public static int viTri(int[] a, int x) {
        for (int i = 0; i < a.length; i++) if (a[i] == x) return i;
        return -1;
    }

    public static <T> boolean chua(T[] a, T x) { return viTri(a, x) >= 0; }

    public static boolean chua(int[] a, int x) { return viTri(a, x) >= 0; }

    /** Xoa phan tu dau tien bang x, tra ve mang moi. */
    public static <T> T[] xoa(T[] a, T x) { return xoaTai(a, viTri(a, x)); }

    /** Sap xep tang dan theo bo so sanh (sap xep chen, on dinh). */
    public static <T> void sapXep(T[] a, Comparator<? super T> cmp) {
        for (int i = 1; i < a.length; i++) {
            T khoa = a[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(a[j], khoa) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = khoa;
        }
    }

    /** Sap xep tang dan mang so nguyen. */
    public static void sapXep(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int khoa = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > khoa) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = khoa;
        }
    }

    /** Dem so phan tu thoa dieu kien. */
    public static <T> int dem(T[] a, java.util.function.Predicate<? super T> dk) {
        int d = 0;
        for (T x : a) if (dk.test(x)) d++;
        return d;
    }

    /** Giu lai cac phan tu thoa dieu kien (thay cho removeIf), tra ve mang moi. */
    public static <T> T[] loc(T[] a, java.util.function.Predicate<? super T> giuLai) {
        T[] b = java.util.Arrays.copyOf(a, 0);
        for (T x : a) if (giuLai.test(x)) b = them(b, x);
        return b;
    }
}
