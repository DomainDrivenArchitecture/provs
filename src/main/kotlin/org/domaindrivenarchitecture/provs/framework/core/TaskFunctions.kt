package org.domaindrivenarchitecture.provs.framework.core


/**
 * Repeats task until it returns success
 */
fun Prov.repeatTaskUntilSuccess(times: Int, sleepInSec: Int, task: Prov.() -> ProvResult) = requireLast {
    require(times > 0)
    var result = ProvResult(false, err = "Internal error")  // Will only be returned if function is not executed at all, otherwise func's last result is returned
    for (i in 1..times) {
        result = task()
        if (result.success)
            return@requireLast result
        Thread.sleep(sleepInSec * 1000L)
    }
    result
}
