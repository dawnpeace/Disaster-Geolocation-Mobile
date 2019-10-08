package com.example.disastergeolocation.Model;

public class HistoryModel {
    private String sent_at;
    private String received_at;
    private String picture_url;

    public HistoryModel(String sent_at, String received_at, String picture_url) {
        this.sent_at = sent_at;
        this.received_at = received_at;
        this.picture_url = picture_url;
    }

    public String getSent_at() {
        return sent_at;
    }

    public String getReceived_at() {
        return received_at;
    }

    public String getPicture_url() {
        return picture_url;
    }
}
