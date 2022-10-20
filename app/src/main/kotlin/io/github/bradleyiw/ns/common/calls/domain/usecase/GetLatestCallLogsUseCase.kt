package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.usecase.OneShotUseCase
import javax.inject.Inject

class GetLatestCallLogsUseCase @Inject constructor(
    private val logsRepository: CallLogsRepository
) : OneShotUseCase<Unit, List<CallLog>>() {
    override suspend fun run(params: Unit): Either<Failure, List<CallLog>> =
        logsRepository.latestCallLogsWithCount()
}
