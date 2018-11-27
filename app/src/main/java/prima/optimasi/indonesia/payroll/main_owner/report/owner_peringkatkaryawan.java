package prima.optimasi.indonesia.payroll.main_owner.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedperingkat;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedpinjaman;
import prima.optimasi.indonesia.payroll.objects.datapinjaman;
import prima.optimasi.indonesia.payroll.objects.listperingkatkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class owner_peringkatkaryawan extends AppCompatActivity {


    TextView ttlbayar,ttlpinjam,ttlsisa;

    Double bayar=0.0d,sisa=0.0d,pinjam=0.0d;

    private View parent_view;

    ProgressDialog pd;

    LinearLayout activatepinjaman;
    listperingkatkaryawan kar;
    List<listperingkatkaryawan> items;
    List<listperingkatkaryawan> itemstemp;
    FloatingActionButton peringkat;
    private RecyclerView recyclerView;
    private AdapterListSectionedperingkat mAdapter;
    public boolean sort=false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_peringkat);

        activatepinjaman = findViewById(R.id.activatepinjaman);

        //activatepinjaman.setVisibility(View.VISIBLE);

        //ttlbayar = findViewById(R.id.ttlbayar);
        //ttlpinjam = findViewById(R.id.ttlpinjam);
        //ttlsisa = findViewById(R.id.ttlsisa);

        initToolbar();

        recyclerView = findViewById(R.id.recycler_report);
        peringkat=findViewById(R.id.peringkat);
        pd = new ProgressDialog(this);

        pd.setTitle("Loading Data Peringkat Karyawan");
        pd.setMessage("Please Wait...");
        pd.show();

        initToolbar();

        parent_view = findViewById(R.id.report_snackbar);

        retriveperingkat peringkatkar = new retriveperingkat(this,pd);
        peringkatkar.execute();
        peringkat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sort){
                    peringkat.setImageDrawable(owner_peringkatkaryawan.this.getResources().getDrawable(R.drawable.ic_arrow_up));
                    sort=false;
                    getSupportActionBar().setTitle("Report Karyawan Terbaik");

                }
                else{
                    peringkat.setImageDrawable(owner_peringkatkaryawan.this.getResources().getDrawable(R.drawable.ic_arrow_down));
                    sort=true;
                    getSupportActionBar().setTitle("Report Karyawan Terburuk");
                }
                items.clear();
                itemstemp.clear();
                if(mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                }
                retriveperingkatref pref=new retriveperingkatref(owner_peringkatkaryawan.this,pd);
                pref.execute();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Karyawan Terbaik");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_share_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class retriveperingkat extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.peringkatkaryawanurl;
        String passeddata = "" ;

        public retriveperingkat(Context context, ProgressDialog pede)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = pede;
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
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

                    RequestBody body = new FormBody.Builder()
                            .add("start",getIntent().getStringExtra("tanggal_masuk"))
                            .add("end",getIntent().getStringExtra("tanggal_keluar"))
                            .build();
                    Log.e("DATE : ", "" + getIntent().getStringExtra("tanggal_masuk") + " " + getIntent().getStringExtra("tanggal_keluar"));

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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        itemstemp= new ArrayList<>();
                        List<String> kodek=new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        JSONArray pengsarray = result.getJSONArray("data");
                        String kodekar="", nama="", jabatan="";
                        int telats=0, absen=0, hadir=0;
                        List<Integer> listtelat=new ArrayList<>();
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if (i + 1 != pengsarray.length()) {
                                if (!tempcall.equals(obj.getString("kode"))) {
                                    if (tempcall.equals("")) {
                                        tempcall = obj.getString("kode");
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        telats += Integer.parseInt(obj.getString("telat"));
                                    } else {
                                        kar.setTelat(telats);
                                        items.add(kar);
                                        telats = 0;
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        tempcall = obj.getString("kode");
                                        telats += Integer.parseInt(obj.getString("telat"));
                                    }
                                } else if (tempcall.equals(obj.getString("kode"))) {
                                    telats += Integer.parseInt(obj.getString("telat"));
                                }
                            } else {
                                if (!tempcall.equals(obj.getString("kode"))) {
                                    if (tempcall.equals("")) {
                                        //tempcall = obj.getString("kode");
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        telats += Integer.parseInt(obj.getString("telat"));
                                        kar.setTelat(telats);
                                        items.add(kar);
                                    }
                                    else {
                                        kar.setTelat(telats);
                                        items.add(kar);
                                        telats = 0;
                                        kar = new listperingkatkaryawan();
                                        if (!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        tempcall = obj.getString("kode");
                                        telats += Integer.parseInt(obj.getString("telat"));
                                        kar.setTelat(telats);
                                        items.add(kar);
                                    }

                                } else if (tempcall.equals(obj.getString("kode"))) {
                                    telats += Integer.parseInt(obj.getString("telat"));
                                    kar.setTelat(telats);
                                    items.add(kar);
                                }
                            }
                        }
                        int j=0;

                        while(j<kodek.size()){
                            telats=0;
                            hadir=0;
                            absen=Integer.parseInt(getIntent().getStringExtra("lama"));
                            kar = new listperingkatkaryawan();
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                if(kodek.get(j).equals(obj.getString("kode"))){
                                    absen--;
                                    hadir++;
                                    kar.setKode(obj.getString("kode"));
                                    kar.setNama(obj.getString("nama"));
                                    kar.setJabatan(obj.getString("jabatan"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    telats += Integer.parseInt(obj.getString("telat"));
                                }
                            }
                            j++;
                            kar.setTelat(telats);
                            kar.setAbsen(absen);
                            kar.setHadir(hadir);
                            kar.setTotal(Integer.parseInt(getIntent().getStringExtra("lama")));
                            itemstemp.add(kar);
                        }


                        Collections.sort(itemstemp, new Comparator<listperingkatkaryawan>() {
                            @Override
                            public int compare(listperingkatkaryawan listperingkatkaryawan, listperingkatkaryawan t1) {
                                //Log.e("ABSEN",""+listperingkatkaryawan.getAbsen().compareTo(t1.getAbsen()));
                                if(listperingkatkaryawan.getHadir().compareTo(t1.getHadir())<0 || listperingkatkaryawan.getHadir().compareTo(t1.getHadir())>0){
                                    return -listperingkatkaryawan.getHadir().compareTo(t1.getHadir());
                                }
                                else if(listperingkatkaryawan.getTelat().compareTo(t1.getTelat())==0 && listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) == 0){
                                    return listperingkatkaryawan.getNama().compareTo(t1.getNama());
                                }
                                return listperingkatkaryawan.getTelat().compareTo(t1.getTelat());

                            }
                        });

                        DecimalFormat formatter =new DecimalFormat("###,###,###");

                        mAdapter = new AdapterListSectionedperingkat(owner_peringkatkaryawan.this, itemstemp, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(owner_peringkatkaryawan.this, 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(mAdapter);



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

    private class retriveperingkatref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.peringkatkaryawanurl;
        String passeddata = "" ;

        public retriveperingkatref(Context context, ProgressDialog pede)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = pede;
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
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

                    RequestBody body = new FormBody.Builder()
                            .add("start",getIntent().getStringExtra("tanggal_masuk"))
                            .add("end",getIntent().getStringExtra("tanggal_keluar"))
                            .build();
                    Log.e("DATE : ", "" + getIntent().getStringExtra("tanggal_masuk") + " " + getIntent().getStringExtra("tanggal_keluar"));

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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        List<String> kodek=new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        JSONArray pengsarray = result.getJSONArray("data");
                        String kodekar="", nama="", jabatan="";
                        int telats=0, absen=0, hadir=0;
                        List<Integer> listtelat=new ArrayList<>();
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if (i + 1 != pengsarray.length()) {
                                if (!tempcall.equals(obj.getString("kode"))) {
                                    if (tempcall.equals("")) {
                                        tempcall = obj.getString("kode");
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        telats += Integer.parseInt(obj.getString("telat"));
                                    } else {
                                        kar.setTelat(telats);
                                        items.add(kar);
                                        telats = 0;
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        tempcall = obj.getString("kode");
                                        telats += Integer.parseInt(obj.getString("telat"));
                                    }
                                } else if (tempcall.equals(obj.getString("kode"))) {
                                    telats += Integer.parseInt(obj.getString("telat"));
                                }
                            } else {
                                if (!tempcall.equals(obj.getString("kode"))) {
                                    if (tempcall.equals("")) {
                                        //tempcall = obj.getString("kode");
                                        kar = new listperingkatkaryawan();
                                        if(!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        telats += Integer.parseInt(obj.getString("telat"));
                                        kar.setTelat(telats);
                                        items.add(kar);
                                    }
                                    else {
                                        kar.setTelat(telats);
                                        items.add(kar);
                                        telats = 0;
                                        kar = new listperingkatkaryawan();
                                        if (!kodek.contains(obj.getString("kode"))) {
                                            kodek.add(obj.getString("kode"));
                                        }
                                        kar.setKode(obj.getString("kode"));
                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        tempcall = obj.getString("kode");
                                        telats += Integer.parseInt(obj.getString("telat"));
                                        kar.setTelat(telats);
                                        items.add(kar);
                                    }

                                } else if (tempcall.equals(obj.getString("kode"))) {
                                    telats += Integer.parseInt(obj.getString("telat"));
                                    kar.setTelat(telats);
                                    items.add(kar);
                                }
                            }
                        }
                        int j=0;

                        while(j<kodek.size()){
                            telats=0;
                            hadir=0;
                            absen=Integer.parseInt(getIntent().getStringExtra("lama"));
                            kar = new listperingkatkaryawan();
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                if(kodek.get(j).equals(obj.getString("kode"))){
                                    absen--;
                                    hadir++;
                                    kar.setKode(obj.getString("kode"));
                                    kar.setNama(obj.getString("nama"));
                                    kar.setJabatan(obj.getString("jabatan"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    telats += Integer.parseInt(obj.getString("telat"));
                                }
                            }
                            j++;
                            kar.setTelat(telats);
                            kar.setAbsen(absen);
                            kar.setHadir(hadir);
                            kar.setTotal(Integer.parseInt(getIntent().getStringExtra("lama")));
                            itemstemp.add(kar);
                        }
                        if(sort) {
                            Collections.sort(itemstemp, new Comparator<listperingkatkaryawan>() {
                                @Override
                                public int compare(listperingkatkaryawan listperingkatkaryawan, listperingkatkaryawan t1) {
                                    //Log.e("ABSEN",""+listperingkatkaryawan.getAbsen().compareTo(t1.getAbsen()));
                                    if (listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) < 0 || listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) > 0) {
                                        return listperingkatkaryawan.getHadir().compareTo(t1.getHadir());
                                    }
                                    else if(listperingkatkaryawan.getTelat().compareTo(t1.getTelat())==0 && listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) == 0){
                                        return -listperingkatkaryawan.getNama().compareTo(t1.getNama());
                                    }
                                    return -listperingkatkaryawan.getTelat().compareTo(t1.getTelat());

                                }
                            });
                        }
                        else{
                            Collections.sort(itemstemp, new Comparator<listperingkatkaryawan>() {
                                @Override
                                public int compare(listperingkatkaryawan listperingkatkaryawan, listperingkatkaryawan t1) {
                                    //Log.e("ABSEN",""+listperingkatkaryawan.getAbsen().compareTo(t1.getAbsen()));
                                    if (listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) < 0 || listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) > 0) {
                                        return -listperingkatkaryawan.getHadir().compareTo(t1.getHadir());
                                    }
                                    else if(listperingkatkaryawan.getTelat().compareTo(t1.getTelat())==0 && listperingkatkaryawan.getHadir().compareTo(t1.getHadir()) == 0){
                                        return listperingkatkaryawan.getNama().compareTo(t1.getNama());
                                    }
                                    return listperingkatkaryawan.getTelat().compareTo(t1.getTelat());

                                }
                            });
                        }

                        if(mAdapter!=null){
                            mAdapter.notifyDataSetChanged();
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

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
}
