package io.github.bradleyiw.ns.common.calls.data

import io.github.bradleyiw.ns.common.calls.CallLogsMapper
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsLocalDataSource
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.exception.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CallLogsRepositoryImpl @Inject constructor(
    private val callLogsLocalDataSource: CallLogsLocalDataSource,
    private val callLogsMapper: CallLogsMapper
) : CallLogsRepository {

    override fun callLogs(): Flow<List<CallLog>> =
        callLogsLocalDataSource.callLogs()
            .map { it.map(callLogsMapper::toCallLog) }

    override suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLog>> =
        callLogsLocalDataSource.latestCallLogsWithCount()
            .map { it.map(callLogsMapper::toCallLog) }

    override suspend fun callLog(number: String): Either<Failure, CallLog> =
        callLogsLocalDataSource.callLog(number)
            .map(callLogsMapper::toCallLog)

    override suspend fun callLog(status: CallStatus): Either<Failure, CallLog> =
        callLogsLocalDataSource.callLog(status)
            .map(callLogsMapper::toCallLog)

    override suspend fun addNewCallLog(callLog: CallLog): Either<Failure, Unit> =
        callLogsLocalDataSource.addNewCallLog(callLogsMapper.toCallLogEntity(callLog))

    override suspend fun updateCallLog(callLog: CallLog): Either<Failure, Unit> =
        callLogsLocalDataSource.updateCallLog(callLogsMapper.toCallLogEntity(callLog))
}

