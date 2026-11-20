
Feature: Product Details
  As a customer
  I want to view product details and interact with product information
  So that I can make informed purchasing decisions

  Scenario: Verify that selecting a product opens its correct product details page
    Given the customer is viewing the product search results
    When the customer selects a product from the search results
    Then the system should open the product details page of the selected product

  Scenario: Verify that the product details page displays the Home breadcrumb
    Given the customer has opened a product details page
    When the customer inspects the breadcrumb
    Then the breadcrumb should display "Home"

  Scenario: Verify that all assigned product information is displayed
    Given the customer has opened a product details page
    And the selected product has assigned product information
    When the customer inspects the displayed product information
    Then the product image should be displayed
    And the product name should be displayed
    And the product price should be displayed
    And the product description should be displayed
    And the product category should be displayed
    And the product tags should be displayed
    And the product review section should be displayed
    And the current review count should be displayed

  Scenario: Verify that all assigned product tags are displayed
    Given the customer has opened a product with assigned tags
    When the customer inspects the assigned tags
    Then each tag assigned to the selected product should be displayed

  Scenario: Verify that the Tags section is not displayed for products without assigned tags
    Given the customer has opened a product without assigned tags
    When the customer inspects the tags area
    Then no empty Tags section or placeholder should be displayed

  Scenario: Verify that each product displays only its own assigned tags
    Given the customer has access to multiple products with different assigned tags
    When the customer opens each product details page
    And the customer inspects the displayed tags
    Then each product should display its own assigned tags
    And no tags belonging to another product should be displayed

  Scenario: Verify that selecting a product image opens it for viewing
    Given the customer has opened a product details page
    And the selected product image is displayed
    When the customer selects the product image
    Then the selected product image should open for viewing

  Scenario: Verify that hovering over a catalogue product image enlarges it
    Given the customer is viewing the product catalogue
    And a product image is displayed
    When the customer hovers over the product image
    Then the image should enlarge or be brought forward

  Scenario: Verify that hovering over a search result product image enlarges it
    Given the customer is viewing the product search results
    And a product image is displayed
    When the customer hovers over the product image
    Then the image should enlarge or be brought forward

  Scenario: Verify that a product image returns to its normal appearance after hovering
    Given the customer is viewing a product image in its enlarged or brought-forward state
    When the customer moves the pointer away from the product image
    Then the image should return to its normal appearance

  Scenario: Verify that hovering over a product image displays the search/zoom icon
    Given the customer is viewing the product catalogue or search results
    And a product image is displayed
    When the customer hovers over the product image
    Then the search/zoom icon should be displayed

  Scenario: Verify that selecting the search/zoom icon opens the enlarged image
    Given the customer is viewing a product image with the search/zoom icon visible
    When the customer selects the search/zoom icon
    Then the selected image should open in an enlarged full-screen view

  Scenario: Verify that closing the enlarged image returns the customer to the product listing page
    Given the customer has opened an enlarged full-screen product image from the product listing page
    When the customer closes the enlarged image view
    Then the enlarged image view should close
    And the customer should return to the product listing page

  Scenario: Verify that the product Description section displays the assigned description
    Given the customer has opened a product with an assigned description
    When the customer inspects the Description section
    Then the Description section should be displayed
    And it should contain the description associated with the selected product

  Scenario: Verify that the Additional information section displays the available product information
    Given the customer has opened a product with available additional information including weight
    When the customer inspects the Additional information section
    Then the Additional information section should be displayed
    And it should contain the selected product's available information including its weight

  Scenario: Verify that the quantity control is displayed on the product details page
    Given the customer has opened a product details page
    When the customer inspects the quantity control
    Then the quantity control should be displayed

  Scenario: Verify that the customer can enter a chosen product quantity
    Given the customer has opened a product details page
    And the quantity control is displayed
    When the customer enters a quantity of "3"
    Then the quantity control should accept the entered quantity of "3"

  Scenario: Verify that the customer can select a chosen product quantity
    Given the customer has opened a product details page
    And the quantity control is displayed
    When the customer selects a quantity of "2"
    Then the quantity control should accept the selected quantity of "2"

  Scenario: Verify that a product is added to the cart using the chosen quantity
    Given the customer has opened a product details page
    And the customer has selected a product quantity of "3"
    When the customer clicks the "Add to cart" button
    Then the selected product should be added to the cart
    And the product should be added using the chosen quantity of "3"

  Scenario: Verify that the cart reflects the selected product and its chosen quantity
    Given the customer has added a product to the cart with a quantity of "3"
    When the customer opens the cart
    Then the cart should display the selected product
    And the product quantity should be displayed as "3"

  Scenario: Verify that the Reviews section is displayed on the product details page
    Given the customer has opened a product details page
    When the customer inspects the Reviews section
    Then the Reviews section should be displayed

  Scenario: Verify that the current review count is displayed
    Given the customer has opened a product with existing reviews
    When the customer inspects the review count in the Reviews section
    Then the current number of reviews associated with the selected product should be displayed

  Scenario: Verify that the Reviews section displays zero reviews for a product without reviews
    Given the customer has opened a product with zero reviews
    When the customer inspects the Reviews section
    Then the section should display exactly "Reviews (0)"

  Scenario: Verify that the customer can access the review form
    Given the customer has opened a product details page
    When the customer accesses the review form
    Then the customer should be able to access the review form

  Scenario: Verify that the review form displays the available fields
    Given the customer has accessed the review form for a selected product
    When the customer inspects the available review form fields
    Then a rating selection field should be available where provided
    And a comment field should be available where provided
