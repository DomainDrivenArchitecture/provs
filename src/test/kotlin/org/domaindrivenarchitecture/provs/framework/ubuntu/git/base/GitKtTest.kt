package org.domaindrivenarchitecture.provs.framework.ubuntu.git.base

import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.isHostKnown
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class GitKtTest {

    @Test
    @ContainerTest
    fun trustGitServers(){
        // given
        val a = defaultTestContainer()
        a.aptInstall("openssh-client")

        // when
        val res = a.trustGithub()
        val res2 = a.trustGitlab()

        // then
        assertTrue(res.success)
        assertTrue(res2.success)

        assertTrue(a.isHostKnown("github.com"), "github.com does not seem to be a known host")
        assertTrue(a.isHostKnown("gitlab.com"), "gitlab.com does not seem to be a known host")
    }

    @Test
    @ContainerTest
    fun gitClone() {
        // given
        val prov = defaultTestContainer()
        prov.aptInstall("openssh-client ssh git")

        // when
        prov.trustGithub()
        prov.gitClone("https://github.com/DomainDrivenArchitecture/dda-git-crate.git", "~/")
        val res = prov.gitClone("https://github.com/DomainDrivenArchitecture/dda-git-crate.git", "~/")

        // then
        assertTrue(res.success)
    }
}