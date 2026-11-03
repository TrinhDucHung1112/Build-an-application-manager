import java.time.LocalDate;

public class BaoHiemYTe extends BaoHiem {
    private static final double TY_LE_NV = 0.015;  // Nhân viên đóng 1.5%
    private static final double TY_LE_DN = 0.03;   // Doanh nghiệp đóng 3%

    private String noiKhamChuaBenh;
    private LocalDate hanTheDen;

    public BaoHiemYTe() {
        super();
        this.noiKhamChuaBenh = "";
        this.hanTheDen = LocalDate.now();
    }

    public BaoHiemYTe(String maBH, String maNV, String soSo, LocalDate ngayThamGia,
                      String noiKhamChuaBenh, LocalDate hanTheDen) {
        super(maBH, maNV, soSo, ngayThamGia);
        this.noiKhamChuaBenh = noiKhamChuaBenh;
        this.hanTheDen = hanTheDen;
    }

    public static double getTyLeNV() { return TY_LE_NV; }
    public static double getTyLeDN() { return TY_LE_DN; }

    public String getNoiKhamChuaBenh() { return noiKhamChuaBenh; }
    public void setNoiKhamChuaBenh(String noiKhamChuaBenh) { this.noiKhamChuaBenh = noiKhamChuaBenh; }

    public LocalDate getHanTheDen() { return hanTheDen; }
    public void setHanTheDen(LocalDate hanTheDen) { this.hanTheDen = hanTheDen; }

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

    // Thẻ còn hạn khi hạn thẻ không trước ngày hôm nay
    public boolean conHanThe() {
        return hanTheDen != null && !hanTheDen.isBefore(LocalDate.now());
    }

    @Override
    public void hienThiThongTin() {
        super.hienThiThongTin();
        System.out.printf("| %-6s | KCB: %s - Hạn thẻ: %s (%s) |%n", "BHYT",
                noiKhamChuaBenh, hanTheDen, conHanThe() ? "còn hạn" : "hết hạn");
    }
}
