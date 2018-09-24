package prima.optimasi.indonesia.payroll.main_kabag;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
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

import qrcodescanner.QrCodeActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.adapter.Adaptermenujabatan;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentCekGaji;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentEmployee;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentHome;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentPengajuan;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag.FragmentProfil;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.universal.absence.facecapture;
import prima.optimasi.indonesia.payroll.universal.absence.facedetection;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class mainmenu_kabag extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences prefs;

    Adaptermenujabatan listAdapter;
    ExpandableListView expListView;
    ProgressDialog loadingdata;
    TabLayout tabpager;
    ViewPager pager;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Menu tempmenu ;
    String TAG = "ABSENSI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        loadingdata = new ProgressDialog(this);
        loadingdata.setTitle("Please Wait");
        loadingdata.setMessage("Loading Data...");
        loadingdata.show();


        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("kabag");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Log.e("sharence status", "onCreate: " + prefs.getInt("statustoken",0) );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        generator initializedata = new generator(mainmenu_kabag.this);

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
            TextView tv=(TextView) LayoutInflater.from(this).inflate(R.layout.customtablayout,null);
            tv.setTextColor(Color.WHITE);
            tabpager.getTabAt(i).setCustomView(tv);

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
                    String[] colors = {"Check IN", "Check OUT","Break OUT","Break IN","Extra IN","Extra OUT"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu_kabag.this);
                    builder.setTitle("Absensi");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(mainmenu_kabag.this,QrCodeActivity.class);
                            a.putExtra("absensi",which);
                            a.putExtra("keepalive",1);
                            startActivity(a);
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
                    }*/
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
    private void preparekabag() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Home");
        listDataHeader.add("Profil");
        listDataHeader.add("Pengumuman");
        listDataHeader.add("Anggota");
        listDataHeader.add("Cek Gaji");
        listDataHeader.add("Pengajuan");
        listDataHeader.add("Absensi");


        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Karyawan");
        top250.add("Sendiri");

        List<String> top2510 = new ArrayList<String>();
        top2510.add("Izin");
        top2510.add("Cuti");
        top2510.add("Pinjaman");

        listDataChild.put(listDataHeader.get(4), top250);
        listDataChild.put(listDataHeader.get(5), top2510);
    }
    public class ExamplePagerAdapter extends FragmentStatePagerAdapter {

        // tab titles
        private String[] tabTitles = new String[]{"Home", "Profil", "Pengumuman","Anggota","Cek Gaji","Pengajuan"};

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
                    return new FragmentProfil();
                case 2:
                    return new FragmentPengumuman();
                case 3:
                    return new FragmentEmployee();
                case 4:
                    return new FragmentCekGaji();
                case 5:
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

    public class loginselainowner extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = generator.scanloginurl;
        String passeddata = "" ;

        public  loginselainowner(Context context,String passed)
        {
            dialog = new ProgressDialog(context);
            passeddata = passed;
            this.username = generator.username;
            this.password = generator.password;
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

            int data = 0;

            while (data==0) {
                try {
                    this.dialog.setMessage("Loading Data...");

                    JSONObject jsonObject;

                    try {
                        OkHttpClient client = new OkHttpClient();


                        RequestBody body = new FormBody.Builder()
                                .add("kode",passeddata)
                                .build();

                        Request request = new Request.Builder()
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
                        }
                        else {
                            jsonObject = new JSONObject(responses.body().string());
                        }
                        result = jsonObject;
                        data=1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }


                    break;
                } catch (IOException e) {
                    this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", "null data" + e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    this.dialog.setMessage("Loading Data... Error Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Error Occured, PLease Contact Administrator/Support";
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
                if(result.getString("status").equals("true")) {
                    JSONArray data = result.getJSONArray("data");
                    JSONObject dataisi = data.getJSONObject(0);
                    String declare = dataisi.getString("otoritas");
                    String iduser = dataisi.getString("idfp");
                }

                //JSONArray bArray= responseObject.getJSONArray("B");
                //for(int i=0;i<bArray.length();i++){
                //    JSONObject innerObject=bArray.getJSONObject(i);
                //    String a= innerObject.getString("a");
                //    String b= innerObject.getString("b");
                //}
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: "+e.getMessage() );
                e.printStackTrace();
                if(result!=null){
                    AlertDialog alertDialog = new AlertDialog.Builder(mainmenu_kabag.this).create();
                    alertDialog.setTitle("Hasil");

                    alertDialog.setMessage(e.getMessage().toString() + " "+ result.toString());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {

                }
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

}
