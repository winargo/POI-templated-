package prima.optimasi.indonesia.payroll.universal.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class ActivityPengajuan extends AppCompatActivity {

    Button send;
    private SimpleDateFormat dateFormatter;
    TextView tglmasuk,tglkeluar;
    Date tanggal_masuk, tanggal_keluar;
    String tanggal_masuk1, tanggal_keluar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);

        spinner.setItems("Pilih Pengajuan","Cuti", "Izin", "Dinas", "Sakit");
        //spinner.setText("Pengajuan");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
        //boolean klik=false;
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                /*
                if(!klik){
                    spinner.setItems("Cuti", "Izin", "Dinas", "Dirumahkan");
                }*/
            }

        });
        tglmasuk=findViewById(R.id.tglmasuk);
        tglkeluar=findViewById(R.id.tglkeluar);
        //tglmasuk.setText(getCurrentDate());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        tglmasuk.setText(formattedDate);
        tglkeluar.setText(formattedDate);
        tanggal_masuk=c.getTime();
        tanggal_keluar=c.getTime();
        tanggal_masuk1=formattedDate;
        tanggal_keluar1=formattedDate;
        //tglkeluar.setText(getNextDate());
        //c.add(Calendar.DATE, 1);

        //formattedDate = df.format(c.getTime());

        //Log.v("NEXT DATE : ", formattedDate);

        send=findViewById(R.id.send_pengajuan);
        initToolbar();
        initComponent();
    }

    private void initComponent() {
        tglmasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglmasuk,"masuk");
            }

        });
        tglkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglkeluar,"keluar");
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tanggalkeluar=tglkeluar.getText().toString();
                String tanggalmasuk=tglmasuk.getText().toString();
                Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                try{
                    Date tgl_masuk=df.parse(tanggal_masuk1);
                    Date tgl_keluar=df.parse(tanggal_keluar1);

                    if(tgl_masuk.compareTo(tgl_keluar)>0) {
                        final Dialog dialog = new Dialog(ActivityPengajuan.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                        dialog.setContentView(R.layout.dialog_warning);
                        dialog.setCancelable(true);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        ImageView icon = dialog.findViewById(R.id.icon);
                        TextView title = dialog.findViewById(R.id.title);
                        TextView content = dialog.findViewById(R.id.content);
                        icon.setVisibility(View.GONE);
                        title.setText("Kesalahan Data Tanggal");
                        content.setText("Tanggal keluar wajib lebih atau sama dari tanggal masuk yang diinput");
                        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    }else{
                        Log.e("NEXT DATE : ", "Berhasil");
                    }
                }catch(Exception e){
                    Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                    Log.e("NEXT DATE : ", ""+tanggal_masuk1+" "+tanggal_keluar1);
                }




            }
        });
    }
    private void tanggal_masuk(Date tanggal){
        tanggal_masuk=tanggal;

    }
    private void tanggal_keluar(Date tanggal){
        tanggal_keluar=tanggal;

    }
    private void tanggal_masuk1(String tanggal){
        tanggal_masuk1=tanggal;

    }
    private void tanggal_keluar1(String tanggal){
        tanggal_keluar1=tanggal;

    }
    private void dialogDatePickerDark(final TextView tv, String jenis) {
        Calendar cur_calender = Calendar.getInstance();
        /*
        String tanggal_pengajuan=tglmasuk.getText().toString();
        String tgl_pengajuan=tanggal_pengajuan.substring(0,2);
        String bln_pengajuan=tanggal_pengajuan.substring(3,5);
        String thn_pengajuan=tanggal_pengajuan.substring(6,10);

        cur_calender.set(Calendar.YEAR, Integer.parseInt(thn_pengajuan));
        cur_calender.set(Calendar.MONTH,Integer.parseInt(bln_pengajuan)-1);
        cur_calender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tgl_pengajuan));
        */
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();

                        //long date_ship_millis = calendar.getTimeInMillis();
                        //tv.setText(Tools.getFormattedDateSimple(date_ship_millis));
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        if(jenis.equals("masuk")){
                            tanggal_masuk(calendar.getTime());
                            tanggal_masuk1(df.format(calendar.getTime()));
                        }
                        else{
                            tanggal_keluar(calendar.getTime());
                            tanggal_keluar1(df.format(calendar.getTime()));
                        }
                        Log.e("NEXT DATE : ", calendar.getTime().toString());
                        tv.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);

                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark theme
        datePicker.setThemeDark(true);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_mainmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        else if (id == R.id.action_logout) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("karyawan");


            Intent logout = new Intent(this,activity_login.class);
            SharedPreferences prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

            if(prefs.getInt("statustoken",0)==0){

            }
            else {
                generator.unregistertokentoserver unregistertokentoserver = new generator.unregistertokentoserver(this,prefs.getString("tokennotif",""),prefs.getString("Authorization",""));
                unregistertokentoserver.execute();
            }

            SharedPreferences.Editor edit = prefs.edit();

            edit.putString("iduser","");
            edit.putString("username","");
            edit.putString("jabatan","");
            edit.putString("level","");
            edit.putString("kodekaryawan","");
            edit.putString("tempatlahir","");
            edit.putString("profileimage","");
            edit.putString("Authorization","");

            edit.commit();

            startActivity(logout);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
