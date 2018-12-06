package prima.optimasi.indonesia.payroll.universal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listtimeline;
import prima.optimasi.indonesia.payroll.universal.adapter.AdapterTimeline;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class viewtimeline extends AppCompatActivity {

    ProgressDialog dialog;
    private View parent_view;
    String namakar="";
    AdapterTimeline timeline;
    listtimeline tl;
    List<listtimeline> itemstl;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timeline);
        initToolbar();
        initComponent();
        dialog.setTitle("Mohon Menunggu");
        dialog.setMessage("Memuat Timeline");
        dialog.show();
        itemstl=new ArrayList<>();
        retrivetimeline time = new retrivetimeline(viewtimeline.this);
        time.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View TimeLine");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        parent_view=findViewById(R.id.parent_view);
        recyclerView=findViewById(R.id.recyclerView);
        namakar=getIntent().getStringExtra("nama");
        dialog=new ProgressDialog(viewtimeline.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class retrivetimeline extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        Context ctx;
        JSONObject result = null ;
        //ProgressDialog dialog;
        String urldata=generator.gettimelineyurl;
        //String passedid = "" ;


        public retrivetimeline(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            //passedid = id;
            this.error = error ;
            ctx=context;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            //this.dialog.show();
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

                    RequestBody body= new FormBody.Builder()
                            .add("id", getIntent().getStringExtra("idkaryawan"))
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

                if (result != null) {
                    try {
                        Log.e(TAG, "data json "+ result);
                        JSONObject obj= result.getJSONObject("data");
                        tl=new listtimeline();
                        tl.setNama(namakar);
                        tl.setAktivitas(" masuk kerja pertama kali");
                        tl.setTanggal(obj.getString("tanggal_masuk").substring(0,10));
                        tl.setKeterangan("Pertama kali masuk kerja pada tanggal "+obj.getString("tanggal_masuk").substring(0,10));
                        itemstl.add(tl);

                        JSONArray arraycuti=obj.getJSONArray("cuti");
                        for(int i=0;i<arraycuti.length();i++){
                            JSONObject objcuti=arraycuti.getJSONObject(i);
                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mengajukan cuti");
                            tl.setTanggal(objcuti.getString("tanggal").substring(0,10));
                            tl.setKeterangan("Pengajuan cuti pada tanggal "+objcuti.getString("mulai_berlaku").substring(0,10)+" s/d "+objcuti.getString("exp_date").substring(0,10)+" dengan "+ objcuti.getString("lama_cuti")+" lama cuti ");
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mulai cuti");
                            tl.setTanggal(objcuti.getString("mulai_berlaku").substring(0,10));
                            tl.setKeterangan("Mulai cuti pada tanggal "+objcuti.getString("mulai_berlaku").substring(0,10));
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" cuti berakhir");
                            tl.setTanggal(objcuti.getString("exp_date").substring(0,10));
                            tl.setKeterangan("Akhir cuti pada tanggal "+objcuti.getString("exp_date").substring(0,10));
                            itemstl.add(tl);
                        }

                        JSONArray arraysakit=obj.getJSONArray("sakit");
                        for(int i=0;i<arraysakit.length();i++){
                            JSONObject objsakit=arraysakit.getJSONObject(i);
                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mengajukan sakit");
                            tl.setTanggal(objsakit.getString("tanggal").substring(0,10));
                            tl.setKeterangan("Pengajuan sakit pada tanggal "+objsakit.getString("tgl_sakit").substring(0,10)+" s/d "+objsakit.getString("akhir_sakit").substring(0,10)+" dengan "+ objsakit.getString("lama")+" lama sakit ");
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mulai sakit");
                            tl.setTanggal(objsakit.getString("tgl_sakit").substring(0,10));
                            tl.setKeterangan("Mulai sakit pada tanggal "+objsakit.getString("tgl_sakit").substring(0,10));
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" sakit berakhir");
                            tl.setTanggal(objsakit.getString("akhir_sakit").substring(0,10));
                            tl.setKeterangan("Akhir sakit pada tanggal "+objsakit.getString("akhir_sakit").substring(0,10));
                            itemstl.add(tl);
                        }

                        JSONArray arraydinas=obj.getJSONArray("dinas");
                        for(int i=0;i<arraydinas.length();i++){
                            JSONObject objdinas=arraydinas.getJSONObject(i);
                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mengajukan dinas");
                            tl.setTanggal(objdinas.getString("tanggal").substring(0,10));
                            tl.setKeterangan("Pengajuan sakit pada tanggal "+objdinas.getString("tgl_dinas").substring(0,10)+" s/d "+objdinas.getString("akhir_dinas").substring(0,10)+" dengan "+ objdinas.getString("lama")+" lama dinas ");
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" mulai dinas");
                            tl.setTanggal(objdinas.getString("tgl_dinas").substring(0,10));
                            tl.setKeterangan("Mulai dinas pada tanggal "+objdinas.getString("tgl_dinas").substring(0,10));
                            itemstl.add(tl);

                            tl=new listtimeline();
                            tl.setNama(namakar);
                            tl.setAktivitas(" akhir dinas");
                            tl.setTanggal(objdinas.getString("akhir_dinas").substring(0,10));
                            tl.setKeterangan("Akhir dinas pada tanggal "+objdinas.getString("akhir_dinas").substring(0,10));
                            itemstl.add(tl);
                        }
                        Collections.sort(itemstl, new Comparator<listtimeline>() {
                            @Override
                            public int compare(listtimeline listtimeline, listtimeline t1) {
                                //Log.e("ABSEN",""+listperingkatkaryawan.getAbsen().compareTo(t1.getAbsen()));

                                return -listtimeline.getTanggal().compareTo(t1.getTanggal());

                            }
                        });
                        Log.e("ITEMSTL",""+itemstl.size());
                        if(itemstl.size()>0){
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            timeline = new AdapterTimeline(viewtimeline.this, itemstl, ItemAnimation.NONE);
                            recyclerView.setLayoutManager(new LinearLayoutManager(viewtimeline.this));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(timeline);
                        }


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
            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

}
