package prima.optimasi.indonesia.payroll.main_owner.manage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.adapter.AdapterListSwipe;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.data.DataGenerator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListBasicjob_extention;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSwipe_approval;
import prima.optimasi.indonesia.payroll.model.Social;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class approval extends AppCompatActivity {


    private View parent_view;

    private AdapterListSwipe_approval adapter;

    private RecyclerView recyclerView;
    private AdapterListSwipe mAdapter;



    String tipe="";
    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_approval);

        parent_view = findViewById(android.R.id.content);

        for (int i = 0 ; i < options.length;i++){
            if(options[i].equals(getIntent().getStringExtra("tipe"))){
                initToolbar(options[i]);
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
        RecyclerView recycler ;
        //listjobextension =

        public retrive(Context context, String choice, View nothing,  RecyclerView recycler)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

            this.recycler = recycler;

            this.txt_nothing = txt_nothing;
            this.nothing = nothing;

            for(int i=0;i<options.length;i++){
                if(options[i]==choice){
                    //urldata = urloptions[i];
                    tipe = choice;
                    this.txt_nothing.setText("Tidak Ada data "+options[i]);
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
                            listjobextension data = new listjobextension();
                            Log.e(TAG, tipe + " " + result.toString());

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("mulai_berlaku").substring(0,10));
                                data.setTanggal2(obj.getString("exp_date").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Dinas") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                            listjobextension data = new listjobextension();

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_dinas").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_dinas").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Dirumahkan") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                            listjobextension data = new listjobextension();

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_dirumahkan").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_dirumahkan").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Izin") ){
                        Log.e(tipe,result.toString() );
                        if(result.getString("status").equals("true")) {
                            Log.e(TAG, tipe + " " + result.toString());
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                            listjobextension data = new listjobextension();

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }
                        }
                    }
                    else if (tipe.equals("Approval Golongan") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                        listjobextension data = new listjobextension();

                        JSONArray arrays = result.getJSONArray("data");
                        for (int i = 0; i < arrays.length(); i++) {
                            JSONObject obj = arrays.getJSONObject(i);
                            data.setJabatan(obj.getString("jabatan"));
                            data.setKeterangan(obj.getString("keterangans"));
                            data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                            data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                            data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                            data.setNamakaryawan(obj.getString("nama"));
                            data.setTipe(tipe);
                            data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                            listdata.add(data);
                        }

                            /*JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }*/
                    }
                    else if (tipe.equals("Approval Karyawan") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                    }
                    else if (tipe.equals("Approval Pinjaman") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                    }
                    else if (tipe.equals("Approval Pdm") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                    }
                    else if (tipe.equals("Approval Punishment") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                    }
                    else if (tipe.equals("Approval Reward") ){
                        Log.e(TAG, tipe+" "+result.toString() );
                        //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                        // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                        // "Approval Reward"};
                        //
                    }

                    if(listdata.size()==0){
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        nothing.setVisibility(View.GONE);
                    }

                    //mAdapter = new AdapterListSwipe (approval.this,listdata);
                    //mAdapter.notifyDataSetChanged();

                    recycler.setLayoutManager(new LinearLayoutManager(approval.this));
                    recycler.setHasFixedSize(true);
                    recycler.setAdapter(mAdapter);
                }
                else {
                    Toast.makeText(approval.this,"Gagal" + result.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Toast.makeText(approval.this,"Gagal" + result,Toast.LENGTH_SHORT).show();
            }



            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}
