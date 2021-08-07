package io.provs.ubuntu.secret.secretSources

import io.provs.core.Secret
import io.provs.ubuntu.secret.SecretSource
import java.awt.FlowLayout
import javax.swing.*


class PasswordPanel : JPanel(FlowLayout()) {

    private val passwordField = JPasswordField(20)
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
        val password = PasswordPanel.requestPassword(input)
        if (password == null) {
            throw IllegalArgumentException("Failed to retrieve secret from prompting.")
        } else {
            return Secret(password)
        }
    }

    override fun secretNullable(): Secret? {
        val password = PasswordPanel.requestPassword(input)

        return if(password == null) {
            null
        }else {
            Secret(password)
        }
    }
}