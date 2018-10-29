package prima.optimasi.indonesia.payroll.objects;

public class listkaryawankontrakkerja {
    String jabatan;
    String kontrakkerja;
    String sisa;
    String iskar;
    String kode;
    boolean section = false;
    String nama,desc;
    String imagelink;
    public boolean expanded = false;

    public String getSisa() {
        return sisa;
    }

    public void setSisa(String sisa) {
        this.sisa = sisa;
    }

    public String getKontrakkerja() {
        return kontrakkerja;
    }

    public void setKontrakkerja(String kontrakkerja) {
        this.kontrakkerja = kontrakkerja;
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

    public listkaryawankontrakkerja(){}

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
