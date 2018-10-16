package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

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
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedjob;
import prima.optimasi.indonesia.payroll.objects.listjob;
import prima.optimasi.indonesia.payroll.objects.listkaryawanaktivitas;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentJob extends Fragment {

    RecyclerView history;
    TextView dt1,dt2;

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};

    AdapterListSectionedjob adapter;

    List<listjob> titles = new ArrayList<>();

    RecyclerView recyclerall;

    CoordinatorLayout parent_view;

    MaterialSpinner pilihan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_job, container, false);

        recyclerall = rootView.findViewById(R.id.recycler_alljob);

        parent_view = rootView.findViewById(R.id.coordinator);



        for (int i =0 ; i < 10;i++){
            listjob approval = new listjob();

            if(i==0){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==1){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==2){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==3){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==4){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==5){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==6){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==7){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==8){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==9){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            titles.add(approval);
        }

        adapter = new AdapterListSectionedjob(getActivity(),titles,ItemAnimation.FADE_IN);

        recyclerall.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerall.setHasFixedSize(true);
        recyclerall.setNestedScrollingEnabled(false);
        recyclerall.setAdapter(adapter);
        return rootView;
    }

    /*

    approvalcuti
    approvaldinas
    approvaldirumahkan
    approvalizin

    approvalgolongan
    approvalkaryawan
    approvalpinjaman
    approvalpdm
    approvalpunishment
    approvalreward

            */

    private class retrive extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getabsensiurl;
        String passeddata = "" ;

        public retrive(Context context,List<String> totalaproval)
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
                            .add("","")
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
                e.printStackTrace();
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


            Log.d(TAG + " onPostExecute", "" + result);
        }
    }
}
