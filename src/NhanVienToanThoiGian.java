import java.time.LocalDate;

public class NhanVienToanThoiGian extends NhanVien{
    private double phuCap;
    private int thamNien;

    public NhanVienToanThoiGian(){

    }

    //setter
    public void setphuCap(double P){
        if (!Double.isFinite(P) || P < 0) {
            throw new IllegalArgumentException("Phu cap khong hop le");
        }
        this.phuCap = P;
    }
    public void setthamNien(int T){
        if (T < 0) {
            throw new IllegalArgumentException("Tham nien khong hop le");
        }
        this.thamNien = T;
    }

    public NhanVienToanThoiGian(String maNV,
                                String hoTen,
                                LocalDate ngaySinh,
                                String gioiTinh,
                                String sdt,
                                String email,
                                double luongCoBan,
                                LocalDate ngayVaoLam,
                                String trangThai,
                                double P,
                                int T){
        super(
        maNV, hoTen, ngaySinh, gioiTinh,
        sdt, email, luongCoBan, ngayVaoLam, trangThai
        );

        setphuCap(P);
        setthamNien(T);
    }

    //getter
    public double getphuCap(){
        return this.phuCap;
    }
    public int getthamNien(){
        return this.thamNien;
    }

    @Override
    public void hienThiThongTin(){
        super.hienThiThongTin();
        System.out.println("Loai nhan vien: Toan thoi gian");
        System.out.println("Phu cap: " + String.format("%,.0f", phuCap));
        System.out.println("Tham nien: " + thamNien + " nam");
        System.out.println("Luong (tinh luong): " + String.format("%,.0f", tinhLuong()));
    }


    @Override
    public double tinhLuong(){
        return getLuongCoBan() + phuCap + thamNien * 200000;
    }
}