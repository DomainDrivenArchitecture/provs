package org.domaindrivenarchitecture.provs.configuration.application

import org.domaindrivenarchitecture.provs.configuration.domain.TargetCliCommand
import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.cli.createRemoteProvInstance
import org.domaindrivenarchitecture.provs.framework.core.cli.getPasswordToConfigureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.base.makeCurrentUserSudoerWithoutPasswordRequired


fun ensureSudoWithoutPassword(prov: Prov, targetCommand: TargetCliCommand): Prov {

    return if (prov.currentUserCanSudoWithoutPassword()) {
        prov
    } else {
        val password = targetCommand.remoteTarget()?.password ?: getPasswordToConfigureSudoWithoutPassword()

        val result = prov.makeCurrentUserSudoerWithoutPasswordRequired(password)

        check(result.success) {
            "Could not make user a sudoer without password required. (E.g. the password provided may be incorrect.)"
        }

        return if (targetCommand.isValidRemote()) {
            // return a new instance as for remote instances a new ssh client is required after user was made sudoer without password
            createRemoteProvInstance(targetCommand.remoteTarget())
        } else {
            prov
        }

    }
}