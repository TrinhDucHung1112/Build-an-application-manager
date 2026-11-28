public abstract class HDChuyenPhat{
    private String maVD, tenNguoiGui, tenNguoiNhan, diaChiNhan;
    private int soKg;

    //set
    public void setmaVD(String m){
        if(m.isBlank() || m == null){
            throw new IllegalArgumentException("Không được để trống maVD");
        }
        this.maVD = m;
    }
    public void settenNguoiGui(String m){
        if(m.isBlank() || m == null){
            throw new IllegalArgumentException("Không được để trống tenNguoiGui");
        }
        this.tenNguoiGui = m;
    }
    public void settenNguoiNhan(String m){
        if(m.isBlank() || m == null){
            throw new IllegalArgumentException("Không được để trống tenNguoiNhan");
        }
        this.tenNguoiNhan = m;
    }
    public void setdiaChiNhan(String m){
        if(m.isBlank() || m == null){
            throw new IllegalArgumentException("Không được để trống diaChiNhan");
        }
        this.tenNguoiNhan = m;
    }
    public void setsoKg(int m){
        if(m < 0){
            throw new IllegalArgumentException("Không được để soKg am");
        }
        this.soKg = m;
    }

    //constructor
    public void HDChuyenPhat(){
        this.maVD = "0000";
        this.tenNguoiGui = "NguyenVanA";
        this.maVD = "0000";
        this.maVD = "0000";
        this.maVD = "0000";
    }

    public void HDChuyenPhat(String maVD, String tenNguoiGui, String tenNguoiNhan, String diaChiNhan, int soKg){
        setmaVD(maVD);
        settenNguoiNhan(tenNguoiNhan);
        settenNguoiGui(tenNguoiGui);
        setdiaChiNhan(diaChiNhan);
        setsoKg(soKg);
    }

    //get
    public String gettenNguoiGui(){
        return this.tenNguoiGui;
    }
    public String gettenNguoiNhan(){
        return this.tenNguoiNhan;
    }
    public String getmaVD(){
        return this.maVD;
    }
    public String getdiaChiNhan(){
        return this.diaChiNhan;
    }
    public int getsoKg(){
        return this.soKg;
    }
}

public class HDThuong extends HoaDon{
    private int soLanGui;

    //set
    public void setsoLanGui(int m){
        if(m < 0){
            throw new IllegalArgumentException("Không được để soLanGui am");
        }
        this.soLanGui = m;
    }

    //constructor
    public void HoaDon(String maVD, String tenNguoiGui, String tenNguoiNhan, String diaChiNhan, int soKg, int soLanGui){
        super(maVD, tenNguoiGui, tenNguoiNhan, diaChiNhan, soKg);
        setsoLanGui(soLanGui);
    }   

    //get
    public int getsoLanGui(){
        return this.soLanGui;
    }

    public long tienCuoc(){
        if(soLanGui < 3) return this.getsoKg() * 8000;
        else return this.getsoKg() * 5000;
    }
}



public class all1 {
    public static void main(String[] args) {
        
    }
}
