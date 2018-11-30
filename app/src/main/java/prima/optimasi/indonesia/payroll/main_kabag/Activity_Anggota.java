package prima.optimasi.indonesia.payroll.main_kabag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapterviewkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class Activity_Anggota extends AppCompatActivity {

    CoordinatorLayout parent_view;

    private SwipeRefreshLayout refreshkabag;
    private SwipeRefreshLayout refreshkaryawan;

    private RecyclerView recyclerViewkaryawan;

    private Adapterviewkaryawan mAdapterkaryawan;

    MaterialSearchView searchView;
    BottomNavigationView bottomnac;

    TextView selectdate;

    List<listkaryawan> itemskaryawan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        refreshkaryawan = findViewById(R.id.swipekaryawan);
        parent_view= findViewById(R.id.employeecoordinator);
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
        recyclerViewkaryawan = (RecyclerView) findViewById(R.id.recyclerView_karyawan);
        recyclerViewkaryawan.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewkaryawan.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 3), true));
        recyclerViewkaryawan.setHasFixedSize(true);
        recyclerViewkaryawan.setNestedScrollingEnabled(false);
        searchView=findViewById(R.id.searchView);

        /*
        EditText search=findViewById(R.id.search_text_karyawan);

        TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mAdapterkaryawan.getFilter().filter(s);
            }
        };
        search.addTextChangedListener(filterTextWatcher);*/
        /*
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        */
        refreshkaryawan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrivekaryawanrefersh ref = new retrivekaryawanrefersh(Activity_Anggota.this);
                ref.execute();
            }
        });

        retrivekaryawan karyawan = new retrivekaryawan(this);
        karyawan.execute();

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
        String passeddata = "" ;

        public retrivekaryawan(Context context)
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
                        itemskaryawan = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        boolean status=result.getBoolean("status");
                        if(!status){
                            LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View SubFragment=inflater.inflate(R.layout.fragment_no_item_search,parent_view,false);
                            parent_view.addView(SubFragment);
                        }
                        else {
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
                            JSONArray pengsarray = result.getJSONArray("data");

                            String tempcall = "";

                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);

                                if(!prefs.getString("kodekaryawan", "").equals(obj.getString("kode_karyawan"))) {
                                    if (obj.getString("otoritas").equals("2")) {

                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setSection(false);
                                        kar.setJabatan("Karyawan");
                                        kar.setIskar(obj.getString("id"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kar.getImagelink());
                                        }
                                        else{
                                            kar.setImagelink("");
                                            Log.e(TAG, "image data" + kar.getImagelink());
                                        }
                                        /*
                                        else{
                                            kar.setImagelink("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qlAqV8WwUWaqr0SDbTj5Ht9lQ");
                                            Log.e(TAG, "image data" + kar.getImagelink());
                                        }*/


                                        kar.setNama(obj.getString("nama"));
                                        kar.setDesc("Karyawan");
                                        itemskaryawan.add(kar);

                                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                                        mAdapterkaryawan = new Adapterviewkaryawan(Activity_Anggota.this, itemskaryawan, ItemAnimation.FADE_IN);
                                        recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
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

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivekaryawanrefersh extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kabaggrupkaryawanurl;
        String passeddata = "" ;

        public retrivekaryawanrefersh(Context context)
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
                        itemskaryawan = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        boolean status=result.getBoolean("status");
                        if(!status){
                            LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View SubFragment=inflater.inflate(R.layout.fragment_no_item_search,parent_view,false);
                            parent_view.addView(SubFragment);
                        }
                        else {
                            JSONArray pengsarray = result.getJSONArray("data");
                            String tempcall = "";


                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);

                                if(!prefs.getString("kodekaryawan", "").equals(obj.getString("kode_karyawan"))) {
                                    if (obj.getString("otoritas").equals("2") || obj.getString("otoritas").equals("3")) {
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setSection(false);
                                        kar.setJabatan("Karyawan");
                                        kar.setIskar(obj.getString("id"));
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kar.getImagelink());
                                        }
                                        else{
                                            kar.setImagelink("");
                                            Log.e(TAG, "image data" + kar.getImagelink());
                                        }


                                        kar.setNama(obj.getString("nama"));
                                        kar.setDesc("Karyawan");
                                        itemskaryawan.add(kar);
                                    }

                                }
                            }

                            refreshkaryawan.setRefreshing(false);
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
    //"data":[{"id_absensi":334,"kode":"EMP-4","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"08:36:16","break_out":null,"break_in":null,"keluar":null,
    // "ket_masuk":"Masuk ke Kabag Kerja Normal 2018-11-23 08:36:16","ket_keluar":"","jam_masuk":"08:30:00","telat":6,"durasi_break":100,"telat_break":0,"jam_keluar":null,
    // "pulang_cepat":0,"kerja":"Normal","shift":"REGULAR","token":0,"longitudecheckin":"98.7261364","latitudecheckin":"3.6129727","longitudebreakin":"","latitudebreakin":"",
    // "longitudebreakout":"","latitudebreakout":"","longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"Musafi'i","otoritas":1,
    // "foto":"cfac83658c20bf988d6fd29e39aa91a1.jpg","jabatan":"Programmer"},

    // {"id_absensi":329,"kode":"EMP-2","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7",
    // "masuk":"08:27:44","break_out":null,"break_in":null,"keluar":null,"ket_masuk":"Masuk ke Kabag Kerja Normal 2018-11-23 08:27:44","ket_keluar":"",
    // "jam_masuk":"08:30:00","telat":0,"durasi_break":100,"telat_break":0,"jam_keluar":null,"pulang_cepat":0,"kerja":"Normal","shift":"REGULAR","token":0,"
    // longitudecheckin":"98.726131","latitudecheckin":"3.6129336","longitudebreakin":"","latitudebreakin":"","longitudebreakout":"","latitudebreakout":"",
    // "longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"Herry Wibowo","otoritas":1,"foto":"0631fa1187b16d780e242bcdf6dd9c5d.png","jabatan":"Programmer"},

    // {"id_absensi":346,"kode":"EMP-9","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"00:00:00","break_out":null,"break_in":null,"keluar":"00:00:00",
    // "ket_masuk":"Sakit","ket_keluar":"Sakit","jam_masuk":"00:00:00","telat":0,"durasi_break":0,"telat_break":0,"jam_keluar":"00:00:00","pulang_cepat":0,"kerja":"Sakit",
    // "shift":"","token":0,"longitudecheckin":"","latitudecheckin":"","longitudebreakin":"","latitudebreakin":"","longitudebreakout":"","latitudebreakout":"",
    // "longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"DUM01","otoritas":4,"foto":"","jabatan":"Programmer"},

    // {"id_absensi":337,"kode":"EMP-9","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"00:00:00","break_out":null,"break_in":null,"keluar":"00:00:00",
    // "ket_masuk":"Sakit","ket_keluar":"Sakit","jam_masuk":"00:00:00","telat":0,"durasi_break":0,"telat_break":0,"jam_keluar":"00:00:00","pulang_cepat":0,"kerja":"Sakit",
    // "shift":"","token":0,"longitudecheckin":"","latitudecheckin":"","longitudebreakin":"","latitudebreakin":"","longitudebreakout":"","latitudebreakout":"",
    // "longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"DUM01","otoritas":4,"foto":"","jabatan":"Programmer"},
    // {"id_absensi":340,"kode":"EMP-9","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"00:00:00","break_out":null,"break_in":null,"keluar":"00:00:00","ket_masuk":"Sakit","ket_keluar":"Sakit","jam_masuk":"00:00:00","telat":0,"durasi_break":0,"telat_break":0,"jam_keluar":"00:00:00","pulang_cepat":0,"kerja":"Sakit","shift":"","token":0,"longitudecheckin":"","latitudecheckin":"","longitudebreakin":"","latitudebreakin":"","longitudebreakout":"","latitudebreakout":"","longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"DUM01","otoritas":4,"foto":"","jabatan":"Programmer"},{"id_absensi":341,"kode":"EMP-9","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"00:00:00","break_out":null,"break_in":null,"keluar":"00:00:00","ket_masuk":"Sakit","ket_keluar":"Sakit","jam_masuk":"00:00:00","telat":0,"durasi_break":0,"telat_break":0,"jam_keluar":"00:00:00","pulang_cepat":0,"kerja":"Sakit","shift":"","token":0,"longitudecheckin":"","latitudecheckin":"","longitudebreakin":"","latitudebreakin":"","longitudebreakout":"","latitudebreakout":"","longitudecheckout":"","latitudecheckout":"","lupa_keluar_kabag":0,"nama":"DUM01","otoritas":4,"foto":"","jabatan":"Programmer"},{"id_absensi":336,"kode":"EMP-9","tanggal":"2018-11-23T00:00:00.000Z","kabag":"EMP-7","masuk":"00:00:00","break_out":null,"break_in":null,"keluar":"00:00:00","ket_masuk":"Sakit","ket_keluar":"Sakit","jam_masuk":"00:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_mainmenu, menu);
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                mAdapterkaryawan.getFilter().filter(query);
                //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //Do some magic
                Log.e("Text", "newText=" + query);
                mAdapterkaryawan.getFilter().filter(query);
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
        } else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }
    }
}
