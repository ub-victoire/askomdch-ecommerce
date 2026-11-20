Feature: Product Search Functionality 


   Scenario Outline: Customer verifies that product searching is displaying a matching products 
        Given the customer is on the product search page
        When customer enters <QUERY> into the product search field 
        And the customer clicks the search button
        Then the system should display expected "<EXPECTED_COUNT>"
        And the system should display expected products "<"EXPECTED_PRODUCTS">"


        Examples:
            | QUERY    | EXPECTED_COUNT              | EXPECTED_PRODUCTS |
            | shirt    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | SHIRT    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | ShIrT    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | Shirt    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | sHiRt    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | shIRT    | Showing all 2 results       | Green Tshirt, Blue Tshirt |
            | jeans    | Showing all 5 results       | [Full expected product list] |
            | Bracelet | Showing all 2 results      | [Full expected product list] |
            | tincidunt| Showing all 13 results (live: Showing 1–8 of 13 results)      | All 13 products (description match) |
            | a        | Showing all 13 results (live: Showing 1–8 of 13 results)      | All 13 products (single-character match) |

    

