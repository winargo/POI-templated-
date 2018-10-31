package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListDaftarAbsensi;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedKontrakKerja;
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedsakit;
import prima.optimasi.indonesia.payroll.main_owner.report.owner_sakit;
import prima.optimasi.indonesia.payroll.objects.datacuti;
import prima.optimasi.indonesia.payroll.objects.listkaryawandaftarabsensi;
import prima.optimasi.indonesia.payroll.objects.listkaryawankontrakkerja;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

public class FragmentHome extends Fragment {

    ImageButton expand, expand2, expand3, expandhabis;
    LinearLayout lyt_parent, lyt_parent2, lyt_parent3, lyt_parenthabis;
    LinearLayout lyt_expand, lyt_expand2, lyt_expand3, lyt_expandhabis;
    TextView sisakontrakkerja, sisakontrakkerja2, sisakontrakkerja3, sisakontrakkerjahabis;
    List<listkaryawankontrakkerja> items;
    List<listkaryawandaftarabsensi> daftaritems;

    CoordinatorLayout parent_view;
    listkaryawankontrakkerja kontrakkerja;
    listkaryawandaftarabsensi daftarabsensi;
    AdapterListSectionedKontrakKerja adapter;
    AdapterListDaftarAbsensi adapterabsensi;
    RecyclerView recyclerView, recyclerView2, recyclerView3, recyclerViewhabis, recyclerViewdaftar;
    boolean expansi=false, expansi2=false, expansi3=false, expansihabis=false;
    int banyak1bulan=0, banyak2bulan=0,banyak3bulan=0, banyakhabis=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);
        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView2=rootView.findViewById(R.id.recyclerView2bulan);
        recyclerView3=rootView.findViewById(R.id.recyclerView3bulan);
        recyclerViewhabis=rootView.findViewById(R.id.recyclerViewhabiskontrak);
        recyclerViewdaftar=rootView.findViewById(R.id.recyclerViewdaftarabsensi);

        parent_view=rootView.findViewById(R.id.parent_view);
        lyt_parent=rootView.findViewById(R.id.lyt_parent);
        lyt_parent2=rootView.findViewById(R.id.lyt_parent2bulan);
        lyt_parent3=rootView.findViewById(R.id.lyt_parent3bulan);
        lyt_parenthabis=rootView.findViewById(R.id.lyt_parenthabiskontrak);

        sisakontrakkerja=rootView.findViewById(R.id.sisakontrakkerja);
        sisakontrakkerja2=rootView.findViewById(R.id.sisakontrakkerja2bulan);
        sisakontrakkerja3=rootView.findViewById(R.id.sisakontrakkerja3bulan);
        sisakontrakkerjahabis=rootView.findViewById(R.id.sisakontrakkerjahabiskontrak);

        expand=rootView.findViewById(R.id.bt_expand);
        expand2=rootView.findViewById(R.id.bt_expand2bulan);
        expand3=rootView.findViewById(R.id.bt_expand3bulan);
        expandhabis=rootView.findViewById(R.id.bt_expandhabiskontrak);

        lyt_expand=rootView.findViewById(R.id.lyt_expand);
        lyt_expand2=rootView.findViewById(R.id.lyt_expand2bulan);
        lyt_expand3=rootView.findViewById(R.id.lyt_expand3bulan);
        lyt_expandhabis=rootView.findViewById(R.id.lyt_expandhabiskontrak);

        retrivekontrakkerja kk=new retrivekontrakkerja(parent_view.getContext());
        kk.execute();

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(banyak1bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if(!expansi){
                        lyt_expand.setVisibility(View.VISIBLE);
                        expansi=true;
                        expand.setImageResource(R.drawable.ic_collapse_arrow);
                    }
                    else{
                        lyt_expand.setVisibility(View.GONE);
                        expansi=false;
                        expand.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }

            }
        });

        expand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak2bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi2) {
                        lyt_expand2.setVisibility(View.VISIBLE);
                        expansi2 = true;
                        expand2.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expand2.setVisibility(View.GONE);
                        expansi2 = false;
                        expand2.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        expand3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak3bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi3) {
                        lyt_expand3.setVisibility(View.VISIBLE);
                        expansi3 = true;
                        expand3.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expand3.setVisibility(View.GONE);
                        expansi3 = false;
                        expand3.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        expandhabis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyakhabis==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansihabis) {
                        lyt_expandhabis.setVisibility(View.VISIBLE);
                        expansihabis = true;
                        expandhabis.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expandhabis.setVisibility(View.GONE);
                        expansihabis = false;
                        expandhabis.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak1bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi) {
                        lyt_expand.setVisibility(View.VISIBLE);
                        expansi = true;
                        expand.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expand.setVisibility(View.GONE);
                        expansi = false;
                        expand.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        lyt_parent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak2bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi2) {
                        lyt_expand2.setVisibility(View.VISIBLE);
                        expansi2 = true;
                        expand2.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expand2.setVisibility(View.GONE);
                        expansi2 = false;
                        expand2.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        lyt_parent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyak3bulan==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansi3) {
                        lyt_expand3.setVisibility(View.VISIBLE);
                        expansi3 = true;
                        expand3.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expand3.setVisibility(View.GONE);
                        expansi3 = false;
                        expand3.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        lyt_parenthabis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(banyakhabis==0){
                    Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                }else {
                    if (!expansihabis) {
                        lyt_expandhabis.setVisibility(View.VISIBLE);
                        expansihabis = true;
                        expandhabis.setImageResource(R.drawable.ic_collapse_arrow);
                    } else {
                        lyt_expandhabis.setVisibility(View.GONE);
                        expansihabis = false;
                        expandhabis.setImageResource(R.drawable.ic_expand_arrow);
                    }
                }
            }
        });

        return rootView;
    }
    private class retrivekontrakkerja extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kontrakkerja1bulanurl;
        String passeddata = "" ;

        public retrivekontrakkerja(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        Log.e(TAG, "onPostExecute: " + pengsarray);
                        String status="";
                        //String tempcall="";
                        int sisa=0;
                        boolean first=true;
                        if(pengsarray.length()==0){
                            sisakontrakkerja.setText("(0 orang)");
                            Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < pengsarray.length(); i++) {
                            status=pengsarray.getString(i);

                            if (!status.equals("null")){


                            JSONObject obj = pengsarray.getJSONObject(i);
                            sisa=Integer.parseInt(obj.getString("sisa"));

                            /*
                            if(first){

                                //if(tempcall.equals("")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 1 bulan");
                                    kontrakkerja.setSection(false);
                                    //tempcall="1bulan";
                                    items.add(kontrakkerja);
                                    first=false;
                                    Log.e(TAG, "onPostExecute: Section1 berhasil" );
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                //}
                                /*
                                else if(!tempcall.equals("1bulan")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 1 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall = sisa;
                                    items.add(kontrakkerja);
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }
                            }*/
                            /*
                            else if(sisa<60 && sisa>30){
                                kontrakkerja = new listkaryawankontrakkerja();
                                kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                kontrakkerja.setSection(true);

                                items.add(kontrakkerja);
                            }*/
                            banyak1bulan++;
                            if(i+1==pengsarray.length()){
                                sisakontrakkerja.setText("("+banyak1bulan+" orang)");

                            }
                            kontrakkerja=new listkaryawankontrakkerja();
                            kontrakkerja.setKontrakkerja("Kontrak kerja "+sisa+" hari");
                            kontrakkerja.setSection(true);
                            //kontrakkerja.setSisa("("+banyak+" orang)");
                            //kar.setIskar(obj.getString("id"));
                            kontrakkerja.setImagelink(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kontrakkerja.getImagelink() );

                            //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                            kontrakkerja.setNama(obj.getString("nama"));
                            items.add(kontrakkerja);

                            //kontrakkerja.setJabatan(obj.getString("jabatan"));

                            /*
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");


                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            */
                            //kar.setJabatan(obj.getString("jabatan"));

                            /*
                            kontrakkerja.setSection(false);
                            kontrakkerja.setImagelink(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kontrakkerja.getImagelink() );
                            kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                            kontrakkerja.setNama(obj.getString("nama"));
                            kontrakkerja.setJabatan(obj.getString("jabatan"));
                            kontrakkerja.setKontrakkerja(obj.getString("kontrak_kerja"));

                            */

                            }


                        }
                        /*
                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));


                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);*/
                        /*
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(!tempcall.equals(obj.getString("tanggal").substring(0,10))){
                                if(tempcall.equals("")){
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                                else{
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                            }
                            datacuti kar = new datacuti();
                            kar.setIssection(false);
                            //kar.setIskar(obj.getString("id"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kar.getImageurl() );

                            kar.setNama(obj.getString("nama"));
                            kar.setJabatan(obj.getString("jabatan"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            //kar.setJabatan(obj.getString("jabatan"));
                            items.add(kontrakkerja);


                        }

                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        */


                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));


                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        retrivekontrakkerja2bulan kar = new retrivekontrakkerja2bulan(getActivity());
                        kar.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }

    private class retrivekontrakkerja2bulan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kontrakkerja2bulanurl;
        String passeddata = "" ;

        public retrivekontrakkerja2bulan(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        Log.e(TAG, "onPostExecute: " + pengsarray);
                        String status="";
                        //String tempcall="";
                        int sisa=0;
                        boolean first=true;
                        if(pengsarray.length()==0){
                            sisakontrakkerja2.setText("(0 orang)");
                            Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < pengsarray.length(); i++) {
                            status=pengsarray.getString(i);

                            if (!status.equals("null")) {


                                JSONObject obj = pengsarray.getJSONObject(i);
                                sisa = Integer.parseInt(obj.getString("sisa"));
                                /*
                                if (first && sisa > 30) {

                                    //if(tempcall.equals("")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall="1bulan";
                                    items.add(kontrakkerja);
                                    first = false;
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                                /*
                                else if(!tempcall.equals("1bulan")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 1 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall = sisa;
                                    items.add(kontrakkerja);
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }*/

                            /*
                            else if(sisa<60 && sisa>30){
                                kontrakkerja = new listkaryawankontrakkerja();
                                kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                kontrakkerja.setSection(true);

                                items.add(kontrakkerja);
                            }*/


                                if(sisa>30 && sisa<=60){
                                    banyak2bulan++;
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setSection(true);
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja "+sisa+" hari");
                                    //kar.setIskar(obj.getString("id"));
                                    kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));

                                    Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                    //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                    kontrakkerja.setNama(obj.getString("nama"));
                                    //kontrakkerja.setJabatan(obj.getString("jabatan"));
                                    items.add(kontrakkerja);
                                }
                                if(i+1==pengsarray.length()){
                                    sisakontrakkerja2.setText("("+banyak2bulan+" orang)");

                                }


                            /*
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");


                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            */
                                //kar.setJabatan(obj.getString("jabatan"));

                            /*
                            kontrakkerja.setSection(false);
                            kontrakkerja.setImagelink(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kontrakkerja.getImagelink() );
                            kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                            kontrakkerja.setNama(obj.getString("nama"));
                            kontrakkerja.setJabatan(obj.getString("jabatan"));
                            kontrakkerja.setKontrakkerja(obj.getString("kontrak_kerja"));

                            */
                            }




                        }
                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView2.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));


                        recyclerView2.setHasFixedSize(true);
                        recyclerView2.setAdapter(adapter);

                        retrivekontrakkerja3bulan kar = new retrivekontrakkerja3bulan(getActivity());
                        kar.execute();
                        /*
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(!tempcall.equals(obj.getString("tanggal").substring(0,10))){
                                if(tempcall.equals("")){
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                                else{
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                            }
                            datacuti kar = new datacuti();
                            kar.setIssection(false);
                            //kar.setIskar(obj.getString("id"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kar.getImageurl() );

                            kar.setNama(obj.getString("nama"));
                            kar.setJabatan(obj.getString("jabatan"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            //kar.setJabatan(obj.getString("jabatan"));
                            items.add(kontrakkerja);


                        }

                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                        */


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
    private class retrivekontrakkerja3bulan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kontrakkerja3bulanurl;
        String passeddata = "" ;

        public retrivekontrakkerja3bulan(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        Log.e(TAG, "onPostExecute: " + pengsarray);
                        String status="";
                        //String tempcall="";
                        int sisa=0;
                        boolean first=true;
                        if(pengsarray.length()==0){
                            sisakontrakkerja3.setText("(0 orang)");
                            Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < pengsarray.length(); i++) {
                            status=pengsarray.getString(i);

                            if (!status.equals("null")) {


                                JSONObject obj = pengsarray.getJSONObject(i);
                                sisa = Integer.parseInt(obj.getString("sisa"));
                                /*
                                if (first && sisa > 30) {

                                    //if(tempcall.equals("")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall="1bulan";
                                    items.add(kontrakkerja);
                                    first = false;
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }*/
                                /*
                                else if(!tempcall.equals("1bulan")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 1 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall = sisa;
                                    items.add(kontrakkerja);
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }*/

                            /*
                            else if(sisa<60 && sisa>30){
                                kontrakkerja = new listkaryawankontrakkerja();
                                kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                kontrakkerja.setSection(true);

                                items.add(kontrakkerja);
                            }*/
                                if(sisa>60  && sisa<=90){
                                    banyak3bulan++;
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setSection(true);
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja "+sisa+" hari");
                                    //kar.setIskar(obj.getString("id"));
                                    kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));

                                    Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                    //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                    kontrakkerja.setNama(obj.getString("nama"));
                                    //kontrakkerja.setJabatan(obj.getString("jabatan"));
                                    items.add(kontrakkerja);
                                }
                                if(i+1==pengsarray.length()){
                                    sisakontrakkerja3.setText("("+banyak3bulan+" orang)");

                                }


                            /*
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");


                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            */
                                //kar.setJabatan(obj.getString("jabatan"));

                            /*
                            kontrakkerja.setSection(false);
                            kontrakkerja.setImagelink(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kontrakkerja.getImagelink() );
                            kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                            kontrakkerja.setNama(obj.getString("nama"));
                            kontrakkerja.setJabatan(obj.getString("jabatan"));
                            kontrakkerja.setKontrakkerja(obj.getString("kontrak_kerja"));

                            */
                            }




                        }

                        /*
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(!tempcall.equals(obj.getString("tanggal").substring(0,10))){
                                if(tempcall.equals("")){
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                                else{
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                            }
                            datacuti kar = new datacuti();
                            kar.setIssection(false);
                            //kar.setIskar(obj.getString("id"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kar.getImageurl() );

                            kar.setNama(obj.getString("nama"));
                            kar.setJabatan(obj.getString("jabatan"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            //kar.setJabatan(obj.getString("jabatan"));
                            items.add(kontrakkerja);


                        }

                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        */
                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView3.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));


                        recyclerView3.setHasFixedSize(true);
                        recyclerView3.setAdapter(adapter);
                        retrivekontrakkerjahabis kar = new retrivekontrakkerjahabis(getActivity());
                        kar.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
    private class retrivekontrakkerjahabis extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kontrakkerjahabisurl;
        String passeddata = "" ;

        public retrivekontrakkerjahabis(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        Log.e(TAG, "onPostExecute: " + pengsarray);
                        String status="";
                        //String tempcall="";
                        int sisa=0;
                        boolean first=true;
                        if(pengsarray.length()==0){
                            sisakontrakkerja3.setText("(0 orang)");
                            Snackbar.make(parent_view,"Tidak ada karyawan",Snackbar.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < pengsarray.length(); i++) {
                            status=pengsarray.getString(i);

                            if (!status.equals("null")) {


                                JSONObject obj = pengsarray.getJSONObject(i);
                                sisa = Integer.parseInt(obj.getString("sisa"));
                                /*
                                if (first && sisa > 30) {

                                    //if(tempcall.equals("")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall="1bulan";
                                    items.add(kontrakkerja);
                                    first = false;
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }*/
                                /*
                                else if(!tempcall.equals("1bulan")){
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 1 bulan");
                                    kontrakkerja.setSection(true);
                                    //tempcall = sisa;
                                    items.add(kontrakkerja);
                                    //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }*/

                            /*
                            else if(sisa<60 && sisa>30){
                                kontrakkerja = new listkaryawankontrakkerja();
                                kontrakkerja.setKontrakkerja("Kontrak Kerja kurang dari 2 bulan");
                                kontrakkerja.setSection(true);

                                items.add(kontrakkerja);
                            }*/
                                if(sisa<0){
                                    banyakhabis++;
                                    kontrakkerja = new listkaryawankontrakkerja();
                                    kontrakkerja.setSection(true);
                                    kontrakkerja.setKontrakkerja("Kontrak Kerja sudah habis");
                                    //kar.setIskar(obj.getString("id"));
                                    kontrakkerja.setImagelink(generator.profileurl + obj.getString("foto"));

                                    Log.e(TAG, "image data" + kontrakkerja.getImagelink());

                                    //kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                                    kontrakkerja.setNama(obj.getString("nama"));
                                    //kontrakkerja.setJabatan(obj.getString("jabatan"));
                                    items.add(kontrakkerja);
                                }
                                if(i+1==pengsarray.length()){
                                    sisakontrakkerjahabis.setText("("+banyakhabis+" orang)");

                                }


                            /*
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");


                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            */
                                //kar.setJabatan(obj.getString("jabatan"));

                            /*
                            kontrakkerja.setSection(false);
                            kontrakkerja.setImagelink(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kontrakkerja.getImagelink() );
                            kontrakkerja.setIskar(obj.getString("kode_karyawan"));
                            kontrakkerja.setNama(obj.getString("nama"));
                            kontrakkerja.setJabatan(obj.getString("jabatan"));
                            kontrakkerja.setKontrakkerja(obj.getString("kontrak_kerja"));

                            */
                            }




                        }

                        /*
                        String tempcall = "";

                        for (int i = 0; i < pengsarray.length(); i++) {
                            JSONObject obj = pengsarray.getJSONObject(i);
                            if(!tempcall.equals(obj.getString("tanggal").substring(0,10))){
                                if(tempcall.equals("")){
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                                else{
                                    datacuti kar = new datacuti();
                                    kar.setDatatgl(obj.getString("tanggal").substring(0,10));
                                    kar.setIssection(true);
                                    tempcall = obj.getString("tanggal").substring(0,10);
                                    items.add(kontrakkerja);
                                }
                            }
                            datacuti kar = new datacuti();
                            kar.setIssection(false);
                            //kar.setIskar(obj.getString("id"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            Log.e(TAG, "image data" + kar.getImageurl() );

                            kar.setNama(obj.getString("nama"));
                            kar.setJabatan(obj.getString("jabatan"));
                            kar.setImageurl(generator.profileurl+obj.getString("foto"));

                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

                            kar.setTglmulai(format2.format(format1.parse(obj.getString("tgl_sakit").substring(0,10))));
                            kar.setTglakhir(format2.format(format1.parse(obj.getString("akhir_sakit").substring(0,10))));
                            kar.setKeterangan(obj.getString("keterangans"));
                            kar.setHari(obj.getString("lama"));
                            //kar.setJabatan(obj.getString("jabatan"));
                            items.add(kontrakkerja);


                        }

                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                        */
                        adapter = new AdapterListSectionedKontrakKerja(getActivity(), items, ItemAnimation.LEFT_RIGHT);
                        recyclerViewhabis.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerViewhabis.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 3), true));


                        recyclerViewhabis.setHasFixedSize(true);
                        recyclerViewhabis.setAdapter(adapter);
                        retrivedaftarabsensi kar = new retrivedaftarabsensi(getActivity());
                        kar.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
    private class retrivedaftarabsensi extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        ProgressDialog dialog ;
        String urldata = generator.kontrakkerjahabisurl;
        String passeddata = "" ;

        public retrivedaftarabsensi(Context context)
        {
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            this.username = generator.username;
            this.password = generator.password;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            //this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                //this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
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
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                //this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                //this.dialog.dismiss();
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
                Log.e(TAG, "data json result" + result.toString());
                if (result != null) {
                    try {
                        items = new ArrayList<>();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("data");
                        Log.e(TAG, "onPostExecute: " + pengsarray);
                        String status="";
                        //String tempcall="";


                        for (int i = 0; i < pengsarray.length(); i++) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onPostExecute: " + e.getMessage());
                    }


                } else {
                    Snackbar.make(parent_view, "Terjadi Kesalahan Koneksi" + result, Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception E){
                E.printStackTrace();
                Log.e(TAG, "onPostExecute: "+E.getMessage().toString() );
                Snackbar.make(parent_view,E.getMessage().toString(),Snackbar.LENGTH_SHORT).show();
            }

            /*
            if(this.dialog.isShowing()){
                dialog.dismiss();
            }*/


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
}
