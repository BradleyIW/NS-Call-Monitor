package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
internal class CallEndedUseCaseTest : UnitTest() {

    private lateinit var callEndedUseCase: CallEndedUseCase

    @RelaxedMockK
    private lateinit var callLogsRepository: CallLogsRepository

    private val endTime: LocalDateTime =
        LocalDateTime.of(2022, 10, 18, 10, 0)

    @BeforeEach
    fun setup() {
        callEndedUseCase = CallEndedUseCase(callLogsRepository)
    }

    @Test
    fun `given previous call state was answered, when run is called, then update to completed`() =
        runTest {
            val slot = slot<CallLog>()
            val params = CallEndedParams(TEST_CONTACT_NUMBER, endTime)

            coEvery {
                callLogsRepository.callLog(TEST_CONTACT_NUMBER)
            } returns Either.Right(mockk(relaxed = true) {
                every { status } returns CallStatus.ONGOING
                every {
                    copy(
                        endTime = any(),
                        status = any(),
                        number = any(),
                        type = any()
                    )
                } answers { callOriginal() }
            })

            coEvery {
                callLogsRepository.updateCallLog(capture(slot))
            } returns Either.Right(Unit)

            callEndedUseCase.run(params)

            coVerify {
                callLogsRepository.callLog(eq(TEST_CONTACT_NUMBER))
            }

            coVerify {
                callLogsRepository.updateCallLog(eq(slot.captured))
            }

            assertEquals(slot.captured.endTime, endTime)
            assertEquals(slot.captured.status, CallStatus.COMPLETE)
        }

    @Test
    fun `given previous call state was ringing, when run is called, then update to missed`() =
        runTest {
            val slot: CapturingSlot<CallLog> = slot()
            val previousLog: CallLog = mockk(relaxed = true) {
                every { status } returns CallStatus.RINGING
                every {
                    copy(
                        endTime = any(),
                        status = any(),
                        number = any(),
                        type = any()
                    )
                } answers { callOriginal() }
            }

            coEvery {
                callLogsRepository.callLog(TEST_CONTACT_NUMBER)
            } returns Either.Right(previousLog)

            val params = CallEndedParams(TEST_CONTACT_NUMBER, endTime)
            callEndedUseCase.run(params)

            coVerify {
                callLogsRepository.callLog(eq(TEST_CONTACT_NUMBER))
            }

            coVerify {
                callLogsRepository.updateCallLog(capture(slot))
            }

            assertNull(slot.captured.endTime)
            assertEquals(slot.captured.status, CallStatus.MISSED)
        }

    @Test
    fun `given no previous call has been made, when run is called, then propagate error`() =
        runTest {
            val params = CallEndedParams(TEST_CONTACT_NUMBER, endTime)

            coEvery {
                callLogsRepository.callLog(TEST_CONTACT_NUMBER)
            } returns Either.Left(NoResultsError)

            callEndedUseCase.run(params)

            coVerify {
                callLogsRepository.callLog(eq(TEST_CONTACT_NUMBER))
            }
            coVerify(exactly = 0) {
                callLogsRepository.updateCallLog(any())
            }
        }

    companion object {
        private const val TEST_CONTACT_NUMBER = "+4927738837728"
    }
}
