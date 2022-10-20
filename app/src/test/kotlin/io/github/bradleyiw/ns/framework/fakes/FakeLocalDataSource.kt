package io.github.bradleyiw.ns.framework.fakes

import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsLocalDataSource
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeLocalDataSource : CallLogsLocalDataSource {

    private val flow = MutableSharedFlow<List<CallLogEntity>>()
    suspend fun emit(value: List<CallLogEntity>) = flow.emit(value)
    override fun callLogs(): Flow<List<CallLogEntity>> =
        flow

    override suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLogEntityWithCount>> =
        Either.Left(NoResultsError)

    override suspend fun callLog(number: String): Either<Failure, CallLogEntity> =
        Either.Left(NoResultsError)

    override suspend fun callLog(status: CallStatus): Either<Failure, CallLogEntity> =
        Either.Left(NoResultsError)

    override suspend fun addNewCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        Either.Left(NoResultsError)

    override suspend fun updateCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        Either.Left(NoResultsError)
}
