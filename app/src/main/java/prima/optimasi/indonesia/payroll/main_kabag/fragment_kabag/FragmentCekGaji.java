package prima.optimasi.indonesia.payroll.main_kabag.fragment_kabag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import prima.optimasi.indonesia.payroll.R;

public class FragmentCekGaji extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_cek_gaji, container, false);

        return rootView;
    }
}
