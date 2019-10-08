package com.example.disastergeolocation.RecyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.disastergeolocation.Model.HistoryModel;
import com.example.disastergeolocation.R;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<HistoryModel> models;

    public HistoryRecyclerViewAdapter(Context context, List<HistoryModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_image_black_24dp);
        Glide.with(context)
                .load(models.get(i).getPicture_url())
                .apply(options)
                .override(600,480)
                .into(myViewHolder.iv_history);

        myViewHolder.tv_date.setText("Diupload Pada" + models.get(i).getSent_at());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_date;
        public ImageView iv_history;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_history = itemView.findViewById(R.id.iv_history);
        }
    }
}
