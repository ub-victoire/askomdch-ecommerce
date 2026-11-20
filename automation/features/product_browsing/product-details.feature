Feature: View Product Details 

    Scenario: Customer verifying the click product details
        Given verify that product details page opens from catalague page 
        When customer selects a product page
        And click the search/zoom icon displayed on the product image
        And close the enlarged full-screen image view by clicking on other side of page
        And click on Description section
        And click on Additional information section
        And click on Reviews section
        Then the Reviews section is displayed, showing the message "There are no reviews yet" and remaining sections.