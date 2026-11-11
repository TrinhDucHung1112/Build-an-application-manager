import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

public class DanhSachThuongPhat implements IQuanLyDuLieu {
//----thuoc tinh----
    public static final String LOAI_NGHI_KHONG_PHEP = "Khong phep";    //phai trung loaiNghi cua NghiPhep

    private ThuongPhat[] danhSach;
    private int soLuong;

//----constructor----
    public DanhSachThuongPhat(int sucChua) {
        this.danhSach = new ThuongPhat[sucChua];
        this.soLuong = 0;
    }

//----methods----
//---them, xoa, tim---
    public boolean them(ThuongPhat tp) {
        if (tp == null) return false;
        if (soLuong >= danhSach.length) danhSach = Arrays.copyOf(danhSach, danhSach.length * 2);
        if (timTheoMa(tp.getMaTP()) != null) return false;             //trung ma
        danhSach[soLuong++] = tp;
        return true;
    }

    public ThuongPhat timTheoMa(String maTP) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaTP().equals(maTP)) return danhSach[i];
        }
        return null;
    }

    public boolean xoaTheoMa(String maTP) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaTP().equals(maTP)) {
                for (int j = i; j < soLuong - 1; j++) danhSach[j] = danhSach[j + 1];
                danhSach[--soLuong] = null;
                return true;
            }
        }
        return false;
    }

    public ThuongPhat[] timTheoMaNV(String maNV) {
        ThuongPhat[] tam = new ThuongPhat[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNV().equals(maNV)) tam[dem++] = danhSach[i];
        }
        return Arrays.copyOf(tam, dem);
    }

//---tong thuong, tong phat---
    //tong tien theo loai cua 1 nhan vien trong thang
    private double tong(String maNV, int thang, int nam, String loai) {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) {
            ThuongPhat tp = danhSach[i];
            if (tp.getMaNV().equals(maNV) && tp.getLoai().equalsIgnoreCase(loai)
                    && tp.getNgayApDung().getMonthValue() == thang
                    && tp.getNgayApDung().getYear() == nam) {
                tong += tp.tinhSoTien();
            }
        }
        return tong;
    }

    public double tongThuong(String maNV, int thang, int nam) {
        return tong(maNV, thang, nam, QuyDinhThuongPhat.THUONG);
    }

    public double tongPhat(String maNV, int thang, int nam) {
        return tong(maNV, thang, nam, QuyDinhThuongPhat.PHAT);
    }

//---tu dong xet---
    //lay gia tri thuc te cua chi tieu cho 1 nhan vien trong thang
    private double layGiaTri(String chiTieu, String maNV, int thang, int nam,
                             DanhSachChamCong dsCC, NghiPhep[] dsNP) {
        if (QuyDinhThuongPhat.GIO_TANG_CA.equals(chiTieu)) {
            return dsCC.thongKeGioTangCa(maNV, thang, nam);
        }
        if (QuyDinhThuongPhat.NGAY_DI_LAM.equals(chiTieu)) {
            return dsCC.thongKeSoNgayDiLam(maNV, thang, nam);
        }
        if (QuyDinhThuongPhat.NGAY_NGHI_KHONG_PHEP.equals(chiTieu)) {
            double tong = 0;
            for (NghiPhep np : dsNP) {
                if (np == null) continue;
                if (np.getMaNV().equalsIgnoreCase(maNV)
                        && LOAI_NGHI_KHONG_PHEP.equalsIgnoreCase(np.getLoaiNghi())) {
                    java.time.LocalDate dauThang = YearMonth.of(nam, thang).atDay(1);
                    java.time.LocalDate cuoiThang = YearMonth.of(nam, thang).atEndOfMonth();
                    java.time.LocalDate batDau = np.getNgayBatDau().isAfter(dauThang)
                            ? np.getNgayBatDau() : dauThang;
                    java.time.LocalDate ketThuc = np.getNgayKetThuc().isBefore(cuoiThang)
                            ? np.getNgayKetThuc() : cuoiThang;
                    if (!ketThuc.isBefore(batDau))
                        tong += java.time.temporal.ChronoUnit.DAYS.between(batDau, ketThuc) + 1;
                }
            }
            return tong;
        }
        return 0;
    }

    //xet thuong/phat theo quy dinh, chay lai nhieu lan khong bi trung
    public void tuDongXetThuongPhat(int thang, int nam, NhanVien[] dsNV, QuyDinhThuongPhat[] dsQD,
                                    DanhSachChamCong dsCC, NghiPhep[] dsNP) {
        LocalDate ngay = YearMonth.of(nam, thang).atEndOfMonth();
        for (NhanVien nv : dsNV) {
            if (nv == null) continue;
            String maNV = nv.getMaNV();
            for (QuyDinhThuongPhat qd : dsQD) {
                if (qd == null) continue;
                double giaTri = layGiaTri(qd.getDonVi(), maNV, thang, nam, dsCC, dsNP);
                if (!qd.kiemTraDieuKien(giaTri)) continue;
                String maTP = "TP_" + maNV + "_" + qd.getMaQD() + "_" + thang + "_" + nam;
                if (timTheoMa(maTP) != null) continue;                 //da xet roi
                them(new ThuongPhat(maTP, maNV, qd, giaTri, ngay));
            }
        }
    }

//---hienthi---
    public void xuatDanhSach() {
        for (int i = 0; i < soLuong; i++) danhSach[i].hienThiThongTin();
    }

    private static final String FILE_TEXT = DuongDanDuLieu.file("thuongphat.txt");

    // Dinh dang moi dong: maTP|maNV|maQD|loai|lyDo|soLuong|soTien|ngayApDung
    @Override
    public void ghiFile() {
        java.util.List<String> dong = new java.util.ArrayList<>();
        for (int i = 0; i < soLuong; i++) {
            ThuongPhat tp = danhSach[i];
            dong.add(TepVanBan.ghep(tp.getMaTP(), tp.getMaNV(), tp.getMaQD(), tp.getLoai(), tp.getLyDo(),
                    tp.getSoLuong(), tp.getSoTien(), tp.getNgayApDung()));
        }
        try {
            TepVanBan.ghiDong(FILE_TEXT, "maTP|maNV|maQD|loai|lyDo|soLuong|soTien|ngayApDung(yyyy-MM-dd)", dong);
        } catch (java.io.IOException e) {
            System.out.println("Loi luu thuong phat: " + e.getMessage());
        }
    }

    @Override
    public void docFile() {
        if (!new java.io.File(FILE_TEXT).exists()) return;
        try {
            java.util.List<String[]> cacDong = TepVanBan.docDong(FILE_TEXT);
            danhSach = new ThuongPhat[Math.max(10, cacDong.size())];
            soLuong = 0;
            for (String[] p : cacDong) {
                if (p.length < 8) continue;
                try {
                    them(new ThuongPhat(p[0], p[1], p[2], p[3], p[4], Double.parseDouble(p[5]),
                            Double.parseDouble(p[6]), java.time.LocalDate.parse(p[7])));
                } catch (RuntimeException e) {
                    System.out.println("Bo qua dong thuong phat loi: " + String.join("|", p));
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Loi doc thuong phat: " + e.getMessage());
        }
    }


    //---getter---
    public int getSoLuong() { return soLuong; }
}
