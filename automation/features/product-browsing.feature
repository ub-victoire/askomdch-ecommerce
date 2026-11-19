Feature: Product browsing

  Rule: AEC-15 search results match the search term
    @AEC-18
    Scenario Outline: Searching for shirts returns the specified products regardless of case
      Given an authorized test environment is configured
      And the customer is on the store page
      When the customer searches for "<query>"
      Then the matching products are exactly:
        | Blue Tshirt  |
        | Green Tshirt |

      Examples:
        | query |
        | shirt |
        | SHIRT |
        | ShIrT |

  Rule: AEC-16 selecting a product opens its details
    @AEC-110
    Scenario: Open a product from the catalogue
      Given an authorized test environment is configured
      And the customer is on the store page
      When the customer opens the first catalogue product
      Then the selected product details are displayed

    @AEC-110
    Scenario: Open a product from search results
      Given an authorized test environment is configured
      And the customer is on the store page
      And the customer searches for "shirt"
      When the customer opens the first catalogue product
      Then the selected product details are displayed
