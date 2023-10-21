package lpambarawa.app.monitoring_pam;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.rgb;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class ThisMonitorAppFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();
        Map<String, String> extraData = remoteMessage.getData();
        String docId = extraData.get("docId");
        String senderid = extraData.get("senderid");
        String messagetype = extraData.get("messagetype");
        String sendername = extraData.get("sendername");
        String senderregu = extraData.get("senderregu");
        String senderphoto = extraData.get("senderphoto");
        String senderlocationlat = extraData.get("senderlocation_lat");
        String senderlocationlongi = extraData.get("senderlocation_longi");
        String lapsusIsi = extraData.get("isilaporan");
        String lapsusNama = extraData.get("namapelapor");
        String lapsusFoto = extraData.get("foto");
        String lapsusFotoPelapor = extraData.get("fotopelapor");
        String lapsusInstruksiPim = extraData.get("instruksipim");
        String lapsusTanggal = extraData.get("tgllaporan");
        assert messagetype != null;
        if(messagetype.equals("emergency")) {
            Intent intent = new Intent(this, ShowEmergencyCall.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("senderid", senderid);
            intent.putExtra("sendername", sendername);
            intent.putExtra("senderregu", senderregu);
            intent.putExtra("senderphoto", senderphoto);
            intent.putExtra("senderlocation_lat", senderlocationlat);
            intent.putExtra("senderlocation_longi", senderlocationlongi);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "MonitorPLPSmgApp")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.drawable.ic_warning)
                            .setColor(rgb(255, 0, 0));
            notificationBuilder.setFullScreenIntent(pendingIntent,true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int id =  (int) System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("MonitorPLPSmgApp","monitoremergencycallnotif",NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(id,notificationBuilder.build());
        }else if(messagetype.equals("lapsus")&lapsusIsi!=null){
            Intent intent = new Intent(this, DetailLapsusAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("docId", docId);
            intent.putExtra("senderid", senderid);
            intent.putExtra("namapelapor", lapsusNama);
            intent.putExtra("isilaporan", lapsusIsi);
            intent.putExtra("senderlocation_lat", senderlocationlat);
            intent.putExtra("senderlocation_longi", senderlocationlongi);
            intent.putExtra("instruksipim", lapsusInstruksiPim);
            intent.putExtra("tgllaporan", lapsusTanggal);
            intent.putExtra("foto", lapsusFoto);
            intent.putExtra("fotopelapor", lapsusFotoPelapor);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "MonitorPLPSmgApp")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.drawable.ic_warning)
                            .setColor(rgb(255, 0, 0));
            notificationBuilder.setFullScreenIntent(pendingIntent,true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            int id =  (int) System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("MonitorPLPSmgApp","monitoremergencycallnotif",NotificationManager.IMPORTANCE_MAX);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(id,notificationBuilder.build());
        }else{ messagetype="nothing"; }
    }
}
