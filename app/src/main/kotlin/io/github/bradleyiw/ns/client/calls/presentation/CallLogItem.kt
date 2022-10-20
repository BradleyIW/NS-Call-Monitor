package io.github.bradleyiw.ns.client.calls.presentation

import io.github.bradleyiw.ns.client.calls.utils.CallLogViewType
import java.time.Duration

data class CallLogItem(
    val id: Int?,
    val viewType: CallLogViewType,
    val name: String?,
    val number: String,
    val createdAt: Long?,
    val duration: Duration?
)
