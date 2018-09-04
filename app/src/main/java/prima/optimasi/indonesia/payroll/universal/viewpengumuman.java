package prima.optimasi.indonesia.payroll.universal;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.objects.pengumuman;

public class viewpengumuman extends AppCompatActivity {

    private FloatingActionButton fab;

    TextView judul , brief ,isi;

    ImageView image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_medium);
        iniComponent();
        initToolbar();
    }

    boolean hide = false;

    private void iniComponent() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ((NestedScrollView) findViewById(R.id.nested_scroll_view)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= oldScrollY) { // down
                    if (hide) return;
                    prima.optimasi.indonesia.payroll.utils.ViewAnimation.hideFab(fab);
                    hide = true;
                } else {
                    if (!hide) return;
                    prima.optimasi.indonesia.payroll.utils.ViewAnimation.showFab(fab);
                    hide = false;
                }
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        judul = findViewById(R.id.pengtitle);
        brief = findViewById(R.id.pengbrief);
        isi = findViewById(R.id.pengisi);
        image = findViewById(R.id.pengimage);

        pengumuman peng =  generator.temppengumumanpassdata.get(0);

        judul.setText(peng.getTitle());

        isi.setText(Html.fromHtml(peng.getContent()));

        Picasso.get().load(peng.getImagelink()).into(image);

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
