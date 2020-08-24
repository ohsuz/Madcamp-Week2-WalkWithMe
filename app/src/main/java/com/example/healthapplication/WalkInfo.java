package com.example.healthapplication;

public class WalkInfo {
    public static String imgPath;
    public static String km;
    public static String time;
    public static String cal;
    public static String km_h;

    public WalkInfo() {
    }

    public static String getImgPath() {
        return imgPath;
    }

    public static void setImgPath(String imgPath) {
        WalkInfo.imgPath = imgPath;
    }

    public static String getKm() {
        return km;
    }

    public static void setKm(String km) {
        WalkInfo.km = km;
    }

    public static String getTime() {
        return time;
    }

    public static void setTime(String time) {
        WalkInfo.time = time;
    }

    public static String getCal() {
        return cal;
    }

    public static void setCal(String cal) {
        WalkInfo.cal = cal;
    }

    public static String getKm_h() {
        return km_h;
    }

    public static void setKm_h(String km_h) {
        WalkInfo.km_h = km_h;
    }
}
