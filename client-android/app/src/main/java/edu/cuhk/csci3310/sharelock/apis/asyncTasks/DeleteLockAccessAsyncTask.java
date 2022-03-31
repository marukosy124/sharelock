package edu.cuhk.csci3310.sharelock.apis.asyncTasks;

import static edu.cuhk.csci3310.sharelock.globals.Constants.BASE_URL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.JWT_FAILED;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockAccessTaskCompleted;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockTaskCompleted;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteLockAccessAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "DeleteLockAccess";
    private FirebaseAuth auth;
    private String token = "";
    private OnLockAccessTaskCompleted listener;
    private ProgressDialog dialog;
    private Bundle lockAccessData;

    public DeleteLockAccessAsyncTask(OnLockAccessTaskCompleted listener, Activity activity, Bundle lockAccessData){
        this.listener = listener;
        this.lockAccessData = lockAccessData;
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.getWindow().setLayout(100, 100);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        AtomicReference<Boolean> isCompleted = new AtomicReference<>(false);
        auth = FirebaseAuth.getInstance();
        AtomicReference<String> result = new AtomicReference<>("");

        auth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult().getToken();
                result.set(deleteLockAccess());
            } else {
                Log.e(TAG, JWT_FAILED);
            }
            isCompleted.set(true);
        });

        // keep track of when the task is completed
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(!isCompleted.get());

        return result.get();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String response) {
        listener.onLockAccessTaskCompleted(null);
        // close dialog after parsing
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String deleteLockAccess () {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/locks/" + lockAccessData.getInt(LOCK_ID) + "/lock_accesses/" + lockAccessData.getInt(LOCK_ACCESS_ID))
                    .method("DELETE", null)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }
}
