package com.example.healthapplication;

public class UserId {
    public static String id = "anonymous";

    public UserId(String uid){
        this.id = uid;
    }

    public static void setId(String id) {
        UserId.id = id;
    }

    public static String getId(){
        return id;
    }

}
