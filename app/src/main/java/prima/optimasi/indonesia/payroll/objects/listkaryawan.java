package prima.optimasi.indonesia.payroll.objects;

import java.util.List;

public class listkaryawan {
    String jabatan;
    List<karyawan> daftar;

    public String getJabatan() {
        return jabatan;
    }

    public List<karyawan> getDaftar() {
        return daftar;
    }

    public void setDaftar(List<karyawan> daftar) {
        this.daftar = daftar;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }
}
