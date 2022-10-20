package io.github.bradleyiw.ns.server.services

@kotlinx.serialization.Serializable
data class ServerServiceItem(
    val name: String,
    val uri: String
)

