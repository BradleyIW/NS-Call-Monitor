package io.github.bradleyiw.ns.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.CallLogsDao
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogQueryEntity
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogsQueryDao

@Database(
    entities = [CallLogEntity::class, CallLogQueryEntity::class],
    version = CallMonitoringDatabase.VERSION
)
@TypeConverters(value = [DatabaseConverters::class])
abstract class CallMonitoringDatabase : RoomDatabase() {

    abstract fun callLogsDao(): CallLogsDao
    abstract fun callLogsQueryDao(): CallLogsQueryDao

    companion object {
        const val VERSION = 1
        const val DB_NAME: String = "CallMonitoringDatabase"
    }
}

