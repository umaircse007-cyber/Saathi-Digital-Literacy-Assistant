# Samsung Zepto marker recovery — 11 October 2026

## Scope

This is a controlled, user-authorized observation on Samsung `SM-M076B`. It
uses Zepto's public product/search surfaces and one previously authorized,
reversible add-to-cart and removal. It does not select an address, proceed to
checkout, submit an order, make a payment, send entered text to a provider, or
make a model request.

## Root cause and fix

The missing marker was first reproduced with Saathi's Accessibility service
disabled after replacing an incompatible app signature. Overlay permission was
already granted, but an unbound service cannot observe the external app or
present grounded guidance. Android's accessibility confirmation was completed
manually by the device owner.

`startLive` now declines to close Saathi into an unobservable session. It
returns a precise recovery action when floating-assistant permission, Screen
guidance, or device unlock is unavailable.

Zepto's empty search field exposed an `EditText` with the public resource ID
`search-query-input`, while the privacy layer correctly withheld its editable
description/value. The local commerce matcher now recognizes a unique search
field through its non-sensitive public hint or resource identifier. It does
not access typed text, descriptions for editable fields, or clipboard content.

## Verified observations

| Surface and request | Result | Snapshot / model calls |
| --- | --- | --- |
| Zepto home, `Order milk` | Grounded Search instruction with active overlay | 345 ms then 619 ms / 0 |
| Exact public result, `Order Amul Gold Full Cream Fresh Milk Pouch` | Grounded exact product Add instruction | 142 ms / 0 |
| Same product after the user-authorized reversible Add | Added-state instruction with unique Cart target | 203 ms / 0 |
| Cart | Review-only text; no marker on address or payment controls | 224 ms / 0 |
| Cleared Zepto search, `Order milk` after the fix | `Use Search…` instruction with active overlay | 49 ms / 0 |

The empty-search regression was executed twice after installing the current
debug APK; both Android instrumentation runs passed. The retained JSON
observation for the final run recorded `status=GUIDING`, `overlay=true`, one
external event, no failures, and the public `EditText` search control. No raw
device XML or screenshots that could include account/location data are stored
with this evidence.

## Boundaries

This verifies one current Zepto UI path on one device/version. It does not
certify arbitrary products, stock/location changes, every merchant, checkout,
payments, completion, or future Zepto accessibility layouts. Product selection
and all consequential actions remain the user's decision.
