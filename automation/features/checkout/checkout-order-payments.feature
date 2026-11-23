Feature: Place an order with each payment method
  These scenarios verify that checkout accepts each available payment method.
  Use approved test data and the site's test payment mode only.

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: AEC-23 payment methods
    @AEC-144 @AEC-145
    Scenario Outline: Place an order using each payment method
      Given the customer enters the billing details shown in the examples
      And the customer selects "<paymentMethod>"
      When the customer places the order
      Then the order is submitted using "<paymentMethod>"

      Examples:
        | firstName | lastName | country       | streetAddress   | city      | state      | postcode | email                         | paymentMethod       |
        | Casey     | Example  | United States | 123 Test Street | Testville | California | 90210    | qa.checkout@example.invalid   | Direct bank transfer |
        | Casey     | Example  | United States | 123 Test Street | Testville | California | 90210    | qa.checkout@example.invalid   | Cash on delivery     |
