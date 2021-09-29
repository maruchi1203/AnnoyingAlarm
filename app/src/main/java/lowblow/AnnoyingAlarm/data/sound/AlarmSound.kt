package lowblow.AnnoyingAlarm.data.sound

import android.net.Uri

data class AlarmSound(
    val uri: Uri,
    val id: Int,
    val title: String,
    val artist: String
)
