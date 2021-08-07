package io.provs.ubuntu.extensions.demos

import io.provs.Prov
import io.provs.local


fun Prov.helloWorld() = def {
    cmd("echo Hello world!")
}


fun main() {
    local().helloWorld()
}
