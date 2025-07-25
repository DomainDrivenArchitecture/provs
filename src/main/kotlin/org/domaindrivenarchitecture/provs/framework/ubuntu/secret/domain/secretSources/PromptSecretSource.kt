package org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.secretSources

import org.domaindrivenarchitecture.provs.framework.core.Secret
import org.domaindrivenarchitecture.provs.framework.ubuntu.secret.domain.SecretSource
import java.awt.FlowLayout
import javax.swing.*


class PasswordPanel : JPanel(FlowLayout()) {

    private val passwordField = JPasswordField(30)
    private var entered = false

    val enteredPassword
        get() = if (entered) String(passwordField.password) else null

    init {
        add(JLabel("Password: "))
        add(passwordField)
        passwordField.setActionCommand("OK")
        passwordField.addActionListener {
            if (it.actionCommand == "OK") {
                entered = true

                SwingUtilities.getWindowAncestor(it.source as JComponent)
                    .dispose()
            }
        }
    }

    private fun request(passwordIdentifier: String) = apply {
        JOptionPane.showOptionDialog(null, this@PasswordPanel,
            "Enter $passwordIdentifier",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null, emptyArray(), null)
    }

    companion object {

        fun requestPassword(passwordIdentifier: String) = PasswordPanel()
            .request(passwordIdentifier)
            .enteredPassword
    }
}

class PromptSecretSource(text: String = "Secret/Password") : SecretSource(text) {

    override fun secret(): Secret {
        val password = PasswordPanel.requestPassword(parameter)
        if (password == null) {
            throw IllegalArgumentException("Failed to retrieve secret from prompting.")
        } else {
            return Secret(password)
        }
    }

    override fun secretNullable(): Secret? {
        val password = PasswordPanel.requestPassword(parameter)

        return if(password == null) {
            null
        }else {
            Secret(password)
        }
    }
}