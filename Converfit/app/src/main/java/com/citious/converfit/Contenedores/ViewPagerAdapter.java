package com.citious.converfit.Contenedores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.citious.converfit.Actividades.ChatWeb.ChatWebFragment;
import com.citious.converfit.Actividades.Conversations.ListConversationsFragments;
import com.citious.converfit.Actividades.Favorites.ListFavoritesFragment;
import com.citious.converfit.Actividades.Settings.SettingsMenuFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter  {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) {
            ChatWebFragment lstChatWeb = new ChatWebFragment();
            return lstChatWeb;
        }else if(position == 1) // if the position is 0 we are returning the First tab
        {
            ListFavoritesFragment listFavorites = new ListFavoritesFragment();
            return listFavorites;
        }else if(position == 2) {
            ListConversationsFragments listConversations = new ListConversationsFragments();
            return listConversations;
        }else {
            SettingsMenuFragment menuSettings = new SettingsMenuFragment();
            return menuSettings;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }


}

