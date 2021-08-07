package org.domaindrivenarchitecture.provs.ubuntu.utils

import org.domaindrivenarchitecture.provs.core.escapeBackslash
import org.domaindrivenarchitecture.provs.core.escapeDoubleQuote


// todo: investigate to use .escapeAndEncloseByDoubleQuoteForShell() or similar instead (?)
internal fun printToShell(text: String): String {
    return "echo -n \"${text.escapeBackslash().escapeDoubleQuote()}\""
}

