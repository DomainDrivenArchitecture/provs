package org.domaindrivenarchitecture.provs.framework.ubuntu.git.base

import org.domaindrivenarchitecture.provs.desktop.domain.addKnownHosts
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.checkDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.framework.ubuntu.keys.base.isKnownHost
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.ExtensiveContainerTest
import org.junit.jupiter.api.Assertions.*


internal class GitKtTest {

    @ContainerTest
    fun trustGitServers(){
        // given
        val a = defaultTestContainer()
        a.aptInstall("openssh-client")

        // when
        val res = a.addKnownHosts()

        // then
        assertTrue(res.success)

        assertTrue(a.isKnownHost("github.com"), "github.com does not seem to be a known host")
        assertTrue(a.isKnownHost("gitlab.com"), "gitlab.com does not seem to be a known host")
    }

    @ExtensiveContainerTest
    fun gitClone() {
        // given
        val repo = "https://gitlab.com/domaindrivenarchitecture/overview.git"
        val prov = defaultTestContainer()
        prov.aptInstall("git")

        // when
        prov.addKnownHosts()
        val res1 = prov.gitClone("https://gitlab.com/domaindrivenarchitecture/not a valid basename.git", "~/")
        val res2 = prov.gitClone(repo)
        val res3 = prov.gitClone(repo, pullIfExisting = false)
        val res4 = prov.gitClone(repo, "pathtocreate")
        val res5 = prov.gitClone(repo, "pathtocreate", targetFolderName = "alternativeBasename")
        val res6 = prov.gitClone(repo, "pathtocreate", targetFolderName = "alternativeBasename")
        val res7 = prov.gitClone(repo, "pathtocreate", false, targetFolderName = "alternativeBasename")

        // then
        assertFalse(res1.success)
        assertTrue(res2.success)
        assertTrue(res3.success)
        assertEquals("Repo [overview] already exists, but might not be up-to-date.", res3.out)
        assertTrue(res4.success)
        assertTrue(prov.checkDir("pathtocreate/overview"))
        assertTrue(res5.success)
        assertTrue(prov.checkDir("pathtocreate/alternativeBasename/.git"))
        assertTrue(res6.success)
        assertTrue(res7.success)
        assertEquals("Repo [pathtocreate/alternativeBasename] already exists, but might not be up-to-date.", res7.out)
    }
}