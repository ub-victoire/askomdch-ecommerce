@cart_session @checkout_order
Feature: Place an order with each payment method
  These scenarios verify that checkout accepts each available payment method.
  Use approved test data and the site's test payment mode only.

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product
    And the approved checkout billing fixture is loaded

  Rule: AEC-23 payment methods
    @AEC-144
    Scenario: Place an order using the default payment method
      Given direct bank transfer is selected by default
      When the customer places the order
      Then the order is submitted using direct bank transfer

    @AEC-145
    Scenario: Place an order using cash on delivery
      When the customer selects cash on delivery and places the order
      Then the order is submitted using cash on delivery
