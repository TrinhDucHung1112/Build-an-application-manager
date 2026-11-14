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
        GiaoDien.tieuMuc("THONG KE BAO HIEM");
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        dong.add(new String[]{"Tong so ho so", String.valueOf(dsBaoHiem.getSoLuong())});
        dong.add(new String[]{"So ho so BHXH", String.valueOf(soBHXH)});
        dong.add(new String[]{"So ho so BHYT", String.valueOf(soBHYT)});
        dong.add(new String[]{"So the BHYT het han", String.valueOf(soTheHetHan)});
        GiaoDien.inThongKe(dong);
    }

    public void thongKeTheoPhongBan(QuanLyNhanSu ql) {
        Map<String, Integer> dem = new LinkedHashMap<>();
        for (NhanVien nv : ql.getDanhSachNV()) {
            String ten = nv.getPhongBan() == null ? "Chua phan phong" : nv.getPhongBan().getTenPB();
            dem.put(ten, dem.getOrDefault(ten, 0) + 1);
        }
        GiaoDien.tieuMuc("NHAN SU THEO PHONG BAN");
        if (dem.isEmpty()) {
            GiaoDien.in("Chua co nhan vien.");
            return;
        }
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        for (Map.Entry<String, Integer> e : dem.entrySet())
            dong.add(new String[]{e.getKey(), String.valueOf(e.getValue())});
        GiaoDien.inBang(new String[]{"PHONG BAN", "SO NHAN VIEN"}, new int[]{28, 14},
                new String[]{GiaoDien.TRAI, GiaoDien.PHAI}, dong);
    }

    public void thongKeLuong(DanhSachBangLuong ds, int thang, int nam) {
        GiaoDien.tieuMuc("THONG KE LUONG " + thang + "/" + nam);
        BangLuong cao = ds.luongCaoNhat(thang, nam);
        BangLuong thap = ds.luongThapNhat(thang, nam);
        if (cao == null) {
            GiaoDien.in("Chua co bang luong trong thang.");
            return;
        }
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        dong.add(new String[]{"Tong quy luong", String.format("%,.0f VND", ds.tongQuyLuong(thang, nam))});
        dong.add(new String[]{"Luong trung binh", String.format("%,.0f VND", ds.luongTrungBinh(thang, nam))});
        dong.add(new String[]{"Luong cao nhat", String.format("%s - %,.0f VND", cao.getMaNV(), cao.getLuongThucNhan())});
        dong.add(new String[]{"Luong thap nhat", String.format("%s - %,.0f VND", thap.getMaNV(), thap.getLuongThucNhan())});
        GiaoDien.inThongKe(dong);
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
        GiaoDien.tieuMuc("TRANG THAI NHAN SU");
        java.util.List<String[]> dong = new java.util.ArrayList<>();
        dong.add(new String[]{"Dang lam", String.valueOf(dangLam)});
        dong.add(new String[]{"Tam nghi", String.valueOf(tamNghi)});
        dong.add(new String[]{"Nghi / khac", String.valueOf(nghi)});
        GiaoDien.inBang(new String[]{"TRANG THAI", "SO LUONG"}, new int[]{28, 10},
                new String[]{GiaoDien.TRAI, GiaoDien.PHAI}, dong);
    }

    public void inBaoCaoToanCongTy(QuanLyNhanSu ql, DanhSachBangLuong ds, int thang, int nam) {
        GiaoDien.in("BAO CAO TOAN CONG TY - THANG " + thang + "/" + nam);
        GiaoDien.in("Ngay lap: " + LocalDate.now() + "   |   Tong nhan vien: " + ql.getSoLuongNV());
        thongKeTheoPhongBan(ql);
        thongKeHoatDongVaNghi(ql);
        thongKeLuong(ds, thang, nam);
        thongKeBaoHiem();
    }

    public void inBaoCaoTheoPhong(String maPB, QuanLyNhanSu ql, DanhSachBangLuong ds, int thang, int nam) {
        NhanVien[] nhanVien = ql.timKiemTheoPhongBan(maPB);
        GiaoDien.in("BAO CAO PHONG " + maPB.toUpperCase() + " - THANG " + thang + "/" + nam);
        GiaoDien.in("So nhan vien: " + nhanVien.length);
        if (nhanVien.length > 0) QuanLyNhanSu.inBang(nhanVien);
        double tongLuong = 0;
        for (NhanVien nv : nhanVien) {
            BangLuong bl = ds.timTheoMaNV(nv.getMaNV(), thang, nam);
            if (bl != null) tongLuong += bl.getLuongThucNhan();
        }
        GiaoDien.in(String.format("Tong luong thuc nhan phong: %,.0f VND", tongLuong));
    }

    public void thongKeNhanSu(QuanLyNhanSu ql, DanhSachBangLuong dsBangLuong) {
        GiaoDien.in("THONG KE NHAN SU");
        GiaoDien.tieuMuc("THEO LOAI NHAN VIEN");
        ql.thongKeTheoLoaiNv();
        GiaoDien.tieuMuc("THEO TRANG THAI");
        ql.thongKeTheoTrangThai();
        GiaoDien.tieuMuc("LUONG TOAN CONG TY");
        ql.thongKeLuong();
        thongKeTheoPhongBan(ql);
        GiaoDien.in("");
        GiaoDien.in("So bang luong da lap: " + dsBangLuong.getSoLuong());
    }

    public void xuatBaoCao() {
        GiaoDien.in("BAO CAO TONG QUAN HE THONG NHAN SU - Ngay lap: " + ngayLap);
        thongKeBaoHiem();
    }
}
