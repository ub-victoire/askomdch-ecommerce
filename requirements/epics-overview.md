# Epics and Stories

This document records all four Epic descriptions and all 15 Story descriptions from the Jira export. Jira markup has been rendered as Markdown for readability; source wording and acceptance-criteria messages are retained. Jira attachment references are named but their binary files are not included.

Story parent keys and Test links below are the relationships present in the CSV. See [requirements traceability](requirements-traceability.md) for a relationship matrix and known gaps.

## Epic index

- [AEC-1 — Account Management](#aec-1) — Stories: AEC-5, AEC-6, AEC-8.
- [AEC-2 — Browsing Products](#aec-2) — Stories: AEC-11, AEC-14, AEC-15, AEC-16.
- [AEC-3 — Cart Management](#aec-3) — Stories: AEC-7, AEC-10, AEC-13.
- [AEC-4 — Checkout](#aec-4) — Stories: AEC-17, AEC-20, AEC-21, AEC-22, AEC-23.

## Epic descriptions

<a id="aec-1"></a>
### AEC-1 — Account Management

**Jira priority:** High

**Description:** Provide users with self-service identity management, order review, address persistence, and credential security tools to manage their relationship with the store.

**In Scope:**
- Account registration with email and password.
- Account login via username or email address.
- Lost password retrieval and reset workflows.
- Dashboard view displaying recent orders and order receipts.
- Managing and saving default billing and shipping addresses.
- Updating display name, email, and modifying existing passwords.
**Out of Scope:**
- Single Sign-On (SSO) via third-party social providers (Google, Apple, Facebook).
- Two-Factor Authentication (2FA / SMS OTP).
- Multi-user corporate role hierarchies or sub-accounts.
- Automatic account deletion (GDPR "Right to be Forgotten" self-service button).

<a id="aec-2"></a>
### AEC-2 — Browsing Products

**Jira priority:** Highest

**Description:** Provide shoppers with catalog navigation, search, and filtering mechanisms to discover products, view details, and verify pricing and availability before adding items to their cart.

In Scope

- Store catalog grid at `/store`.
- Taxonomy filtering by category (Men, Women, Accessories).
- Price range filter slider.
- Keyword search input and result display.
- Catalog sorting options.
- Single product specification view.

### Out of Scope

- Side-by-side product attribute comparison.
- Out-of-stock back-in-stock notification subscriptions.
- 3D/AR product model visualizers.

<a id="aec-3"></a>
### AEC-3 — Cart Management

**Jira priority:** Highest

**Description:** Provide users with real-time control over their staged items, quantity modifications, coupon code redemptions, and order subtotal calculations prior to entering checkout.

### In Scope

- Adding items to cart from store and single product pages.
- Line item quantity increments, decrements, and item deletion.
- Cart subtotal and line item total recalculations.
- Empty cart state presentation with store redirect.

### Out of Scope

- Multi-currency switcher.
- Wishlist / Save-for-later functionality.
- Automated bundle discounts without coupons.
- Promotional coupon code application and removal.

<a id="aec-4"></a>
### AEC-4 — Checkout

**Jira priority:** Highest

**Description:** Guide customers through entering billing and shipping details, reviewing line items, selecting payment options, and submitting transactions securely to generate an order invoice.

### In Scope

- Single-page checkout at `/checkout`.
- Customer billing details collection and input validation.
- Optional secondary shipping address fields.
- Payment method radio selection and gateway instructions.
- Order placement and transition to `/checkout/order-received/`.

### Out of Scope

- Multi-step paginated wizard checkout.
- Third-party address autocomplete lookups.
- Cryptocurrency transactions.

## Stories and original acceptance criteria

<a id="aec-5"></a>
### AEC-5 — Registration

**Parent Epic:** AEC-1 — Account Management<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-40](../test-cases/account-management/AEC-40.md), [AEC-42](../test-cases/account-management/AEC-42.md), [AEC-44](../test-cases/account-management/AEC-44.md), [AEC-45](../test-cases/account-management/AEC-45.md), [AEC-48](../test-cases/account-management/AEC-48.md), [AEC-49](../test-cases/account-management/AEC-49.md), [AEC-51](../test-cases/account-management/AEC-51.md), [AEC-54](../test-cases/account-management/AEC-54.md), [AEC-57](../test-cases/account-management/AEC-57.md), [AEC-58](../test-cases/account-management/AEC-58.md), [AEC-60](../test-cases/account-management/AEC-60.md)

Description:

As a Customer, I want to create an account
so that I can keep a record of my orders.

**Acceptance Criteria:**

- Registration Inputs:
- The Account page displays Username, Email address, and Password inputs
- A “REGISTER” button is displayed
- The registration section displays “TEST MODE ENABLED. DO NOT ENTER YOUR PERSONAL DATA.”
- Username
  - should be one word
  - should only be alphabetical letters
  - not empty
  - 5 chars minimum, 10 maximum
  - unique/not in use in the system
- Email Address:
  - should fulfill the email format (regex)
  - unique/not in use
  - not empty
  - valid email/email exists
- Password:
  - minimum of 8 chars, maximum 15 chars
  - should have special characters and numbers
  - should mix upper cases and lower case letters
  - username should not be in the password (security)
- Clicking “REGISTER” button:
  - If inputs are invalid:
    - username:
      - Customer stays on the same page.
      - System displays error message “Please enter a valid account username”
    - email:
      - Customer stays on the same page
      - System displays error message “Please provide a valid email address” 3. if email is not formatted correctly, error message “Please include @ in the email address” should be displayed.
    - Password:
      - Customer stays on the same page
      - if empty, system displays “Please enter an account password” 3. if formatted incorrectly, “Please enter a valid password”.
    - With Duplicate Details:
- An existing username displays “Error: An account is already registered with that username. Please choose another.”
- An existing email displays “Error: An account is already registered with your email address. Please log in.”
- The duplicate registration is rejected
    - Successful Registration:
- System creates the account
- Customer is automatically logged in
- The account dashboard displays “Hello” followed by the username
- Account navigation includes Dashboard, Orders, Downloads, Addresses, Account details, and Logout

[Jira attachment reference: `image-20261106-080149.png`; binary not included.]

<a id="aec-6"></a>
### AEC-6 — Log In

**Parent Epic:** AEC-1 — Account Management<br>
**Jira priority:** High<br>
**Linked Tests:** [AEC-89](../test-cases/account-management/AEC-89.md), [AEC-90](../test-cases/account-management/AEC-90.md), [AEC-91](../test-cases/account-management/AEC-91.md), [AEC-92](../test-cases/account-management/AEC-92.md), [AEC-93](../test-cases/account-management/AEC-93.md), [AEC-94](../test-cases/account-management/AEC-94.md), [AEC-95](../test-cases/account-management/AEC-95.md), [AEC-96](../test-cases/account-management/AEC-96.md), [AEC-97](../test-cases/account-management/AEC-97.md), [AEC-98](../test-cases/account-management/AEC-98.md)

As customer
I want to log in with my username or email address and password
so that i can access my account, track my orders, and check out faster.

**Acceptance Criteria:**

Username:

- must match a registered username registered on an account.
- should not be empty
- leading and trailing spaces are ignored
- username matching is case insensitive

Password :

- must match the password stored for the matched account.
- should not be empty

Clicking "Log in":

- if the username/email is empty, the customer stays on the account page and sees “Username is required.”
- if the password is empty, the customer stays on the account page and sees "The password field is empty."
- if the username is not registered, the customer stays on the account page and sees "The username {username} is not registered on this site. If you are unsure of your username, try your email address instead."
- if the password is incorrect, the customer stays on the account page and sees "The password you entered for the username {username} is incorrect." together with a "Lost your password?" link.
- if the credentials are valid, the customer is redirected to the My Account dashboard and sees "Hello {username}".

Remember me:

   1.When checked, the login session survives closing and reopening the browser until the Customer logs out or the session cookie expires.

   2.When unchecked, the session ends when the browser is closed.

Session handling

- a logged-in customer who opens /account/ sees the My Account dashboard, not the Login form.

    2.The password is never displayed on screen, placed in the URL, or returned in plain text by the API.

<a id="aec-7"></a>
### AEC-7 — Add Product to Cart

**Parent Epic:** AEC-3 — Cart Management<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-28](../test-cases/cart-management/AEC-28.md)

As a Customer,

I want to add items to my shopping cart

so that I can gather multiple products to purchase together.

**Acceptance Criteria:**

- Supported Pages:
  - Home Page (`/`): Featured / Latest product cards
  - Store Catalog Page (`/store/`): Product catalog grid
  - Category Archive Pages (`/product-category/{category-slug}/`, e.g., `/product-category/men/`, `/product-category/women/`, `/product-category/accessories/`): Filtered category grids
  - Search Results Page (`/store/?s={query}&post_type=product`): Query search result cards
  - Single Product Details Page (`/product/{product-slug}/`): Dedicated product specification view
  - Unsupported Pages: Cart page (`/cart/`), Checkout page (`/checkout/`), and My Account pages (`/my-account/`) do not support direct "Add to Cart" product actions
- Quantity Input:
  - Catalog & Archive Grids (Home, Store, Category Archives, Search Results):
    - does not display a quantity input box
    - clicking “Add to cart” adds a default quantity of 1 unit per click
  - Single product page( `/product/{product-slug}/`):
    - numeric digits only
    - not empty
    - minimum value of 1
    - default value is 1
    - maximum value should not exceed available inventory stock
- Product Stock Status:
  - product must be in stock
  - if out of stock, "ADD TO CART" button is disabled and displays "Out of stock"
- Clicking “ADD TO CART” button:
  - If inputs are valid and product is in stock:
    - product is added to the customer's cart session (1 unit)
    - button state changes temporarily to loading/processing
    - system displays a checkmark/tick icon inside or next to the “Add to cart” button
    - system displays a “View cart” link placed below or adjacent to the button
    - header cart count and subtotal increment dynamically
  - If inputs are invalid:
    - Customer stays on the same page.
    - Quantity is less than 1, system displays error message: ”Value must be greater than or equal to 1”
    - if quantity exceeds available stock, system displays error notice: “You cannot add that amount to the cart — we have {stock_qty} in stock and you already have {cart_qty} in your cart.”

**Screenshot:**

[Jira attachment reference: `image-20261106-082822.png`; binary not included.]

<a id="aec-8"></a>
### AEC-8 — Account Recovery

**Parent Epic:** AEC-1 — Account Management<br>
**Jira priority:** Medium<br>
**Linked Tests:** [AEC-123](../test-cases/account-management/AEC-123.md), [AEC-124](../test-cases/account-management/AEC-124.md), [AEC-125](../test-cases/account-management/AEC-125.md), [AEC-126](../test-cases/account-management/AEC-126.md), [AEC-127](../test-cases/account-management/AEC-127.md), [AEC-130](../test-cases/account-management/AEC-130.md), [AEC-131](../test-cases/account-management/AEC-131.md)

As a Customer,

I want to reset my lost password

so that I can regain access to my account and order history.

**Acceptance Criteria:**

- Username or Email Address:
- not empty
- accepts valid registered username (alphabetical letters, one word, 5 to 10 characters)
- accepts valid registered email address (standard email format)
- must exist and be registered in the system
- Clicking “RESET PASSWORD” button:
- If input is valid and registered:
- System sends a password reset link to the email address associated with the account.
ii. Customer is redirected or stays on the page with a confirmation notice: “Password reset email has been sent.”
- If input is invalid:
- field is empty:
  - Customer stays on the same page.
  - System displays error message: “Enter a username or email address.”
ii. username is invalid or not found:
  - Customer stays on the same page.
  - System displays error message: “Invalid username or email.”
iii. email address is invalid or not registered:
  - Customer stays on the same page.
  - System displays error message: “There is no account with that username or email address.”

[Jira attachment reference: `image-20261106-090021.png`; binary not included.]

<a id="aec-10"></a>
### AEC-10 — Update Cart details

**Parent Epic:** AEC-3 — Cart Management<br>
**Jira priority:** High<br>
**Linked Tests:** [AEC-32](../test-cases/cart-management/AEC-32.md), [AEC-34](../test-cases/cart-management/AEC-34.md), [AEC-85](../test-cases/cart-management/AEC-85.md), [AEC-108](../test-cases/cart-management/AEC-108.md)

As a Customer,

I want to change item quantities products on the cart page

so that my cart contains the exact units and items I wish to buy.

**Acceptance Criteria:**

- Quantity fields:
  - accepts positive numerical integers only
  - not empty
  - minimum value of 1
  - maximum value capped at available product stock
- Clicking “UPDATE CART” button:
  - If quantity inputs are valid:
    - recalculates line totals (`Quantity * Unit Price`)
    - recalculates cart subtotal and grand total
    - Customer stays on `/cart` page
    - system displays message: “Cart updated.”
  - If quantity input is less than 0:
    - Customer stays on `/cart` page
    - system displays error message: Value must be greater than or equal to 0.
  - If quantity input is not a number:
    - Customer stays on `/cart` page
    - system displays error message: Please enter a number
- Empty Cart State:
  - If there’s no items in the cart:
    - cart table and total  box are hidden
    - system displays message: “Your cart is currently empty”
    - system displays a button: “RETURN TO SHOP” linking to /store

**Screenshot:**

As a Customer,

I want to change item quantities products on the cart page

so that my cart contains the exact units and items I wish to buy.

**Acceptance Criteria:**

- Quantity fields:
  - accepts positive numerical integers only
  - not empty
  - minimum value of 1
  - maximum value capped at available product stock
- Clicking “UPDATE CART” button:
  - If quantity inputs are valid:
    - recalculates line totals (`Quantity * Unit Price`)
    - recalculates cart subtotal and grand total
    - Customer stays on `/cart` page
    - system displays message: “Cart updated.”
  - If quantity input is less than 0:
    - Customer stays on `/cart` page
    - system displays error message: Value must be greater than or equal to 0.
  - If quantity input is not a number:
    - Customer stays on `/cart` page
    - system displays error message: Please enter a number
- Empty Cart State:
  - If there’s no items in the cart:
    - cart table and total  box are hidden
    - system displays message: “Your cart is currently empty”
    - system displays a button: “RETURN TO SHOP” linking to /store

**Screenshot:**

[Jira attachment reference: `image-20261106-090515.png`; binary not included.]

[Jira attachment reference: `image-20261106-090531.png`; binary not included.]

> **Source note:** The Jira description repeats its acceptance-criteria text. Both copies are retained here rather than silently deduplicated.

<a id="aec-11"></a>
### AEC-11 — Product Filtering

**Parent Epic:** AEC-2 — Browsing Products<br>
**Jira priority:** High<br>
**Linked Tests:** [AEC-133](../test-cases/browsing-products/AEC-133.md)

As a Customer,

I want to filter products by category and dynamic price ranges across the store

so that I can easily discover items tailored to my specific preferences and budget.

**Acceptance Criteria:**

- Category Filtering Entry Points & Navigation Links:
  - Header Navigation:
    - header menu displays direct category links: “Men”, “Women”, and “Accessories”
  - Store page slider / Browser by Category:
    - displays a “Browse by category” or “Categories” list widget
    - lists available categories with product counts (e.g., Accessories, Men, Women)
    - clicking any category filters the product catalog to that category view
  - Footer Links:
    - footer section contains direct navigational links to main shopping categories
    - clicking a footer category link redirects to that specific category page
- Dynamic Price Filter Slider(Filter by Price):
  - DEFAULT Range on all products:
    - dual-handle range slider spans from the lowest product price to the highest product price across the entire store inventory
    - current selected range displays formatted with currency: “Price: ${min_price} —${max_price}”
  - Dynamic Range Adjustment by category:
    - when navigating to a specific category page (e.g., Accessories), slider minimum resets to the lowest-priced item in that category
    - slider maximum resets to the highest-priced item in that category
    - slider limits update dynamically based on the active category's inventory bounds
  - Handle Manipulation:
    - left handle controls the minimum price threshold; cannot cross over the right handle
    - right handle controls the maximum price threshold; cannot cross under the left handle
    - moving either handle updates the displayed text labels in real-time
- Submitting the Filter (Clicking “FILTER” Button):
  - If matching products exist within the selected range:
    - page reloads/updates retaining the current category URL with price query parameters
    - catalog grid displays only products belonging to the selected category whose prices fall between `${min_price`} and `${max_price`}
    - slider handles and numeric labels retain their submitted positions
    - pagination resets to Page 1 if multiple pages exist
  - If no products fall within the selected price range:
    - catalog grid is cleared
    - system displays message: “No products were found matching your selection.”
    - filter sidebar remains visible and operable so the customer can adjust or reset values
- Combined Filtering & Reset Behavior:
  - switching to another category clears previous custom price parameters and initializes to the new category's full price bounds
  - changing sort order (e.g., "Sort by price: low to high") preserves the currently applied category and price filter parameters

**Screenshot:**

[Jira attachment reference: `image-20261106-092100.png`; binary not included.]

<a id="aec-13"></a>
### AEC-13 — View cart details

**Parent Epic:** AEC-3 — Cart Management<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-108](../test-cases/cart-management/AEC-108.md), [AEC-112](../test-cases/cart-management/AEC-112.md)

As a Customer,

I want to view my shopping cart details on the cart page

so that I can review all selected products, verify item prices, and check the calculated totals before proceeding to checkout.

**Acceptance Criteria:**

- Cart Page access:
  - accessible by navigating to `/cart/` directly or clicking the cart icon in the header
  - accessible via the “View cart” link below the ADD TO CART button shown after clicking ADD TO CART button
  - accessible by navigating on footer link named ”Cart”
  - accessible by clicking VIEW CART button on the cart icon in the header when you hover to it.
- Cart Table display:
  - displays table headers: empty column (for item removal), empty column (for product thumbnail), Product, Price, Quantity, Subtotal
  - Product column displays product thumbnail image and title as a clickable link leading to the product details page
  - Price column displays unit price formatted with the currency symbol (`$`)
  - Quantity column displays an editable numerical input box pre-filled with the current quantity
  - Subtotal column displays calculated item total (`unit price * quantity`) formatted with the currency symbol (`$`)
- Cart Actions SECTION:
  - displays a coupon input field with placeholder text “Coupon code” alongside an “APPLY COUPON” button
  - displays an “UPDATE CART” button
  - “UPDATE CART” button is disabled by default and enables when any quantity value is modified
- Cart total summary section:
  - displays header “Cart totals”
  - Subtotal row displays the sum of all line item subtotals
  - Shipping row displays shipping options/rates or a shipping calculator link
  - Total row displays final payable amount including discounts or shipping charges
  - displays a prominent button labeled “PROCEED TO CHECKOUT”
- Clicking “PROCEED TO CHECKOUT” button:
  - redirects customer directly to the checkout page at `/checkout/`
- Empty Cart state:
  - if the cart has no items:
    - cart table, coupon section, and cart totals panel are hidden
    - system displays notification message: “Your cart is currently empty.”
    - system displays a button: “RETURN TO SHOP” linking to `/store`

**Screenshot:**

[Jira attachment reference: `image-20261106-092402.png`; binary not included.]

<a id="aec-14"></a>
### AEC-14 — Product Sorting

**Parent Epic:** AEC-2 — Browsing Products<br>
**Jira priority:** High<br>
**Linked Tests:** [AEC-119](../test-cases/browsing-products/AEC-119.md)

As a Customer,
I want to sort the catalog results
so that I can view products in my preferred order.

Acceptance Criteria

Sort Dropdown

- The default option is “Default sorting”.
- The following sorting options are available:
- Sort by popularity
- Sort by average rating
- Sort by latest
- Sort by price: low to high
- Sort by price: high to low
- Selecting a sorting option reorders the product grid.
- Selecting a sorting option does not clear the active category filter.

**Screenshot:**

[Jira attachment reference: `image-20261106-100904.png`; binary not included.]

<a id="aec-15"></a>
### AEC-15 — Product Searching

**Parent Epic:** AEC-2 — Browsing Products<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-18](../test-cases/browsing-products/AEC-18.md)

As a Shopper,
I want to search for products by keyword
so that I can find a specific item without browsing the whole catalog.

Acceptance Criteria

Search entry

- The Store page displays a search field labelled “Search product:”.
- The Store page displays a “Search” button.
- The Shopper can enter a keyword in the search field.
- Submitting a non-empty query opens the search-results page.

Search results

- The search-results page displays the submitted query in the heading:
 Search results for “{query}”
- The page displays the number of matching products in the format:
 Showing all {n} results
- Searching for  shirt  returns exactly two products:
- Blue Tshirt
- Green Tshirt
- Search matching is case-insensitive.
- Searching for  shirt ,  SHIRT , or  ShIrT  returns the same matching products.
- The search checks product names and product descriptions.
- Each result uses the same product-card format as the Store page.
- Each result provides the same “Add to cart” action as the Store page.
- The Shopper can select a product from the results to open its product-detail page.
- The Shopper can add a result to the cart using its “Add to cart” action.

No results

- When the query does not match any product:
- The search-results page is displayed.
- The page identifies the submitted query.
- No matching product cards are displayed.
- The system displays:
“No products were found matching your selection.”
- The Shopper can return to the Store page.
- The Shopper can edit the search query and submit another search.

Empty query

- Submitting an empty query does not display an error page.
- The system does not display keyword-specific matching results for an empty query.

Special characters and long queries

- Submitting a query containing special characters does not break the search-results page.
- Submitting a long query does not break the search-results page.
- The system handles both queries without displaying an application or server error.

[Jira attachment reference: `image-20261106-093329.png`; binary not included.]

<a id="aec-16"></a>
### AEC-16 — View Product Details

**Parent Epic:** AEC-2 — Browsing Products<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-110](../test-cases/browsing-products/AEC-110.md), [AEC-81](../test-cases/browsing-products/AEC-81.md)

As a Customer,
I want to view the details of a product and add a chosen quantity to my cart
so that I can evaluate the product before deciding whether to purchase it.

**Acceptance Criteria**

Opening a product

- Selecting a product from the catalog or search results opens its product-detail page.
- The page displays a breadcrumb containing:
- Home
- The product category
- The product name

Product information

The product-detail page displays:

- Product image
- Product name
- Product price
- Product description
- Product category
- Product tags assigned to the selected product
- Product weight in the Additional information section
- Product review section
- Current review count

Product tags

- If the selected product has assigned tags, the page displays each assigned tag.
- If the selected product has no assigned tags, the page does not display an empty Tags section or placeholder.
- The displayed tags correspond to the selected product and are not copied from another product.

Product image

- Selecting the product image opens the product image for viewing.
- Hovering over the product image on the catalog or search-results page enlarges or brings the image forward.
- Moving the pointer away returns the image to its normal appearance.
- Hovering over the product image displays a search/zoom icon.
- Selecting the search/zoom icon opens the selected image in an enlarged full-screen view.
- The Customer can close the enlarged view and return to the product listing page.

Product description

- The page provides a Description section.
- The section displays the description associated with the selected product.

Additional information

- The page provides an Additional information section.
- The section displays the selected product’s available information, including its weight.

Quantity and Add to cart

- The product-detail page displays a quantity control.
- The quantity control allows the Customer to enter or select the quantity to add.
- The Customer can click “Add to cart.”
- Clicking “Add to cart” adds the selected product to the cart using the chosen quantity.
- The cart reflects the selected product and quantity.

Reviews

- The page provides a Reviews section.
- The section displays the current number of reviews.
- If the product has no reviews, the page displays “Reviews (0)”.
- The Customer can access the review form, including a rating selection and comment field where available.

[Jira attachment reference: `image-20261106-095243.png`; binary not included.]

<a id="aec-17"></a>
### AEC-17 — Return Customer Login During Checkout

**Parent Epic:** AEC-4 — Checkout<br>
**Jira priority:** High<br>
**Linked Tests:** [AEC-64](../test-cases/checkout/AEC-64.md), [AEC-69](../test-cases/checkout/AEC-69.md), [AEC-71](../test-cases/checkout/AEC-71.md), [AEC-73](../test-cases/checkout/AEC-73.md), [AEC-79](../test-cases/checkout/AEC-79.md), [AEC-82](../test-cases/checkout/AEC-82.md), [AEC-86](../test-cases/checkout/AEC-86.md), [AEC-99](../test-cases/checkout/AEC-99.md), [AEC-103](../test-cases/checkout/AEC-103.md), [AEC-105](../test-cases/checkout/AEC-105.md), [AEC-109](../test-cases/checkout/AEC-109.md)

As a Customer,

I want to log in directly on the checkout page

so that my billing and shipping information auto-fills without leaving the checkout flow.

**Acceptance Criteria:**

- Login Toggle Banner:
  - displays notice banner at top of `/checkout`: “Returning customer? Click here to login”
  - clicking “Click here to login” toggles open the inline login form container
  - clicking it again collapses the inline login form container
- Username / Email field:
  - accepts registered username or email address
  - not empty
- Password field:
  - accepts registered password that matches entered username or email
  - password should be displayed in dots
  - not empty
- Remember Me Checkbox:
  - optional checkbox labeled “Remember me”
  - when checked, retains login session across browser sessions
- Clicking “LOGIN” button:
  - If credentials are valid:
    - logs the customer into their account
    - reloads or updates checkout page dynamically
    - populates billing and shipping fields with customer’s saved address details
    - hides the “Returning customer?” banner
  - If credentials are invalid:
    - customer remains on the checkout page
    - if username/ email is empty, displays error message: “Error: Username is required”
    - If password is empty, displays error message: “Error: the password field is empyt”
    - If credentials do not much but username or email is registered, displays error message: “Error: the password you entered for the username {username} is incorrect. Lost your password?
    - If username is not registered in the system, displays error message: “The username *ad* is not registered on this site. If you are unsure of your username, try your email address instead.”
    - If email is not registered in the system, displays error message: “Unknown email address. Check again or try your username.”
  - Clicking “Lost your password” link:
    - redirects you to /account/lost-password/
- Screenshot:

[Jira attachment reference: `image-20261106-100339.png`; binary not included.]

<a id="aec-20"></a>
### AEC-20 — Account Creation During Checkout

**Parent Epic:** AEC-4 — Checkout<br>
**Jira priority:** Medium<br>
**Linked Tests:** [AEC-149](../test-cases/checkout/AEC-149.md)

As a Customer,

I want to create an account while completing my checkout

so that my order details and profile are saved for future purchases without a separate registration step.

**Acceptance Criteria:**

- Create Account Checkbox:
  - visible only to non-logged-in (guest) shoppers
  - labeled “Create an account?”
  - unchecked by default
- Account Password Field:
  - hidden when “Create an account?” checkbox is unchecked
  - dynamically unfolds when “Create an account?” is checked
  - minimum 8 characters, maximum 15 characters
  - requires mixed uppercase, lowercase, numbers, and special characters
  - field is mandatory if “create an account is checked
- Processing Order submission:
  - If “Create an account?” is checked and password is valid:
    - system registers a new account using the billing email address as username/login identifier
    - system saves order under the newly generated user ID
    - customer is automatically authenticated upon redirection to `/checkout/order-received/`
  - If “Create an account?” is checked and password is empty or invalid:
    - order is not placed and customer stays on the checkout page
    - if empty, displays error message: “Please enter an account password.”
    - if complexity rules fail, displays error message: “Password must be 8-15 characters and contain uppercase, lowercase, numbers, and special characters.”
    - if billing email is already registered, displays error message: “An account is already registered with your email address. Please log in.”

**Screenshot:**

[Jira attachment reference: `image-20261106-104732.png`; binary not included.]

<a id="aec-21"></a>
### AEC-21 — Alternative Shipping Address

**Parent Epic:** AEC-4 — Checkout<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-75](../test-cases/checkout/AEC-75.md), [AEC-121](../test-cases/checkout/AEC-121.md), [AEC-118](../test-cases/checkout/AEC-118.md), [AEC-117](../test-cases/checkout/AEC-117.md)

As a Customer,

I want to enter a different delivery address from my billing address

so that my order can be delivered to another recipient or destination.

**Acceptance Criteria:**

- Alternative Shipping Toggle:
  - labeled “Ship to a different address?” with a checkbox
  - unchecked by default; shipping destination defaults to billing address
  - clicking the checkbox unfolds the secondary shipping address form
  - unchecking collapses the form and reverts shipping destination back to billing address
- Shipping Form Fields (when checkbox is checked):
  - First Name: alphabetical characters only, mandatory, max 50 chars
  - Last Name: alphabetical characters only, mandatory, max 50 chars
  - Country / Region: mandatory dropdown selection
  - Street Address: alphanumeric characters, mandatory, max 100 chars
  - Town / City: alphabetical characters only, mandatory, max 50 chars
  - State / County: dropdown or text input according to selected country, mandatory where applicable
  - Postcode / ZIP: valid alphanumeric postal format, mandatory
  - Order Notes: optional text area for delivery instructions, max 500 chars
- Form Submission Validation:
  - If all mandatory secondary fields are filled correctly:
    - order submits with secondary address stored as recipient destination
    - `/checkout/order-received/` displays secondary shipping address under “Shipping address” panel
  - If any mandatory secondary field is empty or invalid:
    - Customer stays on the checkout page
    - invalid fields are highlighted with a red outline
    - displays specific error message at the top (e.g., “Shipping First name is a required field.”, “Shipping Street address is a required field.”)

**Screenshot:**

[Jira attachment reference: `image-20261106-105633.png`; binary not included.]

<a id="aec-22"></a>
### AEC-22 — Order review

**Parent Epic:** AEC-4 — Checkout<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-157](../test-cases/checkout/AEC-157.md)

As a Customer,

I want to review my order summary and line items before placing the order

so that I can confirm quantities, price calculations, discounts, and final totals.

**Acceptance Criteria:**

- Order Summary Table Display:
  - displays table titled “Your order”
  - header columns display: “Product” and “Subtotal”
  - each line item displays: product name, multiplier indicator, purchased quantity (`Product Name × Qty`), and calculated row total
- Totals Calculation Breakdown:
  - Subtotal row displays sum of all item subtotals
  - Shipping row displays selected shipping method and shipping fee (or “Free shipping”)
  - State Tax row displays the amount of tax to be paid, if any.
  - Total row displays bold final payable price formatted in store currency (`$`)
- Clicking “PLACE ORDER” button:
  - If order summary is validated and terms are accepted:
    - order is submitted and inventory is deducted
    - customer is redirected to `/checkout/order-received/{order_id}/`
    - page displays order number, date, status, total amount, and item breakdown

**Screenshot:**

[Jira attachment reference: `image-20261106-112340.png`; binary not included.]

<a id="aec-23"></a>
### AEC-23 — Billing Details & Payment method

**Parent Epic:** AEC-4 — Checkout<br>
**Jira priority:** Highest<br>
**Linked Tests:** [AEC-144](../test-cases/checkout/AEC-144.md), [AEC-156](../test-cases/checkout/AEC-156.md), [AEC-153](../test-cases/checkout/AEC-153.md), [AEC-148](../test-cases/checkout/AEC-148.md), [AEC-145](../test-cases/checkout/AEC-145.md)

As a Customer,

I want to enter my billing address and select my preferred payment method

so that my order can be processed and billed correctly.

**Acceptance Criteria:**

- Billing Form Fields:
  - First Name: alphabetical characters only, not empty, maximum 50 characters
  - Last Name: alphabetical characters only, not empty, maximum 50 characters
  - Country / Region: mandatory dropdown selection
  - Street Address: alphanumeric characters, not empty, maximum 100 characters
  - Town / City: alphabetical characters only, not empty, maximum 50 characters
  - State / County: dropdown or text input according to selected country, mandatory where applicable
  - Postcode / ZIP: valid alphanumeric postal format, not empty
  - Phone: numerical characters with optional `+` prefix, not empty, 10 to 15 digits
  - Email Address: valid email format (regex), not empty
- Payment Method selection:
  - displays payment gateway options via radio buttons: “Direct bank transfer” and “Cash on delivery”
  - exactly one payment method must be selected at all times
  - “Direct bank transfer” is selected by default on page load
  - selecting a radio button displays its corresponding instructional message box and collapses the other
- Form validation errors:
  - if any mandatory billing field is empty:
    - Customer remains on `/checkout/`
    - system displays error message at the top: “Billing {Field Name} is a required field.”
    - highlights the missing field with a red border
  - if email address format is invalid:
    - Customer remains on `/checkout/`
    - system displays error message: “Invalid billing email address”
  - if phone number contains letters or invalid characters:
    - Customer remains on `/checkout/`
    - system displays error message: “Please enter a valid phone number.”

**Screenshot:**

[Jira attachment reference: `image-20261106-123752.png`; binary not included.]
