package prima.optimasi.indonesia.payroll.main_kabag;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
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
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapterviewkaryawan;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentAbsensi;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentCekGaji;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentPengajuan;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentHome;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pengajian;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.absence.facedetection;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityLogAbsensi;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityPengajuan;
import prima.optimasi.indonesia.payroll.universal.activity.ActivityPengumuman;
import prima.optimasi.indonesia.payroll.universal.adapter.AdapterListKaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import qrcodescanner.QrCodeActivity;

public class mainmenu_kabag extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int count = 0;

    AdapterListKaryawan adapter;
    SharedPreferences prefs;
    CoordinatorLayout parent_view;
    Adaptermenujabatan listAdapter;
    ExpandableListView expListView;
    ProgressDialog loadingdata;
    TabLayout tabpager;
    ViewPager pager;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<listkaryawan> items;
    listkaryawan kar;
    String[] tabTitles = new String[]{" Home", "  Profil", "Pengumuman", "Anggota", "Cek Gaji", "Pengajuan"};
    int[] iconstyle = new int[]{R.drawable.baseline_home_black_18dp, R.drawable.baseline_account_circle_black_24dp, R.drawable.baseline_announcement_black_24dp, R.drawable.ic_baseline_people_24px, R.drawable.baseline_monetization_on_black_24dp, R.drawable.baseline_assignment_black_24dp};


    Menu tempmenu;
    String TAG = "ABSENSI";
    Double gaji, potongan;
    int posisi;

    FloatingActionButton absensi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        loadingdata = new ProgressDialog(this);
        loadingdata.setTitle("Please Wait");
        loadingdata.setMessage("Loading Data...");
        loadingdata.show();




        prefs = getSharedPreferences("poipayroll", MODE_PRIVATE);

        if (!prefs.getString("absensikode", "").equals("")){
            prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

            SharedPreferences.Editor edit = prefs.edit();


            absensithrowlocation throwdata = new absensithrowlocation(mainmenu_kabag.this, prefs.getInt("absensitype",0),
                    prefs.getString("absensikode",""),
                    prefs.getString("absensilongitude",""),
                    prefs.getString("absensilatitude",""),
                    prefs.getBoolean("absensisecurity",false));
            throwdata.execute();

            edit.apply();
        }

            if (prefs.getInt("statustoken", 0) == 0) {
                generator.registertokentoserver register = new generator.registertokentoserver(this, prefs.getString("tokennotif", ""));
                register.execute();

                FirebaseMessaging.getInstance().subscribeToTopic("kabag");
            }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_kabag);

        Log.e("sharence status", "onCreate: " + prefs.getInt("statustoken", 0));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        generator initializedata = new generator(mainmenu_kabag.this);


        CircularImageView imageuser = findViewById(R.id.imageView);

        if (prefs.getString("profileimage", "").equals(generator.profileurl)) {
            Picasso.get().load("http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png").transform(new CircleTransform()).into(imageuser);
        } else {
            Picasso.get().load(prefs.getString("profileimage", "")).transform(new CircleTransform()).into(imageuser);
        }
        Log.e("picture", "ppicture: " + prefs.getString("profileimage", ""));

        parent_view = findViewById(R.id.parent_view);
        TextView username = findViewById(R.id.username);
        TextView borndate = findViewById(R.id.prof_tempat_lahir);
        TextView pengumumanteks = findViewById(R.id.pengumumanteks);
        TextView log_absensiteks = findViewById(R.id.log_absensiteks);
        TextView anggotateks = findViewById(R.id.anggotateks);
        TextView cekgajiteks = findViewById(R.id.cekgajiteks);
        TextView pengajuanteks = findViewById(R.id.pengajuanteks);
        TextView absensiteks = findViewById(R.id.absensiteks);
        TextView cekjadwalteks = findViewById(R.id.cekjadwalteks);

        FloatingActionButton pengumuman = findViewById(R.id.pengumuman);
        FloatingActionButton log_absensi = findViewById(R.id.log_absensi);
        FloatingActionButton anggota = findViewById(R.id.anggota);
        FloatingActionButton cekgaji = findViewById(R.id.cekgaji);
        FloatingActionButton pengajuan = findViewById(R.id.pengajuan);
        absensi = findViewById(R.id.absensi);
        FloatingActionButton cekjadwal = findViewById(R.id.cekjadwal);

        pengumumanteks.setText("Pengumuman");
        log_absensiteks.setText("Log Absensi");
        anggotateks.setText("Anggota");
        cekgajiteks.setText("Cek Gaji");
        pengajuanteks.setText("Pengajuan");
        absensiteks.setText("Absensi");
        cekjadwalteks.setText("Cek Jadwal");

        LinearLayout linear_view = findViewById(R.id.linear_view);
        pengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainmenu_kabag.this, ActivityPengumuman.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentPengumuman()).addToBackStack("Home").commit();*/
            }
        });
        log_absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainmenu_kabag.this, ActivityLogAbsensi.class);
                startActivity(intent);

                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentAbsensi()).addToBackStack("Home").commit();*/
            }
        });
        anggota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainmenu_kabag.this, Activity_Anggota.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentEmployee()).addToBackStack("Home").commit();*/
            }
        });
        cekgaji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] colors = {"Gaji Karyawan", "Gaji Sendiri"};


                AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                builder.setTitle("Cek Gaji");

                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mainmenu_kabag.this, colors[which], Toast.LENGTH_LONG);

                        if (which == 1) {
                            gaji = 0.0d;
                            potongan = 0.0d;
                            posisi = 0;
                            retrivegaji gajis = new retrivegaji(mainmenu_kabag.this, prefs.getString("id", ""));
                            gajis.execute();
                        } else {
                            gaji = 0.0d;
                            potongan = 0.0d;
                            posisi = 0;
                            retrivekaryawan kar = new retrivekaryawan(mainmenu_kabag.this);
                            kar.execute();
                        }
                    }
                });
                builder.show();
            }
        });
        pengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainmenu_kabag.this, ActivityPengajuan.class);
                startActivity(intent);
                /*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.parent_view,new FragmentPengajuan()).addToBackStack("Home").commit();*/
            }
        });
        absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean permissionGranted = ActivityCompat.checkSelfPermission(mainmenu_kabag.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                if(permissionGranted) {


                    //generator.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    //generator.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    //generator.location = generator.lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    Intent intent = new Intent(mainmenu_kabag.this, ActivityAbsensi.class);
                    intent.putExtra("jabatan", "kabag");
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(mainmenu_kabag.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4011);
                }



            }
        });
        cekjadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] colors = {"Karyawan", "Sendiri"};


                AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                builder.setTitle("Cek Jadwal");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            Intent a = new Intent(mainmenu_kabag.this, cekjadwal.class);
                            a.putExtra("cekjadwal", which);
                            startActivity(a);
                        } else {
                            Intent a = new Intent(mainmenu_kabag.this, cekjadwal.class);
                            a.putExtra("cekjadwal", which);
                            startActivity(a);
                        }
                    }
                });
                builder.show();

            }
        });
        ImageView icon_born = findViewById(R.id.iconborn);
        LinearLayout show = findViewById(R.id.showbutton);
        FloatingActionButton logout = findViewById(R.id.logout);

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
                generator.logout(mainmenu_kabag.this, "kabag");
                /*
                FirebaseMessaging.getInstance().unsubscribeFromTopic("kabag");

                Intent logout = new Intent(mainmenu_kabag.this,activity_login.class);
                SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

                if(prefs.getInt("statustoken",0)==0){

                }
                else {
                    generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(mainmenu_kabag.this,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
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

        username.setText(getSharedPreferences("poipayroll", MODE_PRIVATE).getString("username", ""));

        if (getSharedPreferences("poipayroll", MODE_PRIVATE).getString("tempatlahir", "").equals("")) {

            borndate.setText("Not Available");

        } else {

            borndate.setText(getSharedPreferences("poipayroll", MODE_PRIVATE).getString("tempatlahir", ""));

        }



        /*
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        tabpager = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.viewpager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout linear = (RelativeLayout) navigationView.getHeaderView(0);
        CircularImageView imageuser = linear.findViewById(R.id.imageView);

        if(prefs.getString("profileimage","").equals(generator.profileurl)){

        }
        else {
            Picasso.get().load(prefs.getString("profileimage","")).transform(new CircleTransform()).into(imageuser);
        }
        Log.e("picture", "ppicture: "+ prefs.getString("profileimage",""));

        TextView username = linear.findViewById(R.id.username);
        TextView borndate = linear.findViewById(R.id.prof_tempat_lahir);



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

        tabpager.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position==2){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(true);
                    }
                }
                else {
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(false);
                        tempmenu.findItem(R.id.action_add).setVisible(false);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (int i = 0; i < tabpager.getTabCount(); i++) {
            //noinspection ConstantConditions
            View v = LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
            TextView tv=v.findViewById(R.id.texttab);
            tv.setTextColor(Color.WHITE);
            tv.setText(tabTitles[i]);

            ImageView img = v.findViewById(R.id.icontab);
            img.setImageDrawable(getResources().getDrawable(iconstyle[i]));
            tabpager.getTabAt(i).setCustomView(v);
        }

        preparekabag();

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
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
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
                    String[] colors = {"Check IN", "Break OUT","Break IN","Check OUT","Extra IN","Extra OUT","Absensi","Absensi Wajah"};


                    AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                    builder.setTitle("Absensi");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==6) {
                                LinearLayout l = (LinearLayout) LayoutInflater.from(mainmenu_kabag.this).inflate(R.layout.layout_barcode,null);

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



                                AlertDialog dialog1 = new AlertDialog.Builder(mainmenu_kabag.this).setTitle("Absensi").setView(l).create();

                                dialog1.show();
                            }
                            else if(which==7) {
                                Intent face = new Intent(mainmenu_kabag.this,facedetection.class);
                                startActivity(face);
                            }
                            else {
                                Intent a = new Intent(mainmenu_kabag.this,QrCodeActivity.class);
                                a.putExtra("absensi",which);
                                a.putExtra("security",0);
                                a.putExtra("keepalive",1);
                                startActivity(a);
                            }
                        }
                    });
                    builder.show();
                    /*if (ContextCompat.checkSelfPermission(mainmenu_kabag.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(mainmenu_kabag.this, new String[]{Manifest.permission.CAMERA}, 202);
                        if (ContextCompat.checkSelfPermission(mainmenu_kabag.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED){
                            AlertDialog dialog = new AlertDialog.Builder(mainmenu_kabag.this).setTitle("Permission Required").setMessage("Camera Permission Required !!").show();
                        }
                        else {
                            drawer.closeDrawer(Gravity.START);
                            String[] colors = {"Face Registration", "Face Recognition", "QR Code"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                            builder.setTitle("Pilihan");
                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which==0){
                                        Intent a = new Intent(mainmenu_kabag.this,facecapture.class);
                                        startActivity(a);
                                    }else if(which==1){
                                        Intent a = new Intent(mainmenu_kabag.this,facedetection.class);
                                        startActivity(a);
                                    }else if(which==2){
                                        Intent i = new Intent(mainmenu_kabag.this,QrCodeActivity.class);
                                        startActivityForResult( i,104);
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                    else {
                        drawer.closeDrawer(Gravity.START);
                        String[] colors = {"Face Registration", "Face Recognition", "QR Code"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                        builder.setTitle("Pilihan");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    Intent a = new Intent(mainmenu_kabag.this,facecapture.class);
                                    startActivity(a);
                                }else if(which==1){
                                    Intent a = new Intent(mainmenu_kabag.this,facedetection.class);
                                    startActivity(a);
                                }else if(which==2){
                                    Intent i = new Intent(mainmenu_kabag.this,QrCodeActivity.class);
                                    startActivityForResult( i,104);
                                }
                            }
                        });
                        builder.show();
                    }
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
        if (loadingdata.isShowing()) {
            loadingdata.dismiss();
        }
    }

    private class retrivekaryawan extends AsyncTask<Void, Integer, String> {
        String response = "";
        String error = "";
        String username = "";
        String password = "";
        SharedPreferences prefs;
        JSONObject result = null;
        ProgressDialog dialog;
        String urldata = generator.kabaggrupkaryawanurl;
        String passeddata = "";

        public retrivekaryawan(Context context) {
            prefs = context.getSharedPreferences("poipayroll", Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            this.dialog.show();
            super.onPreExecute();
            this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            try {
                this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("kode", prefs.getString("kodekaryawan", ""))
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization", prefs.getString("Authorization", ""))
                            .post(body)
                            .url(urldata)
                            .build();
                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsonObject = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonObject = null;
                    }

                    if (responses == null) {
                        jsonObject = null;
                        Log.e(TAG, "NULL");
                    } else {

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

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        boolean status = result.getBoolean("status");
                        if (status) {
                            /*
                            "data":[{
                                "id":7, "kode_karyawan":"EMP-07", "idfp":"KRY0007", "nama":
                                "Sparno", "alamat":"Jln.Surabaya", "tempat_lahir":"Bogor",
                                        "tgl_lahir":"1994-07-02T00:00:00.000Z", "telepon":
                                "000000000", "no_wali":"0000000000", "email":"modomodo@gmail.com",
                                        "tgl_masuk":"2018-08-04T00:00:00.000Z", "kelamin":
                                "laki-laki", "status_nikah":"Menikah", "pendidikan":"Sarjana S1",
                                        "wn":"Indonesia", "agama":"Islam", "shift":
                                "ya", "status_kerja":"aktif", "ibu_kandung":"Meiling", "suami_istri":
                                "Lastri",
                                        "tanggungan":3, "npwp":"001011101010", "gaji":0, "rekening":
                                "00010000", "id_bank":1, "id_departemen":16, "id_jabatan":10,
                                        "id_grup":6, "id_golongan":25, "atas_nama":"Sparno", "foto":
                                "17a490b3ab8e38e296e3b1b18a433eb9.jpg", "id_cabang":2,
                                        "start_date":null, "expired_date":null, "jab_index":
                                0, "kontrak":"tidak", "file_kontrak":"", "otoritas":2,
                                        "periode_gaji":"2-Mingguan", "qrcode_file":
                                "4b267aa6e56888580342445702d212f3.png"
                            },
                            {
                                "id":8, "kode_karyawan":"EMP-08",
                                    "idfp":"KRY0008", "nama":"Aston", "alamat":
                                "Jln.Melati1", "tempat_lahir":"Medan", "tgl_lahir":
                                "1992-07-02T00:00:00.000Z",
                                        "telepon":"000000000", "no_wali":"090909090909", "email":
                                "gagaga@gmail.com", "tgl_masuk":"2018-07-09T00:00:00.000Z",
                                    "kelamin":"perempuan", "status_nikah":"Menikah", "pendidikan":
                                "Sarjana S3", "wn":"Indonesia", "agama":"Islam", "shift":"ya",
                                    "status_kerja":"aktif", "ibu_kandung":"Lisa", "suami_istri":
                                "sadaa", "tanggungan":2, "npwp":"0000101010101", "gaji":1,
                                    "rekening":"0101010101011", "id_bank":1, "id_departemen":
                                1, "id_jabatan":23, "id_grup":6, "id_golongan":60, "atas_nama":
                                "Aston",
                                        "foto":"abe6ae2097f676a4e7d7869a75139fb9.jpg", "id_cabang":
                                1, "start_date":null, "expired_date":null, "jab_index":0,
                                    "kontrak":"tidak", "file_kontrak":"", "otoritas":
                                3, "periode_gaji":"Bulanan", "qrcode_file":""
                            },
                            {
                                "id":10, "kode_karyawan":"EMP-10",
                                    "idfp":"KYR0010", "nama":"Sulastri Ningsih", "alamat":
                                "JLn.Bambu1", "tempat_lahir":"sadsad", "tgl_lahir":
                                "1993-12-15T00:00:00.000Z",
                                        "telepon":"01010101000", "no_wali":"01010000100", "email":
                                "asdsadasdllololol@gmail.com", "tgl_masuk":
                                "2018-07-03T00:00:00.000Z",
                                        "kelamin":"perempuan", "status_nikah":
                                "Menikah", "pendidikan":"Sarjana S2", "wn":"Indonesia", "agama":
                                "Islam", "shift":"ya",
                                    "status_kerja":"aktif", "ibu_kandung":"sad", "suami_istri":
                                "asd", "tanggungan":1, "npwp":"2131232321", "gaji":0, "rekening":
                                "8282888828282",
                                        "id_bank":3, "id_departemen":1, "id_jabatan":23, "id_grup":
                                6, "id_golongan":25, "atas_nama":"Sulastri Ningsih",
                                    "foto":"35b234cd6919cd1dabc2c97f0af16436.jpg", "id_cabang":
                                2, "start_date":null, "expired_date":null, "jab_index":0, "kontrak":
                                "tidak",
                                        "file_kontrak":"", "otoritas":1, "periode_gaji":
                                "Bulanan", "qrcode_file":"8a28fb7a5902cc42878dfcbca26bceec.png"
                            },
                            {
                                "id":28,
                                    "kode_karyawan":"EMP-100", "idfp":"KRY0016", "nama":
                                "Queen", "alamat":"Jln.Jambu", "tempat_lahir":"Medan", "tgl_lahir":
                                "2018-09-04T00:00:00.000Z",
                                        "telepon":"0001010101", "no_wali":"0020020002020", "email":
                                "asdsadsadsakjhuguhj@gmail.com", "tgl_masuk":
                                "2018-09-27T00:00:00.000Z",
                                        "kelamin":"laki-laki", "status_nikah":
                                "Menikah", "pendidikan":"Diploma 1", "wn":"Indonesia", "agama":
                                "Islam", "shift":"ya", "status_kerja":"aktif",
                                    "ibu_kandung":"Lastri", "suami_istri":"Suylaiman", "tanggungan":
                                1, "npwp":"009090909", "gaji":1000, "rekening":"80000008000101000",
                                    "id_bank":3, "id_departemen":13, "id_jabatan":27, "id_grup":
                                6, "id_golongan":58, "atas_nama":"Queen", "foto":"", "id_cabang":
                                1, "start_date":null,
                                    "expired_date":null, "jab_index":1, "kontrak":
                                "tidak", "file_kontrak":"", "otoritas":5, "periode_gaji":"Bulanan",
                                    "qrcode_file":"00219b1037b0f71e2a32d8666cb4bee3.png"
                            }]
                            */
                            JSONArray pengsarray = result.getJSONArray("data");

                            items = new ArrayList<>();
                            String tempcall = "";
                            int panjang = 0;
                            for (int i = 0; i < pengsarray.length(); i++) {
                                JSONObject obj = pengsarray.getJSONObject(i);
                                if (!prefs.getString("kodekaryawan", "").equals(obj.getString("kode_karyawan"))) {
                                    if (!obj.getString("otoritas").equals("2")) {
                                        kar = new listkaryawan();
                                        kar.setIskar(obj.getString("id"));

                                        kar.setNama(obj.getString("nama"));
                                        kar.setJabatan(obj.getString("jabatan"));
                                        kar.setJenis("cekgaji");
                                        if (!obj.getString("foto").equals("")) {
                                            kar.setImagelink(generator.profileurl + obj.getString("foto"));
                                        } else {
                                            kar.setImagelink("");
                                        }
                                        items.add(kar);
                                    }
                                }
                            }

                            final Dialog listkar = new Dialog(mainmenu_kabag.this);
                            listkar.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                            listkar.setContentView(R.layout.dialog_listkaryawan);
                            listkar.setCancelable(true);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(listkar.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                            ((ImageButton) listkar.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listkar.dismiss();
                                }
                            });


                            final RecyclerView rvkar = (RecyclerView) listkar.findViewById(R.id.recyclerView);

                            adapter = new AdapterListKaryawan(listkar.getContext(), items, ItemAnimation.BOTTOM_UP);

                            rvkar.setLayoutManager(new LinearLayoutManager(listkar.getContext()));
                            rvkar.setHasFixedSize(true);
                            rvkar.setAdapter(adapter);
                            rvkar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listkar.dismiss();
                                }
                            });


                            listkar.show();
                            listkar.getWindow().setAttributes(lp);
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
            } catch (Exception E) {
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: " + E.getMessage().toString());
                Snackbar.make(parent_view, E.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
            }

            if (this.dialog.isShowing()) {
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivegaji extends AsyncTask<Void, Integer, String> {
        String response = "";
        String error = "";
        String username = "";
        String password = "";
        SharedPreferences prefs;
        JSONObject result = null;
        ProgressDialog dialog;
        String urldata = generator.pengajiangajikaryawanurl;
        String passeddata = "";
        String id = "";
        int p = 0;

        public retrivegaji(Context context, String id) {
            prefs = context.getSharedPreferences("poipayroll", Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error;
            this.id = id;
            this.p = p;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("id", id)
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization", prefs.getString("Authorization", ""))
                            .post(body)
                            .url(urldata)
                            .build();
                    Response responses = null;

                    Log.e(TAG, prefs.getString("Authorization", "") + " " + id);
                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsonObject = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonObject = null;
                    }

                    if (responses == null) {
                        jsonObject = null;
                        Log.e(TAG, "NULL");
                    } else {

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

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {

                if (result != null) {

                    Log.e(TAG, "Gaji" + result.toString());

                    try {
                        //JSONArray pengsarray = result.getJSONArray("data");
                        JSONObject obj = result.getJSONObject("data");
                        JSONArray GB = obj.getJSONArray("gajiBersih");
                        JSONArray GT = obj.getJSONArray("gajiTotal");
                        JSONObject sumGB = GB.getJSONObject(0);
                        JSONObject sumGT = GT.getJSONObject(0);
                        if (!sumGB.getString("gajiBersih").equals("null") || !sumGT.getString("gajiTotal").equals("null")) {
                            gaji += Double.parseDouble(sumGB.getString("gajiBersih"));
                            potongan += Double.parseDouble(sumGT.getString("gajiTotal"));
                            potongan -= Double.parseDouble(sumGB.getString("gajiBersih"));
                        } else {
                            gaji = 0.0d;
                            potongan = 0.0d;
                        }


                        //int gaji=1000000;
                        posisi++;
                        /*
                        for(int i=0;i<pengsarray.length();i++){
                            JSONObject obj = pengsarray.getJSONObject(i);
                            gaji+=Double.parseDouble(obj.getString("totalgaji"));
                            potongan+=Double.parseDouble(obj.getString("totalgaji"));

                        }*/
                        if (posisi == p) {
                            showgaji("Gaji", gaji, potongan);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            } catch (Exception E) {
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: " + E.getMessage().toString());
                Snackbar.make(parent_view, E.getMessage().toString(), Snackbar.LENGTH_SHORT).show();
            }

            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public void showgaji(String teks, Double Gaji, Double Potongan) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");

        final Dialog gajisendiri = new Dialog(mainmenu_kabag.this);
        gajisendiri.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        gajisendiri.setContentView(R.layout.dialog_gajisendiri);
        gajisendiri.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(gajisendiri.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView gajiteks = gajisendiri.findViewById(R.id.gajiteks);
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

        gajibersih.setText("RP " + formatter.format(Gaji));
        gajipotongan.setText("RP " + formatter.format(Potongan));
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
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
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
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    public class absensithrowlocation extends AsyncTask<Void, Integer, String>
    {
        Context ctx;
        String response = "";
        String error = "";
        String kabag = "" ;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = generator.throwlocation;
        String kode = "" ;
        String typeabsensi = "";
        int type=0;
        boolean issecurity = false;

        String longitude = "";
        String latitude = "";

        public  absensithrowlocation(Context context,int type,String kode,String longitude,String latitude,boolean issecurity)
        {
            this.kode = kode;
            this.longitude = longitude;
            this.latitude = latitude;
            this.type = type;
            this.issecurity = issecurity;

            ctx = context;
            dialog = new ProgressDialog(context);


            this.error = error ;

            if(issecurity){
                if(type ==1){
                    typeabsensi = "checkpos";
                }
                else if(type == 2){
                    typeabsensi="checkin";
                }else if(type ==3){
                    typeabsensi = "breakout";
                }else if(type ==4){
                    typeabsensi = "breakin";
                }else if(type ==5){
                    typeabsensi = "breakout";
                }
            }
            else {
                if(type == 0){
                    typeabsensi = "checkin";
                }else if(type ==1){
                    typeabsensi = "breakout";
                }else if(type ==2){
                    typeabsensi ="breakin";
                }else if(type ==3){
                    typeabsensi = "checkout";
                }else if(type ==4){
                    typeabsensi = "extrain";
                }else if(type ==5){
                    typeabsensi = "extraout";
                }
            }
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            try{this.dialog.show();}
            catch (Exception e){
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;



            try {
                this.dialog.setMessage("Loading Data...");
                OkHttpClient client = new OkHttpClient();



                RequestBody body = new FormBody.Builder()
                        .add("longitude",longitude+"")
                        .add("latitude",latitude+"")
                        .add("tipe",typeabsensi)
                        .add("kode",kode)
                        .build();

                Log.e(TAG, "doInBackground: "+kode+kabag );

                Request request=null;

                request = new Request.Builder()
                        .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                        .put(body)
                        .url(urldata)
                        .build();

                Response responses = null;


                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                result = new JSONObject(responses.body().string());



            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }catch (IOException e) {
                this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                e.printStackTrace();
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                this.dialog.dismiss();
                e.printStackTrace();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.setMessage("Loading Data... Error Occured,retrying...");
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
            super.onPostExecute(result1);
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                Log.e(TAG, "onPostExecute: "+result);
                //restartbarcode();


                String title = "";
                if(result!=null) {
                    if (result.getString("status").equals("true")) {
                        title = "Berhasil";
                        final AlertDialog alert = new AlertDialog.Builder(ctx).setTitle(title).setMessage(result.getString("message")).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    prefs = ctx.getSharedPreferences("poipayroll",MODE_PRIVATE);

                                    SharedPreferences.Editor edit = prefs.edit();

                                    edit.putInt("absensitype" ,0);
                                    edit.putString("absensikode","");
                                    edit.putString("absensilongitude","");
                                    edit.putString("absensilatitude","");
                                    edit.putBoolean("absensisecurity",false);

                                    edit.apply();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }//Do something after 100ms
                            }
                        }).create();

                        alert.show();
                    } else {

                        prefs = ctx.getSharedPreferences("poipayroll",MODE_PRIVATE);

                        SharedPreferences.Editor edit = prefs.edit();

                        edit.putInt("absensitype",type);
                        edit.putString("absensikode",kode);
                        edit.putString("absensilongitude",longitude+"");
                        edit.putString("absensilatitude",latitude+"");
                        edit.putBoolean("absensisecurity",issecurity);

                        edit.apply();

                        Log.e("failed send location", result.toString());

                        if(count>=5){

                        }
                        else {
                            absensithrowlocation throwdata = new absensithrowlocation(ctx, type, kode, longitude, latitude, issecurity);
                            throwdata.execute();
                            count++;
                        }
                    }
                }
                else {

                    prefs = ctx.getSharedPreferences("poipayroll",MODE_PRIVATE);

                    SharedPreferences.Editor edit = prefs.edit();

                    edit.putInt("absensitype",type);
                    edit.putString("absensikode",kode);
                    edit.putString("absensilongitude",longitude+"");
                    edit.putString("absensilatitude",latitude+"");
                    edit.putBoolean("absensisecurity",issecurity);

                    edit.apply();

                    if(count>=5){

                    }
                    else {
                        absensithrowlocation throwdata = new absensithrowlocation(ctx, type, kode, longitude, latitude, issecurity);
                        throwdata.execute();
                        count++;
                    }





                    Toast.makeText(ctx, "Terjadi Kesalahan Koneksi", Toast.LENGTH_SHORT).show();
                }


                //JSONArray bArray= responseObject.getJSONArray("B");
                //for(int i=0;i<bArray.length();i++){
                //    JSONObject innerObject=bArray.getJSONObject(i);
                //    String a= innerObject.getString("a");
                //    String b= innerObject.getString("b");
                //}
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute error: "+e.getMessage() );
                e.printStackTrace();
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
                alertDialog.setTitle("Kesalahan");

                alertDialog.setMessage(e.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4011: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            Intent intent = new Intent(mainmenu_kabag.this, ActivityAbsensi.class);
                            intent.putExtra("jabatan", "kabag");
                            startActivity(intent);

                }
            }
        }
    }
}