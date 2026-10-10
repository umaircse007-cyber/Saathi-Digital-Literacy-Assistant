# Saathi continuation handoff — Samsung voice and form follow-up

## Stop rule

The user requires a detailed handoff and a stop before further work when either available Codex allowance is below 10%. Check usage before running another test or source change. At this handoff's creation, the primary 30-day window had 17% remaining. Do not consume reset credits.

## Scope completed in this continuation

The connected device was Samsung SM-M076B on Android API 36. Existing production UI, branding, glass components, navigation, backend behavior, and accessibility code were not changed. No provider request, browser navigation, merchant action, payment, account action, real form submission, commit, push, release, or deployment occurred.

### Test-only correction

`app/src/androidTest/java/com/saathi/ui/VoiceLifecycleTest.kt` was strengthened:

1. It records the microphone and notification permission state before the test, and revokes only permissions that this test granted. It never revokes an existing owner approval.
2. It refuses to run on a screen-off or locked device. The earlier physical failure happened because Android launched Saathi behind a screen-off display; that condition is not a foreground-service regression.
3. After Home, it returns through `com.saathi/.LaunchActivity` and checks the active window package. Android 16 can reject an instrumentation context's background activity launch and `ActivityScenario` can refer to an old instance after task backgrounding.

The initial retry accidentally asserted that a session deliberately stopped before return should still be active. That test assertion was removed. It was not a product failure.

## New evidence

| Check | Result | Scope |
|---|---:|---|
| Samsung `VoiceLifecycleTest` | PASS, 1/1, 8.193 s | Foreground waiting after Home, launcher return, stale notification Stop cannot end replacement session, explicit Stop cleans up. |
| Samsung `SelectionFormTest` | PASS, 2/2, 0.085 s | Required selection controls are guideable without reading selection values; CAPTCHA and OTP-like labels retain safe handovers. |
| Physical engine probe | PASS, 1/1, 30.814 s | Gradle's connected Android JUnit report records the probe as passed. English, Hindi and Hinglish TTS callbacks succeeded; the on-device recognizer was available, ready, and reported a nonempty bounded result. No audio/transcript saved. This proves bounded engine callbacks only. |
| Local Android unit suite | Build task clean | `:app:testDebugUnitTest` was up-to-date after a test-only edit. It is not a fresh rerun. |
| Test cleanup | PASS | `com.saathi.test` was stopped after the probe; zero Saathi service records remained. |

Sanitized evidence lives at `docs/test-evidence/2026-10-11-samsung-voice/README.md`. It intentionally contains no raw device dump, address, audio, transcript, account, or page contents.

## Important boundaries

- Both microphone and notification permissions were already granted before the Samsung lifecycle test. The new “revoke what the test granted” branch is therefore untested. Do **not** force it by revoking the owner's permissions. Use a dedicated test device or get explicit temporary revocation approval.
- Physical callbacks do not prove voice intelligibility, recognition accuracy, natural streaming conversation, lock-screen operation, TalkBack usability, OEM background survival, or long-session performance.
- No new Gemini/Groq call is authorized. All prior allowances are exhausted.
- Keep private field values local; do not weaken privacy boundaries to improve completion rates.
- Do not touch real Zepto cart, payments, real portal forms, legal declarations, CAPTCHA, certificates, browser accounts, deployment, signing, commit, or push without the relevant authorization.

## Existing major evidence to preserve

- `HANDOFF_2026-10-11_TRANSITIONS.md`: 15 Samsung synthetic private/message/CAPTCHA handoff rounds and 20 synthetic WebView mutation/detour/return cycles passed. The original historical WebView stale marker remains causally unresolved.
- `HANDOFF_2026-10-10_SELECTION_FIELDS.md`: selection-field privacy and no-zoom form guidance implementation.
- `HANDOFF_2026-10-10_END_TO_END.md`: broader app/backend/privacy workflow map.
- `docs/test-evidence/2026-10-11-transitions/`: final affected commerce/pause group 5/5 PASS; all selected cases have evidence across runs, but no single fresh 17/17 combined result.
- `docs/REMEDIATION_CAPABILITY_MATRIX.md`, `docs/TEST_RESULTS.md`, and `docs/NEXT_CONTINUATION.md` have been updated with this continuation.

## Remaining work, in safe order

1. **Browser provenance:** preserve fail-closed behavior when Chrome/Samsung browser does not expose a full, non-editable HTTPS address. Test a positive path only with a controlled browser and no user browser-state disruption. Do not equate scheme-less chrome text with verified TLS provenance.
2. **General form capability:** extend only structure-based, non-secret validation/dependency/completion evidence. Current presence remains intentionally different from validity or task completion.
3. **Historical WebView investigation:** use the retained on-screen tap/DOM-change trace. Current passing synthetic loops are not causal proof.
4. **Backend/AI quality:** deterministic/offline work only until fresh, scoped live-model authorization. Do not retry paid providers automatically.
5. **External/manual gates:** physical speech quality/OEM survival/TalkBack, legitimate authenticated cybercrime portal steps, and production host/TLS/shared storage remain external acceptance work.

## Validation commands used

Run only after checking usage and device status:

```text
./gradlew :app:assembleDebugAndroidTest
adb -s <Samsung serial> install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s <Samsung serial> shell am instrument -w -r -e class com.saathi.ui.VoiceLifecycleTest com.saathi.test/androidx.test.runner.AndroidJUnitRunner
adb -s <Samsung serial> shell am instrument -w -r -e class com.saathi.ui.SelectionFormTest com.saathi.test/androidx.test.runner.AndroidJUnitRunner
```

Do not run the physical speech probe again unless the user explicitly wants a new audible/microphone callback check. If a test completes, stop only `com.saathi.test` if it remains; do not stop or clear data for `com.saathi`.
