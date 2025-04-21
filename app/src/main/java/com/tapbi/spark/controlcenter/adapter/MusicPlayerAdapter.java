package com.tapbi.spark.controlcenter.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.feature.controlios14.model.MusicPlayer;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.utils.TinyDB;
import com.tapbi.spark.controlcenter.common.Constant;

import java.util.ArrayList;


public class MusicPlayerAdapter extends RecyclerView.Adapter<MusicPlayerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MusicPlayer> musicPlayers;
    private PackageManager pm;
    private String appSelected;
    private TinyDB tinyDB;

    public MusicPlayerAdapter(Context context, ArrayList<MusicPlayer> musicPlayers) {
        this.context = context;
        this.musicPlayers = musicPlayers;
        pm = context.getApplicationContext().getPackageManager();
        tinyDB = App.tinyDB;
        appSelected = tinyDB.getString(Constant.MUSIC_PLAYER_SELECTED_PACKAGENAME);
    }

    public void setData(ArrayList<MusicPlayer> musicPlayers){
        this.musicPlayers = musicPlayers;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_music_player, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = holder.getLayoutPosition();
        if (position == 0){
            holder.line.setVisibility(View.INVISIBLE);
        }else {
            holder.line.setVisibility(View.VISIBLE);
        }
        final String pka = musicPlayers.get(position).getPackageName();
        final String receiver = musicPlayers.get(position).getReceiverName();
        try {
            Drawable ic = pm.getApplicationIcon(pka);
            holder.icon.setImageDrawable(ic);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ApplicationInfo ai = pm.getApplicationInfo(pka, 0);
            String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            holder.tvAppName.setText(applicationName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pka.equals(appSelected)) {
            holder.tvAppName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkbox_check_background, 0);
        } else {
            holder.tvAppName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSelected = pka;
                tinyDB.putString(Constant.MUSIC_PLAYER_SELECTED_PACKAGENAME, appSelected);
                tinyDB.putString(Constant.MUSIC_PLAYER_SELECTED_RECEIVERNAME, receiver);
                notifyDataSetChanged();
//                if (NotyControlCenterServicev614.getInstance() != null){
//                    NotyControlCenterServicev614.getInstance().setSetViewMusic(appSelected);
//                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return musicPlayers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View line;
        public ImageView icon;
        public TextView tvAppName;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            line = itemView.findViewById(R.id.line);
        }
    }
}
