import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DanhSachBangLuong implements IQuanLyDuLieu {
//----thuoc tinh----
    private static final String TEN_FILE = DuongDanDuLieu.file("bangluong.txt");

    private BangLuong[] danhSach;
    private int soLuong;

//----constructor----
    public DanhSachBangLuong(int sucChua) {
        this.danhSach = new BangLuong[sucChua];
        this.soLuong = 0;
    }

//----methods----
//---them, tim---
    public boolean them(BangLuong bl) {
        if (bl == null) return false;
        if (soLuong >= danhSach.length)
            danhSach = java.util.Arrays.copyOf(danhSach, danhSach.length * 2);
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaBangLuong().equals(bl.getMaBangLuong())) return false;    //trung ma
        }
        danhSach[soLuong++] = bl;
        return true;
    }

    public BangLuong timTheoMaNV(String maNV, int thang, int nam) {
        for (int i = 0; i < soLuong; i++) {
            BangLuong bl = danhSach[i];
            if (bl.getMaNV().equals(maNV) && bl.getThang() == thang && bl.getNam() == nam) return bl;
        }
        return null;
    }

//---lap bang luong---
    //phu cap lay tu ChucVu va TrinhDoChuyenMon cua Nguoi 2
    private double tinhPhuCap(NhanVien nv) {
        double pc = 0;
        if (nv.getChucVu() != null) pc += nv.getChucVu().tinhPhuCapChucVu(nv.getLuongCoBan());
        if (nv.getTrinhDo() != null) {
            for (TrinhDoChuyenMon td : nv.getTrinhDo()) {
                if (td != null) pc += td.tinhPhuCapTrinhDo(nv.getLuongCoBan());
            }
        }
        return pc;
    }

    //bao hiem lay tu DanhSachBaoHiem cua Nguoi 5
    private double tinhTienBaoHiem(NhanVien nv, DanhSachBaoHiem dsBH) {
        double tong = 0;
        BaoHiem[] ds = dsBH.timTheoMaNV(nv.getMaNV());
        if (ds == null) return 0;
        for (BaoHiem bh : ds) {
            if (bh != null && "HieuLuc".equalsIgnoreCase(bh.getTrangThai())) {
                if (!(bh instanceof BaoHiemYTe) || ((BaoHiemYTe) bh).conHanThe())
                    tong += bh.tinhTienNhanVienDong(nv.getLuongCoBan());
            }
        }
        return tong;
    }

    //lap bang luong 1 nhan vien trong thang, da co thi thay the
    public BangLuong lapBangLuong(NhanVien nv, int thang, int nam,
                                  DanhSachThuongPhat dsTP, DanhSachBaoHiem dsBH) {
        String maNV = nv.getMaNV();
        BangLuong bl = new BangLuong("BL_" + maNV + "_" + thang + "_" + nam, thang, nam);
        bl.setMaNV(maNV);
        bl.setLuongNhanVien(nv.tinhLuong());
        bl.setPhuCap(tinhPhuCap(nv));

        ThuongPhat[] dsTPcuaNV = dsTP.timTheoMaNV(maNV);
        for (ThuongPhat tp : dsTPcuaNV) {
            if (tp.getNgayApDung().getMonthValue() == thang && tp.getNgayApDung().getYear() == nam) {
                bl.themLuongThuong(new LuongThuong(tp.getMaTP(), tp.getLoai(), tp.tinhSoTien(), tp.getLyDo()));
            }
        }
        bl.setTienBaoHiem(tinhTienBaoHiem(nv, dsBH));
        bl.tinhLuongThucNhan();

        boolean daThay = false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaBangLuong().equals(bl.getMaBangLuong())) {
                danhSach[i] = bl;
                daThay = true;
                break;
            }
        }
        if (!daThay) them(bl);
        return bl;
    }

    public void lapBangLuongChoTatCa(NhanVien[] dsNV, int thang, int nam,
                                     DanhSachThuongPhat dsTP, DanhSachBaoHiem dsBH) {
        for (NhanVien nv : dsNV) {
            if (nv != null) lapBangLuong(nv, thang, nam, dsTP, dsBH);
        }
    }

//---xuat---
    public void xuatBangLuongNhanVien(String maNV) {
        boolean co = false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equals(maNV)) {
                danhSach[i].xuatBangLuong();
                co = true;
            }
        }
        if (!co) System.out.println("Khong co bang luong cua nhan vien " + maNV);
    }

    //ql de lay nhan vien theo phong ban
    public void xuatBangLuongTheoPhong(String maPB, int thang, int nam, QuanLyNhanSu ql) {
        NhanVien[] dsNV = ql.timKiemTheoPhongBan(maPB);
        boolean co = false;
        if (dsNV != null) {
            for (NhanVien nv : dsNV) {
                if (nv == null) continue;
                BangLuong bl = timTheoMaNV(nv.getMaNV(), thang, nam);
                if (bl != null) {
                    bl.xuatBangLuong();
                    co = true;
                }
            }
        }
        if (!co) System.out.println("Khong co bang luong phong " + maPB + " thang " + thang + "/" + nam);
    }

    public void xuatBangLuongCongTy(int thang, int nam) {
        boolean co = false;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getThang() == thang && danhSach[i].getNam() == nam) {
                danhSach[i].xuatBangLuong();
                co = true;
            }
        }
        if (!co) System.out.println("Khong co bang luong thang " + thang + "/" + nam);
    }

//---thong ke (theo luong thuc nhan)---
    public double tongQuyLuong(int thang, int nam) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getThang() == thang && danhSach[i].getNam() == nam) {
                tong += danhSach[i].getLuongThucNhan();
            }
        }
        return tong;
    }

    public BangLuong luongCaoNhat(int thang, int nam) {
        BangLuong max = null;
        for (int i = 0; i < soLuong; i++) {
            BangLuong bl = danhSach[i];
            if (bl.getThang() == thang && bl.getNam() == nam
                    && (max == null || bl.getLuongThucNhan() > max.getLuongThucNhan())) {
                max = bl;
            }
        }
        return max;
    }

    public BangLuong luongThapNhat(int thang, int nam) {
        BangLuong min = null;
        for (int i = 0; i < soLuong; i++) {
            BangLuong bl = danhSach[i];
            if (bl.getThang() == thang && bl.getNam() == nam
                    && (min == null || bl.getLuongThucNhan() < min.getLuongThucNhan())) {
                min = bl;
            }
        }
        return min;
    }

    public double luongTrungBinh(int thang, int nam) {
        double tong = 0;
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getThang() == thang && danhSach[i].getNam() == nam) {
                tong += danhSach[i].getLuongThucNhan();
                dem++;
            }
        }
        return dem == 0 ? 0 : tong / dem;
    }

//---doc, ghi file---
    //moi dong la 1 bang luong, cac truong cach nhau dau phay
    @Override
    public void ghiFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TEN_FILE))) {
            for (int i = 0; i < soLuong; i++) {
                BangLuong bl = danhSach[i];
                pw.println(bl.getMaBangLuong() + "," + bl.getMaNV() + "," + bl.getThang() + "," + bl.getNam()
                        + "," + TepVanBan.soDep(bl.getPhuCap()) + "," + TepVanBan.soDep(bl.getTienThuong())
                        + "," + TepVanBan.soDep(bl.getTienPhat()) + "," + TepVanBan.soDep(bl.getTienBaoHiem())
                        + "," + TepVanBan.soDep(bl.getLuongNhanVien()));
            }
            System.out.println("Da ghi " + soLuong + " bang luong vao file " + TEN_FILE);
        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        File f = new File(TEN_FILE);
        if (!f.exists()) {
            System.out.println("Chua co file " + TEN_FILE);
            return;
        }
        soLuong = 0;
        danhSach = new BangLuong[Math.max(10, danhSach.length)];
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;
                String[] p = dong.split(",");
                if (p.length < 9) continue;
                try {
                    them(new BangLuong(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                            Double.parseDouble(p[4]), Double.parseDouble(p[5]), Double.parseDouble(p[6]),
                            Double.parseDouble(p[7]), Double.parseDouble(p[8])));
                } catch (NumberFormatException e) {
                    System.out.println("Bo qua dong sai dinh dang: " + dong);
                }
            }
            System.out.println("Da doc " + soLuong + " bang luong tu file " + TEN_FILE);
        } catch (IOException e) {
            System.out.println("Loi doc file: " + e.getMessage());
        }
    }

//---getter---
    public int getSoLuong() { return soLuong; }
    public BangLuong getBangLuong(int i) {
        return (i < 0 || i >= soLuong) ? null : danhSach[i];
    }
}
