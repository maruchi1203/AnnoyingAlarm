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
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeCustomActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeMosquitoActivity
import lowblow.annoying_alarm.data.AlarmType
import lowblow.annoying_alarm.data.alarm.AlarmEntity
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
}