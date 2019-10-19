Feature: Update Process Date for a particular order

Scenario: Updating process date for a particular order ID

Given User is on update process date page 
When User enters a number in order ID as "5"
And User enters Process Date as "2019-10-30"
And user clicks on update button
Then The message such as "Data inserted" is displayed



Scenario: Updating process date for a wrong order ID

Given User is on update process date page 
When User enters a number in order ID as "500"
And User enters Process Date as "2019-10-25"
And user clicks on update button
Then The message such as "RawMaterial Order ID does not exist" is displayed



Scenario: Updating process date by entering invalid process date

Given User is on update process date page 
When User enters a number in order ID as "5"
And User enters Process Date as "2019-06-25"
And user clicks on update button
Then The message such as "Process date cannot be before its arrival(date of delivery)" is displayed