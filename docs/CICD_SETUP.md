# CI/CD Setup, for someone who has never set up a pipeline before

This guide assumes zero prior experience with CI/CD. It explains what it is,
why this project has it, and then walks through turning it on step by step.

## What CI/CD actually means

**CI (Continuous Integration)** means: every time code is pushed, a robot
automatically checks that it still works — here, that means running the
Jest tests for the web app and the JUnit tests for the Java app.

**CD (Continuous Deployment)** means: if the code passes those checks, a
robot automatically ships it somewhere — here, that means uploading the
web app's files to the S3 bucket so the live site updates itself.

You don't run these steps by hand. You push code to GitHub, and GitHub runs
them for you in the background, using a free tool called **GitHub Actions**.
The instructions for what to run live in this repository already, as two
files:

- `.github/workflows/web-ci-cd.yml` — tests the web app, then deploys it to S3
- `.github/workflows/java-ci.yml` — builds and tests the Java app (there's no
  deploy step for Java, since it's meant to be downloaded and run locally,
  not hosted anywhere)

You don't need to write these files — they already exist in this project.
This guide is about making them actually *run*, which requires two things:
(1) the code needs to be on GitHub, and (2) GitHub needs your AWS
credentials so it's allowed to upload to your bucket.

## Prerequisites

Before continuing, make sure you've completed:
- [`GITHUB_SETUP.md`](GITHUB_SETUP.md) — the code needs to already be pushed
  to a GitHub repository for any of this to work.
- [`AWS_S3_SETUP.md`](AWS_S3_SETUP.md) — specifically, you need the **Access
  key ID** and **Secret access key** from step 6 of that guide.

You'll also want these installed on your own machine if you plan to run
tests locally before pushing (GitHub Actions installs its own copies
automatically, so this isn't strictly required, but it's good practice):
- [Node.js](https://nodejs.org/) (version 18 or newer) — for the web app
- [Java (JDK) 17+](https://adoptium.net/) and [Maven](https://maven.apache.org/install.html) — for the Java app

## 1. Add your AWS credentials as GitHub Secrets

"Secrets" are how GitHub Actions gets access to things like your AWS
credentials without those credentials ever appearing in your code or being
visible to anyone browsing the repository.

1. On your repository's GitHub page, click **Settings**.
2. In the left sidebar, click **Secrets and variables**, then **Actions**.
3. Click **New repository secret** and add each of the following, one at a
   time (click **Add secret** after each):

| Secret name | Value |
|---|---|
| `AWS_ACCESS_KEY_ID` | The Access key ID from AWS_S3_SETUP.md step 6 |
| `AWS_SECRET_ACCESS_KEY` | The Secret access key from AWS_S3_SETUP.md step 6 |
| `AWS_REGION` | The region you picked when creating the bucket, e.g. `us-east-1` |
| `S3_BUCKET_NAME` | Your bucket's exact name from AWS_S3_SETUP.md step 2 |

Double-check for typos — a wrong bucket name or region is the most common
reason a first deploy fails.

## 2. Push to `main` and watch it run

The workflows are already configured to run automatically:
- **Any push or pull request** touching `web-js/` runs the Jest tests.
- **A push to `main`** (after tests pass) also deploys `web-js/` to your S3
  bucket.
- **Any push or pull request** touching `java-local/` builds the Java app and
  runs its JUnit tests.

To see it in action:

```bash
git add .
git commit -m "Trigger CI/CD"
git push
```

Then on GitHub, click the **Actions** tab at the top of your repository.
You'll see a workflow run appear, with a yellow dot (running), which turns
into a green check (passed) or a red X (failed) within a minute or two.
Click into a run to see the log output of each step.

## 3. Confirm the deployment worked

Once the "Web App CI/CD" workflow shows a green check on its `deploy` job,
open your S3 bucket's website endpoint URL (from `AWS_S3_SETUP.md` step 3) in
a browser. You should see the current version of the page.

## How to read a failed run

Click the failed run in the **Actions** tab, then click the job that has the
red X, then click the step that failed to expand its log output.

Common first-time issues:
- **`npm ci` fails** — usually means `web-js/package-lock.json` wasn't
  committed. Run `npm install` locally once (which generates it) and commit
  the resulting `package-lock.json`.
- **Deploy step fails with a permissions or "Access Denied" error** — double
  check the four secret names and values from step 1 above, especially that
  there are no extra spaces.
- **`mvn -B verify` fails on the Java side** — click through to the test
  output; it will name the specific failing test and assertion.

## Making a change and seeing the pipeline work end-to-end

A good way to build confidence in the pipeline: make a small, harmless
change (like editing a sentence in `web-js/index.html`), commit it, push it,
and watch the Actions tab. Within a couple of minutes the live S3 site
should reflect your change with no manual upload step at all — that's the
whole point of CD.
