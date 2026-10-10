# Samsung voice and privacy-control follow-up — 11 October 2026

Device: Samsung SM-M076B, Android API 36. The device was interactive and unlocked for the lifecycle check. No browser, merchant, payment, account, or provider request was made.

## Evidence

- `VoiceLifecycleTest.backgroundWaitingAndOldNotificationCannotStopReplacement`: **PASS** (`OK (1)`, 8.193 s). Saathi entered the foreground waiting state, remained active after Home, returned through the normal launcher, rejected an obsolete notification Stop action after a replacement session, and stopped explicitly.
- `SelectionFormTest`: **PASS** (`OK (2)`, 0.085 s). Required native and WebView-style selection controls remain guideable without reading their selected values; CAPTCHA and OTP-like labels retain their respective safety boundaries.
- `PhysicalSpeechTest.physicalEngineCallbacksAndBoundedRecognition`: **PASS** (`1/1`, 30.814 s) in Gradle's connected Android JUnit report. English, Hindi, and Hinglish TTS callbacks succeeded; on-device recognition was available, became ready, and completed with a nonempty result within 15 seconds. The probe deliberately saved no audio or transcript. Its streamed instrumentation output was incomplete, but the generated JUnit XML is the final test result.

## Test reliability correction

The initial voice test failed when the Samsung display was off: Android launched Saathi but kept its activity stopped behind the locked display. This was an invalid physical-test condition, not a service failure. The test now requires an interactive, unlocked device, returns via Saathi's actual launcher component, verifies the active app window, and preserves pre-existing microphone and notification permissions. The permission-restoration branch remains unexercised on this device because both permissions were already granted before the test.

## Limits

No human speech intelligibility, recognition accuracy, natural conversation quality, OEM background survival, lock-screen behavior, TalkBack usability, or arbitrary site/app compatibility is certified by these checks.
