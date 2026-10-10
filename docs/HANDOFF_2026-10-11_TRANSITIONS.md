# Latest continuation — Samsung transition reliability

Read this before the older handoffs. User authorizes connected Samsung testing, preserves UI and requests a Markdown handoff if remaining usage falls below 10%. Deployment/release/push and additional paid model calls remain deferred. No reset consumed.

## 11 October follow-up — physical voice test reliability

No production source or UI changed. `VoiceLifecycleTest` now restores only permissions it granted itself and requires an interactive, unlocked device. It returns through Saathi's real launcher after Home instead of assuming a stale `ActivityScenario` instance will resume on Android 16. Samsung formal result: **1/1 PASS** in 8.193 seconds. The same device passed `SelectionFormTest` **2/2** in 0.085 seconds. See `test-evidence/2026-10-11-samsung-voice/`.

The first two failures are retained as test-condition evidence: first the display was off, then a new assertion incorrectly expected a deliberately stopped initial session to remain active. Neither exposed a production voice-service defect. A bounded physical engine probe formally passed through Gradle's connected Android JUnit report (1/1, 30.814 seconds): English/Hindi/Hinglish TTS callbacks succeeded, and on-device recognition became ready with a nonempty result inside 15 seconds. It saved no audio or transcript. The runner's streamed output was incomplete, but the generated JUnit XML is the final result.

The connected Samsung had microphone and notification permissions before this check, so the new revocation branch is untested; the test intentionally preserves those existing approvals. It also refuses to run on a locked or screen-off device rather than presenting that hardware state as an app regression. Physical quality, long background/OEM survival, TalkBack, protected portals and arbitrary browser acceptance remain manual gates.

## Implemented

The final 10 October pause failure was traced to a test-driver input defect: it tapped x=1076.5 on a 720-pixel-wide display while a new activity was animating into place. The screen remained on choices; the interruption had not been triggered. The previous similar failure tapped x=862. Evidence is retained in the selection-fields folder. This is stronger than merely observing a passing rerun, but does not certify all pause/resume paths or attribute the older WebView issue.

PauseResumeIntegrationTest now waits for the exact fixture control to fit the display and hold the same bounds for 250ms within its existing timeout, including the private-message return control. LiveAccessibilityIntegrationTest uses the same principle and now allows explicitly opted-in physical testing. No production behavior, safety gate, UI layout, theme, audio permission, animation setting or third-party action was changed this run. Preserve every pre-existing production change.

## Verification

- Test APK build passed. Production source unchanged, so the prior 168-unit/build/lint evidence was not rerun or represented as new.
- Samsung: 15 rounds / 45 synthetic private-message-CAPTCHA handoffs PASS (80.972sec), plus manual pause, permission refusal, fresh resumption, stale-stop rejection and explicit Stop.
- Samsung: 20 synthetic WebView mutation/detour/return cycles PASS (42.950sec); same session retained. PSS first/last samples 160995/160798KB; short samples are not leak/OEM certification.
- Local 600-event burst: one coalesced analysis, zero model calls; not a paid cloud cost test.
- Combined 17-test run: 16/17 pass; commerce test-driver stale-rectangle failure retained. Driver now re-grounds the same step/product, then waits for stable on-screen geometry. Final affected rerun: **5/5 PASS** in 36.841sec (`commerce-final.txt`). All 17 distinct selected cases have passing evidence across runs, not one all-passing final combined run. No production change. Cleanup after combined run found zero active Saathi service records.

## Next work

1. Preserve the new physical-device preconditions. If permission restoration needs direct coverage, use a dedicated test device or an explicit temporary permission-revocation authorization; do not revoke the owner's existing microphone/notification approvals.
2. Historical 9 October WebView stale marker remains a separate unresolved causal question: its tap was on-screen and the DOM did change. Current passing cycles are present-day evidence only.
3. General form dependency/validity/completion and researched multi-step browser grounding remain partial. Native and Chromium dropdown guidance is structure-only; it deliberately does not infer correctness from a selected value.
4. Remaining genuine-model quality needs new scoped authorization; previous allowances exhausted. Shared durable cloud state/runtime acceptance remains incomplete, deployment deferred.
5. Physical speech intelligibility/recognition accuracy, long OEM background survival, TalkBack usability and protected portal acceptance remain manual/device checks. Existing synthetic tests are not a substitute.
6. Refresh ignored mentor APK after production changes; production APK unchanged this phase. Do not reinstall/delete app data unnecessarily or alter real Zepto cart.

No live provider/backend server, emulator or reverse mapping started. No transaction, private form entry, complaint submission, commit/push or deployment. Synthetic evidence only is saved; raw window/input dumps stay app-private and must not be committed. Main comprehensive architecture/workflow map is `HANDOFF_2026-10-10_END_TO_END.md`; selection implementation is `HANDOFF_2026-10-10_SELECTION_FIELDS.md`.
