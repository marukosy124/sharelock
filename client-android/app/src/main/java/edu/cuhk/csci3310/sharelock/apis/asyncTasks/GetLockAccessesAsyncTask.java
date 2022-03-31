package edu.cuhk.csci3310.sharelock.apis.asyncTasks;

import static edu.cuhk.csci3310.sharelock.globals.Constants.BASE_URL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;

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

import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLockAccessesCompleted;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLocksCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.globals.Helpers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetLockAccessesAsyncTask extends AsyncTask<String, String, String> {
    private static final String TAG = "GetLockAccesses";
    private FirebaseAuth auth;
    private String token = "";
    private OnGetLockAccessesCompleted listener;
    private ProgressDialog dialog;
    private int lockId;

    public GetLockAccessesAsyncTask(OnGetLockAccessesCompleted listener, Activity activity, int lockId){
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
                result.set(getLockAccesses());
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
            ArrayList<LockAccess> lockAccesses = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                lockAccesses.add(Helpers.parseLockAccess(jsonObject));
            }
            listener.onGetLockAccessesCompleted(lockAccesses);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public String getLockAccesses (){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/locks/" + lockId + "/lock_accesses")
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
