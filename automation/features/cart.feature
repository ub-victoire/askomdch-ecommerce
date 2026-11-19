Feature: Empty cart

  Rule: AEC-13 empty cart state and return to shop
    @AEC-112
    Scenario: A customer with an empty cart returns to the store
      Given an authorized test environment is configured
      And the customer has a fresh browser session
      When the customer opens the cart
      Then the empty cart notice and return to shop button are displayed
      And the cart table, coupon section and cart totals are hidden
      When the customer returns to the shop
      Then the customer arrives at the store page
