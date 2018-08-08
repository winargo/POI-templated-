package prima.optimasi.indonesia.payroll.activity.verification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class VerificationPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_phone);
        Tools.setSystemBarColor(this, R.color.grey_20);
    }
}
