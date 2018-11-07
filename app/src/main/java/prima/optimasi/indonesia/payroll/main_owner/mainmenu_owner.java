package prima.optimasi.indonesia.payroll.main_owner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
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

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.mikhaellopez.circularimageview.CircularImageView;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.AdapterGridCaller;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentJob;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentHome;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentReport;
import prima.optimasi.indonesia.payroll.main_owner.manage.pengumuman.addpengumuman;
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
        searchView=findViewById(R.id.searchView);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = drawer.findViewById(R.id.lvExp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout linear = (RelativeLayout) navigationView.getHeaderView(0);
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

        tabpager.setupWithViewPager(pager);

        tabpager.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabpager.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if(position==3){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(true);
                        tempmenu.findItem(R.id.action_calendar).setVisible(false);

                    }
                }
                else if(position ==1){
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(true);
                        tempmenu.findItem(R.id.action_add).setVisible(false);
                        tempmenu.findItem(R.id.action_calendar).setVisible(false);

                    }
                }
                else {
                    if(tempmenu!=null){
                        tempmenu.findItem(R.id.action_search).setVisible(false);
                        tempmenu.findItem(R.id.action_add).setVisible(false);
                        tempmenu.findItem(R.id.action_calendar).setVisible(false);

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
                if(listDataHeader.get(groupPosition).equals("Home")){
                    pager.setCurrentItem(0);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Karyawan")){
                    pager.setCurrentItem(1);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Informasi")){
                    pager.setCurrentItem(3);
                    drawer.closeDrawer(Gravity.START);
                }else if(listDataHeader.get(groupPosition).equals("Laporan")){
                    posi = posi +1;
                    if(posi==2){
                        pager.setCurrentItem(2);
                        drawer.closeDrawer(Gravity.START);
                    }else {

                    }


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
                if(listDataHeader.get(groupPosition).equals("Tugas")){
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
        }
        else if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_mainmenu, menu);

        tempmenu = menu;
        MenuItem item = menu.findItem(R.id.action_search);

        searchView.setMenuItem(item);

<<<<<<< HEAD
        if(posisi==1){
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    FragmentEmployee fe=new FragmentEmployee();
                    /*
                    //Do some magic
                    Bundle bundle=new Bundle();
                    bundle.putString("query",query);
//set Fragmentclass Arguments
                    FragmentEmployee fragobj=new FragmentEmployee();
                    fragobj.setArguments(bundle);*/
                    //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    //Do some magic
                    Log.e("Text", "newText=" + query);

                    //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
                    return false;
                }
            });
        }
        /*
=======
>>>>>>> 929fab908c4fe5d11845038726302986b733273a
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




                //Do some magic
                /*
                Bundle bundle=new Bundle();
                bundle.putString("query",query);
                //set Fragmentclass Arguments
                FragmentEmployee fragobj=new FragmentEmployee();
                fragobj.setArguments(bundle);
                fragobj.setFilter();*/


                //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
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

                //Do some magic
                /*
                Bundle bundle=new Bundle();
                bundle.putString("query",query);
                //set Fragmentclass Arguments
                FragmentEmployee fragobj=new FragmentEmployee();
                fragobj.setArguments(bundle);*/

                //recyclerViewkaryawan.setAdapter(mAdapterkaryawan);
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

            edit.putString("iduser","");
            edit.putString("username","");
            edit.putString("jabatan","");
            edit.putString("level","");
            edit.putString("tempatlahir","");
            edit.putString("profileimage","");
            edit.putString("Authorization","");

            edit.commit();

            startActivity(logout);

            return true;
        }
        else if(id == R.id.action_add){
            Intent a = new Intent(mainmenu_owner.this,addpengumuman.class);
            startActivity(a);

        }
        else if (id == R.id.action_search) {
            return true;
        }
        else if (id == R.id.action_calendar) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mainmenu_owner.this).setPositiveButton("Select",null).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            LayoutInflater inflate = LayoutInflater.from(mainmenu_owner.this);

            View linear = inflate.inflate(R.layout.calenderview,null);

            CalendarView calender = linear.findViewById(R.id.calenderviews);

            Calendar cal = Calendar.getInstance();



            calender.setOnDayClickListener(new OnDayClickListener() {
                @Override
                public void onDayClick(EventDay eventDay) {
                    Calendar clickedDayCalendar = eventDay.getCalendar();
                    selecteddate = clickedDayCalendar.getTimeInMillis();
                }
            });

            if(selecteddate!=0L){
                cal.setTimeInMillis(selecteddate);
                try {
                    calender.setDate(cal);
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }
            }

            dialog.setView(linear);

            AlertDialog dial = dialog.show();

            dial.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {

                }
            });

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
        listDataHeader.add("Karyawan");
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

        listDataChild.put(listDataHeader.get(5), lapor);
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
                    return new FragmentEmployee();
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
        tempmenu.findItem(R.id.action_search).setVisible(true);
        tempmenu.findItem(R.id.action_calendar).setVisible(false);


    }
    public void closeAll(){
        searchView.closeSearch();
        tempmenu.findItem(R.id.action_search).setVisible(true);
        tempmenu.findItem(R.id.action_add).setVisible(true);

    }
    public void hidesearch(){
        tempmenu.findItem(R.id.action_search).setVisible(false);
        tempmenu.findItem(R.id.action_calendar).setVisible(true);

    }
    public void hideAll(){
        tempmenu.findItem(R.id.action_search).setVisible(false);
        tempmenu.findItem(R.id.action_add).setVisible(false);

    }
}
