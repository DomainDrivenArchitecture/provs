package io.provs.platformTest

import io.provs.getCallingMethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

@EnabledOnOs(OS.LINUX)
internal class UbuntuProvUnitTest {

//    @Test
//    fun that_cond_executes_true_case() {
//        // given
//        val x = mockk<LocalProcessor>()
//        every { x.x(*anyVararg()) } returns ProcessResult(0)
//
//        val a = Prov.newInstance(x,"Linux")
//
//        // when
//        a.cond( { true }, { xec("doit") })
//        a.cond( { false }, { xec("dont") })
//
//        // then
//        verify { x.x("doit") }
//        verify(exactly = 0) { x.x("dont") }
//    }

    @Test
    fun that_callingStack_works() {

        // when
        val s = getCallingMethodName()

        // then
        assert(s == "that_callingStack_works")
    }
}