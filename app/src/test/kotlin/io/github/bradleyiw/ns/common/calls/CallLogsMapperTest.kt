package io.github.bradleyiw.ns.common.calls

import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.common.calls.utils.CallType
import io.github.bradleyiw.ns.framework.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CallLogsMapperTest : UnitTest() {

    private lateinit var callLogsMapper: CallLogsMapper

    @MockK
    private lateinit var callLogEntity: CallLogEntity

    @MockK
    private lateinit var callLogEntityWithCount: CallLogEntityWithCount

    @MockK
    private lateinit var callLog: CallLog

    @BeforeEach
    fun setup() {
        callLogsMapper = CallLogsMapper()
    }

    @Test
    fun `Given argument is of type CallLogEntity, when toCallLog is called, then assert call log values match`() =
        runTest {
            callLogEntityDefaultValues()

            val response = callLogsMapper.toCallLog(callLogEntity)

            assertCallLogEntityValues(response)
        }

    @Test
    fun `Given toCallLog by count is called, then call log values should match`() =
        runTest {
            callLogEntityDefaultValues()

            every { callLogEntityWithCount.logEntity } answers {
                callLogEntity
            }
            every { callLogEntityWithCount.numberOfQueries } returns TEST_NUMBER_OF_QUERIES

            val response = callLogsMapper.toCallLog(callLogEntityWithCount)

            assertCallLogEntityValues(response)
            assertEquals(response.numberOfQueries, TEST_NUMBER_OF_QUERIES)
        }

    @Test
    fun `Given argument is of type CallLog, when toCallLogEntity is called, then assert call entity values match`() =
        runTest {
            callLogDefaultValues()

            val response = callLogsMapper.toCallLogEntity(callLog)

            assertCallLogEntityValues(response)
        }

    private fun assertCallLogEntityValues(response: CallLog) {
        assertEquals(response.id, TEST_ID)
        assertEquals(response.name, TEST_CALL_NAME)
        assertEquals(response.number, TEST_NUMBER)
        assertNull(response.startTime)
        assertNull(response.endTime)
        assertEquals(response.status, CallStatus.ONGOING)
        assertEquals(response.type, CallType.OUTGOING)
        assertEquals(response.createdAt, TEST_CREATED_AT)
        assertEquals(response.modifiedAt, TEST_MODIFIED_AT)
    }

    private fun assertCallLogEntityValues(response: CallLogEntity) {
        assertEquals(response.id, TEST_ID)
        assertEquals(response.name, TEST_CALL_NAME)
        assertEquals(response.number, TEST_NUMBER)
        assertNull(response.startTime)
        assertNull(response.endTime)
        assertEquals(response.status, CallStatus.ONGOING)
        assertEquals(response.type, CallType.OUTGOING)
        assertEquals(response.createdAt, TEST_CREATED_AT)
        assertEquals(response.modifiedAt, TEST_MODIFIED_AT)
    }

    private fun callLogEntityDefaultValues() {
        every { callLogEntity.id } returns TEST_ID
        every { callLogEntity.name } returns TEST_CALL_NAME
        every { callLogEntity.number } returns TEST_NUMBER
        every { callLogEntity.endTime } returns null
        every { callLogEntity.startTime } returns null
        every { callLogEntity.status } returns CallStatus.ONGOING
        every { callLogEntity.type } returns CallType.OUTGOING
        every { callLogEntity.createdAt } returns TEST_CREATED_AT
        every { callLogEntity.modifiedAt } returns TEST_CREATED_AT
    }

    private fun callLogDefaultValues() {
        every { callLog.id } returns TEST_ID
        every { callLog.name } returns TEST_CALL_NAME
        every { callLog.number } returns TEST_NUMBER
        every { callLog.endTime } returns null
        every { callLog.startTime } returns null
        every { callLog.status } returns CallStatus.ONGOING
        every { callLog.type } returns CallType.OUTGOING
        every { callLog.createdAt } returns TEST_CREATED_AT
        every { callLog.modifiedAt } returns TEST_CREATED_AT
    }


    companion object {
        private const val TEST_ID = 200
        private const val TEST_CALL_NAME = "Harold Test"
        private const val TEST_NUMBER = "+49123456789"
        private const val TEST_CREATED_AT = 0L
        private const val TEST_MODIFIED_AT = 0L
        private const val TEST_NUMBER_OF_QUERIES = 20
    }
}
