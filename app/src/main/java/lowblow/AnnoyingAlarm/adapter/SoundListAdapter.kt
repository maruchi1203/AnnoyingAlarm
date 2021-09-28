package lowblow.AnnoyingAlarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lowblow.AnnoyingAlarm.data.sound.AlarmSound
import lowblow.AnnoyingAlarm.databinding.ItemAlarmSoundBinding

class SoundListAdapter(private val mp3List : List<AlarmSound>, val selectMusic: (String, String) -> Unit) : RecyclerView.Adapter<SoundListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAlarmSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alarmSound : AlarmSound) {
            binding.alarmSoundTitleTextView.text = alarmSound.title
            binding.alarmSoundArtistTextView.text = alarmSound.artist

            binding.root.setOnClickListener {
                selectMusic(alarmSound.uri.toString(), alarmSound.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlarmSoundBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mp3List[position])
    }

    override fun getItemCount(): Int {
        return mp3List.size
    }


}