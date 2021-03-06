package prima.optimasi.indonesia.payroll.main_karyawan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listjadwal;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterjadwal;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class cekjadwal extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapterjadwal adapter;
    List<listjadwal> itemjadwal;
    listjadwal lj;
    CoordinatorLayout parent_view;
    Snackbar snackbar;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_jadwal);
        initToolbar();
        initComponent();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        parent_view=findViewById(R.id.parent_view);
        prefs = getApplicationContext().getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
        String kodekaryawan=prefs.getString("kodekaryawan","");
        getSupportActionBar().setTitle("Cek Jadwal Sendiri");
        itemjadwal = new ArrayList<>();
        snackBarWithActionIndefinite();
    }

    private void snackBarWithActionIndefinite() {
        if(generator.checkInternet(cekjadwal.this)) {
            if(snackbar!=null) {
                snackbar.dismiss();
            }
            if (itemjadwal != null) {
                itemjadwal.clear();
            } else {
                itemjadwal = new ArrayList<>();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            retrivejadwal kar = new retrivejadwal(this, prefs.getString("kodekaryawan",""));
            kar.execute();
        }
        else {
            snackbar = Snackbar.make(parent_view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.try_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackBarWithActionIndefinite();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item=menu.findItem(R.id.action_settings);
        item.setTitle("About");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class retrivejadwal extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        Context ctx;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.jadwalurl;
        String passedid = "" ;

        public retrivejadwal(Context context,String id)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            passedid = id;
            this.error = error ;
            ctx=context;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            this.dialog.show();
            super.onPreExecute();
            this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .url(urldata+"/"+passedid)
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
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.dismiss();
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

                        Log.e(TAG, "data json result" + result);
                        JSONArray pengsarray = result.getJSONArray("rows");

                        for (int i = 0; i < pengsarray.length(); i++) {
                            final JSONObject obj = pengsarray.getJSONObject(i);
                            String tahun = obj.getString("tanggal").substring(0,4);
                            String bulan = obj.getString("tanggal").substring(5,7);
                            String tanggal = obj.getString("tanggal").substring(8,10);
                            String jadwal_tanggal=tanggal+"/"+bulan+"/"+tahun;
                            String jadwal_section=bulan+"/"+tahun;
                            String nama_shift = obj.getString("nama_shift");
                            String masuk = obj.getString("masuk");
                            String keluar = obj.getString("keluar");
                            lj = new listjadwal();
                            lj.setTanggal(jadwal_tanggal);
                            lj.setNama_shift(nama_shift);
                            lj.setMasuk(masuk);
                            lj.setKeluar(keluar);
                            //lj.setJadwal_section(jadwal_section);
                            itemjadwal.add(lj);
                            adapter = new Adapterjadwal(ctx, itemjadwal,ItemAnimation.FADE_IN);
                            Log.e(TAG, "data json result" + jadwal_tanggal);
                            recyclerView.setLayoutManager(new LinearLayoutManager(prima.optimasi.indonesia.payroll.main_karyawan.cekjadwal.this));
                            //recyclerView.addItemDecoration(new LineItemDecoration(cekjadwal.this, LinearLayout.VERTICAL));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapter);
                        }

                        /*
                        "rows":[{"nama":"Sparno","jabatan":"Kepala Grup A","departemen":"IT","id":157,"tanggal":"2018-11-04T00:00:00.000Z","karyawan_id":7,"group_id":6,
                        "otoritas_kerja":2,"shift_id":43,"nama_shift":"SHIFT-SORE","token":8,"status_ganti":1,"ganti_karyawan_id":null,"created_at":"2018-10-05T16:14:03.000Z",
                        "updated_at":"2018-10-05T16:14:03.000Z"}
                        */


                    } catch (JSONException e) {
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

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
}
