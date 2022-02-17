package com.example.quantsapptest.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.quantsapptest.R;
import com.example.quantsapptest.fragments.ApiFragment;
import com.example.quantsapptest.fragments.URLFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //it will move laayout under statusbar

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPagerDemo(viewPager);
    }

    public void viewPagerDemo(ViewPager viewPager) {
        TabsFragmentAdapter tabsFragmentAdapter = new TabsFragmentAdapter(getSupportFragmentManager());

        tabsFragmentAdapter.addFragment(new ApiFragment(), "Api");
        tabsFragmentAdapter.addFragment(new URLFragment(), "URL");

        viewPager.setAdapter(tabsFragmentAdapter);
    }

    class TabsFragmentAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        ArrayList<String> title = new ArrayList<String>();

        public TabsFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragments.add(fragment);
            title.add(name.toLowerCase());
        }

    }
}