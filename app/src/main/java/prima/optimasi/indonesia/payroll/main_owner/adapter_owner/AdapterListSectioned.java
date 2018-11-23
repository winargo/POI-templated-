package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterListSectioned extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<datacuti> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, datacuti obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSectioned(Context context, List<datacuti> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView image;
        public TextView name,jabatan,tglawal,tglakhir,tglawaldata,tglakhirdata,keterangandata,keterangan,hari,datahari;
        public CardView card;
        public ImageView status;

        public OriginalViewHolder(View v) {
            super(v);
            tglawal = v.findViewById(R.id.repawal);
            tglakhir = v.findViewById(R.id.repakhir);
            tglawaldata = v.findViewById(R.id.repawaldata);
            tglakhirdata = v.findViewById(R.id.repakhirdata);
            keterangandata = v.findViewById(R.id.repketdata);
            keterangan = v.findViewById(R.id.repket);
            jabatan = v.findViewById(R.id.repjab);
            image =  v.findViewById(R.id.repfoto);
            name = v.findViewById(R.id.repnama);
            hari = v.findViewById(R.id.replama);
            datahari = v.findViewById(R.id.replamadata);
            card = v.findViewById(R.id.repcard);
            status = v.findViewById(R.id.status);

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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_report_list, parent, false);
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
        final datacuti p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.name.setText(p.getNama());
            view.jabatan.setText(p.getJabatan());
            view.tglawal.setText("Tanggal Mulai Cuti");
            view.tglakhir.setText("Tanggal Akhir Cuti");
            view.tglakhirdata.setText(p.getTglakhir());
            view.tglawaldata.setText(p.getTglmulai());
            view.keterangan.setText("Keterangan Cuti");
            view.keterangandata.setText(p.getKeterangan());
            view.hari.setText("Waktu Cuti");
            view.datahari.setText(p.getHari()+" Hari");

            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors = new int[] {
                    ctx.getResources().getColor(R.color.red_50),
                    ctx.getResources().getColor(R.color.red_50),
                    ctx.getResources().getColor(R.color.red_50),
                    ctx.getResources().getColor(R.color.red_50)
            };

            int[] colors1 = new int[] {
                    ctx.getResources().getColor(R.color.green_50),
                    ctx.getResources().getColor(R.color.green_50),
                    ctx.getResources().getColor(R.color.green_50),
                    ctx.getResources().getColor(R.color.green_50)
            };

            int[] colors2 = new int[] {
                    ctx.getResources().getColor(R.color.blue_50),
                    ctx.getResources().getColor(R.color.blue_50),
                    ctx.getResources().getColor(R.color.blue_50),
                    ctx.getResources().getColor(R.color.blue_50)
            };


            ColorStateList myList = new ColorStateList(states, colors1);

            if(p.getStatus().equals("Diterima")){
                Picasso.get().load(R.drawable.approveds).into(view.status);
                //view.card.setBackgroundTintList(myList);
            }
            else if(p.getStatus().equals("Ditolak")){
                Picasso.get().load(R.drawable.rejects).into(view.status);
                //myList = new ColorStateList(states, colors);
                //view.card.setBackgroundTintList(myList);
            }
            else{
                Picasso.get().load(R.drawable.pendings).into(view.status);
                //myList = new ColorStateList(states, colors2);
                //view.card.setBackgroundTintList(myList);
            }

            if(p.getImageurl().contains(".jpg")) {
                Picasso.get().load(p.getImageurl()).transform(new CircleTransform()).into(view.image);
            }
            else {
                Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQvx08eMc0PDklJb6ZMibGD-xZVa-Ghbs5mzmRrzNB3nsXoJ9B3zA").transform(new CircleTransform()).into(view.image);

            }

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

    public void insertItem(int index, datacuti kar){
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