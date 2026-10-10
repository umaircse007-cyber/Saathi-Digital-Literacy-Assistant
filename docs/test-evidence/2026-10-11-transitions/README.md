# Samsung transition-driver correction — 11 October 2026

## Failure and mechanism

The final 10 October failed pause run injected the centre of Rect(781,316–1372,397), x=1076.5, into a display whose root bounds were Rect(0,0–720,1600). The fixture remained on its original choices screen. An earlier failure similarly used Rect(564,338–1160,420), centre x=862. Both targets were outside the display during activity transition. See the retained `2026-10-10-selection-fields/final-tap-geometry.txt` and `final-transition-failure.txt`. This establishes an invalid test input; it does not establish a production pause/resume defect.

The test driver now requires the exact observed control bounds to fit the current display and remain unchanged for 250 ms, under the existing 12-second timeout. No nearby control, coordinate clipping, repeated blind tap, disabled animation, weakened assertion or production permission change is used. The same requirement covers the synthetic private-message return button and the WebView driver. Physical WebView testing now requires explicit `physicalDeviceConfirmed=true`.

## Verification

`pause-15-rounds.txt`: PASS in 80.972 seconds, 15 rounds / 45 private-message-CAPTCHA handoffs, followed by manual pause/resume and stale notification checks. No model calls or real authentication. Final combined test result is saved separately when complete.

The historical 9 October WebView issue is different: its Support tap was within the screen and the page actually changed while the marker remained. Do not attribute that older failure to this off-screen input or declare it fixed merely because current runs pass.

## Scope

Only test-harness code changed in this phase. All pre-existing production/UI changes are preserved. No live providers, real Zepto cart changes, submission, deployment, signing, commit or push. Installed app remains the previous selection-field debug build; test APK updated in place. The one remaining service record from the previous phase must be identified after test cleanup rather than assuming it is an audio/overlay leak.

## Combined-run finding

The 17-test Samsung group finished 16/17 passing, including all pause/resume, WebView, form, privacy and permission cases. The commerce driver retained an Add rectangle across screenshot capture, then failed to find a clickable node at that old rectangle. Its former retry required the same stale bounds for four seconds. The driver now recomputes only the same expected step, verifies the exact fixture milk parent (and excludes the oat product), and requires stable display-contained bounds before a single action. This is test-driver repair; it does not change production guidance or prove the precise reason the earlier geometry changed. Affected final checks are in `commerce-final.txt`.

Final cleanup after the combined run found **zero active Saathi service records**. The earlier single-service observation is not evidence of an ongoing leak.

Final affected commerce/pause group: **5/5 PASS**, 36.841sec. All 17 distinct cases have final applicable passing evidence across runs; do not erase the failed combined run.
