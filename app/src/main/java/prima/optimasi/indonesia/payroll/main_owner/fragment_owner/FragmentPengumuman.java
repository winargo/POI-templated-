package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import prima.optimasi.indonesia.payroll.model.Image;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentPengumuman extends Fragment {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterGridTwoLineLight mAdapter;

    List<pengumuman> items;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private SharedPreferences prefs;
    private View bottom_sheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_bottom_sheet_floating, container, false);

        items = new ArrayList<>();

        parent_view = rootView.findViewById(R.id.bgLayout);
        prefs = getActivity().getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

        initComponent(rootView);
        //showBottomSheetDialog(mAdapter.getItem(0));

        return rootView;
    }




    private void initComponent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        items = new ArrayList<>();

        retrivepengumuman peng = new retrivepengumuman(getActivity(),prefs.getString("Authorization",""));
        peng.execute();

        //set data and list adapter

        bottom_sheet = v.findViewById(R.id.bottom_sheet);
        //mBehavior = BottomSheetBehavior.from(bottom_sheet);
    }

    private void showBottomSheetDialog(final Image obj) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_floating, null);
        ((TextView) view.findViewById(R.id.name)).setText(obj.name);
        ((TextView) view.findViewById(R.id.brief)).setText(obj.brief);
        ((TextView) view.findViewById(R.id.description)).setText(R.string.middle_lorem_ipsum);
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });

        (view.findViewById(R.id.submit_rating)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Submit Rating", Toast.LENGTH_SHORT).show();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }

    public class retrivepengumuman extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = generator.pengumumanurl;
        String passeddata = "" ;

        public retrivepengumuman(Context context, String kodeauth)
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
                            Log.e(TAG, "data json " + responses.body().string() );
                            jsonObject = new JSONObject(responses.body().string());
                        }
                        result = jsonObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                } catch (IOException e) {
                    this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", "null data" + e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    this.dialog.setMessage("Loading Data... Error Occured,retrying...");
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
            super.onPostExecute(result1);
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }

            if(result!=null){
                try {
                    items.clear();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    JSONArray pengsarray = result.getJSONArray("rows");

                    for(int i = 0 ; i<pengsarray.length();i++){
                        JSONObject obj = pengsarray.getJSONObject(i);
                        pengumuman peng = new pengumuman(obj.getString("judul"),obj.getString("penulis"),format.parse(obj.getString("tanggal").substring(0,9)),generator.imageurl+obj.getString("foto"),obj.getString("isi"));
                        items.add(peng);
                    }

                    mAdapter = new AdapterGridTwoLineLight(getActivity(), items);
                    recyclerView.setAdapter(mAdapter);

                    // on item list clicked
                    mAdapter.setOnItemClickListener(new AdapterGridTwoLineLight.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Image obj, int position) {
                            Snackbar.make(parent_view, obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
                            //showBottomSheetDialog(obj);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onPostExecute: "+e.getMessage() );
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onPostExecute: "+e.getMessage() );
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onPostExecute: "+e.getMessage() );
                }



            }
            else {
                Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
}
