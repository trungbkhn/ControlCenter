package com.tapbi.spark.controlcenter.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;

import java.util.ArrayList;

public class RecentAppAdapter extends RecyclerView.Adapter<RecentAppAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AppInstallModel> appInstallModels;
    private OnRecentAppListener onRecentAppListener;

    private int color = Color.BLACK;

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    public RecentAppAdapter(Context context, ArrayList<AppInstallModel> appInstallModels, OnRecentAppListener onRecentAppListener) {
        this.context = context;
        this.appInstallModels = appInstallModels;
        this.onRecentAppListener = onRecentAppListener;
    }
    public void setAppInstallModels(ArrayList<AppInstallModel> appInstallModels){
        this.appInstallModels=appInstallModels;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_recent_app_new, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = holder.getLayoutPosition();
        final AppInstallModel appInstallModel = appInstallModels.get(position);

        holder.icon.setImageDrawable(appInstallModel.getDrawable());
        holder.name.setText(appInstallModel.getName());

        holder.name.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecentAppListener != null) {
                    onRecentAppListener.onClick(appInstallModel.getPackageName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInstallModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
        }
    }

    public interface OnRecentAppListener {
        void onClick(String pka);
    }
}
