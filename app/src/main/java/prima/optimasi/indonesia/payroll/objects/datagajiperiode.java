package prima.optimasi.indonesia.payroll.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class datagajiperiode implements Comparable<datagajiperiode> {
    String nama,jabatan,keterangan,imageurl,datatgl,totalgaji,gajibersih,potongan, tanggalgajian;
    Boolean issection;
    String status;

    public String getTanggalgajian() {
        return tanggalgajian;
    }

    public void setTanggalgajian(String tanggalgajian) {
        this.tanggalgajian = tanggalgajian;
    }

    public String getGajibersih() {
        return gajibersih;
    }

    public void setGajibersih(String gajibersih) {
        this.gajibersih = gajibersih;
    }

    public String getPotongan() {
        return potongan;
    }

    public void setPotongan(String potongan) {
        this.potongan = potongan;
    }

    public String getTotalgaji() {
        return totalgaji;
    }

    public void setTotalgaji(String totalgaji) {
        this.totalgaji = totalgaji;
    }

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


    public String getImageurl() {
        return imageurl;
    }


    public String getKeterangan() {
        return keterangan;
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

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setDatatgl(String datatgl) {
        this.datatgl = datatgl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(datagajiperiode o) {
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
