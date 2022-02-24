package org.domaindrivenarchitecture.provs.configuration.domain

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PlainSecretSource
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.secretSources.PromptSecretSource


private const val USER_HOST_DELIMITER = "@"
private const val USER_PW_DELIMITER = ":"


class TargetCliCommand(
    val target: String,
    val passwordInteractive: Boolean = false
) {
    private var remoteTarget: RemoteTarget? = null

    init {
        remoteTarget = parseRemoteTarget()
    }

    fun isValidLocalhost(): Boolean {
        return target == "local"
    }

    fun isValidRemote(): Boolean {
        return (remoteTarget != null)
    }

    fun isValid(): Boolean {
        return (isValidLocalhost() || isValidRemote())
    }

    private fun parseRemoteTarget(): RemoteTarget? {
        val user: String?
        val host: String?
        var password: Secret? = null

        if (!target.contains(USER_HOST_DELIMITER)) {
            return null
        }

        host = target.substringAfter(USER_HOST_DELIMITER)

        val userPw = target.substringBefore(USER_HOST_DELIMITER)
        if (!userPw.contains(USER_PW_DELIMITER)) {
            user = userPw
        } else {
            user = userPw.substringBefore(USER_PW_DELIMITER)
            password = PlainSecretSource(userPw.substringAfter(USER_PW_DELIMITER)).secret()
        }
        if (passwordInteractive) {
            password = PromptSecretSource("Password for $user on $host").secretNullable()
        }
        return RemoteTarget(user, host, password)
    }

    fun remoteTarget(): RemoteTarget? {
        return remoteTarget
    }

    class RemoteTarget(val user: String, val host: String, val password: Secret?)
}

