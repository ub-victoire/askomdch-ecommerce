# User-reported checkout observations — 2026-11-20

**Source:** User's descriptions during a guided inspection of https://askomdch.com/. The referenced `image.png` files were not available to independently inspect. These are reported observations, not complete Xray results or independent verification.

The user reported adding a product, proceeding from the cart to checkout, and placing an order as a guest. An order-confirmation page was reported. No order URL, key, personal data, order values, or credentials are recorded here. No additional order was requested during the guided inspection.

## Checkout controls

| Area | User-reported observations | Jira context |
|---|---|---|
| Billing | Required: First name, Last name, Country / Region, Street address, Town / City, State, ZIP Code, Email address. Company name and Phone are labelled optional. Street address has two fields. | [AEC-23](../../requirements/epics-overview.md#aec-23) requires a non-empty phone; the optional label needs reconciliation. Label alone does not establish server-side validation. |
| Alternative shipping | Selecting “Ship to a different address?” revealed required First name, Last name, Country / Region, Street address, Town / City, State / County and Postcode / ZIP fields, plus optional Company name. “Order notes” was labelled optional. | [AEC-21](../../requirements/epics-overview.md#aec-21); actual validation, maximum lengths, collapse behavior and saved address were not verified. |
| Account creation | Selecting “Create an account?” revealed required “Account username” and “Create account password” fields. | [AEC-20](../../requirements/epics-overview.md#aec-20) describes account creation using billing email as the username/login identifier. Clarify how the additional required username field relates to that criterion; account creation was not verified. |
| Returning customer | Expanded section showed “Username or email *”, “Password”, “Remember me” and “Lost your password?”. | [AEC-17](../../requirements/epics-overview.md#aec-17); no login, error, session or autofill behavior was checked. |
| Coupon | Expanding “Have a coupon? Click here to enter your code” displayed a code field. | AEC-115 has no exported Story link and coupon scope remains unresolved in [traceability](../../requirements/requirements-traceability.md#traceability-gaps-and-concerns). No coupon was applied and no rejection message was observed. |
| Payment | “Direct bank transfer” was reported selected by default; “Cash on delivery” was also offered. The user reported the notice “TEST MODE ENABLED. DO NOT ENTER YOUR PERSONAL DATA. NO ORDER WILL BE FULLFILLED.” | [AEC-23](../../requirements/epics-overview.md#aec-23). The notice is not independent confirmation of authorization, isolation, payment safety, or cleanup. Payment switching and fulfillment were not verified. |
| Order review | Reported labels: “Your order”, “Product”, “Subtotal”, “Shipping”, “Flat rate”, “CA State Tax” and “Total”. | [AEC-22](../../requirements/epics-overview.md#aec-22); values, calculations and line-item formatting were not checked. |
| Confirmation | The user reported the label “Your order” on the confirmation page. | [AEC-22](../../requirements/epics-overview.md#aec-22); other confirmation details, inventory changes and order persistence remain unverified. |

## User-reported non-production checks

The user identified `https://test.clock.com` as a separate authorized AskOmDch test environment and stated that test accounts and orders are permitted. The cleanup process is unknown. Its identity, authorization, data isolation and behavior have not been independently verified.

- The user reported that the checkout labels matched the public-site labels listed above.
- Switching between Cash on delivery and Direct bank transfer showed each option's instructions with only one option selected.
- The alternative-shipping and account-creation fields appeared when their respective controls were checked and disappeared when unchecked.
- The returning-customer section opened to display fields and collapsed when the link was selected again.
- The user reported that the create-account password input had neither `minlength` nor `maxlength` HTML attributes. This does not establish whether client-side scripts or server-side validation enforce any password rules.

These are user-reported control checks, not complete Xray outcomes. No account or order submission was requested for these checks.

These observations must not change Jira relationships or mark Tests passed. Confirm actual field validation and order behavior only in an explicitly authorized non-production environment with synthetic fixtures and an agreed cleanup process. See the separate [read-only site comparison](2026-11-20-site-jira-comparison.md) for browsing observations and existing gaps.
