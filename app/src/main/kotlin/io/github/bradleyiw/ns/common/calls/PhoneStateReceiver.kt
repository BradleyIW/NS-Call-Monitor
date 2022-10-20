package io.github.bradleyiw.ns.common.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallAnsweredParams
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallAnsweredUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallEndedParams
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallEndedUseCase
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallRingingParams
import io.github.bradleyiw.ns.common.calls.domain.usecase.CallRingingUseCase
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class PhoneStateReceiver(
    private val callRingingUseCase: CallRingingUseCase,
    private val callAnsweredUseCase: CallAnsweredUseCase,
    private val callEndedUseCase: CallEndedUseCase
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        runBlocking {
            val state: String = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                ?: return@runBlocking
            val phoneNumber = intent.getStringExtra(INCOMING_NUMBER_BUNDLE_KEY)
                ?: return@runBlocking

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    val contactName = findContactName(phoneNumber, context)
                    val callRingingParams = CallRingingParams(contactName, phoneNumber)
                    callRingingUseCase.execute(callRingingParams)
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    val contactName = findContactName(phoneNumber, context)
                    val callAnsweredParams =
                        CallAnsweredParams(contactName, phoneNumber, LocalDateTime.now())
                    callAnsweredUseCase.execute(callAnsweredParams)
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    val callEndedParams = CallEndedParams(phoneNumber, LocalDateTime.now())
                    callEndedUseCase.execute(callEndedParams)
                }
            }
        }
    }

    private fun findContactName(phoneNumber: String, context: Context): String? =
        with(context) {
            val contactUri: Uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val displayNameLookup = ContactsContract.PhoneLookup.DISPLAY_NAME
            val projection = arrayOf(displayNameLookup)
            return contentResolver.query(contactUri, projection, null, null, null)
                ?.use { cursor ->
                    val nameColumnIndex = cursor.getColumnIndex(displayNameLookup)
                    return if (cursor.moveToFirst()) {
                        cursor.getString(nameColumnIndex)
                    } else {
                        null
                    }
                }
        }

    companion object {
        private const val INCOMING_NUMBER_BUNDLE_KEY = "incoming_number"
    }
}
