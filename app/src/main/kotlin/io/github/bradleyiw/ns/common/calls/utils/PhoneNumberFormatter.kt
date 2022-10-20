package io.github.bradleyiw.ns.common.calls.utils

import android.telephony.PhoneNumberUtils
import java.util.*

class PhoneNumberFormatter {
    fun format(number: String, locale: Locale = Locale.getDefault()): String =
        PhoneNumberUtils.formatNumber(number, locale.country)
}
