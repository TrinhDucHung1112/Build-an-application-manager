public class DanhSachBaoHiem implements IQuanLyDuLieu {
    private BaoHiem[] danhSach;
    private int soLuong;

    public DanhSachBaoHiem() {
        this(10);
    }

    public DanhSachBaoHiem(int sucChua) {
        this.danhSach = new BaoHiem[sucChua > 0 ? sucChua : 10];
        this.soLuong = 0;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // Lấy phần tử theo chỉ số (để lớp báo cáo duyệt danh sách)
    public BaoHiem getBaoHiem(int i) {
        if (i < 0 || i >= soLuong) return null;
        return danhSach[i];
    }

    private void tangKichThuocMang() {
        BaoHiem[] mangMoi = new BaoHiem[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    // Trả về vị trí của mã BH, -1 nếu không có
    private int viTriTheoMa(String maBH) {
        if (maBH == null) return -1;
        for (int i = 0; i < soLuong; i++) {
            if (maBH.equalsIgnoreCase(danhSach[i].getMaBH())) {
                return i;
            }
        }
        return -1;
    }

    public boolean them(BaoHiem bh) {
        if (bh == null) return false;
        if (viTriTheoMa(bh.getMaBH()) != -1) {
            System.out.println("Lỗi: Mã bảo hiểm đã tồn tại!");
            return false;
        }
        if (soLuong >= danhSach.length) {
            tangKichThuocMang();
        }
        danhSach[soLuong++] = bh;
        return true;
    }

    public boolean xoaTheoMa(String maBH) {
        int viTri = viTriTheoMa(maBH);
        if (viTri == -1) return false;
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[--soLuong] = null;
        return true;
    }

    public BaoHiem[] timTheoMaNV(String maNV) {
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equalsIgnoreCase(maNV)) {
                dem++;
            }
        }
        BaoHiem[] ketQua = new BaoHiem[dem];
        int idx = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equalsIgnoreCase(maNV)) {
                ketQua[idx++] = danhSach[i];
            }
        }
        return ketQua;
    }

    // Tổng tiền nhân viên đóng cho các bảo hiểm còn hiệu lực của 1 nhân viên (đa hình)
    public double tongTienNhanVienDong(String maNV, double luongDongBH) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equalsIgnoreCase(maNV)
                    && danhSach[i].getTrangThai().equalsIgnoreCase("HieuLuc")) {
                tong += danhSach[i].tinhTienNhanVienDong(luongDongBH);
            }
        }
        return tong;
    }

    // Tổng tiền doanh nghiệp đóng cho các bảo hiểm còn hiệu lực của 1 nhân viên (đa hình)
    public double tongTienDoanhNghiepDong(String maNV, double luongDongBH) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equalsIgnoreCase(maNV)
                    && danhSach[i].getTrangThai().equalsIgnoreCase("HieuLuc")) {
                tong += danhSach[i].tinhTienDoanhNghiepDong(luongDongBH);
            }
        }
        return tong;
    }

    public static void inTieuDeBang() {
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-8s | %-8s | %-12s | %-13s | %-10s | %-6s | %s%n",
                "MÃ BH", "MÃ NV", "SỐ SỔ/THẺ", "NGÀY THAM GIA", "TRẠNG THÁI", "LOẠI", "CHI TIẾT");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    @Override
    public void ghiFile() {
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(DuongDanDuLieu.file("baohiem.dat")))) {
            out.writeInt(soLuong);
            for (int i = 0; i < soLuong; i++) out.writeObject(danhSach[i]);
            System.out.println("Da luu danh sach bao hiem.");
        } catch (java.io.IOException e) { System.out.println("Loi luu bao hiem: " + e.getMessage()); }
    }

    @Override
    public void docFile() {
        java.io.File f = new java.io.File(DuongDanDuLieu.file("baohiem.dat"));
        if (!f.exists()) return;
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(f))) {
            int n = in.readInt();
            danhSach = new BaoHiem[Math.max(10, n)];
            soLuong = 0;
            for (int i = 0; i < n; i++) them((BaoHiem) in.readObject());
        } catch (java.io.IOException | ClassNotFoundException e) {
            System.out.println("Loi doc bao hiem: " + e.getMessage());
        }
    }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Danh sách bảo hiểm rỗng.");
            return;
        }
        inTieuDeBang();
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].hienThiThongTin();
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }
}
