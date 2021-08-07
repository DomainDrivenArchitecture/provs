package io.provs.ubuntu.extensions.server_software.nginx.base

class NginxConf(val conf: String = NGINX_MINIMAL_CONF) {
    companion object {}
}


const val NGINX_MINIMAL_CONF = """
events {}

http {
    server {
        listen 80;
        
        location / {
            return 200 'Hi from nginx!';
        }
    }
}
"""


@Suppress("unused") // use later
fun NginxConf.Companion.nginxHttpConf(
    serverName: String = "localhost"
): NginxConf {
    return NginxConf(
        """
events {}

http {
    server {
        listen 80;
        server_name $serverName;

        include /etc/nginx/locations-enabled/port80*$locationsFileExtension;
    }
}
"""
    )
}


fun NginxConf.Companion.nginxHttpsConfWithLocationFiles(
    sslCertificate: String = "/etc/nginx/ssl/cert/selfsigned.crt",
    sslCertificateKey: String = "/etc/nginx/ssl/private/selfsigned.key"
): NginxConf {
    return NginxConf(
        """
events {}

http {
    server {
        listen 443 ssl;
        server_name localhost;

        ssl_certificate  $sslCertificate;
        ssl_certificate_key  $sslCertificateKey;

        include /etc/nginx/locations-enabled/port443*$locationsFileExtension;
    }
}
"""
    )
}


@Suppress("unused") // use later
fun NginxConf.Companion.nginxReverseProxySslConfig(
    serverName: String,
    ssl_certificate: String? = null,
    ssl_certificate_key: String? = null
): NginxConf {
    // see https://help.sonatype.com/repomanager3/installation/run-behind-a-reverse-proxy

    val sslCertificateEntry = ssl_certificate?.let { "ssl_certificate      $ssl_certificate;" }
        ?: "ssl_certificate      /etc/letsencrypt/live/$serverName/fullchain.pem;"
    val sslCertificateKeyEntry = ssl_certificate?.let { "ssl_certificate_key  $ssl_certificate_key;" }
        ?: "ssl_certificate_key  /etc/letsencrypt/live/$serverName/privkey.pem"

    return NginxConf(
        """
events {}         # event context have to be defined to consider config valid

http {

  proxy_send_timeout 120;
  proxy_read_timeout 300;
  proxy_buffering    off;
  keepalive_timeout  5 5;
  tcp_nodelay        on;

  server {
    listen   *:443 ssl;
    server_name  $serverName;

    # allow large uploads of files
    client_max_body_size 1G;

    # optimize downloading files larger than 1G
    #proxy_max_temp_file_size 2G;

    $sslCertificateEntry
    $sslCertificateKeyEntry

    location / {
      # Use IPv4 upstream address instead of DNS name to avoid attempts by nginx to use IPv6 DNS lookup
      proxy_pass http://127.0.0.1:8081/;
      proxy_set_header Host ${'$'}host;
      proxy_set_header X-Real-IP ${'$'}remote_addr;
      proxy_set_header X-Forwarded-For ${'$'}proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto "https";
    }
  }
}
    """
    )
}


@Suppress("unused") // use later
fun NginxConf.Companion.nginxReverseProxyHttpConfig(
    serverName: String
): NginxConf {
    // see https://help.sonatype.com/repomanager3/installation/run-behind-a-reverse-proxy

    return NginxConf(
        """
events {}         # event context have to be defined to consider config valid

http {

  proxy_send_timeout 120;
  proxy_read_timeout 300;
  proxy_buffering    off;
  keepalive_timeout  5 5;
  tcp_nodelay        on;

  server {
    listen   *:80;
    server_name  $serverName;

    # allow large uploads of files
    client_max_body_size 1G;

    # optimize downloading files larger than 1G
    #proxy_max_temp_file_size 2G;

    location / {
      # Use IPv4 upstream address instead of DNS name to avoid attempts by nginx to use IPv6 DNS lookup
      proxy_pass http://127.0.0.1:8081/;
      proxy_set_header Host ${'$'}host;
      proxy_set_header X-Real-IP ${'$'}remote_addr;
      proxy_set_header X-Forwarded-For ${'$'}proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto "https";
    }
  }
}
    """
    )
}

