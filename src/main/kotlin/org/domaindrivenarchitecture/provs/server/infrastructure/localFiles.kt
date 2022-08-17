package org.domaindrivenarchitecture.provs.server.infrastructure

import java.io.File

fun genericFileExistenceCheck(fileName: String): Boolean {
    if (fileName.isEmpty()) {
        return false
    } else if ((!File(fileName).exists())) {
        return false
    }
    return true
}
