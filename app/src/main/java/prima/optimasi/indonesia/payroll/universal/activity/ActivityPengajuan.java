package prima.optimasi.indonesia.payroll.universal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class ActivityPengajuan extends AppCompatActivity {

    private SimpleDateFormat dateFormatter;
    TextView tglmasuk,tglkeluar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);

        spinner.setItems("Pilih Pengajuan","Cuti", "Izin", "Dinas", "Dirumahkan");
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
        initToolbar();
        initComponent();
    }
    private void initComponent() {
        tglmasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglmasuk);
            }
        });
        tglkeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerDark(tglkeluar);
            }
        });
    }

    private void dialogDatePickerDark(final TextView tv) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH,monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        //long date_ship_millis = calendar.getTimeInMillis();
                        //tv.setText(Tools.getFormattedDateSimple(date_ship_millis));
                        tv.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        /*
                        calendar.set(Calendar.YEAR,Calendar.MONTH,Calendar.DATE);
                        tv.setText(calendar.getTimeInMillis());*/
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
