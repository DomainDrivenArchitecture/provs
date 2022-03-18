package org.domaindrivenarchitecture.provs.framework.extensions.server_software.standalone_server.nginx.base

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDirs


internal val certificateName = "selfsigned"
internal val sslDays = 365
val dirSslCert="/etc/nginx/ssl/cert"
val dirSslKey="/etc/nginx/ssl/private"


fun Prov.nginxCreateSelfSignedCertificate(
    country: String = "DE",
    state: String = "test",
    locality: String = "test",
    organization: String = "test",
    organizationalUnit: String = "test",
    commonName: String = "test",
    email : String = "test@test.net"
) = task {
    // inspired by https://gist.github.com/adrianorsouza/2bbfe5e197ce1c0b97c8
    createDirs(dirSslCert, sudo = true)
    createDirs(dirSslKey, sudo = true)
    cmd("cd $dirSslKey && sudo openssl req -x509 -nodes -newkey rsa:2048 -keyout $certificateName.key -out $certificateName.crt -days $sslDays -subj \"/C=$country/ST=$state/L=$locality/O=$organization/OU=$organizationalUnit/CN=$commonName/emailAddress=$email\"")
    cmd("sudo mv $dirSslKey/$certificateName.crt $dirSslCert/")
}
