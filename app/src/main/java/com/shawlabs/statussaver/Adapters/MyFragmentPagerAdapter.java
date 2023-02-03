package com.shawlabs.statussaver.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shawlabs.statussaver.Fragments.DirectChatFragment;
import com.shawlabs.statussaver.Fragments.MoreOptionsFragment;
import com.shawlabs.statussaver.Fragments.WhatsAppBusinessFragment;
import com.shawlabs.statussaver.Fragments.WhatsAppFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        }

@Override
public Fragment getItem(int position) {
        switch (position) {
        case 0:
        return new WhatsAppFragment();
        case 1:
        return new WhatsAppBusinessFragment();
        case 2:
        return new DirectChatFragment();
        case 3:
                return new MoreOptionsFragment();
default:
        return null;
        }


        }

@Override
public int getCount() {
        return 4;
        }
        }