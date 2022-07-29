package org.domaindrivenarchitecture.provs.desktop.domain

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DesktopConfigTest {

    @Test
    fun equals_should_work_for_value_objects() {
        val config1 = DesktopConfig(gitEmail = "dummy")
        assertNotEquals(config1, DesktopConfig());
        assertEquals(config1, DesktopConfig(gitEmail = "dummy"));
    }
}