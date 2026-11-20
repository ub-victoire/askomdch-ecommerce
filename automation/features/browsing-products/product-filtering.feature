Feature: Product category navigation

  Rule: AEC-11 header category links
    @AEC-133 @category_navigation
    Scenario Outline: A header category link opens its category page
      Given an authorized test environment is configured
      And the customer is on the store page
      When the customer opens the "<category>" header category
      Then the category page heading is "<category>"

      Examples:
        | category    |
        | Men         |
        | Women       |
        | Accessories |
