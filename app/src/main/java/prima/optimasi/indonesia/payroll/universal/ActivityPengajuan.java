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
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterhistorypengajuan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class ActivityPengajuan extends AppCompatActivity {
    CoordinatorLayout parent_view;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);
        parent_view=findViewById(R.id.parent_view);
        recyclerView=findViewById(R.id.recyclerView);

        keterangan=findViewById(R.id.keterangan);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);

        spinner.setItems("Pilih Pengajuan","Cuti", "Izin", "Dinas", "Sakit");
        //spinner.setText("Pengajuan");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
        //boolean klik=false;
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                /*
                if(!klik){
                    spinner.setItems("Cuti", "Izin", "Dinas", "Dirumahkan");
                }*/
            }

        });

        tglmasuk=findViewById(R.id.tglmasuk);
        tglkeluar=findViewById(R.id.tglkeluar);
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
        //tglkeluar.setText(getNextDate());
        //c.add(Calendar.DATE, 1);

        //formattedDate = df.format(c.getTime());

        //Log.v("NEXT DATE : ", formattedDate);

        send=findViewById(R.id.send_pengajuan);
        retrivegetcuti cuti=new retrivegetcuti(ActivityPengajuan.this);
        cuti.execute();
        initToolbar();
        initComponent();
    }

    private class retrivegetcuti extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuancutikodeurl;
        String passeddata = "" ;

        public retrivegetcuti(Context context)
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
                        items = new ArrayList<>();
                        JSONArray pengsarray = result.getJSONArray("rows");
                        Log.e(TAG, "data json result" + pengsarray.length());

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            String tgl_izin=obj.getString("tgl_izin").substring(0,10);
                            String akhir_izin=obj.getString("akhir_izin").substring(0,10);
                            String status=obj.getString("status");
                            String keterangan=obj.getString("keterangans");
                            ajukan=new listkaryawanpengajuan();
                            ajukan.setJenis("Cuti");
                            ajukan.setTanggal_masuk(tgl_izin);
                            ajukan.setTanggal_keluar(akhir_izin);
                            ajukan.setStatus(status);
                            if(keterangan.equals("")){
                                Log.e(TAG, "data json result" + "KOSONG");
                            }
                            else{
                                ajukan.setKeterangan(keterangan);
                            }
                            Log.e(TAG, "data json result" + keterangan);

                            items.add(ajukan);
                        }
                        pengajuan = new Adapterhistorypengajuan(ActivityPengajuan.this, items, ItemAnimation.LEFT_RIGHT);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityPengajuan.this));
                        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(ActivityPengajuan.this, 3), true));


                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(pengajuan);
                        /*
                        {"rows":[{"id_izin":24,"id_karyawan":7,"tanggal":"2018-10-30T00:00:00.000Z","keterangans":"izin","tgl_izin":"2018-12-31T00:00:00.000Z",
                        "akhir_izin":"2018-12-31T00:00:00.000Z","lama":"1","status":"Proses","id":7,"kode_karyawan":"EMP-07","idfp":"KRY0007","nama":"Sparno",
                        "alamat":"Jln.Surabaya","tempat_lahir":"Bogor","tgl_lahir":"1994-07-02T00:00:00.000Z","telepon":"000000000","no_wali":"0000000000",
                        "email":"modomodo@gmail.com","tgl_masuk":"2018-08-04T00:00:00.000Z","kelamin":"laki-laki","status_nikah":"Menikah","pendidikan":"Sarjana S1",
                        "wn":"Indonesia","agama":"Islam","shift":"ya","status_kerja":"aktif","ibu_kandung":"Meiling","suami_istri":"Lastri","tanggungan":3,"npwp":"001011101010",
                        "gaji":0,"rekening":"00010000","id_bank":1,"id_departemen":16,"id_jabatan":10,"id_grup":6,"id_golongan":25,"atas_nama":"Sparno",
                        "foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg","id_cabang":2,"start_date":"2018-10-08T00:00:00.000Z","expired_date":"2019-01-27T00:00:00.000Z","
                        jab_index":0,"kontrak":"ya","file_kontrak":"","otoritas":2,"periode_gaji":"2-Mingguan","qrcode_file":"4b267aa6e56888580342445702d212f3.png"},
                        {"id_izin":21,"id_karyawan":7,"tanggal":"2018-08-31T00:00:00.000Z","keterangans":"Terbang","tgl_izin":"2018-08-31T00:00:00.000Z",
                        "akhir_izin":"2018-08-31T00:00:00.000Z","lama":"1","status":"Diterima","id":7,"kode_karyawan":"EMP-07","idfp":"KRY0007","nama":"Sparno",
                        "alamat":"Jln.Surabaya","tempat_lahir":"Bogor","tgl_lahir":"1994-07-02T00:00:00.000Z","telepon":"000000000","no_wali":"0000000000",
                        "email":"modomodo@gmail.com","tgl_masuk":"2018-08-04T00:00:00.000Z","kelamin":"laki-laki","status_nikah":"Menikah","pendidikan":"Sarjana S1",
                        "wn":"Indonesia","agama":"Islam","shift":"ya","status_kerja":"aktif","ibu_kandung":"Meiling","suami_istri":"Lastri","tanggungan":3,
                        "npwp":"001011101010","gaji":0,"rekening":"00010000","id_bank":1,"id_departemen":16,"id_jabatan":10,"id_grup":6,"id_golongan":25,"atas_nama":"Sparno",
                        "foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg","id_cabang":2,"start_date":"2018-10-08T00:00:00.000Z","expired_date":"2019-01-27T00:00:00.000Z","jab_index":0,
                        "kontrak":"ya","file_kontrak":"","otoritas":2,"periode_gaji":"2-Mingguan","qrcode_file":"4b267aa6e56888580342445702d212f3.png"}],
                        "userData":{"nama":"Sparno","jabatan":"Kepala Grup A","iat":1540892686}}
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

    private class retrivegetizin extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajuanizinkodeurl;
        String passeddata = "" ;

        public retrivegetizin(Context context)
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
                        boolean status=result.getBoolean("status");
                        if(!status){
                            Snackbar.make(parent_view, "Gagal mengajukan pengajuan izin" , Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, "Pengajuan Izin berhasil" , Snackbar.LENGTH_SHORT).show();
                        }
                        /*
                        {"rows":[{"id_izin":24,"id_karyawan":7,"tanggal":"2018-10-30T00:00:00.000Z","keterangans":"izin","tgl_izin":"2018-12-31T00:00:00.000Z",
                        "akhir_izin":"2018-12-31T00:00:00.000Z","lama":"1","status":"Proses","id":7,"kode_karyawan":"EMP-07","idfp":"KRY0007","nama":"Sparno",
                        "alamat":"Jln.Surabaya","tempat_lahir":"Bogor","tgl_lahir":"1994-07-02T00:00:00.000Z","telepon":"000000000","no_wali":"0000000000",
                        "email":"modomodo@gmail.com","tgl_masuk":"2018-08-04T00:00:00.000Z","kelamin":"laki-laki","status_nikah":"Menikah","pendidikan":"Sarjana S1",
                        "wn":"Indonesia","agama":"Islam","shift":"ya","status_kerja":"aktif","ibu_kandung":"Meiling","suami_istri":"Lastri","tanggungan":3,"npwp":"001011101010",
                        "gaji":0,"rekening":"00010000","id_bank":1,"id_departemen":16,"id_jabatan":10,"id_grup":6,"id_golongan":25,"atas_nama":"Sparno",
                        "foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg","id_cabang":2,"start_date":"2018-10-08T00:00:00.000Z","expired_date":"2019-01-27T00:00:00.000Z","
                        jab_index":0,"kontrak":"ya","file_kontrak":"","otoritas":2,"periode_gaji":"2-Mingguan","qrcode_file":"4b267aa6e56888580342445702d212f3.png"},
                        {"id_izin":21,"id_karyawan":7,"tanggal":"2018-08-31T00:00:00.000Z","keterangans":"Terbang","tgl_izin":"2018-08-31T00:00:00.000Z",
                        "akhir_izin":"2018-08-31T00:00:00.000Z","lama":"1","status":"Diterima","id":7,"kode_karyawan":"EMP-07","idfp":"KRY0007","nama":"Sparno",
                        "alamat":"Jln.Surabaya","tempat_lahir":"Bogor","tgl_lahir":"1994-07-02T00:00:00.000Z","telepon":"000000000","no_wali":"0000000000",
                        "email":"modomodo@gmail.com","tgl_masuk":"2018-08-04T00:00:00.000Z","kelamin":"laki-laki","status_nikah":"Menikah","pendidikan":"Sarjana S1",
                        "wn":"Indonesia","agama":"Islam","shift":"ya","status_kerja":"aktif","ibu_kandung":"Meiling","suami_istri":"Lastri","tanggungan":3,
                        "npwp":"001011101010","gaji":0,"rekening":"00010000","id_bank":1,"id_departemen":16,"id_jabatan":10,"id_grup":6,"id_golongan":25,"atas_nama":"Sparno",
                        "foto":"17a490b3ab8e38e296e3b1b18a433eb9.jpg","id_cabang":2,"start_date":"2018-10-08T00:00:00.000Z","expired_date":"2019-01-27T00:00:00.000Z","jab_index":0,
                        "kontrak":"ya","file_kontrak":"","otoritas":2,"periode_gaji":"2-Mingguan","qrcode_file":"4b267aa6e56888580342445702d212f3.png"}],
                        "userData":{"nama":"Sparno","jabatan":"Kepala Grup A","iat":1540892686}}
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



    private void initComponent() {
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
                            final Dialog dialog = new Dialog(ActivityPengajuan.this);
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
                                    Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        }else{
                            Log.e("NEXT DATE : ", "Berhasil");
                            if(!keterangan.getText().toString().trim().equals("")){
                                if(spinner.getText().equals("Cuti")){
                                    retrivepengajuancuti cuti =new retrivepengajuancuti(ActivityPengajuan.this);
                                    cuti.execute();
                                }
                                else if(spinner.getText().equals("Izin")){
                                    retrivepengajuanizin izin =new retrivepengajuanizin(ActivityPengajuan.this);
                                    izin.execute();
                                }
                                else if(spinner.getText().equals("Dinas")){
                                    retrivepengajuandinas dinas=new retrivepengajuandinas(ActivityPengajuan.this);
                                    dinas.execute();
                                }
                                else if(spinner.getText().equals("Sakit")){
                                    retrivepengajuansakit sakit =new retrivepengajuansakit(ActivityPengajuan.this);
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_cuti",format.format(format1.parse(tanggal_masuk1)))
                            .add("akhir_cuti",format.format(format1.parse(tanggal_keluar1)))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())

                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(format1.parse(tanggal_masuk1))+" "+format.format(format1.parse(tanggal_keluar1))+" "+lama+" "+keterangan.getText().toString()+" " );

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
                        /*
                        "data":[{
                            "id":7, "kode_karyawan":"EMP-07", "idfp":"KRY0007", "nama":
                            "Sparno", "alamat":"Jln.Surabaya", "tempat_lahir":"Bogor",
                                    "tgl_lahir":"1994-07-02T00:00:00.000Z", "telepon":
                            "000000000", "no_wali":"0000000000", "email":"modomodo@gmail.com",
                                    "tgl_masuk":"2018-08-04T00:00:00.000Z", "kelamin":
                            "laki-laki", "status_nikah":"Menikah", "pendidikan":"Sarjana S1",
                                    "wn":"Indonesia", "agama":"Islam", "shift":
                            "ya", "status_kerja":"aktif", "ibu_kandung":"Meiling", "suami_istri":
                            "Lastri",
                                    "tanggungan":3, "npwp":"001011101010", "gaji":0, "rekening":
                            "00010000", "id_bank":1, "id_departemen":16, "id_jabatan":10,
                                    "id_grup":6, "id_golongan":25, "atas_nama":"Sparno", "foto":
                            "17a490b3ab8e38e296e3b1b18a433eb9.jpg", "id_cabang":2,
                                    "start_date":null, "expired_date":null, "jab_index":
                            0, "kontrak":"tidak", "file_kontrak":"", "otoritas":2,
                                    "periode_gaji":"2-Mingguan", "qrcode_file":
                            "4b267aa6e56888580342445702d212f3.png"
                        },
                        {
                            "id":8, "kode_karyawan":"EMP-08",
                                "idfp":"KRY0008", "nama":"Aston", "alamat":
                            "Jln.Melati1", "tempat_lahir":"Medan", "tgl_lahir":
                            "1992-07-02T00:00:00.000Z",
                                    "telepon":"000000000", "no_wali":"090909090909", "email":
                            "gagaga@gmail.com", "tgl_masuk":"2018-07-09T00:00:00.000Z",
                                "kelamin":"perempuan", "status_nikah":"Menikah", "pendidikan":
                            "Sarjana S3", "wn":"Indonesia", "agama":"Islam", "shift":"ya",
                                "status_kerja":"aktif", "ibu_kandung":"Lisa", "suami_istri":
                            "sadaa", "tanggungan":2, "npwp":"0000101010101", "gaji":1,
                                "rekening":"0101010101011", "id_bank":1, "id_departemen":
                            1, "id_jabatan":23, "id_grup":6, "id_golongan":60, "atas_nama":
                            "Aston",
                                    "foto":"abe6ae2097f676a4e7d7869a75139fb9.jpg", "id_cabang":
                            1, "start_date":null, "expired_date":null, "jab_index":0,
                                "kontrak":"tidak", "file_kontrak":"", "otoritas":
                            3, "periode_gaji":"Bulanan", "qrcode_file":""
                        },
                        {
                            "id":10, "kode_karyawan":"EMP-10",
                                "idfp":"KYR0010", "nama":"Sulastri Ningsih", "alamat":
                            "JLn.Bambu1", "tempat_lahir":"sadsad", "tgl_lahir":
                            "1993-12-15T00:00:00.000Z",
                                    "telepon":"01010101000", "no_wali":"01010000100", "email":
                            "asdsadasdllololol@gmail.com", "tgl_masuk":
                            "2018-07-03T00:00:00.000Z",
                                    "kelamin":"perempuan", "status_nikah":
                            "Menikah", "pendidikan":"Sarjana S2", "wn":"Indonesia", "agama":
                            "Islam", "shift":"ya",
                                "status_kerja":"aktif", "ibu_kandung":"sad", "suami_istri":
                            "asd", "tanggungan":1, "npwp":"2131232321", "gaji":0, "rekening":
                            "8282888828282",
                                    "id_bank":3, "id_departemen":1, "id_jabatan":23, "id_grup":
                            6, "id_golongan":25, "atas_nama":"Sulastri Ningsih",
                                "foto":"35b234cd6919cd1dabc2c97f0af16436.jpg", "id_cabang":
                            2, "start_date":null, "expired_date":null, "jab_index":0, "kontrak":
                            "tidak",
                                    "file_kontrak":"", "otoritas":1, "periode_gaji":
                            "Bulanan", "qrcode_file":"8a28fb7a5902cc42878dfcbca26bceec.png"
                        },
                        {
                            "id":28,
                                "kode_karyawan":"EMP-100", "idfp":"KRY0016", "nama":
                            "Queen", "alamat":"Jln.Jambu", "tempat_lahir":"Medan", "tgl_lahir":
                            "2018-09-04T00:00:00.000Z",
                                    "telepon":"0001010101", "no_wali":"0020020002020", "email":
                            "asdsadsadsakjhuguhj@gmail.com", "tgl_masuk":
                            "2018-09-27T00:00:00.000Z",
                                    "kelamin":"laki-laki", "status_nikah":
                            "Menikah", "pendidikan":"Diploma 1", "wn":"Indonesia", "agama":
                            "Islam", "shift":"ya", "status_kerja":"aktif",
                                "ibu_kandung":"Lastri", "suami_istri":"Suylaiman", "tanggungan":
                            1, "npwp":"009090909", "gaji":1000, "rekening":"80000008000101000",
                                "id_bank":3, "id_departemen":13, "id_jabatan":27, "id_grup":
                            6, "id_golongan":58, "atas_nama":"Queen", "foto":"", "id_cabang":
                            1, "start_date":null,
                                "expired_date":null, "jab_index":1, "kontrak":
                            "tidak", "file_kontrak":"", "otoritas":5, "periode_gaji":"Bulanan",
                                "qrcode_file":"00219b1037b0f71e2a32d8666cb4bee3.png"
                        }]
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_izin",format.format(format1.parse(tanggal_masuk1)))
                            .add("akhir_izin",format.format(format1.parse(tanggal_keluar1)))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(format1.parse(tanggal_masuk1))+" "+format.format(format1.parse(tanggal_keluar1))+" "+lama+" "+keterangan.getText().toString()+" " );

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
                            Snackbar.make(parent_view, "Gagal mengajukan pengajuan izin" , Snackbar.LENGTH_SHORT).show();
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_dinas",format.format(format1.parse(tanggal_masuk1)))
                            .add("akhir_dinas",format.format(format1.parse(tanggal_keluar1)))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(format1.parse(tanggal_masuk1))+" "+format.format(format1.parse(tanggal_keluar1))+" "+lama+" "+keterangan.getText().toString()+" " );

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
                            Snackbar.make(parent_view, "Gagal mengajukan pengajuan dinas" , Snackbar.LENGTH_SHORT).show();
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

                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

                    RequestBody body = new FormBody.Builder()
                            .add("id",prefs.getString("id", ""))
                            .add("mulai_sakit",format.format(format1.parse(tanggal_masuk1)))
                            .add("akhir_sakit",format.format(format1.parse(tanggal_keluar1)))
                            .add("lama", ""+lama)
                            .add("keterangan",keterangan.getText().toString().trim())




                            .build();

                    Log.e(TAG, prefs.getString("id", "")+" "+format.format(format1.parse(tanggal_masuk1))+" "+format.format(format1.parse(tanggal_keluar1))+" "+lama+" "+keterangan.getText().toString()+" " );

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
                            Snackbar.make(parent_view, "Gagal mengajukan pengajuan sakit" , Snackbar.LENGTH_SHORT).show();
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
