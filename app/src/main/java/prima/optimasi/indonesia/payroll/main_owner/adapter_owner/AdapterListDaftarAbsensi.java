package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawandaftarabsensi;
import prima.optimasi.indonesia.payroll.objects.listkaryawankontrakkerja;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class AdapterListDaftarAbsensi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<listkaryawandaftarabsensi> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawandaftarabsensi obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListDaftarAbsensi(Context context, List<listkaryawandaftarabsensi> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView jabatan, banyak, nama, no;
        public View lyt_parent;
        //public ImageButton bt_expand;

        public OriginalViewHolder(View v) {
            super(v);
            image =  v.findViewById(R.id.image);
            //nama = (TextView) v.findViewById(R.id.nama);
            no = (TextView) v.findViewById(R.id.no);

            jabatan = (TextView) v.findViewById(R.id.jabatan);
            banyak = (TextView) v.findViewById(R.id.banyak);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            //bt_expand = (ImageButton) v.findViewById(R.id.bt_expand);
            //lyt_expand = (View) v.findViewById(R.id.lyt_expand);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftar_absensi, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawandaftarabsensi p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            int banyak=position+1;
            //view.nama.setText(p.getNama());
            view.jabatan.setText(p.getJabatan());
            view.banyak.setText("("+p.getBanyak()+"/"+p.getTotal()+")");
            //view.image.setVisibility(View.VISIBLE);
            //view.image.setImageResource(R.drawable.baseline_account_circle_black_24dp);
            //Picasso.get().load(p.getImagelink()).transform(new CircleTransform()).into(view.image);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {

                        Intent viewkaryawans = new Intent(ctx,viewkaryawan.class);

                        viewkaryawans.putExtra("idkaryawan",p.getIskar());

                        ctx.startActivity(viewkaryawans);

                    }
                }
            });
            if(banyak==items.size()){
                //view.bt_expand.setVisibility(View.INVISIBLE);
                view.no.setVisibility(View.INVISIBLE);
            }
            else{
                view.no.setText(""+banyak);
            }
            /*
            view.bt_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean show = toggleLayoutExpand(!p.expanded, v, view.lyt_expand);
                    items.get(position).expanded = show;
                }
            });


            // void recycling view
            if(p.expanded){
                view.lyt_expand.setVisibility(View.VISIBLE);
            } else {
                view.lyt_expand.setVisibility(View.GONE);
            }
            Tools.toggleArrow(p.expanded, view.bt_expand, false);
            */
            setAnimation(view.itemView, position);
        }
    }
    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void insertItem(int index, listkaryawandaftarabsensi kar){
        items.add(index, kar);
        notifyItemInserted(index);
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