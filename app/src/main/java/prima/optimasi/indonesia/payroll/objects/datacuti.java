package prima.optimasi.indonesia.payroll.objects;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class datacuti implements Comparable<datacuti> {
    String nama,jabatan,tglmulai,tglakhir,keterangan,hari,imageurl,datatgl;
    Boolean issection;
    String status;

    public String getDatatgl() {
        return datatgl;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getIssection() {
        return issection;
    }

    public String getNama() {
        return nama;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getHari() {
        return hari;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getTglakhir() {
        return tglakhir;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTglmulai() {
        return tglmulai;
    }

    public void setIssection(Boolean issection) {
        this.issection = issection;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setTglakhir(String tglakhir) {
        this.tglakhir = tglakhir;
    }

    public void setTglmulai(String tglmulai) {
        this.tglmulai = tglmulai;
    }

    public void setDatatgl(String datatgl) {
        this.datatgl = datatgl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(datacuti o) {
        if (getDatatgl() == null ||o.getDatatgl()  == null)
            return 0;
        else {
            String string1 = getDatatgl();
            String string = o.getDatatgl();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = null;
            Date date1 = null;
            try {
                date = format.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                date1 = format.parse(string1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date1.compareTo(date);
        }
    }

}
