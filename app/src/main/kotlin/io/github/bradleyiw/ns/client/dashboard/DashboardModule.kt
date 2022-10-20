package io.github.bradleyiw.ns.client.dashboard

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.bradleyiw.ns.client.calls.presentation.CallLogViewMapper
import io.github.bradleyiw.ns.client.dashboard.presentation.DashboardViewModel
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetCallLogsUseCase
import io.github.bradleyiw.ns.core.service.ServiceController
import io.github.bradleyiw.ns.server.ServerUrlProvider

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    fun providesViewModel(
        callLogsViewMapper: CallLogViewMapper,
        serverUrlProvider: ServerUrlProvider,
        serviceController: ServiceController,
        getCallLogsUseCase: GetCallLogsUseCase
    ): DashboardViewModel =
        DashboardViewModel(
            callLogsViewMapper,
            serverUrlProvider,
            serviceController,
            getCallLogsUseCase
        )

    @Provides
    fun callLogsViewMapper() = CallLogViewMapper()
}
