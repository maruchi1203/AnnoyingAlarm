package lowblow.annoying_alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeCustomActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeMessengerActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeMosquitoActivity
import lowblow.annoying_alarm.activity.wakeup_activity.AlarmWakeSirenActivity
import lowblow.annoying_alarm.data.AlarmType
import kotlin.properties.Delegates

class AlarmReceiver : BroadcastReceiver() {

    private var id by Delegates.notNull<Long>()
    private var alarmType by Delegates.notNull<Int>()

    override fun onReceive(context: Context, intent: Intent) {
        id = intent.extras!!.getLong("id")
        alarmType = intent.extras!!.getInt("alarmType")

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
                AlarmType.FRAGMENT_SIREN.ordinal -> setClass(
                    context,
                    AlarmWakeSirenActivity::class.java
                )
                AlarmType.FRAGMENT_MESSENGER.ordinal -> setClass(
                    context,
                    AlarmWakeMessengerActivity::class.java
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