package prima.optimasi.indonesia.payroll.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.SQLiteHelper;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.data.SharedPref;
import prima.optimasi.indonesia.payroll.objects.company;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterListCompany extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private AlertDialog dialogscompany;

    private List<company> items = new ArrayList<>();
    private Context ctx;

    public AdapterListCompany(Context context, List<company> items, AlertDialog companydialog) {
        this.items = items;
        ctx = context;
        dialogscompany = companydialog;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View lyt_parent;

        ImageButton redx;

        public OriginalViewHolder(View v) {
            super(v);

            redx =  v.findViewById(R.id.deleteitem);
            name = (TextView) v.findViewById(R.id.namapt);
            lyt_parent = v.findViewById(R.id.parent);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_company, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final company p = items.get(position);
            OriginalViewHolder view = (OriginalViewHolder) holder;

            view.name.setText(p.getCompanyname());

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = ctx.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("ip",p.getCompanyip());
                    edit.putString("port",String.valueOf(p.getCompanyport()));
                    edit.putString("companyname",p.getCompanyname());
                    edit.putString("codename",p.getCompanycodename());
                    edit.apply();

                    Log.e("data adapter comp",p.getCompanyip()+" "+p.getCompanyport() );

                    generator.Server = p.getCompanyip();
                    generator.port = String.valueOf(p.getCompanyport());

                    generator.textcompany.setText(p.getCompanyname());
                    generator.determiner = p.getCompanycodename();

                    generator.reloadurl();

                    if(dialogscompany!=null )
                    {
                        if(dialogscompany.isShowing()){
                            dialogscompany.dismiss();
                        }
                    }
                }
            });


            view.redx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle("Warning").setMessage("Hapus "+p.getCompanyname()+" dari daftar ?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SQLiteHelper dbase = new SQLiteHelper(ctx);
                            dbase.deleteaccount(p.getCompanyid());
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,items.size());
                            //pos 0 / itemsize

                            if(items.size()==0){

                                SharedPreferences prefs = ctx.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putString("ip","");
                                edit.putString("port","");
                                edit.putString("companyname","");
                                edit.apply();

                                RecyclerView recycler = generator.tempview.findViewById(R.id.companylist);

                                LinearLayout linearnodata =  generator.tempview.findViewById(R.id.nodatacompany);

                                recycler.setVisibility(View.GONE);
                                linearnodata.setVisibility(View.VISIBLE);
                            }
                            else {
                                RecyclerView recycler = generator.tempview.findViewById(R.id.companylist);

                                LinearLayout linearnodata =  generator.tempview.findViewById(R.id.nodatacompany);

                                recycler.setVisibility(View.VISIBLE);
                                linearnodata.setVisibility(View.GONE);
                            }

                            forceWrapContent(generator.tempview);


                        }
                    }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            generator.reloadurl();
                        }
                    }).create();

                    dialog.show();
                }
            });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void insertItem(int index, company kar){
        items.add(index, kar);
        notifyItemInserted(index);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

}