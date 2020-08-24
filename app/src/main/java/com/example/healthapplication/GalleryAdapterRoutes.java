package com.example.healthapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;


public class GalleryAdapterRoutes extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>{
    private ArrayList<RouteModel> routeList;
    private final GalleryItemClickListenerR galleryItemClickListener;
    public GalleryAdapterRoutes(ArrayList<RouteModel> routeList, GalleryItemClickListenerR galleryItemClickListenerR) {
        this.routeList = routeList;
        this.galleryItemClickListener = galleryItemClickListenerR;
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

        // Set transition name same as the Image name
        ViewCompat.setTransitionName(holder.galleryImageView, routeModel.getImgName());

        holder.galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                galleryItemClickListener.onGalleryItemClickListenerR(holder.getAdapterPosition(), routeModel, holder.galleryImageView);
            }
        });
        // long click 할때 image 삭제
        holder.galleryImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //have to notify to server
                routeList.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }


    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView galleryImageView;

        GalleryViewHolder(View view) {
            super(view);
            galleryImageView = view.findViewById(R.id.galleryImage);
        }
    }
}
