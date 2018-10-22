package prima.optimasi.indonesia.payroll.main_karyawan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;

public class Activity_Pengumuman extends AppCompatActivity {

    ProgressDialog loadingdata;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadingdata = new ProgressDialog(this);
        loadingdata.setTitle("Please Wait");
        loadingdata.setMessage("Loading Data...");
        loadingdata.show();

        prefs = getSharedPreferences("poipayroll",MODE_PRIVATE);

        if(prefs.getInt("statustoken",0)==0){
            generator.registertokentoserver register = new generator.registertokentoserver(this,prefs.getString("tokennotif",""));
            register.execute();

            FirebaseMessaging.getInstance().subscribeToTopic("kabag");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_floating);

        Log.e("sharence status", "onCreate: " + prefs.getInt("statustoken",0) );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(loadingdata.isShowing()){
            loadingdata.dismiss();
        }
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
        if (id == R.id.action_logout) {
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


