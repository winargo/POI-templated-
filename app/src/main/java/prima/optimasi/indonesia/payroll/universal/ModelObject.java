package prima.optimasi.indonesia.payroll.universal;


import java.util.List;

import prima.optimasi.indonesia.payroll.R;

public enum ModelObject {

    RED(R.string.about_text, R.layout.item_keterangan_karyawan),
    BLUE(R.string.action_settings, R.layout.item_keterangan_karyawan),
    GREEN(R.string.title_activity_mainmenu_hrd, R.layout.item_keterangan_karyawan);

    private int mTitleResId;
    private int mLayoutResId;

    public static List<String> layoutresid;


    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }


}