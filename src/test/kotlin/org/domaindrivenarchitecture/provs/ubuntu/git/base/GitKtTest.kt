package org.domaindrivenarchitecture.provs.ubuntu.git.base

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.isHostKnown
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class GitKtTest {

    @Test
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