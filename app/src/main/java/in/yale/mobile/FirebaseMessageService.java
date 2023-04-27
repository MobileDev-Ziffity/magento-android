package in.yale.mobile;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.service.controls.ControlsProviderService.TAG;

public class FirebaseMessageService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {

        Log.d(TAG, token);
        // TODO: Implement this method to send token to your app server.
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            messageNotificationO(remoteMessage);
        }else{
            messageNotification(remoteMessage);
        }


    }

    private void messageNotificationO(RemoteMessage remoteMessage) {

        String title = getString(R.string.flavored_app_name), body= "Latest Products";

        Intent resultIntent = new Intent(getApplicationContext() , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("url")) {

            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            resultIntent.putExtra("pushNotify",remoteMessage.getData().get("url"));
            resultIntent.putExtra("pushNotifyBoolean", true);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            resultIntent.putExtra("pushNotify",Constants.BASE_URL);
            resultIntent.putExtra("pushNotifyBoolean", true);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel("latest_product", "Product Notification", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Notification for new products");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);



        Notification.Builder notificationBuilder = new Notification.Builder(this, "latest_product")
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle())
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSubText("Product")
                .setNumber(1)
                .setTicker(getString(R.string.flavored_app_name))
                .setAutoCancel(true);


        NotificationManager notification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification.notify(0, notificationBuilder.build());
        }

    }

    private void messageNotification(RemoteMessage remoteMessage) {

        String title = getString(R.string.flavored_app_name), body= "Latest Products";

        Intent resultIntent = new Intent(getApplicationContext() , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("url")) {

            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            resultIntent.putExtra("pushNotify",remoteMessage.getData().get("url"));
            resultIntent.putExtra("pushNotifyBoolean", true);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            resultIntent.putExtra("pushNotify",Constants.BASE_URL);
            resultIntent.putExtra("pushNotifyBoolean", true);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "latest_product")
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentInfo("Product")
                .setNumber(1)
                .setTicker(getString(R.string.flavored_app_name))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}

