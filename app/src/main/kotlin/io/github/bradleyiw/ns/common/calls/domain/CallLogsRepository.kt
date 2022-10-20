package io.github.bradleyiw.ns.common.calls.domain

import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import kotlinx.coroutines.flow.Flow

interface CallLogsRepository {
    fun callLogs(): Flow<List<CallLog>>
    suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLog>>
    suspend fun callLog(number: String): Either<Failure, CallLog>
    suspend fun callLog(status: CallStatus): Either<Failure, CallLog>
    suspend fun addNewCallLog(callLog: CallLog): Either<Failure, Unit>
    suspend fun updateCallLog(callLog: CallLog): Either<Failure, Unit>
}
