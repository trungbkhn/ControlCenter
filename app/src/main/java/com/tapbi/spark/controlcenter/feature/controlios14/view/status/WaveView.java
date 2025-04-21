package com.tapbi.spark.controlcenter.feature.controlios14.view.status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.telephony.CellSignalStrength;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("AppCompatCustomView")
public class WaveView extends LinearLayout {

    private Context context;
    private ImageView imgWave;
    private TextView tvGsm;
    private Group groupSim1;
    private Group groupSim2;
    private ImageView imgWave2;
    private TextView tvGsm2;

    private List<String> listSim = new ArrayList<>();

    public WaveView(Context context) {
        super(context);
        init(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        LayoutInflater.from(context).inflate(R.layout.layout_wave, this, true);
        imgWave = findViewById(R.id.imgWave1);
        imgWave2 = findViewById(R.id.imgWave2);
        tvGsm = findViewById(R.id.tvGsm);
        tvGsm2 = findViewById(R.id.tvGsm2);
        groupSim1 = findViewById(R.id.groupSim1);
        groupSim2 = findViewById(R.id.groupSim2);
        updateSim();
        if (NotyControlCenterServicev614.getInstance() != null) {
            onSignalsChange(NotyControlCenterServicev614.getInstance().leverSim);
        }

    }

    public void setColor(int color){
        imgWave.setColorFilter(color);
        imgWave2.setColorFilter(color);
        tvGsm.setTextColor(color);
        tvGsm2.setTextColor(color);
    }

    public void setFont(Typeface typeface){
        tvGsm.setTypeface(typeface);
        tvGsm2.setTypeface(typeface);
    }


//    int count = 0;
//    private Handler handler = new Handler(Looper.getMainLooper());
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            count++;
//            Timber.e("hoangld count " + count);
//            setData();
//            handler.postDelayed(this, 300);
//        }
//    };


    private void updateSim() {
        listSim = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            listSim = SettingUtils.getNetworkOperator(context);
        } else {
            listSim.add(SettingUtils.getGSM(context));
        }

        if (listSim.size() == 1) {
            groupSim2.setVisibility(View.GONE);
            if (listSim.get(0) != null && !listSim.get(0).isEmpty()) {
                tvGsm.setText(listSim.get(0));
            }
        } else if (listSim.size() == 2) {
            groupSim2.setVisibility(View.VISIBLE);
            if (listSim.get(0) != null && !listSim.get(0).isEmpty()) {
                tvGsm.setText(listSim.get(0));
            }
            if (listSim.get(1) != null && !listSim.get(1).isEmpty()) {
                tvGsm2.setText(listSim.get(1));
            }
        } else if (listSim.isEmpty()) {
            groupSim2.setVisibility(View.GONE);
            tvGsm.setText(R.string.no_sim);
            setViewSignals(imgWave, 0);
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().setLevelDisconnect();
            }

        }
//        Timber.e("listSim: " + listSim.size());
//        for (String sim : listSim){
//            Timber.e("sim: " +sim);
//        }
    }

    public void updateStateSim() {
        updateSim();
    }


    private void setViewSignalsApi29(List<CellSignalStrength> listSignals) {
        for (int i = 0; i < listSignals.size(); i++) {
            if (i == 0) {
                setViewSignals(imgWave, listSignals.get(i).getLevel());
            } else if (i == 1) {
                setViewSignals(imgWave2, listSignals.get(i).getLevel());
            }
        }
    }

    private void setViewSignals(ImageView viewSignals, int level) {
        if (level == 0) {
            viewSignals.setImageResource(R.drawable.wave_0);
        } else if (level == 1) {
            viewSignals.setImageResource(R.drawable.wave_1);
        } else if (level == 2) {
            viewSignals.setImageResource(R.drawable.wave_2);
        } else if (level == 3) {
            viewSignals.setImageResource(R.drawable.wave_3);
        } else {
            viewSignals.setImageResource(R.drawable.wave_4);
        }
    }


    public void onSignalsChange(int lever) {
        setViewSignals(imgWave, lever);
        setViewSignals(imgWave2, lever);
    }
}
