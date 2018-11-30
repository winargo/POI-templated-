package prima.optimasi.indonesia.payroll.main_hrd;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd.FragmentCekGaji;
import prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd.FragmentHome;
import prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd.FragmentPengajuan;
import prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_kabag.cekjadwal;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class mainmenu_hrd extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    long selecteddate = 0L;
    MaterialSearchView searchView;
    Adaptermenujabatan listAdapter;
    ExpandableListView expListView;
    private ProgressDialog loadingprogress=null;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Menu tempmenu;
    SharedPreferences prefs;

    String[] tabTitles = new String []{" Home", "Pengumuman","Karyawan","Cek Gaji","Pengajuan"};
    int[] iconstyle = new int[]{R.drawable.baseline_home_black_18dp,R.drawable.baseline_announcement_black_24dp,R.drawable.ic_baseline_people_24px,
            R.drawable.baseline_account_circle_black_24dp,R.drawable.baseline_monetization_on_black_24dp,R.drawable.baseline_assignment_black_24dp};

    ViewPager pager;
    TabLayout tabpager;
    DrawerLayout drawer;
    RelativeLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        initToolbar();
        initComponent();
        initListener();
        if(loadingprogress.isShowing()){
            loadingprogress.dismiss();
        }
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        linear = (RelativeLayout) navigationView.getHeaderView(0);
    }

    private void initComponent(){
        loadingprogress = new ProgressDialog(this);
        loadingprogress.setTitle("Please Wait");
        loadingprogress.setMessage("Loading Data...");
        loadingprogress.show();

        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(mainmenu_hrd.this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("hrd");
        }

        searchView=findViewById(R.id.searchView);

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

        preparehrd();

        ExamplePagerAdapter adapter = new ExamplePagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        tabpager.setupWithViewPager(pager);
        pager.setOffscreenPageLimit(4);

        tabpager.setTabMode(TabLayout.MODE_SCROLLABLE);

        listAdapter = new Adaptermenujabatan(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setGroupIndicator(null);
        expListView.setChildIndicator(null);
        expListView.setChildDivider(null);
        expListView.setDivider(null);
        expListView.setDividerHeight(0);
    }

    private void initListener(){
        tabpager.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                tabpager.setSelectedTabIndicatorColor(mainmenu_hrd.this.getResources().getColor(R.color.blue_100));//set active color
                if(position==9){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(true);

                    }
                }
                else if(position ==1){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(false);
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
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setText(tabTitles[i]);

            ImageView img = v.findViewById(R.id.icontab);
            img.setImageDrawable(getResources().getDrawable(iconstyle[i]));
            tabpager.getTabAt(i).setCustomView(v);
        }

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
                }
                else if(listDataHeader.get(groupPosition).equals("Cek Gaji")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Pengajuan")){
                    pager.setCurrentItem(4);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Absensi")){
                    drawer.closeDrawer(Gravity.START);
                    boolean permissionGranted = ActivityCompat.checkSelfPermission(mainmenu_hrd.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                    if(permissionGranted) {

                        Intent intent = new Intent(mainmenu_hrd.this, ActivityAbsensi.class);
                        intent.putExtra("jabatan", "HRD");
                        startActivity(intent);

                    } else {
                        ActivityCompat.requestPermissions(mainmenu_hrd.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4011);
                    }
                    //String[] colors = {"Check IN", "Check OUT","Break OUT","Break IN","Extra IN","Extra OUT"};
                    /*
                    String[] colors = {"Scan","Check IN", "Check OUT","Break OUT","Break IN","Absensi"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_hrd.this);
                    builder.setTitle("Absensi");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int type=which+1;
                            if(which==5){
                                selectedtype(1,type);
                            }
                            else{
                                absensi();
                            }


                            Intent a = new Intent(mainmenu_hrd.this,QrCodeActivity.class);
                            a.putExtra("absensi",which);
                            a.putExtra("keepalive",1);
                            startActivity(a);
                        }
                    });
                    builder.show();*/
                }
                else if(listDataHeader.get(groupPosition).equals("Cek Jadwal")){
                    String[] colors = {"Karyawan", "Sendiri"};


                    AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_hrd.this);
                    builder.setTitle("Cek Jadwal");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0) {
                                Intent a = new Intent(mainmenu_hrd.this, cekjadwal.class);
                                a.putExtra("cekjadwal",2);
                                startActivity(a);
                            }
                            else {
                                Intent a = new Intent(mainmenu_hrd.this, cekjadwal.class);
                                a.putExtra("cekjadwal",1);
                                startActivity(a);
                            }
                        }
                    });
                    builder.show();

                }
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //  Toast.makeText(getApplicationContext(),
                //    listDataHeader.get(groupPosition) + " Collapsed",
                //   Toast.LENGTH_SHORT).show();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_mainmenu, menu);
        tempmenu = menu;
        MenuItem item = menu.findItem(R.id.action_search);

        searchView.setMenuItem(item);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(generator.posisi==0){
                    generator.adapterkabag.getFilter().filter(query);
                }
                else if(generator.posisi==1){
                    generator.adapterkar.getFilter().filter(query);
                }
                if(generator.posisipengumuman==0){
                    generator.adapterpeng.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.e("Text", "newText=" + query);

                if(generator.posisi==0){
                    generator.adapterkabag.getFilter().filter(query);
                }
                else if(generator.posisi==1){
                    generator.adapterkar.getFilter().filter(query);
                }
                if(generator.posisipengumuman==0){
                    generator.adapterpeng.getFilter().filter(query);
                }
                return false;
            }
        });
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
            FirebaseMessaging.getInstance().unsubscribeFromTopic("hrd");

            Intent logout = new Intent(mainmenu_hrd.this,activity_login.class);
            SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

            if(prefs.getInt("statustoken",0)==0){

            }
            else {
                generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(mainmenu_hrd.this,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
                unregistertokentoserver.execute();
            }

            SharedPreferences.Editor edit = prefs.edit();

            edit.putString("iduser","");
            edit.putString("username","");
            edit.putString("jabatan","");
            edit.putString("level","");
            edit.putString("tempatlahir","");
            edit.putString("profileimage","");
            edit.putString("kodekaryawan","");
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

        return true;
    }

    private void preparehrd() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Home");
        listDataHeader.add("Pengumuman");
        listDataHeader.add("List Karyawan");
        listDataHeader.add("Cek Gaji");
        listDataHeader.add("Cek Jadwal");
        listDataHeader.add("Pengajuan");
        listDataHeader.add("Absensi");

        /*
        List<String> top2501 = new ArrayList<String>();
        top2501.add("Karyawan");
        top2501.add("Sendiri");*/

        /*
        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Izin");
        top250.add("Cuti");
        top250.add("Pinjaman");
        top250.add("Sakit");*/

        //listDataChild.put(listDataHeader.get(3), top2501);
        //listDataChild.put(listDataHeader.get(4), top2501);

        //listDataChild.put(listDataHeader.get(5), top250);
    }

    public class ExamplePagerAdapter extends FragmentStatePagerAdapter {
        // tab titles
        private String[] tabTitles = new String[]{"Home", "Pengumuman","Karyawan", "Cek Gaji","Pengajuan"};

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
                    return new FragmentHome();
                case 1:
                    return new FragmentPengumuman();
                case 2:
                    return new FragmentEmployee();
                case 3:
                    return new FragmentCekGaji();
                case 4:
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
    public void closesearch(){
        searchView.closeSearch();
        tempmenu.findItem(R.id.action_search).setVisible(true);

    }
    public void closeAll(){
        searchView.closeSearch();
        tempmenu.findItem(R.id.action_search).setVisible(true);
        tempmenu.findItem(R.id.action_add).setVisible(false);

    }
    public void hidesearch(){
        tempmenu.findItem(R.id.action_search).setVisible(false);

    }
    public void hideAll(){
        tempmenu.findItem(R.id.action_search).setVisible(false);
        tempmenu.findItem(R.id.action_add).setVisible(false);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else {
            if(pager.getCurrentItem()!=0){
                pager.setCurrentItem(pager.getCurrentItem()-1);
            }
            else{
                super.onBackPressed();
            }
        }
    }
}
