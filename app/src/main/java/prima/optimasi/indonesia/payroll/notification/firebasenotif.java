package prima.optimasi.indonesia.payroll.notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import prima.optimasi.indonesia.payroll.core.generator;

public class firebasenotif extends FirebaseInstanceIdService{

    String TAG = "Firebase notif";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        getSharedPreferences("poipayroll",MODE_PRIVATE).edit().putString("tokennotif",refreshedToken).commit();
        getSharedPreferences("poipayroll",MODE_PRIVATE).edit().putInt("statustoken",0).commit();
        SharedPreferences a =getSharedPreferences("poipayroll", MODE_PRIVATE);
        Log.e(TAG, "sendRegistrationToServer: " + a.getString("notiftoken",""));
    }
}