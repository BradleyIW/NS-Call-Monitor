package io.github.bradleyiw.ns.common.calls.utils

enum class CallStatus {
    RINGING {
        override fun toString(): String = "Ringing"
    },
    ONGOING {
        override fun toString(): String = "Ongoing"
    },
    MISSED {
        override fun toString(): String = "Missed"
    },
    COMPLETE {
        override fun toString(): String = "Completed"
    };
}
