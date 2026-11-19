@cart_session
Feature: Cart session updates
  These checks require a non-production environment and an approved in-stock product.

  Rule: AEC-7 adding a product
    @AEC-28
    Scenario: Add one product from its details page
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      When the customer adds the configured product to the cart
      Then the cart contains one unit of the selected product

  Rule: AEC-10 updating a cart
    @AEC-32
    Scenario: Updating quantity recalculates the product and cart subtotals
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      And the customer adds the configured product to the cart
      When the customer updates its cart quantity to 2
      Then the cart reflects the updated quantity and subtotals

    @AEC-34
    Scenario: Updating quantity to zero removes the only item
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      And the customer adds the configured product to the cart
      When the customer updates its cart quantity to 0
      Then the cart is empty after removing the product

    @AEC-85
    Scenario: Remove the only item from the cart
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      And the customer adds the configured product to the cart
      When the customer removes the product from the cart
      Then the cart is empty after removing the product

  Rule: AEC-10 and AEC-13 cart persistence
    @AEC-108
    Scenario: Cart contents persist after refreshing the cart page
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      And the customer adds the configured product to the cart
      And the cart contains one unit of the selected product
      When the customer refreshes the cart page
      Then the cart still contains the same product, quantity and price
