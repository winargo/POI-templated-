package qrcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import qrcodescanner.camera.CameraManager;
import qrcodescanner.decode.CaptureActivityHandler;
import qrcodescanner.decode.DecodeImageCallback;
import qrcodescanner.decode.DecodeImageThread;
import qrcodescanner.decode.DecodeManager;
import qrcodescanner.decode.InactivityTimer;
import qrcodescanner.view.QrCodeFinderView;


public class QrCodeActivity extends Activity implements Callback, OnClickListener {

    private static final int REQUEST_SYSTEM_PICTURE = 0;
    private static final int REQUEST_PICTURE = 1;
    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;
    private CaptureActivityHandler mCaptureActivityHandler;
    private boolean mHasSurface;
    private boolean mPermissionOk;
    private InactivityTimer mInactivityTimer;
    private QrCodeFinderView mQrCodeFinderView;
    private SurfaceView mSurfaceView;
    private View mLlFlashLight;
    private final DecodeManager mDecodeManager = new DecodeManager();

    private int stayscanning=0;

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;
    private boolean mNeedFlashLightOpen = true;
    private ImageView mIvFlashLight;
    private TextView mTvFlashLightText;
    private Executor mQrCodeExecutor;
    private Handler mHandler;

    private final String GOT_RESULT = "com.blikoon.qrcodescanner.got_qr_scan_relult";
    private final String ERROR_DECODING_IMAGE = "com.blikoon.qrcodescanner.error_decoding_image";
    private final String LOGTAG = "QRScannerQRCodeActivity";
    private Context mApplicationContext;

    private static Intent createIntent(Context context) {
        Intent i = new Intent(context, QrCodeActivity.class);
        return i;
    }

    public static void launch(Context context) {
        Intent i = createIntent(context);
        context.startActivity(i);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initView();
        initData();
        mApplicationContext = getApplicationContext();

    }

    private void checkPermission() {
        boolean hasHardware = checkCameraHardWare(this);
        if (hasHardware) {
            if (!hasCameraPermission()) {
                findViewById(R.id.qr_code_view_background).setVisibility(View.VISIBLE);
                mQrCodeFinderView.setVisibility(View.GONE);
                mPermissionOk = false;
            } else {
                mPermissionOk = true;
            }
        } else {
            mPermissionOk = false;
            finish();
        }
    }

    private void initView() {
        TextView tvPic = (TextView) findViewById(R.id.qr_code_header_black_pic);
        tvPic.setVisibility(View.GONE);
        mIvFlashLight = (ImageView) findViewById(R.id.qr_code_iv_flash_light);
        mTvFlashLightText = (TextView) findViewById(R.id.qr_code_tv_flash_light);
        mQrCodeFinderView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        mSurfaceView = (SurfaceView) findViewById(R.id.qr_code_preview_view);
        mLlFlashLight = findViewById(R.id.qr_code_ll_flash_light);
        mHasSurface = false;
        mIvFlashLight.setOnClickListener(this);
        tvPic.setOnClickListener(this);
    }

    private void initData() {
        CameraManager.init(this);
        mInactivityTimer = new InactivityTimer(QrCodeActivity.this);
        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new WeakHandler(this);
    }

    private boolean hasCameraPermission() {
        PackageManager pm = getPackageManager();
        return PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.CAMERA", getPackageName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        if (!mPermissionOk) {
            mDecodeManager.showPermissionDeniedDialog(this);
            return;
        }
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        turnFlashLightOff();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCaptureActivityHandler != null) {
            mCaptureActivityHandler.quitSynchronously();
            mCaptureActivityHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (null != mInactivityTimer) {
            mInactivityTimer.shutdown();
        }
        super.onDestroy();
    }


    public void handleDecode(Result result) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (null == result) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            String resultString = result.getText();

            handleResult(resultString);

        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.qr_code_camera_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            mDecodeManager.showPermissionDeniedDialog(this);
            return;
        }
        mQrCodeFinderView.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        mLlFlashLight.setVisibility(View.VISIBLE);
        findViewById(R.id.qr_code_view_background).setVisibility(View.GONE);
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = new CaptureActivityHandler(this);
        }
    }

    private void restartPreview() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler.restartPreviewAndDecode();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return mCaptureActivityHandler;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.qr_code_iv_flash_light)
        {
            if (mNeedFlashLightOpen) {
                turnFlashlightOn();
            } else {
                turnFlashLightOff();
            }

        }else if(v.getId() == R.id.qr_code_header_black_pic)
        {
            if (!hasCameraPermission()) {
                mDecodeManager.showPermissionDeniedDialog(this);
            } else {
                openSystemAlbum();
            }

        }

    }

    private void openSystemAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_SYSTEM_PICTURE);
    }

    private void turnFlashlightOn() {
        mNeedFlashLightOpen = false;
        mTvFlashLightText.setText(getString(R.string.qr_code_close_flash_light));
        mIvFlashLight.setBackgroundResource(R.drawable.flashlight_turn_off);
        CameraManager.get().setFlashLight(true);
    }

    private void turnFlashLightOff() {
        mNeedFlashLightOpen = true;
        mTvFlashLightText.setText(getString(R.string.qr_code_open_flash_light));
        mIvFlashLight.setBackgroundResource(R.drawable.flashlight_turn_on);
        CameraManager.get().setFlashLight(false);
    }

    private void handleResult(String resultString) {
        if (TextUtils.isEmpty(resultString)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {

            try {
                if(getIntent().getIntExtra("keepalive",0)==1){
                    SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

                    boolean security = false;

                    if(getIntent().getIntExtra("security",0)==1){
                        security = true;
                    }

                    absensi abs = new absensi(QrCodeActivity.this,prefs.getString("Authorization",""),getIntent().getIntExtra("absensi",0),resultString,prefs.getString("kodekaryawan",""),security);
                    abs.execute();
                }
                else {
                    Log.d(LOGTAG,"Got scan result from user loaded image :"+resultString);
                    Intent data = new Intent();
                    data.putExtra(GOT_RESULT,resultString);
                    setResult(Activity.RESULT_OK,data);

                    finish();
                }
            }
            catch (Exception e){
                Log.d(LOGTAG,"Got scan result from user loaded image :"+resultString);
                Intent data = new Intent();
                data.putExtra(GOT_RESULT,resultString);
                setResult(Activity.RESULT_OK,data);

                finish();

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICTURE:
                finish();
                break;
            case REQUEST_SYSTEM_PICTURE:
                Uri uri = data.getData();
                String imgPath = getPathFromUri(uri);
                if (imgPath!=null && !TextUtils.isEmpty(imgPath) &&null != mQrCodeExecutor)
                {
                    mQrCodeExecutor.execute(new DecodeImageThread(imgPath, mDecodeImageCallback));
                }
                break;
        }
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private DecodeImageCallback mDecodeImageCallback = new DecodeImageCallback() {
        @Override
        public void decodeSucceed(Result result) {
            //Got scan result from scaning an image loaded by the user
            Log.d(LOGTAG,"Decoded the image successfully :"+ result.getText());
            Intent data = new Intent();
            data.putExtra(GOT_RESULT,result.getText());

            try {
                if(getIntent().getStringExtra("keepalive").equals("1")){

                }
            }
            catch (Exception e){
                setResult(Activity.RESULT_OK,data);
                finish();
            }

        }

        @Override
        public void decodeFail(int type, String reason) {
            Log.d(LOGTAG,"Something went wrong decoding the image :"+ reason);
            Intent data = new Intent();
            data.putExtra(ERROR_DECODING_IMAGE,reason);

            try {
                if(getIntent().getStringExtra("keepalive").equals("1")){

                }
            }
            catch (Exception e){
                setResult(Activity.RESULT_CANCELED,data);
                finish();
            }


        }
    };

    private static class WeakHandler extends Handler {
        private WeakReference<QrCodeActivity> mWeakQrCodeActivity;
        private DecodeManager mDecodeManager = new DecodeManager();

        public WeakHandler(QrCodeActivity imagePickerActivity) {
            super();
            this.mWeakQrCodeActivity = new WeakReference<>(imagePickerActivity);
        }


        @Override
        public void handleMessage(Message msg) {
            QrCodeActivity qrCodeActivity = mWeakQrCodeActivity.get();
            switch (msg.what) {
                case MSG_DECODE_SUCCEED:
                    Result result = (Result) msg.obj;
                    if (null == result) {
                        mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    } else {
                        String resultString = result.getText();
                        handleResult(resultString);
                    }
                    break;
                case MSG_DECODE_FAIL:
                    mDecodeManager.showCouldNotReadQrCodeFromPicture(qrCodeActivity);
                    break;
            }
            super.handleMessage(msg);
        }

        private void handleResult(String resultString) {
            QrCodeActivity imagePickerActivity = mWeakQrCodeActivity.get();

            mDecodeManager.showResultDialog(imagePickerActivity, resultString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

    }


    public class absensi extends AsyncTask<Void, Integer, String>
    {
        Context ctx;
        String response = "";
        String error = "";
        String kabag = "" ;
        SharedPreferences prefs ;
        JSONObject result ;
        ProgressDialog dialog ;
        String urldata = "";
        String kode = "" ;
        int type=0;
        boolean issecurity = false;

        public  absensi(Context context,String token,int type,String kodek,String kodekaba,boolean issecurity)
        {
            kode = kodek;
            this.type = type;
            kabag = kodekaba;
            this.issecurity = issecurity;

            ctx = context;
            dialog = new ProgressDialog(context);

            if(issecurity){
                if(type ==1){
                    urldata = generator.scheckemployeeyurl;
                }
                else if(type == 2){
                    urldata = generator.scheckinurl;
                }else if(type ==3){
                    urldata = generator.sbreakouturl;
                }else if(type ==4){
                    urldata = generator.sbreakinurl;
                }else if(type ==5){
                    urldata = generator.scheckouturl;
                }
            }
            else {
                if(type == 0){
                    urldata = generator.checkinurl;
                }else if(type ==1){
                    urldata = generator.breakouturl;
                }else if(type ==2){
                    urldata = generator.breakinurl;
                }else if(type ==3){
                    urldata = generator.checkouturl;
                }else if(type ==4){
                    urldata = generator.extrainurl;
                }else if(type ==5){
                    urldata = generator.extraouturl;
                }
            }


            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            try{this.dialog.show();}
            catch (Exception e){
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            int data = 0;



            try {
                this.dialog.setMessage("Loading Data...");
                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("kode",kode)
                        .add("kode_kabag",kabag)
                        .build();

                Log.e(TAG, "doInBackground: "+kode+kabag );

                Request request=null;
                if(issecurity){
                    if(type ==1){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .post(body)
                                .url(urldata)
                                .build();
                    }
                    else if(type == 2){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .post(body)
                                .url(urldata)
                                .build();
                    }else if(type ==3){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }else if(type ==4){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }else if(type ==5){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }


                }
                else {
                    if(type == 0){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .post(body)
                                .url(urldata)
                                .build();
                    }else if(type ==1){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }else if(type ==2){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }else if(type ==3){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }else if(type ==4){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .post(body)
                                .url(urldata)
                                .build();
                    }else if(type ==5){
                        request = new Request.Builder()
                                .header("Authorization",getSharedPreferences("poipayroll",MODE_PRIVATE).getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();
                    }
                }

                Response responses = null;


                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                result = new JSONObject(responses.body().string());



            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }catch (IOException e) {
                this.dialog.setMessage("Loading Data... IOError Occured,retrying...");
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                e.printStackTrace();
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.setMessage("Loading Data... Internet Error Occured,retrying...");
                this.dialog.dismiss();
                e.printStackTrace();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.setMessage("Loading Data... Error Occured,retrying...");
                this.dialog.dismiss();
                e.printStackTrace();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
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
                Log.e(TAG, "onPostExecute: "+result);
                //restartbarcode();


                String title = "";
                if(result.getString("status").equals("true")){
                    title = "Berhasil";
                }
                else {
                    title = "Gagal";
                }

                final AlertDialog alert = new AlertDialog.Builder(ctx).setTitle(title).setMessage(result.getString("message")).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

                alert.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alert.dismiss();
                        try {
                            mCaptureActivityHandler.restartPreviewAndDecode();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }//Do something after 100ms
                    }
                }, 2000);


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
                    AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
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

                }
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    public void restartbarcode(){
        Intent a = new Intent(QrCodeActivity.this,QrCodeActivity.class);
        a.putExtra("absensi",getIntent().getIntExtra("absensi",0));
        a.putExtra("keepalive",1);
        startActivity(a);
        finish();
    }
}
