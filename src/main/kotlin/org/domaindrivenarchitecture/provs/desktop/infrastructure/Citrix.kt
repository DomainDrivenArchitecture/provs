package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.web.infrastructure.downloadFromURL

/**
 * ATTENTION: Download URL might be only valid for a limited time and thus might not be working.
 * Download from: https://www.citrix.com/downloads/workspace-app/linux/workspace-app-for-linux-latest.html
 */
@Suppress("unused")
fun Prov.installCitrixWorkspaceApp() = task {
    downloadFromURL(
        "https://downloads.citrix.com/20976/linuxx64-22.5.0.16.tar.gz?__gda__=exp=1654847726~acl=/*~hmac=be248338ecd7c7de50950ff7825fc0a80577fef7d3610988c64391cff8eaca16",
        "citrix.tar.gz",
        "/tmp"
    )
    createDir("citrix", "/tmp")

    cmd("tar -xf citrix.tar.gz -C /tmp/citrix")
    // Run /tmp/citrix/setupwfc
}
