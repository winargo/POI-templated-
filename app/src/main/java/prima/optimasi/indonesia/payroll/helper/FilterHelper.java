package prima.optimasi.indonesia.payroll.helper;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapterkaryawan;
import prima.optimasi.indonesia.payroll.main_kabag.adapter.Adapterviewkaryawan;
import prima.optimasi.indonesia.payroll.objects.listkaryawan;

public class FilterHelper extends Filter {
    static List<listkaryawan> currentList;
    static Adapterviewkaryawan adapter;


    public static FilterHelper newInstance(List<listkaryawan> currentList, Adapterviewkaryawan adapter) {
        FilterHelper.adapter=adapter;
        FilterHelper.currentList=currentList;
        return new FilterHelper();
    }

    /*
    - Perform actual filtering.
     */
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();

        if(constraint.length()>0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();

            //HOLD FILTERS WE FIND
            List<listkaryawan> foundFilters=new ArrayList<>();

            String galaxy;

            //ITERATE CURRENT LIST
            for (int i=0;i<currentList.size();i++)
            {
                galaxy= currentList.get(i).getNama();

                //SEARCH
                if(galaxy.toUpperCase().contains(constraint))
                {
                    //ADD IF FOUND
                    foundFilters.add(currentList.get(i));
                }
            }

            //SET RESULTS TO FILTER LIST
            filterResults.count=foundFilters.size();
            filterResults.values=foundFilters;
        }else
        {
            //NO ITEM FOUND.LIST REMAINS INTACT
            filterResults.count=currentList.size();
            filterResults.values=currentList;
        }

        //RETURN RESULTS
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.setItems((List<listkaryawan>) filterResults.values);
        adapter.notifyDataSetChanged();

    }
}