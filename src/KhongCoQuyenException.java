/**
 * Nem ra khi nguoi dung thuc hien thao tac ngoai quyen (CLASS MOI - phan quyen).
 * Main.chay(...) bat ngoai le nay va bao "KHONG DU QUYEN" cho nguoi dung.
 */
public class KhongCoQuyenException extends RuntimeException {
    public KhongCoQuyenException(String thongBao) {
        super(thongBao);
    }
}
