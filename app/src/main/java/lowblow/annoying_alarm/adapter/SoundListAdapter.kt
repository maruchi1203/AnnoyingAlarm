package lowblow.annoying_alarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lowblow.annoying_alarm.data.sound.AlarmSound
import lowblow.annoying_alarm.databinding.ItemAlarmSoundBinding

class SoundListAdapter(val selectMusic: (String, String) -> Unit) :
    ListAdapter<AlarmSound, SoundListAdapter.ViewHolder>(diffUtil) {

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
        holder.bind(getItem(position))
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<AlarmSound>() {
            override fun areItemsTheSame(oldItem: AlarmSound, newItem: AlarmSound): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: AlarmSound, newItem: AlarmSound): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}