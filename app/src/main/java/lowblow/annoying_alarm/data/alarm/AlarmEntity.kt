package lowblow.annoying_alarm.data.alarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import lowblow.annoying_alarm.data.AlarmType
import java.io.Serializable

@Entity
data class AlarmEntity (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "activated") var activated: Boolean,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "days") var days: Int,
    @ColumnInfo(name = "vibration") var vibration: Boolean,
    @ColumnInfo(name = "volume") var volume: Float,
    @ColumnInfo(name = "memo") var memo: String,
    @ColumnInfo(name = "type") var alarmType: AlarmType,
    @ColumnInfo(name = "snooze") var snooze: Boolean,
    @ColumnInfo(name = "alarmUri") var alarmUri: String?,
    @ColumnInfo(name = "temp") var temp: String?
)