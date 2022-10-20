package io.github.bradleyiw.ns.server.calls

import io.github.bradleyiw.ns.common.calls.domain.usecase.GetLatestCallLogsUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetOngoingCallUseCase
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.core.exception.Failure
import io.github.bradleyiw.ns.core.exception.map
import javax.inject.Inject

class CallLogsController @Inject constructor(
    private val callLogsResponseMapper: CallLogResponseMapper,
    private val getLatestCallLogsUseCase: GetLatestCallLogsUseCase,
    private val getOngoingCallUseCase: GetOngoingCallUseCase
) {
    suspend fun onCallLogsRequested(): Either<Failure, List<CallLogResponse>> =
        getLatestCallLogsUseCase.execute(Unit)
            .map { it.map(callLogsResponseMapper::toCallLogResponse) }

    suspend fun onOngoingCallRequested(): Either<Failure, CallLogResponse> =
        getOngoingCallUseCase.execute(Unit)
            .map(callLogsResponseMapper::toCallLogResponse)
}

