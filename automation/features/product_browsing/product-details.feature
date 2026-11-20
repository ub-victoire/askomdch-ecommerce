Feature: View Product Details 

    Scenario Outline: Customer verifying the product details
        Given The <page> page is accessible 
        And The <page> page contains atleast one <product> 
        When customer clicks a <product> from <page>
        Then the <product> details page displays information with it's correspinding product: <product>, price: <price>

    Examples:
        |   page        |       product      |    price           |
        | home          | blue shoes         | $45                |
        | store         | denim blue jeans   | $100               |
   
