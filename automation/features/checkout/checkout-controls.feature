@cart_session @checkout_controls
Feature: Checkout controls and order review

  Background:
    Given an authorized test environment is configured
    And the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: AEC-17 returning customer login controls
    @AEC-64
    Scenario: Show the returning customer banner
      Then the returning customer banner is displayed

    @AEC-69
    Scenario: Expand and collapse the checkout login form
      When the customer opens the checkout login form
      Then the checkout login form is visible
      When the customer closes the checkout login form
      Then the checkout login form is hidden

    @AEC-79
    Scenario: Mask the checkout login password
      When the customer opens the checkout login form
      Then the checkout login password is masked

    @AEC-105
    Scenario: Navigate from checkout to password recovery
      When the customer opens the checkout login form
      And the customer opens the checkout lost-password link
      Then the customer is on the account password recovery page

  Rule: AEC-20 guest account creation controls
    @AEC-149
    Scenario: The guest can reveal and hide the checkout account password
      Then checkout account creation is unchecked and its password field is hidden
      When the customer enables checkout account creation
      Then the checkout account password field is visible
      When the customer disables checkout account creation
      Then checkout account creation is unchecked and its password field is hidden

  Rule: AEC-23 payment choices
    @AEC-145
    Scenario: Payment instructions follow the selected method
      Then direct bank transfer is selected with its instructions visible
      When the customer selects cash on delivery
      Then cash on delivery is selected with its instructions visible
      When the customer selects direct bank transfer
      Then direct bank transfer is selected with its instructions visible

  Rule: AEC-22 review order details
    @AEC-157
    Scenario: Review the selected product and subtotal before ordering
      Then the order review displays the selected product quantity and subtotal
