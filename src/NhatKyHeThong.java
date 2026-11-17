import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 1 dong nhat ky he thong: ai, luc nao, lam gi, voi ai, ket qua (CLASS MOI - phan quyen).
 */
public class NhatKyHeThong implements Serializable {
    public static final String THANH_CONG = "THANH CONG";
    public static final String TU_CHOI = "TU CHOI";         //bi chan vi khong du quyen
    public static final String THAT_BAI = "THAT BAI";       //VD: sai mat khau
    public static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LocalDateTime thoiDiem;
    private String nguoiThucHien;
    private String hanhDong;
    private String doiTuong;
    private String ketQua;

    public NhatKyHeThong(LocalDateTime thoiDiem, String nguoiThucHien, String hanhDong, String doiTuong, String ketQua) {
        if (thoiDiem == null) throw new IllegalArgumentException("Thoi diem khong duoc de trong");
        this.thoiDiem = thoiDiem.withNano(0);
        this.nguoiThucHien = chuan(nguoiThucHien);
        this.hanhDong = chuan(hanhDong);
        this.doiTuong = chuan(doiTuong);
        this.ketQua = chuan(ketQua);
    }

    private static String chuan(String s) {
        return s == null || s.isBlank() ? "-" : s.trim();
    }

    public LocalDateTime getThoiDiem() { return thoiDiem; }
    public String getNguoiThucHien() { return nguoiThucHien; }
    public String getHanhDong() { return hanhDong; }
    public String getDoiTuong() { return doiTuong; }
    public String getKetQua() { return ketQua; }

    public String[] thanhDong() {
        return new String[]{thoiDiem.format(DINH_DANG), nguoiThucHien, hanhDong, doiTuong, ketQua};
    }
}
