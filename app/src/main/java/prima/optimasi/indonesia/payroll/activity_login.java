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

import com.blikoon.qrcodescanner.QrCodeActivity;

import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.utils.Tools;

import org.json.JSONArray;
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

        if(!pref.getString("username","").equals("")){

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

            TestAsync sync = new TestAsync(activity_login.this,generator.keepaliveurl,username,password);
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
        SharedPreferences.Editor edit;
        String password = "" ;
        ProgressDialog dialog ;
        String urldata ;

        public  TestAsync (Context context,String url,String username , String password)
        {
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
            Log.d(TAG + " DoINBackGround","On doInBackground...");

                try {
                    response = okhttpclass.getJsonlogin(urldata, username, password).toString();
                    object = okhttpclass.getJsonlogin(urldata, username, password);
                    token = okhttpclass.getJsonlogin(urldata, username, password);

                    if(object!=null && token!=null ){
                        edit.putString("Authorization",token.getString("token"));
                        edit.putString("username",username);
                        edit.commit();
                        Log.e(TAG, "saved");
                    }
                } catch (IOException e) {
                    object = null;
                    response = "Error IOException";
                } catch (NullPointerException e) {
                    object = null;
                    response = "Please check Connection and Server";
                } catch (Exception e) {
                    object = null;
                    response = "Error Occured, PLease Contact Administrator/Support";
                }
            return response;
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

            try {
                if(object!=null){
                    JSONObject responseObject = object;
                    String status= responseObject.getString("status");

                    Log.e( "onPostExecute: ", object.toString());


                    if(status.equals("true")){
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


            Log.d(TAG + " onPostExecute", "" + result);
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

        }}

}
