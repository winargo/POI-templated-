package prima.optimasi.indonesia.payroll.objects;

public class company {
    String companyid,companyname,companyip,companycodename;
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

    public void setCompanycodename(String companycodename) {
        this.companycodename = companycodename;
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

    public String getCompanycodename() {
        return companycodename;
    }

    public int getCompanyport() {
        return companyport;
    }
}
