package io.github.bradleyiw.ns.server.calls

import io.github.bradleyiw.ns.core.LocalDateTimeSerializer
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
class CallLogResponse(
    val name: String?,
    val number: String,
    @kotlinx.serialization.Serializable(with = LocalDateTimeSerializer::class)
    val startTime: LocalDateTime?,
    @kotlinx.serialization.Serializable(with = LocalDateTimeSerializer::class)
    val endTime: LocalDateTime?,
    val duration: Long?,
    val status: String,
    val type: String,
    val numberOfQueries: Int
)
