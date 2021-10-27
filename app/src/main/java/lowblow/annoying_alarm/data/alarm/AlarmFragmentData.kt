package lowblow.annoying_alarm.data.alarm

import lowblow.annoying_alarm.data.AlarmType

data class AlarmFragmentData(
    val alarmUri: String?,
    val vibration: Boolean,
    val volume: Float,
    val alarmType: AlarmType
)
