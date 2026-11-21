/**
 * Danh sach cac quyen trong he thong (CLASS MOI - phan quyen).
 *
 *  - capDuoi   : khi tac dong len nguoi KHAC trong phong ban, doi tuong phai co cap bac THAP HON nguoi thuc hien
 *                (truong phong khong sua duoc truong phong khac, pho phong khong sua duoc truong phong...).
 *  - khongTuLam: khong duoc tu thuc hien len chinh minh (tu duyet don nghi phep, tu thuong, tu doi luong, tu xoa...).
 *
 * Quyen nao thuoc vai tro nao, va ap dung trong pham vi nao: xem VaiTro.
 */
public enum Quyen {
    //--- phong ban ---
    XEM_PHONG_BAN("Xem phong ban", false, false),
    QUAN_LY_PHONG_BAN("Them/xoa phong ban", false, false),

    //--- nhan vien ---
    XEM_NHAN_VIEN("Xem ho so nhan vien", false, false),
    THEM_NHAN_VIEN("Them nhan vien", true, false),
    SUA_NHAN_VIEN("Sua thong tin nhan vien", true, false),
    SUA_LIEN_HE("Sua so dien thoai, email", true, false),
    DOI_LUONG_CHUC_VU("Doi luong, chuc vu, phong ban, trinh do", true, true),
    CHO_NGHI_VIEC("Cho nhan vien nghi viec", true, true),
    XOA_NHAN_VIEN("Xoa nhan vien khoi he thong", true, true),

    //--- luong ---
    XEM_LUONG("Xem luong va bang luong", true, false),
    LAP_BANG_LUONG("Lap bang luong, thong ke quy luong", false, false),

    //--- cham cong ---
    XEM_CHAM_CONG("Xem cham cong", true, false),
    THEM_CHAM_CONG("Them cham cong cho nhan vien", true, true),
    XOA_CHAM_CONG("Xoa cham cong", true, true),
    TU_CHAM_CONG("Tu cham cong hom nay", false, false),

    //--- nghi phep ---
    TAO_DON_NGHI_PHEP("Tao don nghi phep", false, false),
    XEM_NGHI_PHEP("Xem don nghi phep", true, false),
    DUYET_NGHI_PHEP("Duyet don nghi phep", true, true),
    XOA_NGHI_PHEP("Xoa don nghi phep", false, false),

    //--- bao hiem ---
    XEM_BAO_HIEM("Xem bao hiem", true, false),
    QUAN_LY_BAO_HIEM("Them/xoa bao hiem", false, false),

    //--- thuong phat ---
    XEM_THUONG_PHAT("Xem thuong/phat", true, false),
    THEM_THUONG_PHAT("Them thuong/phat", true, true),
    XOA_THUONG_PHAT("Xoa thuong/phat", false, false),
    TU_DONG_XET_THUONG_PHAT("Tu dong xet thuong/phat", false, false),
    XEM_QUY_DINH("Xem quy dinh thuong phat", false, false),
    QUAN_LY_QUY_DINH("Them/xoa quy dinh thuong phat", false, false),

    //--- bao cao, hop dong ---
    XEM_BAO_CAO_CONG_TY("Xem bao cao toan cong ty", false, false),
    XEM_BAO_CAO_PHONG("Xem bao cao phong ban", false, false),
    XEM_HOP_DONG("Xem hop dong lao dong", true, false),

    //--- yeu cau (nhan vien/truong phong/pho phong gui yeu cau sua ho so len cap tren) ---
    DUYET_YEU_CAU("Duyet yeu cau sua ho so", true, true),

    //--- he thong ---
    QUAN_LY_TAI_KHOAN("Quan ly tai khoan (duyet, reset, khoa/mo khoa)", false, false),
    XEM_NHAT_KY("Xem nhat ky he thong", false, false),
    DOC_DU_LIEU("Doc lai du lieu tu file", false, false);

    private final String moTa;
    private final boolean capDuoi;
    private final boolean khongTuLam;

    Quyen(String moTa, boolean capDuoi, boolean khongTuLam) {
        this.moTa = moTa;
        this.capDuoi = capDuoi;
        this.khongTuLam = khongTuLam;
    }

    public String getMoTa() {
        return moTa;
    }

    public boolean yeuCauCapDuoi() {
        return capDuoi;
    }

    public boolean khongChoTuLam() {
        return khongTuLam;
    }
}
