package io.provs.ubuntu.utils

import io.provs.escapeBackslash
import io.provs.escapeDoubleQuote


// todo: investigate to use .escapeAndEncloseByDoubleQuoteForShell() or similar instead (?)
internal fun printToShell(text: String): String {
    return "echo -n \"${text.escapeBackslash().escapeDoubleQuote()}\""
}

