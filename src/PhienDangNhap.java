import java.time.LocalDateTime;

/**
 * Phien dang nhap hien tai: nhan vien + vai tro + luc dang nhap (CLASS MOI - phan quyen).
 * Thay cho bien static "nguoiDangNhap" trong Main; vai tro duoc tinh san 1 lan khi tao phien.
 * Phien la bat bien: khi du lieu duoc doc lai tu file thi dung lamMoi(...) de tao phien moi cung thoi diem dang nhap.
 */
public class PhienDangNhap {
    private final NhanVien nguoiDung;
    private final VaiTro vaiTro;
    private final LocalDateTime thoiDiemDangNhap;

    public PhienDangNhap(NhanVien nguoiDung) {
        this(nguoiDung, LocalDateTime.now());
    }

    private PhienDangNhap(NhanVien nguoiDung, LocalDateTime thoiDiem) {
        if (nguoiDung == null) throw new IllegalArgumentException("Phien dang nhap phai gan voi 1 nhan vien");
        this.nguoiDung = nguoiDung;
        this.vaiTro = PhanQuyen.xacDinhVaiTro(nguoiDung);
        this.thoiDiemDangNhap = thoiDiem;
    }

    public NhanVien getNguoiDung() { return nguoiDung; }
    public VaiTro getVaiTro() { return vaiTro; }
    public LocalDateTime getThoiDiemDangNhap() { return thoiDiemDangNhap; }
    public String getMaNV() { return nguoiDung.getMaNV(); }

    /** Phien moi cho cung nguoi dung (sau khi doc lai du lieu), giu nguyen thoi diem dang nhap. */
    public PhienDangNhap lamMoi(NhanVien nguoiDungMoi) {
        return new PhienDangNhap(nguoiDungMoi, thoiDiemDangNhap);
    }
}
