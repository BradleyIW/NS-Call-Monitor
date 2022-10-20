package io.github.bradleyiw.ns.common.calls.data.local.query

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity

@Entity(
    tableName = "call_log_query",
    foreignKeys = [
        ForeignKey(
            entity = CallLogEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("call_log_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CallLogQueryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "call_log_id")
    val callLogId: Int?,

    @ColumnInfo(name = "number_of_queries")
    val numberOfQueries: Int
)
