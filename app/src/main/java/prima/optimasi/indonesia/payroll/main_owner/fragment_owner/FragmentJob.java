package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import prima.optimasi.indonesia.payroll.main_owner.adapter_owner.AdapterListSectionedjob;
import prima.optimasi.indonesia.payroll.objects.listjob;
import prima.optimasi.indonesia.payroll.objects.listkaryawanaktivitas;
import prima.optimasi.indonesia.payroll.utils.ItemAnimation;
import prima.optimasi.indonesia.payroll.utils.Tools;
import prima.optimasi.indonesia.payroll.widget.SpacingItemDecoration;

import static android.app.Activity.RESULT_OK;

public class FragmentJob extends Fragment {

    RecyclerView history;
    TextView dt1,dt2;

    ProgressDialog dialog;

    String[] options = new String[]{"Approval Cuti","Approval Dinas","Approval Dirumahkan","Approval Izin","Approval Golongan","Approval Karyawan","Approval Pinjaman","Approval Pdm","Approval Punishment","Approval Reward"};

    AdapterListSectionedjob adapter;

    List<listjob> titles = new ArrayList<>();

    RecyclerView recyclerall;

    SwipeRefreshLayout refresh;

    CoordinatorLayout parent_view;

    MaterialSpinner pilihan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        generator.jobcount = 0;
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_job, container, false);


        recyclerall = rootView.findViewById(R.id.recycler_alljob);

        refresh = rootView.findViewById(R.id.pulljob);

        parent_view = rootView.findViewById(R.id.coordinator);

        dialog = new ProgressDialog(getActivity());

        dialog.setTitle("Loading Jobs");

        dialog.setCancelable(false);

        dialog.show();

        dialog.setMessage("Loading Job (0/10)");



        for (int i =0 ; i < 10;i++){
            listjob approval = new listjob();

            if(i==0){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==1){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==2){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==3){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==4){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==5){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==6){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==7){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==8){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==9){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            titles.add(approval);
        }

        adapter = new AdapterListSectionedjob(getActivity(),titles,ItemAnimation.FADE_IN,dialog,null,null);

        recyclerall.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerall.setHasFixedSize(true);
        recyclerall.setNestedScrollingEnabled(false);
        recyclerall.setAdapter(adapter);



        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = null;
                titles.clear();

                dialog = new ProgressDialog(getActivity());

                dialog.setTitle("Loading Jobs");

                dialog.setCancelable(false);

                dialog.show();

                dialog.setMessage("Loading Job (0/10)");



                for (int i =0 ; i < 10;i++){
                    listjob approval = new listjob();

                    if(i==0){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==1){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==2){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==3){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==4){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==5){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==6){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==7){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==8){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    else if(i==9){
                        approval.setSection(true);
                        approval.setSectionname(options[i]);
                    }
                    titles.add(approval);
                }

                adapter = new AdapterListSectionedjob(getActivity(),titles,ItemAnimation.FADE_IN,dialog,null,null);
                adapter.notifyDataSetChanged();
                recyclerall.setAdapter(adapter);

                refresh.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void refresh(){
        adapter = null;
        titles.clear();

        dialog = new ProgressDialog(getActivity());

        dialog.setTitle("Loading Jobs");

        dialog.setCancelable(false);

        dialog.show();

        dialog.setMessage("Loading Job (0/10)");



        for (int i =0 ; i < 10;i++){
            listjob approval = new listjob();

            if(i==0){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==1){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==2){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==3){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==4){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==5){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==6){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==7){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==8){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            else if(i==9){
                approval.setSection(true);
                approval.setSectionname(options[i]);
            }
            titles.add(approval);
        }

        adapter = new AdapterListSectionedjob(getActivity(),titles,ItemAnimation.FADE_IN,dialog,null,null);
        adapter.notifyDataSetChanged();
        recyclerall.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1006){
            if(resultCode==RESULT_OK){
                refresh();
            }
        }
    }

    /*

    approvalcuti
    approvaldinas
    approvaldirumahkan
    approvalizin

    approvalgolongan
    approvalkaryawan
    approvalpinjaman
    approvalpdm
    approvalpunishment
    approvalreward

            */


}
