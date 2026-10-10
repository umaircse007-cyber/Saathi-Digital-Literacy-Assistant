# Continuation: selection fields and Samsung verification

**Stopped at 8% remaining in the five-hour window (56% weekly), below the user’s 10% floor. No new work or tests may start in this run.**

Final cleanup found one active Saathi ServiceRecord after instrumentation finished; full service shutdown is not certified. The test restores prior accessibility settings, so an accessibility binding may remain. Inspect the component before stopping any user-enabled service.

This is the latest continuation of `HANDOFF_2026-10-10_END_TO_END.md`. Preserve the existing working tree. The latest user stop threshold is **below 10% remaining**, superseding the older 15% threshold. Check usage before continuing; never consume a reset. Deployment, release signing, commits/pushes and new paid provider calls are not authorized for this run.

## Implemented in this phase

- Reproduced on Samsung: a native dropdown's selected value was copied as public text; form guidance omitted it because it was not editable.
- Added structural form roles for native Spinner/checkable controls and the actual Chromium `comboBoxSelect` role. Samsung WebView exposes that role on a generic `android.view.View`; guessing from the displayed value would be wrong.
- Dropdown text/content-description getters and their descendant contents are excluded from observation content. Neither selection presence nor validity is inferred. Checkboxes keep their filtered static labels so CAPTCHA/privacy detection still works; checked state is not used as proof of consent or completion.
- Explicit label relationships supply field labels when available. Missing relationships leave the field unnamed. Labels that are themselves input/selection controls cannot supply values. References are released after reading.
- Local form guidance can mark a dropdown, asks the user to make their own choice, and observes dynamic removal/next-field changes without zoom. Checkboxes and consequential/consent decisions receive manual guidance, with no recommended selection. No field is filled or submitted by Saathi.
- Selection controls are excluded from AI control proposals. The existing finite local form observation window also applies to selection-only forms.
- Main UI layouts, navigation, branding, colors, glass components and logo are unchanged. Functional guidance wording changed only where necessary.

## Validation and failures

See `test-evidence/2026-10-10-selection-fields/`. Android unit tests: **168 passed**, zero failures/skips. Debug/test builds, release Kotlin compilation and lint passed; lint has **0 errors / 60 warnings**. No backend code changed; historical backend results are not fresh evidence.

Before the final defensive label guard, a combined **12 Samsung tests passed**, then **4 synthetic commerce tests passed**. The final guard run completed: **15/16 passed**, with the pause-fixture transition failing again (`Explicit interruption state`, still GUIDING/Help). All selection, native/WebView form, privacy, permission-loss and synthetic commerce cases passed on final code. This is **not** an all-passing final device regression. See `final-device-after-guard.txt`; final structural failure and tap geometry are retained alongside it. No instrumentation remains running.

Retained failures: original value-copy regression; initial unsealed test-node label exception (fixed with optional relationship fallback); WebView dropdown missing due to its generic class (fixed using observed role); diagnostic reader's transient missing branch (test waits for a complete tree). The first combined run also missed a pause-fixture transition. Its screenshot stayed on the choices page; a later run passed unchanged. This does not establish that failure's cause, nor the older historical WebView stale-marker cause. Do not erase these failures or call the app bug-free.

## Device and workflow state

- User-authorized Samsung SM-M076B, Android 16/API 36, connected wirelessly. Rediscover its transport; do not hard-code the previous serial in shared evidence.
- Updated the app/test APKs in place; **no uninstall or data deletion** in this phase. Fixtures restore accessibility/overlay settings and stop the session in `finally`.
- No live model requests, backend server, browser fixture server, reverse mapping, real form submissions or purchases. Real Zepto was not opened or changed; its prior exact-product/Add/cart/remove acceptance remains narrow historical evidence.
- Synthetic screenshots contain fictional canaries only. Do not export real phone screenshots, notifications, account details or full device dumps to the repository.
- Previous physical speech evidence covers three-language synthesis callbacks and a bounded on-device recognizer result, not human transcription/intelligibility or natural conversation. Earlier microphone/notification permission grants remain a documented device side effect.

## Next work in order

1. Diagnose the final reproduced pause-fixture transition failure first. Compare its recorded tap rectangle with current fixture bounds/window state; determine whether the tap reached the fixture before blaming observation. Do not weaken interruption assertions. Full window/input/logcat diagnostics remain in Saathi's app-private test files on the Samsung; inspect privately, never commit raw device dumps. `cleanup.txt` records the final service check.
2. Keep native/WebView forms, selection privacy, pause/resume, permission loss and commerce regressions when editing observation code. Current selection guidance does **not** certify a selected value, semantic validity or form completion; it may remain in manual review until a different visible step is established.
3. Resolve historical browser/touch transition failures from captured window/input evidence. Passing reruns are insufficient causal evidence. Do not widen targets or weaken stale/privacy checks. Trustworthy full HTTPS browser-origin fallback also remains open.
4. Broaden general form dependencies/validation/completion and researched multi-step grounding without service-specific rules. Backend planning quality and shared durable cloud state remain unfinished. Live provider allowance is exhausted: use offline checks until a new scoped allowance is authorized.
5. Use the Samsung for bounded lifecycle, microphone accuracy, human speech intelligibility, TalkBack, interruptions and OEM survival acceptance. Engine callbacks alone do not close those gates. Protected portal steps require legitimate user access and cannot be certified using fixtures.
6. Refresh the ignored mentor APK after the final regression if needed; the installed debug APK is newer than the previously copied mentor artifact. Deployment and final external acceptance stay deferred.

The earlier end-to-end handoff contains the architecture map, backend/research/reporting/commerce/voice workflow inventory, reproduction commands and external blockers. This file supersedes its obsolete usage stop, device-availability and test-status statements, not its safety boundaries.
