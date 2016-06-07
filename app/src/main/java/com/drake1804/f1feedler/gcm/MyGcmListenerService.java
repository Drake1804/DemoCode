package com.drake1804.f1feedler.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.drake1804.f1feedler.R;
import com.drake1804.f1feedler.model.NewsFeedModel;
import com.drake1804.f1feedler.model.NewsModel;
import com.drake1804.f1feedler.view.DetailsActivity;
import com.drake1804.f1feedler.view.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Pavel.Shkaran on 6/7/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Gson gson = new GsonBuilder()
                .setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
                .create();
        NewsFeedModel newsFeedModel = gson.fromJson(data.getString("news"), NewsFeedModel.class);
        saveNewsLinks(newsFeedModel);

        sendNotification(newsFeedModel);
    }

    private void saveNewsLinks(final NewsFeedModel model){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm.copyToRealmOrUpdate(model));
    }


    private void sendNotification(NewsFeedModel model) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("uuid", model.getUuid());
        intent.putExtra("title", model.getTitle());
        intent.putExtra("link", model.getLink());
        intent.putExtra("imageUrl", model.getImageUrl());
        intent.putExtra("logoUrl", model.getResource().getImageUrl());
        intent.putExtra("resource", model.getResource().getTitle());
        intent.putExtra("date", model.getCreatingDate().getTime());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(model.getTitle())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
