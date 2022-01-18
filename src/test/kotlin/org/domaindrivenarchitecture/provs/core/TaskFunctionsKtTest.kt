package org.domaindrivenarchitecture.provs.core

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess
import org.domaindrivenarchitecture.provs.test.testLocal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TaskFunctionsKtTest {

    var count = 1
    fun Prov.altenateSuccessAndFailure() = task {
        if (count == 0) {
            count = 1
            ProvResult(true, out = "0")
        } else {
            count--
            ProvResult(false, err = "1")
        }
    }

    fun Prov.repeating() = requireLast {
        val res = repeatTaskUntilSuccess(4, 1) {
            altenateSuccessAndFailure()
        }

        if (res.success && ("0" == res.out?.trim())) {
            ProvResult(true)
        } else {
            ProvResult(false)
        }
    }


    @Test
    fun repeat_and_requireLast() {
        assertTrue(testLocal().repeating().success)
    }
}