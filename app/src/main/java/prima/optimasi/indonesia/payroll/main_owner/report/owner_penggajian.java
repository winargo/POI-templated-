package prima.optimasi.indonesia.payroll.main_owner.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedgaji;
import prima.optimasi.indonesia.payroll.objects.datagajiobject;
import prima.optimasi.indonesia.payroll.objects.datagajiperiode;
import prima.optimasi.indonesia.payroll.objects.datalaporanpenggajian;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class owner_penggajian extends AppCompatActivity {

    LinearLayout activatepengajian;
    TextView dari, sampai, tidakada;
    TextView ttlgajiawal, ttlgajibersih, ttlpotongan;
    RecyclerView recyclerView;
    AdapterListSectionedgaji mAdapter;
    datagajiperiode gaji;
    List<datagajiperiode> items;
    private View parent_view;
    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_pengajian);
        parent_view = findViewById(R.id.report_snackbar);
        activatepengajian = findViewById(R.id.activatepengajian);

        recyclerView=findViewById(R.id.recycler_report);
        ttlgajiawal = findViewById(R.id.ttlgajiawal);
        ttlgajibersih = findViewById(R.id.ttlgajibersih);
        ttlpotongan = findViewById(R.id.ttlpotongan);

        dari=findViewById(R.id.dari);
        sampai=findViewById(R.id.sampai);
        tidakada=findViewById(R.id.tidakada);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
        String msk=getIntent().getStringExtra("tanggal_masuk");
        String klr=getIntent().getStringExtra("tanggal_keluar");
        try{
            dari.setText("From : "+format1.format(fomat.parse(msk)));
            sampai.setText("To : "+format1.format(fomat.parse(klr)));
        }
        catch(Exception e){
            Log.e("DATE : ", ""+msk+klr);

        }

        pd = new ProgressDialog(this);

        pd.setTitle("Loading Data Penggajian");
        pd.setMessage("Please Wait...");
        pd.show();
        initToolbar();
        retrivegaji gaji = new retrivegaji(this,pd,msk,klr);
        gaji.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Penggajian");
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

    private class retrivegaji extends AsyncTask<Void, Integer, String>
    {
        String response="";
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.laporanpengajianurl;
        String dt1="";
        String dt2="";



        public retrivegaji(Context context, ProgressDialog pede,String dt1,String dt2)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = pede;
            this.dt1 = dt1;
            this.dt2 = dt2;
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
                            .add("start",dt1)
                            .add("end",dt2)
                            .build();
                    Log.e("DATE : ", "" + dt1 + " " + dt2);

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
                List<datagajiperiode> datagaji = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

                String kodepay = "";

                Log.e(TAG, "datapenggajianlaporan" + result.toString());
                if (result != null) {
                    JSONArray objects = result.getJSONArray("data");
                    for(int i = 0 ; i < objects.length();i++){
                        JSONObject obj = objects.getJSONObject(i);

                        if(kodepay.equals("")){
                            datagajiperiode data = new datagajiperiode();
                            data.setIssection(true);
                            data.setKode(obj.getString("kode_payroll"));
                            datagaji.add(data);

                            data = new datagajiperiode();
                            data.setKode(obj.getString("kode_payroll"));
                            data.setIssection(false);
                            kodepay = obj.getString("kode_payroll");
                            data.setTanggalgajian(sdf.format(sdf1.parse(obj.getString("tanggal_proses").substring(0,10))));
                            data.setGajibersih(obj.getDouble("gaji_total"));

                            if(obj.getString("foto").equals("")){
                                data.setImageurl(generator.noimageurl);
                            }
                            else {
                                data.setImageurl(generator.profileurl+obj.getString("foto"));
                            }
                            data.setIssection(false);
                            data.setKeterangan("");
                            data.setReward(obj.getDouble("reward"));
                            data.setPunishment(obj.getDouble("punishment"));
                            data.setTotalgaji(obj.getDouble("gaji_bersih"));
                            data.setPotongan(obj.getDouble("potongan_telat"));
                            data.setJabatan(obj.getString("jabatan"));
                            data.setBpjs(obj.getDouble("potongan_bpjs"));
                            data.setTunlain(obj.getDouble("penggajian_tunlain"));
                            data.setTunjangan(obj.getDouble("tunjangan"));
                            data.setNama(obj.getString("nama"));
                            data.setTanggalgajian(sdf.format(sdf1.parse(obj.getString("tanggal_proses").substring(0,10))));
                            datagaji.add(data);
                        }
                        else if(kodepay.equals(obj.getString("kode_payroll"))) {
                            datagajiperiode data = new datagajiperiode();
                            data.setIssection(false);
                            data.setKode(obj.getString("kode_payroll"));
                            kodepay = obj.getString("kode_payroll");
                            data.setTanggalgajian(sdf.format(sdf1.parse(obj.getString("tanggal_proses").substring(0,10))));
                            data.setGajibersih(obj.getDouble("gaji_total"));

                            if(obj.getString("foto").equals("")){
                                data.setImageurl(generator.noimageurl);
                            }
                            else {
                                data.setImageurl(generator.profileurl+obj.getString("foto"));
                            }
                            data.setIssection(false);
                            data.setKeterangan("");
                            data.setReward(obj.getDouble("reward"));
                            data.setPunishment(obj.getDouble("punishment"));
                            data.setTotalgaji(obj.getDouble("gaji_bersih"));
                            data.setPotongan(obj.getDouble("potongan_telat"));
                            data.setJabatan(obj.getString("jabatan"));
                            data.setBpjs(obj.getDouble("potongan_bpjs"));
                            data.setTunlain(obj.getDouble("penggajian_tunlain"));
                            data.setTunjangan(obj.getDouble("tunjangan"));
                            data.setNama(obj.getString("nama"));
                            data.setTanggalgajian(sdf.format(sdf1.parse(obj.getString("tanggal_proses").substring(0,10))));
                            datagaji.add(data);
                        }
                        else {
                            datagajiperiode data = new datagajiperiode();
                            data.setKode(obj.getString("kode_payroll"));
                            data.setIssection(true);
                            datagaji.add(data);
                        }



                    }

                    mAdapter = new AdapterListSectionedgaji(owner_penggajian.this,datagaji);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);

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

    /*private class retrivegajimentahrecurse extends AsyncTask<Void, Integer, String >
    {
        String response = "";
        SharedPreferences prefs ;
        JSONObject result = null ;
        String urldata = generator.chartgajineedkode;
        int position,itempos;
        Double value = 0.0d;

        List<String> datacode;
        datalaporanpenggajian datad;
        List<datalaporanpenggajian> dataall;

        int[] count;
        int current=0;
        Double Valuechart = 0.0d;
        int last = 0;

        String dt1="";
        String dt2="";

        public retrivegajimentahrecurse(Context context, List<String> datakode,int position ,int itemposition,int lastp,List<datalaporanpenggajian> dataalls,Double valuchart,String dt1,String dt2)
        {
            this.dt1 = dt1;
            this.dt2 = dt2;

            Valuechart = valuchart;
            Log.e(TAG, "retrivegajibychartmentahrecurse: "+"started" );
            dataall = dataalls;
            current = itemposition;
            last = lastp;
            this.datacode = datakode;
            this.position = position;
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.itempos = itemposition;
        }

        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("start",dt1)
                            .add("end",dt2)
                            .add("kode",datacode.get(itempos))
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
                }
            } catch (IOException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
            } catch (Exception e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
            }
            return  response;
        }



        protected void onPostExecute(String result1) {

            try {

                if (result != null) {
                    Log.e(TAG, "String " + result.toString());
                    try {
                        if(result.getString("status").equals("true")){

                            //data1.add(obj.getDouble("tunjangan"));
                            //data1.add(obj.getDouble("punishment"));
                            //data1.add(obj.getDouble("bpjs"));
                            //data1.add(obj.getDouble("reward"));
                            //data1.add(obj.getDouble("potonganTelat"));
                            //data1.add(obj.getDouble("hariKerja"));
                            //data1.add(obj.getDouble("umk"));
                            //data1.add(obj.getDouble("gajiHari"));
                            //dataall.add(data1);
                            //datakode.add(obj.getString("kode"));



                            if(itempos==dataall.size()-1){
                                mAdapter = new AdapterListSectionedgaji(owner_penggajian.this, items, ItemAnimation.LEFT_RIGHT);

                                recyclerView.setLayoutManager(new GridLayoutManager(owner_penggajian.this, 1));
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(mAdapter);
                            }
                        }



                        //JSONArray pengsarray = result.getJSONArray("data");

                        //int gaji=1000000;

                        Log.e("gaji "+position,value+"" );
                        Log.e("current "+current+" "+ last,value+"" );

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }



                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi ", Snackbar.LENGTH_SHORT).show();
                }

            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    private class retrivegajimentah0 extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        SharedPreferences prefs ;
        Double value = 0.0d;
        JSONObject result = null ;
        String urldata = generator.getgajibytwodate;
        SimpleDateFormat sdfchart = new SimpleDateFormat("yyyy-MM-dd");
        int position;

        public retrivegajimentah0(Context context,int position)
        {
            Log.e(TAG, "retrivegajimentah0: "+"working" );
            this.position = position;
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
        }

        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    Log.e("gajimentah by chart",dt1.get(position)+" "+dt2.get(position) );

                    RequestBody body = new FormBody.Builder()
                            .add("start",dt1.get(position))
                            .add("end",dt2.get(position))
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
                }
            } catch (IOException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
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



        protected void onPostExecute(String result1) {
            try {
                Log.e(TAG, "retrive gaji mentah:"+result.toString() );
                if (result != null) {
                    try {
                        dialog.setMessage("Memuat Data Gaji ("+(position+1)+")");
                        if(result.getString("status").equals("true")){



                            JSONArray obj2 = result.getJSONArray("data");

                            Log.e("data "+position, result.toString() );
                            List<Double> data1 = new ArrayList<>();
                            List<String> datakode = new ArrayList<>();
                            List<List<Double>> dataall = new ArrayList<>();

                            for (int i=0 ; i<obj2.length();i++){
                                JSONObject obj = obj2.getJSONObject(i);
                                if(obj.getString("umk").equals("null")){

                                }
                                else{

                                    data1.add(obj.getDouble("tunjangan"));
                                    data1.add(obj.getDouble("punishment"));
                                    data1.add(obj.getDouble("bpjs"));
                                    data1.add(obj.getDouble("reward"));
                                    data1.add(obj.getDouble("potonganTelat"));
                                    data1.add(obj.getDouble("hariKerja"));
                                    data1.add(obj.getDouble("umk"));
                                    data1.add(obj.getDouble("gajiHari"));
                                    dataall.add(data1);
                                    datakode.add(obj.getString("kode"));
                                }

                            }

                            Double Valuechart = 0.0d;

                            DecimalFormat fomatter = new DecimalFormat("###,###,###.00");

                            dialog.setMessage("Memuat Data Gaji ("+(position+1)+"/12)\n");

                            retrivegajimentahrecurse gaji = new retrivegajimentahrecurse(getActivity(),data1,datakode,position,0,datakode.size()-1,dataall,Valuechart);
                            gaji.execute();


                            Log.e("data1dankode", data1.size()+" "+datakode.size()+" "+dataall.size());


                        }
                        else {
                            mRealm.beginTransaction();
                            datagajiobject score1 = new datagajiobject(value.floatValue(), (float) position, monthbarbottom.get(position));
                            //Log.e("replace", "/" + String.valueOf(thisYear) + " " + datesdata.get(finalI));
                            mRealm.copyToRealm(score1);
                            mRealm.commitTransaction();

                            barChart.invalidate();
                            barChart.getAxisLeft().setDrawGridLines(false);
                            barChart.getXAxis().setDrawGridLines(false);
                            barChart.setExtraBottomOffset(5f);

                            barChart.getXAxis().setLabelCount(7);
                            barChart.getXAxis().setGranularity(1f);

                            // no description text
                            barChart.getDescription().setEnabled(false);

                            // enable touch gestures
                            barChart.setTouchEnabled(true);

                            if (barChart instanceof BarLineChartBase) {

                                BarLineChartBase mChart = (BarLineChartBase) barChart;

                                mChart.setDrawGridBackground(false);

                                // enable scaling and dragging
                                mChart.setDragEnabled(true);
                                mChart.setScaleEnabled(true);

                                // if disabled, scaling can be done on x- and y-axis separately
                                mChart.setPinchZoom(false);

                                YAxis leftAxis = mChart.getAxisLeft();
                                leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                                leftAxis.setTextSize(8f);
                                leftAxis.setTextColor(Color.BLACK);

                                XAxis xAxis = mChart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setTextSize(8f);
                                xAxis.setTextColor(Color.BLACK);

                                mChart.getAxisRight().setEnabled(false);
                            }

                            RealmResults<datagajiobject> results = mRealm.where(datagajiobject.class).findAll();

                            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return monthbarbottom.get((int) value);
                                }
                            };
                            barChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());

                            barChart.getXAxis().setValueFormatter(formatter);

                            // BAR-CHART
                            RealmBarDataSet<datagajiobject> barDataSet = new RealmBarDataSet<datagajiobject>(results, "ranges", "datagaji");

                            barDataSet.setColor(Color.BLUE);

                            //barDataSet.setColor(generator.green);

                            barDataSet.setLabel("Period Gaji : " + finalPerios);

                            ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
                            barDataSets.add(barDataSet);

                            BarData barData = new BarData(barDataSets);

                            barChart.setData(barData);
                            barChart.setFitBars(true);
                            barChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);

                            barChart.invalidate();

                            Log.e("chartmonthposition",position+"" );

                            if(position<12) {
                                retrivegajimentah0 gajim = new retrivegajimentah0(getActivity(), position + 1);
                                gajim.execute();
                            }
                            else {
                                mRealm.close();
                                if(dialog.isShowing()){
                                    dialog.dismiss();
                                }
                            }

                        }
                        Log.e("data kode",result.toString() );

                        //JSONArray pengsarray = result.getJSONArray("data");

                        //int gaji=1000000;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }



                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi ", Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }
        }
    }*/


}
