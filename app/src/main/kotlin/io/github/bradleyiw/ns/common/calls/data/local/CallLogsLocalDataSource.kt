package io.github.bradleyiw.ns.common.calls.data.local

import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import kotlinx.coroutines.flow.Flow

interface CallLogsLocalDataSource {
    fun callLogs(): Flow<List<CallLogEntity>>
    suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLogEntityWithCount>>
    suspend fun callLog(number: String): Either<Failure, CallLogEntity>
    suspend fun callLog(status: CallStatus): Either<Failure, CallLogEntity>
    suspend fun addNewCallLog(callLog: CallLogEntity): Either<Failure, Unit>
    suspend fun updateCallLog(callLog: CallLogEntity): Either<Failure, Unit>
}
