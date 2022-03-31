package edu.cuhk.csci3310.sharelock.apis.asyncTasks;

import static edu.cuhk.csci3310.sharelock.globals.Constants.BASE_URL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.JWT_FAILED;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockTaskCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.classes.User;
import edu.cuhk.csci3310.sharelock.globals.Helpers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddLockAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "AddLockAsyncTask";
    private FirebaseAuth auth;
    private String token = "";
    private OnLockTaskCompleted listener;
    private ProgressDialog dialog;
    private Bundle lockData;

    public AddLockAsyncTask(OnLockTaskCompleted listener, Activity activity, Bundle lockData){
        this.listener = listener;
        this.lockData = lockData;
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
                result.set(addLock());
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
            listener.onLockTaskCompleted(Helpers.parseLock(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // close dialog after parsing
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String addLock () {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(LOCK_NAME, lockData.getString(LOCK_NAME))
                    .addFormDataPart(LOCK_PRIVATE_KEY, lockData.getString(LOCK_PRIVATE_KEY))
                    .build();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/locks")
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
