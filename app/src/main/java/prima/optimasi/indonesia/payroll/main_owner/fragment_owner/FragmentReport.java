package prima.optimasi.indonesia.payroll.main_owner.fragment_owner;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.adapter.AdapterListBasic;
import prima.optimasi.indonesia.payroll.data.DataGenerator;
import prima.optimasi.indonesia.payroll.model.People;
import prima.optimasi.indonesia.payroll.objects.listreport;
import prima.optimasi.indonesia.payroll.utils.Tools;

public class FragmentReport extends Fragment {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterListBasic mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_report, container, false);

        parent_view = rootView.findViewById(R.id.laporansnack);

        initComponent(rootView);

        return rootView;
    }

    private void initComponent(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        List<listreport> items =new ArrayList<>();

        listreport izin = new listreport();
        izin.setJudul("Izin");
        izin.setDeskripsi("Laporan Dan Statistik Izin Karyawan");

        items.add(izin);

        izin = new listreport();
        izin.setJudul("Cuti");
        izin.setDeskripsi("Laporan dan Statistik Cuti Karyawan");

        items.add(izin);
        izin = new listreport();
        izin.setJudul("Sakit");
        izin.setDeskripsi("Laporan dan Statistik Karyawan yang Sakit");

        items.add(izin);
        izin = new listreport();
        izin.setJudul("Pinjaman");
        izin.setDeskripsi("Laporan Dan Statistik Pinjaman Karyawan");

        items.add(izin);
        izin = new listreport();
        izin.setJudul("Penggajian");
        izin.setDeskripsi("Laporan Dan Statistik Pengajian Karyawan");

        items.add(izin);
        //set data and list adapter
        mAdapter = new AdapterListBasic(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListBasic.OnItemClickListener() {
            @Override
            public void onItemClick(View view, People obj, int position) {
                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
