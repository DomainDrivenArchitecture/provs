#!/usr/bin/env -S kotlinc -cp /usr/local/bin/provs-desktop.jar -Djava.awt.headless=false -script

// Prerequisites:
// * To run the script directly, you must have Kotlin installed, see https://kotlinlang.org/docs/command-line.html
// * adjust path above /usr/local/bin/provs-desktop.jar to correct location of your jar

// Run this script directly by: ./DemoScript.kts

import org.domaindrivenarchitecture.provs.framework.core.local


local().task {
    cmd("echo Hello KotlinScript!")
}
