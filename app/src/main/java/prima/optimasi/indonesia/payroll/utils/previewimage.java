package prima.optimasi.indonesia.payroll.utils;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;


public class previewimage extends AppCompatActivity {

    ImageView image ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image);

        View view = LayoutInflater.from(this).inflate(R.layout.preview_image,null);

        image = findViewById(R.id.imagepreview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Preview");

        Tools.setSystemBarColor(this, R.color.colorPrimary);

        if(getIntent().getStringExtra("bitmap").equals("")){
            finish();
        }
        else {
            Bitmap bit = generator.decodeBase64(getIntent().getStringExtra("bitmap"));



            image.setImageBitmap(bit);

            image.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return true;
    }


}