package io.github.bradleyiw.ns.client.calls.presentation

import io.github.bradleyiw.ns.client.calls.utils.CallLogViewType
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.core.extension.toDuration

class CallLogViewMapper {

    fun toCallLogItem(log: CallLog): CallLogItem =
        CallLogItem(
            id = log.id,
            viewType = mapLogToViewType(log.type, log.status),
            name = log.name,
            number = log.number,
            createdAt = log.createdAt,
            duration = log.endTime?.let { log.startTime?.toDuration(it) }
        )

    private fun mapLogToViewType(type: CallType, status: CallStatus): CallLogViewType =
        when {
            status == CallStatus.MISSED && type == CallType.INCOMING -> {
                CallLogViewType.MISSED_INCOMING
            }
            status == CallStatus.MISSED && type == CallType.OUTGOING -> {
                CallLogViewType.MISSED_OUTGOING
            }
            status == CallStatus.COMPLETE && type == CallType.INCOMING -> {
                CallLogViewType.COMPLETE_INCOMING
            }
            status == CallStatus.COMPLETE && type == CallType.OUTGOING -> {
                CallLogViewType.COMPLETE_OUTGOING
            }
            else -> {
                CallLogViewType.ONGOING
            }
        }
}
