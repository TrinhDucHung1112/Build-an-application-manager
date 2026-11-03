import java.time.LocalDate;

public class BaoCaoNhanSu {
    private LocalDate ngayLap;
    private DanhSachBaoHiem dsBaoHiem;
    // TODO (khi có code người 2, 3, 4): thêm các thuộc tính
    // private QuanLyNhanSu ql;
    // private DanhSachBangLuong dsBangLuong;
    // private DanhSachThuongPhat dsThuongPhat;

    public BaoCaoNhanSu(DanhSachBaoHiem dsBaoHiem) {
        this.ngayLap = LocalDate.now();
        this.dsBaoHiem = dsBaoHiem;
    }

    // TODO: constructor theo diagram, bật lên khi ghép code
    // public BaoCaoNhanSu(QuanLyNhanSu ql, DanhSachBangLuong dsBangLuong,
    //                     DanhSachBaoHiem dsBaoHiem, DanhSachThuongPhat dsThuongPhat) { ... }

    public LocalDate getNgayLap() { return ngayLap; }

    public void thongKeBaoHiem() {
        int soBHXH = 0;
        int soBHYT = 0;
        int soTheHetHan = 0;
        for (int i = 0; i < dsBaoHiem.getSoLuong(); i++) {
            BaoHiem bh = dsBaoHiem.getBaoHiem(i);
            if (bh instanceof BaoHiemXaHoi) {
                soBHXH++;
            } else if (bh instanceof BaoHiemYTe) {
                soBHYT++;
                if (!((BaoHiemYTe) bh).conHanThe()) {
                    soTheHetHan++;
                }
            }
        }
        System.out.println("\n----- THỐNG KÊ BẢO HIỂM -----");
        System.out.println("Tổng số hồ sơ        : " + dsBaoHiem.getSoLuong());
        System.out.println("Số hồ sơ BHXH        : " + soBHXH);
        System.out.println("Số hồ sơ BHYT        : " + soBHYT);
        System.out.println("Số thẻ BHYT hết hạn  : " + soTheHetHan);
        dsBaoHiem.xuatDanhSach();
    }

    // TODO: các hàm còn lại theo diagram, viết khi có code người khác:
    // thongKeTheoPhongBan(), thongKeLuong(), thongKeHoatDongVaNghi(),
    // thongKeLuongCaoThapNhat(thang, nam), inBaoCaoToanCongTy(thang, nam),
    // inBaoCaoTheoPhong(maPB, thang, nam)

    public void xuatBaoCao() {
        System.out.println("\n=======================================================");
        System.out.println("         BÁO CÁO TỔNG QUAN HỆ THỐNG NHÂN SỰ");
        System.out.println("              Ngày lập: " + ngayLap);
        System.out.println("=======================================================");
        thongKeBaoHiem();
    }
}
