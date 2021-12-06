package org.domaindrivenarchitecture.provs.core.cli

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.core.local
import org.domaindrivenarchitecture.provs.core.remote
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.GopassSecretSource
import org.domaindrivenarchitecture.provs.ubuntu.secret.secretSources.PromptSecretSource
import org.domaindrivenarchitecture.provs.ubuntu.user.base.currentUserCanSudo
import org.domaindrivenarchitecture.provs.ubuntu.user.base.makeUserSudoerWithNoSudoPasswordRequired
import org.domaindrivenarchitecture.provs.ubuntu.user.base.whoami
import kotlin.system.exitProcess


class TargetCliCommand(
    val localHost: Boolean?,
    val remoteHost: String?,
    val userName: String?,
    val sshWithPasswordPrompt: Boolean,
    val sshWithGopassPath: String?,
    val sshWithKey: Boolean
) {
    fun isValidLocalhost(): Boolean {
        return (localHost ?: false) && remoteHost == null && userName == null && sshWithGopassPath == null &&
                !sshWithPasswordPrompt && !sshWithKey
    }

    fun hasValidPasswordOption(): Boolean {
        return (sshWithGopassPath != null) xor sshWithPasswordPrompt xor sshWithKey
    }

    fun isValidRemote(): Boolean {
        return remoteHost != null && userName != null && hasValidPasswordOption()
    }

    fun isValid(): Boolean {
        return (isValidLocalhost() || isValidRemote())
    }
}

fun parseTarget(
    programName: String = "java -jar provs.jar",
    args: Array<String>
): TargetCliCommand {
    val parser = TargetParser(programName)
    parser.parse(args)

    return TargetCliCommand(
        parser.localHost,
        parser.remoteHost,
        parser.userName,
        parser.sshWithPasswordPrompt,
        parser.sshWithGopassPath,
        parser.sshWithKey
    )
}
