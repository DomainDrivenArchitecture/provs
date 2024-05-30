package org.domaindrivenarchitecture.provs.server.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResource
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFileFromResourceTemplate
import org.domaindrivenarchitecture.provs.server.domain.k3s.FileMode
import java.io.File

private const val hetznerCSIResourceDir = "org/domaindrivenarchitecture/provs/server/infrastructure/hetznerCSI/"
fun Prov.provisionHetznerCSIForK8s(hetznerApiToken: Secret, encryptionPassphrase: Secret) {
    // CSI Driver
    createFileFromResourceTemplate(
        k3sManualManifestsDir + "hcloud-api-token-secret.yaml",
        "hcloud-api-token-secret.template.yaml",
        resourcePath = hetznerCSIResourceDir,
        posixFilePermission = "644",
        values = mapOf(
            "HETZNER_API_TOKEN" to hetznerApiToken.plain()
        ))
    cmd("kubectl apply -f hcloud-api-token-secret.yaml", k3sManualManifestsDir)
    applyHetznerCSIFileFromResource(File(k3sManualManifestsDir, "hcloud-csi.yaml"))

    // Encryption
    createFileFromResourceTemplate(
        k3sManualManifestsDir + "hcloud-encryption-secret.yaml",
        "hcloud-encryption-secret.template.yaml",
        resourcePath = hetznerCSIResourceDir,
        posixFilePermission = "644",
        values = mapOf(
            "HETZNER_ENCRYPTION_PASSPHRASE" to encryptionPassphrase.plain()
        ))
    cmd("kubectl apply -f hcloud-encryption-secret.yaml", k3sManualManifestsDir)
    applyHetznerCSIFileFromResource(File(k3sManualManifestsDir, "hcloud-encrypted-storage-class.yaml"))
}

private fun Prov.createHetznerCSIFileFromResource(
    file: File,
    posixFilePermission: FileMode? = "644"
) = task {
    createFileFromResource(
        file.path,
        file.name,
        hetznerCSIResourceDir,
        posixFilePermission,
        sudo = true
    )
}

private fun Prov.applyHetznerCSIFileFromResource(file: File, posixFilePermission: FileMode? = "644") = task {
    createHetznerCSIFileFromResource(file, posixFilePermission)
    cmd("kubectl apply -f ${file.path}", sudo = true)
}