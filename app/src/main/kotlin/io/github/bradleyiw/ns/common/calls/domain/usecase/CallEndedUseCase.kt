package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.exception.flatMap
import io.github.bradleyiw.ns.core.usecase.OneShotUseCase
import java.time.LocalDateTime
import javax.inject.Inject

class CallEndedUseCase @Inject constructor(
    private val callLogsRepository: CallLogsRepository
) : OneShotUseCase<CallEndedParams, Unit>() {
    override suspend fun run(params: CallEndedParams): Either<Failure, Unit> =
        with(params) {
            callLogsRepository.callLog(number)
                .flatMap { previousLog ->
                    val updatedLog = if (previousLog.status == CallStatus.ONGOING) {
                        previousLog.copy(
                            number = previousLog.number,
                            endTime = endTime,
                            status = CallStatus.COMPLETE,
                            type = previousLog.type
                        )
                    } else {
                        previousLog.copy(
                            number = previousLog.number,
                            endTime = null,
                            status = CallStatus.MISSED,
                            type = previousLog.type
                        )
                    }
                    callLogsRepository.updateCallLog(updatedLog)
                }
        }
}

data class CallEndedParams(
    val number: String,
    val endTime: LocalDateTime
)
