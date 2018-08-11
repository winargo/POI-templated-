package prima.optimasi.indonesia.payroll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    private ProgressBar progress_bar;
    private FloatingActionButton fab;
    SharedPreferences pref;
    private View parent_view;
    TextView usr;
    TextView pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(!pref.getString("username","").equals("") && !pref.getString("level","").equals("") ){

            if(pref.getString("level","").equals("hrd")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_hrd.class);
                startActivity(mainmenu);
                finish();
            }
            else if(pref.getString("level","").equals("kabag")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_kabag.class);
                startActivity(mainmenu);
                finish();
            }
            else if(pref.getString("level","").equals("owner")){
                Intent mainmenu = new Intent(activity_login.this,mainmenu_owner.class);
                startActivity(mainmenu);
                finish();
            }
            else {
                Intent mainmenu = new Intent(activity_login.this,mainmenu_karyawan.class);
                startActivity(mainmenu);
                finish();
            }

        }
        else {

            usr = findViewById(R.id.usrname);
            pass = findViewById(R.id.usrpass);

            parent_view = findViewById(android.R.id.content);
            progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
            fab = (FloatingActionButton) findViewById(R.id.fab);

            Tools.setSystemBarColor(this, R.color.cyan_800);

            ((View) findViewById(R.id.sign_up_for_account)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(parent_view, "Sign up for an account", Snackbar.LENGTH_SHORT).show();
                }
            });

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

        TestAsync sync = new TestAsync(activity_login.this,"http://"+ generator.Server+"/poihrd/auth/login_ajax",username,password);
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
        JSONObject object = null;
        String username=  "" ;
        String password = "" ;
        ProgressDialog dialog ;
        String urldata ;

        public  TestAsync (Context context,String url,String username , String password)
        {
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
                response = okhttpclass.getJsonlogin(urldata,username,password).toString();
                object = okhttpclass.getJsonlogin(urldata,username,password);
            } catch (IOException e) {
                object = null;
                response = "Error IOException";
            } catch (NullPointerException e){
                object = null;
                response = "Please check Connection and Server";
            }catch (Exception e){
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
                    JSONObject responseObject = new JSONObject(response);
                    String status= responseObject.getString("status");




                    if(status.equals("true")){
                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        JSONObject array = object.getJSONObject("data");
                        final String level = array.getString("level");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                SharedPreferences.Editor prefedit = pref.edit();
                                prefedit.putString("username",username);
                                prefedit.putString("password",password);
                                prefedit.putString("level",level);
                                prefedit.commit();

                                if(level.equals("hrd")){
                                    Intent mainmenu = new Intent(activity_login.this,mainmenu_hrd.class);
                                    startActivity(mainmenu);
                                    finish();
                                }
                                else if(level.equals("kabag")){
                                    Intent mainmenu = new Intent(activity_login.this,mainmenu_kabag.class);
                                    startActivity(mainmenu);
                                    finish();
                                }
                                else if(level.equals("owner")){
                                    Intent mainmenu = new Intent(activity_login.this,mainmenu_owner.class);
                                    startActivity(mainmenu);
                                    finish();
                                }
                                else {
                                    Intent mainmenu = new Intent(activity_login.this,mainmenu_karyawan.class);
                                    startActivity(mainmenu);
                                    finish();
                                }

                                finish();

                            }
                        }, 1000);

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



}
