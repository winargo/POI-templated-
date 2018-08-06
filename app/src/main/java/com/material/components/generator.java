package com.material.components;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class generator {

    public static String Server="192.168.1.254";


    public static Boolean isconnected(Context context){
        Boolean status = false;



        return  status;
    }

    public static void retryconnection(Context context){

    }

    private class ConnectionAsync extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        JSONObject object = null;
        String username=  "" ;
        String password = "" ;
        ProgressDialog dialog ;
        String urldata ;

        public  ConnectionAsync (Context context, String url, String username , String password)
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                            }
                        }, 1000);

                    }
                    else if(status.equals("false")) {
                    }
                    else {
                    }

                }
                else {
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
