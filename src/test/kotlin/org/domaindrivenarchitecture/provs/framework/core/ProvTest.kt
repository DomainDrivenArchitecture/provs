package org.domaindrivenarchitecture.provs.framework.core

import ch.qos.logback.classic.Level
import org.domaindrivenarchitecture.provs.framework.core.docker.provideContainer
import org.domaindrivenarchitecture.provs.test.setRootLoggingLevel
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


internal class ProvTest {

    private fun Prov.task_returningFalse() = taskWithResult {
        ProvResult(false)
    }

    private fun Prov.task_returningTrue() = taskWithResult {
        ProvResult(true)
    }


    @Test
    fun cmd() {
        // when
        val res = Prov.newInstance(name = "testing").cmd("echo --testing--").success

        // then
        assertTrue(res)
    }

    @ContainerTest
    fun sh() {
        // given
        val script = """
            # test some script commands
        
            ping -c1 hetzner.com
            echo something
        """

        // when
        val res = Prov.newInstance(name = "testing").sh(script).success

        // then
        assertTrue(res)
    }

    @ContainerTest
    @NonCi
    fun sh_with_dir_and_sudo() {
        // given
        val script = """
            # test some script commands
        
            ping -c1 hetzner.com
            echo something
            echo 1 # comment behind command
        """

        // when
        val res = Prov.newInstance(name = "provs_test").sh(script, "/root", true).success

        // then
        assertTrue(res)
    }

    @Test
    fun task_modeOptional_result_true() {
        // given
        fun Prov.tst_task() = optional {
            task_returningFalse()
            task_returningTrue()
            task_returningFalse()
        }

        // when
        val res = testLocal().tst_task().success

        // then
        assertTrue(res)
    }

    @Test
    fun task_modeLast_result_true() {
        // given
        fun Prov.tst_task() = requireLast {
            task_returningFalse()
            task_returningTrue()
        }

        // when
        val res = testLocal().tst_task().success

        // then
        assertTrue(res)
    }

    @Test
    fun task_modeLast_result_false() {
        // given
        fun Prov.tst_task() = requireLast {
            task_returningTrue()
            task_returningFalse()
        }

        // when
        val res = testLocal().tst_task().success

        // then
        assertFalse(res)
    }

    @Test
    fun task_mode_ALL_result_true() {
        // given
        fun Prov.tst_task_all_true_mode_ALL() = task {
            task_returningTrue()
            task_returningTrue()
        }

        // when
        val res = testLocal().tst_task_all_true_mode_ALL().success

        // then
        assertTrue(res)
    }

    // given
    fun Prov.tst_task_one_false_mode_ALL() = task {
        task_returningTrue()
        task_returningFalse()
        task_returningTrue()
    }

    @Test
    fun task_modeALL_resultFalse() {
        // when
        val res = testLocal().tst_task_one_false_mode_ALL().success

        // then
        assertFalse(res)
    }

    // given
    fun Prov.tst_task_one_false_mode_ALL_nested() = task {
        task_returningTrue()
        tst_task_one_false_mode_ALL()
        task_returningTrue()
        tst_ALL_returningTrue()
    }

    // given
    fun Prov.tst_ALL_returningTrue() = task {
        ProvResult(true)
    }

    @Test
    fun task_modeALLnested_resultFalse() {
        // when
        val res = testLocal().tst_task_one_false_mode_ALL_nested().success

        // then
        assertFalse(res)
    }

    @Test
    fun task_mode_ALL_LAST_NONE_nested() {
        // given
        fun Prov.tst_task_last() = task {
            task_returningTrue()
            task_returningFalse()
        }

        fun Prov.tst_task_one_false_mode_ALL() = task {
            tst_task_last()
            task_returningTrue()
        }

        // when
        val res = testLocal().tst_task_one_false_mode_ALL().success

        // then
        assertFalse(res)
    }

    @Test
    fun task_mode_FAILEXIT_nested_false() {
        // given
        fun Prov.tst_task_failexit_inner() = exitOnFailure {
            task_returningTrue()
            task_returningFalse()
        }

        fun Prov.tst_task_failexit_outer() = exitOnFailure {
            tst_task_failexit_inner()
            task_returningTrue()
        }

        // when
        val res = testLocal().tst_task_failexit_outer().success

        // then
        assertFalse(res)
    }

    @Test
    fun task_mode_FAILEXIT_nested_true() {
        // given
        fun Prov.tst_task_failexit_inner() = exitOnFailure {
            task_returningTrue()
            task_returningTrue()
        }

        fun Prov.tst_task_failexit_outer() = exitOnFailure {
            tst_task_failexit_inner()
            task_returningTrue()
        }

        // when
        val res = testLocal().tst_task_failexit_outer().success

        // then
        assertTrue(res)
    }

    @Test
    fun task_mode_multiple_nested() {
        // given
        fun Prov.tst_nested() = task {
            task {
                task_returningTrue()
                task {
                    task_returningFalse()
                    task_returningTrue()
                }
                task_returningFalse()
                task_returningTrue()
                optional {
                    task_returningFalse()
                }
            }
        }

        // when
        val res = testLocal().tst_nested().success

        // then
        assertFalse(res)
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
    fun prov_prints_correct_output_for_overall_success() {

        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        Prov.newInstance(name = "test instance with no progress info", progressType = ProgressType.NONE)
            .methodThatProvidesSomeOutput()

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput =
            "============================================== SUMMARY (test instance with no progress info) =============================================\n" +
                    ">  \u001B[92mSuccess\u001B[0m -- methodThatProvidesSomeOutput (requireLast) \n" +
                    "--->  \u001B[93mFAILED\u001B[0m  -- checkPrereq_evaluateToFailure (requireLast)  -- Error: This is a test error.\n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- sh \n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo -Start test-]\n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo Some output]\n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- sh \n" +
                    "------>  \u001B[92mSuccess\u001B[0m -- cmd [/bin/bash, -c, echo -End test-]\n" +
                    "----------------------------------------------------------------------------------------------------\n" +
                    "Overall >  \u001B[92mSuccess\u001B[0m\n" +
                    "============================================ SUMMARY END ===========================================\n" +
                    "\n"

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))
    }

    @Test
    @NonCi
    fun prov_prints_correct_output_for_failure() {

        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        Prov.newInstance(name = "test instance with no progress info", progressType = ProgressType.NONE)
            .checkPrereq_evaluateToFailure()

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput =
            "============================================== SUMMARY (test instance with no progress info) =============================================\n" +
                    ">  \u001B[91mFAILED\u001B[0m  -- checkPrereq_evaluateToFailure (requireLast)  -- Error: This is a test error.\n" +
                    "============================================ SUMMARY END ===========================================\n" +
                    "\n"

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))
    }

    @Test
    @NonCi
    fun prov_marks_failed_output_yellow_if_optional() {

        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        Prov.newInstance(name = "test instance with no progress info", progressType = ProgressType.NONE).task("taskA") {
            optional {
                taskWithResult("taskB") {
                    taskWithResult("taskC") {
                        ProvResult(false)
                    }
                }
            }
        }

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput =
            "============================================== SUMMARY (test instance with no progress info) =============================================\n" +
                    ">  \u001B[92mSuccess\u001B[0m -- taskA \n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- prov_marks_failed_output_yellow_if_optional (optional) \n" +
                    "------>  \u001B[93mFAILED\u001B[0m  -- taskB \n" +
                    "--------->  \u001B[93mFAILED\u001B[0m  -- taskC \n" +
                    "----------------------------------------------------------------------------------------------------\n" +
                    "Overall >  \u001B[92mSuccess\u001B[0m\n" +
                    "============================================ SUMMARY END ===========================================\n" +
                    "\n"

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))
    }

    @Test
    fun chk_returnsTrue() {
        // when
        val res = testLocal().chk("echo 123")

        // then
        assertTrue(res)
    }

    @Test
    fun chk_returnsFalse() {
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
    @NonCi
    fun custom_task_name_appears_in_results() {
        // given
        fun Prov.taskA() = task("TaskB") {
            task("taskC") {
                ProvResult(true)
            }
        }

        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        // when
        Prov.newInstance(name = "test instance", progressType = ProgressType.NONE).taskA()

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput =
            "============================================== SUMMARY (test instance) =============================================\n" +
                    ">  \u001B[92mSuccess\u001B[0m -- TaskB \n" +
                    "--->  \u001B[92mSuccess\u001B[0m -- taskC \n" +
                    "----------------------------------------------------------------------------------------------------\n" +
                    "Overall >  \u001B[92mSuccess\u001B[0m\n" +
                    "============================================ SUMMARY END ===========================================\n" +
                    "\n"

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))
    }

    @Test
    fun addResultToEval_success() {
        // given
        fun Prov.inner() {
            addResultToEval(ProvResult(true))
        }

        fun Prov.outer() = task {
            inner()
            ProvResult(true)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(true), res)
    }

    @Test
    fun task_with_subtask_and_failed_result_fails() {
        // given
        fun Prov.inner() {
            addResultToEval(ProvResult(true))
        }

        fun Prov.outer() = taskWithResult {
            inner()
            ProvResult(false)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(false), res)
    }

    @Test
    fun task_with_failing_subtask_and_successful_result_fails() {
        // given
        fun Prov.inner() = taskWithResult {
            ProvResult(false)
        }

        fun Prov.outer() = taskWithResult {
            inner()
            ProvResult(true)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(false), res)
    }

    @Test
    fun addResultToEval_failure() {
        // given
        fun Prov.inner() {
            addResultToEval(ProvResult(false))
        }

        fun Prov.outer() = taskWithResult {
            inner()
            ProvResult(true)
        }

        // when
        val res = testLocal().outer()

        //then
        assertEquals(ProvResult(false), res)
    }

    @Test
    @ContainerTest
    @NonCi
    fun inContainer_locally() {
        // given
        val containerName = "provs_test"
        testLocal().provideContainer(containerName, "ubuntu")

        fun Prov.inner() = task {
            cmd("echo in container")
        }

        // then
        fun Prov.outer() = task {
            taskInContainer(containerName) {
                inner()
                cmd("echo testfile > testfile.txt")
            }
        }

        val res = testLocal().task { outer() }

        // then
        assertEquals(true, res.success)
    }

    @Test
    @Disabled // run manually after updating host and remoteUser
    fun inContainer_remotely() {
        // given
        val host = "192.168.56.135"
        val remoteUser = "az"

        fun Prov.inner() = task {
            cmd("echo 'in testfile' > testfile.txt")
        }

        // then
        val res = remote(host, remoteUser).task {
            inner()  // executed on the remote host
            taskInContainer("prov_default") {
                inner()  // executed in the container on the remote host
            }
        }

        // then
        assertEquals(true, res.success)
    }

    @Test
    fun infoText_is_printed_correctly() {
        // given
        setRootLoggingLevel(Level.OFF)

        val outContent = ByteArrayOutputStream()
        val errContent = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))

        val prov = Prov.newInstance(name = "test instance with no progress info", progressType = ProgressType.NONE)

            // when
        prov.task {
            addInfoText("Text1")
            addInfoText("Text2\nwith newline")
        }

        // then
        System.setOut(originalOut)
        System.setErr(originalErr)

        println(outContent.toString())

        val expectedOutput =
            "============================================== SUMMARY (test instance with no progress info) =============================================\n" +
                    ">  \u001B[92mSuccess\u001B[0m -- infoText_is_printed_correctly \n" +
                    "+++++++++++++++++++++++++++++++++++  \u001B[94mAdditional information\u001B[0m  +++++++++++++++++++++++++++++++++++++++\n" +
                    "Text1\n" +
                    "Text2\n" +
                    "with newline\n" +
                    "============================================ SUMMARY END ===========================================\n" +
                    "\n"

        assertEquals(expectedOutput, outContent.toString().replace("\r", ""))

    }

}

