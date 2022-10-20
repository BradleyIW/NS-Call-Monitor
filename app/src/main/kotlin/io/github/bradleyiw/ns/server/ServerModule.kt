package io.github.bradleyiw.ns.server

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetLatestCallLogsUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetOngoingCallUseCase
import io.github.bradleyiw.ns.common.calls.utils.PhoneNumberFormatter
import io.github.bradleyiw.ns.server.calls.CallLogResponseMapper
import io.github.bradleyiw.ns.server.calls.CallLogsController

@Module
@InstallIn(SingletonComponent::class)
object ServerModule {

    @Provides
    fun providersServerDetailsProvider(@ApplicationContext context: Context): ServerUrlProvider =
        ServerUrlProvider(context)

    @Provides
    fun provideHttpServer(
        callLogsController: CallLogsController,
        serviceDetailsProvider: ServerUrlProvider
    ): HttpServer = HttpServer(callLogsController, serviceDetailsProvider.provide())

    @Provides
    fun provideCallLogsController(
        callLogResponseMapper: CallLogResponseMapper,
        getLatestCallLogsUseCase: GetLatestCallLogsUseCase,
        getOngoingCallUseCase: GetOngoingCallUseCase
    ): CallLogsController = CallLogsController(
        callLogResponseMapper,
        getLatestCallLogsUseCase,
        getOngoingCallUseCase
    )

    @Provides
    fun providesViewResponseMapper(phoneNumberFormatter: PhoneNumberFormatter) =
        CallLogResponseMapper(phoneNumberFormatter)

    @Provides
    fun providesPhoneNumberFormatter() = PhoneNumberFormatter()
}

