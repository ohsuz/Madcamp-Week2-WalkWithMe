package com.example.healthapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class GalleryPagerAdapterR extends FragmentStatePagerAdapter{
    private ArrayList<RouteModel> images;

    public GalleryPagerAdapterR(FragmentManager fm, ArrayList<RouteModel> images) {
        super(fm);
        this.images = images;
    }

    @Override
    public Fragment getItem(int position) {
        RouteModel image = images.get(position);
        return ImageDetailFragmentR.newInstance(image, image.getImgName());
    }

    @Override
    public int getCount() {
        return images.size();
    }
}
