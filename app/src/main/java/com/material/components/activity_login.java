package com.material.components;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.InstrumentationInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class activity_login extends AppCompatActivity {

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

        if(!pref.getString("username","").equals("")){
            Intent mainmenu = new Intent(activity_login.this,activity_mainmenu.class);
            startActivity(mainmenu);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TestAsync sync = new TestAsync(activity_login.this,"http://"+generator.Server+"/poihrd/auth/login_ajax",username,password);
                sync.execute();


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
                response = "Error Data Null Occured";
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

            try {
                if(object!=null){
                    JSONObject responseObject = new JSONObject(response);
                    String status= responseObject.getString("status");

                    if(status.equals("true")){
                        Snackbar.make(parent_view, "Success , Loging in ...", Snackbar.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent mainmenu = new Intent(activity_login.this,activity_mainmenu.class);
                                startActivity(mainmenu);

                                SharedPreferences.Editor prefedit = pref.edit();
                                prefedit.putString("username",username);
                                prefedit.putString("password",password);
                                prefedit.apply();

                                finish();

                            }
                        }, 1000);

                    }
                    else if(status.equals("false")) {
                        Snackbar.make(parent_view, status, Snackbar.LENGTH_SHORT).show();
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
