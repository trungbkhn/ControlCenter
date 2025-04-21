package com.tapbi.spark.controlcenter.receiver;

import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

import timber.log.Timber;

public class MyPhoneStateListener extends PhoneStateListener {

    private IListenerSim iListenerSim;
    private int level = 0;
    private int state = -1;

    public MyPhoneStateListener(IListenerSim iListenerSim) {
        this.iListenerSim = iListenerSim;
    }


    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        int oldLv = level;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                level = signalStrength.getLevel();
            }catch (SecurityException ignored){}
        } else {
            if (signalStrength.getGsmSignalStrength() == 0) {
                level = 0;
            } else if (signalStrength.getGsmSignalStrength() > 0 && signalStrength.getGsmSignalStrength() <= 3) {
                level = 1;
            } else if (signalStrength.getGsmSignalStrength() > 3 && signalStrength.getGsmSignalStrength() <= 20) {
                level = 2;
            } else if (signalStrength.getGsmSignalStrength() > 20 && signalStrength.getGsmSignalStrength() <= 30) {
                level = 3;
            } else {
                level = 4;
            }
        }
        if (oldLv != level && iListenerSim != null) {
            iListenerSim.onSignalsChange(level);
        }
    }


    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);
        Timber.e("phone:" + phoneNumber);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        Timber.e("."+ serviceState.getState());
//        if (state != serviceState.getState() && iListenerSim!=null) {
//            iListenerSim.onSignalsChange(serviceState.getState() == ServiceState.STATE_IN_SERVICE ? level : 0);
//        }
//        state = serviceState.getState();
    }

    public void setLevelDisconnect(){
        level = 0;
    }


    public interface IListenerSim {
        void onSignalsChange(int lever);
    }


}
