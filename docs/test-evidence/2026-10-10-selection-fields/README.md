# Selection-field regression evidence

Scope: Samsung Android 16; synthetic native/WebView fixtures through the actual AccessibilityService, plus in-memory Android accessibility nodes. No external account, real transaction or model call.

- `before.txt`: failing selected-value-copy regression on the earlier production build.
- `first-device-run.txt`: six failures out of eleven, retained for audit. Includes unsealed optional-label exceptions, unsupported WebView role and a missed pause-fixture transition.
- `role-fixture-transition-failure.txt`: diagnostic setup encountered an incomplete WebView tree; fixed with bounded complete-tree polling.
- `browser-role-before.txt`: WebView selection remained unrecognized before the role adapter.
- `selection-roles-web.txt`: observed Chromium `comboBoxSelect` metadata on generic View. Role labels are fixture metadata, not selected values.
- `final-device.txt`: 12/12 Samsung passes before the last defensive label-source guard.
- `commerce-regression.txt`: 4/4 synthetic commerce passes before that last guard. No real cart mutation.
- `final-device-after-guard.txt`: final combined regression result; read the footer for the actual count/status.
- `final-build-after-guard.txt`: final unit/build/release-compilation/lint run. 168 Android unit tests; zero lint errors, 60 warnings.
- `selection-native.png`, `selection-web.png`: synthetic dropdown marker and local guidance without manual zoom. Existing overlay styling remains; the near-top WebView marker label overlaps the status-bar area in this fixture, a remaining positioning limitation rather than a polished-layout acceptance claim.

No passing rerun proves the root cause of the older WebView stale-marker issue or the first run's missed native pause-fixture tap. Dropdown presence is not semantic validity, task completion, consent or site safety. Unknown browser roles stay unsupported; no universal-browser claim.
