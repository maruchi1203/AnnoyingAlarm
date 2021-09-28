package lowblow.motivated.data.alarm

import android.net.Uri
import lowblow.motivated.data.Mode

data class AlarmFragmentData(
    val alarmUri: String?,
    val vibration: Boolean,
    val loudness: Float,
    val gentleAlarm: Boolean,
    val mode: Mode
)
