# Java App — for local deployment

A small Swing desktop app that a teacher downloads and runs on their own
machine. No server, no internet connection required at run time.

## Files

| File | Purpose |
|---|---|
| `src/main/java/.../Unit.java` | Enum of the 10 supported units, each tagged with a `TEMPERATURE` or `VOLUME` category |
| `src/main/java/.../ConversionEngine.java` | Pure conversion math. No Swing dependency. |
| `src/main/java/.../Grader.java` | Grading logic (Correct / Incorrect / Invalid). No Swing dependency. |
| `src/main/java/.../MainGUI.java` | The Swing GUI — the *only* class that touches the UI toolkit |
| `src/test/java/.../ConversionEngineTest.java` | JUnit 5 tests for the conversion math |
| `src/test/java/.../GraderTest.java` | JUnit 5 tests for the grading logic |
| `pom.xml` | Maven build file |

Keeping `MainGUI` as the *only* Swing-aware class is deliberate: it means
`mvn test` can run the entire logic test suite on a headless CI runner (like
GitHub Actions) with no display attached, because the tests never touch
Swing at all.

## Requirements

- Java 17 or newer (check with `java -version`)
- Maven 3.6+ (check with `mvn -version`)

If you need to install either, see the "Prerequisites" section of
[`../docs/CICD_SETUP.md`](../docs/CICD_SETUP.md) — the same tools are used
locally and in CI.

## Running locally

```bash
mvn verify          # compiles the code and runs all JUnit tests
mvn package          # builds target/unit-converter.jar (a runnable "fat jar")
java -jar target/unit-converter.jar
```

A window should open with the same four fields as the web app: input value,
input unit, target unit, and student response, plus a "Check Answer" button.

## Notes on the conversion math

Identical rules to the web app (see
[`../web-js/README.md`](../web-js/README.md#notes-on-the-conversion-math)):
Kelvin-canonical temperature conversion, Liters-canonical US-customary volume
conversion, and grading by comparing both values rounded to the tenths place.
The constants in `ConversionEngine.java` are kept in sync by hand with the
constants in `web-js/js/converter.js` — see task 3 in
[`../docs/NEXT_STEPS.md`](../docs/NEXT_STEPS.md) for a proposed fix to that.
