package lowblow.annoying_alarm.system_manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.receiver.AlarmReceiver
import java.util.*
import kotlin.math.pow

class AlarmController(private val context: Context) {

    private val alarmManager =
        context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    private val intent = Intent(context, AlarmReceiver::class.java)

    @SuppressLint("SimpleDateFormat", "UnspecifiedImmutableFlag")
    fun setAlarm(id: Long, alarmEntity: AlarmEntity) {
        //알람에 맞춰져있는 시간으로 설정
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmEntity.hour)
            set(Calendar.MINUTE, alarmEntity.minute)
            set(Calendar.SECOND, 0)
        }

        //만약 calendar 에 설정된 시간이 이미 지난 경우
        if (calendar.before(Calendar.getInstance())) {

            //AlarmEntity 에서 가장 가까운 요일 검색
            if (alarmEntity.days != 0) {

                //AlarmEntity 는 일요일이 0이고, Calendar 는 일요일이 1로 되어있으니
                val dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
                for (i in 1..7) {
                    if ((alarmEntity.days and (2.0).pow((dayOfTheWeek + i) % 7).toInt()) != 0) {
                        calendar.add(Calendar.DATE, i)
                        break
                    }
                }

            } else {
                calendar.add(Calendar.DATE, 1)
            }
        }

        val bundle = Bundle()
        bundle.putLong("id", id)
        bundle.putInt("alarmType", alarmEntity.alarmType.ordinal)

        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun snoozeAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)

        val trigger = (SystemClock.elapsedRealtime() + 60 * 1000 * 5)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            SNOOZE_ALARM_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            trigger,
            pendingIntent
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelAlarm(alarmEntity: AlarmEntity) {
        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmEntity.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val SNOOZE_ALARM_CODE = -1
    }
}