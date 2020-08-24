package com.example.healthapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {
    private String date, imgPath;
    public ImageModel(String d, String ip) {
        date = d;
        imgPath = ip;
    }
    public ImageModel(Parcel in) {
        date = in.readString();
        imgPath = in.readString();
    }
    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }
        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    // Percel 하려는 오브젝트의 종류 정의
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(imgPath);
    }
}
