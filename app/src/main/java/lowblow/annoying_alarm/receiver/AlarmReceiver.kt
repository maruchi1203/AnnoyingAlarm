package lowblow.annoying_alarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeCustomActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeMosquitoActivity
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.system_manager.DataController
import kotlin.properties.Delegates

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    private var id by Delegates.notNull<Long>()

    override fun onReceive(context: Context, intent: Intent) {
        id = intent.extras!!.getLong("id")

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager


        CoroutineScope(Dispatchers.Main).launch {
            val alarmEntity = DataController(context).getAlarmData(id)

                createNotificationChannel()
                notifyNotification(context, alarmEntity!!)

                wakeUpAlarm(context, alarmEntity)
        }
    }

    private fun wakeUpAlarm(context: Context, alarmEntity: AlarmEntity) {
        val alarmIntent = Intent("android.intent.action.sec")

        alarmIntent.apply {
            when (alarmEntity.alarmType) {
                AlarmType.FRAGMENT_CUSTOM -> setClass(
                    context,
                    AlarmWakeCustomActivity::class.java
                )
                AlarmType.FRAGMENT_MOSQUITO -> setClass(
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

    private fun notifyNotification(context: Context, alarmEntity: AlarmEntity) {
        val build = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentTitle("알람 종료 알림")
            setContentText("여길 누르면 알람이 완전히 종료됩니다")
            setSmallIcon(R.drawable.ic_baseline_access_alarms_36)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
        }

        if (alarmEntity.days == 0) {
            DataController(context).alarmDataDelete(alarmEntity)
        } else {
            alarmEntity.snooze = false
            DataController(context).alarmDataUpdate(alarmEntity)
        }

        notificationManager.notify(NOTIFICATION_ID, build.build())
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "Alarm Channel"
    }
}