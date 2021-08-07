package io.provs.ubuntu.git.base

import io.provs.test.defaultTestContainer
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.keys.base.isHostKnown
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