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
import android.support.v7.widget.GridLayoutManager;
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
        retrivegaji gaji = new retrivegaji(this,pd);
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
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.laporanpengajianurl;
        String passeddata = "" ;

        public retrivegaji(Context context, ProgressDialog pede)
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
                        if(result.getString("status").equals("true")) {

                            JSONArray pengsarray = result.getJSONArray("data");

                            String tempcall = "";
                            Double gajiawal = 0.0d, gajibersih = 0.0d, potongan = 0.0d;

                            activatepengajian.setVisibility(View.VISIBLE);


                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                            /*
                            if(!tempcall.equals(obj.getString("tanggal_proses").substring(0,10))){
                                if(tempcall.equals("")){
                                    gaji = new datagajiperiode();
                                    gaji.setDatatgl(obj.getString("tanggal_proses").substring(0,10));
                                    gaji.setIssection(true);
                                    tempcall = obj.getString("tanggal_proses").substring(0,10);
                                    items.add(gaji);
                                }
                                else{
                                    gaji = new datagajiperiode();
                                    gaji.setDatatgl(obj.getString("tanggal_proses").substring(0,10));
                                    gaji.setIssection(true);
                                    tempcall = obj.getString("tanggal_proses").substring(0,10);
                                    items.add(gaji);
                                }
                            }*/
                                gaji = new datagajiperiode();
                                gaji.setIssection(false);
                                //kar.setIskar(obj.getString("id"));
                            /*
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kar.getImageurl() );*/

                                gajiawal = gajiawal + Double.parseDouble(obj.getString("gaji_total"));
                                gajibersih = gajibersih + Double.parseDouble(obj.getString("gaji_bersih"));
                                potongan = potongan + Double.parseDouble(obj.getString("potongan_telat")) + Double.parseDouble(obj.getString("potongan_pinjaman"));

                                Integer pinjaman = Integer.parseInt(obj.getString("potongan_telat")) + Integer.parseInt(obj.getString("potongan_pinjaman"));
                                gaji.setNama(obj.getString("nama"));
                                gaji.setGajibersih(obj.getString("gaji_bersih"));
                                gaji.setTotalgaji(obj.getString("gaji_total"));
                                gaji.setPotongan("" + pinjaman);
                                gaji.setJabatan(obj.getString("jabatan"));
                                gaji.setTanggalgajian(obj.getString("tanggal_proses"));

                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                                items.add(gaji);
                            }

                            DecimalFormat formatter = new DecimalFormat("###,###,###");

                            ttlgajiawal.setText("Rp" + formatter.format(gajiawal));
                            ttlgajibersih.setText("Rp" + formatter.format(gajibersih));
                            ttlpotongan.setText("Rp" + formatter.format(potongan));

                            mAdapter = new AdapterListSectionedgaji(owner_penggajian.this, items, ItemAnimation.LEFT_RIGHT);

                            recyclerView.setLayoutManager(new GridLayoutManager(owner_penggajian.this, 1));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(mAdapter);

                            //"data":[{"id_penggajian":14,"kode_payroll":"PAYROLL-1","id_karyawan":3,"tanggal_proses":"2018-10-31T00:00:00.000Z",
                            // "start_date":null,"end_date":"2018-10-31T00:00:00.000Z","shift":"tidak","periode_penggajian":"Bulanan","hari_kerja":27,"reward":100000,
                            // "punishment":100000,"token":120,"gaji_total":0,"gaji_extra":0,"gaji_bersih":-9945,"potongan_telat":0,"potongan_pinjaman":0,"keterangan":"",
                            // "create_at":"2018-10-31T10:53:44.000Z","id":3,"kode_karyawan":"EMP-2","idfp":"KRY0002","nama":"Tes12","alamat":"asdasddasd","tempat_lahir":"tetetettettetetete",
                            // "tgl_lahir":"0000-00-00","telepon":"1231321321321","no_wali":"123213123","email":"12312@sad.asd","tgl_masuk":"2018-08-29T00:00:00.000Z","kelamin":"perempuan",
                            // "status_nikah":"Menikah","pendidikan":"SMP","wn":"Indonesia","agama":"Protestan","status_kerja":"non_aktif","ibu_kandung":"asdsad","suami_istri":"assdaadsd",
                            // "tanggungan":0,"npwp":"1232132131","gaji":96153,"rekening":"1231223112313213","id_bank":6,"id_departemen":15,"id_jabatan":26,"id_grup":8,"id_golongan":15,
                            // "atas_nama":"tes12","foto":"","id_cabang":2,"expired_date":null,"jab_index":0,"kontrak":"tidak","file_kontrak":"","otoritas":3,"periode_gaji":"Bulanan",
                            // "qrcode_file":"3a5db9d0649c4a3668d4c942b7209e89.png","golongan":"GOL-06","kehadiran":230000,"makan":4400,"transport":4400,"thr":0,"tundip":230000,
                            // "tunjangan_lain":0,"absen":2,"bpjs":2,"pot_lain":0,"lembur":2,"umk":2720000,"lembur_tetap":0},

                            // {"id_penggajian":15,"kode_payroll":"PAYROLL-1","id_karyawan":4,
                            // "tanggal_proses":"2018-10-31T00:00:00.000Z","start_date":null,"end_date":"2018-10-31T00:00:00.000Z","shift":"tidak","periode_penggajian":"Bulanan",
                            // "hari_kerja":27,"reward":0,"punishment":0,"token":480,"gaji_total":100001,"gaji_extra":0,"gaji_bersih":629075,"potongan_telat":10000,"potongan_pinjaman":0,
                            // "keterangan":"-","create_at":"2018-10-31T10:53:44.000Z","id":4,"kode_karyawan":"EMP-04","idfp":"KYR0003","nama":"Sutrina Sudjipto1","alamat":"Jln.Melati No.77",
                            // "tempat_lahir":"Medan","tgl_lahir":"1992-08-01T00:00:00.000Z","telepon":"000000000","no_wali":"000000000000","email":"gagaga@gmail.com",
                            // "tgl_masuk":"2018-08-01T00:00:00.000Z","kelamin":"perempuan","status_nikah":"Menikah","pendidikan":"Diploma 3","wn":"Indonesia","agama":"Katholik",
                            // "status_kerja":"non_aktif","ibu_kandung":"adssdasa","suami_istri":"daasda","tanggungan":123132133,"npwp":"1232132113","gaji":100001,"rekening":"213131321",
                            // "id_bank":1,"id_departemen":15,"id_jabatan":30,"id_grup":9,"id_golongan":28,"atas_nama":"Sutrina Sudjipto1","foto":"","id_cabang":1,"expired_date":null,
                            // "jab_index":0,"kontrak":"tidak","file_kontrak":"","otoritas":1,"periode_gaji":"1-Mingguan","qrcode_file":"feb25f128304d004ccdec1f20d9cfb6a.png",
                            // "golongan":"GOL-20","kehadiran":10000,"makan":10000,"transport":10000,"thr":10000,"tundip":10000,"tunjangan_lain":10000,"absen":2,"bpjs":1,"pot_lain":4,
                            // "lembur":2,"umk":2500000,"lembur_tetap":0},

                            // {"id_penggajian":16,"kode_payroll":"PAYROLL-1","id_karyawan":8,"tanggal_proses":"2018-10-31T00:00:00.000Z",
                            // "start_date":null,"end_date":"2018-10-31T00:00:00.000Z","shift":"ya","periode_penggajian":"Bulanan","hari_kerja":27,"reward":0,"punishment":0,"token":1,
                            // "gaji_total":0,"gaji_extra":0,"gaji_bersih":-10000,"potongan_telat":0,"potongan_pinjaman":0,"keterangan":"-","create_at":"2018-10-31T10:53:44.000Z",
                            // "id":8,"kode_karyawan":"EMP-08","idfp":"KRY0008","nama":"Aston","alamat":"Jln.Melati1","tempat_lahir":"Medan","tgl_lahir":"1992-07-02T00:00:00.000Z",
                            // "telepon":"000000000","no_wali":"090909090909","email":"gagaga@gmail.com","tgl_masuk":"2018-07-09T00:00:00.000Z","kelamin":"perempuan","status_nikah":"Menikah",
                            // "pendidikan":"Sarjana S3","wn":"Indonesia","agama":"Islam","status_kerja":"aktif","ibu_kandung":"Lisa","suami_istri":"sadaa","tanggungan":2,
                            // "npwp":"0000101010101","gaji":100001,"rekening":"0101010101011","id_bank":1,"id_departemen":11,"id_jabatan":13,"id_grup":8,"id_golongan":28,"atas_nama":"Aston",
                            // "foto":"abe6ae2097f676a4e7d7869a75139fb9.jpg","id_cabang":2,"expired_date":null,"jab_index":0,"kontrak"

                        }
                        else{
                            tidakada.setVisibility(View.VISIBLE);
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
/*
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
    }

    private class retrivegajimentahrecurse extends AsyncTask<Void, Integer, String >
    {
        String response = "";
        SharedPreferences prefs ;
        JSONObject result = null ;
        String urldata = generator.chartgajineedkode;
        int position,itempos;
        Double value = 0.0d;

        List<String> datacode;
        List<Double> datad;
        List<List<Double>> dataall;

        int[] count;
        int current=0;
        Double Valuechart = 0.0d;
        int last = 0;

        public retrivegajimentahrecurse(Context context,List<Double> datad , List<String> datakode,int position ,int itemposition,int lastp,List<List<Double>> dataalls,Double valuchart)
        {
            Valuechart = valuchart;
            Log.e(TAG, "retrivegajibychartmentahrecurse: "+"started" );
            dataall = dataalls;
            current = itemposition;
            last = lastp;
            this.datad = datad;
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

                    Log.e("gajibychart "+position,dt1.get(position)+" "+dt2.get(position) + " data kode "+ datacode.get(itempos) );

                    RequestBody body = new FormBody.Builder()
                            .add("start",dt1.get(position))
                            .add("end",dt2.get(position))
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

                            Log.e(TAG, "dataallvalues "+dataall.get(current).get(0)+" "+ dataall.get(current).get(1)+ " "+ dataall.get(current).get(2)+ " " + dataall.get(current).get(3)+" "+ dataall.get(current).get(4)+" "+dataall.get(current).get(6) +" "+ dataall.get(current).get(5)+" "+ result.getDouble("data"));

                            if(result.getDouble("data")!=0){
                                JSONArray arrays = result.getJSONArray("data1");
                                JSONObject obj = arrays.getJSONObject(0);
                                //Valuechart = Valuechart +((dataall.get(current).get(6)/ dataall.get(current).get(5) * result.getDouble("data")) + dataall.get(current).get(0)-dataall.get(current).get(1)-dataall.get(current).get(2)+dataall.get(current).get(3)-dataall.get(current).get(4));
                                Valuechart = Valuechart +((dataall.get(current).get(7) * result.getDouble("data")) + dataall.get(current).get(0)-dataall.get(current).get(1)-dataall.get(current).get(2)+dataall.get(current).get(3)-dataall.get(current).get(4));
                            }

                            DecimalFormat fomatter = new DecimalFormat("###,###,###.00");


                            Log.e("valuecalculate",Valuechart.toString() );

                            Log.e("Currentorlast",current+" "+last);

                            if(current<last){
                                retrivegajimentahrecurse gaji = new retrivegajimentahrecurse(getActivity(),datad,datacode,position,current+1,datacode.size()-1,dataall,Valuechart);
                                gaji.execute();
                            }
                            else {
                                mRealm.beginTransaction();
                                datagajiobject score1 = new datagajiobject(Valuechart.floatValue(), (float) position, monthbarbottom.get(position));
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

                            //mRealm.close();

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

                if(monthbarbottom.size()==position){
                    if(dialog!=null){
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }
        }

    }*/
}
