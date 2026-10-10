package com.saathi.ui

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saathi.accessibility.NodeMasker
import com.saathi.orchestrator.FormGuide
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectionFormTest {
    @Test fun requiredSelectionIsGuidedWithoutReadingOrRepeatingItsValue() {
        for (kind in listOf("android.widget.Spinner", "android.widget.CheckBox", "android.widget.RadioButton", "android.view.View")) {
            @Suppress("DEPRECATION") val raw = AccessibilityNodeInfo.obtain()
            try {
                raw.setBoundsInScreen(Rect(10, 20, 250, 90))
                raw.isVisibleToUser = true; raw.isEnabled = true; raw.isClickable = true
                raw.className = kind; raw.hintText = "Country *"
                if(kind=="android.view.View") raw.extras.putCharSequence("AccessibilityNodeInfo.chromeRole", "comboBoxSelect")
                raw.text = "fictional-selection-canary"; raw.contentDescription = "fictional-description-canary"
                val nodes = NodeMasker.flatten(raw)
                val dropdown = kind.endsWith("Spinner") || kind=="android.view.View"
                if (dropdown) {
                    assertNull("Selection values must stay local: $kind", nodes.single().text)
                    assertNull(nodes.single().description)
                    assertFalse(nodes.single().valueKnown)
                }
                for (locale in listOf("en-IN", "hi-IN", "hinglish")) {
                    val step = FormGuide.next(nodes, locale)
                    if (dropdown) assertEquals("Selection control was omitted: $kind", 0, step.target?.nodeIndex)
                    else {
                        assertNull("Decisions must remain with the user", step.target)
                        assertFalse(step.speechText.contains("cannot identify"))
                    }
                    assertFalse(step.goalComplete)
                    assertFalse(step.speechText.contains("canary"))
                }
                raw.isEnabled = false
                assertNull(FormGuide.next(NodeMasker.flatten(raw), "en-IN").target)
                raw.isEnabled = true; raw.hintText = "Accept terms and conditions *"
                assertNull("A consent choice must not be recommended", FormGuide.next(NodeMasker.flatten(raw), "en-IN").target)
            } finally { @Suppress("DEPRECATION") raw.recycle() }
        }
    }

    @Test fun checkboxLabelsStillExposeChallengeAndPrivacyBoundaries() {
        @Suppress("DEPRECATION") val raw = AccessibilityNodeInfo.obtain()
        try {
            raw.setBoundsInScreen(Rect(10, 20, 250, 90)); raw.isVisibleToUser = true
            raw.isEnabled = true; raw.isCheckable = true; raw.isClickable = true
            raw.className = "android.widget.CheckBox"
            raw.text = "Verify you are human"
            assertEquals(com.saathi.core.ScreenInterruption.Reason.CAPTCHA,
                com.saathi.core.ScreenInterruption.reason(NodeMasker.flatten(raw)))
            raw.text = "OTP 123456"
            assertEquals(com.saathi.core.ScreenInterruption.Reason.PRIVATE,
                com.saathi.core.ScreenInterruption.reason(NodeMasker.flatten(raw)))
            raw.text = "Receive newsletter"
            val nodes = NodeMasker.flatten(raw)
            val gate = com.saathi.core.ObservationGate().apply { start() }
            assertNull(com.saathi.core.LiveAiPolicy.snapshot(gate.observe("example.app", 1)!!,
                nodes, "Help", "en-IN", System.currentTimeMillis(), emptyList()))
        } finally { @Suppress("DEPRECATION") raw.recycle() }
    }
}
