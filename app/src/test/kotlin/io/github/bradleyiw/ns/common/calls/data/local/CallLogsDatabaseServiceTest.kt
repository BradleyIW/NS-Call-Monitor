package io.github.bradleyiw.ns.common.calls.data.local

import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogsQueryDao
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.assertLeft
import io.github.bradleyiw.ns.framework.assertRight
import io.github.bradleyiw.ns.framework.buildListOfType
import io.github.bradleyiw.ns.framework.fakes.FakeCallLogsDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CallLogsDatabaseServiceTest : UnitTest() {

    private lateinit var callLogsDatabaseService: CallLogsDatabaseService

    private lateinit var fakeCallLogsDao: FakeCallLogsDao

    @MockK
    private lateinit var callLogsDao: CallLogsDao

    @RelaxedMockK
    private lateinit var callLogsQueryDao: CallLogsQueryDao

    @BeforeEach
    fun setup() {
        callLogsDatabaseService = CallLogsDatabaseService(callLogsQueryDao, callLogsDao)
    }

    @Test
    fun `given database response is valid, when callLogs are requested, then assert flow response matches`() =
        runTest {
            val service = fakeDatabaseService()
            val entity = mockk<CallLogEntity> {
                every { id } returns TEST_CALL_LOG_ID
            }
            val entities = buildListOfType(NUMBER_OF_TABLE_ROWS, entity)

            val logs = mutableListOf<List<CallLogEntity>>()
            val collectJob = launch(UnconfinedTestDispatcher()) {
                service.callLogs().toList(logs)
            }

            fakeCallLogsDao.emit(entities)

            Assertions.assertEquals(logs.first(), entities)

            coVerify(exactly = NUMBER_OF_TABLE_ROWS) {
                callLogsQueryDao.updateQueryCount(eq(TEST_CALL_LOG_ID))
            }

            collectJob.cancel()
        }

    @Test
    fun `given local response is empty, when callLogs are requested, then assert flow response is empty`() =
        runTest {
            val repository = fakeDatabaseService()
            val logs = mutableListOf<List<CallLogEntity>>()
            val collectJob = launch(UnconfinedTestDispatcher()) {
                repository.callLogs().toList(logs)
            }
            fakeCallLogsDao.emit(emptyList())
            Assertions.assertTrue(logs.first().isEmpty())
            collectJob.cancel()
        }

    @Test
    fun `given response is valid, when latestCallLogsWithCount, then propagate and update query count`() =
        runTest {
            val callLogWithCount: CallLogEntityWithCount = mockk {
                every { logEntity } answers {
                    mockk { every { id } returns TEST_CALL_LOG_ID }
                }
            }
            val rows = buildListOfType(NUMBER_OF_TABLE_ROWS, callLogWithCount)

            coEvery { callLogsQueryDao.updateQueryCount(TEST_CALL_LOG_ID) } returns Unit
            coEvery { callLogsDao.latestCallLogsWithCount() } returns rows

            val response = callLogsDatabaseService.latestCallLogsWithCount()

            response.assertRight {
                Assertions.assertEquals(it, rows)
            }
            coVerify { callLogsDao.latestCallLogsWithCount() }
            coVerify(exactly = NUMBER_OF_TABLE_ROWS) {
                callLogsQueryDao.updateQueryCount(eq(TEST_CALL_LOG_ID))
            }
        }

    @Test
    fun `given response is empty, when latestCallLogsWithCount, then propagate error`() =
        runTest {
            coEvery { callLogsQueryDao.updateQueryCount(TEST_CALL_LOG_ID) } returns Unit
            coEvery { callLogsDao.latestCallLogsWithCount() } returns emptyList()

            val response = callLogsDatabaseService.latestCallLogsWithCount()
            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDao.latestCallLogsWithCount() }
            coVerify(exactly = 0) {
                callLogsQueryDao.updateQueryCount(any())
            }
        }

    @Test
    fun `given response is valid, when callLog by status, then propagate values and update query count`() {
        runTest {
            val status = CallStatus.ONGOING
            val row: CallLogEntity = mockk {
                every { id } returns TEST_CALL_LOG_ID
            }

            coEvery { callLogsDao.callLog(status) } returns row

            val response = callLogsDatabaseService.callLog(status)

            response.assertRight {
                Assertions.assertEquals(it, row)
            }

            coVerify { callLogsDao.callLog(eq(status)) }
            coVerify(exactly = 1) { callLogsQueryDao.updateQueryCount(eq(TEST_CALL_LOG_ID)) }
        }
    }

    @Test
    fun `given response is empty, when callLog by status, then propagate value and update query count`() =
        runTest {
            val status = CallStatus.ONGOING
            coEvery { callLogsDao.callLog(status) } returns null

            val response = callLogsDatabaseService.callLog(status)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }

            coVerify { callLogsDao.callLog(eq(status)) }
            coVerify(exactly = 0) { callLogsQueryDao.updateQueryCount(any()) }
        }

    @Test
    fun `given response is valid, when callLog by number, then propagate values and update query count`() {
        runTest {
            val row: CallLogEntity = mockk {
                every { id } returns TEST_CALL_LOG_ID
            }

            coEvery { callLogsDao.callLog(TEST_NUMBER) } returns row

            val response = callLogsDatabaseService.callLog(TEST_NUMBER)

            response.assertRight {
                Assertions.assertEquals(it, row)
            }

            coVerify { callLogsDao.callLog(eq(TEST_NUMBER)) }
            coVerify(exactly = 1) { callLogsQueryDao.updateQueryCount(eq(TEST_CALL_LOG_ID)) }
        }
    }

    @Test
    fun `given response is empty, when callLog by number, then propagate values with updated query count`() =
        runTest {
            coEvery { callLogsDao.callLog(TEST_NUMBER) } returns null

            val response = callLogsDatabaseService.callLog(TEST_NUMBER)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }

            coVerify { callLogsDao.callLog(eq(TEST_NUMBER)) }
            coVerify(exactly = 0) { callLogsQueryDao.updateQueryCount(any()) }
        }

    @Test
    fun `given addNewCallLog is requested, then propagate success`() =
        runTest {
            val callLogEntity = mockk<CallLogEntity>()

            coEvery { callLogsDao.addCallLog(callLogEntity) } returns Unit

            val response = callLogsDatabaseService.addNewCallLog(callLogEntity)
            response.assertRight()

            coVerify { callLogsDao.addCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given updateCallLog is requested, then propagate success`() =
        runTest {
            val callLogEntity = mockk<CallLogEntity>()

            coEvery { callLogsDao.updateCallLog(callLogEntity) } returns Unit

            val response = callLogsDatabaseService.updateCallLog(callLogEntity)
            response.assertRight()

            coVerify { callLogsDao.updateCallLog(eq(callLogEntity)) }
        }

    private fun fakeDatabaseService(): CallLogsDatabaseService {
        fakeCallLogsDao = FakeCallLogsDao()
        return CallLogsDatabaseService(callLogsQueryDao, fakeCallLogsDao)
    }

    companion object {
        private const val NUMBER_OF_TABLE_ROWS = 5
        private const val TEST_NUMBER = "+49736636637"
        private const val TEST_CALL_LOG_ID = 100
    }
}
