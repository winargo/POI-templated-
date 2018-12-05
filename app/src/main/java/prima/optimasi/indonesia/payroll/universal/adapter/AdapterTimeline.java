package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.model.Image;
import prima.optimasi.indonesia.payroll.objects.listkaryawan_izincutisakit;
import prima.optimasi.indonesia.payroll.objects.listtimeline;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterTimeline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<listtimeline> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listtimeline obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterTimeline(Context context, List<listtimeline> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal, nama, aktivitas, keterangan;
        //public View viewizin, viewsakit, viewcuti, viewabsen, viewtelat, viewdinas;
        public ImageView dots;
        public OriginalViewHolder(View v) {
            super(v);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            nama = (TextView) v.findViewById(R.id.nama);
            aktivitas = (TextView) v.findViewById(R.id.aktivitas);
            keterangan = (TextView) v.findViewById(R.id.keterangan);
            dots = (ImageView) v.findViewById(R.id.dots);

            /*
            viewizin = (View) v.findViewById(R.id.karviewizin);
            viewsakit = (View) v.findViewById(R.id.karviewsakit);
            viewabsen = (View) v.findViewById(R.id.karviewabsen);
            viewcuti = (View) v.findViewById(R.id.karviewcuti);
            viewtelat = (View) v.findViewById(R.id.karviewtelat);
            viewdinas = (View) v.findViewById(R.id.karviewdinas);*/

        }
    }

    /*
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        //if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
            vh = new OriginalViewHolder(v);
        /*
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }*/

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listtimeline p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat parsed1 = new SimpleDateFormat("dd MMMM yyyy");
            String bulan="";
            try {
                bulan= parsed1.format(parsed.parse(p.getTanggal()));
                Log.e("Bulan",bulan);
                Calendar cal=Calendar.getInstance();
                String tglskrg=parsed1.format(cal.getTime());
                if(tglskrg.equals(bulan)){
                    view.dots.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.dot));
                }
            }
            catch (ParseException e){
                e.printStackTrace();
            }

            view.tanggal.setText(p.getTanggal());
            view.nama.setText(p.getNama());
            view.keterangan.setText(p.getKeterangan());
            view.aktivitas.setText(p.getAktivitas());
            //view.absen.setText(p.getAbsen());


            setAnimation(view.itemView, position);
        }
        /*
        else{
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(p.getBulan());
            //p.setSection(false);
        }*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /*
    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).getBoolean() ? VIEW_SECTION : VIEW_ITEM;
    }
    */

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}