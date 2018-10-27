package prima.optimasi.indonesia.payroll.objects;

import java.util.ArrayList;
import java.util.List;

public class logabsensi_karyawan {
    String tanggal;
    String checkin;
    String checkout;
    String breakout;
    String breakin;
    public boolean expanded = false, checkposss=false, checkins=false,checkouts=false,breakins=false,breakouts=false;;

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public String getBreakin() {
        return breakin;
    }

    public void setBreakin(String breakin) {
        this.breakin = breakin;
    }

    public String getBreakout() {
        return breakout;
    }

    public void setBreakout(String breakout) {
        this.breakout = breakout;
    }

    public Boolean getCheckposss() {
        return checkposss;
    }

    public void setCheckposss(boolean checkposss) {
        this.checkposss = checkposss;
    }

    public Boolean getBreakins() {
        return breakins;
    }

    public Boolean getBreakouts() {
        return breakouts;
    }

    public Boolean getCheckins() {
        return checkins;
    }

    public Boolean getCheckouts() {
        return checkouts;
    }

    public void setBreakins(boolean breakins) {
        this.breakins = breakins;
    }

    public void setBreakouts(boolean breakouts) {
        this.breakouts = breakouts;
    }

    public void setCheckins(boolean checkins) {
        this.checkins = checkins;
    }

    public void setCheckouts(boolean checkouts) {
        this.checkouts = checkouts;
    }

    List<String> listtanggal=new ArrayList<>();

    public List<String> getListtanggal() {
        return listtanggal;
    }

    public void setListtanggal(List<String> listtanggal) {
        this.listtanggal = listtanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public logabsensi_karyawan(){}

    //"rows":[{"nama":"Sulastri Ningsih","id_absensi":3923,"kode":"EMP-10","tanggal":"2018-10-19T00:00:00.000Z","kabag":"EMP-10",
    // "masuk":"10:10:10","break_out":null,"break_in":null,"keluar":null,"ket_masuk":"-","ket_keluar":"","jam_masuk":null,"telat":0
    //,"durasi_break":0,"telat_break":0,"jam_keluar":null,"pulang_cepat":0,"status_kabag":0,"kerja":"","shift":"","token":0,"lupa_keluar_kabag":0}
}
