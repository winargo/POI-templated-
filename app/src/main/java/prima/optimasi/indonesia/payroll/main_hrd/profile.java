package prima.optimasi.indonesia.payroll.main_hrd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.listkaryawan_izincutisakit;
import prima.optimasi.indonesia.payroll.universal.adapter.Adapterviewkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.previewimage;

public class profile extends AppCompatActivity {



    private View parent_view;

    LinearLayout linear_view, dotsLayout;
    TextView templahir,tanggallahir,nama,jabatan,telepon,hp,alamat,email,agama,pend,gaji,status;
    TextView scount,icount,acount;
    ImageButton message,phone;
    CircularImageView image;
    String[] tabTitles = new String []{"Januari", "Februari","Maret","April","Mei","Juni","Juli"};

    ViewPager pager;
    TabLayout tabpager;
    TabLayout indicator;

    listkaryawan_izincutisakit kar;
    List<listkaryawan_izincutisakit> items;
    List<Integer> color;
    List<String> colorName;
    private TextView[] dots;
    private RecyclerView recyclerView;
    //private AdapterListSectioned mAdapter;
    private Adapterviewkaryawan adapter;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_karyawan);

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

        parent_view=findViewById(R.id.parent_view);
        linear_view=findViewById(R.id.linear_view);
        message = findViewById(R.id.karmsg);
        phone = findViewById(R.id.karphone);

        icount = findViewById(R.id.karizin);
        scount = findViewById(R.id.karsakit);
        acount = findViewById(R.id.karabsen);

        //pager = findViewById(R.id.view_pager);
        //dotsLayout=findViewById(R.id.layout_dots);
        recyclerView=findViewById(R.id.recyclerView);
        /*
        tabpager = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.view_pager);

        try{
            //setupViewPager(pager);
            //viewkaryawan.ExamplePagerAdapter adapter = new viewkaryawan.ExamplePagerAdapter(getSupportFragmentManager());
            //pager.setAdapter(adapter);


            tabpager.setupWithViewPager(pager);

            for (int i = 0; i < tabpager.getTabCount(); i++) {
                //noinspection ConstantConditions
                View v = LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
                TextView tv=v.findViewById(R.id.texttab);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.BLUE);
                tv.setText(tabTitles[i]);
                tabpager.getTabAt(i).setCustomView(v);
            }

        }catch (NullPointerException e) {
            Log.e("doInBackground: ", "null data" + e.getMessage());
        } catch (Exception e) {
            Log.e("doInBackground: ", e.getMessage());
        }
        */

        /*
        indicator=findViewById(R.id.indicator);
        color = new ArrayList<>();
        color.add(Color.RED);
        color.add(Color.GREEN);
        color.add(Color.BLUE);

        colorName = new ArrayList<>();
        colorName.add("RED");
        colorName.add("GREEN");
        colorName.add("BLUE");

        indicator.setupWithViewPager(pager, true);
        */



        retrivekaryawan kar = new retrivekaryawan(this,getIntent().getStringExtra("idkaryawan"));
        kar.execute();



        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] numbers= {telepon.getText().toString(), hp.getText().toString()};

                AlertDialog.Builder dialog = new AlertDialog.Builder(profile.this);
                dialog.setTitle("Sms to");
                dialog.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:"+numbers[which]));
                        if (sendIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(sendIntent);
                        }
                    }
                });

                dialog.show();



            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] numbers= {telepon.getText().toString(), hp.getText().toString()};

                AlertDialog.Builder dialog = new AlertDialog.Builder(profile.this);
                dialog.setTitle("Call");
                dialog.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+numbers[which]));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });

                dialog.show();



            }
        });

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
        Context ctx;
        ProgressDialog dialog ;
        String urldata = generator.listemployeeurl;
        String passedid = "" ;

        public retrivekaryawan(Context context,String id)
        {
            ctx=context;
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
                        final JSONObject obj = pengsarray.getJSONObject(0);
                        templahir.setText(obj.getString("tempat_lahir"));

                        SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM-dd");

                        Date temp = parsed.parse(obj.getString("tgl_lahir"));

                        parsed = new SimpleDateFormat("dd MMMM yyyy");

                        tanggallahir.setText(parsed.format(temp));
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

                        final Bitmap[] bm = {null};

                        Picasso.get().load(generator.profileurl+"/"+obj.getString("foto")).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bm[0] = bitmap;
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent a = new Intent(profile.this,previewimage.class);
                                        Bitmap bm1= null;
                                        bm1 = bm[0];
                                        generator.tempbitmap = bm1;
                                        startActivity(a);

                                    }
                                });
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });



                        Log.e(TAG, "onPostExecute: "+ generator.profileurl+"/"+obj.getString("foto") );


                        retrivekaryawanizin karizin = new retrivekaryawanizin(ctx,getIntent().getStringExtra("idkaryawan"));
                        karizin.execute();

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

    private class retrivekaryawanizin extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        Context ctx;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.getdataizinbulananyurl;
        //String passedid = "" ;

        public retrivekaryawanizin(Context context,String id)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            //passedid = id;
            this.error = error ;
            ctx=context;
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
                            .add("id",getIntent().getStringExtra("idkaryawan"))
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

                if (result != null) {
                    try {
                        items=new ArrayList<>();
                        Log.e(TAG, "data json result" + result);
                        kar=new listkaryawan_izincutisakit();
                        JSONArray pengsarray = result.getJSONArray("rows");
                        Log.e(TAG, "data json result" + pengsarray.length());
                        String tempcall="",temp="";
                        String tempbulan="";
                        int banyakizin=0;
                        for (int i = 0; i < pengsarray.length(); i++) {

                            JSONObject obj = pengsarray.getJSONObject(i);
                            String thn_izin=obj.getString("tgl_izin").substring(0,4);
                            String bln_izin=obj.getString("tgl_izin").substring(5,7);
                            String tgl_izin=obj.getString("tgl_izin").substring(8,10);
                            String totalizin=""+pengsarray.length();
                            Log.e(TAG, "data json result" + "Berhasil1");
                            //kar.setIzin(""+pengsarray.length());
                            //items.add(kar);

                            if(!tempcall.equals(obj.getString("tgl_izin").substring(5,7))){
                                if(tempcall.equals("")){
                                    kar = new listkaryawan_izincutisakit();
                                    kar.setBulan(obj.getString("tgl_izin").substring(5,7));
                                    kar.setSection(true);
                                    tempcall = obj.getString("tgl_izin").substring(5,7);
                                    items.add(kar);
                                    banyakizin=0;
                                    Log.e(TAG, "data json result" + "Berhasil2");
                                }
                                else{
                                    kar = new listkaryawan_izincutisakit();
                                    kar.setBulan(obj.getString("tgl_izin").substring(5,7));
                                    kar.setSection(true);
                                    tempcall = obj.getString("tgl_izin").substring(5,7);
                                    items.add(kar);
                                    banyakizin=0;
                                    Log.e(TAG, "data json result" + "Berhasil3");
                                }
                            }
                            else if(tempcall.equals(obj.getString("tgl_izin").substring(5,7)) && i+1<pengsarray.length()){
                                banyakizin++;
                                Log.e(TAG, "data json result" + "Berhasil4");
                            }
                            else if(tempcall.equals(obj.getString("tgl_izin").substring(5,7)) && i+1==pengsarray.length()){
                                banyakizin++;
                                kar=new listkaryawan_izincutisakit();
                                kar.setSection(false);
                                //kar.setIskar(obj.getString("id"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setIskar(obj.getString("kode_karyawan"));
                                kar.setNama(obj.getString("nama"));
                                kar.setJabatan(obj.getString("jabatan"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));
                                kar.setIzin(""+banyakizin);
                                items.add(kar);
                                banyakizin=0;
                                Log.e(TAG, "data json result" + "Berhasil6");
                            }
                            else if(!tempcall.equals(obj.getString("tgl_izin").substring(5,7)) && banyakizin>0){
                                kar=new listkaryawan_izincutisakit();
                                kar.setSection(false);
                                //kar.setIskar(obj.getString("id"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));

                                Log.e(TAG, "image data" + kar.getImagelink() );

                                kar.setIskar(obj.getString("kode_karyawan"));
                                kar.setNama(obj.getString("nama"));
                                kar.setJabatan(obj.getString("jabatan"));
                                kar.setImagelink(generator.profileurl+obj.getString("foto"));
                                kar.setIzin(""+banyakizin);
                                items.add(kar);
                                banyakizin=0;
                                Log.e(TAG, "data json result" + "Berhasil5");
                                i--;
                            }
                        }
                        adapter = new Adapterviewkaryawan(profile.this, items, ItemAnimation.LEFT_RIGHT);
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
