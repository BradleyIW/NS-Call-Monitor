package io.github.bradleyiw.ns.common.calls

import io.github.bradleyiw.ns.common.calls.data.local.CallLogEntity
import io.github.bradleyiw.ns.common.calls.data.local.query.CallLogEntityWithCount
import io.github.bradleyiw.ns.common.calls.domain.CallLog

class CallLogsMapper {

    fun toCallLog(log: CallLogEntity): CallLog =
        CallLog(
            id = log.id,
            name = log.name,
            number = log.number,
            startTime = log.startTime,
            endTime = log.endTime,
            status = log.status,
            type = log.type,
            createdAt = log.createdAt,
            modifiedAt = log.modifiedAt
        )

    fun toCallLog(log: CallLogEntityWithCount): CallLog =
        CallLog(
            id = log.logEntity.id,
            name = log.logEntity.name,
            number = log.logEntity.number,
            startTime = log.logEntity.startTime,
            endTime = log.logEntity.endTime,
            status = log.logEntity.status,
            type = log.logEntity.type,
            numberOfQueries = log.numberOfQueries,
            createdAt = log.logEntity.createdAt,
            modifiedAt = log.logEntity.modifiedAt
        )


    fun toCallLogEntity(log: CallLog): CallLogEntity =
        CallLogEntity(
            id = log.id,
            name = log.name,
            number = log.number,
            startTime = log.startTime,
            endTime = log.endTime,
            status = log.status,
            type = log.type,
            createdAt = log.createdAt,
            modifiedAt = log.modifiedAt
        )
}
