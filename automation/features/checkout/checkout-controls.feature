Feature: Checkout controls and order review

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: AEC-17 returning customer login controls
    @AEC-64 @phase-1
    Scenario: Show the returning customer banner
      Then the returning customer banner is displayed

    @AEC-69 @phase-1
    Scenario: Expand and collapse the checkout login form
      When the customer opens the checkout login form
      Then the checkout login form is visible
      When the customer closes the checkout login form
      Then the checkout login form is hidden

    @AEC-105
    Scenario: Navigate from checkout to password recovery
      When the customer opens the checkout login form
      And the customer opens the checkout lost-password link
      Then the customer is on the account password recovery page

  Rule: AEC-20 guest account creation controls
    @AEC-149 @phase-1
    Scenario: Show and hide the account creation fields
      Then account creation is unchecked and the username and password fields are hidden
      When the customer enables checkout account creation
      Then the account username and password fields are visible
      When the customer disables checkout account creation
      Then account creation is unchecked and the username and password fields are hidden

  Rule: AEC-21 alternative shipping address
    @AEC-21 @phase-1
    Scenario: Show and hide the alternative shipping form
      Then the alternative shipping checkbox is unchecked and its fields are hidden
      When the customer enables a different shipping address
      Then the alternative shipping fields are visible
      When the customer disables a different shipping address
      Then the alternative shipping checkbox is unchecked and its fields are hidden

  Rule: AEC-22 checkout coupon
    @AEC-157
    Scenario: Open the checkout coupon form
      When the customer opens the checkout coupon form
      Then the coupon field and Apply coupon button are visible

  Rule: AEC-23 payment choices
    @AEC-145 @phase-1
    Scenario: Payment instructions follow the selected method
      Then direct bank transfer is selected with its test-mode message visible
      When the customer selects cash on delivery
      Then cash on delivery is selected with its test-mode message visible
      When the customer selects direct bank transfer
      Then direct bank transfer is selected with its test-mode message visible

  Rule: AEC-22 review order details
    @AEC-157
    Scenario: Review products and quantities before ordering
      Then the order review lists each product, quantity, and line subtotal
