package prima.optimasi.indonesia.payroll.objects;


import io.realm.RealmObject;

public class datagajiobject extends RealmObject {

    String dateformatted;
    float datagaji;
    float ranges;

    public datagajiobject(){

    }

    public datagajiobject(float datasinlo,float rage,String monthnum){
        dateformatted=monthnum;
        datagaji=datasinlo;
        ranges=rage;
    }

    public float getDatagaji() {
        return datagaji;
    }

    public float getRanges() {
        return ranges;
    }

    public String getDateformatted() {
        return dateformatted;
    }

    public void setDatagaji(float datagaji) {
        this.datagaji = datagaji;
    }

    public void setDateformatted(String dateformatted) {
        this.dateformatted = dateformatted;
    }

    public void setRanges(float ranges) {
        this.ranges = ranges;
    }
}