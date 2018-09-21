package prima.optimasi.indonesia.payroll.objects;

import java.util.Date;

public class pengumuman {
    String title , created ,imagelink,content;
    Date createdate;
    String tipe;
    String idpengumuman;

    public pengumuman(String title,String created,Date cd,String imagelink,String content){
        this.title = title;
        this.created = created;
        this.createdate = cd;
        this.imagelink = imagelink;
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIdpengumuman(String idpengumuman) {
        this.idpengumuman = idpengumuman;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public String getContent() {
        return content;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getIdpengumuman() {
        return idpengumuman;
    }

    public String getTipe() {
        return tipe;
    }

    public Date getCreatedate() {
        return createdate;
    }


    public String getImagelink() {
        return imagelink;
    }

    public String getCreated() {
        return created;
    }

    public String getTitle() {
        return title;
    }
}
