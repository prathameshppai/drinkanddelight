Feature: Track Raw Material Order

Scenario: Tracking a particular raw material order

Given User is on track raw material order page 
When User enters number in the order ID field as "5"
And user clicks on track order button present below
Then The shelf life of the given order Id 5 is displayed below



Scenario: Tracking a raw material order giving wrong order id

Given User is on track raw material order page 
When User enters number in the order ID field as "500"
And user clicks on track order button present below
Then A message "RawMaterial Order ID does not exist" is displayed in the blank space below