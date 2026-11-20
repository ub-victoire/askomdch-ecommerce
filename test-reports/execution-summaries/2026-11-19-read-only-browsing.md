# Read-only browsing checks — 2026-11-19

**Target:** https://askomdch.com/

**Completed:** 2026-11-19 15:49:02 +02:00

**Scope:** Partial AEC-119 and AEC-133 checks only; no account, checkout, or order submission.

| Check | Result | Scope |
|---|---|---|
| [AEC-119](../../automation/features/browsing-products/product-sorting.feature) | 2 scenarios passed | Sorting options and both price orders within the Men category. |
| [AEC-133](../../automation/features/browsing-products/product-filtering.feature) | 3 scenarios passed | Men, Women and Accessories header links lead to matching category headings. |

The five browser scenarios and 25 steps passed (Maven exit code 0). This does **not** establish a complete Xray result for either Jira Test: other sort modes, category contents, price filtering, and other entry points remain unverified. No Jira results were changed.

Command run from the repository root:

```sh
mvn -B -f automation/pom.xml -P e2e -Dtest=TestConfigTest -Dit.test=RunCucumberIT '-Dcucumber.filter.tags=@AEC-119 or @AEC-133' -DbaseUrl=https://askomdch.com -Dwebdriver.chrome.driver=/home/victoire/.cache/selenium/chromedriver/linux64/157.0.8103.78/chromedriver verify
```

The generated Failsafe report was in the ignored `automation/target/failsafe-reports/` directory and was removed by a later clean build; the raw report is no longer available. Selenium emitted a Chrome DevTools Protocol version warning; the checks did not use DevTools features.
