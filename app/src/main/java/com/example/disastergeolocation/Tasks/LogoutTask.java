package com.example.disastergeolocation.Tasks;

import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class LogoutTask extends AsyncTask<Void,Void,Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
