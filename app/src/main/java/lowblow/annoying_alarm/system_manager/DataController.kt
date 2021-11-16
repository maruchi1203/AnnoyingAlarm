package lowblow.annoying_alarm.system_manager

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.*
import lowblow.annoying_alarm.data.alarm.AlarmDatabase
import lowblow.annoying_alarm.data.alarm.AlarmEntity

class DataController(val context: Context) {

    private val database = AlarmDatabase.getInstance(context.applicationContext)

    suspend fun getAlarmData(id: Long): AlarmEntity? {

        var alarmEntity : AlarmEntity? = null

        val job = CoroutineScope(Dispatchers.IO).launch {
            alarmEntity = database!!.alarmDao().getAlarmById(id)
        }

        job.join()
        return alarmEntity

    }

    suspend fun getAllAlarmData() : List<AlarmEntity> {
        var alarmEntityList = emptyList<AlarmEntity>()

        val job = CoroutineScope(Dispatchers.IO).launch {
            alarmEntityList = database!!.alarmDao().getAllAlarms()
        }

        job.join()
        return alarmEntityList
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun alarmDataCreate(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            val id = database!!.alarmDao().insertAlarm(alarmEntity)
            AlarmController(context).setAlarmState(id, alarmEntity)
        }

    }

    fun alarmDataUpdate(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().updateAlarm(alarmEntity)
            AlarmController(context).setAlarmState(alarmEntity.id, alarmEntity)
        }
    }

    fun alarmDataDelete(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.Main).launch {
            database!!.alarmDao().deleteAlarm(alarmEntity)
            alarmEntity.activated = false
            AlarmController(context).setAlarmState(alarmEntity.id, alarmEntity)
        }

    }

}