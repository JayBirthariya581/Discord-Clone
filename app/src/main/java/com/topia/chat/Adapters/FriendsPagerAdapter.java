package com.topia.chat.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.topia.chat.Fragments.FriendListFragment;
import com.topia.chat.Fragments.FriendRequestFragment;

public class FriendsPagerAdapter extends FragmentPagerAdapter {
    //Setting adapter for pager
    //Responsible for changing pages according to category selected
    private int tabCount=2;
    FragmentManager fm;

    public FriendsPagerAdapter(FragmentManager fm) {
        super(fm);


    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){//alloting category according to position

            case 0:
                return new FriendListFragment();


            case 1:
                return new FriendRequestFragment();


            default:
                return null;


        }

    }

    @Override
    public int getCount() {
        return this.tabCount;
    }
}