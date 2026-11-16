/**
 * Quy tac dat ma nhan vien va mat khau mac dinh (CLASS MOI, chi chua ham tien ich).
 *
 *   Giam doc ............ GD001, GD002, ...
 *   Truong phong ........ TP001, TP002, ...
 *   Pho phong ........... PP001, PP002, ...
 *   Nhan vien toan thoi gian ... NVT001, ...
 *   Nhan vien ban thoi gian .... NVB001, ...
 *   Nhan vien hop dong ......... NVH001, ...
 *
 * Mat khau mac dinh = phan SO + phan CHU cua ma.  VD: TP001 -> 001TP, NVT002 -> 002NVT
 */
public final class MaNhanVien {
    public static final String GIAM_DOC = "GD";
    public static final String TRUONG_PHONG = "TP";
    public static final String PHO_PHONG = "PP";
    public static final String NV_TOAN_THOI_GIAN = "NVT";
    public static final String NV_BAN_THOI_GIAN = "NVB";
    public static final String NV_HOP_DONG = "NVH";

    private MaNhanVien() {}

    /** Tien to theo chuc vu truoc (GD/TP/PP), la nhan vien thuong thi theo loai hinh lam viec. */
    public static String tienTo(int loai, ChucVu chucVu) {
        if (chucVu != null) {
            switch (chucVu.getMaCV()) {
                case "CV01": return GIAM_DOC;
                case "CV02": return TRUONG_PHONG;
                case "CV03": return PHO_PHONG;
                default: break;
            }
        }
        switch (loai) {
            case 1: return NV_TOAN_THOI_GIAN;
            case 2: return NV_BAN_THOI_GIAN;
            default: return NV_HOP_DONG;
        }
    }

    /** Tao ma moi: lay so lon nhat dang co cua tien to do, cong 1, dem du 3 chu so. */
    public static String taoMaMoi(String tienTo, NhanVien[] danhSach) {
        int max = 0;
        if (danhSach != null) {
            for (NhanVien nv : danhSach) {
                if (nv == null) continue;
                String ma = nv.getMaNV().toUpperCase();
                if (!ma.startsWith(tienTo)) continue;
                String phanSo = ma.substring(tienTo.length());
                if (phanSo.matches("\\d+")) {
                    try { max = Math.max(max, Integer.parseInt(phanSo)); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return String.format("%s%03d", tienTo, max + 1);
    }

    /** TP001 -> 001TP. Ma khong co so thi dung chinh ma lam mat khau. */
    public static String matKhauMacDinh(String maNV) {
        String ma = maNV.trim().toUpperCase();
        String so = ma.replaceAll("[^0-9]", "");
        String chu = ma.replaceAll("[^A-Z]", "");
        return so.isEmpty() ? ma : so + chu;
    }
}
