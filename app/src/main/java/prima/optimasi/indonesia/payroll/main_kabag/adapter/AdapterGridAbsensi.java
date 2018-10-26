package prima.optimasi.indonesia.payroll.main_kabag.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.logabsensi_karyawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class AdapterGridAbsensi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private List<logabsensi_karyawan> items = new ArrayList<>();
    //private logabsensi_karyawan items=new logabsensi_karyawan();
    private List<String> items = new ArrayList<>();
    //private String[] items;

    private int animation_type = 0;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, logabsensi_karyawan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridAbsensi(Context context, List<String> items, int animation) {
        this.items = items;
        ctx = context;
        this.animation_type = animation;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name, content;

        public ImageView image;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            content = (TextView) v.findViewById(R.id.brief);
            image = (ImageView) v.findViewById(R.id.image);
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
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            //final String p = items.get(position);
            view.name.setText(items.get(position));
            view.content.setText("");

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

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}