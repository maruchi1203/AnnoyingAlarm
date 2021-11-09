package lowblow.annoying_alarm.data.alarm

import lowblow.annoying_alarm.data.AlarmType
import java.io.Serializable

data class AlarmFragmentData(
    val vibration: Boolean,
    val volume: Float,
    val alarmType: AlarmType,
    val alarmUri: String?,
    val temp: String?
) : Serializable
