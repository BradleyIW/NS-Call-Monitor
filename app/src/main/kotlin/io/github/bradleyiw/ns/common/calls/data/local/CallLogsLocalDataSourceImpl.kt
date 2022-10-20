package io.github.bradleyiw.ns.common.calls.data.local

import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CallLogsLocalDataSourceImpl @Inject constructor(
    private val callLogsDatabaseService: CallLogsDatabaseService
) : CallLogsLocalDataSource {
    override fun callLogs(): Flow<List<CallLogEntity>> =
        callLogsDatabaseService.callLogs()

    override suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLogEntityWithCount>> =
        callLogsDatabaseService.latestCallLogsWithCount()

    override suspend fun callLog(number: String): Either<Failure, CallLogEntity> =
        callLogsDatabaseService.callLog(number)

    override suspend fun callLog(status: CallStatus): Either<Failure, CallLogEntity> =
        callLogsDatabaseService.callLog(status)

    override suspend fun addNewCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        callLogsDatabaseService.addNewCallLog(callLog)

    override suspend fun updateCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        callLogsDatabaseService.updateCallLog(callLog)

}
