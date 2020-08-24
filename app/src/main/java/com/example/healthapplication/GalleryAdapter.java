package com.example.healthapplication;

//Used for RecyclerView Adapter

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private final GalleryItemClickListener galleryItemClickListener;
    private ArrayList<ImageModel> galleryList;

    public GalleryAdapter(ArrayList<ImageModel> galleryList, GalleryItemClickListener galleryItemClickListener) {
        this.galleryList = galleryList;
        this.galleryItemClickListener = galleryItemClickListener;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        final ImageModel imageModel = galleryList.get(position);

        Glide.with(holder.galleryImageView.getContext()).
                load(imageModel.getImgPath())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.galleryImageView);

        // Set transition name same as the Image name
        ViewCompat.setTransitionName(holder.galleryImageView, imageModel.getDate());

        holder.galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryItemClickListener.onGalleryItemClickListener(holder.getAdapterPosition(), imageModel, holder.galleryImageView);
            }
        });
        // long click 할때 image 삭제
        holder.galleryImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //have to notify to server
                galleryList.remove(position);
                notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    static class GalleryViewHolder extends RecyclerView.ViewHolder {
      ImageView galleryImageView;

        GalleryViewHolder(View view) {
            super(view);
            galleryImageView = view.findViewById(R.id.galleryImage);
        }
    }
}
