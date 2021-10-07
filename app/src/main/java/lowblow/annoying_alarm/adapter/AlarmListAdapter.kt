package lowblow.annoying_alarm.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lowblow.annoying_alarm.activity.AlarmSettingActivity
import lowblow.annoying_alarm.data.Mode
import lowblow.annoying_alarm.system_manager.PreferenceManager
import lowblow.annoying_alarm.data.alarm.AlarmEntity
import lowblow.annoying_alarm.databinding.ItemAlarmBinding
import lowblow.annoying_alarm.system_manager.AlarmController
import lowblow.annoying_alarm.system_manager.DataController
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

class AlarmListAdapter(
    private val context: Context
) : ListAdapter<AlarmEntity, AlarmListAdapter.ViewHolder>(diffUtil), Serializable {

    inner class ViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: AlarmEntity) {
            val imageView = binding.alarmImageView

            // 알람 종류 표시
            binding.alarmItemModeTextView.text = when (entity.mode) {
                Mode.FRAGMENT_CUSTOM -> "커스텀 알람"
                Mode.FRAGMENT_SIREN -> "사이렌"
                Mode.FRAGMENT_MESSENGER -> "메신저"
                Mode.FRAGMENT_MOSQUITO -> "모기 습격"
            }

            //알람 작동 유무 표시
            imageView.setOnClickListener {
                if (entity.activated) {
                    imageView.setColorFilter(Color.parseColor("#ffcccccc"), PorterDuff.Mode.SRC_IN)
                    entity.activated = false
                    AlarmController(context).cancelAlarm(entity)
                } else {
                    imageView.setColorFilter(Color.parseColor("#ff000000"), PorterDuff.Mode.SRC_IN)
                    entity.activated = true
                    AlarmController(context).setAlarm(entity.id, entity)
                }
            }

            //알람 클릭시 업데이트 화면 작동
            binding.root.setOnClickListener {
                val intent = Intent(context, AlarmSettingActivity::class.java).apply {
                    putExtra("id", entity.id)
                }
                context.startActivity(intent)
            }

            //알람 길게 누를 시 삭제 화면 작동
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

            //알람 제목(메모) 표시
            binding.alarmTitleTextView.text = if (entity.memo == "") "알람" else entity.memo

            //알람 시간 표시
            binding.alarmTimeTextView.text =
                if (PreferenceManager(context.applicationContext).getBoolean("is24hour")) {
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

            //알람 반복 요일, 작동 시간 표시
            bindDaysTextView(binding, entity)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindDaysTextView(binding: ItemAlarmBinding, entity: AlarmEntity) {
        val daysText = binding.alarmDaysTextView

        when (entity.days) {
            0 -> {
                val millis = System.currentTimeMillis()
                val alarmCalendar = Calendar.getInstance().apply {
                    timeInMillis = millis
                    set(Calendar.HOUR_OF_DAY, entity.hour)
                    set(Calendar.MINUTE, entity.minute)
                    set(Calendar.SECOND, 0)
                }

                daysText.text = if (millis > alarmCalendar.timeInMillis) "내일 실행" else "오늘 실행"

            }
            127 -> {
                daysText.text = "매일"
            }
            else -> {
                var tempDaysText = ""
                for (i in 0 until 7) {
                    if ((entity.days and (2.0).pow(i).toInt()) != 0) {
                        when (i) {
                            0 -> tempDaysText += " 일"
                            1 -> tempDaysText += " 월"
                            2 -> tempDaysText += " 화"
                            3 -> tempDaysText += " 수"
                            4 -> tempDaysText += " 목"
                            5 -> tempDaysText += " 금"
                            6 -> tempDaysText += " 토"
                        }
                    }
                }
                daysText.text = tempDaysText
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