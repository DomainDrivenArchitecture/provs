package org.domaindrivenarchitecture.provs.server.application

import org.domaindrivenarchitecture.provs.server.application.main
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class CliServerKtTest {

    @Test
    @Disabled // run manually -- todo mock execution
    fun provision_remotely() {

        main(arrayOf("-r", "192.168.56.141", "-u", "user", "-i"))
    }
}