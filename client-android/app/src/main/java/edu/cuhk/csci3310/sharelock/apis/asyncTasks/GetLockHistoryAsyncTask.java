package edu.cuhk.csci3310.sharelock.apis.asyncTasks;

import static edu.cuhk.csci3310.sharelock.globals.Constants.BASE_URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLockHistoryCompleted;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLocksCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockHistory;
import edu.cuhk.csci3310.sharelock.globals.Helpers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetLockHistoryAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "GetLockHistoryAsyncTask";
    private FirebaseAuth auth;
    private String token = "";
    private OnGetLockHistoryCompleted listener;
    private ProgressDialog dialog;
    private int lockId;

    public GetLockHistoryAsyncTask(OnGetLockHistoryCompleted listener, Activity activity, int lockId){
        this.listener = listener;
        this.lockId = lockId;
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
                Log.d(TAG, token);
                result.set(getLockHistory());
            } else {
                Log.e(TAG, "Failed to get JWT token");
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
        try {
            JSONArray jsonArray = new JSONArray(response);
            ArrayList<LockHistory> logs = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                logs.add(Helpers.parseLockHistory(jsonObject));
            }
            listener.onGetLockHistoryCompleted(logs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String getLockHistory (){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/locks/" + lockId + "/histories")
                    .method("GET", null)
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
