import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tong hop cac bao cao co ban cua he thong quan ly nhan su.
 */
public class BaoCaoNhanSu {
    private LocalDate ngayLap;
    private DanhSachBaoHiem dsBaoHiem;

    public BaoCaoNhanSu(DanhSachBaoHiem dsBaoHiem) {
        this.ngayLap = LocalDate.now();
        this.dsBaoHiem = dsBaoHiem;
    }

    public BaoCaoNhanSu(QuanLyNhanSu ql, DanhSachBangLuong dsBangLuong,
                        DanhSachBaoHiem dsBaoHiem, DanhSachThuongPhat dsThuongPhat) {
        this(dsBaoHiem);
    }

    public LocalDate getNgayLap() { return ngayLap; }

    public void thongKeBaoHiem() {
        int soBHXH = 0, soBHYT = 0, soTheHetHan = 0;
        for (int i = 0; i < dsBaoHiem.getSoLuong(); i++) {
            BaoHiem bh = dsBaoHiem.getBaoHiem(i);
            if (bh instanceof BaoHiemXaHoi) soBHXH++;
            if (bh instanceof BaoHiemYTe) {
                soBHYT++;
                if (!((BaoHiemYTe) bh).conHanThe()) soTheHetHan++;
            }
        }
        System.out.println("\n----- THONG KE BAO HIEM -----");
        System.out.println("Tong so ho so       : " + dsBaoHiem.getSoLuong());
        System.out.println("So ho so BHXH       : " + soBHXH);
        System.out.println("So ho so BHYT       : " + soBHYT);
        System.out.println("So the BHYT het han : " + soTheHetHan);
    }

    public void thongKeTheoPhongBan(QuanLyNhanSu ql) {
        Map<String, Integer> dem = new LinkedHashMap<>();
        for (NhanVien nv : ql.getDanhSachNV()) {
            String ten = nv.getPhongBan() == null ? "Chua phan phong" : nv.getPhongBan().getTenPB();
            dem.put(ten, dem.getOrDefault(ten, 0) + 1);
        }
        System.out.println("\n----- NHAN SU THEO PHONG BAN -----");
        if (dem.isEmpty()) System.out.println("Chua co nhan vien.");
        for (Map.Entry<String, Integer> e : dem.entrySet())
            System.out.println(e.getKey() + ": " + e.getValue() + " nhan vien");
    }

    public void thongKeLuong(DanhSachBangLuong ds, int thang, int nam) {
        System.out.println("\n----- THONG KE LUONG " + thang + "/" + nam + " -----");
        System.out.printf("Tong quy luong : %,.0f VND%n", ds.tongQuyLuong(thang, nam));
        System.out.printf("Luong trung binh: %,.0f VND%n", ds.luongTrungBinh(thang, nam));
        BangLuong cao = ds.luongCaoNhat(thang, nam);
        BangLuong thap = ds.luongThapNhat(thang, nam);
        if (cao == null) System.out.println("Chua co bang luong trong thang.");
        else {
            System.out.printf("Luong cao nhat : %s - %,.0f VND%n", cao.getMaNV(), cao.getLuongThucNhan());
            System.out.printf("Luong thap nhat: %s - %,.0f VND%n", thap.getMaNV(), thap.getLuongThucNhan());
        }
    }

    public void thongKeLuongCaoThapNhat(DanhSachBangLuong ds, int thang, int nam) {
        thongKeLuong(ds, thang, nam);
    }

    public void thongKeHoatDongVaNghi(QuanLyNhanSu ql) {
        int dangLam = 0, tamNghi = 0, nghi = 0;
        for (NhanVien nv : ql.getDanhSachNV()) {
            String tt = nv.getTrangThai() == null ? "" : nv.getTrangThai().toLowerCase();
            if (tt.contains("dang") || tt.contains("đang") || tt.contains("lam") || tt.contains("làm")) dangLam++;
            else if (tt.contains("tam")) tamNghi++;
            else nghi++;
        }
        System.out.println("\n----- TRANG THAI NHAN SU -----");
        System.out.println("Dang lam: " + dangLam);
        System.out.println("Tam nghi: " + tamNghi);
        System.out.println("Nghi/khac: " + nghi);
    }

    public void inBaoCaoToanCongTy(QuanLyNhanSu ql, DanhSachBangLuong ds, int thang, int nam) {
        System.out.println("\n========== BAO CAO TOAN CONG TY ==========");
        System.out.println("Ngay lap: " + LocalDate.now());
        System.out.println("Tong nhan vien: " + ql.getSoLuongNV());
        thongKeTheoPhongBan(ql);
        thongKeHoatDongVaNghi(ql);
        thongKeLuong(ds, thang, nam);
        thongKeBaoHiem();
    }

    public void inBaoCaoTheoPhong(String maPB, QuanLyNhanSu ql, DanhSachBangLuong ds, int thang, int nam) {
        NhanVien[] nhanVien = ql.timKiemTheoPhongBan(maPB);
        System.out.println("\n========== BAO CAO PHONG " + maPB + " ==========");
        System.out.println("So nhan vien: " + nhanVien.length);
        ql.inKetQua(nhanVien);
        double tongLuong = 0;
        for (NhanVien nv : nhanVien) {
            BangLuong bl = ds.timTheoMaNV(nv.getMaNV(), thang, nam);
            if (bl != null) tongLuong += bl.getLuongThucNhan();
        }
        System.out.printf("Tong luong thuc nhan phong: %,.0f VND%n", tongLuong);
    }

    public void thongKeNhanSu(QuanLyNhanSu ql, DanhSachBangLuong dsBangLuong) {
        System.out.println("\n----- THONG KE NHAN SU -----");
        System.out.println("Tong so nhan vien: " + ql.getSoLuongNV());
        ql.thongKeTheoLoaiNv();
        ql.thongKeTheoTrangThai();
        ql.thongKeLuong();
        thongKeTheoPhongBan(ql);
        System.out.println("So bang luong da lap: " + dsBangLuong.getSoLuong());
    }

    public void xuatBaoCao() {
        System.out.println("\n=======================================================");
        System.out.println("         BAO CAO TONG QUAN HE THONG NHAN SU");
        System.out.println("              Ngay lap: " + ngayLap);
        System.out.println("=======================================================");
        thongKeBaoHiem();
    }
}
