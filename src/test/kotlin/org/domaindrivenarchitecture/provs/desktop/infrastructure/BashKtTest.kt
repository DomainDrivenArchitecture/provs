package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.getResourceAsText
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class BashKtTest {

    @Test
    @ContainerTest
    fun configureBashForUser() {
        // when
        val res = defaultTestContainer().task {
            configureBashForUser()
            createFile(".bashrc.d/testbashrcd.sh", "alias testbashrcd=\"echo -n works\"\n")
        }
        val resourcePath = "org/domaindrivenarchitecture/provs/desktop/infrastructure/"
        val expectedText = getResourceAsText(resourcePath + "bashrcd-enhancement.sh").trimIndent()
        val containsText = defaultTestContainer().fileContainsText(".bashrc", expectedText)
        val out = defaultTestContainer().cmd("/bin/bash -ci \". ~/.bashrc && testbashrcd\"").out

        // then
        assertTrue(res.success)
        assertTrue(containsText)
        assertEquals("works", out)
    }
}