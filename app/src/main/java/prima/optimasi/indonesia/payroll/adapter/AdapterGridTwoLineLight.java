package prima.optimasi.indonesia.payroll.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.model.Image;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.universal.viewpengumuman;
import prima.optimasi.indonesia.payroll.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterGridTwoLineLight extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<pengumuman> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Image obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridTwoLineLight(Context context, List<pengumuman> items) {
        this.items = items;
        ctx = context;
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
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_grid_image_two_line_light, null);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final pengumuman obj = items.get(position);
            final AdapterGridTwoLineLight.OriginalViewHolder view = (AdapterGridTwoLineLight.OriginalViewHolder) holder;
            view.name.setText(obj.getTitle());
            final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            view.brief.setText(format.format(obj.getCreatedate())+" - Oleh "+obj.getCreated());

            Picasso.get().load(obj.getImagelink()).centerCrop()
                .resize(400,400).into(view.image);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    generator.temppengumumanpassdata = new ArrayList<>();

                    generator.temppengumumanpassdata.add(obj);

                    Intent a = new Intent(ctx,viewpengumuman.class);
                    ctx.startActivity(a);
                }
            });

            if(ctx.getSharedPreferences("poipayroll",Context.MODE_PRIVATE).getString("level","").equals("owner")){
                view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(ctx, view.lyt_parent);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menu_delete);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_delete:
                                        AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle("Pengumuman").setMessage("Apakah anda yakin ingin menghapus pengumuman dari "+obj.getCreated()+" pada tanggal "+format.format(obj.getCreatedate())+"").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();

                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                        return true;
                    }
                });
            }
            else
            {

            }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public pengumuman getItem(int position) {
        return items.get(position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}