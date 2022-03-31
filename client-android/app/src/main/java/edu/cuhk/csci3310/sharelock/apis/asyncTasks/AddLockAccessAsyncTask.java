package edu.cuhk.csci3310.sharelock.apis.asyncTasks;

import static edu.cuhk.csci3310.sharelock.globals.Constants.BASE_URL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.JWT_FAILED;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_EXPIRED_AT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_PERMISSION;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_USER_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_NAME;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_PRIVATE_KEY;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockAccessTaskCompleted;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockTaskCompleted;
import edu.cuhk.csci3310.sharelock.globals.Helpers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddLockAccessAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "AddLockAccessAsyncTask";
    private FirebaseAuth auth;
    private String token = "";
    private OnLockAccessTaskCompleted listener;
    private ProgressDialog dialog;
    private Bundle lockAccessData;

    public AddLockAccessAsyncTask(OnLockAccessTaskCompleted listener, Activity activity, Bundle lockAccessData){
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
                result.set(addLockAccess());
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
        // parse response to obj
        try {
            JSONObject jsonObject = new JSONObject(response);
            listener.onLockAccessTaskCompleted(Helpers.parseLockAccess(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // close dialog after parsing
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String addLockAccess () {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(LOCK_ACCESS_USER_EMAIL, lockAccessData.getString(LOCK_ACCESS_USER_EMAIL))
                    .addFormDataPart(LOCK_ACCESS_PERMISSION, lockAccessData.getString(LOCK_ACCESS_PERMISSION).toLowerCase())
                    .addFormDataPart(LOCK_ACCESS_EXPIRED_AT,  lockAccessData.getString(LOCK_ACCESS_EXPIRED_AT) == null ? "" : lockAccessData.getString(LOCK_ACCESS_EXPIRED_AT))
                    .build();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/locks/" + lockAccessData.getInt(LOCK_ID) + "/lock_accesses")
                    .method("POST", body)
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
