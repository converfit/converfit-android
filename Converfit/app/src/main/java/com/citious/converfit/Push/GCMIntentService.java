package com.citious.converfit.Push;

import com.citious.converfit.Actividades.UserAcces.MainActivity;
import com.citious.converfit.Utils.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.citious.converfit.R;
import static com.citious.converfit.Utils.UtilidadesGCM.*;

public class GCMIntentService extends IntentService
{
    private static final int NOTIF_ALERTA_ID = 1;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String titulo = "";
        String mensaje = "";
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                titulo = extras.getString("title");
                mensaje = extras.getString("message");
                Utils.pushConversationKey = extras.getString("conversationKey");

            }if(actividadAbierta){
                mostrarMensaje(getApplicationContext(),mensaje);
            }else {
                mostrarNotification(titulo, mensaje);
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void mostrarNotification(String titulo, String mensaje)
    {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.push)
                        .setColor(getResources().getColor(R.color.RojoCabecera))
                        .setAutoCancel(true)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setDefaults(Notification.DEFAULT_ALL);

        Intent notIntent =  new Intent(this, MainActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(
                this, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
    }
}