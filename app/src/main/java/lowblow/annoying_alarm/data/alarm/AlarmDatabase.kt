package lowblow.annoying_alarm.data.alarm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AlarmEntity::class], version = 1)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        private var alarmDatabase: AlarmDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AlarmDatabase? {
            if (alarmDatabase == null) {
                synchronized(AlarmDatabase::class) {
                    alarmDatabase = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        "User_Database"
                    ).build()
                }
            }
            return alarmDatabase
        }
    }
}