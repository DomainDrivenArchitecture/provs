package org.domaindrivenarchitecture.provs.core.processors


@Suppress("unused")  // used externally
class PrintOnlyProcessor : Processor {

    override fun x(vararg args: String): ProcessResult
    {
        print("PrintOnlyProcessor >>> ")
        for (n in args) print("\"$n\" ")
        println()
        return ProcessResult(0, args = args)
    }

    override fun xNoLog(vararg args: String): ProcessResult
    {
        print("PrintOnlyProcessor >>> ********")
        return ProcessResult(0, args = args)
    }
}