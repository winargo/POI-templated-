package prima.optimasi.indonesia.payroll.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class generator {
    //public static String Server="153.92.4.147";
    //public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";
    //public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";
    //public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";
    //public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";
    //public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";

    //public static String Server="153.92.4.147";
    public static String Server="192.168.5.254";

    public static Bitmap tempbitmap = null;

    public static String port = "4000";
    public static String username = "";
    public static String password = "";
    public static String token = "";
    public static String jabatan = "";


    public static String keepaliveurl="http://"+ generator.Server+":"+port+"/login";
    public static String imageurl="http://"+ generator.Server+"/poihrd/uploads/aktivitas/pengumuman/";
    public static String profileurl="http://"+ generator.Server+"/poihrd/uploads/master/karyawan/orang/";

    public static String getpicpengumumanurl="http://"+ generator.Server+"/poihrd/uploads/aktivitasowner/pengumuman/";
    public static String uploadpengumumanurl="http://"+ generator.Server+":4000/pengumuman/upload";
    public static String deletepengumumanurl="http://"+ generator.Server+":4000/pengumuman/";
    public static String pengumumanurl="http://"+ generator.Server+":4000/pengumuman";

    public static String getabsensiurl="http://"+ generator.Server+":4000/absensi";

    public static String ownerurl="http://"+ generator.Server+"/poihrd/uploads/pengguna/";

    public static String scanloginurl = "http://"+ generator.Server+":4000/loginScan";
    public static String sendtokenurl = "http://"+ generator.Server+":4000/postnotif";
    public static String listemployeeurl = "http://"+ generator.Server+":4000/karyawan";

    public static String calenderurl = "http://"+ generator.Server+":4000/calendar";

    public static String cutiurl="http://"+ generator.Server+":4000/laporan/cuti";
    public static String sakiturl="http://"+ generator.Server+":4000/laporan/sakit";
    public static String pinjamanurl = "http://"+ generator.Server+":4000/laporan/pinjaman";
    public static String izinurl = "http://"+ generator.Server+":4000/laporan/izin";
    public static String pengajianurl = "http://"+ generator.Server+":4000/laporan/pengajian";

    public static String checkinurl="http://"+ generator.Server+":4000/absensi/checkin";
    public static String checkouturl="http://"+ generator.Server+":4000/absensi/checkout";
    public static String breakinurl="http://"+ generator.Server+":4000/absensi/breakin";
    public static String breakouturl="http://"+ generator.Server+":4000/absensi/breakout";
    public static String extrainurl="http://"+ generator.Server+":4000/absensi/extrain";
    public static String extraouturl="http://"+ generator.Server+":4000/absensi/extraout";

    public static String scheckinurl="http://"+ generator.Server+":4000/absensiSecurity/checkin";
    public static String scheckouturl="http://"+ generator.Server+":4000/absensiSecurity/checkout";
    public static String sbreakinurl="http://"+ generator.Server+":4000/absensiSecurity/breakin";
    public static String sbreakouturl="http://"+ generator.Server+":4000/absensiSecurity/breakout";
    public static String scheckemployeeyurl="http://"+ generator.Server+":4000/absensiSecurity/checkkaryawan";

    public static String approvaldinasyurl="http://"+ generator.Server+":4000/approvalDinas";
    public static String approvalcutiyurl="http://"+ generator.Server+":4000/approvalCuti";
    public static String approvalizinyurl="http://"+ generator.Server+":4000/approvalIzin";
    public static String approvaldirumahkanyurl="http://"+ generator.Server+":4000/approvalDirumahkan";
    public static String approvalkaryawanyurl="http://"+ generator.Server+":4000/approvalKaryawan";
    public static String approvalrewardyurl="http://"+ generator.Server+":4000/approvalReward";
    public static String approvalpunihsmentyurl="http://"+ generator.Server+":4000/approvalPunishment";
    public static String approvalpinjamanyurl="http://"+ generator.Server+":4000/approvalPinjaman";
    public static String approvalgolonganyurl="http://"+ generator.Server+":4000/approvalGolongan";
    public static String approvalpdlyurl="http://"+ generator.Server+":4000/approvalPdl";

    public static String getapprovaldinasyurl="http://"+ generator.Server+":4000/dinas";
    public static String getapprovalcutiyurl="http://"+ generator.Server+":4000/cuti";
    public static String getapprovalizinyurl="http://"+ generator.Server+":4000/izin";
    public static String getapprovaldirumahkanyurl="http://"+ generator.Server+":4000/dirumahkan";
    public static String getapprovalkaryawanyurl="http://"+ generator.Server+":4000/karyawan";
    public static String getapprovalrewardyurl="http://"+ generator.Server+":4000/reward";
    public static String getapprovalpunihsmentyurl="http://"+ generator.Server+":4000/punishment";
    public static String getapprovalpinjamanyurl="http://"+ generator.Server+":4000/pinjaman";
    public static String getapprovalgolonganyurl="http://"+ generator.Server+":4000/golongan";
    public static String getapprovalpdlyurl="http://"+ generator.Server+":4000/pdl";


    public static String jadwalurl="http://"+ generator.Server+":4000/karyawan/checkjadwal/";


    public static JSONObject jsondatalogin = null ;
    public static JSONObject jsondatajadwal = null ;

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
                alertDialog.setTitle("Selamat Datang");
                alertDialog.setMessage("Anda Berhasil Login PrimaHRD");
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

    public static class getjadwal extends AsyncTask<Void, Integer, String>
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
        String urldata = generator.jadwalurl;
        String kodekaryawan = "" ;

        public getjadwal(Context context, String passed,String authtoken)
        {
            this.authtoken=authtoken;
            cntx = context;
            generator.jsondatajadwal = null;
            prefs = context.getSharedPreferences("poipayroll",MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            kodekaryawan = passed;
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


                    Request request = new Request.Builder()
                            .header("Authorization",authtoken)
                            .url(urldata+kodekaryawan)
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
                generator.jsondatajadwal = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatajadwal = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.setMessage("Loading Data... Error Occured,retrying...");
                this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatajadwal = null;
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
                    Log.e(TAG, "onPostExecute: "+result );
                    generator.jsondatajadwal = result;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            try {
                AlertDialog alertDialog = new AlertDialog.Builder(cntx).create();
                alertDialog.setTitle("Selamat Datang");
                alertDialog.setMessage("Anda Berhasil Login PrimaHRD");
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

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
