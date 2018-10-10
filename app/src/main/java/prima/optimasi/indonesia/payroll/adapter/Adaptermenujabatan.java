package prima.optimasi.indonesia.payroll.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;

import static android.content.Context.MODE_PRIVATE;

public class Adaptermenujabatan extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public Adaptermenujabatan(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = LayoutInflater.from(_context);
            convertView = infalInflater.inflate(R.layout.item_menu_child, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_menu_parent, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        ImageView icon = (ImageView) convertView.findViewById(R.id.menu_icon);

        final ImageView expadingarrowdown = convertView.findViewById(R.id.arrow_icon);
        final ImageView expadingarrowup = convertView.findViewById(R.id.arrow_iconup);




        if(headerTitle.equals("Pengumuman")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_announcement_black_24dp);
        }else if(headerTitle.equals("Approval")){
            expadingarrowdown.setVisibility(View.VISIBLE);
            icon.setBackgroundResource(R.drawable.baseline_check_circle_outline_black_24dp);
        }else if(headerTitle.equals("Chart Kehadiran")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_pie_chart_black_24dp);
        }else if(headerTitle.equals("Log Absensi")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_pie_chart_black_24dp);
        }else if(headerTitle.equals("Total Gaji")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_monetization_on_black_24dp);
        }else if(headerTitle.equals("Karyawan")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_person_black_24dp);
        }else if(headerTitle.equals("Anggota")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_person_black_24dp);
        }else if(headerTitle.equals("List Karyawan")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.ic_baseline_people_24px);
        }else if(headerTitle.equals("Home")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_home_black_18dp);
        }else if(headerTitle.equals("Absensi")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.ic_baseline_aspect_ratio_24px);
        }else if(headerTitle.equals("Cek Gaji")){
            if(_context.getSharedPreferences("poipayroll",MODE_PRIVATE).getString("level","").equals("karyawan")){
                expadingarrowdown.setVisibility(View.GONE);
            }
            else {
                expadingarrowdown.setVisibility(View.VISIBLE);
            }
            icon.setBackgroundResource(R.drawable.baseline_monetization_on_black_24dp);

        }else if(headerTitle.equals("Cek Jadwal")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.ic_baseline_today_24px);
        }else if(headerTitle.equals("Profil")){
            expadingarrowdown.setVisibility(View.GONE);
            icon.setBackgroundResource(R.drawable.baseline_account_circle_black_24dp);
        }else if(headerTitle.equals("Pengajuan")){
            expadingarrowdown.setVisibility(View.VISIBLE);
            icon.setBackgroundResource(R.drawable.baseline_assignment_black_24dp);
        }else if(headerTitle.equals("Laporan")){
            expadingarrowdown.setVisibility(View.VISIBLE);
            icon.setBackgroundResource(R.drawable.baseline_assessment_black_24dp);
        }
        else{
            expadingarrowdown.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
