package android.trithe.real.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.trithe.real.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {
    private FirebaseAuth mAuth;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        mAuth = FirebaseAuth.getInstance();
        String dataFrom = remoteMessage.getData().get("from_id");
        String dataTo = remoteMessage.getData().get("to_id");
        if (!dataTo.equals(mAuth.getCurrentUser().getUid())) {
            String click_action = remoteMessage.getNotification().getClickAction();
            String dataMessage = remoteMessage.getData().get("message");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(remoteMessage.getNotification().getTitle()).setContentText(remoteMessage.getNotification().getBody());

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("dataMessage", dataMessage);
            resultIntent.putExtra("dataFrom", dataFrom);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            int mNoficationId = (int) System.currentTimeMillis();
            NotificationManager mNotifi = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifi.notify(mNoficationId, mBuilder.build());
        }
    }
}
