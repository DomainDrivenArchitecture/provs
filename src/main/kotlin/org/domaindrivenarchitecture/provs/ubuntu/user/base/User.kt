package org.domaindrivenarchitecture.provs.ubuntu.user.base

import org.domaindrivenarchitecture.provs.core.Prov
import org.domaindrivenarchitecture.provs.core.ProvResult
import org.domaindrivenarchitecture.provs.core.Secret
import org.domaindrivenarchitecture.provs.core.processors.RemoteProcessor
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.createDirs
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.fileExists
import org.domaindrivenarchitecture.provs.ubuntu.filesystem.base.userHome
import org.domaindrivenarchitecture.provs.ubuntu.git.provisionGit
import org.domaindrivenarchitecture.provs.ubuntu.keys.base.gpgFingerprint
import org.domaindrivenarchitecture.provs.ubuntu.keys.provisionKeysCurrentUser
import org.domaindrivenarchitecture.provs.ubuntu.user.UserConfig
import java.net.InetAddress


fun Prov.userExists(userName: String): Boolean {
    return cmdNoEval("grep -c '^$userName:' /etc/passwd").success
}


/**
 * Creates a new user.
 */
fun Prov.createUser(
    userName: String,
    password: Secret? = null,
    sudo: Boolean = false,
    copyAuthorizedSshKeysFromCurrentUser: Boolean = false
): ProvResult = requireAll {
    if (!userExists(userName)) {
        cmd("sudo adduser --gecos \"First Last,RoomNumber,WorkPhone,HomePhone\" --disabled-password --home /home/$userName $userName")
    }
    password?.let { cmdNoLog("sudo echo \"$userName:${password.plain()}\" | sudo chpasswd") } ?: ProvResult(true)
    if (sudo) {
        makeUserSudoerWithNoSudoPasswordRequired(userName)
    }
    val authorizedKeysFile = userHome() + ".ssh/authorized_keys"
    if (copyAuthorizedSshKeysFromCurrentUser && fileExists(authorizedKeysFile)) {
        val sshPathForNewUser = "/home/$userName/.ssh"
        createDirs(sshPathForNewUser, sudo = true)
        cmd("chown $userName $sshPathForNewUser", sudo = true)

        val newAuthorizedKeysFile = "$sshPathForNewUser/authorized_keys"
        cmd("cp $authorizedKeysFile $newAuthorizedKeysFile", sudo = true)
        cmd("chown $userName $newAuthorizedKeysFile", sudo = true)
    }
    ProvResult(true) // dummy
}


/**
 * Configures gpg and ssh keys for the current if keys are provided in the config.
 * Installs and configures git for the user if gitEmail is provided in the config.
 * Does NOT CREATE the user.
 */
fun Prov.configureUser(config: UserConfig) = requireAll {
    provisionKeysCurrentUser(
        config.gpg?.keyPair(),
        config.ssh?.keyPair()
    )

    config.gitEmail?.run {
        provisionGit(
            config.userName,
            config.gitEmail,
            config.gpg?.keyPair()?.let { gpgFingerprint(it.publicKey.plain()) })
    } ?: ProvResult(true)
}


@Suppress("unused")
fun Prov.deleteUser(userName: String, deleteHomeDir: Boolean = false): ProvResult = requireAll {
    val flagToDeleteHomeDir = if (deleteHomeDir) " -r " else ""
    if (userExists(userName)) {
        cmd("sudo userdel $flagToDeleteHomeDir $userName")
    } else {
        ProvResult(false, err = "User $userName cannot be deleted as it does not exist.")
    }
}


/**
 * Makes userName a sudoer who does not need a password to sudo.
 * The current (executing) user must already be a sudoer. If he is a sudoer with password required then
 * his password must be provided.
 */
fun Prov.makeUserSudoerWithNoSudoPasswordRequired(
    userName: String,
    password: Secret? = null,
    overwriteFile: Boolean = false
): ProvResult = def {
    val userSudoFile = "/etc/sudoers.d/$userName"
    if (!fileExists(userSudoFile) || overwriteFile) {
        val sudoPrefix = if (password == null) "sudo" else "echo ${password.plain()} | sudo -S"
        // see https://stackoverflow.com/questions/323957/how-do-i-edit-etc-sudoers-from-a-script
        val result = cmdNoLog(sudoPrefix + " sh -c \"echo '$userName   ALL=(ALL) NOPASSWD:ALL' | (sudo su -c 'EDITOR=\"tee\" visudo -f " + userSudoFile + "')\"")
        // don't log the command (containing the password) resp. don't include it in the ProvResult, just include success and err
        ProvResult(result.success, err = result.err)
    } else {
        ProvResult(true, out = "File already exists")
    }
}


/**
 * Makes the current (executing) user be able to sudo without password.
 * IMPORTANT: Current user must already by sudoer when calling this function.
 */
@Suppress("unused") // used externally
fun Prov.makeUserSudoerWithNoSudoPasswordRequired(password: Secret) = def {
    val currentUser = whoami()
    if (currentUser != null) {
        makeUserSudoerWithNoSudoPasswordRequired(currentUser, password, overwriteFile = true)
    } else {
        ProvResult(false, "Current user could not be determined.")
    }
}


/**
 * Checks if user is in group sudo.
 */
@Suppress("unused")
fun Prov.userIsInGroupSudo(userName: String): Boolean {
    return cmd("getent group sudo | grep -c '$userName'").success
}


/**
 * Checks if current user can execute sudo commands.
 */
@Suppress("unused")
fun Prov.currentUserCanSudo(): Boolean {
    return chk("timeout 1 sudo id")
}


/**
 * Returns username of current user if it can be determined
 */
fun Prov.whoami(): String? {
    return cmd("whoami").run { if (success) out?.trim() else null }
}


/**
 * Creates a new user on the specified host.
 *
 * @host hostname or ip-address
 * @hostUser user on the remote system, which is used to create the new user,
 * hostUser must be sudoer
 * @hostPassword pw of hostUser on the remote system;
 * ssh-key authentication will be used if hostPassword is null
 */
@Suppress("api") // use externally
fun createRemoteUser(
    host: InetAddress,
    hostUser: String,
    hostPassword: Secret?,
    newUserName: String,
    newUserPW: Secret,
    makeNewUserSudoer: Boolean = false
) {
    Prov.newInstance(RemoteProcessor(host, hostUser, hostPassword), name = "createRemoteUser")
        .createUser(newUserName, newUserPW, makeNewUserSudoer)
}

