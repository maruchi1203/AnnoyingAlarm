package lowblow.motivated.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import lowblow.motivated.adapter.SoundListAdapter
import lowblow.motivated.data.sound.AlarmSound
import lowblow.motivated.databinding.ActivityChooseAlarmSoundBinding

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
        binding.alarmSoundRecyclerView.adapter = SoundListAdapter(loadRecycleItemData()) { uri, title ->
            selectedUri = uri
            selectedTitle = title

            val resultIntent = Intent().apply {
                putExtra("selectedUri", selectedUri)
                putExtra("selectedTitle", selectedTitle)
            }

            setResult(REQUEST_CODE, resultIntent)

            finish()
        }

        (binding.alarmSoundRecyclerView.adapter as? SoundListAdapter)?.apply {
            notifyDataSetChanged()
        }
    }

    private fun loadRecycleItemData(): MutableList<AlarmSound> {
        val state = Environment.getExternalStorageState()

        if (Environment.MEDIA_MOUNTED != state &&
            Environment.MEDIA_MOUNTED_READ_ONLY != state
        ) {
            Toast.makeText(this, "외부 저장소 권한이 승인되지 않았습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val uriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3")

        val cursor = contentResolver.query(
            uriExternal,
            projection,
            selectionMimeType,
            arrayOf(mimeType),
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