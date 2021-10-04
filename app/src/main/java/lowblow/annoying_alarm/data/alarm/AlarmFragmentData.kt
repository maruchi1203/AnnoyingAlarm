package lowblow.annoying_alarm.data.alarm

import lowblow.annoying_alarm.data.Mode

data class AlarmFragmentData(
    val alarmUri: String?,
    val vibration: Boolean,
    val loudness: Float,
    val gentleAlarm: Boolean,
    val mode: Mode
)
