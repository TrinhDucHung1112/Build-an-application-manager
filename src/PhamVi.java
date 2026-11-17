/**
 * Pham vi ap dung cua 1 quyen (CLASS MOI - phan quyen).
 * Thu tu tu hep den rong: KHONG < BAN_THAN < PHONG_BAN < TAT_CA.
 */
public enum PhamVi {
    KHONG("Khong co quyen"),
    BAN_THAN("Chi du lieu cua ban than"),
    PHONG_BAN("Trong pham vi phong ban cua minh"),
    TAT_CA("Toan cong ty");

    private final String moTa;

    PhamVi(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    /** true neu pham vi nay rong hon hoac bang pham vi khac. */
    public boolean baoGom(PhamVi khac) {
        return this.ordinal() >= khac.ordinal();
    }
}
