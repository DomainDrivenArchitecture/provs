package org.domaindrivenarchitecture.provs.server.domain

import org.domaindrivenarchitecture.provs.server.domain.ServerType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class CertmanagerEndpointTest {

    @Test
    fun shouldResultCorrectEndpoint() {
        // given
        val cut = CertmanagerEndpoint.prod

        // when
        val result = cut.endpointUri()

        // then
        assertEquals("https://acme-v02.api.letsencrypt.org/directory", result)
    }
}