package io.github.bradleyiw.ns.client.dashboard.presentation

import io.github.bradleyiw.ns.client.calls.presentation.CallLogItem
import io.github.bradleyiw.ns.client.calls.presentation.CallLogViewMapper
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.core.service.ServiceController
import io.github.bradleyiw.ns.core.usecase.ReactiveUseCase
import io.github.bradleyiw.ns.framework.ViewModelTest
import io.github.bradleyiw.ns.framework.buildListOfType
import io.github.bradleyiw.ns.framework.livedata.awaitValue
import io.github.bradleyiw.ns.server.ServerUrlProvider
import io.github.bradleyiw.ns.server.calls.CallMonitoringService
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FakeUseCase : ReactiveUseCase<Unit, List<CallLog>> {
    private val flow = MutableSharedFlow<List<CallLog>>()
    suspend fun emit(value: List<CallLog>) = flow.emit(value)
    override fun run(params: Unit): Flow<List<CallLog>> =
        flow
}

@ExperimentalCoroutinesApi
internal class DashboardViewModelTest : ViewModelTest() {

    private lateinit var viewModel: DashboardViewModel

    private val getCallLogsUseCase: FakeUseCase = FakeUseCase()

    @MockK
    private lateinit var serviceDetailsProvider: ServerUrlProvider

    @RelaxedMockK
    private lateinit var serviceController: ServiceController

    @RelaxedMockK
    private lateinit var callLogsViewMapper: CallLogViewMapper

    @BeforeEach
    fun setup() {
        viewModel = DashboardViewModel(
            callLogsViewMapper,
            serviceDetailsProvider,
            serviceController,
            getCallLogsUseCase
        )
    }

    @Test
    fun `given call logs exist, then map and propagate call log view items`() = runTest {
        val callLogs = mockk<CallLog>()

        val viewLogs = mutableListOf<List<CallLogItem>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.callLogs.toList(viewLogs)
        }

        getCallLogsUseCase.emit(buildListOfType(NUMBER_OF_LOGS, callLogs))

        assertEquals(viewLogs.first().size, NUMBER_OF_LOGS)
        coVerify(exactly = NUMBER_OF_LOGS) { callLogsViewMapper.toCallLogItem(eq(callLogs)) }

        collectJob.cancel()
    }

    @Test
    fun `given no call logs exist, then don't propagate flow`() = runTest {
        val viewLogs = mutableListOf<List<CallLogItem>>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.callLogs.toList(viewLogs)
        }

        getCallLogsUseCase.emit(emptyList())

        assertTrue(viewLogs.first().isEmpty())
        coVerify(exactly = 0) { callLogsViewMapper.toCallLogItem(any()) }

        collectJob.cancel()
    }

    @Test
    fun `given server is running, when server data is requested, then update isServerRunning live data`() {
        runTest {
            every {
                serviceDetailsProvider.provide()
            } returns mockk {
                every { toUrl() } returns TEST_SERVER_URL
            }

            every {
                serviceController.isRunning(CallMonitoringService::class.java)
            } returns true

            viewModel.onServerDataRequested()

            assertTrue(viewModel.isServiceRunningLiveData.awaitValue())
        }
    }

    @Test
    fun `given server is not running, when server data is requested, then update isServerRunning live data`() {
        runTest {
            every {
                serviceDetailsProvider.provide()
            } returns mockk {
                every { toUrl() } returns TEST_SERVER_URL
            }

            every {
                serviceController.isRunning(CallMonitoringService::class.java)
            } returns false

            viewModel.onServerDataRequested()

            assertFalse(viewModel.isServiceRunningLiveData.awaitValue())
        }
    }

    @Test
    fun `given server details url, when server data is requested, then update serverDetails live data`() {
        runTest {
            every {
                serviceDetailsProvider.provide()
            } returns mockk {
                every { toUrl() } returns TEST_SERVER_URL
            }

            every {
                serviceController.isRunning(CallMonitoringService::class.java)
            } returns false

            viewModel.onServerDataRequested()

            assertEquals(viewModel.serverUrlLiveData.awaitValue(), TEST_SERVER_URL)
        }
    }

    @Test
    fun `given server is running, when monitor calls button is clicked, then stop server and propagate status`() {
        runTest {
            every {
                serviceController.isRunning(CallMonitoringService::class.java)
            } returns true

            viewModel.onMonitoringCallsButtonClicked()

            verify {
                serviceController.stop(eq(CallMonitoringService::class.java))
            }
            assertFalse(viewModel.isServiceRunningLiveData.awaitValue())
        }
    }

    @Test
    fun `given server is not running, when monitor calls button is clicked, then start server and propagate status`() {
        runTest {
            every {
                serviceController.isRunning(CallMonitoringService::class.java)
            } returns false

            viewModel.onMonitoringCallsButtonClicked()

            verify {
                serviceController.start(eq(CallMonitoringService::class.java))
            }
            assertTrue(viewModel.isServiceRunningLiveData.awaitValue())
        }
    }

    companion object {
        private const val NUMBER_OF_LOGS = 5
        private const val TEST_SERVER_URL = "https:/192.168.8.0:8080"
    }
}
