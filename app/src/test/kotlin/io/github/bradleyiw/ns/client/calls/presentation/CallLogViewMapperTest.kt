package io.github.bradleyiw.ns.client.calls.presentation

import io.github.bradleyiw.ns.client.calls.utils.CallLogViewType
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class CallLogViewMapperTest : UnitTest() {

    private lateinit var callLogViewMapper: CallLogViewMapper

    @MockK
    lateinit var callLog: CallLog

    @BeforeEach
    fun setUp() {
        callLogViewMapper = CallLogViewMapper()
        buildDefaultValues()
    }

    private fun buildDefaultValues() {
        every { callLog.id } returns TEST_CALL_ID
        every { callLog.name } returns TEST_CALL_NAME
        every { callLog.number } returns TEST_NUMBER
        every { callLog.type } returns CallType.INCOMING
        every { callLog.status } returns CallStatus.ONGOING
        every { callLog.createdAt } returns TEST_CREATED_AT
        every { callLog.endTime } returns null
        every { callLog.startTime } returns null
    }

    @Test
    fun `Given toCallLogItem is called, then assert id is same`() {
        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.id == TEST_CALL_ID)
    }

    @Test
    fun `Given toCallLogItem is called, then assert name is same`() {
        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.name == TEST_CALL_NAME)
    }

    @Test
    fun `Given toCallLogItem is called, then assert number is same`() {
        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.number == TEST_NUMBER)
    }

    @Test
    fun `Given toCallLogItem is called, then assert createdAt is same`() {
        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.createdAt == TEST_CREATED_AT)
    }

    @Test
    fun `Given call is missed incoming, when toCallLogItem, then viewType should be MISSED_INCOMING`() {
        every { callLog.status } returns CallStatus.MISSED
        every { callLog.type } returns CallType.INCOMING

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.viewType == CallLogViewType.MISSED_INCOMING)
    }

    @Test
    fun `Given call is missed outgoing, when toCallLogItem, then viewType should be MISSED_OUTGOING`() {
        every { callLog.status } returns CallStatus.MISSED
        every { callLog.type } returns CallType.OUTGOING

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.viewType == CallLogViewType.MISSED_OUTGOING)
    }

    @Test
    fun `Given call is complete incoming, when toCallLogItem, then viewType should be COMPLETE_INCOMING`() {
        every { callLog.status } returns CallStatus.COMPLETE
        every { callLog.type } returns CallType.INCOMING

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.viewType == CallLogViewType.COMPLETE_INCOMING)
    }

    @Test
    fun `Given call is complete outgoing, when toCallLogItem, then viewType should be COMPLETE_OUTGOING`() {
        every { callLog.status } returns CallStatus.COMPLETE
        every { callLog.type } returns CallType.OUTGOING

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.viewType == CallLogViewType.COMPLETE_OUTGOING)
    }

    @Test
    fun `Given status is ONGOING, when toCallLogItem, then assert viewType is ONGOING`() {
        every { callLog.status } returns CallStatus.ONGOING

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertTrue(response.viewType == CallLogViewType.ONGOING)
    }

    @Test
    fun `Given startTime and endTime are valid, when toCallLogItem, then assert duration is correct`() {
        every { callLog.startTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 0)
        every { callLog.endTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 20)

        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertNotNull(response.duration)
        Assertions.assertTrue(response.duration?.seconds == 20L)
    }

    @Test
    fun `Given startTime exists and endTime is null, when toCallLogItem, then assert duration is null`() {
        every { callLog.startTime } returns LocalDateTime.of(2022, 10, 18, 10, 30, 0)
        val response = callLogViewMapper.toCallLogItem(callLog)
        Assertions.assertNull(response.duration)
    }

    companion object {
        private const val TEST_CALL_ID = 100
        private const val TEST_CALL_NAME = "Harold Test"
        private const val TEST_NUMBER = "+49123456789"
        private const val TEST_CREATED_AT = 0L
    }
}

