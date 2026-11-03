import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static DanhSachBaoHiem dsBaoHiem = new DanhSachBaoHiem();
    private static BaoCaoNhanSu baoCao = new BaoCaoNhanSu(dsBaoHiem);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        napDuLieuMau();
        int chon;
        do {
            System.out.println("\n========== HỆ THỐNG QUẢN LÝ NHÂN SỰ (module Bảo hiểm) ==========");
            System.out.println("1. Xem danh sách bảo hiểm");
            System.out.println("2. Thêm mới Bảo hiểm Xã hội (BHXH)");
            System.out.println("3. Thêm mới Bảo hiểm Y tế (BHYT)");
            System.out.println("4. Tìm bảo hiểm theo Mã NV");
            System.out.println("5. Xóa hồ sơ bảo hiểm theo Mã BH");
            System.out.println("6. Tính tiền bảo hiểm của 1 nhân viên");
            System.out.println("7. Xuất báo cáo nhân sự tổng hợp");
            System.out.println("0. Thoát chương trình");
            System.out.print("Mời bạn chọn chức năng (0-7): ");

            try {
                chon = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                chon = -1;
            }

            switch (chon) {
                case 1:
                    dsBaoHiem.xuatDanhSach();
                    break;
                case 2:
                    themBHXH();
                    break;
                case 3:
                    themBHYT();
                    break;
                case 4:
                    timTheoNhanVien();
                    break;
                case 5:
                    xoaBaoHiem();
                    break;
                case 6:
                    tinhTienChoNhanVien();
                    break;
                case 7:
                    baoCao.xuatBaoCao();
                    break;
                case 0:
                    System.out.println("Đã đóng chương trình.");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập lại!");
            }
        } while (chon != 0);
    }

    // Đọc 1 số thực, nhập sai thì hỏi lại, không làm chương trình bị sập
    private static double docSo(String loiNhac) {
        while (true) {
            System.out.print(loiNhac);
            try {
                double so = Double.parseDouble(scanner.nextLine().trim());
                if (so < 0) {
                    System.out.println("Giá trị không được âm, nhập lại!");
                } else {
                    return so;
                }
            } catch (Exception e) {
                System.out.println("Vui lòng nhập một số hợp lệ!");
            }
        }
    }

    // Đọc ngày dạng yyyy-MM-dd, nhập sai thì hỏi lại
    private static LocalDate docNgay(String loiNhac) {
        while (true) {
            System.out.print(loiNhac);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Ngày không hợp lệ, nhập theo dạng yyyy-MM-dd (ví dụ 2027-12-31)!");
            }
        }
    }

    private static void napDuLieuMau() {
        dsBaoHiem.them(new BaoHiemXaHoi("BH01", "NV001", "SH0001", LocalDate.of(2025, 1, 1)));
        dsBaoHiem.them(new BaoHiemYTe("BH02", "NV001", "DN4791234", LocalDate.of(2025, 1, 1),
                "BV Gia Dinh", LocalDate.of(2027, 12, 31)));
        dsBaoHiem.them(new BaoHiemXaHoi("BH03", "NV002", "SH0002", LocalDate.of(2025, 6, 1)));
    }

    private static void themBHXH() {
        System.out.print("Nhập mã BH: ");
        String maBH = scanner.nextLine().trim();
        System.out.print("Nhập mã NV: ");
        String maNV = scanner.nextLine().trim();
        System.out.print("Nhập số sổ BHXH: ");
        String soSo = scanner.nextLine().trim();

        BaoHiem bh = new BaoHiemXaHoi(maBH, maNV, soSo, LocalDate.now());
        if (dsBaoHiem.them(bh)) {
            System.out.println("Thêm BHXH thành công!");
        }
    }

    private static void themBHYT() {
        System.out.print("Nhập mã BH: ");
        String maBH = scanner.nextLine().trim();
        System.out.print("Nhập mã NV: ");
        String maNV = scanner.nextLine().trim();
        System.out.print("Nhập số thẻ BHYT: ");
        String soThe = scanner.nextLine().trim();
        System.out.print("Nơi khám chữa bệnh: ");
        String noiKCB = scanner.nextLine().trim();
        LocalDate hanThe = docNgay("Hạn thẻ đến ngày (yyyy-MM-dd): ");

        BaoHiem bh = new BaoHiemYTe(maBH, maNV, soThe, LocalDate.now(), noiKCB, hanThe);
        if (dsBaoHiem.them(bh)) {
            System.out.println("Thêm BHYT thành công!");
        }
    }

    private static void timTheoNhanVien() {
        System.out.print("Nhập mã nhân viên cần tra cứu: ");
        String maNV = scanner.nextLine().trim();
        BaoHiem[] kq = dsBaoHiem.timTheoMaNV(maNV);
        if (kq.length == 0) {
            System.out.println("Không tìm thấy dữ liệu bảo hiểm của nhân viên này.");
        } else {
            System.out.println("Tìm thấy " + kq.length + " bản ghi:");
            DanhSachBaoHiem.inTieuDeBang();
            for (BaoHiem bh : kq) {
                bh.hienThiThongTin();
            }
        }
    }

    private static void xoaBaoHiem() {
        System.out.print("Nhập mã bảo hiểm muốn xóa: ");
        String maBH = scanner.nextLine().trim();
        if (dsBaoHiem.xoaTheoMa(maBH)) {
            System.out.println("Xóa thành công!");
        } else {
            System.out.println("Không tìm thấy mã bảo hiểm cần xóa.");
        }
    }

    private static void tinhTienChoNhanVien() {
        System.out.print("Nhập mã nhân viên: ");
        String maNV = scanner.nextLine().trim();
        double luong = docSo("Nhập mức lương đóng bảo hiểm: ");
        double nvDong = dsBaoHiem.tongTienNhanVienDong(maNV, luong);
        double dnDong = dsBaoHiem.tongTienDoanhNghiepDong(maNV, luong);
        System.out.printf("Nhân viên đóng   : %,.0f VND%n", nvDong);
        System.out.printf("Doanh nghiệp đóng: %,.0f VND%n", dnDong);
    }
}
