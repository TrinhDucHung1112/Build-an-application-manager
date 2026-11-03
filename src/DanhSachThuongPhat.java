import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

public class DanhSachThuongPhat {
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
        if (tp == null || soLuong >= danhSach.length) return false;
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
            return dsCC.thongKeGioTangCa(maNV, thang);
        }
        if (QuyDinhThuongPhat.NGAY_DI_LAM.equals(chiTieu)) {
            return dsCC.thongKeSoNgayDiLam(maNV, thang);
        }
        if (QuyDinhThuongPhat.NGAY_NGHI_KHONG_PHEP.equals(chiTieu)) {
            double tong = 0;
            for (NghiPhep np : dsNP) {
                if (np == null) continue;
                if (np.getMaNV().equals(maNV)
                        && LOAI_NGHI_KHONG_PHEP.equalsIgnoreCase(np.getLoaiNghi())
                        && np.getNgayBatDau().getMonthValue() == thang
                        && np.getNgayBatDau().getYear() == nam) {
                    tong += np.tinhSoNgayNghi();
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

//---getter---
    public int getSoLuong() { return soLuong; }
}
