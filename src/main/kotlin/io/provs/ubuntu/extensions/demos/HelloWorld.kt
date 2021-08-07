package io.provs.ubuntu.extensions.demos

import io.provs.core.Prov
import io.provs.core.local


fun Prov.helloWorld() = def {
    cmd("echo Hello world!")
}


fun main() {
    local().helloWorld()
}
