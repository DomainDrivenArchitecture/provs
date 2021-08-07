package org.domaindrivenarchitecture.provs.extensions.server_software.nginx.base

import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.configFile
import org.domaindrivenarchitecture.provs.extensions.server_software.nginx.provisionNginxStandAlone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.NonCi

internal class LocationsKtTest {

    @Test
    @NonCi
    fun nginxIncludeLocationFolders() {
        // given
        val a = defaultTestContainer()
        a.provisionNginxStandAlone()
        a.createFile(configFile, NGINX_MINIMAL_CONF, sudo = true)

        // when
        val res = a.nginxIncludeLocationFolders()

        // then
        assertTrue(res.success)
        assertTrue(a.fileContainsText(
            configFile, """listen 80;
                  include /etc/nginx/locations-enabled/port80*.conf
                  include /etc/nginx/locations-enabled/port443*.conf"""))
        // just 1 occurrence
        assertEquals("1", a.cmd("grep -o 'listen 80;' $configFile | wc -l").out?.trim())
    }
}