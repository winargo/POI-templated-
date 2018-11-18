package prima.optimasi.indonesia.payroll.objects;

public class company {
    String companyid,companyname,companyip;
    int companyport;

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public void setCompanyip(String companyip) {
        this.companyip = companyip;
    }

    public void setCompanyport(int companyport) {
        this.companyport = companyport;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompanyid() {
        return companyid;
    }

    public String getCompanyip() {
        return companyip;
    }

    public String getCompanyname() {
        return companyname;
    }

    public int getCompanyport() {
        return companyport;
    }
}
