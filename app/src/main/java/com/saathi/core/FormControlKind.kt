package com.saathi.core

/** Platform roles, not website names or guesses based on a displayed value. */
enum class FormControlKind { NONE, DROPDOWN, DECISION;
    companion object {
        fun from(className: String?, checkable: Boolean = false, browserRole: String? = null): FormControlKind = when {
            // Observed Chromium accessibility role; unlike roleDescription it is not
            // localized. Unknown roles stay unknown and displayed text is never guessed.
            browserRole == "comboBoxSelect" -> DROPDOWN
            className?.substringAfterLast('.') == "Spinner" -> DROPDOWN
            checkable || className?.substringAfterLast('.') in setOf("CheckBox", "RadioButton", "Switch", "ToggleButton") -> DECISION
            else -> NONE
        }
    }
}
