package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentPengumuman;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listjadwal;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class Adapterjadwal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<listjadwal> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listjadwal obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapterjadwal(Context context, List<listjadwal> items,int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal, nama_shift, masuk, keluar;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            nama_shift = v.findViewById(R.id.nama_shift);
            masuk = v.findViewById(R.id.masuk);
            keluar = v.findViewById(R.id.keluar);


            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    /*
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }
    */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        //if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
            vh = new OriginalViewHolder(v);
            /*
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        */
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listjadwal p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.tanggal.setText(p.getTanggal());
            view.nama_shift.setText(p.getNama_shift());
            view.masuk.setText("In: "+p.getMasuk());
            view.keluar.setText("Out: "+p.getKeluar());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                    //Toast.makeText(ctx,"Halo",Toast.LENGTH_LONG);
                    /*
                    if (mOnItemClickListener != null) {

                        Intent viewkaryawans = new Intent(ctx,viewkaryawan.class);

                        viewkaryawans.putExtra("idkaryawan","");

                        ctx.startActivity(viewkaryawans);

                    }*/
                }
            });
            setAnimation(view.itemView, position);
        }
        /*
        else{
            SectionViewHolder view = (SectionViewHolder) holder;
            view.title_section.setText(p.getJadwal_section());
            p.setSection(false);
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
/*package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listjadwal;
import prima.optimasi.indonesia.payroll.objects.logabsensi_karyawan;
import prima.optimasi.indonesia.payroll.model.Social;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class Adapterjadwal extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<listjadwal> items = new ArrayList<>();
    //private logabsensi_karyawan items=new logabsensi_karyawan();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listjadwal obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapterjadwal(Context context, List<listjadwal> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal;
        public TextView nama_shift;

        public OriginalViewHolder(View v) {
            super(v);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            nama_shift = (TextView) v.findViewById(R.id.nama_shift);


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final listjadwal p = items.get(position);
            view.tanggal.setText(p.getTanggal());
            view.nama_shift.setText(p.getNama_shift());
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}*/