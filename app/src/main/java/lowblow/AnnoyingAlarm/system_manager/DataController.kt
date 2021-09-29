package lowblow.AnnoyingAlarm.system_manager

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.*
import lowblow.AnnoyingAlarm.data.alarm.AlarmDatabase
import lowblow.AnnoyingAlarm.data.alarm.AlarmEntity

class DataController(val context: Context) {

    private val database = AlarmDatabase.getInstance(context.applicationContext)

    suspend fun getAlarmData(id: Int): AlarmEntity? {

        var alarmEntity : AlarmEntity? = null

        val job = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                alarmEntity = database!!.alarmDao().getAlarm(id)
            }
        }

        job.join()
        return alarmEntity

    }

    suspend fun getAllAlarmData() : List<AlarmEntity> {
        var alarmEntityList = emptyList<AlarmEntity>()

        val job = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                alarmEntityList = database!!.alarmDao().getAllAlarms()
            }
        }

        job.join()
        return alarmEntityList
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun alarmDataCreate(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().insertAlarm(alarmEntity)

            withContext(Dispatchers.Main) {
                AlarmController(context).setAlarm(alarmEntity)
            }
        }

    }

    fun alarmDataUpdate(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().updateAlarm(alarmEntity)

            withContext(Dispatchers.Main) {
                AlarmController(context).setAlarm(alarmEntity)
            }
        }

    }

    fun alarmDataDelete(alarmEntity: AlarmEntity) {

        CoroutineScope(Dispatchers.IO).launch {
            database!!.alarmDao().deleteAlarm(alarmEntity)

            withContext(Dispatchers.Main) {
                AlarmController(context).cancelAlarm(alarmEntity)
            }
        }

    }

}