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
    protected String[][] thongTinRieng(){
        String[][] r = new String[0][];
        r = Mang.them(r, new String[]{"Loai nhan vien", "Ban thoi gian"});
        r = Mang.them(r, new String[]{"So gio lam", String.valueOf(soGioLam)});
        r = Mang.them(r, new String[]{"Luong theo gio", String.format("%,.0f", luongTheoGio)});
        return r;
    }

    @Override
    public String tenLoai(){
        return "Ban TG";
    }


    @Override 
    public double tinhLuong(){
        return soGioLam * luongTheoGio;
    }
}
