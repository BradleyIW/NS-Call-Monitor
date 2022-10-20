package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.usecase.OneShotUseCase
import javax.inject.Inject

class CallRingingUseCase @Inject constructor(
    private val callLogsRepository: CallLogsRepository
) : OneShotUseCase<CallRingingParams, Unit>() {
    override suspend fun run(params: CallRingingParams): Either<Failure, Unit> =
        with(params) {
            val callLog = CallLog(
                name = contactName,
                number = contactNumber,
                status = CallStatus.RINGING,
                type = CallType.INCOMING
            )
            callLogsRepository.addNewCallLog(callLog)
        }
}

data class CallRingingParams(
    val contactName: String?,
    val contactNumber: String
)
