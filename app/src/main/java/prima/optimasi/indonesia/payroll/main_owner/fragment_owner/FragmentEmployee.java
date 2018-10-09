package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import prima.optimasi.indonesia.payroll.adapter.AdapterListSectioned;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterGridCaller;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentEmployee extends Fragment {

    private View parent_view;

    private SwipeRefreshLayout refreshkabag;
    private SwipeRefreshLayout refreshkaryawan;

    private RecyclerView recyclerViewkabag;
    private RecyclerView recyclerViewkaryawan;

    private AdapterGridCaller mAdapterkabag;
    private AdapterGridCaller mAdapterkaryawan;

    BottomNavigationView bottomnac;

    List<listkaryawan> itemskaryawan;
    List<listkaryawan> itemskabag;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_employee, container, false);

        parent_view = rootView.findViewById(R.id.employeecoordinator);

        refreshkabag = rootView.findViewById(R.id.swipekabag);
        refreshkaryawan = rootView.findViewById(R.id.swipekaryawan);

        refreshkabag.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrivekaryawanrefersh ref = new retrivekaryawanrefersh(getActivity());
                ref.execute();
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

        bottomnac.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_kabag:
                        refreshkabag.setVisibility(View.VISIBLE);
                        refreshkaryawan.setVisibility(View.GONE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.light_blue_900));
                        return true;
                    case R.id.navigation_people:
                        refreshkabag.setVisibility(View.GONE);
                        refreshkaryawan.setVisibility(View.VISIBLE);
                        bottomnac.setBackgroundColor(getResources().getColor(R.color.green_900));
                        return true;
                }
                return false;
            }
        });

        initComponent(rootView);

        return rootView;
    }


    private void initComponent(View v) {
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
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

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
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

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
                        mAdapterkabag = new AdapterGridCaller(getActivity(), itemskabag,ItemAnimation.FADE_IN);
                        mAdapterkaryawan = new AdapterGridCaller(getActivity(), itemskaryawan,ItemAnimation.FADE_IN);
                        recyclerViewkabag.setAdapter(mAdapterkabag);
                        recyclerViewkaryawan.setAdapter(mAdapterkaryawan);



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
        String urldata = generator.listemployeeurl;
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
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

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
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

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

                        if(mAdapterkabag!=null){
                            mAdapterkabag.notifyDataSetChanged();
                        }
                        if(mAdapterkaryawan!=null){
                            mAdapterkaryawan.notifyDataSetChanged();
                        }

                        refreshkabag.setRefreshing(false);
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
}
