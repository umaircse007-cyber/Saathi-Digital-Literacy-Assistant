# Saathi handoff — ready for hosting, not deployed

## Scope of this final pre-hosting checkpoint

This checkpoint completes the repository work that can be verified without selecting a host, issuing production access, accepting third-party terms, using a protected account, or making new paid model calls. The application UI is unchanged: Saathi colors, logo, navigation, liquid-glass controls, content-sized buttons and user-facing flows remain as previously implemented.

No hosting, deployment, DNS, TLS setup, release signing, public publishing, commit or push was performed.

## Current verified build

| Check | Result | Scope |
| --- | --- | --- |
| Android unit tests | **168 passed** | Existing accessibility, privacy, guidance, form, interruption and session logic. |
| Backend tests | **174 passed** | Synthetic provider, diagnostics, deadlines, cancellation, privacy, research, planning, browser safety, hosted boundary and new deployment-preflight coverage. |
| Android debug/release/lint | **Passed** | Lint: 0 errors, 60 warnings. |
| Deployment preflight tests | **4 passed** | Offline configuration/state safety and secret-output boundary. |
| Tracked-source key-pattern scan | **No matches** | No Gemini/Groq-shaped credentials found in tracked text; ignored local credential files were not read. |
| Samsung app launch | **Passed** | Restored current debug APK opened `LaunchActivity` in 272 ms; Saathi process was present. |
| Samsung voice lifecycle | **Method passed** | JUnit records 1/1 pass; Gradle reported a runner teardown crash afterwards. This is not counted as a clean device-suite pass. |
| Samsung speech/selection | **Previously passed** | Bounded engine callbacks and structural selection guidance only. |

The backend suite's localhost-only tests were run with explicit loopback permission. They made no external network or Gemini/Groq request.

## Completed product behavior

- Accessibility-based local guidance with stale-screen rejection and exact visible-option grounding.
- Cross-app structural guidance for supported accessibility trees, with fallback when a target is ambiguous or unsafe.
- Private-field classification that avoids treating ordinary dates, prices, quantities, locations and public product ranges as secrets merely because they contain digits or currency.
- Privacy handover for passwords, OTPs, PINs, payment credentials and other sensitive contexts; values remain unread and are not sent to the backend.
- Native and WebView form-control recognition, including selection controls, with finite re-observation and no manual zoom requirement.
- Pause/resume, session replacement, permission loss, authentication/CAPTCHA interruption and return-to-task behavior.
- User-controlled typed and voice request paths, text-only or spoken guidance, bounded voice turn lifecycle and local fallback.
- Cyber-fraud reporting advice, incident worksheet, per-field copy approval and manual-paste guidance. The app does not submit complaints or infer legal completion.
- Local commerce guidance, including the documented controlled Zepto Add → cart-review → remove path, with no automatic transaction or checkout.
- Source-reviewed research, authority/freshness metadata, prerequisite graphs, eligibility uncertainty, task predicates and fail-closed browser-origin handling for supported Chrome/Brave browser chrome.
- Backend provider routing, fixed freshness deadlines, cancellation, budgets, request correlation and privacy-safe status metadata. No request retries silently after dispatch.
- Prepared single-host pilot package: non-root Docker template, Caddy/systemd configuration, hosted authentication and durable SQLite caps for a single worker.

## New pre-hosting safeguard

`deploy/preflight.py` is ready to run on the future selected host before services start:

```sh
python3 deploy/preflight.py \
  --env-file /etc/saathi/backend.env \
  --state-dir /var/lib/saathi \
  --repo-root /opt/saathi
```

It checks private permissions, required/placeholder-free provider settings, valid model identifiers, bounded call caps, durable-state permissions and replacement of the Caddy placeholder domain. It deliberately does **not** start Gunicorn/Caddy, open a port, contact models or print secret values. `--skip-template-domain` is for packaging review only, never service start.

## Remaining external/manual gates

These are not repository bugs and must be completed in the separate hosting/release phase:

1. Select a host, public HTTPS domain, managed TLS certificate and private secret store.
2. Validate Caddy/systemd/Gunicorn on that selected Linux host; verify remote Android HTTPS connectivity and per-user token provisioning.
3. Build/sign the release APK with the selected public HTTPS origin.
4. Run a fresh, explicitly budgeted Gemini/Groq evaluation using fictional non-sensitive cases. Existing live-call allowances are exhausted; offline behavior does not prove model accuracy or latency.
5. Confirm human voice quality, recognition accuracy, background behavior, TalkBack and performance on representative physical devices/OEMs.
6. Obtain legitimate authenticated access for protected browser/cybercrime portals. Do not bypass CAPTCHAs, certificates, legal declarations or identity requirements; do not submit a real complaint as a test.
7. Accept that arbitrary browser metadata may be incomplete. Unsupported, editable, truncated or ambiguous browser addresses must remain a manual handoff.
8. If multi-instance/cloud scaling is chosen, implement the shared durable-state design in `CLOUD_DURABLE_STATE_DESIGN.md`; the current SQLite pilot is intentionally single-worker/single-host.

## Important limits that remain intentional

- Saathi does not automatically tap, type, paste, submit, pay, accept terms, solve challenges or claim task completion.
- Private values are never read from accessibility nodes or automatically uploaded merely because the screen is highlighted.
- A successful provider transport response is not a guarantee of factual reasoning or current policy accuracy.
- Passing mutation regressions do not identify a unique root cause for the historical intermittent WebView stale-marker incident.
- The product must not be described as universally compatible, provider-accurate, crash-free or production-certified until the external gates above are accepted.

## Next phase: hosting and release only

Before starting the next phase, preserve the current tree, check the provider/account permissions chosen by the operator, and run the offline preflight on the future host. Then follow `docs/DEPLOYMENT.md` in order. No production deployment action has been performed in this checkpoint.
