package com.tapbi.spark.controlcenter.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.AppInstallModel;

import java.util.ArrayList;

public class SearchAppAdapter extends RecyclerView.Adapter<SearchAppAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AppInstallModel> appSearch;
    private OnSearchAppAdapterListener onSearchAppAdapterListener;

    public SearchAppAdapter(Context context, ArrayList<AppInstallModel> appSearch, OnSearchAppAdapterListener onSearchAppAdapterListener) {
        this.context = context;
        this.appSearch = appSearch;
        this.onSearchAppAdapterListener = onSearchAppAdapterListener;
    }
    public void setAppSearch( ArrayList<AppInstallModel> appSearch){
        this.appSearch=appSearch;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_search_app, viewGroup, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        int i = viewHolder.getLayoutPosition();
        final AppInstallModel appInstallModel = appSearch.get(i);
        viewHolder.icon.setImageDrawable(appInstallModel.getDrawable());
        viewHolder.appName.setText(appInstallModel.getName());

        viewHolder.itemView.setOnClickListener(v -> {
            if (onSearchAppAdapterListener != null) {
                onSearchAppAdapterListener.onClickApp(appInstallModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appSearch.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            appName = itemView.findViewById(R.id.appName);
        }
    }

    public interface OnSearchAppAdapterListener {
        void onClickApp(AppInstallModel appInstallModel);
    }
}
