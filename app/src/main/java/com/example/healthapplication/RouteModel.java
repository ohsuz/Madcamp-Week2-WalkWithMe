package com.example.healthapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteModel implements Parcelable {
    private String imgPath;
    private String imgName;

    public RouteModel(String ip, String in) {
        imgPath = ip;
        imgName = in;
    }

    public RouteModel(Parcel in) {
        imgPath = in.readString();
        imgName = in.readString();
    }

    public static final Creator<RouteModel> CREATOR = new Creator<RouteModel>() {
        @Override
        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }
        @Override
        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    // Percel 하려는 오브젝트의 종류 정의
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgPath);
        dest.writeString(imgName);
    }
}
