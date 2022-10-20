package io.github.bradleyiw.ns.server.calls

import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.common.calls.utils.PhoneNumberFormatter
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
internal class CallLogResponseMapperTest : UnitTest() {

    private lateinit var callLogResponseMapper: CallLogResponseMapper

    @RelaxedMockK
    private lateinit var phoneNumberFormatter: PhoneNumberFormatter

    @MockK
    private lateinit var callLog: CallLog

    @BeforeEach
    fun setup() {
        callLogResponseMapper = CallLogResponseMapper(phoneNumberFormatter)
        callLogDefaultValues()
    }

    private fun callLogDefaultValues() {
        every { callLog.name } returns TEST_CALL_NAME
        every { callLog.number } returns TEST_NUMBER
        every { callLog.endTime } returns null
        every { callLog.startTime } returns null
        every { callLog.type } returns CallType.INCOMING
        every { callLog.status } returns CallStatus.ONGOING
        every { callLog.createdAt } returns TEST_CREATED_AT
        every { callLog.numberOfQueries } returns TEST_NUMBER_OF_QUERIES
    }

    @Test
    fun `Given toCallLogResponse is called, then assert name is same`() {
        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertTrue(response.name == TEST_CALL_NAME)
    }

    @Test
    fun `Given toCallLogResponse is called, then assert number is same`() {
        every { phoneNumberFormatter.format(TEST_NUMBER) } returns TEST_NUMBER

        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertTrue(response.number == TEST_NUMBER)
    }

    @Test
    fun `Given startTime and endTime are valid, when toCallLogResponse, then assert duration is valid`() {
        every { callLog.startTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 0)
        every { callLog.endTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 20)

        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertNotNull(response.duration)
        Assertions.assertTrue(response.duration == 20000L)
    }

    @Test
    fun `Given endTime is null, when toCallLogResponse, then assert duration is null`() {
        every { callLog.startTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 0)
        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertNull(response.duration)
    }

    @Test
    fun `Given toCallLogResponse is called, then assert number of queries is the same`() {
        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertTrue(response.numberOfQueries == TEST_NUMBER_OF_QUERIES)
    }

    @Test
    fun `Given toCallLogResponse is called, then assert status is the same`() {
        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertTrue(response.status == CallStatus.ONGOING.toString())
    }

    @Test
    fun `Given toCallLogResponse is called, then assert type is the same`() {
        val response = callLogResponseMapper.toCallLogResponse(callLog)
        Assertions.assertTrue(response.type == CallType.INCOMING.toString())
    }

    companion object {
        private const val TEST_CALL_NAME = "Harold Test"
        private const val TEST_NUMBER = "+49123456789"
        private const val TEST_CREATED_AT = 0L
        private const val TEST_NUMBER_OF_QUERIES = 20
    }
}
