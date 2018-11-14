package prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.data.SharedPref;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.adapter.AdapterListKaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class FragmentCekGaji extends Fragment {

    Double gaji,gajiblnlalu;
    CoordinatorLayout parent_view;
    List<listkaryawan> items;
    listkaryawan kar;
    AdapterListKaryawan adapter;
    View lyt_gajibulanini, lyt_gajibulanlalu;
    TextView gajibulanini, gajibulanlalu;
    RecyclerView recyclerView;
    SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_cek_gaji, container, false);

        recyclerView=rootView.findViewById(R.id.listkaryawancekgajihrd);
        gajibulanini=rootView.findViewById(R.id.gajibulanini);
        gajibulanlalu=rootView.findViewById(R.id.gajibulanlalu);
        lyt_gajibulanini=rootView.findViewById(R.id.gajibulanini);
        lyt_gajibulanlalu=rootView.findViewById(R.id.gajibulanlalu);
        parent_view=rootView.findViewById(R.id.parent_view);
        gaji=0.0d;
        gajiblnlalu=0.0d;
        prefs = getActivity().getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

        retrivegaji kar=new retrivegaji(getActivity());
        kar.execute();
        return rootView;
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
        String urldata = generator.pengajiangajikaryawanurl;
        String passeddata = "" ;
        Context ctx;
        public retrivegaji(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.ctx=context;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
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
                            .add("id",prefs.getString("id",""))
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

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {

                    Log.e(TAG, "Gaji" + result.toString());

                    try {
                        JSONObject obj = result.getJSONObject("data");
                        JSONArray GB = obj.getJSONArray("gajiBersih");
                        JSONArray GT = obj.getJSONArray("gajiTotal");
                        JSONObject sumGB = GB.getJSONObject(0);
                        JSONObject sumGT = GT.getJSONObject(0);
                        if(!sumGB.getString("gajiBersih").equals("null") || !sumGT.getString("gajiTotal").equals("null")){
                            gaji+=Double.parseDouble(sumGB.getString("gajiBersih"));
                            DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                            gajibulanini.setText("RP "+formatter.format(gaji));
                        }
                        else {
                            gaji=0.0d;
                            DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                            gajibulanini.setText("RP "+formatter.format(gaji));
                        }


                        retrivegajibulanlalu gbl=new retrivegajibulanlalu(getActivity());
                        gbl.execute();

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

            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivegajibulanlalu extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.pengajiangajibulanlalukaryawanurl;
        String passeddata = "" ;
        Context ctx;
        public retrivegajibulanlalu(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.ctx=context;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
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
                            .add("id",prefs.getString("id",""))
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

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {

                    Log.e(TAG, "Gaji" + result.toString());

                    try {
                        JSONObject obj = result.getJSONObject("data");

                        JSONArray GB = obj.getJSONArray("gajiBersih");

                        JSONObject sumGB = GB.getJSONObject(0);
                        if(!sumGB.getString("gajiBersih").equals("null")){
                            gajiblnlalu+=Double.parseDouble(sumGB.getString("gajiBersih"));
                            DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                            gajibulanlalu.setText("RP "+formatter.format(gajiblnlalu));
                        }
                        else{
                            gajiblnlalu=0.0d;
                            DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                            gajibulanlalu.setText("RP "+formatter.format(gajiblnlalu));
                        }

                        retrivekaryawan kar=new retrivekaryawan(getActivity());
                        kar.execute();

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

            Log.d(TAG + " onPostExecute", "" + result1);
        }
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

                if (result != null) {
                    try {
                        Log.e(TAG, "data json result" + result.toString());
                        items = new ArrayList<>();
                        JSONArray pengsarray = result.getJSONArray("rows");

                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(!obj.getString("id").equals(prefs.getString("id",""))) {
                                if (obj.getString("otoritas").equals("1")) {
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
                                    kar.setJabatan(obj.getString("jabatan"));
                                    kar.setIskar(obj.getString("id"));
                                    kar.setJenis("cekgaji");
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    } else {
                                        kar.setImagelink("");
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    }

                                    kar.setNama(obj.getString("nama"));
                                    items.add(kar);
                                } else if (obj.getString("otoritas").equals("2")) {
                                    if (!tempcall.equals(obj.getString("otoritas"))) {
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
                                    kar.setJabatan(obj.getString("jabatan"));
                                    kar.setJenis("cekgaji");

                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    } else {
                                        kar.setImagelink("");
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    }
                                    kar.setNama(obj.getString("nama"));

                                    items.add(kar);
                                } else if (obj.getString("otoritas").equals("3")) {
                                    if (!tempcall.equals(obj.getString("otoritas"))) {
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
                                    }*/
                                    }
                                    listkaryawan kar = new listkaryawan();
                                    kar.setJabatan("HRD");
                                    kar.setJenis("cekgaji");
                                    kar.setIskar(obj.getString("id"));
                                    if (!obj.getString("foto").equals("")) {
                                        kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    } else {
                                        kar.setImagelink("");
                                        Log.e(TAG, "image data" + kar.getImagelink());
                                    }

                                    kar.setNama(obj.getString("nama"));

                                    items.add(kar);
                                }
                            }
                        }
                        adapter = new AdapterListKaryawan(getActivity(), items,ItemAnimation.FADE_IN);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);


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
