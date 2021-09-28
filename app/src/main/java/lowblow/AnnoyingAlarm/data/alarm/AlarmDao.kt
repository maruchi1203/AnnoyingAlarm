package lowblow.AnnoyingAlarm.data.alarm

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface AlarmDao {

    @Query("SELECT * FROM AlarmEntity")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Query("SELECT * FROM AlarmEntity WHERE id = :id")
    suspend fun getAlarm(id: Int): AlarmEntity

    @Insert
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)
}