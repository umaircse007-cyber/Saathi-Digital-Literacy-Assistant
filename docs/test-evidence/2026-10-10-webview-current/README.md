# Current synthetic WebView reliability verification — 10 October 2026

## Scope

This run used the local Pixel emulator, Saathi's own synthetic external fixture and the real enabled AccessibilityService. It did not open a real website, call a model, use an account, collect private input, make a payment or submit a form.

## Result

`LiveAccessibilityIntegrationTest.realServiceTracksExternalDetourReturnAndWebChangeThenStops` passed with 40 synthetic WebView mutation/detour/return cycles.

- Same Saathi guidance session persisted through all 40 cycles.
- After the fixture's Support control changed to Support opened, the old target was cleared before the next step; the test would have failed if stale guidance remained.
- Retargeting, external detour/return, native-to-WebView switch, own-app clearing, explicit Stop, stale presentation rejection and quiet no-event request replacement also passed.
- The synthetic 600-event storm generated one coalesced screen analysis during the measured interval, with no cloud/model call.

## Measurements

| Measurement | Observed value |
|---|---:|
| WebView cycles | 40 |
| Cycle duration | 70,854 ms |
| App PSS, first sample | 108,236 KB |
| App PSS, last sample | 110,529 KB |
| Synthetic event-storm duration | 1,678 ms |
| Event-storm analyzed screens | 1 |
| Model calls | 0 |

The short test is evidence of current synthetic behavior only. It is not a heap-leak certification, OEM endurance result, universal browser result or proof of the historical intermittent WebView failure's single root cause.

## Harness notes

The first run used an external `/sdcard` output directory, which modern Android storage rejected. The valid rerun used app-private evidence storage. A freshly installed emulator package was also left "enabled but crashed" by Android's accessibility manager; the existing test-only `DeviceTestAccess.reconnect` helper now toggles the service before the assertion. No production Android guidance code changed for either harness correction.
