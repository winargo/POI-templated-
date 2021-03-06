package prima.optimasi.indonesia.payroll.main_hrd.adapter_hrd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;
import prima.optimasi.indonesia.payroll.objects.listjob;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;

public class AdapterListSectionedjob extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private int animation_type = 0;

    View nothing;
    RecyclerView recycler;

    private ProgressDialog dialog ;

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


    private List<listjob> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listjob obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListSectionedjob(Context context, List<listjob> items, int animation_type, ProgressDialog dialog, View nothing, RecyclerView showup) {
        this.dialog = dialog;
        recycler = showup ;
        this.nothing = nothing;
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
        public TextView desc;
        public TextView count;
        public MaterialRippleLayout view;

        public SectionViewHolder(View v) {
            super(v);

            view = v.findViewById(R.id.lyt_parent);
            desc = v.findViewById(R.id.description);
            count = v.findViewById(R.id.numbering);
            title_section = (TextView) v.findViewById(R.id.name);
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

            view.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(ctx,approval.class);
                    a.putExtra("tipe",p.getSectionname());
                    ((Activity)ctx).startActivityForResult(a,1006);
                }
            });


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


            //view.adapter = new AdapterListBasicjob_extention(ctx,)
            retrive ret = new retrive(ctx,p.getSectionname(),view.desc,view.count,position);
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
        int positions;

        TextView desc;
        TextView counting;
        //listjobextension =

        public retrive(Context context,String choice,TextView desc,TextView count,int position)
        {
            this.positions = position;

            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);

            this.desc = desc;
            counting = count;


            for(int i=0;i<options.length;i++){
                if(options[i]==choice){
                    urldata = urloptions[i];
                    tipe = choice;
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

                                //Log.e(TAG, tipe + " " + result.toString());
                                //Log.e("urldata",urldata);

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

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Dinas") ){
                            //Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                //Log.e(TAG, tipe + " " + result.toString());

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

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Dirumahkan") ){
                            //Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                //Log.e(TAG, tipe + " " + result.toString());

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

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Izin") ){
                            //Log.e(tipe,result.toString() );
                            if(result.getString("status").equals("true")) {
                                //Log.e(TAG, tipe + " " + result.toString());

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

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Golongan") ){


                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                            if (result.getString("status").equals("true")) {
                                Boolean tambah = false;

                                JSONArray arrays = result.getJSONArray("data");
                                JSONArray arrays1 = result.getJSONArray("data1");
                                for (int i = 0; i < arrays1.length(); i++) {
                                    JSONObject obj1 = arrays1.getJSONObject(i);

                                    listjobextension data = new listjobextension();

                                    String arrow = " ➡ ";

                                    int index = 0;

                                    Boolean isnotsame = false;

                                    if (obj1.getString("status_info").equals("Tambah")) {

                                        tambah = true;
                                        isnotsame = true;
                                    } else {
                                        for (int j = 0; j < arrays.length(); j++) {
                                            JSONObject obj = arrays.getJSONObject(j);
                                            if (obj.getString("id_golongan").equals(obj1.getString("id_golongan"))) {
                                                isnotsame = false;


                                                break;
                                            } else {
                                                isnotsame = true;
                                                index++;
                                            }
                                        }
                                    }

                                    if (isnotsame) {
                                        data.setNamakaryawan(obj1.getString("golongan") + "(" + obj1.getString("status_info") + ")");

                                        data.setTipe(tipe);

                                        DecimalFormat formatter = new DecimalFormat("###,###,###");


                                        Double gaji = Double.parseDouble(obj1.getString("gaji"));
                                        Double makan = Double.parseDouble(obj1.getString("makan"));
                                        Double transport = Double.parseDouble(obj1.getString("transport"));
                                        Double thr = Double.parseDouble(obj1.getString("thr"));
                                        Double tundip = Double.parseDouble(obj1.getString("tundip"));
                                        Double tunjangan_lain = Double.parseDouble(obj1.getString("tunjangan_lain"));
                                        Double bpjs = Double.parseDouble(obj1.getString("bpjs"));
                                        Double potlain = Double.parseDouble(obj1.getString("pot_lain"));
                                        String lembur = "";

                                        if (obj1.getInt("lembur") == 1) {
                                            lembur = "Hidup";
                                        } else {
                                            lembur = "Tetap";
                                        }

                                        Double umk = Double.parseDouble(obj1.getString("umk"));

                                        String newline = "\n";
                                        String spacing = " : ";

                                        data.setKeterangan("Keterangan" + spacing + obj1.getString("keterangan") + newline
                                                + "Gaji" + spacing + formatter.format(gaji) + newline + "Makan" + spacing + formatter.format(makan) + newline + "Transport"
                                                + spacing + formatter.format(transport) + newline + "thr" + spacing + formatter.format(thr) + newline + "Tundip" + spacing + formatter.format(tundip) + newline
                                                + "Tunjangan Lain" + spacing + formatter.format(tunjangan_lain) + newline + "BPJS" + spacing + formatter.format(bpjs) + newline
                                                + "Potongan Lain" + spacing + formatter.format(potlain) + newline + "Lembur" + spacing + lembur + newline + "UMK" + spacing + formatter.format(umk) + newline);

                                        listdata.add(data);

                                    } else {
                                        data.setNamakaryawan(obj1.getString("golongan") + "(" + obj1.getString("status_info") + ")");
                                        data.setTipe(tipe);

                                        DecimalFormat formatter = new DecimalFormat("###,###,###");


                                        Double gaji1 = Double.parseDouble(obj1.getString("gaji"));
                                        Double makan1 = Double.parseDouble(obj1.getString("makan"));
                                        Double transport1 = Double.parseDouble(obj1.getString("transport"));
                                        Double thr1 = Double.parseDouble(obj1.getString("thr"));
                                        Double tundip1 = Double.parseDouble(obj1.getString("tundip"));
                                        Double tunjangan_lain1 = Double.parseDouble(obj1.getString("tunjangan_lain"));
                                        Double bpjs1 = Double.parseDouble(obj1.getString("bpjs"));
                                        Double potlain1 = Double.parseDouble(obj1.getString("pot_lain"));
                                        String lembur1 = "";

                                        if (obj1.getInt("lembur") == 1) {
                                            lembur1 = "Hidup";
                                        } else {
                                            lembur1 = "Tetap";
                                        }

                                        Double umk1 = Double.parseDouble(obj1.getString("umk"));


                                        String newline = "\n";
                                        String spacing = " : ";

                                        if (!obj1.getString("golongan").equals(obj1.getString("golongan"))) {
                                            data.setNamakaryawan(obj1.getString("golongan") + arrow + obj1.getString("golongan"));
                                        } else {
                                            data.setNamakaryawan(obj1.getString("golongan"));
                                        }

                                        data.setTipe(tipe);

                                        String texting = "Keterangan" + spacing;

                                        if (!obj1.getString("golongan").equals(obj1.getString("golongan"))) {
                                            texting = texting + obj1.getString("keterangan") + arrow + obj1.getString("golongan") + newline;
                                        } else {
                                            texting = texting + obj1.getString("keterangan") + newline;
                                        }

                                        Double gaji2 = Double.parseDouble(obj1.getString("gaji"));
                                        Double makan2 = Double.parseDouble(obj1.getString("makan"));
                                        Double transport2 = Double.parseDouble(obj1.getString("transport"));
                                        Double thr2 = Double.parseDouble(obj1.getString("thr"));
                                        //Double tundip2 = Double.parseDouble(obj1.getString("tundip"));
                                        Double tunjangan_lain2 = Double.parseDouble(obj1.getString("tunjangan_lain"));
                                        Double bpjs2 = Double.parseDouble(obj1.getString("bpjs"));
                                        Double potlain2 = Double.parseDouble(obj1.getString("pot_lain"));
                                        String lembur2 = "";

                                        if (obj1.getInt("lembur") == 1) {
                                            lembur2 = "Hidup";
                                        } else {
                                            lembur2 = "Tetap";
                                        }

                                        Double umk2 = Double.parseDouble(obj1.getString("umk"));

                                        if (gaji1 == gaji2) {
                                            texting = texting + "Gaji" + spacing + formatter.format(gaji1) + newline;
                                        } else {
                                            texting = texting + "Gaji" + spacing + formatter.format(gaji1) + arrow + formatter.format(gaji2) + newline;
                                        }

                                        if (makan1 == makan2) {
                                            texting = texting + "Makan" + spacing + formatter.format(makan1) + newline;
                                        } else {
                                            texting = texting + "Makan" + spacing + formatter.format(makan1) + arrow + formatter.format(makan2) + newline;
                                        }

                                        if (transport1 == transport2) {
                                            texting = texting + "Transport" + spacing + formatter.format(transport1) + newline;
                                        } else {
                                            texting = texting + "Transport" + spacing + formatter.format(transport1) + arrow + formatter.format(transport2) + newline;
                                        }

                                        if (thr1 == thr2) {
                                            texting = texting + "THR" + spacing + formatter.format(thr1) + newline;
                                        } else {
                                            texting = texting + "THR" + spacing + formatter.format(thr1) + arrow + formatter.format(thr2) + newline;
                                        }

                                    /*if(tundip1==tundip2){
                                        texting = texting+"Tundip"+spacing+formatter.format(tundip1)+newline;
                                    }
                                    else {
                                        texting = texting+"Tundip"+spacing+formatter.format(tundip1)+arrow+formatter.format(tundip2)+newline;
                                    }*/

                                        if (tunjangan_lain1 == tunjangan_lain2) {
                                            texting = texting + "Tunjangan Lain" + spacing + formatter.format(tunjangan_lain1) + newline;
                                        } else {
                                            texting = texting + "Tunjangan Lain" + spacing + formatter.format(tunjangan_lain1) + arrow + formatter.format(tunjangan_lain2) + newline;
                                        }

                                        if (bpjs1 == bpjs2) {
                                            texting = texting + "BPJS" + spacing + formatter.format(bpjs1) + newline;
                                        } else {
                                            texting = texting + "BPJS" + spacing + formatter.format(bpjs1) + arrow + formatter.format(bpjs2) + newline;
                                        }

                                        if (potlain1 == potlain2) {
                                            texting = texting + "Potongan Lain" + spacing + formatter.format(potlain1) + newline;
                                        } else {
                                            texting = texting + "Potongan Lain" + spacing + formatter.format(potlain1) + arrow + formatter.format(potlain2) + newline;
                                        }

                                        if (lembur1.equals(lembur2)) {
                                            texting = texting + "Lembur" + spacing + lembur1 + newline;
                                        } else {
                                            texting = texting + "Lembur" + spacing + lembur1 + arrow + lembur2 + newline;
                                        }

                                        if (umk1 == umk2) {
                                            texting = texting + "UMK" + spacing + formatter.format(umk1) + newline;
                                        } else {
                                            texting = texting + "UMK" + spacing + formatter.format(umk1) + arrow + formatter.format(umk2) + newline;
                                        }


                                        String newline2 = "\n";
                                        String spacing2 = " : ";


                                        data.setKeterangan(texting);

                                        listdata.add(data);


                                    }

                                }
                            }
                        }
                        else if (tipe.equals("Approval Karyawan") ){

                            if(result.getString("status").equals("true")) {

                                String newline = "\n";
                                String spacing = " : ";


                                JSONArray arrays = result.getJSONArray("data");
                                JSONArray arrays1 = result.getJSONArray("data1");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);

                                    listjobextension data = new listjobextension();

                                    String texting = "";

                                    String arrow = " ➡ ";

                                    int index = 0;

                                    Boolean isnotsame = false;

                                    for (int j = 0; j < arrays1.length(); j++) {
                                        JSONObject obj1 = arrays1.getJSONObject(j);
                                        if (obj.getString("idfp").equals(obj1.getString("idfp"))) {
                                            isnotsame = false;

                                            break;
                                        } else {
                                            isnotsame = true;
                                            index++;
                                        }

                                    }

                                    if (isnotsame) {
                                        data.setNamakaryawan(obj.getString("nama"));
                                        data.setTipe(tipe);

                                        if (obj.getString("foto").equals("")) {
                                            data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                        } else {
                                            data.setProfilepicture(generator.profileurl + obj.getString("foto"));
                                        }


                                        //0,1,2,39,37,36

                                        for (int k = 0; k < obj.length(); k++) {
                                            if (k == 0) {

                                            } else if (k == 1) {

                                            } else if (k == 2) {

                                            } else if (k == 39) {

                                            } else if (k == 37) {

                                            } else if (k == 36) {

                                            } else {
                                                if (k == 3) {
                                                    texting = texting + karyawandetail[k] + spacing + obj.getString(karyawan[k]) + newline;

                                                }
                                                if (k == 7) {
                                                    texting = texting + karyawandetail[k] + spacing + obj.getString(karyawan[k]) + newline;
                                                }
                                            }

                                        }
                                        data.setKeterangan(texting);

                                        //listdata.add(data);

                                    } else {
                                        Log.e(TAG, "array 1"+arrays1.toString() );

                                        if(arrays1.length()==0){

                                        }
                                        else {
                                            JSONObject obj1 = arrays1.getJSONObject(index);

                                            data.setNamakaryawan(obj1.getString("nama") + "(" + obj1.getString("info_approve").toUpperCase() + ")");
                                            data.setTipe(tipe);

                                            if (obj.getString("foto").equals("")) {
                                                data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                            } else {
                                                data.setProfilepicture(generator.profileurl + obj1.getString("foto"));
                                            }


                                            DecimalFormat formatter = new DecimalFormat("###,###,###");

                                            //0,1,2,39,37,36

                                            for (int k = 0; k < obj.length(); k++) {
                                                if (k == 0) {

                                                } else if (k == 1) {

                                                } else if (k == 2) {

                                                } else if (k == 39) {

                                                } else if (k == 37) {

                                                } else if (k == 36) {

                                                } else {
                                                    if (k == 22) {
                                                        if (obj.getString(karyawan[k]).equals(obj1.getString(karyawan[k]))) {

                                                        } else {
                                                            texting = texting + karyawandetail[k] + spacing + formatter.format(Integer.parseInt(obj.getString(karyawan[k]))) + arrow + formatter.format(Integer.parseInt(obj1.getString(karyawan[k]))) + newline;
                                                        }

                                                    } else {
                                                        if (obj.getString(karyawan[k]).equals(obj1.getString(karyawan[k]))) {

                                                        } else {
                                                            if (obj.getString(karyawan[k]).contains("T00:00:00.000Z")) {
                                                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                                                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                                                                if (obj1.getString(karyawan[k]).equals("null")) {
                                                                    texting = texting + karyawandetail[k] + spacing + format2.format(format1.parse(obj.getString(karyawan[k]).substring(0, 10))) + arrow + obj1.getString(karyawan[k]) + newline;
                                                                } else {
                                                                    texting = texting + karyawandetail[k] + spacing + format2.format(format1.parse(obj.getString(karyawan[k]).substring(0, 10))) + arrow + format2.format(format1.parse(obj1.getString(karyawan[k]).substring(0, 10))) + newline;
                                                                }

                                                            } else {


                                                                if (obj1.getString(karyawan[k]).contains("T00:00:00.000Z")) {
                                                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                                                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                                                                    texting = texting + karyawandetail[k] + spacing + obj.getString(karyawan[k]) + arrow + format2.format(format1.parse(obj1.getString(karyawan[k]).substring(0, 10))) + newline;
                                                                } else {
                                                                    texting = texting + karyawandetail[k] + spacing + obj.getString(karyawan[k]) + arrow + obj1.getString(karyawan[k]) + newline;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (texting.equals("")) {
                                                data.setKeterangan("Tidak Ada Perubahaan");
                                            } else {
                                                data.setKeterangan(texting);
                                            }


                                            listdata.add(data);
                                            //listdata.add(data);
                                        }
                                    }
                                }


                            }
                        }
                        else if (tipe.equals("Approval Pinjaman") ){
                            
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

                            Log.e("PDM",result.toString() );

                            String newline = "\n";
                            String spacing = " : ";

                            if(result.getString("status").equals("true"))
                            {
                                JSONArray arrays = result.getJSONArray("data");
                                JSONArray arrays1 = result.getJSONArray("data1");
                                for (int i = 0; i < arrays1.length(); i++) {
                                    JSONObject obj1 = arrays1.getJSONObject(i);



                                    String arrow = " ➡ ";
                                    if(obj1.getString("status_info").equals("Tambah")){

                                        listjobextension data = new listjobextension();

                                        data.setTipe(tipe);

                                        if(obj1.getString("foto").equals("")){
                                            data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                        }
                                        else {
                                            data.setProfilepicture(generator.profileurl+obj1.getString("foto"));
                                        }

                                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                                        data.setNamakaryawan(obj1.getString("judul"));
                                        data.setKeterangan("Tanggal Pengajuan"+spacing+format2.format(format1.parse(obj1.getString("create_at").substring(0,10)))+newline);

                                        data.setKeterangan(data.getKeterangan()+"Nama"+spacing+obj1.getString("nama")+newline);
                                        data.setKeterangan(data.getKeterangan()+"Jabatan"+spacing+obj1.getString("jabatan")+newline);
                                        data.setKeterangan(data.getKeterangan()+"Group"+spacing+obj1.getString("grup")+newline);
                                        data.setKeterangan(data.getKeterangan()+"Departemen"+spacing+obj1.getString("departemen")+newline);
                                        data.setKeterangan(data.getKeterangan()+"Cabang"+spacing+obj1.getString("cabang")+newline);
                                        data.setKeterangan(data.getKeterangan()+"Golongan"+spacing+obj1.getString("golongan")+newline);
                                        listdata.add(data);
                                    }
                                    else {
                                        for (int j = 0; j < arrays.length(); j++) {
                                            JSONObject obj = arrays.getJSONObject(j);
                                            if(obj.getString("id_promosi").equals(obj1.getString("id_promosi"))) {





                                                listjobextension data = new listjobextension();

                                                data.setTipe(tipe);

                                                if(obj.getString("foto").equals("")){
                                                    data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                                }
                                                else {
                                                    data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                                }


                                            /*"nama": "Aston",
                                            "foto": "abe6ae2097f676a4e7d7869a75139fb9.jpg",
                                            "jabatan": "Kepala Aksesoris",
                                            "grup": "Group Lain",
                                            "departemen": "Accounting",
                                            "cabang": "Denya Tamora",
                                            "golongan": "GOL-01"*/
                                                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                                SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                                                data.setNamakaryawan(obj1.getString("judul"));
                                                data.setKeterangan("Tanggal Pengajuan"+spacing+format2.format(format1.parse(obj1.getString("create_at").substring(0,10)))+newline);

                                                if(obj.getString("nama").equals(obj1.getString("nama"))){
                                                    data.setKeterangan(data.getKeterangan()+"Nama"+spacing+obj.getString("nama")+newline);
                                                }
                                                else {
                                                    data.setNamakaryawan(data.getKeterangan()+"Nama"+spacing+obj.getString("nama")+arrow+obj1.getString("nama")+newline);
                                                }
                                                if(obj.getString("jabatan").equals(obj1.getString("jabatan"))){
                                                    //data.setKeterangan(data.getKeterangan()+"Jabatan"+spacing+obj.getString("jabatan")+newline);
                                                }
                                                else {
                                                    data.setKeterangan(data.getKeterangan()+"Jabatan"+spacing+obj.getString("jabatan")+arrow+obj1.getString("jabatan")+newline);
                                                }
                                                if(obj.getString("grup").equals(obj1.getString("grup"))){
                                                    //data.setKeterangan(data.getKeterangan()+"Group"+spacing+obj.getString("grup")+newline);
                                                }
                                                else {
                                                    data.setKeterangan(data.getKeterangan()+"Group"+spacing+obj.getString("grup")+arrow+obj1.getString("grup")+newline);
                                                }
                                                if(obj.getString("departemen").equals(obj1.getString("departemen"))){
                                                    //data.setKeterangan(data.getKeterangan()+"Departemen"+spacing+obj.getString("departemen")+newline);
                                                }
                                                else {
                                                    data.setKeterangan(data.getKeterangan()+"Departemen"+spacing+obj.getString("departemen")+arrow+obj1.getString("departemen")+newline);
                                                }
                                                if(obj.getString("cabang").equals(obj1.getString("cabang"))){
                                                    //data.setKeterangan(data.getKeterangan()+"Cabang"+spacing+obj.getString("cabang")+newline);
                                                }
                                                else {
                                                    data.setKeterangan(data.getKeterangan()+"Cabang"+spacing+obj.getString("cabang")+arrow+obj1.getString("cabang")+newline);
                                                }
                                                if(obj.getString("golongan").equals(obj1.getString("golongan"))){
                                                    //data.setKeterangan(data.getKeterangan()+"Golongan"+spacing+obj.getString("golongan")+newline);
                                                }
                                                else {
                                                    data.setKeterangan(data.getKeterangan()+"Golongan"+spacing+obj.getString("golongan")+arrow+obj1.getString("golongan")+newline);
                                                }
                                                listdata.add(data);

                                            }
                                            else {

                                            }
                                        }

                                    }



                                }
                            }
                        }
                        else if (tipe.equals("Approval Punishment") ){
                            
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //

                            if(result.getString("status").equals("true")) {

                                DecimalFormat formatter = new DecimalFormat("###,###,###");

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan("Kasus : "+obj.getString("judul")+"\n"+"Nilai : "+formatter.format(obj.getInt("nilai"))+"\nKeterangan : "+obj.getString("keterangan"));
                                    data.setTgldiajukan(obj.getString("created_at").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }
                            }
                        }
                        else if (tipe.equals("Approval Reward") ){
                            
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //

                            if(result.getString("status").equals("true")) {

                                DecimalFormat formatter = new DecimalFormat("###,###,###");

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
                                    listjobextension data = new listjobextension();
                                    data.setJabatan(obj.getString("jabatan"));
                                    data.setKeterangan("Nilai : "+formatter.format(obj.getInt("nilai"))+"\nKeterangan : "+obj.getString("keteranganr"));
                                    data.setTgldiajukan(obj.getString("created_at").substring(0,10));
                                    data.setNamakaryawan(obj.getString("nama"));
                                    data.setTipe(tipe);

                                    if(obj.getString("foto").equals("")){
                                        data.setProfilepicture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ");
                                    }
                                    else {
                                        data.setProfilepicture(generator.profileurl+obj.getString("foto"));
                                    }

                                    listdata.add(data);
                                }

                            }

                        }


                        if(listdata.size()==0){
                            String data = String.valueOf(listdata.size());
                            desc.setText("Tidak ada "+tipe +" saat ini");
                            counting.setText(data);
                            counting.setVisibility(View.GONE);
                        }
                        else {
                            String data = String.valueOf(listdata.size());
                            desc.setText(listdata.size()+" "+tipe+" tersisa");
                            counting.setText(data);
                        }

                        JumpingBeans jumpingBeans1 = JumpingBeans.with(counting)
                                .makeTextJump(0, counting.getText().toString().length())
                                .setIsWave(false)
                                .setLoopDuration(1000)  // ms
                                .build();

                    }
                    else {
                        Toast.makeText(ctx,"Gagal" + result.toString(),Toast.LENGTH_LONG).show();
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

    public void removeItem(int position) {
        items.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());

    }
    public void removeItems(int position) {
        items.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);

    }
    public void removeItemfast(int position) {
        items.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyDataSetChanged();

    }






}