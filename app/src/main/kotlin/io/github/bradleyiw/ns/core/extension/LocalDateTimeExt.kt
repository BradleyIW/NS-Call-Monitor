package io.github.bradleyiw.ns.core.extension

import java.time.Duration
import java.time.LocalDateTime

fun LocalDateTime.toDuration(
    dateTime: LocalDateTime = LocalDateTime.now()
): Duration = Duration.between(this, dateTime)
