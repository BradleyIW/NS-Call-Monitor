package io.github.bradleyiw.ns.common.calls.data.local.query

import androidx.room.ColumnInfo
import androidx.room.Embedded
import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity

data class CallLogEntityWithCount(
    @Embedded
    val logEntity: CallLogEntity,
    @ColumnInfo(name = "number_of_queries")
    val numberOfQueries: Int
)
