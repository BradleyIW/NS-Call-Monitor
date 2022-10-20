package io.github.bradleyiw.ns.common.calls.utils

enum class CallType {
    INCOMING {
        override fun toString(): String {
            return "Incoming"
        }
    },
    OUTGOING {
        override fun toString(): String {
            return "Outgoing"
        }
    }
}
