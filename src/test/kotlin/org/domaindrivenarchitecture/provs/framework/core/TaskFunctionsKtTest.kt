package org.domaindrivenarchitecture.provs.framework.core

import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TaskFunctionsKtTest {

    var count1 = 2
    fun Prov.alternatingSuccessAndFailure() = task {
        if (count1 == 1) {
            count1 = 2
            ProvResult(true, out = "1")
        } else {
            count1--
            ProvResult(false, err = count1.toString())
        }
    }

    fun Prov.secondTimeSuccess() = task {
        val res = repeatTaskUntilSuccess(3, 1) {
            alternatingSuccessAndFailure()
        }
        if (res.success && ("1" == res.out?.trim())) {
            ProvResult(true)
        } else {
            ProvResult(false)
        }
    }

    var count2 = 3
    fun Prov.thirdTimeSuccess() = task {
        if (count2 <= 1) {
            count2 = 3
            ProvResult(true, out = "1")
        } else {
            count2--
            ProvResult(false, err = count2.toString())
        }
    }

    fun thirdTimeSuccessForNotAProvTaks(): ProvResult {
        if (count2 <= 1) {
            count2 = 3
            return ProvResult(true, out = "1")
        } else {
            count2--
            return ProvResult(false, err = count2.toString())
        }
    }


    @Test
    fun repeat_and_requireLast() {
        // when
        val res1 = testLocal().secondTimeSuccess()
        val res2 = testLocal().repeatTaskUntilSuccess(3, 0) { thirdTimeSuccess() }
        val res3 = testLocal().repeatTaskUntilSuccess(2, 0) { thirdTimeSuccess() }
        val res4 = testLocal().repeatTaskUntilSuccess(3, 0) { thirdTimeSuccessForNotAProvTaks() }

        // then
        assertTrue(res1.success)
        assertTrue(res2.success)
        assertFalse(res3.success)
        assertTrue(res4.success)
    }
}