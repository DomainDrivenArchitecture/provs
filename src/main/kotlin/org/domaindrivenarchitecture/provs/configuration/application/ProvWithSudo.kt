package org.domaindrivenarchitecture.provs.configuration.application

import org.domaindrivenarchitecture.provs.framework.core.Prov
import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.core.cli.getPasswordToConfigureSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.currentUserCanSudoWithoutPassword
import org.domaindrivenarchitecture.provs.framework.ubuntu.user.infrastructure.makeCurrentUserSudoerWithoutPasswordRequired


fun Prov.ensureSudoWithoutPassword(password: Secret?) {

    if (!currentUserCanSudoWithoutPassword()) {
        val passwordNonNull = password ?: getPasswordToConfigureSudoWithoutPassword()

        val result = makeCurrentUserSudoerWithoutPasswordRequired(passwordNonNull)

        check(result.success) {
            "Could not make user a sudoer without password required. (E.g. the password provided may be incorrect.)"
        }
    }
}