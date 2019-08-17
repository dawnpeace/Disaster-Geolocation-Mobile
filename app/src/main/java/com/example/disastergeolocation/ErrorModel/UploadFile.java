package com.example.disastergeolocation.ErrorModel;

public class UploadFile {
    String[] lat;
    String[] lng;
    String[] photo;
    String[] target;
    String[] android_curtime;

    public UploadFile(String[] lat, String[] lng, String[] photo, String[] target, String[] android_curtime) {
        this.lat = lat;
        this.lng = lng;
        this.photo = photo;
        this.target = target;
        this.android_curtime = android_curtime;
    }

    public String[] getLat() {
        return lat;
    }

    public String[] getLng() {
        return lng;
    }

    public String[] getPhoto() {
        return photo;
    }

    public String[] getTarget() {
        return target;
    }

    public String[] getAndroid_curtime() {
        return android_curtime;
    }
}
