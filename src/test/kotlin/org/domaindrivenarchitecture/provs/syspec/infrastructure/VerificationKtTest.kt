package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDirs
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createFile
import org.domaindrivenarchitecture.provs.syspec.domain.*
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class VerificationKtTest {

    @ContainerTest
    fun test_verify_folder_existing() {
        // given
        val dir = "/home/testuser/testdir4spec"
        val prov = defaultTestContainer()
        prov.createDirs(dir)

        // when
        val res = prov.task {
            verify(FolderSpec(dir))
            ProvResult(true) // dummy
        }
        val res2 = prov.task {
            verify(FolderSpec(dir, exists = false))
            ProvResult(true) // dummy
        }

        // then
        assertTrue(res.success)
        assertFalse(res2.success)
    }

    @ContainerTest
    fun test_verify_folder_nonexisting() {
        // given
        val dir = "/home/testuser/testdirnonexisting"
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            verify(FolderSpec(dir))
            ProvResult(true) // dummy
        }
        val res2 = prov.task {
            verify(FolderSpec(dir, exists = false))
            ProvResult(true) // dummy
        }

        // then
        assertFalse(res.success)
        assertTrue(res2.success)
    }

    @ContainerTest
    fun test_verify_file_existing() {
        // given
        val file = "/home/testuser/testfile4spec"
        val prov = defaultTestContainer()
        prov.createFile(file,"testcontent")

        // when
        val res = prov.task {
            verify(FileSpec(file))
            ProvResult(true) // dummy
        }
        val res2 = prov.task {
            verify(FileSpec(file, exists = false))
            ProvResult(true) // dummy
        }

        // then
        assertTrue(res.success)
        assertFalse(res2.success)
    }

    @ContainerTest
    fun test_verify_file_nonexisting() {
        // given
        val file = "/home/testuser/testfile-nonexisting"
        val prov = defaultTestContainer()

        // when
        val res = prov.task {
            verify(FileSpec(file))
            ProvResult(true) // dummy
        }
        val res2 = prov.task {
            verify(FileSpec(file, exists = false))
            ProvResult(true) // dummy
        }

        // then
        assertFalse(res.success)
        assertTrue(res2.success)
    }

    @Test
    fun test_verify_empty_SpecConfig() {
        assert(testLocal().verifySpecConfig(SyspecConfig()).success)
    }

    @Test
    fun test_verify_socketSpec_successfully() {
        // given
        val out: List<String> =
            ("Netid  State   Recv-Q  Send-Q        Local Address:Port      Peer Address:Port  Process                                                                         \n" +
                    "udp    UNCONN  0       0                   0.0.0.0:5353           0.0.0.0:*      users:((\"avahi-daemon\",pid=906,fd=12)) uid:116 ino:25024 sk:3 <->              \n" +
                    "tcp    LISTEN  0       128                 0.0.0.0:22             0.0.0.0:*      users:((\"sshd\",pid=1018,fd=3)) ino:29320 sk:a <->                              \n").split(
                "\n"
            )

        // when
        val res = testLocal().task {
            verifySocketSpec(SocketSpec("sshd", 22), out)
            verifySocketSpec(SocketSpec("sshd", 23, running = false), out)
        }
        val res2 = testLocal().verifySocketSpec(SocketSpec("sshd", 23), out).success
        val res3 = testLocal().verifySocketSpec(SocketSpec("sshd", 22, running = false), out).success

        // then
        assertTrue(res.success)
        assertFalse(res2)
        assertFalse(res3)
    }

    @ContainerTest
    fun test_verifySpecConfig_succeeds() {
        // given
        val dir = "/home/testuser"
        val prov = defaultTestContainer()

        // when
        val res = prov.verifySpecConfig(SyspecConfig(folder = listOf(FolderSpec(dir)), command = listOf(CommandSpec("echo bla"))))

        // then
        assertTrue(res.success)
    }

    @ContainerTest
    fun test_verifySpecConfig_fails() {
        // given
        val dir = "/home/testuser"
        val prov = defaultTestContainer()

        // when
        val res = prov.verifySpecConfig(SyspecConfig(command = listOf(CommandSpec("echoo bla"), CommandSpec("echo bla")), folder = listOf(FolderSpec(dir))))

        // then
        assertFalse(res.success)
    }

}