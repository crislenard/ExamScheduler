package edu.jru.deleon.crislenard.examscheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = 1010;
    private DbHandler admin;
    private Cursor fila;
    private SQLiteDatabase bd;
    private String alarm,desc,title,coursecode;



    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MyTestService.class);
        context.startService(i);
        Calendar calendar = Calendar.getInstance();
        int day, min,month,year,hours;
        String cadenaF, cadenaH,yearsystem,hoursystem;

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH)+1;
        year = calendar.get(Calendar.YEAR);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        yearsystem=day+"/"+month+"/"+year;
        hoursystem=hours+":"+min;
        admin = new DbHandler(context);
        bd = admin.getWritableDatabase();


            int beforehour = hours - 1;
            String beforetime = beforehour+":"+min;
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            Date date = null;
            Date before = null;
            try {
                date = fmt.parse(hoursystem);
                before = fmt.parse(beforetime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat frmtOut = new SimpleDateFormat("hh:mm a");
            String formattedTime = frmtOut.format(date);
            String alarmTime = frmtOut.format(before);



            if(bd!=null) {
                fila = bd.rawQuery("SELECT * FROM exam_table WHERE date='"+yearsystem+"' AND status='Upcoming' AND time='"+formattedTime+"'", null);
                if(fila.moveToFirst()){
                    alarm=fila.getString(0);
                    title=fila.getString(1);
                    desc =fila.getString(2);
                    coursecode = fila.getString(1);
                    admin.updateStatus(coursecode);
                    triggerNotification(context,title+"\n"+desc);

                }
              //  fila = bd.rawQuery("SELECT * FROM exam_table WHERE date='"+yearsystem+"' AND status='Upcoming' AND beforetime='"+alarmTime+"'", null);
            //    if(fila.moveToFirst()){
             //       alarm=fila.getString(0);
             //       title=fila.getString(1);
            //        desc =fila.getString(2);
             //       coursecode = fila.getString(1);
            //        triggerNotificationBefore(context,title+"\n"+desc);
           //     }

            }
            bd.close();




    }

    private void triggerNotification(Context contexto, String t) {
        Intent notificationIntent = new Intent(contexto, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = new long[]{2000, 1000, 2000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
        builder.setContentIntent(contentIntent)

                .setTicker("")
                .setContentTitle("YOU HAVE EXAM FOR "+title+ " TODAY")
                .setContentText("God Bless to your Exam! Do your best!")
                .setContentInfo("Info")
                .setLargeIcon(BitmapFactory.decodeResource(contexto.getResources(), R.drawable.gradreminder))
                .setSmallIcon(R.drawable.reminder)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setVibrate(pattern);

        Notification notificacion = new NotificationCompat.BigTextStyle(builder)
                .bigText(t)
                .setBigContentTitle("YOU HAVE EXAM FOR "+title+ " TODAY")
                .setSummaryText("God Bless to your Exam! Do your best!")
                .build();

        notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificacion);
    }


    private void triggerNotificationBefore(Context contexto, String t) {
        Intent notificationIntent = new Intent(contexto, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(contexto, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = new long[]{2000, 1000, 2000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(contexto);
        builder.setContentIntent(contentIntent)

                .setTicker("")
                .setContentTitle("YOU HAVE EXAM FOR "+title+ " TODAY!")
                .setContentText("Your exam starts now! God bless!")
                .setContentInfo("Info")
                .setLargeIcon(BitmapFactory.decodeResource(contexto.getResources(), R.drawable.gradreminder))
                .setSmallIcon(R.drawable.reminder)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setVibrate(pattern);

        Notification notificacion = new NotificationCompat.BigTextStyle(builder)
                .bigText(t)
                .setBigContentTitle("YOU HAVE EXAM FOR "+title+ " TODAY")
                .setSummaryText("Your exam starts now! God bless!")
                .build();

        notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificacion);
    }

}
