package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawanpengajuan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class Adapterhistorypengajuankabag extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

    public Adapterhistorypengajuankabag(Context context, List<listkaryawanpengajuan> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView nama, jenis, tglmasuk, tglkeluar, keterangan;
        public ImageView status;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            nama = (TextView) v.findViewById(R.id.nama);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_pengajuan_kabag, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawanpengajuan p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.nama.setText(p.getNama());
            view.jenis.setText(p.getJenis());
            if(p.getStatus().equals("Approved")){
                Picasso.get().load(R.drawable.approveds).into(view.status);
            }
            else if(p.getStatus().equals("Rejected")){
                Picasso.get().load(R.drawable.rejects).into(view.status);
            }
            else if(p.getStatus().equals("Pending")){
                Picasso.get().load(R.drawable.pendings).into(view.status);
            }
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
            try {
                view.tglmasuk.setText(format1.format(formate.parse(p.getTanggal_masuk())));
                view.tglkeluar.setText(format1.format(formate.parse(p.getTanggal_keluar())));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            view.keterangan.setText(p.getKeterangan());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
            setAnimation(view.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}
