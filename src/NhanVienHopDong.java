import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NhanVienHopDong extends NhanVien {
    private int thoiHanHopDong;
    private double luongTheoHopDong;
    private LocalDate ngayBatDauThucTe;
    private LocalDate ngayKetThucTe;
    private int thoiGianThucTe;
    private HopDongLaoDong hopDong;     //hop dong lao dong cua nhan vien (quan he has-a)

    public NhanVienHopDong(){

    }

    //setter
    public void setthoiHanHopDong(int T){
        if(T < 0){
            throw new IllegalArgumentException("Thoi han hop dong khong hop le");
        }
        this.thoiHanHopDong = T;
    }
    public void setluongTheoHopDong(double L){
        if (!Double.isFinite(L) || L < 0) {
            throw new IllegalArgumentException("Luong theo hop dong khong hop le");
        }
        this.luongTheoHopDong = L;
    }
    public void setngayBatDauThucTe(LocalDate N){
        if(N == null){
            throw new IllegalArgumentException("Ngay bat dau thuc the khong duoc de trong");
        }
        this.ngayBatDauThucTe = N;
    }
    public void setngayKetThucTe(LocalDate NK){
        if(NK == null){
            throw new IllegalArgumentException("Ngay ket thuc thuc the khong duoc de trong");
        }
        this.ngayKetThucTe = NK;
    }
    public void setthoiGianThucTe(int TT){
        if(TT < 0){
            throw new IllegalArgumentException("Thoi han hop dong khong hop le");
        }
        this.thoiGianThucTe = TT;
    }



    public  NhanVienHopDong(String maNV,
                                String hoTen,
                                LocalDate ngaySinh,
                                String gioiTinh,
                                String sdt,
                                String email,
                                double luongCoBan,
                                LocalDate ngayVaoLam,
                                String trangThai,
                                int thoiHanHopDong,
                                double luongTheoHopDong,
                                LocalDate ngayBatDauThucTe,
                                LocalDate ngayKetThucTe,
                                int thoiGianThucTe){
        super(
        maNV, hoTen, ngaySinh, gioiTinh,
        sdt, email, luongCoBan, ngayVaoLam, trangThai
        );

        setthoiHanHopDong(thoiHanHopDong);
        setluongTheoHopDong(luongTheoHopDong);
        setngayBatDauThucTe(ngayBatDauThucTe);
        setngayKetThucTe(ngayKetThucTe);
        setthoiGianThucTe(thoiGianThucTe);
        taoHopDong();
    }

    //getter
    public int getthoiHanHopDong(){
        return  this.thoiHanHopDong;
    }
    public double getluongTheoHopDong(){
        return  this.luongTheoHopDong;
    }
    public LocalDate getngayBatDauThucTe(){
        return  this.ngayBatDauThucTe;
    }
    public LocalDate getngayKetThucTe(){
        return  this.ngayKetThucTe;
    }
    public int getthoiGianThucTe(){
        return  this.thoiGianThucTe;
    }
    public HopDongLaoDong gethopDong(){
        return this.hopDong;
    }

    //tao hop dong lao dong tu ngay vao lam va ngay ket thuc cua nhan vien (khong tao neu ngay khong hop le)
    private void taoHopDong(){
        if(getNgayVaoLam() == null || ngayKetThucTe == null || ngayKetThucTe.isBefore(getNgayVaoLam())) return;
        this.hopDong = new HopDongLaoDong("HD_" + getMaNV(), getNgayVaoLam(), ngayKetThucTe, "Co thoi han");
    }

    @Override
    public void hienThiThongTin(){
        super.hienThiThongTin();
        System.out.println("Loai nhan vien: Hop dong");
        System.out.println("Thoi han hop dong: " + thoiHanHopDong + " ngay");
        System.out.println("Luong theo hop dong: " + String.format("%,.0f", luongTheoHopDong));
        System.out.println("Ngay bat dau thuc te: " + ngayBatDauThucTe);
        System.out.println("Ngay ket thuc thuc te: " + ngayKetThucTe);
        System.out.println("Thoi gian thuc te: " + thoiGianThucTe + " ngay");
        if(hopDong != null)
            hopDong.hienThiThongTin();
    }


    @Override 
    public double tinhLuong(){
        return luongTheoHopDong;
    }

    public int tinhThoiGianThucTe(){
        if (ngayBatDauThucTe == null || ngayKetThucTe == null) {
            throw new IllegalArgumentException("Ngay bat dau va ket thuc khong duoc de trong");
        }
        if (ngayKetThucTe.isBefore(ngayBatDauThucTe)) {
            throw new IllegalArgumentException("Ngay ket thuc khong hop le");
        }
        return (int)ChronoUnit.DAYS.between(ngayBatDauThucTe, ngayKetThucTe);
    }

    public boolean kiemTraDungHan(){
        return tinhThoiGianThucTe() <= thoiHanHopDong;
    }
}
