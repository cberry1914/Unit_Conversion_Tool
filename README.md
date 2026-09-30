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


# Next development tasks

Five concrete follow-ups for extending this tool beyond its current scope.

## 1. Add a batch/CSV grading mode

Right now a teacher grades one student response at a time. A natural next
step is letting a teacher upload a CSV of an entire class's answers (student
name, input value, input unit, target unit, response) and get back a graded
CSV or an on-screen summary table. This is the highest-value addition for
actual classroom use, since it turns a one-at-a-time tool into something
that can grade a whole assignment in one pass.

## 2. Share the conversion constants between the two implementations

`web-js/js/converter.js` and `java-local/.../ConversionEngine.java` currently
duplicate the same liters-per-unit and temperature-conversion constants by
hand. A shared, language-agnostic source of truth — e.g. a small JSON or YAML
file of unit definitions, checked in at the repo root, that both a
build-time script (for the JS side) and a code-generation step or a runtime
loader (for the Java side) read from — would remove the risk of the two
implementations silently drifting apart if a constant is ever updated in only
one place.

## 3. Add score persistence and a results history

Neither implementation currently remembers past results. Adding local
storage (browser `localStorage` for the web app, a small SQLite or flat file
for the Java app) so a teacher can review a history of what was graded in a
session — and export it — would make the tool useful for record-keeping, not
just live spot-checks.

## 4. Scope the AWS IAM deployment user down to least privilege

`AWS_S3_SETUP.md` currently attaches the broad `AmazonS3FullAccess` policy to
the deployment IAM user for simplicity during initial setup. A follow-up
security hardening task: replace it with a custom policy scoped to only
`s3:PutObject`, `s3:GetObject`, `s3:DeleteObject`, and `s3:ListBucket` on the
one specific bucket ARN, following the principle of least privilege.

## 5. Add input-level validation and inline error messaging to the UI

Currently, an invalid numeric *input value* (as opposed to an invalid
*student response*, which is already handled) shows a plain warning message.
A more polished version would validate the input value field as the teacher
types (not just on submit), disable the "Check Answer" button until the form
is valid, and give each field its own inline error state — closer to what a
production-quality form would offer a teacher using this daily.


"What happens with a negative volume, or 0 Kelvin, or a Rankine value below absolute zero?" — nothing currently stops nonsensical-but-numeric physical inputs; likely flagged as a gap.
