package lowblow.annoying_alarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.activity.AlarmWakeUpActivity

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val id = intent.extras!!.getLong("id")
        wakeUpAlarm(context, id)
    }

    private fun wakeUpAlarm(context: Context, id: Long) {
        val alarmIntent = Intent("android.intent.action.sec")

        alarmIntent.apply {
            setClass(context, AlarmWakeUpActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            putExtra("id", id)
        }

        context.startActivity(alarmIntent)
    }

    private fun notifyNotification(context: Context) {
        val build = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("알람")
            .setContentText("일어날 시간입니다")
            .setSmallIcon(R.drawable.ic_baseline_access_alarms_36)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notificationManager.notify(NOTIFICATION_ID, build.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "Alarm Channel"
    }

}