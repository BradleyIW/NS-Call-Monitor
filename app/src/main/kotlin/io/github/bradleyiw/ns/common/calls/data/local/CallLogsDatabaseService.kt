package io.github.bradleyiw.ns.common.calls.data.local

import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogsQueryDao
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.DatabaseService
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.exception.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CallLogsDatabaseService @Inject constructor(
    private val callLogsQueryDao: CallLogsQueryDao,
    private val callLogsDao: CallLogsDao
) : DatabaseService() {

    fun callLogs(): Flow<List<CallLogEntity>> =
        callLogsDao.distinctCallLogs()
            .map { logs -> logs.map { incrementQueryCount(it.id, it) } }

    suspend fun latestCallLogsWithCount(): Either<Failure, List<CallLogEntityWithCount>> =
        oneShotDbRequest {
            callLogsDao.latestCallLogsWithCount()
        }.map { logs -> logs.map { incrementQueryCount(it.logEntity.id, it) } }

    suspend fun callLog(status: CallStatus): Either<Failure, CallLogEntity> =
        oneShotDbRequest {
            callLogsDao.callLog(status)
        }.map { incrementQueryCount(it.id, it) }

    suspend fun callLog(number: String): Either<Failure, CallLogEntity> =
        oneShotDbRequest {
            callLogsDao.callLog(number)
        }.map { incrementQueryCount(it.id, it) }

    /**
     * Number of queries updated by trigger.
     * @see CoreModule.providesCallMonitoringDatabase
     */
    suspend fun addNewCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        oneShotDbRequest {
            callLogsDao.addCallLog(callLog)
        }

    /**
     * Number of queries updated by trigger.
     * @see CoreModule.providesCallMonitoringDatabase
     */
    suspend fun updateCallLog(callLog: CallLogEntity): Either<Failure, Unit> =
        oneShotDbRequest {
            callLogsDao.updateCallLog(callLog)
        }

    private suspend fun <T> incrementQueryCount(callLogId: Int?, response: T): T {
        updateQueryCount(callLogId)
        return response
    }

    private suspend fun updateQueryCount(callLogId: Int?): Unit =
        callLogsQueryDao.updateQueryCount(callLogId)
}
