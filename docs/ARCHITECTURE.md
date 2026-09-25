# Architecture

## Goal and constraints

The tool needed to exist in two forms — a web page hostable on a plain S3
bucket, and a desktop app runnable with no server — while guaranteeing both
forms grade a student identically. That constraint drove every structural
decision below.

## Shared design: logic separated from interface

Both implementations use the same internal split:

```
┌─────────────────────┐        ┌─────────────────────┐
│   Interface layer    │        │   Interface layer    │
│  (app.js / MainGUI)  │        │                       │
│  reads the form,      │        │  reads the form,      │
│  calls the engine,    │        │  calls the engine,    │
│  writes the result    │        │  writes the result    │
└──────────┬───────────┘        └──────────┬───────────┘
           │                                │
           ▼                                ▼
┌─────────────────────────────────────────────────────┐
│              Logic layer (pure, no UI code)           │
│   Unit categories · conversion math · grading rule     │
│   web-js/js/converter.js    java-local/.../Conversion-  │
│                              Engine.java + Grader.java   │
└─────────────────────────────────────────────────────┘
```

This matters for two concrete reasons:

1. **Testability.** The logic layer has zero dependency on a browser DOM or
   on Swing/AWT. That's what lets `npm test` and `mvn test` both run
   headlessly in CI — there's no display to attach to and nothing to mock.
2. **Parity.** Because the interface layer is thin (it does almost nothing
   but read fields and write a result), the two implementations are unlikely
   to drift into disagreeing about an edge case — most of the actual
   behavior lives in the small, symmetric logic files.

## Conversion strategy: canonical intermediate units

Rather than writing a direct formula for every possible unit pair (which
would mean 4×3 = 12 temperature formulas and 6×5 = 30 volume formulas), both
implementations convert *into* a canonical unit first, then *out of* it:

- **Temperature** → always converts through **Kelvin**.
- **Volume** → always converts through **Liters**.

So converting cups to gallons is actually "cups → liters, then liters →
gallons" under the hood. This keeps the amount of conversion math small (4
temperature formulas, 6 volume factors) and means adding a new unit later is
a one-line addition (see `docs/NEXT_STEPS.md`), not a combinatorial one.

Volume unit factors are **US customary** definitions:

| Unit | Liters |
|---|---|
| Liter | 1 |
| Tablespoon (US) | 0.0147867648 |
| Cubic inch | 0.016387064 |
| Cup (US) | 0.2365882365 |
| Cubic foot | 28.316846592 |
| Gallon (US) | 3.785411784 |

## Preventing invalid unit pairs structurally, not just by validation

Rather than allowing any of the 10 units in both dropdowns and then checking
for a mismatched pair (e.g. gallons → Kelvin) at grading time, the *first*
dropdown's selection determines what the *second* dropdown even offers:
picking a temperature unit filters the second dropdown to the other three
temperature units, and picking a volume unit filters it to the other five
volume units. A category mismatch is therefore impossible to submit from the
UI at all.

The logic layer's `convert()` function still checks categories and throws if
it's ever called directly with mismatched units — as defense-in-depth for
any future caller that bypasses the UI (a test, a CLI, an API endpoint).

## Grading rule

1. Not a numeric string → **Invalid**.
2. Numeric, and rounds to the same tenths-place value as the authoritative
   answer → **Correct**.
3. Numeric, but doesn't match → **Incorrect**.

Rounding both sides to the tenths place (rather than requiring an exact
floating-point match) is what allows a student to type `543.94` and be
marked correct against an authoritative value of `543.87` — both round to
`543.9`.

## Why two separate CI/CD workflows

`.github/workflows/web-ci-cd.yml` and `.github/workflows/java-ci.yml` are
kept separate, each scoped (via `paths:`) to only trigger on changes inside
its own subfolder. This means a change to the Java app doesn't re-run the
JavaScript test suite or attempt an S3 deploy, and vice versa — each
implementation has its own independent pipeline, matching the fact that they
have independent toolchains (npm vs. Maven) and independent deployment
targets (S3 vs. "download and run locally," which has no deploy step at
all).
