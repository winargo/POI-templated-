package prima.optimasi.indonesia.payroll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class okhttpclass {
    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String getString(String url) throws IOException {
        okhttpclass example = new okhttpclass();
        String response = example.run(url);
        return response;
    }

    public static JSONArray getJsonArray(String url, Map<String, String> args) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();

            JSONArray jsonArray = new JSONArray(jsonData);

            return jsonArray;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonlogin(String url,String user , String Pass) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONArray array = new JSONArray();

            RequestBody body = new FormBody.Builder()
                    .add("username", user)
                    .add("password", Pass)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }

            if (responses==null){
                return null;
            }
            else {
                JSONObject jsonObject = new JSONObject(responses.body().string());
                return jsonObject;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonlogin(String url,String token,String user , String Pass) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONArray array = new JSONArray();

            RequestBody body = new FormBody.Builder()
                    .add("username", user)
                    .add("password", Pass)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }

            if (responses==null){
                return null;
            }
            else {
                JSONObject jsonObject = new JSONObject(responses.body().string());
                return jsonObject;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonpengumuman(String url,String token) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONArray array = new JSONArray();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }

            if (responses==null){
                return null;
            }
            else {
                JSONObject jsonObject = new JSONObject(responses.body().string());
                return jsonObject;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static JSONObject getJsonurl(String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONArray array = new JSONArray();

            RequestBody body = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }

            if (responses==null){
                return null;
            }
            else {
                JSONObject jsonObject = new JSONObject(responses.body().string());
                return jsonObject;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
