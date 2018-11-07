package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawankontrakkerja;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class AdapterListKaryawan extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private List<listkaryawan> items = new ArrayList<>();
    private List<listkaryawan> itemsfilter = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListKaryawan(Context context, List<listkaryawan> items, int animation_type) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
        this.itemsfilter = items;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView jabatan, nama;
        public View lyt_parent, bt_expand, lyt_expand;

        public OriginalViewHolder(View v) {
            super(v);
            jabatan = v.findViewById(R.id.jabatan);
            image =  v.findViewById(R.id.image);
            nama = (TextView) v.findViewById(R.id.nama);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            //bt_expand = (View) v.findViewById(R.id.bt_expand);
            //lyt_expand = (View) v.findViewById(R.id.lyt_expand);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_karyawan, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawan p = itemsfilter.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.nama.setText(p.getNama());
            view.jabatan.setText(p.getJabatan());
            //view.image.setVisibility(View.VISIBLE);
            //view.image.setImageResource(R.drawable.baseline_account_circle_black_24dp);

            if(p.getImagelink().equals("")){
                Picasso.get().load("http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png").transform(new CircleTransform()).into(view.image);
                //Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ").into(view.image);
            }

            else{
                Tools.displayImageOriginal(ctx, view.image, p.getImagelink());
            }

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
        return itemsfilter.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, listkaryawan kar){
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

    public void setItems(List<listkaryawan> filteredGalaxies)
    {
        this.itemsfilter=filteredGalaxies;
    }

    @Override
    public Filter getFilter() {
        //this.itemsfilter=items;
        return new Filter() {


            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults filterResults = new Filter.FilterResults();

                if (constraint.length() > 0) {
                    //CHANGE TO UPPER
                    constraint = constraint.toString().toUpperCase();

                    //HOLD FILTERS WE FIND
                    List<listkaryawan> foundFilters = new ArrayList<>();

                    String galaxy;

                    //ITERATE CURRENT LIST
                    for (int i = 0; i < items.size(); i++) {
                        galaxy = items.get(i).getNama();

                        //SEARCH
                        if (galaxy.toUpperCase().contains(constraint)) {
                            //ADD IF FOUND
                            foundFilters.add(items.get(i));
                        }
                    }

                    //SET RESULTS TO FILTER LIST
                    filterResults.count = foundFilters.size();
                    filterResults.values = foundFilters;
                } else {
                    //NO ITEM FOUND.LIST REMAINS INTACT
                    filterResults.count = items.size();
                    filterResults.values = items;
                }

                //RETURN RESULTS
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                setItems((List<listkaryawan>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

}