package io.github.bradleyiw.ns.common.calls.domain.usecase

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CallRingingUseCaseTest : UnitTest() {

    private lateinit var callRingingUseCase: CallRingingUseCase

    @RelaxedMockK
    private lateinit var callLogsRepository: CallLogsRepository

    @BeforeEach
    fun setup() {
        callRingingUseCase = CallRingingUseCase(callLogsRepository)
    }

    @Test
    fun `given contactName is valid, when run is called, then add new call log`() =
        runTest {
            val slot = slot<CallLog>()
            val params = CallRingingParams(TEST_CONTACT_NAME, TEST_CONTACT_NUMBER)

            callRingingUseCase.run(params)

            coVerify {
                callLogsRepository.addNewCallLog(capture(slot))
            }

            assertEquals(slot.captured.name, TEST_CONTACT_NAME)
            assertEquals(slot.captured.number, TEST_CONTACT_NUMBER)
            assertEquals(slot.captured.status, CallStatus.RINGING)
            assertEquals(slot.captured.type, CallType.INCOMING)
        }

    @Test
    fun `given contactName is null, when run is called, then add new call log`() =
        runTest {
            val slot = slot<CallLog>()
            val params = CallRingingParams(null, TEST_CONTACT_NUMBER)

            callRingingUseCase.run(params)

            coVerify {
                callLogsRepository.addNewCallLog(capture(slot))
            }
            assertNull(slot.captured.name)
        }

    companion object {
        private const val TEST_CONTACT_NAME = "Jane Doe"
        private const val TEST_CONTACT_NUMBER = "+4927738837728"
    }
}
