package io.github.bradleyiw.ns.server

import io.github.bradleyiw.ns.framework.UnitTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ServerUrlDetailsTest : UnitTest() {

    @Test
    fun `given port and ip address, when toUrl is called, then propagate url`() {
        val serverUrlDetails = ServerUrlDetails(
            ipAddress = "192.168.9.0",
            port = 2345
        )

        val url = serverUrlDetails.toUrl()
        Assertions.assertEquals(url, "http://192.168.9.0:2345")
    }
}
