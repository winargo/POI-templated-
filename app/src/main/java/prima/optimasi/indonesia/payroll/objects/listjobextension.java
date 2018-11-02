package prima.optimasi.indonesia.payroll.objects;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class listjobextension {
    String namakaryawan,keterangan,profilepicture,tanggal1,tanggal2,jabatan,tipe,tgldiajukan,iddata;

    List<String> passedparameters;

    Boolean isselected = false;

    public String getTgldiajukan() {
        return tgldiajukan;
    }

    public String getTipe() {
        return tipe;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNamakaryawan() {
        return namakaryawan;
    }

    public String getProfilepicture() {
        return profilepicture;

    }

    public List<String> getPassedparameters() {
        return passedparameters;
    }


    public String getTanggal1() {
        return tanggal1;
    }

    public String getTanggal2() {
        return tanggal2;
    }

    public Boolean getIsselected() {
        return isselected;
    }

    public String getIddata() {
        return iddata;
    }

    public void setPassedparameters(List<String> passedparameters) {
        this.passedparameters = passedparameters;
    }

    public void setTgldiajukan(String tgldiajukan) {
        this.tgldiajukan = tgldiajukan;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setNamakaryawan(String namakaryawan) {
        this.namakaryawan = namakaryawan;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public void setTanggal1(String tanggal1) {
        this.tanggal1 = tanggal1;
    }

    public void setTanggal2(String tanggal2) {
        this.tanggal2 = tanggal2;
    }

    public void setIsselected(Boolean isselected) {
        this.isselected = isselected;
    }

    public void setIddata(String iddata) {
        this.iddata = iddata;
    }
}
