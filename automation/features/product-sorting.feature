Feature: Product sorting

  Rule: AEC-14 sorting options and category retention
    @AEC-119 @sorting_options
    Scenario: The store displays the specified sorting options
      Given an authorized test environment is configured
      And the customer is on the store page
      Then the default sorting option is "Default sorting"
      And the sorting options include:
        | Sort by popularity         |
        | Sort by average rating     |
        | Sort by latest             |
        | Sort by price: low to high |
        | Sort by price: high to low |

    @AEC-119 @sorting_price
    Scenario: Price sorting retains the selected category
      Given an authorized test environment is configured
      And the customer is on the store page
      And the customer opens the Men category
      When the customer selects "Sort by price: low to high"
      Then the Men category remains selected
      And the visible product prices are in ascending order
      When the customer selects "Sort by price: high to low"
      Then the Men category remains selected
      And the visible product prices are in descending order
