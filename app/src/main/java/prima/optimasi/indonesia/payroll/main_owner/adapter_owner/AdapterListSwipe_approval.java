package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.app.AlertDialog;
import android.app.Dialog;
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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    String[] urloptions = new String[]{generator.approvalcutiyurl,generator.approvaldinasyurl,generator.approvaldirumahkanyurl,generator.approvalizinyurl,generator.approvalgolonganyurl,generator.approvalkaryawanyurl,generator.approvalpinjamanyurl,generator.approvalpdlyurl,generator.approvalpunihsmentyurl,generator.approvalrewardyurl};

    List<String> passed;

    private Context ctx;
    private AdapterListBasicjob_extention.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final AdapterListBasicjob_extention.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSwipe_approval(Context context, List<listjobextension> items,CoordinatorLayout snakebar,List<String> pass) {
        snake = snakebar;
        passed =pass;
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

        public List<String> passed1;

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

            listjobextension p = items.get(position);

            view.passed1 = new ArrayList<>();

            view.passed1=passed;

            view.spark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.spark.playAnimation();
                    if(view.spark.isChecked()){
                        acceptordeny acc = new acceptordeny(ctx,p.getTipe(),p.getIddata(),0,snake,view.passed1,position);
                        acc.execute();
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
                        acceptordeny acc = new acceptordeny(ctx,p.getTipe(),p.getIddata(),1,snake,view.passed1,position);
                        acc.execute();
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
        String iddata;
        String status;
        CoordinatorLayout parent;
        int position;
        List<String> passs =new ArrayList<>();
        //listjobextension =

        public acceptordeny(Context context,String choice,String iddata,int stat, CoordinatorLayout snakebcar,List<String> passedy,int positoin)
        {

            passs = passedy;
            String message = "";
            dialog = new ProgressDialog(ctx);

            dialog.setCancelable(false);




            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

            if(stat==1){
                status = "Diterima";
                message = message+ "Approving ";
            }
            else {
                status = "Ditolak";
                message = message+ "Approving";
            }

            parent = snakebcar;
            this.iddata = iddata;



            this.txt_nothing = txt_nothing;
            this.nothing = nothing;

            for(int i=0;i<options.length;i++){
                if(options[i]==choice){

                    if(i==4){
                        if(stat==0){
                            urldata = urloptions[i]+"/reject";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }
                        else {
                            urldata = urloptions[i]+"/approve";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }

                    }
                    else if(i==5){
                        if(stat==0){
                            urldata = urloptions[i]+"/reject";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }
                        else {
                            urldata = urloptions[i]+"/approve";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }

                    }
                    else if(i==7){
                        if(stat==0){
                            urldata = urloptions[i]+"/reject";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }
                        else {
                            urldata = urloptions[i]+"/approve";
                            tipe = choice;

                            dialog.setMessage(message + tipe);
                            dialog.show();
                        }

                    }
                    else {
                        urldata = urloptions[i];
                        tipe = choice;

                        dialog.setMessage(message + tipe);
                        dialog.show();
                    }
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
            Log.d(TAG + " DoINBackGround",urldata);

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
                        RequestBody body = new FormBody.Builder()
                                .add("id",iddata)
                                .add("status",status)
                                .build();
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();

                    }
                    else if (tipe.equals("Approval Dinas") ){
                        RequestBody body = new FormBody.Builder()
                                .add("id",iddata)
                                .add("status",status)
                                .build();
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();

                    }
                    else if (tipe.equals("Approval Dirumahkan") ){
                        RequestBody body = new FormBody.Builder()
                                .add("id",iddata)
                                .add("status",status)
                                .build();
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();

                    }
                    else if (tipe.equals("Approval Izin") ){
                        RequestBody body = new FormBody.Builder()
                                .add("id",iddata)
                                .add("status",status)
                                .build();
                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();

                    }
                    else if (tipe.equals("Approval Golongan") ){
                        Log.e(TAG, "doInBackground: "+"golongan" );
                        Log.e("list", passs.toString());
                        RequestBody body = new FormBody.Builder()
                                .add("temp_id",passs.get(0))
                                .add("golongan",passs.get(1))
                                .add("keterangan",passs.get(2))
                                .add("gaji",passs.get(3))
                                .add("kehadiran",passs.get(4))
                                .add("makan",passs.get(5))
                                .add("transport",passs.get(6))
                                .add("thr",passs.get(7))
                                .add("tundip",passs.get(8))
                                .add("tunjangan",passs.get(9))
                                .add("absen",passs.get(10))
                                .add("bjps",passs.get(11))
                                .add("pot_lain",passs.get(12))
                                .add("lembur",passs.get(13))
                                .add("umk",passs.get(14))
                                .build();


                        Log.e(TAG, "temp_id"+passs.get(0) );

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Karyawan") ){
                        Log.e(TAG, "doInBackground: "+"karyawan" );
                        RequestBody body = new FormBody.Builder()
                                .add("id",passs.get(0))
                                .build();

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .post(body)
                                .url(urldata)
                                .build();

                    }

                    else if (tipe.equals("Approval Pinjaman") ){

                        RequestBody body = new FormBody.Builder()
                                .add("id",passs.get(0))
                                .add("status",status)
                                .build();

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();

                    }
                    else if (tipe.equals("Approval Pdm") ){
                        Log.e(TAG, "doInBackground: "+"pdm" );
                        RequestBody body = new FormBody.Builder()
                                .add("id",passs.get(0))
                                .add("id_promosi",passs.get(1))
                                .add("id_karyawan",passs.get(2))
                                .add("id_cabang1",passs.get(3))
                                .add("id_departemen1",passs.get(4))
                                .add("id_jabatan1",passs.get(5))
                                .add("id_golongan1",passs.get(6))
                                .add("id_grup1",passs.get(7))
                                .add("tanggal",passs.get(8))
                                .add("judul",passs.get(9))
                                .add("keterangans",passs.get(10))
                                .build();

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .put(body)
                                .url(urldata)
                                .build();

                    }
                    else if (tipe.equals("Approval Punishment") ){

                        RequestBody body = new FormBody.Builder()
                                .add("id",passs.get(0))
                                .add("status",status)
                                .build();

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
                                .build();


                    }
                    else if (tipe.equals("Approval Reward") ){

                        RequestBody body = new FormBody.Builder()
                                .add("id",passs.get(0))
                                .add("status",status)
                                .build();

                        request = new Request.Builder()
                                .header("Authorization",prefs.getString("Authorization",""))
                                .url(urldata)
                                .put(body)
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

                    Log.e("list", passs.toString());

                    if(tipe.equals("Approval Cuti")){
                        if(result.getString("status").equals("true")){
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Dinas") ){
                        if(result.getString("status").equals("true")){
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Dirumahkan") ){
                        if(result.getString("status").equals("true")){
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Izin") ){
                        if(result.getString("status").equals("true")){
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Golongan") ){
                        if(result.getString("status").equals("true")){
                            for (int i=0;i<15;i++){
                                passs.remove(position*14);
                            }
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Karyawan") ){
                        if(result.getString("status").equals("true")){
                            passs.remove(position);
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Pinjaman") ){
                        if(result.getString("status").equals("true")){
                            for (int i=0;i<2;i++){
                                passs.remove(position*1);
                            }
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Pdm") ){
                        if(result.getString("status").equals("true")){
                            for (int i=0;i<10;i++){
                                passs.remove(position*9);
                            }
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Punishment") ){
                        if(result.getString("status").equals("true")){
                            for (int i=0;i<2;i++){
                                passs.remove(position*1);
                            }
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else if (tipe.equals("Approval Reward") ){
                        if(result.getString("status").equals("true")){
                            for (int i=0;i<2;i++){
                                passs.remove(position*1);
                            }
                            Snackbar.make(parent,"Sukses Approval",Snackbar.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                        else {
                            Snackbar.make(parent,"Gagal Approval",Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    if(dialog!=null){
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }



                }
                else {
                    Snackbar.make(parent,"Gagal" + result.toString(),Toast.LENGTH_LONG).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Snackbar.make(parent,"Gagal" + E.getMessage(),Toast.LENGTH_LONG).show();
                //Snackbar.make(parent_view,"Gagal" + E.getMessage(),Toast.LENGTH_LONG).show();
            }



            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}