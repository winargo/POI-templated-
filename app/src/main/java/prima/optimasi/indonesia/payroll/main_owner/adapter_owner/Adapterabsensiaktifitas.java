package prima.optimasi.indonesia.payroll.main_owner.adapter_owner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawanaktivitas;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.utils.ViewAnimation;

public class Adapterabsensiaktifitas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<listkaryawanaktivitas> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;
    private int animation_type = 0;


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, listkaryawan obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public Adapterabsensiaktifitas(Context context, List<listkaryawanaktivitas> items, int animation) {
        this.items = items;
        ctx = context;
        this.animation_type = animation;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView image;
        public TextView name;
        public TextView jabatan;
        public ImageButton arrow;

        public View lyt_parent;

        public TextView txt_totaljam;

        public TextView txt_checkpos;
        public TextView txt_checkin;
        public TextView txt_checkout;
        public TextView txt_breakout;
        public TextView txt_breakin;
        public TextView txt_extrain;
        public TextView txt_extraout;
        public NestedScrollView nested_scroll_view;

        public ImageButton bt_toggle_text;
        public Button bt_hide_text;
        public View lyt_expand_text;


        public OriginalViewHolder(View v) {
            super(v);

            txt_totaljam  = v.findViewById(R.id.text_jamkerjatotal_karyawan);

            arrow = v.findViewById(R.id.bt_toggle_text);
            image = v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            jabatan = (TextView) v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);

            bt_toggle_text = (ImageButton) v.findViewById(R.id.bt_toggle_text);
            bt_hide_text = (Button) v.findViewById(R.id.bt_hide_text);
            lyt_expand_text = (View) v.findViewById(R.id.lyt_expand_text);

            nested_scroll_view = (NestedScrollView) v.findViewById(R.id.nested_scroll_view);

            txt_checkpos = v.findViewById(R.id.activity_check_pos);
            txt_checkin = v.findViewById(R.id.activity_check_in);
            txt_checkout = v.findViewById(R.id.activity_check_out);
            txt_breakin = v.findViewById(R.id.activity_break_in);
            txt_breakout = v.findViewById(R.id.activity_break_out);
            txt_extrain = v.findViewById(R.id.activity_extra_in);
            txt_extraout = v.findViewById(R.id.activity_extra_out);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_absensi, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listkaryawanaktivitas obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            view.name.setText(obj.getNama());
            view.jabatan.setText(obj.getDesc() + " - " + obj.getJabatan());
            view.txt_checkout.setText(obj.getCheckout());
            view.txt_checkin.setText(obj.getCheckin());
            view.txt_breakout.setText(obj.getBreakout());
            view.txt_breakin.setText(obj.getBreakin());
            view.txt_extrain.setText(obj.getExtrain());
            view.txt_extraout.setText(obj.getExtraout());

            view.txt_checkpos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(obj.getCheckposss()){

                    }
                    else {
                        obj.setCheckposss(true);
                        String temp = "";
                        temp = view.txt_checkpos.getText().toString();
                        view.txt_checkpos.setText("Check POS");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.txt_checkpos.setText(finalTemp);
                                obj.setCheckposss(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.txt_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(obj.getCheckouts()){

                    }
                    else {
                        obj.setCheckouts(true);
                        String temp = "";
                        temp = view.txt_checkout.getText().toString();
                        view.txt_checkout.setText("Check OUT");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.txt_checkout.setText(finalTemp);
                                obj.setCheckouts(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.txt_checkin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(obj.getCheckins()){

                    }
                    else {
                        obj.setCheckins(true);
                        String temp = "";
                        temp = view.txt_checkin.getText().toString();
                        view.txt_checkin.setText("Check IN");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.txt_checkin.setText(finalTemp);
                                obj.setCheckins(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.txt_breakin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(obj.getBreakins()){

                    }
                    else {
                        obj.setBreakins(true);
                        String temp = "";
                        temp = view.txt_breakin.getText().toString();
                        view.txt_breakin.setText("Break IN");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.txt_breakin.setText(finalTemp);
                                obj.setBreakins(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            view.txt_breakout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(obj.getBreakouts()){

                    }
                    else {
                        obj.setBreakouts(true);
                        String temp = "";
                        temp = view.txt_breakout.getText().toString();
                        view.txt_breakout.setText("Break OUT");
                        final Handler handler = new Handler();
                        String finalTemp = temp;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.txt_breakout.setText(finalTemp);
                                obj.setBreakouts(false);
                                //Do something after 100ms
                            }
                        }, 2000);
                    }
                }
            });

            if(obj.getImagelink().equals("")){
                Picasso.get().load("http://www.racemph.com/wp-content/uploads/2016/09/profile-image-placeholder.png").into(view.image);
                //Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQM1rF7DteSU8zDGipqBKZgmLHv7qIAqV8WwUWaqr0SDbTj5Ht9lQ").into(view.image);
            }

            else{
                Tools.displayImageOriginal(ctx, view.image, obj.getImagelink());
            }
            view.lyt_expand_text.setVisibility(View.GONE);

            view.bt_toggle_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    boolean show = toggleArrow(view1);
                    if (show) {
                        ViewAnimation.expand(view.lyt_expand_text, new ViewAnimation.AnimListener() {
                            @Override
                            public void onFinish() {
                                Tools.nestedScrollTo(view.nested_scroll_view, view.lyt_expand_text);
                            }
                        });
                    } else {
                        ViewAnimation.collapse(view.lyt_expand_text);
                    }
                }
            });

            view.bt_hide_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    boolean show = toggleArrow(view.bt_toggle_text);
                    if (show) {
                        ViewAnimation.expand(view.lyt_expand_text, new ViewAnimation.AnimListener() {
                            @Override
                            public void onFinish() {
                                Tools.nestedScrollTo(view.nested_scroll_view,view.lyt_expand_text);
                            }
                        });
                    } else {
                        ViewAnimation.collapse(view.lyt_expand_text);
                    }
                }
            });

            if(!view.txt_checkin.getText().equals("-") && !view.txt_checkout.getText().equals("-")){
                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

                Date date1=null;
                Date date2=null;

                try {
                    date1 = format.parse(view.txt_checkin.getText().toString());
                    date2 = format.parse(view.txt_checkout.getText().toString());

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
                    view.txt_totaljam.setText("Total Jam Kerja : " + hours + " Jam " + min +" Menit");


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else if(!view.txt_checkin.getText().equals("-") && view.txt_checkout.getText().equals("-"))
            {
                Stopwatch timer = new Stopwatch();
                final int REFRESH_RATE = 1000;



                Timer T=new Timer();
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity)ctx).runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");

                                Date date1=null;
                                Date date2=null;

                                try {

                                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(new Date());

                                    final int year = cal.get(Calendar.YEAR);
                                    final int month = cal.get(Calendar.MONTH)+1;
                                    final int day = cal.get(Calendar.DAY_OF_MONTH);
                                    String[] spliter = view.txt_checkin.getText().toString().split(":");
                                    final int hour = Integer.valueOf(spliter[0]);
                                    final int minute = Integer.valueOf(spliter[1]);
                                    final int second = Integer.valueOf(spliter[2]);

                                    String datadate = year + "-"+month+"-"+day+" "+ hour+ ":"+minute+":"+second;
                                    date1 = format1.parse(datadate);
                                    date2 = new Date();

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
                                    //hours = hours+(days*24);

                                    int sec = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours) - (1000*60*min)) / 1000;

                                    view.txt_totaljam.setText("Jam Kerja Sementara : " + hours + " Jam " + min +" Menit "+sec+" Detik");


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //view.txt_totaljam.setText("Total Jam Kerja : " + " Total Jam Tidak Tersedia");
                            }
                        });
                    }
                }, 1000, 1000);


                
                //view.txt_totaljam.setText("Total Jam Kerja : " + " Total Jam Tidak Tersedia");
            }
            else {
                view.txt_totaljam.setText("Total Jam Kerja : " + " Total Jam Tidak Tersedia");
            }

           /* try{
                if(obj.getImagelink().equals("")){

                }
            }
            catch (NullPointerException e){
                view.lyt_parent.setVisibility(View.GONE);
            }*/

            setAnimation(view.itemView, position);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }
    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

}