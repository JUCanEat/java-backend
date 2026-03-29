<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Sign up</title>

    <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css" />
</head>

<body>

<div class="page">
    <div class="card register">

        <h1 class="title">Sign up</h1>

        <#if message?has_content>
            <div class="error">
                ${message.summary}
            </div>
        </#if>

        <form action="${url.registrationAction}" method="post">

            <div class="field">
                <label>First name</label>
                <input type="text" name="firstName" required />
            </div>

            <div class="field">
                <label>Last name</label>
                <input type="text" name="lastName" required />
            </div>

            <div class="field">
                <label>Email</label>
                <input type="email" name="email" required />
            </div>

            <input type="hidden" name="username" />

            <div class="field">
                <label>Password</label>
                <input type="password" name="password" required />
            </div>

            <div class="field">
                <label>Confirm password</label>
                <input type="password" name="password-confirm" required />
            </div>

            <button type="submit" class="btn primary">
                Sign up
            </button>

        </form>

        <a class="back-link" href="${url.loginUrl}">
            Back to login
        </a>

    </div>
</div>

</body>
</html>
