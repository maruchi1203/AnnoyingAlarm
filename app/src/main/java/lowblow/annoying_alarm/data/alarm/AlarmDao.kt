package lowblow.annoying_alarm.data.alarm

import androidx.room.*

@Dao
interface AlarmDao {

    @Query("SELECT * FROM AlarmEntity")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Query("SELECT COUNT(*) FROM AlarmEntity")
    suspend fun getAmount(): Int

    @Query("SELECT * FROM AlarmEntity WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity

    @Insert
    suspend fun insertAlarm(alarm: AlarmEntity) : Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}