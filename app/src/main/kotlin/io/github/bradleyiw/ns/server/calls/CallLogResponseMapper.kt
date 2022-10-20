package io.github.bradleyiw.ns.server.calls

import io.github.bradleyiw.ns.common.calls.utils.PhoneNumberFormatter
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.core.extension.toDuration

class CallLogResponseMapper(private val phoneNumberFormat: PhoneNumberFormatter) {
    fun toCallLogResponse(log: CallLog): CallLogResponse =
        CallLogResponse(
            name = log.name,
            number = phoneNumberFormat.format(log.number),
            startTime = log.startTime,
            endTime = log.endTime,
            duration = log.endTime?.let { log.startTime?.toDuration(it) }?.toMillis(),
            status = log.status.toString(),
            type = log.type.toString(),
            numberOfQueries = log.numberOfQueries
        )
}
