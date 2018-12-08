package prima.optimasi.indonesia.payroll.objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class datagajiperiode implements Comparable<datagajiperiode> {
    String nama,jabatan,kode,keterangan,imageurl,dataproses, tanggalgajian,tanggal1,tanggal2;
    Double totalgaji,gajibersih,potongan,bpjs,tunjangan,tunlain,punishment,reward;
    Boolean issection;

    public String getDataproses() {
        return dataproses;
    }

    public String getTanggal1() {
        return tanggal1;
    }

    public String getTanggal2() {
        return tanggal2;
    }

    public String getKode() {
        return kode;
    }

    public Double getTunjangan() {
        return tunjangan;
    }

    public Double getReward() {
        return reward;
    }

    public Double getBpjs() {
        return bpjs;
    }

    public Boolean getIssection() {
        return issection;
    }

    public Double getGajibersih() {
        return gajibersih;
    }

    public Double getPotongan() {
        return potongan;
    }

    public Double getPunishment() {
        return punishment;
    }

    public Double getTotalgaji() {
        return totalgaji;
    }

    public Double getTunlain() {
        return tunlain;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggalgajian() {
        return tanggalgajian;
    }

    public void setPunishment(Double punishment) {
        this.punishment = punishment;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public void setBpjs(Double bpjs) {
        this.bpjs = bpjs;
    }

    public void setTunjangan(Double tunjangan) {
        this.tunjangan = tunjangan;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public void setDataproses(String dataproses) {
        this.dataproses = dataproses;
    }

    public void setGajibersih(Double gajibersih) {
        this.gajibersih = gajibersih;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setIssection(Boolean issection) {
        this.issection = issection;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setPotongan(Double potongan) {
        this.potongan = potongan;
    }

    public void setTanggalgajian(String tanggalgajian) {
        this.tanggalgajian = tanggalgajian;
    }

    public void setTotalgaji(Double totalgaji) {
        this.totalgaji = totalgaji;
    }

    public void setTunlain(Double tunlain) {
        this.tunlain = tunlain;
    }

    public void setTanggal1(String tanggal1) {
        this.tanggal1 = tanggal1;
    }

    public void setTanggal2(String tanggal2) {
        this.tanggal2 = tanggal2;
    }

    @Override
    public int compareTo(datagajiperiode o) {
        if (getTanggalgajian() == null ||o.getTanggalgajian()  == null)
            return 0;
        else {
            String string1 = getTanggalgajian();
            String string = o.getTanggalgajian();
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
