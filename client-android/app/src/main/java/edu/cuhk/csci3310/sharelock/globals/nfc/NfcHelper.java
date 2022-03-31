package edu.cuhk.csci3310.sharelock.globals.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;

import androidx.appcompat.app.AlertDialog;

public class NfcHelper {

    private Context mContext;
    NfcAdapter mNfcAdapter;

    public NfcHelper(Context context) {
        this.mContext = context;

        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        checkNfcAvailability();
        checkIfNfcEnabled();
    }

    private void checkNfcAvailability() {
        if (this.mNfcAdapter == null) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Error")
                    .setMessage("There is no NFC module in this device")
                    .setPositiveButton("Quit App", (dialogInterface, i) -> {
                        ((Activity) mContext).finish();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void checkIfNfcEnabled() {
        if (!this.mNfcAdapter.isEnabled()) {
            new AlertDialog.Builder(mContext)
                    .setTitle("NFC is turned off")
                    .setMessage("NFC is needed for unlocking locks")
                    .setPositiveButton("Enable NFC", (dialogInterface, i) -> {
                        Intent settingsIntent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
                        mContext.startActivity(settingsIntent);
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

}
