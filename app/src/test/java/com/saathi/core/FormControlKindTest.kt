package com.saathi.core

import org.junit.Assert.assertEquals
import org.junit.Test

class FormControlKindTest {
    @Test fun rolesAreStructuralAndUnknownDescriptionsDoNotBecomeFields() {
        assertEquals(FormControlKind.DROPDOWN, FormControlKind.from("android.widget.Spinner"))
        assertEquals(FormControlKind.DROPDOWN, FormControlKind.from("android.view.View", browserRole="comboBoxSelect"))
        assertEquals(FormControlKind.DECISION, FormControlKind.from("example.Toggle", checkable=true))
        for(role in listOf("button", "dropdown", "Country *", "combobox")) {
            assertEquals(FormControlKind.NONE, FormControlKind.from("android.view.View", browserRole=role))
        }
    }
}
