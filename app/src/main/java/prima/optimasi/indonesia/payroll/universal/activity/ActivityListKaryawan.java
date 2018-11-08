package prima.optimasi.indonesia.payroll.universal.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentHome;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawandaftarabsensi;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.universal.adapter.AdapterListKaryawan;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterhistorypengajuan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class ActivityListKaryawan extends AppCompatActivity {
    MaterialSearchView searchView;
    CoordinatorLayout parent_view;
    TextView tanggal;
    RecyclerView recyclerView;
    AdapterListKaryawan adapter;
    List<listkaryawan> items;
    listkaryawan kar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_karyawan);
        parent_view=findViewById(R.id.parent_view);
        recyclerView=findViewById(R.id.recyclerView);
        tanggal=findViewById(R.id.tanggal);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        tanggal.setText(formattedDate);

        if(getIntent().getStringExtra("keterangan").equals("izin")){
            retriveketerangan kar=new retriveketerangan(this,"izin");
            kar.execute();
        }
        else if(getIntent().getStringExtra("keterangan").equals("sakit")){
            retriveketerangan kar=new retriveketerangan(this,"sakit");
            kar.execute();
        }
        else if(getIntent().getStringExtra("keterangan").equals("cuti")){
            retriveketerangan kar=new retriveketerangan(this,"cuti");
            kar.execute();
        }
        else{
            retrivelistkaryawan kar=new retrivelistkaryawan(this,getIntent().getStringExtra("keterangan"));
            kar.execute();
        }


        searchView=(MaterialSearchView)findViewById(R.id.searchView);
        initToolbar();
    }

    private class retriveketerangan extends AsyncTask<Void, Integer, String>
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
        String keterangan="";

        public retriveketerangan(Context context, String keterangan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
            if(keterangan.equals("izin")){
                urldata=generator.getizinbulananyurl;
            }
            else if(keterangan.equals("sakit")){
                urldata=generator.getsakitbulananyurl;
            }
            else{
                urldata=generator.getcutibulananyurl;
            }
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .post(body)
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items=new ArrayList<>();
                        JSONArray pengsarray = result.getJSONArray("rows");
                        if(pengsarray.length()==0){
                            tanggal.setText("Tidak ada karyawan yang "+keterangan+" bulan ini");
                        }
                        else{
                            for (int i=0;i<pengsarray.length();i++){
                                JSONObject obj=pengsarray.getJSONObject(i);
                                kar=new listkaryawan();
                                kar.setNama(obj.getString("nama"));
                                kar.setIskar(obj.getString("id"));
                                kar.setJabatan(obj.getString("jabatan"));
                                if(!obj.getString("foto").equals("")){
                                    kar.setImagelink(generator.profileurl+obj.getString("foto"));
                                }
                                else{
                                    kar.setImagelink("");
                                }
                                items.add(kar);
                            }
                            adapter=new AdapterListKaryawan(ActivityListKaryawan.this,items, ItemAnimation.BOTTOM_UP);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ActivityListKaryawan.this));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapter);
                        }

                    }  catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }  catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivelistkaryawan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.karyawanjabatanurl;
        String passeddata = "" ;
        String jabatan="";

        public retrivelistkaryawan(Context context, String jabatan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.jabatan=jabatan;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("jabatan",jabatan)
                            .build();

                    Log.e(TAG, jabatan);

                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .post(body)
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                if (result != null) {
                    try {
                        //"data":[{"id":1,"kode_karyawan":"EMP-1","idfp":"KRY0001","nama":"Tes1","alamat":"1","tempat_lahir":"1","tgl_lahir":"2018-08-23T00:00:00.000Z",
                        // "telepon":"12313","no_wali":"12321321321","email":"12312@sad.asd","tgl_masuk":"0000-00-00","kelamin":"laki-laki","status_nikah":"Menikah",
                        // "pendidikan":"SMA Sederajat","wn":"Asing","agama":"Katholik","shift":"tidak","status_kerja":"non_aktif","ibu_kandung":"12312321","suami_istri":"12312321",
                        // "tanggungan":123213,"npwp":"1318","gaji":32640000,"rekening":"12321312312133","id_bank":6,"id_departemen":1,"id_jabatan":1,"id_grup":8,"id_golongan":15,
                        // "atas_nama":"tes1","foto":"","id_cabang":2,"start_date":null,"expired_date":null,"jab_index":0,"kontrak":"tidak","file_kontrak":"","otoritas":2,
                        // "periode_gaji":"2-Mingguan","qrcode_file":"d8ea4629488be4b2dad4bfff524d5667.png","jabatan":"Admin Kantor","keterangan":"Jabatan","tunjangan":0},
                        // {"id":23,"kode_karyawan":"EMP-99","idfp":"KRY0013","nama":"Astros","alamat":"asdsa","tempat_lahir":"1","tgl_lahir":"2018-09-26T00:00:00.000Z",
                        // "telepon":"1231321321321","no_wali":"090909090909","email":"12312@sad.asd","tgl_masuk":"2018-09-26T00:00:00.000Z","kelamin":"laki-laki",
                        // "status_nikah":"Menikah","pendidikan":"Sarjana S3","wn":"Indonesia","agama":"Protestan","shift":"ya","status_kerja":"aktif","ibu_kandung":"12312321",
                        // "suami_istri":"assdaadsd","tanggungan":1,"npwp":"12312321","gaji":32640000,"rekening":"12312321","id_bank":5,"id_departemen":1,"id_jabatan":1,"id_grup":9,
                        // "id_golongan":null,"atas_nama":"Astros","foto":"","id_cabang":2,"start_date":null,"expired_date":null,"jab_index":0,"kontrak":"tidak","file_kontrak":"",
                        // "otoritas":4,"periode_gaji":"2-Mingguan","qrcode_file":"196d351656861176861dc93ac15a9fee.png","jabatan":"Admin Kantor","keterangan":"Jabatan","tunjangan":0}]}

                        Log.e(TAG, "daftar absensi" + result.toString());
                        items=new ArrayList<>();
                        JSONArray pengsarray = result.getJSONArray("data");
                        for(int i=0;i<pengsarray.length();i++){
                            JSONObject obj = pengsarray.getJSONObject(i);
                            kar=new listkaryawan();
                            kar.setIskar(obj.getString("id"));
                            kar.setNama(obj.getString("nama"));
                            kar.setJabatan(jabatan);
                            if(!obj.getString("foto").equals("")){
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));
                            }
                            else{
                                kar.setImagelink("");
                            }
                            items.add(kar);
                        }
                        adapter=new AdapterListKaryawan(ActivityListKaryawan.this,items, ItemAnimation.BOTTOM_UP);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityListKaryawan.this));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                    }catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                adapter.getFilter().filter(query);
                //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //Do some magic
                Log.e("Text", "newText=" + query);
                adapter.getFilter().filter(query);
                //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        else if(id==R.id.action_search){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
