package prima.optimasi.indonesia.payroll.universal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.utils.previewimage;

public class viewpengumuman extends AppCompatActivity {

    private FloatingActionButton fab;

    TextView judul , brief ,isi,creator,createdate;
    ImageView image ,imagecreator;
    boolean hide = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_medium);
        initToolbar();
        iniComponent();
    }

    private void iniComponent() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        ((NestedScrollView) findViewById(R.id.nested_scroll_view)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= oldScrollY) { // down
                    if (hide) return;
                    prima.optimasi.indonesia.payroll.utils.ViewAnimation.hideFab(fab);
                    hide = true;
                } else {
                    if (!hide) return;
                    prima.optimasi.indonesia.payroll.utils.ViewAnimation.hideFab(fab);
                    hide = false;
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        imagecreator = findViewById(R.id.image);
        imagecreator.setVisibility(View.GONE);

        createdate = findViewById(R.id.pengtextdatecreator);
        creator = findViewById(R.id.pengtextcreator);

        judul = findViewById(R.id.pengtitle);
        brief = findViewById(R.id.pengbrief);
        isi = findViewById(R.id.pengisi);
        image = findViewById(R.id.pengimage);

        pengumuman peng =  generator.temppengumumanpassdata.get(0);

        creator.setText(peng.getCreated());

        SimpleDateFormat formats = new SimpleDateFormat("dd MMMM yyyy");

        createdate.setText(formats.format(peng.getCreatedate()));

        judul.setText(peng.getTitle());

        isi.setText(Html.fromHtml(peng.getContent()));

        Picasso.get().load(peng.getImagelink()).into(image);

        final Bitmap[] bm = {null};

        Picasso.get().load(peng.getImagelink()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bm[0] = bitmap;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent a = new Intent(viewpengumuman.this,previewimage.class);
                        Bitmap bm1= null;
                        bm1 = bm[0];
                        generator.tempbitmap = bm1;
                        startActivity(a);

                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        brief.setText(date.format(peng.getCreatedate()) + " - Oleh " + peng.getCreated());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prima.optimasi.indonesia.payroll.utils.Tools.setSystemBarColor(this, R.color.colorPrimary);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_share_save, menu);
        prima.optimasi.indonesia.payroll.utils.Tools.changeMenuIconColor(menu, getResources().getColor(R.color.white_transparency));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
