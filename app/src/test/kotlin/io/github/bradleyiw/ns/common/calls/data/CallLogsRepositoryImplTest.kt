package io.github.bradleyiw.ns.common.calls.data

import io.github.bradleyiw.ns.common.calls.CallLogsMapper
import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsLocalDataSource
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.assertLeft
import io.github.bradleyiw.ns.framework.assertRight
import io.github.bradleyiw.ns.framework.buildListOfType
import io.github.bradleyiw.ns.framework.fakes.FakeLocalDataSource
import io.mockk.coEvery
import io.mockk.coVerify
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
internal class CallLogsRepositoryImplTest : UnitTest() {

    private lateinit var callLogsRepository: CallLogsRepository

    private val fakeLocalDataSource: FakeLocalDataSource = FakeLocalDataSource()

    @MockK
    private lateinit var callLogsLocalDataSource: CallLogsLocalDataSource

    @RelaxedMockK
    private lateinit var callLogsMapper: CallLogsMapper

    @BeforeEach
    fun setup() {
        callLogsRepository = CallLogsRepositoryImpl(callLogsLocalDataSource, callLogsMapper)
    }

    @Test
    fun `given local response is valid, when callLogs are requested, then assert flow response matches`() =
        runTest {
            val repository = fakeRepository()
            val entity = mockk<CallLogEntity>()

            val logs = mutableListOf<List<CallLog>>()
            val collectJob = launch(UnconfinedTestDispatcher()) {
                repository.callLogs().toList(logs)
            }

            fakeLocalDataSource.emit(buildListOfType(NUMBER_OF_TABLE_ROWS, entity))

            Assertions.assertEquals(logs.first().size, NUMBER_OF_TABLE_ROWS)
            coVerify(exactly = NUMBER_OF_TABLE_ROWS) { callLogsMapper.toCallLog(eq(entity)) }

            collectJob.cancel()
        }

    @Test
    fun `given local response is empty, when callLogs is called, then assert flow response is empty`() =
        runTest {
            val repository = fakeRepository()
            val logs = mutableListOf<List<CallLog>>()
            val collectJob = launch(UnconfinedTestDispatcher()) {
                repository.callLogs().toList(logs)
            }
            fakeLocalDataSource.emit(emptyList())
            Assertions.assertTrue(logs.first().isEmpty())
            coVerify(exactly = 0) { callLogsMapper.toCallLog(any<CallLogEntity>()) }
            collectJob.cancel()
        }

    @Test
    fun `given local response is valid, when latestCallLogsWithCount is called, then propagate mapped values`() =
        runTest {
            val entity = mockk<CallLogEntityWithCount>()
            val callLog = mockk<CallLog>()
            val listOfEntities = buildListOfType(NUMBER_OF_TABLE_ROWS, entity)
            val listOfLogs = buildListOfType(NUMBER_OF_TABLE_ROWS, callLog)

            coEvery {
                callLogsMapper.toCallLog(entity)
            } returns callLog

            coEvery {
                callLogsLocalDataSource.latestCallLogsWithCount()
            } returns Either.Right(listOfEntities)

            val response = callLogsRepository.latestCallLogsWithCount()

            response.assertRight {
                Assertions.assertEquals(it, listOfLogs)
            }
            coVerify { callLogsLocalDataSource.latestCallLogsWithCount() }
            coVerify(exactly = NUMBER_OF_TABLE_ROWS) { callLogsMapper.toCallLog(any<CallLogEntityWithCount>()) }
        }

    @Test
    fun `given local response is empty, when latestCallLogsWithCount is called, then propagate error`() =
        runTest {
            coEvery {
                callLogsLocalDataSource.latestCallLogsWithCount()
            } returns Either.Left(NoResultsError)

            val response = callLogsRepository.latestCallLogsWithCount()

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsLocalDataSource.latestCallLogsWithCount() }
            coVerify(exactly = 0) { callLogsMapper.toCallLog(any<CallLogEntityWithCount>()) }
        }

    @Test
    fun `given local response is valid, when callLog by number is called, then propagate mapped value`() =
        runTest {
            val callLogEntity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLog(callLogEntity)
            } returns callLog

            coEvery {
                callLogsLocalDataSource.callLog(TEST_NUMBER)
            } returns Either.Right(callLogEntity)

            val response = callLogsRepository.callLog(TEST_NUMBER)

            response.assertRight {
                Assertions.assertEquals(it, callLog)
            }
            coVerify { callLogsLocalDataSource.callLog(eq(TEST_NUMBER)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given local response is empty, when callLog by number is called, then propagate error`() =
        runTest {
            coEvery {
                callLogsLocalDataSource.callLog(TEST_NUMBER)
            } returns Either.Left(NoResultsError)

            val response = callLogsRepository.callLog(TEST_NUMBER)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsLocalDataSource.callLog(eq(TEST_NUMBER)) }
            coVerify(exactly = 0) { callLogsMapper.toCallLog(any<CallLogEntityWithCount>()) }
        }

    @Test
    fun `given local response is valid, when callLog by status is called, then propagate mapped value`() =
        runTest {
            val status = CallStatus.ONGOING
            val callLogEntity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLog(callLogEntity)
            } returns callLog

            coEvery {
                callLogsLocalDataSource.callLog(status)
            } returns Either.Right(callLogEntity)

            val response = callLogsRepository.callLog(status)

            response.assertRight { Assertions.assertEquals(it, callLog) }
            coVerify { callLogsLocalDataSource.callLog(eq(status)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given log does not exist, when callLog by status is called, then propagate error`() =
        runTest {
            coEvery {
                callLogsLocalDataSource.callLog(CallStatus.ONGOING)
            } returns Either.Left(NoResultsError)

            val response = callLogsRepository.callLog(CallStatus.ONGOING)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsLocalDataSource.callLog(eq(CallStatus.ONGOING)) }
            coVerify(exactly = 0) { callLogsMapper.toCallLog(any<CallLogEntityWithCount>()) }
        }

    @Test
    fun `given local response is valid, when addNewCallLog is called, then propagate success`() =
        runTest {
            val entity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLogEntity(callLog)
            } returns entity

            coEvery {
                callLogsLocalDataSource.addNewCallLog(entity)
            } returns Either.Right(Unit)

            val response = callLogsRepository.addNewCallLog(callLog)

            response.assertRight()
            coVerify { callLogsLocalDataSource.addNewCallLog(eq(entity)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLogEntity(eq(callLog)) }
        }

    @Test
    fun `given local response fails, when addNewCallLog is called, then propagate error`() =
        runTest {
            val entity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLogEntity(callLog)
            } returns entity

            coEvery {
                callLogsLocalDataSource.addNewCallLog(entity)
            } returns Either.Left(NoResultsError)

            val response = callLogsRepository.addNewCallLog(callLog)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsLocalDataSource.addNewCallLog(eq(entity)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLogEntity(eq(callLog)) }
        }

    @Test
    fun `given local response succeeds, when updateCallLog is called, then propagate success`() =
        runTest {
            val entity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLogEntity(callLog)
            } returns entity

            coEvery {
                callLogsLocalDataSource.updateCallLog(entity)
            } returns Either.Right(Unit)

            val response = callLogsRepository.updateCallLog(callLog)

            response.assertRight()
            coVerify { callLogsLocalDataSource.updateCallLog(eq(entity)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLogEntity(eq(callLog)) }
        }

    @Test
    fun `given local response fails, when updateCallLog is called, then propagate error`() =
        runTest {
            val entity = mockk<CallLogEntity>()
            val callLog = mockk<CallLog>()

            coEvery {
                callLogsMapper.toCallLogEntity(callLog)
            } returns entity

            coEvery {
                callLogsLocalDataSource.updateCallLog(entity)
            } returns Either.Left(NoResultsError)

            val response = callLogsRepository.updateCallLog(callLog)

            response.assertLeft {
                Assertions.assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsLocalDataSource.updateCallLog(eq(entity)) }
            coVerify(exactly = 1) { callLogsMapper.toCallLogEntity(eq(callLog)) }
        }

    private fun fakeRepository(): CallLogsRepository {
        return CallLogsRepositoryImpl(fakeLocalDataSource, callLogsMapper)
    }

    companion object {
        private const val NUMBER_OF_TABLE_ROWS = 5
        private const val TEST_NUMBER = "+49 636736 367d"
    }
}
