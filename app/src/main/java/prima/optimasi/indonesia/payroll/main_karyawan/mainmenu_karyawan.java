package prima.optimasi.indonesia.payroll.main_karyawan;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.activity.MainMenu;
import prima.optimasi.indonesia.payroll.main_kabag.ActivityAbsensi;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentCekGaji;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityLogAbsensi;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityPengajuan;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityPengumuman;
import qrcodescanner.QrCodeActivity;

import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentAbsensi;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentPengajuan;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class mainmenu_karyawan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CoordinatorLayout parent_view;
    Adaptermenujabatan listAdapter;
    ExpandableListView expListView;
    ProgressDialog loadingdata;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    SharedPreferences prefs;

    String[] tabTitles = new String []{"Pengumuman", "Cek Gaji","Log Absensi","Pengajuan"};
    //R.drawable.baseline_account_circle_black_24dp,
    int[] iconstyle = new int[]{R.drawable.baseline_announcement_black_24dp,R.drawable.baseline_monetization_on_black_24dp,R.drawable.baseline_pie_chart_black_24dp,R.drawable.baseline_assignment_black_24dp};

    ViewPager pager;
    TabLayout tabpager;
    Double gaji, potongan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingdata = new ProgressDialog(this);
        loadingdata.setTitle("Please Wait");
        loadingdata.setMessage("Loading Data...");
        loadingdata.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_karyawan);

        parent_view=findViewById(R.id.parent_view);
        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("karyawan");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        generator initializedata = new generator(mainmenu_karyawan.this);

        CircularImageView imageuser =findViewById(R.id.imageView);

        if(prefs.getString("profileimage","").equals(generator.profileurl)){
            Picasso.get().load("http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png").transform(new CircleTransform()).into(imageuser);
        }
        else {
            Picasso.get().load(prefs.getString("profileimage","")).transform(new CircleTransform()).into(imageuser);
        }
        Log.e("picture", "ppicture: "+ prefs.getString("profileimage",""));

        TextView username = findViewById(R.id.username);
        TextView borndate = findViewById(R.id.prof_tempat_lahir);
        TextView pengumumanteks = findViewById(R.id.pengumumanteks);
        TextView log_absensiteks = findViewById(R.id.log_absensiteks);
        TextView cekgajiteks = findViewById(R.id.cekgajiteks);
        TextView pengajuanteks = findViewById(R.id.pengajuanteks);
        TextView cekjadwalteks = findViewById(R.id.cekjadwalteks);
        TextView absensiteks = findViewById(R.id.absensiteks);

        FloatingActionButton pengumuman=findViewById(R.id.pengumuman);
        FloatingActionButton log_absensi=findViewById(R.id.log_absensi);
        FloatingActionButton cekgaji=findViewById(R.id.cekgaji);
        FloatingActionButton pengajuan=findViewById(R.id.pengajuan);
        FloatingActionButton cek_jadwal=findViewById(R.id.cekjadwal);
        FloatingActionButton absensi=findViewById(R.id.absensi);

        pengumumanteks.setText("Pengumuman");
        log_absensiteks.setText("Log Absensi");
        cekgajiteks.setText("Cek Gaji");
        pengajuanteks.setText("Pengajuan");
        cekjadwalteks.setText("Cek Jadwal");
        absensiteks.setText("Absensi");

        pengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mainmenu_karyawan.this, ActivityPengumuman.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentPengumuman()).addToBackStack("Home").commit();*/
            }
        });
        log_absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mainmenu_karyawan.this,ActivityLogAbsensi.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentAbsensi()).addToBackStack("Home").commit();*/
            }
        });
        cekgaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gaji=0.0d;
                potongan=0.0d;
                retrivegaji gajis=new retrivegaji(mainmenu_karyawan.this);
                gajis.execute();
            }
        });
        pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mainmenu_karyawan.this,ActivityPengajuan.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentPengajuan()).addToBackStack("Home").commit();*/
            }
        });
        cek_jadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(mainmenu_karyawan.this, cekjadwal.class);
                startActivity(a);
            }
        });

        absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Jabatan : ", prefs.getString("jabatan",""));
                if(prefs.getString("jabatan","1").equals("4")){

                    boolean permissionGranted = ActivityCompat.checkSelfPermission(mainmenu_karyawan.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                    if(permissionGranted) {
                        generator.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        generator.location = generator.lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        Intent intent = new Intent(mainmenu_karyawan.this, ActivityAbsensi.class);
                        intent.putExtra("jabatan", "security");
                        startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions(mainmenu_karyawan.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4011);
                    }
                }
                else {
                    LinearLayout l = (LinearLayout) LayoutInflater.from(mainmenu_karyawan.this).inflate(R.layout.layout_barcode, null);

                    ImageView barcode = l.findViewById(R.id.barcodekaryawan);


                    String text = prefs.getString("kodekaryawan", "");
                    Log.e("data json", "onClick: " + prefs.getString("kodekaryawan", ""));
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        barcode.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    AlertDialog dialog1 = new AlertDialog.Builder(mainmenu_karyawan.this).setTitle("Absensi").setView(l).create();

                    dialog1.show();
                }
            }
        });

        ImageView icon_born=findViewById(R.id.iconborn);
        LinearLayout show=findViewById(R.id.showbutton);
        FloatingActionButton logout=findViewById(R.id.logout);
        final CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int min_height = ViewCompat.getMinimumHeight(collapsing_toolbar) * 2;
                float scale = (float) (min_height + verticalOffset) / min_height;
                imageuser.setScaleX(scale >= 0 ? scale : 0);
                imageuser.setScaleY(scale >= 0 ? scale : 0);
                username.setScaleX(scale >= 0 ? scale : 0);
                username.setScaleY(scale >= 0 ? scale : 0);
                icon_born.setScaleX(scale >= 0 ? scale : 0);
                icon_born.setScaleY(scale >= 0 ? scale : 0);
                borndate.setScaleX(scale >= 0 ? scale : 0);
                borndate.setScaleY(scale >= 0 ? scale : 0);
                //show.setScaleX(scale >= 0 ? 0 : scale*4);
                //show.setScaleY(scale >= 0 ? 0 : scale*4);
            }

        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.logout(mainmenu_karyawan.this,"karyawan");
                /*
                FirebaseMessaging.getInstance().unsubscribeFromTopic("kabag");

                Intent logout = new Intent(mainmenu_karyawan.this,activity_login.class);
                SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

                if(prefs.getInt("statustoken",0)==0){

                }
                else {
                    generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(mainmenu_karyawan.this,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
                    unregistertokentoserver.execute();
                }


                SharedPreferences.Editor edit = prefs.edit();

                edit.putString("iduser","");
                edit.putString("username","");
                edit.putString("jabatan","");
                edit.putString("level","");
                edit.putString("tempatlahir","");
                edit.putString("profileimage","");
                edit.putString("Authorization","");
                edit.putString("kodekaryawan","");

                edit.commit();

                startActivity(logout);*/
            }
        });
        /*
        pengumuman.setBackgroundResource(R.drawable.baseline_announcement_black_24dp);
        absensi.setBackgroundResource(R.drawable.baseline_monetization_on_black_24dp);
        cekgaji.setBackgroundResource(R.drawable.baseline_pie_chart_black_24dp);
        pengajuan.setBackgroundResource(R.drawable.baseline_assignment_black_24dp);
        */
        username.setText(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("username",""));

        if(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("tempatlahir","").equals("")){

            borndate.setText("Not Available");

        }else{

            borndate.setText(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("tempatlahir",""));

        }
        /*
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout linear = (RelativeLayout) navigationView.getHeaderView(0);



        TextView username = linear.findViewById(R.id.username);
        TextView borndate = linear.findViewById(R.id.prof_tempat_lahir);

        LinearLayout setprofile = linear.findViewById(R.id.lyt_profile);

        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_profil=new Intent(mainmenu_karyawan.this,profil_karyawan.class);
                startActivity(intent_profil);

            }
        });


        username.setText(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("username",""));

        if(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("tempatlahir","").equals("")){

            borndate.setText("Not Available");

        }else{

            borndate.setText(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("tempatlahir",""));

        }


        tabpager = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.viewpager);



        ExamplePagerAdapter adapter = new ExamplePagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        tabpager.setupWithViewPager(pager);

        for (int i = 0; i < tabpager.getTabCount(); i++) {
            //noinspection ConstantConditions
            View v = LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
            TextView tv=v.findViewById(R.id.texttab);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setText(tabTitles[i]);

            ImageView img = v.findViewById(R.id.icontab);
            img.setImageDrawable(getResources().getDrawable(iconstyle[i]));
            tabpager.getTabAt(i).setCustomView(v);
        }

        preparekaryawan();

        listAdapter = new Adaptermenujabatan(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setGroupIndicator(null);
        expListView.setChildIndicator(null);
        expListView.setChildDivider(null);
        expListView.setDivider(null);
        expListView.setDividerHeight(0);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(listDataHeader.get(groupPosition).equals("Pengumuman")){
                    pager.setCurrentItem(1);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("List Karyawan")){
                    pager.setCurrentItem(2);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Home")){
                    pager.setCurrentItem(0);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Profil")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Absensi")){

                    drawer.closeDrawer(Gravity.START);

                    if(prefs.getString("jabatan","1").equals("4")){

                        String[] colors = {"Absensi","Scan Karyawan","Check IN","Break OUT","Break IN","Check OUT"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_karyawan.this);
                        builder.setTitle("Absensi");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    LinearLayout l = (LinearLayout) LayoutInflater.from(mainmenu_karyawan.this).inflate(R.layout.layout_barcode,null);

                                    ImageView barcode = l.findViewById(R.id.barcodekaryawan);


                                    String text=prefs.getString("kodekaryawan","");
                                    Log.e("data json", "onClick: "+prefs.getString("kodekaryawan","") );
                                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                    try {
                                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,400,400);
                                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                        barcode.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }



                                    AlertDialog dialog1 = new AlertDialog.Builder(mainmenu_karyawan.this).setTitle("Absensi").setView(l).create();

                                    dialog1.show();
                                }
                                else {
                                    Intent a = new Intent(mainmenu_karyawan.this,QrCodeActivity.class);
                                    a.putExtra("absensi",which);
                                    a.putExtra("security",1);
                                    a.putExtra("keepalive",1);
                                    startActivity(a);
                                }
                            }
                        });
                        builder.show();
                    }
                    else{
                        drawer.closeDrawer(Gravity.START);

                        LinearLayout l = (LinearLayout) LayoutInflater.from(mainmenu_karyawan.this).inflate(R.layout.layout_barcode,null);

                        ImageView barcode = l.findViewById(R.id.barcodekaryawan);



                        String text=prefs.getString("kodekaryawan","");
                        Log.e("data json", "onClick: "+prefs.getString("kodekaryawan","") );
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,400,400);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            barcode.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }



                        AlertDialog dialog1 = new AlertDialog.Builder(mainmenu_karyawan.this).setTitle("Absensi").setView(l).create();

                        dialog1.show();

                    }





                }
                else if(listDataHeader.get(groupPosition).equals("Cek Jadwal")){

                    generator.getjadwal get = new generator.getjadwal(mainmenu_karyawan.this,prefs.getString("kodekaryawan",""),prefs.getString("Authorization",""));
                    get.execute();

                }
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });




        */
        if(loadingdata.isShowing()){
            loadingdata.dismiss();
        }
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
                        if(!sumGB.getString("gajiBersih").equals("null") || !sumGT.getString("gajiTotal").equals("null")) {
                            gaji += Double.parseDouble(sumGB.getString("gajiBersih"));
                            potongan += Double.parseDouble(sumGT.getString("gajiTotal"));
                            potongan -= Double.parseDouble(sumGB.getString("gajiBersih"));
                        }
                        else{
                            gaji=0.0d;
                            potongan=0.0d;
                        }

                        showgaji("Gaji",gaji,potongan);


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

    public void showgaji(String teks, Double Gaji, Double Potongan){
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");

        final Dialog gajisendiri = new Dialog(mainmenu_karyawan.this);
        gajisendiri.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        gajisendiri.setContentView(R.layout.dialog_gajisendiri);
        gajisendiri.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(gajisendiri.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView gajiteks=gajisendiri.findViewById(R.id.gajiteks);
        gajiteks.setText(teks);
        ((ImageButton) gajisendiri.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gajisendiri.dismiss();
            }
        });
        ((Button) gajisendiri.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gajisendiri.dismiss();
            }
        });

        final TextView gajibersih = (TextView) gajisendiri.findViewById(R.id.gajibersih);
        final TextView gajipotongan = (TextView) gajisendiri.findViewById(R.id.gajipotongan);

        gajibersih.setText("RP "+formatter.format(Gaji));
        gajipotongan.setText("RP "+formatter.format(Potongan));
        gajisendiri.show();
        gajisendiri.getWindow().setAttributes(lp);
    }

    /*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item=menu.findItem(R.id.action_settings);
        item.setTitle("About");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4011: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    generator.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    generator.location = generator.lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    Intent intent = new Intent(mainmenu_karyawan.this, ActivityAbsensi.class);
                    intent.putExtra("jabatan", "security");
                    startActivity(intent);
                }
            }
        }
    }

}
