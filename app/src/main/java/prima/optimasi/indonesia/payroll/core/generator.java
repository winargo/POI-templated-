package prima.optimasi.indonesia.payroll.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.okhttpclass;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class generator {
    public static String Server="192.168.1.254";
    public static String port = "4000";
    public static String username = "";
    public static String password = "";
    public static String token = "";
    public static String jabatan = "";
    public static String keepaliveurl="http://"+ generator.Server+":"+port+"/login";
    public static String imageurl="http://"+ generator.Server+"/poihrd/uploads/aktivitas/pengumuman/";
    public static String pengumumanurl="http://"+ generator.Server+":4000/pengumuman";
    public static JSONObject jsondatalogin = null ;

    public static Boolean isconnected(Context context){
        Boolean status = false;



        return  status;
    }

    public generator (Context context){
        SharedPreferences pref = context.getSharedPreferences("poipayroll",MODE_PRIVATE);
        generator.username=pref.getString("username","");
        if(generator.username.equals("")){
            Log.e("Warning", "User is Empty" );
        }

        generator.password=pref.getString("password","");
        if(generator.password.equals("")){
            Log.e("Warning", "password is Empty" );
        }
    }

    public static void retryconnection(Context context){

    }

    public static class retrivedata extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        ProgressDialog dialog ;
        String urldata = keepaliveurl;

        public  retrivedata (Context context,ProgressDialog dialogue)
        {
            dialog = dialogue;
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
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
                    response = okhttpclass.getJsonlogin(urldata, generator.username, generator.password).toString();
                    JSONObject dataJSON = okhttpclass.getJsonlogin(urldata, generator.username, generator.password);

                    generator.setJsondatalogin(dataJSON);
                    data=1;
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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {


                //JSONArray bArray= responseObject.getJSONArray("B");
                //for(int i=0;i<bArray.length();i++){
                //    JSONObject innerObject=bArray.getJSONObject(i);
                //    String a= innerObject.getString("a");
                //    String b= innerObject.getString("b");
                //}
            } catch (Exception e) {
                e.printStackTrace();
                error = e.getMessage();
            }


            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

    public static void setJsondatalogin(JSONObject jsondatalogin) {
        generator.jsondatalogin = jsondatalogin;
    }
}
