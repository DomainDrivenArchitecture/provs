package org.domaindrivenarchitecture.provs.extensions.server_software.k3s.application

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class CliKtTest {

    @Test
    @Disabled // run manually -- todo mock execution
    fun provision_remotely() {

        main(arrayOf("-r", "192.168.56.141", "-u", "user", "-i"))
    }
}