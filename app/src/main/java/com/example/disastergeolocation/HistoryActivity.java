package com.example.disastergeolocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.disastergeolocation.Model.HistoryModel;
import com.example.disastergeolocation.RecyclerViewAdapters.HistoryRecyclerViewAdapter;
import com.example.disastergeolocation.RetrofitInterface.ReportInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rv_history;
    private SharedPrefHelper sharedPrefHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("Riwayat Laporan Anda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadView();
    }

    private void loadView(){
        rv_history = findViewById(R.id.rv_history);
        loadData();
    }

    public void loadData(){
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        Retrofit retrofit = RetrofitInstance.getRetrofit(sharedPrefHelper.getInterceptor());
        ReportInterface reportInterface = retrofit.create(ReportInterface.class);
        Call<List<HistoryModel>> call = reportInterface.getHistory();
        call.enqueue(new Callback<List<HistoryModel>>() {
            @Override
            public void onResponse(Call<List<HistoryModel>> call, Response<List<HistoryModel>> response) {
                if(response.isSuccessful()){
                    rv_history.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                    rv_history.setAdapter(new HistoryRecyclerViewAdapter(HistoryActivity.this,response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<HistoryModel>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Kesalahan Koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
