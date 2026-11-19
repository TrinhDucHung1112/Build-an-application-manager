import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 1 yeu cau gui len 1 nguoi khac de duyet (CLASS MOI).
 * Hien dung cho loai SUA_HO_SO: nhan vien / truong phong / pho phong KHONG tu sua ho so duoc,
 * ma gui yeu cau, nguoi nhan dong y thi ho so moi duoc sua.
 * (Muon them loai yeu cau khac chi can them hang LOAI_... va xu ly o noi duyet.)
 */
public class YeuCau implements Serializable {
    public static final String LOAI_SUA_HO_SO = "SUA_HO_SO";

    public static final String CHO_DUYET = "CHO_DUYET";
    public static final String DA_DUYET = "DA_DUYET";
    public static final String TU_CHOI = "TU_CHOI";
    public static final String DA_HUY = "DA_HUY";

    public static final DateTimeFormatter DINH_DANG = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String maYC;
    private final String nguoiGui;
    private final String nguoiNhan;
    private final String loai;
    private final int nam;              //nam cua ho so can sua
    private final String truong;        //muc can sua (HO_TEN, SDT, EMAIL, PHONG_BAN, CHUC_VU, LUONG_CO_BAN...)
    private final String giaTriCu;
    private final String giaTriMoi;
    private final LocalDateTime ngayGui;
    private String trangThai;
    private LocalDateTime ngayXuLy;
    private String ghiChuXuLy;

    public YeuCau(String maYC, String nguoiGui, String nguoiNhan, String loai, int nam, String truong,
                  String giaTriCu, String giaTriMoi, LocalDateTime ngayGui,
                  String trangThai, LocalDateTime ngayXuLy, String ghiChuXuLy) {
        if (maYC == null || maYC.isBlank()) throw new IllegalArgumentException("Ma yeu cau khong duoc de trong");
        if (nguoiGui == null || nguoiGui.isBlank() || nguoiNhan == null || nguoiNhan.isBlank())
            throw new IllegalArgumentException("Thieu nguoi gui hoac nguoi nhan");
        if (nguoiGui.equalsIgnoreCase(nguoiNhan)) throw new IllegalArgumentException("Khong the gui yeu cau cho chinh minh");
        this.maYC = maYC.trim().toUpperCase();
        this.nguoiGui = nguoiGui.trim().toUpperCase();
        this.nguoiNhan = nguoiNhan.trim().toUpperCase();
        this.loai = loai;
        this.nam = nam;
        this.truong = truong;
        this.giaTriCu = giaTriCu == null ? "" : giaTriCu;
        this.giaTriMoi = giaTriMoi == null ? "" : giaTriMoi;
        this.ngayGui = ngayGui;
        this.trangThai = trangThai == null ? CHO_DUYET : trangThai;
        this.ngayXuLy = ngayXuLy;
        this.ghiChuXuLy = ghiChuXuLy == null ? "" : ghiChuXuLy;
    }

    //yeu cau moi (cho duyet)
    public static YeuCau moi(String maYC, String nguoiGui, String nguoiNhan, int nam, String truong,
                             String giaTriCu, String giaTriMoi) {
        return new YeuCau(maYC, nguoiGui, nguoiNhan, LOAI_SUA_HO_SO, nam, truong, giaTriCu, giaTriMoi,
                LocalDateTime.now().withSecond(0).withNano(0), CHO_DUYET, null, "");
    }

    public String getMaYC() { return maYC; }
    public String getNguoiGui() { return nguoiGui; }
    public String getNguoiNhan() { return nguoiNhan; }
    public String getLoai() { return loai; }
    public int getNam() { return nam; }
    public String getTruong() { return truong; }
    public String getGiaTriCu() { return giaTriCu; }
    public String getGiaTriMoi() { return giaTriMoi; }
    public LocalDateTime getNgayGui() { return ngayGui; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayXuLy() { return ngayXuLy; }
    public String getGhiChuXuLy() { return ghiChuXuLy; }

    public boolean dangCho() { return CHO_DUYET.equals(trangThai); }

    public void duyet() { xuLy(DA_DUYET, ""); }
    public void tuChoi(String lyDo) { xuLy(TU_CHOI, lyDo); }
    public void huy() { xuLy(DA_HUY, ""); }

    private void xuLy(String trangThaiMoi, String ghiChu) {
        if (!dangCho()) throw new IllegalStateException("Yeu cau nay da duoc xu ly truoc do (" + trangThai + ")");
        this.trangThai = trangThaiMoi;
        this.ngayXuLy = LocalDateTime.now().withSecond(0).withNano(0);
        this.ghiChuXuLy = ghiChu == null ? "" : ghiChu;
    }

    public String tenTrangThai() {
        switch (trangThai) {
            case CHO_DUYET: return "Cho duyet";
            case DA_DUYET: return "Da duyet";
            case TU_CHOI: return "Bi tu choi";
            case DA_HUY: return "Da huy";
            default: return trangThai;
        }
    }

    public static String tenTruong(String truong) {
        switch (truong) {
            case "HO_TEN": return "Ho ten";
            case "NGAY_SINH": return "Ngay sinh";
            case "GIOI_TINH": return "Gioi tinh";
            case "SDT": return "So dien thoai";
            case "EMAIL": return "Email";
            case "NGAY_VAO_LAM": return "Ngay vao lam";
            case "PHONG_BAN": return "Phong ban";
            case "CHUC_VU": return "Chuc vu";
            case "LUONG_CO_BAN": return "Luong co ban";
            default: return truong;
        }
    }
}
