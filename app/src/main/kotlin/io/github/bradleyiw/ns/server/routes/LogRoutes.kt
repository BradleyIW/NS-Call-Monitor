package io.github.bradleyiw.ns.server.routes

import io.github.bradleyiw.ns.core.exception.onFailure
import io.github.bradleyiw.ns.core.exception.onSuccess
import io.github.bradleyiw.ns.server.calls.CallLogsController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.registerLogRoutes(callLogsController: CallLogsController) {
    routing {
        getOngoingCallLogRoute(callLogsController)
        getAllLogsRoute(callLogsController)
    }
}

fun Route.getAllLogsRoute(callLogsController: CallLogsController) {
    get("/logs") {
        runCatching {
            callLogsController.onCallLogsRequested()
                .onSuccess {
                    call.respond(HttpStatusCode.Accepted, it)
                }
                .onFailure {
                    call.respond(HttpStatusCode.NotFound, "No monitored calls available.")
                }
        }.getOrElse {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Something has gone wrong: ${it.localizedMessage}"
            )
        }
    }
}

fun Route.getOngoingCallLogRoute(callLogsController: CallLogsController) {
    get("/ongoing") {
        runCatching {
            callLogsController.onOngoingCallRequested()
                .onSuccess {
                    call.respond(HttpStatusCode.Accepted, it)
                }
                .onFailure {
                    call.respond(HttpStatusCode.NotFound, "There are no ongoing calls happening.")
                }
        }.getOrElse {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Something has gone wrong: ${it.localizedMessage}"
            )
        }
    }
}
