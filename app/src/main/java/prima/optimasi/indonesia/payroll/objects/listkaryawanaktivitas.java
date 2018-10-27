package prima.optimasi.indonesia.payroll.objects;

public class listkaryawanaktivitas {
    String jabatan;
    String iskar;String nama,desc;
    String imagelink;
    String checkin;
    String checkout;
    String breakout;
    String breakin;
    String extrain;
    String extraout;
    Boolean checkins=false,checkouts=false,breakins=false,breakouts=false,extrains=false,extraouts=false,checkposss=false;

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

    public Boolean getExtrains() {
        return extrains;
    }

    public Boolean getExtraouts() {
        return extraouts;
    }

    public String getExtraout() {
        return extraout;
    }

    public String getExtrain() {
        return extrain;
    }

    public String getCheckout() {
        return checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public String getBreakout() {
        return breakout;
    }

    public String getBreakin() {
        return breakin;
    }

    public String getDesc() {
        return desc;
    }

    public String getImagelink() {
        return imagelink;
    }

    public String getIskar() {
        return iskar;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getNama() {
        return nama;
    }

    public Boolean getCheckposss() {
        return checkposss;
    }

    public void setExtraout(String extraout) {
        this.extraout = extraout;
    }

    public void setExtrain(String extrain) {
        this.extrain = extrain;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public void setBreakout(String breakout) {
        this.breakout = breakout;
    }

    public void setBreakin(String breakin) {
        this.breakin = breakin;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public void setIskar(String iskar) {
        this.iskar = iskar;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setBreakins(Boolean breakins) {
        this.breakins = breakins;
    }

    public void setBreakouts(Boolean breakouts) {
        this.breakouts = breakouts;
    }

    public void setCheckins(Boolean checkins) {
        this.checkins = checkins;
    }

    public void setCheckouts(Boolean checkouts) {
        this.checkouts = checkouts;
    }

    public void setExtrains(Boolean extrains) {
        this.extrains = extrains;
    }

    public void setExtraouts(Boolean extraouts) {
        this.extraouts = extraouts;
    }

    public void setCheckposss(Boolean checkposss) {
        this.checkposss = checkposss;
    }
}
