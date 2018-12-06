package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.fragment_owner.FragmentEmployee;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan_nonaktif;
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
            if(ctx.getSharedPreferences("poipayroll",Context.MODE_PRIVATE).getString("level","").equals("owner")){
                view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(ctx, view.lyt_parent);
                        //inflating menu from xml resource
                        if (obj.getStatus().equals("aktif")) {
                            popup.inflate(R.menu.menu_freeze);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_freeze:
                                            AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle("Freeze Karyawan").setMessage("Apakah anda yakin ingin membekukan karyawan, karyawan tidak dapat melakukan absensi").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //dialog.dismiss();
                                                    retrivefreeze freeze = new retrivefreeze(ctx, obj.getIskar(), obj.getStatus(),obj.getNama(), obj.getJabatan());
                                                    freeze.execute();
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
                        else {
                            popup.inflate(R.menu.menu_unfreeze);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_unfreeze:
                                            AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle("Unfreeze Karyawan").setMessage("Apakah anda yakin ingin mengaktifkan karyawan kembali").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //dialog.dismiss();
                                                    retrivefreeze freeze = new retrivefreeze(ctx, obj.getIskar(), obj.getStatus(), obj.getNama(), obj.getJabatan());
                                                    freeze.execute();
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
                    }

                });
            }
            setAnimation(view.itemView, position);
        }
    }

    private class retrivefreeze extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata;
        String id = "";
        String status="";
        String nama="";
        String jabatan="";

        public retrivefreeze(Context context, String id, String status, String nama, String jabatan)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.id = id ;
            this.status=status;
            this.nama=nama;
            this.jabatan=jabatan;
            if(status.equals("aktif")){
                urldata=generator.freezeemployeeurl;
            }
            else {
                urldata=generator.unfreezeemployeeurl;
            }
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            this.dialog.show();
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                this.dialog.setMessage("Posting Pengumuman...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    RequestBody body= new FormBody.Builder()
                            .add("id",id)
                            .build();

                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .put(body)
                            .url(urldata)
                            .build();
                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        jsonObject =  null;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        jsonObject = null;
                    }

                    if (responses==null){
                        jsonObject = null;
                        Log.e(TAG, "NULL");
                    }
                    else {

                        result = new JSONObject(responses.body().string());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.dismiss();
                Log.e("doInBackground: ", e.getMessage());
                generator.jsondatalogin = null;
                response = "Error Occured, PLease Contact Administrator/Support";
            }


            return response;
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result1) {

            try {
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        if(result.getString("status").equals("true")){
                            if(status.equals("aktif")) {
                                generator.employee.refkaryawan(jabatan);
                                Toast.makeText(ctx, "Berhasil membekukan karyawan", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                //HOLD FILTERS WE FIND
                                List<listkaryawan> foundFilters = new ArrayList<>();

                                String galaxy;

                                //ITERATE CURRENT LIST
                                for (int i = 0; i < items.size(); i++) {
                                    galaxy = items.get(i).getNama();

                                    //SEARCH
                                    if (!galaxy.toUpperCase().contains(nama.toUpperCase())) {
                                        //ADD IF FOUND
                                        foundFilters.add(items.get(i));
                                    }
                                }
                                setItems((List<listkaryawan>) foundFilters);
                                notifyDataSetChanged();
                                generator.employee.refkaryawan(jabatan);
                                Toast.makeText(ctx, "Berhasil mengaktifkan karyawan", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else {
                            if(status.equals("aktif")) {
                                Toast.makeText(ctx, "Gagal bekukan, cek koneksi", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(ctx, "Gagal aktifkan, cek koneksi", Toast.LENGTH_SHORT).show();

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Toast.makeText(ctx, "Terjadi Kesalahan Koneksi", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                //Snackbar.make(snake,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
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