import java.io.Serializable;
import java.time.LocalDate;

public class HopDongLaoDong implements Serializable, IThongTin{
    private String maHD;
    private LocalDate ngayKy;
    private LocalDate ngayHetHan;
    private String loaiHopDong;

    public HopDongLaoDong(){

    }

    //setter
    public void setmaHD(String m){
        if(m == null || m.isBlank()){
            throw new IllegalArgumentException("Ma hop dong khong duoc de trong");
        }
        this.maHD = m;
    }
    public void setngayKy(LocalDate n){
        if(n == null|| n.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Ngay ky khong hop le");
        }
        this.ngayKy = n;
    }
    public void setngayHetHan(LocalDate nh){
        if (nh == null || (ngayKy != null && nh.isBefore(ngayKy))) {
            throw new IllegalArgumentException("Ngay het han khong hop le");
        }
        this.ngayHetHan = nh;
    }
    public void setloaiHopDong(String l){
        if(l == null || l.isBlank()){
            throw new IllegalArgumentException("Loai hop dong khong duoc de trong");
        }
        this.loaiHopDong = l;
    }

    public HopDongLaoDong(String maHD,
                            LocalDate ngayKy,
                            LocalDate ngayHetHan,
                            String loaiHopDong){
        
        setmaHD(maHD);
        setngayKy(ngayKy);
        setngayHetHan(ngayHetHan);
        setloaiHopDong(loaiHopDong);
    }

    //getter
    public String getmaHD(){
        return  this.maHD;
    }
    public LocalDate getngayKy(){
        return  this.ngayKy;
    }
    public LocalDate getngayHetHan(){
        return  this.ngayHetHan;
    }
    public String getloaiHopDong(){
        return this.loaiHopDong;
    }

    public void giaHan(LocalDate ngayHetHanMoi) {
        if (ngayHetHan == null || ngayHetHanMoi == null) {
            throw new IllegalArgumentException("Ngay het han khong hop le");
        }
        if (!ngayHetHanMoi.isAfter(ngayHetHan)) {
            throw new IllegalArgumentException("Ngay het han moi phai sau ngay het han cu");
        }
        this.ngayHetHan = ngayHetHanMoi;
    }

    public boolean conHieuLuc() {
        return ngayHetHan != null && !LocalDate.now().isAfter(ngayHetHan);
    }

    @Override
    public void hienThiThongTin(){
        System.out.println("Ma hop dong: " + maHD);
        System.out.println("Loai hop dong: " + loaiHopDong);
        System.out.println("Ngay ky: " + ngayKy);
        System.out.println("Ngay het han: " + ngayHetHan + (conHieuLuc() ? " (con hieu luc)" : " (het hieu luc)"));
    }
}