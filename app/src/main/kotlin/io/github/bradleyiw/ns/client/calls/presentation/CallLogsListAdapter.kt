package io.github.bradleyiw.ns.client.calls.presentation

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.github.bradleyiw.ns.R
import io.github.bradleyiw.ns.client.calls.utils.CallLogViewType
import io.github.bradleyiw.ns.databinding.ItemCallLogBinding
import java.time.Duration
import java.util.*

class CallLogsListAdapter : ListAdapter<CallLogItem, ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CallLogItem>() {
            override fun areItemsTheSame(oldItem: CallLogItem, newItem: CallLogItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CallLogItem, newItem: CallLogItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        getItem(position).viewType.type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val inflate = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            CallLogViewType.MISSED_INCOMING.type -> {
                MissedIncomingCallViewHolder(inflate)
            }
            CallLogViewType.MISSED_OUTGOING.type -> {
                MissedOutgoingCallViewHolder(inflate)
            }
            CallLogViewType.COMPLETE_INCOMING.type -> {
                CompleteIncomingCallViewHolder(inflate)
            }
            CallLogViewType.COMPLETE_OUTGOING.type -> {
                CompleteOutgoingCallViewHolder(inflate)
            }
            else -> CallLogViewHolder(inflate)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CallLogViewType.MISSED_INCOMING.type -> {
                (holder as MissedIncomingCallViewHolder).bind(getItem(position))
            }
            CallLogViewType.MISSED_OUTGOING.type -> {
                (holder as MissedOutgoingCallViewHolder).bind(getItem(position))
            }
            CallLogViewType.COMPLETE_INCOMING.type -> {
                (holder as CompleteIncomingCallViewHolder).bind(getItem(position))
            }
            CallLogViewType.COMPLETE_OUTGOING.type -> {
                (holder as CompleteOutgoingCallViewHolder).bind(getItem(position))
            }
            else -> (holder as CallLogViewHolder).bind(getItem(position))
        }
    }

    open class CallLogViewHolder(
        private var binding: ItemCallLogBinding
    ) : ViewHolder(binding.root) {
        open fun bind(callLog: CallLogItem) {
            with(itemView) {
                binding.callLogItemNameTextView.text =
                    callLog.name ?: context.getString(R.string.unknown_caller)
                binding.callLogItemNumberTextView.text =
                    PhoneNumberUtils.formatNumber(callLog.number, Locale.getDefault().country)
                binding.callLogItemCallStatusIcon.setImageResource(R.drawable.ic_ongoing_call)

                formatTimeAgo(callLog.createdAt)?.let {
                    binding.callLogItemTimeAgoTextView.text = it
                }

                formatDuration(context, callLog.duration)?.let {
                    binding.callLogItemDurationTextView.text = it
                }
            }
        }

        private fun formatTimeAgo(timeAgo: Long?): String? =
            timeAgo?.let {
                android.text.format.DateUtils.getRelativeTimeSpanString(timeAgo)
                    .toString()
            }

        private fun formatDuration(context: Context, duration: Duration?): String? =
            duration?.let {
                val hours = duration.toHours()
                val minutes = duration.minusHours(hours).toMinutes()
                val seconds = duration.minusHours(hours).minusMinutes(minutes).toSeconds()
                val timeRange = (1..WITHIN_TIME_FRAME)
                return when {
                    hours > 0 -> {
                        context.getString(
                            R.string.duration_in_hours,
                            hours,
                            minutes
                        )
                    }
                    minutes in timeRange -> {
                        context.getString(
                            R.string.duration_in_minutes,
                            minutes,
                            seconds
                        )
                    }
                    seconds in timeRange -> {
                        context.resources.getQuantityString(
                            R.plurals.duration_in_seconds,
                            seconds.toInt(),
                            seconds.toInt()
                        )
                    }
                    else -> null
                }
            }

        companion object {
            private const val WITHIN_TIME_FRAME = 59
        }
    }

    class MissedIncomingCallViewHolder(
        private var binding: ItemCallLogBinding
    ) : CallLogViewHolder(binding) {
        override fun bind(callLog: CallLogItem) {
            super.bind(callLog)
            with(itemView) {
                val missedCallColorRes = ContextCompat.getColor(context, R.color.valencia)
                binding.callLogItemNumberTextView.setTextColor(missedCallColorRes)
                binding.callLogItemCallStatusIcon.setImageResource(R.drawable.ic_call_missed_incoming)
            }
        }
    }

    class MissedOutgoingCallViewHolder(
        private var binding: ItemCallLogBinding
    ) : CallLogViewHolder(binding) {
        override fun bind(callLog: CallLogItem) {
            super.bind(callLog)
            with(itemView) {
                val missedCallColorRes = ContextCompat.getColor(context, R.color.valencia)
                binding.callLogItemNumberTextView.setTextColor(missedCallColorRes)
                binding.callLogItemCallStatusIcon.setImageResource(R.drawable.ic_call_missed_outgoing)
            }
        }
    }

    class CompleteIncomingCallViewHolder(
        private var binding: ItemCallLogBinding
    ) : CallLogViewHolder(binding) {
        override fun bind(callLog: CallLogItem) {
            super.bind(callLog)
            binding.callLogItemCallStatusIcon.setImageResource(R.drawable.ic_call_received_incoming)
        }
    }

    class CompleteOutgoingCallViewHolder(
        private var binding: ItemCallLogBinding
    ) : CallLogViewHolder(binding) {
        override fun bind(callLog: CallLogItem) {
            super.bind(callLog)
            binding.callLogItemCallStatusIcon.setImageResource(R.drawable.ic_call_received_outgoing)
        }
    }
}
