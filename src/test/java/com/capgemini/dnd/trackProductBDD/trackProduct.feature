Feature: Track Product Order

Scenario: Tracking a particular product order

Given User is on track product order page 
When User entered the order ID as "7"
And user clicked on track order button
Then The shelf life of order Id "7" is displayed below



Scenario: Tracking a product order giving wrong order id

Given User is on track product order page 
When User entered the order ID as "500"
And user clicked on track order button
Then A message "Product Order ID does not exist" is displayed below