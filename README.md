# Science Class Unit Converter

A grading tool for a science class. A teacher enters a numeric value and a
unit, picks the unit a student should convert it to, types in the student's
answer, and the tool reports **Correct**, **Incorrect**, or **Invalid**.

Two implementations of the same logic ship in this repository:

| Implementation | Location | Deployment target |
|---|---|---|
| JavaScript (browser) | [`web-js/`](web-js/) | Static website hosted in an AWS S3 bucket |
| Java (desktop) | [`java-local/`](java-local/) | Run locally on a teacher's own machine |

Both implementations share the same conversion math, the same grading rule,
and the same set of supported units, so a teacher gets identical results
whichever one they use.

## What it converts

**Temperature:** Kelvin, Celsius, Fahrenheit, Rankine
**Volume (US customary units):** Liters, Tablespoons, Cubic Inches, Cups, Cubic Feet, Gallons

The unit dropdowns are linked: whichever unit is picked first (temperature or
volume) determines what's offered in the second dropdown, so a student is
never asked to convert, say, gallons into Kelvin — the two categories never
mix.

## Grading rule

1. If the student's response isn't a numeric value at all → **Invalid**.
2. Otherwise, compute the authoritative converted value, round it to the
   tenths place, and round the student's response to the tenths place too.
   If the two rounded numbers match → **Correct**. If they don't → **Incorrect**.

Example: converting 25.6 cups to liters gives an authoritative answer of
6.0567 L. Rounded to the tenths place, that's 6.1 — so a student answer of
`6.1` is graded Correct.

## Repository layout

```
.
├── web-js/                  JavaScript implementation (see web-js/README.md)
├── java-local/               Java implementation (see java-local/README.md)
├── docs/
│   ├── ARCHITECTURE.md       How the code is organized and why
│   ├── GITHUB_SETUP.md       Step-by-step: create the repo, add a reviewer
│   ├── AWS_S3_SETUP.md       Step-by-step: host the web app on S3
│   └── CICD_SETUP.md         Step-by-step: set up the GitHub Actions pipelines
└── .github/workflows/        The two CI/CD pipeline definitions
```

## Quick start

**Web app (for local preview before deploying to S3):**
```bash
cd web-js
npm install
npm test          # run the automated test suite
# then just open index.html in a browser, or serve the folder with any
# static file server, e.g.: npx serve .
```

**Java app:**
```bash
cd java-local
mvn verify         # compiles and runs the automated test suite
mvn package         # builds target/unit-converter.jar
java -jar target/unit-converter.jar
```

Full, no-assumptions instructions — including how to put the web app on S3,
push this repo to GitHub, add a peer reviewer, and wire up the CI/CD
pipelines — are in the `docs/` folder linked above.

## For peer reviewers

Start with [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for how the code is
organized, then [`web-js/README.md`](web-js/README.md) and
[`java-local/README.md`](java-local/README.md) for implementation-specific
notes. Both implementations have automated test suites
(`web-js/tests/converter.test.js` and
`java-local/src/test/java/.../*Test.java`) that encode the grading examples
the tool was built against, which is the fastest way to confirm the
conversion math is correct.

## Next development tasks

Five concrete follow-ups for extending this tool are listed in
[`docs/NEXT_STEPS.md`](docs/NEXT_STEPS.md).
