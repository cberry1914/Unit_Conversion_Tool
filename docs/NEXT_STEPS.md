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
