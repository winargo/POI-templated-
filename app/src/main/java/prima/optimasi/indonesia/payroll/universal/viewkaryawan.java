package prima.optimasi.indonesia.payroll.universal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity.picker.PickerColor;
import prima.optimasi.indonesia.payroll.adapter.AdapterListSectioned;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class viewkaryawan extends AppCompatActivity {


    private View parent_view;

    TextView templahir,tanggallahir,nama,jabatan,telepon,hp,alamat,email,agama,pend,gaji,status;
    TextView scount,icount,acount;
    ImageButton message,phone;
    CircularImageView image;

    private RecyclerView recyclerView;
    private AdapterListSectioned mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_purple);

        image = findViewById(R.id.karimage);

        templahir = findViewById(R.id.kartempatlahir);
        tanggallahir = findViewById(R.id.kartanggallahir);
        nama = findViewById(R.id.karname);
        jabatan = findViewById(R.id.karjob);
        telepon = findViewById(R.id.kartel);
        hp = findViewById(R.id.karhp);
        alamat = findViewById(R.id.karala);
        email = findViewById(R.id.karemail);
        agama = findViewById(R.id.karagama);
        pend = findViewById(R.id.karpend);
        gaji = findViewById(R.id.kargaji);
        status = findViewById(R.id.karstatus);

        icount = findViewById(R.id.karizin);
        scount = findViewById(R.id.karsakit);
        acount = findViewById(R.id.karabsen);

        retrivekaryawan kar = new retrivekaryawan(this,getIntent().getStringExtra("idkaryawan"));
        kar.execute();

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        String passedid = "" ;

        public retrivekaryawan(Context context,String id)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            passedid = id;
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
                            .url(urldata+"/"+passedid)
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

                        DecimalFormat formatter = new DecimalFormat("###,###,###.00");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("row");
                        JSONObject obj = pengsarray.getJSONObject(0);
                        templahir.setText(obj.getString("tempat_lahir"));
                        tanggallahir.setText(obj.getString("tgl_lahir"));
                        nama.setText(obj.getString("nama"));
                        jabatan.setText(obj.getString("jabatan"));
                        telepon.setText(obj.getString("telepon"));
                        hp.setText(obj.getString("no_wali"));
                        alamat.setText(obj.getString("alamat"));
                        email.setText(obj.getString("email"));
                        agama.setText(obj.getString("agama"));
                        pend.setText(obj.getString("pendidikan"));
                        gaji.setText("Rp "+ formatter.format(obj.getDouble("gaji")));
                        status.setText(obj.getString("status_kerja"));
                        scount.setText("-");
                        icount.setText("-");
                        acount.setText("-");

                        Picasso.get().load(generator.profileurl+"/"+obj.getString("foto")).transform(new CircleTransform()).into(image);

                        Log.e(TAG, "onPostExecute: "+ generator.profileurl+"/"+obj.getString("foto") );


                        // on item list clicked
                        mAdapter.setOnItemClickListener(new AdapterListSectioned.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, listkaryawan obj, int position) {
                                Snackbar.make(parent_view, "Item " + obj.getNama() + " clicked", Snackbar.LENGTH_SHORT).show();
                            }
                        });


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
