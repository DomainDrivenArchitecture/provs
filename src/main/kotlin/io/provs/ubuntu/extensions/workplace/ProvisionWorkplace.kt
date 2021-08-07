package io.provs.ubuntu.extensions.workplace

import io.provs.core.*
import io.provs.core.processors.RemoteProcessor
import io.provs.ubuntu.extensions.workplace.base.*
import io.provs.ubuntu.git.provisionGit
import io.provs.ubuntu.install.base.aptInstall
import io.provs.ubuntu.install.base.aptInstallFromPpa
import io.provs.ubuntu.install.base.aptPurge
import io.provs.ubuntu.keys.KeyPair
import io.provs.ubuntu.keys.base.gpgFingerprint
import io.provs.ubuntu.keys.provisionKeysCurrentUser
import io.provs.ubuntu.secret.secretSources.PromptSecretSource
import io.provs.ubuntu.user.base.currentUserCanSudo
import io.provs.ubuntu.user.base.makeUserSudoerWithNoSudoPasswordRequired
import io.provs.ubuntu.user.base.whoami
import java.net.InetAddress
import kotlin.system.exitProcess


enum class WorkplaceType {
    MINIMAL, OFFICE, IDE
}


/**
 * Provisions software and configurations for a personal workplace.
 * Offers the possibility to choose between different types.
 * Type OFFICE installs office-related software like Thunderbird, LibreOffice, and much more.
 * Type IDE provides additional software for a development environment, such as Visual Studio Code, IntelliJ, etc.
 *
 * Prerequisites: user must be sudoer. If password is required for user to execute sudo, then also parameter userPassword must be provided
 *
 * @param workplaceType
 * @param userPassword only needs to be provided if user cannot sudo without password
 */
fun Prov.provisionWorkplace(
    workplaceType: WorkplaceType = WorkplaceType.MINIMAL,
    ssh: KeyPair? = null,
    gpg: KeyPair? = null,
    gitUserName: String? = null,
    gitEmail: String? = null,
    userPassword: Secret? = null
) = requireAll {

    userPassword?.also { makeUserSudoerWithNoSudoPasswordRequired(it) }

    if (!currentUserCanSudo()) {
        throw Exception("Current user ${whoami()} cannot execute sudo without a password, but he must be able to in order to provisionWorkplace")
    }

    aptInstall("ssh gnupg curl git")

    provisionKeysCurrentUser(gpg, ssh)
    provisionGit(gitUserName ?: whoami(), gitEmail, gpg?.let { gpgFingerprint(it.publicKey.plain()) })

    installVirtualBoxGuestAdditions()

    aptPurge("remove-power-management xfce4-power-manager " +
            "xfce4-power-manager-plugins xfce4-power-manager-data")
    aptPurge("abiword gnumeric")
    aptPurge("popularity-contest")

    configureNoSwappiness()

    if (workplaceType == WorkplaceType.OFFICE || workplaceType == WorkplaceType.IDE) {
        aptInstall("seahorse")
        aptInstall(BASH_UTILS)
        aptInstall(OS_ANALYSIS)
        aptInstall(ZIP_UTILS)

        aptInstall("firefox chromium-browser")
        aptInstall("thunderbird libreoffice")
        aptInstall("xclip")

        installZimWiki()
        installGopass()
        aptInstallFromPpa("nextcloud-devs", "client", "nextcloud-client")

        aptInstall("inkscape")
        aptInstall("dia")

        aptInstall(SPELLCHECKING_DE)

        installRedshift()
        configureRedshift()
    }

    if (workplaceType == WorkplaceType.IDE) {

        aptInstall(JAVA_JDK)

        aptInstall(OPEN_VPM)
        aptInstall(OPENCONNECT)
        aptInstall(VPNC)

        installDocker()

        // IDEs
        cmd("sudo snap install intellij-idea-community --classic")
        installVSC("python", "clojure")
    }

    ProvResult(true) // dummy
}


/**
 * Provisions a workplace on a remote machine.
 * Prerequisite: you have built the uberjar by ./gradlew uberJarLatest
 * The remote host and remote user are specified by args parameters.
 * The first argument specifies hostName or IP-Address of the remote machine,
 * the second argument defines the user on the remote machine for whom the workplace is provisioned;
 * You can invoke this method e.g. using the jar-file from the project root by:
 * java -jar build/libs/provs-extensions-uber.jar io.provs.ubuntu.extensions.workplace.ProvisionWorkplaceKt provisionRemote <ip> <user>
 * You will be prompted for the password of the remote user.
 *
 * @param args host and userName of the remote machine as the first resp. second argument
 */
fun provisionRemote(args: Array<String>) {
    if (args.size != 2) {
        println("Please specify host and user.")
        exitProcess(1)
    }

    val host = InetAddress.getByName(args[0])
    val userName = args[1]
    val pwSecret = PromptSecretSource("Password for user $userName on $host").secret()
    val pwFromSecret = Password(pwSecret.plain())

    val config = readWorkplaceConfigFromFile() ?: WorkplaceConfig()
    Prov.newInstance(RemoteProcessor(host, userName, pwFromSecret), OS.LINUX.name).provisionWorkplace(
        config.type,
        config.ssh?.keyPair(),
        config.gpg?.keyPair(),
        config.gitUserName,
        config.gitEmail,
        pwFromSecret
    )
}


/**
 * Provisions a workplace on a remote machine by calling method provisionRemote.
 *
 * @ see #provisionRemote(args: Array<String>)
 *
 * You can invoke this method e.g. using the jar-file from the project root by:
 * java -jar build/libs/provs-ext-latest.jar workplace.WorkplaceKt main
 *
 * @param args host and userName of the remote machine as first resp. second argument
 */
fun main(args: Array<String>) {
    provisionRemote(args = args)
}