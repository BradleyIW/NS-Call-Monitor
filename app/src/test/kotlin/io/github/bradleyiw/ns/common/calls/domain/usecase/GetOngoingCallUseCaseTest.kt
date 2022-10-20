package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.assertLeft
import io.github.bradleyiw.ns.framework.assertRight
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class GetOngoingCallUseCaseTest : UnitTest() {
    private lateinit var getOngoingCallUseCase: GetOngoingCallUseCase

    @MockK
    private lateinit var callLogsRepository: CallLogsRepository

    @BeforeEach
    fun setup() {
        getOngoingCallUseCase = GetOngoingCallUseCase(callLogsRepository)
    }

    @Test
    fun `given run is called, when repository result is success, then propagate repository result`() {
        runTest {
            val callLog = mockk<CallLog>()
            coEvery {
                callLogsRepository.callLog(CallStatus.ONGOING)
            } returns Either.Right(callLog)

            val response = getOngoingCallUseCase.run(Unit)
            response.assertRight {
                assertEquals(it, callLog)
            }
            coVerify { callLogsRepository.callLog(eq(CallStatus.ONGOING)) }
        }
    }

    @Test
    fun `given run is called, when repository result fails, then propagate repository result`() {
        runTest {
            coEvery {
                callLogsRepository.callLog(CallStatus.ONGOING)
            } returns Either.Left(NoResultsError)

            val response = getOngoingCallUseCase.run(Unit)
            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsRepository.callLog(eq(CallStatus.ONGOING)) }
        }
    }
}
