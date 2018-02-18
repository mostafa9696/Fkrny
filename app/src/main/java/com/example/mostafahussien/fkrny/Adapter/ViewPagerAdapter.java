package com.example.mostafahussien.fkrny.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.mostafahussien.fkrny.Fragment.TabsFragment;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.ListenersInterfac.ReminderListener;
import com.example.mostafahussien.fkrny.ListenersInterfac.SelectedListener;
import com.example.mostafahussien.fkrny.R;


public class ViewPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider{
    Context con;
    TabsFragment fragment;

    private final int[] Icons={
            R.drawable.selector_icon_active,
            R.drawable.selector_icon_inactive
    };

    public ViewPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        con=context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        if(position==1){  // default
            bundle.putInt("Type", Reminder.INACTIVE);
        }
        else {
            bundle.putInt("Type", Reminder.ACTIVE);
        }
        fragment=new TabsFragment();
        fragment.setArguments(bundle);
        fragment.setReminderListener((ReminderListener) con);
        fragment.setSelectedListener((SelectedListener) con);
        return fragment;
    }

    @Override
    public int getCount() {
        return Icons.length;
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {
        FrameLayout layout=(FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tab, parent, false);
        ((ImageView)layout.findViewById(R.id.image)).setImageResource(Icons[position]);
        return layout;
    }

    @Override
    public void tabSelected(View tab) {
        tab.setSelected(true);
    }
    @Override
    public void tabUnselected(View tab) {
        tab.setSelected(false);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
