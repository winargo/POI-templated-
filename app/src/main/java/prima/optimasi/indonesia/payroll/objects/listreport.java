package prima.optimasi.indonesia.payroll.objects;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class listreport {
    String judul,deskripsi;
    Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getJudul() {
        return judul;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }
}
