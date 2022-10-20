package io.github.bradleyiw.ns.common.calls.data.local

import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.utils.CallStatus
import io.github.bradleyiw.ns.core.database.NoResultsError
import io.github.bradleyiw.ns.core.exception.Either
import io.github.bradleyiw.ns.framework.UnitTest
import io.github.bradleyiw.ns.framework.assertLeft
import io.github.bradleyiw.ns.framework.assertRight
import io.github.bradleyiw.ns.framework.buildListOfType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class CallLogsLocalDataSourceTest : UnitTest() {

    private lateinit var callLogsLocalDataSource: CallLogsLocalDataSourceImpl

    @RelaxedMockK
    private lateinit var callLogsDatabaseService: CallLogsDatabaseService

    @BeforeEach
    fun setup() {
        callLogsLocalDataSource = CallLogsLocalDataSourceImpl(callLogsDatabaseService)
    }

    @Test
    fun `given callLogs is requested, then verify correct call is made`() {
        callLogsLocalDataSource.callLogs()

        verify {
            callLogsDatabaseService.callLogs()
        }
    }

    @Test
    fun `given database service response succeeds, when latestCallLogsWithCount is requested, then propagate values`() =
        runTest {
            val callLogEntityWithCount: CallLogEntityWithCount = mockk()
            val latestCallLogs = buildListOfType(NUMBER_OF_TABLE_ROWS, callLogEntityWithCount)

            coEvery {
                callLogsDatabaseService.latestCallLogsWithCount()
            } returns Either.Right(latestCallLogs)

            val response = callLogsLocalDataSource.latestCallLogsWithCount()

            response.assertRight {
                assertEquals(it, latestCallLogs)
            }
            coVerify { callLogsDatabaseService.latestCallLogsWithCount() }
        }

    @Test
    fun `given database service response fails, when latestCallLogsWithCount is requested, then propagate error`() =
        runTest {
            coEvery {
                callLogsDatabaseService.latestCallLogsWithCount()
            } returns Either.Left(NoResultsError)

            val response = callLogsLocalDataSource.latestCallLogsWithCount()

            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDatabaseService.latestCallLogsWithCount() }
        }

    @Test
    fun `given database service response succeeds, when callLog by number is requested, then propagate values`() =
        runTest {
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.callLog(TEST_NUMBER)
            } returns Either.Right(callLogEntity)

            val response = callLogsLocalDataSource.callLog(TEST_NUMBER)

            response.assertRight {
                assertEquals(it, callLogEntity)
            }
            coVerify { callLogsDatabaseService.callLog(eq(TEST_NUMBER)) }
        }

    @Test
    fun `given database service response fails, when callLog by number is requested, then propagate error`() =
        runTest {
            coEvery {
                callLogsDatabaseService.callLog(TEST_NUMBER)
            } returns Either.Left(NoResultsError)

            val response = callLogsLocalDataSource.callLog(TEST_NUMBER)

            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDatabaseService.callLog(eq(TEST_NUMBER)) }
        }

    @Test
    fun `given database service response succeeds, when callLog by status is requested, then propagate values`() =
        runTest {
            val status = CallStatus.ONGOING
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.callLog(status)
            } returns Either.Right(callLogEntity)

            val response = callLogsLocalDataSource.callLog(status)

            response.assertRight {
                assertEquals(it, callLogEntity)
            }
            coVerify { callLogsDatabaseService.callLog(eq(status)) }
        }

    @Test
    fun `given database service response fails, when callLog by status is requested, then propagate error`() =
        runTest {
            val status = CallStatus.ONGOING
            coEvery {
                callLogsDatabaseService.callLog(status)
            } returns Either.Left(NoResultsError)

            val response = callLogsLocalDataSource.callLog(status)

            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDatabaseService.callLog(eq(status)) }
        }

    @Test
    fun `given database service response succeeds, when addNewCallLog is requested, then propagate success`() =
        runTest {
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.addNewCallLog(callLogEntity)
            } returns Either.Right(Unit)

            val response = callLogsLocalDataSource.addNewCallLog(callLogEntity)

            response.assertRight()
            coVerify { callLogsDatabaseService.addNewCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given database service response fails, when addNewCallLog is requested, then propagate error`() =
        runTest {
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.addNewCallLog(callLogEntity)
            } returns Either.Left(NoResultsError)

            val response = callLogsLocalDataSource.addNewCallLog(callLogEntity)

            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDatabaseService.addNewCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given database service response succeeds, when updateCallLog is requested, then propagate success`() =
        runTest {
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.updateCallLog(callLogEntity)
            } returns Either.Right(Unit)

            val response = callLogsLocalDataSource.updateCallLog(callLogEntity)

            response.assertRight()
            coVerify { callLogsDatabaseService.updateCallLog(eq(callLogEntity)) }
        }

    @Test
    fun `given database service response fails, when updateCallLog is requested, then propagate error`() =
        runTest {
            val callLogEntity: CallLogEntity = mockk()
            coEvery {
                callLogsDatabaseService.updateCallLog(callLogEntity)
            } returns Either.Left(NoResultsError)

            val response = callLogsLocalDataSource.updateCallLog(callLogEntity)

            response.assertLeft {
                assertInstanceOf(NoResultsError::class.java, it)
            }
            coVerify { callLogsDatabaseService.updateCallLog(eq(callLogEntity)) }
        }

    companion object {
        private const val NUMBER_OF_TABLE_ROWS = 5
        private const val TEST_NUMBER = "+49736636637"
    }
}
