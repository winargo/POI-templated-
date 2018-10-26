package prima.optimasi.indonesia.payroll.main_owner.manage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.adapter.AdapterListBasic;
import prima.optimasi.indonesia.payroll.adapter.AdapterListSwipe;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.data.DataGenerator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_helper.adapterapprovalhelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListBasicjob_extention;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSwipe_approval;
import prima.optimasi.indonesia.payroll.model.Social;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class approval extends AppCompatActivity implements adapterapprovalhelper.RecyclerItemTouchHelperListener {


    private CoordinatorLayout parent_view;

    private AdapterListSwipe_approval adapter;

    private RecyclerView recyclerView;



    String tipe="";

    String[] real = new String[]{"id","kode_karyawan","idfp","nama","alamat","tempat_lahir","tgl_lahir","telepon","no_wali","email","tgl_masuk","kelamin",
            "status_nikah","pendidikan","wn","agama","shift", "status_kerja","ibu_kandung","suami_istri","tanggungan","npwp", "gaji","rekening","id_bank",
            "id_departemen","id_jabatan","id_grup", "id_golongan","atas_nama","foto","id_cabang",
            "start_date","expired_date","jab_index","kontrak","file_kontrak", "otoritas","periode_gaji",
            "qrcode_file"};

    //0,1,2,39,37,36

    String[] karyawan = new String[]{"id","kode_karyawan","idfp","nama", "alamat", "tempat_lahir", "tgl_lahir", "telepon", "no_wali", "email",
            "tgl_masuk", "kelamin", "status_nikah", "pendidikan", "wn", "agama", "shift", "status_kerja", "ibu_kandung",
            "suami_istri", "tanggungan", "npwp", "gaji", "rekening", "id_bank", "id_departemen", "id_jabatan", "id_grup",
            "id_golongan", "atas_nama", "foto", "id_cabang", "start_date", "expired_date", "jab_index","kontrak","file_kontrak",
            "otoritas", "periode_gaji"};

    String[] karyawandetail = new String[]{"id","kode_karyawan","idfp","Nama", "Alamat", "Tempat Lahir", "Tanggal Lahir", "Telepon", "No Wali", "Email",
            "Tanggal Masuk", "Kelamin", "Status Nikah", "Pendidikan", "Warga Negara", "Agama", "Shift", "Status Kerja", "Ibu Kandung",
            "Suami Istri", "Tanggungan", "NPWP", "Gaji", "Rekening", "ID Bank", "ID Departemen", "ID Jabatan", "ID Grup",
            "ID Golongan", "Atas Nama", "Foto", "ID Cabang", "Tanggal Mulai Kontrak", "Tanggal Akhir Kontrak","jab_index", "Kontrak","file_kontrak",
            "Otoritas", "Periode Gajian"};

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};
    String[] urloptions = new String[]{generator.getapprovalcutiyurl,generator.getapprovaldinasyurl,generator.getapprovaldirumahkanyurl,generator.getapprovalizinyurl,generator.getapprovalgolonganyurl,generator.getapprovalkaryawanyurl,generator.getapprovalpinjamanyurl,generator.getapprovalpdlyurl,generator.getapprovalpunihsmentyurl,generator.getapprovalrewardyurl};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_approval);

        parent_view = findViewById(R.id.snakebar);

        recyclerView = findViewById(R.id.recyclerView);

        AlertDialog dialog = new AlertDialog.Builder(approval.this).setTitle("Instruction").setMessage("Slide Right to Accept and Slide Left To Deny / Ignore , to Multiple Select , Long Press Any Item").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        dialog.show();


        for (int i = 0 ; i < options.length;i++){
            if(options[i].equals(getIntent().getStringExtra("tipe"))){
                initToolbar(options[i]);
                retrive adddata = new retrive(approval.this,options[i]);
                adddata.execute();
            }
        }



        initComponent();
    }

    private void initToolbar(String Title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(adapter!=null){
            adapter.removeItem(viewHolder.getAdapterPosition());
        }

    }

    private class retrive extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = "";
        String passeddata = "" ;
        String tipe = "";
        View nothing ;
        TextView txt_nothing;
        //listjobextension =

        public retrive(Context context,String choice)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);


            this.txt_nothing = txt_nothing;
            this.nothing = nothing;

            for(int i=0;i<options.length;i++){
                if(options[i]==choice){
                    urldata = urloptions[i];
                    tipe = choice;
                    //this.txt_nothing.setText("Tidak Ada data "+options[i]);
                }
            }

        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    /*RequestBody body = new FormBody.Builder()
                            .add("","")
                            .build();*/
                    Request request=null;
                    /*String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            "Approval Reward"};*/

                    request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .url(urldata)
                            .build();

                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsonObject =  null;
                    }catch (Exception e){
                        e.printStackTrace();
                        jsonObject = null;
                    }

                    if (responses==null){
                        jsonObject = null;
                        Log.e(TAG, "NULL");
                    }
                    else {

                        result = new JSONObject(responses.body().string());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }


            return response;
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {
                List<listjobextension> listdata = new ArrayList<>();

                if (result != null) {

                    if(tipe.equals("Approval Cuti")){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {

                            Log.e(TAG, tipe + " " + result.toString());
                            Log.e("urldata",urldata);

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("mulai_berlaku").substring(0,10));
                                data.setTanggal2(obj.getString("exp_date").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Dinas") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_dinas").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_dinas").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Dirumahkan") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_dirumahkan").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_dirumahkan").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Izin") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Golongan") ){

                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //


                        JSONArray arrays = result.getJSONArray("data");
                        JSONArray arrays1 = result.getJSONArray("data1");
                        for (int i = 0; i < arrays.length(); i++) {
                            JSONObject obj = arrays.getJSONObject(i);

                            listjobextension data = new listjobextension();

                            String arrow = " ➡ ";

                            int index = 0 ;

                            Boolean isnotsame = false;

                            for (int j = 0; j < arrays1.length(); j++) {
                                JSONObject obj1 = arrays1.getJSONObject(j);
                                if(obj.getString("id_golongan").equals(obj1.getString("id_golongan"))){
                                    isnotsame = false;

                                    break;
                                }
                                else{
                                    isnotsame = true;
                                    index++;
                                }

                            }

                            if(isnotsame){
                                data.setNamakaryawan(obj.getString("golongan"));
                                data.setTipe(tipe);

                                DecimalFormat formatter = new DecimalFormat("###,###,###");


                                Double gaji = Double.parseDouble(obj.getString("gaji"));
                                Double makan = Double.parseDouble(obj.getString("makan"));
                                Double transport = Double.parseDouble(obj.getString("transport"));
                                Double thr = Double.parseDouble(obj.getString("thr"));
                                Double tundip = Double.parseDouble(obj.getString("tundip"));
                                Double tunjangan_lain = Double.parseDouble(obj.getString("tunjangan_lain"));
                                Double bpjs = Double.parseDouble(obj.getString("bpjs"));
                                Double potlain = Double.parseDouble(obj.getString("pot_lain"));
                                String lembur = "";

                                if(obj.getInt("lembur")==1){
                                    lembur = "Hidup";
                                }
                                else {
                                    lembur = "Tetap";
                                }

                                Double umk = Double.parseDouble(obj.getString("umk"));

                                String newline = "\n";
                                String spacing = " : ";

                                data.setKeterangan("Keterangan"+spacing+obj.getString("keterangan")+newline
                                        +"Gaji"+spacing+formatter.format(gaji)+newline+"Makan"+spacing+formatter.format(makan)+newline+"Transport"
                                        +spacing+formatter.format(transport)+newline+"thr"+spacing+formatter.format(thr)+newline+"Tundip"+spacing+formatter.format(tundip)+newline
                                        +"Tunjangan Lain"+spacing+formatter.format(tunjangan_lain)+newline+"BPJS"+spacing+formatter.format(bpjs)+newline
                                        +"Potongan Lain"+spacing+formatter.format(potlain)+newline+"Lembur"+spacing+lembur+newline+"UMK"+spacing+formatter.format(umk)+newline);

                                listdata.add(data);
                            }else{

                                JSONObject obj1 = arrays1.getJSONObject(index);

                                data.setNamakaryawan(obj.getString("golongan"));
                                data.setTipe(tipe);

                                DecimalFormat formatter = new DecimalFormat("###,###,###");


                                Double gaji1 = Double.parseDouble(obj.getString("gaji"));
                                Double makan1 = Double.parseDouble(obj.getString("makan"));
                                Double transport1 = Double.parseDouble(obj.getString("transport"));
                                Double thr1 = Double.parseDouble(obj.getString("thr"));
                                Double tundip1 = Double.parseDouble(obj.getString("tundip"));
                                Double tunjangan_lain1 = Double.parseDouble(obj.getString("tunjangan_lain"));
                                Double bpjs1 = Double.parseDouble(obj.getString("bpjs"));
                                Double potlain1 = Double.parseDouble(obj.getString("pot_lain"));
                                String lembur1 = "";

                                if(obj.getInt("lembur")==1){
                                    lembur1 = "Hidup";
                                }
                                else {
                                    lembur1 = "Tetap";
                                }

                                Double umk1 = Double.parseDouble(obj.getString("umk"));


                                String newline = "\n";
                                String spacing = " : ";

                                if(!obj.getString("golongan").equals(obj1.getString("golongan"))){
                                    data.setNamakaryawan(obj.getString("golongan")+arrow+obj1.getString("golongan"));
                                }
                                else {
                                    data.setNamakaryawan(obj.getString("golongan"));
                                }

                                data.setTipe(tipe);

                                String texting = "Keterangan"+spacing;

                                if(!obj.getString("golongan").equals(obj1.getString("golongan"))){
                                    texting = texting + obj.getString("keterangan")+arrow+obj1.getString("golongan")+newline;
                                }
                                else {
                                    texting = texting + obj.getString("keterangan")+newline;
                                }

                                Double gaji2 = Double.parseDouble(obj1.getString("gaji"));
                                Double makan2 = Double.parseDouble(obj1.getString("makan"));
                                Double transport2 = Double.parseDouble(obj1.getString("transport"));
                                Double thr2 = Double.parseDouble(obj1.getString("thr"));
                                //Double tundip2 = Double.parseDouble(obj1.getString("tundip"));
                                Double tunjangan_lain2 = Double.parseDouble(obj1.getString("tunjangan_lain"));
                                Double bpjs2 = Double.parseDouble(obj1.getString("bpjs"));
                                Double potlain2 = Double.parseDouble(obj1.getString("pot_lain"));
                                String lembur2 = "";

                                if(obj1.getInt("lembur")==1){
                                    lembur2 = "Hidup";
                                }
                                else {
                                    lembur2 = "Tetap";
                                }

                                Double umk2 = Double.parseDouble(obj1.getString("umk"));

                                if(gaji1==gaji2){
                                    texting = texting+"Gaji"+spacing+formatter.format(gaji1)+newline;
                                }
                                else {
                                    texting = texting+"Gaji"+spacing+formatter.format(gaji1)+arrow+formatter.format(gaji2)+newline;
                                }

                                if(makan1==makan2){
                                    texting = texting+"Makan"+spacing+formatter.format(makan1)+newline;
                                }
                                else {
                                    texting = texting+"Makan"+spacing+formatter.format(makan1)+arrow+formatter.format(makan2)+newline;
                                }

                                if(transport1 == transport2){
                                    texting = texting+"Transport"+spacing+formatter.format(transport1)+newline;
                                }
                                else {
                                    texting = texting+"Transport"+spacing+formatter.format(transport1)+arrow+formatter.format(transport2)+newline;
                                }

                                if(thr1==thr2){
                                    texting = texting+"THR"+spacing+formatter.format(thr1)+newline;
                                }
                                else {
                                    texting = texting+"THR"+spacing+formatter.format(thr1)+arrow+formatter.format(thr2)+newline;
                                }

                                    /*if(tundip1==tundip2){
                                        texting = texting+"Tundip"+spacing+formatter.format(tundip1)+newline;
                                    }
                                    else {
                                        texting = texting+"Tundip"+spacing+formatter.format(tundip1)+arrow+formatter.format(tundip2)+newline;
                                    }*/

                                if(tunjangan_lain1==tunjangan_lain2){
                                    texting = texting+"Tunjangan Lain"+spacing+formatter.format(tunjangan_lain1)+newline;
                                }
                                else {
                                    texting = texting+"Tunjangan Lain"+spacing+formatter.format(tunjangan_lain1)+arrow+formatter.format(tunjangan_lain2)+newline;
                                }

                                if(bpjs1==bpjs2){
                                    texting = texting+"BPJS"+spacing+formatter.format(bpjs1)+newline;
                                }
                                else {
                                    texting = texting+"BPJS"+spacing+formatter.format(bpjs1)+arrow+formatter.format(bpjs2)+newline;
                                }

                                if(potlain1==potlain2){
                                    texting = texting+"Potongan Lain"+spacing+formatter.format(potlain1)+newline;
                                }
                                else {
                                    texting = texting + "Potongan Lain" + spacing + formatter.format(potlain1) + arrow + formatter.format(potlain2) + newline;
                                }

                                if(lembur1.equals(lembur2)){
                                    texting = texting+"Lembur"+spacing+lembur1+newline;
                                }
                                else {
                                    texting = texting + "Lembur" + spacing + lembur1 + arrow + lembur2 + newline;
                                }

                                if(umk1==umk2){
                                    texting = texting+"UMK"+spacing+formatter.format(umk1)+newline;
                                }
                                else {
                                    texting = texting + "UMK" + spacing + formatter.format(umk1) + arrow + formatter.format(umk2) + newline;
                                }





                                String newline2 = "\n";
                                String spacing2 = " : ";


                                data.setKeterangan(texting);

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Karyawan") ){

                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //

                        String newline = "\n";
                        String spacing = " : ";




                        JSONArray arrays = result.getJSONArray("data");
                        JSONArray arrays1 = result.getJSONArray("data1");
                        for (int i = 0; i < arrays.length(); i++) {
                            JSONObject obj = arrays.getJSONObject(i);

                            listjobextension data = new listjobextension();

                            String texting = "";

                            String arrow = " ➡ ";

                            int index = 0 ;

                            Boolean isnotsame = false;

                            for (int j = 0; j < arrays1.length(); j++) {
                                JSONObject obj1 = arrays1.getJSONObject(j);
                                if(obj.getString("idfp").equals(obj1.getString("idfp"))){
                                    isnotsame = false;

                                    break;
                                }
                                else{
                                    isnotsame = true;
                                    index++;
                                }

                            }

                            if(isnotsame){
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }


                                DecimalFormat formatter = new DecimalFormat("###,###,###");

                                //0,1,2,39,37,36

                                for (int k = 0 ; k<obj.length();k++){
                                    if(k==0){

                                    }
                                    else if(k==1){

                                    }else if(k==2){

                                    }
                                    else if(k==39){

                                    }
                                    else if(k==37){

                                    }
                                    else if(k==36){

                                    }
                                    else {
                                        if(k==3){
                                            texting = texting + karyawandetail[k]+spacing+obj.getString(karyawan[k])+newline;

                                        }
                                        if(k==7){
                                            texting = texting + karyawandetail[k]+spacing+obj.getString(karyawan[k])+newline;
                                        }
                                    }

                                }
                                data.setKeterangan(texting);

                                listdata.add(data);

                                    /*Double gaji = Double.parseDouble(obj.getString("gaji"));
                                    String kode_karyawan = obj.getString("kode_karyawan");
                                    String idfp= obj.getString("kode_karyawan");
                                    String alamat = obj.getString("");
                                    String tempat_lahir = obj.getString("");
                                    String tgl_lahir = obj.getString("");
                                    String telepon = obj.getString("");
                                    String no_wali = obj.getString("");
                                    String email = obj.getString("");
                                    String tglmasuk = obj.getString("");
                                    String kelamin = obj.getString("");
                                    String status_nikah = obj.getString("");
                                    String pendidikan = obj.getString("");
                                    String wn = obj.getString("");
                                    String agama = obj.getString("");
                                    String shift = obj.getString("");
                                    String status_kerja = obj.getString("");
                                    String foto = obj.getString("foto");
                                    String kontrak = obj.getString("");
                                    String periode = obj.getString("");
                                    String  = obj.getString("");
                                    String  = obj.getString("");
                                    String lembur = "";*/
                            }else{

                                JSONObject obj1 = arrays1.getJSONObject(index);

                                data.setNamakaryawan(obj1.getString("nama")+"("+obj1.getString("info_approve").toUpperCase()+")");
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj1.getString("foto"));
                                }


                                DecimalFormat formatter = new DecimalFormat("###,###,###");

                                //0,1,2,39,37,36

                                for (int k = 0 ; k<obj.length();k++){
                                    if(k==0){

                                    }
                                    else if(k==1){

                                    }else if(k==2){

                                    }
                                    else if(k==39){

                                    }
                                    else if(k==37){

                                    }
                                    else if(k==36){

                                    }
                                    else {
                                        if(k==22){
                                            if(obj.getString(karyawan[k]).equals(obj1.getString(karyawan[k]))){

                                            }
                                            else {
                                                texting = texting + karyawandetail[k]+spacing+formatter.format(Integer.parseInt(obj.getString(karyawan[k])))+arrow+formatter.format(Integer.parseInt(obj1.getString(karyawan[k])))+newline;
                                            }
                                        }
                                        else {
                                            if(obj.getString(karyawan[k]).equals(obj1.getString(karyawan[k]))){

                                            }
                                            else {
                                                texting = texting + karyawandetail[k]+spacing+obj.getString(karyawan[k])+arrow+obj1.getString(karyawan[k])+newline;
                                            }

                                        }
                                    }

                                }
                                if(texting.equals("")){
                                    data.setKeterangan("Tidak Ada Perubahaan");
                                }
                                else {
                                    data.setKeterangan(texting);
                                }


                                listdata.add(data);



                                //listdata.add(data);
                            }
                        }




                    }
                    else if (tipe.equals("Approval Pinjaman") ){

                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //Approval Pinjaman {"status":true,"message":"berhasil get data","res1":[{"nama":"Sparno","id_pinjaman":17,"id_karyawan":7,"tanggal":"2018-10-23T00:00:00.000Z","jumlah":100000,
                        // "bayar":0,"sisa":100000,"keterangans":"-","status":"Proses","cicilan":0,"cara_pembayaran":"Cash"}]}
                        //
                        JSONArray arrays = result.getJSONArray("res1");
                        for (int i = 0; i < arrays.length(); i++) {
                            JSONObject obj = arrays.getJSONObject(i);

                            DecimalFormat formatter = new DecimalFormat("###,###,###");
                            listjobextension data = new listjobextension();

                            data.setTgldiajukan(obj.getString("tanggal").substring(0,10));

                            data.setNamakaryawan(obj.getString("nama"));
                            data.setTipe(tipe);

                            data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                            String neline = "\n";
                            String spacing = " : ";

                            String jumlah = formatter.format(obj.getDouble("jumlah"));

                            String cicilan = formatter.format(obj.getDouble("cicilan")),cara_pembayaran=obj.getString("cara_pembayaran"),keterangans=obj.getString("keterangans");

                            data.setKeterangan("Jumlah Pinjaman"+spacing+jumlah+neline+"Cicilan / Bulan"+spacing+cicilan+neline+"Cara pembayaran"+spacing+cara_pembayaran+neline+"Keterangan"+spacing+keterangans);

                            listdata.add(data);
                        }

                    }
                    else if (tipe.equals("Approval Pdm") ){

                        Log.e(TAG, tipe+" "+result.toString() );

                            /*E/retrive: Approval Pdm {"status":true,"message":"berhasil get data","res1":[{"nama":"Sparno","foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg",
                                    "temp_id_promosi":146,"id_promosi":35,"id_karyawan":7,"id_cabang1":3,"id_departemen1":2,"id_jabatan1":4,"id_golongan1":15,"id_grup1":6,
                                    "tanggal":"2018-09-01T00:00:00.000Z","judul":"Hapus Data","keterangans":"asdasdsa","status":"Proses","status_info":"Hapus",
                                    "create_at":"2018-10-08T12:23:10.000Z","approve_at":"2018-09-21T10:44:42.000Z"},{"nama":"Sparno","foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg",
                                    "temp_id_promosi":147,"id_promosi":38,"id_karyawan":7,"id_cabang1":3,"id_departemen1":2,"id_jabatan1":4,"id_golongan1":15,"id_grup1":6,
                                    "tanggal":"2018-09-01T00:00:00.000Z","judul":"Hapus Data","keterangans":"asdfasd1234333","status":"Proses","status_info":"Hapus",
                                    "create_at":"2018-10-08T13:24:29.000Z","approve_at":"2018-09-21T10:44:38.000Z"}],"userData":{"username":"MANADI","password":"manadi",
                                    "id_pengguna":2,"iat":1540435376}}*/


                            /*JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);

                                if(obj.getString("status_info").equals("Hapus")){

                                }
                                else if(obj.getString("status_info").equals("Edit")){

                                }
                                else {

                                }

                                DecimalFormat formatter = new DecimalFormat("###,###,###");
                                listjobextension data = new listjobextension();

                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));

                                data.setNamakaryawan(obj.getString("nama")+obj.getString("judul"));
                                data.setTipe(tipe);

                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                String neline = "\n";
                                String spacing = " : ";

                                //String jumlah = formatter.format(obj.getDouble("jumlah"));

                                //String cicilan = formatter.format(obj.getDouble("cicilan")),cara_pembayaran=obj.getString("cara_pembayaran"),keterangans=obj.getString("keterangans");

                                //data.setKeterangan("Jumlah Pinjaman"+spacing+jumlah+neline+"Cicilan / Bulan"+spacing+cicilan+neline+"Cara pembayaran"+spacing+cara_pembayaran+neline+"Keterangan"+spacing+keterangans);

                                //listdata.add(data);
                            }*/
                    }
                    else if (tipe.equals("Approval Punishment") ){

                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //

                        if(result.getString("status").equals("true")) {

                            DecimalFormat formatter = new DecimalFormat("###,###,###");

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan("Kasus : "+obj.getString("judul")+"\n"+"Nilai : "+formatter.format(obj.getInt("nilai"))+"\nKeterangan : "+obj.getString("keterangan"));
                                data.setTgldiajukan(obj.getString("created_at").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Reward") ){

                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //

                        if(result.getString("status").equals("true")) {

                            DecimalFormat formatter = new DecimalFormat("###,###,###");

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                listjobextension data = new listjobextension();
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan("Nilai : "+formatter.format(obj.getInt("nilai"))+"\nKeterangan : "+obj.getString("keteranganr"));
                                data.setTgldiajukan(obj.getString("created_at").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                if(obj.getString("foto").equals("")){
                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                }
                                else {
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                }

                                listdata.add(data);
                            }
                        }

                    }

                    /*if(listdata.size()==0){
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        nothing.setVisibility(View.GONE);
                    }*/

                    adapter = new AdapterListSwipe_approval(approval.this,listdata);
                    adapter.notifyDataSetChanged();

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(adapter);

                    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new adapterapprovalhelper(0, ItemTouchHelper.LEFT , approval.this);
                    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


                }
                else {
                    Snackbar.make(parent_view,"Gagal" + result.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,"Gagal" + E.getMessage(),Toast.LENGTH_LONG).show();
            }



            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}
