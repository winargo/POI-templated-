package prima.optimasi.indonesia.payroll.main_karyawan.adapter;

import android.app.Activity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.Stopwatch;
import prima.optimasi.indonesia.payroll.objects.logabsensi_karyawan;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class Adapter_absensi_karyawan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    public Adapter_absensi_karyawan(Context context, List<logabsensi_karyawan> items) {
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
            else if(view.checkout.getText().equals("-")){
                Stopwatch timer = new Stopwatch();
                final int REFRESH_RATE = 1000;


                Timer T = new Timer();
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) ctx).runOnUiThread(new Runnable() {
                            public void run() {
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

                                Date date1 = null;
                                Date date2 = null;

                                try {

                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(new Date());

                                    final int year = cal.get(Calendar.YEAR);
                                    final int month = cal.get(Calendar.MONTH) + 1;
                                    final int day = cal.get(Calendar.DAY_OF_MONTH);
                                    String[] spliter = view.checkin.getText().toString().split(":");
                                    final int hour = Integer.valueOf(spliter[0]);
                                    final int minute = Integer.valueOf(spliter[1]);
                                    final int second = Integer.valueOf(spliter[2]);

                                    String datadate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                                    date1 = format1.parse(datadate);
                                    date2 = new Date();

                                    long difference = date2.getTime() - date1.getTime();
                                    if (difference < 0) {
                                        Date dateMax = format.parse("24:00:00");
                                        Date dateMin = format.parse("00:00:00");
                                        difference = (dateMax.getTime() - date1.getTime()) + (date2.getTime() - dateMin.getTime());
                                    }
                                    int days = (int) (difference / (1000 * 60 * 60 * 24));
                                    int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                    int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                                    //hours = hours+(days*24);

                                    int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / 1000;

                                    view.jamkerja.setText("Jam Kerja Sementara : " + hours + " Jam " + min + " Menit " + sec + " Detik");


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //view.txt_totaljam.setText("Total Jam Kerja : " + " Total Jam Tidak Tersedia");
                            }
                        });
                    }
                }, 1000, 1000);
            }
            else {
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
                    if(!p.getCheckposss()){
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
                    if(!p.getCheckins()){
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
                    if(!p.getCheckouts()){
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
                    if(!p.getBreakins()){
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
                    if(!p.getBreakouts()){
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