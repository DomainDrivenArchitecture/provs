package org.domaindrivenarchitecture.provs.extensions.server_software.standalone_server.nexus.base

fun reverseProxyConfigHttpPort80(serverName: String): String {
    // see https://help.sonatype.com/repomanager3/installation/run-behind-a-reverse-proxy
    return """
    events {}         # event context have to be defined to consider config valid  
              
    http {
      
      proxy_send_timeout 120;
      proxy_read_timeout 300;
      proxy_buffering    off;
      keepalive_timeout  5 5;
      tcp_nodelay        on;
      
      server {
        listen   80;
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
          proxy_set_header X-Forwarded-Proto "http";
        }
      }
    }
    """.trimIndent()
}


fun reverseProxyConfigSsl(serverName: String, ssl_certificate: String? = null, ssl_certificate_key: String? = null): String {
    // see https://help.sonatype.com/repomanager3/installation/run-behind-a-reverse-proxy

    val sslCertificateEntry = ssl_certificate?.let { "ssl_certificate      $ssl_certificate;" } ?: "ssl_certificate      /etc/letsencrypt/live/$serverName/fullchain.pem;"
    val sslCertificateKeyEntry = ssl_certificate?.let { "ssl_certificate_key  $ssl_certificate_key;" } ?: "ssl_certificate_key  /etc/letsencrypt/live/$serverName/privkey.pem"

    return """
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
    # proxy_max_temp_file_size 2G;

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
}


