package com.tapbi.spark.controlcenter.feature.controlios14.view.status;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.util.ArrayList;
import java.util.List;

public class WaveViewNotyIOS extends RelativeLayout {

    private Context context;

    private ImageView imgWave1;
    private ImageView imgWave2;
    private ImageView imgWaveOneSim;
    private TextView tvNoSim;
    private List<String> listSim = new ArrayList<>();
    private boolean oneSim = true;

    public WaveViewNotyIOS(Context context) {
        super(context);
        init(context);
    }

    public WaveViewNotyIOS(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaveViewNotyIOS(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context ctx) {
        this.context = ctx;
        LayoutInflater.from(context).inflate(R.layout.layout_wave_noty_ios, this, true);
        imgWave1 = findViewById(R.id.imgWave1);
        imgWave2 = findViewById(R.id.imgWave2);
        imgWaveOneSim = findViewById(R.id.imgWaveOneSim);
        tvNoSim = findViewById(R.id.tvNoSim);
        setData();
    }

    public void setColor(int color){
        imgWave1.setColorFilter(color);
        imgWave2.setColorFilter(color);
        imgWaveOneSim.setColorFilter(color);
        tvNoSim.setTextColor(color);
    }


    private void setData() {
        updateSim();
        if (NotyControlCenterServicev614.getInstance() != null) {
            int lever = NotyControlCenterServicev614.getInstance().leverSim;
            setViewSignalSim1(lever);
            setViewSignalSim2(lever);
            setViewSignalOneSim(lever);
        }

    }

    private void updateSim() {
        listSim = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            listSim = SettingUtils.getNetworkOperator(context);
        } else {
            listSim.add(SettingUtils.getGSM(context));
        }

        if (listSim.size() == 1 || listSim.isEmpty()) {
            oneSim = true;
        } else if (listSim.size() == 2) {
            oneSim = false;
        }

        if (oneSim) {
            imgWave2.setVisibility(View.GONE);
            imgWave1.setVisibility(View.GONE);
            imgWaveOneSim.setVisibility(View.VISIBLE);
        } else {
            imgWave2.setVisibility(View.VISIBLE);
            imgWave1.setVisibility(View.VISIBLE);
            imgWaveOneSim.setVisibility(View.GONE);
        }

        boolean hasSim = false;
        for (String sim : listSim) {
            if (sim != null && !sim.isEmpty()) {
                hasSim = true;
                break;
            }
        }

//        Timber.e("listSim: " +listSim.size());
        if (hasSim) {
            tvNoSim.setVisibility(GONE);
        } else {
            tvNoSim.setVisibility(VISIBLE);
            imgWave1.setVisibility(GONE);
            imgWave2.setVisibility(GONE);
            imgWaveOneSim.setVisibility(GONE);
        }

    }

    public void updateStateSim() {
        updateSim();
    }


    public void onSignalsChange(int lever) {
        updateSim();
        setViewSignalSim1(lever);
        setViewSignalSim2(lever);
        setViewSignalOneSim(lever);
    }

    private void setViewSignalSim1(int level) {
        if (level == 0) {
            imgWave1.setImageResource(R.drawable.wave_sim1_0);
        } else if (level == 1) {
            imgWave1.setImageResource(R.drawable.wave_sim1_1);
        } else if (level == 2) {
            imgWave1.setImageResource(R.drawable.wave_sim1_2);
        } else if (level == 3) {
            imgWave1.setImageResource(R.drawable.wave_sim1_3);
        } else {
            imgWave1.setImageResource(R.drawable.wave_sim1_4);
        }
    }

    private void setViewSignalSim2(int level) {
        if (level == 0) {
            imgWave2.setImageResource(R.drawable.wave_sim2_0);
        } else if (level == 1) {
            imgWave2.setImageResource(R.drawable.wave_sim2_1);
        } else if (level == 2) {
            imgWave2.setImageResource(R.drawable.wave_sim2_2);
        } else if (level == 3) {
            imgWave2.setImageResource(R.drawable.wave_sim2_3);
        } else {
            imgWave2.setImageResource(R.drawable.wave_sim2_4);
        }
    }

    private void setViewSignalOneSim(int level) {
        if (level == 0) {
            imgWaveOneSim.setImageResource(R.drawable.wave_0);
        } else if (level == 1) {
            imgWaveOneSim.setImageResource(R.drawable.wave_1);
        } else if (level == 2) {
            imgWaveOneSim.setImageResource(R.drawable.wave_2);
        } else if (level == 3) {
            imgWaveOneSim.setImageResource(R.drawable.wave_3);
        } else {
            imgWaveOneSim.setImageResource(R.drawable.wave_4);
        }
    }
}