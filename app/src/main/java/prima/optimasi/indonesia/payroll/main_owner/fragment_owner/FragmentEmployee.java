package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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
import prima.optimasi.indonesia.payroll.adapter.AdapterListSectioned;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterGridCaller;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.Adapterabsensiaktifitas;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawanaktivitas;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentEmployee extends Fragment{

    private View parent_view;

    private SwipeRefreshLayout refreshkabag;
    private SwipeRefreshLayout refreshkaryawan;
    private SwipeRefreshLayout refreshaktifitas;

    private RecyclerView recyclerViewkabag;
    private RecyclerView recyclerViewkaryawan;
    private RecyclerView recyclerViewaktifitas;

    private AdapterGridCaller mAdapterkabag;
    private AdapterGridCaller mAdapterkaryawan;
    private Adapterabsensiaktifitas mAdapteraktifitas;

    //ProgressDialog progressDialog;
    BottomNavigationView bottomnac;
    String tanggal="";
    ImageView selectdate;

    TextView totalkabag,totalkaryawan,totalhadir;

    List<listkaryawan> itemskaryawan;
    List<listkaryawan> itemskabag;
    List<listkaryawanaktivitas> itemaktifitas;

    long selecteddate = 0L;

    int nilaikabag=0,nilaikaryawan=0,nilaikehadiran=0,nilaiall = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_employee, container, false);
        generator.posisi=0;
        parent_view = rootView.findViewById(R.id.employeecoordinator);

        refreshkabag = rootView.findViewById(R.id.swipekabag);
        refreshkaryawan = rootView.findViewById(R.id.swipekaryawan);
        refreshaktifitas = rootView.findViewById(R.id.swipeaktifitas);

        totalhadir = rootView.findViewById(R.id.totalhadir);
        totalkabag = rootView.findViewById(R.id.totalkabag);
        totalkaryawan = rootView.findViewById(R.id.totalkaryawan);

        bottomnac = rootView.findViewById(R.id.navigation);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        tanggal=format.format(c.getTime());
        Log.e("Tanggal sekarang", tanggal);
        initComponent(rootView);
        /*
        progressDialog = new ProgressDialog(rootView.getContext());

        progressDialog.setTitle("Loading...");

        progressDialog.setCancelable(false);

        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },2000L);*/

        selectdate = rootView.findViewById(R.id.dateselection_karyawan);
        SimpleDateFormat fomat = new SimpleDateFormat("dd/MM/yyyy");


        selectdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()).setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        retriveabsensispecified absensi = new retriveabsensispecified(getActivity(),tanggal);
                        absensi.execute();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflate = LayoutInflater.from(getActivity());

                View linear = inflate.inflate(R.layout.calenderview,null);

                CalendarView calender = linear.findViewById(R.id.calenderviews);
                calender.setHeaderColor(R.color.red_500);

                //calender.setHeaderColor(getActivity().getResources().getColor(R.color.red_500));

                Calendar cal = Calendar.getInstance();

                calender.setOnDayClickListener(new OnDayClickListener() {
                    @Override
                    public void onDayClick(EventDay eventDay) {

                        if(mAdapteraktifitas!=null){
                            itemaktifitas.clear();
                            mAdapteraktifitas.notifyDataSetChanged();
                        }
                        Calendar clickedDayCalendar = eventDay.getCalendar();
                        selecteddate = clickedDayCalendar.getTimeInMillis();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/YYYY");
                        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                        tanggal=format.format(clickedDayCalendar.getTime());
                        Log.e("Tanggal", tanggal);


                    }
                });

                if(selecteddate!=0L){
                    cal.setTimeInMillis(selecteddate);
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
            }
        });






        /*String query=getArguments().getString("query");
        if(!query.equals(null)){
            if(refreshkabag.getVisibility()==View.VISIBLE){
                mAdapterkabag.getFilter().filter(query);
            }
            else{
                mAdapterkaryawan.getFilter().filter(query);
            }
        }
        */
        return rootView;
    }


    private void initComponent(View v) {

        refreshkabag.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nilaiall = 0;
                retrivekaryawanrefersh ref = new retrivekaryawanrefersh(getActivity());
                ref.execute();
            }
        });

        refreshkaryawan.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                nilaiall = 0;
                retrivekaryawanrefersh ref = new retrivekaryawanrefersh(getActivity());
                ref.execute();
            }
        });
        refreshaktifitas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mAdapteraktifitas!=null){
                    itemaktifitas.clear();
                    mAdapteraktifitas.notifyDataSetChanged();
                }

                retriveabsensirefresh ref = new retriveabsensirefresh(getActivity());
                ref.execute();
            }
        });



        bottomnac.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_kabag:
                        refreshkabag.setVisibility(View.VISIBLE);
                        refreshaktifitas.setVisibility(View.GONE);
                        refreshkaryawan.setVisibility(View.GONE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.light_blue_900));
                        generator.posisi=0;
                        ((mainmenu_owner) getActivity()).closesearch();

                        return true;
                    case R.id.navigation_people:
                        refreshkabag.setVisibility(View.GONE);
                        refreshaktifitas.setVisibility(View.GONE);
                        refreshkaryawan.setVisibility(View.VISIBLE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.green_900));
                        generator.posisi=1;
                        ((mainmenu_owner) getActivity()).closesearch();

                        return true;
                    case R.id.navigation_recents:
                        refreshkabag.setVisibility(View.GONE);
                        refreshaktifitas.setVisibility(View.VISIBLE);
                        refreshkaryawan.setVisibility(View.GONE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.red_900));
                        generator.posisi=2;
                        ((mainmenu_owner) getActivity()).hidesearch();

                        return true;
                }
                return false;
            }
        });


        recyclerViewkaryawan = (RecyclerView) v.findViewById(R.id.recyclerView_karyawan);
        recyclerViewkaryawan.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewkaryawan.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));
        recyclerViewkaryawan.setHasFixedSize(true);
        recyclerViewkaryawan.setNestedScrollingEnabled(false);

        recyclerViewkabag = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerViewkabag.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewkabag.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));
        recyclerViewkabag.setHasFixedSize(true);
        recyclerViewkabag.setNestedScrollingEnabled(false);

        recyclerViewaktifitas = (RecyclerView) v.findViewById(R.id.recyclerView_aktifitas);
        recyclerViewaktifitas.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerViewaktifitas.setHasFixedSize(true);
        recyclerViewaktifitas.setNestedScrollingEnabled(false);

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
        String urldata = generator.listemployeeurl;
        String passeddata = "" ;

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
                        itemskabag = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(obj.getString("otoritas").equals("1")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
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
                                kar.setDesc(obj.getString("jabatan"));
                                itemskaryawan.add(kar);
                            }else if(obj.getString("otoritas").equals("2")){
                                if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }*/
                                }
                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("Kepala Bagian");
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
                                kar.setDesc(obj.getString("jabatan"));
                                itemskabag.add(kar);
                            }else if(obj.getString("otoritas").equals("3")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }
                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("HRD");
                                kar.setIskar(obj.getString("id"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                items.add(kar);*/
                            }

                        }

                        nilaikabag = itemskabag.size();
                        nilaikaryawan = itemskaryawan.size();

                        totalkaryawan.setText("Jumlah Karyawan : "+nilaikaryawan);
                        totalkabag.setText("Jumlah Kepala bagian : "+nilaikabag);

                        nilaiall = itemskabag.size()+itemskaryawan.size();

                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        mAdapterkabag = new AdapterGridCaller(getActivity(), itemskabag,ItemAnimation.FADE_IN);
                        mAdapterkaryawan = new AdapterGridCaller(getActivity(), itemskaryawan,ItemAnimation.FADE_IN);
                        recyclerViewkabag.setAdapter(mAdapterkabag);
                        recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                        generator.adapterkabag=mAdapterkabag;
                        generator.adapterkar=mAdapterkaryawan;
                        retriveabsensi absensi = new retriveabsensi(getActivity());
                        absensi.execute();

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

    private class retrivekaryawanrefersh extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.listemployeeurl;
        String passeddata = "" ;

        public retrivekaryawanrefersh(Context context)
        {
            nilaikabag = 0;
            nilaikaryawan = 0;
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
                        itemskaryawan = new ArrayList<>();
                        itemskabag = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(obj.getString("otoritas").equals("1")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
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
                                kar.setDesc(obj.getString("jabatan"));
                                itemskaryawan.add(kar);
                            }else if(obj.getString("otoritas").equals("2")){
                                if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }*/
                                }
                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("Kepala Bagian");
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
                                kar.setDesc(obj.getString("jabatan"));
                                itemskabag.add(kar);
                            }else if(obj.getString("otoritas").equals("3")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }
                                listkaryawan kar = new listkaryawan();
                                kar.setSection(false);
                                kar.setJabatan("HRD");
                                kar.setIskar(obj.getString("id"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                items.add(kar);*/
                            }
                            /*int sect_count = 0;
                            int sect_idx = 0;
                            List<String> months = DataGenerator.getStringsMonth(getActivity());
                            for (int i = 0; i < items.size() / 6; i++) {
                                items.add(sect_count, new People(months.get(sect_idx), true));
                                sect_count = sect_count + 5;
                                sect_idx++;
                            }*/

                        }

                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        if(mAdapteraktifitas!=null){
                            mAdapteraktifitas.notifyDataSetChanged();
                        }

                        nilaikabag = itemskabag.size();
                        nilaikaryawan = itemskaryawan.size();

                        totalkaryawan.setText("Jumlah Karyawan : "+nilaikaryawan);
                        totalkabag.setText("Jumlah Kepala bagian : "+nilaikabag);

                        nilaiall = nilaikabag + nilaikaryawan;

                        refreshkabag.setRefreshing(false);
                        refreshkaryawan.setRefreshing(false);
                        ((mainmenu_owner) getActivity()).closesearch();
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
    private class retriveabsensi extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        JSONObject result1 = null ;
        ProgressDialog dialog ;
        String urldata = generator.getabsensidateurl;
        String passeddata = "" ;

        public retriveabsensi(Context context)
        {
            nilaikehadiran=0;
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

                    RequestBody body = new FormBody.Builder()
                            .add("date",tanggal)
                            .build();

                    Log.e(TAG, tanggal);

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
                /*
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

                        result1 = new JSONObject(responses.body().string());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }*/
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

        protected void onPostExecute(String result2) {

            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result1 != null) {
                    generator.servertime = result1.getString("jam");
                }
                if (result != null) {
                    try {
                        itemaktifitas = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(obj.getString("otoritas").equals("1")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Karyawan");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("2")){
                                if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }*/
                                }
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Kepala Bagian");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("3")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }
                                kar.setJabatan("HRD");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else{
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Security");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }
                            /*int sect_count = 0;
                            int sect_idx = 0;
                            List<String> months = DataGenerator.getStringsMonth(getActivity());
                            for (int i = 0; i < items.size() / 6; i++) {
                                items.add(sect_count, new People(months.get(sect_idx), true));
                                sect_count = sect_count + 5;
                                sect_idx++;
                            }*/

                        }
                        nilaikehadiran = itemaktifitas.size();

                        totalhadir.setText("Jumlah Karyawan Hadir : ("+nilaikehadiran+"/"+nilaiall+")");

                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        mAdapteraktifitas = new Adapterabsensiaktifitas(getActivity(), itemaktifitas,ItemAnimation.FADE_IN);
                        recyclerViewaktifitas.setAdapter(mAdapteraktifitas);

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

    private class retriveabsensirefresh extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getabsensidateurl;
        String passeddata = "" ;

        public retriveabsensirefresh(Context context)
        {
            nilaikehadiran = 0;
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

                    RequestBody body = new FormBody.Builder()
                            .add("date",tanggal)
                            .build();

                    Log.e(TAG, tanggal);

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
                Log.e(TAG, "data absensi karyawan" + result.toString());
                if (result != null) {
                    try {
                        if(itemaktifitas==null){
                          itemaktifitas  = new ArrayList<>();
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(obj.getString("otoritas").equals("1")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Karyawan");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("2")){
                                if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }*/
                                }
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Kepala Bagian");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("3")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }
                                kar.setJabatan("HRD");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else{
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Security");
                                if (!obj.getString("foto").equals("")) {
                                    kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }
                                else{
                                    kar.setImagelink("");
                                    Log.e(TAG, "image data" + kar.getImagelink());
                                }

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }
                            /*int sect_count = 0;
                            int sect_idx = 0;
                            List<String> months = DataGenerator.getStringsMonth(getActivity());
                            for (int i = 0; i < items.size() / 6; i++) {
                                items.add(sect_count, new People(months.get(sect_idx), true));
                                sect_count = sect_count + 5;
                                sect_idx++;
                            }*/

                        }

                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        nilaikehadiran = itemaktifitas.size();

                        totalhadir.setText("Jumlah Karyawan Hadir : ("+nilaikehadiran+"/"+nilaiall+")");
                        if(mAdapteraktifitas!=null){
                            mAdapteraktifitas.notifyDataSetChanged();
                        }
                        refreshaktifitas.setRefreshing(false);
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

    private class retriveabsensispecified extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getabsensidateurl;
        String passeddata = "" ;

        public retriveabsensispecified(Context context,String dates)
        {
            passeddata = dates;
            nilaikehadiran = 0;
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

                    RequestBody body = new FormBody.Builder()
                            .add("date",passeddata)
                            .build();

                    Log.e(TAG, tanggal);

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
                Log.e(TAG, "data absensi karyawan" + result.toString());
                if (result != null) {
                    try {
                        itemaktifitas.clear();

                        //mAdapteraktifitas.notifyDataSetChanged();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(obj.getString("otoritas").equals("1")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Karyawan");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Karyawan");
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("2")){
                                if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("Kepala Bagian");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }*/
                                }
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Kepala Bagian");
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else if(obj.getString("otoritas").equals("3")){
                                /*if(!tempcall.equals(obj.getString("otoritas"))){
                                    /*if(tempcall.equals("")){
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                    else{
                                        listkaryawan kar = new listkaryawan();
                                        kar.setJabatan("HRD");
                                        kar.setSection(true);
                                        tempcall = obj.getString("otoritas");
                                        items.add(kar);
                                    }
                                }*/
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }
                                kar.setJabatan("HRD");
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }else{
                                listkaryawanaktivitas kar = new listkaryawanaktivitas();

                                if(!obj.getString("masuk").equals("null")){
                                    kar.setCheckin(obj.getString("masuk"));
                                }
                                else {
                                    kar.setCheckin("-");
                                }
                                if(!obj.getString("keluar").equals("null")){
                                    kar.setCheckout(obj.getString("keluar"));
                                }
                                else {
                                    kar.setCheckout("-");
                                }
                                if(!obj.getString("break_in").equals("null")){
                                    kar.setBreakin(obj.getString("break_in"));
                                }
                                else {
                                    kar.setBreakin("-");
                                }
                                if(!obj.getString("break_out").equals("null")){
                                    kar.setBreakout(obj.getString("break_out"));
                                }
                                else {
                                    kar.setBreakout("-");
                                }

                                kar.setJabatan("Security");
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setNama(obj.getString("nama"));
                                kar.setDesc(obj.getString("jabatan"));
                                itemaktifitas.add(kar);
                            }
                            /*int sect_count = 0;
                            int sect_idx = 0;
                            List<String> months = DataGenerator.getStringsMonth(getActivity());
                            for (int i = 0; i < items.size() / 6; i++) {
                                items.add(sect_count, new People(months.get(sect_idx), true));
                                sect_count = sect_count + 5;
                                sect_idx++;
                            }*/

                        }

                        //mAdapter = new AdapterListSectioned(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        nilaikehadiran = itemaktifitas.size();

                        totalhadir.setText("Jumlah Karyawan Hadir : ("+nilaikehadiran+"/"+nilaiall+")");
                        if(mAdapteraktifitas!=null){
                            mAdapteraktifitas.notifyDataSetChanged();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if(refreshkabag !=null && refreshkaryawan !=null && refreshaktifitas!=null && bottomnac!=null){
                bottomnac.setSelectedItemId(R.id.navigation_kabag);
                refreshkabag.setVisibility(View.VISIBLE);
                refreshaktifitas.setVisibility(View.GONE);
                refreshkaryawan.setVisibility(View.GONE);
                bottomnac.setBackgroundColor(getResources().getColor(R.color.light_blue_900));
                generator.posisi=0;
                ((mainmenu_owner) getActivity()).closesearch();
            }
        }
    }
}
