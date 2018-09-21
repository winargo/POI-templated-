package prima.optimasi.indonesia.payroll.main_owner.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectioned;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class owner_cuti extends AppCompatActivity {

    private View parent_view;

    ProgressDialog pd;

    private RecyclerView recyclerView;
    private AdapterListSectioned mAdapter;

    List<datacuti> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        recyclerView = findViewById(R.id.recycler_report);

        pd = new ProgressDialog(this);

        pd.setTitle("Loading Data Cuti");
        pd.setMessage("Please Wait...");
        pd.show();

        initToolbar();

        parent_view = findViewById(R.id.report_snackbar);

        retrivecuti cuti = new retrivecuti(this,pd);
        cuti.execute();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Cuti");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private class retrivecuti extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.cutiurl;
        String passeddata = "" ;

        public retrivecuti(Context context,ProgressDialog pede)
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
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                                if(!tempcall.equals(obj.getString("tanggal").substring(0,10))){
                                    if(tempcall.equals("")){
                                        datacuti kar = new datacuti();
                                        kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                        kar.setIssection(true);
                                        tempcall = obj.getString("tanggal").substring(0,10);
                                        items.add(kar);
                                    }
                                    else{
                                        datacuti kar = new datacuti();
                                        kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                        kar.setIssection(true);
                                        tempcall = obj.getString("tanggal").substring(0,10);
                                        items.add(kar);
                                    }
                                }
                                    datacuti kar = new datacuti();
                                    kar.setIssection(false);
                                    //kar.setIskar(obj.getString("id"));
                                    kar.setImageurl(generator.profileurl+obj.getString("foto"));

                                    Log.e(TAG, "image data" + kar.getImageurl() );

                                    kar.setNama(obj.getString("nama"));
                                    kar.setJabatan(obj.getString("jabatan"));
                                    kar.setImageurl(generator.profileurl+obj.getString("foto"));

                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                                    kar.setTglmulai(format2.format(format1.parse(obj.getString("mulai_berlaku").substring(0,10))));
                                    kar.setTglakhir(format2.format(format1.parse(obj.getString("exp_date").substring(0,10))));
                                    kar.setKeterangan(obj.getString("keterangans"));
                                    kar.setHari(obj.getString("lama_cuti"));
                                    kar.setStatus(obj.getString("status"));
                                    //kar.setJabatan(obj.getString("jabatan"));
                                    items.add(kar);
                            /*int sect_count = 0;
                            int sect_idx = 0;
                            List<String> months = DataGenerator.getStringsMonth(getActivity());
                            for (int i = 0; i < items.size() / 6; i++) {
                                items.add(sect_count, new People(months.get(sect_idx), true));
                                sect_count = sect_count + 5;
                                sect_idx++;
                            }*/

                        }

                        mAdapter = new AdapterListSectioned(owner_cuti.this, items, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(owner_cuti.this, 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(mAdapter);

                        // on item list clicked
                        mAdapter.setOnItemClickListener(new AdapterListSectioned.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, datacuti obj, int position) {
                                Snackbar.make(parent_view, "Item " + obj.getNama() + " clicked", Snackbar.LENGTH_SHORT).show();
                            }
                        });


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

