package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.Activity_Chart;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListDaftarAbsensi;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedKontrakKerja;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedsakit;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_sakit;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.objects.datagajiobject;
import prima.optimasi.indonesia.payroll.objects.listkaryawandaftarabsensi;
import prima.optimasi.indonesia.payroll.objects.listkaryawankontrakkerja;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityListKaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentHome extends Fragment {

    String tanggal="";
    private SwipeRefreshLayout refreshhome;
    View lyt_totalizin, lyt_totalsakit,lyt_totalcuti, lyt_totalabsen, lyt_totaltelat,lyt_totaldinas;
    TextView totalizin, totalsakit, totalcuti, totalabsen, totaltelat, totaldinas, totalgajibersih, totalgajiestimasi, totalgajipotongan;
    ImageButton expand, expand2, expand3, expandhabis;
    LinearLayout lyt_parent, lyt_parent2, lyt_parent3, lyt_parenthabis;
    LinearLayout lyt_expand, lyt_expand2, lyt_expand3, lyt_expandhabis;
    TextView sisakontrakkerja, sisakontrakkerja2, sisakontrakkerja3, sisakontrakkerjahabis;
    List<listkaryawankontrakkerja> items;
    List<listkaryawandaftarabsensi> daftaritems;
    List<String> list_jabatan;
    CoordinatorLayout parent_view;
    listkaryawankontrakkerja kontrakkerja;

    Double counting=0.0d;

    listkaryawandaftarabsensi daftarabsensi;
    AdapterListSectionedKontrakKerja adapter;
    AdapterListDaftarAbsensi adapterabsensi;
    RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerViewhabis, recyclerViewdaftar;
    boolean expansi=false, expansi2=false, expansi3=false, expansihabis=false;
    int banyak1bulan=0, banyak2bulan=0,banyak3bulan=0, banyakhabis=0, banyakkaryawan=0, totalkaryawan=0;

    BarChart barChart ;

    Realm mRealm;

    String[] keterangan = new String[]{"izin", "sakit", "cuti","dinas","telat"};
    String[] kontrak_kerja = new String[]{"habis", "1bulan", "2bulan","3bulan"};
    String[] tipegaji = new String[]{"bersih", "estimasi"};
    public List<TextView> ket;
    public List<TextView> kk;
    public List<Integer> banyakkontrakkerja;
    public List<RecyclerView> rv_kk;
    public List<View> lyt_ket;
    public List<TextView> tgaji;

    ProgressDialog dialog;

    String perios="";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);

        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Loading Data..");
        dialog.show();

        mRealm = Realm.getDefaultInstance();

        barChart =rootView.findViewById(R.id.incbarchart);

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<datagajiobject> rows = realm.where(datagajiobject.class).findAll();
                rows.deleteAllFromRealm();
            }
        });

        Calendar calendar = Calendar.getInstance();

        int thisYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat sdfyear = new SimpleDateFormat("MMM");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-yy");
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, -12);



        List<String> monthbarbottom = new ArrayList<>();
        List<String> monthnumber = new ArrayList<>();
        List<Integer> yearnumber = new ArrayList<>();

// loop adding one day in each iteration
        for(int i = 0; i< 12; i++) {
            cal.add(Calendar.MONTH, 1);

            monthbarbottom.add(sdf.format(cal.getTime()));

            //f((Calendar.MONTH+"").length()){
                Log.e("month data",""+(cal.get(Calendar.MONTH)+1) );
                monthnumber.add(""+(cal.get(Calendar.MONTH)+1));
            /*}
            else {
                Log.e("month data","0"+(cal.get(Calendar.MONTH)+1) );
                monthnumber.add("0"+(cal.get(Calendar.MONTH)+1));
            }*/


            yearnumber.add(cal.get(Calendar.YEAR));

            Log.e("Month data",(cal.get(Calendar.MONTH)+1)+" / "+cal.get(Calendar.YEAR) );


            if (i == 0) {
                perios = perios + sdf.format(cal.getTime()) + " - ";
            }
            if (i == 11) {
                perios = perios + sdf.format(cal.getTime());
            }

            System.out.println(sdf.format(cal.getTime()) + sdfyear.format(cal.getTime()));
            int finalI = i;


        }



        final int[] count = {0};
        for(int i =0 ; i<monthbarbottom.size();i++) {

            Calendar c = Calendar.getInstance();

            try {
                c.setTime(sdf.parse(monthbarbottom.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Double value =0.0d;

            Realm finalMRealm1 = mRealm;
            String finalPerios = perios;

            SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");

            String dt1 = yearnumber.get(i)+"-"+monthnumber.get(i)+"-0"+c.get(Calendar.DAY_OF_MONTH);
            String dt2 = yearnumber.get(i)+"-"+monthnumber.get(i)+"-"+c.getActualMaximum(Calendar.DAY_OF_MONTH);


            retrivegajibychart gaji = new retrivegajibychart(getActivity(), dt1, dt2 ,mRealm,perios,i,monthbarbottom,count,dialog);
            gaji.execute();

        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        tanggal=format.format(c.getTime());

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView2=rootView.findViewById(R.id.recyclerView2bulan);
        recyclerView3=rootView.findViewById(R.id.recyclerView3bulan);
        recyclerViewhabis=rootView.findViewById(R.id.recyclerViewhabiskontrak);
        recyclerViewdaftar=rootView.findViewById(R.id.recyclerViewdaftarabsensi);

        totalizin=rootView.findViewById(R.id.totalizin);
        totalsakit=rootView.findViewById(R.id.totalsakit);
        totalcuti=rootView.findViewById(R.id.totalcuti);
        totalabsen=rootView.findViewById(R.id.totalabsen);
        totaltelat=rootView.findViewById(R.id.totaltelat);
        totaldinas=rootView.findViewById(R.id.totaldinas);


        lyt_totalizin=rootView.findViewById(R.id.lyt_totalizin);
        lyt_totalsakit=rootView.findViewById(R.id.lyt_totalsakit);
        lyt_totalcuti=rootView.findViewById(R.id.lyt_totalcuti);
        lyt_totalabsen=rootView.findViewById(R.id.lyt_totalabsen);
        lyt_totaltelat=rootView.findViewById(R.id.lyt_totaltelat);
        lyt_totaldinas=rootView.findViewById(R.id.lyt_totaldinas);


        totalgajibersih=rootView.findViewById(R.id.totalgajibersih);
        totalgajiestimasi=rootView.findViewById(R.id.totalgajiestimasi);


        parent_view=rootView.findViewById(R.id.parent_view);
        lyt_parent=rootView.findViewById(R.id.lyt_parent);
        lyt_parent2=rootView.findViewById(R.id.lyt_parent2bulan);
        lyt_parent3=rootView.findViewById(R.id.lyt_parent3bulan);
        lyt_parenthabis=rootView.findViewById(R.id.lyt_parenthabiskontrak);

        sisakontrakkerja=rootView.findViewById(R.id.sisakontrakkerja);
        sisakontrakkerja2=rootView.findViewById(R.id.sisakontrakkerja2bulan);
        sisakontrakkerja3=rootView.findViewById(R.id.sisakontrakkerja3bulan);
        sisakontrakkerjahabis=rootView.findViewById(R.id.sisakontrakkerjahabiskontrak);

        expand=rootView.findViewById(R.id.bt_expand);
        expand2=rootView.findViewById(R.id.bt_expand2bulan);
        expand3=rootView.findViewById(R.id.bt_expand3bulan);
        expandhabis=rootView.findViewById(R.id.bt_expandhabiskontrak);

        lyt_expand=rootView.findViewById(R.id.lyt_expand);
        lyt_expand2=rootView.findViewById(R.id.lyt_expand2bulan);
        lyt_expand3=rootView.findViewById(R.id.lyt_expand3bulan);
        lyt_expandhabis=rootView.findViewById(R.id.lyt_expandhabiskontrak);

        kk=new ArrayList<>();
        kk.add(sisakontrakkerjahabis);
        kk.add(sisakontrakkerja);
        kk.add(sisakontrakkerja2);
        kk.add(sisakontrakkerja3);

        rv_kk=new ArrayList<>();
        rv_kk.add(recyclerViewhabis);
        rv_kk.add(recyclerView);
        rv_kk.add(recyclerView2);
        rv_kk.add(recyclerView3);

        tgaji=new ArrayList<>();
        tgaji.add(totalgajibersih);
        tgaji.add(totalgajiestimasi);

        banyakkontrakkerja=new ArrayList<>();
        banyakkontrakkerja.add(banyakhabis);
        banyakkontrakkerja.add(banyak1bulan);
        banyakkontrakkerja.add(banyak2bulan);
        banyakkontrakkerja.add(banyak3bulan);

        ket=new ArrayList<>();
        ket.add(totalizin);
        ket.add(totalsakit);
        ket.add(totalcuti);
        ket.add(totaldinas);
        ket.add(totaltelat);

        lyt_ket=new ArrayList<>();
        lyt_ket.add(lyt_totalizin);
        lyt_ket.add(lyt_totalsakit);
        lyt_ket.add(lyt_totalcuti);
        lyt_ket.add(lyt_totaldinas);
        lyt_ket.add(lyt_totaltelat);
        for(int i=0;i<kontrak_kerja.length;i++){
            retrivekontrakkerjakar k_k=new retrivekontrakkerjakar(parent_view.getContext(),kontrak_kerja[i], kk.get(i),rv_kk.get(i),banyakkontrakkerja.get(i));
            k_k.execute();
        }

        for(int i=0;i<keterangan.length;i++){
            retriveketerangan keterangans=new retriveketerangan(getActivity(), keterangan[i], ket.get(i), lyt_ket.get(i));
            keterangans.execute();
        }

        for(int i=0;i<tipegaji.length;i++){
            retrivetotalgaji kar = new retrivetotalgaji(getActivity(),tgaji.get(i),tipegaji[i]);
            kar.execute();
        }
        retrivegetjabatan jab=new retrivegetjabatan(getActivity());
        jab.execute();

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(banyak1bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if(!expansi){
                        lyt_expand.setVisibility(View.VISIBLE);
                        expansi=true;
                        expand.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    }
                    else{
                        lyt_expand.setVisibility(View.GONE);
                        expansi=false;
                        expand.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }

            }
        });

        expand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak2bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi2) {
                        lyt_expand2.setVisibility(View.VISIBLE);
                        expansi2 = true;
                        expand2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expand2.setVisibility(View.GONE);
                        expansi2 = false;
                        expand2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        expand3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak3bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi3) {
                        lyt_expand3.setVisibility(View.VISIBLE);
                        expansi3 = true;
                        expand3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expand3.setVisibility(View.GONE);
                        expansi3 = false;
                        expand3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        expandhabis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyakhabis==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansihabis) {
                        lyt_expandhabis.setVisibility(View.VISIBLE);
                        expansihabis = true;
                        expandhabis.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expandhabis.setVisibility(View.GONE);
                        expansihabis = false;
                        expandhabis.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak1bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi) {
                        lyt_expand.setVisibility(View.VISIBLE);
                        expansi = true;
                        expand.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expand.setVisibility(View.GONE);
                        expansi = false;
                        expand.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        lyt_parent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak2bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi2) {
                        lyt_expand2.setVisibility(View.VISIBLE);
                        expansi2 = true;
                        expand2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expand2.setVisibility(View.GONE);
                        expansi2 = false;
                        expand2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        lyt_parent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak3bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi3) {
                        lyt_expand3.setVisibility(View.VISIBLE);
                        expansi3 = true;
                        expand3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expand3.setVisibility(View.GONE);
                        expansi3 = false;
                        expand3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        lyt_parenthabis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyakhabis==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansihabis) {
                        lyt_expandhabis.setVisibility(View.VISIBLE);
                        expansihabis = true;
                        expandhabis.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_collapse_arrow));
                    } else {
                        lyt_expandhabis.setVisibility(View.GONE);
                        expansihabis = false;
                        expandhabis.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                    }
                }
            }
        });

        lyt_totalabsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivityListKaryawan.class);
                intent.putExtra("keterangan","absen");
                getActivity().startActivity(intent);
            }
        });

        refreshhome = rootView.findViewById(R.id.swipehome);
        refreshhome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                banyakkaryawan=0;
                totalkaryawan=0;

                banyakhabis=0;
                banyak1bulan=0;
                banyak2bulan=0;
                banyak3bulan=0;

                expansi=false;
                expansi2=false;
                expansi3=false;
                expansihabis=false;

                lyt_expandhabis.setVisibility(View.GONE);
                expandhabis.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                lyt_expand.setVisibility(View.GONE);
                expand.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                lyt_expand2.setVisibility(View.GONE);
                expand2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));
                lyt_expand3.setVisibility(View.GONE);
                expand3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_expand_arrow));

                ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading Charts");

                for(int i =0 ; i<monthbarbottom.size();i++) {

                    Calendar c = Calendar.getInstance();

                    try {
                        c.setTime(sdf.parse(monthbarbottom.get(i)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String dt1 = yearnumber.get(i)+"-"+monthnumber.get(i)+"-0"+c.get(Calendar.DAY_OF_MONTH);
                    String dt2 = yearnumber.get(i)+"-"+monthnumber.get(i)+"-"+c.getActualMaximum(Calendar.DAY_OF_MONTH);

                    retrivegajibychart gaji = new retrivegajibychart(getActivity(), dt1, dt2 ,mRealm,perios,i,monthbarbottom,count,dialog);
                    gaji.execute();
                }

                for (int i = 0; i < kontrak_kerja.length; i++) {
                    retrivekontrakkerjakarref ref = new retrivekontrakkerjakarref(parent_view.getContext(), kontrak_kerja[i], kk.get(i), rv_kk.get(i), banyakkontrakkerja.get(i));
                    ref.execute();
                }
                retrivegetjabatanref jab=new retrivegetjabatanref(getActivity());
                jab.execute();
                refreshhome.setRefreshing(false);
            }
        });

        return rootView;
    }

    private class retrivekontrakkerjakar extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata;
        String passeddata = "" ;
        TextView tv;
        RecyclerView rv;
        int banyak=0;
        String kontrakskerja="";

        public retrivekontrakkerjakar(Context context, String kontrakskerja, TextView tv, RecyclerView rv, int banyak)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog=new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.tv=tv;
            this.rv=rv;
            this.banyak=banyak;
            this.kontrakskerja=kontrakskerja;
            if(kontrakskerja.equals("habis")){
                urldata=generator.kontrakkerjahabisurl;
            }
            else if(kontrakskerja.equals("1bulan")){
                urldata=generator.kontrakkerja1bulanurl;
            }
            else if(kontrakskerja.equals("2bulan")){
                urldata=generator.kontrakkerja2bulanurl;
            }
            else{
                urldata=generator.kontrakkerja3bulanurl;
            }
        }

        String TAG = getClass().getSimpleName();


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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }
            return response;
        }

        protected void onPostExecute(String result1) {
            try {

                if (result != null) {
                    Log.e(TAG, kontrakskerja + result.toString());
                    try {
                        if(result.getString("status").equals("true")) {
                            items = new ArrayList<>();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            JSONArray pengsarray = result.getJSONArray("data");
                            String status = "";
                            //String tempcall="";
                            int sisa = 0;
                            if (pengsarray.length() == 0) {
                                tv.setText("(0 orang)");
                                //Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                            }
                            for (int i = 0; i < pengsarray.length(); i++) {
                                status = pengsarray.getString(i);

                                if (!status.equals("null")) {

                                    JSONObject obj = pengsarray.getJSONObject(i);
                                    sisa = Integer.parseInt(obj.getString("sisa"));

                                    if(kontrakskerja.equals("habis")){
                                        if(sisa<=0){
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();
                                            kontrakkerja.setKontrakkerja("Kontrak kerja habis");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else if(kontrakskerja.equals("1bulan")){
                                        if(sisa>0 && sisa<31) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else if(kontrakskerja.equals("2bulan")){
                                        if(sisa>30 && sisa<61) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else{
                                        if(sisa>60 && sisa<91) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    if (i + 1 == pengsarray.length()) {
                                        tv.setText("(" + banyak + " orang)");
                                        if(kontrakskerja.equals("habis")){
                                            banyakhabis=banyak;
                                        }
                                        else if(kontrakskerja.equals("1bulan")){
                                            banyak1bulan=banyak;
                                        }
                                        else if(kontrakskerja.equals("2bulan")){
                                            banyak2bulan=banyak;
                                        }
                                        else{
                                            banyak3bulan=banyak;
                                        }
                                    }
                                }
                            }
                            adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rv.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));
                            rv.setHasFixedSize(true);
                            rv.setAdapter(adapter);

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

    private class retrivekontrakkerjakarref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata;
        String passeddata = "" ;
        TextView tv;
        RecyclerView rv;
        int banyak=0;
        String kontrakskerja="";

        public retrivekontrakkerjakarref(Context context, String kontrakskerja, TextView tv, RecyclerView rv, int banyak)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog=new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.tv=tv;
            this.rv=rv;
            this.banyak=banyak;
            this.kontrakskerja=kontrakskerja;
            if(kontrakskerja.equals("habis")){
                urldata=generator.kontrakkerjahabisurl;
            }
            else if(kontrakskerja.equals("1bulan")){
                urldata=generator.kontrakkerja1bulanurl;
            }
            else if(kontrakskerja.equals("2bulan")){
                urldata=generator.kontrakkerja2bulanurl;
            }
            else{
                urldata=generator.kontrakkerja3bulanurl;
            }
        }

        String TAG = getClass().getSimpleName();


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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }
            return response;
        }

        

        protected void onPostExecute(String result1) {
            try {

                if (result != null) {
                    Log.e(TAG, kontrakskerja + result.toString());
                    try {
                        if(result.getString("status").equals("true")) {
                            items = new ArrayList<>();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            JSONArray pengsarray = result.getJSONArray("data");
                            String status = "";
                            //String tempcall="";
                            int sisa = 0;
                            if (pengsarray.length() == 0) {
                                tv.setText("(0 orang)");
                                //Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                            }
                            for (int i = 0; i < pengsarray.length(); i++) {
                                status = pengsarray.getString(i);

                                if (!status.equals("null")) {

                                    JSONObject obj = pengsarray.getJSONObject(i);
                                    sisa = Integer.parseInt(obj.getString("sisa"));

                                    if(kontrakskerja.equals("habis")){
                                        if(sisa<=0){
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();
                                            kontrakkerja.setKontrakkerja("Kontrak kerja habis");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else if(kontrakskerja.equals("1bulan")){
                                        if(sisa>0 && sisa<31) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else if(kontrakskerja.equals("2bulan")){
                                        if(sisa>30 && sisa<61) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    else{
                                        if(sisa>60 && sisa<91) {
                                            banyak++;
                                            kontrakkerja = new listkaryawankontrakkerja();

                                            kontrakkerja.setKontrakkerja("Sisa kontrak kerja " + sisa + " hari");
                                            kontrakkerja.setSection(true);
                                            //kontrakkerja.setSisa("("+banyak+" orang)");
                                            //kar.setIskar(obj.getString("id"));
                                            kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));
                                            Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                            kontrakkerja.setNama(obj.getString("nama"));
                                            items.add(kontrakkerja);
                                        }
                                    }
                                    if (i + 1 == pengsarray.length()) {
                                        tv.setText("(" + banyak + " orang)");
                                        if(kontrakskerja.equals("habis")){
                                            banyakhabis=banyak;
                                        }
                                        else if(kontrakskerja.equals("1bulan")){
                                            banyak1bulan=banyak;
                                        }
                                        else if(kontrakskerja.equals("2bulan")){
                                            banyak2bulan=banyak;
                                        }
                                        else{
                                            banyak3bulan=banyak;
                                        }
                                    }
                                }
                            }
                            if(adapter!=null){
                                adapter.notifyDataSetChanged();
                            }
                            //refreshhome.setRefreshing(false);

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

    private class retrivegetjabatan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.jabatanurl;
        String passeddata = "" ;

        public retrivegetjabatan(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog=new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();


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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }


            return response;
        }

        

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {
                    try {
                        if (result.getString("status").equals("true")) {
                            Log.e(TAG, "data jabatan" + result.toString());
                            list_jabatan = new ArrayList<>();
                            JSONArray pengsarray = result.getJSONArray("data");
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                list_jabatan.add(obj.getString("jabatan"));
                                retrivedaftarabsensi kar = new retrivedaftarabsensi(getActivity(), list_jabatan.get(i), i);
                                kar.execute();
                            }

                            daftaritems = new ArrayList<>();

                            adapterabsensi = new AdapterListDaftarAbsensi(getActivity(), daftaritems, ItemAnimation.BOTTOM_UP);
                            recyclerViewdaftar.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerViewdaftar.setHasFixedSize(true);
                            recyclerViewdaftar.setAdapter(adapterabsensi);
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

    private class retrivegetjabatanref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.jabatanurl;
        String passeddata = "" ;

        public retrivegetjabatanref(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();


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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }


            return response;
        }

        

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {
                    try {
                        if (result.getString("status").equals("true")) {
                            Log.e(TAG, "data jabatan" + result.toString());
                            list_jabatan = new ArrayList<>();
                            JSONArray pengsarray = result.getJSONArray("data");
                            banyakkaryawan=0;
                            totalkaryawan=0;
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                list_jabatan.add(obj.getString("jabatan"));
                                retrivedaftarabsensiref kar = new retrivedaftarabsensiref(getActivity(), list_jabatan.get(i), i);
                                kar.execute();
                            }

                            daftaritems = new ArrayList<>();

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

    private class retrivedaftarabsensi extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.daftarabsensiurl;
        String passeddata = "" ;
        String jabatan="";
        int panjang;

        public retrivedaftarabsensi(Context context, String jabatan, int panjang)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.jabatan=jabatan;
            this.panjang=panjang;
        }

        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("jabatan",jabatan)
                            .add("date",tanggal)
                            .build();

                    Log.e(TAG, jabatan);

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

        

        protected void onPostExecute(String result1) {

            try {
                if (result != null) {
                    try {

                        Log.e(TAG, "daftar absensi" + result.toString());

                        List<String> listjabatan = new ArrayList<>();
                        List<String> listbanyak = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        if(!result.getString("data1").equals("0")){
                            daftarabsensi=new listkaryawandaftarabsensi();
                            daftarabsensi.setJabatan(jabatan);
                            daftarabsensi.setBanyak(""+pengsarray.length());
                            banyakkaryawan+=pengsarray.length();
                            daftarabsensi.setTotal(result.getString("data1"));
                            totalkaryawan+=Integer.parseInt(result.getString("data1"));
                            daftaritems.add(daftarabsensi);
                        }

                        if(panjang+1==list_jabatan.size()){
                            daftarabsensi=new listkaryawandaftarabsensi();
                            daftarabsensi.setJabatan("Total Karyawan");
                            daftarabsensi.setBanyak(""+banyakkaryawan);
                            daftarabsensi.setTotal(""+totalkaryawan);
                            daftaritems.add(daftarabsensi);
                            int absen=0;
                            absen=totalkaryawan-banyakkaryawan;
                            totalabsen.setText(String.valueOf(absen));
                            adapterabsensi.notifyDataSetChanged();

                            //getketerangan();
                        }

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

    private class retrivedaftarabsensiref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.daftarabsensiurl;
        String passeddata = "" ;
        String jabatan="";
        int panjang;

        public retrivedaftarabsensiref(Context context, String jabatan, int panjang)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.jabatan=jabatan;
            this.panjang=panjang;
        }

        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("jabatan",jabatan)
                            .add("date",tanggal)
                            .build();

                    Log.e(TAG, jabatan);

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

        

        protected void onPostExecute(String result1) {

            try {
                if (result != null) {
                    try {

                        Log.e(TAG, "daftar absensi" + result.toString());

                        List<String> listjabatan = new ArrayList<>();
                        List<String> listbanyak = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        if(!result.getString("data1").equals("0")){
                            daftarabsensi=new listkaryawandaftarabsensi();
                            daftarabsensi.setJabatan(jabatan);
                            daftarabsensi.setBanyak(""+pengsarray.length());
                            banyakkaryawan+=pengsarray.length();
                            daftarabsensi.setTotal(result.getString("data1"));
                            totalkaryawan+=Integer.parseInt(result.getString("data1"));
                            daftaritems.add(daftarabsensi);
                        }

                        if(panjang+1==list_jabatan.size()){
                            if(daftaritems.size()>0){
                                daftarabsensi=new listkaryawandaftarabsensi();
                                daftarabsensi.setJabatan("Total Karyawan");
                                daftarabsensi.setBanyak(""+banyakkaryawan);
                                daftarabsensi.setTotal(""+totalkaryawan);
                                daftaritems.add(daftarabsensi);
                            }

                            int absen=0;
                            absen=totalkaryawan-banyakkaryawan;
                            totalabsen.setText(String.valueOf(absen));

                            //Log.e("MASUK ADAPTER", "Berhasil"+absen+"total"+totalkaryawan);
                            if(adapterabsensi!=null){
                                adapterabsensi.notifyDataSetChanged();
                            }
                            //refreshhome.setRefreshing(false);


                            for(int i=0;i<tipegaji.length;i++){
                                retrivetotalgaji kar = new retrivetotalgaji(getActivity(),tgaji.get(i),tipegaji[i]);
                                kar.execute();
                            }


                        }
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

    private class retrivetotalgaji extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata;
        String passeddata = "" ;
        TextView tv;
        String tipe="";
        public retrivetotalgaji(Context context, TextView tv, String tipe)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog=new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.tv=tv;
            this.tipe=tipe;
            if(tipe.equals("bersih")){
                urldata=generator.totalgajiurl;
            }
            else{
                urldata=generator.gajiestimasiurl;
            }
        }

        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
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

        

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {

                    Log.e(TAG, "total gaji" + result.toString());

                    try {
                        //JSONArray pengsarray = result.getJSONArray("data");
                        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                        if(tipe.equals("bersih")){
                            JSONObject obj = result.getJSONObject("data");

                            tv.setText("RP "+formatter.format(Double.parseDouble(obj.getString("gajiBersih"))));

                        }
                        else{
                            tv.setText("RP "+formatter.format(Double.parseDouble(result.getString("data"))));


                        }
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

    private class retriveketerangan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = "";
        String passeddata = "" ;
        String keterangan="";
        TextView tv;
        View lyt;
        public retriveketerangan(Context context, String keterangan, TextView tv, View lyt)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
            this.tv=tv;
            this.lyt=lyt;
            if(keterangan.equals("izin")){
                urldata=generator.getizinhariyurl;
            }
            else if(keterangan.equals("sakit")){
                urldata=generator.getsakithariyurl;
            }
            else if(keterangan.equals("cuti")){
                urldata=generator.getcutihariyurl;
            }
            else if(keterangan.equals("dinas")){
                urldata=generator.getdinashariyurl;
            }
            else if(keterangan.equals("telat")){
                urldata=generator.absensitelatyurl;
            }
            /*
            else{
                urldata=generator.listemployeeurl;
            }*/
        }
        String TAG = getClass().getSimpleName();


        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("date",tanggal)
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

        

        protected void onPostExecute(String result1) {

            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        JSONArray pengsarray = result.getJSONArray("rows");


                        tv.setText(String.valueOf(pengsarray.length()));
                        lyt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getActivity(),ActivityListKaryawan.class);
                                intent.putExtra("keterangan",keterangan);
                                getActivity().startActivity(intent);
                            }
                        });
                    }  catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }  catch (Exception e) {
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

    private class retrivegajibychart extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getgajibytwodate;
        String passeddata = "" ;
        SimpleDateFormat sdfchart = new SimpleDateFormat("yyyy-MM-dd");
        String dt1="";
        String dt2="";
        int position =0;
        Double value = 0.0d;
        List<String> monthbarbottom ;
        String finalPerios = "";
        int[] count;

        public retrivegajibychart(Context context,String dt1,String dt2,Realm frealm,String periode,int postion,List<String> montbarbottom,int[] count,ProgressDialog dialogs)
        {

            //dialog.setMessage("Loading Data...");
            this.count = count;
            dialog=dialogs;
            dialog.setMessage("Loading Charts");

            Log.e("data 1 data 2",dt1+" "+dt2 );

            this.dt1 = dt1;
            this.dt2 = dt2;

            finalPerios = periode;

            Log.e( "dt1 and 2 ",dt1+" "+dt2 );
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;

            monthbarbottom = montbarbottom ;

            this.position = postion;
        }

        String TAG = getClass().getSimpleName();


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

        

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {
                    Log.e(TAG, "kerja 1 bulan" + result.toString());
                    try {
                        if(result.getString("status").equals("true")){
                            JSONObject obj = result.getJSONObject("data");

                            DecimalFormat formattery = new DecimalFormat("###,###,###.00");

                            value = Double.parseDouble(obj.getString("gajiBersih").replace(",",""));
                        }
                        //JSONArray pengsarray = result.getJSONArray("data");

                        //int gaji=1000000;

                        Log.e("gaji",value+"" );

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
                        if(counting==0){
                            counting = value;
                        }
                        else if(counting==value){
                            counting = value;
                            //barDataSet.setColor(Color.BLUE);
                        }
                        else if(counting<value){
                            counting = value;
                            //barDataSet.setColor(Color.GREEN);
                        }
                        else if(counting>value){
                            counting = value;
                            //barDataSet.setColor(Color.RED);
                        }

                        //barDataSet.setColor(generator.green);

                        barDataSet.setLabel("Period Gaji : " + finalPerios);

                        ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
                        barDataSets.add(barDataSet);

                        BarData barData = new BarData(barDataSets);

                        barChart.setData(barData);
                        barChart.setFitBars(true);
                        barChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);

                        barChart.invalidate();

                        if (count[0] == monthbarbottom.size()) {
                            mRealm.close();
                        }

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


            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }



}
