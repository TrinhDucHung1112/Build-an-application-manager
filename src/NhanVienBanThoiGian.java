import java.time.LocalDate;

public class NhanVienBanThoiGian extends NhanVien {
    private int soGioLam;
    private double luongTheoGio;

    public NhanVienBanThoiGian(){

    }

    //setter
    public void setsoGioLam(int S){
        if(S < 0){
            throw new IllegalArgumentException("So gio lam khong duoc am");
        }
        this.soGioLam = S;
    }
    public void setluongTheoGio(double L){
        if(L < 0 || !Double.isFinite(L)){
            throw new IllegalArgumentException("Luong theo gio khong hop le");
        }
        this.luongTheoGio = L;
    }

    public NhanVienBanThoiGian(String maNV,
                                String hoTen,
                                LocalDate ngaySinh,
                                String gioiTinh,
                                String sdt,
                                String email,
                                double luongCoBan,
                                LocalDate ngayVaoLam,
                                String trangThai,
                                int S,
                                double L){
        super(
        maNV, hoTen, ngaySinh, gioiTinh,
        sdt, email, luongCoBan, ngayVaoLam, trangThai
        );
        
        setsoGioLam(S);
        setluongTheoGio(L);
    }   

    //getter
    public int getsoGioLam(){
        return this.soGioLam;
    }
    public double getluongTheoGio(){
        return  this.luongTheoGio;
    }

    @Override
    public void hienThiThongTin(){
        super.hienThiThongTin();
        System.out.println("Loai nhan vien: Ban thoi gian");
        System.out.println("So gio lam: " + soGioLam);
        System.out.println("Luong theo gio: " + String.format("%,.0f", luongTheoGio));
        System.out.println("Luong (tinh luong): " + String.format("%,.0f", tinhLuong()));
    }


    @Override 
    public double tinhLuong(){
        return soGioLam * luongTheoGio;
    }
}
