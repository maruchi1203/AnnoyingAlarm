package lowblow.annoying_alarm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lowblow.annoying_alarm.R
import lowblow.annoying_alarm.data.MessageEntity
import lowblow.annoying_alarm.databinding.ItemMessageBinding

class MessageListAdapter(
    private val context: Context
) : ListAdapter<MessageEntity, MessageListAdapter.ViewHolder>(diffUtil) {

    private val messageList = mutableListOf<MessageEntity>()

    inner class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(messageEntity: MessageEntity) {
            val textView = binding.messageTextView

            if (messageEntity.isUser) {
                val img = context.getDrawable(R.drawable.ic_baseline_person_36)

                textView.gravity = Gravity.END or Gravity.CENTER
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
            } else {
                val img = context.getDrawable(R.drawable.ic_baseline_message_36)

                textView.gravity = Gravity.START or Gravity.CENTER
                textView.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
            }

            textView.text = messageEntity.message
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateMessageList(message: MessageEntity) {
        messageList.add(message)
        submitList(messageList)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MessageEntity>() {

            override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MessageEntity,
                newItem: MessageEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}