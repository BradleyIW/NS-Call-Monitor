package io.github.bradleyiw.ns.server

import io.github.bradleyiw.ns.server.calls.CallLogsController
import io.github.bradleyiw.ns.server.routes.registerLogRoutes
import io.github.bradleyiw.ns.server.services.ServerServiceItem
import io.github.bradleyiw.ns.server.services.ServerServicesResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HttpServer(
    private val callLogsController: CallLogsController,
    private val serverDetails: ServerUrlDetails
) {

    private val server: CIOApplicationEngine by lazy {
        createServer()
    }

    private lateinit var startTime: LocalDateTime

    fun start() {
        CoroutineScope(Dispatchers.IO)
            .launch {
                startTime = LocalDateTime.now()
                server.start(wait = true)
            }
    }

    fun stop() {
        server.stop(0, 0)
    }

    private fun createServer(): CIOApplicationEngine =
        embeddedServer(CIO, serverDetails.port) {
            install(ContentNegotiation) {
                json()
            }
            val routing = install(Routing)

            registerLogRoutes(callLogsController)

            routing {
                get("/") {
                    val services = mapRoutesToService(serverDetails.toUrl(), routing)
                        .filterNot { call.request.path().contains(it.name) }
                    if (services.isNotEmpty()) {
                        val servicesResponse =
                            ServerServicesResponse(
                                startTime = startTime.format(DateTimeFormatter.ISO_DATE_TIME),
                                services = services
                            )
                        call.respond(HttpStatusCode.Accepted, servicesResponse)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "No services found on this server.")
                    }
                }
            }
        }

    private fun mapRoutesToService(url: String, root: Route): List<ServerServiceItem> =
        root.children.map {
            val name = it.toString().drop(1)
            val uri = "${url}/$name"
            ServerServiceItem(name, uri)
        }
}
