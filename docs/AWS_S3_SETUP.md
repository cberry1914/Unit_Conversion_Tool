# AWS S3 Setup: host the web app as a static website

This walks through creating an S3 bucket, turning on static website hosting,
and doing a first manual upload so you can confirm it works before wiring up
the automated pipeline in `CICD_SETUP.md`.

## 1. Create an AWS account (skip if you already have one)

Go to <https://aws.amazon.com/> and click **Create an AWS Account**. AWS
requires a credit card even if you stay within the free tier; S3 hosting for
a small class tool like this one costs a very small amount (typically well
under $1/month).

## 2. Create the S3 bucket

1. Sign in to the [AWS Console](https://console.aws.amazon.com/) and search
   for **S3** in the top search bar, then open the S3 service.
2. Click **Create bucket**.
3. **Bucket name:** must be globally unique across all of AWS, e.g.
   `your-name-unit-converter-2026`. Write this name down — you'll need it
   again later.
4. **AWS Region:** pick one close to you or your students (e.g.
   `us-east-1`). Write this down too.
5. Under **Block Public Access settings for this bucket**, **uncheck** "Block
   all public access." You'll be asked to confirm you understand the bucket
   will become public — check the acknowledgment box. (This is safe here
   because the bucket only contains a public, non-sensitive class tool.)
6. Leave the other settings at their defaults and click **Create bucket**.

## 3. Turn on static website hosting

1. Click into the bucket you just created.
2. Go to the **Properties** tab.
3. Scroll down to **Static website hosting** and click **Edit**.
4. Select **Enable**.
5. **Index document:** type `index.html`.
6. Click **Save changes**.
7. Back on the Properties tab, scroll back down to **Static website
   hosting** — AWS now shows you a **Bucket website endpoint** URL. That's
   the public URL your students will use. Write it down.

## 4. Allow public read access to the files

Enabling static website hosting doesn't automatically make the files
readable — you need a bucket policy that says so.

1. Go to the **Permissions** tab of the bucket.
2. Scroll to **Bucket policy** and click **Edit**.
3. Paste in the following, replacing `YOUR-BUCKET-NAME` with your actual
   bucket name from step 2:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::YOUR-BUCKET-NAME/*"
    }
  ]
}
```

4. Click **Save changes**.

## 5. Upload the site manually (first test, before automating it)

1. Go to the **Objects** tab of the bucket and click **Upload**.
2. Click **Add files**, and select `index.html` from the `web-js/` folder.
3. Click **Add folder**, and add the `web-js/css` and `web-js/js` folders.
4. Click **Upload**.
5. Open the **Bucket website endpoint** URL from step 3 in a browser. You
   should see the unit converter page, and it should work end-to-end.

## 6. Create an IAM user for automated deployments

The CI/CD pipeline (see `CICD_SETUP.md`) needs its own AWS credentials to
upload files on your behalf, separate from your personal login.

1. In the AWS Console, search for **IAM** and open it.
2. In the left sidebar, click **Users**, then **Create user**.
3. Name it something like `github-actions-deployer`. Do **not** check "Provide
   user access to the AWS Management Console" — this user only needs
   programmatic access, not console login.
4. Click **Next**. Choose **Attach policies directly**, then search for and
   select **AmazonS3FullAccess** (simplest to set up; a peer-review follow-up
   in `NEXT_STEPS.md` covers scoping this down to just the one bucket).
5. Click **Next**, then **Create user**.
6. Click into the new user, go to the **Security credentials** tab, scroll
   to **Access keys**, and click **Create access key**.
7. Choose **Third-party service** as the use case, acknowledge the warning,
   and click **Next**, then **Create access key**.
8. AWS shows you an **Access key ID** and a **Secret access key**. Copy both
   somewhere safe right now — this is the only time the secret key is shown.
   You'll paste these into GitHub in `CICD_SETUP.md`.

## What's next

- [`CICD_SETUP.md`](CICD_SETUP.md) — connect GitHub to this bucket so every
  push to `main` automatically re-deploys the site
