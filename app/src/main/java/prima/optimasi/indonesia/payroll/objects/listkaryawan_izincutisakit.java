package prima.optimasi.indonesia.payroll.objects;

public class listkaryawan_izincutisakit {
    String jabatan;
    String izin;
    String absen, cuti, dinas, telat;
    String sakit;
    String bulan;
    String iskar;
    String kode;
    boolean section = false;
    String nama,desc;
    String imagelink;

    public String getTelat() {
        return telat;
    }

    public void setTelat(String telat) {
        this.telat = telat;
    }

    public String getAbsen() {
        return absen;
    }

    public void setAbsen(String absen) {
        this.absen = absen;
    }

    public String getDinas() {
        return dinas;
    }

    public void setDinas(String dinas) {
        this.dinas = dinas;
    }

    public String getCuti() {
        return cuti;
    }

    public void setCuti(String cuti) {
        this.cuti = cuti;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getIzin() {
        return izin;
    }

    public void setIzin(String izin) {
        this.izin = izin;
    }

    public String getSakit() {
        return sakit;
    }

    public void setSakit(String sakit) {
        this.sakit = sakit;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public void setIskar(String iskar) {
        this.iskar = iskar;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public listkaryawan_izincutisakit(){}

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIskar() {
        return iskar;
    }


    public String getDesc() {
        return desc;
    }

    public String getNama() {
        return nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getImagelink() {
        return imagelink;
    }

    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }
}
