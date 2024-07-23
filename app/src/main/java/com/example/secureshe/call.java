package com.example.secureshe;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class call extends BroadcastReceiver {
    public call() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Call is ended
            handleCallEnded();
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // Call is answered
            handleCallAnswered();
        }
    }
    private void handleCallEnded() {
        // Implement logic for when the call is ended
        // Update the isCallAttended flag accordingly
    }

    private void handleCallAnswered() {
        // Implement logic for when the call is answered
        // Update the isCallAttended flag accordingly
    }
}