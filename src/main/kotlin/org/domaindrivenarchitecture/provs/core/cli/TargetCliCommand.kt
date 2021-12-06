package org.domaindrivenarchitecture.provs.core.cli


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
