import java.io.Serializable;
import java.time.LocalDate;
import java.util.Scanner;

public abstract class NhanVien implements IThongTin, Serializable{
    private String maNV;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String sdt;
    private String email;
    private double luongCoBan;
    private LocalDate ngayVaoLam;
    private String trangThai;
    private ChucVu chucVu;
    private PhongBan phongBan;
    private TrinhDoChuyenMon[] trinhDo;

    private static int soLuongNV = 0;

    //setter
    public void setMaNV(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("Ma nhan vien khong duoc de trong");
        }
        this.maNV = m;
    }
    public void setHoTen(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("Ho va Ten khong duoc de trong");
        }
        this.hoTen = m;
    }
    public void setNgaySinh(LocalDate m){
        if(m == null|| m.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Ngay sinh khong hop le");
        }
        this.ngaySinh = m;
    }
    public void setGioiTinh(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("Gioi tinh khong duoc de trong");
        }
        this.gioiTinh = m;
    }
    public void setSdt(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("So dien thoai khong duoc de trong");
        }
        this.sdt = m;
    }
    public void setEmail(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("Email khong duoc de trong");
        }
        this.email = m;
    }
    public void setLuongCoBan(double m){
        if(m < 0){
            throw new IllegalArgumentException("Luong khong hop le");
        }
        this.luongCoBan = m;
    }
    public void setNgayVaoLam(LocalDate m){
        if(m == null|| m.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Ngay vao lam khong duoc de trong");
        }
        this.ngayVaoLam = m;
    }
    public void setTrangThai(String m){
        if(m == null|| m.isBlank()){
            throw new IllegalArgumentException("Trang thai khong duoc de trong");
        }
        this.trangThai = m;
    }
    public void setChucVu(ChucVu chucVu) {
        if (chucVu == null) {
            throw new IllegalArgumentException("Chuc vu khong duoc de trong");
        }
        this.chucVu = chucVu;
    }
    public void setPhongBan(PhongBan phongBan) {
        if (phongBan == null) {
            throw new IllegalArgumentException("Phong ban khong duoc de trong");
        }
        this.phongBan = phongBan;
    }
    public void xoaPhongBan() {
        this.phongBan = null;
    }
    public void setTrinhDo(TrinhDoChuyenMon[] trinhDo) {
        if (trinhDo == null) {
            throw new IllegalArgumentException("Trinh do khong duoc de trong");
        }
        this.trinhDo = trinhDo.clone();
    }

    //Constructor
    public NhanVien(){
        soLuongNV++;
    }
    public NhanVien(
        String maNV,
        String hoTen,
        LocalDate ngaySinh,
        String gioiTinh,
        String sdt,
        String email,
        double luongCoBan,
        LocalDate ngayVaoLam,
        String trangThai){

        setMaNV(maNV);
        setHoTen(hoTen);
        setNgaySinh(ngaySinh);
        setGioiTinh(gioiTinh);
        setSdt(sdt);
        setEmail(email);
        setLuongCoBan(luongCoBan);
        setNgayVaoLam(ngayVaoLam);
        setTrangThai(trangThai);

        soLuongNV++;
    }

    public abstract double tinhLuong();

    public void hienThiThongTin(){
        System.out.println("Ma nhan vien: " + maNV);
        System.out.println("Ho ten: " + hoTen);
        System.out.println("Ngay sinh: " + ngaySinh);
        System.out.println("Gioi tinh: " + gioiTinh);
        System.out.println("So dien thoai: " + sdt);
        System.out.println("Email: " + email);
        System.out.println("Luong co ban: " + luongCoBan);
        System.out.println("Ngay vao lam: " + ngayVaoLam);
        System.out.println("Trang thai: " + trangThai);
    }

    public static int getSoLuongNV() {
        return soLuongNV;
    }

    public void nhap() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Ma nhan vien: ");
        setMaNV(sc.nextLine());

        System.out.print("Ho ten: ");
        setHoTen(sc.nextLine());

        System.out.print("Ngay sinh (yyyy-MM-dd): ");
        setNgaySinh(LocalDate.parse(sc.nextLine()));

        System.out.print("Gioi tinh: ");
        setGioiTinh(sc.nextLine());

        System.out.print("So dien thoai: ");
        setSdt(sc.nextLine());

        System.out.print("Email: ");
        setEmail(sc.nextLine());

        System.out.print("Luong co ban: ");
        setLuongCoBan(Double.parseDouble(sc.nextLine()));

        System.out.print("Ngay vao lam (yyyy-MM-dd): ");
        setNgayVaoLam(LocalDate.parse(sc.nextLine()));

        System.out.print("Trang thai: ");
        setTrangThai(sc.nextLine());
    }

    //getter
    public String getMaNV(){
        return this.maNV;
    }
    public String getHoTen(){
        return this.hoTen;
    }
    public LocalDate getNgaySinh(){
        return this.ngaySinh;
    }
    public String getGioiTinh(){
        return this.gioiTinh;
    }
    public String getSdt(){
        return this.sdt;
    }
    public String getEmail(){
        return this.email;
    }
    public double getLuongCoBan(){
        return this.luongCoBan;
    }
    public LocalDate getNgayVaoLam(){
        return this.ngayVaoLam;
    }
    public String getTrangThai(){
        return this.trangThai;
    }

    public ChucVu getChucVu() { return chucVu; }
    public PhongBan getPhongBan() { return phongBan; }
    public TrinhDoChuyenMon[] getTrinhDo() {
        return trinhDo == null ? null : trinhDo.clone();
    }

    public double chamCong(int ngay, int gioVao, int gioRa){
        if (ngay < 1 || ngay > 31) {
            throw new IllegalArgumentException("Ngay khong hop le");
        }
        if (gioVao < 0 || gioVao > 23 ||
            gioRa < 0 || gioRa > 23) {
            throw new IllegalArgumentException("Gio khong hop le");
        }
        if (gioRa < gioVao) {
            throw new IllegalArgumentException("Gio ra phai lon hon gio vao");
        }

        return gioRa - gioVao;
    }

    public double tinhThuongPhat(boolean datKPI, int soNgayDiMuon, int soNamLamViec){
        double tienKPI = datKPI ? 500000 : 0;
        double tienPhat = 0;
        double thuongThamNien = 0;
        if (soNgayDiMuon < 0 || soNgayDiMuon > 30) {
            throw new IllegalArgumentException("So ngay di muon khong hop le");
        }
        else tienPhat = soNgayDiMuon * 100000;
        if (soNamLamViec < 0) {
            throw new IllegalArgumentException("So nam lam viec khong hop le");
        }
        else thuongThamNien = Math.min(soNamLamViec, 5) * 200000;
        return  tienKPI - tienPhat + thuongThamNien;
    }

    public double tienBaoHiem(){
        if (luongCoBan < 0) {
            throw new IllegalArgumentException("Luong khong hop le");
        }
        return luongCoBan * 0.105;
    }


}