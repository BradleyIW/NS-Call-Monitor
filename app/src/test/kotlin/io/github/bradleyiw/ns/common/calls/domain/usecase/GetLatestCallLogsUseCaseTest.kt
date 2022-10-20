package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.assertLeft
import io.github.bradleyiw.ns.framework.assertRight
import io.github.bradleyiw.ns.framework.buildListOfType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class GetLatestCallLogsUseCaseTest : UnitTest() {
    private lateinit var getLatestCallLogsUseCase: GetLatestCallLogsUseCase

    @MockK
    private lateinit var callLogsRepository: CallLogsRepository

    @BeforeEach
    fun setup() {
        getLatestCallLogsUseCase = GetLatestCallLogsUseCase(callLogsRepository)
    }

    @Test
    fun `given run is called, when repository result is success, then propagate repository result`() {
        runTest {
            val callLog = mockk<CallLog>()
            val callLogs = buildListOfType(5, callLog)
            coEvery {
                callLogsRepository.latestCallLogsWithCount()
            } returns Either.Right(callLogs)

            val response = getLatestCallLogsUseCase.run(Unit)
            response.assertRight {
                Assertions.assertEquals(it, callLogs)
            }
            coVerify { callLogsRepository.latestCallLogsWithCount() }
        }
    }

    @Test
    fun `given run is called, when repository result fails, then propagate repository result`() {
        runTest {
            coEvery {
                callLogsRepository.latestCallLogsWithCount()
            } returns Either.Left(NoResultsError)

            val response = getLatestCallLogsUseCase.run(Unit)
            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsRepository.latestCallLogsWithCount() }
        }
    }
}
