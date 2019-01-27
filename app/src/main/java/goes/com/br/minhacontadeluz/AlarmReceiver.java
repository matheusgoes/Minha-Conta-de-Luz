package goes.com.br.minhacontadeluz;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by matheusgoes on 21/07/15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    
    static final String CHECKBOX_ADDITIONAL_BILL="pref_checkbox_additional_bill";
    static final String CHECKBOX_RED_FLAG="pref_checkbox_red_flag";
    static final String CHECKBOX_PUBLIC_ILLUMINATION = "pref_checkbox_public_illumination";
    static final String VALUE_KWH="value_kwh";
    static final String VALUE_PUBLIC_ILLUMINATION = "value_public_illumination";
    static final String DAY_PICKER = "day";
    static final String NOTIFICATIONS_CHECKBOX="set_notifications";


    private ArrayList<Device> devices;
    private SharedPreferences sharedPreferences;
    private static Database_Acesso db_acesso;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Notification n;
    NotificationManager mNotificationManager;
    private boolean hasAdditionalBill = false, hasRedFlag = false;
    private int monthDay = 0;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALARM RECEIVER", "on Receive");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getBoolean(NOTIFICATIONS_CHECKBOX, true)){
            //Define intent da notificação
            mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Intent i = new Intent(context , MainActivity.class);
            //Define acão da intent
            i.setAction("New_bill");
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);
            //Constroi notificação
            n  = new Notification.Builder(context)
                    .setContentTitle("Hoje é o dia do vencimento de sua conta!")
                    .setContentText("Clique aqui para verificar o valor")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancelar", pIntent).build();
            //Exibe notificação
            mNotificationManager.notify(0, n);
        }else{
            cancelAlarm(context);
        }
        completeWakefulIntent(intent);
    }

    public void setAlarm(Context context){
        Log.i("ALARM RECEIVER", "SETTING ALARM");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("alarmset", false)){
            Calendar c = Calendar.getInstance();

            sharedPreferences.edit().putInt(DAY_PICKER, c.get(Calendar.DAY_OF_MONTH)).commit();

            int numberOfDaysInActualMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            long month_interval = AlarmManager.INTERVAL_DAY * numberOfDaysInActualMonth;

            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    month_interval, alarmIntent);
            sharedPreferences.edit().putBoolean("alarmset", true);
        }else{
            if(n!=null){
                mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
            }
        }
    }
    public void cancelAlarm(Context context){
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmMgr.cancel(alarmIntent);
            Log.i("ALARM RECEIVER", "Cancel: Alarm Cancelled");
        }else{
            Log.i("ALARM RECEIVER", "Cancel: alarm = null");
        }
    }
}
