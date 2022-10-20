package io.github.bradleyiw.ns.common.calls.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface CallLogsDao {

    @Query("SELECT * FROM call_log ORDER BY created_at DESC")
    fun callLogs(): Flow<List<CallLogEntity>>

    @Query(
        "SELECT call_log.*, call_log_query.number_of_queries FROM call_log LEFT JOIN call_log_query " +
                "ON call_log.id = call_log_query.call_log_id ORDER BY created_at DESC"
    )
    suspend fun latestCallLogsWithCount(): List<CallLogEntityWithCount>

    @Query("SELECT * FROM call_log WHERE status = :status ORDER BY created_at DESC LIMIT 1")
    suspend fun callLog(status: CallStatus): CallLogEntity?

    @Query("SELECT * FROM call_log WHERE number = :number ORDER BY created_at DESC LIMIT 1")
    suspend fun callLog(number: String): CallLogEntity?

    fun distinctCallLogs(): Flow<List<CallLogEntity>> =
        callLogs().distinctUntilChanged()

    @Transaction
    suspend fun addCallLog(callLogEntity: CallLogEntity) {
        insert(
            callLogEntity.copy(
                createdAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    suspend fun updateCallLog(callLogEntity: CallLogEntity) {
        update(
            callLogEntity.copy(modifiedAt = System.currentTimeMillis())
        )
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(callLogEntity: CallLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(callLogEntity: CallLogEntity)
}
