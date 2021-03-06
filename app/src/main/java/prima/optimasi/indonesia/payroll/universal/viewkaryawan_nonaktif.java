package prima.optimasi.indonesia.payroll.universal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterGridCaller;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class viewkaryawan_nonaktif extends AppCompatActivity {

    CoordinatorLayout parent_view;
    private SwipeRefreshLayout refreshkaryawan;
    private RecyclerView recyclerViewkaryawan;
    private AdapterGridCaller mAdapterkaryawan;
    MaterialSearchView searchView;
    List<listkaryawan> itemskaryawan;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        initToolbar();
        initComponent();
    }

    private void snackBarWithActionIndefinite() {
        if(generator.checkInternet(viewkaryawan_nonaktif.this)) {
            if(snackbar!=null) {
                snackbar.dismiss();
            }
            if(itemskaryawan!=null){
                itemskaryawan.clear();
            }
            else{
                itemskaryawan= new ArrayList<>();
            }
            if(mAdapterkaryawan!=null){
                mAdapterkaryawan.notifyDataSetChanged();
            }
            retrivekaryawanrefersh ref = new retrivekaryawanrefersh(viewkaryawan_nonaktif.this);
            ref.execute();
        }
        else {
            if(refreshkaryawan.isRefreshing()){
                refreshkaryawan.setRefreshing(false);
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
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Karyawan Non Aktif");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        refreshkaryawan = findViewById(R.id.swipekaryawan);
        parent_view= findViewById(R.id.employeecoordinator);

        recyclerViewkaryawan = (RecyclerView) findViewById(R.id.recyclerView_karyawan);
        recyclerViewkaryawan.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewkaryawan.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 3), true));
        recyclerViewkaryawan.setHasFixedSize(true);
        recyclerViewkaryawan.setNestedScrollingEnabled(false);
        searchView=findViewById(R.id.searchView);

        refreshkaryawan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                snackBarWithActionIndefinite();
            }
        });
        snackBarWithActionIndefinite();

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
        String urldata = generator.listemployeeurl;

        public retrivekaryawan(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
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
                e.printStackTrace();
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
                        itemskaryawan = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if (!obj.getString("status_kerja").equals("aktif")) {
                                if (obj.getString("otoritas").equals("1")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Karyawan");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else if (obj.getString("otoritas").equals("2")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Kepala Bagian");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else if (obj.getString("otoritas").equals("3")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("HRD");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());

                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Karyawan");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                }
                            }
                        }
                        mAdapterkaryawan = new AdapterGridCaller(viewkaryawan_nonaktif.this, itemskaryawan,ItemAnimation.FADE_IN);
                        recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                        generator.adapterkar=mAdapterkaryawan;

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

    public class retrivekaryawanrefersh extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.listemployeeurl;

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
            //this.dialog.setMessage("Getting Data...");
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
                        if(itemskaryawan==null){
                            itemskaryawan  = new ArrayList<>();
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if (!obj.getString("status_kerja").equals("aktif")) {
                                if (obj.getString("otoritas").equals("1")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Karyawan");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else if (obj.getString("otoritas").equals("2")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Kepala Bagian");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else if (obj.getString("otoritas").equals("3")) {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("HRD");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                } else {
                                    listkaryawan kar = new listkaryawan();
                                    kar.setSection(false);
                                    kar.setJabatan("Karyawan");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    } else {
                                        kar.setImagelink("");
                                    }
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                    kar.setNama(obj.getString("nama"));
                                    kar.setDesc(obj.getString("jabatan"));
                                    kar.setStatus(obj.getString("status_kerja"));
                                    itemskaryawan.add(kar);
                                }
                            }
                        }

                        if(mAdapterkaryawan!=null){
                            mAdapterkaryawan.notifyDataSetChanged();
                        }
                        else{
                            mAdapterkaryawan = new AdapterGridCaller(viewkaryawan_nonaktif.this, itemskaryawan,ItemAnimation.FADE_IN);
                            recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                            generator.adapterkar=mAdapterkaryawan;
                        }
                        refreshkaryawan.setRefreshing(false);
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
                if(mAdapterkaryawan!=null) {
                    mAdapterkaryawan.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //Do some magic
                if(mAdapterkaryawan!=null) {
                    mAdapterkaryawan.getFilter().filter(query);
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
