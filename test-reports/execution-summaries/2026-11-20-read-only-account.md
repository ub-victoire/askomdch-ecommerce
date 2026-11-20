# Read-only account checks — 2026-11-20

**Target:** https://askomdch.com/

**Completed:** 2026-11-20 09:48:14 +02:00

Six Cucumber scenarios and 18 steps passed (Maven exit code 0). These checks did not submit account forms, send recovery emails, create accounts, or place orders.

| Jira Test | Observed scope |
|---|---|
| [AEC-48](../../automation/features/account-management/account-forms.feature) | Four scenarios checked visibility of the registration controls. |
| [AEC-89](../../automation/features/account-management/account-forms.feature) | One scenario checked visibility of login controls. |
| [AEC-123](../../automation/features/account-management/recovery-form.feature) | One scenario checked the recovery instruction and controls. |

These are scoped automation checks, not complete Xray execution results. No Jira results were changed.

Separately, a browser inspection filled the registration email field with the synthetic value `invalid-email-without-at-sign` without submitting the form. The browser reported the field invalid and returned `Please include an '@' in the email address. 'invalid-email-without-at-sign' is missing an '@'.` AEC-5 specifies `Please include @ in the email address`. Browser-native wording can depend on browser and locale. This is a wording discrepancy to clarify, not a complete execution of AEC-45.

```sh
mvn -B -f automation/pom.xml -P e2e -DbaseUrl=https://askomdch.com \
  -Dwebdriver.chrome.driver=/home/victoire/.cache/selenium/chromedriver/linux64/157.0.8103.78/chromedriver \
  -Dtest=TestConfigTest '-Dcucumber.filter.tags=@AEC-48 or @AEC-89 or @AEC-123' verify
```

The local driver path is specific to the machine used. Selenium emitted a Chrome DevTools Protocol version warning; these checks did not use DevTools features. Generated Failsafe reports are under the ignored `automation/target/` directory and may be removed by a later clean build.
