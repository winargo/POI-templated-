package prima.optimasi.indonesia.payroll.main_owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentApproval;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentChartKehadiran;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentReport;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentTotalGaji;

public class mainmenu_owner extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Adaptermenujabatan listAdapter;
    private ProgressDialog loadingprogress=null;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ViewPager pager;
    TabLayout tabpager ;
    Menu tempmenu ;

    int posi = 0;

    SharedPreferences prefs;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadingprogress = new ProgressDialog(this);
        loadingprogress.setTitle("Please Wait");
        loadingprogress.setMessage("Loading Data...");
        loadingprogress.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("owner");
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        generator initializedata = new generator(mainmenu_owner.this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout linear = navigationView.findViewById(R.id.datanav);
        ImageView imageuser = linear.findViewById(R.id.imageView);
        TextView username = linear.findViewById(R.id.username);

        SharedPreferences pref = this.getSharedPreferences("poipayroll",MODE_PRIVATE);

        username.setText(pref.getString("username",""));

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
            TextView tv=(TextView) LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
            tv.setTextColor(Color.WHITE);
            tabpager.getTabAt(i).setCustomView(tv);

        }

        preparehrd();

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
                if(listDataHeader.get(groupPosition).equals("Chart Kehadiran")){
                    pager.setCurrentItem(0);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Total Gaji")){
                    pager.setCurrentItem(1);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Seluruh Karyawan")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Pengumuman")){
                    pager.setCurrentItem(2);
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
                if(listDataHeader.get(groupPosition).equals("Approval")){
                    pager.setCurrentItem(4);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Laporan")){
                    pager.setCurrentItem(5);
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
        }
*/

        if(loadingprogress.isShowing()){
            loadingprogress.dismiss();
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

        tempmenu = menu;

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
            FirebaseMessaging.getInstance().unsubscribeFromTopic("owner");

            Intent logout = new Intent(mainmenu_owner.this,activity_login.class);
            SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

            if(prefs.getInt("statustoken",0)==0){

            }
            else {


                generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(mainmenu_owner.this,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
                unregistertokentoserver.execute();
            }

            SharedPreferences.Editor edit = prefs.edit();

            edit.putString("username","");
            edit.putString("password","");
            edit.putString("level","");
            edit.putString("token","");
            edit.putString("jabatan","");

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

    private void preparehrd() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Chart Kehadiran");
        listDataHeader.add("Total Gaji");
        listDataHeader.add("Pengumuman");
        listDataHeader.add("Seluruh Karyawan");
        listDataHeader.add("Approval");
        listDataHeader.add("Laporan");


        List<String> top2510 = new ArrayList<String>();
        top2510.add("Pinjaman");
        top2510.add("Karyawan Baru");
        top2510.add("Golongan");
        top2510.add("Promosi");
        top2510.add("Reward / Punish");

        List<String> lapor = new ArrayList<String>();
        lapor.add("Cuti");
        lapor.add("Izin");
        lapor.add("Sakit");
        lapor.add("Pinjaman");
        lapor.add("Pengajian");


        listDataChild.put(listDataHeader.get(4), top2510);
        listDataChild.put(listDataHeader.get(5), lapor);
    }

    public class ExamplePagerAdapter extends FragmentStatePagerAdapter {

        // tab titles
        private String[] tabTitles = new String[]{"Chart Kehadiran", "Total Gaji", "Pengumuman","Seluruh Karyawan","Approval","Laporan"};

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
                    return new FragmentChartKehadiran();
                case 1:
                    return new FragmentTotalGaji();
                case 2:
                    return new FragmentPengumuman();
                case 3:
                    return new FragmentEmployee();
                case 4:
                    return new FragmentApproval();
                case 5:
                    return new FragmentReport();
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
