package prima.optimasi.indonesia.payroll.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.main_hrd.mainmenu_hrd;
import prima.optimasi.indonesia.payroll.main_kabag.mainmenu_kabag;
import prima.optimasi.indonesia.payroll.main_karyawan.mainmenu_karyawan;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.okhttpclass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

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
    public static String profileurl="http://"+ generator.Server+"/poihrd/uploads/master/karyawan/orang/";
    public static String ownerurl="http://"+ generator.Server+"/poihrd/uploads/pengguna/";
    public static String pengumumanurl="http://"+ generator.Server+":4000/pengumuman";
    public static String scanloginurl = "http://"+ generator.Server+":4000/loginScan";
    public static String sendtokenurl = "http://"+ generator.Server+":4000/postnotif";
    public static String listemployeeurl = "http://"+ generator.Server+":4000/karyawan";
    public static String checkinurl="http://"+ generator.Server+"/absensi/checkin";
    public static String checkouturl="http://"+ generator.Server+"/absensi/checkout";
    public static String breakinurl="http://"+ generator.Server+"/absensi/breakin";
    public static String breakouturl="http://"+ generator.Server+"/absensi/breakout";
    public static String extrainurl="http://"+ generator.Server+"/absensi/extrain";
    public static String extraouturl="http://"+ generator.Server+"/absensi/extraout";


    public static JSONObject jsondatalogin = null ;

    //tempdata
    public static List<pengumuman> temppengumumanpassdata = null;

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

    public static class registertokentoserver extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        Context cntx;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = generator.scanloginurl;
        String passeddata = "" ;

        public  registertokentoserver(Context context, String passed)
        {
            cntx = context;
            prefs = context.getSharedPreferences("poipayroll",MODE_PRIVATE);
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
            this.dialog.setMessage("Registering Device...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;

            try {
                this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("tokennotif",passeddata)
                            .add("level",prefs.getString("level",""))
                            .add("userid",prefs.getString("iduser",""))
                            .build();

                    Request request = new Request.Builder()
                            .url(generator.sendtokenurl)
                            .post(body)
                            .build();
                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                    }

                    if (responses==null){
                        Log.e(TAG, "sendRegistrationToServer: failed" );


                    }
                    else {
                        jsonObject = new JSONObject(responses.body().string());
                        Log.e(TAG, "success" );
                        result = jsonObject;
                        data=1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                }
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
            String res = "";

            if(result!=null){
                try {
                    if(result.getString("status").equals("true")){
                        prefs.edit().putInt("statustoken",1).commit();
                        Log.e(TAG, "onPostExecute: "+"Registered to server" );
                    }
                    else {
                        res = "Fail";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            try {
                AlertDialog alertDialog = new AlertDialog.Builder(cntx).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage(result.toString() + res);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

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


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public static class unregistertokentoserver extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String authtoken="";
        String username=  "" ;
        String password = "" ;
        Context cntx;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = generator.scanloginurl;
        String passeddata = "" ;

        public  unregistertokentoserver(Context context, String passed,String authtoken)
        {
            this.authtoken=authtoken;
            cntx = context;
            prefs = context.getSharedPreferences("poipayroll",MODE_PRIVATE);
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
            this.dialog.setMessage("Registering Device...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;

            try {
                this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = new FormBody.Builder()
                            .add("tokennotif",passeddata)
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization",authtoken)
                            .url(generator.sendtokenurl)
                            .delete(body)
                            .build();
                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                    }

                    if (responses==null){
                        Log.e(TAG, "sendRegistrationToServer: failed" );

                    }
                    else {
                        jsonObject = new JSONObject(responses.body().string());
                        Log.e(TAG, "success" );
                        result = jsonObject;
                        data=1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "sendRegistrationToServer: failed" + e.getMessage() );
                }
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
            String res = "";

            if(result!=null){
                try {
                    if(result.getString("status").equals("true")){
                        prefs.edit().putInt("statustoken",0).commit();

                        Log.e(TAG, "onPostExecute: "+"Registered to server" );
                        ((Activity)cntx).finish();
                    }
                    else {
                        res = "Fail";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            try {
                AlertDialog alertDialog = new AlertDialog.Builder(cntx).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage(result.toString() + res);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

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


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

}
