package lowblow.annoying_alarm.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import lowblow.annoying_alarm.adapter.SoundListAdapter
import lowblow.annoying_alarm.data.sound.AlarmSound
import lowblow.annoying_alarm.databinding.ActivityChooseAlarmSoundBinding

class AlarmSoundActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChooseAlarmSoundBinding.inflate(layoutInflater)
    }

    private val mp3ListArray: MutableList<AlarmSound> = mutableListOf()

    private lateinit var selectedUri: String
    private lateinit var selectedTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding.alarmSoundRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.alarmSoundRecyclerView.adapter = SoundListAdapter() { uri, title ->
            selectedUri = uri
            selectedTitle = title

            val resultIntent = Intent().apply {
                putExtra("selectedUri", selectedUri)
                putExtra("selectedTitle", selectedTitle)
            }

            setResult(REQUEST_CODE, resultIntent)

            finish()
        }

        (binding.alarmSoundRecyclerView.adapter as SoundListAdapter).submitList(loadRecycleItemData())

        (binding.alarmSoundRecyclerView.adapter as? SoundListAdapter)?.apply {
            notifyDataSetChanged()
        }
    }

    private fun loadRecycleItemData(): MutableList<AlarmSound> {
        val soundType = intent.getStringExtra("soundType")
        val state = Environment.getExternalStorageState()

        if (Environment.MEDIA_MOUNTED != state &&
            Environment.MEDIA_MOUNTED_READ_ONLY != state
        ) {
            Toast.makeText(this, "외부 저장소 권한이 승인되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val uriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        //IS_RINGTONE, IS_ALARM.... << These are compared with INTEGER
        val selection = "$soundType != 0"

        val cursor = contentResolver.query(
            uriExternal,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        if (cursor!!.count != 0) {

            while (cursor.moveToNext()) {

                val contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                )

                mp3ListArray.add(
                    AlarmSound(
                        contentUri,
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    )
                )
            }
            cursor.close()
        }

        return mp3ListArray
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}