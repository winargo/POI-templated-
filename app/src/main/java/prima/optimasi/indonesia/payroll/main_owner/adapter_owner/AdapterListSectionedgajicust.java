package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.objects.datagajiperiode;

public class AdapterListSectionedgajicust extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    private List<datagajiperiode> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, datacuti obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSectionedgajicust(Context context, List<datagajiperiode> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal1,tanggal2,bpjs,potongantelat,tunjangan,nama,jabatan,reward,punishment,totalbersih,gajihari,tunjanganlain;
        public CardView card;

        ImageButton delete;

        public OriginalViewHolder(View v) {
            super(v);
            tanggal1 = v.findViewById(R.id.tanggal1);
            tanggal2 = v.findViewById(R.id.tanggal2);
            bpjs = v.findViewById(R.id.nilaibpjs);
            potongantelat = v.findViewById(R.id.nilaipotongan);
            punishment = v.findViewById(R.id.nilaipunishment);
            totalbersih = v.findViewById(R.id.nilaigajibersih);
            gajihari = v.findViewById(R.id.gajikerjahari);
            tunjangan = v.findViewById(R.id.nilaitunjangan);
            tunjanganlain = v.findViewById(R.id.nilaitunjanganlain);
            reward = v.findViewById(R.id.nilaireward);

            delete = v.findViewById(R.id.removeitem);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;

        public SectionViewHolder(View v) {
            super(v);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_report_list_pengajian_cust, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final datagajiperiode p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            DecimalFormat formatter = new DecimalFormat("###,###,###");

            view.totalbersih.setText("Rp "+formatter.format(p.getGajibersih()));

            view.gajihari.setText("Rp "+formatter.format(p.getTotalgaji()));
            view.gajihari.setTextColor(ctx.getResources().getColor(R.color.green_700));

            view.potongantelat.setText("Rp "+formatter.format(p.getPotongan()));
            view.potongantelat.setTextColor(Color.RED);

            String gajian=p.getTanggalgajian();
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
            view.bpjs.setText("RP "+formatter.format(p.getBpjs()));
            view.bpjs.setTextColor(Color.RED);
            view.tunjangan.setText("Rp "+formatter.format(p.getTunjangan()));
            view.tunjangan.setTextColor(ctx.getResources().getColor(R.color.green_700));
            view.tunjanganlain.setText("Rp "+formatter.format(p.getTunlain()));
            view.tunjanganlain.setTextColor(ctx.getResources().getColor(R.color.green_700));

            view.punishment.setText("Rp "+formatter.format(p.getPunishment()));
            view.punishment.setTextColor(Color.RED);

            view.reward.setText("Rp "+formatter.format(p.getReward()));
            view.reward.setTextColor(ctx.getResources().getColor(R.color.green_700));

            try{
                view.tanggal1.setText(p.getTanggal1());
                view.tanggal2.setText(p.getTanggal2());
            }catch(Exception e){
                Log.e("DATE : ", ""+gajian);

            }

            view.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,items.size());
                }
            });


            /*
            if(p.getImageurl().contains(".jpg")) {
                Picasso.get().load(p.getImageurl()).transform(new CircleTransform()).into(view.image);
            }
            else {
                Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQvx08eMc0PDklJb6ZMibGD-xZVa-Ghbs5mzmRrzNB3nsXoJ9B3zA").transform(new CircleTransform()).into(view.image);

            }*/
            /*view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {

                        Intent viewkaryawans = new Intent(ctx,viewkaryawan.class);

                        viewkaryawans.putExtra("idkaryawan",p.getIskar());

                        ctx.startActivity(viewkaryawans);

                    }
                }
            });*/
            //setAnimation(view.itemView, position);
        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
            view.title_section.setText(p.getKode());
            view.title_section.setTextColor(Color.BLACK);
            view.title_section.setTextSize(15f);
            view.title_section.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).getIssection() ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, datagajiperiode kar){
        items.add(index, kar);
        notifyItemInserted(index);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;


}