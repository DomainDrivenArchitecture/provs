package org.domaindrivenarchitecture.provs.ubuntu.extensions.demos

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.local


fun Prov.helloWorld() = def {
    cmd("echo Hello world!")
}


fun main() {
    local().helloWorld()
}
