package prima.optimasi.indonesia.payroll.main_owner.manage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import prima.optimasi.indonesia.payroll.R;

public class approval extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    String tipe="";
    String[] tipelist = new String[]{"cuti","sakit","izin","pdm","dirumahkan","dinas","","","","",""};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_approval);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
