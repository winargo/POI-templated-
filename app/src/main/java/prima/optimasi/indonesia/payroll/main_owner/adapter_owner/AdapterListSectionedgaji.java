package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.objects.datagajiperiode;
import prima.optimasi.indonesia.payroll.objects.datapinjaman;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterListSectionedgaji extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<datagajiperiode> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, datacuti obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSectionedgaji(Context context, List<datagajiperiode> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView image;
        public TextView name,jabatan,gajiawal, gajibersih, potongan, tanggalgajian;
        public CardView card;

        public OriginalViewHolder(View v) {
            super(v);
            gajiawal = v.findViewById(R.id.gajiawal);
            gajibersih = v.findViewById(R.id.gajibersih);
            potongan = v.findViewById(R.id.potongan);
            jabatan = v.findViewById(R.id.repjab);
            name = v.findViewById(R.id.repnama);
            tanggalgajian = v.findViewById(R.id.tanggalgajian);

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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_report_list_pengajian, parent, false);
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

            view.name.setText(p.getNama());
            view.jabatan.setText(p.getJabatan());

            DecimalFormat formatter = new DecimalFormat("###,###,###");

            view.gajiawal.setText("Rp"+formatter.format(Integer.parseInt(p.getTotalgaji())));
            view.gajibersih.setText("Rp"+formatter.format(Integer.parseInt(p.getGajibersih())));
            view.potongan.setText("Rp"+formatter.format(Integer.parseInt(p.getPotongan())));
            String gajian=p.getTanggalgajian();
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
            try{
                view.tanggalgajian.setText(format1.format((fomat.parse(gajian))));

            }catch(Exception e){
                Log.e("DATE : ", ""+gajian);

            }
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
            setAnimation(view.itemView, position);
        } else {
            SectionViewHolder view = (SectionViewHolder) holder;
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                Log.e("date data", "onBindViewHolder: "+p.getDatatgl() );
            try {
                view.title_section.setText(format1.format(formate.parse(p.getDatatgl())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}