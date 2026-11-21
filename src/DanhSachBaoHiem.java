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
            GiaoDien.in("Loi: Ma bao hiem da ton tai!");
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

    //in danh sach bao hiem dang bang co STT (dung chung cho xem danh sach va tim theo ma NV)
    public static void inBang(BaoHiem[] ds) {
        String[][] dong = new String[0][];
        for (BaoHiem bh : ds) {
            if (bh == null) continue;
            String loai;
            String chiTiet;
            if (bh instanceof BaoHiemYTe) {
                BaoHiemYTe yt = (BaoHiemYTe) bh;
                loai = "BHYT";
                chiTiet = "KCB: " + yt.getNoiKhamChuaBenh() + "\nHan the: " + yt.getHanTheDen()
                        + (yt.conHanThe() ? " (con han)" : " (het han)");
            } else {
                loai = "BHXH";
                chiTiet = String.format("NV dong %.1f%% - DN dong %.1f%%",
                        BaoHiemXaHoi.getTyLeNV() * 100, BaoHiemXaHoi.getTyLeDN() * 100);
            }
            dong = Mang.them(dong, new String[]{bh.getMaBH(), bh.getMaNV(), bh.getSoSo(),
                    String.valueOf(bh.getNgayThamGia()), bh.getTrangThai(), loai, chiTiet});
        }
        GiaoDien.inBang(
                new String[]{"MA BH", "MA NV", "SO SO/THE", "NGAY THAM GIA", "TRANG THAI", "LOAI", "CHI TIET"},
                new int[]{8, 8, 14, 13, 10, 5, 36},
                new String[]{GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI, GiaoDien.TRAI},
                dong);
    }

    private static final String FILE_TEXT = DuongDanDuLieu.file("baohiem.txt");

    // Dinh dang moi dong:
    //   BHXH|maBH|maNV|soSo|ngayThamGia|trangThai
    //   BHYT|maBH|maNV|soSo|ngayThamGia|trangThai|noiKhamChuaBenh|hanTheDen
    @Override
    public void ghiFile() {
        String[] dong = new String[0];
        for (int i = 0; i < soLuong; i++) {
            BaoHiem bh = danhSach[i];
            if (bh instanceof BaoHiemYTe) {
                BaoHiemYTe yt = (BaoHiemYTe) bh;
                dong = Mang.them(dong, TepVanBan.ghep("BHYT", yt.getMaBH(), yt.getMaNV(), yt.getSoSo(), yt.getNgayThamGia(),
                        yt.getTrangThai(), yt.getNoiKhamChuaBenh(), yt.getHanTheDen()));
            } else {
                dong = Mang.them(dong, TepVanBan.ghep("BHXH", bh.getMaBH(), bh.getMaNV(), bh.getSoSo(), bh.getNgayThamGia(),
                        bh.getTrangThai()));
            }
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "BHXH|maBH|maNV|soSo|ngayThamGia|trangThai   va   BHYT|...|noiKCB|hanTheDen", dong);
            GiaoDien.in("Da luu danh sach bao hiem.");
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi luu bao hiem: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            String[][] cacDong = TepVanBan.docDong(FILE_TEXT);
            danhSach = new BaoHiem[Math.max(10, cacDong.length)];
            soLuong = 0;
            for (String[] p : cacDong) {
                try {
                    BaoHiem bh;
                    if (p[0].equalsIgnoreCase("BHYT") && p.length >= 8) {
                        bh = new BaoHiemYTe(p[1], p[2], p[3], java.time.LocalDate.parse(p[4]),
                                p[6], java.time.LocalDate.parse(p[7]));
                    } else if (p[0].equalsIgnoreCase("BHXH") && p.length >= 6) {
                        bh = new BaoHiemXaHoi(p[1], p[2], p[3], java.time.LocalDate.parse(p[4]));
                    } else {
                        continue;
                    }
                    bh.setTrangThai(p[5]);
                    them(bh);
                } catch (RuntimeException e) {
                    GiaoDien.in("Bo qua dong bao hiem loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            GiaoDien.in("Loi doc bao hiem: " + e.getMessage());
        }
    }


    public void xuatDanhSach() {
        if (soLuong == 0) {
            GiaoDien.in("Danh sach bao hiem rong.");
            return;
        }
        BaoHiem[] ds = new BaoHiem[soLuong];
        System.arraycopy(danhSach, 0, ds, 0, soLuong);
        inBang(ds);
        GiaoDien.in("Tong so ho so bao hiem: " + soLuong);
    }
}
