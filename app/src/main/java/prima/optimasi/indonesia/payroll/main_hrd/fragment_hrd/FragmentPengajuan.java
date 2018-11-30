package prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
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
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_kabag.Activity_Anggota;
import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapterviewkaryawan;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedKontrakKerja;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterhistorypengajuan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentPengajuan extends Fragment {
    SwipeRefreshLayout swipehome;
    CoordinatorLayout parent_view;
    NestedScrollView nsv;
    Button send;
    MaterialSpinner spinner;
    private SimpleDateFormat dateFormatter;
    TextView tglmasuk,tglkeluar;
    EditText keterangan;
    Date tanggal_masuk, tanggal_keluar;
    String tanggal_masuk1, tanggal_keluar1;
    RecyclerView recyclerView;
    Date tgl_masuk, tgl_keluar;
    int lama=0;
    Adapterhistorypengajuan pengajuan;
    List<listkaryawanpengajuan> items;
    listkaryawanpengajuan ajukan;
    String[] kets={"Izin","Sakit","Cuti","Dinas"};
    ProgressDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_pengajuan, container, false);
        initComponent(rootView);
        initListener();
        for (int i=0;i<kets.length;i++){
            retrivegetketerangan ket=new retrivegetketerangan(getActivity(), kets[i]);
            ket.execute();
        }
        return rootView;
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
        String passeddata = "" ;
        String keterangan="";

        public retrivegetketerangan(Context context, String keterangan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
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
                            .add("id",prefs.getString("id", ""))

                            .build();

                    Log.e(TAG, prefs.getString("id", ""));

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
                                Log.e(TAG, "data json result" + "KOSONG");
                            } else {
                                ajukan.setKeterangan(keterangans);
                            }
                            Log.e(TAG, "data json result" + keterangans);

                            items.add(ajukan);
                        }

                        if(items.size()>0 && keterangan.equals("Dinas")){
                            pengajuan = new Adapterhistorypengajuan(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(pengajuan);
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

            if (this.dialog.isShowing()) {
                dialog.dismiss();
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
        String passeddata = "" ;
        String keterangan="";

        public retrivegetketeranganref(Context context, String keterangan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
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
                            .add("id",prefs.getString("id", ""))

                            .build();

                    Log.e(TAG, prefs.getString("id", ""));

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
                //
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
                                Log.e(TAG, "data json result" + "KOSONG");
                            } else {
                                ajukan.setKeterangan(keterangans);
                            }
                            Log.e(TAG, "data json result" + keterangans);

                            items.add(ajukan);
                        }

                        if(keterangan.equals("Dinas")){

                            if(pengajuan!=null){
                                pengajuan.notifyDataSetChanged();
                            }
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            swipehome.setRefreshing(false);
                            /*
                            pengajuan = new Adapterhistorypengajuan(ActivityPengajuan.this, items, ItemAnimation.LEFT_RIGHT);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ActivityPengajuan.this));
                            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(ActivityPengajuan.this, 3), true));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(pengajuan);*/
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

    private void initComponent(View rootView) {
        parent_view=rootView.findViewById(R.id.parent_view);
        nsv=rootView.findViewById(R.id.nsv);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        keterangan=rootView.findViewById(R.id.keterangan);
        swipehome=rootView.findViewById(R.id.swipehome);
        spinner = (MaterialSpinner) rootView.findViewById(R.id.spinner);

        spinner.setItems("Pilih Pengajuan","Cuti", "Izin", "Dinas", "Sakit");

        nsv.setPadding(0,0,0,0);
        tglmasuk=rootView.findViewById(R.id.tglmasuk);
        tglkeluar=rootView.findViewById(R.id.tglkeluar);
        //tglmasuk.setText(getCurrentDate());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        tglmasuk.setText(formattedDate);
        tglkeluar.setText(formattedDate);
        tanggal_masuk=c.getTime();
        tanggal_keluar=c.getTime();
        tanggal_masuk1=formattedDate;
        tanggal_keluar1=formattedDate;

        send=rootView.findViewById(R.id.send_pengajuan);
        items = new ArrayList<>();
    }

    private void initListener(){
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            //boolean klik=false;
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }

        });

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
                Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                try{
                    tgl_masuk=df.parse(tanggal_masuk1);
                    tgl_keluar=df.parse(tanggal_keluar1);
                    lama=tgl_masuk.compareTo(tgl_keluar)+1;
                    if(!spinner.getText().equals("Pilih Pengajuan")){
                        if(tgl_masuk.compareTo(tgl_keluar)>0) {
                            final Dialog dialog = new Dialog(getActivity());
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
                                    Toast.makeText(getActivity(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        }else{
                            Log.e("NEXT DATE : ", "Berhasil");
                            if(!keterangan.getText().toString().trim().equals("")){
                                if(spinner.getText().equals("Cuti")){
                                    retrivepengajuancuti cuti =new retrivepengajuancuti(getActivity());
                                    cuti.execute();
                                }
                                else if(spinner.getText().equals("Izin")){
                                    retrivepengajuanizin izin =new retrivepengajuanizin(getActivity());
                                    izin.execute();
                                }
                                else if(spinner.getText().equals("Dinas")){
                                    retrivepengajuandinas dinas=new retrivepengajuandinas(getActivity());
                                    dinas.execute();
                                }
                                else if(spinner.getText().equals("Sakit")){
                                    retrivepengajuansakit sakit =new retrivepengajuansakit(getActivity());
                                    sakit.execute();
                                }

                            }
                            else{
                                Log.e("NEXT DATE : ", "Isi Keterangan Anda");
                            }

                        }
                    }

                    else if(spinner.getText().equals("Pilih Pengajuan")){
                        Log.e("SPINNER : ", "Isi Jenis Pengajuan Anda");
                    }

                }catch(Exception e){
                    Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                    Log.e("NEXT DATE : ", ""+tanggal_masuk1+" "+tanggal_keluar1);
                }

            }
        });

        swipehome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading ...");
                dialog.show();
                if(items!=null){
                    items.clear();
                }
                else{
                    items=new ArrayList<>();
                }
                spinner.setSelectedIndex(0);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,0);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                tglmasuk.setText(formattedDate);
                tglkeluar.setText(formattedDate);
                keterangan.setText("");
                if(pengajuan!=null){
                    pengajuan.notifyDataSetChanged();
                }
                for (int i=0;i<kets.length;i++) {
                    retrivegetketeranganref ketref = new retrivegetketeranganref(getActivity(), kets[i]);
                    ketref.execute();
                }
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
        String passeddata = "" ;

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
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_cuti",format.format(tgl_masuk))
                            .add("akhir_cuti",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())

                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(tgl_masuk)+" "+format.format(tgl_keluar)+" "+lama+" "+keterangan.getText().toString()+" " );

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
                        //items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Cuti berhasil" , Snackbar.LENGTH_SHORT).show();
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
        String passeddata = "" ;

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
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_izin",format.format(tgl_masuk))
                            .add("akhir_izin",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(tgl_masuk)+" "+format.format(tgl_keluar)+" "+lama+" "+keterangan.getText().toString()+" " );

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
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Izin berhasil" , Snackbar.LENGTH_SHORT).show();
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
        String passeddata = "" ;

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
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_dinas",format.format(tgl_masuk))
                            .add("akhir_dinas",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(tgl_masuk)+" "+format.format(tgl_keluar)+" "+lama+" "+keterangan.getText().toString()+" " );

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
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Dinas berhasil" , Snackbar.LENGTH_SHORT).show();
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
        String passeddata = "" ;

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
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_sakit",format.format(tgl_masuk))
                            .add("akhir_sakit",format.format(tgl_keluar))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(tgl_masuk)+" "+format.format(tgl_keluar)+" "+lama+" "+keterangan.getText().toString()+" " );

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
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, result.getString("message") , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Sakit berhasil" , Snackbar.LENGTH_SHORT).show();
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
        /*
        String tanggal_pengajuan=tglmasuk.getText().toString();
        String tgl_pengajuan=tanggal_pengajuan.substring(0,2);
        String bln_pengajuan=tanggal_pengajuan.substring(3,5);
        String thn_pengajuan=tanggal_pengajuan.substring(6,10);

        cur_calender.set(Calendar.YEAR, Integer.parseInt(thn_pengajuan));
        cur_calender.set(Calendar.MONTH,Integer.parseInt(bln_pengajuan)-1);
        cur_calender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tgl_pengajuan));
        */
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
        datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }
}
