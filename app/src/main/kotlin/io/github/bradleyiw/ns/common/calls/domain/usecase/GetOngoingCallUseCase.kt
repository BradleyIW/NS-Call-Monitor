package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.usecase.OneShotUseCase

class GetOngoingCallUseCase(
    private val callLogsRepository: CallLogsRepository
) : OneShotUseCase<Unit, CallLog>() {
    override suspend fun run(params: Unit): Either<Failure, CallLog> =
        callLogsRepository.callLog(CallStatus.ONGOING)
}
