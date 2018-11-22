package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listjadwal;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class Adapterhistorypengajuan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<listkaryawanpengajuan> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawanpengajuan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapterhistorypengajuan(Context context, List<listkaryawanpengajuan> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView jenis, tglmasuk, tglkeluar, keterangan;
        public ImageView status;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            jenis = (TextView) v.findViewById(R.id.jenis);
            status = (ImageView) v.findViewById(R.id.status);
            tglmasuk = v.findViewById(R.id.tglmasuk);
            tglkeluar = v.findViewById(R.id.tglkeluar);
            keterangan= v.findViewById(R.id.keterangan);


            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        //if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_pengajuan, parent, false);
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
        final listkaryawanpengajuan p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.jenis.setText(p.getJenis());
            //view.status.setText(p.getStatus());
            if(p.getStatus().equals("Approved")){
                Picasso.get().load(R.drawable.ic_file_download).into(view.status);
            }
            else if(p.getStatus().equals("Rejected")){
                Picasso.get().load(R.drawable.ic_file_download).into(view.status);
            }
            else if(p.getStatus().equals("Pending")){
                Picasso.get().load(R.drawable.ic_file_download).into(view.status);
            }
            view.tglmasuk.setText(p.getTanggal_masuk());
            view.tglkeluar.setText(p.getTanggal_keluar());
            Log.e("KETERANGAN", p.getKeterangan());
            view.keterangan.setText(p.getKeterangan());
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
