<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Login</title>

  <link rel="stylesheet" href="${url.resourcesPath}/css/styles.css" />
</head>

<body>

<div class="page">
  <div class="card">

    <h1 class="title">Login</h1>

    <#if message?has_content>
      <div class="error">
        ${message.summary}
      </div>
    </#if>

    <form action="${url.loginAction}" method="post">

      <div class="field">
        <label>Email</label>
        <input
                type="text"
                name="username"
                placeholder="m@example.com"
                autocomplete="username"
                required
        />
      </div>

      <div class="field">
        <div class="password-row">
          <label>Password</label>

          <#if realm.resetPasswordAllowed>
            <a class="forgot" href="${url.loginResetCredentialsUrl}">
              Forgot your password?
            </a>
          </#if>
        </div>

        <input
                type="password"
                name="password"
                autocomplete="current-password"
                required
        />
      </div>

      <div class="login-actions">
        <button type="submit" class="btn primary">
          Login
        </button>
      </div>

    </form>

    <div class="login-secondary">

      <#if social.providers??>
        <#list social.providers as p>
          <a class="btn outline" href="${p.loginUrl}">
            Continue with ${p.displayName}
          </a>
        </#list>
      </#if>

      <#if realm.registrationAllowed>
        <a class="btn outline signup" href="${url.registrationUrl}">
          Sign up
        </a>
      </#if>

    </div>

  </div>
</div>

</body>
</html>
