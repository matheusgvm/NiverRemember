package com.vilaca.niverremember.notificacao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vilaca.niverremember.R;
import com.vilaca.niverremember.activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Cria a notificação
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "TESTE")
                .setSmallIcon(R.drawable.ic_cake_24dp)
                .setContentTitle("a")
                .setContentText("b")
                .setAutoCancel(true);

        //Checa a versão e define se deve ser colocado um ícone grande na notificação
        //(Isso pode ser feito para qualquer versão, mas eu acho que não fica muito bom em versões
        //mais antigas do Android).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_cake_24dp);
            mBuilder.setLargeIcon(bm);
        }

        //Cria uma intent indicanado que activity será chamada quando a notificação for clicada
        Intent resultIntent = new Intent(context, MainActivity.class);

        //Se a activity aberta pela notificação não for exclusiva da notificação é necessário criar
        //definir uma pilha
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        //Envia a noitificação para o usuário
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(5, mBuilder.build());
    }}