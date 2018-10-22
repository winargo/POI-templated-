package prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.adapter.AdapterListSectioned;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterGridCaller;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.Adapterabsensiaktifitas;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawanaktivitas;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentEmployee extends Fragment {

    private View parent_view;

    private SwipeRefreshLayout refreshkabag;
    private SwipeRefreshLayout refreshkaryawan;

    private RecyclerView recyclerViewkaryawan;

    private AdapterGridCaller mAdapterkaryawan;

    CoordinatorLayout employeecoordinator;
    BottomNavigationView bottomnac;

    TextView selectdate;

    List<listkaryawan> itemskaryawan;
    List<listkaryawan> itemskabag;
    List<listkaryawanaktivitas> itemaktifitas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_employee, container, false);

        parent_view = rootView.findViewById(R.id.employeecoordinator);

        refreshkabag = rootView.findViewById(R.id.swipekabag);
        refreshkaryawan = rootView.findViewById(R.id.swipekaryawan);
        employeecoordinator= rootView.findViewById(R.id.employeecoordinator);

        selectdate = rootView.findViewById(R.id.dateselection_karyawan);
        SimpleDateFormat fomat = new SimpleDateFormat("dd/MM/yyyy");


        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()).setPositiveButton("Select",null).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflate = LayoutInflater.from(getActivity());

                View linear = inflate.inflate(R.layout.calenderview,null);

                dialog.setView(linear);

                dialog.show();
            }
        });

        refreshkaryawan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrivekaryawanrefersh ref = new retrivekaryawanrefersh(getActivity());
                ref.execute();
            }
        });

        bottomnac = rootView.findViewById(R.id.navigation);
        bottomnac.setVisibility(View.GONE);
        refreshkabag.setVisibility(View.GONE);
        refreshkaryawan.setVisibility(View.VISIBLE);
        /*
        bottomnac.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_people:
                        refreshkaryawan.setVisibility(View.VISIBLE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.green_900));
                        return true;
                }
                return false;
            }
        });
        */
        initComponent(rootView);

        return rootView;
    }


    private void initComponent(View v) {
        recyclerViewkaryawan = (RecyclerView) v.findViewById(R.id.recyclerView_karyawan);
        recyclerViewkaryawan.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewkaryawan.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));
        recyclerViewkaryawan.setHasFixedSize(true);
        recyclerViewkaryawan.setNestedScrollingEnabled(false);

        retrivekaryawan karyawan = new retrivekaryawan(getActivity());
        karyawan.execute();

        /*int sect_count = 0;
        int sect_idx = 0;
        List<String> months = DataGenerator.getStringsMonth(getActivity());
        for (int i = 0; i < items.size() / 6; i++) {
            items.add(sect_count, new People(months.get(sect_idx), true));
            sect_count = sect_count + 5;
            sect_idx++;
        }*/

        //set data and list adapter

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
                        itemskabag = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        boolean status=result.getBoolean("status");
                        if(!status){
                            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View SubFragment=inflater.inflate(R.layout.fragment_no_item_search,employeecoordinator,false);
                            employeecoordinator.addView(SubFragment);
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

                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("Karyawan");
                                kar.setIskar(obj.getString("id"));
                                if(!obj.getString("foto").equals("")){
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }




                                kar.setNama(obj.getString("nama"));
                                kar.setDesc("Karyawan");
                                itemskaryawan.add(kar);

                                //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                                mAdapterkaryawan = new AdapterGridCaller(getActivity(), itemskaryawan, ItemAnimation.FADE_IN);
                                recyclerViewkaryawan.setAdapter(mAdapterkaryawan);

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
                        itemskabag = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        boolean status=result.getBoolean("status");
                        if(!status){
                            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View SubFragment=inflater.inflate(R.layout.fragment_no_item_search,employeecoordinator,false);
                            employeecoordinator.addView(SubFragment);
                        }
                        else {
                            JSONArray pengsarray = result.getJSONArray("data");
                            String tempcall = "";


                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);

                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("Karyawan");
                                kar.setIskar(obj.getString("id"));
                                if(!obj.getString("foto").equals("")){
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }



                                kar.setNama(obj.getString("nama"));
                                kar.setDesc("Karyawan");
                                itemskaryawan.add(kar);
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

}
