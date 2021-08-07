package io.provs.ubuntu.extensions.demos

import io.provs.core.*


/**
 * Prints some information and settings of the operating system and environment.
 *
 * For running locally no arguments are required.
 * For running remotely either 2 or 3 arguments must be provided:
 * either host and user for connection by ssh key ()
 * or host, user and password for password-authenticated connection.
 * E.g. 172.0.0.123 username or 172.0.0.123 username password
 */
fun main(vararg args: String) {
    if (args.isEmpty()) {
        local().printInfos()
    } else {
        if (args.size !in 2..3)  {
            println("Wrong number of arguments. Please specify either host and user if connection is done by ssh key or otherwise host, user and password. E.g. 172.0.0.123 username password")
        } else {
            val password = if (args.size == 2) null else Secret(args[3])
            remote(args[0], args[1], password = password).printInfos()
        }
    }
}


fun Prov.printInfos() = def {
    println("\nUbuntu Version:\n${ubuntuVersion()}")
    println("\nCurrent directory:\n${currentDir()}")
    println("\nTime zone:\n${timeZone()}")

    val dir = cmd("pwd").out
    println("\nCurrent directory: $dir")

    ProvResult(true)
}


fun Prov.ubuntuVersion(): String? {
    return cmd("lsb_release -a").out
}


fun Prov.currentDir(): String? {
    return cmd("pwd").out
}


fun Prov.timeZone(): String? {
    return cmd("cat /etc/timezone").out
}

