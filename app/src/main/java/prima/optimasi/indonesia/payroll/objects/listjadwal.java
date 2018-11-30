package prima.optimasi.indonesia.payroll.objects;

public class listjadwal {
    String nama, tanggal, nama_shift, masuk, keluar;
    boolean section = true;
    public listjadwal(){

    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNama_shift() {
        return nama_shift;
    }

    public void setNama_shift(String nama_shift) {
        this.nama_shift = nama_shift;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public boolean getBoolean() {
        return section;
    }

    public String getKeluar() {
        return keluar;
    }

    public void setKeluar(String keluar) {
        this.keluar = keluar;
    }

    public String getMasuk() {
        return masuk;
    }

    public void setMasuk(String masuk) {
        this.masuk = masuk;
    }

    /*
    public String getJadwal_section() {
        return jadwal_section;
    }

    public void setJadwal_section(String jadwal_section) {
        this.jadwal_section = jadwal_section;
    }*/
}
