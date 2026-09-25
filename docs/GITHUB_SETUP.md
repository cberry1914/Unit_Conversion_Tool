# GitHub Setup: create the repo, push the code, add a peer reviewer

Written for someone who hasn't done this before. Every step is something you
type or click — nothing is assumed.

## 1. Create the repository on GitHub

1. Sign in to GitHub and click the **+** icon in the top-right corner, then
   **New repository**.
2. Name it something clear, e.g. `science-unit-converter`.
3. Set visibility to **Private** (recommended — this is classwork, and you'll
   add your reviewer as an explicit collaborator in step 3 rather than making
   it public).
4. Do **not** check "Add a README file," "Add .gitignore," or "Choose a
   license" — this project already has those files locally, and letting
   GitHub create its own would conflict when you push.
5. Click **Create repository**. GitHub will show you a page with setup
   commands — keep that tab open, you'll use one of the command blocks in the
   next step.

## 2. Push this project to the new repository

Open a terminal in the root of this project (the folder containing this
`docs/` folder, `web-js/`, and `java-local/`), and run:

```bash
git init
git add .
git commit -m "Initial commit: unit converter (web + Java)"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/science-unit-converter.git
git push -u origin main
```

Replace `YOUR-USERNAME` and the repository name with your actual GitHub
username and the name you chose in step 1 — GitHub shows you the exact URL
to use on the page from step 1.

If this is the first time you've used `git` on this machine, it may ask you
to configure your identity first:

```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

If it asks you to log in, GitHub no longer accepts your account password for
this — you'll need a **Personal Access Token** instead. Create one under
GitHub → your profile photo → **Settings** → **Developer settings** →
**Personal access tokens** → **Tokens (classic)** → **Generate new token**.
Give it `repo` scope, generate it, and paste it in as the password when Git
prompts you (the username is still your GitHub username).

## 3. Add "FlexionCodeReview" as a collaborator

1. On your repository's GitHub page, click **Settings** (top tab bar).
2. In the left sidebar, click **Collaborators**.
3. Click **Add people**.
4. Type `FlexionCodeReview` and select it from the results.
5. Choose a role:
   - **Read** — they can view and clone the code and leave comments, but not
     push changes. This is usually the right choice for a peer reviewer.
   - **Write** — they can also push changes directly. Only choose this if you
     want them making edits themselves, not just reviewing.
6. Click **Add [FlexionCodeReview] to this repository**. GitHub sends them an
   invitation, which they need to accept before they can see the repo.

## 4. (Recommended) Open a pull request so review happens on real changes

Rather than pushing straight to `main`, a common workflow for peer review is:

```bash
git checkout -b initial-review
git push -u origin initial-review
```

Then on GitHub, click **Compare & pull request**, add a short description,
and click **Create pull request**. This gives FlexionCodeReview a dedicated
place to leave line-by-line comments, and gives you a record of what was
reviewed and approved before it landed on `main`. Once the CI/CD pipelines
are set up (see `CICD_SETUP.md`), this also means the automated tests run on
the pull request *before* anything is merged or deployed.

## What's next

- [`AWS_S3_SETUP.md`](AWS_S3_SETUP.md) — host the web app
- [`CICD_SETUP.md`](CICD_SETUP.md) — automate testing and deployment on every push
