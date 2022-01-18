package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources

import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class PromptSecretSourceTest {

    @Test
    @Disabled // run manually
    fun secret() {
        println("Secret: " + PromptSecretSource().secret().plain())
    }
}