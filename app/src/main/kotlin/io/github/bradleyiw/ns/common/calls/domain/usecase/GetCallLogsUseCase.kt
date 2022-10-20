package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.core.usecase.ReactiveUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCallLogsUseCase @Inject constructor(
    private val logsRepository: CallLogsRepository
) : ReactiveUseCase<Unit, List<CallLog>> {

    override fun run(params: Unit): Flow<List<CallLog>> =
        logsRepository.callLogs()
}
