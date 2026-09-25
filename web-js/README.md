# Web App (JavaScript) — for AWS S3 static hosting

A single-page, dependency-free web app. It's plain HTML/CSS/JS on purpose —
no build step, no framework, no bundler — because it needs to be uploaded
as-is to an S3 bucket and served as a static website.

## Files

| File | Purpose |
|---|---|
| `index.html` | Page structure and the four form fields |
| `css/style.css` | Styling |
| `js/converter.js` | **Pure** conversion + grading logic. No DOM code. Testable under Node. |
| `js/app.js` | DOM wiring: reads the form, calls into `converter.js`, writes the result back to the page |
| `tests/converter.test.js` | Jest test suite |
| `package.json` | Only used for running tests locally / in CI — **not** needed to deploy the site itself |

The split between `converter.js` (logic) and `app.js` (DOM glue) is
deliberate: it's what makes the conversion and grading math unit-testable
without spinning up a browser.

## Running locally

```bash
npm install
npm test              # runs the Jest suite
```

To preview the page itself, just open `index.html` directly in a browser, or
serve the folder so relative paths behave exactly as they will on S3:

```bash
npx serve .
```

## Deploying

See [`../docs/AWS_S3_SETUP.md`](../docs/AWS_S3_SETUP.md) for a full walkthrough
of creating the bucket and turning on static website hosting, and
[`../docs/CICD_SETUP.md`](../docs/CICD_SETUP.md) for how pushes to `main`
automatically re-deploy this folder to S3 via GitHub Actions
(`.github/workflows/web-ci-cd.yml`).

Only the contents of `web-js/` are synced to the bucket — `node_modules/`,
`tests/`, and `package*.json` are excluded by the deploy step, since the
bucket only needs to serve `index.html`, `css/`, and `js/`.

## Notes on the conversion math

- Temperature conversions all route through Kelvin as an intermediate step.
- Volume conversions all route through Liters as an intermediate step, using
  **US customary** definitions (US cup = 236.5882365 mL, US tablespoon =
  14.7867648 mL, US gallon = 3.785411784 L, etc.).
- A student's answer is graded by rounding both the authoritative answer and
  the student's answer to one decimal place and comparing for equality — see
  `roundToTenth()` in `converter.js`.
