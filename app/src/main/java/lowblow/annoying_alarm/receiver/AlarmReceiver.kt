package lowblow.annoying_alarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager.STREAM_ALARM
import android.net.Uri
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
    private var alarmType by Delegates.notNull<Int>()

    override fun onReceive(context: Context, intent: Intent) {
        id = intent.extras!!.getLong("id")
        alarmType = intent.extras!!.getInt("alarmType")

        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        CoroutineScope(Dispatchers.Main).launch {
            val alarmEntity = DataController(context).getAlarmData(id)

            createNotificationChannel()
            if (alarmEntity != null) {
                notifyNotification(context, alarmEntity)
            }

            wakeUpAlarm(context)
        }
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

    private fun notifyNotification(context: Context, alarmEntity: AlarmEntity) {
        val build = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("알람")
            .setContentText("일어나십시오 휴먼")
            .setSmallIcon(R.drawable.ic_baseline_access_alarms_36)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if(alarmEntity.vibration)
            build.setVibrate(longArrayOf(500, 500, 500, 500, 500, 500))
        if(alarmEntity.alarmUri != null)
            build.setSound(Uri.parse(alarmEntity.alarmUri), STREAM_ALARM)


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