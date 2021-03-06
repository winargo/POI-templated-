package prima.optimasi.indonesia.payroll.main_kabag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.utils.Tools;
import qrcodescanner.QrCodeActivity;

public class ActivityAbsensi extends AppCompatActivity {
    CoordinatorLayout parent_view;

    public static String jabatan="JABATAN";
    int checker=0;
    FloatingActionButton checkin, checkout, breakin, breakout, extrain, extraout, absensi, absensiwajah, scan;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);
        initToolbar();
        initComponent();
        initListener();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Absensi Anggota");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent() {
        parent_view= findViewById(R.id.parent_view);

        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        checkin=findViewById(R.id.lyt_parent);
        checkout=findViewById(R.id.lyt_parent_checkout);
        breakin=findViewById(R.id.lyt_parent_breakin);
        breakout=findViewById(R.id.lyt_parent_breakout);
        extrain=findViewById(R.id.lyt_parent_extrain);
        extraout=findViewById(R.id.lyt_parent_extraout);
        absensi=findViewById(R.id.lyt_parent_absensi);
        absensiwajah=findViewById(R.id.lyt_parent_absensiwajah);

        absensiwajah.setVisibility(View.GONE);

        TextView checkinteks=findViewById(R.id.name);
        TextView checkoutteks=findViewById(R.id.namecheckout);
        TextView breakinteks=findViewById(R.id.namebreakin);
        TextView breakoutteks=findViewById(R.id.namebreakout);
        TextView extrainteks=findViewById(R.id.nameextrain);
        TextView extraouteks=findViewById(R.id.nameextraout);
        TextView absensiteks=findViewById(R.id.nameabsensi);
        TextView absensiwajahteks=findViewById(R.id.nameabsensiwajah);
        absensiwajahteks.setVisibility(View.GONE);

        checkinteks.setText("Check IN");
        checkoutteks.setText("Check OUT");
        breakinteks.setText("Break IN");
        breakoutteks.setText("Break OUT");
        extrainteks.setText("Extra IN");
        extraouteks.setText("Extra OUT");
        absensiteks.setText("Absensi");
        absensiwajahteks.setText("Absensi Wajah");

        /*
        ImageView image = findViewById(R.id.image);
        ImageView image_checkout = findViewById(R.id.image_checkout);
        ImageView image_breakin = findViewById(R.id.image_breakin);
        ImageView image_breakout = findViewById(R.id.image_breakout);
        ImageView image_extrain = findViewById(R.id.image_extrain);
        ImageView image_extraout = findViewById(R.id.image_extraout);
        ImageView image_absensi = findViewById(R.id.image_absensi);
        ImageView image_absensiwajah = findViewById(R.id.image_absensiwajah);
        Picasso.get().load("http://www.icym.edu.my/v13/images/checklist.jpg").into(image);
        Picasso.get().load("http://sainte-catherine.fr/wordpress/wp-content/uploads/2018/05/sondage-padel-620x330.jpg").into(image_checkout);
        Picasso.get().load("http://www.metrochemgroup.com/wp-content/uploads/2018/04/meal-break.jpg").into(image_breakin);
        Picasso.get().load("https://www.kidcheck.com/wp-content/uploads/2016/06/time-limit-1-300x217.jpg").into(image_breakout);
        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlrePrH8HNpXnZDBTvLTGSDpTngRFS8kfJHrRM8HvOP94eYC3M").into(image_extrain);
        Picasso.get().load("http://www.officepower.net/wp-content/uploads/2015/11/OP_Blog_Q4_0411_Freedom.jpg").into(image_extraout);
        Picasso.get().load("https://d35kskn2b3gqvv.cloudfront.net/wp-content/uploads/2016/10/qrcodes.png").into(image_absensi);
        Picasso.get().load("https://wi-images.condecdn.net/image/wgyjRWNyLYJ/crop/810/f/istockfae.jpg").into(image_absensiwajah);*/

    }

    private void initListener(){
        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,0,checkin);
            }
        });

        breakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,1,checkout);
            }
        });

        breakin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,2,checkout);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,3,checkout);
            }
        });

        extrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,4,checkout);
            }
        });

        extraout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedtype(0,5,checkout);
            }
        });

        absensiwajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent face = new Intent(ActivityAbsensi.this, facedetection.class);
                //startActivity(face);
            }
        });

        absensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(ActivityAbsensi.this).create();
                LinearLayout l = (LinearLayout) LayoutInflater.from(ActivityAbsensi.this).inflate(R.layout.layout_barcode, null);

                dialog.setTitle("Absensi");

                ImageView barcode = l.findViewById(R.id.barcodekaryawan);

                SharedPreferences prefs = getSharedPreferences("poipayroll", MODE_PRIVATE);
                String text = prefs.getString("kodekaryawan", "");
                Log.e("data json", "onClick: " + prefs.getString("kodekaryawan", ""));
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    barcode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                dialog.setView(l);
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item=menu.findItem(R.id.action_settings);
        item.setTitle("About");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectedtype(int issecurity, int typeabsensi, FloatingActionButton passedbutton){

        ConnectivityManager connecManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        LocationManager gpsmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(gpsmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            NetworkInfo myNetworkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (myNetworkInfo.isConnected()) {
                invokegpsabsence(issecurity, typeabsensi,passedbutton);
            } else {
                NetworkInfo myNetworkInfowifi = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(myNetworkInfowifi.isConnected()){
                    invokegpsabsence(issecurity, typeabsensi,passedbutton);
                }
                else {
                    AlertDialog dialog = new AlertDialog.Builder(ActivityAbsensi.this).setTitle("Koneksi Mati").setMessage("Mohon Hidupkan Mobile Data atau Wi-Fi untuk Absensi..").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    dialog.show();
                }
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(ActivityAbsensi.this).setTitle("GPS Mati").setMessage("Mohon Hidupkan GPS untuk Absensi..").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            dialog.show();
        }
    }

    private  void  invokegpsabsence(int issecurity, int typeabsensi, FloatingActionButton passedbutton){

        checker=1;

        generator.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                getResources().getColor(R.color.colorPrimary),
                Color.RED,
                Color.GREEN,
                getResources().getColor(R.color.colorPrimary)
        };

        ColorStateList myList = new ColorStateList(states, colors);

        breakin.setEnabled(false);
        breakout.setEnabled(false);
        checkin.setEnabled(false);
        checkout.setEnabled(false);
        extrain.setEnabled(false);
        extraout.setEnabled(false);
        absensi.setEnabled(false);

        breakin.setBackgroundTintList(myList);
        breakout.setBackgroundTintList(myList);
        checkin.setBackgroundTintList(myList);
        checkout.setBackgroundTintList(myList);
        extrain.setBackgroundTintList(myList);
        extraout.setBackgroundTintList(myList);
        absensi.setBackgroundTintList(myList);

        final String[] data = {""};

        ProgressDialog dialog = new ProgressDialog(ActivityAbsensi.this);
        dialog.setTitle("Mohon Tunggu");
        dialog.setMessage("Mendapatkan Lokasi , scan segera dieksekusi...");
        dialog.show();

        dialog.setCancelable(false);

        final Intent[] intent = {null};

        generator.listernerlocation = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                generator.location=location;

                Log.e("onLocationChanged: ",location.getLatitude()+","+location.getLongitude() );

                if(dialog!=null){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }

                if(generator.tempactivity==null){
                    intent[0] = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                    intent[0].putExtra("absensi", typeabsensi);
                    intent[0].putExtra("security", issecurity);
                    intent[0].putExtra("keepalive", 1);
                    startActivityForResult(intent[0],200);
                }
                else {

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                if(dialog!=null){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    dialog.show();
                }
            }

            public void onProviderEnabled(String provider) {

                if(dialog!=null){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    dialog.show();
                }

                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(ActivityAbsensi.this).setTitle("Warning").setMessage("Mohon tunggu Proses scan akan dieksekusi").create();
                alert.show();
            }

            public void onProviderDisabled(String provider) {

                if(dialog!=null){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }

                Log.e("check ", "provider diasabled" );
                if(generator.tempactivity!=null){
                    ((Activity) generator.tempactivity).finish();
                    generator.tempactivity = null;
                }
                android.app.AlertDialog alert = new android.app.AlertDialog.Builder(ActivityAbsensi.this).setTitle("Warning").setMessage("Mohon hidupkan Settingan gps , scan akan dieksekusi..").create();
                alert.show();

            }
        };

        generator.lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,generator.listernerlocation);
    }

    @Override
    public void onBackPressed() {
        if(checker==0){
            super.onBackPressed();
        }
        else{
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors = new int[] {
                    getResources().getColor(R.color.colorPrimary),
                    Color.RED,
                    Color.GREEN,
                    getResources().getColor(R.color.colorPrimary)
            };

            ColorStateList myList = new ColorStateList(states, colors);

            breakin.setEnabled(true);
            breakout.setEnabled(true);
            checkin.setEnabled(true);
            checkout.setEnabled(true);
            extrain.setEnabled(true);
            extraout.setEnabled(true);
            absensi.setEnabled(true);

            breakin.setBackgroundTintList(myList);
            breakout.setBackgroundTintList(myList);
            checkin.setBackgroundTintList(myList);
            checkout.setBackgroundTintList(myList);
            extrain.setBackgroundTintList(myList);
            extrain.setBackgroundTintList(myList);
            checker=0;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==200){
            if(resultCode==RESULT_OK){

                int[][] states = new int[][] {
                        new int[] { android.R.attr.state_enabled}, // enabled
                        new int[] {-android.R.attr.state_enabled}, // disabled
                        new int[] {-android.R.attr.state_checked}, // unchecked
                        new int[] { android.R.attr.state_pressed}  // pressed
                };

                int[] colors = new int[] {
                        getResources().getColor(R.color.colorPrimary),
                        Color.RED,
                        Color.GREEN,
                        getResources().getColor(R.color.colorPrimary)
                };

                ColorStateList myList = new ColorStateList(states, colors);
                
                breakin.setEnabled(true);
                breakout.setEnabled(true);
                checkin.setEnabled(true);
                checkout.setEnabled(true);
                extrain.setEnabled(true);
                extraout.setEnabled(true);
                absensi.setEnabled(true);

                breakin.setBackgroundTintList(myList);
                breakout.setBackgroundTintList(myList);
                checkin.setBackgroundTintList(myList);
                checkout.setBackgroundTintList(myList);
                extrain.setBackgroundTintList(myList);
                extraout.setBackgroundTintList(myList);
                absensi.setBackgroundTintList(myList);
            }
        }
    }
}
