package org.domaindrivenarchitecture.provs.framework.core.processors


class DummyProcessor : Processor {

    override fun x(vararg args: String): ProcessResult
    {
       return ProcessResult(0, args = args)
    }

    override fun xNoLog(vararg args: String): ProcessResult
    {
        return ProcessResult(0, args = args)
    }
}