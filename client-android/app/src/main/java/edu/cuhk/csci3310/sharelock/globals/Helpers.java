package edu.cuhk.csci3310.sharelock.globals;

import static edu.cuhk.csci3310.sharelock.globals.Constants.DISPLAY_FORMAT;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;

import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.classes.LockHistory;
import edu.cuhk.csci3310.sharelock.classes.User;

public class Helpers {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Lock parseLock(JSONObject jsonObject) {
        try {
            String token = jsonObject.optString("token", "");
            Lock lock = new Lock(jsonObject.getInt("id"), jsonObject.getString("name"), parseLockAccesses(jsonObject), token);
            return lock;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LockAccess> parseLockAccesses (JSONObject jsonObject) {
        try {
            JSONArray lockAccesses = jsonObject.getJSONArray("lock_accesses");
            ArrayList<LockAccess> lockAccessesArr = new ArrayList();
            for (int j = 0; j < lockAccesses.length(); j++) {
                JSONObject access = lockAccesses.getJSONObject(j);
                JSONObject user = new JSONObject(access.getString("user"));
                User newUser = new User(user.getString("id"), user.getString("name"), user.getString("created_at"),  user.getString("updated_at"));
                LockAccess newAccess = new LockAccess(access.getInt("id"), newUser, access.getString("permission"), access.getString("expired_at"));
                lockAccessesArr.add(newAccess);
            }
            return lockAccessesArr;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LockAccess parseLockAccess (JSONObject jsonObject) {
        try {
            JSONObject user = new JSONObject(jsonObject.getString("user"));
            User newUser = new User(user.getString("id"), user.getString("name"), user.getString("created_at"), user.getString("updated_at"));
            LockAccess newAccess = new LockAccess(jsonObject.getInt("id"), newUser, jsonObject.getString("permission"), jsonObject.getString("expired_at"));
            return newAccess;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LockHistory parseLockHistory (JSONObject jsonObject) {
        try {
            JSONObject user = new JSONObject(jsonObject.getString("user"));
            User newUser = new User(user.getString("id"), user.getString("name"), user.getString("created_at"), user.getString("updated_at"));
            LockHistory history = new LockHistory(jsonObject.getInt("id"), newUser, jsonObject.getString("accessed_at"));
            return history;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeKeyboard (Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void threadPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatISOToDisplay(String isoString) {
        if(isoString == null || isoString == "" || isoString.equals("null")){
            return "Never";
        } else {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
            TemporalAccessor accessor = timeFormatter.parse(isoString);
            Date date = Date.from(Instant.from(accessor));
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_FORMAT);
            return displayFormat.format(date.getTime());
        }
    }
}
