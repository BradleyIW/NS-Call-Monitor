package io.github.bradleyiw.ns.server

data class ServerUrlDetails(
    val ipAddress: String?,
    val port: Int
) {
    fun toUrl(): String = "http://${ipAddress}:${port}"
}
