package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_cuti;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_izin;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pengajian;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pinjaman;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_sakit;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listjobextension;

public class AdapterListBasicjob_extention extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<listjobextension> items = new ArrayList<>();

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBasicjob_extention(Context context, List<listjobextension> items) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_extension, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            listjobextension p = items.get(position);
            view.name.setText(p.getNamakaryawan()+" "+p.getJabatan());
            view.desc.setText(p.getKeterangan() +" dari "+p.getTanggal1()+" hingga "+p.getTanggal2());


            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    for (int i=0;i<options.length;i++){
                        if(p.getTipe()==options[i]){
                            Intent a = new Intent(ctx,approval.class);
                            a.putExtra("tipe",options[i]);
                            ctx.startActivity(a);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}