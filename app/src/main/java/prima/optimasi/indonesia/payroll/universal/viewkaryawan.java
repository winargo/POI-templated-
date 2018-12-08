package prima.optimasi.indonesia.payroll.universal;

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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.previewimage;

public class viewkaryawan extends AppCompatActivity {
    FloatingActionButton timeline;
    private View parent_view;
    String kodekar="", namakar="";
    LinearLayout linear_view;
    TextView templahir,tanggallahir,nama,jabatan,telepon,hp,alamat,email,agama,pend,gaji,status;
    ImageButton message,phone;
    CircularImageView image;
    String[] keterangan = new String []{"izin", "sakit","cuti","dinas","telat"};
    List<String> listizin;
    List<String> listsakit;
    List<String> listcuti;
    List<String> listdinas;
    ProgressDialog dialog;
    String blnskrg="";
    listkaryawan_izincutisakit kar;
    List<listkaryawan_izincutisakit> items;
    List<Integer> color;
    List<String> dt1, dt2;
    private TextView[] dots;
    RecyclerView recyclerView;
    Adapterviewkaryawan adapter;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_karyawan);
        initToolbar();
        initComponent();
        initListener();
        snackBarWithActionIndefinite();
    }

    private void snackBarWithActionIndefinite() {
        if(generator.checkInternet(viewkaryawan.this)) {
            if(snackbar!=null) {
                snackbar.dismiss();
            }
            retrivekaryawan kar = new retrivekaryawan(this, getIntent().getStringExtra("idkaryawan"));
            kar.execute();

            SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM");
            Calendar cal = Calendar.getInstance();
            blnskrg = parsed.format(cal.getTime());
            cal.add(Calendar.MONTH, -12);

            dialog.setTitle("Mohon Menunggu");
            dialog.setMessage("Memuat Data");
            dialog.show();
            for (int j = 0; j < 12; j++) {
                cal.add(Calendar.MONTH, 1);
                String dat1 = parsed.format(cal.getTime()) + "-0" + cal.getActualMinimum(Calendar.DAY_OF_MONTH);
                String dat2 = parsed.format(cal.getTime()) + "-" + cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                dt1.add(dat1);
                dt2.add(dat2);
            }
            for (int x = 0; x < 12; x++) {
                for (int i = 0; i < keterangan.length; i++) {
                    retriveketerangan ket = new retriveketerangan(viewkaryawan.this, keterangan[i], dt1.get(x), dt2.get(x));
                    ket.execute();
                }
            }
        }
        else {
            snackbar = Snackbar.make(parent_view, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction("COBA LAGI", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackBarWithActionIndefinite();
                        }
                    });
            snackbar.show();
        }
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
        timeline = findViewById(R.id.timeline);
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
        recyclerView=findViewById(R.id.recyclerView);
        listizin=new ArrayList<>();
        listsakit=new ArrayList<>();
        listcuti=new ArrayList<>();
        listdinas=new ArrayList<>();
        items=new ArrayList<>();
        dt1=new ArrayList<>();
        dt2=new ArrayList<>();
        dialog=new ProgressDialog(viewkaryawan.this);
    }

    private void initListener(){
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] numbers= {telepon.getText().toString(), hp.getText().toString()};
                AlertDialog.Builder dialog = new AlertDialog.Builder(viewkaryawan.this);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(viewkaryawan.this);
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

        timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(namakar.equals("")){
                }
                else {
                    Intent intent = new Intent(viewkaryawan.this, viewtimeline.class);
                    intent.putExtra("idkaryawan", getIntent().getStringExtra("idkaryawan"));
                    intent.putExtra("nama", namakar);
                    startActivity(intent);
                }
            }
        });
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

                    Log.e(TAG ,prefs.getString("Authorization",""));
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

                        kodekar=obj.getString("kode_karyawan");
                        tanggallahir.setText(parsed.format(temp));
                        nama.setText(obj.getString("nama"));
                        namakar=obj.getString("nama");
                        jabatan.setText(obj.getString("jabatan"));
                        telepon.setText(obj.getString("telepon"));
                        hp.setText(obj.getString("no_wali"));
                        alamat.setText(obj.getString("alamat"));
                        email.setText(obj.getString("email"));
                        agama.setText(obj.getString("agama"));
                        pend.setText(obj.getString("pendidikan"));
                        gaji.setText("Rp "+ formatter.format(obj.getDouble("gaji")));
                        status.setText(obj.getString("status_kerja"));
                        String urlpath="";
                        if(obj.getString("foto").equals("")){
                            urlpath="http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png";
                        }
                        else{
                            urlpath=generator.profileurl+"/"+obj.getString("foto");
                        }
                        Picasso.get().load(urlpath).transform(new CircleTransform()).into(image);
                        final Bitmap[] bm = {null};

                        Picasso.get().load(urlpath).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bm[0] = bitmap;
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent a = new Intent(viewkaryawan.this,previewimage.class);
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

    private class retriveketerangan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        Context ctx;
        JSONObject result = null ;
        //ProgressDialog dialog;
        String urldata;
        String keterangan="";
        String date1="";
        String date2="";

        public retriveketerangan(Context context,String keterangan, String date1, String date2)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
            this.keterangan=keterangan;
            this.date1=date1;
            this.date2=date2;
            ctx=context;
            if(keterangan.equals("izin")){
                urldata=generator.getizinperbulanyurl;
            }
            else if(keterangan.equals("sakit")){
                urldata=generator.getsakitperbulanyurl;
            }
            else if(keterangan.equals("cuti")){
                urldata=generator.getcutiperbulanyurl;
            }
            else if(keterangan.equals("dinas")){
                urldata=generator.getdinasperbulanyurl;
            }
            else{
                urldata=generator.gettelatperbulanyurl;
            }
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

                    RequestBody body= new FormBody.Builder()
                            .add("id", getIntent().getStringExtra("idkaryawan"))
                            .add("start",date1)
                            .add("end",date2)
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
                    try {
                        Log.e(TAG, keterangan+" : "+ result);
                        JSONArray pengsarray;
                        if(keterangan.equals("telat")){
                            pengsarray= result.getJSONArray("data");
                        }else{
                            pengsarray = result.getJSONArray("rows");
                        }
                        SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM");
                        SimpleDateFormat parsed1 = new SimpleDateFormat("MMMM yyyy");
                        if(keterangan.equals("izin")){
                            listizin.clear();
                            listsakit.clear();
                            listcuti.clear();
                            listdinas.clear();
                            listizin.add(String.valueOf(pengsarray.length()));
                        }
                        else if(keterangan.equals("sakit")){
                            listsakit.add(String.valueOf(pengsarray.length()));
                        }
                        else if(keterangan.equals("cuti")){
                            listcuti.add(String.valueOf(pengsarray.length()));
                        }
                        else if(keterangan.equals("dinas")){
                            listdinas.add(String.valueOf(pengsarray.length()));
                        }
                        else if(keterangan.equals("telat")){
                            kar=new listkaryawan_izincutisakit();
                            String blnthn=parsed1.format(parsed.parse(date1.substring(0,7)));
                            kar.setBulan(blnthn);
                            if(listizin.size()==0){
                                kar.setIzin("0");
                            }
                            else{
                                kar.setIzin(listizin.get(0));
                            }
                            if(listsakit.size()==0){
                                kar.setSakit("0");
                            }
                            else{
                                kar.setSakit(listsakit.get(0));
                            }
                            if(listcuti.size()==0){
                                kar.setCuti("0");

                            }
                            else{
                                kar.setCuti(listcuti.get(0));

                            }
                            if(listdinas.size()==0){
                                kar.setDinas("0");

                            }
                            else{
                                kar.setDinas(listdinas.get(0));

                            }
                            kar.setTelat(String.valueOf(pengsarray.length()));
                            items.add(kar);
                        }
                        if(date1.substring(0,7).equals(blnskrg) && keterangan.equals("telat")){
                            if(items.size()>0){
                                adapter = new Adapterviewkaryawan(viewkaryawan.this, items, ItemAnimation.NONE);
                                recyclerView.setLayoutManager(new LinearLayoutManager(viewkaryawan.this, LinearLayoutManager.HORIZONTAL, false));
                                recyclerView.setAdapter(adapter);
                                PagerSnapHelper snapHelper = new PagerSnapHelper();
                                snapHelper.attachToRecyclerView(recyclerView);
                            }
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
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

    private class retriveketerangans extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        Context ctx;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata;
        String keterangan="";
        //String passedid = "" ;

        public retriveketerangans(Context context,String keterangan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            //dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            //passedid = id;
            this.error = error ;
            this.keterangan=keterangan;
            ctx=context;
            if(keterangan.equals("izin")){
                urldata=generator.pengajuanizinkodeurl;
            }
            else if(keterangan.equals("sakit")){
                urldata=generator.pengajuansakitkodeurl;
            }
            else if(keterangan.equals("cuti")){
                urldata=generator.pengajuancutikodeurl;
            }
            else{
                urldata=generator.pengajuandinaskodeurl;
            }
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

                    RequestBody body= new FormBody.Builder()
                            .add("id", getIntent().getStringExtra("idkaryawan"))
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
                    try {

                        Log.e(TAG, "data json result" + result);

                        JSONArray pengsarray = result.getJSONArray("rows");
                        Log.e(TAG, "data json result" + pengsarray.length());
                        String tempcall="",temp="";
                        String tempbulan="";
                        int banyakizin=0, banyaksakit=0, banyakcuti=0, banyakdinas=0;
                        boolean ada;
                        /*
                        if(keterangan.equals("izin")){
                            listizin.add(""+pengsarray.length());
                        }else{
                            listsakit.add(""+pengsarray.length());
                            kar=new listkaryawan_izincutisakit();
                            kar.setIzin(listizin);
                        }*/

                        SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM");
                        SimpleDateFormat parsed1 = new SimpleDateFormat("MMMM yyyy");
                        Calendar cal = Calendar.getInstance();

                        cal.add(Calendar.MONTH, -12);
                        Log.e("Bulan ",parsed1.format(cal.getTime()));

                        //cal.add(Calendar.MONTH, 1);
                        //Log.e("Bulan ",parsed1.format(cal.getTime()));
                        String dbulan="";
                        /*
                        for(int x=0;x<12;x++) {
                            cal.add(Calendar.MONTH, 1);
                            String dbulan = parsed1.format(cal.getTime());*/

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);

                            if (keterangan.equals("izin")) {
                                dbulan = parsed1.format(cal.getTime());
                                String bulan = parsed1.format(parsed.parse(obj.getString("tgl_izin").substring(0, 7)));
                                Log.e("DBulan", bulan + dbulan);
                                int j=0;
                                while(j<12){
                                    cal.add(Calendar.MONTH, 1);
                                    dbulan = parsed1.format(cal.getTime());
                                    if(dbulan.compareTo(bulan)==-1){
                                        kar = new listkaryawan_izincutisakit();
                                        kar.setBulan(dbulan);
                                        kar.setIzin(String.valueOf(0));
                                        kar.setSakit(String.valueOf(0));
                                        kar.setCuti(String.valueOf(0));
                                        kar.setDinas(String.valueOf(0));
                                        kar.setAbsen(String.valueOf(0));
                                        items.add(kar);
                                        Log.e("DBulan", "Berhasil"+dbulan.compareTo(bulan));
                                    }
                                    else{
                                        Log.e("DBulan", "T Berhasil"+dbulan.compareTo(bulan));
                                    }
                                    j++;
                                }

                                Log.e("Bulan", bulan);
                                if (i + 1 != pengsarray.length()) {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {
                                            tempbulan = bulan;
                                            kar = new listkaryawan_izincutisakit();
                                            kar.setBulan(bulan);

                                            banyakizin += Integer.parseInt(obj.getString("lama"));
                                        } else {
                                            tempbulan = bulan;
                                            kar.setIzin(String.valueOf(banyakizin));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);
                                            banyakizin = 0;
                                            kar = new listkaryawan_izincutisakit();
                                            kar.setBulan(bulan);
                                            banyakizin += Integer.parseInt(obj.getString("lama"));
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakizin += Integer.parseInt(obj.getString("lama"));
                                    }
                                } else {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {
                                            kar = new listkaryawan_izincutisakit();
                                            kar.setBulan(bulan);
                                            banyakizin += Integer.parseInt(obj.getString("lama"));
                                            kar.setIzin(String.valueOf(banyakizin));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);

                                        } else {
                                            kar.setIzin(String.valueOf(banyakizin));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);

                                            kar = new listkaryawan_izincutisakit();
                                            kar.setBulan(bulan);
                                            kar.setIzin(obj.getString("lama"));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);

                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakizin += Integer.parseInt(obj.getString("lama"));
                                        kar.setIzin(String.valueOf(banyakizin));
                                        kar.setSakit(String.valueOf(0));
                                        kar.setCuti(String.valueOf(0));
                                        kar.setDinas(String.valueOf(0));
                                        kar.setAbsen(String.valueOf(0));
                                        items.add(kar);

                                    }
                                }
                            } else if (keterangan.equals("sakit")) {
                                ada = false;
                                String bulan = obj.getString("tgl_sakit").substring(0, 7);
                                Log.e("Bulan", bulan);
                                if (i + 1 != pengsarray.length()) {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {
                                            tempbulan = bulan;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyaksakit += Integer.parseInt(obj.getString("lama"));
                                        } else {

                                            //kar.setIzin(String.valueOf(banyakizin));
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(banyaksakit));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setSakit(String.valueOf(banyaksakit));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(banyaksakit));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }

                                            }

                                            tempbulan = bulan;

                                            banyaksakit = 0;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyaksakit += Integer.parseInt(obj.getString("lama"));
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyaksakit += Integer.parseInt(obj.getString("lama"));
                                    }
                                } else {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {

                                            banyaksakit += Integer.parseInt(obj.getString("lama"));

                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(banyaksakit));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setSakit(String.valueOf(banyaksakit));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(banyaksakit));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                        } else {
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(banyaksakit));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(tempbulan)) {
                                                        items.get(j).setSakit(String.valueOf(banyaksakit));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(banyaksakit));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                            tempbulan = bulan;
                                            banyaksakit = 0;
                                            banyaksakit += Integer.parseInt(obj.getString("lama"));
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(banyaksakit));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setSakit(String.valueOf(banyaksakit));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(banyaksakit));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                }
                                            }
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyaksakit += Integer.parseInt(obj.getString("lama"));
                                        //kar.setIzin(String.valueOf(banyaksakit));
                                        //kar.setSakit(String.valueOf(banyaksakit));
                                        //kar.setAbsen(String.valueOf(i));
                                        if (items.size() == 0) {
                                            kar.setIzin(String.valueOf(0));
                                            kar.setSakit(String.valueOf(banyaksakit));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);
                                        } else {
                                            for (int j = 0; j < items.size(); j++) {
                                                if (items.get(j).getBulan().equals(bulan)) {
                                                    items.get(j).setSakit(String.valueOf(banyaksakit));
                                                    ada = true;
                                                }
                                            }
                                            if (!ada) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(banyaksakit));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            }
                                        }
                                    }
                                }
                            } else if (keterangan.equals("cuti")) {
                                ada = false;
                                String bulan = obj.getString("mulai_berlaku").substring(0, 7);
                                Log.e("Bulan", bulan);
                                if (i + 1 != pengsarray.length()) {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {
                                            tempbulan = bulan;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyakcuti += Integer.parseInt(obj.getString("lama_cuti"));
                                        } else {

                                            //kar.setIzin(String.valueOf(banyakizin));
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(banyakcuti));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setCuti(String.valueOf(banyakcuti));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(banyakcuti));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }

                                            }

                                            tempbulan = bulan;

                                            banyakcuti = 0;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyakcuti += Integer.parseInt(obj.getString("lama_cuti"));
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakcuti += Integer.parseInt(obj.getString("lama_cuti"));
                                    }
                                } else {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {

                                            banyakcuti += Integer.parseInt(obj.getString("lama_cuti"));

                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(banyakcuti));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setCuti(String.valueOf(banyakcuti));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(banyakcuti));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                        } else {
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(banyakcuti));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(tempbulan)) {
                                                        items.get(j).setCuti(String.valueOf(banyakcuti));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(banyakcuti));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                            tempbulan = bulan;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(obj.getString("lama_cuti"));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setCuti(obj.getString("lama_cuti"));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(obj.getString("lama_cuti"));
                                                    kar.setDinas(String.valueOf(0));
                                                    kar.setAbsen(String.valueOf(0));
                                                }
                                            }
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakcuti += Integer.parseInt(obj.getString("lama_cuti"));
                                        //kar.setIzin(String.valueOf(banyaksakit));
                                        //kar.setSakit(String.valueOf(banyaksakit));
                                        //kar.setAbsen(String.valueOf(i));
                                        if (items.size() == 0) {
                                            kar.setIzin(String.valueOf(0));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(banyakcuti));
                                            kar.setDinas(String.valueOf(0));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);
                                        } else {
                                            for (int j = 0; j < items.size(); j++) {
                                                if (items.get(j).getBulan().equals(bulan)) {
                                                    items.get(j).setCuti(String.valueOf(banyakcuti));
                                                    ada = true;
                                                }
                                            }
                                            if (!ada) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(banyakcuti));
                                                kar.setDinas(String.valueOf(0));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            }
                                        }
                                    }
                                }
                            } else {
                                ada = false;
                                String bulan = obj.getString("tgl_dinas").substring(0, 7);
                                Log.e("Bulan", bulan);
                                if (i + 1 != pengsarray.length()) {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {
                                            tempbulan = bulan;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyakdinas += Integer.parseInt(obj.getString("lama"));
                                        } else {

                                            //kar.setIzin(String.valueOf(banyakizin));
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(banyakdinas));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setDinas(String.valueOf(banyakdinas));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(banyakdinas));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }

                                            }

                                            tempbulan = bulan;

                                            banyakdinas = 0;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                }
                                            }
                                            banyakdinas += Integer.parseInt(obj.getString("lama"));
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakdinas += Integer.parseInt(obj.getString("lama"));
                                    }
                                } else {
                                    if (!tempbulan.equals(bulan)) {
                                        if (tempbulan.equals("")) {

                                            banyakdinas += Integer.parseInt(obj.getString("lama"));

                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(banyakdinas));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setDinas(String.valueOf(banyakdinas));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(banyakdinas));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                        } else {
                                            if (items.size() == 0) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(banyakdinas));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(tempbulan)) {
                                                        items.get(j).setDinas(String.valueOf(banyakdinas));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(String.valueOf(banyakdinas));
                                                    kar.setAbsen(String.valueOf(0));
                                                    items.add(kar);
                                                }
                                            }
                                            tempbulan = bulan;
                                            if (items.size() == 0) {
                                                kar = new listkaryawan_izincutisakit();
                                                kar.setBulan(bulan);
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(obj.getString("lama"));
                                                kar.setAbsen(String.valueOf(0));
                                            } else {
                                                for (int j = 0; j < items.size(); j++) {
                                                    if (items.get(j).getBulan().equals(bulan)) {
                                                        items.get(j).setDinas(obj.getString("lama"));
                                                        ada = true;
                                                    }
                                                }
                                                if (!ada) {
                                                    kar = new listkaryawan_izincutisakit();
                                                    kar.setBulan(bulan);
                                                    kar.setIzin(String.valueOf(0));
                                                    kar.setSakit(String.valueOf(0));
                                                    kar.setCuti(String.valueOf(0));
                                                    kar.setDinas(obj.getString("lama"));
                                                    kar.setAbsen(String.valueOf(0));
                                                }
                                            }
                                        }

                                    } else if (tempbulan.equals(bulan)) {
                                        banyakdinas += Integer.parseInt(obj.getString("lama"));
                                        //kar.setIzin(String.valueOf(banyaksakit));
                                        //kar.setSakit(String.valueOf(banyaksakit));
                                        //kar.setAbsen(String.valueOf(i));
                                        if (items.size() == 0) {
                                            kar.setIzin(String.valueOf(0));
                                            kar.setSakit(String.valueOf(0));
                                            kar.setCuti(String.valueOf(0));
                                            kar.setDinas(String.valueOf(banyakdinas));
                                            kar.setAbsen(String.valueOf(0));
                                            items.add(kar);
                                        } else {
                                            for (int j = 0; j < items.size(); j++) {
                                                if (items.get(j).getBulan().equals(bulan)) {
                                                    items.get(j).setDinas(String.valueOf(banyakdinas));
                                                    ada = true;
                                                }
                                            }
                                            if (!ada) {
                                                kar.setIzin(String.valueOf(0));
                                                kar.setSakit(String.valueOf(0));
                                                kar.setCuti(String.valueOf(0));
                                                kar.setDinas(String.valueOf(banyakdinas));
                                                kar.setAbsen(String.valueOf(0));
                                                items.add(kar);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //}


                            /*
                            JSONObject obj = pengsarray.getJSONObject(i);
                            String thn_izin=obj.getString("tgl_izin").substring(0,4);
                            String bln_izin=obj.getString("tgl_izin").substring(5,7);
                            String tgl_izin=obj.getString("tgl_izin").substring(8,10);
                            String totalizin=""+pengsarray.length();
                            Log.e(TAG, "data json result" + "Berhasil1");


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
                            /*
                            if(!tempcall.equals(obj.getString("tgl_izin").substring(5,7)) && banyakizin==0){
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
                            else if(tempcall.equals(obj.getString("tgl_izin").substring(5,7))){
                                banyakizin++;
                                Log.e(TAG, "data json result" + "Berhasil4");
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
                                banyakizin=0;
                                Log.e(TAG, "data json result" + "Berhasil5");
                                i--;
                            }
                            */

                            /*
                            if(temp.equals("")){
                                kar.setBulan(bln_izin);
                                kar.setIzin(""+i+1);
                            }
                            else if(!temp.equals(bln_izin)){
                                kar.setBulan(tempbulan);
                                kar.setIzin(""+banyak);
                                banyak=0;
                            }
                            else if(temp.equals(bln_izin)){
                                tempbulan=bln_izin;
                                banyak++;
                            }
                            */
                        //kar.setIzin();


                        Collections.sort(items, new Comparator<listkaryawan_izincutisakit>() {
                            @Override
                            public int compare(listkaryawan_izincutisakit listkaryawan_izincutisakit, listkaryawan_izincutisakit t1) {
                                //Log.e("ABSEN",""+listperingkatkaryawan.getAbsen().compareTo(t1.getAbsen()));

                                return listkaryawan_izincutisakit.getBulan().compareTo(t1.getBulan());

                            }
                        });
                        if(keterangan.equals("dinas")) {
                            if(items.size()>0){

                                adapter = new Adapterviewkaryawan(viewkaryawan.this, items, ItemAnimation.LEFT_RIGHT);
                                recyclerView.setLayoutManager(new LinearLayoutManager(viewkaryawan.this, LinearLayoutManager.HORIZONTAL, false));
                                recyclerView.setAdapter(adapter);
                                PagerSnapHelper snapHelper = new PagerSnapHelper();
                                snapHelper.attachToRecyclerView(recyclerView);
                            }
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

}
