package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.base

import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.fileContainsText
import org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.NGINX_CONFIG_FILE
import org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.provisionNginxStandAlone
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.junit.jupiter.api.Disabled

internal class LocationsKtTest {

    @Test
    @ContainerTest
    @NonCi
    @Disabled   // todo: fix test
    fun nginxIncludeLocationFolders() {
        // given
        val a = defaultTestContainer()
        a.provisionNginxStandAlone()
        a.createFile(NGINX_CONFIG_FILE, NGINX_MINIMAL_CONF, sudo = true)

        // when
        val res = a.nginxIncludeLocationFolders()

        // then
        assertTrue(res.success)
        assertTrue(a.fileContainsText(
            NGINX_CONFIG_FILE, """listen 80;
                  include /etc/nginx/locations-enabled/port80*.conf
                  include /etc/nginx/locations-enabled/port443*.conf"""))
        // just 1 occurrence
        assertEquals("1", a.cmd("grep -o 'listen 80;' $NGINX_CONFIG_FILE | wc -l").out?.trim())
    }
}