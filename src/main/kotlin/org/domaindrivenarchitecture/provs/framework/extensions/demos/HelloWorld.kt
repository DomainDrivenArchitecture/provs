package org.domaindrivenarchitecture.provs.framework.extensions.demos

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.local


fun Prov.helloWorld() = def {
    cmd("echo Hello world!")
}


fun main() {
    local().helloWorld()
}