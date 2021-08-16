package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.io.ByteArrayOutputStream
import java.io.PrintStream


internal class ProvTest {

    private fun Prov.def_returnungFalse() = def {
        ProvResult(false)
    }

    private fun Prov.def_returningTrue() = def {
        ProvResult(true)
    }


    @Test
    @EnabledOnOs(OS.LINUX)
    fun cmd_onLinux() {
        // when
        val res = Prov.newInstance(name = "testing").cmd("echo --testing--").success

        // then
        assert(res)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @ContainerTest
    fun sh_onLinux() {
        // given
        val script = """
            # test some script commands
        
            ping -c1 nu.nl
            echo something
            ping -c1 github.com
        """

        // when
        val res = Prov.newInstance(name = "testing").sh(script).success

        // then
        assert(res)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @ContainerTest
    @NonCi
    fun sh_onLinux_with_dir_and_sudo() {
        // given
        val script = """
            # test some script commands
        
            ping -c1 google.com
            echo something
            ping -c1 github.com
            echo 1 # comment behind command
        """

        // when
        val res = Prov.newInstance(name = "provs_test").sh(script, "/root", true).success

        // then
        assert(res)
    }


    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun cmd_onWindows() {
        // when
        val res = Prov.newInstance(name = "testing").cmd("echo --testing--").success

        // then
        assert(res)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun sh_onWindows() {
        // given
        val script = """
            # test some script commands
        
            ping -n 1 nu.nl
            echo something
            ping -n 1 github.com
        """

        // when
        val res = Prov.newInstance(name = "testing").sh(script).success

        // then
        assert(res)
    }

    @Test
    fun def_modeOptional_result_true() {
        // given
        fun Prov.tst_def() = optional {
            def_returnungFalse()
            def_returningTrue()
            def_returnungFalse()
        }

        // when
        val res = testLocal().tst_def().success

        // then
        assert(res)
    }

    @Test
    fun def_modeLast_result_true() {
        // given
        fun Prov.tst_def() = requireLast {
            def_returnungFalse()
            def_returningTrue()
        }

        // when
        val res = testLocal().tst_def().success

        // then
        assert(res)
    }

    @Test
    fun def_modeLast_result_false() {
        // given
        fun Prov.tst_def() = requireLast {
            def_returningTrue()
            def_returnungFalse()
        }

        // when
        val res = testLocal().tst_def().success

        // then
        assert(!res)
    }

    @Test
    fun def_mode_ALL_result_true() {
        // given
        fun Prov.tst_def_all_true_mode_ALL() = requireAll {
            def_returningTrue()
            def_returningTrue()
        }

        // when
        val res = testLocal().tst_def_all_true_mode_ALL().success

        // then
        assert(res)
    }

    // given
    fun Prov.tst_def_one_false_mode_ALL() = requireAll {
        def_returningTrue()
        def_returnungFalse()
        def_returningTrue()
    }

    @Test
    fun def_modeALL_resultFalse() {
        // when
        val res = testLocal().tst_def_one_false_mode_ALL().success

        // then
        assert(!res)
    }

    // given
    fun Prov.tst_def_one_false_mode_ALL_nested() = requireAll {
        def_returningTrue()
        tst_def_one_false_mode_ALL()
        def_returningTrue()
        tst_ALL_returningTrue()
    }

    // given
    fun Prov.tst_ALL_returningTrue() = requireAll {
        ProvResult(true)
    }

    @Test
    fun def_modeALLnested_resultFalse() {
        // when
        val res = testLocal().tst_def_one_false_mode_ALL_nested().success

        // then
        assert(!res)
    }

    @Test
    fun def_mode_ALL_LAST_NONE_nested() {
        // given
        fun Prov.tst_def_last() = def {
            def_returningTrue()
            def_returnungFalse()
        }

        fun Prov.tst_def_one_false_mode_ALL() = requireAll {
            tst_def_last()
            def_returningTrue()
        }

        // when
        val res = testLocal().tst_def_one_false_mode_ALL().success

        // then
        assert(!res)
    }

    @Test
    fun def_mode_FAILEXIT_nested_false() {
        // given
        fun Prov.tst_def_failexit_inner() = exitOnFailure {
            def_returningTrue()
            def_returnungFalse()
        }

        fun Prov.tst_def_failexit_outer() = exitOnFailure {
            tst_def_failexit_inner()
            def_returningTrue()
        }

        // when
        val res = testLocal().tst_def_failexit_outer().success

        // then
        assert(!res)
    }

    @Test
    fun def_mode_FAILEXIT_nested_true() {
        // given
        fun Prov.tst_def_failexit_inner() = exitOnFailure {
            def_returningTrue()
            def_returningTrue()
        }

        fun Prov.tst_def_failexit_outer() = exitOnFailure {
            tst_def_failexit_inner()
            def_returningTrue()
        }

        // when
        val res = testLocal().tst_def_failexit_outer().success

        // then
        assert(res)
    }

    @Test
    fun def_mode_multiple_nested() {
        // given
        fun Prov.tst_nested() = def {
            requireAll {
                def_returningTrue()
                def {
                    def_returnungFalse()
                    def_returningTrue()
                }
                def_returnungFalse()
                def_returningTrue()
                optional {
                    def_returnungFalse()
                }
            }
        }

        // when
        val res = testLocal().tst_nested().success

        // then
        assert(!res)
    }


    // given
    fun Prov.checkPrereq_evaluateToFailure() = requireLast {
        ProvResult(false, err = "This is a test error.")
    }

    fun Prov.methodThatProvidesSomeOutput() = requireLast {

        if (!checkPrereq_evaluateToFailure().success) {
            sh(
                """
                    echo -Start test-
                    echo Some output
                    """
            )
        }

        sh("echo -End test-")
    }

    @Test
    @NonCi
    fun runProv_printsCorrectOutput() {

        // given
        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        Prov.newInstance(name = "test instance", progressType = ProgressType.NONE).methodThatProvidesSomeOutput()

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput = if (OS.WINDOWS.isCurrentOs) "\n" +
                "============================================== SUMMARY (test Instance) ============================================== \n" +
                ">  Success -- methodThatProvidesSomeOutput (requireLast) \n" +
                "--->  FAILED -- checkPrereq_evaluateToFailure (requireLast)  -- Error: This is a test error.\n" +
                "--->  Success -- sh \n" +
                "------>  Success -- cmd [cmd.exe, /c, echo -Start test-]\n" +
                "------>  Success -- cmd [cmd.exe, /c, echo Some output]\n" +
                "--->  Success -- sh \n" +
                "------>  Success -- cmd [cmd.exe, /c, echo -End test-]\n" +
                "============================================ SUMMARY END ============================================ \n"
        else if (OS.LINUX.isCurrentOs()) {
                    "============================================== SUMMARY (test instance) ============================================== \n" +
                    ">  \u001B[92mSuccess\u001B[0m -- methodThatProvidesSomeOutput (requireLast) \n" +
                    "--->  \u001B[91mFAILED\u001B[0m -- checkPrereq_evaluateToFailure (requireLast)  -- Error: This is a test error.\n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- sh \n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo -Start test-]\n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo Some output]\n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- sh \n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo -End test-]\n" +
                    "----------------------------------------------------------------------------------------------------- \n" +
                    "Overall >  \u001B[92mSuccess\u001B[0m\n" +
                    "============================================ SUMMARY END ============================================ \n" +
                    "\n"
        } else {
            "OS " + System.getProperty("os.name") + " not yet supported"
        }

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))
    }

    @Test
    fun check_returnsTrue() {
        // when
        val res = testLocal().chk("echo 123")

        // then
        assertTrue(res)
    }

    @Test
    fun check_returnsFalse() {
        // when
        val res = testLocal().chk("cmddoesnotexist")

        // then
        assertFalse(res)
    }

    @Test
    fun getSecret_returnsSecret() {
        // when
        val res = testLocal().getSecret("echo 123")

        // then
        assertEquals("123", res?.plain()?.trim())
    }

    @Test
    fun addResultToEval_success() {
        // given
        fun Prov.inner() {
            addResultToEval(ProvResult(true))
        }

        fun Prov.outer() = requireAll {
            inner()
            ProvResult(true)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(true), res)
    }

    @Test
    fun addResultToEval_failure() {
        // given
        fun Prov.inner() {
            addResultToEval(ProvResult(false))
        }

        fun Prov.outer() = requireAll {
            inner()
            ProvResult(true)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(false), res)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @NonCi
    fun inContainer_locally() {
        // given
        val containerName = "provs_test"
        testLocal().provideContainer(containerName, "ubuntu")

        fun Prov.inner() = def {
            cmd("echo in container")
        }

        // then
        fun Prov.outer() = def {
            inContainer(containerName) {
                inner()
                cmd("echo testfile > testfile.txt")
            }
        }

        val res = testLocal().def { outer() }

        // then
        assertEquals(true, res.success)
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @Disabled // run manually after updating host and remoteUser
    fun inContainer_remotely() {
        // given
        val host = "192.168.56.135"
        val remoteUser = "az"

        fun Prov.inner() = def {
            cmd("echo 'in testfile' > testfile.txt")
        }

        // then
        val res = remote(host, remoteUser).def {
            inner()  // executed on the remote host
            inContainer("prov_default") {
                inner()  // executed in the container on the remote host
            }
        }

        // then
        assertEquals(true, res.success)
    }
}
