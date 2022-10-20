package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.buildListOfType
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class GetCallLogsUseCaseTest : UnitTest() {

    private lateinit var getCallLogsUseCase: GetCallLogsUseCase

    @MockK
    private lateinit var callLogsRepository: CallLogsRepository

    @BeforeEach
    fun setup() {
        getCallLogsUseCase = GetCallLogsUseCase(callLogsRepository)
    }

    @Test
    fun `given run is called, when repository result is success, then propagate repository result`() {
        runTest {
            val callLog = mockk<CallLog>()
            val callLogs = buildListOfType(5, callLog)
            coEvery {
                callLogsRepository.callLogs()
            } returns flowOf(callLogs)

            getCallLogsUseCase.run(Unit)

            verify { callLogsRepository.callLogs() }
        }
    }
}
