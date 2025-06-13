package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.fileContainsText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class BashKtTest {

    @ContainerTest
    fun configureBashForUser() {
        // given
        val prov = defaultTestContainer()
        // when
        val res = prov.task {
            configureBashForUser()
            createFile(".bashrc.d/testbashrcd.sh", "alias testbashrcd=\"echo -n works\"\n")
        }
        val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure/"
        val expectedText = getResourceAsText(resourcePath + "bashrcd-enhancement.sh").trimIndent()
        val containsText = prov.fileContainsText(".bashrc", expectedText)
        val out = prov.cmd("/bin/bash -ci \". ~/.bashrc && testbashrcd\"").out

        // then
        assertTrue(res.success)
        assertTrue(containsText)
        assertEquals("works", out)
    }
}