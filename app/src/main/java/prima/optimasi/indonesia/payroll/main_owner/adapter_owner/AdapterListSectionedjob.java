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
                                listjobextension data = new listjobextension();
                                Log.e(TAG, tipe + " " + result.toString());

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
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
                                listjobextension data = new listjobextension();

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
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
                                listjobextension data = new listjobextension();

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
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
                                listjobextension data = new listjobextension();

                                JSONArray arrays = result.getJSONArray("data");
                                for (int i = 0; i < arrays.length(); i++) {
                                    JSONObject obj = arrays.getJSONObject(i);
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
                            listjobextension data = new listjobextension();

                            JSONArray arrays = result.getJSONArray("data");
                            for (int i = 0; i < arrays.length(); i++) {
                                JSONObject obj = arrays.getJSONObject(i);
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

                                if(obj.getString("lembur").equals("1")){
                                    lembur = "Hidup";
                                }
                                else {
                                    lembur = "Tetap";
                                }

                                Double umk = Double.parseDouble(obj.getString("umk"));

                                String newline = "\n";
                                String spacing = " : ";

                                data.setKeterangan("Keterangan"+spacing+obj.getString("keterangan")+newline
                                        +"Gaji"+spacing+gaji+newline+"Makan"+spacing+makan+newline+"Transport"
                                        +spacing+transport+newline+"thr"+spacing+thr+newline+"Tundip"+spacing+tundip+newline
                                        +"Tunjangan Lain"+spacing+tunjangan_lain+newline+"BPJS"+spacing+bpjs+newline
                                        +"Potongan Lain"+spacing+potlain+newline+"Lembur"+spacing+newline+lembur+"UMK"+spacing+umk+newline);

                                listdata.add(data);
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
                        }
                        else if (tipe.equals("Approval Pinjaman") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
                        }
                        else if (tipe.equals("Approval Pdm") ){
                            Log.e(TAG, tipe+" "+result.toString() );
                            //String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin",
                            // "Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment",
                            // "Approval Reward"};
                            //
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