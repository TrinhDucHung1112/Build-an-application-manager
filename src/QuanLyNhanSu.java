import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class QuanLyNhanSu implements IQuanLyDuLieu{
//----thuoc tinh----
    private NhanVien[] danhSachNV;  //tao mang danh sach NhanVien(chua duoc tao, class QLNS tao)
    private DanhSachPhongBan danhSachPB;    //lay mang danh sach cua PB(da duoc tao o DSPB)
    private int soLuongNV;
    Scanner sc = new Scanner(System.in);
    private static final String DUONG_DAN = DuongDanDuLieu.file("nhanvien.dat");

//----constructor----
    public QuanLyNhanSu(int sucChua){
        this.danhSachNV = new NhanVien[sucChua];
        this.soLuongNV = 0;
    }

    public QuanLyNhanSu(int sucChua, DanhSachPhongBan dSachPhongBan){
         this.danhSachNV = new NhanVien[sucChua];
        this.soLuongNV = 0;
        this.danhSachPB = dSachPhongBan;
    }

//----methods----
//---getter, setter---
    public int getSoLuongNV(){
        return soLuongNV;
    }

    public NhanVien[] getDanhSachNV() {
        NhanVien[] ketQua = new NhanVien[soLuongNV];
        System.arraycopy(danhSachNV, 0, ketQua, 0, soLuongNV);
        return ketQua;
    }

    public int getSucChua(){
        return danhSachNV.length;
    }

    public void setDanhSachPhongBan(DanhSachPhongBan danhSachPB){   //dung khi khoi tao bang constructor dau tien
        this.danhSachPB = danhSachPB;
    }

//---nhapdanhsach---
    public void nhapDanhSachNV(Scanner sc){
        System.out.println("nhap vao so luong nhan vien muon nhap:");
        int k = Integer.parseInt(sc.nextLine());
        themKNhanVien(k);
    }

    public boolean themNhanVien(NhanVien nv){
        if (nv == null) return false;
        if (soLuongNV == danhSachNV.length) return false;
        if (timViTri(nv.getMaNV()) != -1) return false;
        danhSachNV[soLuongNV] = nv;
        soLuongNV++;
        if (nv.getPhongBan() != null) nv.getPhongBan().tangSoNhanVien();
        return true;
    }

    public void themKNhanVien(int k){
        int conTrong = danhSachNV.length - soLuongNV;
        while(k > conTrong){
            System.out.println("danh sasch da day, vui long giam lai (<=" + conTrong + ")");
            k = Integer.parseInt(sc.nextLine());
        }
        for(int i = 0; i < k; i++){
            System.out.println("nhan vien: " + (i +1));
            System.out.println("chon loai nhan vien (1: NV toan thoi gian, 2: Nv ban thoi gian, 3: Nv hop dong)");
            int choose;
            NhanVien nv;
            do{
                choose = Integer.parseInt(sc.nextLine());
            }while(choose < 1 || choose > 3); 
            switch(choose){
                case 1:
                    nv = new NhanVienToanThoiGian();
                    break;
                case 2:
                    nv = new NhanVienBanThoiGian();
                    break;
                default:
                    nv = new NhanVienHopDong();
                    break;
            }
            nv.nhap();        //thong nhat goi nv.nhap() khong tham so theo dung so do UML
            //viec nv.nhap() chi xu li duoc thuoc tinh rieng, thuoc tinh co lop rieng thi khong the
            //thuoc tinh co lop rieng khi dung nhap se khong tro dung vao doi duong trong DSPB
            ganLienKet(nv);
            if(!themNhanVien(nv)){
                System.out.println("nhan vien da ton tai");
                i--;
            }
        }
    }

    private ChucVu hoiChucVu(){
        System.out.println("nhap vao chuc vu cua nhan vien:");
        String c = sc.nextLine();       //nhap c chi co tac dung la xac nhan la co nhap lai chuc vu thoi, khong con su dung gi khac
        if (!c.equalsIgnoreCase("c")) return null;
        ChucVu cv = new ChucVu();
        cv.nhap(sc);        //chuyen qua phuong thuc nhap trong chuc vu va thuc hien nhap lai chuc vu
        return cv;
    }

    private PhongBan hoiPhongBan(){
        if(danhSachPB == null) return null;
        while(true){
            System.out.println("nhap vao maPB cua nhan vien: ");
            String ma = sc.nextLine().toUpperCase();
            PhongBan pb = danhSachPB.timTheoMa(ma);
            if(pb != null) return pb;
            System.out.println("khong tim thay phong ban nay");
        }
    }
    
    private void ganLienKet(NhanVien nv){ 
        if(nv.getPhongBan() == null) {
            PhongBan pb = hoiPhongBan();
            if (pb != null) nv.setPhongBan(pb);
        }
        if(nv.getChucVu() == null) {
            ChucVu cv = hoiChucVu();
            if (cv != null) nv.setChucVu(cv);
        }
    }

//---timvitri---
    public int timViTri(String maNV){
        if(maNV == null) return -1;
        for(int i = 0; i < soLuongNV; i++){
            if(danhSachNV[i].getMaNV().equalsIgnoreCase(maNV))  //tim thay nhan vien trung
                return i;
        }
        return -1;  //khong tim thay nhan vien
    }

//---timTheoMa---
    public NhanVien timTheoMa(String maNV){
        int vt = timViTri(maNV);
        if(vt == -1) 
            return null;
        return danhSachNV[vt];
    }

//dung khi doc file
    private void lienKetLaiPhongBan(NhanVien nv) {
        PhongBan pb = nv.getPhongBan();
        if (pb == null || danhSachPB == null) return;
        PhongBan goc = danhSachPB.timTheoMa(pb.getMaPB());
        if (goc != null) {
            nv.setPhongBan(goc);        //doi tro cua nv qua PhongBan goc dang co  
        } else {
            danhSachPB.themPhong(pb);         //them phong moi vao danhSachPB khi chua co 
        }
    }

//---xuatdanhsach---
    public void xuatDanhSachNV() {
        if (soLuongNV == 0) {
            System.out.println("danh sach trong");
            return;
        }
        for (int i = 0; i < soLuongNV; i++) {
            System.out.println("nhan vien: " + (i+1));
            danhSachNV[i].hienThiThongTin();      
        }
    }

//---doiphongban---
    private void doiPhongBan(NhanVien nv) {
        if (danhSachPB == null) {
            System.out.println("chua lien ket phong ban nao");
            return;
        }
        PhongBan moi = hoiPhongBan();
        if (moi == null) return;
        PhongBan cu = nv.getPhongBan();
        if (cu != null) cu.giamSoNhanVien();    //giam nhan vien phong ban cu
        nv.setPhongBan(moi);
        moi.tangSoNhanVien();       //tang nhan vien phong ban moi
        System.out.println("da chuyen sang phong ban " + moi.getTenPB());
    }
    
    public boolean suaNhanVien(String maNV){
        NhanVien nv = timTheoMa(maNV);
        if(nv == null) return false;
        System.out.println("sua nhan vien: (1: sua phong ban, 2: sua chuc vu, 3: sua lai toan bo)");
        int choose = Integer.parseInt(sc.nextLine());
        while(choose < 1 || choose > 3){
            System.out.println("nhap lai: 1, 2, 3");
            choose = Integer.parseInt(sc.nextLine());
        }
        switch (choose) {
            case 1:
                doiPhongBan(nv);
                break;
            case 2:
                ChucVu cv = new ChucVu();
                cv.nhap(sc);         
                nv.setChucVu(cv);   //setChucVu trong nhanvien
                break;
            default:
                PhongBan pbCu = nv.getPhongBan();     //lay ra phong ban hien tai cua nhan vien
                nv.nhap();          //nhap vao phong ban moi cua nhan vien
                PhongBan pbMoi = nv.getPhongBan();
                if(pbCu != pbMoi){     //so sanh phong ban moi va phong ban cu
                    if(pbCu != null) pbCu.giamSoNhanVien();
                    if(pbMoi != null) pbMoi.tangSoNhanVien();
                }
                int dem = 0;
                for(int i = 0; i < soLuongNV; i++){
                    if(danhSachNV[i].getMaNV().equalsIgnoreCase(nv.getMaNV()))       //thuc hien kiem tra sau khi thay doi thong tin nhan vien co bi trung trong danh sach khong
                        dem++;
                }
                if(dem > 1)
                    System.out.println("nhan vien vua nhap da bi trung");
                break;
        }
        return true;
    }

//---xoanhanvien---
    public boolean xoaNhanVien(String maNV){
        int vt = timViTri(maNV);
        if(vt == -1) return false;
        PhongBan pb = danhSachNV[vt].getPhongBan();
        if(pb != null) pb.giamSoNhanVien();
        for(int i = vt; i < soLuongNV - 1; i++){
            danhSachNV[i] = danhSachNV[i + 1];
        }
        danhSachNV[soLuongNV - 1] = null;  //neu khong thuc hien dong nay, nhan vien nay van nam trong bo nho
        soLuongNV--;
        return true;
    }

//---timKiemTuKHoa---
    // Tim kiem theo tu khoa: uu tien khop chinh xac, dong thoi chap nhan khop gan dung.
    // Ho tro ma NV, ho ten, so dien thoai va email.
    public NhanVien[] timKiemTuKhoa(String tuKhoa){
        if (tuKhoa == null || tuKhoa.isBlank()) return new NhanVien[0];
        String key = chuanHoaTuKhoa(tuKhoa);
        boolean[] khop = new boolean[soLuongNV];
        for (int i = 0; i < soLuongNV; i++) {
            NhanVien nv = danhSachNV[i];
            if (nv == null) continue;
            String thongTin = chuanHoaTuKhoa(
                nv.getMaNV() + " " + nv.getHoTen() + " " + nv.getSdt() + " " + nv.getEmail()
            );
            khop[i] = thongTin.contains(key);
        }
        return lietKe(khop);
    }

    private String chuanHoaTuKhoa(String value) {
        return java.text.Normalizer.normalize(value == null ? "" : value,
                java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(java.util.Locale.ROOT).trim();
    }

    public NhanVien[] timKiemGanDung(String tuKhoa) {
        return timKiemTuKhoa(tuKhoa);
    }

    // Cap nhat thong tin co ban, dong bo lai so nhan vien cua cac phong ban.
    public boolean capNhatThongTinCoBan(String maNV, String hoTen, java.time.LocalDate ngaySinh,
            String gioiTinh, String sdt, String email, double luongCoBan,
            java.time.LocalDate ngayVaoLam, String trangThai, PhongBan phongBanMoi) {
        NhanVien nv = timTheoMa(maNV);
        if (nv == null) return false;
        nv.setHoTen(hoTen);
        nv.setNgaySinh(ngaySinh);
        nv.setGioiTinh(gioiTinh);
        nv.setSdt(sdt);
        nv.setEmail(email);
        nv.setLuongCoBan(luongCoBan);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setTrangThai(trangThai);
        PhongBan phongBanCu = nv.getPhongBan();
        if (phongBanCu != phongBanMoi) {
            if (phongBanCu != null) phongBanCu.giamSoNhanVien();
            if (phongBanMoi != null) phongBanMoi.tangSoNhanVien();
            // Cho phep bo phan nhan vien khoi phong ban.
            if (phongBanMoi != null) nv.setPhongBan(phongBanMoi);
            else nv.xoaPhongBan();
        }
        return true;
    }

    public NhanVien[] lietKe(boolean[] khop){
        int dem = 0;
        for(int i = 0; i < khop.length; i++){
            if(khop[i]) 
                dem++;
        }
        NhanVien[] ketQua = new NhanVien[dem];
        int doi = 0;
        for(int i = 0; i < khop.length; i++){
            if(khop[i])
                ketQua[doi++] = danhSachNV[i];
        }
        return ketQua;
    }

//---timkiemtheophongban---
    public NhanVien[] timKiemTheoPhongBan(String maPB){
        if(maPB == null) return new NhanVien[0];
        boolean khop[] = new boolean[soLuongNV];
        for(int i = 0; i < soLuongNV; i++){
            if(danhSachNV[i] == null) continue;
            PhongBan pb = danhSachNV[i].getPhongBan();
            if(pb != null && pb.getMaPB() != null && pb.getMaPB().equalsIgnoreCase(maPB))
                khop[i] = true;
        }
        return lietKe(khop);
    }

//---timkiemtheoluong---
    public NhanVien[] timKiemTheoLuong(double min, double max){
        if(min > max){
            double tmp = max;
            max = min;
            min = tmp;
        }
        boolean khop[] = new boolean[soLuongNV];
        for(int i = 0; i < soLuongNV; i++){
            double luong = danhSachNV[i].tinhLuong();
            if(luong >= min && luong <= max){
                khop[i] = true;
            }
        }
        return lietKe(khop);
    }

//---inraketqua(in ra ket qua cua ham liet ke)---
    public void inKetQua(NhanVien[] ketQua){
        for(NhanVien nv: ketQua){
            nv.hienThiThongTin();
        }
    }

//---thongketheoloainhanvien---
    public void thongKeTheoLoaiNv(){
        int nhanVienTTG = 0;
        int nhanVienBTG = 0;
        int nhanVienHD = 0;
        for(int i = 0; i < soLuongNV; i++){
            NhanVien nv = danhSachNV[i];
            if(nv instanceof NhanVienToanThoiGian)
                nhanVienTTG++;
            if(nv instanceof NhanVienBanThoiGian)
                nhanVienBTG++;
            if(nv instanceof NhanVienHopDong)
                nhanVienHD++;
        }
        System.out.println("so luong nhan vien toan thoi gian: " + nhanVienTTG);
        System.out.println("so luong nhan vien ban thoi gian: " + nhanVienBTG);
        System.out.println("so luong nhan vien hop dong: " + nhanVienHD);
    }

//---thongketheotrangthai---
    public void thongKeTheoTrangThai(){
        System.out.println("thong ke theo trang thai");
        String tenTT[] = new String[soLuongNV];     //dung de them trang thai
        int demTT[] = new int[soLuongNV];       //dung de dem trang thai trung
        int stt = 0;
        for(int i = 0; i < soLuongNV; i++){
            String tt = danhSachNV[i].getTrangThai();
            if(tt == null || tt.isEmpty())
                tt = "chua co trang thai";
            int vitri = -1;
            for(int j = 0; j < stt; j++){
                if(tenTT[j].equalsIgnoreCase(tt)){
                    vitri = j;
                    break;
                }
            }
            if(vitri == -1){        //trang thai mai, them trang thai
                tenTT[stt] = tt;
                demTT[stt] = 1;
                stt++;
            }
            else{
                demTT[vitri]++;
            }
        }
        for(int i = 0; i < stt; i++){
            System.out.println("trang thai " + tenTT[i] + " co so luong la:" + demTT[i]);
        }
        if(stt == 0) System.out.println("danh sach rong");
    }

    public void thongKeLuong(){
        if(soLuongNV == 0) return;
        double tong = 0;
        NhanVien luongMax = danhSachNV[0];
        NhanVien luongMin = danhSachNV[0];
        for(int i = 0; i < soLuongNV; i++){
            double l = danhSachNV[i].tinhLuong();
            tong += l;
            if(l > luongMax.tinhLuong())
                luongMax = danhSachNV[i];
            if(l < luongMin.tinhLuong())
                luongMin = danhSachNV[i];
        }
        System.out.println("tong luong cua toan cong ty: " + tong);
        System.out.println("luong trung binh cua 1 nhan vien: " + (tong/soLuongNV));
        System.out.println("nhan vien co luong cao nhat: " + luongMax.getMaNV() + " " + luongMax.getHoTen() + " " + luongMax.tinhLuong());
        System.out.println("nhan vien co luong cao thap nhat: " + luongMin.getMaNV() + " " + luongMin.getHoTen() + " " + luongMin.tinhLuong());
    }

@Override 
    public void docFile(){
        File f = new File(DUONG_DAN);
        if(!f.exists()){
            System.out.println("Chua co du lieu nhan vien (" + DUONG_DAN + "), bat dau moi.");
            return;
        }
        NhanVien[] tam;     //thuc hien kiem tra doi tuong sau doc file, neu dung ghi vao danh sach dang co, khong thi bo qua
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            int n = ois.readInt();
            tam = new NhanVien[n];
            for (int i = 0; i < n; i++) {
                tam[i] = (NhanVien) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Lỗi đọc file nhân viên: " + e.getMessage());
            return;
        }

        //neu khong loi thuc hien dua vao danh sach
        int boQua = 0;
        for(NhanVien nv: tam){
            lienKetLaiPhongBan(nv);     //doi tro cua nv vao doi tuong phong ban trong danh sach(phong ban that)
            if(!themNhanVien(nv))   //kiem tra so truong hop ma khong them nhan vien(trung ma, nv == null, danh sach day)
                boQua++;
        }
        System.out.println("da doc soLuongNv = " + soLuongNV);
        System.out.println("so nhan vien da bo qua(trung ma, danh sach day, loi tham chieu): " + boQua);
    }

@Override 
    //dung su ly ngoai le de chuong trinh khong sap giua chung
    public void ghiFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DUONG_DAN))) {   //FileOutputStream: ghi ra file kem xoa noi dung cu, ObjectOutputStream: giup ghi du lieu ca 1 doi tuong (implements Seriazilizable)
            oos.writeInt(soLuongNV);
            for (int i = 0; i < soLuongNV; i++) {
                oos.writeObject(danhSachNV[i]);   // ghi ca doi tuong(kem phongban, chuc vu, trinh do chuyen mon o ben trong (luu y: cac class nay bat buoc phai implement Serializable))
            }
            System.out.println("da ghi " + soLuongNV + " nhan vien vao file" + DUONG_DAN);
        } catch (NotSerializableException e) {
            System.out.println("loi: class " + e.getMessage()
                    + " chua implements Serializable. hay them implements Serializable cho NhanVien.");
        } catch (IOException e) {
            System.out.println("loi ghi file nhan vien " + e.getMessage());
        }
    }
}