package android.trithe.real.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.trithe.real.fragment.LichChieuFragment;
import android.trithe.real.fragment.SapChieuFragment;
import android.trithe.real.fragment.TintucFragment;

public class PaperViewBrowseAdapter extends FragmentPagerAdapter {

    public PaperViewBrowseAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
            TintucFragment tintucFragment = new TintucFragment();
            return tintucFragment;
            case 1:
                SapChieuFragment sapChieuFragment = new SapChieuFragment();
                return sapChieuFragment;
            case 2:
                LichChieuFragment lichChieuFragment = new LichChieuFragment();
                return lichChieuFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
