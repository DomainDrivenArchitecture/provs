package io.provs.ubuntu.extensions.server_software.firewall

import io.provs.Prov
import io.provs.ProvResult
import io.provs.ubuntu.install.base.aptInstall


fun Prov.saveIpTables() = requireAll {
    sh("""
        iptables-save > /etc/iptables/rules.v4
        ip6tables-save > /etc/iptables/rules.v6

        netfilter-persistent save""",
        sudo = true)
}


fun Prov.makeIpTablesPersistent() = requireAll {
    // inspired by https://gist.github.com/alonisser/a2c19f5362c2091ac1e7
    // enables iptables-persistent to be installed without manual input
    sh("""
        echo iptables-persistent iptables-persistent/autosave_v4 boolean true | sudo debconf-set-selections
        echo iptables-persistent iptables-persistent/autosave_v6 boolean true | sudo debconf-set-selections
    """.trimIndent())

    aptInstall("iptables-persistent netfilter-persistent")
    saveIpTables()
}


fun Prov.resetFirewall() = requireAll {
    sh("""
        #!/bin/bash
        sudo iptables -F
        sudo iptables -X
        sudo iptables -t nat -F
        sudo iptables -t nat -X
        sudo iptables -t mangle -F
        sudo iptables -t mangle -X

        # the rules allow us to reconnect by opening up all traffic.
        sudo iptables -P INPUT ACCEPT
        sudo iptables -P FORWARD ACCEPT
        sudo iptables -P OUTPUT ACCEPT

        # print out all rules to the console after running this file.
        sudo iptables -nL
    """, sudo = true
    )
}


fun Prov.provisionFirewall(addNetworkProtections: Boolean = false) = requireAll {
    if (addNetworkProtections) {
        networkProtections()
    }

    // inspired by: https://github.com/ChrisTitusTech/firewallsetup/blob/master/firewall
    sh("""
        # Firewall
        
        # Accept all traffic first to avoid ssh lockdown via iptables firewall rules #
        iptables -P INPUT ACCEPT
        iptables -P FORWARD ACCEPT
        iptables -P OUTPUT ACCEPT
        
        # Flush all chains
        iptables --flush
         
        # Allow unlimited traffic on the loopback interface
        iptables -A INPUT -i lo -j ACCEPT
        iptables -A OUTPUT -o lo -j ACCEPT
         
        # Previously initiated and accepted exchanges bypass rule checking
        # Allow unlimited outbound traffic
        iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
        iptables -A OUTPUT -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT
         
        #Ratelimit SSH for attack protection
        iptables -A INPUT -p tcp --dport 22 -m state --state NEW -m recent --update --seconds 60 --hitcount 4 -j DROP
        iptables -A INPUT -p tcp --dport 22 -m state --state NEW -m recent --set
        iptables -A INPUT -p tcp --dport 22 -m state --state NEW -j ACCEPT
         
        # Allow http/https ports to be accessible from the outside
        iptables -A INPUT -p tcp --dport 80 -m state --state NEW -j ACCEPT    # http
        iptables -A INPUT -p tcp --dport 443 -m state --state NEW -j ACCEPT   # https

        # UDP packet rule.  This is just a random udp packet rule as an example only
        # iptables -A INPUT -p udp --dport 5021 -m state --state NEW -j ACCEPT

        # Allow pinging of your server
        iptables -A INPUT -p icmp --icmp-type 8 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT

          
        # Drop all other traffic
        iptables -A INPUT -j DROP

        # print the activated rules to the console when script is completed
        iptables -nL

        # Set default policies
        iptables --policy INPUT DROP
        iptables --policy OUTPUT DROP
        iptables --policy FORWARD DROP
    """, sudo = true)
    if (chk("docker -v")) {
        ipTablesRecreateDockerRules()
    } else {
        ProvResult(true, "No need to create iptables docker rules as no docker installed.")
    }
}


fun Prov.networkProtections() = def {
    sh("""
        # Drop ICMP echo-request messages sent to broadcast or multicast addresses
        echo 1 > /proc/sys/net/ipv4/icmp_echo_ignore_broadcasts

        # Drop source routed packets
        echo 0 > /proc/sys/net/ipv4/conf/all/accept_source_route
         
        # Enable TCP SYN cookie protection from SYN floods
        echo 1 > /proc/sys/net/ipv4/tcp_syncookies
         
        # Don't accept ICMP redirect messages
        echo 0 > /proc/sys/net/ipv4/conf/all/accept_redirects
         
        # Don't send ICMP redirect messages
        echo 0 > /proc/sys/net/ipv4/conf/all/send_redirects
         
        # Enable source address spoofing protection
        echo 1 > /proc/sys/net/ipv4/conf/all/rp_filter
         
        # Log packets with impossible source addresses
        echo 1 > /proc/sys/net/ipv4/conf/all/log_martians
    """.trimIndent())
}


fun Prov.ipTablesRecreateDockerRules() = requireAll {
    // see https://stackoverflow.com/questions/25917941/docker-how-to-re-create-dockers-additional-iptables-rules
    cmd("sudo service docker restart")
}