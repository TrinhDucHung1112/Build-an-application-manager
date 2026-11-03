import java.time.LocalDate;

public class BaoHiemXaHoi extends BaoHiem {
    private static final double TY_LE_NV = 0.08;   // Nhân viên đóng 8%
    private static final double TY_LE_DN = 0.175;  // Doanh nghiệp đóng 17.5%

    public BaoHiemXaHoi() {
        super();
    }

    public BaoHiemXaHoi(String maBH, String maNV, String soSo, LocalDate ngayThamGia) {
        super(maBH, maNV, soSo, ngayThamGia);
    }

    public static double getTyLeNV() { return TY_LE_NV; }
    public static double getTyLeDN() { return TY_LE_DN; }

    @Override
    public double tinhTienNhanVienDong(double luongDongBH) {
        if (luongDongBH < 0) return 0;
        return luongDongBH * TY_LE_NV;
    }

    @Override
    public double tinhTienDoanhNghiepDong(double luongDongBH) {
        if (luongDongBH < 0) return 0;
        return luongDongBH * TY_LE_DN;
    }

    @Override
    public void hienThiThongTin() {
        super.hienThiThongTin();
        System.out.printf("| %-6s | NV %.1f%% - DN %.1f%% |%n", "BHXH", TY_LE_NV * 100, TY_LE_DN * 100);
    }
}
