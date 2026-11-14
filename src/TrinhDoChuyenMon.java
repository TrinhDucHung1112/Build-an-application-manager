import java.io.Serializable;
import java.time.Year;
import java.util.Scanner;

public class TrinhDoChuyenMon implements Serializable, IThongTin{
    private String maTD;
    private String tenTD;
    private String chuyenNganh;
    private String truongDaoTao;
    private int namTotNghiep;
    private double heSoTD;

    // Constructor
    public TrinhDoChuyenMon(){
        this.maTD = "";
        this.tenTD = "";
        this.chuyenNganh = "";
        this.truongDaoTao = "";
        this.namTotNghiep = 0;
        this.heSoTD = 0;
    }
    public TrinhDoChuyenMon(String maTD, String tenTD, String chuyenNganh,
                            String truongDaoTao, int namTotNghiep, double heSoTD){
        setMaTD(maTD);
        setTenTD(tenTD);
        setChuyenNganh(chuyenNganh);
        setTruongDaoTao(truongDaoTao);
        setNamTotNghiep(namTotNghiep);
        setHeSoTD(heSoTD);
    }

    // Getter, setter
    public String getMaTD(){
        return maTD;
    }
    public void setMaTD(String maTD){
        if (maTD == null || maTD.isBlank()){
            throw new IllegalArgumentException("Ma trinh do khong duoc de trong");
        }
        this.maTD = maTD.trim().toUpperCase();
    }

    public String getTenTD(){
        return tenTD;
    }
    public void setTenTD(String tenTD){
        if (tenTD == null || tenTD.isBlank()){
            throw new IllegalArgumentException("Ten trinh do khong duoc de trong");
        }
        this.tenTD = tenTD.trim().toUpperCase();
    }

    public String getChuyenNganh(){
        return chuyenNganh;
    }
    public void setChuyenNganh(String chuyenNganh){
        if (chuyenNganh == null || chuyenNganh.isBlank()){
            throw new IllegalArgumentException("Chuyen nganh khong duoc de trong");
        }
        this.chuyenNganh = chuyenNganh.trim().toUpperCase();
    }

    public String getTruongDaoTao(){
        return truongDaoTao;
    }
    public void setTruongDaoTao(String truongDaoTao){
        if (truongDaoTao == null || truongDaoTao.isBlank()){
            throw new IllegalArgumentException("Truong dao tao khong duoc de trong");
        }
        this.truongDaoTao = truongDaoTao.trim().toUpperCase();
    }

    public int getNamTotNghiep(){
        return namTotNghiep;
    }
    public void setNamTotNghiep(int namTotNghiep){
        if (namTotNghiep < 0 || namTotNghiep > Year.now().getValue()){
            throw new IllegalArgumentException("Nam tot nghiep khong hop le");
        }
        this.namTotNghiep = namTotNghiep;
    }

    public double getHeSoTD(){
        return heSoTD;
    }
    public void setHeSoTD(double heSoTD) {
        if (!Double.isFinite(heSoTD) || heSoTD < 0){
            throw new IllegalArgumentException("He so trinh do khong hop le");
        }
        this.heSoTD = heSoTD;
    }

    // Tính phụ cấp trình độ
    public double tinhPhuCapTrinhDo(double luongCoBan){
        if (!Double.isFinite(luongCoBan) || luongCoBan < 0){
            throw new IllegalArgumentException("Luong co ban khong hop le");
        }
        return heSoTD * luongCoBan;
    }

    // Hiển thị thông tin
    @Override
    public void hienThiThongTin(){
        System.out.println("Ma trinh do cua nhan vien: " + maTD);
        System.out.println("Ten trinh do cua nhan vien: " + tenTD);
        System.out.println("Chuyen nganh cua nhan vien: " + chuyenNganh);
        System.out.println("Truong dao tao cua nhan vien: " + truongDaoTao);
        System.out.println("Nam tot nghiep cua nhan vien: " + namTotNghiep);
        System.out.println("He so trinh do cua nhan vien: " + heSoTD);
    }

    // Nhập thông tin trình độ chuyên môn
    public void nhapThongTinTD(Scanner sc){
        System.out.println("Nhap vao ma trinh do cua nhan vien:");
        setMaTD(sc.nextLine());
        System.out.println("Nhap vao ten trinh do cua nhan vien:");
        setTenTD(sc.nextLine());
        System.out.println("Nhap vao chuyen nganh cua nhan vien:");
        setChuyenNganh(sc.nextLine());
        System.out.println("Nhap vao truong dao tao cua nhan vien:");
        setTruongDaoTao(sc.nextLine());
        System.out.println("Nhap vao nam tot nghiep cua nhan vien:");
        setNamTotNghiep(Integer.parseInt(sc.nextLine()));
        System.out.println("Nhap vao he so trinh do cua nhan vien:");
        setHeSoTD(Double.parseDouble(sc.nextLine()));
    }
}