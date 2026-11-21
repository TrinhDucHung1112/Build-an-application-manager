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
    protected String[][] thongTinRieng(){
        String[][] r = new String[0][];
        r = Mang.them(r, new String[]{"Loai nhan vien", "Toan thoi gian"});
        r = Mang.them(r, new String[]{"Phu cap", String.format("%,.0f", phuCap)});
        r = Mang.them(r, new String[]{"Tham nien", thamNien + " nam"});
        return r;
    }

    @Override
    public String tenLoai(){
        return "Toan TG";
    }


    @Override
    public double tinhLuong(){
        return getLuongCoBan() + phuCap + thamNien * 200000;
    }
}
