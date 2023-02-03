package com.shawlabs.statussaver.Adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments.Images;
import com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments.Images_Business;
import com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments.Saved;
import com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments.Videos;
import com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments.Videos_Business;

public class WhatsAppFragmentAdapter extends FragmentPagerAdapter {

    int whatsApp_or_WABusiness=0;
    private final Context mContext;


//consteructror
    public WhatsAppFragmentAdapter(int whatsApp_or_WABusiness,Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        this.whatsApp_or_WABusiness = whatsApp_or_WABusiness;
    }

    @Override
    public Fragment getItem(int position) {

        //whtsapp
        if(whatsApp_or_WABusiness==1) {

            if (position == 0)
                return new Images();
            else if (position == 1)
                return new Videos();
            else return new Saved();
        }
        else{

            //business
            if (position == 0)
                return new Images_Business();
            else if (position == 1)
                return new Videos_Business();
            else return new Saved();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "Images";
        else  if(position==1)
            return "Videos";
        else return "Saved";

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}