package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.syspec.domain.SocketSpec
import org.domaindrivenarchitecture.provs.syspec.domain.SpecConfig
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Test

internal class VerificationKtTest {

    @Test
    fun test_verify_empty_SpecConfig() {
        assert(testLocal().verifySpecConfig(SpecConfig()).success)
    }

    @Test
    fun test_verify_socketSpec_successfully() {
        // given
        val out: List<String> = ("Netid  State   Recv-Q  Send-Q        Local Address:Port      Peer Address:Port  Process                                                                         \n" +
                "udp    UNCONN  0       0                   0.0.0.0:5353           0.0.0.0:*      users:((\"avahi-daemon\",pid=906,fd=12)) uid:116 ino:25024 sk:3 <->              \n" +
                "tcp    LISTEN  0       128                 0.0.0.0:22             0.0.0.0:*      users:((\"sshd\",pid=1018,fd=3)) ino:29320 sk:a <->                              \n").split("\n")

        // when
        val res = testLocal().task {
            verifySocketSpec(SocketSpec("sshd", 22), out)
            verifySocketSpec(SocketSpec("sshd", 23, running = false), out)
        }
        val res2 = testLocal().verifySocketSpec(SocketSpec("sshd", 23), out).success
        val res3 = testLocal().verifySocketSpec(SocketSpec("sshd", 22, running = false), out).success

        // then
        assert(res.success)
        assert(!res2)
        assert(!res3)
    }

}