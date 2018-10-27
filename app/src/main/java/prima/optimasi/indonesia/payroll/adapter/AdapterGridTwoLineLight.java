package prima.optimasi.indonesia.payroll.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.manage.pengumuman.addpengumuman;
import prima.optimasi.indonesia.payroll.model.Image;
import prima.optimasi.indonesia.payroll.objects.pengumuman;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.universal.viewpengumuman;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.previewimage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.media.MediaCodec.MetricsConstants.MODE;
import static android.widget.Toast.LENGTH_SHORT;

public class AdapterGridTwoLineLight extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<pengumuman> items = new ArrayList<>();

    private View ssnake;

    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Image obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridTwoLineLight(Context context, List<pengumuman> items , View snake) {
        this.items = items;
        ctx = context;
        ssnake = snake;
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
                                                deletepengumuman peng = new deletepengumuman(ctx,ssnake,view.lyt_parent,obj.getIdpengumuman(),position);
                                                peng.execute();
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

    private class deletepengumuman extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        int index = 0;
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.deletepengumumanurl;
        String id = "";
        View snake = null;
        View parent = null;

        public deletepengumuman(Context context, View snake, View parent, String idpeng,int index)
        {
            this.snake = snake;
            this.parent = parent;
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            this.index = index;
            this.id = idpeng ;
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

                    /*File f = new File(addpengumuman.this.getCacheDir(), textdata.getText().toString());
                    f.createNewFile();

                    //Convert bitmap to byte array
                    Bitmap bitmap1 = bmp ;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();*/

                    /*RequestBody bodydata=  new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", textdata.getText().toString(),
                                    RequestBody.create(MediaType.get("image/jpeg"),f))
                            .addFormDataPart("tanggal",formatter.format(new Date()))
                            .addFormDataPart("tipe",tempspinerdata)
                            .addFormDataPart("judul",title.getText().toString())
                            .addFormDataPart("foto",textdata.getText().toString())
                            .addFormDataPart("isi",mPreview.getText().toString())
                            .addFormDataPart("penulis",prefs.getString("username",""))
                            .build();*/

                    /*RequestBody bodydata=  new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", textdata.getText().toString(),
                                    RequestBody.create(MediaType.get("image/jpeg"),f))
                            .build();*/

                    RequestBody postdata= new FormBody.Builder()
                            .add("id",id)
                            .build();


                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .delete()
                            .url(urldata+id)
                            .build();
                    Response responses = null;

                    try {
                        responses = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsonObject =  null;
                    }catch (Exception e){
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
                            Snackbar.make(snake,"Berhasil Menghapus pengumuman",Snackbar.LENGTH_SHORT).show();
                            items.remove(index);
                            notift();
                        }
                        else {
                            Snackbar.make(snake,"Gagal Menghapus Pengumuman, cek koneksi",Snackbar.LENGTH_SHORT).show();
                        }



                        //Picasso.get().load(generator.profileurl+"/"+obj.getString("foto")).transform(new CircleTransform()).into(image);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(snake, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(snake,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }


    }

    public void notift() {
        this.notifyDataSetChanged();
    }
}