package io.github.bradleyiw.ns.server

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import io.github.bradleyiw.ns.BuildConfig
import java.net.Inet4Address
import javax.inject.Inject

@ActivityScoped
class ServerUrlProvider @Inject constructor(
    @ApplicationContext
    private var context: Context?
) {

    fun provide() = ServerUrlDetails(
        ipAddress = ipAddress(),
        port = port()
    )

    private fun port(): Int = BuildConfig.CALL_MONITOR_SERVER_PORT.toInt()

    private fun ipAddress(): String? =
        activeNetworkLinkProperties()?.let {
            it.linkAddresses.firstNotNullOf { linkAddress ->
                val address = linkAddress.address
                if (address is Inet4Address
                    && !address.isLoopbackAddress
                    && address.isSiteLocalAddress
                ) {
                    address.hostAddress
                } else null
            }
        }

    private fun activeNetworkLinkProperties(): LinkProperties? =
        connectivityManager()?.let { connectivityManager ->
            connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        }

    private fun connectivityManager(): ConnectivityManager? = context?.let {
        it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
