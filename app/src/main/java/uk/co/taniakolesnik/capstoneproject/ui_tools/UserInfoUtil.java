package uk.co.taniakolesnik.capstoneproject.ui_tools;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import timber.log.Timber;

public class UserInfoUtil {

    private UserInfoUtil() {}

    private static final String BASE_URL = "https://api.github.com/user";

    public static Map<String, Object> fetchUserInfo (String mAcceessToken, Context context){

        Timber.i("Sundat fetchUserInfo started for %s", mAcceessToken);

        String jsonReply = "";
        URL url = createURL(BASE_URL);
        try {
            jsonReply = makehttprequest(url, mAcceessToken);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return extractUserInfoFromJson(jsonReply, context);
    }

    private static URL createURL(String mUrl) {
        URL url;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }

    private static String makehttprequest(URL url, String mAcceessToken) throws IOException {
        String jsonReply = "";
        if (url == null) {
            return jsonReply;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + mAcceessToken);
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonReply = readFromInput(inputStream);
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonReply;
    }

    private static Map<String, Object> extractUserInfoFromJson(String jsonReply, Context context) {
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(jsonReply, mapType );
        return map;
    }

    private static String readFromInput(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

}

