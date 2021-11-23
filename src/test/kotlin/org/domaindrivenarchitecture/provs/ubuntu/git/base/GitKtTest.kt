package org.domaindrivenarchitecture.provs.ubuntu.git.base

import org.domaindrivenarchitecture.provs.core.processors.ContainerStartMode
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.isHostKnown
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class GitKtTest {

    @Test
    fun trustGitServers(){
        // given
        val a = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)
        a.aptInstall("openssh-client")

        // when
        val res = a.trustGithub()
        val known = a.isHostKnown("github.com")
        val res2 = a.trustGitlab()
        val known2 = a.isHostKnown("gitlab.com")

        // then
        assertTrue(res.success)
        assertTrue(known)
        assertTrue(res2.success)
        assertTrue(known2)
    }

    @Test
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