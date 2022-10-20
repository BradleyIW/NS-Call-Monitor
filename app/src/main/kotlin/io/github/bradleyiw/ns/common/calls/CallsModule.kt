package io.github.bradleyiw.ns.common.calls

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.bradleyiw.ns.common.CallMonitoringDatabase
import io.github.bradleyiw.ns.common.calls.data.CallLogsRepositoryImpl
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsDao
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsDatabaseService
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsLocalDataSource
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsLocalDataSourceImpl
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogsQueryDao
import io.github.bradleyiw.ns.common.calls.domain.CallLogsRepository
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallAnsweredUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallEndedUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallRingingUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetCallLogsUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetLatestCallLogsUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.GetOngoingCallUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CallsModule {

    @Binds
    fun bindCallLogsRepository(callLogsRepository: CallLogsRepositoryImpl): CallLogsRepository

    @Binds
    fun bindCallLogsLocalDataSource(callLogsRepository: CallLogsLocalDataSourceImpl): CallLogsLocalDataSource

    companion object {

        @Provides
        fun providesPhoneStateReceiver(
            callRingingUseCase: CallRingingUseCase,
            callAnsweredUseCase: CallAnsweredUseCase,
            callEndedUseCase: CallEndedUseCase
        ): PhoneStateReceiver =
            PhoneStateReceiver(callRingingUseCase, callAnsweredUseCase, callEndedUseCase)

        @Provides
        @Singleton
        fun providesCallLogsRepository(
            callLogsLocalDataSource: CallLogsLocalDataSourceImpl,
            callsMapper: CallLogsMapper
        ): CallLogsRepositoryImpl =
            CallLogsRepositoryImpl(
                callLogsLocalDataSource,
                callsMapper
            )

        @Provides
        fun providesCallLogsLocalDataSource(
            callLogsDatabaseService: CallLogsDatabaseService
        ): CallLogsLocalDataSourceImpl =
            CallLogsLocalDataSourceImpl(callLogsDatabaseService)

        @Provides
        fun providesCallLogsDatabaseService(
            callLogsQueryDao: CallLogsQueryDao,
            callsDao: CallLogsDao
        ): CallLogsDatabaseService =
            CallLogsDatabaseService(callLogsQueryDao, callsDao)

        @Provides
        fun provideCallLogsDao(database: CallMonitoringDatabase): CallLogsDao =
            database.callLogsDao()

        @Provides
        fun provideCallLogsQueryDao(database: CallMonitoringDatabase): CallLogsQueryDao =
            database.callLogsQueryDao()

        @Provides
        fun providesCallLogsMapper(): CallLogsMapper = CallLogsMapper()

        @Provides
        fun provideCallEndedUseCase(callLogsRepository: CallLogsRepository): CallEndedUseCase =
            CallEndedUseCase(callLogsRepository)

        @Provides
        fun provideCallRingingUseCase(callLogsRepository: CallLogsRepository): CallRingingUseCase =
            CallRingingUseCase(callLogsRepository)

        @Provides
        fun provideCallAnsweredUseCase(callLogsRepository: CallLogsRepository): CallAnsweredUseCase =
            CallAnsweredUseCase(callLogsRepository)

        @Provides
        fun provideGetCallLogsUseCase(callLogsRepository: CallLogsRepository): GetCallLogsUseCase =
            GetCallLogsUseCase(callLogsRepository)

        @Provides
        fun provideGetLatestCallLogsUseCase(callLogsRepository: CallLogsRepository): GetLatestCallLogsUseCase =
            GetLatestCallLogsUseCase(callLogsRepository)

        @Provides
        fun provideGetOngoingCallUseCase(callLogsRepository: CallLogsRepository): GetOngoingCallUseCase =
            GetOngoingCallUseCase(callLogsRepository)
    }
}
