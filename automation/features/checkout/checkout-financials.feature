@cart_session @checkout_financials @phase-3
Feature: Checkout discounts and totals
  Use only coupons created for the approved test environment.

  Background:
    Given the customer has a fresh browser session
    And the customer adds the configured product to the cart
    And the cart contains one unit of the selected product
    And the customer opens checkout with that product

  Rule: Apply and remove coupons
    @pending
    Scenario: Apply a valid coupon
      Given an approved valid test coupon is configured
      When the customer applies the coupon at checkout
      Then the coupon is shown as applied
      And the order review shows the discount

    @pending
    Scenario Outline: Reject an invalid coupon
      When the customer applies coupon "<code>" at checkout
      Then the coupon is not applied
      And the checkout shows 'Coupon "<messageCode>" does not exist!'

      Examples:
        | code                     | messageCode              |
        | INVALID-DOES-NOT-EXIST   | invalid-does-not-exist   |

    @pending
    Scenario: Remove an applied coupon
      Given an approved valid test coupon is applied
      When the customer removes the coupon
      Then the coupon is no longer shown
      And the order review no longer shows the discount

  Rule: Recalculate checkout totals
    @pending
    Scenario: Calculate the final total from current order values
      Given an approved valid test coupon is applied
      When the customer reviews the order
      Then the order review shows the subtotal, shipping, applicable tax, discount, and total
      And the total equals subtotal plus shipping and tax minus discount

    @AEC-157
    Scenario: Show shipping and tax in the order total
      When the customer reviews the order
      Then the order review shows the subtotal, shipping, applicable tax, and total
      And the total equals subtotal plus shipping and applicable tax
