package prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapter_absensi;
import prima.optimasi.indonesia.payroll.objects.logabsensi_karyawan;
import prima.optimasi.indonesia.payroll.widget.LineItemDecoration;

public class FragmentAbsensi extends Fragment {

    List<logabsensi_karyawan> itemabsensi;
    RecyclerView recyclerView;
    Adapter_absensi mAdapter;
    CoordinatorLayout parent_view;
    logabsensi_karyawan absensi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_log_absensi, container, false);

        parent_view=rootView.findViewById(R.id.parent_view);

        initComponent(rootView);
        return rootView;
    }
    private void initComponent(View v) {



        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        CoordinatorLayout.LayoutParams lp=(CoordinatorLayout.LayoutParams)parent_view.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams=new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,CoordinatorLayout.LayoutParams.MATCH_PARENT
        );
        lp.bottomMargin=0;
        lp.topMargin=160;
        parent_view.setLayoutParams(lp);
        parent_view.requestLayout();

        retrivelogabsensi absensi=new retrivelogabsensi(getActivity());
        absensi.execute();

        //List<Social> items = DataGenerator.getSocialData(getActivity());
        /*
        //set data and list adapter
        mAdapter = new Adapter_absensi_karyawan(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new Adapter_absensi_karyawan.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Social obj, int position) {
                Snackbar.make(v, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
        */

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
    private class retrivelogabsensi extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getabsensikaryawanurl;
        String passeddata = "" ;
        String tanggal="";

        public retrivelogabsensi(Context context)
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
                Log.e(TAG, "data absensi karyawan" + result.toString());
                if (result != null) {
                    try {
                        itemabsensi = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            String masuk=obj.getString("masuk");
                            String keluar=obj.getString("keluar");
                            String breakin=obj.getString("break_in");
                            String breakout=obj.getString("break_out");

                            String tanggal=obj.getString("tanggal").substring(8,10);
                            String bulan=obj.getString("tanggal").substring(5,7);
                            String tahun=obj.getString("tanggal").substring(0,4);
                            String tanggal_absensi=tanggal+"/"+bulan+"/"+tahun;
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
                            absensi = new logabsensi_karyawan();
                            if(masuk.equals("null")){
                                masuk="-";
                            }
                            if(keluar.equals("null")){
                                keluar="-";
                            }
                            if(breakin.equals("null")){
                                breakin="-";
                            }
                            if(breakout.equals("null")){
                                breakout="-";
                            }
                            absensi.setTanggal(tanggal_absensi);
                            absensi.setCheckin(masuk);
                            absensi.setCheckout(keluar);
                            absensi.setBreakin(breakin);
                            absensi.setBreakout(breakout);

                            itemabsensi.add(absensi);
                            //set data and list adapter
                            mAdapter = new Adapter_absensi(getActivity(), itemabsensi);
                            recyclerView.setAdapter(mAdapter);

                            /*
                            // on item list clicked
                            mAdapter.setOnItemClickListener(new Adapter_absensi_karyawan.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Social obj, int position) {
                                    Snackbar.make(v, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            */



                        }

                    }catch (Exception e) {
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
