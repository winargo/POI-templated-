package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.adapter.AdapterListBasic;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.mainmenu_owner;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listjob;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterListSectionedjob extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    private ProgressDialog dialog ;

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};
    String[] urloptions = new String[]{generator.getapprovalcutiyurl,generator.getapprovaldinasyurl,generator.getapprovaldirumahkanyurl,generator.getapprovalizinyurl,generator.getapprovalgolonganyurl,generator.getapprovalkaryawanyurl,generator.getapprovalpinjamanyurl,generator.getapprovalpdlyurl,generator.getapprovalpunihsmentyurl,generator.getapprovalrewardyurl};


    private List<listjob> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listjob obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSectionedjob(Context context, List<listjob> items, int animation_type,ProgressDialog dialog) {
        this.dialog = dialog;
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public OriginalViewHolder(View v) {
            super(v);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title_section;
        public RecyclerView recycler;
        View nothing;
        AdapterListBasicjob_extention adapter;
        public TextView txt_type;

        public SectionViewHolder(View v) {
            super(v);
            txt_type = v.findViewById(R.id.txt_nothing);
            nothing = v.findViewById(R.id.lyt_parent1);
            recycler = v.findViewById(R.id.recyclerViewjob);
            title_section = (TextView) v.findViewById(R.id.title_section);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_approval, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_job, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listjob p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

        } else {

            SectionViewHolder view = (SectionViewHolder) holder;
            SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

            view.title_section.setText(p.getSectionname());
            view.title_section.setTextColor(Color.BLACK);
            view.title_section.setTextSize(15f);
            view.title_section.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if(dialog!=null && dialog.isShowing()){
                dialog.setMessage("Getting job ("+generator.jobcount+"/10)");
                generator.jobcount++;
            }
            if(generator.jobcount>=10){
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                    generator.jobcount=0;
                }
            }


            view.recycler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            //view.adapter = new AdapterListBasicjob_extention(ctx,)
            retrive ret = new retrive(ctx,p.getSectionname(),view.nothing,view.txt_type,view.adapter,view.recycler);
            ret.execute();


            setAnimation(view.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).getSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    public void insertItem(int index, listjob kar){
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

    private class retrive extends AsyncTask<Void, Integer, String>
    {
        AdapterListBasicjob_extention adapter;
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
        RecyclerView recycler ;
        //listjobextension =

        public retrive(Context context,String choice,View nothing,TextView txt_nothing,AdapterListBasicjob_extention adapter,RecyclerView recycler)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

            this.recycler = recycler;

            this.adapter = adapter;

            this.txt_nothing = txt_nothing;
            this.nothing = nothing;

            for(int i=0;i<options.length;i++){
                if(options[i]==choice){
                    urldata = urloptions[i];
                    tipe = choice;
                    this.txt_nothing.setText("Tidak Ada data "+options[i]);
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

                    request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .url(urldata)
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
                            Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {

                                Log.e(TAG, tipe + " " + result.toString());
                                Log.e("urldata",urldata);

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan(obj.getString("keterangans"));
                                    data.setTanggal1(obj.getString("mulai_berlaku").substring(0,10));
                                    data.setTanggal2(obj.getString("exp_date").substring(0,10));
                                    data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Dinas") ){
                            Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                Log.e(TAG, tipe + " " + result.toString());
                                //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                                // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                                // "Approval Reward"};
                                //


                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan(obj.getString("keterangans"));
                                    data.setTanggal1(obj.getString("tgl_dinas").substring(0,10));
                                    data.setTanggal2(obj.getString("akhir_dinas").substring(0,10));
                                    data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Dirumahkan") ){
                            Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                Log.e(TAG, tipe + " " + result.toString());
                                //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                                // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                                // "Approval Reward"};
                                //


                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan(obj.getString("keterangans"));
                                    data.setTanggal1(obj.getString("tgl_dirumahkan").substring(0,10));
                                    data.setTanggal2(obj.getString("akhir_dirumahkan").substring(0,10));
                                    data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Izin") ){
                            Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                Log.e(TAG, tipe + " " + result.toString());
                                //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                                // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                                // "Approval Reward"};
                                //


                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan(obj.getString("keterangans"));
                                    data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                                    data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                                    data.setTgldiajukan(obj.getString("tanggal").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);
                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Golongan") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //


                            JSONArray arrays = result.getJSONArray("data");
                            JSONArray arrays1 = result.getJSONArray("data1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj1 = arrays1.getJSONObject(i);
                                JSONObject obj = arrays.getJSONObject(i);

                                listjobextension data = new listjobextension();

                                String arrow = " ▶ ";

                                if(!obj.getString("id_golongan").equals(obj1.getString("id_golongan"))){
                                    data.setTgldiajukan(obj.getString("create_at").substring(0,10));
                                    data.setNamakaryawan(obj.getString("golongan"));
                                    data.setTipe(tipe);

                                    DecimalFormat formatter = new DecimalFormat("###,###,###");


                                    Double gaji = Double.parseDouble(obj.getString("gaji"));
                                    Double makan = Double.parseDouble(obj.getString("makan"));
                                    Double transport = Double.parseDouble(obj.getString("transport"));
                                    Double thr = Double.parseDouble(obj.getString("thr"));
                                    Double tundip = Double.parseDouble(obj.getString("tundip"));
                                    Double tunjangan_lain = Double.parseDouble(obj.getString("tunjangan_lain"));
                                    Double bpjs = Double.parseDouble(obj.getString("bpjs"));
                                    Double potlain = Double.parseDouble(obj.getString("pot_lain"));
                                    String lembur = "";

                                    if(obj.getInt("lembur")==1){
                                        lembur = "Hidup";
                                    }
                                    else {
                                        lembur = "Tetap";
                                    }

                                    Double umk = Double.parseDouble(obj.getString("umk"));

                                    String newline = "\n";
                                    String spacing = " : ";

                                    data.setKeterangan("Keterangan"+spacing+obj.getString("keterangan")+newline
                                            +"Gaji"+spacing+formatter.format(gaji)+newline+"Makan"+spacing+formatter.format(makan)+newline+"Transport"
                                            +spacing+formatter.format(transport)+newline+"thr"+spacing+formatter.format(thr)+newline+"Tundip"+spacing+formatter.format(tundip)+newline
                                            +"Tunjangan Lain"+spacing+formatter.format(tunjangan_lain)+newline+"BPJS"+spacing+formatter.format(bpjs)+newline
                                            +"Potongan Lain"+spacing+formatter.format(potlain)+newline+"Lembur"+spacing+lembur+newline+"UMK"+spacing+formatter.format(umk)+newline);

                                    listdata.add(data);
                                }else{
                                    data.setTgldiajukan(obj.getString("create_at").substring(0,10));


                                    data.setNamakaryawan(obj.getString("golongan"));
                                    data.setTipe(tipe);

                                    DecimalFormat formatter = new DecimalFormat("###,###,###");


                                    Double gaji1 = Double.parseDouble(obj.getString("gaji"));
                                    Double makan1 = Double.parseDouble(obj.getString("makan"));
                                    Double transport1 = Double.parseDouble(obj.getString("transport"));
                                    Double thr1 = Double.parseDouble(obj.getString("thr"));
                                    Double tundip1 = Double.parseDouble(obj.getString("tundip"));
                                    Double tunjangan_lain1 = Double.parseDouble(obj.getString("tunjangan_lain"));
                                    Double bpjs1 = Double.parseDouble(obj.getString("bpjs"));
                                    Double potlain1 = Double.parseDouble(obj.getString("pot_lain"));
                                    String lembur1 = "";

                                    if(obj.getInt("lembur")==1){
                                        lembur1 = "Hidup";
                                    }
                                    else {
                                        lembur1 = "Tetap";
                                    }

                                    Double umk = Double.parseDouble(obj.getString("umk"));


                                    String newline = "\n";
                                    String spacing = " : ";

                                    if(obj.getString("golongan").equals(obj1.getString("golongan"))){
                                        data.setNamakaryawan(obj.getString("golongan")+arrow+obj1.getString("golongan"));
                                    }
                                    else {

                                    }

                                    data.setTipe(tipe);



                                    Double gaji2 = Double.parseDouble(obj.getString("gaji"));
                                    Double makan2 = Double.parseDouble(obj.getString("makan"));
                                    Double transport2 = Double.parseDouble(obj.getString("transport"));
                                    Double thr2 = Double.parseDouble(obj.getString("thr"));
                                    Double tundip2 = Double.parseDouble(obj.getString("tundip"));
                                    Double tunjangan_lain2 = Double.parseDouble(obj.getString("tunjangan_lain"));
                                    Double bpjs2 = Double.parseDouble(obj.getString("bpjs"));
                                    Double potlain2 = Double.parseDouble(obj.getString("pot_lain"));
                                    String lembur2 = "";

                                    if(obj.getInt("lembur")==1){
                                        lembur2 = "Hidup";
                                    }
                                    else {
                                        lembur2 = "Tetap";
                                    }

                                    Double umk2 = Double.parseDouble(obj.getString("umk"));


                                    String newline2 = "\n";
                                    String spacing2 = " : ";

                                    if()


                                    data.setKeterangan("Keterangan"+spacing+obj.getString("keterangan")+newline
                                            +"Gaji"+spacing+formatter.format(gaji)+newline+"Makan"+spacing+formatter.format(makan)+newline+"Transport"
                                            +spacing+formatter.format(transport)+newline+"thr"+spacing+formatter.format(thr)+newline+"Tundip"+spacing+formatter.format(tundip)+newline
                                            +"Tunjangan Lain"+spacing+formatter.format(tunjangan_lain)+newline+"BPJS"+spacing+formatter.format(bpjs)+newline
                                            +"Potongan Lain"+spacing+formatter.format(potlain)+newline+"Lembur"+spacing+lembur+newline+"UMK"+spacing+formatter.format(umk)+newline);

                                    listdata.add(data);
                                }


                            }

                            /*JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
                                data.setJabatan(obj.getString("jabatan"));
                                data.setKeterangan(obj.getString("keterangans"));
                                data.setTanggal1(obj.getString("tgl_izin").substring(0,10));
                                data.setTanggal2(obj.getString("akhir_izin").substring(0,10));
                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);
                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                listdata.add(data);
                            }*/
                        }
                        else if (tipe.equals("Approval Karyawan") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //


                            JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);

                                listjobextension data = new listjobextension();

                                data.setTgldiajukan(obj.getString("created_at").substring(0,10));

                                String penentuan = "";

                                if(obj.getString("judul_approve").contains("Update")){
                                    penentuan = "UPDATE";
                                }
                                else if(obj.getString("judul_approve").contains("Edit")) {
                                    penentuan = "EDIT";
                                }
                                else {
                                    penentuan = "HAPUS";
                                }
                                data.setNamakaryawan(obj.getString("nama")+"("+penentuan+")");
                                data.setTipe(tipe);

                                DecimalFormat formatter = new DecimalFormat("###,###,###");

                                String alamat = obj.getString("alamat");
                                String tempatlahir = obj.getString("tempat_lahir");
                                String tgllahir = obj.getString("tgl_lahir");
                                String telepon = obj.getString("telepon");
                                String nowali = obj.getString("no_wali");
                                String email = obj.getString("email");
                                String tglmasuk = obj.getString("tgl_masuk");
                                String shift =  obj.getString("shift");
                                String jeniskelamin= obj.getString("kelamin"),Status_kerja= obj.getString("status_kerja"),gaji= formatter.format(obj.getDouble("gaji")),kontrak= obj.getString("kontrak");

                                String neline = "\n";
                                String spacing = " : ";

                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                data.setKeterangan("Alamat"+spacing+alamat+neline +"Tempat Lahir"+spacing+tempatlahir+neline+"Tanggal Lahir"+spacing+tgllahir+neline+"Telepon"+spacing+telepon+neline
                                        +"No Wali"+spacing+nowali+neline+"Email"+spacing+email+neline+"Tanggal Masuk"+spacing+tglmasuk+neline+"Shift"+spacing+shift+neline+"Jenis Kelamin"+spacing+jeniskelamin
                                        +neline+"Status Kerja"+spacing+Status_kerja+neline+"Gaji"+spacing+gaji+neline+"Kontrak"+spacing+kontrak+neline);

                                listdata.add(data);
                            }

                        }
                        else if (tipe.equals("Approval Pinjaman") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //Approval Pinjaman {"status":true,"message":"berhasil get data","res1":[{"nama":"Sparno","id_pinjaman":17,"id_karyawan":7,"tanggal":"2018-10-23T00:00:00.000Z","jumlah":100000,
                            // "bayar":0,"sisa":100000,"keterangans":"-","status":"Proses","cicilan":0,"cara_pembayaran":"Cash"}]}
                            //
                            JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);

                                DecimalFormat formatter = new DecimalFormat("###,###,###");
                                listjobextension data = new listjobextension();

                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));

                                data.setNamakaryawan(obj.getString("nama"));
                                data.setTipe(tipe);

                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                String neline = "\n";
                                String spacing = " : ";

                                String jumlah = formatter.format(obj.getDouble("jumlah"));

                                String cicilan = formatter.format(obj.getDouble("cicilan")),cara_pembayaran=obj.getString("cara_pembayaran"),keterangans=obj.getString("keterangans");

                                data.setKeterangan("Jumlah Pinjaman"+spacing+jumlah+neline+"Cicilan / Bulan"+spacing+cicilan+neline+"Cara pembayaran"+spacing+cara_pembayaran+neline+"Keterangan"+spacing+keterangans);

                                listdata.add(data);
                            }

                        }
                        else if (tipe.equals("Approval Pdm") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //10-23 14:32:23.807 10754-10754/prima.optimasi.indonesia.payroll E/retrive:
                            // 10-23 14:32:23.896 10754-10754/prima.optimasi.indonesia.payroll E/retrive: Approval Pdm
                            // {"status":true,"message":"berhasil get data","res1":
                            // [{"temp_id_promosi":146,"id_promosi":35,"id_karyawan":7,"id_cabang1":3,"id_departemen1":2,"id_jabatan1":4,
                            // "id_golongan1":15,"id_grup1":6,"tanggal":"2018-09-01T00:00:00.000Z","judul":"Hapus Data",
                            // "keterangans":"asdasdsa","status":"Proses","status_info":"Hapus","create_at":"2018-10-08T12:23:10.000Z",
                            // "approve_at":"2018-09-21T10:44:42.000Z"},{"temp_id_promosi":147,"id_promosi":38,"id_karyawan":7,"id_cabang1":3,
                            // "id_departemen1":2,"id_jabatan1":4,"id_golongan1":15,"id_grup1":6,"tanggal":"2018-09-01T00:00:00.000Z",
                            // "judul":"Hapus Data","keterangans":"asdfasd1234333","status":"Proses","status_info":"Hapus",
                            // "create_at":"2018-10-08T13:24:29.000Z","approve_at":"2018-09-21T10:44:38.000Z"},


                            JSONArray arrays = result.getJSONArray("res1");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);

                                if(obj.getString("status_info").equals("Hapus")){

                                }
                                else if(obj.getString("status_info").equals("Edit")){

                                }
                                else {

                                }


                                DecimalFormat formatter = new DecimalFormat("###,###,###");
                                listjobextension data = new listjobextension();

                                data.setTgldiajukan(obj.getString("tanggal").substring(0,10));

                                data.setNamakaryawan(obj.getString("nama")+obj.getString("judul"));
                                data.setTipe(tipe);

                                data.setProfilepicture(generator.profileurl+obj.getString("foto"));

                                String neline = "\n";
                                String spacing = " : ";

                                String jumlah = formatter.format(obj.getDouble("jumlah"));

                                String cicilan = formatter.format(obj.getDouble("cicilan")),cara_pembayaran=obj.getString("cara_pembayaran"),keterangans=obj.getString("keterangans");

                                data.setKeterangan("Jumlah Pinjaman"+spacing+jumlah+neline+"Cicilan / Bulan"+spacing+cicilan+neline+"Cara pembayaran"+spacing+cara_pembayaran+neline+"Keterangan"+spacing+keterangans);

                                listdata.add(data);
                            }
                        }
                        else if (tipe.equals("Approval Punishment") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                        }
                        else if (tipe.equals("Approval Reward") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //

                        }

                        if(listdata.size()==0){
                        nothing.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            nothing.setVisibility(View.GONE);
                        }

                        adapter = new AdapterListBasicjob_extention(ctx,listdata);
                        adapter.notifyDataSetChanged();

                        recycler.setLayoutManager(new LinearLayoutManager(ctx));
                        recycler.setHasFixedSize(true);
                        recycler.setAdapter(adapter);
                    }
                    else {
                        Toast.makeText(ctx,"Gagal" + result.getString("message"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Toast.makeText(ctx,"Gagal" + result,Toast.LENGTH_SHORT).show();
            }



            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

}