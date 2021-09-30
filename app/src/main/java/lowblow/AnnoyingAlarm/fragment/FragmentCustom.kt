package lowblow.AnnoyingAlarm.fragment

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import lowblow.AnnoyingAlarm.activity.AlarmSoundActivity
import lowblow.AnnoyingAlarm.R
import lowblow.AnnoyingAlarm.data.Mode
import lowblow.AnnoyingAlarm.data.alarm.AlarmFragmentData
import lowblow.AnnoyingAlarm.databinding.FragmentCustomBinding

class FragmentCustom : FragmentParent() {

    private lateinit var binding: FragmentCustomBinding

    private val media = MediaPlayer()

    private var selectedUri: Uri? = null

    private var isPaused: Boolean = true

    //진동 버튼
    private lateinit var vibrationSwitch: SwitchCompat

    private var loudness: Float = 1.toFloat()
    private var isVibrate = true
    private var isGentleAlarm = false

    private lateinit var resultListener: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomBinding.inflate(layoutInflater)
        vibrationSwitch = binding.alarmVibrationSwitchCompat

        initMusicChangeButton()
        initAlarmPlayTestButton()
        initVibrationSwitch()
        initSeekbar()
        initGentleAlarm()

        return binding.root
    }

    private fun initMusicChangeButton() {

        resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_CODE && result.data != null) {
                    selectedUri = Uri.parse(result.data!!.getStringExtra("selectedUri"))
                    binding.alarmMusicChangeButton.text =
                        result.data!!.getStringExtra("selectedTitle")
                } else {
                    binding.alarmMusicChangeButton.text = "무음"
                }
            }

        binding.alarmMusicChangeButton.setOnClickListener {
            resultListener.launch(Intent(requireContext(), AlarmSoundActivity::class.java))
        }
    }

    private fun initAlarmPlayTestButton() {

        binding.alarmPlayTestButton.setOnClickListener {
            if (isPaused) {
                if (selectedUri != null) {
                    media.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    media.setDataSource(requireContext(), selectedUri!!)
                    media.prepare()
                    media.start()

                    isPaused = false
                    binding.alarmPlayTestButton.setImageResource(R.drawable.ic_baseline_stop_24)
                } else {
                    Toast.makeText(requireContext(), "selectedUri 미설정", Toast.LENGTH_SHORT).show()
                }
            } else {
                media.reset()
                isPaused = true
                binding.alarmPlayTestButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    private fun initVibrationSwitch() {
        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            isVibrate = !isChecked
            if (isVibrate) {
                vibrationSwitch.setThumbResource(R.drawable.ic_baseline_vibration_24)
            } else {
                vibrationSwitch.setThumbResource(R.drawable.ic_baseline_vibration_cancel_24)
            }
        }
    }

    private fun initSeekbar() {
        binding.alarmSoundControlSeekBar.progress = 100
        loudness = binding.alarmSoundControlSeekBar.progress.toFloat() / 100
        media.setVolume(loudness, loudness)

        binding.alarmSoundControlSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                loudness = progress.toFloat() / 100
                media.setVolume(loudness, loudness)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}


        })
    }

    private fun initGentleAlarm() {
        binding.alarmGentleSoundSwitch.setOnCheckedChangeListener { _, isChecked ->
            isGentleAlarm = isChecked
        }
    }

    override fun getData(): AlarmFragmentData {

        return AlarmFragmentData(
            selectedUri?.toString(),
            isVibrate,
            loudness,
            isGentleAlarm,
            Mode.FRAGMENT_CUSTOM
        )

    }

    companion object {
        const val RESULT_CODE = 101
    }
}