package prima.optimasi.indonesia.payroll.main_owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedgajesti;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedgaji;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedgajicust;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSwipe_approval;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_penggajian;
import prima.optimasi.indonesia.payroll.objects.datagajiperiode;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class detail_dashboard_gaji extends AppCompatActivity {

    LinearLayout activatepengajian;
    TextView tidakada;
    TextView ttlgajiawal, ttlgajibersih, ttlpotongan;
    RecyclerView recyclerView;
    AdapterListSectionedgajicust mAdapter;
    AdapterListSectionedgajesti mAdapter1;
    datagajiperiode gaji;
    List<datagajiperiode> items;
    private View parent_view;
    ProgressDialog pd;
    MaterialSpinner spinnerdates;


    Double tempbpjs=0.0d,temptelat=0.0d,temptunjangan=0.0d,temptunlain=0.0d,tempreward=0.0d,temppunishment=0.0d,tempbersih=0.0d,temptotal=0.0d;

    String[] months = new String[]{"January","February","March","April","May","June","July"," August","September","October","November","December"};

    SparkButton proses;

    List<datagajiperiode> datagaji = new ArrayList<>();

    List<String> preview = new ArrayList<>();
    List<String> dt1 = new ArrayList<>();
    List<String> dt2 = new ArrayList<>();

    String dari="",sampai="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_pengajian_cust);

        pd = new ProgressDialog(this);

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH,-11);


        for(int i = 0; i< 12; i++) {

            preview.add(months[cal.get(Calendar.MONTH)]);
            Log.e("Month data",(cal.get(Calendar.MONTH)+1)+" / "+cal.get(Calendar.YEAR) );

            String dat1 = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-0"+1;
            String dat2 = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Log.e("datesdata",dat1+"  " +dat2 );
            dt1.add(dat1);
            dt2.add(dat2);
            cal.add(Calendar.MONTH, 1);
        }
        Collections.reverse(preview);
        Collections.reverse(dt1);
        Collections.reverse(dt2);

        dari = dt1.get(dt1.size()-1);
        sampai = dt2.get(dt2.size()-1);

        spinnerdates = (MaterialSpinner) findViewById(R.id.spinnerdate);
        spinnerdates.setItems(preview);
        spinnerdates.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                dari = dt1.get(position);
                sampai = dt2.get(position);
            }
        });

        pd.setTitle("Loading Data Penggajian");
        pd.setMessage("Please Wait...");
        pd.show();

        proses = findViewById(R.id.proceedselected);

        parent_view = findViewById(R.id.report_snackbar);
        activatepengajian = findViewById(R.id.activatepengajian);

        recyclerView=findViewById(R.id.recycler_report);
        ttlgajiawal = findViewById(R.id.ttlgajiawal);
        ttlgajibersih = findViewById(R.id.ttlgajibersih);
        ttlpotongan = findViewById(R.id.ttlpotongan);

        tidakada=findViewById(R.id.tidakada);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
        String msk=getIntent().getStringExtra("tanggal_masuk");
        String klr=getIntent().getStringExtra("tanggal_keluar");


        if(getIntent().getStringExtra("estimasi").equals("0")){

        }
        else {
            /*cal.add(Calendar.MONTH,1);
            dat1[0] = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-01";

            dat2[0] = fomat.format(new Date());*/
        }


        try{
            //
        }
        catch(Exception e){
            Log.e("DATE : ", ""+msk+klr);

        }




        final Long[] selecteddate1 = {0L};
        final Long[] selecteddate2 = {0L};

        /*dari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] tanggal = {""};
                tanggal[0]=fomat.format(new Date());
                AlertDialog.Builder dialog = new AlertDialog.Builder(detail_dashboard_gaji.this).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            dari.setText(format1.format(fomat.parse(tanggal[0])));
                            dat1[0] =tanggal[0];
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflate = LayoutInflater.from(detail_dashboard_gaji.this);

                View linear = inflate.inflate(R.layout.calenderview,null);

                CalendarView calender = linear.findViewById(R.id.calenderviews);
                calender.setHeaderColor(R.color.blue_500);

                //calender.setHeaderColor(getActivity().getResources().getColor(R.color.red_500));

                Calendar cal = Calendar.getInstance();

                calender.setOnDayClickListener(new OnDayClickListener() {
                    @Override
                    public void onDayClick(EventDay eventDay) {
                        Calendar clickedDayCalendar = eventDay.getCalendar();
                        selecteddate1[0] = clickedDayCalendar.getTimeInMillis();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                        tanggal[0] =format.format(clickedDayCalendar.getTime());
                        Log.e("Tanggal", tanggal[0]);
                    }
                });

                if(selecteddate1[0] !=0L){
                    cal.setTimeInMillis(selecteddate1[0]);
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
                dial.show();
            }
        });

        sampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] tanggal = {""};
                tanggal[0]=fomat.format(new Date());
                AlertDialog.Builder dialog = new AlertDialog.Builder(detail_dashboard_gaji.this).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            sampai.setText(format1.format(fomat.parse(tanggal[0])));
                            dat2[0] =tanggal[0];
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflate = LayoutInflater.from(detail_dashboard_gaji.this);

                View linear = inflate.inflate(R.layout.calenderview,null);

                CalendarView calender = linear.findViewById(R.id.calenderviews);
                calender.setHeaderColor(R.color.blue_500);

                //calender.setHeaderColor(getActivity().getResources().getColor(R.color.red_500));

                Calendar cal = Calendar.getInstance();

                calender.setOnDayClickListener(new OnDayClickListener() {
                    @Override
                    public void onDayClick(EventDay eventDay) {
                        Calendar clickedDayCalendar = eventDay.getCalendar();
                        selecteddate2[0] = clickedDayCalendar.getTimeInMillis();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                        tanggal[0] =format.format(clickedDayCalendar.getTime());
                        Log.e("Tanggal", tanggal[0]);
                    }
                });

                if(selecteddate2[0] !=0L){
                    cal.setTimeInMillis(selecteddate2[0]);
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
                dial.show();
            }
        });*/

        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pd.show();
                    proses.playAnimation();
                    proses.setChecked(true);
                    if(!pd.isShowing()){

                    }
                    if(getIntent().getStringExtra("estimasi").equals("0")){
                        retrivegaji gaji = new retrivegaji(detail_dashboard_gaji.this,pd, dari, sampai);
                        gaji.execute();
                    }
                    else {
                        retriveestimasi gaji = new retriveestimasi(detail_dashboard_gaji.this,pd, dari, sampai);
                        gaji.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        initToolbar();

        if(getIntent().getStringExtra("estimasi").equals("0")){
            retrivegaji gaji = new retrivegaji(this,pd, dari, sampai);
            gaji.execute();
        }
        else {
            retriveestimasi gaji = new retriveestimasi(this,pd, dari, sampai);
            gaji.execute();
        }



        if(getIntent().getStringExtra("estimasi").equals("0")){
            mAdapter = new AdapterListSectionedgajicust(detail_dashboard_gaji.this,datagaji);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter1 = new AdapterListSectionedgajesti(detail_dashboard_gaji.this,datagaji);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter1);
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        if(getIntent().getStringExtra("estimasi").equals("0")){
            getSupportActionBar().setTitle("Detail Penggajian");
        }
        else {
            getSupportActionBar().setTitle("Detail Estimasi");
        }


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
        String urldata = generator.dashboardpenggajiannurl;
        String dt1="";
        String dt2="";



        public retrivegaji(Context context, ProgressDialog pede, String dt1, String dt2)
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


                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

                String kodepay = "";

                Log.e(TAG, "datapenggajianlaporan" + result.toString());
                if (result != null) {

                    if(result.getString("status").equals("true")){
                        JSONArray arrydata = result.getJSONArray("data");

                        List<String> datapayroll = new ArrayList<>();
                        if(arrydata.length()==0){
                            Toast.makeText(detail_dashboard_gaji.this, "Tidak Ada Proses Penggajian pada Bulan Ini", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            String tanggal1 = "", tanggal2 = "";
                            tanggal1=dt1;
                            tanggal2=dt2;


                            tanggal1 = sdf.format(sdf1.parse(tanggal1));
                            tanggal2 = sdf.format(sdf1.parse(tanggal2));

                            for (int i = 0; i < arrydata.length(); i++) {
                                JSONObject obj = arrydata.getJSONObject(i);
                                if (datapayroll.size() == 0) {
                                    datapayroll.add(obj.getString("kode_payroll"));
                                } else {
                                    for (int j = 0; j < datapayroll.size(); j++) {
                                        if (datapayroll.get(j).equals(obj.getString("kode_payroll"))) {
                                            break;
                                        }
                                        if (datapayroll.size() - 1 == j) {
                                            datapayroll.add(obj.getString("kode_payroll"));
                                        }
                                    }
                                }
                            }

                            Double reward = 0.0d, punishment = 0.0d, bpjs = 0.0d, tunlain = 0.0d, tunjangan = 0.0d, telat = 0.0d, kotor = 0.0d, bersih = 0.0d, total = 0.0d,makan = 0.0d,transport = 0.0d;

                            for (int j = 0; j < datapayroll.size(); j++) {
                                for (int i = 0; i < arrydata.length(); i++) {
                                    JSONObject obj = arrydata.getJSONObject(i);

                                    if(i==arrydata.length()-1){
                                        reward = 0.0d ;
                                        punishment = 0.0d ;
                                        total = 0.0d ;
                                        makan = 0.0d ;
                                        transport = 0.0d ;
                                        bpjs = 0.0d ;
                                        tunjangan = 0.0d ;
                                        tunjangan = 0.0d ;
                                        reward = 0.0d ;
                                        reward = 0.0d ;
                                        reward = 0.0d ;
                                        reward = 0.0d ;
                                        reward = 0.0d ;
                                    }
                                }
                            }


                            datagajiperiode data = new datagajiperiode();
                            data.setTanggal1(tanggal1);
                            data.setTanggal2(tanggal2);
                            data.setIssection(false);
                            data.setTanggalgajian("");
                            data.setGajibersih(bersih);
                            data.setKeterangan("");
                            data.setReward(reward);
                            data.setPunishment(punishment);
                            data.setTotalgaji(total);
                            data.setPotongan(telat);
                            data.setBpjs(bpjs);
                            data.setTunlain(tunlain);
                            data.setTunjangan(tunjangan);
                            datagaji.add(data);

                            for (int j = 0; j < datapayroll.size(); j++) {

                            }
                        }



                        Log.e("listpenggajianpayroll",datapayroll.toString() );
                    }


                    /*if(result.getString("status").equals("true")){
                        JSONArray objects = result.getJSONArray("data");

                        String tanggal1 = "", tanggal2 = "";
                        tanggal1=dt1;
                        tanggal2=dt2;
                        Double reward = 0.0d, punishment = 0.0d, bpjs = 0.0d, tunlain = 0.0d, tunjangan = 0.0d, telat = 0.0d, kotor = 0.0d, bersih = 0.0d, total = 0.0d;

                        tanggal1 = sdf.format(sdf1.parse(tanggal1));
                        tanggal2 = sdf.format(sdf1.parse(tanggal2));

                        for (int i = 0; i < objects.length(); i++) {
                            JSONObject obj = objects.getJSONObject(i);

                            reward = reward + obj.getDouble("reward");
                            punishment = punishment + obj.getDouble("punishment");
                            total = total + obj.getDouble("gaji_total");

                            bersih = bersih + obj.getDouble("gaji_bersih");

                            bpjs = bpjs + obj.getDouble("potongan_bpjs");

                            tunjangan = tunjangan + obj.getDouble("tunjangan");

                            tunlain = tunlain + obj.getDouble("penggajian_tunlain");

                            telat = telat + obj.getDouble("potongan_telat");

                        }

                        datagajiperiode data = new datagajiperiode();
                        data.setTanggal1(tanggal1);
                        data.setTanggal2(tanggal2);
                        data.setIssection(false);
                        data.setTanggalgajian("");
                        data.setGajibersih(bersih);
                        data.setKeterangan("");
                        data.setReward(reward);
                        data.setPunishment(punishment);
                        data.setTotalgaji(total);
                        data.setPotongan(telat);
                        data.setBpjs(bpjs);
                        data.setTunlain(tunlain);
                        data.setTunjangan(tunjangan);
                        datagaji.add(data);

                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }

                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        if(proses.isChecked()){
                            proses.setChecked(false);
                        }
                    }
                    else {

                        String tanggal1 = "", tanggal2 = "";

                        tanggal1=dt1;
                        tanggal2=dt2;

                        Double reward = 0.0d, punishment = 0.0d, bpjs = 0.0d, tunlain = 0.0d, tunjangan = 0.0d, telat = 0.0d, kotor = 0.0d, bersih = 0.0d, total = 0.0d;

                        tanggal1 = sdf.format(sdf1.parse(tanggal1));
                        tanggal2 = sdf.format(sdf1.parse(tanggal2));

                        datagajiperiode data = new datagajiperiode();
                        data.setTanggal1(tanggal1);
                        data.setTanggal2(tanggal2);
                        data.setIssection(false);
                        data.setTanggalgajian("");
                        data.setGajibersih(bersih);
                        data.setKeterangan("");
                        data.setReward(reward);
                        data.setPunishment(punishment);
                        data.setTotalgaji(total);
                        data.setPotongan(telat);
                        data.setBpjs(bpjs);
                        data.setTunlain(tunlain);
                        data.setTunjangan(tunjangan);
                        datagaji.add(data);

                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }

                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }*/
                    if(proses.isChecked()){
                        proses.setChecked(false);
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

    private class retriveestimasi extends AsyncTask<Void, Integer, String>
    {
        String response="";
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.estimasidetaildashboard;
        String dt1="";
        String dt2="";



        public retriveestimasi(Context context, ProgressDialog pede, String dt1, String dt2)
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


                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

                String kodepay = "";

                Log.e(TAG, "datapenggajianlaporan" + result.toString());
                if (result != null) {

                    if(result.getString("status").equals("true")){
                        JSONArray objects = result.getJSONArray("data2");

                        String tanggal1 = "", tanggal2 = "";
                        tanggal1=dt1;
                        tanggal2=dt2;
                        Double reward = 0.0d, punishment = 0.0d, bpjs = 0.0d, tunlain = 0.0d, tunjangan = 0.0d, telat = 0.0d, kotor = 0.0d, bersih = 0.0d, total = 0.0d;

                        tanggal1 = sdf.format(sdf1.parse(tanggal1));
                        tanggal2 = sdf.format(sdf1.parse(tanggal2));

                        for (int i = 0; i < objects.length(); i++) {
                            JSONObject obj = objects.getJSONObject(i);

                            reward = reward + obj.getDouble("makan");

                            punishment = punishment + obj.getDouble("transport");
                            total = total + ((obj.getDouble("umk")/(26*480)) * obj.getDouble("token"));

                            bersih = bersih + ((obj.getDouble("umk")/(26*480)) * obj.getDouble("token")) + obj.getDouble("transport") +  obj.getDouble("makan") ;



                        }
                        telat = result.getDouble("totalPotonganTelat");

                        datagajiperiode data = new datagajiperiode();
                        data.setTanggal1(tanggal1);
                        data.setTanggal2(tanggal2);
                        data.setIssection(false);
                        data.setTanggalgajian("");
                        data.setGajibersih(bersih-telat+punishment+reward);
                        data.setKeterangan("");
                        data.setReward(reward);
                        data.setPunishment(punishment);
                        data.setTotalgaji(total);
                        data.setPotongan(telat);
                        data.setBpjs(bpjs);
                        data.setTunlain(tunlain);
                        data.setTunjangan(tunjangan);
                        datagaji.add(data);

                        if (mAdapter1 != null) {
                            mAdapter1.notifyDataSetChanged();
                        }

                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        if(proses.isChecked()){
                            proses.setChecked(false);
                        }
                    }
                    else {

                        String tanggal1 = "", tanggal2 = "";

                        tanggal1=dt1;
                        tanggal2=dt2;

                        Double reward = 0.0d, punishment = 0.0d, bpjs = 0.0d, tunlain = 0.0d, tunjangan = 0.0d, telat = 0.0d, kotor = 0.0d, total = 0.0d;

                        tanggal1 = sdf.format(sdf1.parse(tanggal1));
                        tanggal2 = sdf.format(sdf1.parse(tanggal2));

                        datagajiperiode data = new datagajiperiode();
                        data.setTanggal1(tanggal1);
                        data.setTanggal2(tanggal2);
                        data.setIssection(false);
                        data.setTanggalgajian("");
                        data.setGajibersih(kotor);
                        data.setKeterangan("");
                        data.setReward(reward);
                        data.setPunishment(0.0d);
                        data.setTotalgaji(total);
                        data.setPotongan(telat);
                        data.setBpjs(bpjs);
                        data.setTunlain(tunlain);
                        data.setTunjangan(tunjangan);
                        datagaji.add(data);

                        if (mAdapter1 != null) {
                            mAdapter1.notifyDataSetChanged();
                        }

                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                    }
                    if(proses.isChecked()){
                        proses.setChecked(false);
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

    private class hitungmanual extends AsyncTask<Void, Integer, String>
    {
        String response="";
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.estimasidetaildashboard;
        String dt1="";
        String dt2="";
        int position,last;
        String kode="";

        List<List<Double>> dataall;

        public hitungmanual(Context context, ProgressDialog pede, String dt1, String dt2,String kodekaryawan,List<List<Double>> dataall,int pos,int las)
        {
            kode = kodekaryawan;
            position = pos;
            last = las;

            this.dataall = dataall;

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
                            .add("kode",kode)
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


                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

                String kodepay = "";

                Log.e(TAG, "datapenggajianlaporan" + result.toString());
                if (result != null) {

                    if(result.getString("status").equals("true")) {

                    }
                    else{

                    }

                    if(position==last){

                    }

                    if(proses.isChecked()){
                        proses.setChecked(false);
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
