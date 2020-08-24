package com.example.healthapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TabLayout tabLayout = findViewById(R.id.tabBar);

        final ViewPager viewPager = findViewById(R.id.viewPager);

        // 각 탭에 아이콘 추가
        tabLayout.getTabAt(0).setIcon(R.drawable.mypage);
        tabLayout.getTabAt(1).setIcon(R.drawable.star);
        tabLayout.getTabAt(2).setIcon(R.drawable.walk);

        /*
        PagerAdapter: pass data from the tabs and display them into the view pager
         */
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        // 여기
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /*
        Change the tabs view when the tab is selected
         */
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.mypage2);
                    tabLayout.getTabAt(1).setIcon(R.drawable.star);
                    tabLayout.getTabAt(2).setIcon(R.drawable.walk);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(0).setIcon(R.drawable.mypage);
                    tabLayout.getTabAt(1).setIcon(R.drawable.star2);
                    tabLayout.getTabAt(2).setIcon(R.drawable.walk);
                } else {
                    tabLayout.getTabAt(0).setIcon(R.drawable.mypage);
                    tabLayout.getTabAt(1).setIcon(R.drawable.star);
                    tabLayout.getTabAt(2).setIcon(R.drawable.walk2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        onTabSelectedListener.onTabSelected(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

    }
    class PagerAdapter extends FragmentPagerAdapter {

        private int numOfTabs;

        public PagerAdapter(FragmentManager fm, int numOfTabs){
            super(fm, numOfTabs);
            this.numOfTabs = numOfTabs;
        }

        // getItem: where we initialize the fragments for android tab layout ex) tab1, tab2, tab3 ...
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new MyPage();
                case 1:
                    return new Routes();
                case 2:
                    return new Walk();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }


}