package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.AdapterGridTwoLineLight;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.data.DataGenerator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.model.Image;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentPengumuman extends Fragment {

    private View parent_view;

    private SwipeRefreshLayout refreshpengumuman;

    private RecyclerView recyclerView;
    private AdapterGridTwoLineLight mAdapter;

    List<pengumuman> items;

    BottomNavigationView bottomnac;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private SharedPreferences prefs;
    private View bottom_sheet;

    CalendarView calender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_bottom_sheet_floating, container, false);

        generator.posisipengumuman=0;

        items = new ArrayList<>();


        bottomnac = rootView.findViewById(R.id.navigation);

        calender = rootView.findViewById(R.id.calendarView);




        parent_view = rootView.findViewById(R.id.bgLayout);

        prefs = getActivity().getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

        bottomnac.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_pengumuman:
                        calender.setVisibility(View.GONE);
                        refreshpengumuman.setVisibility(View.VISIBLE);
                        generator.posisipengumuman=0;
                        ((mainmenu_owner) getActivity()).closeAll();
                        //refreshkabag.setVisibility(View.VISIBLE);
                        //refreshaktifitas.setVisibility(View.GONE);
                        //refreshkaryawan.setVisibility(View.GONE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.light_blue_900));
                        return true;
                    case R.id.navigation_kalender:
                        calender.setVisibility(View.VISIBLE);
                        refreshpengumuman.setVisibility(View.GONE);
                        generator.posisipengumuman=1;
                        ((mainmenu_owner) getActivity()).hideAll();

                        //refreshkabag.setVisibility(View.GONE);
                        //refreshaktifitas.setVisibility(View.GONE);
                        //refreshkaryawan.setVisibility(View.VISIBLE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.deep_purple_900));
                        return true;
                }
                return false;
            }
        });

        initComponent(rootView);
        //showBottomSheetDialog(mAdapter.getItem(0));

        return rootView;
    }



    private void initComponent(View v) {

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        refreshpengumuman = v.findViewById(R.id.pengswiperefresh);

        refreshpengumuman.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("siwped", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        if(mAdapter!=null){
                            retrivepengumumanref peng = new retrivepengumumanref(getActivity(),prefs.getString("Authorization",""));
                            peng.execute();
                        }
                    }
                }
        );



        items = new ArrayList<>();

        retrivepengumuman peng = new retrivepengumuman(getActivity(),prefs.getString("Authorization",""));
        peng.execute();



        //set data and list adapter

        bottom_sheet = v.findViewById(R.id.bottom_sheet);
        //mBehavior = BottomSheetBehavior.from(bottom_sheet);
    }

    private class retrivepengumuman extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengumumanurl;
        String passeddata = "" ;

        public retrivepengumuman(Context context, String kodeauth)
        {
            Log.e(TAG, "code: "+kodeauth );
            dialog = new ProgressDialog(context);
            passeddata = kodeauth;
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



                        Request request = new Request.Builder()
                                .header("Authorization",passeddata)
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

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            Log.e(TAG, " data peng " + obj.getString("penulis"));
                            pengumuman peng = new pengumuman(obj.getString("judul"), obj.getString("penulis"), format.parse(obj.getString("tanggal").substring(0, 9)), generator.getpicpengumumanurl+ obj.getString("foto"), obj.getString("isi"));
                            peng.setIdpengumuman(obj.getString("id_pengumuman"));
                            items.add(peng);
                        }



                        mAdapter = new AdapterGridTwoLineLight(getActivity(), items,parent_view);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(mAdapter);
                        generator.adapterpeng=mAdapter;
                        retriveliburan peng = new retriveliburan(getActivity(),prefs.getString("Authorization",""));
                        peng.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (ParseException e) {
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
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public class retrivepengumumanref extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengumumanurl;
        String passeddata = "" ;

        public retrivepengumumanref(Context context, String kodeauth)
        {
            dialog = new ProgressDialog(context);
            passeddata = kodeauth;
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
                this.dialog.setMessage("Refreshing Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
                            .header("Authorization",passeddata)
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
                        items.clear();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            Log.e(TAG, " data peng " + obj.getString("penulis"));
                            pengumuman peng = new pengumuman(obj.getString("judul"), obj.getString("penulis"), format.parse(obj.getString("tanggal").substring(0, 9)), generator.getpicpengumumanurl + obj.getString("foto"), obj.getString("isi"));
                            peng.setIdpengumuman(obj.getString("id_pengumuman"));
                            items.add(peng);
                        }

                        if(mAdapter!=null) {
                            mAdapter.notifyDataSetChanged();
                        }

                        refreshpengumuman.setRefreshing(false);
                        ((mainmenu_owner) getActivity()).closesearch();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (ParseException e) {
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
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

    private class retriveliburan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.calenderurl;
        String passeddata = "" ;

        public retriveliburan(Context context, String kodeauth)
        {
            Log.e(TAG, "code: "+kodeauth );
            dialog = new ProgressDialog(context);
            passeddata = kodeauth;
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



                    Request request = new Request.Builder()
                            .header("Authorization",passeddata)
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
                        List<EventDay> events = new ArrayList<>();
                        if(result.getString("status").equals("true")){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            JSONArray pengsarray = result.getJSONArray("row1");

                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);

                                Date data = format.parse(obj.getString("tanggal"));
                                Calendar calendars = Calendar.getInstance();
                                calendars.setTime(data);
                                events.add(new EventDay(calendars, R.drawable.dot));
                            }
                        }
                        Calendar calendar = Calendar.getInstance();

                        calender.setDate(calendar);
                        calender.setEvents(events);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (ParseException e) {
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

            if(refreshpengumuman !=null && calender !=null  && bottomnac!=null){
                bottomnac.setSelectedItemId(R.id.navigation_pengumuman);
                calender.setVisibility(View.GONE);
                refreshpengumuman.setVisibility(View.VISIBLE);
                generator.posisipengumuman=0;
                ((mainmenu_owner) getActivity()).closeAll();
            }
        }
    }

}
