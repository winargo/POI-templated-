package prima.optimasi.indonesia.payroll.universal.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.universal.ModelObject;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private JSONArray pengsarray;
    private List<Integer> color;
    private List<String> colorName;
    TextView kartanggal, karizin, karsakit, karabsen;
    /*
    public ViewPagerAdapter(Context context, List<Integer> color, List<String> colorName) {
        this.context = context;
        this.color = color;
        this.colorName = colorName;
    }
    */
    public ViewPagerAdapter(Context context, JSONArray pengsarray) {
        this.context = context;
        this.pengsarray=pengsarray;
    }
    /*@Override
    public int getCount() {
        return color.size();
    }*/

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ModelObject modelObject=ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        for (int j = 0; j < pengsarray.length(); j++) {
            collection.addView(layout);
            kartanggal = collection.findViewById(R.id.karytanggal);
            karizin = collection.findViewById(R.id.karyizin);
            karsakit = collection.findViewById(R.id.karysakit);
            karabsen = collection.findViewById(R.id.karyabsen);
            try {
                Log.e("Error", "onPostExecute: " + pengsarray);
                for (int i = 0; i < pengsarray.length(); i++) {

                    final JSONObject obj = pengsarray.getJSONObject(i);
                    String tgl_izin=obj.getString("tgl_izin").substring(5,7);

                    kartanggal.setText("Bulan "+tgl_izin);
                    karizin.setText("-"+ pengsarray.length());
                    karsakit.setText("-");
                    karabsen.setText("-");
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.e("Error", "onPostExecute: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error", "onPostExecute: " + e.getMessage());
            }
        }
        return layout;
        /*
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_keterangan_karyawan, null);

        TextView karizin = view.findViewById(R.id.karizin);
        TextView karsakit = view.findViewById(R.id.karsakit);
        TextView karabsen = view.findViewById(R.id.karabsen);


        karizin.setText(""+position);
        karsakit.setText(""+position);
        karabsen.setText(""+position);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
        */
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];
        return context.getString(customPagerEnum.getTitleResId());
    }
    /*
    public final List<Fragment> mFragmentlist=new ArrayList<>();
    public final List<String> mFragmenttitlelist=new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager){
        super(manager);
    }

    @Override
    public Fragment getItem(int position){
        return mFragmentlist.get(position);
    }

    @Override
    public int getCount(){
        return mFragmentlist.size();
    }
    public void addFragment(Fragment fragment, String title){
        mFragmentlist.add(fragment);
        mFragmenttitlelist.add(title);
    }
    */
}

