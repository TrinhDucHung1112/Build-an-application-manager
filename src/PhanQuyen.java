/**
 * Noi duy nhat quyet dinh "ai duoc lam gi voi ai" (CLASS MOI - phan quyen, chi chua ham tien ich).
 *
 * Quy tac:
 *  1. Quyen cua nguoi dung = bang quyen cua VaiTro (xem VaiTro), vai tro suy ra tu chuc vu + loai nhan vien.
 *  2. Pham vi TAT_CA        -> duoc phep voi moi nguoi.
 *     Pham vi PHONG_BAN     -> chi voi nguoi CUNG PHONG BAN (hoac chinh minh);
 *                              neu quyen yeu cau "cap duoi" thi doi tuong phai co cap bac thap hon.
 *     Pham vi BAN_THAN      -> chi voi chinh minh.
 *  3. Quyen khongTuLam (tu duyet, tu thuong, tu doi luong, tu xoa...) bi chan ke ca voi giam doc.
 */
public final class PhanQuyen {
    private PhanQuyen() {}

//----xac dinh vai tro----
    public static VaiTro xacDinhVaiTro(NhanVien nv) {
        if (nv == null) return VaiTro.NV_BAN_THOI_GIAN;     //khong ro -> vai tro thap nhat
        ChucVu cv = nv.getChucVu();
        if (cv != null && cv.getMaCV() != null) {
            switch (cv.getMaCV().toUpperCase()) {
                case "CV01": return VaiTro.GIAM_DOC;
                case "CV02": return VaiTro.TRUONG_PHONG;
                case "CV03": return VaiTro.PHO_PHONG;
                default: break;
            }
        }
        if (nv instanceof NhanVienHopDong) return VaiTro.NV_HOP_DONG;
        if (nv instanceof NhanVienBanThoiGian) return VaiTro.NV_BAN_THOI_GIAN;
        return VaiTro.NV_TOAN_THOI_GIAN;
    }

    public static int capBac(NhanVien nv) {
        return xacDinhVaiTro(nv).capBac();
    }

//----kiem tra quyen----
    public static PhamVi phamVi(NhanVien nguoi, Quyen q) {
        if (nguoi == null || q == null) return PhamVi.KHONG;
        return xacDinhVaiTro(nguoi).phamVi(q);
    }

    /** Co quyen nay o bat ky pham vi nao (dung cho thao tac khong co doi tuong cu the). */
    public static boolean coQuyen(NhanVien nguoi, Quyen q) {
        return phamVi(nguoi, q) != PhamVi.KHONG;
    }

    /** Nguoi dung co duoc thuc hien quyen q len nhan vien doiTuong khong. */
    public static boolean choPhep(NhanVien nguoi, Quyen q, NhanVien doiTuong) {
        PhamVi pv = phamVi(nguoi, q);
        if (pv == PhamVi.KHONG || doiTuong == null) return false;
        if (cungMa(nguoi, doiTuong)) return !q.khongChoTuLam();         //tac dong len chinh minh
        if (pv == PhamVi.BAN_THAN) return false;
        if (pv == PhamVi.TAT_CA) return true;
        //pham vi PHONG_BAN
        if (!cungPhong(nguoi, doiTuong)) return false;
        return !q.yeuCauCapDuoi() || capBac(doiTuong) < capBac(nguoi);
    }

    /** Quyen doi voi 1 phong ban (VD: xem phong ban): nhan vien chi xem duoc phong cua minh. */
    public static boolean choPhepPhongBan(NhanVien nguoi, Quyen q, PhongBan pb) {
        PhamVi pv = phamVi(nguoi, q);
        if (pv == PhamVi.KHONG || pb == null) return false;
        if (pv == PhamVi.TAT_CA) return true;
        PhongBan cua = nguoi.getPhongBan();
        return cua != null && cua.getMaPB() != null && cua.getMaPB().equalsIgnoreCase(pb.getMaPB());
    }

    /** Giu lai trong danh sach nhung nhan vien ma nguoi dung duoc phep thuc hien quyen q. */
    public static NhanVien[] loc(NhanVien nguoi, Quyen q, NhanVien[] danhSach) {
        if (danhSach == null) return new NhanVien[0];
        NhanVien[] tam = new NhanVien[danhSach.length];
        int dem = 0;
        for (NhanVien nv : danhSach) {
            if (nv != null && choPhep(nguoi, q, nv)) tam[dem++] = nv;
        }
        return java.util.Arrays.copyOf(tam, dem);
    }

    /** Nem KhongCoQuyenException neu khong du quyen. */
    public static void yeuCau(NhanVien nguoi, Quyen q, NhanVien doiTuong) {
        if (!choPhep(nguoi, q, doiTuong)) {
            String ai = doiTuong == null ? "" : " voi nhan vien " + doiTuong.getMaNV();
            throw new KhongCoQuyenException("Vai tro " + xacDinhVaiTro(nguoi).ten()
                    + " khong duoc phep: " + q.getMoTa() + ai + ".");
        }
    }

    public static void yeuCau(NhanVien nguoi, Quyen q) {
        if (!coQuyen(nguoi, q)) {
            throw new KhongCoQuyenException("Vai tro " + xacDinhVaiTro(nguoi).ten()
                    + " khong duoc phep: " + q.getMoTa() + ".");
        }
    }

//----ham phu----
    public static boolean cungMa(NhanVien a, NhanVien b) {
        return a != null && b != null && a.getMaNV().equalsIgnoreCase(b.getMaNV());
    }

    public static boolean cungPhong(NhanVien a, NhanVien b) {
        if (a == null || b == null) return false;
        PhongBan pa = a.getPhongBan();
        PhongBan pb = b.getPhongBan();
        return pa != null && pb != null && pa.getMaPB() != null && pa.getMaPB().equalsIgnoreCase(pb.getMaPB());
    }
}
