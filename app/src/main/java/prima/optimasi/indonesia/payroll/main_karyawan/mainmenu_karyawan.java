package prima.optimasi.indonesia.payroll.main_karyawan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import prima.optimasi.indonesia.payroll.activity.MainMenu;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentCekGaji;
import qrcodescanner.QrCodeActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentAbsensi;
import prima.optimasi.indonesia.payroll.main_karyawan.fragment_karyawan.FragmentPengajuan;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class mainmenu_karyawan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Adaptermenujabatan listAdapter;
    ExpandableListView expListView;
    ProgressDialog loadingdata;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    SharedPreferences prefs;

    String[] tabTitles = new String []{"Pengumuman", "Cek Gaji","Log Absensi","Pengajuan"};
    int[] iconstyle = new int[]{R.drawable.baseline_announcement_black_24dp,R.drawable.baseline_account_circle_black_24dp,R.drawable.baseline_monetization_on_black_24dp,R.drawable.baseline_pie_chart_black_24dp,R.drawable.baseline_assignment_black_24dp};

    ViewPager pager;
    TabLayout tabpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadingdata = new ProgressDialog(this);
        loadingdata.setTitle("Please Wait");
        loadingdata.setMessage("Loading Data...");
        loadingdata.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("karyawan");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        generator initializedata = new generator(mainmenu_karyawan.this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
                // TODO Auto-generated method stub
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





        if(loadingdata.isShowing()){
            loadingdata.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("karyawan");


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
            edit.putString("kodekaryawan","");
            edit.putString("tempatlahir","");
            edit.putString("profileimage","");
            edit.putString("Authorization","");

            edit.commit();

            startActivity(logout);

            return true;
        }

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void preparekaryawan() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Pengumuman");
        listDataHeader.add("Cek Gaji");
        listDataHeader.add("Log Absensi");
        listDataHeader.add("Pengajuan");
        listDataHeader.add("Cek Jadwal");
        listDataHeader.add("Absensi");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Izin");
        top250.add("Cuti");
        top250.add("Pinjaman");

        listDataChild.put(listDataHeader.get(4), top250);
    }

    public class ExamplePagerAdapter extends FragmentStatePagerAdapter {

        // tab titles
        private String[] tabTitles = new String[]{"Pengumuman", "Cek Gaji","Log Absensi","Pengajuan"};

        public ExamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // overriding getPageTitle()
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentPengumuman();
                case 1:
                    return new FragmentCekGaji();
                case 2:
                    return new FragmentAbsensi();
                case 3:
                    return new FragmentPengajuan();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
        // ...
    }
}
