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

import com.google.firebase.messaging.FirebaseMessaging;

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
    public static String Server="192.168.5.254";
    //public static String Server="153.92.4.147";
    //public static String Server="153.92.4.147";

    public static mainmenu_owner tempactivity = null;

    //public static String Server="153.92.4.147";

    public static Bitmap tempbitmap = null;

    public static String port = "4000";
    public static String username = "";
    public static String password = "";
    public static String token = "";
    public static String jabatan = "";

    public static String servertime = "";

    public static int jobcount = 0 ;


    public static String keepaliveurl="http://"+ generator.Server+":"+port+"/login";
    public static String imageurl="http://"+ generator.Server+"/poihrd/uploads/aktivitas/pengumuman/";
    public static String profileurl="http://"+ generator.Server+"/poihrd/uploads/master/karyawan/orang/";

    public static String getpicpengumumanurl="http://"+ generator.Server+"/poihrd/uploads/aktivitasowner/pengumuman/";
    public static String uploadpengumumanurl="http://"+ generator.Server+":"+generator.port+"/pengumuman/upload";
    public static String deletepengumumanurl="http://"+ generator.Server+":"+generator.port+"/pengumuman/";
    public static String pengumumanurl="http://"+ generator.Server+":"+generator.port+"/pengumuman";

    public static String kabaggrupkaryawanurl="http://"+ generator.Server+":"+generator.port+"/groupkaryawan/list";

    public static String getabsensiurl="http://"+ generator.Server+":"+generator.port+"/absensi";

    public static String ownerurl="http://"+ generator.Server+"/poihrd/uploads/pengguna/";

    public static String scanloginurl = "http://"+ generator.Server+":"+generator.port+"/loginScan";
    public static String sendtokenurl = "http://"+ generator.Server+":"+generator.port+"/postnotif";
    public static String listemployeeurl = "http://"+ generator.Server+":"+generator.port+"/karyawan";

    public static String calenderurl = "http://"+ generator.Server+":"+generator.port+"/calendar";

    public static String cutiurl="http://"+ generator.Server+":"+generator.port+"/laporan/cuti";
    public static String sakiturl="http://"+ generator.Server+":"+generator.port+"/laporan/sakit";
    public static String pinjamanurl = "http://"+ generator.Server+":"+generator.port+"/laporan/pinjaman";
    public static String izinurl = "http://"+ generator.Server+":"+generator.port+"/laporan/izin";
    public static String pengajianurl = "http://"+ generator.Server+":"+generator.port+"/laporan/pengajian";

    public static String checkinurl="http://"+ generator.Server+":"+generator.port+"/absensi/checkin";
    public static String checkouturl="http://"+ generator.Server+":"+generator.port+"/absensi/checkout";
    public static String breakinurl="http://"+ generator.Server+":"+generator.port+"/absensi/breakin";
    public static String breakouturl="http://"+ generator.Server+":"+generator.port+"/absensi/breakout";
    public static String extrainurl="http://"+ generator.Server+":"+generator.port+"/absensi/extrain";
    public static String extraouturl="http://"+ generator.Server+":"+generator.port+"/absensi/extraout";

    public static String scheckinurl="http://"+ generator.Server+":"+generator.port+"/absensiSecurity/checkin";
    public static String scheckouturl="http://"+ generator.Server+":"+generator.port+"/absensiSecurity/checkout";
    public static String sbreakinurl="http://"+ generator.Server+":"+generator.port+"/absensiSecurity/breakin";
    public static String sbreakouturl="http://"+ generator.Server+":"+generator.port+"/absensiSecurity/breakout";
    public static String scheckemployeeyurl="http://"+ generator.Server+":"+generator.port+"/absensiSecurity/checkkaryawan";

    public static String approvaldinasyurl="http://"+ generator.Server+":"+generator.port+"/approvalDinas";
    public static String approvalcutiyurl="http://"+ generator.Server+":"+generator.port+"/approvalCuti";
    public static String approvalizinyurl="http://"+ generator.Server+":"+generator.port+"/approvalIzin";
    public static String approvaldirumahkanyurl="http://"+ generator.Server+":"+generator.port+"/approvalDirumahkan";
    public static String approvalkaryawanyurl="http://"+ generator.Server+":"+generator.port+"/approvalKaryawan";
    public static String approvalrewardyurl="http://"+ generator.Server+":"+generator.port+"/approvalReward";
    public static String approvalpunihsmentyurl="http://"+ generator.Server+":"+generator.port+"/approvalPunishment";
    public static String approvalpinjamanyurl="http://"+ generator.Server+":"+generator.port+"/approvalPinjaman";
    public static String approvalgolonganyurl="http://"+ generator.Server+":"+generator.port+"/approvalGolongan";
    public static String approvalpdlyurl="http://"+ generator.Server+":"+generator.port+"/approvalPdm";

    public static String getapprovaldinasyurl="http://"+ generator.Server+":"+generator.port+"/approvaldinas";
    public static String getapprovalcutiyurl="http://"+ generator.Server+":"+generator.port+"/approvalcuti";
    public static String getapprovalizinyurl="http://"+ generator.Server+":"+generator.port+"/approvalizin";
    public static String getapprovaldirumahkanyurl="http://"+ generator.Server+":"+generator.port+"/approvaldirumahkan";
    public static String getapprovalkaryawanyurl="http://"+ generator.Server+":"+generator.port+"/karyawan/approval/proses";
    public static String getapprovalrewardyurl="http://"+ generator.Server+":"+generator.port+"/approvalreward";
    public static String getapprovalpunihsmentyurl="http://"+ generator.Server+":"+generator.port+"/approvalpunishment";
    public static String getapprovalpinjamanyurl="http://"+ generator.Server+":"+generator.port+"/pinjaman/approval/proses";
    public static String getapprovalgolonganyurl="http://"+ generator.Server+":"+generator.port+"/approvalGolongan/proses";
    public static String getapprovalpdlyurl="http://"+ generator.Server+":"+generator.port+"/approvalpdm/proses";

    public static String getizinbulananyurl="http://"+ generator.Server+":"+generator.port+"/izin/bulan";
    public static String getcutibulananyurl="http://"+ generator.Server+":"+generator.port+"/cuti/bulan";
    public static String getsakitbulananyurl="http://"+ generator.Server+":"+generator.port+"/sakit/bulan";
    public static String getabsensikaryawanurl="http://"+ generator.Server+":"+generator.port+"/logabsensi";

    public static String jadwalurl="http://"+ generator.Server+":"+generator.port+"/karyawan/checkjadwal";

    public static String servertimeurl="http://"+ generator.Server+":"+generator.port+"/setting/waktu";

    public static String pengajuancutiurl="http://"+ generator.Server+":"+generator.port+"/cuti";
    public static String pengajuanizinurl="http://"+ generator.Server+":"+generator.port+"/izin";
    public static String pengajuansakiturl="http://"+ generator.Server+":"+generator.port+"/sakit";
    public static String pengajuandinasurl="http://"+ generator.Server+":"+generator.port+"/dinas";

    public static String totalgajiurl="http://"+ generator.Server+":"+generator.port+"/penggajian/totalgaji";

    public static String pengajuancutikodeurl="http://"+ generator.Server+":"+generator.port+"/cuti/kode";
    public static String pengajuanizinkodeurl="http://"+ generator.Server+":"+generator.port+"/izin/kode";
    public static String pengajuansakitkodeurl="http://"+ generator.Server+":"+generator.port+"/sakit/kode";
    public static String pengajuandinaskodeurl="http://"+ generator.Server+":"+generator.port+"/dinas/kode";

    public static String getdataizinbulananyurl="http://"+ generator.Server+":"+generator.port+"/izin/bulanan";

    public static String kontrakkerjahabisurl="http://"+ generator.Server+":"+generator.port+"/kontrak/habis";
    public static String kontrakkerja1bulanurl="http://"+ generator.Server+":"+generator.port+"/kontrak/1bulan";
    public static String kontrakkerja2bulanurl="http://"+ generator.Server+":"+generator.port+"/kontrak/2bulan";
    public static String kontrakkerja3bulanurl="http://"+ generator.Server+":"+generator.port+"/kontrak/3bulan";

    public static String daftarabsensiurl="http://"+ generator.Server+":"+generator.port+"/absensi/today";
    public static String jabatanurl="http://"+ generator.Server+":"+generator.port+"/jabatan";

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

    public static void logout(Context ctx, String jabatan){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(jabatan);

        Intent logout = new Intent(ctx,activity_login.class);
        SharedPreferences prefs = ctx.getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){

        }
        else {
            generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(ctx,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
            unregistertokentoserver.execute();
        }


        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("id","");
        edit.putString("idfp","");
        edit.putString("username","");
        edit.putString("jabatan","");
        edit.putString("level","");
        edit.putString("tempatlahir","");
        edit.putString("profileimage","");
        edit.putString("Authorization","");
        edit.putString("kodekaryawan","");

        edit.commit();

        ctx.startActivity(logout);
    }

}
