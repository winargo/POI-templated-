package prima.optimasi.indonesia.payroll.objects;

import android.graphics.drawable.Drawable;

public class listjob {
    String sectionname;
    Boolean section;
    String detailsection;

    public Boolean getSection() {
        return section;
    }

    public String getDetailsection() {
        return detailsection;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setDetailsection(String detailsection) {
        this.detailsection = detailsection;
    }

    public void setSection(Boolean section) {
        this.section = section;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }
}
