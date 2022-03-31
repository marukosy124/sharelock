package edu.cuhk.csci3310.sharelock.globals.nfc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class NfcServiceController {

    private static final NfcServiceController instance = new NfcServiceController();
    private static String TAG = "NfcServiceController";

    private Intent serviceIntent;
    private boolean isServiceRunning = false;

    private NfcServiceController() {}

    public static NfcServiceController getInstance() {
        return instance;
    }

    public void enableNfcWithString(Context context, String payload) {
        if (isServiceRunning) {
            stopNfc(context);
        }

        serviceIntent = new Intent(context, KHostApduService.class);
        serviceIntent.putExtra("ndefMessage", payload);
        ComponentName name = context.startService(serviceIntent);
        isServiceRunning = name == null;
        if (name == null) {
            Log.e(TAG, "Unable to start service");
        }
    }

    public void stopNfc(Context context) {
        context.stopService(serviceIntent);
        isServiceRunning = false;
    }
}
