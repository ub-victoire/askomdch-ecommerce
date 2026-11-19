@cart_session @checkout_login
Feature: Checkout login required-field validation

  Rule: AEC-17 requires a username or email address
    @AEC-79 @AEC-109
    Scenario: An empty username is rejected without leaving checkout
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      And the customer adds the configured product to the cart
      And the cart contains one unit of the selected product
      And the customer opens checkout with that product
      And the customer opens the checkout login form
      When the customer submits checkout login without a username or email
      Then the customer remains on the checkout page
      And the checkout login displays "Error: Username is required"
