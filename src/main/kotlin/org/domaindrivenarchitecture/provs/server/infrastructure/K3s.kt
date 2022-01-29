package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.ProvResult
import org.domaindrivenarchitecture.provs.framework.core.repeatTaskUntilSuccess
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.*

private const val k3sResourcePath = "org/domaindrivenarchitecture/provs/infrastructure/k3s/"
private const val k3sManifestsDir = "/etc/rancher/k3s/manifests/"
private const val k3sConfigFile = "/etc/rancher/k3s/config.yaml"
private const val k3sAppleFile = k3sManifestsDir + "apple.yaml"
private const val certManagerDeployment = k3sManifestsDir + "certmanager.yaml"
private const val certManagerIssuer = k3sManifestsDir + "issuer.yaml"

private const val k3sInstallFile = "/usr/local/bin/k3s-install.sh"

enum class CertManagerEndPoint {
    STAGING, PROD
}

fun Prov.testConfigExists(): Boolean {
    return fileExists(k3sConfigFile)
}

fun Prov.deprovisionK3sInfra() = task {
    deleteFile(k3sInstallFile, sudo = true)
    deleteFile(k3sAppleFile, sudo = true)
    deleteFile(certManagerDeployment, sudo = true)
    deleteFile(certManagerIssuer, sudo = true)
    cmd("k3s-uninstall.sh")
}

/**
 * Installs a k3s server.
 * If docker is true, then docker will be installed (may conflict if docker is already existing) and k3s will be installed with docker option.
 * If tlsHost is specified, then tls (if configured) also applies to the specified host.
 */
fun Prov.provisionK3sInfra(tlsName: String, nodeIpv4: String, loopbackIpv4: String, loopbackIpv6: String,
                           nodeIpv6: String? = null, tlsHost: String? = null) = task {
    val isDualStack = nodeIpv6?.isNotEmpty() ?: false
    if (!testConfigExists()) {
        createDirs(k3sManifestsDir, sudo = true)
        var k3sConfigFileName = "config.yaml.template"
        var k3sConfigMap: Map<String, String> = mapOf("loopback_ipv4" to loopbackIpv4, "loopback_ipv6" to loopbackIpv6,
            "node_ipv4" to nodeIpv4, "tls_name" to tlsName)
        if (isDualStack) {
            k3sConfigFileName += ".dual"
            k3sConfigMap = k3sConfigMap.plus("node_ipv6" to nodeIpv6!!)
        } else {
            k3sConfigFileName += ".ipv4"
        }
        createFileFromResourceTemplate(
            k3sConfigFile,
            k3sConfigFileName,
            k3sResourcePath,
            k3sConfigMap,
            "644",
            sudo = true
        )
        createFileFromResource(
            k3sInstallFile,
            "k3s-install.sh",
            k3sResourcePath,
            "755",
            sudo = true
        )
        cmd("k3s-install.sh")
    } else {
        ProvResult(true)
    }
}


fun Prov.provisionK3sCertManager(endpoint: CertManagerEndPoint) = task {
    createFileFromResource(
        certManagerDeployment,
        "cert-manager.yaml",
        k3sResourcePath,
        "644",
        sudo = true
    )
    createFileFromResourceTemplate(
        certManagerIssuer,
        "le-issuer.template.yaml",
        k3sResourcePath,
        mapOf("endpoint" to endpoint.name.lowercase()),
        "644",
        sudo = true
    )
    cmd("kubectl apply -f $certManagerDeployment", sudo = true)

    repeatTaskUntilSuccess(10, 10) {
        cmd("kubectl apply -f $certManagerIssuer", sudo = true)
    }
}

fun Prov.provisionK3sApple(fqdn: String, endpoint: CertManagerEndPoint) = task {
    createFileFromResourceTemplate(
        k3sAppleFile,
        "apple.template.yaml",
        k3sResourcePath,
        mapOf("fqdn" to fqdn, "issuer_name" to endpoint.name.lowercase()),
        "644",
        sudo = true
    )
    cmd("kubectl apply -f $k3sAppleFile", sudo = true)

    repeatTaskUntilSuccess(10, 10) {
        cmd("kubectl apply -f $certManagerIssuer", sudo = true)
    }
}
