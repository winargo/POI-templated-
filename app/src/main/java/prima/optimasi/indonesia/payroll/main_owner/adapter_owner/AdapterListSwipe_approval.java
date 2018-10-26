package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkButtonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.helper.SwipeItemTouchHelper;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listjobextension;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;

public class AdapterListSwipe_approval extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

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

    public AdapterListSwipe_approval(Context context, List<listjobextension> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView desc;
        public RelativeLayout green;
        public RelativeLayout red;
        public RelativeLayout main;
        public SparkButton spark;

        public OriginalViewHolder(View v) {
            super(v);
            spark = v.findViewById(R.id.spark_button);
            main = v.findViewById(R.id.mainlayout);
            green = v.findViewById(R.id.greeny);
            red = v.findViewById(R.id.reddy);
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
                    removeItem(position);
                }
            });

            listjobextension p = items.get(position);

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


            }else if(p.getTipe().equals(options[4])){

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
    }

    public void restoreItem(listjobextension item, int position) {
        items.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}