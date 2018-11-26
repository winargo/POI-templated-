package prima.optimasi.indonesia.payroll.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_cuti;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_izin;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_penggajian;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_peringkatkaryawan;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_pinjaman;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_sakit;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listreport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterListBasic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Date tanggal_masuk, tanggal_keluar, tgl_masuk, tgl_keluar;
    String tanggal_masuk1, tanggal_keluar1;
    private List<listreport> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBasic(Context context, List<listreport> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView desc;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            desc = v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            listreport p = items.get(position);
            view.name.setText(p.getJudul());
            view.desc.setText(p.getDeskripsi());


            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(view.name.getText().toString().equals("Cuti")){
                        Intent cuti = new Intent(ctx,owner_cuti.class);
                        ctx.startActivity(cuti);
                    }
                    else if(view.name.getText().toString().equals("Izin")){
                        Intent izin = new Intent(ctx,owner_izin.class);
                        ctx.startActivity(izin);
                    }else if(view.name.getText().toString().equals("Sakit")){
                        Intent sakit = new Intent(ctx,owner_sakit.class);
                        ctx.startActivity(sakit);
                    }else if(view.name.getText().toString().equals("Pinjaman")){
                        Intent pinjaman = new Intent(ctx,owner_pinjaman.class);
                        ctx.startActivity(pinjaman);
                    }else if(view.name.getText().toString().equals("Penggajian")){
                        final Dialog dialog = new Dialog(ctx);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                        dialog.setContentView(R.layout.dialog_range_tanggal);
                        dialog.setCancelable(true);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        final Button spn_from_date = (Button) dialog.findViewById(R.id.from_date);
                        final Button spn_to_date = (Button) dialog.findViewById(R.id.to_date);

                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DATE,0);
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = df.format(c.getTime());
                        spn_from_date.setText(formattedDate);
                        spn_to_date.setText(formattedDate);
                        tanggal_masuk=c.getTime();
                        tanggal_keluar=c.getTime();
                        tanggal_masuk1=formattedDate;
                        tanggal_keluar1=formattedDate;

                        spn_from_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDatePickerLight(spn_from_date, "masuk");
                            }
                        });

                        spn_to_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDatePickerLight(spn_to_date, "keluar");
                            }
                        });

                        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    tgl_masuk = df.parse(tanggal_masuk1);
                                    tgl_keluar = df.parse(tanggal_keluar1);
                                    if (tgl_masuk.compareTo(tgl_keluar) > 0) {
                                        final Dialog warn = new Dialog(ctx);
                                        warn.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                        warn.setContentView(R.layout.dialog_warning);
                                        warn.setCancelable(true);

                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(warn.getWindow().getAttributes());
                                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        ImageView icon = warn.findViewById(R.id.icon);
                                        TextView title = warn.findViewById(R.id.title);
                                        TextView content = warn.findViewById(R.id.content);
                                        icon.setVisibility(View.GONE);
                                        title.setText("Kesalahan Data Tanggal");
                                        content.setText("Tanggal keluar wajib lebih atau sama dari tanggal masuk yang diinput");
                                        ((AppCompatButton) warn.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(ctx, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                                                warn.dismiss();
                                            }
                                        });

                                        warn.show();
                                        warn.getWindow().setAttributes(lp);
                                    } else {
                                        dialog.dismiss();
                                        Intent pengajian = new Intent(ctx, owner_penggajian.class);
                                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                                        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
                                        pengajian.putExtra("tanggal_masuk", fomat.format(format1.parse(tanggal_masuk1)));
                                        pengajian.putExtra("tanggal_keluar", fomat.format(format1.parse(tanggal_keluar1)));
                                        Log.e("DATE : ", "" + fomat.format(format1.parse(tanggal_masuk1)) + " " + fomat.format(format1.parse(tanggal_keluar1)));
                                        ctx.startActivity(pengajian);
                                    }
                                }
                                catch(Exception e){
                                    Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                                    Log.e("NEXT DATE : ", ""+tanggal_masuk1+" "+tanggal_keluar1);
                                }

                            }
                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    }else if(view.name.getText().toString().equals("Kinerja Karyawan")){
                        final Dialog dialog = new Dialog(ctx);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                        dialog.setContentView(R.layout.dialog_range_tanggal);
                        dialog.setCancelable(true);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        final TextView title = (TextView) dialog.findViewById(R.id.title);
                        final Button spn_from_date = (Button) dialog.findViewById(R.id.from_date);
                        final Button spn_to_date = (Button) dialog.findViewById(R.id.to_date);

                        title.setText("Peringkat Karyawan");
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DATE,0);
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = df.format(c.getTime());
                        spn_from_date.setText(formattedDate);
                        spn_to_date.setText(formattedDate);
                        tanggal_masuk=c.getTime();
                        tanggal_keluar=c.getTime();
                        tanggal_masuk1=formattedDate;
                        tanggal_keluar1=formattedDate;

                        spn_from_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDatePickerLight(spn_from_date, "masuk");
                            }
                        });

                        spn_to_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDatePickerLight(spn_to_date, "keluar");
                            }
                        });

                        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    tgl_masuk = df.parse(tanggal_masuk1);
                                    tgl_keluar = df.parse(tanggal_keluar1);
                                    if (tgl_masuk.compareTo(tgl_keluar) > 0) {
                                        final Dialog warn = new Dialog(ctx);
                                        warn.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                        warn.setContentView(R.layout.dialog_warning);
                                        warn.setCancelable(true);

                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(warn.getWindow().getAttributes());
                                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                        ImageView icon = warn.findViewById(R.id.icon);
                                        TextView title = warn.findViewById(R.id.title);
                                        TextView content = warn.findViewById(R.id.content);
                                        icon.setVisibility(View.GONE);
                                        title.setText("Kesalahan Data Tanggal");
                                        content.setText("Tanggal keluar wajib lebih atau sama dari tanggal masuk yang diinput");
                                        ((AppCompatButton) warn.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(ctx, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                                                warn.dismiss();
                                            }
                                        });

                                        warn.show();
                                        warn.getWindow().setAttributes(lp);
                                    } else {
                                        dialog.dismiss();
                                        Intent peringkat = new Intent(ctx, owner_peringkatkaryawan.class);
                                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                                        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");

                                        peringkat.putExtra("tanggal_masuk", fomat.format(format1.parse(tanggal_masuk1)));
                                        peringkat.putExtra("tanggal_keluar", fomat.format(format1.parse(tanggal_keluar1)));
                                        Log.e("DATE : ", "" + fomat.format(format1.parse(tanggal_masuk1)) + " " + fomat.format(format1.parse(tanggal_keluar1)));
                                        ctx.startActivity(peringkat);
                                    }
                                }
                                catch(Exception e){
                                    Log.e("NEXT DATE : ", ""+tanggal_masuk.compareTo(tanggal_keluar));
                                    Log.e("NEXT DATE : ", ""+tanggal_masuk1+" "+tanggal_keluar1);
                                }

                            }
                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                    }
                }
            });
        }
    }
    private void tanggal_masuk(Date tanggal){
        tanggal_masuk=tanggal;

    }
    private void tanggal_keluar(Date tanggal){
        tanggal_keluar=tanggal;

    }
    private void tanggal_masuk1(String tanggal){
        tanggal_masuk1=tanggal;

    }
    private void tanggal_keluar1(String tanggal){
        tanggal_keluar1=tanggal;

    }
    private void dialogDatePickerLight(Button bt, String jenis) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        if(jenis.equals("masuk")){
                            tanggal_masuk(calendar.getTime());
                            tanggal_masuk1(df.format(calendar.getTime()));
                            bt.setText(tanggal_masuk1);
                        }
                        else{
                            tanggal_keluar(calendar.getTime());
                            tanggal_keluar1(df.format(calendar.getTime()));
                            bt.setText(tanggal_keluar1);
                        }
                        Log.e("NEXT DATE : ", calendar.getTime().toString());
                        //bt.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(ctx.getResources().getColor(R.color.colorPrimary));
        datePicker.setMaxDate(cur_calender);
        datePicker.show(((Activity)ctx).getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}