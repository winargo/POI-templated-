package prima.optimasi.indonesia.payroll.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class listperingkatkaryawan {
    String jabatan;
    String iskar;
    String kode;
    String nama;
    String imagelink;
    int telat, absen, hadir, total;

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

    public listperingkatkaryawan(){}

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIskar() {
        return iskar;
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

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setTelat(Integer telat) {
        this.telat = telat;
    }

    public Integer getTelat() {
        return telat;
    }

    public Integer getAbsen() {
        return absen;
    }

    public void setAbsen(Integer absen) {
        this.absen = absen;
    }

    public void setHadir(Integer hadir) {
        this.hadir = hadir;
    }

    public Integer getHadir() {
        return hadir;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
