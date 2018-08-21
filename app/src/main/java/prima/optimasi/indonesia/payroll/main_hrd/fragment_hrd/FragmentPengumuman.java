package prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity.MainMenu;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.okhttpclass;

public class FragmentPengumuman extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_pengumuman, container, false);

        getpengumuman exec = new getpengumuman(getActivity());
        exec.execute();

        return rootView;
    }

    private class getpengumuman extends AsyncTask<String,String,String>{
        private Boolean issuccess = false ;
        private Context context;
        private JSONObject reponse=null;
        private ProgressDialog dialog=null;

    public getpengumuman(Context contex){
        context = contex;
        dialog = new ProgressDialog(contex);
        dialog.setMessage("Memuat Pengumuman...");
        dialog.show();
    }
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... strings) {

            try {
                okhttpclass httpconnect = new okhttpclass();

                reponse = httpconnect.getJsonurl(generator.pengumumanurl);

                issuccess = true;
            }catch (Exception e){
                Log.e("Pengumumanhrd",e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(dialog.isShowing()){
                dialog.dismiss();
            }
            if(issuccess)
            {
                Log.e("Success ",reponse.toString() );
            }
            else {
                Log.e("Failed","");
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
