package android.trithe.real.server;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.trithe.real.R;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessageService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String dataFrom = remoteMessage.getData().get("from_id");
        String dataTo = remoteMessage.getData().get("to_id");
        if (!Objects.requireNonNull(dataTo).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
            String click_action = Objects.requireNonNull(remoteMessage.getNotification()).getClickAction();
            String dataMessage = remoteMessage.getData().get("message");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(remoteMessage.getNotification().getTitle()).setContentText(remoteMessage.getNotification().getBody());

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("dataMessage", dataMessage);
            resultIntent.putExtra("user_id", dataFrom);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            int mNoficationId = (int) System.currentTimeMillis();
            NotificationManager mNotifi = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifi.notify(mNoficationId, mBuilder.build());
        }
    }

    private void checkBigImage(FirebaseFirestore firebaseFirestore, String blog_id, String dataTo,
                               RemoteMessage remoteMessage, FirebaseAuth mAuth) {
        firebaseFirestore.collection("Posts").document(blog_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(task.getResult().getString("image_url"))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                if (!Objects.requireNonNull(dataTo).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                                    String click_action = Objects.requireNonNull(remoteMessage.getNotification()).getClickAction();
                                    String dataMessage = remoteMessage.getData().get("message");
                                    NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
                                    bpStyle.bigPicture(resource);
                                    Intent resultIntent = new Intent(click_action);
                                    resultIntent.putExtra("dataMessage", dataMessage);
                                    resultIntent.putExtra("user_id", blog_id);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.logo)
                                            .setContentTitle(remoteMessage.getNotification().getTitle())
                                            .setContentText(remoteMessage.getNotification().getBody())
                                            .addAction(R.drawable.love, "Watch", pendingIntent)
                                            .setStyle(bpStyle);
                                    mBuilder.setContentIntent(pendingIntent);
                                    int idNotification = (int) System.currentTimeMillis();
                                    NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    mNotification.notify(idNotification, mBuilder.build());
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                pushNormalNotification(blog_id, remoteMessage, dataTo, mAuth);
            }
        });
    }

    private void pushNormalNotification(String blog_id, RemoteMessage remoteMessage, String dataTo, FirebaseAuth mAuth) {
        if (!Objects.requireNonNull(dataTo).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
            String click_action = Objects.requireNonNull(remoteMessage.getNotification()).getClickAction();
            String dataMessage = remoteMessage.getData().get("message");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody());

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("dataMessage", dataMessage);
            resultIntent.putExtra("user_id", blog_id);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            int idNotification = (int) System.currentTimeMillis();
            NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotification.notify(idNotification, mBuilder.build());
        }
    }
}
