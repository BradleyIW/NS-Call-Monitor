package io.github.bradleyiw.ns.common.calls.data.local.query

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CallLogsQueryDao {

    @Insert
    fun insert(entity: CallLogQueryEntity): Long

    @Update
    fun update(entity: CallLogQueryEntity): Int

    @Query("SELECT * FROM call_log_query WHERE call_log_id = :callLogId LIMIT 1")
    fun queriesForLog(callLogId: Int): CallLogQueryEntity?

    @Transaction
    suspend fun updateQueryCount(callLogId: Int?) {
        callLogId?.let {
            queriesForLog(it)?.let { entity ->
                val numberOfQueries = entity.numberOfQueries
                update(entity.copy(numberOfQueries = numberOfQueries.inc()))
            } ?: insert(CallLogQueryEntity(callLogId, 1))
        }
    }
}
