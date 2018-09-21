package prima.optimasi.indonesia.payroll.adapter;

import android.arch.lifecycle.ReportFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_cuti;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_izin;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pengajian;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pinjaman;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_sakit;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listreport;
import prima.optimasi.indonesia.payroll.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterListBasic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<listreport> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBasic(Context context, List<listreport> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView desc;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            desc = v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            listreport p = items.get(position);
            view.name.setText(p.getJudul());
            view.desc.setText(p.getDeskripsi());


            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(view.name.getText().toString().equals("Cuti")){
                        Intent cuti = new Intent(ctx,owner_cuti.class);
                        ctx.startActivity(cuti);
                    }
                    else if(view.name.getText().toString().equals("Izin")){
                        Intent izin = new Intent(ctx,owner_izin.class);
                        ctx.startActivity(izin);
                    }else if(view.name.getText().toString().equals("Sakit")){
                        Intent sakit = new Intent(ctx,owner_sakit.class);
                        ctx.startActivity(sakit);
                    }else if(view.name.getText().toString().equals("Pinjaman")){
                        Intent pinjaman = new Intent(ctx,owner_pinjaman.class);
                        ctx.startActivity(pinjaman);
                    }else if(view.name.getText().toString().equals("Pengajian")){
                        Intent pengajian = new Intent(ctx,owner_pengajian.class);
                        ctx.startActivity(pengajian);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}