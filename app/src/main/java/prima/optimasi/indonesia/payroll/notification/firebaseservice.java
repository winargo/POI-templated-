package prima.optimasi.indonesia.payroll.notification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import prima.optimasi.indonesia.payroll.R;
import prima.optimasi.indonesia.payroll.activity_login;
import prima.optimasi.indonesia.payroll.main_owner.manage.approval;


public class firebaseservice extends FirebaseMessagingService {

    String TAG = "firebse service";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
        Log.d("NEW_TOKEN",s);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        getSharedPreferences("poipayroll",MODE_PRIVATE).edit().putString("tokennotif",refreshedToken).commit();
        getSharedPreferences("poipayroll",MODE_PRIVATE).edit().putString("notiftoken",refreshedToken).commit();
        getSharedPreferences("poipayroll",MODE_PRIVATE).edit().putInt("statustoken",0).commit();
        SharedPreferences a =getSharedPreferences("poipayroll", MODE_PRIVATE);
        Log.e(TAG, "tokenservernodejs" + a.getString("tokennotif",""));
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("Msgnodejsfrom", "From: " + remoteMessage.getFrom());
        Log.e("Msgnodejsdata", "From: " + remoteMessage.getData());
        Log.e("Msgnodejsnotific", "From: " + remoteMessage.getNotification());
        Log.e("Msgnodejsbody", "From: " + remoteMessage.getNotification().getBody());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Message", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            //showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow() {
    }

    private void scheduleJob() {
    }

    private void showNotification(String title, String content) {

        if(getSharedPreferences("poipayroll",MODE_PRIVATE).getString("port","").equals("")){

        }
        else {
            if(title.contains("#"+getSharedPreferences("poipayroll",MODE_PRIVATE).getString("port",""))){
                String[] titlesplit = title.split("#"+getSharedPreferences("poipayroll",MODE_PRIVATE).getString("port",""));

                if(content.contains("#"+getSharedPreferences("poipayroll",MODE_PRIVATE).getString("port",""))){
                    String[] contentsplit = title.split("#"+getSharedPreferences("poipayroll",MODE_PRIVATE).getString("port",""));
                    content=titlesplit[0];
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("default",
                                "Prima Optimasi",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription("Prima Optimasi Indonesia");
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                            .setSmallIcon(R.drawable.logobolong) // notification icon
                            .setContentTitle(titlesplit[0]) // title for notification
                            .setContentText(contentsplit[0])
                            .setColor(getResources().getColor(R.color.blue_500))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true); // clear notification after click

                    Intent intent=null;

                    if(contentsplit[1].equals("001")) {
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("002")) {
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Cuti");
                    }
                    else if(contentsplit[1].equals("003")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Dinas");
                    }
                    else if(contentsplit[1].equals("004")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Pinjaman");
                    }
                    else if(contentsplit[1].equals("005")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Golongan");

                    }
                    else if(contentsplit[1].equals("006")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Golongan");

                    }
                    else if(contentsplit[1].equals("007")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Golongan");

                    }
                    else if(contentsplit[1].equals("008")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Karyawan");

                    }
                    else if(contentsplit[1].equals("009")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Karyawan");

                    }
                    else if(contentsplit[1].equals("010")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Karyawan");

                    }
                    else if(contentsplit[1].equals("011")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Pdm");

                    }
                    else if(contentsplit[1].equals("012")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Punishment");

                    }
                    else if(contentsplit[1].equals("013")){
                        intent = new Intent(getApplicationContext(),approval.class);
                        intent.putExtra("tipe","Approval Reward");

                    }
                    else if(contentsplit[1].equals("014")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("015")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("016")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("017")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("018")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("019")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("020")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("021")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else if(contentsplit[1].equals("022")){
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    else {
                        intent = new Intent(getApplicationContext(), activity_login.class);
                    }
                    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    Random rand = new Random();
                    int value = rand.nextInt(2000000);
                    mNotificationManager.notify(value, mBuilder.build());

                }else{
                    
                }
            }else{

            }
        }
    }
}
/**/