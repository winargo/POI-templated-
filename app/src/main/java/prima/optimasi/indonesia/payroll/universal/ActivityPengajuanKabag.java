package prima.optimasi.indonesia.payroll.universal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterhistorypengajuankabag;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class ActivityPengajuanKabag extends AppCompatActivity {
    SwipeRefreshLayout swipehome;
    CoordinatorLayout parent_view;
    Button send;
    MaterialSpinner spinner, spinnerkar;
    private SimpleDateFormat dateFormatter;
    TextView tglmasuk,tglkeluar;
    EditText keterangan;
    Date tanggal_masuk, tanggal_keluar;
    String tanggal_masuk1, tanggal_keluar1;
    RecyclerView recyclerView;
    Date tgl_masuk, tgl_keluar;
    int lama=0;
    Adapterhistorypengajuankabag pengajuan;
    List<listkaryawanpengajuan> items;
    listkaryawanpengajuan ajukan;
    List<listkaryawan> itemskaryawan;
    List<String> listid;
    List<String> listnama;
    listkaryawan kar;
    String[] kets={"Izin","Sakit","Cuti","Dinas"};
    ProgressDialog dialog;
    String idkar="";
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_kabag);
        initToolbar();
        initComponent();
        initListener();
        if(generator.checkInternet(ActivityPengajuanKabag.this)) {
            retrivekaryawan kar = new retrivekaryawan(ActivityPengajuanKabag.this, dialog);
            kar.execute();
        }
        else{
            snackBarWithActionIndefinite();
        }
    }

    private void snackBarWithActionIndefinite() {
        if(generator.checkInternet(ActivityPengajuanKabag.this)) {
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
            if(itemskaryawan!=null){
                itemskaryawan.clear();
            }
            else{
                itemskaryawan = new ArrayList<>();
            }
            if(listnama!=null){
                listnama.clear();
            }
            else{
                listnama = new ArrayList<>();
            }
            if(listid!=null){
                listid.clear();
            }
            else{
                listid = new ArrayList<>();
            }
            if(pengajuan!=null){
                pengajuan.notifyDataSetChanged();
            }
            spinner.setSelectedIndex(0);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE,0);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());
            tglmasuk.setText(formattedDate);
            tglkeluar.setText(formattedDate);
            keterangan.setText("");
            retrivekaryawan kar=new retrivekaryawan(ActivityPengajuanKabag.this,dialog);
            kar.execute();
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

    private class retrivekaryawan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kabaggrupkaryawanurl;

        public retrivekaryawan(Context context, ProgressDialog dialog)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.dialog=dialog;
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

                    RequestBody body = new FormBody.Builder()
                            .add("kode",prefs.getString("kodekaryawan", ""))
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

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view,result.getString("message"),Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            JSONArray pengsarray = result.getJSONArray("data");

                            String tempcall = "";

                            listnama.add("Pilih Karyawan");
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);

                                if(!prefs.getString("kodekaryawan", "").equals(obj.getString("kode_karyawan"))) {
                                    if (obj.getString("otoritas").equals("3")) {

                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setSection(false);
                                        kar.setJabatan("Karyawan");
                                        kar.setIskar(obj.getString("id"));
                                        listid.add(obj.getString("id"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        }
                                        else{
                                            kar.setImagelink("");
                                        }
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                        kar.setNama(obj.getString("nama"));
                                        listnama.add(obj.getString("nama"));
                                        kar.setDesc("Karyawan");
                                        itemskaryawan.add(kar);

                                    }
                                }
                            }
                            spinnerkar.setItems(listnama);
                            spinnerkar.setSelectedIndex(0);
                            if(!swipehome.isRefreshing()){
                                items = new ArrayList<>();
                                for (int i=0;i<kets.length;i++){
                                    for (int j=0;j<listid.size();j++){
                                        retrivegetketerangan ket=new retrivegetketerangan(ActivityPengajuanKabag.this, dialog, kets[i],listid.get(j));
                                        ket.execute();
                                    }
                                }
                            }
                            else{
                                for (int i=0;i<kets.length;i++){
                                    for (int j=0;j<listid.size();j++){
                                        retrivegetketeranganref ketref=new retrivegetketeranganref(ActivityPengajuanKabag.this, kets[i],listid.get(j));
                                        ketref.execute();
                                    }
                                }
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

    private class retrivegetketerangan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata ;
        String keterangan="";
        String id="";
        public retrivegetketerangan(Context context, ProgressDialog dialog, String keterangan, String id)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.dialog=dialog;
            this.keterangan=keterangan;
            this.id=id;
            if(keterangan.equals("Izin")){
                urldata= generator.pengajuanizinkodeurl;
            }
            else if(keterangan.equals("Sakit")){
                urldata= generator.pengajuansakitkodeurl;
            }
            else if(keterangan.equals("Cuti")){
                urldata= generator.pengajuancutikodeurl;
            }
            else if(keterangan.equals("Dinas")){
                urldata= generator.pengajuandinaskodeurl;
            }

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
                            .add("id",id)
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
                        Log.e(TAG, "data json result" + result.toString());

                        JSONArray pengsarray = result.getJSONArray("rows");
                        Log.e(TAG, "data json result" + pengsarray.length());

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            String mulai="", akhir="", status="";
                            if(keterangan.equals("Sakit")) {
                                mulai = obj.getString("tgl_sakit").substring(0, 10);
                                akhir = obj.getString("akhir_sakit").substring(0, 10);
                                status = "Diterima";
                            }
                            else if(keterangan.equals("Cuti")) {
                                mulai=obj.getString("mulai_berlaku").substring(0,10);
                                akhir=obj.getString("exp_date").substring(0,10);
                                status=obj.getString("status");
                            }
                            else if(keterangan.equals("Dinas")) {
                                mulai=obj.getString("tgl_dinas").substring(0,10);
                                akhir=obj.getString("akhir_dinas").substring(0,10);
                                status=obj.getString("status");
                            }
                            else if(keterangan.equals("Izin")) {
                                mulai=obj.getString("tgl_izin").substring(0,10);
                                akhir=obj.getString("akhir_izin").substring(0,10);
                                status=obj.getString("status");
                            }

                            String keterangans = obj.getString("keterangans");

                            ajukan = new listkaryawanpengajuan();
                            ajukan.setJenis(keterangan);
                            ajukan.setNama(obj.getString("nama"));
                            ajukan.setTanggal_masuk(mulai);
                            ajukan.setTanggal_keluar(akhir);
                            if(status.equals("Ditolak")){
                                ajukan.setStatus("Rejected");
                            }
                            else if(status.equals("Diterima")){
                                ajukan.setStatus("Approved");
                            }
                            else{
                                ajukan.setStatus("Pending");
                            }
                            if (keterangans.equals("")) {
                                ajukan.setKeterangan("-");
                            } else {
                                ajukan.setKeterangan(keterangans);
                            }

                            items.add(ajukan);
                        }

                        if(items.size()>0 && keterangan.equals("Dinas")){
                            Collections.sort(items, new Comparator<listkaryawanpengajuan>() {
                                @Override
                                public int compare(listkaryawanpengajuan listkaryawanpengajuan, listkaryawanpengajuan t1) {
                                    if(listkaryawanpengajuan.getTanggal_masuk().compareTo(t1.getTanggal_masuk())==0){
                                        if(listkaryawanpengajuan.getTanggal_keluar().compareTo(t1.getTanggal_keluar())==0){
                                            if(listkaryawanpengajuan.getJenis().compareTo(t1.getJenis())==0){
                                                return -listkaryawanpengajuan.getNama().compareTo(t1.getNama());
                                            }
                                            return -listkaryawanpengajuan.getJenis().compareTo(t1.getJenis());
                                        }
                                        return -listkaryawanpengajuan.getTanggal_keluar().compareTo(t1.getTanggal_keluar());
                                    }
                                    return -listkaryawanpengajuan.getTanggal_masuk().compareTo(t1.getTanggal_masuk());

                                }
                            });
                            pengajuan = new Adapterhistorypengajuankabag(ActivityPengajuanKabag.this, items, ItemAnimation.LEFT_RIGHT);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ActivityPengajuanKabag.this));
                            //recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(ActivityPengajuanKabag.this, 3), true));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(pengajuan);
                            if (this.dialog.isShowing()) {
                                dialog.dismiss();
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
            } catch (Exception E) {
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: " + E.getMessage().toString());
                Snackbar.make(parent_view, E.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
            }




            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivegetketeranganref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        //ProgressDialog dialog ;
        String urldata ;
        String keterangan="";
        String id="";

        public retrivegetketeranganref(Context context, String keterangan, String id)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
            this.id=id;
            if(keterangan.equals("Izin")){
                urldata= generator.pengajuanizinkodeurl;
            }
            else if(keterangan.equals("Sakit")){
                urldata= generator.pengajuansakitkodeurl;
            }
            else if(keterangan.equals("Cuti")){
                urldata= generator.pengajuancutikodeurl;
            }
            else if(keterangan.equals("Dinas")){
                urldata= generator.pengajuandinaskodeurl;
            }

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
                            .add("id",id)
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
                        Log.e(TAG, "data json result" + result.toString());

                        JSONArray pengsarray = result.getJSONArray("rows");
                        Log.e(TAG, "data json result" + pengsarray.length());

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            String mulai="", akhir="", status="";
                            if(keterangan.equals("Sakit")) {
                                mulai = obj.getString("tgl_sakit").substring(0, 10);
                                akhir = obj.getString("akhir_sakit").substring(0, 10);
                                status = "Diterima";
                            }
                            else if(keterangan.equals("Cuti")) {
                                mulai=obj.getString("mulai_berlaku").substring(0,10);
                                akhir=obj.getString("exp_date").substring(0,10);
                                status=obj.getString("status");
                            }
                            else if(keterangan.equals("Dinas")) {
                                mulai=obj.getString("tgl_dinas").substring(0,10);
                                akhir=obj.getString("akhir_dinas").substring(0,10);
                                status=obj.getString("status");
                            }
                            else if(keterangan.equals("Izin")) {
                                mulai=obj.getString("tgl_izin").substring(0,10);
                                akhir=obj.getString("akhir_izin").substring(0,10);
                                status=obj.getString("status");
                            }

                            String keterangans = obj.getString("keterangans");
                            ajukan = new listkaryawanpengajuan();
                            ajukan.setJenis(keterangan);
                            ajukan.setTanggal_masuk(mulai);
                            ajukan.setTanggal_keluar(akhir);
                            if(status.equals("Ditolak")){
                                ajukan.setStatus("Rejected");
                            }
                            else if(status.equals("Diterima")){
                                ajukan.setStatus("Approved");
                            }
                            else{
                                ajukan.setStatus("Pending");
                            }

                            if (keterangans.equals("")) {
                                ajukan.setKeterangan("-");
                            } else {
                                ajukan.setKeterangan(keterangans);
                            }

                            items.add(ajukan);
                        }

                        if(keterangan.equals("Dinas")){
                            Collections.sort(items, new Comparator<listkaryawanpengajuan>() {
                                @Override
                                public int compare(listkaryawanpengajuan listkaryawanpengajuan, listkaryawanpengajuan t1) {
                                    if(listkaryawanpengajuan.getTanggal_masuk().compareTo(t1.getTanggal_masuk())==0){
                                        if(listkaryawanpengajuan.getTanggal_keluar().compareTo(t1.getTanggal_keluar())==0){
                                            if(listkaryawanpengajuan.getJenis().compareTo(t1.getJenis())==0){
                                                return -listkaryawanpengajuan.getNama().compareTo(t1.getNama());
                                            }
                                            return -listkaryawanpengajuan.getJenis().compareTo(t1.getJenis());
                                        }
                                        return -listkaryawanpengajuan.getTanggal_keluar().compareTo(t1.getTanggal_keluar());
                                    }
                                    return -listkaryawanpengajuan.getTanggal_masuk().compareTo(t1.getTanggal_masuk());

                                }
                            });
                            if(pengajuan!=null){
                                pengajuan.notifyDataSetChanged();
                            }
                            else{
                                pengajuan = new Adapterhistorypengajuankabag(ActivityPengajuanKabag.this, items, ItemAnimation.LEFT_RIGHT);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ActivityPengajuanKabag.this));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(pengajuan);
                            }
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            swipehome.setRefreshing(false);

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
            } catch (Exception E) {
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: " + E.getMessage().toString());
                Snackbar.make(parent_view, E.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
            }

            /*
            if (this.dialog.isShowing()) {
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private void initComponent() {
        parent_view=findViewById(R.id.parent_view);
        recyclerView=findViewById(R.id.recyclerView);
        swipehome=findViewById(R.id.swipehome);
        keterangan=findViewById(R.id.keterangan);

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Pilih Pengajuan","Cuti", "Izin", "Dinas", "Sakit");

        spinnerkar = (MaterialSpinner) findViewById(R.id.spinnerkar);
        dialog=new ProgressDialog(ActivityPengajuanKabag.this);

        tglmasuk=findViewById(R.id.tglmasuk);
        tglkeluar=findViewById(R.id.tglkeluar);
        //tglmasuk.setText(getCurrentDate());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        tglmasuk.setText(formattedDate);
        tglkeluar.setText(formattedDate);
        tanggal_masuk=c.getTime();
        tanggal_keluar=c.getTime();
        tanggal_masuk1=formattedDate;
        tanggal_keluar1=formattedDate;

        send=findViewById(R.id.send_pengajuan);
        itemskaryawan = new ArrayList<>();
        listnama=new ArrayList<>();
        listid=new ArrayList<>();
    }

    private void initListener(){
        tglmasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglmasuk,"masuk");
            }
        });
        tglkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglkeluar,"keluar");
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    tgl_masuk = df.parse(tglmasuk.getText().toString());
                    tgl_keluar = df.parse(tglkeluar.getText().toString());
                    lama = tgl_masuk.compareTo(tgl_keluar) + 1;
                    if(!spinnerkar.getText().equals("Pilih Karyawan")) {
                        if (!spinner.getText().equals("Pilih Pengajuan")) {
                            if (keterangan.getText().toString().trim().equals("")) {
                                Snackbar.make(parent_view, "Keterangan Tidak Boleh Kosong", Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (tgl_masuk.compareTo(tgl_keluar) > 0) {
                                    final Dialog dialog = new Dialog(ActivityPengajuanKabag.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                    dialog.setContentView(R.layout.dialog_warning);
                                    dialog.setCancelable(true);

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    ImageView icon = dialog.findViewById(R.id.icon);
                                    TextView title = dialog.findViewById(R.id.title);
                                    TextView content = dialog.findViewById(R.id.content);
                                    icon.setVisibility(View.GONE);
                                    title.setText("Kesalahan Data Tanggal");
                                    content.setText("Tanggal keluar wajib lebih atau sama dari tanggal masuk yang diinput");
                                    ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);
                                } else {
                                    for(int i=0;i<itemskaryawan.size();i++){
                                        if(itemskaryawan.get(i).getNama().equals(spinnerkar.getText().toString())){
                                            idkar=itemskaryawan.get(i).getIskar();
                                        }
                                    }
                                    if(!idkar.equals("")) {
                                        if(generator.checkInternet(ActivityPengajuanKabag.this)) {
                                            if (spinner.getText().equals("Cuti")) {
                                                retrivepengajuancuti cuti = new retrivepengajuancuti(ActivityPengajuanKabag.this);
                                                cuti.execute();
                                            } else if (spinner.getText().equals("Izin")) {
                                                retrivepengajuanizin izin = new retrivepengajuanizin(ActivityPengajuanKabag.this);
                                                izin.execute();
                                            } else if (spinner.getText().equals("Dinas")) {
                                                retrivepengajuandinas dinas = new retrivepengajuandinas(ActivityPengajuanKabag.this);
                                                dinas.execute();
                                            } else if (spinner.getText().equals("Sakit")) {
                                                retrivepengajuansakit sakit = new retrivepengajuansakit(ActivityPengajuanKabag.this);
                                                sakit.execute();
                                            }
                                        }
                                        else{
                                            Toast.makeText(ActivityPengajuanKabag.this, R.string.no_connection,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Log.e("retrivekaryawan", "bermasalah");
                                    }
                                }
                            }
                        } else if (spinner.getText().equals("Pilih Pengajuan")) {
                            Snackbar.make(parent_view, "Pilih Jenis Pengajuan", Snackbar.LENGTH_SHORT).show();
                        }
                    }else if (spinner.getText().equals("Pilih Karyawan")) {
                        Snackbar.make(parent_view, "Pilih Karyawan", Snackbar.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("NEXT DATE : ", "" + tanggal_masuk.compareTo(tanggal_keluar));
                }
            }
        });

        swipehome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                snackBarWithActionIndefinite();
            }
        });
    }

    private class retrivepengajuancuti extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuancutiurl;

        public retrivepengajuancuti(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",idkar)
                            .add("mulai_cuti",format.format(tgl_masuk))
                            .add("akhir_cuti",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())
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
                //
                if (result != null) {
                    try {
                        Log.e(TAG, "data json result" + result.toString());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Cuti berhasil" , Snackbar.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 2000);
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

    private class retrivepengajuanizin extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuanizinurl;

        public retrivepengajuanizin(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",idkar)
                            .add("mulai_izin",format.format(tgl_masuk))
                            .add("akhir_izin",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())
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
                        Log.e(TAG, "data json result" + result.toString());
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Izin berhasil" , Snackbar.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 2000);
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

    private class retrivepengajuandinas extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuandinasurl;

        public retrivepengajuandinas(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",idkar)
                            .add("mulai_dinas",format.format(tgl_masuk))
                            .add("akhir_dinas",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())
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
                        Log.e(TAG, "data json result" + result.toString());
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Dinas berhasil" , Snackbar.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 2000);
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

    private class retrivepengajuansakit extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuansakiturl;

        public retrivepengajuansakit(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",idkar)
                            .add("mulai_sakit",format.format(tgl_masuk))
                            .add("akhir_sakit",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())
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
                        Log.e(TAG, "data json result" + result.toString());
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Sakit berhasil" , Snackbar.LENGTH_SHORT).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, 2000);
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

    private void tanggal_masuk(Date tanggal){
        tanggal_masuk=tanggal;
    }
    private void tanggal_keluar(Date tanggal){
        tanggal_keluar=tanggal;
    }
    private void tanggal_masuk1(String tanggal){
        tanggal_masuk1=tanggal;
    }
    private void tanggal_keluar1(String tanggal){
        tanggal_keluar1=tanggal;
    }
    private void dialogDatePickerDark(final TextView tv, String jenis) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        //long date_ship_millis = calendar.getTimeInMillis();
                        //tv.setText(Tools.getFormattedDateSimple(date_ship_millis));
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        if(jenis.equals("masuk")){
                            tanggal_masuk(calendar.getTime());
                            tanggal_masuk1(df.format(calendar.getTime()));
                        }
                        else{
                            tanggal_keluar(calendar.getTime());
                            tanggal_keluar1(df.format(calendar.getTime()));
                        }
                        Log.e("NEXT DATE : ", calendar.getTime().toString());
                        tv.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);

                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark theme
        datePicker.setThemeDark(true);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
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
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item=menu.findItem(R.id.action_settings);
        item.setTitle("About");
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
        return super.onOptionsItemSelected(item);
    }
}
