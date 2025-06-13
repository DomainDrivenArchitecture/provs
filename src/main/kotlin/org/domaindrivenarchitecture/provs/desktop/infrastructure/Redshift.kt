package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.infrastructure.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.infrastructure.aptInstall


fun Prov.installRedshift() = task {
    aptInstall("redshift redshift-gtk")
}


fun Prov.configureRedshift() = task {
    aptInstall("redshift redshift-gtk")

    createDir(".config")
    createFile("~/.config/redshift.conf", config)
}


val config = """
    [redshift]
    temp-day=5500
    temp-night=2700
    brightness-day=1
    brightness-night=0.6
    fade=1

    location-provider=manual

    [manual]
    lat=48.783333
    lon=9.1833334
""".trimIndent()
