package com.example.healthapplication;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


public class GalleryAdapterRoutes2 extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>{
    private ArrayList<RouteModel> routeList;

    public GalleryAdapterRoutes2(ArrayList<RouteModel> routeList) {
        this.routeList = routeList;

    }

    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryAdapter.GalleryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GalleryAdapter.GalleryViewHolder holder, int position) {
        final RouteModel routeModel = routeList.get(position);


        Glide.with(holder.galleryImageView.getContext()).
                load(routeModel.getImgPath())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.galleryImageView);

        // Set transition name same as the Image name

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }


}
