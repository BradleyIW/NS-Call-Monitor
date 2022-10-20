package io.github.bradleyiw.ns.framework.fakes

import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsDao
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeCallLogsDao : CallLogsDao {
    private val flow = MutableSharedFlow<List<CallLogEntity>>()
    suspend fun emit(value: List<CallLogEntity>) = flow.emit(value)
    override fun callLogs(): Flow<List<CallLogEntity>> =
        flow

    override suspend fun latestCallLogsWithCount(): List<CallLogEntityWithCount> = emptyList()
    override suspend fun callLog(status: CallStatus): CallLogEntity? = null
    override suspend fun callLog(number: String): CallLogEntity? = null
    override suspend fun update(callLogEntity: CallLogEntity) = Unit
    override suspend fun insert(callLogEntity: CallLogEntity) = Unit
}
