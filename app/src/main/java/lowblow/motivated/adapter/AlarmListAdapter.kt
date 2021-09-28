package lowblow.motivated.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import lowblow.motivated.activity.AlarmSettingActivity
import lowblow.motivated.data.Mode
import lowblow.motivated.system_manager.PreferenceManager
import lowblow.motivated.data.alarm.AlarmEntity
import lowblow.motivated.databinding.ItemAlarmBinding
import kotlin.math.pow

class AlarmListAdapter(private val alarmList: List<AlarmEntity>, private val context: Context) :
    RecyclerView.Adapter<AlarmListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(entity: AlarmEntity) {
            val imageView = binding.alarmImageView

            binding.alarmItemModeTextView.text = when (entity.mode) {
                Mode.FRAGMENT_CUSTOM -> "커스텀 알람"
                Mode.FRAGMENT_SIREN -> "사이렌"
                Mode.FRAGMENT_MESSENGER -> "메신저"
                Mode.FRAGMENT_MOSQUITO -> "모기 습격"
            }

            imageView.setOnClickListener {
                if (entity.activated) {
                    imageView.setColorFilter(Color.parseColor("#ffcccccc"), PorterDuff.Mode.SRC_IN)
                    entity.activated = false
                } else {
                    imageView.setColorFilter(Color.parseColor("#ff000000"), PorterDuff.Mode.SRC_IN)
                    entity.activated = true
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, AlarmSettingActivity::class.java).apply {
                    putExtra("id", entity.id)
                }
                context.startActivity(intent)
            }

            binding.alarmTitleTextView.text = if (entity.memo == "") "알람" else entity.memo

            binding.alarmTimeTextView.text = if (PreferenceManager(context).getBoolean("isAmPm")) {
                "%s %02d : %02d".format(
                    (if (entity.hour < 12) "오전" else "오후"),
                    (if (entity.hour % 12 == 0) 12 else entity.hour % 12),
                    entity.minute
                )
            } else {
                "%02d : %02d".format(
                    entity.hour,
                    entity.minute
                )
            }

            for (i in 0 until binding.alarmDaysLinearLayout.childCount) {
                binding.alarmDaysLinearLayout.getChildAt(i).isGone = (entity.days % (2.0).pow(i)
                    .toInt()) == 0
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }

    override fun getItemCount(): Int = alarmList.size

}