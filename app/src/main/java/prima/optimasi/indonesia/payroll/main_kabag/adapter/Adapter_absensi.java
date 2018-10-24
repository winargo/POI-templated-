package prima.optimasi.indonesia.payroll.main_kabag.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.logabsensi_karyawan;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class Adapter_absensi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<logabsensi_karyawan> items = new ArrayList<>();
    //private logabsensi_karyawan items=new logabsensi_karyawan();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, logabsensi_karyawan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapter_absensi(Context context, List<logabsensi_karyawan> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggal;
        public TextView checkpos;
        public TextView checkin;
        public TextView checkout;
        public TextView breakin;
        public TextView breakout;
        public TextView jamkerja;
        public ImageButton bt_expand;
        public View lyt_expand;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tanggal = (TextView) v.findViewById(R.id.tanggal);
            checkpos = (TextView) v.findViewById(R.id.activity_check_pos);
            checkin = (TextView) v.findViewById(R.id.activity_check_in);
            checkout = (TextView) v.findViewById(R.id.activity_check_out);
            breakin = (TextView) v.findViewById(R.id.activity_break_in);
            breakout = (TextView) v.findViewById(R.id.activity_break_out);
            jamkerja = (TextView) v.findViewById(R.id.text_jamkerjatotal_karyawan);

            bt_expand = (ImageButton) v.findViewById(R.id.bt_expand);
            lyt_expand = (View) v.findViewById(R.id.lyt_expand);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absensi_karyawan, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final logabsensi_karyawan p = items.get(position);
            view.tanggal.setText(p.getTanggal());

            view.checkin.setText(p.getCheckin());
            view.checkout.setText(p.getCheckout());
            view.breakin.setText(p.getBreakin());
            view.breakout.setText(p.getBreakout());

            if(!view.checkin.getText().equals("-") && !view.checkout.getText().equals("-")){
                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

                Date date1=null;
                Date date2=null;

                try {
                    date1 = format.parse(view.checkin.getText().toString());
                    date2 = format.parse(view.checkout.getText().toString());


                    long difference = date2.getTime() - date1.getTime();
                    if(difference<0)
                    {
                        Date dateMax = format.parse("24:00:00");
                        Date dateMin = format.parse("00:00:00");
                        difference=(dateMax.getTime() -date1.getTime() )+(date2.getTime()-dateMin.getTime());
                    }
                    int days = (int) (difference / (1000*60*60*24));
                    int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                    int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
                    hours = hours+(days*24);
                    view.jamkerja.setText("Total Jam Kerja : " + hours + " Jam " + min +" Menit");


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else
            {
                view.jamkerja.setText("Total Jam Kerja : " + " Total Jam Tidak Tersedia");
            }
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });

            view.bt_expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean show = toggleLayoutExpand(!p.expanded, v, view.lyt_expand);
                    items.get(position).expanded = show;
                }
            });


            view.checkpos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(p.getCheckposss()){

                    }
                    else {
                        p.setCheckposss(true);
                        String temp = "";
                        temp = view.checkpos.getText().toString();
                        view.checkpos.setText("Check POS");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.checkpos.setText(finalTemp);
                                p.setCheckposss(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.checkin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(p.getCheckins()){

                        /*
                        if(p.getCheckin().equals("null")){
                            view.checkin.setText("-");
                        }
                        else{
                            view.checkin.setText(p.getCheckin());
                        }
                        */
                    }
                    else {
                        p.setCheckins(true);
                        String temp = "";
                        temp = view.checkin.getText().toString();
                        view.checkin.setText("Check IN");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.checkin.setText(finalTemp);
                                p.setCheckins(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(p.getCheckouts()){

                        /*
                        if(p.getCheckout().equals("null")){
                            view.checkout.setText("-");
                        }
                        else{
                            view.checkout.setText(p.getCheckout());
                        }*/
                    }
                    else {
                        p.setCheckouts(true);
                        String temp = "";
                        temp = view.checkout.getText().toString();
                        view.checkout.setText("Check OUT");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.checkout.setText(finalTemp);
                                p.setCheckouts(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.breakin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(p.getBreakins()){

                        /*
                        if(p.getBreakin().equals("null")){
                            view.breakin.setText("-");
                        }
                        else{
                            view.breakin.setText(p.getBreakin());
                        }*/
                    }
                    else {
                        p.setBreakins(true);
                        String temp = "";
                        temp = view.breakin.getText().toString();
                        view.breakin.setText("Break IN");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.breakin.setText(finalTemp);
                                p.setBreakins(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.breakout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    if(p.getBreakouts()){

                        /*
                        if(p.getBreakout().equals("null")){
                            view.breakout.setText("-");
                        }
                        else{
                            view.breakout.setText(p.getBreakout());
                        }*/
                    }
                    else {
                        p.setBreakouts(true);
                        String temp = "";
                        temp = view.breakout.getText().toString();
                        view.breakout.setText("Break OUT");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.breakout.setText(finalTemp);
                                p.setBreakouts(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            // void recycling view
            if(p.expanded){
                view.lyt_expand.setVisibility(View.VISIBLE);
            } else {
                view.lyt_expand.setVisibility(View.GONE);
            }
            Tools.toggleArrow(p.expanded, view.bt_expand, false);

        }
    }

    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}