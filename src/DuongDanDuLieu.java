import java.io.File;

/**
 * Noi duy nhat quyet dinh cac file du lieu nam o dau.
 * Thu muc "data" nam canh thu muc "src" (Build-an-application-manager/data)
 * va duoc tu tao neu chua co, nen chay "java Main" tu src hay tu thu muc goc deu dung.
 */
public final class DuongDanDuLieu {
    private static final String TEN_THU_MUC = "data";

    private DuongDanDuLieu() {}

    /** Tra ve duong dan day du cua file trong thu muc data, tao thu muc neu can. */
    public static String file(String tenFile) {
        File thuMuc = xacDinhThuMuc();
        if (!thuMuc.exists() && !thuMuc.mkdirs()) {
            System.out.println("Khong tao duoc thu muc " + thuMuc.getAbsolutePath());
        }
        return new File(thuMuc, tenFile).getPath();
    }

    private static File xacDinhThuMuc() {
        File cwd = new File("").getAbsoluteFile();
        // Dang chay trong src -> dung ../data
        if (cwd.getName().equals("src")) {
            return new File(cwd.getParentFile(), TEN_THU_MUC);
        }
        // Dang chay tu thu muc ngoai (VD: mo VS Code o thu muc OOP(PRJ) roi bam F5) -> tim data ben trong du an
        File trongDuAn = new File(new File(cwd, "Build-an-application-manager"), TEN_THU_MUC);
        if (!new File(cwd, TEN_THU_MUC).exists() && trongDuAn.exists()) {
            return trongDuAn;
        }
        return new File(cwd, TEN_THU_MUC);
    }
}