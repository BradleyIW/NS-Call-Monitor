package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.exception.map
import io.github.bradleyiw.ns.core.exception.onFailure
import io.github.bradleyiw.ns.core.exception.onSuccess
import io.github.bradleyiw.ns.core.usecase.OneShotUseCase
import java.time.LocalDateTime
import javax.inject.Inject

class CallAnsweredUseCase @Inject constructor(
    private val callLogsRepository: CallLogsRepository
) : OneShotUseCase<CallAnsweredParams, Unit>() {
    override suspend fun run(params: CallAnsweredParams): Either<Failure, Unit> =
        with(params) {
            callLogsRepository.callLog(contactNumber)
                .onSuccess { previousLog ->
                    val callLog = previousLog.copy(
                        startTime = startTime,
                        status = CallStatus.ONGOING,
                        number = previousLog.number,
                        type = previousLog.type
                    )
                    callLogsRepository.updateCallLog(callLog)
                }
                .onFailure {
                    addNewLog(contactName, contactNumber, startTime)
                }.map {}
        }

    private suspend fun addNewLog(
        contactName: String?,
        contactNumber: String,
        startTime: LocalDateTime
    ) {
        val callLog = CallLog(
            name = contactName,
            number = contactNumber,
            startTime = startTime,
            status = CallStatus.ONGOING,
            type = CallType.OUTGOING
        )
        callLogsRepository.addNewCallLog(callLog)
    }
}

data class CallAnsweredParams(
    val contactName: String?,
    val contactNumber: String,
    val startTime: LocalDateTime
)
