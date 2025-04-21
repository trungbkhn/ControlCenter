package com.tapbi.spark.controlcenter.feature.controlios14.view.status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.utils.NetworkUtil;
import com.tapbi.spark.controlcenter.utils.SettingUtils;


@SuppressLint("AppCompatCustomView")
public class WifiView extends RelativeLayout {

    private Context context;

    private ImageView imgWifi;
    private TextView tv3G;
    private DataMobileUtils dataMobileUtils;
    private boolean statusWifiOn = true;

    private TelephonyManager mTelephonyManager;

    public WifiView(Context context) {
        super(context);
        init(context);
    }

    public WifiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WifiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        dataMobileUtils = new DataMobileUtils(context);
        mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_wifi, this, true);
        imgWifi = findViewById(R.id.wifi);
        tv3G = findViewById(R.id.tv3G);
    }

    public void setColor(int color){
        tv3G.setTextColor(color);
        imgWifi.setColorFilter(color);
    }

    public void updateWifiMode(boolean b) {
        statusWifiOn = b;
        if (b) {
            if (NetworkUtil.getConnectivityStatus(context) == NetworkUtil.NETWORK_STATUS_WIFI) {
                imgWifi.setVisibility(VISIBLE);
                tv3G.setVisibility(View.GONE);
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifi.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
                try {
                    if (level >= 75) {
                        imgWifi.setImageResource(R.drawable.wifi_3);
                    } else if (level >= 50) {
                        imgWifi.setImageResource(R.drawable.wifi_2);
                    } else if (level >= 25) {
                        imgWifi.setImageResource(R.drawable.wifi_1);
                    }
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
            } else {
                imgWifi.setVisibility(View.GONE);
                updateTvDataMobile();
            }
        } else {
            imgWifi.setVisibility(View.GONE);
            updateTvDataMobile();
        }
    }

    public void updateTvDataMobile() {
        if (!statusWifiOn) {
            tv3G.setVisibility(dataMobileUtils.isDataEnable() && !SettingUtils.isAirplaneModeOn(context) ? VISIBLE : GONE);
            tv3G.setText(SettingUtils.getNetworkType(context, mTelephonyManager));
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() == View.VISIBLE && dataMobileUtils != null) {
            updateTvDataMobile();
        }
    }
}

