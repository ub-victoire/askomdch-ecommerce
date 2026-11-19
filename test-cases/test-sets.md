# Jira Test Sets

All eight Test Set issues are listed here. A parent key establishes only the exported parent relationship. The CSV contains no Test-to-Test-Set membership or Test-Set-to-Test-Execution links; no membership is inferred from a name, description, or parent.

<a id="aec-27"></a>
## AEC-27 — Cart Management Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Jira labels:** `cart`<br>
**Parent key:** AEC-3<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

_Description is empty in the CSV._

<a id="aec-63"></a>
## AEC-63 — Checkout Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** AEC-4<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

Contains functional tests for checkout workflows, including returning-customer authentication during checkout, billing and shipping details, guest and registered checkout, order review, payment selection and order placement.

<a id="aec-113"></a>
## AEC-113 — Smoke Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** AEC-4<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

**Purpose:**
The Smoke Test Set contains a selected group of high-priority test cases designed to verify that the essential functionalities of the AskOmDch e-commerce application are working correctly after a new build or deployment.

**Scope:**
The test cases cover the most critical customer journeys, including:

- Customer registration and login.
- Product browsing and search.
- Adding products to the cart.
- Reviewing and updating cart contents.
- Accessing checkout.
- Returning-customer login during checkout.
- Reviewing order details and selecting a payment method.
- Completing an order and receiving confirmation.

**Selection Criteria:**
Only critical and high-priority test cases that validate the application's core functionality and allow further testing to proceed should be included.

**Expected Outcome:**
The Smoke Test Set should provide an early indication of whether the application is stable enough for more extensive functional testing. Any critical failure should be investigated before proceeding with further testing.

> **Traceability note:** This Test Set is parented to AEC-4 (Checkout), while its description describes coverage across the four functional Epics. Confirm intended Jira hierarchy; it is not changed here.

<a id="aec-116"></a>
## AEC-116 — Regression Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** AEC-4<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

**Purpose:**
The Regression Test Set contains previously designed and validated test cases used to verify that existing AskOmDch e-commerce functionalities continue to work correctly after application changes, bug fixes, enhancements or new deployments.

**Scope:**
The test cases cover the four main functional areas:

- Account Management.
- Product Browsing.
- Cart Management.
- Checkout.

**Selection Criteria:**
Test cases are selected based on the risk of regression, business importance, dependencies between functionalities and the potential impact of changes on existing features.

Priority should be given to critical customer journeys, previously identified defects and functionalities that may be affected by code changes.

**Expected Outcome:**
The Regression Test Set should help identify unintended side effects caused by application changes and provide confidence that previously working functionalities remain stable.

> **Traceability note:** This Test Set is parented to AEC-4 (Checkout), while its description describes coverage across the four functional Epics. Confirm intended Jira hierarchy; it is not changed here.

<a id="aec-137"></a>
## AEC-137 — Account Management Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** None exported<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

This is for grouping test cases under the Account Management Epic

> **Traceability note:** No Parent key is exported for this Test Set.

<a id="aec-139"></a>
## AEC-139 — Product Browsing Test Set

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** None exported<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

_Description is empty in the CSV._

> **Traceability note:** No Parent key is exported for this Test Set.

<a id="aec-155"></a>
## AEC-155 — Smoke Test Set for Product Browsing

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** AEC-2<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

Verify that the core Product Browsing functionalities are working as expected by performing smoke tests on product searching, viewing product details, sorting products, and filtering products by category and price. The objective is to confirm that customers can successfully navigate and interact with the store's main product browsing features and that the functionality is stable enough for further testing.

<a id="aec-162"></a>
## AEC-162 — Regression Test for Product Browsing

**Jira status:** To Do (not an execution result)<br>
**Priority:** Medium<br>
**Parent key:** AEC-2<br>
**Linked Tests / Executions:** no explicit membership relationship exported.

Verify that existing Product Browsing functionalities, including product searching, viewing product details, sorting, and category and price filtering, continue to work correctly after application changes or bug fixes.
