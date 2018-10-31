package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkButtonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class AdapterListSwipe_approval extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CoordinatorLayout snake;

    private List<listjobextension> items = new ArrayList<>();

    String[] real = new String[]{"id","kode_karyawan","idfp","nama","alamat","tempat_lahir","tgl_lahir","telepon","no_wali","email","tgl_masuk","kelamin",
            "status_nikah","pendidikan","wn","agama","shift", "status_kerja","ibu_kandung","suami_istri","tanggungan","npwp", "gaji","rekening","id_bank",
            "id_departemen","id_jabatan","id_grup", "id_golongan","atas_nama","foto","id_cabang",
            "start_date","expired_date","jab_index","kontrak","file_kontrak", "otoritas","periode_gaji",
            "qrcode_file"};

    //0,1,2,39,37,36

    String[] karyawan = new String[]{"id","kode_karyawan","idfp","nama", "alamat", "tempat_lahir", "tgl_lahir", "telepon", "no_wali", "email",
            "tgl_masuk", "kelamin", "status_nikah", "pendidikan", "wn", "agama", "shift", "status_kerja", "ibu_kandung",
            "suami_istri", "tanggungan", "npwp", "gaji", "rekening", "id_bank", "id_departemen", "id_jabatan", "id_grup",
            "id_golongan", "atas_nama", "foto", "id_cabang", "start_date", "expired_date", "jab_index","kontrak","file_kontrak",
            "otoritas", "periode_gaji"};

    String[] karyawandetail = new String[]{"id","kode_karyawan","idfp","Nama", "Alamat", "Tempat Lahir", "Tanggal Lahir", "Telepon", "No Wali", "Email",
            "Tanggal Masuk", "Kelamin", "Status Nikah", "Pendidikan", "Warga Negara", "Agama", "Shift", "Status Kerja", "Ibu Kandung",
            "Suami Istri", "Tanggungan", "NPWP", "Gaji", "Rekening", "ID Bank", "ID Departemen", "ID Jabatan", "ID Grup",
            "ID Golongan", "Atas Nama", "Foto", "ID Cabang", "Tanggal Mulai Kontrak", "Tanggal Akhir Kontrak","jab_index", "Kontrak","file_kontrak",
            "Otoritas", "Periode Gajian"};

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};
    String[] urloptions = new String[]{generator.getapprovalcutiyurl,generator.getapprovaldinasyurl,generator.getapprovaldirumahkanyurl,generator.getapprovalizinyurl,generator.getapprovalgolonganyurl,generator.getapprovalkaryawanyurl,generator.getapprovalpinjamanyurl,generator.getapprovalpdlyurl,generator.getapprovalpunihsmentyurl,generator.getapprovalrewardyurl};



    private Context ctx;
    private AdapterListBasicjob_extention.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final AdapterListBasicjob_extention.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSwipe_approval(Context context, List<listjobextension> items,CoordinatorLayout snakebar) {
        snake = snakebar;
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView desc;
        public RelativeLayout main;
        public SparkButton spark;
        public SparkButton sparktrue;

        public Boolean spark1=false;
        public Boolean spark2=true;

        public FrameLayout view_parent;

        public OriginalViewHolder(View v) {
            super(v);

            view_parent = v.findViewById(R.id.view_parent);
            spark = v.findViewById(R.id.spark_button_false);
            sparktrue = v.findViewById(R.id.spark_button_true);
            main = v.findViewById(R.id.mainlayout);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            desc = v.findViewById(R.id.description);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_extension_recycler, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {




            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

            final OriginalViewHolder view = (OriginalViewHolder) holder;



            view.spark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.spark.playAnimation();
                    if(view.spark.isChecked()){
                       //execute data
                        removeItem(position);
                    }
                    else {
                        Snackbar.make(snake,"Press again to Confirm",Snackbar.LENGTH_SHORT).show();
                        view.spark.setChecked(true);
                        view.sparktrue.setChecked(false);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.spark.setChecked(false);
                                //Do something after 100ms
                            }
                        }, 3000);
                    }
                }
            });

            view.sparktrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.sparktrue.playAnimation();
                    if(view.sparktrue.isChecked()){
                        removeItem(position);
                    }
                    else {
                        Snackbar.make(snake,"Press again to Confirm",Snackbar.LENGTH_SHORT).show();
                        view.sparktrue.setChecked(true);
                        view.spark.setChecked(false);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.sparktrue.setChecked(false);
                                //Do something after 100ms
                            }
                        }, 3000);
                    }
                }
            });


            listjobextension p = items.get(position);

            if (p.getIsselected()){
                //if item is selected then,set foreground color of FrameLayout.
                view.view_parent.setForeground(new ColorDrawable(ContextCompat.getColor(ctx,R.color.colorPrimary)));
            }
            else {
                //else remove selected item color.
                view.view_parent.setForeground(new ColorDrawable(ContextCompat.getColor(ctx,android.R.color.transparent)));
            }


            if(p.getTipe().equals(options[4])){
                view.image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.g));
                view.name.setText(p.getNamakaryawan());
                //try {
                view.desc.setText(/*"Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+*/p.getKeterangan());
                /*} catch (ParseException e) {
                    e.printStackTrace();
                }*/


            }
            else if(p.getTipe().equals(options[5])){

                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);

                view.name.setText(p.getNamakaryawan());
                //try {
                view.desc.setText(/*"Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+*/p.getKeterangan());
                /*} catch (ParseException e) {
                    e.printStackTrace();
                }*/


            }
            else if(p.getTipe().equals(options[7])){

                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);

                view.name.setText(p.getNamakaryawan());
                //try {
                view.desc.setText(/*"Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+*/p.getKeterangan());
                /*} catch (ParseException e) {
                    e.printStackTrace();
                }*/


            }else if(p.getTipe().equals(options[6])){
                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);

                view.name.setText(p.getNamakaryawan());
                try {
                    view.desc.setText("Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+p.getKeterangan());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }else if(p.getTipe().equals(options[8])){
                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);

                view.name.setText(p.getNamakaryawan());
                //try {
                try {
                    view.desc.setText("Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+p.getKeterangan());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*} catch (ParseException e) {
                    e.printStackTrace();
                }*/


            }else if(p.getTipe().equals(options[9])){
                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);

                view.name.setText(p.getNamakaryawan());
                //try {
                try {
                    view.desc.setText("Tanggal Pengajuan : "+format1.format(format.parse(p.getTgldiajukan()))+"\n"+p.getKeterangan());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /*} catch (ParseException e) {
                    e.printStackTrace();
                }*/


            }
            else {
                for(int i = 0 ; i < options.length;i++){
                    if(p.getTipe().equals(options[i])){

                    }

                }

                view.name.setText(p.getNamakaryawan()+" ("+p.getJabatan()+")");
                try {
                    view.desc.setText("Tanggal Pengajuan :" + format1.format(format.parse(p.getTgldiajukan())) +"\nKeterangan : "+p.getKeterangan() +"\nWaktu :"+format1.format(format.parse(p.getTanggal1()))+" - "+format1.format(format.parse(p.getTanggal2())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.e("picasso",p.getProfilepicture() );
                Picasso.get().load(p.getProfilepicture()).transform(new CircleTransform()).into(view.image);
            }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());

    }

    public void restoreItem(listjobextension item, int position) {
        items.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
        notifyDataSetChanged();
    }


    private class acceptordeny extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = "";
        String passeddata = "" ;
        String tipe = "";
        View nothing ;
        TextView txt_nothing;
        int accordeny;
        //listjobextension =

        public acceptordeny(Context context,String choice,int acceptordenydata)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

            accordeny = acceptordenydata;

            this.txt_nothing = txt_nothing;
            this.nothing = nothing;

            for(int i=0;i<options.length;i++){
                if(options[i]==choice){
                    urldata = urloptions[i];
                    tipe = choice;
                    //this.txt_nothing.setText("Tidak Ada data "+options[i]);
                }
            }

        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();

                    /*RequestBody body = new FormBody.Builder()
                            .add("","")
                            .build();*/
                    Request request=null;
                    /*String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            "Approval Reward"};*/

                    if(tipe.equals("Approval Cuti")){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Dinas") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Dirumahkan") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Izin") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Golongan") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Karyawan") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Pinjaman") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Pdm") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Punishment") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Reward") ){
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .build();

                    }

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
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                e.printStackTrace();
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
                List<listjobextension> listdata = new ArrayList<>();

                if (result != null) {

                    if(tipe.equals("Approval Cuti")){

                    }
                    else if (tipe.equals("Approval Dinas") ){

                    }
                    else if (tipe.equals("Approval Dirumahkan") ){

                    }
                    else if (tipe.equals("Approval Izin") ){

                    }
                    else if (tipe.equals("Approval Golongan") ){

                    }
                    else if (tipe.equals("Approval Karyawan") ){

                    }
                    else if (tipe.equals("Approval Pinjaman") ){

                    }
                    else if (tipe.equals("Approval Pdm") ){

                    }
                    else if (tipe.equals("Approval Punishment") ){

                    }
                    else if (tipe.equals("Approval Reward") ){

                    }



                }
                else {
                    //Snackbar.make(parent_view,"Gagal" + result.getString("message"),Toast.LENGTH_LONG).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                //Snackbar.make(parent_view,"Gagal" + E.getMessage(),Toast.LENGTH_LONG).show();
            }



            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}