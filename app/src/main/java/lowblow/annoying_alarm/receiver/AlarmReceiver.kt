package lowblow.annoying_alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeCustomActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeMosquitoActivity
import lowblow.annoying_alarm.data.AlarmType
import kotlin.properties.Delegates

class AlarmReceiver : BroadcastReceiver() {

//    private lateinit var notificationManager: NotificationManager

    private var id by Delegates.notNull<Long>()
    private var alarmType by Delegates.notNull<Int>()

    override fun onReceive(context: Context, intent: Intent) {
        id = intent.extras!!.getLong("id")
        alarmType = intent.extras!!.getInt("alarmType")

//        notificationManager = context.getSystemService(
//            Context.NOTIFICATION_SERVICE
//        ) as NotificationManager

        wakeUpAlarm(context)
    }

    private fun wakeUpAlarm(context: Context) {
        val alarmIntent = Intent("android.intent.action.sec")

        alarmIntent.apply {
            when (alarmType) {
                AlarmType.FRAGMENT_CUSTOM.ordinal -> setClass(
                    context,
                    AlarmWakeCustomActivity::class.java
                )
                AlarmType.FRAGMENT_MOSQUITO.ordinal -> setClass(
                    context,
                    AlarmWakeMosquitoActivity::class.java
                )
                else -> setClass(
                    context,
                    AlarmWakeCustomActivity::class.java
                )
            }

            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            putExtra("id", id)
        }

        context.startActivity(alarmIntent)
    }

//    private fun notifyNotification(context: Context) {
//        val build = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setContentTitle("알람")
//            .setContentText("일어날 시간입니다")
//            .setSmallIcon(R.drawable.ic_baseline_access_alarms_36)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//
//        notificationManager.notify(NOTIFICATION_ID, build.build())
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                CHANNEL_ID,
//                "Alarm Notification",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }
//
//    companion object {
//        const val NOTIFICATION_ID = 0
//        const val CHANNEL_ID = "Alarm Channel"
//    }
}