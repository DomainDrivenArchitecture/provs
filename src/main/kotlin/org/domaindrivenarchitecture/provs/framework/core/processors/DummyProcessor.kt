package org.domaindrivenarchitecture.provs.framework.core.processors


class DummyProcessor : Processor {

    override fun exec(vararg args: String): ProcessResult
    {
       return ProcessResult(0, args = args)
    }

    override fun execNoLog(vararg args: String): ProcessResult
    {
        return ProcessResult(0, args = args)
    }
}