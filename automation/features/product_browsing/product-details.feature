Feature: View Product Details 

    Scenario: Customer verifying the click product details
        Given verify that product details page opens from catalague page 
        When customer selects a product page
        Then the system opens the product-detail page of the selected product
        And Open a product-detail page and inspect the displayed product information
        And hover over the product image to verify that the image is displayed in a larger view
        And selects the search/zoom button to view the product image in a larger view
        And closes the larger view of the product image by clicking on the other side of page
        And open product-details page and inspect discription section
        And open product-details page and inspect the additional information section
        And open product-details page and inspect quantity control section
        And enter the quantity into the quantity control 
        And selects quantity using the quantity control
        And clicking add to cart button after choosing product quantity 
        And open the cart after adding the selected product
        And open a product-detail page and inspect the Reviews section
        And inspect the review count displayed in reviews section
        And access the review form from the product-detail page
        Then submit a review using the review form
