package io.github.bradleyiw.ns.client.dashboard.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.bradleyiw.ns.client.calls.presentation.CallLogViewMapper
import io.github.bradleyiw.ns.common.calls.domain.CallLog
import io.github.bradleyiw.ns.core.service.ServiceController
import io.github.bradleyiw.ns.core.usecase.ReactiveUseCase
import io.github.bradleyiw.ns.server.ServerUrlProvider
import io.github.bradleyiw.ns.server.calls.CallMonitoringService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val callLogsViewMapper: CallLogViewMapper,
    private val serverUrlProvider: ServerUrlProvider,
    private val serviceController: ServiceController,
    callLogsUseCase: ReactiveUseCase<Unit, List<CallLog>>
) : ViewModel() {

    private val _isServiceRunningLiveData = MutableLiveData<Boolean>()
    val isServiceRunningLiveData: LiveData<Boolean> = _isServiceRunningLiveData

    private val _serverUrlLiveData = MutableLiveData<String>()
    val serverUrlLiveData: LiveData<String> = _serverUrlLiveData

    val callLogs by lazy {
        callLogsUseCase.execute(Unit)
            .flowOn(Dispatchers.IO)
            .map { it.map(callLogsViewMapper::toCallLogItem) }
    }

    fun onServerDataRequested() {
        serverUrlProvider.let {
            val serverDetails = it.provide()
            _serverUrlLiveData.value = serverDetails.toUrl()
        }

        serviceController.let {
            val isServiceRunning = it.isRunning(CallMonitoringService::class.java)
            _isServiceRunningLiveData.value = isServiceRunning
        }
    }

    fun onMonitoringCallsButtonClicked() {
        serviceController.let {
            val isServiceRunning = it.isRunning(CallMonitoringService::class.java)
            if (isServiceRunning) {
                it.stop(CallMonitoringService::class.java)
            } else {
                it.start(CallMonitoringService::class.java)
            }
            _isServiceRunningLiveData.value = !isServiceRunning
        }
    }
}
