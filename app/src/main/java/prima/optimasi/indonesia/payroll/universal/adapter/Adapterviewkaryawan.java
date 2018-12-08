package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawan_izincutisakit;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class Adapterviewkaryawan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int animation_type = 0;

    private List<listkaryawan_izincutisakit> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawan_izincutisakit obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapterviewkaryawan(Context context, List<listkaryawan_izincutisakit> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal, izin, sakit, cuti, absen, telat, dinas;
        public View viewizin, viewsakit, viewcuti, viewabsen, viewtelat, viewdinas;

        public OriginalViewHolder(View v) {
            super(v);
            tanggal = (TextView) v.findViewById(R.id.karytanggal);
            izin = v.findViewById(R.id.karyizin);
            sakit = v.findViewById(R.id.karysakit);
            absen = v.findViewById(R.id.karyabsen);

            cuti = v.findViewById(R.id.karycuti);
            telat = v.findViewById(R.id.karytelat);
            dinas = v.findViewById(R.id.karydinas);

            viewizin = (View) v.findViewById(R.id.karviewizin);
            viewsakit = (View) v.findViewById(R.id.karviewsakit);
            viewabsen = (View) v.findViewById(R.id.karviewabsen);

            viewcuti = (View) v.findViewById(R.id.karviewcuti);
            viewtelat = (View) v.findViewById(R.id.karviewtelat);
            viewdinas = (View) v.findViewById(R.id.karviewdinas);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keterangan_karyawan, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawan_izincutisakit p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.tanggal.setText(p.getBulan());
            view.izin.setText(p.getIzin());
            view.sakit.setText(p.getSakit());
            view.cuti.setText(p.getCuti());
            view.dinas.setText(p.getDinas());
            view.telat.setText(p.getTelat());
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