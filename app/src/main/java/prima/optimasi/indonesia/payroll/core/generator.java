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
    public static String Server="192.168.1.222";
    public static String keepaliveurl="http://"+ generator.Server+"/poihrd/auth/login_ajax";
    public static JSONObject jsondatalogin = null ;

    public static Boolean isconnected(Context context){
        Boolean status = false;



        return  status;
    }

    public static void retryconnection(Context context){

    }

    private static class retrivedata extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        ProgressDialog dialog ;
        String urldata ;

        public  retrivedata (Context context,String url,String username , String password,String error)
        {
            dialog = new ProgressDialog(context);
            this.urldata = url;
            this.username = username;
            this.password = password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            this.dialog.setMessage("Getting Data...");
            this.dialog.show();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                response = okhttpclass.getJsonlogin(urldata,username,password).toString();
                generator.jsondatalogin = okhttpclass.getJsonlogin(urldata,username,password);
            } catch (IOException e) {
                generator.jsondatalogin  = null;
                response = "Error IOException";
            } catch (NullPointerException e){
                generator.jsondatalogin  = null;
                response = "Please check Connection and Server";
            }catch (Exception e){
                generator.jsondatalogin  = null;
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

            if(dialog.isShowing()){
                dialog.dismiss();
            }
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
}
