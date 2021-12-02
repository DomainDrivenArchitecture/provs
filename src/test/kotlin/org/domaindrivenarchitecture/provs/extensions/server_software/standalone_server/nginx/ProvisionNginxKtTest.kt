package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx

import org.domaindrivenarchitecture.provs.test.defaultTestContainer
import org.domaindrivenarchitecture.provs.test.tags.NonCi
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.replaceTextInFile
import org.domaindrivenarchitecture.provs.ubuntu.install.base.aptInstall
import org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nginx.base.*
import org.domaindrivenarchitecture.provs.test.tags.ContainerTest
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


internal class ProvisionNginxKtTest {

    @Test
    @ContainerTest
    @NonCi
    fun provisionNginxStandAlone_customConfig() {
        // given
        val a = defaultTestContainer()
        val config = """
            events {}         # event context have to be defined to consider config valid  
              
            http {  
             server {  
                listen 80;  
                server_name localhost;  
              
                return 200 "Hello";  
              }  
            }  
        """.trimIndent()
        a.aptInstall("curl")

        // when
        val res = a.requireAll {
            provisionNginxStandAlone(NginxConf(config))
            cmd("curl localhost")
        }

        // then
        assertTrue(res.success)
    }

    @Test
    @ContainerTest
    @NonCi
    fun provisionNginxStandAlone_defaultConfig() {
        // given
        val a = defaultTestContainer()

        // when
        val res = a.requireAll {
            provisionNginxStandAlone()
        }

        // then
        assertTrue(res.success)
    }

    @Test
    @ContainerTest
    @NonCi
    fun provisionNginxStandAlone_sslConfig() {
        // given
        val a = defaultTestContainer()
        a.def {
            val file = "/etc/ssl/openssl.cnf"
            if (fileExists(file)) {
                replaceTextInFile(file, "RANDFILE", "#RANDFILE")
            }
            aptInstall("openssl")
        }

        // when
        val res = a.def {
            nginxCreateSelfSignedCertificate()

            provisionNginxStandAlone(
                NginxConf.nginxReverseProxySslConfig(
                    "localhost",
                    dirSslCert + "/" + certificateName + ".crt",
                    dirSslKey + "/" + certificateName + ".key"
                )
            )
        }

        // then
        assertTrue(res.success)
    }
}

