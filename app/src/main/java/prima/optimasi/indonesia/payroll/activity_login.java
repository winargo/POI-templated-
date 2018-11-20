package prima.optimasi.indonesia.payroll;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.adapter.AdapterListCompany;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.objects.company;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;
import qrcodescanner.QrCodeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class activity_login extends FragmentActivity {

    public String TAG="activity_login";

    private ProgressBar progress_bar;
    private FloatingActionButton fab;
    SharedPreferences pref;
    private View parent_view;
    SQLiteHelper dbase;
    LinearLayout scanqr;
    TextView usr;
    TextView pass;

    ImageButton settings;

    AlertDialog companydata;

    AdapterListCompany adapter;

    ImageView scanbar,entertext;
    EditText codeentered;

    List<company> companylist;

    TextView selectedcompany;

    LinearLayout linearnodata;

    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Realm.init(this);
        dbase= new SQLiteHelper(this);

        pref = getSharedPreferences("poipayroll",MODE_PRIVATE);

        companydata = new AlertDialog.Builder(activity_login.this).create();

        LayoutInflater inflater = LayoutInflater.from(activity_login.this);

        View view = inflater.inflate(R.layout.company_data,null);
        generator.tempview = view;
        codeentered =  view.findViewById(R.id.enteredcode);
        selectedcompany = view.findViewById(R.id.selectedcompany);



        generator.textcompany = selectedcompany;

        recycler = view.findViewById(R.id.companylist);

        recycler.setLayoutManager(new GridLayoutManager(this, 1));
        recycler.setHasFixedSize(true);

        linearnodata = view.findViewById(R.id.nodatacompany);

        scanbar = view.findViewById(R.id.scanbarcode);
        entertext = view.findViewById(R.id.correcttext);

        entertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeentered.getText().toString().equals("")){
                    AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Warning").setMessage("Please Input code").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

                    dialog.show();
                }
                else {
                    daftarcompany company = new daftarcompany(activity_login.this,codeentered.getText().toString());
                    company.execute();
                }
            }
        });


        scanbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(activity_login.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(activity_login.this, new String[] {Manifest.permission.CAMERA}, 5);
                }
                else {
                    Intent i = new Intent(activity_login.this,QrCodeActivity.class);
                    i.putExtra("keepalive",0);
                    startActivityForResult( i,100);
                }


            }
        });

        companylist = new ArrayList<>();

        companylist = dbase.getAllcompany();

        adapter = new AdapterListCompany(activity_login.this,companylist,companydata);

        /*adapter.setOnItemClickListener(new AdapterListCompany.OnItemClickListener() {
            @Override
            public void onItemClick(View view, company obj1, int position) {
                SharedPreferences prefs = getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
                prefs.edit().putString("ip",obj1.getCompanyip());
                prefs.edit().putString("port",String.valueOf(obj1.getCompanyport()));
                prefs.edit().putString("companyname",obj1.getCompanyname());
                prefs.edit().apply();

                Log.e("data adapter comp",obj1.getCompanyip()+" "+obj1.getCompanyport());

                generator.Server = obj1.getCompanyip();
                generator.port = String.valueOf(obj1.getCompanyport());

                if(companydata!=null )
                {
                    if(companydata.isShowing()){
                        companydata.dismiss();
                    }
                }

                selectedcompany.setText(pref.getString("companyname",""));
                companydata.dismiss();
            }
        });*/

        recycler.setAdapter(adapter);

        if(pref.getString("ip","").equals("")){
        }
        else {
            generator.Server = pref.getString("ip","");
            generator.reloadurl();
        }

        if(pref.getString("port","").equals("")){

        }
        else {
            generator.port = pref.getString("port","");
            generator.reloadurl();
        }
        if(pref.getString("companyname","").equals("")){

        }
        else {
            selectedcompany.setText(pref.getString("companyname",""));
            generator.reloadurl();
        }
        if(pref.getString("codename","").equals("")){

        }
        else {
            generator.determiner=pref.getString("codename","");
            generator.reloadurl();
        }

        Log.e( "databased",generator.Server+" data  "+generator.port );



        if(companylist.size() == 0){
            linearnodata.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else {
            linearnodata.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }

        companydata.setView(view);

        settings = findViewById(R.id.settings);



        if(!generator.token.equals("")){
            pref.edit().putString("tokennotif",generator.token).commit();
            pref.edit().putInt("statustoken",0).commit();
            Log.e(TAG, "Data saved");
        }

        /*if(pref.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,pref.getString("tokennotif",""));
            register.execute();

        }*/

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeentered.setText("");
                companydata.show();
            }
        });

        if(!pref.getString("level","").equals("")){
            String declare = pref.getString("level","");
            if(declare.equals("hrd")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_hrd.class);
                startActivity(mainmenu);
                finish();
            }else if(declare.equals("owner")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_owner.class);
                startActivity(mainmenu);
                finish();
            }
            else if(declare.equals("kabag")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_kabag.class);
                startActivity(mainmenu);
                finish();
            }
            else {
                Intent mainmenu = new Intent(activity_login.this,mainmenu_karyawan.class);
                startActivity(mainmenu);
                finish();
            }

        }
        else if(!pref.getString("username","").equals("")){

            Intent mainmenu = new Intent(activity_login.this,mainmenu_owner.class);
            startActivity(mainmenu);
            finish();
        }
        else {

            usr = findViewById(R.id.usrname);
            scanqr = findViewById(R.id.scanqr);
            pass = findViewById(R.id.usrpass);

            parent_view = findViewById(android.R.id.content);
            progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
            fab = (FloatingActionButton) findViewById(R.id.fab);

            scanqr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(generator.Server.equals("")){
                        AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Gagal").setMessage("Mohon scan Kartu perusahaan , hubungi kepala bagian atau HRD").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                        dialog.show();
                    }
                    else {

                        if (ContextCompat.checkSelfPermission(activity_login.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(activity_login.this, new String[]{Manifest.permission.CAMERA}, 4);
                        } else {
                            Intent i = new Intent(activity_login.this, QrCodeActivity.class);
                            i.putExtra("keepalive", 0);
                            startActivityForResult(i, 104);
                        }
                    }


                }
            });

            Tools.setSystemBarColor(this, R.color.cyan_800);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(generator.Server.equals("")){
                        AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Gagal").setMessage("Mohon scan Kartu perusahaan , hubungi kepala bagian atau HRD").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();

                        dialog.show();
                    }
                    else {

                        if (usr != null && pass != null) {
                            if (usr.getText().toString().equals("")) {
                                Snackbar.make(parent_view, "Username Is Empty", Snackbar.LENGTH_SHORT).show();

                            } else if (pass.getText().toString().equals("")) {
                                Snackbar.make(parent_view, "Please Input Password", Snackbar.LENGTH_SHORT).show();
                            } else {
                                searchAction(usr.getText().toString(), pass.getText().toString());
                            }
                        }
                    }
                }
            });

        }
    }

    private void searchAction(final String username, final String password) {
        progress_bar.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);

            TestAsync sync = new TestAsync(activity_login.this,generator.keepaliveurl,username,password,"");
            sync.execute();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



            }
        }, 1000);
    }

    private class TestAsync extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        JSONObject token = null;
        JSONObject object = null;
        String username=  "" ;
        String retoken = "";
        SharedPreferences.Editor edit;
        String password = "" ;
        ProgressDialog dialog ;
        String urldata ;

        public  TestAsync (Context context,String url,String username , String password,String reauthorization)
        {
            retoken = reauthorization;
            edit = context.getSharedPreferences("poipayroll",MODE_PRIVATE).edit();
            dialog = new ProgressDialog(context);
            this.urldata = url;

            Log.e(TAG, "TestAsync: "+url );
            this.username = username;
            this.password = password;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            this.dialog.setMessage("Login...");
            this.dialog.show();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            if (retoken.equals("")) {
                Log.d(TAG + " DoINBackGround", "On doInBackground...");

                try {
                    response = okhttpclass.getJsonlogin(urldata, username, password).toString();
                    object = okhttpclass.getJsonlogin(urldata, username, password);
                    token = okhttpclass.getJsonlogin(urldata, username, password);


                } catch (IOException e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Error Occured, Please Contact Administrator/Support";
                }
                return response;
            } else {
                try {
                    OkHttpClient client = new OkHttpClient();

                    JSONArray array = new JSONArray();

                    RequestBody body = new FormBody.Builder()
                            .add("username", username)
                            .add("password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url(generator.keepaliveurl)
                            .addHeader("Authorization", retoken)
                            .post(body)
                            .build();
                    Response responses = null;

                    responses = client.newCall(request).execute();

                    if (responses == null) {
                        return null;
                    } else {
                        response = responses.body().toString();
                        object = new JSONObject(responses.body().string());
                        token = new JSONObject(responses.body().string());
                    }

                } catch (IOException e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    object = null;
                    Log.e(TAG, "doInBackground: " + e.getMessage().toString());
                    response = "Error Occured, Please Contact Administrator/Support";
                }

                return response;


            }
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(this.dialog.isShowing()){
                this.dialog.dismiss();
            }


            if(retoken.equals("")){
                try {
                    if(object!=null){
                        JSONObject responseObject = object;
                        String status= responseObject.getString("status");

                        Log.e( "onPostExecute: ", object.toString());


                        if(status.equals("true")){
                            if(object!=null && token!=null ){
                                pref.edit().putString("level","owner").commit();
                                pref.edit().putString("Authorization",responseObject.getString("token")).commit();
                                pref.edit().putString("jabatan","owner").commit();
                                pref.edit().putString("iduser","").commit();

                                pref.edit().putString("profileimage",generator.ownerurl+responseObject.getString("foto")).commit();
                                pref.edit().putString("tempatlahir","").commit();
                                pref.edit().putString("username",responseObject.getString("nama_pengguna")).commit();
                            }

                            Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();
                            Intent mainmenu = new Intent(activity_login.this,mainmenu_owner.class);
                            startActivity(mainmenu);
                            finish();
                        }
                        else if(status.equals("false")) {
                            Snackbar.make(parent_view, "Username atau Password Salah", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(parent_view, status, Snackbar.LENGTH_SHORT).show();
                        }

                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                    }
                    else {
                        Snackbar.make(parent_view, response, Snackbar.LENGTH_SHORT).show();
                        progress_bar.setVisibility(View.GONE);
                        fab.setAlpha(1f);
                    }



                    //JSONArray bArray= responseObject.getJSONArray("B");
                    //for(int i=0;i<bArray.length();i++){
                    //    JSONObject innerObject=bArray.getJSONObject(i);
                    //    String a= innerObject.getString("a");
                    //    String b= innerObject.getString("b");
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {

            }



        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setMessage(format+"   "+contents).setTitle("Canceled").show();
            } else if (resultCode == RESULT_CANCELED) {
                AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setMessage("canceled by user").setTitle("Canceled").show();
            }
        }
        else if(requestCode == 104){

            if(resultCode == Activity.RESULT_OK)
            {
                if(intent==null)
                    return;
                String result = intent.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Log.d(TAG,"Have scan result in your app activity :"+ result);

                codeentered.setText(result);

                loginselainowner owner = new loginselainowner(activity_login.this,result);
                owner.execute();
                /*

                AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage(result);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                */



                return;

            }
            else {

                Log.d(TAG,"COULD NOT GET A GOOD RESULT.");
                if(intent==null)
                    return;
                //Getting the passed result
                String result = intent.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
                if( result!=null)
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
                    alertDialog.setTitle("Scan Error");
                    alertDialog.setMessage("QR Code could not be scanned");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return;
            }
        }
        else if(requestCode == 100) {

            if (resultCode == Activity.RESULT_OK) {
                if (intent == null)
                    return;
                String result = intent.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Log.d(TAG, "Have scan result in your app activity :" + result);

                daftarcompany owner = new daftarcompany(activity_login.this, result);
                owner.execute();
                /*

                AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage(result);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                */


                return;

            } else {

                Log.d(TAG, "COULD NOT GET A GOOD RESULT.");
                if (intent == null)
                    return;
                //Getting the passed result
                String result = intent.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
                if (result != null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
                    alertDialog.setTitle("Scan Error");
                    alertDialog.setMessage("QR Code could not be scanned");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return;
            }
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 4) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(activity_login.this, QrCodeActivity.class);
                i.putExtra("keepalive",0);
                startActivityForResult( i,104);

            } else {

                Snackbar.make(parent_view, "Camera Permission Required", Snackbar.LENGTH_SHORT).show();

            }

        }
        if (requestCode == 5) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(activity_login.this, QrCodeActivity.class);
                i.putExtra("keepalive",0);
                startActivityForResult( i,100);

            } else {

                Snackbar.make(parent_view, "Camera Permission Required", Snackbar.LENGTH_SHORT).show();

            }

        }
    }

    public class loginselainowner extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result =new JSONObject();
        ProgressDialog dialog ;
        String urldata = generator.scanloginurl;
        String passeddata = "" ;

        public  loginselainowner(Context context,String passed)
        {
            Log.e(TAG, "result"+passed );
            dialog = new ProgressDialog(context);
            passeddata = passed;
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            try {
                this.dialog.show();
                super.onPreExecute();
                this.dialog.setMessage("Getting Data...");
            }catch (Exception e){
                Log.d(TAG + " PreExceute","On pre Exceute......");
            }

        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;



                try {
                    this.dialog.setMessage("Loading Data...");
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    result = new JSONObject(responses.body().string());
                    Log.e(TAG, "doInBackground: "+result +"" );



                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }catch (IOException e) {
                    //this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                    this.dialog.dismiss();
                    Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                    generator.jsondatalogin = null;
                    e.printStackTrace();
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    //this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                    this.dialog.dismiss();
                    e.printStackTrace();
                    Log.e("doInBackground: ", "null data" + e.getMessage());
                    generator.jsondatalogin = null;
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    //this.dialog.setMessage("Loading Data... Error Occured,retrying...");
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
                Log.e(TAG, "Json login data "+result );
                if(result==null){
                    Snackbar.make(parent_view, "Kesalahan Koneksi", Snackbar.LENGTH_SHORT).show();
                }
                else if(result.getString("status").equals("true")){
                    JSONArray data = result.getJSONArray("data");
                    JSONObject dataisi = data.getJSONObject(0);
                    String declare = dataisi.getString("otoritas");
                    String idfp = dataisi.getString("idfp");
                    String id = dataisi.getString("id");
                    if(declare.contains("3")){

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_hrd.class);
                        startActivity(hrd);
                        pref.edit().putString("level","hrd").commit();
                        pref.edit().putString("kodekaryawan",dataisi.getString("kode_karyawan")).commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("idfp",idfp).commit();
                        pref.edit().putString("id",id).commit();
                        pref.edit().putString("tempatlahir",dataisi.getString("tempat_lahir")).commit();
                        pref.edit().putString("profileimage",generator.profileurl+dataisi.getString("foto")).commit();
                        pref.edit().putString("username",dataisi.getString("nama")).commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("hrd");



                    }
                    else if(declare.contains("2")){

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_kabag.class);
                        startActivity(hrd);
                        pref.edit().putString("level","kabag").commit();
                        pref.edit().putString("kodekaryawan",dataisi.getString("kode_karyawan")).commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("idfp",idfp).commit();
                        pref.edit().putString("id",id).commit();
                        pref.edit().putString("tempatlahir",dataisi.getString("tempat_lahir")).commit();
                        pref.edit().putString("profileimage",generator.profileurl+dataisi.getString("foto")).commit();
                        pref.edit().putString("username",dataisi.getString("nama")).commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("kabag");

                    }
                    else{

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_karyawan.class);
                        startActivity(hrd);
                        pref.edit().putString("level","karyawan").commit();
                        pref.edit().putString("kodekaryawan",dataisi.getString("kode_karyawan")).commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("idfp",idfp).commit();
                        pref.edit().putString("id",id).commit();
                        pref.edit().putString("tempatlahir",dataisi.getString("tempat_lahir")).commit();
                        pref.edit().putString("profileimage",generator.profileurl+dataisi.getString("foto")).commit();
                        pref.edit().putString("username",dataisi.getString("nama")).commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("karyawan");


                    }

                }
                else {
                    Snackbar.make(parent_view, result.getString("message"), Snackbar.LENGTH_SHORT).show();
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
                    AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
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
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi", Snackbar.LENGTH_SHORT).show();
                }
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public class daftarcompany extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result =new JSONObject();
        ProgressDialog dialog ;
        String urldata = generator.mainurl;
        String md5code = "" ;

        public daftarcompany(Context context,String md5code)
        {
            this.md5code = md5code;
            dialog = new ProgressDialog(context);
            dialog.setTitle("Memuat Data Kartu");
            dialog.show();
        }

        String TAG = getClass().getSimpleName();

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;



            try {
                this.dialog.setMessage("Memuat Data Perusahan...");
                OkHttpClient client = new OkHttpClient();


                RequestBody body = new FormBody.Builder()
                        .add("id",md5code)
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
                }catch (Exception e){
                    e.printStackTrace();
                }
                result = new JSONObject(responses.body().string());
                Log.e(TAG, "doInBackground: "+result +"" );



            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }catch (IOException e) {
                //this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                e.printStackTrace();
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                this.dialog.dismiss();
                e.printStackTrace();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.setMessage("Loading Data... Error Occured,retrying...");
                this.dialog.dismiss();
                AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Error Connection").setMessage("Terjadi Kesalahan Koneksi , mohon ulangi").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

                dialog.show();

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
                Log.e(TAG, "Json login data "+result );
                if(result!=null){

                    List<company> datacomp = new ArrayList<>();

                    datacomp = dbase.getAllcompany();



                    Log.e(TAG, "json data company"+result );
                    JSONArray arrays = result.getJSONArray("data");
                    JSONObject obj = arrays.getJSONObject(0);

<<<<<<< HEAD
                    company datatemp = dbase.getcompany(md5code);

                    if(datatemp==null){
=======
                    if(datacomp.size()!=0) {
                        for (int i = 0; i < datacomp.size(); i++) {
                            Log.e(TAG, "onPostExecute: " );
                            if (obj.getString("company_name").equals(datacomp.get(i).getCompanyname())) {
                                AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Gagal").setMessage("Data perusahaan sudah diregistrasi").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                                dialog.show();
                            } else {
                                company comp = new company();
                                comp.setCompanyid(md5code);
                                comp.setCompanyname(obj.getString("company_name"));
                                comp.setCompanyip(obj.getString("ip"));
                                comp.setCompanyport(Integer.valueOf(obj.getString("port")));

                                dbase.createAccount(comp, "");
                                refesh();
                            }
                        }
                    }
                    else {
>>>>>>> 44ba226b64a57638e05cda459c183b5e68780162
                        company comp = new company();
                        comp.setCompanyid(md5code);
                        comp.setCompanyname(obj.getString("company_name"));
                        comp.setCompanyip(obj.getString("ip"));
                        comp.setCompanyport(Integer.valueOf(obj.getString("port")));
                        comp.setCompanycodename(obj.getString("codename"));

                        dbase.createAccount(comp,"");
                        refesh();
                    }
                    else {
                        if(datatemp.getCompanyid().equals(md5code)){
                            AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Gagal").setMessage("Data perusahaan sudah diregistrasi").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();

                            dialog.show();
                        }
                        else {
                            company comp = new company();
                            comp.setCompanyid(md5code);
                            comp.setCompanyname(obj.getString("company_name"));
                            comp.setCompanyip(obj.getString("ip"));
                            comp.setCompanyport(Integer.valueOf(obj.getString("port")));
                            comp.setCompanycodename(obj.getString("codename"));

                            dbase.createAccount(comp,"");
                            refesh();
                        }
                    }



                }
                else {
                    AlertDialog dialog = new AlertDialog.Builder(activity_login.this).setTitle("Failure").setMessage("Please retry and check internet connection").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

                    dialog.show();
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
                    AlertDialog alertDialog = new AlertDialog.Builder(activity_login.this).create();
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
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi", Snackbar.LENGTH_SHORT).show();
                }
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public void refesh(){
        companylist.clear();

        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }

        List<company> tempdbase = new ArrayList<>();

        tempdbase = dbase.getAllcompany();

        for (int i=0;i<tempdbase.size();i++) {
            companylist.add(tempdbase.get(i));
        }

        if(companylist.size() == 0){
            linearnodata.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }
        else {
            linearnodata.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }

        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }
}
