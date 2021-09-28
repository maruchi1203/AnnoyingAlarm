package lowblow.motivated.data.alarm

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import lowblow.motivated.data.Mode
import java.util.*

@Entity
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "activated") var activated: Boolean,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "days") var days: Int,
    @ColumnInfo(name = "uri") var alarmUri: String?,
    @ColumnInfo(name = "vibration") var vibration: Boolean,
    @ColumnInfo(name = "loudness") var loudness: Float,
    @ColumnInfo(name = "gentle") var gentleAlarm: Boolean,
    @ColumnInfo(name = "memo") var memo: String,
    @ColumnInfo(name = "mode") var mode: Mode
)