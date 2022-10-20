package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
internal class CallAnsweredUseCaseTest : UnitTest() {

    private lateinit var callAnsweredUseCase: CallAnsweredUseCase

    @RelaxedMockK
    private lateinit var callLogsRepository: CallLogsRepository

    private val startTime: LocalDateTime =
        LocalDateTime.of(2022, 10, 18, 10, 0)

    @BeforeEach
    fun setup() {
        callAnsweredUseCase = CallAnsweredUseCase(callLogsRepository)
    }

    @Test
    fun `given previous call exists, when run is called, then update to ongoing`() =
        runTest {
            val slot = slot<CallLog>()
            val params = CallAnsweredParams(TEST_CONTACT_NAME, TEST_CONTACT_NUMBER, startTime)

            coEvery {
                callLogsRepository.callLog(TEST_CONTACT_NUMBER)
            } returns Either.Right(mockk(relaxed = true) {
                every { status } returns CallStatus.RINGING
                every {
                    copy(
                        startTime = any(),
                        status = any(),
                        number = any(),
                        type = any()
                    )
                } answers { callOriginal() }
            })

            coEvery {
                callLogsRepository.updateCallLog(capture(slot))
            } returns Either.Right(Unit)

            callAnsweredUseCase.run(params)

            coVerify {
                callLogsRepository.callLog(eq(TEST_CONTACT_NUMBER))
            }

            coVerify {
                callLogsRepository.updateCallLog(eq(slot.captured))
            }

            Assertions.assertEquals(slot.captured.startTime, startTime)
            Assertions.assertEquals(slot.captured.status, CallStatus.ONGOING)
        }

    @Test
    fun `given previous does not exist, when run is called, then add new log`() =
        runTest {
            val slot = slot<CallLog>()
            val params = CallAnsweredParams(TEST_CONTACT_NAME, TEST_CONTACT_NUMBER, startTime)

            coEvery {
                callLogsRepository.callLog(TEST_CONTACT_NUMBER)
            } returns Either.Left(NoResultsError)

            coEvery {
                callLogsRepository.addNewCallLog(capture(slot))
            } returns Either.Right(Unit)

            callAnsweredUseCase.run(params)

            coVerify {
                callLogsRepository.callLog(eq(TEST_CONTACT_NUMBER))
            }

            coVerify {
                callLogsRepository.addNewCallLog(eq(slot.captured))
            }

            Assertions.assertEquals(slot.captured.name, TEST_CONTACT_NAME)
            Assertions.assertEquals(slot.captured.number, TEST_CONTACT_NUMBER)
            Assertions.assertEquals(slot.captured.startTime, startTime)
            Assertions.assertEquals(slot.captured.status, CallStatus.ONGOING)
            Assertions.assertEquals(slot.captured.type, CallType.OUTGOING)
        }

    companion object {
        private const val TEST_CONTACT_NAME = "Jane Doe"
        private const val TEST_CONTACT_NUMBER = "+4927738837728"
    }
}
