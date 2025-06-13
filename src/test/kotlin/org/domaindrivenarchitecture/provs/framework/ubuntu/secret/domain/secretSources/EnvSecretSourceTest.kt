package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class EnvSecretSourceTest {

    @Test
    @Disabled // set env variable "envtest=envtestval" externally e.g. in IDE and run manually
    fun secret() {
        assertEquals("envtestval",  EnvSecretSource("envtest").secret().plain())
    }
}