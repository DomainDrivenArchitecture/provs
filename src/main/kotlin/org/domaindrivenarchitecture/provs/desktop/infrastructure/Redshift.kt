package org.domaindrivenarchitecture.provs.desktop.infrastructure

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createDir
import org.domaindrivenarchitecture.provs.framework.ubuntu.filesystem.base.createFile
import org.domaindrivenarchitecture.provs.framework.ubuntu.install.base.aptInstall


fun Prov.installRedshift() = def {
    aptInstall("redshift redshift-gtk")
}


fun Prov.configureRedshift() = def {
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
