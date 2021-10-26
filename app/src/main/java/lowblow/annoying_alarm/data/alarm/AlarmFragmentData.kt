package lowblow.annoying_alarm.data.alarm

import lowblow.annoying_alarm.data.AlarmType

data class AlarmFragmentData(
    val alarmUri: String?,
    val vibration: Boolean,
    val loudness: Float,
    val gentleAlarm: Boolean,
    val alarmType: AlarmType
)
