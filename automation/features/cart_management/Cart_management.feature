Feature: Cart Management

    Provide users with real-time control over their staged items,
    quantity modifications, coupon code redemptions, and order subtotal calculations prior to entering checkout

    Background:
        Given The site is accessible



    Scenario Outline: Add a product to the cart
        Given The <page> containing the products is accessible
        Given Atleast one product is available
        When a customer navigates to the <page>
        When a customer selects a <product>
        When a customer clicks on "Add to Cart" button
        When a customer opens the cart page
        Then The cart displays the selected product with the correct details.
        But The cart should not display any other products.

        Examples:
            | product    | page  |
            | Blue shoes | store |
            | Jeans      | men   |

    Scenario Outline: Update quantity recalculates totals
        Given The Cart page is accessible
        Given The cart contains atleast one product
        When a customer updates <quantity> of <product>>
        When a customer clicks on "Update Cart" button
        Then the Cart should diplay the updated <quantity> for <product>
        Then the Cart should recalculate the subtotal and total amounts accordingly.

        Examples:
            | product    | quantity | price |
            | Blue shoes | 2        | 50    |

    Scenario Outline: Reject an invalid coupon
        Given The cart page is accessible
        Given The Cart contains atleast one product
        When a customer enters <invalid_coupon_code> in the coupon input field
        When a customer clicks on "APPLY COUPON" button
        Then The system should display the message "Coupon <invalid_coupon_code> does not exist!"
        But The cart should not apply any discount in the subtotal

        Examples:
            | invalid_coupon_code |
            | INVALID10           |
            | FAKECODE            |

    Scenario Outline: Remove a product from the cart
        Given The Cart page is accessible
        Given The cart contains atleast two <product>
        When a customer clicks on the "X" button next to <product>
        Then The product is removed from the cart
        And The cart total is updated excluding the details of the <product>

        Examples:
            | product    |
            | Blue shoes |
            | Jeans      |
            