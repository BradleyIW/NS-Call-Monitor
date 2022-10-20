package io.github.bradleyiw.ns.common.calls.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import java.time.LocalDateTime

@Entity(tableName = "call_log")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "number")
    val number: String,

    @ColumnInfo(name = "start_time")
    val startTime: LocalDateTime?,

    @ColumnInfo(name = "end_time")
    val endTime: LocalDateTime?,

    @ColumnInfo(name = "type")
    val type: CallType,

    @ColumnInfo(name = "status")
    val status: CallStatus,

    @ColumnInfo(name = "created_at")
    val createdAt: Long?,

    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long?
)
