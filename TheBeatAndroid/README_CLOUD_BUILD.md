# How to Get Your APK (Cloud Build)

Since you don't have Android Studio, we will use **GitHub** to build the app for you. It's free and automatic.

## Step 1: Create a GitHub Repository
1.  Go to [GitHub.com](https://github.com/) and sign up or log in.
2.  Click the **+** icon in the top right and select **New repository**.
3.  Name it `TheBeat-App`.
4.  Make sure it is **Public** (or Private, both work).
5.  Click **Create repository**.

## Step 2: Upload the Code
1.  Click the link that says **"uploading an existing file"** (under the Quick setup box).
2.  Drag and drop all the files from inside your `TheBeatAndroid` folder into the browser window.
    *   **Important:** You might need to drag folders in groups if there are too many files.
    *   Ensure `.github/workflows/build.yml` is uploaded!
3.  Commit the changes (Click "Commit changes").

## Step 3: Download Your App
1.  Once the files are uploaded, click on the **Actions** tab at the top of your repository page.
2.  You should see a workflow named "Build Android APK" running (yellow circle).
3.  Wait for it to turn green (Success).
4.  Click on the workflow run (e.g., "Initial commit").
5.  Scroll down to the **Artifacts** section.
6.  Click on **TheBeat-App** to download your APK file!
7.  Transfer this file to your phone and install it.
