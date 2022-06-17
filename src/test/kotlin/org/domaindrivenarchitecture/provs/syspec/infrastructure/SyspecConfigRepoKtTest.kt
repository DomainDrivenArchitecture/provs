package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.syspec.domain.CommandSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SyspecConfigRepoKtTest {

    @Test
    fun findSpecConfigFromFile_success() {
        // when
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")  // null would reveal test error
        val filePath = javaClass.classLoader.getResource("syspec-config.yaml").file
        val res = findSpecConfigFromFile(ConfigFileName(filePath))

        // then
        assertEquals(listOf(CommandSpec("echo just_for_test", "just_for_test")), res.getOrNull()?.command)
    }

    @Test
    fun findSpecConfigFromResource_success() {
        // when
        val res = findSpecConfigFromResource("syspec-config.yaml")

        // then
        assertEquals(listOf(CommandSpec("echo just_for_test", "just_for_test")), res.getOrNull()?.command)
    }

    @Test
    fun findSpecConfigFromFile_null() {
        // when
        val res = findSpecConfigFromFile(ConfigFileName("dontexist"))

        // then
        assertNull(res.getOrNull())
    }

    @Test
    fun findSpecConfigFromResource_null() {
        // when
        val res = findSpecConfigFromResource("dontexist")

        // then
        assertNull(res.getOrNull())
    }
}