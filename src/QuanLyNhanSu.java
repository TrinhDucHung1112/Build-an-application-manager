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
    private static final String DUONG_DAN = DuongDanDuLieu.file("nhanvien.txt");

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
            GiaoDien.in("Danh sach nhan vien trong.");
            return;
        }
        inBang(getDanhSachNV());
        GiaoDien.in("Tong so nhan vien: " + soLuongNV);
    }

    //in danh sach nhan vien dang bang co STT (dung chung cho xem danh sach, tim kiem, bao cao)
    //muon xem day du thong tin 1 nhan vien thi goi nv.hienThiThongTin()
    public static void inBang(NhanVien[] ds) {
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for (NhanVien nv : ds) {
            if (nv == null) continue;
            dong.add(new String[]{
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getPhongBan() == null ? "(chua phan)" : nv.getPhongBan().getTenPB(),
                nv.getChucVu() == null ? "" : nv.getChucVu().getTenCV(),
                nv.tenLoai(),
                String.format("%,.0f", nv.tinhLuong()),
                nv.getTrangThai()});
        }
        GiaoDien.inBang(
            new String[]{"MA NV", "HO TEN", "PHONG BAN", "CHUC VU", "LOAI", "LUONG", "TRANG THAI"},
            new int[]{8, 22, 14, 16, 8, 12, 11},
            new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI,
                         GiaoDien.TRAI, GiaoDien.PHAI, GiaoDien.TRAI},
            dong);
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

//---timkiemuutienchinhxac---
    // Tim theo nhieu khoa (ma NV, ho ten, sdt, email): ket qua khop CHINH XAC dung truoc, khop GAN DUNG dung sau
    public NhanVien[] timKiemUuTien(String tuKhoa){
        if (tuKhoa == null || tuKhoa.isBlank()) return new NhanVien[0];
        String key = chuanHoaTuKhoa(tuKhoa);
        boolean[] chinhXac = new boolean[soLuongNV];
        boolean[] ganDung = new boolean[soLuongNV];
        for (int i = 0; i < soLuongNV; i++) {
            NhanVien nv = danhSachNV[i];
            if (nv == null) continue;
            String[] cacKhoa = { nv.getMaNV(), nv.getHoTen(), nv.getSdt(), nv.getEmail() };
            for (String k : cacKhoa) {
                if (chuanHoaTuKhoa(k).equals(key)) chinhXac[i] = true;
            }
            String thongTin = chuanHoaTuKhoa(
                nv.getMaNV() + " " + nv.getHoTen() + " " + nv.getSdt() + " " + nv.getEmail()
            );
            ganDung[i] = !chinhXac[i] && thongTin.contains(key);
        }
        NhanVien[] a = lietKe(chinhXac);
        NhanVien[] b = lietKe(ganDung);
        NhanVien[] ketQua = new NhanVien[a.length + b.length];
        System.arraycopy(a, 0, ketQua, 0, a.length);
        System.arraycopy(b, 0, ketQua, a.length, b.length);
        return ketQua;
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
        GiaoDien.in("Tim thay " + ketQua.length + " nhan vien.");
        if(ketQua.length > 0) inBang(ketQua);
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
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        dong.add(new String[]{"Nhan vien toan thoi gian", String.valueOf(nhanVienTTG)});
        dong.add(new String[]{"Nhan vien ban thoi gian", String.valueOf(nhanVienBTG)});
        dong.add(new String[]{"Nhan vien hop dong", String.valueOf(nhanVienHD)});
        GiaoDien.inBang(new String[]{"LOAI NHAN VIEN", "SO LUONG"}, new int[]{28, 10},
                new String[]{GiaoDien.TRAI, GiaoDien.PHAI}, dong);
        GiaoDien.in("Tong so nhan vien: " + soLuongNV);
    }

//---thongketheotrangthai---
    public void thongKeTheoTrangThai(){
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
        if(stt == 0){
            GiaoDien.in("Danh sach rong.");
            return;
        }
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for(int i = 0; i < stt; i++){
            dong.add(new String[]{tenTT[i], String.valueOf(demTT[i])});
        }
        GiaoDien.inBang(new String[]{"TRANG THAI", "SO LUONG"}, new int[]{28, 10},
                new String[]{GiaoDien.TRAI, GiaoDien.PHAI}, dong);
    }

    public void thongKeLuong(){
        if(soLuongNV == 0){
            GiaoDien.in("Danh sach nhan vien rong, chua co gi de thong ke luong.");
            return;
        }
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
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        dong.add(new String[]{"Tong luong toan cong ty", String.format("%,.0f", tong) + " VND"});
        dong.add(new String[]{"Luong trung binh 1 nhan vien", String.format("%,.0f", tong/soLuongNV) + " VND"});
        dong.add(new String[]{"Luong cao nhat", luongMax.getMaNV() + " - " + luongMax.getHoTen() + ": " + String.format("%,.0f", luongMax.tinhLuong()) + " VND"});
        dong.add(new String[]{"Luong thap nhat", luongMin.getMaNV() + " - " + luongMin.getHoTen() + ": " + String.format("%,.0f", luongMin.tinhLuong()) + " VND"});
        GiaoDien.inThongKe(dong);
    }

    // Dinh dang moi dong (13 truong chung + truong rieng cua tung loai):
    //   loai|maNV|hoTen|ngaySinh|gioiTinh|sdt|email|luongCoBan|ngayVaoLam|trangThai|maPB|chucVu|trinhDo|...
    //   TTG : ...|phuCap|thamNien
    //   BTG : ...|soGioLam|luongTheoGio
    //   HD  : ...|thoiHanHopDong|luongTheoHopDong|ngayBatDauThucTe|ngayKetThucTe|thoiGianThucTe
    //   chucVu  = maCV;tenCV;heSo           (de trong neu chua co)
    //   trinhDo = maTD;tenTD;nganh;truong;namTotNghiep;heSo  (nhieu muc cach nhau boi ~)
    @Override
    public void docFile(){
        File f = new File(DUONG_DAN);
        if(!f.exists()){
            GiaoDien.in("Chua co du lieu nhan vien (" + DUONG_DAN + "), bat dau moi.");
            return;
        }
        java.util.List<String[]> cacDong;
        try {
            cacDong = TepVanBan.docDong(DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi doc file nhan vien: " + e.getMessage());
            return;
        }

        //doc thanh cong moi xoa danh sach cu, tranh viec doc 2 lan bi trung hoac lech phong ban
        java.util.Arrays.fill(danhSachNV, null);
        soLuongNV = 0;

        int boQua = 0;
        for(String[] p : cacDong){
            NhanVien nv = taoNhanVienTuDong(p);
            if(nv == null || !themNhanVien(nv)){    //loi dinh dang, trung ma hoac danh sach day
                GiaoDien.in("Bo qua dong nhan vien: " + String.join("|", p));
                boQua++;
            }
        }
        GiaoDien.in("Da doc " + soLuongNV + " nhan vien tu file, bo qua " + boQua + " dong (trung ma / danh sach day / sai dinh dang).");
    }

    //tao nhan vien dung loai tu 1 dong text, tra ve null neu dong sai
    private NhanVien taoNhanVienTuDong(String[] p){
        try {
            if(p.length < 15) return null;
            java.time.LocalDate ngaySinh = java.time.LocalDate.parse(p[3]);
            java.time.LocalDate ngayVaoLam = java.time.LocalDate.parse(p[8]);
            double luongCB = Double.parseDouble(p[7]);
            NhanVien nv;
            switch(p[0].toUpperCase()){
                case "TTG":
                    nv = new NhanVienToanThoiGian(p[1], p[2], ngaySinh, p[4], p[5], p[6], luongCB, ngayVaoLam, p[9],
                            Double.parseDouble(p[13]), Integer.parseInt(p[14]));
                    break;
                case "BTG":
                    nv = new NhanVienBanThoiGian(p[1], p[2], ngaySinh, p[4], p[5], p[6], luongCB, ngayVaoLam, p[9],
                            Integer.parseInt(p[13]), Double.parseDouble(p[14]));
                    break;
                case "HD":
                    if(p.length < 18) return null;
                    nv = new NhanVienHopDong(p[1], p[2], ngaySinh, p[4], p[5], p[6], luongCB, ngayVaoLam, p[9],
                            Integer.parseInt(p[13]), Double.parseDouble(p[14]),
                            java.time.LocalDate.parse(p[15]), java.time.LocalDate.parse(p[16]), Integer.parseInt(p[17]));
                    break;
                default:
                    return null;
            }
            if(!p[10].isEmpty() && danhSachPB != null){
                PhongBan pb = danhSachPB.timTheoMa(p[10]);      //tro vao phong ban that trong danhSachPB
                if(pb != null) nv.setPhongBan(pb);
            }
            if(!p[11].isEmpty()){
                String[] cv = p[11].split(";");
                nv.setChucVu(new ChucVu(cv[0], cv[1], Double.parseDouble(cv[2])));
            }
            if(!p[12].isEmpty()){
                String[] cacTD = p[12].split("~");
                TrinhDoChuyenMon[] td = new TrinhDoChuyenMon[cacTD.length];
                for(int i = 0; i < cacTD.length; i++){
                    String[] t = cacTD[i].split(";");
                    td[i] = new TrinhDoChuyenMon(t[0], t[1], t[2], t[3], Integer.parseInt(t[4]), Double.parseDouble(t[5]));
                }
                nv.setTrinhDo(td);
            }
            return nv;
        } catch (RuntimeException e) {       //DateTimeParseException, NumberFormatException, IllegalArgumentException...
            return null;
        }
    }

    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuongNV; i++) {
            NhanVien nv = danhSachNV[i];
            String loai;
            Object[] rieng;
            if (nv instanceof NhanVienToanThoiGian) {
                NhanVienToanThoiGian t = (NhanVienToanThoiGian) nv;
                loai = "TTG";
                rieng = new Object[]{t.getphuCap(), t.getthamNien()};
            } else if (nv instanceof NhanVienBanThoiGian) {
                NhanVienBanThoiGian b = (NhanVienBanThoiGian) nv;
                loai = "BTG";
                rieng = new Object[]{b.getsoGioLam(), b.getluongTheoGio()};
            } else {
                NhanVienHopDong h = (NhanVienHopDong) nv;
                loai = "HD";
                rieng = new Object[]{h.getthoiHanHopDong(), h.getluongTheoHopDong(),
                        h.getngayBatDauThucTe(), h.getngayKetThucTe(), h.getthoiGianThucTe()};
            }
            String maPB = nv.getPhongBan() == null ? "" : nv.getPhongBan().getMaPB();
            Object[] chung = {loai, nv.getMaNV(), nv.getHoTen(), nv.getNgaySinh(), nv.getGioiTinh(), nv.getSdt(),
                    nv.getEmail(), nv.getLuongCoBan(), nv.getNgayVaoLam(), nv.getTrangThai(), maPB,
                    chucVuThanhChuoi(nv.getChucVu()), trinhDoThanhChuoi(nv.getTrinhDo())};
            Object[] tatCa = java.util.Arrays.copyOf(chung, chung.length + rieng.length);
            System.arraycopy(rieng, 0, tatCa, chung.length, rieng.length);
            dong.add(TepVanBan.ghep(tatCa));
        }
        try {
            TepVanBan.ghiDong(DUONG_DAN, "loai|maNV|hoTen|ngaySinh|gioiTinh|sdt|email|luongCoBan|ngayVaoLam|trangThai|maPB|chucVu|trinhDo|(truong rieng theo loai)", dong);
            GiaoDien.in("Da ghi " + soLuongNV + " nhan vien vao file " + DUONG_DAN);
        } catch (IOException e) {
            GiaoDien.in("Loi ghi file nhan vien: " + e.getMessage());
        }
    }

    private String chucVuThanhChuoi(ChucVu cv){
        if(cv == null) return "";
        return TepVanBan.lamSachCon(cv.getMaCV()) + ";" + TepVanBan.lamSachCon(cv.getTenCV()) + ";" + TepVanBan.soDep(cv.getHeSoLuong());
    }

    private String trinhDoThanhChuoi(TrinhDoChuyenMon[] ds){
        if(ds == null || ds.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for(TrinhDoChuyenMon t : ds){
            if(t == null) continue;
            if(sb.length() > 0) sb.append("~");
            sb.append(TepVanBan.lamSachCon(t.getMaTD())).append(";").append(TepVanBan.lamSachCon(t.getTenTD())).append(";")
              .append(TepVanBan.lamSachCon(t.getChuyenNganh())).append(";").append(TepVanBan.lamSachCon(t.getTruongDaoTao())).append(";")
              .append(t.getNamTotNghiep()).append(";").append(TepVanBan.soDep(t.getHeSoTD()));
        }
        return sb.toString();
    }

}
