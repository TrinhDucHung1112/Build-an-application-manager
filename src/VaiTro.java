import java.util.EnumMap;

/**
 * Vai tro trong he thong (CLASS MOI - phan quyen).
 * Vai tro KHONG luu rieng ma suy ra tu chuc vu + loai nhan vien (xem PhanQuyen.xacDinhVaiTro),
 * nen doi chuc vu la quyen tu doi theo.
 *
 * Moi vai tro giu 1 bang: Quyen -> PhamVi (Quyen khong co trong bang = khong duoc phep).
 */
public enum VaiTro {
    GIAM_DOC("Giam doc", 4),
    TRUONG_PHONG("Truong phong", 3),
    PHO_PHONG("Pho phong", 2),
    NV_TOAN_THOI_GIAN("Nhan vien toan thoi gian", 1),
    NV_HOP_DONG("Nhan vien hop dong", 1),
    NV_BAN_THOI_GIAN("Nhan vien ban thoi gian", 1);

    private final String ten;
    private final int capBac;
    private final EnumMap<Quyen, PhamVi> bangQuyen = new EnumMap<>(Quyen.class);

    VaiTro(String ten, int capBac) {
        this.ten = ten;
        this.capBac = capBac;
    }

    private void cap(PhamVi phamVi, Quyen... cacQuyen) {
        for (Quyen q : cacQuyen) bangQuyen.put(q, phamVi);
    }

    //bang phan quyen cua toan he thong nam gon o day: muon doi quyen chi can sua o day
    static {
        //Giam doc: tat ca quyen, toan cong ty
        for (Quyen q : Quyen.values()) GIAM_DOC.bangQuyen.put(q, PhamVi.TAT_CA);

        //Truong phong: quan ly trong phong cua minh, chi tac dong len cap duoi (pho phong, nhan vien)
        TRUONG_PHONG.cap(PhamVi.PHONG_BAN,
                Quyen.XEM_PHONG_BAN, Quyen.XEM_NHAN_VIEN, Quyen.THEM_NHAN_VIEN, Quyen.SUA_NHAN_VIEN,
                Quyen.SUA_LIEN_HE, Quyen.CHO_NGHI_VIEC, Quyen.XEM_LUONG,
                Quyen.XEM_CHAM_CONG, Quyen.THEM_CHAM_CONG, Quyen.XOA_CHAM_CONG,
                Quyen.XEM_NGHI_PHEP, Quyen.DUYET_NGHI_PHEP, Quyen.XEM_BAO_HIEM,
                Quyen.XEM_THUONG_PHAT, Quyen.THEM_THUONG_PHAT,
                Quyen.XEM_BAO_CAO_PHONG, Quyen.XEM_HOP_DONG, Quyen.DUYET_YEU_CAU);

        //Pho phong: xem trong phong, cham cong + duyet nghi phep cho nhan vien thuong, khong xem luong nguoi khac
        PHO_PHONG.cap(PhamVi.PHONG_BAN,
                Quyen.XEM_PHONG_BAN, Quyen.XEM_NHAN_VIEN, Quyen.SUA_LIEN_HE,
                Quyen.XEM_CHAM_CONG, Quyen.THEM_CHAM_CONG,
                Quyen.XEM_NGHI_PHEP, Quyen.DUYET_NGHI_PHEP,
                Quyen.XEM_THUONG_PHAT, Quyen.XEM_BAO_CAO_PHONG);
        PHO_PHONG.cap(PhamVi.BAN_THAN, Quyen.XEM_LUONG, Quyen.XEM_BAO_HIEM, Quyen.XEM_HOP_DONG);

        //Truong phong, pho phong, nhan vien deu: tu cham cong, tao/xem/xoa don nghi phep cua minh, xem quy dinh
        for (VaiTro v : new VaiTro[]{TRUONG_PHONG, PHO_PHONG, NV_TOAN_THOI_GIAN, NV_HOP_DONG, NV_BAN_THOI_GIAN}) {
            v.cap(PhamVi.BAN_THAN, Quyen.TU_CHAM_CONG, Quyen.TAO_DON_NGHI_PHEP, Quyen.XOA_NGHI_PHEP);
            v.cap(PhamVi.TAT_CA, Quyen.XEM_QUY_DINH);
        }

        //Nhan vien (ca 3 loai): chi xem/sua lien he cua chinh minh
        for (VaiTro v : new VaiTro[]{NV_TOAN_THOI_GIAN, NV_HOP_DONG, NV_BAN_THOI_GIAN}) {
            v.cap(PhamVi.BAN_THAN,
                    Quyen.XEM_PHONG_BAN, Quyen.XEM_NHAN_VIEN, Quyen.SUA_LIEN_HE, Quyen.XEM_LUONG,
                    Quyen.XEM_CHAM_CONG, Quyen.XEM_NGHI_PHEP, Quyen.XEM_BAO_HIEM, Quyen.XEM_THUONG_PHAT);
        }
        //chi nhan vien hop dong moi co hop dong lao dong de xem (toan thoi gian, ban thoi gian khong co)
        NV_HOP_DONG.cap(PhamVi.BAN_THAN, Quyen.XEM_HOP_DONG);
    }

    public String ten() {
        return ten;
    }

    public int capBac() {
        return capBac;
    }

    /** Pham vi ap dung cua quyen (PhamVi.KHONG neu vai tro khong co quyen nay). */
    public PhamVi phamVi(Quyen q) {
        PhamVi pv = bangQuyen.get(q);
        return pv == null ? PhamVi.KHONG : pv;
    }

    public boolean coQuyen(Quyen q) {
        return phamVi(q) != PhamVi.KHONG;
    }

    /** Giam doc, truong phong, pho phong dung menu quan ly; nhan vien dung menu ca nhan. */
    public boolean laQuanLy() {
        return capBac >= 2;
    }

    @Override
    public String toString() {
        return ten;
    }
}
