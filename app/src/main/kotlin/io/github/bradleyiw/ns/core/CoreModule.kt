package io.github.bradleyiw.ns.core

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.bradleyiw.ns.BuildConfig
import io.github.bradleyiw.ns.common.CallMonitoringDatabase
import io.github.bradleyiw.ns.core.service.ServiceController
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun providesCallMonitoringDatabase(
        @ApplicationContext context: Context
    ): CallMonitoringDatabase {
        val newLogTrigger = """
            CREATE TRIGGER create_log_trigger
            AFTER INSERT ON call_log
            BEGIN
              INSERT INTO call_log_query(call_log_id, number_of_queries) VALUES (NEW.id, 1);
            END;
        """.trimIndent()
        val updateLogTrigger = """
            CREATE TRIGGER update_log_trigger 
            AFTER UPDATE ON call_log FOR EACH ROW
            BEGIN
                UPDATE call_log_query
                SET number_of_queries = number_of_queries + 1
                WHERE call_log_id = NEW.id;
            END;
        """.trimIndent()
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(newLogTrigger)
                db.execSQL(updateLogTrigger)
            }
        }
        return Room.databaseBuilder(
            context,
            CallMonitoringDatabase::class.java,
            CallMonitoringDatabase.DB_NAME
        ).addCallback(callback).setQueryCallback({ sqlQuery, _ ->
            if (BuildConfig.DEBUG) {
                Log.d(CallMonitoringDatabase.DB_NAME, sqlQuery)
            }
        }, Executors.newSingleThreadExecutor()).build()
    }

    @Provides
    fun providesServiceController(@ApplicationContext context: Context): ServiceController =
        ServiceController(context)

}
