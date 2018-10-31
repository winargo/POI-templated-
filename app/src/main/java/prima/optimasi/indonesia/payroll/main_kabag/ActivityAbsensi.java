package prima.optimasi.indonesia.payroll.main_kabag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.universal.absence.facedetection;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;
import qrcodescanner.QrCodeActivity;

public class ActivityAbsensi extends AppCompatActivity {
    CoordinatorLayout parent_view;

    public static String jabatan="JABATAN";
    MaterialRippleLayout checkin, checkout, breakin, breakout, extrain, extraout, absensi, absensiwajah, scan;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);
        Log.e("Jabatan : ", getIntent().getStringExtra("jabatan"));

        if(getIntent().getStringExtra("jabatan").equals("kabag")) {
            setContentView(R.layout.activity_absensi);
            initComponent1();
        }
        else {
            setContentView(R.layout.activity_absensi_security);
            initComponent2();
        }
        //recyclerView = findViewById(R.id.recyclerView);
        parent_view= findViewById(R.id.parent_view);
        initToolbar();
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    private void initComponent1() {
        checkin=findViewById(R.id.lyt_parent);
        checkout=findViewById(R.id.lyt_parent_checkout);
        breakin=findViewById(R.id.lyt_parent_breakin);
        breakout=findViewById(R.id.lyt_parent_breakout);
        extrain=findViewById(R.id.lyt_parent_extrain);
        extraout=findViewById(R.id.lyt_parent_extraout);
        absensi=findViewById(R.id.lyt_parent_absensi);
        absensiwajah=findViewById(R.id.lyt_parent_absensiwajah);

        TextView checkinteks=findViewById(R.id.name);
        TextView checkoutteks=findViewById(R.id.namecheckout);
        TextView breakinteks=findViewById(R.id.namebreakin);
        TextView breakoutteks=findViewById(R.id.namebreakout);
        TextView extrainteks=findViewById(R.id.nameextrain);
        TextView extraouteks=findViewById(R.id.nameextraout);
        TextView absensiteks=findViewById(R.id.nameabsensi);
        TextView absensiwajahteks=findViewById(R.id.nameabsensiwajah);

        checkinteks.setText("Check IN");
        checkoutteks.setText("Check OUT");
        breakinteks.setText("Break IN");
        breakoutteks.setText("Break OUT");
        extrainteks.setText("Extra IN");
        extraouteks.setText("Extra OUT");
        absensiteks.setText("Absensi");
        absensiwajahteks.setText("Absensi Wajah");

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
        Picasso.get().load("https://wi-images.condecdn.net/image/wgyjRWNyLYJ/crop/810/f/istockfae.jpg").into(image_absensiwajah);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 0);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 3);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        breakin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 2);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        breakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 1);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        extrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 4);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        extraout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 5);
                a.putExtra("security", 0);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        absensiwajah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent face = new Intent(ActivityAbsensi.this, facedetection.class);
                startActivity(face);
            }
        });

        absensi();
    }

    private void initComponent2() {
        scan=findViewById(R.id.lyt_parent);
        checkin=findViewById(R.id.lyt_parent_checkin);
        checkout=findViewById(R.id.lyt_parent_checkout);
        breakin=findViewById(R.id.lyt_parent_breakin);
        breakout=findViewById(R.id.lyt_parent_breakout);
        absensi=findViewById(R.id.lyt_parent_absensi);

        TextView scanteks=findViewById(R.id.name);
        TextView checkinteks=findViewById(R.id.namecheckin);
        TextView checkoutteks=findViewById(R.id.namecheckout);
        TextView breakinteks=findViewById(R.id.namebreakin);
        TextView breakoutteks=findViewById(R.id.namebreakout);
        TextView absensiteks=findViewById(R.id.nameabsensi);

        scanteks.setText("Scan Karyawan");
        checkinteks.setText("Check IN");
        checkoutteks.setText("Check OUT");
        breakinteks.setText("Break IN");
        breakoutteks.setText("Break OUT");
        absensiteks.setText("Absensi");

        ImageView imagescan = findViewById(R.id.image);
        ImageView image_checkin = findViewById(R.id.image_checkin);
        ImageView image_checkout = findViewById(R.id.image_checkout);
        ImageView image_breakin = findViewById(R.id.image_breakin);
        ImageView image_breakout = findViewById(R.id.image_breakout);
        ImageView image_absensi = findViewById(R.id.image_absensi);

        Picasso.get().load("https://cmkt-image-prd.global.ssl.fastly.net/0.1.0/ps/1526884/910/607/m1/fpnw/wm0/calendar-icon-on-white-background-01-.jpg?1470399461&s=0993250c9c0f1dcc4aeaaf5caccb6222").into(imagescan);
        Picasso.get().load("http://www.icym.edu.my/v13/images/checklist.jpg").into(image_checkin);
        Picasso.get().load("http://sainte-catherine.fr/wordpress/wp-content/uploads/2018/05/sondage-padel-620x330.jpg").into(image_checkout);
        Picasso.get().load("http://www.metrochemgroup.com/wp-content/uploads/2018/04/meal-break.jpg").into(image_breakin);
        Picasso.get().load("https://www.kidcheck.com/wp-content/uploads/2016/06/time-limit-1-300x217.jpg").into(image_breakout);
        Picasso.get().load("https://d35kskn2b3gqvv.cloudfront.net/wp-content/uploads/2016/10/qrcodes.png").into(image_absensi);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 1);
                a.putExtra("security", 1);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 2);
                a.putExtra("security", 1);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 5);
                a.putExtra("security", 1);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        breakin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 4);
                a.putExtra("security", 1);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        breakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(ActivityAbsensi.this, QrCodeActivity.class);
                a.putExtra("absensi", 3);
                a.putExtra("security", 1);
                a.putExtra("keepalive", 1);
                startActivity(a);
            }
        });

        absensi();
    }

    public void absensi() {
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
}
