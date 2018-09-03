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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class activity_login extends FragmentActivity {

    public String TAG="activity_login";

    private ProgressBar progress_bar;
    private FloatingActionButton fab;
    SharedPreferences pref;
    private View parent_view;
    LinearLayout scanqr;
    TextView usr;
    TextView pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        pref = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(!generator.token.equals("")){
            pref.edit().putString("tokennotif",generator.token).commit();
            pref.edit().putInt("statustoken",0).commit();
            Log.e(TAG, "Data saved");
        }

        /*if(pref.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,pref.getString("tokennotif",""));
            register.execute();

        }*/

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
            else if(declare.equals("kepala")){
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

                    if (ContextCompat.checkSelfPermission(activity_login.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(activity_login.this, new String[] {Manifest.permission.CAMERA}, 4);
                    }
                    else {
                        Intent i = new Intent(activity_login.this,QrCodeActivity.class);
                        startActivityForResult( i,104);
                    }


                }
            });

            Tools.setSystemBarColor(this, R.color.cyan_800);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(usr!=null && pass!=null){
                        if(usr.getText().toString().equals("")){
                            Snackbar.make(parent_view,"Username Is Empty",Snackbar.LENGTH_SHORT).show();

                        }
                        else if(pass.getText().toString().equals("")){
                            Snackbar.make(parent_view,"Please Input Password",Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            searchAction(usr.getText().toString(),pass.getText().toString());
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
                                pref.edit().putString("Authorization",token.getString("token")).commit();
                                pref.edit().putString("jabatan","owner").commit();
                                pref.edit().putString("iduser","").commit();
                                pref.edit().putString("username",username).commit();
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
                try {
                    if(object!=null){
                        JSONObject responseObject = object;
                        String status= responseObject.getString("status");

                        Log.e( "onPostExecute: ", object.toString());


                        if(status.equals("true")){
                            if(object!=null && token!=null ){
                                pref.edit().putString("level","owner").commit();
                                pref.edit().putString("Authorization",retoken).commit();
                                pref.edit().putString("jabatan","owner").commit();
                                pref.edit().putString("iduser","").commit();
                                pref.edit().putString("username",username).commit();
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
                //Getting the passed result
                String result = intent.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Log.d(TAG,"Have scan result in your app activity :"+ result);

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
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 4) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(activity_login.this,QrCodeActivity.class);
                startActivityForResult( i,104);

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
                if(result.getString("status").equals("true")){
                    JSONArray data = result.getJSONArray("data");
                    JSONObject dataisi = data.getJSONObject(0);
                    String declare = dataisi.getString("jabatan");
                    String iduser = dataisi.getString("idfp");
                    if(declare.contains("hrd")){

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_hrd.class);
                        startActivity(hrd);
                        pref.edit().putString("level","hrd").commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("iduser",iduser).commit();
                        pref.edit().putString("username",dataisi.getString("nama")).commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("hrd");



                    }
                    else if(declare.contains("Kepala") || declare.toUpperCase().contains("KEPALA") || declare.toLowerCase().contains("kepala")){

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_kabag.class);
                        startActivity(hrd);
                        pref.edit().putString("level","kabag").commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("iduser",iduser).commit();
                        pref.edit().putString("username",dataisi.getString("nama")).commit();

                        FirebaseMessaging.getInstance().subscribeToTopic("kabag");

                    }
                    else{

                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        Intent hrd = new Intent(activity_login.this,mainmenu_karyawan.class);
                        startActivity(hrd);
                        pref.edit().putString("level","karyawan").commit();
                        pref.edit().putString("Authorization",result.getString("token")).commit();
                        pref.edit().putString("jabatan",declare).commit();
                        pref.edit().putString("iduser",iduser).commit();
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

}
