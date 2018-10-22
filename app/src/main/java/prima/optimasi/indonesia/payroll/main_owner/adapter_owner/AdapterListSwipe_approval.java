package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.objects.listjobextension;

public class AdapterListSwipe_approval extends SwipeAdapter {

    private List<listjobextension> items = new ArrayList<>();
    private List<listjobextension> items_swiped = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listjobextension obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSwipe_approval(Context context, List<listjobextension> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public ImageButton bt_move;
        public Button bt_undo;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            bt_move = (ImageButton) v.findViewById(R.id.bt_move);
            bt_undo = (Button) v.findViewById(R.id.bt_undo);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateSwipeViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_job_extension, null);
        OriginalViewHolder sampleViewHolder = new OriginalViewHolder(v);
        return sampleViewHolder;
    }

    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, int i) {
        return new SwipeConfiguration.Builder(context)
                .setBackgroundColorResource(R.color.red_500)
                .setDrawableResource(R.drawable.ic_delete_white_24dp)
                .build();
    }

    @Override
    public void onSwipe(int i, int i1) {

    }

}