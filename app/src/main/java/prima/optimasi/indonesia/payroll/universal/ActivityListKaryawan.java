package prima.optimasi.indonesia.payroll.universal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.adapter.AdapterListKaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class ActivityListKaryawan extends AppCompatActivity {
    SwipeRefreshLayout swipehome;
    View lyt_selectdate;
    long selecteddate = 0L;
    MaterialSearchView searchView;
    CoordinatorLayout parent_view;
    TextView tanggal, tidakada;
    RecyclerView recyclerView;
    AdapterListKaryawan adapter;
    List<listkaryawan> items, total, hadir;
    listkaryawan kar;
    ProgressDialog dialog;
    String tanggalskrg="", settanggal;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_karyawan);
        initToolbar();
        initComponent();
        initListener();
        if(generator.checkInternet(ActivityListKaryawan.this)) {
            dialog.setTitle("Memuat Data");
            dialog.setMessage("Data Karyawan yang " + getIntent().getStringExtra("keterangan") + " sedang Dimuat.. mohon tunggu");
            dialog.show();
            setretrive();
        }
        else {
            snackBarWithActionIndefinite();
        }
    }

    private void snackBarWithActionIndefinite() {
        if(generator.checkInternet(ActivityListKaryawan.this)) {
            if(snackbar!=null) {
                snackbar.dismiss();
            }
            dialog.setMessage("Loading ...");
            dialog.show();
            if(items!=null){
                items.clear();
            }
            else{
                items=new ArrayList<>();
            }
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,0);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());
            tanggal.setText(formattedDate);
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
            setretrive();
            if(swipehome.isRefreshing()){
                swipehome.setRefreshing(false);
            }
        }
        else {
            if(swipehome.isRefreshing()){
                swipehome.setRefreshing(false);
            }
            snackbar = Snackbar.make(parent_view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction("COBA LAGI", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackBarWithActionIndefinite();
                        }
                    });
            snackbar.show();
        }
    }

    public void setretrive(){
        if(getIntent().getStringExtra("keterangan").equals("izin") || getIntent().getStringExtra("keterangan").equals("sakit") || getIntent().getStringExtra("keterangan").equals("cuti")
                || getIntent().getStringExtra("keterangan").equals("dinas") || getIntent().getStringExtra("keterangan").equals("telat")){
            if(dialog.isShowing()){

            }
            else {
                dialog.show();
            }

            retriveketerangan kar=new retriveketerangan(this,getIntent().getStringExtra("keterangan"));
            kar.execute();
        }
        else if(getIntent().getStringExtra("keterangan").equals("absen")){

            if(dialog.isShowing()){

            }
            else {
                dialog.show();
            }

            retrivetotal kar=new retrivetotal(this);
            kar.execute();
        }
        else{
            dialog.setMessage("Memuat Data Karyawan");

            if(dialog.isShowing()){

            }
            else {
                dialog.show();
            }

            retrivelistkaryawan kar=new retrivelistkaryawan(this,getIntent().getStringExtra("keterangan"));
            kar.execute();
        }
    }

    private class retriveketerangan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        String urldata = "";
        String keterangan="";

        public retriveketerangan(Context context, String keterangan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
            if(keterangan.equals("izin")){
                urldata=generator.getizinhariyurl;
            }
            else if(keterangan.equals("sakit")){
                urldata=generator.getsakithariyurl;
            }
            else if(keterangan.equals("cuti")){
                urldata=generator.getcutihariyurl;
            }
            else if(keterangan.equals("dinas")){
                urldata=generator.getdinashariyurl;
            }
            else if(keterangan.equals("telat")){
                urldata=generator.absensitelatyurl;
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
                            .add("date",settanggal)
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
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        if(items!=null){
                            items.clear();
                        }
                        else{
                            items=new ArrayList<>();
                        }
                        JSONArray pengsarray = result.getJSONArray("rows");
                        if(pengsarray.length()==0){
                            tidakada.setVisibility(View.VISIBLE);
                            tidakada.setText("Tidak ada karyawan yang "+keterangan);
                        }
                        else{
                            tidakada.setVisibility(View.GONE);

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
                            if(adapter!=null){
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                adapter=new AdapterListKaryawan(ActivityListKaryawan.this,items, ItemAnimation.BOTTOM_UP);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityListKaryawan.this));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(adapter);
                            }

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

    private class retrivetotal extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        String urldata = generator.listemployeeurl;

        public retrivetotal(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
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


                    Request request = new Request.Builder()
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

            if(dialog.isShowing()){
                dialog.dismiss();
            }

            try {
                Log.e(TAG, "data json yang absen" + result.toString());
                if (result != null) {
                    try {
                        if(total!=null){
                            total.clear();
                        }
                        else{
                            total=new ArrayList<>();
                        }
                        JSONArray pengsarray = result.getJSONArray("rows");

                        for (int i=0;i<pengsarray.length();i++){
                            JSONObject obj=pengsarray.getJSONObject(i);
                            kar=new listkaryawan();
                            kar.setIskar(obj.getString("id"));
                            kar.setNama(obj.getString("nama"));
                            kar.setKode(obj.getString("kode_karyawan"));
                            kar.setJabatan(obj.getString("jabatan"));
                            if(!obj.getString("foto").equals("")){
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));
                            }
                            else{
                                kar.setImagelink("");
                            }
                            total.add(kar);
                        }
                        tidakada.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        retriveabsen absen=new retriveabsen(ActivityListKaryawan.this,pengsarray.length());
                        absen.execute();

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


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retriveabsen extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        JSONObject result1 = null ;
        int karyawanjumlah=0;
        String urldata = generator.getabsensidateurl;

        public retriveabsen(Context context,int karyawanjumlah)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.karyawanjumlah=karyawanjumlah;
            this.error = error ;
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

                    RequestBody body = new FormBody.Builder()
                            .add("date",settanggal)
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

        protected void onPostExecute(String result2) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result1 != null) {
                    generator.servertime = result1.getString("jam");
                }
                if (result != null) {
                    try {
                        if(items!=null){
                            items.clear();
                        }
                        else{
                            items=new ArrayList<>();
                        }
                        if(hadir!=null){
                            hadir.clear();
                        }
                        else{
                            hadir=new ArrayList<>();
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        if(pengsarray.length()==0){
                            recyclerView.setVisibility(View.GONE);
                            tidakada.setVisibility(View.VISIBLE);
                            tidakada.setText("Semua Karyawan Hadir");
                        }
                        else {
                            String tempcall = "";
                            boolean hadir;
                            for (int j = 0; j < karyawanjumlah; j++) {
                                hadir = false;
                                for (int i = 0; i < pengsarray.length(); i++) {
                                    JSONObject obj = pengsarray.getJSONObject(i);
                                    if(total.get(j).getKode().equals(obj.getString("kode"))){
                                        hadir=true;
                                        break;
                                    }
                                    if(i+1==pengsarray.length() && !hadir){
                                        items.add(total.get(j));
                                    }
                                }
                            }

                            if(items.size()==0){
                                recyclerView.setVisibility(View.GONE);
                                tidakada.setVisibility(View.VISIBLE);
                                tidakada.setText("Semua Karyawan Hadir");
                            }
                            if(adapter!=null){
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                adapter = new AdapterListKaryawan(ActivityListKaryawan.this, items, ItemAnimation.BOTTOM_UP);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityListKaryawan.this));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(adapter);
                            }
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

    private class retrivelistkaryawan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        String urldata = generator.karyawanjabatanurl;
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
                            .add("date",settanggal)
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
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                if (result != null) {
                    try {
                        Log.e(TAG, "daftar absensi" + result.toString());
                        if(items!=null){
                            items.clear();
                        }
                        else{
                            items=new ArrayList<>();
                        }
                        JSONArray pengsarray = result.getJSONArray("data");
                        if(pengsarray.length()==0){
                            tidakada.setVisibility(View.VISIBLE);
                            tidakada.setText("Tidak ada karyawan dengan jabatan ("+jabatan+") yang hadir");
                        }
                        else {
                            tidakada.setVisibility(View.GONE);
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                kar = new listkaryawan();
                                kar.setIskar(obj.getString("id"));
                                kar.setNama(obj.getString("nama"));
                                kar.setJabatan(jabatan);
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                } else {
                                    kar.setImagelink("");
                                }
                                items.add(kar);
                            }
                        }
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            adapter=new AdapterListKaryawan(ActivityListKaryawan.this,items, ItemAnimation.BOTTOM_UP);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ActivityListKaryawan.this));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(adapter);
                        }

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
        getSupportActionBar().setTitle("Data Karyawan "+ getIntent().getStringExtra("keterangan"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        parent_view=findViewById(R.id.parent_view);
        recyclerView=findViewById(R.id.recyclerView);
        lyt_selectdate=findViewById(R.id.lyt_selectdate);
        searchView=(MaterialSearchView)findViewById(R.id.searchView);
        swipehome=findViewById(R.id.swipehome);

        dialog = new ProgressDialog(this);

        tanggal=findViewById(R.id.tanggal);
        tidakada=findViewById(R.id.tidakada);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        tanggalskrg=format.format(c.getTime());
        settanggal=tanggalskrg;

        c.add(Calendar.DATE,0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        tanggal.setText(formattedDate);
    }

    private void initListener(){
        lyt_selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityListKaryawan.this).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setretrive();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflate = LayoutInflater.from(ActivityListKaryawan.this);

                View linear = inflate.inflate(R.layout.calenderview,null);

                CalendarView calender = linear.findViewById(R.id.calenderviews);
                calender.setHeaderColor(R.color.red_500);
                //calender.setHeaderColor(ActivityListKaryawan.this.getResources().getColor(R.color.red_500));

                Calendar cal = Calendar.getInstance();
                /*
                if(getIntent().getStringExtra("keterangan").equals("absen") ||  getIntent().getStringExtra("keterangan").equals("telat")) {
                    calender.setMaximumDate(cal);
                }*/
                calender.setOnDayClickListener(new OnDayClickListener() {
                    @Override
                    public void onDayClick(EventDay eventDay) {

                        if(adapter!=null){
                            items.clear();
                            adapter.notifyDataSetChanged();
                        }
                        Calendar clickedDayCalendar = eventDay.getCalendar();
                        selecteddate = clickedDayCalendar.getTimeInMillis();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        settanggal=format.format(clickedDayCalendar.getTime());
                        tanggal.setText(format1.format(clickedDayCalendar.getTime()));

                    }
                });
                if(selecteddate!=0L){
                    cal.setTimeInMillis(selecteddate);
                    try {
                        calender.setDate(cal);
                    } catch (OutOfDateRangeException e) {
                        e.printStackTrace();
                    }
                }

                dialog.setView(linear);

                AlertDialog dial = dialog.show();

                dial.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                    }
                });
            }
        });

        swipehome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                snackBarWithActionIndefinite();
            }
        });
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
                if(adapter!=null) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //Do some magic
                if(adapter!=null){
                    adapter.getFilter().filter(query);
                }
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
    @Override
    public void onBackPressed() {

        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else{
            super.onBackPressed();
        }
    }
}
