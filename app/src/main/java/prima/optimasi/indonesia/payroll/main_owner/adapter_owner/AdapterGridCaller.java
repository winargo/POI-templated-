package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

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

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class AdapterGridCaller extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<listkaryawan> items = new ArrayList<>();
    private List<listkaryawan> itemsfilter = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;
    private int animation_type = 0;


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridCaller(Context context, List<listkaryawan> items, int animation) {
        this.items = items;
        ctx = context;
        this.animation_type = animation;
        this.itemsfilter = items;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView brief;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            brief = (TextView) v.findViewById(R.id.brief);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_caller, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawan obj = itemsfilter.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.name.setText(obj.getNama());
            view.brief.setText(obj.getDesc() + " - " + obj.getJabatan());
            if(obj.getImagelink().equals("")){
                Picasso.get().load("http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png").into(view.image);
                //Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ").into(view.image);
            }

            else{
                Tools.displayImageOriginal(ctx, view.image, obj.getImagelink());
            }
           /* try{
                if(obj.getImagelink().equals("")){

                }
            }
            catch (NullPointerException e){
                view.lyt_parent.setVisibility(View.GONE);
            }*/

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent viewkaryawans = new Intent(ctx,viewkaryawan.class);

                        viewkaryawans.putExtra("idkaryawan",obj.getIskar());

                        ctx.startActivity(viewkaryawans);
                }
            });
            setAnimation(view.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return itemsfilter.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
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