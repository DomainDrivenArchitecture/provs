package org.domaindrivenarchitecture.provs.framework.core.processors


@Suppress("unused")  // used externally
class PrintOnlyProcessor : Processor {

    override fun exec(vararg args: String): ProcessResult
    {
        print("PrintOnlyProcessor >>> ")
        for (n in args) print("\"$n\" ")
        println()
        return ProcessResult(0, args = args)
    }

    override fun execNoLog(vararg args: String): ProcessResult
    {
        print("PrintOnlyProcessor >>> ********")
        return ProcessResult(0, args = args)
    }
}