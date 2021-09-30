package lowblow.AnnoyingAlarm.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.AnnoyingAlarm.activity.AlarmSettingActivity
import lowblow.AnnoyingAlarm.data.Mode
import lowblow.AnnoyingAlarm.system_manager.PreferenceManager
import lowblow.AnnoyingAlarm.data.alarm.AlarmEntity
import lowblow.AnnoyingAlarm.databinding.ItemAlarmBinding
import lowblow.AnnoyingAlarm.system_manager.AlarmController
import lowblow.AnnoyingAlarm.system_manager.DataController
import java.io.Serializable
import kotlin.math.pow

class AlarmListAdapter(
    private val context: Context
) : ListAdapter<AlarmEntity, AlarmListAdapter.ViewHolder>(diffUtil), Serializable {

    inner class ViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
                    AlarmController(context).cancelAlarm(entity)
                } else {
                    imageView.setColorFilter(Color.parseColor("#ff000000"), PorterDuff.Mode.SRC_IN)
                    entity.activated = true
                    AlarmController(context).setAlarm(entity)
                }
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, AlarmSettingActivity::class.java).apply {
                    putExtra("id", entity.id)
                }
                context.startActivity(intent)
            }

            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(context)

                builder.setTitle("알람 삭제").setMessage("알람을 삭제하시겠습니까?")

                builder.setPositiveButton("좋아") { _, _ ->
                    DataController(context).alarmDataDelete(entity)
                    CoroutineScope(Dispatchers.Main).launch {
                        this@AlarmListAdapter.submitList(DataController(context).getAllAlarmData())
                    }
                }

                builder.setNegativeButton("싫어") { _, _ -> }

                builder.create().show()

                return@setOnLongClickListener true
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
                binding.alarmDaysLinearLayout.getChildAt(i).isGone =
                    (entity.days and (2.0).pow(i).toInt()) == 0
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
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<AlarmEntity>() {

            override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}