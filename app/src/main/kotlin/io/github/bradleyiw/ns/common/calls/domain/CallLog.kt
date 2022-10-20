package io.github.bradleyiw.ns.common.calls.domain

import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.core.extension.empty
import java.time.LocalDateTime

data class CallLog(
    val id: Int? = null,
    val name: String?,
    val number: String = String.empty(),
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val status: CallStatus,
    val type: CallType = CallType.OUTGOING,
    val numberOfQueries: Int = 0,
    val createdAt: Long? = null,
    val modifiedAt: Long? = null
)
