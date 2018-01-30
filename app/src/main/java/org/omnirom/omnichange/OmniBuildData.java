package org.omnirom.omnichange;


import android.util.Log;

import com.bytehamster.changelog.Main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class OmniBuildData {
    private static final String TAG = "OmniBuildData";
    private static final int HTTP_READ_TIMEOUT = 30000;
    private static final int HTTP_CONNECTION_TIMEOUT = 30000;
	private static final String URL_BAS_JSON = "https://dl.omnirom.org/json.php";

    private static HttpsURLConnection setupHttpsRequest(String urlStr){
        URL url;
        HttpsURLConnection urlConnection = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(HTTP_READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            if (code != HttpsURLConnection.HTTP_OK) {
                Log.d(TAG, "response: " + code);
                return null;
            }
            return urlConnection;
        } catch (Exception e) {
            Log.d(TAG, "Failed to connect to server");
            return null;
        }
    }

    private static String downloadUrlMemoryAsString(String url) {
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = setupHttpsRequest(url);
            if(urlConnection == null){
                return null;
            }

            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            int byteInt;

            while((byteInt = is.read()) >= 0){
                byteArray.write(byteInt);
            }

            byte[] bytes = byteArray.toByteArray();
            if(bytes == null){
                return null;
            }
            String responseBody = new String(bytes, StandardCharsets.UTF_8);

            return responseBody;
        } catch (Exception e) {
            // Download failed for any number of reasons, timeouts, connection
            // drops, etc. Just log it in debugging mode.
            Log.e(TAG, "downloadUrlMemoryAsString", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static boolean isMatchingImage(String fileName) {
        try {
            if(fileName.endsWith(".zip") && fileName.indexOf(Main.getDefaultDevice()) != -1) {
                if(fileName.contains(Main.DEFAULT_VERSION)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "isMatchingImage", e);
        }
        return false;
    }

    private static long getUnifiedWeeklyBuildTime(long weeklyTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(weeklyTime);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    public static List<Long> getWeeklyBuildTimes() {
        String url = URL_BAS_JSON;
        List<Long> weeklyBuildTimes = new ArrayList<>();
        String buildData = downloadUrlMemoryAsString(url);
        if (buildData == null || buildData.length() == 0) {
            return weeklyBuildTimes;
        }
        try {
            JSONObject object = new JSONObject(buildData);
            Iterator<String> nextKey = object.keys();
            while (nextKey.hasNext()) {
                String key = nextKey.next();
                if (key.equals("./" + Main.getDefaultDevice())) {
                    JSONArray builds = object.getJSONArray(key);
                    for (int i = 0; i < builds.length(); i++) {
                        JSONObject build = builds.getJSONObject(i);
                        String fileName = build.getString("filename");
                        if(isMatchingImage(fileName)) {
                            long modTime = build.getLong("timestamp") * 1000;
                            weeklyBuildTimes.add(getUnifiedWeeklyBuildTime(modTime));
                        }
                    }
                }
            }
            Collections.sort(weeklyBuildTimes);
            Collections.reverse(weeklyBuildTimes);
        } catch (Exception e) {
            Log.e(TAG, "getWeeklyBuildTimes", e);
        }
        return weeklyBuildTimes;
    }
}
