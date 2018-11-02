package prima.optimasi.indonesia.payroll.main_hrd.fragment_hrd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.core.generator;
import prima.optimasi.indonesia.payroll.universal.viewkaryawan;
import prima.optimasi.indonesia.payroll.utils.CircleTransform;
import prima.optimasi.indonesia.payroll.utils.previewimage;

public class FragmentProfil extends Fragment {


    TextView templahir,tanggallahir,nama,jabatan,telepon,hp,alamat,email,agama,pend,gaji,status;
    TextView scount,icount,acount;
    CircularImageView image;
    SharedPreferences prefs;
    CoordinatorLayout parent_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_profil, container, false);

        prefs = getActivity().getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
        parent_view=rootView.findViewById(R.id.parent_view);
        image = rootView.findViewById(R.id.karimage);

        templahir = rootView.findViewById(R.id.kartempatlahir);
        tanggallahir = rootView.findViewById(R.id.kartanggallahir);
        nama = rootView.findViewById(R.id.karname);
        jabatan = rootView.findViewById(R.id.karjob);
        telepon = rootView.findViewById(R.id.kartel);
        hp = rootView.findViewById(R.id.karhp);
        alamat = rootView.findViewById(R.id.karala);
        email = rootView.findViewById(R.id.karemail);
        agama = rootView.findViewById(R.id.karagama);
        pend = rootView.findViewById(R.id.karpend);
        gaji = rootView.findViewById(R.id.kargaji);
        status = rootView.findViewById(R.id.karstatus);

        retrivekaryawan kar=new retrivekaryawan(getActivity(),prefs.getString("id",""));
        kar.execute();
        return rootView;
    }

    private class retrivekaryawan extends AsyncTask<Void, Integer, String>
    {
        String response = "";
        String error = "";
        String username=  "" ;
        String password = "" ;
        SharedPreferences prefs ;
        JSONObject result = null ;
        Context ctx;
        ProgressDialog dialog ;
        String urldata = generator.listemployeeurl;
        String passedid = "" ;

        public retrivekaryawan(Context context,String id)
        {
            ctx=context;
            prefs = context.getSharedPreferences("poipayroll",Context.MODE_PRIVATE);
            dialog = new ProgressDialog(context);
            this.username = generator.username;
            this.password = generator.password;
            passedid = id;
            this.error = error ;
        }

        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            this.dialog.show();
            super.onPreExecute();
            this.dialog.setMessage("Getting Data...");
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            try {
                this.dialog.setMessage("Loading Data...");

                JSONObject jsonObject;

                try {
                    OkHttpClient client = new OkHttpClient();



                    Request request = new Request.Builder()
                            .header("Authorization",prefs.getString("Authorization",""))
                            .url(urldata+"/"+passedid)
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
                this.dialog.dismiss();
                Log.e("doInBackground: ", "IO Exception" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Error IOException";
            } catch (NullPointerException e) {
                this.dialog.dismiss();
                Log.e("doInBackground: ", "null data" + e.getMessage());
                generator.jsondatalogin = null;
                response = "Please check Connection and Server";
            } catch (Exception e) {
                this.dialog.dismiss();
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

                        DecimalFormat formatter = new DecimalFormat("###,###,###.00");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        JSONArray pengsarray = result.getJSONArray("row");
                        final JSONObject obj = pengsarray.getJSONObject(0);
                        templahir.setText(obj.getString("tempat_lahir"));

                        SimpleDateFormat parsed = new SimpleDateFormat("yyyy-MM-dd");

                        Date temp = parsed.parse(obj.getString("tgl_lahir"));

                        parsed = new SimpleDateFormat("dd MMMM yyyy");

                        tanggallahir.setText(parsed.format(temp));
                        nama.setText(obj.getString("nama"));
                        jabatan.setText(obj.getString("jabatan"));
                        telepon.setText(obj.getString("telepon"));
                        hp.setText(obj.getString("no_wali"));
                        alamat.setText(obj.getString("alamat"));
                        email.setText(obj.getString("email"));
                        agama.setText(obj.getString("agama"));
                        pend.setText(obj.getString("pendidikan"));
                        gaji.setText("Rp "+ formatter.format(obj.getDouble("gaji")));
                        status.setText(obj.getString("status_kerja"));

                        Picasso.get().load(generator.profileurl+"/"+obj.getString("foto")).transform(new CircleTransform()).into(image);

                        final Bitmap[] bm = {null};

                        Picasso.get().load(generator.profileurl+"/"+obj.getString("foto")).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                bm[0] = bitmap;
                                image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent a = new Intent(getActivity(),previewimage.class);
                                        Bitmap bm1= null;
                                        bm1 = bm[0];
                                        generator.tempbitmap = bm1;
                                        startActivity(a);

                                    }
                                });
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                        Log.e(TAG, "onPostExecute: "+ generator.profileurl+"/"+obj.getString("foto") );

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

            if(this.dialog.isShowing()){
                dialog.dismiss();
            }


            Log.d(TAG + " onPostExecute", "" + result1);
        }
    }
}
