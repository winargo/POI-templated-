package prima.optimasi.indonesia.payroll.main_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentHome;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentJob;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentReport;
import prima.optimasi.indonesia.payroll.main_owner.manage.pengumuman.addpengumuman;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan_nonaktif;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class mainmenu_owner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    long selecteddate = 0L;
    MaterialSearchView searchView;
    Adaptermenujabatan listAdapter;
    private ProgressDialog loadingprogress=null;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ViewPager pager;
    TabLayout tabpager ;
    Menu tempmenu ;
    int posisi=0;
    int[] iconstyle = {R.drawable.baseline_home_black_18dp,R.drawable.ic_person_outline_white_24dp,R.drawable.baseline_assessment_black_24dp,R.drawable.baseline_announcement_black_24dp,R.drawable.ic_baseline_work_24px};

    private String[] tabTitles = new String[]{"Home", "Karyawan", "Laporan","Informasi","Tugas"};

    int posi = 0;

    SharedPreferences prefs;
    DrawerLayout drawer;
    RelativeLayout linear;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        initToolbar();
        initComponent();
        initListener();
        if (loadingprogress.isShowing()) {
            loadingprogress.dismiss();
        }

        JSONObject data = null;
      /*  generator.retrivedata async = new generator.retrivedata(mainmenu_owner.this,loadingprogress);
        async.execute();

        while(generator.jsondatalogin==null){
            if(generator.jsondatalogin==null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("json generator status", "empty" );
            }
            else {
                Log.e("json generator status", "not empty" );
                break;
            }
        }

        data = generator.jsondatalogin;
        generator.jsondatalogin=null;
        Log.e("JSON data",data.toString() );

        JSONObject second = null;

        try {
            second = data.getJSONObject("data");
            username.setText(second.getString("username"));
            Log.e("onCreate: ",second.getString("username") );
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("owner");
        }

        searchView=findViewById(R.id.searchView);

        CircularImageView imageuser= linear.findViewById(R.id.imageView);

        if(prefs.getString("profileimage","").equals(generator.ownerurl)){

        }
        else {
            Picasso.get().load(prefs.getString("profileimage","")).transform(new CircleTransform()).into(imageuser);
        }
        Log.e("picture", "ppicture: "+ prefs.getString("profileimage",""));

        TextView username = linear.findViewById(R.id.username);
        final TextView borndate = linear.findViewById(R.id.prof_tempat_lahir);

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
        pager.setOffscreenPageLimit(4);

        tabpager.setupWithViewPager(pager);

        tabpager.setTabMode(TabLayout.MODE_SCROLLABLE);

        preparehrd();

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
                if(position==3){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_show).setVisible(false);
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(true);

                    }
                }
                else if(position ==1){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_show).setVisible(true);
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(false);

                    }
                }
                else {
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_show).setVisible(false);
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

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected}, // enabled
                new int[] {-android.R.attr.state_selected}, // disabled

        };

        int[] colors = new int[] {
                getResources().getColor(R.color.qr_code_white),
                getResources().getColor(R.color.light_color),
        };

        ColorStateList myList = new ColorStateList(states, colors);
        for (int i = 0; i < tabpager.getTabCount(); i++) {
            //noinspection ConstantConditions
            View v = LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
            TextView tv=v.findViewById(R.id.texttab);
            //tv.setTextColor(Color.WHITE);
            tv.setTextColor(myList);
            tv.setText(tabTitles[i]);

            ImageView img = v.findViewById(R.id.icontab);
            img.setImageDrawable(getResources().getDrawable(iconstyle[i]));
            tabpager.getTabAt(i).setCustomView(v);
        }

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if(listDataHeader.get(groupPosition).equals("Dashboard")){
                    pager.setCurrentItem(0);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Karyawan")){
                    pager.setCurrentItem(1);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Pengumuman")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Laporan")){
                    pager.setCurrentItem(2);
                    drawer.closeDrawer(Gravity.START);

                }else if(listDataHeader.get(groupPosition).equals("Approval")){
                    pager.setCurrentItem(4);
                    drawer.closeDrawer(Gravity.START);
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
                if(listDataHeader.get(groupPosition).equals("Dashboard")){
                    pager.setCurrentItem(0);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Karyawan")){
                    pager.setCurrentItem(1);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Laporan")){
                    pager.setCurrentItem(2);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Informasi")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }
                else if(listDataHeader.get(groupPosition).equals("Tugas")){
                    pager.setCurrentItem(4);
                    drawer.closeDrawer(Gravity.START);
                }
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
                //FragmentEmployee fe=new FragmentEmployee();
                //fe.setFilter(query);

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
                //Do some magic
                //FragmentEmployee fe=new FragmentEmployee();
                //fe.setFilter(query);
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
            generator.logout(mainmenu_owner.this,"owner");
            return true;
        }
        else if(id == R.id.action_add){
            Intent a = new Intent(mainmenu_owner.this,addpengumuman.class);
            startActivity(a);
        }
        else if (id == R.id.action_search) {
            return true;
        }
        else if (id == R.id.action_show) {
            Intent a = new Intent(mainmenu_owner.this,viewkaryawan_nonaktif.class);
            startActivity(a);
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

    private void preparehrd() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Dashboard");
        listDataHeader.add("Pengumuman");
        listDataHeader.add("Karyawan");
        listDataHeader.add("Approval");
        listDataHeader.add("Laporan");
    }

    public class ExamplePagerAdapter extends FragmentStatePagerAdapter {

        // tab titles
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
                    FragmentEmployee employees=new FragmentEmployee();
                    generator.employee=employees;
                    return employees;
                case 2:
                    return new FragmentReport();
                case 3:
                    return new FragmentPengumuman();
                case 4:
                    return new FragmentJob();
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
        tempmenu.findItem(R.id.action_show).setVisible(true);
        tempmenu.findItem(R.id.action_search).setVisible(true);

    }
    public void hidesearch(){
        tempmenu.findItem(R.id.action_show).setVisible(false);
        tempmenu.findItem(R.id.action_search).setVisible(false);

    }

    public void closesearchpeng(){
        searchView.closeSearch();
        tempmenu.findItem(R.id.action_show).setVisible(false);
        tempmenu.findItem(R.id.action_search).setVisible(true);

    }
    public void closeAll(){
        searchView.closeSearch();
        tempmenu.findItem(R.id.action_search).setVisible(true);
        tempmenu.findItem(R.id.action_add).setVisible(true);

    }

    public void hideAll(){
        tempmenu.findItem(R.id.action_show).setVisible(false);
        tempmenu.findItem(R.id.action_search).setVisible(false);
        tempmenu.findItem(R.id.action_add).setVisible(false);

    }
    public boolean checkInternet(){
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            connectStatus = true;
        }
        else {
            connectStatus = false;
        }
        return connectStatus;
    }
}
