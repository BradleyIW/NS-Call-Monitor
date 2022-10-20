@file:Suppress("MagicNumber")
package io.github.bradleyiw.ns.client.calls.utils

enum class CallLogViewType(val type: Int) {
    MISSED_INCOMING(1),
    MISSED_OUTGOING(2),
    COMPLETE_INCOMING(3),
    COMPLETE_OUTGOING(4),
    ONGOING(5)
}
