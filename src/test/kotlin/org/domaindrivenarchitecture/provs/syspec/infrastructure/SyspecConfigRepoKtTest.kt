package org.domaindrivenarchitecture.provs.syspec.infrastructure

import org.domaindrivenarchitecture.provs.configuration.domain.ConfigFileName
import org.domaindrivenarchitecture.provs.syspec.domain.CommandSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SyspecConfigRepoKtTest {

    @Test
    fun findSpecConfigFromFile_use_default_config_if_default_config_file_is_not_found() {
        // when
        val res = findSpecConfigFromFile(ConfigFileName("syspec-config.yaml"))

        // then
        assertEquals(
            "SyspecConfig(command=[CommandSpec(command=tfenv -h, out=null), CommandSpec(command=python3 --version, out=null), CommandSpec(command=pip3 --version, out=null), CommandSpec(command=terraform --version, out=1.0.8)], file=null, folder=null, host=null, package=[PackageSpec(name=firefox, installed=true), PackageSpec(name=thunderbird, installed=true), PackageSpec(name=ssh, installed=true), PackageSpec(name=git, installed=true), PackageSpec(name=leiningen, installed=true)], netcat=null, socket=null, certificate=null, s3=null)",
            res.getOrNull().toString())
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