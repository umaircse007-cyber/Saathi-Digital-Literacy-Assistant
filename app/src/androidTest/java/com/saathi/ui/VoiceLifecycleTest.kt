package com.saathi.ui

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.PowerManager
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.saathi.MainActivity
import com.saathi.language.GuidanceLanguage
import com.saathi.orchestrator.SaathiSession
import com.saathi.speech.VoiceConversationService
import com.saathi.speech.VoicePhase
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Exercises the real foreground service in a waiting state; never feeds or records speech. */
@RunWith(AndroidJUnit4::class)
class VoiceLifecycleTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context: Context get() = instrumentation.targetContext
    private fun shell(command: String): String =
        ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command))
            .bufferedReader().use { it.readText() }
    private fun waitUntil(condition: () -> Boolean) {
        val end = SystemClock.uptimeMillis() + 10_000
        while (!condition() && SystemClock.uptimeMillis() < end) SystemClock.sleep(50)
        assertTrue("Expected service transition within 10 seconds", condition())
    }
    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
    private fun activePackage(): String? = instrumentation.uiAutomation.rootInActiveWindow?.let { root ->
        try { root.packageName?.toString() } finally { @Suppress("DEPRECATION") root.recycle() }
    }

    @Test fun backgroundWaitingAndOldNotificationCannotStopReplacement() {
        val power = context.getSystemService(PowerManager::class.java)
        val keyguard = context.getSystemService(android.app.KeyguardManager::class.java)
        assumeTrue("Physical voice lifecycle testing requires an interactive, unlocked device",
            power.isInteractive && !keyguard.isKeyguardLocked)
        val prior = shell("appops get com.saathi SYSTEM_ALERT_WINDOW")
        val mode = Regex("SYSTEM_ALERT_WINDOW: (allow|deny|ignore|default)").find(prior)?.groupValues?.get(1) ?: "default"
        val microphoneWasGranted = context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val notificationsWasGranted = Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        shell("appops set com.saathi SYSTEM_ALERT_WINDOW allow")
        instrumentation.uiAutomation.grantRuntimePermission("com.saathi", Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) instrumentation.uiAutomation.grantRuntimePermission("com.saathi", Manifest.permission.POST_NOTIFICATIONS)
        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                main {
                    SaathiSession.start(context, "Pay my water bill", GuidanceLanguage.ENGLISH)
                    SaathiSession.startConversation(context)
                }
                val supported = VoiceConversationService.supported(context)
                val output = File(InstrumentationRegistry.getArguments().getString("additionalTestOutputDir") ?: context.filesDir.path)
                output.mkdirs()
                File(output, "voice-capability.txt").writeText("onDeviceRecognizerAvailable=$supported; SDK=${Build.VERSION.SDK_INT}; no audio input/output quality tested")
                if (supported) {
                    waitUntil { VoiceConversationService.phase.value == VoicePhase.WAITING }
                    val notification = context.getSystemService(NotificationManager::class.java)
                    waitUntil { notification.activeNotifications.any { it.id == 41 } }
                    val oldStop = notification.activeNotifications.first { it.id == 41 }.notification.actions.last().actionIntent
                    shell("input keyevent KEYCODE_HOME")
                    waitUntil { scenario.state == Lifecycle.State.CREATED }
                    assertEquals(VoicePhase.WAITING, VoiceConversationService.phase.value)
                    assertTrue(SaathiSession.isActive())
                    main { SaathiSession.stop() }
                    waitUntil { !VoiceConversationService.isRunning() }
                    // Returning through the actual launcher component avoids background-start
                    // restrictions on recent Android versions. The original ActivityScenario
                    // instance is not the assertion target after the task has left foreground.
                    shell("am start -n com.saathi/.LaunchActivity")
                    waitUntil { activePackage() == context.packageName }
                    main {
                        SaathiSession.start(context, "Pay my water bill", GuidanceLanguage.ENGLISH)
                        SaathiSession.startConversation(context)
                    }
                    waitUntil { VoiceConversationService.phase.value == VoicePhase.WAITING }
                    oldStop.send()
                    instrumentation.waitForIdleSync()
                    assertTrue("Old Stop must not end the replacement session", SaathiSession.isActive())
                    assertEquals(VoicePhase.WAITING, VoiceConversationService.phase.value)
                } else {
                    waitUntil { VoiceConversationService.phase.value == VoicePhase.UNAVAILABLE }
                    assertTrue("Visual session survives unavailable recognition", SaathiSession.isActive())
                }
                main { SaathiSession.stop() }
                waitUntil { !VoiceConversationService.isRunning() }
                assertEquals(VoicePhase.OFF, VoiceConversationService.phase.value)
            }
        } finally {
            main { SaathiSession.stop() }
            shell("appops set com.saathi SYSTEM_ALERT_WINDOW $mode")
            // Instrumentation must not leave a user with a permission that the test granted.
            // Do not revoke a permission the person had already approved before this test.
            if (!microphoneWasGranted) runCatching {
                instrumentation.uiAutomation.revokeRuntimePermission("com.saathi", Manifest.permission.RECORD_AUDIO)
            }
            if (Build.VERSION.SDK_INT >= 33 && !notificationsWasGranted) runCatching {
                instrumentation.uiAutomation.revokeRuntimePermission("com.saathi", Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
