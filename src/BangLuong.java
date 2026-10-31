import java.util.Arrays;

public class BangLuong {
//----thuoc tinh----
    private static final double LUONG_TOI_THIEU = 5000000;     //sua theo quy dinh nhom/co

    private String maBangLuong;
    private String maNV;
    private int thang;
    private int nam;
    private double phuCap;
    private double tienThuong;
    private double tienPhat;
    private double tienBaoHiem;
    private double tongLuong;
    private double luongThucNhan;
    private double luongNhanVien;                              //them: luong tu NhanVien.tinhLuong()
    private LuongThuong[] dsLuongThuong = new LuongThuong[0];  //them: cac dong thuong/phat chi tiet

//----constructor----
    public BangLuong(String maBangLuong, int thang, int nam) {
        this.maBangLuong = maBangLuong;
        this.maNV = "";
        this.thang = thang;
        this.nam = nam;
    }

    //dung khi doc file
    public BangLuong(String maBangLuong, String maNV, int thang, int nam, double phuCap,
                     double tienThuong, double tienPhat, double tienBaoHiem, double luongNhanVien) {
        this.maBangLuong = maBangLuong;
        this.maNV = maNV;
        this.thang = thang;
        this.nam = nam;
        this.phuCap = phuCap;
        this.tienThuong = tienThuong;
        this.tienPhat = tienPhat;
        this.tienBaoHiem = tienBaoHiem;
        this.luongNhanVien = luongNhanVien;
        tinhLuongThucNhan();
    }

//----methods----
//---thuong phat chi tiet---
    public void themLuongThuong(LuongThuong lt) {
        dsLuongThuong = Arrays.copyOf(dsLuongThuong, dsLuongThuong.length + 1);
        dsLuongThuong[dsLuongThuong.length - 1] = lt;
        capNhatThuongPhat();
    }

    //tinh lai tienThuong, tienPhat tu dsLuongThuong
    public void capNhatThuongPhat() {
        double thuong = 0;
        double phat = 0;
        for (LuongThuong lt : dsLuongThuong) {
            thuong += lt.tinhThuong();
            phat += lt.tinhPhat();
        }
        this.tienThuong = thuong;
        this.tienPhat = phat;
    }

//---tinh luong---
    //tong luong = luong NV + phu cap + thuong - phat
    public double tinhTongLuong() {
        tongLuong = luongNhanVien + phuCap + tienThuong - tienPhat;
        return tongLuong;
    }

    //luong thuc nhan = tong luong - bao hiem (khong am)
    public double tinhLuongThucNhan() {
        tinhTongLuong();
        luongThucNhan = tongLuong - tienBaoHiem;
        if (luongThucNhan < 0) luongThucNhan = 0;
        return luongThucNhan;
    }

//---xuat---
    public void xuatBangLuong() {
        System.out.println("===== BANG LUONG " + thang + "/" + nam + " =====");
        System.out.println("Ma bang luong : " + maBangLuong + " | Ma NV: " + maNV);
        System.out.println(String.format("Luong nhan vien: %,.0f", luongNhanVien));
        System.out.println(String.format("Phu cap        : %,.0f", phuCap));
        System.out.println(String.format("Tien thuong    : %,.0f", tienThuong));
        System.out.println(String.format("Tien phat      : %,.0f", tienPhat));
        for (LuongThuong lt : dsLuongThuong) lt.hienThi();
        System.out.println(String.format("Tong luong     : %,.0f", tongLuong));
        System.out.println(String.format("Tien bao hiem  : %,.0f", tienBaoHiem));
        System.out.println(String.format("LUONG THUC NHAN: %,.0f", luongThucNhan));
        if (luongThucNhan < LUONG_TOI_THIEU) {
            System.out.println("(Luu y: thap hon luong toi thieu " + String.format("%,.0f", LUONG_TOI_THIEU) + ")");
        }
    }

//---setter, getter---
    public static double getLuongToiThieu() { return LUONG_TOI_THIEU; }

    public String getMaBangLuong() { return maBangLuong; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public int getThang() { return thang; }
    public int getNam() { return nam; }
    public double getPhuCap() { return phuCap; }
    public void setPhuCap(double phuCap) { this.phuCap = phuCap; }
    public double getTienThuong() { return tienThuong; }
    public double getTienPhat() { return tienPhat; }
    public double getTienBaoHiem() { return tienBaoHiem; }
    public void setTienBaoHiem(double tienBaoHiem) { this.tienBaoHiem = tienBaoHiem; }
    public double getTongLuong() { return tongLuong; }
    public double getLuongThucNhan() { return luongThucNhan; }
    public double getLuongNhanVien() { return luongNhanVien; }
    public void setLuongNhanVien(double luongNhanVien) { this.luongNhanVien = luongNhanVien; }
    public LuongThuong[] getDsLuongThuong() { return dsLuongThuong; }
}
