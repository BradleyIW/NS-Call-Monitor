package io.github.bradleyiw.ns.server.services

@kotlinx.serialization.Serializable
data class ServerServicesResponse(
    val startTime: String,
    val services: List<ServerServiceItem>
)
